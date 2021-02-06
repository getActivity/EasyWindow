package com.hjq.xtoast;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XToast
 *    time   : 2019/01/04
 *    desc   : Toast 显示销毁监听
 */
public interface OnToastListener {

    /**
     * 显示回调
     */
    void onShow(XToast<?> toast);

    /**
     * 消失回调
     */
    void onDismiss(XToast<?> toast);
}