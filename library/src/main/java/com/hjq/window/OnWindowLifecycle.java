package com.hjq.window;

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
    default void onWindowShow(EasyWindow<?> easyWindow) {}

    /**
     * 窗口更新回调
     */
    default void onWindowUpdate(EasyWindow<?> easyWindow) {}

    /**
     * 窗口消失回调
     */
    default void onWindowCancel(EasyWindow<?> easyWindow) {}

    /**
     * 窗口回收回调
     */
    default void onWindowRecycle(EasyWindow<?> easyWindow) {}

    /**
     * 窗口可见性发生变化
     */
    default void onWindowVisibilityChanged(EasyWindow<?> easyWindow, int visibility) {}
}