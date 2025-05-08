package com.hjq.window;

import android.support.annotation.NonNull;
import android.view.View;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyWindow
 *    time   : 2019/01/04
 *    desc   : {@link View.OnClickListener} 包装类
 */
@SuppressWarnings("rawtypes")
final class ViewClickWrapper implements View.OnClickListener {

    @NonNull
    private final EasyWindow<?> mEasyWindow;
    @NonNull
    private final OnViewClickListener mListener;

    ViewClickWrapper(@NonNull EasyWindow<?> easyWindow, @NonNull OnViewClickListener listener) {
        mEasyWindow = easyWindow;
        mListener = listener;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onClick(View view) {
        mListener.onClick(mEasyWindow, view);
    }
}