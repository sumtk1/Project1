package com.gloiot.hygounionmerchant.utils;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

import com.gloiot.hygounionmerchant.R;


/**
 * 验证按钮是否满足点击条件（本项目专用）
 * Created by Dlt on 2017/7/26 18:17
 */
public abstract class VerifyButtonEnableWatcher implements TextWatcher {

    private Context context;
    private TextView textView;

    public VerifyButtonEnableWatcher(Context context, TextView textView) {
        this.context = context;
        this.textView = textView;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        if (verifyCondition(s)) {
            textView.setEnabled(true);
            textView.setBackgroundResource(R.drawable.bg_btn_green_4dp);
            textView.setTextColor(context.getResources().getColor(R.color.white));
        } else {
            textView.setEnabled(false);
            textView.setBackgroundResource(R.drawable.bg_green_disable_4dp);
            textView.setTextColor(context.getResources().getColor(R.color.white));
        }

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    public abstract boolean verifyCondition(CharSequence s);

}
