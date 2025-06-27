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

        setGravity(Gravity.START | Gravity.TOP);
        setXOffset(0);
        setYOffset(200);

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
        removeRunnable(mStayEdgeRunnable);
        postDelayed(mStayEdgeRunnable, 3000);
    }

    private final Runnable mStayEdgeRunnable = () -> {
        if (mAnimatingFlag || mDraggingFlag) {
            return;
        }

        if (isLeftShow()) {
            if (isTopShow()) {
                hideHalfView(Gravity.TOP);
            } else {
                hideHalfView(Gravity.LEFT);
            }
        } else {
            if (isTopShow()) {
                hideHalfView(Gravity.TOP);
            } else {
                hideHalfView(Gravity.RIGHT);
            }
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
        View windowRootLayout = getWindowRootLayout();
        if (windowRootLayout == null) {
            return;
        }

        int viewWidth = getWindowContentWidth();
        int viewHeight = getWindowContentHeight();

        // 创建一个矩形来定义裁剪区域
        Rect clipBounds = new Rect();
        switch (gravity) {
            case Gravity.LEFT:
                Rect safeInsetRect = windowDraggableRule.getSafeInsetRect();
                if (safeInsetRect != null && safeInsetRect.left > 0) {
                    WindowManager.LayoutParams windowParams = getWindowParams();
                    windowDraggableRule.updateLocation(windowParams.x - viewWidth / 2f, windowParams.y, true);
                } else {
                    int offSet = getWindowContentWidth() / 2; //用小球来做偏移
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
            case Gravity.TOP:
                int offSetHeight = viewHeight / 2;
                clipBounds.set(0, offSetHeight, viewWidth, viewHeight);
                // 设置画板偏移
                windowRootLayout.setTranslationX(0);
                windowRootLayout.setTranslationY(-offSetHeight);
                // 设置裁剪区域
                windowRootLayout.setClipBounds(clipBounds);
                break;
            default:
                break;
        }
    }

    private void showFullView() {
        View windowRootLayout = getWindowRootLayout();
        if (windowRootLayout == null) {
            return;
        }
        int viewWidth = windowRootLayout.getWidth();
        int viewHeight = windowRootLayout.getHeight();
        AbstractWindowDraggableRule windowDraggableRule = getWindowDraggableRule();
        if (windowDraggableRule == null) {
            return;
        }
        Rect safeInsetRect = windowDraggableRule.getSafeInsetRect();
        if (safeInsetRect != null && safeInsetRect.left > 0 && !isTopShow()) {
            WindowManager.LayoutParams windowParams = getWindowParams();
            windowDraggableRule.updateLocation(windowParams.x + viewWidth / 2f, windowParams.y, false);
        }
        // 设置画板偏移
        windowRootLayout.setTranslationX(0);
        windowRootLayout.setTranslationY(0);
        // 设置裁剪区域
        windowRootLayout.setClipBounds(new Rect(0, 0, viewWidth, viewHeight));
    }

    /**
     * View 是否全屏显示
     */
    private boolean isFullShowView() {
        View view = getWindowRootLayout();
        AbstractWindowDraggableRule windowDraggableRule = getWindowDraggableRule();
        if (windowDraggableRule == null) {
            return true;
        }
        Rect safeInsetRect = windowDraggableRule.getSafeInsetRect();
        if (safeInsetRect != null && safeInsetRect.left > 0 && !isTopShow()) {
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
     * 悬浮球是否靠顶显示
     */
    private boolean isTopShow() {
        WindowManager.LayoutParams windowParams = getWindowParams();
        if (isLeftShow()) {
            return windowParams.x > windowParams.y;
        } else {
            return getScreenWidth() - windowParams.x - getWindowContentWidth() > windowParams.y;
        }
    }

    private int getScreenWidth() {
        Context context = getContext();
        if (context == null) {
            return 0;
        }
        Resources resources = getContext().getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        return displayMetrics.widthPixels;
    }

    /**
     * 悬浮球是否靠左显示
     */
    private boolean isLeftShow(){
        return (getWindowParams().x + getWindowContentWidth() / 2f)  < getScreenWidth() / 2f;
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
            return;
        }
        Toaster.show("我被点击了");
    }

    /** {@link OnWindowLifecycleCallback} */

    @Override
    public void onWindowShow(@NonNull EasyWindow<?> easyWindow) {
        postStayEdgeRunnable();
    }
}