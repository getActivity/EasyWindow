package com.hjq.window;

import android.support.annotation.NonNull;
import android.view.View;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyWindow
 *    time   : 2025/05/03
 *    desc   : 窗口 View 的点击事件监听
 */
public interface OnWindowViewClickListener<V extends View> {

    /**
     * 点击回调
     */
    void onClick(@NonNull EasyWindow<?> easyWindow, @NonNull V view);
}