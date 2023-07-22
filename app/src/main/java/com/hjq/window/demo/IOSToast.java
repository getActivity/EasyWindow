package com.hjq.window.demo;

import android.app.Activity;

import com.hjq.window.EasyWindow;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyWindow
 *    time   : 2019/01/04
 *    desc   : 仿 IOS 弹框
 */
public final class IOSToast {

    private static final int TIME = 3000;

    public static void showSucceed(Activity activity, CharSequence text) {
        EasyWindow.with(activity)
                .setDuration(TIME)
                .setContentView(R.layout.window_hint)
                .setAnimStyle(android.R.style.Animation_Translucent)
                .setImageDrawable(android.R.id.icon, R.drawable.ic_dialog_tip_finish)
                .setText(android.R.id.message, text)
                .show();
    }

    public static void showFail(Activity activity, CharSequence text) {
        EasyWindow.with(activity)
                .setDuration(TIME)
                .setContentView(R.layout.window_hint)
                .setAnimStyle(android.R.style.Animation_Activity)
                .setImageDrawable(android.R.id.icon, R.drawable.ic_dialog_tip_error)
                .setText(android.R.id.message, text)
                .show();
    }

    public static void showWarn(Activity activity, CharSequence text) {
        EasyWindow.with(activity)
                .setDuration(TIME)
                .setContentView(R.layout.window_hint)
                .setAnimStyle(android.R.style.Animation_Dialog)
                .setImageDrawable(android.R.id.icon, R.drawable.ic_dialog_tip_warning)
                .setText(android.R.id.message, text)
                .show();
    }
}