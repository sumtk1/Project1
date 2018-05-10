package com.gloiot.hygounionmerchant.ui.activity.login;

import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.gloiot.chatsdk.DataBase.UnionDBManager;
import com.gloiot.chatsdk.socket.SocketListener;
import com.gloiot.hygounionmerchant.R;
import com.gloiot.hygounionmerchant.server.network.RequestAction;
import com.gloiot.hygounionmerchant.ui.BaseActivity;
import com.gloiot.hygounionmerchant.ui.activity.MainActivity;
import com.gloiot.hygounionmerchant.ui.activity.mine.password.SetPayPwdActivity;
import com.gloiot.hygounionmerchant.utils.CommonInputUtils;
import com.gloiot.hygounionmerchant.utils.CommonUtils;
import com.gloiot.hygounionmerchant.utils.ConstantUtils;
import com.gloiot.hygounionmerchant.utils.MaxLengthWatcher;
import com.gloiot.hygounionmerchant.utils.VerifyButtonEnableWatcher;
import com.zyd.wlwsdk.utils.L;
import com.zyd.wlwsdk.utils.MD5Utlis;
import com.zyd.wlwsdk.utils.NoDoubleClickUtils;
import com.zyd.wlwsdk.utils.SharedPreferencesUtils;
import com.zyd.wlwsdk.widge.MyDialogBuilder;
import com.zyd.wlwsdk.widge.MyNewDialogBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.et_account)
    EditText mEtAccount;
    @Bind(R.id.et_password)
    EditText mEtPassword;
    @Bind(R.id.iv_password_state)
    ImageView mIvPasswordState;
    @Bind(R.id.iv_password_clear)
    ImageView mIvPasswordClear;
    @Bind(R.id.tv_login)
    TextView mTvLogin;

    public static Activity loginActivity;

    private String loginType, mAccount, mPassword;
    private boolean isShowPwd = false;
    private MyNewDialogBuilder myDialogBuilder;

    @Override
    public int initResource() {
        return R.layout.activity_login;
    }

    @Override
    public void initData() {
        loginActivity = this;
        Intent intent = getIntent();
        loginType = intent.getStringExtra("loginType");
        String title = intent.getStringExtra("title");
        CommonUtils.setTitleBar(this, true, title, "");

//        if (title.contains("小铺")) {
//            loginType = "小铺";
//        } else if (title.contains("旅游")) {
//            loginType = "旅行社";
//        } else if (title.contains("酒店")) {
//            loginType = "酒店";
//        }
        setTextWatcher();
        CommonInputUtils.filterBlank(mEtAccount);//禁止输入空格
//        CommonInputUtils.filterBlank(mEtPassword);
        mEtPassword.addTextChangedListener(new MaxLengthWatcher(16, mEtPassword));//密码最多16位
    }

    private void setTextWatcher() {

        mEtAccount.addTextChangedListener(new VerifyButtonEnableWatcher(mContext, mTvLogin) {
            @Override
            public boolean verifyCondition(CharSequence s) {
                return mEtPassword.getText().toString().trim().length() >= 6 && s.length() >= 6;
            }
        });

        mEtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.length() > 0) {
                    mIvPasswordState.setVisibility(View.VISIBLE);
                    mIvPasswordClear.setVisibility(View.VISIBLE);
                } else {
                    mIvPasswordState.setVisibility(View.GONE);
                    mIvPasswordClear.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (mEtAccount.getText().toString().trim().length() >= 6 && s.length() >= 6) {
                    mTvLogin.setEnabled(true);
                    mTvLogin.setBackgroundResource(R.drawable.bg_btn_green_4dp);
                    mTvLogin.setTextColor(getResources().getColor(R.color.white));
                    mTvLogin.setOnClickListener(LoginActivity.this);
                } else {
                    mTvLogin.setEnabled(false);
                    mTvLogin.setBackgroundResource(R.drawable.bg_green_disable_4dp);
                    mTvLogin.setTextColor(getResources().getColor(R.color.white));
                }

            }
        });

    }

    @OnClick({R.id.iv_password_state, R.id.iv_password_clear, R.id.tv_forget_pwd, R.id.tv_login})
    @Override
    public void onClick(View v) {
        if (NoDoubleClickUtils.isDoubleClick()) return; // 防止连续点击
        switch (v.getId()) {
            case R.id.iv_password_state:
                if (isShowPwd) {
                    mIvPasswordState.setImageResource(R.drawable.ic_eye_close);
                    mEtPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);//设置密码不可见，如果只设置TYPE_TEXT_VARIATION_PASSWORD则无效

                    mEtPassword.setSelection(mEtPassword.getText().toString().length());
                    isShowPwd = false;
                } else {
                    mIvPasswordState.setImageResource(R.drawable.ic_eye_open);
                    mEtPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);//设置密码可见

                    mEtPassword.setSelection(mEtPassword.getText().toString().length());
                    isShowPwd = true;
                }
                break;
            case R.id.iv_password_clear:
                mEtPassword.setText("");
                break;
            case R.id.tv_forget_pwd:
//                Intent intent = new Intent(this, ForgetPwd1Activity.class);
//                intent.putExtra("pwdType", "login");
//                startActivity(intent);

                startActivity(new Intent(LoginActivity.this, ForgetLoginPwd1Activity.class));

