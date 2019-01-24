package com.hjq.xtoast;

import android.annotation.IdRes;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.hjq.xtoast.draggable.MovingDraggable;

import java.util.Timer;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/ToastUtils
 *    time   : 2019/01/04
 *    desc   : 超级 Toast（能做 Toast 做不到的事，应付项目中的特殊需求）
 */
public class XToast<X extends XToast> {

    // 当前是否已经显示
    private boolean isShow;

    // 自定义拖动处理
    private AbsDraggable mDraggable;

    // 显示时长
    private int mDuration;

    // 吐司显示和取消监听
    private OnToastLifecycle mListener;

    private WindowManager mWindowManager;

    private WindowManager.LayoutParams mWindowParams;

    private View mRootView;

    private Context mContext;

    public XToast(Activity activity) {
        this((Context) activity);

        // 跟随 Activity 的生命周期
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            new ToastLifecycle(activity, this);
        }
    }

    public XToast(Application application) {
        this((Context) application);

        // 设置成全局的悬浮窗
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mWindowParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            mWindowParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
    }

    private XToast(Context context) {
        mContext = context;
        mWindowManager = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE));

        mWindowParams = new WindowManager.LayoutParams();
        // 配置一些默认的参数
        mWindowParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        mWindowParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        mWindowParams.format = PixelFormat.TRANSLUCENT;
        mWindowParams.windowAnimations = android.R.style.Animation_Toast;
        mWindowParams.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        mWindowParams.packageName = context.getPackageName();
        mWindowParams.gravity = Gravity.CENTER;
    }


    public WindowManager getWindowManager() {
        return mWindowManager;
    }

    /**
     * 设置标志位
     */
    public X setFlags(int flags) {
        mWindowParams.flags = flags;
        return (X) this;
    }

    /**
     * 设置显示的类型
     */
    public X setType(int type) {
        mWindowParams.type = type;
        return (X) this;
    }

    /**
     * 设置动画样式
     */
    public X setAnimStyle(int resId) {
        mWindowParams.windowAnimations = resId;
        return (X) this;
    }

    /**
     * 设置可以自由拖动
     */
    public X setDraggable() {
        return setDraggable(new MovingDraggable());
    }

    public X setDraggable(AbsDraggable draggable) {
        // WindowManager 几个焦点总结：https://blog.csdn.net/zjx2014430/article/details/51776128
        // 设置触摸范围为当前的 RootView，而不是整个WindowManager
        mWindowParams.flags |= WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mDraggable = draggable;
        return (X) this;
    }

    /**
     * 设置宽度
     */
    public X setWidth(int width) {
        mWindowParams.width = width;
        return (X) this;
    }

    /**
     * 设置高度
     */
    public X setHeight(int height) {
        mWindowParams.height = height;
        return (X) this;
    }

    /**
     * 限定显示时长
     */
    public X setDuration(int duration) {
        mDuration = duration;
        return (X) this;
    }

    /**
     * 设置监听
     */
    public X setOnToastLifecycle(OnToastLifecycle l) {
        mListener = l;
        return (X) this;
    }

    /**
     * 设置重心
     */
    public X setGravity(int gravity) {
        mWindowParams.gravity = gravity;
        return (X) this;
    }

    /**
     * 设置 X 轴偏移量
     */
    public X setXOffset(int x) {
        mWindowParams.x = x;
        return (X) this;
    }

    /**
     * 设置 Y 轴偏移量
     */
    public X setYOffset(int y) {
        mWindowParams.y = y;
        return (X) this;
    }

    /**
     * 设置 WindowManager 参数集
     */
    public X setWindowParams(WindowManager.LayoutParams params) {
        mWindowParams = params;
        return (X) this;
    }

    /**
     * 当前是否已经显示
     */
    public boolean isShow() {
        return isShow;
    }

    /**
     * 显示
     */
    public void show() {
        if (mRootView == null || mWindowParams == null) {
            throw new IllegalArgumentException("WindowParams and view cannot be empty");
        }

        // 如果当前已经显示取消上一次显示
        if (isShow) {
            cancel();
        }
        try {
            // 如果这个 View 对象被重复添加到 WindowManager 则会抛出异常
            // java.lang.IllegalStateException: View android.widget.TextView{3d2cee7 V.ED..... ......ID 0,0-312,153} has already been added to the window manager.
            mWindowManager.addView(mRootView, mWindowParams);
            // 当前已经显示
            isShow = true;
            // 如果当前限定了显示时长
            if (mDuration != 0) {
                new Timer().schedule(new ToastDismissTask(this), mDuration);
            }
            // 如果当前设置了拖拽
            if (mDraggable != null) {
                mDraggable.start(this);
            }

            // 回调监听
            if (mListener != null) {
                mListener.onShow(this);
            }
        } catch (NullPointerException | IllegalStateException | WindowManager.BadTokenException ignored) {}
    }

    /**
     * 取消
     */
    public void cancel() {
        if (isShow) {
            try {
                // 如果当前 WindowManager 没有附加这个 View 则会抛出异常
                // java.lang.IllegalArgumentException: View=android.widget.TextView{3d2cee7 V.ED..... ........ 0,0-312,153} not attached to window manager
                mWindowManager.removeView(mRootView);
                // 回调监听
                if (mListener != null) {
                    mListener.onDismiss(this);
                }
            } catch (NullPointerException | IllegalArgumentException ignored) {}
            // 当前没有显示
            isShow = false;
        }
    }

    /**
     * 获取 WindowManager 参数集
     */
    public WindowManager.LayoutParams getWindowParams() {
        return mWindowParams;
    }

    /**
     * 设置布局
     */
    public X setView(int layoutId) {
        return setView(LayoutInflater.from(mContext).inflate(layoutId, null));
    }

    public X setView(View view) {
        cancel();
        mRootView = view;
        return (X) this;
    }

    /**
     * 获取布局
     */
    public View getView() {
        return mRootView;
    }

    /**
     * 根据 ViewId 获取 View
     */
    public <V extends View> V findViewById(int id) {
        if (mRootView == null) {
            throw new IllegalStateException("Please setup view");
        }
        return (V) mRootView.findViewById(id);
    }

    /**
     * 设置可见状态
     */
    public X setVisibility(@IdRes int id, @View.Visibility int visibility) {
        findViewById(id).setVisibility(visibility);
        return (X) this;
    }

    /**
     * 设置文本
     */
    public X setText(@IdRes int id, int resId) {
        return setText(id, mContext.getResources().getString(resId));
    }

    public X setText(@IdRes int id, CharSequence text) {
        ((TextView) findViewById(id)).setText(text);
        return (X) this;
    }

    /**
     * 设置背景
     */
    public X setBackground(@IdRes int id, int resId) {
        return setBackground(id, mContext.getResources().getDrawable(resId));
    }

    public X setBackground(@IdRes int id, Drawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            findViewById(id).setBackground(drawable);
        } else {
            findViewById(id).setBackgroundDrawable(drawable);
        }
        return (X) this;
    }

    /**
     * 设置图片
     */
    public X setImageDrawable(@IdRes int id, int resId) {
        return setBackground(id, mContext.getResources().getDrawable(resId));
    }

    public X setImageDrawable(@IdRes int id, Drawable drawable) {
        ((ImageView) findViewById(id)).setImageDrawable(drawable);
        return (X) this;
    }

    /**
     * 设置点击事件
     */
    public X setOnClickListener(@IdRes int id, OnClickListener l) {
        new ViewClickHandler(this, findViewById(id), l);
        // 当前是否设置了不可触摸，如果是就移除掉
        if ((mWindowParams.flags & WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE) != 0) {
            mWindowParams.flags &= ~WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        }
        return (X) this;
    }
}