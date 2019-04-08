package com.hjq.xtoast.draggable;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;

import com.hjq.xtoast.AbsDraggable;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/ToastUtils
 *    time   : 2019/01/04
 *    desc   : 移动拖拽处理实现类
 */
public class MovingDraggable extends AbsDraggable {

    private boolean isTouchMove;

    private int mViewDownX;
    private int mViewDownY;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isTouchMove = false;
                mViewDownX = (int) event.getX();
                mViewDownY = (int) event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                isTouchMove = true;
                int moveX = (int) event.getRawX();
                int moveY = (int) (event.getRawY() - getStatusBarHeight());
                updateViewLayout(moveX - mViewDownX, moveY - mViewDownY);
                break;
        }
        return isTouchMove;
    }
}