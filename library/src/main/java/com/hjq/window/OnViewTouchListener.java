package com.hjq.window;

import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyWindow
 *    time   : 2025/05/03
 *    desc   : View 的触摸事件监听
 */
public interface OnViewTouchListener<V extends View> {

    /**
     * 触摸回调
     */
    boolean onTouch(@NonNull EasyWindow<?> easyWindow, @NonNull V view, @NonNull MotionEvent event);
}