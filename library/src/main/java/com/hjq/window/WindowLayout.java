package com.hjq.window;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyWindow
 *    time   : 2021/01/04
 *    desc   : 悬浮窗根布局（处理触摸事件冲突）
 */
public final class WindowLayout extends FrameLayout {

    /** 触摸事件监听 */
    private OnTouchListener mOnTouchListener;

    public WindowLayout(Context context) {
        super(context);
    }

    public WindowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WindowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    private boolean mDownEventFlag;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // 为什么要那么写？有人反馈给子 View 设置 OnClickListener 后，父 View 的 OnTouchListener 收不到事件
        // 经过排查发现：父 View 在 dispatchTouchEvent 方法中直接将触摸事件派发给了子 View 的 onTouchEvent 方法
        // 从而导致父 View.OnTouchListener 收不到该事件，解决方案是重写 View 的触摸规则，让父 View 的触摸监听优于子 View 的点击事件
        if (mOnTouchListener != null && mOnTouchListener.onTouch(this, ev)) {
            // 处理悬浮窗移动后仍然会触发 View 长按事件的问题，本质上是 View 在 Down 触摸事件的时候延迟发送了 LongClick 事件的 Runnable
            // 假设没有其他事件触发取消 LongClick 事件的 Runnable，则 LongClick 事件的 Runnable 就会执行
            // 这里的解决方案是模拟发送一个 Cancel 触摸事件，这样就触发取消 LongClick 事件的 Runnable 的执行
            // Github 地址：https://github.com/getActivity/EasyWindow/issues/71
            if (mDownEventFlag) {
                MotionEvent newMotionEvent = MotionEvent.obtain(ev);
                newMotionEvent.setAction(MotionEvent.ACTION_CANCEL);
                super.dispatchTouchEvent(newMotionEvent);
                mDownEventFlag = false;
            }
            return true;
        }

        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            mDownEventFlag = true;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void setOnTouchListener(OnTouchListener l) {
        //super.setOnTouchListener(l);
        mOnTouchListener = l;
    }
}