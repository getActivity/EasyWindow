package com.hjq.window;

import android.view.View;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyWindow
 *    time   : 2025/05/03
 *    desc   : View 的点击事件监听
 */
public interface OnViewClickListener<V extends View> {

    /**
     * 点击回调
     */
    void onClick(EasyWindow<?> easyWindow, V view);
}
