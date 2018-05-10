package com.gloiot.hygounionmerchant.ui.activity.mine.bonus;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.gloiot.hygounionmerchant.R;
import com.gloiot.hygounionmerchant.ui.BaseActivity;
import com.gloiot.hygounionmerchant.utils.CommonUtils;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 提取到银行卡/支付宝完成
 * Created by Dlt on 2017/11/1 17:07
 */
public class ExtractFinishActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.tv_wait_message)
    TextView mTvWaitMessage;
    @Bind(R.id.tv_account_type)
    TextView mTvAccountType;
    @Bind(R.id.tv_bank_card_info)
    TextView mTvBankCardInfo;
    @Bind(R.id.tv_tixian_money)
    TextView mTvTixianMoney;
    @Bind(R.id.tv_service_charge)
    TextView mTvServiceCharge;
    @Bind(R.id.tv_finish)
    TextView mTvFinish;

    @Override
    public int initResource() {
        return R.layout.activity_extract_finish;
    }

    @Override
    public void initData() {
        Intent intent = getIntent();
        String type = intent.getStringExtra("type");//bankcard/alipay
        if (type.equals("bankcard")) {
            CommonUtils.setTitleBar(this, true, "提取到银行卡", "");
            mTvWaitMessage.setText("请等待工作人员审核（1~3天）");
            mTvAccountType.setText("银行卡");

            String bankName = intent.getStringExtra("bankName");
            String cardNum = intent.getStringExtra("cardNum");
            try {
                mTvBankCardInfo.setText(bankName + "  尾号" + cardNum.substring(cardNum.length() - 4, cardNum.length()));
            } catch (StringIndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        } else if (type.equals("alipay")) {
            CommonUtils.setTitleBar(this, true, "提取到支付宝", "");
            mTvWaitMessage.setText("请等待支付宝处理");
            mTvAccountType.setText("支付宝账号");
            String alipayAccount = intent.getStringExtra("alipayAccount");

            try {
                if (CommonUtils.isInteger(alipayAccount) && alipayAccount.length() == 11) {//十一位整数，默认是手机号
                    mTvBankCardInfo.setText(alipayAccount.substring(0, 3) + " **** " + alipayAccount.substring(alipayAccount.length() - 4, alipayAccount.length()));
                } else if (alipayAccount.contains("@") && alipayAccount.contains(".") && alipayAccount.indexOf("@") < alipayAccount.indexOf(".")) {//邮箱---@前三位的字符全部隐藏
                    int index = alipayAccount.indexOf("@");
                    mTvBankCardInfo.setText("****" + alipayAccount.substring(alipayAccount.length() - (index - 2), alipayAccount.length()));
                } else {//其他情况
                    mTvBankCardInfo.setText("****" + alipayAccount.substring(alipayAccount.length() - 3, alipayAccount.length()));
                }
            } catch (StringIndexOutOfBoundsException e) {
                e.printStackTrace();
            }

        }
        String extractBonus = intent.getStringExtra("extractBonus");
        String shouxufei = intent.getStringExtra("shouxufei");
        mTvTixianMoney.setText("￥" + extractBonus);
        mTvServiceCharge.setText("￥" + shouxufei);
    }

    @OnClick(R.id.tv_finish)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_finish:
                finish();
                break;
        }
    }

}
