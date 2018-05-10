package com.gloiot.hygounionmerchant.ui.activity.home;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.gloiot.hygounionmerchant.R;
import com.gloiot.hygounionmerchant.ui.BaseActivity;
import com.gloiot.hygounionmerchant.utils.CommonInputUtils;
import com.gloiot.hygounionmerchant.utils.CommonUtils;
import com.gloiot.hygounionmerchant.utils.ConstantUtils;
import com.gloiot.hygounionmerchant.utils.MaxLengthWatcher;
import com.gloiot.hygounionmerchant.utils.VerifyButtonEnableWatcher;
import com.zyd.wlwsdk.utils.SharedPreferencesUtils;
import com.zyd.wlwsdk.utils.StatusBarUtil;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 输码认证
 * Created by Dlt on 2017/11/15 9:49
 */
public class InputCodeAuthenticationActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.et_num_for_use)
    EditText mEtNumForUse;
    @Bind(R.id.et_yzm)
    EditText mEtYzm;
    @Bind(R.id.tv_renzheng)
    TextView mTvRenzheng;

    private String accountType, accountName;

    @Override
    public int initResource() {
        return R.layout.activity_input_code_authentication;
    }

    @Override
    public void initData() {
        StatusBarUtil.setColor(this, getResources().getColor(R.color.green_21d1c1), 0);
        accountType = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTTYPE, "");
        accountName = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTNAME, "");
        CommonUtils.setTitleBar(this, true, accountName, "");

        setTextWatcher();
        CommonInputUtils.filterBlank(mEtNumForUse);//禁止输入空格
        CommonInputUtils.filterBlank(mEtYzm);
        mEtNumForUse.addTextChangedListener(new MaxLengthWatcher(16, mEtNumForUse));//编号最多16位
    }

    private void setTextWatcher() {
        //暂定编号最少8位，验证码最少六位。具体再由需求定
        mEtNumForUse.addTextChangedListener(new VerifyButtonEnableWatcher(mContext, mTvRenzheng) {
            @Override
            public boolean verifyCondition(CharSequence s) {
                return mEtYzm.getText().toString().trim().length() >= 6 && s.length() >= 8;
            }
        });

        mEtYzm.addTextChangedListener(new VerifyButtonEnableWatcher(mContext, mTvRenzheng) {
            @Override
            public boolean verifyCondition(CharSequence s) {
                return mEtNumForUse.getText().toString().trim().length() >= 8 && s.length() >= 6;
            }
        });

    }

    @OnClick(R.id.tv_renzheng)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_renzheng:

                break;
        }
    }

}
