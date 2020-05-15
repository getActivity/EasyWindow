package com.hjq.xtoast;

import java.lang.ref.WeakReference;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XToast
 *    time   : 2019/01/04
 *    desc   : Toast 定时销毁任务
 */
final class ToastDismissRunnable extends WeakReference<XToast>
        implements Runnable {

    ToastDismissRunnable(XToast toast) {
        super(toast);
    }

    @Override
    public void run() {
        XToast toast = get();
        if (toast != null && toast.isShow()) {
            toast.cancel();
        }
    }
}