WheelView
=========

## explain
之前使用了wangjiegulu大神造的WheelView
https://github.com/wangjiegulu/WheelView

觉得有几点缺陷。

1，滑动速度很快的时候，要很长时间才会停下来，用户要等待很久。

2，滑动将要停止的时候很大概率会发生回转，感觉有点难受。

3，回调的index是加上offset后的值，使用不太方便。

4，新增了手动stop功能，适用于Dialog或PopupWindow。

所以就在wangjiegulu大神的基础上做了一番改造。

如图，图1是原版效果，图2是我改造后的效果。（录的24帧的GIF，效果差，真实效果好很多）

<img src='image/2.gif' height='500px'/>
<img src='image/1.gif' height='500px'/>

## How to use

### layout:
```xml
<com.vinnie.weight.WheelView
    android:id="@+id/wheel_view"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    />
```

### Activity:
```java
WheelView wv = findViewById(R.id.wheel_view);
wv.setOffset(1);
wv.setItems(Arrays.asList(PLANETS));
wv.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
    @Override
    public void onSelected(int selectedIndex, String item) {
        Log.d(TAG, "selectedIndex: " + selectedIndex + ", item: " + item);
    }
});
```

#### Show in dialog:
```java
View outerView = LayoutInflater.from(this).inflate(R.layout.view_wheel_view, null);
final WheelView wv = outerView.findViewById(R.id.wheel_view);
wv.setOffset(2);
wv.setItems(Arrays.asList(PLANETS));
wv.setSelection(3);
wv.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
    @Override
    public void onSelected(int selectedIndex, String item) {
        Log.e(TAG, "[Dialog]selectedIndex: " + selectedIndex + ", item: " + item);
    }
});

new AlertDialog.Builder(this)
        .setTitle("WheelView in Dialog")
        .setView(outerView)
        .setPositiveButton("OK", null)
        .setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                //dialog dismiss时需要手动调用stop，否则取到的值是最后一次停止时的值。
                wv.stop();
                Log.e(TAG, "[Dialog#dismiss]selectedIndex: " + wv.getSelectedIndex() + ", item: " + wv.getSelectedItem());
            }
        }).show();
```

#### WheelViewPopupWindow:
```java
List<String> list = Arrays.asList(PLANETS);
List<List<String>> data = new ArrayList<>();
data.add(list);
data.add(list);
data.add(list);
WheelViewPopupWindow wheelViewPopupWindow = new WheelViewPopupWindow(MainActivity.this)
        .setOffset(3)
        .setData(data)
        .setDefaultSelection(5, 6, 7, 8)
        .setOnSelectListener(new WheelViewPopupWindow.OnSelectListener() {
            @Override
            public void onSelect(List<String> list) {
                StringBuilder sb = new StringBuilder();
                for (String s : list) {
                    sb.append(s);
                    sb.append(",");
                }
                sb.deleteCharAt(sb.length() - 1);
                Log.e(TAG, "[PopupWindow] selected: " + sb.toString());
            }
        });
wheelViewPopupWindow.showAtCenter();
```
    
License
=======

    Copyright 2018 LVinnie

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.


[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-WheelView-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/1433)
