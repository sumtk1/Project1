package com.gloiot.hygounionmerchant.ui.activity.mine;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.gloiot.hygounionmerchant.R;
import com.gloiot.hygounionmerchant.server.network.RequestAction;
import com.gloiot.hygounionmerchant.ui.BaseActivity;
import com.gloiot.hygounionmerchant.utils.CommonInputUtils;
import com.gloiot.hygounionmerchant.utils.CommonUtils;
import com.gloiot.hygounionmerchant.utils.ConstantUtils;
import com.gloiot.hygounionmerchant.utils.MaxLengthWatcher;
import com.zyd.wlwsdk.utils.L;
import com.zyd.wlwsdk.utils.MToast;
import com.zyd.wlwsdk.utils.NoDoubleClickUtils;
import com.zyd.wlwsdk.utils.SharedPreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 绑定环游购账号
 * Created by Dlt on 2017/8/24 16:20
 */
public class BindingHygoAccountActivity extends BaseActivity implements View.OnClickListener, BaseActivity.RequestErrorCallback {

    @Bind(R.id.tv_query)
    TextView mTvQuery;
    @Bind(R.id.et_hygo_account)
    EditText mEtHygoAccount;
    @Bind(R.id.tv_name)
    TextView mTvName;
    @Bind(R.id.tv_phone_num)
    TextView mTvPhoneNum;
    @Bind(R.id.tv_explain)
    TextView mTvExplain;
    @Bind(R.id.tv_confirm)
    TextView mTvConfirm;

    private String accountType;
    private String hygoPhone, hygoName;

    @Override
    public int initResource() {
        return R.layout.activity_binding_hygo_account;
    }

    @Override
    public void initData() {
        accountType = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTTYPE, "");
        CommonUtils.setTitleBar(this, true, "绑定环游购账户", "");
        setTextWatcher();
        CommonInputUtils.filterBlank(mEtHygoAccount);//禁止输入空格
        mEtHygoAccount.addTextChangedListener(new MaxLengthWatcher(20, mEtHygoAccount));//账号最多16位(包含字母的多少位？放宽20位？)
        String explainText = "绑定环游购账号后，收益将提取到该环游购账号，没有环游购账号的请先下载环游购用户版APP进行注册，然后回来进行账号关联，不然无法提取积分。";
        mTvExplain.setText(explainText);
        setRequestErrorCallback(this);
    }

    private void setTextWatcher() {

//        mEtHygoAccount.addTextChangedListener(new VerifyButtonEnableWatcher(mContext, mTvConfirm) {
//            @Override
//            public boolean verifyCondition(CharSequence s) {
//                return s.length() > 7;
//            }
//        });

    }

    @OnClick({R.id.tv_name, R.id.tv_phone_num, R.id.tv_explain, R.id.tv_query, R.id.tv_confirm})
    @Override
    public void onClick(View v) {
        if (NoDoubleClickUtils.isDoubleClick()) return; // 防止连续点击
        switch (v.getId()) {
            case R.id.tv_name:
            case R.id.tv_phone_num:
            case R.id.tv_explain:
                String hygoAccount1 = mEtHygoAccount.getText().toString().trim();
                if (hygoAccount1.length() >= 8) {
                    requestHandleArrayList.add(requestAction.queryHygoAccountInfo(this, hygoAccount1, accountType));
                }
                break;
            case R.id.tv_query:
//                String hygoAccount1 = mEtHygoAccount.getText().toString().trim();
//                requestHandleArrayList.add(requestAction.queryHygoAccountInfo(this, hygoAccount1, accountType));
                break;
            case R.id.tv_confirm:
                String hygoAccount = mEtHygoAccount.getText().toString().trim();
                String phoneNum = mTvPhoneNum.getText().toString().trim();
                String name = mTvName.getText().toString().trim();
                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phoneNum)) {
                    MToast.showToast(mContext, "请先获取账号相关的姓名手机号等信息");
                } else {
                    requestHandleArrayList.add(requestAction.bindHygoAccount(this, hygoAccount, phoneNum, name, accountType));
                }
                break;
            default:

                break;
        }
    }

    @Override
    public void requestSuccess(int requestTag, JSONObject response, int showLoad) throws JSONException {
        super.requestSuccess(requestTag, response, showLoad);
        switch (requestTag) {
            case RequestAction.TAG_QUERYHYGOACCOUNTINFO:
                L.e("查询环游购账号", response.toString());
                hygoPhone = response.getString("手机号");
                hygoName = response.getString("真实名");
                mTvPhoneNum.setText(hygoPhone);
                mTvName.setText(hygoName);

                //使确认按钮可以点击
                mTvConfirm.setEnabled(true);
                mTvConfirm.setBackgroundResource(R.drawable.bg_btn_green_4dp);
                mTvConfirm.setTextColor(mContext.getResources().getColor(R.color.white));

                break;
            case RequestAction.TAG_BINDHYGOACCOUNT:
                L.e("绑定环游购账号", response.toString());
                MToast.showToast(mContext, "绑定环游购账户成功");
                String hygoAccount = mEtHygoAccount.getText().toString().trim();
                SharedPreferencesUtils.setString(mContext, ConstantUtils.SP_BINDINGHYGOSTATE, hygoAccount);
                finish();
                break;
        }
    }

    @Override
    public void requestErrorcallback(int requestTag, JSONObject response) throws Exception {
        switch (requestTag) {
            case RequestAction.TAG_BINDHYGOACCOUNT:
                L.e("绑定环游购账号失败返回结果：", "--" + response);
                MToast.showToast(mContext, response.getString("状态"));

                mEtHygoAccount.setText("");
                mTvPhoneNum.setText("");
                mTvName.setText("");

                mTvConfirm.setEnabled(false);
                mTvConfirm.setBackgroundResource(R.drawable.bg_green_disable_4dp);
                mTvConfirm.setTextColor(mContext.getResources().getColor(R.color.white));

                break;
            default:
                MToast.showToast(mContext, response.getString("状态"));
                break;
        }
    }
}
