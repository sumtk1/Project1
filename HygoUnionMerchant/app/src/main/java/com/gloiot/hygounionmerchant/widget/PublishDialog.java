package com.gloiot.hygounionmerchant.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.gloiot.hygounionmerchant.R;

/**
 * 首页上传发布弹窗
 * Created by Dlt on 2017/8/17 16:32
 */
public class PublishDialog extends Dialog {

    private RelativeLayout rlMain;
    private Context context;
    private LinearLayout llTiaoma, llRuku;
    private ImageView ivTiaoma, ivRuku;
    private ImageView ivMenu;
    private Handler handler;

    public PublishDialog(Context context) {
        this(context, R.style.main_publishdialog_style);
    }

    private PublishDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        handler = new Handler();
        //填充视图
        setContentView(R.layout.main_dialog_publish);
        rlMain = (RelativeLayout) findViewById(R.id.rl_main);

        llTiaoma = (LinearLayout) findViewById(R.id.ll_tiaoma);
        llRuku = (LinearLayout) findViewById(R.id.ll_ruku);
        ivTiaoma = (ImageView) findViewById(R.id.iv_tiaoma);
        ivRuku = (ImageView) findViewById(R.id.iv_ruku);
        ivMenu = (ImageView) findViewById(R.id.iv_menu);

        ivMenu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                outputDialog();
            }
        });
        rlMain.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                outputDialog();
            }
        });
    }

    /**
     * 进入对话框（带动画）
     */
    private void inputDialog() {
        llTiaoma.setVisibility(View.INVISIBLE);
        llRuku.setVisibility(View.INVISIBLE);

        //背景动画
        rlMain.startAnimation(AnimationUtils.loadAnimation(context, R.anim.mainactivity_fade_in));
        //菜单按钮动画
        ivMenu.startAnimation(AnimationUtils.loadAnimation(context, R.anim.mainactivity_rotate_right));
        //选项动画
        llTiaoma.setVisibility(View.VISIBLE);
        llTiaoma.startAnimation(AnimationUtils.loadAnimation(context, R.anim.mainactivity_push_bottom_in));
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                llRuku.setVisibility(View.VISIBLE);
                llRuku.startAnimation(AnimationUtils.loadAnimation(context, R.anim.mainactivity_push_bottom_in));
            }
        }, 100);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (isShowing()) {
            outputDialog();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    /**
     * 取消对话框（带动画）
     */
    public  void outputDialog() {
        //退出动画
        rlMain.startAnimation(AnimationUtils.loadAnimation(context, R.anim.mainactivity_fade_out));
        ivMenu.startAnimation(AnimationUtils.loadAnimation(context, R.anim.mainactivity_rotate_left));
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                dismiss();
            }
        }, 400);
        llTiaoma.startAnimation(AnimationUtils.loadAnimation(context, R.anim.mainactivity_push_bottom_out));
        llTiaoma.setVisibility(View.INVISIBLE);
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                llRuku.startAnimation(AnimationUtils.loadAnimation(context, R.anim.mainactivity_push_bottom_out));
                llRuku.setVisibility(View.INVISIBLE);
            }
        }, 50);


    }

    @Override
    public void show() {
        super.show();
        inputDialog();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //全屏
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes(params);
    }

    public PublishDialog setTiaomaBtnClickListener(android.view.View.OnClickListener clickListener) {
        ivTiaoma.setOnClickListener(clickListener);
        return this;
    }

    public PublishDialog setRukuBtnClickListener(android.view.View.OnClickListener clickListener) {
        ivRuku.setOnClickListener(clickListener);
        return this;
    }

}
