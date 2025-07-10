package com.hjq.window;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyWindow
 *    time   : 2025/05/03
 *    desc   : 窗口布局填充回调
 */
public interface OnWindowLayoutInflateListener {

    /**
     * 布局填充完成回调
     *
     * @param easyWindow        当前窗口对象
     * @param view              填充完成的 View
     * @param layoutId          布局 id
     * @param parentView        填充布局所用父布局对象
     */
    void onWindowLayoutInflateFinished(@NonNull EasyWindow<?> easyWindow, @NonNull View view, @LayoutRes int layoutId, @NonNull ViewGroup parentView);
}