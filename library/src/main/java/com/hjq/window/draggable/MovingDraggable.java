package com.hjq.window.draggable;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyWindow
 *    time   : 2019/01/04
 *    desc   : 移动拖拽处理实现类
 */
public class MovingDraggable extends BaseDraggable {

    /** 手指按下的坐标 */
    private float mViewDownX;
    private float mViewDownY;

    /** 触摸移动标记 */
    private boolean mTouchMoving;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 记录按下的位置（相对 View 的坐标）
                mViewDownX = event.getX();
                mViewDownY = event.getY();
                mTouchMoving = false;
                break;
            case MotionEvent.ACTION_MOVE:
                // 记录移动的位置（相对屏幕的坐标）
                float rawMoveX = event.getRawX() - getWindowInvisibleWidth();
                float rawMoveY = event.getRawY() - getWindowInvisibleHeight();

                float newX = Math.max(rawMoveX - mViewDownX, 0);
                float newY = Math.max(rawMoveY - mViewDownY, 0);

                // 更新移动的位置
                updateLocation(newX, newY);

                if (mTouchMoving) {
                    dispatchExecuteDraggingCallback();
                } else if (isFingerMove(mViewDownX, event.getX(), mViewDownY, event.getY())) {
                    // 如果用户移动了手指，那么就拦截本次触摸事件，从而不让点击事件生效
                    mTouchMoving = true;
                    dispatchStartDraggingCallback();
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mTouchMoving) {
                    dispatchStopDraggingCallback();
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

    /**
     * 当前是否处于触摸移动状态
     */
    public boolean isTouchMoving() {
        return mTouchMoving;
    }
}