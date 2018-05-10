package com.gloiot.hygounionmerchant.ui.activity.mine.deposit;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.gloiot.hygounionmerchant.R;
import com.gloiot.hygounionmerchant.ui.BaseActivity;
import com.gloiot.hygounionmerchant.utils.CommonUtils;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 缴纳保证金成功
 * Created by Dlt on 2018/1/5 10:02
 */
public class PayDepositSuccessActivity extends BaseActivity {

    @Bind(R.id.tv_notice)
    TextView mTvNotice;
    @Bind(R.id.tv_account)
    TextView mTvAccount;
    @Bind(R.id.tv_money)
    TextView mTvMoney;
    @Bind(R.id.tv_time)
    TextView mTvTime;

    @Override
    public int initResource() {
        return R.layout.activity_pay_deposit_success;
    }

    @Override
    public void initData() {
        CommonUtils.setTitleBar(this, true, "缴纳成功", "");
        Intent intent = getIntent();
        mTvNotice.setText(intent.getStringExtra("notice"));
        mTvAccount.setText(intent.getStringExtra("account"));
        mTvMoney.setText("￥" + intent.getStringExtra("money"));
        mTvTime.setText(intent.getStringExtra("time"));
    }

    @OnClick({R.id.tv_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_confirm:
                finish();
                break;
        }
    }

}
