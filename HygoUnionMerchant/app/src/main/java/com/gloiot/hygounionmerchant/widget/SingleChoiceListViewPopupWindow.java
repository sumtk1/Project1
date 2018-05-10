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
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.gloiot.hygounionmerchant.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dlt on 2017/9/20 11:00
 */
public class SingleChoiceListViewPopupWindow extends PopupWindow {

    private Context mContext;
    private TextView tv_cancel, tv_confirm;
    private ListView list_view;
    private View mMenuView, customView;
    private List<String[]> list = new ArrayList<String[]>();
    private int wHeight;//屏幕高度

    @SuppressLint("InflateParams")
    public SingleChoiceListViewPopupWindow(Context context, View.OnClickListener itemsOnClick) {
        super(context);
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.layout_single_choice_list_view_popup_window, null);

        wHeight = getScreenHeight();//屏幕高度

        tv_cancel = (TextView) mMenuView.findViewById(R.id.tv_cancel);
        tv_confirm = (TextView) mMenuView.findViewById(R.id.tv_confirm);
        list_view = (ListView) mMenuView.findViewById(R.id.list_view);

        // 设置监听
        tv_cancel.setOnClickListener(itemsOnClick);//取消
        tv_confirm.setOnClickListener(itemsOnClick);//确定

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
     * 获取ListView
     *
     * @return
     */
    public ListView getListView() {
        return list_view;
    }

    /**
     * 设置最大高度
     *
     * @return
     */
    public SingleChoiceListViewPopupWindow setMaxHeight(final View view) {
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
