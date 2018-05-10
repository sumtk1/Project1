package com.gloiot.hygounionmerchant.ui.activity.mine.deposit;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.gloiot.hygounionmerchant.R;
import com.gloiot.hygounionmerchant.server.network.RequestAction;
import com.gloiot.hygounionmerchant.ui.BaseActivity;
import com.gloiot.hygounionmerchant.ui.webview.WebActivity;
import com.gloiot.hygounionmerchant.utils.CommonUtils;
import com.gloiot.hygounionmerchant.utils.ConstantUtils;
import com.gloiot.hygounionmerchant.utils.DialogUtils;
import com.zyd.wlwsdk.utils.L;
import com.zyd.wlwsdk.utils.MD5Utlis;
import com.zyd.wlwsdk.utils.MToast;
import com.zyd.wlwsdk.utils.SharedPreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 缴纳保证金
 * Created by Dlt on 2018/1/2 14:25
 */
public class PayDepositActivity extends BaseActivity {

    @Bind(R.id.tv_daijiaona)
    TextView mTvDaijiaona;
    @Bind(R.id.tv_yijiaona)
    TextView mTvYijiaona;
    @Bind(R.id.tv_ketiqu)
    TextView mTvKetiqu;
    @Bind(R.id.et_pay_money)
    EditText mEtPayMoney;
    @Bind(R.id.cb_read_notice)
    CheckBox mCbReadNotice;
    @Bind(R.id.tv_go_to_pay)
    TextView mTvGoToPay;

    private String accountType = "";
    private String daijiaona = "", yijiaona = "", ketiqu = "", depositNoticeUrl = "";
    private String payMoney = "";

    @Override
    public int initResource() {
        return R.layout.activity_pay_deposit;
    }

    @Override
    public void initData() {
        accountType = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTTYPE, "");
        CommonUtils.setTitleBar(this, true, "缴纳信誉保证金", "");
        Intent intent = getIntent();
        daijiaona = intent.getStringExtra("daijiaona");
        yijiaona = intent.getStringExtra("yijiaona");
        ketiqu = intent.getStringExtra("ketiqu");
        depositNoticeUrl = intent.getStringExtra("url");
        mTvDaijiaona.setText(daijiaona);
        mTvYijiaona.setText("已缴纳" + "(￥" + yijiaona + "元）");
        mTvKetiqu.setText("(￥" + ketiqu + ")");
    }

    @OnClick({R.id.tv_notice_link, R.id.tv_go_to_pay, R.id.tv_pay_records})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_notice_link:
                Intent intent = new Intent(PayDepositActivity.this, WebActivity.class);
                intent.putExtra("url", depositNoticeUrl);
                startActivity(intent);
                break;
            case R.id.tv_go_to_pay:
                if (verifyData()) {
                    DialogUtils.oneBtnPwd(this, "¥" + payMoney, new DialogUtils.PasswordCallback() {
                        @Override
                        public void callback(String data) {
                            if (TextUtils.isEmpty(data) || data.length() < 6) {
                                MToast.showToast(mContext, "请输入支付密码");
                            } else {
                                requestHandleArrayList.add(requestAction.payTheDeposit(PayDepositActivity.this, payMoney,
                                        MD5Utlis.Md5(data), accountType));

                            }
                        }
                    });
                }
                break;
            case R.id.tv_pay_records:
                startActivity(new Intent(PayDepositActivity.this, PayDepositRecordsActivity.class));
                break;
        }
    }

    /**
     * 页面数据验证
     *
     * @return
     */
    private boolean verifyData() {
        payMoney = mEtPayMoney.getText().toString().trim();
        if (TextUtils.isEmpty(payMoney)) {
            MToast.showToast(mContext, "请输入要缴纳的金额");
            return false;
        } else if (!mCbReadNotice.isChecked()) {
            MToast.showToast(mContext, "请确认已认真阅读并同意《信誉保证金须知》");
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void requestSuccess(int requestTag, JSONObject response, int showLoad) throws JSONException {
        super.requestSuccess(requestTag, response, showLoad);
        switch (requestTag) {
            case RequestAction.TAG_PAYTHEDEPOSIT:
                L.e("缴纳保证金", response.toString());
                Intent intent = new Intent(PayDepositActivity.this, PayDepositSuccessActivity.class);
                intent.putExtra("notice", response.getString("文字说明"));
                intent.putExtra("account", response.getString("缴纳账户"));
                intent.putExtra("money", response.getString("缴纳金额"));
                intent.putExtra("time", response.getString("操作时间"));
                startActivity(intent);
                finish();
                break;
        }
    }

}
