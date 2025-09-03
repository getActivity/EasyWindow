package com.hjq.window.demo;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
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

    private boolean mAnimatingFlag;
    private boolean mDraggingFlag;

    public SemiStealthWindow(@NonNull Activity activity) {
        super(activity);
    }

    @Override
    protected void initWindow(@NonNull Context context) {
        super.initWindow(context);

        setContentView(R.layout.window_semi_stealth);

        setWindowLocation(0, 200);

        SpringBackWindowDraggableRule springBackWindowDraggableRule = new SpringBackWindowDraggableRule(
            SpringBackWindowDraggableRule.ORIENTATION_HORIZONTAL);
        springBackWindowDraggableRule.setAllowMoveToScreenNotch(false);
        springBackWindowDraggableRule.setWindowDraggingListener(this);
        springBackWindowDraggableRule.setSpringBackAnimCallback(this);
        setWindowDraggableRule(springBackWindowDraggableRule);

        setOnClickListenerByView(android.R.id.icon, this);
        setOnWindowLifecycleCallback(this);
    }

    /**
     * 发送贴边显示任务
     */
    public void postStayEdgeRunnable() {
        cancelTask(mStayEdgeRunnable);
        sendTask(mStayEdgeRunnable, 3000);
    }

    private final Runnable mStayEdgeRunnable = () -> {
        if (mAnimatingFlag || mDraggingFlag) {
            return;
        }

        if (isLeftShow()) {
            hideHalfView(Gravity.LEFT);
        } else {
            hideHalfView(Gravity.RIGHT);
        }
    };

    /**
     * 隐藏 View 一半显示
     */
    private void hideHalfView(int gravity) {
        AbstractWindowDraggableRule windowDraggableRule = getWindowDraggableRule();
        if (windowDraggableRule == null) {
            return;
        }
        View windowRootLayout = getRootLayout();
        if (windowRootLayout == null) {
            return;
        }

        int viewWidth = getWindowViewWidth();
        int viewHeight = getWindowViewHeight();

        // 创建一个矩形来定义裁剪区域
        Rect clipBounds = new Rect();
        switch (gravity) {
            case Gravity.LEFT:
                Rect safeInsetRect = windowDraggableRule.getSafeInsetRect();
                if (safeInsetRect != null && safeInsetRect.left > 0) {
                    WindowManager.LayoutParams windowParams = getWindowParams();
                    windowDraggableRule.updateLocation(windowParams.x - viewWidth / 2f, windowParams.y, true);
                } else {
                    int offSet = getWindowViewWidth() / 2;
                    clipBounds.set(offSet, 0, viewWidth, viewHeight);
                    // 设置画板偏移
                    windowRootLayout.setTranslationX(-offSet);
                    windowRootLayout.setTranslationY(0);
                    // 设置裁剪区域
                    windowRootLayout.setClipBounds(clipBounds);
                }
                break;
            case Gravity.RIGHT:
                int offSet = viewWidth / 2;
                clipBounds.set(0, 0, viewWidth - offSet, viewHeight);
                // 设置画板偏移
                windowRootLayout.setTranslationX(offSet);
                windowRootLayout.setTranslationY(0);
                // 设置裁剪区域
                windowRootLayout.setClipBounds(clipBounds);
                break;
            default:
                break;
        }
    }

    private void showFullView() {
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
        if (safeInsetRect != null && safeInsetRect.left > 0) {
            WindowManager.LayoutParams windowParams = getWindowParams();
            windowDraggableRule.updateLocation(windowParams.x + viewWidth / 2f, windowParams.y, false);
        }
        // 设置画板偏移
        rootLayout.setTranslationX(0);
        rootLayout.setTranslationY(0);
        // 设置裁剪区域
        rootLayout.setClipBounds(new Rect(0, 0, viewWidth, viewHeight));
    }

    /**
     * View 是否全屏显示
     */
    private boolean isFullShowView() {
        View view = getRootLayout();
        AbstractWindowDraggableRule windowDraggableRule = getWindowDraggableRule();
        if (windowDraggableRule == null) {
            return true;
        }
        Rect safeInsetRect = windowDraggableRule.getSafeInsetRect();
        if (safeInsetRect != null && safeInsetRect.left > 0) {
            if (getWindowParams().x < safeInsetRect.left) {
                return false;
            }
        }
        if (view == null) {
            return true;
        }
        int viewWidth = view.getWidth();
        int viewHeight = view.getHeight();
        Rect clipBounds = view.getClipBounds();
        if (view.getTranslationX() != 0 && view.getTranslationY() != 0) {
            return false;
        }
        if (clipBounds == null) {
            return true;
        }
        return clipBounds.left == 0 && clipBounds.top == 0 &&
            clipBounds.right == viewWidth && clipBounds.bottom == viewHeight;
    }

    /**
     * 获取当前屏幕宽度
     */
    private int getScreenWidth() {
        Context context = getContext();
        if (context == null) {
            return 0;
        }
        Resources resources = context.getResources();
        if (resources == null) {
            return 0;
        }
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        if (displayMetrics == null) {
            return 0;
        }
        return displayMetrics.widthPixels;
    }

    /**
     * 悬浮球是否靠左显示
     */
    private boolean isLeftShow(){
        return (getWindowParams().x + getWindowViewWidth() / 2f)  < getScreenWidth() / 2f;
    }

    /** {@link OnWindowDraggingListener} */

    @Override
    public void onWindowDraggingStart(@NonNull EasyWindow<?> easyWindow) {
        mDraggingFlag = true;
        if (!isFullShowView()) {
            showFullView();
        }
    }

    @Override
    public void onWindowDraggingStop(@NonNull EasyWindow<?> easyWindow) {
        mDraggingFlag = false;
    }

    /** {@link SpringBackAnimCallback} */

    @Override
    public void onSpringBackAnimationStart(@NonNull EasyWindow<?> easyWindow, @NonNull Animator animator) {
        mAnimatingFlag = true;
    }

    @Override
    public void onSpringBackAnimationEnd(@NonNull EasyWindow<?> easyWindow, @NonNull Animator animator) {
        mAnimatingFlag = false;
        postStayEdgeRunnable();
    }

    /** {@link SpringBackAnimCallback} */

    @Override
    public void onClick(@NonNull EasyWindow<?> easyWindow, @NonNull View view) {
        if (!isFullShowView()) {
            showFullView();
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
}