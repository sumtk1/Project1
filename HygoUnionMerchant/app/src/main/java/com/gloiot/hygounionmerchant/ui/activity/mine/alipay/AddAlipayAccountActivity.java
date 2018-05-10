package com.gloiot.hygounionmerchant.ui.activity.mine.alipay;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.gloiot.hygounionmerchant.R;
import com.gloiot.hygounionmerchant.server.network.RequestAction;
import com.gloiot.hygounionmerchant.ui.BaseActivity;
import com.gloiot.hygounionmerchant.utils.CommonInputUtils;
import com.gloiot.hygounionmerchant.utils.CommonUtils;
import com.gloiot.hygounionmerchant.utils.ConstantUtils;
import com.gloiot.hygounionmerchant.utils.VerifyButtonEnableWatcher;
import com.zyd.wlwsdk.utils.L;
import com.zyd.wlwsdk.utils.MToast;
import com.zyd.wlwsdk.utils.SharedPreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 添加支付宝账号
 * Created by Dlt on 2017/12/21 16:43
 */
public class AddAlipayAccountActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.tv_alipay_name)
    TextView mTvAlipayName;
    @Bind(R.id.et_alipay_account)
    EditText mEtAlipayAccount;
    @Bind(R.id.tv_confirm)
    TextView mTvConfirm;

    private String accountType = "";

    @Override
    public int initResource() {
        return R.layout.activity_add_alipay_account;
    }

    @Override
    public void initData() {
        accountType = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTTYPE, "");
        CommonUtils.setTitleBar(this, true, "添加支付宝账号", "");
        setTextWatcher();
        CommonInputUtils.filterBlank(mEtAlipayAccount);//过滤空格
        requestHandleArrayList.add(requestAction.getPrincipalName(AddAlipayAccountActivity.this, accountType));
    }

    private void setTextWatcher() {
        mEtAlipayAccount.addTextChangedListener(new VerifyButtonEnableWatcher(mContext, mTvConfirm) {
            @Override
            public boolean verifyCondition(CharSequence s) {
                return mTvAlipayName.getText().toString().length() >= 1 && s.length() >= 1;
            }
        });

    }

    @OnClick(R.id.tv_confirm)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_confirm:
                String alipayAccount = mEtAlipayAccount.getText().toString();
                String alipayName = mTvAlipayName.getText().toString();
                requestHandleArrayList.add(requestAction.bindingAlipayAccount(AddAlipayAccountActivity.this, alipayAccount, alipayName, accountType));
                break;
        }
    }

    @Override
    public void requestSuccess(int requestTag, JSONObject response, int showLoad) throws JSONException {
        super.requestSuccess(requestTag, response, showLoad);
        switch (requestTag) {
            case RequestAction.TAG_QUERYPRINCIPALNAME:
                L.e("负责人姓名", response.toString());
                String principalName = response.getString("负责人姓名");
                mTvAlipayName.setText(principalName);
                break;
            case RequestAction.TAG_BINDINGALIPAYACCOUNT:
                L.e("绑定支付宝账号", response.toString());
                MToast.showToast(mContext, "添加成功");
                finish();
                break;
            default:
                break;
        }
    }

}
