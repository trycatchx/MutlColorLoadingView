# MutlColorLoadingView
![Build Status](https://img.shields.io/badge/build-passing-brightgreen) ![java](https://img.shields.io/badge/language-java-Borange.svg)  ![java](https://img.shields.io/badge/version-1.0.0-orange.svg)

[Englist](https://github.com/zhangchaojiong/MutlColorLoadingView/blob/master/README.md) | [中文版本](https://github.com/zhangchaojiong/MutlColorLoadingView/blob/master/README_zh.md)
>一个漂亮的 Loading 动画

## 效果展示

![demo.gif](https://github.com/zhangchaojiong/MutlColorLoadingView/blob/master/image/demo.gif)

## 如何使用：
* 加入 gradle 依赖

```
implementation 'com.mutlcolorloadingview:mutlcolorloadingview:1.0.0'
```
* 在 xml 加入相应的动画::

```
<com.mutlcolorloadingview.MutlColorLoadingView
        android:id="@+id/mclv"
        android:layout_width="120dp"
        android:layout_height="105dp"
        android:padding="28dp"
        android:background="@drawable/bg_loading"
        app:mclv_duration="2000"
        app:mclv_first_color="@color/color_FFFFFFFF"
        app:mclv_second_color="@color/color_FF4D6BFF"
        app:mclv_three_color="@color/color_FFFF8400"
        app:mclv_stroke_width="4dp"
        />
```



## 可使用的参数解析 
LightProgressView 自定义参数：
```
    <declare-styleable name="MutlColorLoadingView">
        <!--第一种进度条的颜色-->
        <attr name="mclv_first_color" format="color" />
        <!--第二种进度条的颜色-->
        <attr name="mclv_second_color" format="color" />
        <!--第三种进度条的颜色-->
        <attr name="mclv_three_color" format="color" />
        <!--一次完整动画时长-->
        <attr name="mclv_duration" format="integer" />
        <!--开启动画的角度-->
        <attr name="mclv_start_angle" format="integer" />
        <!--进度条的宽度-->
        <attr name="mclv_stroke_width" format="integer|dimension"/>
        <!--第一圈，白色进度占总进度的时长比例-->
        <attr name="mclv_rate_first_round" format="float" />
    </declare-styleable>
```


## 版权声明

```
Copyright (C) 2020 chaojiong.zhang

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