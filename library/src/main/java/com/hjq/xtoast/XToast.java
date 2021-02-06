package com.hjq.xtoast;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hjq.xtoast.draggable.BaseDraggable;
import com.hjq.xtoast.draggable.MovingDraggable;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XToast
 *    time   : 2019/01/04
 *    desc   : 超级 Toast（能做 Toast 做不到的事，应付项目中的特殊需求）
 */
@SuppressWarnings("unchecked")
public class XToast<X extends XToast<?>> {

    private static final Handler HANDLER = new Handler(Looper.getMainLooper());

    /** 上下文 */
    private Context mContext;
    /** 根布局 */
    private View mRootView;
    /** 悬浮窗口 */
    private WindowManager mWindowManager;
    /** 窗口参数 */
    private WindowManager.LayoutParams mWindowParams;

    /** 当前是否已经显示 */
    private boolean mShow;
    /** 窗口显示时长 */
    private int mDuration;
    /** Toast 生命周期管理 */
    private ToastLifecycle mLifecycle;
    /** 自定义拖动处理 */
    private BaseDraggable mDraggable;
    /** 吐司显示和取消监听 */
    private OnToastListener mListener;

    /**
     * 创建一个局部悬浮窗
     */
    public XToast(Activity activity) {
        this((Context) activity);

        if ((activity.getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) != 0 ||
                (activity.getWindow().getDecorView().getSystemUiVisibility() & View.SYSTEM_UI_FLAG_FULLSCREEN) != 0) {
            // 如果当前 Activity 是全屏模式，那么需要添加这个标记，否则会导致 WindowManager 在某些机型上移动不到状态栏的位置上
            // 如果不想让状态栏显示的时候把 WindowManager 顶下来，可以添加 FLAG_LAYOUT_IN_SCREEN，但是会导致软键盘无法调整窗口位置
            addWindowFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.TYPE_STATUS_BAR);
        }

