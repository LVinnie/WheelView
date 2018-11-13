package com.vinnie.weight;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * WheelView
 * @author Vinnie
 */
public class WheelView extends ScrollView {

    public static final String TAG = WheelView.class.getSimpleName();

    public static class OnWheelViewListener {
        public void onSelected(int selectedIndex, String item) {
        }
    }

    private Context context;
    private LinearLayout views;

    public WheelView(Context context) {
        super(context);
        init(context);
    }

    public WheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public WheelView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    List<String> items;

    private List<String> getItems() {
        return items;
    }

    public void setItems(List<String> list) {
        if (null == items) {
            items = new ArrayList<String>();
        }
        items.clear();
        items.addAll(list);

        // 前面和后面补全
        for (int i = 0; i < offset; i++) {
            items.add(0, "");
            items.add("");
        }

        initData();
    }


    public static final int OFF_SET_DEFAULT = 1;

    int offset = OFF_SET_DEFAULT; // 偏移量（需要在最前面和最后面补全）

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    int displayItemCount; // 每页显示的数量

    int selectedIndex = 1;

    int fastSpeed, centerSpeed, slowSpeed;

    private void init(Context context) {
        this.context = context;

        float displayMetrics = context.getResources().getDisplayMetrics().density;
        fastSpeed = (int) (displayMetrics * 16);
        centerSpeed = (int) (displayMetrics * 8);
        slowSpeed = (int) (displayMetrics * 4);

        this.setVerticalScrollBarEnabled(false);
        this.setOverScrollMode(View.OVER_SCROLL_NEVER);

        views = new LinearLayout(context);
        views.setOrientation(LinearLayout.VERTICAL);
        this.addView(views);

        scrollerTask = new Runnable() {

            public void run() {

                int newY = getScrollY();
                boolean stopped = (initialY - newY) == 0;
                if (speedY <= fastSpeed || stopped) { // stopped
                    final int remainder = initialY % itemHeight;
                    final int divided = initialY / itemHeight;
                    if (stopped && remainder == 0) {
                        selectedIndex = divided + offset;
                        onSelectedCallBack();
                    } else {
                        if (scrollDirection == SCROLL_DIRECTION_DOWN) {
                            WheelView.this.post(new Runnable() {
                                @Override
                                public void run() {

                                    if (speedY < slowSpeed) {
                                        if (remainder > itemHeight / 2) {
                                            selectedIndex = divided + offset + 1;
                                        } else {
                                            selectedIndex = divided + offset;
                                        }
                                    } else if (speedY < centerSpeed) {
                                        if (remainder > itemHeight / 2) {
                                            selectedIndex = divided + offset + 2;
                                        } else {
                                            selectedIndex = divided + offset + 1;
                                        }
                                    } else {
                                        if (remainder > itemHeight / 2) {
                                            selectedIndex = divided + offset + 3;
                                        } else {
                                            selectedIndex = divided + offset + 2;
                                        }
                                    }

                                    WheelView.this.smoothScrollTo(0, itemHeight * (selectedIndex - offset));
                                    onSelectedCallBack();
                                }
                            });
                        } else {
                            WheelView.this.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (speedY < slowSpeed) {
                                        if (remainder > itemHeight / 2) {
                                            selectedIndex = divided + offset + 1;
                                        } else {
                                            selectedIndex = divided + offset;
                                        }
                                    } else if (speedY < centerSpeed) {
                                        if (remainder > itemHeight / 2) {
                                            selectedIndex = divided + offset - 0;
                                        } else {
                                            selectedIndex = divided + offset - 1;
                                        }
                                    } else {
                                        if (remainder > itemHeight / 2) {
                                            selectedIndex = divided + offset - 2;
                                        } else {
                                            selectedIndex = divided + offset - 3;
                                        }
                                    }
                                    WheelView.this.smoothScrollTo(0, itemHeight * (selectedIndex - offset));
                                    onSelectedCallBack();
                                }
                            });
                        }
                    }
                } else {
                    initialY = getScrollY();
                    WheelView.this.postDelayed(scrollerTask, newCheck);
                }
            }
        };
    }

    int initialY;

    Runnable scrollerTask;
    int newCheck = 30;

    public void startScrollerTask() {
        initialY = getScrollY();
        this.postDelayed(scrollerTask, newCheck);
    }

    private void initData() {
        displayItemCount = offset * 2 + 1;

        for (String item : items) {
            views.addView(createView(item));
        }

        refreshItemView(0);
    }

    int itemHeight = 0;

    private TextView createView(String item) {
        if (itemHeight == 0) {
            itemHeight = dip2px(60);
            views.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, itemHeight * displayItemCount));
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.getLayoutParams();
            this.setLayoutParams(new LinearLayout.LayoutParams(lp.width, itemHeight * displayItemCount));
        }
        TextView tv = new TextView(context);
        tv.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, itemHeight));
        tv.setSingleLine(true);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        tv.setText(item);
        tv.setGravity(Gravity.CENTER);
        int padding = dip2px(15);
        tv.setPadding(padding, 0, padding, 0);
        return tv;
    }

    int speedY = 0;
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        speedY = Math.abs(t - oldt);
        refreshItemView(t);
        if (t > oldt) {
            scrollDirection = SCROLL_DIRECTION_DOWN;
        } else {
            scrollDirection = SCROLL_DIRECTION_UP;
        }
    }

    private void refreshItemView(int y) {
        int position = y / itemHeight + offset;
        int remainder = y % itemHeight;
        int divided = y / itemHeight;

        if (remainder == 0) {
            position = divided + offset;
        } else {
            if (remainder > itemHeight / 2) {
                position = divided + offset + 1;
            }
        }

        int childSize = views.getChildCount();
        for (int i = 0; i < childSize; i++) {
            TextView itemView = (TextView) views.getChildAt(i);
            if (null == itemView) {
                return;
            }
            if (position == i) {
                itemView.setTextColor(Color.parseColor("#2BB054"));
                itemView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 33);
            } else {
                itemView.setTextColor(Color.parseColor("#727171"));
                itemView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 22);
            }
        }
    }

    /**
     * 获取选中区域的边界
     */
    int[] selectedAreaBorder;

    private int[] obtainSelectedAreaBorder() {
        if (null == selectedAreaBorder) {
            selectedAreaBorder = new int[2];
            selectedAreaBorder[0] = itemHeight * offset;
            selectedAreaBorder[1] = itemHeight * (offset + 1);
        }
        return selectedAreaBorder;
    }


    private int scrollDirection = -1;
    private static final int SCROLL_DIRECTION_UP = 0;
    private static final int SCROLL_DIRECTION_DOWN = 1;

    Paint paint;
    int viewWidth;

    @Override
    public void setBackgroundDrawable(Drawable background) {

        if (viewWidth == 0) {
            viewWidth = ((Activity) context).getWindowManager().getDefaultDisplay().getWidth();
        }

        if (null == paint) {
            paint = new Paint();
            paint.setColor(Color.parseColor("#E0E0E0"));
            paint.setStrokeWidth(dip2px(1f));
        }

        background = new Drawable() {
            @Override
            public void draw(Canvas canvas) {
                // 设置分割线
                canvas.drawLine(0, obtainSelectedAreaBorder()[0], viewWidth, obtainSelectedAreaBorder()[0], paint);
                canvas.drawLine(0, obtainSelectedAreaBorder()[1], viewWidth, obtainSelectedAreaBorder()[1], paint);
            }

            @Override
            public void setAlpha(int alpha) {

            }

            @Override
            public void setColorFilter(ColorFilter cf) {

            }

            @Override
            public int getOpacity() {
                return PixelFormat.UNKNOWN;
            }
        };


        super.setBackgroundDrawable(background);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewWidth = w;
        setBackgroundDrawable(null);
    }

    /**
     * 选中回调
     */
    private void onSelectedCallBack() {

        if (selectedIndex < offset) {
            selectedIndex = offset;
        } else if (selectedIndex > items.size() - offset - 1) {
            selectedIndex = items.size() - offset - 1;
        }

        if (null != onWheelViewListener) {
            onWheelViewListener.onSelected(selectedIndex - offset, items.get(selectedIndex));
        }
    }

    public void setSelection(int position) {
        final int p = position;
        selectedIndex = p + offset;
        this.post(new Runnable() {
            @Override
            public void run() {
                WheelView.this.smoothScrollTo(0, p * itemHeight);
            }
        });

    }

    public String getSelectedItem() {
        return items.get(selectedIndex);
    }

    public int getSelectedIndex() {
        return selectedIndex - offset;
    }

    @Override
    public void fling(int velocityY) {
        super.fling(velocityY / 2);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {

            startScrollerTask();
        }
        return super.onTouchEvent(ev);
    }

    private OnWheelViewListener onWheelViewListener;

    public OnWheelViewListener getOnWheelViewListener() {
        return onWheelViewListener;
    }

    public void setOnWheelViewListener(OnWheelViewListener onWheelViewListener) {
        this.onWheelViewListener = onWheelViewListener;
    }

    private int dip2px(float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private int getViewMeasuredHeight(View view) {
        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int expandSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, View.MeasureSpec.AT_MOST);
        view.measure(width, expandSpec);
        return view.getMeasuredHeight();
    }
}
