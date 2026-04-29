package com.hjq.window.draggable;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.DisplayCutout;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.webkit.WebView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SeekBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.NestedScrollingChild;
import androidx.core.view.NestedScrollingParent;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import com.hjq.window.EasyWindow;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyWindow
 *    time   : 2019/01/04
 *    desc   : 拖拽抽象类
 */
public abstract class AbstractWindowDraggableRule implements OnTouchListener {

    /** 屏幕旋转缓冲时间 */
    public static final long SCREEN_ROTATION_BUFFER_TIME = 100;

    @Nullable
    private EasyWindow<?> mEasyWindow;
    @Nullable
    private ViewGroup mRootLayout;

    /** 是否允许移动到屏幕安全区域 */
    private boolean mAllowMoveToScreenSafeArea = true;

    /** 拖拽回调监听对象（可能为空） */
    @Nullable
    private OnWindowDraggingListener mWindowDraggingListener;

    @NonNull
    private final Rect mTempRect = new Rect();

    private int mCurrentScreenWidth;
    private int mCurrentScreenHeight;

    private int mCurrentScreenInvisibleWidth;
    private int mCurrentScreenInvisibleHeight;

    /** 当前屏幕的物理尺寸 */
    private double mScreenPhysicalSize;

    /** 需要消费触摸事件的 View（可能为空）*/
    @Nullable
    private View mConsumeTouchView;

    /**
     * 判断当前是否处于触摸移动状态
     */
    public abstract boolean isTouchMoving();

    /**
     * 窗口显示后回调这个方法
     */
    @SuppressLint("ClickableViewAccessibility")
    public void start(@NonNull EasyWindow<?> easyWindow) {
        mEasyWindow = easyWindow;
        mRootLayout = easyWindow.getRootLayout();
        if (mRootLayout == null) {
            return;
        }
        mRootLayout.setOnTouchListener(this);

        refreshWindowInfo();
        refreshScreenPhysicalSize();
    }

    /**
     * 窗口回收后回调这个方法
     */
    public void recycle() {
        mEasyWindow = null;
        if (mRootLayout != null) {
            mRootLayout.setOnTouchListener(null);
            mRootLayout = null;
        }
    }

    @Override
    public final boolean onTouch(@NonNull View view, @NonNull MotionEvent event) {
        EasyWindow<?> easyWindow = mEasyWindow;
        ViewGroup rootLayout = mRootLayout;
        if (easyWindow == null || rootLayout == null) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 在按下的时候先更新一下窗口信息和坐标信息，否则点击可能会出现坐标偏移的问题
                // 全局的悬浮窗在非全屏的页面创建，跳转到全屏的页面展示就会导致坐标偏移
                // 这是因为在跳转到全屏的悬浮窗的时候没有更新当前 Window 信息导致的
                // 目前能想到比较好的办法就是在悬浮窗移动前之前先更新 Window 信息和 View 坐标
                // Github issue 地址：https://github.com/getActivity/EasyWindow/issues/69
                refreshWindowInfo();
                refreshScreenPhysicalSize();

                mConsumeTouchView = null;
                View consumeTouchEventView = findNeedConsumeTouchView(rootLayout, event);
                if (consumeTouchEventView != null && dispatchTouchEventToChildView(rootLayout, consumeTouchEventView, event)) {
                    mConsumeTouchView = consumeTouchEventView;
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mConsumeTouchView != null) {
                    try {
                        return dispatchTouchEventToChildView(rootLayout, mConsumeTouchView, event);
                    } finally {
                        // 释放/置空对象
                        mConsumeTouchView = null;
                    }
                }
            default:
                if (mConsumeTouchView != null) {
                    return dispatchTouchEventToChildView(rootLayout, mConsumeTouchView, event);
                }
                break;
        }

