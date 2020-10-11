# 超级 Toast

> 码云地址：[Gitee](https://gitee.com/getActivity/XToast)

> 博客地址：[悬浮窗需求终结者](https://www.jianshu.com/p/247d705b87b6)

> 已投入公司项目多时，没有任何毛病，可胜任任何需求，[点击此处下载Demo](XToast.apk)

![](XToast.gif)

#### 本框架意在解决一些极端需求，如果是普通的 Toast 封装推荐使用 [ToastUtils](https://github.com/getActivity/ToastUtils)

#### 集成步骤

```groovy
dependencies {
    implementation 'com.hjq:xtoast:6.0'
}
```

#### 使用案例

```java
// 传入 Application 对象表示设置成全局的
new XToast(XToastActivity.this)
        .setView(R.layout.toast_hint)
        // 设置成可拖拽的
        //.setDraggable()
        // 设置显示时长
        .setDuration(1000)
        // 设置动画样式
        .setAnimStyle(android.R.style.Animation_Translucent)
        // 设置外层不能被触摸
        .setOutsideTouchable(false)
        // 设置窗口背景阴影强度
        .setBackgroundDimAmount(0.5f)
        .setImageDrawable(android.R.id.icon, R.mipmap.ic_dialog_tip_finish)
        .setText(android.R.id.message, "点我消失")
        .setOnClickListener(android.R.id.message, new OnClickListener<TextView>() {

            @Override
            public void onClick(XToast toast, TextView view) {
                // 点击这个 View 后消失
                toast.cancel();
                // 跳转到某个Activity
                // toast.startActivity(intent);
            }
        })
        .show();
```

#### 框架亮点（原生 Toast 无法实现的功能）

* 支持自定义 Toast 动画样式

* 支持自定义 Toast 显示时长

* 支持监听 Toast 的显示和销毁

* 支持监听 Toast 中点击事件

* 支持一键开启 Toast 拖拽功能

* 支持 Toast 全局显示（需要悬浮窗权限）

#### 作者的其他开源项目

* 安卓架构：[AndroidProject](https://github.com/getActivity/AndroidProject)

* 网络框架：[EasyHttp](https://github.com/getActivity/EasyHttp)

* 日志框架：[Logcat](https://github.com/getActivity/Logcat)

* 权限框架：[XXPermissions](https://github.com/getActivity/XXPermissions)

* 吐司框架：[ToastUtils](https://github.com/getActivity/ToastUtils)

* 标题栏框架：[TitleBar](https://github.com/getActivity/TitleBar)

* 国际化框架：[MultiLanguages](https://github.com/getActivity/MultiLanguages)

#### Android技术讨论Q群：78797078

#### 如果您觉得我的开源库帮你节省了大量的开发时间，请扫描下方的二维码随意打赏，要是能打赏个 10.24 :monkey_face:就太:thumbsup:了。您的支持将鼓励我继续创作:octocat:

![](https://raw.githubusercontent.com/getActivity/Donate/master/picture/pay_ali.png) ![](https://raw.githubusercontent.com/getActivity/Donate/master/picture/pay_wechat.png)

#### [点击查看捐赠列表](https://github.com/getActivity/Donate)

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
