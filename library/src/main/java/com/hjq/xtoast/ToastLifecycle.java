package com.hjq.xtoast;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.Bundle;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/ToastUtils
 *    time   : 2019/01/04
 *    desc   : Toast 生命周期管理，防止内存泄露
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
final class ToastLifecycle implements Application.ActivityLifecycleCallbacks {

    private Activity mActivity;
    private XToast mToast;

    ToastLifecycle(Activity activity, XToast toast) {
        mActivity = activity;
        mToast = toast;

        activity.getApplication().registerActivityLifecycleCallbacks(this);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {}

    @Override
    public void onActivityStarted(Activity activity) {}

    @Override
    public void onActivityResumed(Activity activity) {}

    @Override
    public void onActivityPaused(Activity activity) {
        // 一定要在 onPaused 方法中销毁掉，如果在 onDestroyed 方法中还是会导致内存泄露
        if (mActivity == activity && activity.isFinishing() && mToast.isShow()) {
            mToast.cancel();
        }
    }

    @Override
    public void onActivityStopped(Activity activity) {}

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}

    @Override
    public void onActivityDestroyed(Activity activity) {}
}
