# 超级 Toast

> 博客地址：[悬浮窗需求终结者](https://www.jianshu.com/p/247d705b87b6)

> 已投入公司项目多时，没有任何毛病，可胜任任何需求，[点击此处下载Demo](https://raw.githubusercontent.com/getActivity/XToast/master/XToast.apk)

> 想了解实现原理的可以点击此链接查看：[XToast](https://github.com/getActivity/XToast/blob/master/library/src/main/java/com/hjq/xtoast/XToast.java) 源码

![](XToast.gif)

#### 本框架意在解决一些极端需求，如果是普通的 Toast 封装推荐使用 [ToastUtils](https://github.com/getActivity/ToastUtils)

#### 集成步骤

    dependencies {
        implementation 'com.hjq:xtoast:2.0'
    }

#### 使用案例

    new XToast(XToastActivity.this) // 传入 Application 表示设置成全局的
            .setView(R.layout.toast_hint)
            .setDraggable() // 设置成可拖拽的
			.setDuration(1000) // 设置显示时长
			.setAnimStyle(android.R.style.Animation_Translucent) // 设置动画样式
            .setImageDrawable(android.R.id.icon, R.mipmap.ic_dialog_tip_finish)
            .setText(android.R.id.message, "点我消失")
            .setOnClickListener(android.R.id.message, new OnClickListener<TextView>() {

                @Override
                public void onClick(XToast toast, TextView view) {
					// 点击这个 View 后消失
                    toast.cancel();
                }
            })
            .show();

#### 混淆规则

    -keep class com.hjq.xtoast.** {*;}

#### 框架亮点（原生 Toast 无法实现的功能）

* 支持自定义 Toast 动画样式

* 支持自定义 Toast 显示时长

* 支持监听 Toast 的显示和销毁

* 支持监听 Toast 中点击事件

* 支持一键开启 Toast 拖拽功能

* 支持 Toast 全局显示（需要权限）

#### 作者的其他开源项目

* 架构工程：[AndroidProject](https://github.com/getActivity/AndroidProject)

* 权限封装：[XXPermissions](https://github.com/getActivity/XXPermissions)

* 吐司封装：[ToastUtils](https://github.com/getActivity/ToastUtils)

* 标题栏封装：[TitleBar](https://github.com/getActivity/TitleBar)

#### Android技术讨论Q群：78797078

#### 如果您觉得我的开源库帮你节省了大量的开发时间，请扫描下方的二维码随意打赏，要是能打赏个 10.24 :monkey_face:就太:thumbsup:了。您的支持将鼓励我继续创作:octocat:

![](pay_ali.png) ![](pay_wechat.png)

## License

```text
Copyright 2018 Huang JinQun

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
