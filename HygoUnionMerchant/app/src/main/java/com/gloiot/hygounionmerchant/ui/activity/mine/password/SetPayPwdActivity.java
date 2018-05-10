package com.gloiot.hygounionmerchant.ui.activity.mine.password;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.gloiot.hygounionmerchant.R;
import com.gloiot.hygounionmerchant.server.network.RequestAction;
import com.gloiot.hygounionmerchant.ui.BaseActivity;
import com.gloiot.hygounionmerchant.ui.activity.MainActivity;
import com.gloiot.hygounionmerchant.ui.activity.login.ForgetLoginPwd1Activity;
import com.gloiot.hygounionmerchant.ui.activity.login.ForgetLoginPwd2Activity;
import com.gloiot.hygounionmerchant.ui.activity.login.LoginActivity;
import com.gloiot.hygounionmerchant.ui.activity.login.SelectLoginTypeActivity;
import com.gloiot.hygounionmerchant.utils.CommonUtils;
import com.gloiot.hygounionmerchant.utils.ConstantUtils;
import com.gloiot.hygounionmerchant.utils.MaxLengthWatcher;
import com.gloiot.hygounionmerchant.utils.VerifyButtonEnableWatcher;
import com.zyd.wlwsdk.utils.L;
import com.zyd.wlwsdk.utils.MD5Utlis;
import com.zyd.wlwsdk.utils.MToast;
import com.zyd.wlwsdk.utils.SharedPreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Dlt on 2017/8/16 19:22
 */
public class SetPayPwdActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.et_pay_pwd)
    EditText mEtPayPwd;
    @Bind(R.id.et_pay_pwd_confirm)
    EditText mEtPayPwdConfirm;
    @Bind(R.id.tv_confirm)
    TextView mTvConfirm;

    private String accountType;
    private String newPwd, confirmPwd;
    private String fromFlag;

    @OnClick(R.id.tv_confirm)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_confirm:
                if (verifyData()) {
                    requestHandleArrayList.add(requestAction.setAccountPayPwd(SetPayPwdActivity.this, MD5Utlis.Md5(newPwd), MD5Utlis.Md5(confirmPwd), accountType));
                }
                break;
        }
    }

    @Override
    public int initResource() {
        return R.layout.activity_set_pay_pwd;
    }

    @Override
    public void initData() {
        Intent intent = getIntent();
        fromFlag = intent.getStringExtra("from");//取值login/forget

        accountType = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTTYPE, "");
        CommonUtils.setTitleBar(this, true, "设置支付密码", "");
        mEtPayPwd.addTextChangedListener(new MaxLengthWatcher(6, mEtPayPwd));//只能是6位数字
        mEtPayPwdConfirm.addTextChangedListener(new MaxLengthWatcher(6, mEtPayPwdConfirm));//只能是6位数字
        setTextWatcher();
    }

    private void setTextWatcher() {

        mEtPayPwd.addTextChangedListener(new VerifyButtonEnableWatcher(mContext, mTvConfirm) {
            @Override
            public boolean verifyCondition(CharSequence s) {
                return mEtPayPwdConfirm.getText().toString().trim().length() == 6 && s.length() >= 6;
            }
        });

        mEtPayPwdConfirm.addTextChangedListener(new VerifyButtonEnableWatcher(mContext, mTvConfirm) {
            @Override
            public boolean verifyCondition(CharSequence s) {
                return mEtPayPwd.getText().toString().trim().length() == 6 && s.length() >= 6;
            }
        });
    }

    @Override
    public void requestSuccess(int requestTag, JSONObject response, int showLoad) throws JSONException {
        super.requestSuccess(requestTag, response, showLoad);
        switch (requestTag) {
            case RequestAction.TAG_SETACCOUNTPAYPWD:
                L.e("设置支付密码：", response + "");
                MToast.showToast(mContext, "支付密码设置成功");
                SharedPreferencesUtils.setString(mContext, ConstantUtils.SP_ACCOUNTPAYPWDSTATE, "已设置");
                if (fromFlag.equals("login")) {
                    startActivity(new Intent(SetPayPwdActivity.this, MainActivity.class));
                    finish();
                    LoginActivity.loginActivity.finish();
                    SelectLoginTypeActivity.selectLoginTypeActivity.finish();
                } else if (fromFlag.equals("forget")) {
                    startActivity(new Intent(SetPayPwdActivity.this, MainActivity.class));
                    finish();
//                    ForgetPwd2Activity.forgetPwd2Activity.finish();
//                    ForgetPwd1Activity.forgetPwd1Activity.finish();
                    ForgetLoginPwd2Activity.forgetLoginPwd2Activity.finish();
                    ForgetLoginPwd1Activity.forgetLoginPwd1Activity.finish();
                    LoginActivity.loginActivity.finish();
                    SelectLoginTypeActivity.selectLoginTypeActivity.finish();
                }

                break;
        }
    }

    private boolean verifyData() {
        newPwd = mEtPayPwd.getText().toString().trim();
        confirmPwd = mEtPayPwdConfirm.getText().toString().trim();

        if (TextUtils.isEmpty(newPwd)) {
            MToast.showToast(mContext, "支付密码不能为空");
            return false;
        } else if (newPwd.length() < 6) {
            MToast.showToast(mContext, "支付密码不能少于六位");
            return false;
        } else if (TextUtils.isEmpty(confirmPwd)) {
            MToast.showToast(mContext, "确认密码不能为空");
            return false;
        } else if (!newPwd.equals(confirmPwd)) {
            MToast.showToast(mContext, "密码不一致，请重新输入");
            return false;
        } else {
            return true;
        }

    }

}
