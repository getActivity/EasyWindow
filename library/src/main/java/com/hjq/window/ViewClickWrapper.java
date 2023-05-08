package com.hjq.window;

import android.view.View;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyWindow
 *    time   : 2019/01/04
 *    desc   : {@link View.OnClickListener} 包装类
 */
@SuppressWarnings("rawtypes")
final class ViewClickWrapper implements View.OnClickListener {

    private final EasyWindow<?> mWindow;
    private final EasyWindow.OnClickListener mListener;

    ViewClickWrapper(EasyWindow<?> window, EasyWindow.OnClickListener listener) {
        mWindow = window;
        mListener = listener;
    }

    @SuppressWarnings("unchecked")
    @Override
    public final void onClick(View view) {
        if (mListener == null) {
            return;
        }
        mListener.onClick(mWindow, view);
    }
}