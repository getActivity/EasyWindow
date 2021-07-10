package com.hjq.xtoast.demo;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.hjq.toast.ToastUtils;
import com.hjq.xtoast.XToast;
import com.hjq.xtoast.draggable.MovingDraggable;
import com.hjq.xtoast.draggable.SpringDraggable;

import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XToast
 *    time   : 2019/01/04
 *    desc   : Demo 使用案例
 */
public final class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void show1(View v) {
        new XToast<>(this)
                .setDuration(3000)
                .setView(R.layout.toast_hint)
                .setAnimStyle(android.R.style.Animation_Translucent)
                .setImageDrawable(android.R.id.icon, R.mipmap.ic_dialog_tip_finish)
                .setText(android.R.id.message, "这个动画是不是很骚")
                .show();
    }

    public void show2(View v) {
        new XToast<>(this)
                .setDuration(1000)
                .setView(R.layout.toast_hint)
                .setAnimStyle(android.R.style.Animation_Activity)
                .setImageDrawable(android.R.id.icon, R.mipmap.ic_dialog_tip_error)
                .setText(android.R.id.message, "一秒后消失")
                .show();
    }

    public void show3(View v) {
        new XToast<>(this)
                .setDuration(3000)
                .setView(R.layout.toast_hint)
                .setAnimStyle(android.R.style.Animation_Dialog)
                .setImageDrawable(android.R.id.icon, R.mipmap.ic_dialog_tip_warning)
                .setText(android.R.id.message, "是不是感觉很牛逼")
                .setOnToastListener(new XToast.OnToastListener() {

                    @Override
                    public void onShow(XToast<?> toast) {
                        Snackbar.make(getWindow().getDecorView(), "XToast 显示了", Snackbar.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onDismiss(XToast<?> toast) {
                        Snackbar.make(getWindow().getDecorView(), "XToast 消失了", Snackbar.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    public void show4(View v) {
        new XToast<>(this)
                .setView(R.layout.toast_hint)
                .setAnimStyle(android.R.style.Animation_Translucent)
                .setImageDrawable(android.R.id.icon, R.mipmap.ic_dialog_tip_finish)
                .setText(android.R.id.message, "点我点我点我")
                .setOnClickListener(android.R.id.message, new XToast.OnClickListener<TextView>() {

                    @Override
                    public void onClick(final XToast<?> toast, TextView view) {
                        view.setText("那么听话啊");
                        toast.postDelayed(new Runnable() {
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
        new XToast<>(this)
                .setView(R.layout.toast_hint)
                .setAnimStyle(android.R.style.Animation_Translucent)
                .setImageDrawable(android.R.id.icon, R.mipmap.ic_dialog_tip_finish)
                .setDuration(2000)
                .setText(android.R.id.message, "位置算的准不准")
                .setOnClickListener(android.R.id.message, new XToast.OnClickListener<TextView>() {

                    @Override
                    public void onClick(final XToast<?> toast, TextView view) {
                        toast.cancel();
                    }
                })
                .showAsDropDown(v, Gravity.BOTTOM);
    }

    public void show6(View v) {
        new XToast<>(this)
                .setView(R.layout.toast_hint)
                .setAnimStyle(android.R.style.Animation_Translucent)
                .setImageDrawable(android.R.id.icon, R.mipmap.ic_dialog_tip_finish)
                .setText(android.R.id.message, "点我消失")
                // 设置外层是否能被触摸
                .setOutsideTouchable(false)
                // 设置窗口背景阴影强度
                .setBackgroundDimAmount(0.5f)
                // 设置成可拖拽的
                .setDraggable(new MovingDraggable())
                .setOnClickListener(android.R.id.message, new XToast.OnClickListener<TextView>() {

                    @Override
                    public void onClick(XToast<?> toast, TextView view) {
                        toast.cancel();
                    }
                })
                .show();
    }

    public void show7(View v) {
        XXPermissions.with(this)
                .permission(Permission.SYSTEM_ALERT_WINDOW)
                .request(new OnPermissionCallback() {

                    @Override
                    public void onGranted(List<String> granted, boolean all) {
                        // 传入 Application 表示这个是一个全局的 Toast
                        new XToast<>(getApplication())
                                .setView(R.layout.toast_phone)
                                .setGravity(Gravity.END | Gravity.BOTTOM)
                                .setYOffset(200)
                                // 设置指定的拖拽规则
                                .setDraggable(new SpringDraggable())
                                .setOnClickListener(android.R.id.icon, new XToast.OnClickListener<ImageView>() {

                                    @Override
                                    public void onClick(XToast<?> toast, ImageView view) {
                                        ToastUtils.show("我被点击了");
                                        // 点击后跳转到拨打电话界面
                                        // Intent intent = new Intent(Intent.ACTION_DIAL);
                                        // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        // toast.startActivity(intent);
                                        // 安卓 10 在后台跳转 Activity 需要额外适配
                                        // https://developer.android.google.cn/about/versions/10/privacy/changes#background-activity-starts
                                    }
                                })
                                .show();
                    }

                    @Override
                    public void onDenied(List<String> denied, boolean never) {
                        new XToast<>(MainActivity.this)
                                .setDuration(1000)
                                .setView(R.layout.toast_hint)
                                .setImageDrawable(android.R.id.icon, R.mipmap.ic_dialog_tip_error)
                                .setText(android.R.id.message, "请先授予悬浮窗权限")
                                .show();
                    }
                });
    }

    public void show8(View v) {
        // 将 ToastUtils 中的 View 转移给 XToast 来显示
        new XToast<>(this)
                .setDuration(1000)
                .setView(ToastUtils.getStyle().createView(this))
                .setAnimStyle(android.R.style.Animation_Translucent)
                .setText(android.R.id.message, "就问你溜不溜")
                .setGravity(Gravity.BOTTOM)
                .setYOffset(100)
                .show();
    }
}