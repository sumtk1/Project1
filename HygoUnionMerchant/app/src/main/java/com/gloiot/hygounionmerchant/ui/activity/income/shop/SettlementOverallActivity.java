package com.gloiot.hygounionmerchant.ui.activity.income.shop;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.gloiot.hygounionmerchant.R;
import com.gloiot.hygounionmerchant.server.network.RequestAction;
import com.gloiot.hygounionmerchant.ui.BaseActivity;
import com.gloiot.hygounionmerchant.utils.CommonUtils;
import com.gloiot.hygounionmerchant.utils.ConstantUtils;
import com.zyd.wlwsdk.utils.L;
import com.zyd.wlwsdk.utils.MToast;
import com.zyd.wlwsdk.utils.SharedPreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.OnClick;

import static com.gloiot.hygounionmerchant.R.id.tv_settlementoverall_daozhang;
import static com.gloiot.hygounionmerchant.R.id.tv_settlementoverall_jiaoyi;
import static com.gloiot.hygounionmerchant.R.id.tv_settlementoverall_lastTime;
import static com.gloiot.hygounionmerchant.R.id.tv_settlementoverall_personName;
import static com.gloiot.hygounionmerchant.R.id.tv_settlementoverall_thisTime;
import static com.gloiot.hygounionmerchant.R.id.tv_settlementoverall_totalNums;

/**
 * 小铺--去结算
 * Created by Dlt on 2017/8/23 10:21
 */
public class SettlementOverallActivity extends BaseActivity implements View.OnClickListener {

    @Bind(tv_settlementoverall_jiaoyi)
    TextView mTvSettlementoverallJiaoyi;
    @Bind(tv_settlementoverall_daozhang)
    TextView mTvSettlementoverallDaozhang;
    @Bind(tv_settlementoverall_personName)
    TextView mTvSettlementoverallPersonName;
    @Bind(tv_settlementoverall_totalNums)
    TextView mTvSettlementoverallTotalNums;
    @Bind(tv_settlementoverall_lastTime)
    TextView mTvSettlementoverallLastTime;
    @Bind(tv_settlementoverall_thisTime)
    TextView mTvSettlementoverallThisTime;

    private String accountType;
    private String lastSettlementTime, thisTime, jiaoyi, daozhang, personName, totalNum;

    @OnClick({R.id.tv_settlementoverall_comfirm, R.id.tv_settlementoverall_cancle})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_settlementoverall_comfirm:
                requestHandleArrayList.add(requestAction.shopGotoSettle(this, lastSettlementTime, thisTime, jiaoyi, daozhang, accountType));
                break;
            case R.id.tv_settlementoverall_cancle:
                finish();
                break;
        }
    }

    @Override
    public int initResource() {
        return R.layout.activity_settlement_overall;
    }

    @Override
    public void initData() {
        accountType = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTTYPE, "");
        CommonUtils.setTitleBar(this, true, "结算", "");
        Intent intent = getIntent();
        lastSettlementTime = intent.getStringExtra("上次结算时间");
        requestHandleArrayList.add(requestAction.shopJiesuanDetail(SettlementOverallActivity.this, accountType));
    }

    @Override
    public void requestSuccess(int requestTag, JSONObject response, int showLoad) throws JSONException {
        super.requestSuccess(requestTag, response, showLoad);
        switch (requestTag) {
            case RequestAction.TAG_SHOPJIESUANDETAIL:
                L.e("结算细节", response.toString());

                lastSettlementTime = response.getString("上次结算时间");
                thisTime = response.getString("本次结算时间");
                jiaoyi = response.getString("实收金额");
                daozhang = response.getString("到账金额");
                personName = response.getString("结算人");
                totalNum = response.getString("累计条数");

                mTvSettlementoverallJiaoyi.setText("￥" + jiaoyi);
                mTvSettlementoverallDaozhang.setText("￥" + daozhang);
                mTvSettlementoverallPersonName.setText(personName);
                mTvSettlementoverallThisTime.setText(thisTime);
                mTvSettlementoverallLastTime.setText(lastSettlementTime);
                mTvSettlementoverallTotalNums.setText(totalNum + "笔");
                break;
            case RequestAction.TAG_SHOPGOSETTLE:
                MToast.showToast(mContext, "结算成功");
                finish();
                break;
        }
    }

}
