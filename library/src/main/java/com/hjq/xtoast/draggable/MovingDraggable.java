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

    private int mDownX;
    private int mDownY;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = (int) event.getX();
                mDownY = (int) event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int moveX = (int) event.getRawX();
                int moveY = (int) (event.getRawY() - getStatusBarHeight());
                updateViewLayout(moveX - mDownX, moveY - mDownY);
                break;
        }
        return false;
    }
}
