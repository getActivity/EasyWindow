package com.hjq.window;

import android.support.annotation.NonNull;
import android.view.View;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyWindow
 *    time   : 2021/09/03
 *    desc   : {@link View.OnLongClickListener} 包装类
 */
@SuppressWarnings("rawtypes")
final class ViewLongClickListenerWrapper implements View.OnLongClickListener {

    @NonNull
    private final EasyWindow<?> mEasyWindow;
    @NonNull
    private final OnWindowViewLongClickListener mListener;

    ViewLongClickListenerWrapper(@NonNull EasyWindow<?> easyWindow, @NonNull OnWindowViewLongClickListener listener) {
        mEasyWindow = easyWindow;
        mListener = listener;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean onLongClick(View view) {
        return mListener.onLongClick(mEasyWindow, view);
    }
}