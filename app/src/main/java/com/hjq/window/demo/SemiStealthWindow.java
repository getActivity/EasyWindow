package com.hjq.window.demo;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import androidx.annotation.NonNull;
import com.hjq.toast.Toaster;
import com.hjq.window.EasyWindow;
import com.hjq.window.OnWindowLifecycleCallback;
import com.hjq.window.OnWindowViewClickListener;
import com.hjq.window.draggable.AbstractWindowDraggableRule;
import com.hjq.window.draggable.AbstractWindowDraggableRule.OnWindowDraggingListener;
import com.hjq.window.draggable.SpringBackWindowDraggableRule;
import com.hjq.window.draggable.SpringBackWindowDraggableRule.SpringBackAnimCallback;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyWindow
 *    time   : 2025/06/27
 *    desc   : 拖拽后半隐的悬浮窗
 */
public final class SemiStealthWindow extends EasyWindow<SemiStealthWindow>
                                    implements OnWindowDraggingListener,
                                                SpringBackAnimCallback,
                                                OnWindowViewClickListener<View>,
                                                OnWindowLifecycleCallback {

    /** 贴边间隔时间 */
    private static final int STAY_EDGE_INTERVAL_TIME = 3000;

    /** 动画是否进行中 */
    private boolean mAnimatingRunning;

    /** 拖拽是否进行中 */
    private boolean mDraggingRunning;

    public SemiStealthWindow(@NonNull Activity activity) {
        super(activity);
    }

    @Override
    protected void initWindow(@NonNull Context context) {
        super.initWindow(context);

        setContentView(R.layout.window_semi_stealth);

        SpringBackWindowDraggableRule windowDraggableRule = new SpringBackWindowDraggableRule(
            SpringBackWindowDraggableRule.ORIENTATION_HORIZONTAL);
        windowDraggableRule.setAllowMoveToScreenSafeArea(false);
        windowDraggableRule.setWindowDraggingListener(this);
        windowDraggableRule.setSpringBackAnimCallback(this);
        setWindowDraggableRule(windowDraggableRule);

        setOnClickListenerByView(android.R.id.icon, this);
        setOnWindowLifecycleCallback(this);

        int x = 0;
        if (!windowDraggableRule.isAllowMoveToScreenSafeArea() && context instanceof Activity) {
            Rect safeInsetRect = AbstractWindowDraggableRule.getSafeInsetRect(((Activity) context).getWindow());
            if (safeInsetRect != null) {
                x = safeInsetRect.left;
            }
        }
        setWindowLocation(x, 200);
    }

    /**
     * 发送贴边显示任务
     */
    public void postStayEdgeRunnable() {
        cancelTask(mStayEdgeRunnable);
        sendTask(mStayEdgeRunnable, STAY_EDGE_INTERVAL_TIME);
    }

    @SuppressLint("RtlHardcoded")
    private final Runnable mStayEdgeRunnable = () -> {
        if (mAnimatingRunning || mDraggingRunning) {
            return;
        }

        AbstractWindowDraggableRule windowDraggableRule = getWindowDraggableRule();
        if (windowDraggableRule == null) {
            return;
        }

        if ((getWindowParams().x + getWindowViewWidth() / 2f)  < windowDraggableRule.getScreenWidth() / 2f) {
            toHalfShow(Gravity.LEFT);
        } else {
            toHalfShow(Gravity.RIGHT);
        }
    };

    /**
     * 开启半边显示
     */
    @SuppressLint("RtlHardcoded")
    private void toHalfShow(int gravity) {
        AbstractWindowDraggableRule windowDraggableRule = getWindowDraggableRule();
        if (windowDraggableRule == null) {
            return;
        }
        View rootLayout = getRootLayout();
        if (rootLayout == null) {
            return;
        }

        int viewWidth = getWindowViewWidth();
        int viewHeight = getWindowViewHeight();

        // 创建一个矩形来定义裁剪区域
        Rect clipBounds = new Rect();
        switch (gravity) {
            case Gravity.LEFT:
                WindowManager.LayoutParams windowParams = getWindowParams();
                Rect safeInsetRect = windowDraggableRule.getSafeInsetRect();
                if (safeInsetRect != null && safeInsetRect.left > 0 && windowParams.x > 0) {
                    windowDraggableRule.updateLocation(windowParams.x - viewWidth / 2f, windowParams.y, true);
                } else {
                    int offSet = getWindowViewWidth() / 2;
                    clipBounds.set(offSet, 0, viewWidth, viewHeight);
                    // 设置画板偏移
                    rootLayout.setTranslationX(-offSet);
                    rootLayout.setTranslationY(0);
                    // 设置裁剪区域
                    rootLayout.setClipBounds(clipBounds);
                }
                break;
            case Gravity.RIGHT:
                int offSet = viewWidth / 2;
                clipBounds.set(0, 0, viewWidth - offSet, viewHeight);
                // 设置画板偏移
                rootLayout.setTranslationX(offSet);
                rootLayout.setTranslationY(0);
                // 设置裁剪区域
                rootLayout.setClipBounds(clipBounds);
                break;
            default:
                break;
        }
    }

    /**
     * 取消半边显示
     */
    private void cancelHalfShow() {
        View rootLayout = getRootLayout();
        if (rootLayout == null) {
            return;
        }
        int viewWidth = rootLayout.getWidth();
        int viewHeight = rootLayout.getHeight();
        AbstractWindowDraggableRule windowDraggableRule = getWindowDraggableRule();
        if (windowDraggableRule == null) {
            return;
        }
        Rect safeInsetRect = windowDraggableRule.getSafeInsetRect();
        if (safeInsetRect != null && safeInsetRect.left > 0 &&
            getWindowParams().x > 0 && getWindowParams().x < safeInsetRect.left) {
            WindowManager.LayoutParams windowParams = getWindowParams();
            windowDraggableRule.updateLocation(windowParams.x + viewWidth / 2f, windowParams.y);
        }
        // 将画板偏移还原回去
        if (rootLayout.getTranslationX() != 0) {
            rootLayout.setTranslationX(0);
        }
        if (rootLayout.getTranslationY() != 0) {
            rootLayout.setTranslationY(0);
        }
        // 设置裁剪区域
        Rect clipBounds = rootLayout.getClipBounds();
        if (clipBounds == null) {
            clipBounds = new Rect();
        }
        if (clipBounds .left != 0 || clipBounds.top != 0 ||
            clipBounds.right != viewWidth || clipBounds.bottom != viewHeight) {
            rootLayout.setClipBounds(new Rect(0, 0, viewWidth, viewHeight));
        }
    }

    /**
     * 当前是否为半边显示
     */
    private boolean isHalfShow() {
        AbstractWindowDraggableRule windowDraggableRule = getWindowDraggableRule();
        if (windowDraggableRule == null) {
            return false;
        }
        Rect safeInsetRect = windowDraggableRule.getSafeInsetRect();
        if (safeInsetRect != null && safeInsetRect.left > 0 &&
            getWindowParams().x > 0 && getWindowParams().x < safeInsetRect.left) {
            return true;
        }
        View rootLayout = getRootLayout();
        if (rootLayout == null) {
            return false;
        }
        int viewWidth = rootLayout.getWidth();
        int viewHeight = rootLayout.getHeight();
        Rect clipBounds = rootLayout.getClipBounds();
        if (rootLayout.getTranslationX() != 0 && rootLayout.getTranslationY() != 0) {
            return true;
        }
        if (clipBounds == null) {
            return false;
        }
        return clipBounds.left != 0 || clipBounds.top != 0 ||
            clipBounds.right != viewWidth || clipBounds.bottom != viewHeight;
    }

    /** {@link OnWindowDraggingListener} */

    @Override
    public void onWindowDraggingStart(@NonNull EasyWindow<?> easyWindow) {
        mDraggingRunning = true;
        if (isHalfShow()) {
            cancelHalfShow();
        }
    }

    @Override
    public void onWindowDraggingStop(@NonNull EasyWindow<?> easyWindow) {
        mDraggingRunning = false;
    }

    /** {@link SpringBackAnimCallback} */

    @Override
    public void onSpringBackAnimationStart(@NonNull EasyWindow<?> easyWindow, @NonNull Animator animator) {
        mAnimatingRunning = true;
    }

    @Override
    public void onSpringBackAnimationEnd(@NonNull EasyWindow<?> easyWindow, @NonNull Animator animator) {
        mAnimatingRunning = false;
        postStayEdgeRunnable();
    }

    /** {@link SpringBackAnimCallback} */

    @Override
    public void onClick(@NonNull EasyWindow<?> easyWindow, @NonNull View view) {
        if (isHalfShow()) {
            cancelHalfShow();
            postStayEdgeRunnable();
            return;
        }
        Toaster.show(R.string.demo_toast_click);
    }

    /** {@link OnWindowLifecycleCallback} */

    @Override
    public void onWindowShow(@NonNull EasyWindow<?> easyWindow) {
        postStayEdgeRunnable();
    }

    @Override
    public void onScreenOrientationChange(int newOrientation) {
        if (isHalfShow()) {
            cancelHalfShow();
        }
        super.onScreenOrientationChange(newOrientation);
        postStayEdgeRunnable();
    }
}