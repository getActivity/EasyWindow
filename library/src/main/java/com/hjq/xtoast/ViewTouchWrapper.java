package com.hjq.xtoast;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XToast
 *    time   : 2019/01/04
 *    desc   : {@link View.OnTouchListener} 包装类
 */
@SuppressWarnings("rawtypes")
final class ViewTouchWrapper implements View.OnTouchListener {

    private final XToast<?> mToast;
    private final OnTouchListener mListener;

    ViewTouchWrapper(XToast<?> toast, View view, OnTouchListener listener) {
        mToast = toast;
        mListener = listener;

        view.setEnabled(true);
        view.setOnTouchListener(this);
    }

    @SuppressLint("ClickableViewAccessibility")
    @SuppressWarnings("unchecked")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mListener.onTouch(mToast, v, event);
    }
}