        // 跟随 Activity 的生命周期
        mLifecycle = new ToastLifecycle(this, activity);
    }

    /**
     * 创建一个全局悬浮窗
     */
    public XToast(Application application) {
        this((Context) application);

        // 设置成全局的悬浮窗，注意需要先申请悬浮窗权限，推荐使用：https://github.com/getActivity/XXPermissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setWindowType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        } else {
            setWindowType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
    }

    private XToast(Context context) {
        mContext = context;
        mWindowManager = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE));
        // 配置一些默认的参数
        mWindowParams = new WindowManager.LayoutParams();
        mWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowParams.format = PixelFormat.TRANSLUCENT;
        mWindowParams.windowAnimations = android.R.style.Animation_Toast;
        mWindowParams.packageName = context.getPackageName();
        // 开启窗口常亮和设置可以触摸外层布局（除 WindowManager 外的布局，默认是 WindowManager 显示的时候外层不可触摸）
        // 需要注意的是设置了 FLAG_NOT_TOUCH_MODAL 必须要设置 FLAG_NOT_FOCUSABLE，否则就会导致用户按返回键无效
        mWindowParams.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
    }

    /**
     * 是否外层可触摸
     */
    public X setOutsideTouchable(boolean touchable) {
        int flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        if (touchable) {
            addWindowFlags(flags);
        } else {
            clearWindowFlags(flags);
        }
        if (isShow()) {
            update();
        }
        return (X) this;
    }

    /**
     * 设置窗口背景阴影强度
     */
    public X setBackgroundDimAmount(float amount) {
        if (amount < 0 || amount > 1) {
            throw new IllegalArgumentException("are you ok?");
        }
        mWindowParams.dimAmount = amount;
        int flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        if (amount != 0) {
            addWindowFlags(flags);
        } else {
            clearWindowFlags(flags);
        }
        if (isShow()) {
            update();
        }
        return (X) this;
    }

    /**
     * 是否有这个标志位
     */
    public boolean hasWindowFlags(int flags) {
        return (mWindowParams.flags & flags) != 0;
    }

    /**
     * 添加一个标记位
     */
    public X addWindowFlags(int flags) {
        mWindowParams.flags |= flags;
        if (isShow()) {
            update();
        }
        return (X) this;
    }

    /**
     * 移除一个标记位
     */
    public X clearWindowFlags(int flags) {
        mWindowParams.flags &= ~flags;
        if (isShow()) {
            update();
        }
        return (X) this;
    }

    /**
     * 设置标记位
     */
    public X setWindowFlags(int flags) {
        mWindowParams.flags = flags;
        if (isShow()) {
            update();
        }
        return (X) this;
    }

    /**
     * 设置窗口类型
     */
    public X setWindowType(int type) {
        mWindowParams.type = type;
        if (isShow()) {
            update();
        }
        return (X) this;
    }

    /**
     * 设置动画样式
     */
    public X setAnimStyle(int id) {
        mWindowParams.windowAnimations = id;
        if (isShow()) {
            update();
        }
        return (X) this;
    }

    /**
     * 设置随意拖动
     */
    public X setDraggable() {
        return setDraggable(new MovingDraggable());
    }

    /**
     * 设置拖动规则
     */
    public X setDraggable(BaseDraggable draggable) {
        // 当前是否设置了不可触摸，如果是就擦除掉这个标记
        if (hasWindowFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)) {
            clearWindowFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
        mDraggable = draggable;
        if (isShow()) {
            update();
            mDraggable.start(this);
        }
        return (X) this;
    }

    /**
     * 设置宽度
     */
    public X setWidth(int width) {
        mWindowParams.width = width;
        if (isShow()) {
            update();
        }
        return (X) this;
    }

    /**
     * 设置高度
     */
    public X setHeight(int height) {
        mWindowParams.height = height;
        if (isShow()) {
            update();
        }
        return (X) this;
    }

    /**
     * 限定显示时长
     */
    public X setDuration(int duration) {
        mDuration = duration;
        if (isShow() && mDuration != 0) {
            removeCallbacks();
            postDelayed(new CancelRunnable(this), mDuration);
        }
        return (X) this;
    }

    /**
     * 设置监听
     */
    public X setOnToastListener(OnToastListener listener) {
        mListener = listener;
        return (X) this;
    }

    /**
     * 设置窗口重心
     */
    public X setGravity(int gravity) {
        mWindowParams.gravity = gravity;
        if (isShow()) {
            update();
        }
        return (X) this;
    }

    /**
     * 设置窗口方向
     *
     * 自适应：{@link ActivityInfo#SCREEN_ORIENTATION_UNSPECIFIED}
     * 横屏：{@link ActivityInfo#SCREEN_ORIENTATION_LANDSCAPE}
     * 竖屏：{@link ActivityInfo#SCREEN_ORIENTATION_PORTRAIT}
     */
    public X setOrientation(int orientation) {
        mWindowParams.screenOrientation = orientation;
        if (isShow()) {
            update();
        }
        return (X) this;
    }

    /**
     * 设置水平偏移量
     */
    public X setXOffset(int x) {
        mWindowParams.x = x;
        if (isShow()) {
            update();
        }
        return (X) this;
    }

    /**
     * 设置垂直偏移量
     */
    public X setYOffset(int y) {
        mWindowParams.y = y;
        if (isShow()) {
            update();
        }
        return (X) this;
    }

    /**
     * 重新设置 WindowManager 参数集
     */
    public X setWindowParams(WindowManager.LayoutParams params) {
        mWindowParams = params;
        if (isShow()) {
            update();
        }
        return (X) this;
    }

    /**
     * 设置布局
     */
    public X setView(int id) {
        return setView(LayoutInflater.from(mContext).inflate(id, new FrameLayout(mContext), false));
    }

    public X setView(View view) {
        mRootView = view;

        ViewGroup.LayoutParams params = mRootView.getLayoutParams();
        if (params != null && mWindowParams.width == WindowManager.LayoutParams.WRAP_CONTENT &&
                mWindowParams.height == WindowManager.LayoutParams.WRAP_CONTENT) {
            // 如果当前 Dialog 的宽高设置了自适应，就以布局中设置的宽高为主
            setWidth(params.width);
            setHeight(params.height);
        }

        // 如果当前没有设置重心，就自动获取布局重心
        if (mWindowParams.gravity == Gravity.NO_GRAVITY) {
            if (params instanceof FrameLayout.LayoutParams) {
                setGravity(((FrameLayout.LayoutParams) params).gravity);
            } else if (params instanceof LinearLayout.LayoutParams) {
                setGravity(((LinearLayout.LayoutParams) params).gravity);
            } else {
                // 默认重心是居中
                setGravity(Gravity.CENTER);
            }
        }

        if (isShow()) {
            update();
        }
        return (X) this;
    }

    /**
     * 显示悬浮窗
     */
    public X show() {
        if (mRootView == null || mWindowParams == null) {
            throw new IllegalArgumentException("WindowParams and view cannot be empty");
        }

        // 如果当前已经显示则进行更新
        if (mShow) {
            update();
            return (X) this;
        }

        if (mContext instanceof Activity) {
            if (((Activity) mContext).isFinishing() ||
                    (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 &&
                            ((Activity) mContext).isDestroyed())) {
                return (X) this;
            }
        }

        try {
            mWindowManager.addView(mRootView, mWindowParams);
            // 当前已经显示
            mShow = true;
            // 如果当前限定了显示时长
            if (mDuration != 0) {
                postDelayed(new CancelRunnable(this), mDuration);
            }
            // 如果设置了拖拽规则
            if (mDraggable != null) {
                mDraggable.start(this);
            }

            // 注册 Activity 生命周期
            if (mLifecycle != null) {
                mLifecycle.register();
            }

            // 回调监听
            if (mListener != null) {
                mListener.onShow(this);
            }
        } catch (NullPointerException | IllegalStateException | WindowManager.BadTokenException e) {
            // 如果这个 View 对象被重复添加到 WindowManager 则会抛出异常
            // java.lang.IllegalStateException: View android.widget.TextView{3d2cee7 V.ED..... ......ID 0,0-312,153} has already been added to the window manager.
            e.printStackTrace();
        }

        return (X) this;
    }

    /**
     * 销毁悬浮窗
     */
    public X cancel() {
        if (!mShow) {
            return (X) this;
        }

        try {

            // 反注册 Activity 生命周期
            if (mLifecycle != null) {
                mLifecycle.unregister();
            }

            // 如果当前 WindowManager 没有附加这个 View 则会抛出异常
            // java.lang.IllegalArgumentException: View=android.widget.TextView{3d2cee7 V.ED..... ........ 0,0-312,153} not attached to window manager
            mWindowManager.removeViewImmediate(mRootView);

            // 回调监听
            if (mListener != null) {
                mListener.onDismiss(this);
            }

        } catch (NullPointerException | IllegalArgumentException | IllegalStateException e) {
            e.printStackTrace();
        } finally {
            // 当前没有显示
            mShow = false;
        }

        return (X) this;
    }

    /**
     * 刷新悬浮窗
     */
    public void update() {
        // 更新 WindowManger 的显示
        mWindowManager.updateViewLayout(mRootView, mWindowParams);
    }

    /**
     * 回收操作
     */
    public void recycle() {
        mContext = null;
        mRootView = null;
        mWindowManager = null;
        mWindowParams = null;
        mLifecycle = null;
        mDraggable = null;
        mListener = null;
    }

    /**
     * 当前是否已经显示
     */
    public boolean isShow() {
        return mShow;
    }

    /**
     * 获取 WindowManager 对象
     */
    public WindowManager getWindowManager() {
        return mWindowManager;
    }

    /**
     * 获取 WindowManager 参数集
     */
    public WindowManager.LayoutParams getWindowParams() {
        return mWindowParams;
    }

    /**
     * 获取上下文对象
     */
    public Context getContext() {
        return mContext;
    }

    /**
     * 获取根布局
     */
    public View getView() {
        return mRootView;
    }

    /**
     * 根据 ViewId 获取 View
     */
    public <V extends View> V findViewById(int id) {
        if (mRootView == null) {
            throw new IllegalStateException("Please setup view");
        }
        return (V) mRootView.findViewById(id);
    }

    /**
     * 跳转 Activity
     */
    public void startActivity(Class<? extends Activity> clazz) {
        startActivity(new Intent(mContext, clazz));
    }

    public void startActivity(Intent intent) {
        if (!(mContext instanceof Activity)) {
            // 如果当前的上下文不是 Activity，调用 startActivity 必须加入新任务栈的标记
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        mContext.startActivity(intent);
    }

    /**
     * 设置可见状态
     */
    public X setVisibility(int id, int visibility) {
        findViewById(id).setVisibility(visibility);
        return (X) this;
    }

    /**
     * 设置文本
     */
    public X setText(int id) {
        return setText(android.R.id.message, id);
    }

    public X setText(int viewId, int stringId) {
        return setText(viewId, mContext.getResources().getString(stringId));
    }

    public X setText(CharSequence text) {
        return setText(android.R.id.message, text);
    }

    public X setText(int id, CharSequence text) {
        ((TextView) findViewById(id)).setText(text);
        return (X) this;
    }

    /**
     * 设置文本颜色
     */
    public X setTextColor(int id, int color) {
        ((TextView) findViewById(id)).setTextColor(color);
        return (X) this;
    }

    /**
     * 设置提示
     */
    public X setHint(int viewId, int stringId) {
        return setHint(viewId, mContext.getResources().getString(stringId));
    }
    public X setHint(int id, CharSequence text) {
        ((TextView) findViewById(id)).setHint(text);
        return (X) this;
    }

    /**
     * 设置提示文本颜色
     */
    public X setHintColor(int id, int color) {
        ((TextView) findViewById(id)).setHintTextColor(color);
        return (X) this;
    }

    /**
     * 设置背景
     */
    public X setBackground(int viewId, int drawableId) {
        Drawable drawable;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawable = mContext.getDrawable(drawableId);
        } else {
            drawable = mContext.getResources().getDrawable(drawableId);
        }
        return setBackground(viewId, drawable);
    }

    public X setBackground(int id, Drawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            findViewById(id).setBackground(drawable);
        } else {
            findViewById(id).setBackgroundDrawable(drawable);
        }
        return (X) this;
    }

    /**
     * 设置图片
     */
    public X setImageDrawable(int viewId, int drawableId) {
        Drawable drawable;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawable = mContext.getDrawable(drawableId);
        } else {
            drawable = mContext.getResources().getDrawable(drawableId);
        }
        return setImageDrawable(viewId, drawable);
    }

    public X setImageDrawable(int viewId, Drawable drawable) {
        ((ImageView) findViewById(viewId)).setImageDrawable(drawable);
        return (X) this;
    }

    /**
     * 设置点击事件
     */
    public X setOnClickListener(OnClickListener<? extends View> listener) {
        return setOnClickListener(mRootView, listener);
    }

    public X setOnClickListener(int id, OnClickListener<? extends View> listener) {
        return setOnClickListener(findViewById(id), listener);
    }

    private X setOnClickListener(View view, OnClickListener<? extends View> listener) {
        // 当前是否设置了不可触摸，如果是就擦除掉
        if (hasWindowFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)) {
            clearWindowFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
        new ViewClickWrapper(this, view, listener);
        return (X) this;
    }

    /**
     * 设置触摸事件
     */
    public X setOnTouchListener(OnTouchListener<? extends View> listener) {
        return setOnTouchListener(mRootView, listener);
    }

    public X setOnTouchListener(int id, OnTouchListener<? extends View> listener) {
        return setOnTouchListener(findViewById(id), listener);
    }

    private X setOnTouchListener(View view, OnTouchListener<? extends View> listener) {
        // 当前是否设置了不可触摸，如果是就擦除掉
        if (hasWindowFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)) {
            clearWindowFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
        new ViewTouchWrapper(this, view, listener);
        return (X) this;
    }

    /**
     * 获取 Handler
     */
    public Handler getHandler() {
        return HANDLER;
    }

    /**
     * 延迟执行
     */
    public boolean post(Runnable r) {
        return postDelayed(r, 0);
    }

    /**
     * 延迟一段时间执行
     */
    public boolean postDelayed(Runnable r, long delayMillis) {
        if (delayMillis < 0) {
            delayMillis = 0;
        }
        return postAtTime(r, SystemClock.uptimeMillis() + delayMillis);
    }

    /**
     * 在指定的时间执行
     */
    public boolean postAtTime(Runnable r, long uptimeMillis) {
        // 发送和这个 WindowManager 相关的消息回调
        return HANDLER.postAtTime(r, this, uptimeMillis);
    }

    /**
     * 移除消息回调
     */
    public void removeCallbacks() {
        HANDLER.removeCallbacksAndMessages(this);
    }
}