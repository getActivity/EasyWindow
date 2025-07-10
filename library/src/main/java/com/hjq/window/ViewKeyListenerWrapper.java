package com.hjq.window;

import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.View;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyWindow
 *    time   : 2025/07/10
 *    desc   : {@link View.OnKeyListener} 包装类
 */
@SuppressWarnings("rawtypes")
final class ViewKeyListenerWrapper implements View.OnKeyListener {

    @NonNull
    private final EasyWindow<?> mEasyWindow;
    @NonNull
    private final OnWindowViewKeyListener mListener;

    ViewKeyListenerWrapper(@NonNull EasyWindow<?> easyWindow, @NonNull OnWindowViewKeyListener listener) {
        mEasyWindow = easyWindow;
        mListener = listener;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean onKey(View view, int keyCode, KeyEvent event) {
        return mListener.onKey(mEasyWindow, view, event, keyCode);
    }
}