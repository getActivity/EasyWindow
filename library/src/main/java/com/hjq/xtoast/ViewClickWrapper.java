package com.hjq.xtoast;

import android.view.View;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XToast
 *    time   : 2019/01/04
 *    desc   : {@link View.OnClickListener} 包装类
 */
@SuppressWarnings("rawtypes")
final class ViewClickWrapper implements View.OnClickListener {

    private final XToast<?> mToast;
    private final OnClickListener mListener;

    ViewClickWrapper(XToast<?> toast, View view, OnClickListener listener) {
        mToast = toast;
        mListener = listener;

        view.setClickable(true);
        view.setOnClickListener(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public final void onClick(View v) {
        mListener.onClick(mToast, v);
    }
}