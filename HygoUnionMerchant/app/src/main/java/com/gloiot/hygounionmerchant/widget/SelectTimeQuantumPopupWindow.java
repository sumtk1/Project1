package com.gloiot.hygounionmerchant.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.gloiot.hygounionmerchant.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 选择时间段
 * Created by Dlt on 2017/9/18 16:11
 */
public class SelectTimeQuantumPopupWindow extends PopupWindow {

    private Context mContext;
    private ImageView iv_close;
    private TextView tv_start_time, tv_end_time, tv_reset, tv_complete;
    private View mMenuView, customView;
    private List<String[]> list = new ArrayList<String[]>();
    private int wHeight;//屏幕高度

    @SuppressLint("InflateParams")
    public SelectTimeQuantumPopupWindow(Context context, View.OnClickListener itemsOnClick) {
        super(context);
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.layout_select_time_quantum_popup_window, null);


        wHeight = getScreenHeight();//屏幕高度

        iv_close = (ImageView) mMenuView.findViewById(R.id.iv_close);
        tv_start_time = (TextView) mMenuView.findViewById(R.id.tv_start_time);
        tv_end_time = (TextView) mMenuView.findViewById(R.id.tv_end_time);
        tv_reset = (TextView) mMenuView.findViewById(R.id.tv_reset);
        tv_complete = (TextView) mMenuView.findViewById(R.id.tv_complete);

        // 设置监听
        iv_close.setOnClickListener(itemsOnClick);//取消
        tv_start_time.setOnClickListener(itemsOnClick);//开始时间
        tv_end_time.setOnClickListener(itemsOnClick);//结束时间
        tv_reset.setOnClickListener(itemsOnClick);//重置
        tv_complete.setOnClickListener(itemsOnClick);//完成

        // 设置弹出窗体的View
        this.setContentView(mMenuView);
        // 设置弹出窗体的宽
//        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        // 设置弹出窗体的高
//        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        // 设置弹出窗体可点击
        this.setFocusable(true);
        // 设置弹出窗体动画效果
        this.setAnimationStyle(R.style.anim_popup_dir);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0x80000000);
        // 设置弹出窗体的背景
        this.setBackgroundDrawable(dw);
        // mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        mMenuView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            @SuppressLint("ClickableViewAccessibility")
            public boolean onTouch(View v, MotionEvent event) {

                int height = mMenuView.findViewById(R.id.pop_layout).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });

    }

    /**
     * 获得屏幕高度
     *
     * @return
     */
    public int getScreenHeight() {
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    /**
     * 设置开始时间
     */
    public SelectTimeQuantumPopupWindow setStartTime(String text) {
        tv_start_time.setText(text);
        return this;
    }

    /**
     * 设置结束时间
     */
    public SelectTimeQuantumPopupWindow setEndTime(String text) {
        tv_end_time.setText(text);
        return this;
    }

    /**
     * 获取开始时间
     *
     * @return
     */
    public String getStartTime() {
        return tv_start_time.getText().toString().trim();
    }

    /**
     * 获取结束时间
     *
     * @return
     */
    public String getEndTime() {
        return tv_end_time.getText().toString().trim();
    }

    /**
     * 设置最大高度
     *
     * @return
     */
    public SelectTimeQuantumPopupWindow setMaxHeight(final View view) {
        ViewTreeObserver vto = view.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                int cHeight = view.getHeight();
                ViewGroup.LayoutParams lp = view.getLayoutParams();
                if (cHeight >= wHeight * 2 / 5) {
                    lp.height = wHeight * 2 / 5;
                }
                view.setLayoutParams(lp);
            }
        });
        return this;
    }

}
