package com.hjq.xtoast.draggable;

import android.content.res.Resources;
import android.graphics.Rect;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.hjq.xtoast.XToast;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XToast
 *    time   : 2019/01/04
 *    desc   : 拖拽抽象类
 */
public abstract class BaseDraggable implements View.OnTouchListener {

    private XToast<?> mToast;
    private View mDecorView;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mWindowParams;

    private final Rect mTempRect = new Rect();

    /**
     * Toast 显示后回调这个类
     */
    public void start(XToast<?> toast) {
        mToast = toast;
        mDecorView = toast.getDecorView();
        mWindowManager = toast.getWindowManager();
        mWindowParams = toast.getWindowParams();

        mDecorView.setOnTouchListener(this);
    }

    protected XToast<?> getXToast() {
        return mToast;
    }

    protected WindowManager getWindowManager() {
        return mWindowManager;
    }

    protected WindowManager.LayoutParams getWindowParams() {
        return mWindowParams;
    }

    protected View getDecorView() {
        return mDecorView;
    }

    /**
     * 获取当前 Window 的宽度
     */
    protected int getWindowWidth() {
        getDecorView().getWindowVisibleDisplayFrame(mTempRect);
        return mTempRect.right - mTempRect.left;
    }

    /**
     * 获取当前 Window 的高度
     */
    protected int getWindowHeight() {
        getDecorView().getWindowVisibleDisplayFrame(mTempRect);
        return mTempRect.bottom - mTempRect.top;
    }

    /**
     * 获取窗口不可见的宽度，一般情况下为横屏状态下刘海的高度
     */
    protected int getWindowInvisibleWidth() {
        getDecorView().getWindowVisibleDisplayFrame(mTempRect);
        return mTempRect.left;
    }

    /**
     * 获取窗口不可见的高度，一般情况下为状态栏的高度
     */
    protected int getWindowInvisibleHeight() {
        getDecorView().getWindowVisibleDisplayFrame(mTempRect);
        return mTempRect.top;
    }

    /**
     * 屏幕方向发生了变化
     */
    public void onScreenOrientationChange(int orientation) {
        int windowWidth = getWindowWidth();
        int windowHeight = getWindowHeight();

        int[] location = new int[2];
        getDecorView().getLocationOnScreen(location);

        int viewWidth = getDecorView().getWidth();
        int viewHeight = getDecorView().getHeight();

        float startX = location[0] - getWindowInvisibleWidth();
        float startY = location[1] - getWindowInvisibleHeight();

        float percentX;
        if (startX < 1) {
            percentX = 0;
        } else if (Math.abs(windowWidth - (startX + viewWidth)) < 1) {
            percentX = 1;
        } else {
            float centerX = startX + viewWidth / 2f;
            percentX = centerX / (float) windowWidth;
        }

        float percentY;
        if (startY < 1) {
            percentY = 0;
        } else if (Math.abs(windowHeight - (startY + viewHeight)) < 1) {
            percentY = 1;
        } else {
            float centerY = startY + viewHeight / 2f;
            percentY = centerY / (float) windowHeight;
        }

        getXToast().postDelayed(() -> {
            int x = (int) (getWindowWidth() * percentX - viewWidth / 2f);
            int y = (int) (getWindowHeight() * percentY - viewWidth / 2f);
            updateLocation(x, y);
        }, 100);
    }

    /**
     * 更新悬浮窗的位置
     *
     * @param x             x 坐标
     * @param y             y 坐标
     */
    protected void updateLocation(float x, float y) {
        updateLocation((int) x, (int) y);
    }

    /**
     * 更新 WindowManager 所在的位置
     */
    protected void updateLocation(int x, int y) {
        // 屏幕默认的重心
        int screenGravity = Gravity.TOP | Gravity.START;
        // 判断本次移动的位置是否跟当前的窗口位置是否一致
        if (mWindowParams.gravity == screenGravity && mWindowParams.x == x && mWindowParams.y == y) {
            return;
        }

        mWindowParams.x = x;
        mWindowParams.y = y;
        // 一定要先设置重心位置为左上角
        mWindowParams.gravity = screenGravity;
        try {
            mWindowManager.updateViewLayout(mDecorView, mWindowParams);
        } catch (IllegalArgumentException e) {
            // 当 WindowManager 已经消失时调用会发生崩溃
            // IllegalArgumentException: View not attached to window manager
            e.printStackTrace();
        }
    }

    /**
     * 判断用户是否移动了，判断标准以下：
     * 根据手指按下和抬起时的坐标进行判断，不能根据有没有 move 事件来判断
     * 因为在有些机型上面，就算用户没有手指没有移动也会产生 move 事件
     *
     * @param downX         手指按下时的 x 坐标
     * @param upX           手指抬起时的 x 坐标
     * @param downY         手指按下时的 y 坐标
     * @param upY           手指抬起时的 y 坐标
     */
    protected boolean isTouchMove(float downX, float upX, float downY, float upY) {
        float minTouchSlop = getScaledTouchSlop();
        return Math.abs(downX - upX) >= minTouchSlop || Math.abs(downY - upY) >= minTouchSlop;
    }

    /**
     * 获取最小触摸距离
     */
    protected float getScaledTouchSlop() {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, Resources.getSystem().getDisplayMetrics());
    }
}