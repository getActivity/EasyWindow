package com.hjq.window.draggable;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Build;
import android.util.TypedValue;
import android.view.DisplayCutout;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnLayoutChangeListener;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import com.hjq.window.EasyWindow;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyWindow
 *    time   : 2019/01/04
 *    desc   : 拖拽抽象类
 */
public abstract class BaseDraggable implements View.OnTouchListener {

    private EasyWindow<?> mEasyWindow;
    private View mDecorView;

    /** 是否允许移动到挖孔屏区域 */
    private boolean mAllowMoveToScreenNotch = true;

    /** 拖拽回调监听 */
    private DraggingCallback mDraggingCallback;

    private final Rect mTempRect = new Rect();

    private int mCurrentWindowWidth;
    private int mCurrentWindowHeight;
    private int mCurrentViewOnScreenX;
    private int mCurrentViewOnScreenY;
    private int mCurrentWindowInvisibleWidth;
    private int mCurrentWindowInvisibleHeight;

    /**
     * Toast 显示后回调这个类
     */
    @SuppressLint("ClickableViewAccessibility")
    public void start(EasyWindow<?> easyWindow) {
        mEasyWindow = easyWindow;
        mDecorView = easyWindow.getDecorView();
        mDecorView.setOnTouchListener(this);
        mDecorView.post(() -> {
            refreshWindowInfo();
            refreshLocationCoordinate();
        });
    }

    public EasyWindow<?> getEasyWindow() {
        return mEasyWindow;
    }

    public View getDecorView() {
        return mDecorView;
    }

    public void setAllowMoveToScreenNotch(boolean allowMoveToScreenNotch) {
        mAllowMoveToScreenNotch = allowMoveToScreenNotch;
    }

    public boolean isAllowMoveToScreenNotch() {
        return mAllowMoveToScreenNotch;
    }

    /**
     * 获取当前 Window 的宽度
     */
    public int getWindowWidth() {
        return mCurrentWindowWidth;
    }

    /**
     * 获取当前 Window 的高度
     */
    public int getWindowHeight() {
        return mCurrentWindowHeight;
    }

    /**
     * 获取当前 View 的宽度
     */
    public int getViewWidth() {
        return mEasyWindow.getViewWidth();
    }

    /**
     * 获取当前 View 的高度
     */
    public int getViewHeight() {
        return mEasyWindow.getViewHeight();
    }

    /**
     * 获取窗口不可见的宽度，一般情况下为横屏状态下刘海的高度
     */
    public int getWindowInvisibleWidth() {
        return mCurrentWindowInvisibleWidth;
    }

    /**
     * 获取窗口不可见的高度，一般情况下为状态栏的高度
     */
    public int getWindowInvisibleHeight() {
        return mCurrentWindowInvisibleHeight;
    }

    /**
     * 获取 View 在当前屏幕的 X 坐标
     */
    public int getViewOnScreenX() {
        return mCurrentViewOnScreenX;
    }

    /**
     * 获取 View 在当前屏幕的 Y 坐标
     */
    public int getViewOnScreenY() {
        return mCurrentViewOnScreenY;
    }

    /**
     * 刷新当前 Window 信息
     */
    public void refreshWindowInfo() {
        View decorView = getDecorView();
        if (decorView == null) {
            return;
        }

        // Log.i(getClass().getSimpleName(), "刷新当前 Window 信息");

        // 这里为什么要这么写，因为发现了鸿蒙手机在进行屏幕旋转的时候
        // 回调 onConfigurationChanged 方法的时候获取到这些参数已经变化了
        // 所以需要提前记录下来，避免后续进行坐标计算的时候出现问题
        decorView.getWindowVisibleDisplayFrame(mTempRect);
        mCurrentWindowWidth = mTempRect.right - mTempRect.left;
        mCurrentWindowHeight = mTempRect.bottom - mTempRect.top;

        mCurrentWindowInvisibleWidth = mTempRect.left;
        mCurrentWindowInvisibleHeight = mTempRect.top;

        /*
        Log.i(getClass().getSimpleName(),
            "CurrentWindowWidth = " + mCurrentWindowWidth +
            "，CurrentWindowHeight = " + mCurrentWindowHeight +
            "，CurrentWindowInvisibleWidth = " + mCurrentWindowInvisibleWidth +
            "，CurrentWindowInvisibleHeight = " + mCurrentWindowInvisibleHeight);
         */
    }

    /**
     * 刷新当前 View 在屏幕的坐标信息
     */
    public void refreshLocationCoordinate() {
        View decorView = getDecorView();
        if (decorView == null) {
            return;
        }

        int[] location = new int[2];
        decorView.getLocationOnScreen(location);
        mCurrentViewOnScreenX = location[0];
        mCurrentViewOnScreenY = location[1];
    }

