package com.hjq.window.draggable;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import com.hjq.window.EasyWindow;

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

    /** 拖拽生效阈值 */
    private int mTouchSlop;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onDragWindow(EasyWindow<?> easyWindow, View decorView, MotionEvent event) {
        // 初始化系统拖拽阈值
        if (mTouchSlop == 0) {
            mTouchSlop = ViewConfiguration.get(decorView.getContext()).getScaledTouchSlop();
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 记录按下的位置（相对 View 的坐标）
                mViewDownX = event.getX();
                mViewDownY = event.getY();
                mTouchMoving = false;
                break;
            case MotionEvent.ACTION_MOVE:
                // 拖拽阈值判断逻辑
                if (!mTouchMoving) {
                    float dx = Math.abs(event.getX() - mViewDownX);
                    float dy = Math.abs(event.getY() - mViewDownY);
                    if (dx > mTouchSlop || dy > mTouchSlop) {
                        mTouchMoving = true;
                        dispatchStartDraggingCallback();
                    }
                }

                if (mTouchMoving) {
                    // 记录移动的位置（相对屏幕的坐标）
                    float rawMoveX = event.getRawX() - getWindowInvisibleWidth();
                    float rawMoveY = event.getRawY() - getWindowInvisibleHeight();

                    float newX = rawMoveX - mViewDownX;
                    float newY = rawMoveY - mViewDownY;

                    // 判断当前是否支持移动到屏幕外
                    if (!isSupportMoveOffScreen()) {
                        newX = Math.max(newX, 0);
                        newY = Math.max(newY, 0);
                    }

                    // 更新移动的位置
                    updateLocation(newX, newY);

                    dispatchExecuteDraggingCallback();
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mTouchMoving) {
                    dispatchStopDraggingCallback();
                } else {
                    // 未达阈值触发点击（新增）
                    decorView.performClick();
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
        return mTouchMoving;
    }

    /**
     * 当前是否处于触摸移动状态
     */
    @Override
    public boolean isTouchMoving() {
        return mTouchMoving;
    }
}