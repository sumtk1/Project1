package com.gloiot.hygounionmerchant.ui.activity.mine.setting;

import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;

import com.gloiot.hygounionmerchant.R;
import com.gloiot.hygounionmerchant.ui.BaseActivity;
import com.gloiot.hygounionmerchant.ui.activity.mine.password.ModifyPwdActivity;
import com.gloiot.hygounionmerchant.utils.CommonUtils;
import com.gloiot.hygounionmerchant.utils.ConstantUtils;
import com.zyd.wlwsdk.utils.SharedPreferencesUtils;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 账号与安全（账户管理）
 * Created by Dlt on 2017/8/24 14:54
 */
public class AccountSecurityActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.rl_modify_pay_pwd)
    RelativeLayout mRlModifyPayPwd;

    private String identityType;

    @Override
    public int initResource() {
        return R.layout.activity_account_security;
    }

    @Override
    public void initData() {
        CommonUtils.setTitleBar(this, true, "账户管理", "");
        String payPwdState = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTPAYPWDSTATE, "");

        identityType = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_IDENTITYTYPE, "");

        if (identityType.equals("负责人")) {
//            mRlModifyPayPwd.setVisibility(View.VISIBLE);

            if (payPwdState.equals("未设置")) {
                mRlModifyPayPwd.setVisibility(View.GONE);
            } else if (payPwdState.equals("已设置")) {
                mRlModifyPayPwd.setVisibility(View.VISIBLE);
            }

        } else {
            mRlModifyPayPwd.setVisibility(View.GONE);
        }

    }

    @OnClick({R.id.rl_modify_login_pwd, R.id.rl_modify_pay_pwd})
    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.rl_modify_login_pwd:
                intent = new Intent(AccountSecurityActivity.this, ModifyPwdActivity.class);
                intent.putExtra("pwdType", "login");
                startActivity(intent);
                break;
            case R.id.rl_modify_pay_pwd:
                intent = new Intent(AccountSecurityActivity.this, ModifyPwdActivity.class);
                intent.putExtra("pwdType", "pay");
                startActivity(intent);
                break;
        }
    }

}
