package com.vinnie.weight.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.vinnie.weight.R;
import com.vinnie.weight.WheelView;
import com.vinnie.weight.popup.WheelViewPopupWindow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String[] PLANETS = new String[]{"S00", "S01", "S02", "S03", "S04", "S05", "S06", "S07", "S08", "S09", "S10", "S11", "S12", "S13", "S14", "S15", "S16", "S17", "S18", "S19", "S20", "S21", "S22", "S23", "S24", "S25", "S26", "S27", "S28", "S29", "S30", "S31", "S32", "S33", "S34", "S35", "S36", "S37", "S38", "S39", "S40", "S41", "S42", "S43", "S44", "S45", "S46", "S47", "S48", "S49", "S50"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WheelView wv = findViewById(R.id.wheel_view);

        wv.setOffset(1);
        wv.setItems(Arrays.asList(PLANETS));
        wv.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                Log.e(TAG, "selectedIndex: " + selectedIndex + ", item: " + item);
            }
        });

        findViewById(R.id.main_show_dialog_btn).setOnClickListener(this);
        findViewById(R.id.main_show_popup_btn).setOnClickListener(this);
        findViewById(R.id.main_reset_popup_btn).setOnClickListener(this);
    }

    AlertDialog alertDialog;
    WheelViewPopupWindow wheelViewPopupWindow;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_show_dialog_btn:
                if (alertDialog == null) {
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

                    alertDialog = new AlertDialog.Builder(this)
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
                            }).create();
                }
                alertDialog.show();

                /*View outerView = LayoutInflater.from(this).inflate(R.layout.view_wheel_view, null);
                WheelView wv = outerView.findViewById(R.id.wheel_view);
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
                        .show();*/
                break;
            case R.id.main_show_popup_btn:
                if (wheelViewPopupWindow == null) {
                    List<String> list = Arrays.asList(PLANETS);
                    List<List<String>> data = new ArrayList<>();
                    data.add(list);
                    data.add(list);
                    data.add(list);
                    wheelViewPopupWindow = new WheelViewPopupWindow(MainActivity.this)
                            .setOffset(3)
                            .setData(data)
                            .setDefaultSelection(5, 6, 7)
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
                }
                wheelViewPopupWindow.showAtCenter();
                break;
            case R.id.main_reset_popup_btn:
                if (wheelViewPopupWindow != null) {
                    wheelViewPopupWindow.resetSelection();
                }
                break;
        }
    }
}
