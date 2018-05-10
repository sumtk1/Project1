package com.gloiot.hygounionmerchant.ui.activity.login;

import android.app.Activity;
import android.content.Intent;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

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
import com.zyd.wlwsdk.utils.MToast;
import com.zyd.wlwsdk.utils.NoDoubleClickUtils;
import com.zyd.wlwsdk.utils.SharedPreferencesUtils;
import com.zyd.wlwsdk.widge.MyDialogBuilder;
import com.zyd.wlwsdk.widge.MyNewDialogBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.OnClick;

import static com.zyd.wlwsdk.utils.MD5Utlis.Md5;

/**
 * 忘记密码第二步
 * Created by Dlt on 2017/8/16 15:18
 */
public class ForgetPwd2Activity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.et_new_pwd)
    EditText mEtNewPwd;
    @Bind(R.id.tv_confirm)
    TextView mTvConfirm;

    private String loginType, accountType;
    private String pwdType = "", phoneNum, newPwd;
    private String flag;
    private MyNewDialogBuilder myDialogBuilder;
    public static Activity forgetPwd2Activity;

    @Override
    public int initResource() {
        return R.layout.activity_forget_pwd2;
    }

    @Override
    public void initData() {
        forgetPwd2Activity = this;
        loginType = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_LOGINTYPE, "");
        accountType = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTTYPE, "");
        Intent intent = getIntent();
        pwdType = intent.getStringExtra("pwdType");
        phoneNum = intent.getStringExtra("phoneNum");
        flag = intent.getStringExtra("flag");
        try {
            if (pwdType.equals("login")) {

                CommonUtils.setTitleBar(this, true, "设置新登录密码", "");
                mEtNewPwd.addTextChangedListener(new MaxLengthWatcher(16, mEtNewPwd));//最多16位
//            mEtNewPwd.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);//设置密码不可见，如果只设置TYPE_TEXT_VARIATION_PASSWORD则无效
                mEtNewPwd.setInputType(InputType.TYPE_TEXT_VARIATION_NORMAL | InputType.TYPE_CLASS_TEXT);//20170830要求显示出来给用户看到密码？
                mEtNewPwd.setHint("请设置新登录密码（6-16位）");

                CommonInputUtils.filterBlank(mEtNewPwd);//过滤空格

            } else if (pwdType.equals("pay")) {
                CommonUtils.setTitleBar(this, true, "设置新支付密码", "");
                mEtNewPwd.addTextChangedListener(new MaxLengthWatcher(6, mEtNewPwd));//只能是6位数字
//            mEtNewPwd.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD | InputType.TYPE_CLASS_NUMBER);//正常的密码格式
                mEtNewPwd.setInputType(InputType.TYPE_NUMBER_VARIATION_NORMAL | InputType.TYPE_CLASS_NUMBER);//20170830要求显示出来给用户看到密码？
                mEtNewPwd.setHint("请设置新支付密码（6位数字）");
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            L.e("pwdType", "空指针");
        }

        setTextWatcher();

    }

    private void setTextWatcher() {

        mEtNewPwd.addTextChangedListener(new VerifyButtonEnableWatcher(mContext, mTvConfirm) {
            @Override
            public boolean verifyCondition(CharSequence s) {
                return s.length() >= 6;
            }
        });

    }

    @OnClick(R.id.tv_confirm)
    @Override
    public void onClick(View v) {
        if (NoDoubleClickUtils.isDoubleClick()) return;
        switch (v.getId()) {
            case R.id.tv_confirm:
                newPwd = Md5(mEtNewPwd.getText().toString().trim());
                if (pwdType.equals("login")) {
                    requestHandleArrayList.add(requestAction.resetLoginPwd(ForgetPwd2Activity.this, phoneNum, newPwd, flag, loginType));
                } else if (pwdType.equals("pay")) {
                    requestHandleArrayList.add(requestAction.resetPayPwd(ForgetPwd2Activity.this, newPwd, accountType));//20170828修改不用传入flag。
                }
                break;
        }
    }

    @Override
    public void requestSuccess(int requestTag, JSONObject response, int showLoad) throws JSONException {
        super.requestSuccess(requestTag, response, showLoad);
        switch (requestTag) {
            case RequestAction.TAG_RESETLOGINPWD:
                L.e("重设登录密码2：", response + "");
                String pwd = MD5Utlis.Md5(mEtNewPwd.getText().toString().trim());
                String account = response.getString("账号");
                String loginType = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_LOGINTYPE, "");
                requestHandleArrayList.add(requestAction.userLogin(ForgetPwd2Activity.this, account, pwd, loginType));
//                startActivity(new Intent(ForgetPwd2Activity.this, MainActivity.class));
                break;
            case RequestAction.TAG_RESETPAYPWD:
                L.e("重设支付密码2：", response + "");
                MToast.showToast(mContext, "设置支付密码成功");
                finish();
                ForgetPwd1Activity.forgetPwd1Activity.finish();

                break;

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

                SharedPreferencesUtils.setString(mContext, ConstantUtils.SP_RANDOMCODE, randomCode);//将随机码存储起来
                SharedPreferencesUtils.setString(mContext, ConstantUtils.SP_USERACCOUNT, userAccount);//用户账号
                SharedPreferencesUtils.setString(mContext, ConstantUtils.SP_LOGINSTATE, loginState);//登录状态
                SharedPreferencesUtils.setString(mContext, ConstantUtils.SP_ACCOUNTPAYPWDSTATE, payPwdState);//支付密码状态。取值：已设置/未设置
                SharedPreferencesUtils.setString(mContext, ConstantUtils.SP_ACCOUNTNICKNAME, response.getString("姓名"));//账号昵称
                SharedPreferencesUtils.setString(mContext, ConstantUtils.SP_ACCOUNTPORTRAIT, response.getString("头像"));//账号头像
                SharedPreferencesUtils.setString(mContext, ConstantUtils.SP_BINDINGHYGOSTATE, response.getString("绑定状态"));//账号绑定环游购状态
                SharedPreferencesUtils.setString(mContext, ConstantUtils.SP_ACCOUNTTYPE, accountType);//账号（商户）类别。取值：小铺/旅行社/酒店
                if (accountType.equals("小铺")) {
                    SharedPreferencesUtils.setString(mContext, ConstantUtils.SP_SHOPTYPE, response.getString("小铺类别"));//小铺类别。取值：服务/商超
                    SharedPreferencesUtils.setString(mContext, ConstantUtils.SP_SHOPISNEEDTOCOMPLETECOMMODITYINFO, response.getString("信息补全"));//小铺类别。取值：服务/商超
                }

                if (payPwdState.equals("未设置")) {
                    myDialogBuilder = MyNewDialogBuilder.getInstance(mContext);
//                    myDialogBuilder.setCancelable(false);//设置返回键不可点击 //20170824测试时先让其可以取消
                    myDialogBuilder
                            .withTitie("设置支付密码")
                            .withCenterContent("您还没有设置支付密码，设置后方可操作")
                            .withEffects(MyDialogBuilder.SlideTop, MyDialogBuilder.SlideTopDismiss)
                            .setOneBtn("去设置", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    myDialogBuilder.dismissNoAnimator();
                                    Intent intent = new Intent(ForgetPwd2Activity.this, SetPayPwdActivity.class);
                                    intent.putExtra("from", "forget");
                                    startActivity(intent);
                                }
                            })
                            .show();
                } else {
                    startActivity(new Intent(ForgetPwd2Activity.this, MainActivity.class));

                    finish();
                    ForgetPwd1Activity.forgetPwd1Activity.finish();
                    LoginActivity.loginActivity.finish();
                    SelectLoginTypeActivity.selectLoginTypeActivity.finish();
                }

                break;

        }
    }
}
