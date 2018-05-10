package com.gloiot.hygounionmerchant.ui.activity.mine.bonus;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
 * 提取记录明细（从接口取数据而不是传值）
 * Created by Dlt on 2018/1/15 11:56
 */
public class NewExtractRecordDetailActivity extends BaseActivity {

    @Bind(R.id.iv_extract_state_logo)
    ImageView mIvExtractStateLogo;
    @Bind(R.id.tv_extract_state)
    TextView mTvExtractState;
    @Bind(R.id.tv_extract_money)
    TextView mTvExtractMoney;
    @Bind(R.id.tv_extract_to_where)
    TextView mTvExtractToWhere;
    @Bind(R.id.tv_extract_service_charge)
    TextView mTvExtractServiceCharge;
    @Bind(R.id.rl_extract_service_charge)
    RelativeLayout mRlExtractServiceCharge;
    @Bind(R.id.tv_extract_time)
    TextView mTvExtractTime;
    @Bind(R.id.tv_order_num)
    TextView mTvOrderNum;
    @Bind(R.id.rl_fail_reason)
    RelativeLayout mRlFailReason;
    @Bind(R.id.tv_fail_reason)
    TextView mTvFailReason;

    private String accountType = "";
    private String type = "";
    private String currentState = "";

    @Override
    public int initResource() {
        return R.layout.activity_new_extract_record_detail;
    }

    @Override
    public void initData() {
        accountType = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTTYPE, "");
        CommonUtils.setTitleBar(this, true, "记录详情", "");
        Intent intent = getIntent();
        type = intent.getStringExtra("type");
        String id = intent.getStringExtra("id");
        requestHandleArrayList.add(requestAction.getMyEarningsListDetail(this, type, id, accountType));
    }

    @Override
    public void requestSuccess(int requestTag, JSONObject response, int showLoad) throws JSONException {
        super.requestSuccess(requestTag, response, showLoad);
        switch (requestTag) {
            case RequestAction.TAG_MYEARNINGSLISTDETAIL:
                L.e("我的收益列表明细", response.toString());
                String money = response.getString("积分");
                String time = response.getString("提现时间");
                String state = response.getString("提现状态");
                String extractType = response.getString("提取方式");
                String shouxufei = response.getString("手续费");
                String ordernum = response.getString("提现单号");
                String beizhu = response.getString("备注");
                String failReason = response.getString("提交失败原因");
                String personAccount = response.getString("收款人账号");
                String where = response.getString("提取去向");

                if (type.equals("银行卡")) {
                    String bank = response.getString("转入银行");
                    String bankcardNum = response.getString("转入银行卡号");
                } else if (type.equals("支付宝")) {
                    String alipayAccount = response.getString("支付宝账号");
                    String alipayName = response.getString("支付宝姓名");
                }

                if (state.equals("提现成功")) {
                    currentState = "提现成功";
                    mIvExtractStateLogo.setBackgroundResource(R.drawable.ic_tixianchenggong);
                    mTvExtractState.setTextColor(getResources().getColor(R.color.green_21d1c1));
                    mTvExtractMoney.setTextColor(getResources().getColor(R.color.black_333));
                    mRlFailReason.setVisibility(View.GONE);
                } else if (state.equals("发起提现") || state.equals("审核通过") || state.equals("处理中")) {
                    currentState = "处理中";
                    mIvExtractStateLogo.setBackgroundResource(R.drawable.ic_wait);
                    mTvExtractState.setTextColor(getResources().getColor(R.color.green_21d1c1));
                    mTvExtractMoney.setTextColor(getResources().getColor(R.color.gray_ccc));
                    mRlFailReason.setVisibility(View.GONE);
                } else if (state.equals("提现失败") || state.equals("审核未通过")) {
                    currentState = "提现失败";
                    mIvExtractStateLogo.setBackgroundResource(R.drawable.ic_failed);
                    mTvExtractState.setTextColor(getResources().getColor(R.color.red_ff7676));
                    mTvExtractMoney.setTextColor(getResources().getColor(R.color.gray_ccc));
                    mRlFailReason.setVisibility(View.VISIBLE);
                    mTvFailReason.setText(failReason);
                }

                mTvExtractState.setText(currentState);
                mTvExtractMoney.setText(money);
                mTvExtractToWhere.setText(where);

                if ((type.contains("银行卡") && (currentState.equals("提现成功") || currentState.equals("处理中")))
                        || (type.contains("支付宝") && (currentState.equals("提现成功") || currentState.equals("处理中")))) {
                    mRlExtractServiceCharge.setVisibility(View.VISIBLE);
                    mTvExtractServiceCharge.setText("￥" + shouxufei);
                } else {
                    mRlExtractServiceCharge.setVisibility(View.GONE);
                }

                mTvExtractTime.setText(time);
                mTvOrderNum.setText(ordernum);
                break;

        }

    }

}
