package com.hjq.xtoast.demo;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // 这个过程专门用于堆分析的 leak 金丝雀
        // 你不应该在这个过程中初始化你的应用程序
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }

        // 内存泄漏检测
        LeakCanary.install(this);
    }
}
