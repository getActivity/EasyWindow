package com.hjq.xtoast;

import java.lang.ref.SoftReference;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XToast
 *    time   : 2019/01/04
 *    desc   : Toast 定时销毁任务
 */
final class CancelRunnable extends SoftReference<XToast<?>> implements Runnable {

    CancelRunnable(XToast toast) {
        super(toast);
    }

    @Override
    public void run() {
        XToast<?> toast = get();
        if (toast == null || !toast.isShow()) {
            return;
        }
        toast.cancel();
    }
}