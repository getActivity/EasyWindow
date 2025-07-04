package com.hjq.window;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.NonNull;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyWindow
 *    time   : 2025/07/04
 *    desc   : 窗口任务处理类
 */
public final class WindowTaskHandler {

    /** Handler 对象 */
    private static final Handler HANDLER = new Handler(Looper.getMainLooper());

    /**
     * 延迟发送一个任务
     */
    public static void sendTask(@NonNull Runnable runnable, long delayMillis) {
        HANDLER.postDelayed(runnable, delayMillis);
    }

    /**
     * 延迟发送一个指定令牌的任务
     */
    public static void sendTask(@NonNull Runnable runnable, @NonNull Object token, long delayMillis) {
        if (delayMillis < 0) {
            delayMillis = 0;
        }
        long uptimeMillis = SystemClock.uptimeMillis() + delayMillis;
        HANDLER.postAtTime(runnable, token, uptimeMillis);
    }

    /**
     * 取消一个指定的任务
     */
    public static void cancelTask(@NonNull Runnable runnable) {
        HANDLER.removeCallbacks(runnable);
    }

    /**
     * 取消一个指定的令牌任务
     */
    public static void cancelTask(@NonNull Object token) {
        // 移除和当前对象相关的消息回调
        HANDLER.removeCallbacksAndMessages(token);
    }
}