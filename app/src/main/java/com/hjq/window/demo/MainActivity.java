package com.hjq.window.demo;

import android.animation.Animator;
import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
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
import com.hjq.permissions.XXPermissions;
import com.hjq.permissions.permission.PermissionLists;
import com.hjq.permissions.permission.base.IPermission;
import com.hjq.toast.Toaster;
import com.hjq.window.EasyWindow;
import com.hjq.window.EasyWindowManager;
import com.hjq.window.OnWindowLayoutInflateListener;
import com.hjq.window.OnWindowLifecycleCallback;
import com.hjq.window.OnWindowViewClickListener;
import com.hjq.window.OnWindowViewKeyListener;
import com.hjq.window.OnWindowViewLongClickListener;
import com.hjq.window.demo.DemoAdapter.OnItemClickListener;
import com.hjq.window.demo.DemoAdapter.OnItemLongClickListener;
import com.hjq.window.draggable.AbstractWindowDraggableRule.OnWindowDraggingListener;
import com.hjq.window.draggable.MovingWindowDraggableRule;
import com.hjq.window.draggable.SpringBackWindowDraggableRule;
import com.hjq.window.draggable.SpringBackWindowDraggableRule.SpringBackAnimCallback;
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
        findViewById(R.id.btn_main_view).setOnClickListener(this);
        findViewById(R.id.btn_main_input).setOnClickListener(this);
        findViewById(R.id.btn_main_web).setOnClickListener(this);
        findViewById(R.id.btn_main_list).setOnClickListener(this);
        findViewById(R.id.btn_main_draggable).setOnClickListener(this);
        findViewById(R.id.btn_main_global).setOnClickListener(this);
        findViewById(R.id.btn_main_semi_stealth).setOnClickListener(this);
        findViewById(R.id.btn_main_toaster_example).setOnClickListener(this);
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
                    .setWindowDuration(1000)
                    .setContentView(R.layout.window_hint)
                    .setWindowAnim(R.style.TopAnimStyle)
                    .setImageDrawableByImageView(android.R.id.icon, R.drawable.ic_dialog_tip_finish)
                    .setTextByTextView(android.R.id.message, getString(R.string.demo_animation_cool))
                    .show();

        } else if (viewId == R.id.btn_main_duration) {

            EasyWindow.with(this)
                    .setWindowDuration(1000)
                    .setContentView(R.layout.window_hint)
                    .setWindowAnim(R.style.IOSAnimStyle)
                    .setImageDrawableByImageView(android.R.id.icon, R.drawable.ic_dialog_tip_error)
                    .setTextByTextView(android.R.id.message, getString(R.string.demo_auto_dismiss))
                    .show();

        } else if (viewId == R.id.btn_main_overlay) {

            EasyWindow.with(this)
                    .setContentView(R.layout.window_hint)
                    .setWindowAnim(R.style.IOSAnimStyle)
                    .setImageDrawableByImageView(android.R.id.icon, R.drawable.ic_dialog_tip_finish)
                    .setTextByTextView(android.R.id.message, getString(R.string.demo_click_to_dismiss))
                    // 设置外层是否能被触摸
                    .setOutsideTouchable(false)
                    // 设置窗口背景阴影强度
                    .setBackgroundDimAmount(0.5f)
                    .setOnClickListenerByView(android.R.id.message, new OnWindowViewClickListener<TextView>() {

                        @Override
                        public void onClick(@NonNull EasyWindow<?> easyWindow, @NonNull TextView view) {
                            easyWindow.cancel();
                        }
                    })
                    .show();

        } else if (viewId == R.id.btn_main_lifecycle) {

            EasyWindow.with(this)
                    .setWindowDuration(3000)
                    .setContentView(R.layout.window_hint)
                    .setWindowAnim(R.style.IOSAnimStyle)
                    .setImageDrawableByImageView(android.R.id.icon, R.drawable.ic_dialog_tip_warning)
                    .setTextByTextView(android.R.id.message, getString(R.string.demo_notice_snackbar))
                    .setOnWindowLifecycleCallback(new OnWindowLifecycleCallback() {

                        @Override
                        public void onWindowShow(@NonNull EasyWindow<?> easyWindow) {
                            Snackbar.make(getWindow().getDecorView(), getString(R.string.demo_callback_show), Snackbar.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onWindowCancel(@NonNull EasyWindow<?> easyWindow) {
                            Snackbar.make(getWindow().getDecorView(), getString(R.string.demo_dismiss_callback), Snackbar.LENGTH_SHORT).show();
                        }
                    })
                    .show();

        } else if (viewId == R.id.btn_main_view) {

            EasyWindow.with(this)
                    .setContentView(R.layout.window_hint)
                    .setWindowAnim(R.style.RightAnimStyle)
                    .setImageDrawableByImageView(android.R.id.icon, R.drawable.ic_dialog_tip_finish)
                    .setWindowDuration(2000)
                    .setTextByTextView(android.R.id.message, getString(R.string.demo_text_position_accuracy))
                    .setOnClickListenerByView(android.R.id.message, new OnWindowViewClickListener<TextView>() {

                        @Override
                        public void onClick(final @NonNull EasyWindow<?> easyWindow, @NonNull TextView view) {
                            easyWindow.cancel();
                        }
                    })
                    .showAsDropDown(v, Gravity.BOTTOM);

        } else if (viewId == R.id.btn_main_input) {

            EasyWindow.with(this)
                    .setContentView(R.layout.window_input)
                    .setWindowAnim(R.style.BottomAnimStyle)
                    .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
                    .setOnClickListenerByView(R.id.tv_window_close, new OnWindowViewClickListener<TextView>() {

                        @Override
                        public void onClick(final @NonNull EasyWindow<?> easyWindow, @NonNull TextView view) {
                            easyWindow.cancel();
                        }
                    })
                    .show();

        } else if (viewId == R.id.btn_main_web) {

            EasyWindow.with(this)
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED)
                .setWindowSizePercent(0.8f, 0.8f)
                .setContentView(R.layout.window_web, new OnWindowLayoutInflateListener() {

                    @Override
                    public void onWindowLayoutInflateFinished(@NonNull EasyWindow<?> easyWindow, @NonNull View view, int layoutId, @NonNull ViewGroup parentView) {
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

                        // 加载指定的网页链接
                        webView.loadUrl("https://github.com/getActivity/EasyWindow");
                    }
                })
                .setWindowAnim(R.style.IOSAnimStyle)
                // 设置成可拖拽的
                .setWindowDraggableRule(new MovingWindowDraggableRule())
                .setOnClickListenerByView(R.id.iv_window_web_close, new OnWindowViewClickListener<ImageView>() {

                    @Override
                    public void onClick(@NonNull EasyWindow<?> easyWindow, @NonNull ImageView view) {
                        easyWindow.cancel();
                    }
                })
                .setOnLongClickListenerByView(R.id.iv_window_web_close, new OnWindowViewLongClickListener<View>() {

                    @Override
                    public boolean onLongClick(@NonNull EasyWindow<?> easyWindow, @NonNull View view) {
                        Toaster.show(getString(R.string.demo_close_button_long_press));
                        return false;
                    }
                })
                .setOnKeyListenerByView(R.id.wv_window_web_content, new OnWindowViewKeyListener<WebView>() {

                    @Override
                    public boolean onKey(@NonNull EasyWindow<?> easyWindow, @NonNull WebView webView, @NonNull KeyEvent event, int keyCode) {
                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
                            // 判断网页是否能回退
                            if (webView.canGoBack()) {
                                // 如果可以，则返回到上一页
                                webView.goBack();
                            } else {
                                // 如果不行，则关闭窗口
                                easyWindow.cancel();
                            }
                            return true;
                        }
                        return false;
                    }
                })
                .show();

        } else if (viewId == R.id.btn_main_list) {

            EasyWindow.with(this)
                .setContentView(R.layout.window_list, new OnWindowLayoutInflateListener() {

                    @Override
                    public void onWindowLayoutInflateFinished(@NonNull EasyWindow<?> easyWindow, @NonNull View view, int layoutId, @NonNull ViewGroup parentView) {
                        RecyclerView recyclerView = view.findViewById(R.id.rv_window_list_view);
                        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

                        List<String> dataList = new ArrayList<>();
                        for (int i = 1; i <= 20; i++) {
                            dataList.add(getString(R.string.demo_item_prefix) + " " + i);
                        }

                        DemoAdapter adapter = new DemoAdapter(dataList);
                        adapter.setOnItemClickListener(new OnItemClickListener() {

                            @Override
                            public void onItemClick(View itemView, int position) {
                                Toaster.show(getString(R.string.demo_item_clicked, (position + 1)));
                            }
                        });
                        adapter.setOnItemLongClickListener(new OnItemLongClickListener() {
                            @Override
                            public boolean onItemLongClick(View itemView, int position) {
                                Toaster.show(getString(R.string.demo_item_long_pressed, (position + 1)));
                                return false;
                            }
                        });
                        recyclerView.setAdapter(adapter);
                    }
                })
                .setWindowAnim(R.style.IOSAnimStyle)
                // 设置成可拖拽的
                .setWindowDraggableRule(new MovingWindowDraggableRule())
                .setOnClickListenerByView(R.id.iv_window_list_close, new OnWindowViewClickListener<ImageView>() {

                    @Override
                    public void onClick(@NonNull EasyWindow<?> easyWindow, @NonNull ImageView view) {
                        easyWindow.cancel();
                    }
                })
                .setOnLongClickListenerByView(R.id.iv_window_list_close, new OnWindowViewLongClickListener<View>() {

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
                    .setWindowAnim(R.style.IOSAnimStyle)
                    .setImageDrawableByImageView(android.R.id.icon, R.drawable.ic_dialog_tip_finish)
                    .setTextByTextView(android.R.id.message, "点我消失")
                    // 设置成可拖拽的
                    .setWindowDraggableRule(new MovingWindowDraggableRule())
                    .setOnClickListenerByView(android.R.id.message, new OnWindowViewClickListener<TextView>() {

                        @Override
                        public void onClick(@NonNull EasyWindow<?> easyWindow, @NonNull TextView view) {
                            easyWindow.cancel();
                        }
                    })
                    .show();

        } else if (viewId == R.id.btn_main_global) {

            XXPermissions.with(this)
                .permission(PermissionLists.getSystemAlertWindowPermission())
                .request(new OnPermissionCallback() {

                    @Override
                    public void onGranted(@NonNull List<IPermission> permissions, boolean allGranted) {
                        showGlobalWindow(getApplication());
                    }

                    @Override
                    public void onDenied(@NonNull List<IPermission> permissions, boolean doNotAskAgain) {
                        Toaster.show("申请悬浮窗权限失败，无法显示全局的窗口");
                    }
                });

        } else if (viewId == R.id.btn_main_semi_stealth) {

            new SemiStealthWindow(this)
                .show();

        } else if (viewId == R.id.btn_main_cancel_all) {

            // 关闭当前正在显示的悬浮窗
            // EasyWindow.cancelAll();
            // 回收当前正在显示的悬浮窗
            EasyWindowManager.recycleAllWindow();

        } else if (viewId == R.id.btn_main_toaster_example) {

            EasyWindow.with(this)
                    // 将 Toaster 中的 View 转移给 EasyWindow 来显示
                    .setContentView(Toaster.getStyle().createView(this))
                    .setWindowLocation(Gravity.BOTTOM, 0, 100)
                    .setWindowDuration(1000)
                    .setWindowAnim(R.style.ScaleAnimStyle)
                    .setTextByTextView(android.R.id.message, "就问你溜不溜")
                    .show();
        }
    }

    /**
     * 显示全局弹窗
     * @noinspection unchecked
     */
    public static void showGlobalWindow(Application application) {
        SpringBackWindowDraggableRule springBackWindowDraggableRule = new SpringBackWindowDraggableRule(
            SpringBackWindowDraggableRule.ORIENTATION_HORIZONTAL);
        springBackWindowDraggableRule.setAllowMoveToScreenNotch(false);
        springBackWindowDraggableRule.setSpringBackAnimCallback(new SpringBackAnimCallback() {

            @Override
            public void onSpringBackAnimationStart(@NonNull EasyWindow<?> easyWindow, @NonNull Animator animator) {
                Log.i(TAG, "onSpringBackAnimationStart");
            }

            @Override
            public void onSpringBackAnimationEnd(@NonNull EasyWindow<?> easyWindow, @NonNull Animator animator) {
                Log.i(TAG, "onSpringBackAnimationEnd");
            }
        });
        springBackWindowDraggableRule.setWindowDraggingListener(new OnWindowDraggingListener() {

            @Override
            public void onWindowDraggingStart(@NonNull EasyWindow<?> easyWindow) {
                Log.i(TAG, "onWindowDraggingStart");
            }

            @Override
            public void onWindowDraggingRunning(@NonNull EasyWindow<?> easyWindow) {
                Log.i(TAG, "onWindowDraggingRunning");
            }

            @Override
            public void onWindowDraggingStop(@NonNull EasyWindow<?> easyWindow) {
                Log.i(TAG, "onWindowDraggingStop");
            }
        });
        // 传入 Application 表示这个是一个全局的 Toast
        EasyWindow.with(application)
                .setContentView(R.layout.window_phone)
                .setWindowLocationPercent(Gravity.END | Gravity.BOTTOM, 0, 0.2f)
                // 设置指定的拖拽规则
                .setWindowDraggableRule(springBackWindowDraggableRule)
                .setOnClickListenerByView(android.R.id.icon, new OnWindowViewClickListener<ImageView>() {

                    @Override
                    public void onClick(@NonNull EasyWindow<?> easyWindow, @NonNull ImageView view) {
                        Toaster.show(R.string.demo_toast_click);
                        // 点击后跳转到拨打电话界面
                        // Intent intent = new Intent(Intent.ACTION_DIAL);
                        // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        // toast.startActivity(intent);
                        // 安卓 10 在后台跳转 Activity 需要额外适配
                        // https://developer.android.google.cn/about/versions/10/privacy/changes#background-activity-starts
                    }
                })
                .setOnLongClickListenerByView(android.R.id.icon, new OnWindowViewLongClickListener<ImageView>() {

                    @Override
                    public boolean onLongClick(@NonNull EasyWindow<?> easyWindow, @NonNull ImageView view) {
                        Toaster.show(R.string.demo_toast_long_press);
                        // 这里返回 true 就不会触发点击事件
                        return true;
                    }
                })
                .show();
    }
}