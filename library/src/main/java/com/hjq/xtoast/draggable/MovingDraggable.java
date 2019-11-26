package com.hjq.xtoast.draggable;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;

import com.hjq.xtoast.BaseDraggable;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XToast
 *    time   : 2019/01/04
 *    desc   : 移动拖拽处理实现类
 */
public class MovingDraggable extends BaseDraggable {

    private float mViewDownX;
    private float mViewDownY;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mViewDownX = event.getX();
                mViewDownY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float rawMoveX = event.getRawX();
                float rawMoveY = event.getRawY() - getStatusBarHeight();
                updateLocation(rawMoveX - mViewDownX, rawMoveY - mViewDownY);
                break;
            case MotionEvent.ACTION_UP:
                return mViewDownX != event.getX() || mViewDownY != event.getY();
            default:
                break;
        }
        return false;
    }
}