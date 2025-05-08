package com.hjq.window;

import android.support.annotation.NonNull;
import android.view.View;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyWindow
 *    time   : 2025/05/03
 *    desc   : 窗口生命周期监听
 */
public interface OnWindowLifecycle {

    /**
     * 窗口显示回调
     */
    default void onWindowShow(@NonNull EasyWindow<?> easyWindow) {}

    /**
     * 窗口更新回调
     */
    default void onWindowUpdate(@NonNull EasyWindow<?> easyWindow) {}

    /**
     * 窗口消失回调
     */
    default void onWindowCancel(@NonNull EasyWindow<?> easyWindow) {}

    /**
     * 窗口回收回调
     */
    default void onWindowRecycle(@NonNull EasyWindow<?> easyWindow) {}

    /***
     * 窗口可见性发生变化
     *
     * @param visibility            窗口可见性类型，有三种类型：
     *                              {@link View#VISIBLE}
     *                              {@link View#INVISIBLE}
     *                              {@link View#GONE}
     */
    default void onWindowVisibilityChanged(@NonNull EasyWindow<?> easyWindow, int visibility) {}
}