package com.vinnie.weight.popup;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.vinnie.weight.R;
import com.vinnie.weight.WheelView;

import java.util.ArrayList;
import java.util.List;


/**
 * WheelView选择弹窗
 * @author 刘志阳
 */
public class WheelViewPopupWindow extends PopupWindow {

    private Context context;
    private LinearLayout layout;
    private int offset = 2;
    private List<WheelView> wheelViews;
    private int[] defaultValue;

    public WheelViewPopupWindow(Context context) {
        super(context);
        this.context = context;

        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(0));

        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        initView();
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                setBackgroundAlpha(1);
                if (onSelectListener != null) {
                    List<String> list = new ArrayList<>();
                    for (WheelView wheelView : wheelViews) {
                        wheelView.stop();
                        list.add(wheelView.getSelectedItem());
                    }
                    onSelectListener.onSelect(list);
                }
            }
        });
    }

    private void initView() {
        View view = LayoutInflater.from(context).inflate(R.layout.popup_wheel_view, null, false);
        layout = view.findViewById(R.id.layout);
        view.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        setContentView(view);
    }

    public WheelViewPopupWindow setOffset(int offset) {
        this.offset = offset;
        return this;
    }

    public WheelViewPopupWindow setData(List<List<String>> list) {

        if (wheelViews == null) {
            wheelViews = new ArrayList<>();
        } else {
            wheelViews.clear();
        }
        layout.removeAllViews();

        defaultValue = new int[list.size()];

        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(dp2px(120), LinearLayout.LayoutParams.WRAP_CONTENT);

        for (List<String> list2 : list) {
            WheelView wheelView = new WheelView(context);
            layout.addView(wheelView, ll);
            wheelView.setOffset(offset);
            wheelView.setItems(list2);
            wheelViews.add(wheelView);
        }
        return this;
    }

    /**
     * 设置WheelView的初始值
     * @param valueIndex 所有WheelView初始值坐标都一样
     * @return
     */
    public WheelViewPopupWindow setDefaultSelection(int valueIndex) {
        if (wheelViews == null) {
            return this;
        }
        int l = wheelViews.size();
        for (int i=0; i<l; i++) {
            defaultValue[i] = valueIndex;
            wheelViews.get(i).setSelection(valueIndex);
        }
        return this;
    }

    /**
     * 设置WheelView的初始值
     * @param valueIndex
     * @return
     */
    public WheelViewPopupWindow setDefaultSelection(int... valueIndex) {
        if (wheelViews == null || wheelViews.size() != valueIndex.length) {
            return this;
        }
        defaultValue = valueIndex;
        for (int i=0,l=wheelViews.size(); i<l; i++) {
            wheelViews.get(i).setSelection(valueIndex[i]);
        }
        return this;
    }

    public void resetSelection() {
        for (int i=0,l=wheelViews.size(); i<l; i++) {
            wheelViews.get(i).setSelection(defaultValue[i]);
        }
    }

    public void showAtCenter() {
        AppCompatActivity activity = (AppCompatActivity) context;
        showAtLocation(activity.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        setBackgroundAlpha(0.6f);
        super.showAtLocation(parent, gravity, x, y);
    }

    private OnSelectListener onSelectListener;
    public interface OnSelectListener {
        void onSelect(List<String> list);
    }

    public WheelViewPopupWindow setOnSelectListener(OnSelectListener onSelectListener) {
        this.onSelectListener = onSelectListener;
        return this;
    }

    public int dp2px(final float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 设置屏幕透明度
     * @param bgAlpha
     */
    public void setBackgroundAlpha(float bgAlpha) {
        AppCompatActivity activity = (AppCompatActivity) context;
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        activity.getWindow().setAttributes(lp);
    }
}