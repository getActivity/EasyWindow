package com.hjq.xtoast.draggable;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XToast
 *    time   : 2019/01/04
 *    desc   : 拖拽后回弹处理实现类
 */
public class SpringDraggable extends BaseDraggable {

    /** 水平方向回弹 */
    public static final int ORIENTATION_HORIZONTAL = LinearLayout.HORIZONTAL;
    /** 垂直方向回弹 */
    public static final int ORIENTATION_VERTICAL = LinearLayout.VERTICAL;

    /** 手指按下的坐标 */
    private float mViewDownX;
    private float mViewDownY;

    /** 回弹的方向 */
    private final int mOrientation;

    /** 触摸移动标记 */
    private boolean mMoveTouch;

    public SpringDraggable() {
        this(ORIENTATION_HORIZONTAL);
    }

    public SpringDraggable(int orientation) {
        mOrientation = orientation;
        switch (mOrientation) {
            case ORIENTATION_HORIZONTAL:
            case ORIENTATION_VERTICAL:
                break;
            default:
                throw new IllegalArgumentException("You cannot pass in directions other than horizontal or vertical");
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float rawMoveX;
        float rawMoveY;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 记录按下的位置（相对 View 的坐标）
                mViewDownX = event.getX();
                mViewDownY = event.getY();
                mMoveTouch = false;
                break;
            case MotionEvent.ACTION_MOVE:
                // 记录移动的位置（相对屏幕的坐标）
                rawMoveX = event.getRawX() - getWindowInvisibleWidth();
                rawMoveY = event.getRawY() - getWindowInvisibleHeight();

                // 更新移动的位置
                updateLocation(rawMoveX - mViewDownX, rawMoveY - mViewDownY);

                if (!mMoveTouch && isTouchMove(mViewDownX, event.getX(), mViewDownY, event.getY())) {
                    // 如果用户移动了手指，那么就拦截本次触摸事件，从而不让点击事件生效
                    mMoveTouch = true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                // 记录移动的位置（相对屏幕的坐标）
                rawMoveX = event.getRawX() - getWindowInvisibleWidth();
                rawMoveY = event.getRawY() - getWindowInvisibleHeight();

                // 自动回弹吸附
                switch (mOrientation) {
                    case ORIENTATION_HORIZONTAL:
                        final float rawFinalX;
                        // 获取当前屏幕的宽度
                        int screenWidth = getWindowWidth();
                        if (rawMoveX < screenWidth / 2f) {
                            // 回弹到屏幕左边
                            rawFinalX = 0f;
                        } else {
                            // 回弹到屏幕右边
                            rawFinalX = screenWidth;
                        }
                        // 从移动的点回弹到边界上
                        startHorizontalAnimation(rawMoveX - mViewDownX,
                                rawFinalX  - mViewDownX, rawMoveY - mViewDownY);
                        break;
                    case ORIENTATION_VERTICAL:
                        final float rawFinalY;
                        // 获取当前屏幕的高度
                        int screenHeight = getWindowHeight();
                        if (rawMoveY < screenHeight / 2f) {
                            // 回弹到屏幕顶部
                            rawFinalY = 0f;
                        } else {
                            // 回弹到屏幕底部
                            rawFinalY = screenHeight;
                        }
                        // 从移动的点回弹到边界上
                        startVerticalAnimation(rawMoveX - mViewDownX,
                                rawMoveY - mViewDownY, rawFinalY);
                        break;
                    default:
                        break;
                }
                return mMoveTouch;
            default:
                break;
        }
        return false;
    }

    /**
     * 执行水平回弹动画
     *
     * @param startX        X 轴起点坐标
     * @param endX          X 轴终点坐标
     * @param y             Y 轴坐标
     */
    private void startHorizontalAnimation(float startX, float endX, final float y) {
        ValueAnimator animator = ValueAnimator.ofFloat(startX, endX);
        animator.setDuration(calculateAnimationDuration(startX, endX));
        animator.addUpdateListener(animation -> updateLocation((float) animation.getAnimatedValue(), y));
        animator.start();
    }

    /**
     * 执行垂直回弹动画
     *
     * @param x             X 轴坐标
     * @param startY        Y 轴起点坐标
     * @param endY          Y 轴终点坐标
     */
    private void startVerticalAnimation(final float x, float startY, final float endY) {
        ValueAnimator animator = ValueAnimator.ofFloat(startY, endY);
        animator.setDuration(calculateAnimationDuration(startY, endY));
        animator.addUpdateListener(animation -> updateLocation(x, (float) animation.getAnimatedValue()));
        animator.start();
    }

    /**
     * 根据距离算出动画的时间
     *
     * @param startCoordinate               起始坐标
     * @param endCoordinate                 结束坐标
     */
    private long calculateAnimationDuration(float startCoordinate, float endCoordinate) {
        // 为什么要根据距离来算出动画的时间？
        // issue 地址：https://github.com/getActivity/XToast/issues/36
        // 因为不那么做，如果悬浮球回弹的距离比较短的情况，加上 ValueAnimator 动画更新回调次数比较多的情况下
        // 会导致自动回弹的时候出现轻微卡顿，但这其实不是卡顿，而是一次滑动的距离太短的导致的
        long animationDuration = (long) ((Math.abs(endCoordinate - startCoordinate)) / 2f);
        if (animationDuration > 800) {
            animationDuration = 800;
        }
        return animationDuration;
    }
}