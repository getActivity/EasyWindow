package com.hjq.xtoast.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.hjq.toast.ToastUtils;
import com.hjq.xtoast.OnClickListener;
import com.hjq.xtoast.OnToastListener;
import com.hjq.xtoast.XToast;
import com.hjq.xtoast.draggable.MovingDraggable;
import com.hjq.xtoast.draggable.SpringDraggable;

import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XToast
 *    time   : 2019/01/04
 *    desc   : XToast 使用案例
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void show1(View v) {
        new XToast(this)
                .setDuration(3000)
                .setView(R.layout.toast_hint)
                .setAnimStyle(android.R.style.Animation_Translucent)
                .setImageDrawable(android.R.id.icon, R.mipmap.ic_dialog_tip_finish)
                .setText(android.R.id.message, "这个动画是不是很骚")
                .show();
    }

    public void show2(View v) {
        new XToast(this)
                .setDuration(1000)
                .setView(R.layout.toast_hint)
                .setAnimStyle(android.R.style.Animation_Activity)
                .setImageDrawable(android.R.id.icon, R.mipmap.ic_dialog_tip_error)
                .setText(android.R.id.message, "一秒后消失")
                .show();
    }

    public void show3(View v) {
        new XToast(this)
                .setDuration(3000)
                .setView(R.layout.toast_hint)
                .setAnimStyle(android.R.style.Animation_Dialog)
                .setImageDrawable(android.R.id.icon, R.mipmap.ic_dialog_tip_warning)
                .setText(android.R.id.message, "是不是感觉很牛逼")
                .setOnToastListener(new OnToastListener() {

                    @Override
                    public void onShow(XToast toast) {
                        Snackbar.make(getWindow().getDecorView(), "XToast 显示了", Snackbar.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onDismiss(XToast toast) {
                        Snackbar.make(getWindow().getDecorView(), "XToast 消失了", Snackbar.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    public void show4(View v) {
        new XToast(this)
                .setView(R.layout.toast_hint)
                .setAnimStyle(android.R.style.Animation_Translucent)
                .setImageDrawable(android.R.id.icon, R.mipmap.ic_dialog_tip_finish)
                .setText(android.R.id.message, "点我点我点我")
                .setOnClickListener(android.R.id.message, new OnClickListener<TextView>() {

                    @Override
                    public void onClick(final XToast toast, TextView view) {
                        view.setText("那么听话啊");
                        getWindow().getDecorView().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                toast.cancel();
                            }
                        }, 1000);
                    }
                })
                .show();
    }

    public void show5(View v) {
        new XToast(this)
                .setView(R.layout.toast_hint)
                .setAnimStyle(android.R.style.Animation_Translucent)
                .setImageDrawable(android.R.id.icon, R.mipmap.ic_dialog_tip_finish)
                .setText(android.R.id.message, "点我消失")
                // 设置外层不能被触摸
                .setOutsideTouchable(false)
                // 设置窗口背景阴影强度
                .setBackgroundDimAmount(0.5f)
                // 设置成可拖拽的
                .setDraggable(new MovingDraggable())
                .setOnClickListener(android.R.id.message, new OnClickListener<TextView>() {

                    @Override
                    public void onClick(XToast toast, TextView view) {
                        toast.cancel();
                    }
                })
                .show();
    }

    public void show6(View v) {
        XXPermissions.with(this)
                .permission(Permission.SYSTEM_ALERT_WINDOW)
                .request(new OnPermission() {

                    @Override
                    public void hasPermission(List<String> granted, boolean all) {

                        // 传入 Application 表示这个是一个全局的 Toast
                        new XToast(getApplication())
                                .setView(R.layout.toast_phone)
                                .setGravity(Gravity.END | Gravity.BOTTOM)
                                .setXOffset(100)
                                .setYOffset(100)
                                // 设置指定的拖拽规则
                                .setDraggable(new SpringDraggable())
                                .setOnClickListener(android.R.id.icon, new OnClickListener<ImageView>() {

                                    @Override
                                    public void onClick(XToast toast, ImageView view) {
                                        // 点击后跳转到拨打电话界面
                                        Intent intent = new Intent(Intent.ACTION_DIAL);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        toast.startActivity(intent);
                                        // 安卓 10 在后台跳转 Activity 需要额外适配
                                        // https://developer.android.google.cn/about/versions/10/privacy/changes#background-activity-starts
                                    }
                                })
                                .show();
                    }

                    @Override
                    public void noPermission(List<String> denied, boolean never) {
                        new XToast(MainActivity.this)
                                .setDuration(1000)
                                .setView(R.layout.toast_hint)
                                .setImageDrawable(android.R.id.icon, R.mipmap.ic_dialog_tip_error)
                                .setText(android.R.id.message, "请先授予悬浮窗权限")
                                .show();
                    }
                });
    }

    public void show7(View v) {
        // 这里需要先初始化 ToastUtils，实际开发中这句代码应当放在 Application.onCreate 方法中
        ToastUtils.init(getApplication());
        // 将 ToastUtils 中的 View 转移给 XToast 来显示
        new XToast(MainActivity.this)
                .setDuration(1000)
                .setView(ToastUtils.getToast().getView())
                .setAnimStyle(android.R.style.Animation_Translucent)
                .setText(android.R.id.message, "就问你溜不溜")
                .show();
    }
}