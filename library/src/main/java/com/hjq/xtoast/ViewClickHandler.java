package com.hjq.xtoast;

import android.view.View;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/ToastUtils
 *    time   : 2019/01/04
 *    desc   : {@link View.OnClickListener} 包装类
 */
final class ViewClickHandler implements View.OnClickListener {

    private final XToast mToast;
    private final OnClickListener mListener;

    ViewClickHandler(XToast toast, View view, OnClickListener listener) {
        mToast = toast;
        mListener = listener;

        view.setOnClickListener(this);
    }

    @Override
    public final void onClick(View v) {
        mListener.onClick(mToast, v);
    }
}