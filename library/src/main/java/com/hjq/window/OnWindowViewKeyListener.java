package com.hjq.window;

import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.View;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyWindow
 *    time   : 2025/07/10
 *    desc   : 窗口 View 的按键事件监听
 */
public interface OnWindowViewKeyListener<V extends View> {

    /**
     * 点击回调
     */
    boolean onKey(@NonNull EasyWindow<?> easyWindow, @NonNull V view, @NonNull KeyEvent event, int keyCode);
}