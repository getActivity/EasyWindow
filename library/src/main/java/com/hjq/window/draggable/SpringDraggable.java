package com.hjq.window.draggable;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyWindow
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
    private boolean mTouchMoving;

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
                mTouchMoving = false;
                break;
            case MotionEvent.ACTION_MOVE:
                // 记录移动的位置（相对屏幕的坐标）
                rawMoveX = event.getRawX() - getWindowInvisibleWidth();
                rawMoveY = event.getRawY() - getWindowInvisibleHeight();

                float newX = rawMoveX - mViewDownX;
                float newY = rawMoveY - mViewDownY;
                if (newX < 0) {
                    newX = 0;
                }
                if (newY < 0) {
                    newY = 0;
                }

                // 更新移动的位置
                updateLocation(newX, newY);

                if (!mTouchMoving && isFingerMove(mViewDownX, event.getX(), mViewDownY, event.getY())) {
                    // 如果用户移动了手指，那么就拦截本次触摸事件，从而不让点击事件生效
                    mTouchMoving = true;
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
                        float startX = rawMoveX - mViewDownX;
                        // 如果在最左边向左移动就会产生负数，这里需要处理掉，因为坐标没有负数这一说
                        if (startX < 0) {
                            startX = 0;
                        }
                        float endX;
                        // 获取当前屏幕的宽度
                        int screenWidth = getWindowWidth();
                        if (rawMoveX < screenWidth / 2f) {
                            // 回弹到屏幕左边
                            endX = 0f;
                        } else {
                            // 回弹到屏幕右边（注意减去 View 宽度，因为 View 坐标系是从屏幕左上角开始算的）
                            endX = screenWidth - v.getWidth();
                            // 如果在最右边向右移动就会产生负数，这里需要处理掉，因为坐标没有负数这一说
                            if (endX < 0) {
                                endX = 0;
                            }
                        }
                        float y = rawMoveY - mViewDownY;
                        // 从移动的点回弹到边界上
                        startHorizontalAnimation(startX, endX, y);
                        break;
                    case ORIENTATION_VERTICAL:
                        float x = rawMoveX - mViewDownX;
                        float startY = rawMoveY - mViewDownY;
                        // 如果在最顶部向上移动就会产生负数，这里需要处理掉，因为坐标没有负数这一说
                        if (startY < 0) {
                            startY = 0;
                        }
                        float endY;
                        // 获取当前屏幕的高度
                        int screenHeight = getWindowHeight();
                        if (rawMoveY < screenHeight / 2f) {
                            // 回弹到屏幕顶部
                            endY = 0f;
                        } else {
                            // 回弹到屏幕底部（注意减去 View 高度，因为 View 坐标系是从屏幕左上角开始算的）
                            endY = screenHeight - v.getHeight();
                            // 如果在最底部向下移动就会产生负数，这里需要处理掉，因为坐标没有负数这一说
                            if (endY < 0) {
                                endY = 0;
                            }
                        }
                        // 从移动的点回弹到边界上
                        startVerticalAnimation(x, startY, endY);
                        break;
                    default:
                        break;
                }
                try {
                    return mTouchMoving;
                } finally {
                    // 重置触摸移动标记
                    mTouchMoving = false;
                }
            default:
                break;
        }
        return false;
    }

    protected void startHorizontalAnimation(float startX, float endX, final float y) {
        startHorizontalAnimation(startX, endX, y, calculateAnimationDuration(startX, endX));
    }

    /**
     * 执行水平回弹动画
     *
     * @param startX        X 轴起点坐标
     * @param endX          X 轴终点坐标
     * @param y             Y 轴坐标
     * @param duration      动画时长
     */
    protected void startHorizontalAnimation(float startX, float endX, final float y, long duration) {
        ValueAnimator animator = ValueAnimator.ofFloat(startX, endX);
        animator.setDuration(duration);
        animator.addUpdateListener(animation -> updateLocation((float) animation.getAnimatedValue(), y));
        animator.start();
    }

    protected void startVerticalAnimation(final float x, float startY, final float endY) {
        startVerticalAnimation(x, startY, endY, calculateAnimationDuration(startY, endY));
    }

    /**
     * 执行垂直回弹动画
     *
     * @param x             X 轴坐标
     * @param startY        Y 轴起点坐标
     * @param endY          Y 轴终点坐标
     * @param duration      动画时长
     */
    protected void startVerticalAnimation(final float x, float startY, final float endY, long duration) {
        ValueAnimator animator = ValueAnimator.ofFloat(startY, endY);
        animator.setDuration(duration);
        animator.addUpdateListener(animation -> updateLocation(x, (float) animation.getAnimatedValue()));
        animator.start();
    }

    /**
     * 根据距离算出动画的时间
     *
     * @param startCoordinate               起始坐标
     * @param endCoordinate                 结束坐标
     */
    public long calculateAnimationDuration(float startCoordinate, float endCoordinate) {
        // 为什么要根据距离来算出动画的时间？
        // issue 地址：https://github.com/getActivity/EasyWindow/issues/36
        // 因为不那么做，如果悬浮球回弹的距离比较短的情况，加上 ValueAnimator 动画更新回调次数比较多的情况下
        // 会导致自动回弹的时候出现轻微卡顿，但这其实不是卡顿，而是一次滑动的距离太短的导致的
        long animationDuration = (long) ((Math.abs(endCoordinate - startCoordinate)) / 2f);
        if (animationDuration > 800) {
            animationDuration = 800;
        }
        return animationDuration;
    }
}