package com.hjq.window;

import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyWindow
 *    time   : 2019/01/04
 *    desc   : 悬浮窗生命周期管理，防止内存泄露
 */
final class WindowLifecycleControl implements Application.ActivityLifecycleCallbacks {

    @Nullable
    private Activity mActivity;
    @Nullable
    private EasyWindow<?> mEasyWindow;

    WindowLifecycleControl(@NonNull EasyWindow<?> easyWindow, @NonNull Activity activity) {
        mActivity = activity;
        mEasyWindow = easyWindow;
    }

    /**
     * 注册监听
     */
    void register() {
        if (mActivity == null) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            mActivity.registerActivityLifecycleCallbacks(this);
        } else {
            mActivity.getApplication().registerActivityLifecycleCallbacks(this);
        }
    }

    /**
     * 取消监听
     */
    void unregister() {
        if (mActivity == null) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            mActivity.unregisterActivityLifecycleCallbacks(this);
        } else {
            mActivity.getApplication().unregisterActivityLifecycleCallbacks(this);
        }
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, Bundle savedInstanceState) {
        // default implementation ignored
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        // default implementation ignored
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        // default implementation ignored
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        // 一定要在 onPaused 方法中销毁掉，如果放在 onDestroyed 方法中还是有一定几率会导致内存泄露
        if (mActivity != activity || !mActivity.isFinishing() || mEasyWindow == null || !mEasyWindow.isShowing()) {
            return;
        }
        mEasyWindow.cancel();
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        // default implementation ignored
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
        // default implementation ignored
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        if (mActivity != activity) {
            return;
        }
        // 释放 Activity 的引用
        mActivity = null;

        if (mEasyWindow == null) {
            return;
        }
        mEasyWindow.recycle();
        mEasyWindow = null;
    }
}