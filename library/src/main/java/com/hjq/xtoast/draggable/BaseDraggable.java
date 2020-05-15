package com.hjq.xtoast.draggable;

import android.graphics.Rect;
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

    private XToast mToast;
    private View mRootView;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mWindowParams;

    /**
     * Toast 显示后回调这个类
     */
    public void start(XToast toast) {
        mToast = toast;
        mRootView = toast.getView();
        mWindowManager = toast.getWindowManager();
        mWindowParams = toast.getWindowParams();

        mRootView.setOnTouchListener(this);
    }

    protected XToast getXToast() {
        return mToast;
    }

    protected WindowManager getWindowManager() {
        return mWindowManager;
    }

    protected WindowManager.LayoutParams getWindowParams() {
        return mWindowParams;
    }

    protected View getRootView() {
        return mRootView;
    }

    /**
     * 获取状态栏的高度
     */
    protected int getStatusBarHeight() {
        Rect frame = new Rect();
        getRootView().getWindowVisibleDisplayFrame(frame);
        return frame.top;
    }

    protected void updateLocation(float x, float y) {
        updateLocation((int) x, (int) y);
    }

    /**
     * 更新 WindowManager 所在的位置
     */
    protected void updateLocation(int x, int y) {
        if (mWindowParams.x != x || mWindowParams.y != y) {
            mWindowParams.x = x;
            mWindowParams.y = y;
            // 一定要先设置重心位置为左上角
            mWindowParams.gravity = Gravity.TOP | Gravity.START;
            try {
                mWindowManager.updateViewLayout(mRootView, mWindowParams);
            } catch (IllegalArgumentException ignored) {
                // 当 WindowManager 已经消失时调用会发生崩溃
                // IllegalArgumentException: View not attached to window manager
            }
        }
    }
}