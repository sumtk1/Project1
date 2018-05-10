package com.gloiot.hygounionmerchant.ui.activity.mine.deposit;

import android.widget.ImageView;
import android.widget.TextView;

import com.gloiot.hygounionmerchant.R;
import com.gloiot.hygounionmerchant.server.network.RequestAction;
import com.gloiot.hygounionmerchant.ui.BaseActivity;
import com.gloiot.hygounionmerchant.utils.CommonUtils;
import com.gloiot.hygounionmerchant.utils.ConstantUtils;
import com.zyd.wlwsdk.utils.L;
import com.zyd.wlwsdk.utils.SharedPreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;

/**
 * 保证金缴纳详情
 * Created by Dlt on 2018/1/5 15:43
 */
public class PayDepositDetailActivity extends BaseActivity {

    @Bind(R.id.iv_extract_state_logo)
    ImageView mIvExtractStateLogo;
    @Bind(R.id.tv_extract_state)
    TextView mTvExtractState;
    @Bind(R.id.tv_extract_money)
    TextView mTvExtractMoney;
    @Bind(R.id.tv_extract_time)
    TextView mTvExtractTime;
    @Bind(R.id.tv_explain)
    TextView mTvExplain;
    private String accountType = "";

    @Override
    public int initResource() {
        return R.layout.activity_pay_deposit_detail;
    }

    @Override
    public void initData() {
        accountType = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTTYPE, "");
//        CommonUtils.setTitleBar(this, true, "缴纳记录详情", "");
        CommonUtils.setTitleBar(this, true, "记录详情", "");
        requestHandleArrayList.add(requestAction.getPayTheDepositDetail(PayDepositDetailActivity.this,
                getIntent().getStringExtra("id"), accountType));
    }

    @Override
    public void requestSuccess(int requestTag, JSONObject response, int showLoad) throws JSONException {
        super.requestSuccess(requestTag, response, showLoad);
        switch (requestTag) {
            case RequestAction.TAG_PAYTHEDEPOSITDETAIL:
                L.e("缴纳详情", response.toString());
                String state = response.getString("提现状态");
                if (state.equals("成功")) {
                    mIvExtractStateLogo.setBackgroundResource(R.drawable.ic_tixianchenggong);
                    mTvExtractState.setTextColor(getResources().getColor(R.color.green_21d1c1));
                    mTvExtractMoney.setTextColor(getResources().getColor(R.color.black_333));
                } else if (state.equals("失败")) {
                    mIvExtractStateLogo.setBackgroundResource(R.drawable.ic_failed);
                    mTvExtractState.setTextColor(getResources().getColor(R.color.red_ff7676));
                    mTvExtractMoney.setTextColor(getResources().getColor(R.color.gray_ccc));
                }
                mTvExtractState.setText(response.getString("标题"));
                mTvExtractMoney.setText(response.getString("金额"));
                mTvExtractTime.setText(response.getString("时间"));
                mTvExplain.setText(response.getString("说明"));
                break;
        }
    }
}