    /**
     * 屏幕方向发生了改变
     */
    public void onScreenOrientationChange() {
        // Log.i(getClass().getSimpleName(), "屏幕方向发生了改变");

        long refreshDelayMillis = 100;

        if (!isFollowScreenRotationChanges()) {
            getEasyWindow().postDelayed(() -> {
                refreshWindowInfo();
                refreshLocationCoordinate();
            }, refreshDelayMillis);
            return;
        }

        int viewWidth = getDecorView().getWidth();
        int viewHeight = getDecorView().getHeight();

        // Log.i(getClass().getSimpleName(), "当前 ViewWidth = " + viewWidth + "，ViewHeight = " + viewHeight);

        int startX = mCurrentViewOnScreenX - mCurrentWindowInvisibleWidth;
        int startY = mCurrentViewOnScreenY - mCurrentWindowInvisibleHeight;

        float percentX;
        // 这里为什么用 getMinTouchDistance()，而不是 0？
        // 因为其实用 getLocationOnScreen 测量出来的值不太准，有时候是 0，有时候是 1，有时候 2
        // 但大多数情况是 0 和 1，这里为了兼容这种误差，使用了最小触摸距离来作为基准值
        float minTouchDistance = getMinTouchDistance();

        if (startX <= minTouchDistance) {
            percentX = 0;
        } else if (Math.abs(mCurrentWindowWidth - (startX + viewWidth)) < minTouchDistance) {
            percentX = 1;
        } else {
            float centerX = startX + viewWidth / 2f;
            percentX = centerX / mCurrentWindowWidth;
        }

        float percentY;
        if (startY <= minTouchDistance) {
            percentY = 0;
        } else if (Math.abs(mCurrentWindowHeight - (startY + viewHeight)) < minTouchDistance) {
            percentY = 1;
        } else {
            float centerY = startY + viewHeight / 2f;
            percentY = centerY / mCurrentWindowHeight;
        }

        View decorView = getDecorView();
        if (decorView == null) {
            return;
        }

        // Github issue 地址：https://github.com/getActivity/EasyWindow/issues/49
        // 修复在竖屏状态下，先锁屏，再旋转到横屏，后进行解锁，出现的 View.getWindowVisibleDisplayFrame 计算有问题的 Bug
        // 这是因为屏幕在旋转的时候，视图正处于改变状态，此时通过 View 获取窗口可视区域是有问题，会获取到旧的可视区域
        // 解决方案是监听一下 View 布局变化监听，在收到回调的时候再去获取 View 获取窗口可视区域
        decorView.addOnLayoutChangeListener(new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                view.removeOnLayoutChangeListener(this);
                view.postDelayed(() -> {
                    // 先刷新当前窗口信息
                    refreshWindowInfo();
                    int x = Math.max((int) (mCurrentWindowWidth * percentX - viewWidth / 2f), 0);
                    int y = Math.max((int) (mCurrentWindowHeight * percentY - viewWidth / 2f), 0);
                    updateLocation(x, y);
                    // 需要注意，这里需要延迟执行，否则会有问题
                    view.post(() -> onScreenRotateInfluenceCoordinateChangeFinish());
                }, refreshDelayMillis);
            }
        });
    }

    /**
     * 屏幕旋转导致悬浮窗坐标发生变化完成方法
     */
    protected void onScreenRotateInfluenceCoordinateChangeFinish() {
        refreshWindowInfo();
        refreshLocationCoordinate();
    }

    /**
     * 悬浮窗是否跟随屏幕方向变化而发生变化
     */
    public boolean isFollowScreenRotationChanges() {
        return true;
    }

    public void updateLocation(float x, float y) {
        updateLocation(x, y, isAllowMoveToScreenNotch());
    }

    public void updateLocation(float x, float y, boolean allowMoveToScreenNotch) {
        updateLocation((int) x, (int) y, allowMoveToScreenNotch);
    }

    /**
     * 更新悬浮窗的位置
     *
     * @param x                                 x 坐标（相对与屏幕左上位置）
     * @param y                                 y 坐标（相对与屏幕左上位置）
     * @param allowMoveToScreenNotch            是否允许移动到挖孔屏的区域
     */
    public void updateLocation(int x, int y, boolean allowMoveToScreenNotch) {
        if (allowMoveToScreenNotch) {
            updateWindowCoordinate(x, y);
            return;
        }

        Rect safeInsetRect = getSafeInsetRect();
        if (safeInsetRect == null) {
            updateWindowCoordinate(x, y);
            return;
        }

        if (safeInsetRect.left > 0 && safeInsetRect.right > 0 &&
            safeInsetRect.top > 0 && safeInsetRect.bottom > 0) {
            updateWindowCoordinate(x, y);
            return;
        }

        int viewWidth = mEasyWindow.getViewWidth();
        int viewHeight = mEasyWindow.getViewHeight();

        int windowWidth = getWindowWidth();
        int windowHeight = getWindowHeight();

        // Log.i(getClass().getSimpleName(), "开始 x 坐标为：" + x);
        // Log.i(getClass().getSimpleName(), "开始 y 坐标为：" + y);

        if (x < safeInsetRect.left - getWindowInvisibleWidth()) {
            x = safeInsetRect.left - getWindowInvisibleWidth();
            // Log.i(getClass().getSimpleName(), "x 坐标已经触碰到屏幕左侧的安全区域");
        } else if (x > windowWidth - safeInsetRect.right - viewWidth) {
            x = windowWidth - safeInsetRect.right - viewWidth;
            // Log.i(getClass().getSimpleName(), "x 坐标已经触碰到屏幕右侧的安全区域");
        }

        // Log.i(getClass().getSimpleName(), "最终 x 坐标为：" + x);

        if (y < safeInsetRect.top - getWindowInvisibleHeight()) {
            y = safeInsetRect.top - getWindowInvisibleHeight();
            // Log.i(getClass().getSimpleName(), "y 坐标已经触碰到屏幕顶侧的安全区域");
        } else if (y > windowHeight - safeInsetRect.bottom - viewHeight) {
            y = windowHeight - safeInsetRect.bottom - viewHeight;
            // Log.i(getClass().getSimpleName(), "y 坐标已经触碰到屏幕底部的安全区域");
        }

        // Log.i(getClass().getSimpleName(), "最终 y 坐标为：" + y);

        updateWindowCoordinate(x, y);
    }

    public void updateWindowCoordinate(int x, int y) {
        WindowManager.LayoutParams params = mEasyWindow.getWindowParams();
        if (params == null) {
            return;
        }

        // 屏幕默认的重心（一定要先设置重心位置为左上角）
        int screenGravity = Gravity.TOP | Gravity.START;

        // 判断本次移动的位置是否跟当前的窗口位置是否一致
        if (params.gravity == screenGravity && params.x == x && params.y == y) {
            return;
        }

        params.x = x;
        params.y = y;
        params.gravity = screenGravity;

        mEasyWindow.update();
        refreshLocationCoordinate();
    }

    public Rect getSafeInsetRect() {
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
     * 获取屏幕安全区域位置（返回的对象可能为空）
     */
    public static Rect getSafeInsetRect(Window window) {
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
        //        用 getScaledTouchSlop 获取到的是 24，而系统 1dp 获取的到是 3，
        //        两者相差太大，因为 getScaledTouchSlop API 默认获取的是 8dp * 3 = 24px
        // 疑问三：为什么要用 Resources.getSystem 来获取，而不是 context.getResources？
        //        这是因为如果用了 AutoSize 这个框架，上下文中的 1dp 就不是 3px 了
        //        使用 Resources.getSystem 能够保证 Resources 对象 dp 计算规则不被第三方框架篡改
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1,
                Resources.getSystem().getDisplayMetrics());
    }

    /**
     * 设置拖拽回调
     */
    public void setDraggingCallback(DraggingCallback callback) {
        mDraggingCallback = callback;
    }

    /**
     * 派发开始拖拽事件
     */
    protected void dispatchStartDraggingCallback() {
        // Log.i(getClass().getSimpleName(), "开始拖拽");
        if (mDraggingCallback == null) {
            return;
        }
        mDraggingCallback.onStartDragging(mEasyWindow);
    }

    /**
     * 派发拖拽中事件
     */
    protected void dispatchExecuteDraggingCallback() {
        // Log.i(getClass().getSimpleName(), "拖拽中");
        if (mDraggingCallback == null) {
            return;
        }
        mDraggingCallback.onExecuteDragging(mEasyWindow);
    }

    /**
     * 派发停止拖拽事件
     */
    protected void dispatchStopDraggingCallback() {
        // Log.i(getClass().getSimpleName(), "停止拖拽");
        if (mDraggingCallback == null) {
            return;
        }
        mDraggingCallback.onStopDragging(mEasyWindow);
    }

    public interface DraggingCallback {

        /**
         * 开始拖拽
         */
        default void onStartDragging(EasyWindow<?> easyWindow) {}

        /**
         * 执行拖拽中
         */
        default void onExecuteDragging(EasyWindow<?> easyWindow) {}

        /**
         * 停止拖拽
         */
        default void onStopDragging(EasyWindow<?> easyWindow) {}
    }
}