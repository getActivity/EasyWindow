package com.hjq.xtoast.draggable;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XToast
 *    time   : 2019/01/04
 *    desc   : 拖拽后回弹处理实现类
 */
public class SpringDraggable extends BaseDraggable {

    /** 手指按下的坐标 */
    private float mViewDownX;
    private float mViewDownY;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int rawMoveX;
        int rawMoveY;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 记录按下的位置（相对 View 的坐标）
                mViewDownX = (int) event.getX();
                mViewDownY = (int) event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                // 记录移动的位置（相对屏幕的坐标）
                rawMoveX = (int) event.getRawX();
                rawMoveY = (int) (event.getRawY() - getStatusBarHeight());
                // 更新移动的位置
                updateLocation(rawMoveX - mViewDownX, rawMoveY - mViewDownY);
                break;
            case MotionEvent.ACTION_UP:
                // 记录移动的位置（相对屏幕的坐标）
                rawMoveX = (int) event.getRawX();
                rawMoveY = (int) (event.getRawY() - getStatusBarHeight());
                // 获取当前屏幕的宽度
                int screenWidth = getScreenWidth();
                // 自动回弹吸附
                final float rawFinalX;
                if (rawMoveX < screenWidth / 2) {
                    // 回弹到屏幕左边
                    rawFinalX = 0;
                } else {
                    // 回弹到屏幕右边
                    rawFinalX = screenWidth;
                }
                // 从移动的点回弹到边界上
                startAnimation(rawMoveX - mViewDownX, rawFinalX  - mViewDownX, rawMoveY - mViewDownY);
                // 如果用户移动了手指，那么就拦截本次触摸事件，从而不让点击事件生效
                return isTouchMove(mViewDownX, event.getX(), mViewDownY, event.getY());
            default:
                break;
        }
        return false;
    }

    /**
     *  获取屏幕的宽度
     */
    private int getScreenWidth() {
        WindowManager manager = getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     * 执行动画
     *
     * @param startX        X轴起点坐标
     * @param endX          X轴终点坐标
     * @param y             Y轴坐标
     */
    private void startAnimation(float startX, float endX, final float y) {
        ValueAnimator animator = ValueAnimator.ofFloat(startX, endX);
        animator.setDuration(500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                updateLocation((float) animation.getAnimatedValue(), y);
            }
        });
        animator.start();
    }
}