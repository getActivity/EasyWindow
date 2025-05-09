package com.hjq.window;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyWindow
 *    time   : 2019/01/04
 *    desc   : {@link View.OnTouchListener} 包装类
 */
@SuppressWarnings("rawtypes")
final class ViewTouchListenerWrapper implements View.OnTouchListener {

    @NonNull
    private final EasyWindow<?> mEasyWindow;
    @NonNull
    private final OnWindowVIewTouchListener mListener;

    ViewTouchListenerWrapper(@NonNull EasyWindow<?> easyWindow, @NonNull OnWindowVIewTouchListener listener) {
        mEasyWindow = easyWindow;
        mListener = listener;
    }

    @SuppressLint("ClickableViewAccessibility")
    @SuppressWarnings("unchecked")
    @Override
    public boolean onTouch(View view, MotionEvent event) {
        return mListener.onTouch(mEasyWindow, view, event);
    }
}