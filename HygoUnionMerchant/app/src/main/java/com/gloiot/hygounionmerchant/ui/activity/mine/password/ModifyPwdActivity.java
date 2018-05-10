package com.gloiot.hygounionmerchant.ui.activity.mine.password;

import android.content.Intent;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.gloiot.hygounionmerchant.R;
import com.gloiot.hygounionmerchant.server.network.RequestAction;
import com.gloiot.hygounionmerchant.ui.App;
import com.gloiot.hygounionmerchant.ui.BaseActivity;
import com.gloiot.hygounionmerchant.ui.activity.login.SelectLoginTypeActivity;
import com.gloiot.hygounionmerchant.utils.CommonInputUtils;
import com.gloiot.hygounionmerchant.utils.CommonUtils;
import com.gloiot.hygounionmerchant.utils.ConstantUtils;
import com.gloiot.hygounionmerchant.utils.MaxLengthWatcher;
import com.zyd.wlwsdk.utils.L;
import com.zyd.wlwsdk.utils.MD5Utlis;
import com.zyd.wlwsdk.utils.MToast;
import com.zyd.wlwsdk.utils.SharedPreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 修改（登录/支付）密码
 * Created by Dlt on 2017/8/24 15:40
 */
public class ModifyPwdActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.et_pwd_original)
    EditText mEtPwdOriginal;
    @Bind(R.id.iv_eye_1)
    ImageView mIvEye1;
    @Bind(R.id.et_pwd_new)
    EditText mEtPwdNew;
    @Bind(R.id.iv_eye_2)
    ImageView mIvEye2;
    @Bind(R.id.et_pwd_new_confirm)
    EditText mEtPwdNewConfirm;
    @Bind(R.id.iv_eye_3)
    ImageView mIvEye3;
    @Bind(R.id.tv_confirm)
    TextView mTvConfirm;

    private String accountType;
    private String pwdType, originalPwd, newPwd, confirmPwd;
    private boolean isShowPwd = false, isShowPwd1 = false, isShowPwd2 = false, isShowPwd3 = false;

    @Override
    public int initResource() {
        return R.layout.activity_modify_pwd;
    }

    @Override
    public void initData() {
        accountType = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTTYPE, "");
        Intent intent = getIntent();
        pwdType = intent.getStringExtra("pwdType");
        if (pwdType.equals("login")) {
            CommonUtils.setTitleBar(this, true, "修改登录密码", "");

            mEtPwdOriginal.addTextChangedListener(new MaxLengthWatcher(16, mEtPwdOriginal));//最多16位
            mEtPwdOriginal.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
            mEtPwdOriginal.setHint("请输入原登录密码");
            mEtPwdNew.addTextChangedListener(new MaxLengthWatcher(16, mEtPwdNew));
            mEtPwdNew.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
            mEtPwdNew.setHint("请输入新登录密码(6-16位)");
            mEtPwdNewConfirm.addTextChangedListener(new MaxLengthWatcher(16, mEtPwdNewConfirm));
            mEtPwdNewConfirm.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);

            CommonInputUtils.filterBlank(mEtPwdOriginal);
            CommonInputUtils.filterBlank(mEtPwdNew);
            CommonInputUtils.filterBlank(mEtPwdNewConfirm);

        } else if (pwdType.equals("pay")) {
            CommonUtils.setTitleBar(this, true, "修改支付密码", "");

            mEtPwdOriginal.addTextChangedListener(new MaxLengthWatcher(6, mEtPwdOriginal));//6位数字
            mEtPwdOriginal.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD | InputType.TYPE_CLASS_NUMBER);
            mEtPwdOriginal.setHint("请输入原支付密码");
            mEtPwdNew.addTextChangedListener(new MaxLengthWatcher(6, mEtPwdNew));
            mEtPwdNew.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD | InputType.TYPE_CLASS_NUMBER);
            mEtPwdNew.setHint("请输入新支付密码(6位数字)");
            mEtPwdNewConfirm.addTextChangedListener(new MaxLengthWatcher(6, mEtPwdNewConfirm));
            mEtPwdNewConfirm.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD | InputType.TYPE_CLASS_NUMBER);
        }

        setTextWatcher();
    }

    private void setTextWatcher() {
        mEtPwdOriginal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    mIvEye1.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (mEtPwdNew.getText().toString().trim().length() >= 6 && mEtPwdNewConfirm.getText().toString().trim().length() >= 6 && s.length() >= 6) {
                    mTvConfirm.setEnabled(true);
                    mTvConfirm.setBackgroundResource(R.drawable.bg_btn_green_4dp);
                    mTvConfirm.setTextColor(getResources().getColor(R.color.white));
                    mTvConfirm.setOnClickListener(ModifyPwdActivity.this);
                } else {
                    mTvConfirm.setEnabled(false);
                    mTvConfirm.setBackgroundResource(R.drawable.bg_green_disable_4dp);
                    mTvConfirm.setTextColor(getResources().getColor(R.color.white));
                }


            }
        });

        mEtPwdNew.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    mIvEye2.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (mEtPwdOriginal.getText().toString().trim().length() >= 6 && mEtPwdNewConfirm.getText().toString().trim().length() >= 6 && s.length() >= 6) {
                    mTvConfirm.setEnabled(true);
                    mTvConfirm.setBackgroundResource(R.drawable.bg_btn_green_4dp);
                    mTvConfirm.setTextColor(getResources().getColor(R.color.white));
                    mTvConfirm.setOnClickListener(ModifyPwdActivity.this);
                } else {
                    mTvConfirm.setEnabled(false);
                    mTvConfirm.setBackgroundResource(R.drawable.bg_green_disable_4dp);
                    mTvConfirm.setTextColor(getResources().getColor(R.color.white));
                }


            }
        });

        mEtPwdNewConfirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    mIvEye3.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (mEtPwdOriginal.getText().toString().trim().length() >= 6 && mEtPwdNew.getText().toString().trim().length() >= 6 && s.length() >= 6) {
                    mTvConfirm.setEnabled(true);
                    mTvConfirm.setBackgroundResource(R.drawable.bg_btn_green_4dp);
                    mTvConfirm.setTextColor(getResources().getColor(R.color.white));
                    mTvConfirm.setOnClickListener(ModifyPwdActivity.this);
                } else {
                    mTvConfirm.setEnabled(false);
                    mTvConfirm.setBackgroundResource(R.drawable.bg_green_disable_4dp);
                    mTvConfirm.setTextColor(getResources().getColor(R.color.white));
                }

            }
        });
    }

    @OnClick({R.id.iv_eye_1, R.id.iv_eye_2, R.id.iv_eye_3, R.id.tv_confirm})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_eye_1:
                if (isShowPwd1) {
                    mIvEye1.setImageResource(R.drawable.ic_eye_close);
                    if (pwdType.equals("login")) {
                        mEtPwdOriginal.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);//设置密码不可见，如果只设置TYPE_TEXT_VARIATION_PASSWORD则无效
                    } else if (pwdType.equals("pay")) {
                        mEtPwdOriginal.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD | InputType.TYPE_CLASS_NUMBER);
                    }
                    mEtPwdOriginal.setSelection(mEtPwdOriginal.getText().toString().length());
                    isShowPwd1 = false;
                } else {
                    mIvEye1.setImageResource(R.drawable.ic_eye_open);
                    if (pwdType.equals("login")) {
                        mEtPwdOriginal.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);//设置密码可见
                    } else if (pwdType.equals("pay")) {
                        mEtPwdOriginal.setInputType(InputType.TYPE_CLASS_NUMBER);
                    }
                    mEtPwdOriginal.setSelection(mEtPwdOriginal.getText().toString().length());
                    isShowPwd1 = true;
                }
                break;
            case R.id.iv_eye_2:
                if (isShowPwd2) {
                    mIvEye2.setImageResource(R.drawable.ic_eye_close);
                    if (pwdType.equals("login")) {
                        mEtPwdNew.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
                    } else if (pwdType.equals("pay")) {
                        mEtPwdNew.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD | InputType.TYPE_CLASS_NUMBER);
                    }
                    mEtPwdNew.setSelection(mEtPwdNew.getText().toString().length());
                    isShowPwd2 = false;
                } else {
                    mIvEye2.setImageResource(R.drawable.ic_eye_open);
                    if (pwdType.equals("login")) {
                        mEtPwdNew.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);//设置密码可见
                    } else if (pwdType.equals("pay")) {
                        mEtPwdNew.setInputType(InputType.TYPE_CLASS_NUMBER);
                    }
                    mEtPwdNew.setSelection(mEtPwdNew.getText().toString().length());
                    isShowPwd2 = true;
                }
                break;
            case R.id.iv_eye_3:
                if (isShowPwd3) {
                    mIvEye3.setImageResource(R.drawable.ic_eye_close);
                    if (pwdType.equals("login")) {
                        mEtPwdNewConfirm.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
                    } else if (pwdType.equals("pay")) {
                        mEtPwdNewConfirm.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD | InputType.TYPE_CLASS_NUMBER);
                    }
                    mEtPwdNewConfirm.setSelection(mEtPwdNewConfirm.getText().toString().length());
                    isShowPwd3 = false;
                } else {
                    mIvEye3.setImageResource(R.drawable.ic_eye_open);
                    if (pwdType.equals("login")) {
                        mEtPwdNewConfirm.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);//设置密码可见
                    } else if (pwdType.equals("pay")) {
                        mEtPwdNewConfirm.setInputType(InputType.TYPE_CLASS_NUMBER);
                    }
                    mEtPwdNewConfirm.setSelection(mEtPwdNewConfirm.getText().toString().length());
                    isShowPwd3 = true;
                }
                break;
            case R.id.tv_confirm:
                if (verifyData()) {
                    L.e("密码", "yuan=" + originalPwd + ",new=" + newPwd + ",que=" + confirmPwd);
                    if (pwdType.equals("login")) {
                        requestHandleArrayList.add(requestAction.updateLoginOrPayPwd(ModifyPwdActivity.this,
                                "登录", MD5Utlis.Md5(originalPwd), MD5Utlis.Md5(newPwd), MD5Utlis.Md5(confirmPwd), accountType));
                    } else if (pwdType.equals("pay")) {
                        requestHandleArrayList.add(requestAction.updateLoginOrPayPwd(ModifyPwdActivity.this,
                                "支付", MD5Utlis.Md5(originalPwd), MD5Utlis.Md5(newPwd), MD5Utlis.Md5(confirmPwd), accountType));
                    }
                }
                break;
        }
    }

    @Override
    public void requestSuccess(int requestTag, JSONObject response, int showLoad) throws JSONException {
        super.requestSuccess(requestTag, response, showLoad);
        switch (requestTag) {
            case RequestAction.TAG_UPDATELOGINORPAYPWD://登录/支付密码修改成功

                if (pwdType.equals("login")) {
                    MToast.showToast(mContext, "登录密码修改成功");
                    SharedPreferencesUtils.setString(mContext, ConstantUtils.SP_LOGINSTATE, "失败");//登录状态
                    startActivity(new Intent(ModifyPwdActivity.this, SelectLoginTypeActivity.class));
                    finish();
                    App.getInstance().mActivityStack.AppExit();
                } else if (pwdType.equals("pay")) {
                    MToast.showToast(mContext, "支付密码修改成功");
                    finish();
                }

                break;
        }
    }

    private boolean verifyData() {
        originalPwd = mEtPwdOriginal.getText().toString().trim();
        newPwd = mEtPwdNew.getText().toString().trim();
        confirmPwd = mEtPwdNewConfirm.getText().toString().trim();

        if (TextUtils.isEmpty(originalPwd)) {
            MToast.showToast(mContext, "原密码不能为空");
            return false;
        } else if (originalPwd.length() < 6) {
            MToast.showToast(mContext, "原密码不能少于六位");
            return false;
        } else if (TextUtils.isEmpty(newPwd)) {
            MToast.showToast(mContext, "新密码不能为空");
            return false;
        } else if (newPwd.length() < 6) {
            MToast.showToast(mContext, "新密码不能少于六位");
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
