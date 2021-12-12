package com.hjq.xtoast.demo;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;
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
public final class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_main_anim).setOnClickListener(this);
        findViewById(R.id.btn_main_duration).setOnClickListener(this);
        findViewById(R.id.btn_main_overlay).setOnClickListener(this);
        findViewById(R.id.btn_main_lifecycle).setOnClickListener(this);
        findViewById(R.id.btn_main_click).setOnClickListener(this);
        findViewById(R.id.btn_main_view).setOnClickListener(this);
        findViewById(R.id.btn_main_input).setOnClickListener(this);
        findViewById(R.id.btn_main_draggable).setOnClickListener(this);
        findViewById(R.id.btn_main_global).setOnClickListener(this);
        findViewById(R.id.btn_main_utils).setOnClickListener(this);

        TitleBar titleBar = findViewById(R.id.tb_main_bar);
        titleBar.setOnTitleBarListener(new OnTitleBarListener() {
            @Override
            public void onTitleClick(TitleBar titleBar) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(titleBar.getTitle().toString()));
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.btn_main_anim) {

            new XToast<>(this)
                    .setDuration(1000)
                    .setContentView(R.layout.window_hint)
                    .setAnimStyle(R.style.TopAnimStyle)
                    .setImageDrawable(android.R.id.icon, R.mipmap.ic_dialog_tip_finish)
                    .setText(android.R.id.message, "这个动画是不是很骚")
                    .show();

        } else if (viewId == R.id.btn_main_duration) {

            new XToast<>(this)
                    .setDuration(1000)
                    .setContentView(R.layout.window_hint)
                    .setAnimStyle(R.style.IOSAnimStyle)
                    .setImageDrawable(android.R.id.icon, R.mipmap.ic_dialog_tip_error)
                    .setText(android.R.id.message, "一秒后自动消失")
                    .show();

        } else if (viewId == R.id.btn_main_overlay) {

            new XToast<>(this)
                    .setContentView(R.layout.window_hint)
                    .setAnimStyle(R.style.IOSAnimStyle)
                    .setImageDrawable(android.R.id.icon, R.mipmap.ic_dialog_tip_finish)
                    .setText(android.R.id.message, "点我消失")
                    // 设置外层是否能被触摸
                    .setOutsideTouchable(false)
                    // 设置窗口背景阴影强度
                    .setBackgroundDimAmount(0.5f)
                    .setOnClickListener(android.R.id.message, new XToast.OnClickListener<TextView>() {

                        @Override
                        public void onClick(XToast<?> toast, TextView view) {
                            toast.cancel();
                        }
                    })
                    .show();

        } else if (viewId == R.id.btn_main_lifecycle) {

            new XToast<>(this)
                    .setDuration(3000)
                    .setContentView(R.layout.window_hint)
                    .setAnimStyle(R.style.IOSAnimStyle)
                    .setImageDrawable(android.R.id.icon, R.mipmap.ic_dialog_tip_warning)
                    .setText(android.R.id.message, "请注意下方 Snackbar")
                    .setOnToastLifecycle(new XToast.OnLifecycle() {

                        @Override
                        public void onShow(XToast<?> toast) {
                            Snackbar.make(getWindow().getDecorView(), "显示回调", Snackbar.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onDismiss(XToast<?> toast) {
                            Snackbar.make(getWindow().getDecorView(), "消失回调", Snackbar.LENGTH_SHORT).show();
                        }
                    })
                    .show();

        } else if (viewId == R.id.btn_main_click) {

            new XToast<>(this)
                    .setContentView(R.layout.window_hint)
                    .setAnimStyle(R.style.IOSAnimStyle)
                    .setImageDrawable(android.R.id.icon, R.mipmap.ic_dialog_tip_finish)
                    .setText(android.R.id.message, "点我点我点我")
                    .setOnClickListener(android.R.id.message, new XToast.OnClickListener<TextView>() {

                        @Override
                        public void onClick(final XToast<?> toast, TextView view) {
                            view.setText("不错，很听话");
                            toast.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    toast.cancel();
                                }
                            }, 1000);
                        }
                    })
                    .show();

        } else if (viewId == R.id.btn_main_view) {

            new XToast<>(this)
                    .setContentView(R.layout.window_hint)
                    .setAnimStyle(R.style.RightAnimStyle)
                    .setImageDrawable(android.R.id.icon, R.mipmap.ic_dialog_tip_finish)
                    .setDuration(2000)
                    .setText(android.R.id.message, "位置算得准不准")
                    .setOnClickListener(android.R.id.message, new XToast.OnClickListener<TextView>() {

                        @Override
                        public void onClick(final XToast<?> toast, TextView view) {
                            toast.cancel();
                        }
                    })
                    .showAsDropDown(v, Gravity.BOTTOM);

        } else if (viewId == R.id.btn_main_input) {

            new XToast<>(this)
                    .setContentView(R.layout.window_input)
                    .setAnimStyle(R.style.BottomAnimStyle)
                    .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
                    .setOnClickListener(R.id.tv_window_close, new XToast.OnClickListener<TextView>() {

                        @Override
                        public void onClick(final XToast<?> toast, TextView view) {
                            toast.cancel();
                        }
                    })
                    .show();

        } else if (viewId == R.id.btn_main_draggable) {

            new XToast<>(this)
                    .setContentView(R.layout.window_hint)
                    .setAnimStyle(R.style.IOSAnimStyle)
                    .setImageDrawable(android.R.id.icon, R.mipmap.ic_dialog_tip_finish)
                    .setText(android.R.id.message, "点我消失")
                    // 设置成可拖拽的
                    .setDraggable(new MovingDraggable())
                    .setOnClickListener(android.R.id.message, new XToast.OnClickListener<TextView>() {

                        @Override
                        public void onClick(XToast<?> toast, TextView view) {
                            toast.cancel();
                        }
                    })
                    .show();

        } else if (viewId == R.id.btn_main_global) {

            XXPermissions.with(this)
                    .permission(Permission.SYSTEM_ALERT_WINDOW)
                    .request(new OnPermissionCallback() {

                        @Override
                        public void onGranted(List<String> granted, boolean all) {
                            showGlobalWindow(getApplication());
                        }

                        @Override
                        public void onDenied(List<String> denied, boolean never) {
                            new XToast<>(MainActivity.this)
                                    .setDuration(1000)
                                    .setContentView(R.layout.window_hint)
                                    .setImageDrawable(android.R.id.icon, R.mipmap.ic_dialog_tip_error)
                                    .setText(android.R.id.message, "请先授予悬浮窗权限")
                                    .show();
                        }
                    });

        } else if (viewId == R.id.btn_main_utils) {

            new XToast<>(this)
                    .setDuration(1000)
                    // 将 ToastUtils 中的 View 转移给 XToast 来显示
                    .setContentView(ToastUtils.getStyle().createView(this))
                    .setAnimStyle(R.style.ScaleAnimStyle)
                    .setText(android.R.id.message, "就问你溜不溜")
                    .setGravity(Gravity.BOTTOM)
                    .setYOffset(100)
                    .show();
        }
    }

    /**
     * 显示全局弹窗
     */
    public static void showGlobalWindow(Application application) {
        // 传入 Application 表示这个是一个全局的 Toast
        new XToast<>(application)
                .setContentView(R.layout.window_phone)
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
}