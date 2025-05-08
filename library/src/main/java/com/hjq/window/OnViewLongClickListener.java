package com.hjq.window;

import android.support.annotation.NonNull;
import android.view.View;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyWindow
 *    time   : 2025/05/03
 *    desc   : View 的长按事件监听
 */
public interface OnViewLongClickListener<V extends View> {

    /**
     * 长按回调
     */
    boolean onLongClick(@NonNull EasyWindow<?> easyWindow, @NonNull V view);
}