        return onDragWindow(easyWindow, rootLayout, event);
    }

    /**
     * 派发触摸事件给子 View
     *
     * @param event                 触摸事件
     * @param parentView            父 View
     * @param childView             子 View（同时也是被触摸的 View）
     */
    public boolean dispatchTouchEventToChildView(@NonNull View parentView, @NonNull View childView, @NonNull MotionEvent event) {
        // 派发触摸事件之前，先将 MotionEvent 对象中的位置进行纠偏，否则会导致点击坐标对不上的情况
        offsetMotionEventLocation(parentView, childView, event);
        return childView.dispatchTouchEvent(event);
    }

    /**
     * 偏移触摸事件坐标，这样子 View 能接受到正确的坐标
     *
     * @param event                 触摸事件
     * @param parentView            父 View
     * @param childView             子 View（同时也是被触摸的 View）
     */
    public void offsetMotionEventLocation(@NonNull View parentView, @NonNull View childView, @NonNull MotionEvent event) {
        // 这部分代码参考自 ViewGroup.dispatchTransformedTouchEvent 方法实现
        final int offsetX = parentView.getScrollX() - childView.getLeft();
        final int offsetY = parentView.getScrollY() - childView.getTop();
        event.offsetLocation(offsetX, offsetY);
    }

    /**
     * 窗口拖拽回调方法
     *
     * @param easyWindow        当前窗口对象
     * @param rootLayout        当前窗口视图
     * @param event             当前触摸事件
     * @return                  根据返回值决定是否拦截该事件
     */
    public abstract boolean onDragWindow(@NonNull EasyWindow<?> easyWindow, @NonNull ViewGroup rootLayout, @NonNull MotionEvent event);

    @Nullable
    public EasyWindow<?> getEasyWindow() {
        return mEasyWindow;
    }

    @Nullable
    public ViewGroup getRootLayout() {
        return mRootLayout;
    }

    /**
     * 设置是否可以移动到屏幕安全区域
     */
    public void setAllowMoveToScreenSafeArea(boolean allowMoveToScreenSafeArea) {
        mAllowMoveToScreenSafeArea = allowMoveToScreenSafeArea;
    }

    /**
     * 当前是否可以移动到屏幕安全区域
     */
    public boolean isAllowMoveToScreenSafeArea() {
        return mAllowMoveToScreenSafeArea;
    }

    /**
     * 获取当前屏幕的宽度
     */
    public int getScreenWidth() {
        return mCurrentScreenWidth;
    }

    /**
     * 获取当前屏幕的高度
     */
    public int getScreenHeight() {
        return mCurrentScreenHeight;
    }

    /**
     * 获取屏幕不可用的宽度，一般情况下为横屏状态下刘海的高度
     */
    public int getScreenInvisibleWidth() {
        return mCurrentScreenInvisibleWidth;
    }

    /**
     * 获取屏幕不可用的高度，一般情况下为状态栏的高度
     */
    public int getScreenInvisibleHeight() {
        return mCurrentScreenInvisibleHeight;
    }

    /**
     * 获取当前窗口视图的宽度
     */
    public int getWindowViewWidth() {
        if (mEasyWindow == null) {
            return 0;
        }
        return mEasyWindow.getWindowViewWidth();
    }

    /**
     * 获取当前窗口视图的高度
     */
    public int getWindowViewHeight() {
        if (mEasyWindow == null) {
            return 0;
        }
        return mEasyWindow.getWindowViewHeight();
    }

    /**
     * 刷新当前 Window 信息
     */
    public void refreshWindowInfo() {
        if (mEasyWindow == null) {
            return;
        }

        Context context = mEasyWindow.getContext();
        if (context == null) {
            return;
        }

        // 相关问题地址：https://github.com/getActivity/EasyWindow/issues/85
        View decorView = getRootLayout();

        if (decorView == null && context instanceof Activity) {
            decorView = ((Activity) context).getWindow().getDecorView();
        }

        if (decorView == null) {
            return;
        }

        // Log.i(getClass().getSimpleName(), "刷新当前 Window 信息");

        // 这里为什么要这么写，因为发现了鸿蒙手机在进行屏幕旋转的时候
        // 回调 onConfigurationChanged 方法的时候获取到这些参数已经变化了
        // 所以需要提前记录下来，避免后续进行坐标计算的时候出现问题
        decorView.getWindowVisibleDisplayFrame(mTempRect);
        mCurrentScreenWidth = mTempRect.right - mTempRect.left;
        mCurrentScreenHeight = mTempRect.bottom - mTempRect.top;

        mCurrentScreenInvisibleWidth = Math.max(mTempRect.left, 0);
        mCurrentScreenInvisibleHeight = Math.max(mTempRect.top, 0);

        /*
        Log.i(getClass().getSimpleName(),
            "CurrentScreenWidth = " + mCurrentScreenWidth +
            "，CurrentScreenHeight = " + mCurrentScreenHeight +
            "，CurrentScreenInvisibleWidth = " + mCurrentScreenInvisibleWidth +
            "，CurrentScreenInvisibleHeight = " + mCurrentScreenInvisibleHeight);
         */
    }

    /**
     * 刷新当前屏幕的物理尺寸
     */
    @SuppressWarnings("deprecation")
    public void refreshScreenPhysicalSize() {
        if (mEasyWindow == null) {
            return;
        }

        WindowManager windowManager = mEasyWindow.getWindowManager();
        Display defaultDisplay = windowManager.getDefaultDisplay();
        if (defaultDisplay == null) {
            return;
        }

        DisplayMetrics metrics = new DisplayMetrics();
        defaultDisplay.getMetrics(metrics);

        float screenWidthInInches;
        float screenHeightInInches;
        if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR1) {
            Point point = new Point();
            defaultDisplay.getRealSize(point);
            screenWidthInInches = point.x / metrics.xdpi;
            screenHeightInInches = point.y / metrics.ydpi;
        } else {
            screenWidthInInches = metrics.widthPixels / metrics.xdpi;
            screenHeightInInches = metrics.heightPixels / metrics.ydpi;
        }

        // 勾股定理：直角三角形的两条直角边的平方和等于斜边的平方
        mScreenPhysicalSize = Math.sqrt(Math.pow(screenWidthInInches, 2) + Math.pow(screenHeightInInches, 2));
    }

    /**
     * 屏幕方向发生了改变
     */
    public void onScreenOrientationChange() {
        // Log.i(getClass().getSimpleName(), "屏幕方向发生了改变");
        final ViewGroup rootLayout = getRootLayout();
        if (rootLayout == null) {
            return;
        }

        final EasyWindow<?> easyWindow = getEasyWindow();
        if (easyWindow == null) {
            return;
        }

        final WindowManager.LayoutParams windowParams = easyWindow.getWindowParams();

        if (!isFollowScreenRotationChanges()) {
            easyWindow.sendTask(() -> {
                refreshWindowInfo();
                refreshScreenPhysicalSize();
            }, SCREEN_ROTATION_BUFFER_TIME);
            return;
        }

        final int screenWidth = getScreenWidth();
        final int screenHeight = getScreenHeight();
        // Log.i(getClass().getSimpleName(), "屏幕旋转前 screenWidth = " + screenWidth + "，screenHeight = " + screenHeight);

        final int screenInvisibleWidth = getScreenInvisibleWidth();
        final int screenInvisibleHeight = getScreenInvisibleHeight();
        // Log.i(getClass().getSimpleName(), "屏幕旋转前 screenInvisibleWidth = " + screenInvisibleWidth + "，screenInvisibleHeight = " + screenInvisibleHeight);

        final int windowViewWidth = getWindowViewWidth();
        final int windowViewHeight = getWindowViewHeight();
        // Log.i(getClass().getSimpleName(), "屏幕旋转前 windowViewWidth = " + windowViewWidth + "，windowViewHeight = " + windowViewHeight);

        final int windowGravity;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            windowGravity = Gravity.getAbsoluteGravity(windowParams.gravity, rootLayout.getLayoutDirection());
        } else {
            windowGravity = windowParams.gravity;
        }
        final int windowHorizontalOffset = windowParams.x;
        final int windowVerticalOffset = windowParams.y;

        int windowHorizontalGravity = windowGravity & Gravity.HORIZONTAL_GRAVITY_MASK;
        final int currentViewOnScreenX;
        if (windowHorizontalGravity == Gravity.LEFT) {
            currentViewOnScreenX = windowHorizontalOffset;
        } else if (windowHorizontalGravity == Gravity.RIGHT) {
            currentViewOnScreenX = (screenWidth - screenInvisibleWidth) - windowHorizontalOffset;
        } else {
            // Gravity.CENTER_HORIZONTAL
            currentViewOnScreenX = (screenWidth - screenInvisibleWidth - windowViewWidth) / 2 + windowHorizontalOffset;
        }

        int windowVerticalGravity = windowGravity & Gravity.VERTICAL_GRAVITY_MASK;
        final int currentViewOnScreenY;
        if (windowVerticalGravity == Gravity.TOP) {
            currentViewOnScreenY = windowVerticalOffset;
        } else if (windowVerticalGravity == Gravity.BOTTOM) {
            currentViewOnScreenY = (screenHeight - screenInvisibleHeight) - windowVerticalOffset;
        } else {
            // Gravity.CENTER_VERTICAL
            currentViewOnScreenY = (screenHeight - screenInvisibleHeight - windowViewHeight) / 2 + windowVerticalOffset;
        }
        // Log.i(getClass().getSimpleName(), "currentViewOnScreenX = " + currentViewOnScreenX + "，currentViewOnScreenY = " + currentViewOnScreenY);

        // 先扣除 View 自身宽度，用剩余空余空间算比例
        int currentHorizontalGap = Math.max(screenWidth - windowViewWidth, 0);
        final float leftGapRatio = currentHorizontalGap > 0 ? currentViewOnScreenX / (float) currentHorizontalGap : 0.5f;
        // 先扣除 View 自身高度，用剩余空余空间算比例
        int currentVerticalGap = Math.max(screenHeight - windowViewHeight, 0);
        final float topGapRatio = currentVerticalGap > 0 ? currentViewOnScreenY / (float) currentVerticalGap : 0.5f;
        // Log.i(getClass().getSimpleName(), "leftGapRatio = " + leftGapRatio + "，topGapRatio = " + topGapRatio);

        easyWindow.sendTask(() -> {
            refreshWindowInfo();
            refreshScreenPhysicalSize();
            int newScreenWidth = getScreenWidth();
            int newScreenHeight = getScreenHeight();
            // Log.i(getClass().getSimpleName(), "屏幕旋转后 screenWidth = " + newScreenWidth + "，screenHeight = " + newScreenHeight);

            int newHorizontalGap = newScreenWidth - windowViewWidth;
            int newVerticalGap = newScreenHeight - windowViewHeight;

            int newViewOnScreenX = newHorizontalGap <= 0 ? 0 : (int) (newHorizontalGap * leftGapRatio);
            int newViewOnScreenY = newVerticalGap <= 0 ? 0 : (int) (newVerticalGap * topGapRatio);

            // 边界安全限位，防止浮点误差越界
            newViewOnScreenX = Math.max(0, Math.min(newViewOnScreenX, newScreenWidth - windowViewWidth));
            newViewOnScreenY = Math.max(0, Math.min(newViewOnScreenY, newScreenHeight - windowViewHeight));

            // Log.i(getClass().getSimpleName(), "屏幕旋转后 newViewOnScreenX = " + newViewOnScreenX + "，newViewOnScreenY = " + newViewOnScreenY);
            updateLocation(newViewOnScreenX, newViewOnScreenY);

            // 需要注意，这里需要延迟执行，否则会有问题
            easyWindow.sendTask(this::onScreenRotateInfluenceCoordinateChangeFinish);

        }, SCREEN_ROTATION_BUFFER_TIME);
    }

    /**
     * 屏幕旋转导致悬浮窗坐标发生变化完成方法
     */
    protected void onScreenRotateInfluenceCoordinateChangeFinish() {
        // default implementation ignored
    }

    /**
     * 悬浮窗是否跟随屏幕方向变化而发生变化
     */
    public boolean isFollowScreenRotationChanges() {
        return true;
    }

    public void updateLocation(float x, float y) {
        updateLocation(x, y, isAllowMoveToScreenSafeArea());
    }

    public void updateLocation(float x, float y, boolean allowMoveToScreenSafeArea) {
        updateLocation((int) x, (int) y, allowMoveToScreenSafeArea);
    }

    /**
     * 更新悬浮窗的位置
     *
     * @param x                                 x 坐标（相对与屏幕左上位置）
     * @param y                                 y 坐标（相对与屏幕左上位置）
     * @param allowMoveToScreenSafeArea         是否允许移动到屏幕安全区域
     */
    public void updateLocation(int x, int y, boolean allowMoveToScreenSafeArea) {
        if (allowMoveToScreenSafeArea) {
            updateWindowCoordinate(x, y);
            return;
        }

        Rect safeInsetRect = getSafeInsetRect();
        if (safeInsetRect == null ||
            (safeInsetRect.left == 0 && safeInsetRect.right == 0 &&
            safeInsetRect.top == 0 && safeInsetRect.bottom == 0)) {
            updateWindowCoordinate(x, y);
            return;
        }

        int viewWidth = getWindowViewWidth();
        int viewHeight = getWindowViewHeight();

        int screenWidth = getScreenWidth();
        int screenHeight = getScreenHeight();

        // Log.i(getClass().getSimpleName(), "开始 x 坐标为：" + x);
        // Log.i(getClass().getSimpleName(), "开始 y 坐标为：" + y);

        if (x < safeInsetRect.left - getScreenInvisibleWidth()) {
            x = safeInsetRect.left - getScreenInvisibleWidth();
            // Log.i(getClass().getSimpleName(), "x 坐标已经触碰到屏幕左侧的安全区域");
        } else if (x > screenWidth - safeInsetRect.right - viewWidth) {
            x = screenWidth - safeInsetRect.right - viewWidth;
            // Log.i(getClass().getSimpleName(), "x 坐标已经触碰到屏幕右侧的安全区域");
        }

        // Log.i(getClass().getSimpleName(), "最终 x 坐标为：" + x);

        if (y < safeInsetRect.top - getScreenInvisibleHeight()) {
            y = safeInsetRect.top - getScreenInvisibleHeight();
            // Log.i(getClass().getSimpleName(), "y 坐标已经触碰到屏幕顶侧的安全区域");
        } else if (y > screenHeight - safeInsetRect.bottom - viewHeight) {
            y = screenHeight - safeInsetRect.bottom - viewHeight;
            // Log.i(getClass().getSimpleName(), "y 坐标已经触碰到屏幕底部的安全区域");
        }

        // Log.i(getClass().getSimpleName(), "最终 y 坐标为：" + y);

        updateWindowCoordinate(x, y);
    }

    public void updateWindowCoordinate(int x, int y) {
        if (mEasyWindow == null) {
            return;
        }
        WindowManager.LayoutParams params = mEasyWindow.getWindowParams();

        // 屏幕默认的重心（一定要先设置重心位置为左上角）
        int screenGravity = Gravity.LEFT | Gravity.TOP;

        // 判断本次移动的位置是否跟当前的窗口位置是否一致
        if (params.gravity == screenGravity && params.x == x && params.y == y) {
            return;
        }

        params.x = x;
        params.y = y;
        params.gravity = screenGravity;

        mEasyWindow.update();
    }

    /**
     * 获取当前屏幕安全区域
     */
    @Nullable
    public Rect getSafeInsetRect() {
        if (mEasyWindow == null) {
            return null;
        }
        Context context = mEasyWindow.getContext();
        Window window;
        if (!(context instanceof Activity)) {
            return null;
        }

        window = ((Activity) context).getWindow();
        if (window == null) {
            return null;
        }

        return getSafeInsetRect(window);
    }

    /**
     * 根据 Window 对象获取屏幕安全区域位置（返回的对象可能为空）
     */
    @Nullable
    public static Rect getSafeInsetRect(@Nullable Window window) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            return null;
        }

        View activityDecorView = null;
        if (window != null) {
            activityDecorView = window.getDecorView();
        }
        WindowInsets rootWindowInsets = null;
        if (activityDecorView != null) {
            rootWindowInsets = activityDecorView.getRootWindowInsets();
        }
        DisplayCutout displayCutout = null;
        if (rootWindowInsets != null) {
            displayCutout = rootWindowInsets.getDisplayCutout();
        }

        if (displayCutout != null) {
            // 安全区域距离屏幕左边的距离
            int safeInsetLeft = displayCutout.getSafeInsetLeft();
            // 安全区域距离屏幕顶部的距离
            int safeInsetTop = displayCutout.getSafeInsetTop();
            // 安全区域距离屏幕右部的距离
            int safeInsetRight = displayCutout.getSafeInsetRight();
            // 安全区域距离屏幕底部的距离
            int safeInsetBottom = displayCutout.getSafeInsetBottom();

            // Log.i(getClass().getSimpleName(), "安全区域距离屏幕左侧的距离 SafeInsetLeft：" + safeInsetLeft);
            // Log.i(getClass().getSimpleName(), "安全区域距离屏幕右侧的距离 SafeInsetRight：" + safeInsetRight);
            // Log.i(getClass().getSimpleName(), "安全区域距离屏幕顶部的距离 SafeInsetTop：" + safeInsetTop);
            // Log.i(getClass().getSimpleName(), "安全区域距离屏幕底部的距离 SafeInsetBottom：" + safeInsetBottom);

            return new Rect(safeInsetLeft, safeInsetTop, safeInsetRight, safeInsetBottom);
        }

        return null;
    }

    /**
     * 判断用户手指是否移动了，判断标准以下：
     * 根据手指按下和抬起时的坐标进行判断，不能根据有没有 move 事件来判断
     * 因为在有些机型上面，就算用户没有手指移动，只是简单点击也会产生 move 事件
     *
     * @param downX         手指按下时的 x 坐标
     * @param upX           手指抬起时的 x 坐标
     * @param downY         手指按下时的 y 坐标
     * @param upY           手指抬起时的 y 坐标
     */
    protected boolean isFingerMove(float downX, float upX, float downY, float upY) {
        float minTouchSlop = getMinTouchDistance();
        return Math.abs(downX - upX) >= minTouchSlop || Math.abs(downY - upY) >= minTouchSlop;
    }

    /**
     * 获取最小触摸距离
     */
    protected float getMinTouchDistance() {
        // 疑问一：为什么要使用 1dp 来作为最小触摸距离？
        //        这是因为用户点击的时候，手指 down 和 up 的坐标不相等，会存在一点误差
        //        在有些手机上面，误差会比较小，还有一些手机上面，误差会比较大
        //        经过拿不同的手机测试和验证，这个误差值可以锁定在 1dp 内
        //        当然我的结论不一定正确，你要是有发现新的问题也可以找我反馈，我会持续优化这个问题
        // 疑问二：为什么不使用 ViewConfiguration.get(context).getScaledTouchSlop() ？
        //        这是因为这个 API 获取到的数值太大了，有一定概率会出现误判，同样的手机上面
        //        用 getScaledTouchSlop 获取到的是 24，而系统 1dp 获取的到是 3px，
        //        两者相差太大，因为 getScaledTouchSlop API 默认获取的是 8dp * 3 = 24px
        // 疑问三：为什么要用 Resources.getSystem 来获取，而不是 context.getResources？
        //        这是因为如果用了 AutoSize 这个框架，上下文中的 1dp 就不是 3px 了
        //        使用 Resources.getSystem 能够保证 Resources 对象 dp 计算规则不被第三方框架篡改
        // 疑问四：为什么用屏幕的物理尺寸来算出最小触摸距离呢？
        //        这是因为在超大屏的设备上面，单击悬浮窗的误差就不止 1dp，可能是更大的值，所以需要更大的值来兼容
        //        Github issue：https://github.com/getActivity/EasyWindow/pull/79
        double screenPhysicalSize = getScreenPhysicalSize();
        int dpValue;
        if (screenPhysicalSize > 0) {
            // 市面上的平板最大尺寸不超过 15 英寸
            dpValue = (int) Math.ceil(screenPhysicalSize / 15);
        } else {
            dpValue = 1;
        }
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue,
            Resources.getSystem().getDisplayMetrics());
    }

    /**
     * 寻找需要消费触摸事件的 View（可能为空）
     */
    @Nullable
    protected View findNeedConsumeTouchView(@NonNull ViewGroup viewGroup, @NonNull MotionEvent event) {
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {

            View childView = viewGroup.getChildAt(i);
            int[] location = new int[2];
            childView.getLocationOnScreen(location);
            int left = location[0];
            int top = location[1];
            int right = left + childView.getWidth();
            int bottom = top + childView.getHeight();

            float x = event.getRawX();
            float y = event.getRawY();

            // 判断触摸位置是否在这个 View 内
            if (x >= left && x <= right && y >= top && y <= bottom) {
                if (isViewNeedConsumeTouchEvent(childView)) {
                    return childView;
                } else if (childView instanceof ViewGroup) {
                    return findNeedConsumeTouchView((ViewGroup) childView, event);
                }
            }
        }
        return null;
    }

    /**
     * 判断 View 是否需要消费当前触摸事件
     */
    protected boolean isViewNeedConsumeTouchEvent(@NonNull View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && view instanceof ViewGroup && view.isScrollContainer()) {
            return canTouchByView(view);
        }

        // NestedScrollingChild 的子类有：RecyclerView、NestedScrollView、SwipeRefreshLayout 等等
        if (view instanceof NestedScrollingChild || view instanceof NestedScrollingParent || view instanceof WebView ||
            view instanceof ScrollView || view instanceof ListView || view instanceof SeekBar || view instanceof ViewPager) {
            return canTouchByView(view);
        }

        Class<? extends View> viewClass = view.getClass();
        try {
            if (viewClass.isAssignableFrom(Class.forName("androidx.viewpager2.widget.ViewPager2"))) {
                return canTouchByView(view);
            }
        } catch (ClassNotFoundException ignored) {
            // default implementation ignored
        }

        return false;
    }

    /**
     * 判断 View 是否能被触摸
     */
    protected boolean canTouchByView(@NonNull View view) {
        if (view instanceof RecyclerView && !canScrollByRecyclerView(((RecyclerView) view))) {
            // 如果这个 RecyclerView 禁止了触摸事件，就不要启动触摸事件
            return false;
        }

        // 这个 View 必须是启用状态，才认为有可能传递触摸事件
        return view.isEnabled();
    }

    /**
     * 判断 RecyclerView 是否能被触摸
     */
    protected boolean canScrollByRecyclerView(@NonNull RecyclerView recyclerView) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager == null) {
            // 如果没有设置 LayoutManager，则默认不需要触摸事件
            return false;
        }

        // 当前这个 LayoutManager 必须开启垂直滚动或者水平滚动
        return layoutManager.canScrollVertically() || layoutManager.canScrollHorizontally();
    }

    /**
     * 获取屏幕的物理尺寸（单位：英寸）
     */
    protected double getScreenPhysicalSize() {
        return mScreenPhysicalSize;
    }

    /**
     * 判断当前悬浮窗是否可以移动到屏幕之外的地方
     */
    protected boolean isSupportMoveOffScreen() {
        if (mEasyWindow == null) {
            return false;
        }
        return mEasyWindow.hasWindowFlags(LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    /**
     * 设置拖拽回调
     */
    public void setWindowDraggingListener(@Nullable OnWindowDraggingListener callback) {
        mWindowDraggingListener = callback;
    }

    /**
     * 派发开始拖拽事件
     */
    protected void dispatchStartDraggingCallback() {
        // Log.i(getClass().getSimpleName(), "开始拖拽");
        if (mEasyWindow == null) {
            return;
        }
        if (mWindowDraggingListener == null) {
            return;
        }
        mWindowDraggingListener.onWindowDraggingStart(mEasyWindow);
    }

    /**
     * 派发拖拽中事件
     */
    protected void dispatchRunningDraggingCallback() {
        // Log.i(getClass().getSimpleName(), "拖拽中");
        if (mEasyWindow == null) {
            return;
        }
        if (mWindowDraggingListener == null) {
            return;
        }
        mWindowDraggingListener.onWindowDraggingRunning(mEasyWindow);
    }

    /**
     * 派发停止拖拽事件
     */
    protected void dispatchStopDraggingCallback() {
        // Log.i(getClass().getSimpleName(), "停止拖拽");
        if (mEasyWindow == null) {
            return;
        }
        if (mWindowDraggingListener == null) {
            return;
        }
        mWindowDraggingListener.onWindowDraggingStop(mEasyWindow);
    }

    public interface OnWindowDraggingListener {

        /**
         * 开始拖拽
         */
        default void onWindowDraggingStart(@NonNull EasyWindow<?> easyWindow) {
            // default implementation ignored
        }

        /**
         * 拖拽中
         */
        default void onWindowDraggingRunning(@NonNull EasyWindow<?> easyWindow) {
            // default implementation ignored
        }

        /**
         * 停止拖拽
         */
        default void onWindowDraggingStop(@NonNull EasyWindow<?> easyWindow) {
            // default implementation ignored
        }
    }
}