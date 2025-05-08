package com.hjq.window.demo;

import android.animation.Animator;
import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.hjq.toast.Toaster;
import com.hjq.window.EasyWindow;
import com.hjq.window.OnWindowLayoutInflateListener;
import com.hjq.window.OnViewClickListener;
import com.hjq.window.OnViewLongClickListener;
import com.hjq.window.OnWindowLifecycle;
import com.hjq.window.demo.DemoAdapter.OnItemClickListener;
import com.hjq.window.demo.DemoAdapter.OnItemLongClickListener;
import com.hjq.window.draggable.BaseDraggable.DraggingCallback;
import com.hjq.window.draggable.MovingDraggable;
import com.hjq.window.draggable.SpringBackDraggable;
import com.hjq.window.draggable.SpringBackDraggable.SpringBackAnimCallback;
import java.util.ArrayList;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyWindow
 *    time   : 2019/01/04
 *    desc   : Demo 使用案例
 */
public final class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "EasyWindow";

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
        findViewById(R.id.btn_main_web).setOnClickListener(this);
        findViewById(R.id.btn_main_list).setOnClickListener(this);
        findViewById(R.id.btn_main_draggable).setOnClickListener(this);
        findViewById(R.id.btn_main_global).setOnClickListener(this);
        findViewById(R.id.btn_main_utils).setOnClickListener(this);
        findViewById(R.id.btn_main_cancel_all).setOnClickListener(this);

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
    public void onBackPressed() {
        // 在某些手机上面无法通过返回键销毁当前 Activity 对象，从而无法触发 LeakCanary 回收对象
        finish();
    }

    /** @noinspection unchecked*/
    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.btn_main_anim) {

            EasyWindow.with(this)
                    .setDuration(1000)
                    .setContentView(R.layout.window_hint)
                    .setAnimStyle(R.style.TopAnimStyle)
                    .setImageDrawable(android.R.id.icon, R.drawable.ic_dialog_tip_finish)
                    .setText(android.R.id.message, "这个动画是不是很骚")
                    .show();

        } else if (viewId == R.id.btn_main_duration) {

            EasyWindow.with(this)
                    .setDuration(1000)
                    .setContentView(R.layout.window_hint)
                    .setAnimStyle(R.style.IOSAnimStyle)
                    .setImageDrawable(android.R.id.icon, R.drawable.ic_dialog_tip_error)
                    .setText(android.R.id.message, "一秒后自动消失")
                    .show();

        } else if (viewId == R.id.btn_main_overlay) {

            EasyWindow.with(this)
                    .setContentView(R.layout.window_hint)
                    .setAnimStyle(R.style.IOSAnimStyle)
                    .setImageDrawable(android.R.id.icon, R.drawable.ic_dialog_tip_finish)
                    .setText(android.R.id.message, "点我消失")
                    // 设置外层是否能被触摸
                    .setOutsideTouchable(false)
                    // 设置窗口背景阴影强度
                    .setBackgroundDimAmount(0.5f)
                    .setOnClickListener(android.R.id.message, new OnViewClickListener<TextView>() {

                        @Override
                        public void onClick(@NonNull EasyWindow<?> easyWindow, @NonNull TextView view) {
                            easyWindow.cancel();
                        }
                    })
                    .show();

        } else if (viewId == R.id.btn_main_lifecycle) {

            EasyWindow.with(this)
                    .setDuration(3000)
                    .setContentView(R.layout.window_hint)
                    .setAnimStyle(R.style.IOSAnimStyle)
                    .setImageDrawable(android.R.id.icon, R.drawable.ic_dialog_tip_warning)
                    .setText(android.R.id.message, "请注意下方 Snackbar")
                    .setOnWindowLifecycle(new OnWindowLifecycle() {

                        @Override
                        public void onWindowShow(@NonNull EasyWindow<?> easyWindow) {
                            Snackbar.make(getWindow().getDecorView(), "显示回调", Snackbar.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onWindowCancel(@NonNull EasyWindow<?> easyWindow) {
                            Snackbar.make(getWindow().getDecorView(), "消失回调", Snackbar.LENGTH_SHORT).show();
                        }
                    })
                    .show();

        } else if (viewId == R.id.btn_main_click) {

            EasyWindow.with(this)
                    .setContentView(R.layout.window_hint)
                    .setAnimStyle(R.style.IOSAnimStyle)
                    .setImageDrawable(android.R.id.icon, R.drawable.ic_dialog_tip_finish)
                    .setText(android.R.id.message, "点我点我点我")
                    .setOnClickListener(android.R.id.message, new OnViewClickListener<TextView>() {

                        @Override
                        public void onClick(final @NonNull EasyWindow<?> easyWindow, @NonNull TextView view) {
                            view.setText("不错，很听话");
                            easyWindow.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    easyWindow.cancel();
                                }
                            }, 1000);
                        }
                    })
                    .show();

        } else if (viewId == R.id.btn_main_view) {

            EasyWindow.with(this)
                    .setContentView(R.layout.window_hint)
                    .setAnimStyle(R.style.RightAnimStyle)
                    .setImageDrawable(android.R.id.icon, R.drawable.ic_dialog_tip_finish)
                    .setDuration(2000)
                    .setText(android.R.id.message, "位置算得准不准")
                    .setOnClickListener(android.R.id.message, new OnViewClickListener<TextView>() {

                        @Override
                        public void onClick(final @NonNull EasyWindow<?> easyWindow, @NonNull TextView view) {
                            easyWindow.cancel();
                        }
                    })
                    .showAsDropDown(v, Gravity.BOTTOM);

        } else if (viewId == R.id.btn_main_input) {

            EasyWindow.with(this)
                    .setContentView(R.layout.window_input)
                    .setAnimStyle(R.style.BottomAnimStyle)
                    .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
                    .setOnClickListener(R.id.tv_window_close, new OnViewClickListener<TextView>() {

                        @Override
                        public void onClick(final @NonNull EasyWindow<?> easyWindow, @NonNull TextView view) {
                            easyWindow.cancel();
                        }
                    })
                    .show();

        } else if (viewId == R.id.btn_main_web) {

            EasyWindow.with(this)
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
                .setContentView(R.layout.window_web, new OnWindowLayoutInflateListener() {

                    @Override
                    public void onWindowLayoutInflateFinished(@NonNull EasyWindow<?> easyWindow, @Nullable View view, int layoutId, @NonNull ViewGroup parentView) {
                        WebView webView = view.findViewById(R.id.wv_window_web_content);
                        WebSettings settings = webView.getSettings();
                        // 允许文件访问
                        settings.setAllowFileAccess(true);
                        // 允许网页定位
                        settings.setGeolocationEnabled(true);
                        // 允许保存密码
                        //settings.setSavePassword(true);
                        // 开启 JavaScript
                        settings.setJavaScriptEnabled(true);
                        // 允许网页弹对话框
                        settings.setJavaScriptCanOpenWindowsAutomatically(true);
                        // 加快网页加载完成的速度，等页面完成再加载图片
                        settings.setLoadsImagesAutomatically(true);
                        // 本地 DOM 存储（解决加载某些网页出现白板现象）
                        settings.setDomStorageEnabled(true);
                        // 解决 Android 5.0 上 WebView 默认不允许加载 Http 与 Https 混合内容
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
                        }

                        webView.setWebViewClient(new WebViewClient() {

                            @Override
                            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                view.loadUrl(url);
                                return true;
                            }
                        });

                        webView.loadUrl("https://github.com/getActivity/EasyWindow");
                    }
                })
                .setAnimStyle(R.style.IOSAnimStyle)
                // 设置成可拖拽的
                .setDraggable(new MovingDraggable())
                .setOnClickListener(R.id.iv_window_web_close, new OnViewClickListener<ImageView>() {

                    @Override
                    public void onClick(@NonNull EasyWindow<?> easyWindow, @NonNull ImageView view) {
                        easyWindow.cancel();
                    }
                })
                .setOnLongClickListener(R.id.iv_window_web_close, new OnViewLongClickListener<View>() {
                    @Override
                    public boolean onLongClick(@NonNull EasyWindow<?> easyWindow, @NonNull View view) {
                        Toaster.show("关闭按钮被长按了");
                        return false;
                    }
                })
                .show();

        } else if (viewId == R.id.btn_main_list) {

            EasyWindow.with(this)
                .setContentView(R.layout.window_list, new OnWindowLayoutInflateListener() {
                    @Override
                    public void onWindowLayoutInflateFinished(@NonNull EasyWindow<?> easyWindow, @Nullable View view, int layoutId, @NonNull ViewGroup parentView) {
                        RecyclerView recyclerView = view.findViewById(R.id.rv_window_list_view);
                        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

                        List<String> dataList = new ArrayList<>();
                        for (int i = 1; i <= 20; i++) {
                            dataList.add("我是条目 " + i);
                        }

                        DemoAdapter adapter = new DemoAdapter(dataList);
                        adapter.setOnItemClickListener(new OnItemClickListener() {

                            @Override
                            public void onItemClick(View itemView, int position) {
                                Toaster.show("条目 " + (position +  1) + " 被点击了");
                            }
                        });
                        adapter.setOnItemLongClickListener(new OnItemLongClickListener() {
                            @Override
                            public boolean onItemLongClick(View itemView, int position) {
                                Toaster.show("条目 " + (position +  1) + " 被长按了");
                                return false;
                            }
                        });
                        recyclerView.setAdapter(adapter);
                    }
                })
                .setAnimStyle(R.style.IOSAnimStyle)
                // 设置成可拖拽的
                .setDraggable(new MovingDraggable())
                .setOnClickListener(R.id.iv_window_list_close, new OnViewClickListener<ImageView>() {

                    @Override
                    public void onClick(@NonNull EasyWindow<?> easyWindow, @NonNull ImageView view) {
                        easyWindow.cancel();
                    }
                })
                .setOnLongClickListener(R.id.iv_window_list_close, new OnViewLongClickListener<View>() {
                    @Override
                    public boolean onLongClick(@NonNull EasyWindow<?> easyWindow, @NonNull View view) {
                        Toaster.show("关闭按钮被长按了");
                        return false;
                    }
                })
                .show();

        } else if (viewId == R.id.btn_main_draggable) {

            EasyWindow.with(this)
                    .setContentView(R.layout.window_hint)
                    .setAnimStyle(R.style.IOSAnimStyle)
                    .setImageDrawable(android.R.id.icon, R.drawable.ic_dialog_tip_finish)
                    .setText(android.R.id.message, "点我消失")
                    // 设置成可拖拽的
                    .setDraggable(new MovingDraggable())
                    .setOnClickListener(android.R.id.message, new OnViewClickListener<TextView>() {

                        @Override
                        public void onClick(@NonNull EasyWindow<?> easyWindow, @NonNull TextView view) {
                            easyWindow.cancel();
                        }
                    })
                    .show();

        } else if (viewId == R.id.btn_main_global) {

            XXPermissions.with(MainActivity.this)
                    .permission(Permission.SYSTEM_ALERT_WINDOW)
                    .request(new OnPermissionCallback() {

                        @Override
                        public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                            // 这里最好要做一下延迟显示，因为在某些手机（华为鸿蒙 3.0）上面立即显示会导致显示效果有一些瑕疵
                            runOnUiThread(() -> showGlobalWindow(getApplication()));
                        }

                        @Override
                        public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {
                            EasyWindow.with(MainActivity.this)
                                    .setDuration(1000)
                                    .setContentView(R.layout.window_hint)
                                    .setImageDrawable(android.R.id.icon, R.drawable.ic_dialog_tip_error)
                                    .setText(android.R.id.message, "请先授予悬浮窗权限")
                                    .show();
                        }
                    });

        } else if (viewId == R.id.btn_main_cancel_all) {

            // 关闭当前正在显示的悬浮窗
            // EasyWindow.cancelAll();
            // 回收当前正在显示的悬浮窗
            EasyWindow.recycleAllWindow();

        } else if (viewId == R.id.btn_main_utils) {

            EasyWindow.with(this)
                    .setDuration(1000)
                    // 将 Toaster 中的 View 转移给 EasyWindow 来显示
                    .setContentView(Toaster.getStyle().createView(this))
                    .setAnimStyle(R.style.ScaleAnimStyle)
                    .setText(android.R.id.message, "就问你溜不溜")
                    .setGravity(Gravity.BOTTOM)
                    .setYOffset(100)
                    .show();
        }
    }

    /**
     * 显示全局弹窗
     * @noinspection unchecked
     */
    public static void showGlobalWindow(Application application) {
        SpringBackDraggable springBackDraggable = new SpringBackDraggable(SpringBackDraggable.ORIENTATION_HORIZONTAL);
        springBackDraggable.setAllowMoveToScreenNotch(false);
        springBackDraggable.setSpringBackAnimCallback(new SpringBackAnimCallback() {

            @Override
            public void onSpringBackAnimationStart(@NonNull EasyWindow<?> easyWindow, @NonNull Animator animator) {
                Log.i(TAG, "onSpringBackAnimationStart");
            }

            @Override
            public void onSpringBackAnimationEnd(@NonNull EasyWindow<?> easyWindow, @NonNull Animator animator) {
                Log.i(TAG, "onSpringBackAnimationEnd");
            }
        });
        springBackDraggable.setDraggingCallback(new DraggingCallback() {

            @Override
            public void onStartDragging(@NonNull EasyWindow<?> easyWindow) {
                Log.i(TAG, "onStartDragging");
            }

            @Override
            public void onExecuteDragging(@NonNull EasyWindow<?> easyWindow) {
                Log.i(TAG, "onExecuteDragging");
            }

            @Override
            public void onStopDragging(@NonNull EasyWindow<?> easyWindow) {
                Log.i(TAG, "onStopDragging");
            }
        });
        // 传入 Application 表示这个是一个全局的 Toast
        EasyWindow.with(application)
                .setContentView(R.layout.window_phone)
                .setGravity(Gravity.END | Gravity.BOTTOM)
                .setYOffset(200)
                // 设置指定的拖拽规则
                .setDraggable(springBackDraggable)
                .setOnClickListener(android.R.id.icon, new OnViewClickListener<ImageView>() {

                    @Override
                    public void onClick(@NonNull EasyWindow<?> easyWindow, @NonNull ImageView view) {
                        Toaster.show("我被点击了");
                        // 点击后跳转到拨打电话界面
                        // Intent intent = new Intent(Intent.ACTION_DIAL);
                        // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        // toast.startActivity(intent);
                        // 安卓 10 在后台跳转 Activity 需要额外适配
                        // https://developer.android.google.cn/about/versions/10/privacy/changes#background-activity-starts
                    }
                })
                .setOnLongClickListener(android.R.id.icon, new OnViewLongClickListener<ImageView>() {
                    @Override
                    public boolean onLongClick(@NonNull EasyWindow<?> easyWindow, @NonNull ImageView view) {
                        Toaster.show("我被长按了");
                        return false;
                    }
                })
                .show();
    }
}