//                startActivity(new Intent(LoginActivity.this, TestTimeButtonActivity.class));
//                startActivity(new Intent(LoginActivity.this, TestToolbarActivity.class));

                break;
            case R.id.tv_login:
                mAccount = mEtAccount.getText().toString().trim();
                mPassword = mEtPassword.getText().toString().trim();

                requestHandleArrayList.add(requestAction.userLogin(LoginActivity.this, mAccount, MD5Utlis.Md5(mPassword), loginType));
                break;
        }
    }

    @Override
    public void requestSuccess(int requestTag, JSONObject response, int showLoad) throws JSONException {
        super.requestSuccess(requestTag, response, showLoad);
        switch (requestTag) {
            case RequestAction.TAG_USERLOGIN:
                L.e("登录", response + "");
                String userAccount = response.getString("账号");
                String randomCode = response.getString("随机码");
                String loginState = response.getString("登录状态");
                String accountType = response.getString("商户类别");
                String payPwdState = response.getString("支付密码状态");

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("账号", userAccount);
                hashMap.put("随机码", randomCode);
                hashMap.putAll(CommonUtils.loadMap(ConstantUtils.SP_PHONEINFO_JSON));
                CommonUtils.saveMap(ConstantUtils.SP_REQUESTINFO_JSON, hashMap);

//                Set<Map.Entry<String, Object>> entry = hashMap.entrySet();
//                for (Map.Entry<String, Object> e : entry) {
//                    Log.e("SP_INFO_JSON", e.getKey() + "-" + e.getValue());
//                }

                SharedPreferencesUtils.setString(mContext, ConstantUtils.SP_RANDOMCODE, randomCode);//将随机码存储起来
                SharedPreferencesUtils.setString(mContext, ConstantUtils.SP_USERACCOUNT, userAccount);//用户账号
                SharedPreferencesUtils.setString(mContext, ConstantUtils.SP_LOGINSTATE, loginState);//登录状态
                SharedPreferencesUtils.setString(mContext, ConstantUtils.SP_ACCOUNTPAYPWDSTATE, payPwdState);//支付密码状态。取值：已设置/未设置
                SharedPreferencesUtils.setString(mContext, ConstantUtils.SP_ACCOUNTNICKNAME, response.getString("姓名"));//账号昵称
                SharedPreferencesUtils.setString(mContext, ConstantUtils.SP_ACCOUNTPORTRAIT, response.getString("头像"));//账号头像
                SharedPreferencesUtils.setString(mContext, ConstantUtils.SP_BINDINGHYGOSTATE, response.getString("绑定状态"));//账号绑定环游购状态(返回绑定的环游购账号)

                SharedPreferencesUtils.setString(mContext, ConstantUtils.SP_HYGOACCOUNT, response.getString("环游购账号"));//绑定的环游购账号
                SharedPreferencesUtils.setString(mContext, ConstantUtils.SP_IDENTITYTYPE, response.getString("身份类别"));//负责人/员工
                SharedPreferencesUtils.setString(mContext, ConstantUtils.SP_ACCOUNTNAME, response.getString("商户名称"));//账号名称（商户名称）

                SharedPreferencesUtils.setString(mContext, ConstantUtils.SP_ZIZHIRENZHENGSTATE, response.getString("资质认证状态"));//资质认证状态（取值：去认证/认证中/已认证）

                SharedPreferencesUtils.setString(mContext, ConstantUtils.SP_ACCOUNTTYPE, accountType);//账号（商户）类别。取值：小铺/旅行社/酒店
                if (accountType.equals("小铺")) {
                    SharedPreferencesUtils.setString(mContext, ConstantUtils.SP_SHOPTYPE, response.getString("小铺类别"));//小铺类别。取值：服务/商超
                    SharedPreferencesUtils.setString(mContext, ConstantUtils.SP_SHOPISNEEDTOCOMPLETECOMMODITYINFO, response.getString("信息补全"));//小铺类别。取值：服务/商超
                }

                if (payPwdState.equals("未设置")) {
                    myDialogBuilder = MyNewDialogBuilder.getInstance(mContext);
                    //            myDialogBuilder.setCancelable(false);//设置返回键不可点击 //20170824测试时先让其可以取消
                    myDialogBuilder
                            .withTitie("设置支付密码")
                            .withCenterContent("您还没有设置支付密码，设置后方可操作")
                            .withEffects(MyDialogBuilder.SlideTop, MyDialogBuilder.SlideTopDismiss)
                            .setOneBtn("去设置", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    myDialogBuilder.dismissNoAnimator();
                                    Intent intent = new Intent(LoginActivity.this, SetPayPwdActivity.class);
                                    intent.putExtra("from", "login");
                                    startActivity(intent);
                                }
                            })
                            .show();
                } else {
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    LoginActivity.this.finish();
                    SelectLoginTypeActivity.selectLoginTypeActivity.finish();
                }
                imDB(userAccount);
                imSocket(userAccount, randomCode);
                break;
        }
    }

    private void imDB(String account) {
        L.e("--imDB--", "account = " + account);
        UnionDBManager.getInstance(mContext, account);
    }

    // socket认证
    private void imSocket(String account, String random) {
        if (!TextUtils.isEmpty(account)) {
            SocketListener.getInstance().connectionRenZheng(account, random);
        }
    }

}
