package com.gloiot.hygounionmerchant.ui.activity.mine.bonus;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gloiot.hygounionmerchant.R;
import com.gloiot.hygounionmerchant.ui.BaseActivity;
import com.gloiot.hygounionmerchant.utils.CommonUtils;

import butterknife.Bind;

/**
 * 提取记录详情
 * Created by Dlt on 2017/11/2 14:45
 */
public class ExtractRecordDetailActivity extends BaseActivity {

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

    private String currentState = "";

    @Override
    public int initResource() {
        return R.layout.activity_extract_record_detail;
    }

    @Override
    public void initData() {
        CommonUtils.setTitleBar(this, true, "记录详情", "");
        Intent intent = getIntent();
        String state = intent.getStringExtra("state");
        String money = intent.getStringExtra("money");
        String where = intent.getStringExtra("where");
        String type = intent.getStringExtra("type");
        String shouxufei = intent.getStringExtra("shouxufei");
        String time = intent.getStringExtra("time");
        String ordernum = intent.getStringExtra("ordernum");
        String beizhu = intent.getStringExtra("beizhu");

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
            mTvFailReason.setText(beizhu);
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

    }

}
