# [English Doc](README-en.md)

# 悬浮窗框架

* 项目地址：[Github](https://github.com/getActivity/EasyWindow)

* 博客地址：[悬浮窗需求终结者](https://www.jianshu.com/p/247d705b87b6)

* 可以扫码下载 Demo 进行演示或者测试，如果扫码下载不了的，[点击此处可直接下载](https://github.com/getActivity/EasyWindow/releases/download/13.0/EasyWindow.apk)

![](picture/zh/demo_code.png)

![](picture/zh/dynamic_figure.gif)

#### 本框架意在解决一些悬浮窗的需求，如果是普通的 Toast 封装推荐使用 [Toaster](https://github.com/getActivity/Toaster)

#### 集成步骤

* 如果你的项目 Gradle 配置是在 `7.0 以下`，需要在 `build.gradle` 文件中加入

```groovy
allprojects {
    repositories {
        // JitPack 远程仓库：https://jitpack.io
        maven { url 'https://jitpack.io' }
    }
}
```

* 如果你的 Gradle 配置是 `7.0 及以上`，则需要在 `settings.gradle` 文件中加入

```groovy
dependencyResolutionManagement {
    repositories {
        // JitPack 远程仓库：https://jitpack.io
        maven { url 'https://jitpack.io' }
    }
}
```

* 配置完远程仓库后，在项目 app 模块下的 `build.gradle` 文件中加入远程依赖

```groovy
android {
    // 支持 JDK 1.8
    compileOptions {
        targetCompatibility JavaVersion.VERSION_1_8
        sourceCompatibility JavaVersion.VERSION_1_8
    }
}

dependencies {
    // 悬浮窗框架：https://github.com/getActivity/EasyWindow
    implementation 'com.github.getActivity:EasyWindow:13.0'
}
```

#### AndroidX 兼容

* 如果项目是基于 **AndroidX** 库，请在项目 `gradle.properties` 文件中加入以下配置，如果已经加入则可以忽略

```text
# 表示使用 AndroidX
android.useAndroidX = true
# 表示将第三方库迁移到 AndroidX
android.enableJetifier = true
```

* 如果项目是基于 **Support** 库则不需要加入此配置

#### 使用案例

* Java 用法

```java
// 传入 Activity 对象表示设置成局部的，不需要有悬浮窗权限
// 传入 Application 对象表示设置成全局的，但需要有悬浮窗权限
EasyWindow.with(this)
        .setContentView(R.layout.toast_hint)
        // 设置成可拖拽的
        //.setWindowDraggableRule()
        // 设置显示时长
        .setWindowDuration(1000)
        // 设置动画样式
        //.setWindowAnim(android.R.style.Animation_Translucent)
        // 设置外层是否能被触摸
        //.setOutsideTouchable(false)
        // 设置窗口背景阴影强度
        //.setBackgroundDimAmount(0.5f)
        .setImageDrawableByImageView(android.R.id.icon, R.mipmap.ic_dialog_tip_finish)
        .setTextByTextView(android.R.id.message, "点我消失")
        .setOnClickListenerByView(android.R.id.message, new OnWindowViewClickListener<TextView>() {

            @Override
            public void onClick(@NonNull EasyWindow<?> easyWindow, @NonNull TextView view) {
                easyWindow.cancel();
                // 跳转到某个Activity
                // easyWindow.startActivity(intent);
            }
        })
        .show();
```

* Kotlin 用法（二选一）

```kotlin
EasyWindow.with(activity).apply {
    setContentView(R.layout.toast_hint)
    // 设置成可拖拽的
    //setWindowDraggableRule()
    // 设置显示时长
    setWindowDuration(1000)
    // 设置动画样式
    //setWindowAnim(android.R.style.Animation_Translucent)
    // 设置外层是否能被触摸
    //setOutsideTouchable(false)
    // 设置窗口背景阴影强度
    //setBackgroundDimAmount(0.5f)
    setImageDrawableByImageView(android.R.id.icon, R.mipmap.ic_dialog_tip_finish)
    setTextByTextView(android.R.id.message, "点我消失")
    setOnClickListenerByView(android.R.id.message, OnWindowViewClickListener<TextView?> { easyWindow: EasyWindow<*>, view: TextView ->
        easyWindow.cancel()
        // 跳转到某个Activity
        // easyWindow.startActivity(intent)
    })
}.show()
```

```kotlin
EasyWindow.with(activity)
        .setContentView(R.layout.toast_hint)
        // 设置成可拖拽的
        //.setWindowDraggableRule()
        // 设置显示时长
        .setWindowDuration(1000)
        // 设置动画样式
        //.setWindowAnim(android.R.style.Animation_Translucent)
        // 设置外层是否能被触摸
        //.setOutsideTouchable(false)
        // 设置窗口背景阴影强度
        //.setBackgroundDimAmount(0.5f)
        .setImageDrawableByImageView(android.R.id.icon, R.mipmap.ic_dialog_tip_finish)
        .setTextByTextView(android.R.id.message, "点我消失")
        .setOnClickListenerByView(android.R.id.message, OnWindowViewClickListener<TextView?> { easyWindow: EasyWindow<*>, view: TextView ->
            easyWindow.cancel()
            // 跳转到某个Activity
            // easyWindow.startActivity(intent)
        })
        .show()
```

#### 没有悬浮窗权限如何全局显示？

* 没有悬浮窗权限是不能全局显示在其他应用上的，但是全局显示在自己的应用上是可以实现的

* 但是当前 Activity 创建的悬浮窗只能在当前 Activity 上面显示，如果想在所有的 Activity 都显示需要做特殊处理

* 我们可以通过 Application 来监听所有 Activity 的生命周期方法，然后在每个 Activity.onCreate 时创建悬浮窗

```java
public final class WindowLifecycleControl implements Application.ActivityLifecycleCallbacks {

    static void with(Application application) {
        application.registerActivityLifecycleCallbacks(new FloatingLifecycle());
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        EasyWindow.with(activity)
                .setContentView(R.layout.xxx)
                .show();
    }

    ......
}
```

#### 框架的 API 介绍

* 对象方法

```java
// 显示悬浮窗
easyWindow.show();
// 延迟显示悬浮窗（可在子线程中调用，不怕频繁调用）
easyWindow.delayShow();
easyWindow.delayShow(long delayMillis);
// 将悬浮窗显示在指定 View 的旁边
easyWindow.showAsDropDown(@NonNull View anchorView, int showGravity, int xOff, int yOff);
// 取消显示悬浮窗
easyWindow.cancel();
// 延迟取消显示悬浮窗（可在子线程中调用，不怕频繁调用）
easyWindow.delayCancel();
easyWindow.delayCancel(long delayMillis);
// 取消显示并回收悬浮窗
easyWindow.recycle();
// 延迟回收悬浮窗（可在子线程中调用，不怕频繁调用）
easyWindow.delayRecycle();
easyWindow.delayRecycle(long delayMillis);
// 更新悬浮窗（在更新了悬浮窗参数才需要调用）
easyWindow.update();
// 延迟更新悬浮窗（可在子线程中调用，不怕频繁调用）
easyWindow.delayUpdate();
easyWindow.delayUpdate(long delayMillis);
// 当前悬浮窗是否正在显示
easyWindow.isShowing();

// 设置窗口生命周期回调监听
easyWindow.setOnWindowLifecycleCallback(@Nullable OnWindowLifecycleCallback callback);
// 设置悬浮窗拖拽规则（框架内部提供了两种拖拽规则，MovingWindowDraggableRule 和 SpringBackWindowDraggableRule ）
easyWindow.setWindowDraggableRule(@Nullable AbstractWindowDraggableRule draggableRule);
// 设置悬浮窗拖拽规则（可能为空）
easyWindow.getWindowDraggableRule();

// 设置悬浮窗内容布局
easyWindow.setContentView(@LayoutRes int layoutId);
easyWindow.setContentView(@LayoutRes int layoutId, @Nullable OnWindowLayoutInflateListener listener);
easyWindow.setContentView(@NonNull View view);
// 获取内容布局（可能为空）
easyWindow.getContentView();
// 限定悬浮窗显示时长
easyWindow.setWindowDuration(@IntRange(from = 0) int delayMillis);
// 设置悬浮窗 tag
easyWindow.setWindowTag(@Nullable String tag);
// 获取悬浮窗 tag
easyWindow.getWindowTag();
// 设置悬浮窗宽度和高度
easyWindow.setWindowSize(int width, int height);
// 设置悬浮窗大小（按照屏幕百分比）
easyWindow.setWindowSizePercent(@FloatRange(from = 0, to = 1) float widthPercent, @FloatRange(from = 0, to = 1) float heightPercent);

// 设置悬浮窗的位置
easyWindow.setWindowLocation(@Px int x, @Px int y);
easyWindow.setWindowLocation(@GravityFlag int gravity, @Px int x, @Px int y);
// 设置窗口位置（偏移量按照屏幕百分比）
easyWindow.setWindowLocationPercent(@FloatRange(from = 0, to = 1) @Px float horizontalPercent, @FloatRange(from = 0, to = 1) @Px float verticalPercent);
easyWindow.setWindowLocationPercent(@GravityFlag int gravity, @FloatRange(from = 0, to = 1) @Px float horizontalPercent, @FloatRange(from = 0, to = 1) @Px float verticalPercent);

// 设置悬浮窗外层是否可触摸
easyWindow.setOutsideTouchable(boolean touchable);
// 设置悬浮窗背景阴影强度
easyWindow.setBackgroundDimAmount(@FloatRange(from = 0.0, to = 1.0) float amount);

// 添加窗口标记
easyWindow.addWindowFlags(int flags);
// 移除窗口标记
easyWindow.removeWindowFlags(int flags);
// 设置窗口标记
easyWindow.setWindowFlags(int flags);
// 是否存在某个窗口标记
easyWindow.hasWindowFlags(int flags);
// 设置悬浮窗的显示类型
easyWindow.setWindowType(int type);

// 设置悬浮窗动画样式
easyWindow.setWindowAnim(int id);
// 设置悬浮窗软键盘模式
easyWindow.setSoftInputMode(int softInputMode);
// 设置悬浮窗 Token
easyWindow.setWindowToken(@Nullable IBinder token);

// 设置悬浮窗透明度
easyWindow.setWindowAlpha(@FloatRange(from = 0.0, to = 1.0) float alpha);
// 设置容器和窗口小部件之间的垂直边距
easyWindow.setVerticalMargin(float verticalMargin);
// 设置容器和窗口小部件之间的水平边距
easyWindow.setHorizontalMargin(float horizontalMargin);
// 设置位图格式
easyWindow.setBitmapFormat(int format);
// 设置状态栏的可见性
easyWindow.setSystemUiVisibility(int systemUiVisibility);
// 设置垂直权重
easyWindow.setVerticalWeight(float verticalWeight);
// 设置挖孔屏下的显示模式
easyWindow.setLayoutInDisplayCutoutMode(int layoutInDisplayCutoutMode);
// 设置悬浮窗在哪个显示屏上显示
easyWindow.setPreferredDisplayModeId(int preferredDisplayModeId);
// 设置悬浮窗标题
easyWindow.setWindowTitle(@Nullable CharSequence title);
// 设置屏幕的亮度
easyWindow.setScreenBrightness(@FloatRange(from = -1.0, to = 1.0) float screenBrightness);
// 设置键盘背光的亮度
easyWindow.setButtonBrightness(@FloatRange(from = -1.0, to = 1.0) float buttonBrightness);
// 设置悬浮窗的刷新率
easyWindow.setPreferredRefreshRate(float preferredRefreshRate);
// 设置悬浮窗的颜色模式
easyWindow.setColorMode(int colorMode);
// 设置悬浮窗背后的高斯模糊半径大小（Android 12 及以上才支持）
easyWindow.setBlurBehindRadius(@IntRange(from = 0) int blurBehindRadius);
// 设置悬浮窗屏幕方向
easyWindow.setScreenOrientation(int screenOrientation);

// 设置悬浮窗可见性
easyWindow.setWindowViewVisibility(int visibility);
// 获取悬浮窗可见性
easyWindow.getWindowViewVisibility();
// 设置悬浮窗根布局（一般情况下推荐使用 {@link #setContentView} 方法来填充布局）
easyWindow.setRootLayout(@NonNull ViewGroup viewGroup);
// 重新设置 WindowManager 参数集
easyWindow.setWindowParams(@NonNull WindowManager.LayoutParams params);
// 重新设置 WindowManager 对象
easyWindow.setWindowManager(@NonNull WindowManager windowManager);

// 获取当前窗口视图宽度
easyWindow.getWindowViewWidth();
// 获取当前窗口视图高度
easyWindow.getWindowViewHeight();

// 设置可见性状态给 View
easyWindow.setVisibilityByView(@IdRes int viewId, int visibility);
// 设置背景 Drawable 给 View
easyWindow.setBackgroundDrawableByView(@IdRes int viewId, @DrawableRes int drawableId);
easyWindow.setBackgroundDrawableByView(@IdRes int viewId, @Nullable Drawable drawable);
// 设置文本给 TextView
easyWindow.setTextByTextView(@IdRes int viewId, @StringRes int stringId);
easyWindow.setTextByTextView(@IdRes int viewId, @Nullable CharSequence text);
// 设置字体颜色给 TextView
easyWindow.setTextColorByTextView(@IdRes int viewId, @ColorInt int colorValue);
// 设置字体大小给 TextView
easyWindow.setTextSizeByTextView(@IdRes int viewId, float textSize);
easyWindow.setTextSizeByTextView(@IdRes int viewId, int unit, float textSize);
// 设置提示文本给 TextView
easyWindow.setHintTextByTextView(@IdRes int viewId, @StringRes int stringId);
easyWindow.setHintTextByTextView(@IdRes int viewId, @Nullable CharSequence text);
// 设置提示文本颜色给 TextView
easyWindow.setHintTextColorByTextView(@IdRes int viewId, @ColorInt int colorValue);
// 设置图片 Drawable 给 ImageView
easyWindow.setImageDrawableByImageView(@IdRes int viewId, @DrawableRes int drawableId);
easyWindow.setImageDrawableByImageView(@IdRes int viewId, @Nullable Drawable drawable);

// 设置点击监听（没有传入 View 的方法默认使用窗口根布局）
easyWindow.setOnClickListener(@Nullable OnWindowViewClickListener<? extends View> listener);
easyWindow.setOnClickListenerByView(@IdRes int id, @Nullable OnWindowViewClickListener<? extends View> listener);
// 设置长按监听（没有传入 View 的方法默认使用窗口根布局）
easyWindow.setOnLongClickListener(@Nullable OnWindowViewLongClickListener<? extends View> listener);
easyWindow.setOnLongClickListenerByView(@IdRes int id, @Nullable OnWindowViewLongClickListener<? extends View> listener);
// 设置触摸监听（没有传入 View 的方法默认使用窗口根布局）
easyWindow.setOnTouchListener(@Nullable OnWindowViewTouchListener<? extends View> listener);
easyWindow.setOnTouchListenerByView(@IdRes int id, @Nullable OnWindowViewTouchListener<? extends View> listener);
// 设置按键监听（没有传入 View 的方法默认使用窗口根布局）
easyWindow.setOnKeyListener(@Nullable OnWindowViewKeyListener<? extends View> listener);
easyWindow.setOnKeyListenerByView(@IdRes int id, @Nullable OnWindowViewKeyListener<? extends View> listener);

// 跳转 Activity
easyWindow.startActivity(@Nullable Class<? extends Activity> clazz);
easyWindow.startActivity(@Nullable Intent intent);

// 延迟执行任务
easyWindow.sendTask(Runnable runnable);
// 延迟一段时间执行任务
easyWindow.sendTask(@NonNull Runnable runnable, long delayMillis);
// 移除指定的任务
easyWindow.cancelTask(@NonNull Runnable runnable);
// 移除所有的任务
easyWindow.cancelAllTask();
```

* 静态方法

```java
// 取消所有正在显示的悬浮窗
EasyWindowManager.cancelAllWindow();
// 取消特定类名的悬浮窗
EasyWindowManager.cancelWindowByClass(@Nullable Class<? extends EasyWindow<?>> clazz);
// 取消特定标记的悬浮窗
EasyWindowManager.cancelWindowByTag(@Nullable String tag);

// 显示所有已取消但未回收的悬浮窗
EasyWindowManager.showAllWindow();
// 显示特定类名已取消但未回收的悬浮窗
EasyWindowManager.showWindowByClass(@Nullable Class<? extends EasyWindow<?>> clazz);
// 显示特定标记已取消但未回收的悬浮窗
EasyWindowManager.showWindowByTag(@Nullable String tag);

// 回收所有正在显示的悬浮窗
EasyWindowManager.recycleAllWindow();
// 回收特定类名的悬浮窗
EasyWindowManager.recycleWindowByClass(@Nullable Class<? extends EasyWindow<?>> clazz);
// 回收特定标记的悬浮窗
EasyWindowManager.recycleWindowByTag(@Nullable String tag);

// 判断当前是否有悬浮窗正在显示
EasyWindowManager.existAnyWindowShowing();
// 判断当前是否有特定类名的悬浮窗正在显示
EasyWindowManager.existWindowShowingByClass(@Nullable Class<? extends EasyWindow<?>> clazz);
// 判断当前是否有特定标记的悬浮窗正在显示
EasyWindowManager.existWindowShowingByTag(@Nullable String tag);

// 寻找特定类名的悬浮窗
EasyWindowManager.findWindowInstanceByClass(@Nullable Class<? extends EasyWindow<?>> clazz);
// 寻找特定标记的悬浮窗
EasyWindowManager.findWindowInstanceByTag(@Nullable String tag);

// 获取所有的悬浮窗
EasyWindowManager.getAllWindowInstance();
```

#### 作者的其他开源项目

* 安卓技术中台：[AndroidProject](https://github.com/getActivity/AndroidProject) ![](https://img.shields.io/github/stars/getActivity/AndroidProject.svg) ![](https://img.shields.io/github/forks/getActivity/AndroidProject.svg)

* 安卓技术中台 Kt 版：[AndroidProject-Kotlin](https://github.com/getActivity/AndroidProject-Kotlin) ![](https://img.shields.io/github/stars/getActivity/AndroidProject-Kotlin.svg) ![](https://img.shields.io/github/forks/getActivity/AndroidProject-Kotlin.svg)

* 权限框架：[XXPermissions](https://github.com/getActivity/XXPermissions) ![](https://img.shields.io/github/stars/getActivity/XXPermissions.svg) ![](https://img.shields.io/github/forks/getActivity/XXPermissions.svg)

* 吐司框架：[Toaster](https://github.com/getActivity/Toaster) ![](https://img.shields.io/github/stars/getActivity/Toaster.svg) ![](https://img.shields.io/github/forks/getActivity/Toaster.svg)

* 网络框架：[EasyHttp](https://github.com/getActivity/EasyHttp) ![](https://img.shields.io/github/stars/getActivity/EasyHttp.svg) ![](https://img.shields.io/github/forks/getActivity/EasyHttp.svg)

* 标题栏框架：[TitleBar](https://github.com/getActivity/TitleBar) ![](https://img.shields.io/github/stars/getActivity/TitleBar.svg) ![](https://img.shields.io/github/forks/getActivity/TitleBar.svg)

* 设备兼容框架：[DeviceCompat](https://github.com/getActivity/DeviceCompat) ![](https://img.shields.io/github/stars/getActivity/DeviceCompat.svg) ![](https://img.shields.io/github/forks/getActivity/DeviceCompat.svg)

* ShapeView 框架：[ShapeView](https://github.com/getActivity/ShapeView) ![](https://img.shields.io/github/stars/getActivity/ShapeView.svg) ![](https://img.shields.io/github/forks/getActivity/ShapeView.svg)

* ShapeDrawable 框架：[ShapeDrawable](https://github.com/getActivity/ShapeDrawable) ![](https://img.shields.io/github/stars/getActivity/ShapeDrawable.svg) ![](https://img.shields.io/github/forks/getActivity/ShapeDrawable.svg)

* 语种切换框架：[MultiLanguages](https://github.com/getActivity/MultiLanguages) ![](https://img.shields.io/github/stars/getActivity/MultiLanguages.svg) ![](https://img.shields.io/github/forks/getActivity/MultiLanguages.svg)

* Gson 解析容错：[GsonFactory](https://github.com/getActivity/GsonFactory) ![](https://img.shields.io/github/stars/getActivity/GsonFactory.svg) ![](https://img.shields.io/github/forks/getActivity/GsonFactory.svg)

* 日志查看框架：[Logcat](https://github.com/getActivity/Logcat) ![](https://img.shields.io/github/stars/getActivity/Logcat.svg) ![](https://img.shields.io/github/forks/getActivity/Logcat.svg)

* 嵌套滚动布局框架：[NestedScrollLayout](https://github.com/getActivity/NestedScrollLayout) ![](https://img.shields.io/github/stars/getActivity/NestedScrollLayout.svg) ![](https://img.shields.io/github/forks/getActivity/NestedScrollLayout.svg)

* Android 版本适配：[AndroidVersionAdapter](https://github.com/getActivity/AndroidVersionAdapter) ![](https://img.shields.io/github/stars/getActivity/AndroidVersionAdapter.svg) ![](https://img.shields.io/github/forks/getActivity/AndroidVersionAdapter.svg)

* Android 代码规范：[AndroidCodeStandard](https://github.com/getActivity/AndroidCodeStandard) ![](https://img.shields.io/github/stars/getActivity/AndroidCodeStandard.svg) ![](https://img.shields.io/github/forks/getActivity/AndroidCodeStandard.svg)

* Android 资源大汇总：[AndroidIndex](https://github.com/getActivity/AndroidIndex) ![](https://img.shields.io/github/stars/getActivity/AndroidIndex.svg) ![](https://img.shields.io/github/forks/getActivity/AndroidIndex.svg)

* Android 开源排行榜：[AndroidGithubBoss](https://github.com/getActivity/AndroidGithubBoss) ![](https://img.shields.io/github/stars/getActivity/AndroidGithubBoss.svg) ![](https://img.shields.io/github/forks/getActivity/AndroidGithubBoss.svg)

* Studio 精品插件：[StudioPlugins](https://github.com/getActivity/StudioPlugins) ![](https://img.shields.io/github/stars/getActivity/StudioPlugins.svg) ![](https://img.shields.io/github/forks/getActivity/StudioPlugins.svg)

* 表情包大集合：[EmojiPackage](https://github.com/getActivity/EmojiPackage) ![](https://img.shields.io/github/stars/getActivity/EmojiPackage.svg) ![](https://img.shields.io/github/forks/getActivity/EmojiPackage.svg)

* AI 资源大汇总：[AiIndex](https://github.com/getActivity/AiIndex) ![](https://img.shields.io/github/stars/getActivity/AiIndex.svg) ![](https://img.shields.io/github/forks/getActivity/AiIndex.svg)

* 省市区 Json 数据：[ProvinceJson](https://github.com/getActivity/ProvinceJson) ![](https://img.shields.io/github/stars/getActivity/ProvinceJson.svg) ![](https://img.shields.io/github/forks/getActivity/ProvinceJson.svg)

* Markdown 语法文档：[MarkdownDoc](https://github.com/getActivity/MarkdownDoc) ![](https://img.shields.io/github/stars/getActivity/MarkdownDoc.svg) ![](https://img.shields.io/github/forks/getActivity/MarkdownDoc.svg)

#### 微信公众号：Android轮子哥

![](https://raw.githubusercontent.com/getActivity/Donate/master/picture/official_ccount.png)

#### Android 技术 Q 群：10047167

#### 如果您觉得我的开源库帮你节省了大量的开发时间，请扫描下方的二维码随意打赏，要是能打赏个 10.24 :monkey_face:就太:thumbsup:了。您的支持将鼓励我继续创作:octocat:（[点击查看捐赠列表](https://github.com/getActivity/Donate)）

![](https://raw.githubusercontent.com/getActivity/Donate/master/picture/pay_ali.png) ![](https://raw.githubusercontent.com/getActivity/Donate/master/picture/pay_wechat.png)

## License

```text
Copyright 2019 Huang JinQun

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```