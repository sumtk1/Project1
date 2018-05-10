package com.gloiot.hygounionmerchant.ui.activity.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.gloiot.hygounionmerchant.R;
import com.gloiot.hygounionmerchant.server.network.RequestAction;
import com.gloiot.hygounionmerchant.ui.BaseActivity;
import com.gloiot.hygounionmerchant.utils.CommonUtils;
import com.gloiot.hygounionmerchant.utils.ConstantUtils;
import com.gloiot.hygounionmerchant.utils.MaxLengthWatcher;
import com.gloiot.hygounionmerchant.utils.VerifyButtonEnableWatcher;
import com.gloiot.hygounionmerchant.widget.TimeButton;
import com.zyd.wlwsdk.utils.L;
import com.zyd.wlwsdk.utils.MToast;
import com.zyd.wlwsdk.utils.NoDoubleClickUtils;
import com.zyd.wlwsdk.utils.SharedPreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 忘记(登录/支付)密码第一步
 * Created by Dlt on 2017/8/16 14:52
 */
public class ForgetPwd1Activity extends BaseActivity implements View.OnClickListener, BaseActivity.RequestErrorCallback {

    @Bind(R.id.et_phone_num)
    EditText mEtPhoneNum;
    @Bind(R.id.et_yzm)
    EditText mEtYzm;
    @Bind(R.id.tv_getyzm)
    TimeButton mTvGetyzm;
    @Bind(R.id.tv_next)
    TextView mTvNext;

    private String loginType, accountType;
    private String pwdType = "", phoneNum, yzm;
    public static Activity forgetPwd1Activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTvGetyzm.onCreate(savedInstanceState);
    }

    @Override
    public int initResource() {
        return R.layout.activity_forget_pwd1;
    }

    @Override
    public void initData() {
        forgetPwd1Activity = this;
        loginType = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_LOGINTYPE, "");
        accountType = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTTYPE, "");
        Intent intent = getIntent();
        pwdType = intent.getStringExtra("pwdType");
        try {
            if (pwdType.equals("login")) {
                CommonUtils.setTitleBar(this, true, "重设登录密码", "");
            } else if (pwdType.equals("pay")) {
                CommonUtils.setTitleBar(this, true, "重设支付密码", "");
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            L.e("pwdType", "空指针");
        }
        mTvGetyzm.setTextAfter("秒后重新获取").setTextBefore("获取验证码").setLenght(120 * 1000);

        mEtPhoneNum.addTextChangedListener(new MaxLengthWatcher(11, mEtPhoneNum));//手机号只能输入11位
        mEtYzm.addTextChangedListener(new MaxLengthWatcher(6, mEtYzm));//验证码最多6位
        setTextWatcher();
        setRequestErrorCallback(this);
    }

    private void setTextWatcher() {

        mEtPhoneNum.addTextChangedListener(new VerifyButtonEnableWatcher(mContext, mTvNext) {
            @Override
            public boolean verifyCondition(CharSequence s) {
                return mEtYzm.getText().toString().trim().length() >= 4 && s.length() >= 11;
            }
        });

        mEtYzm.addTextChangedListener(new VerifyButtonEnableWatcher(mContext, mTvNext) {
            @Override
            public boolean verifyCondition(CharSequence s) {
                return mEtPhoneNum.getText().toString().trim().length() >= 11 && s.length() >= 4;
            }
        });

    }

    @OnClick({R.id.tv_getyzm, R.id.tv_next})
    @Override
    public void onClick(View v) {
        if (NoDoubleClickUtils.isDoubleClick()) return;
        switch (v.getId()) {
            case R.id.tv_getyzm:
                phoneNum = mEtPhoneNum.getText().toString().trim();
                if (TextUtils.isEmpty(phoneNum)) {
                    MToast.showToast(mContext, "请输入手机号");
                } else if (phoneNum.length() != 11) {
                    MToast.showToast(mContext, "您输入的手机号有误，请重新输入");
                } else {
                    mTvGetyzm.setCondition(true);
                    if (pwdType.equals("login")) {
//                        requestHandleArrayList.add(requestAction.sendYZM(ForgetPwd1Activity.this, phoneNum, loginType));//20170925 改接口，这里用不到了。
                    } else if (pwdType.equals("pay")) {
                        requestHandleArrayList.add(requestAction.sendYzmWithAccount(ForgetPwd1Activity.this, phoneNum, accountType));
                    }

                }
                break;
            case R.id.tv_next:
                phoneNum = mEtPhoneNum.getText().toString().trim();
                yzm = mEtYzm.getText().toString().trim();
                if (pwdType.equals("login")) {
                    requestHandleArrayList.add(requestAction.prepareResetLoginPwd(ForgetPwd1Activity.this, phoneNum, yzm, loginType));
                } else if (pwdType.equals("pay")) {
                    requestHandleArrayList.add(requestAction.prepareResetPayPwd(ForgetPwd1Activity.this, phoneNum, yzm, accountType));
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void requestSuccess(int requestTag, JSONObject response, int showLoad) throws JSONException {
        super.requestSuccess(requestTag, response, showLoad);
        Intent intent;
        switch (requestTag) {
            case RequestAction.TAG_SENDYZM:
                L.e("login验证码成功返回结果", response + "");
                break;
            case RequestAction.TAG_SENDYZMWITHACCOUNT:
                L.e("pay验证码成功返回结果：", response + "");
                break;
            case RequestAction.TAG_PREPARERESETLOGINPWD:
                L.e("重设登录密码1：", response + "");
                String flag = response.getString("flag");
                phoneNum = mEtPhoneNum.getText().toString().trim();
                intent = new Intent(this, ForgetPwd2Activity.class);
                intent.putExtra("pwdType", pwdType);
                intent.putExtra("phoneNum", phoneNum);
                intent.putExtra("flag", flag);
                startActivity(intent);
                break;
            case RequestAction.TAG_PREPARERESETPAYPWD:
                L.e("重设支付密码1：", response + "");
                String flag1 = response.getString("flag");
                phoneNum = mEtPhoneNum.getText().toString().trim();
                intent = new Intent(this, ForgetPwd2Activity.class);
                intent.putExtra("pwdType", pwdType);
                intent.putExtra("phoneNum", phoneNum);
                intent.putExtra("flag", flag1);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void requestErrorcallback(int requestTag, JSONObject response) throws Exception {
        switch (requestTag) {
            case RequestAction.TAG_SENDYZM:
                L.e("login验证码失败返回结果：", "--" + response);
//                MToast.showToast(mContext, response.getString("状态"));
                mTvGetyzm.resetState();//重置TimeButton
                break;
            case RequestAction.TAG_SENDYZMWITHACCOUNT:
                L.e("pay验证码失败返回结果：", response + "");
//                MToast.showToast(mContext, response.getString("状态"));
                mTvGetyzm.resetState();//重置TimeButton
                break;
            default:
//                MToast.showToast(mContext, response.getString("状态"));
                break;
        }
    }

    @Override
    protected void onDestroy() {
        mTvGetyzm.onDestroy();
        super.onDestroy();
    }

}
