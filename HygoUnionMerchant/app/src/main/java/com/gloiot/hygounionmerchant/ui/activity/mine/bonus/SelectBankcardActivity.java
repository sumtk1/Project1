package com.gloiot.hygounionmerchant.ui.activity.mine.bonus;

import android.content.Intent;
import android.view.View;
import android.widget.ListView;

import com.gloiot.hygounionmerchant.R;
import com.gloiot.hygounionmerchant.server.network.RequestAction;
import com.gloiot.hygounionmerchant.ui.BaseActivity;
import com.gloiot.hygounionmerchant.utils.CommonUtils;
import com.gloiot.hygounionmerchant.utils.ConstantUtils;
import com.zyd.wlwsdk.adapter.ViewHolder;
import com.zyd.wlwsdk.adapter.abslistview.CommonAdapter;
import com.zyd.wlwsdk.utils.L;
import com.zyd.wlwsdk.utils.SharedPreferencesUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * 选择银行卡/支付宝账号
 * Created by Dlt on 2017/10/31 17:06
 */
public class SelectBankcardActivity extends BaseActivity {

    @Bind(R.id.list_view)
    ListView mListView;

    public static final int RESULT_SELECTBANKCARD = 8;
    private String accountType = "", type = "";
    private List<String[]> list = new ArrayList<String[]>();
    private CommonAdapter mBankCardAdapter;
    private String selectCardNum = "";

    @Override
    public int initResource() {
        return R.layout.activity_select_bankcard;
    }

    @Override
    public void initData() {
        accountType = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTTYPE, "");
        Intent intent = getIntent();
        type = intent.getStringExtra("type");
        if (type.equals("bankcard")) {
            selectCardNum = intent.getStringExtra("cardNum");
            CommonUtils.setTitleBar(this, true, "选择银行卡", "");
            requestHandleArrayList.add(requestAction.getMyBankcardList(SelectBankcardActivity.this, accountType));
        } else if (type.equals("alipay")) {
            selectCardNum = intent.getStringExtra("cardNum");
            CommonUtils.setTitleBar(this, true, "选择支付宝账号", "");
            requestHandleArrayList.add(requestAction.getAlipayAccountList(SelectBankcardActivity.this, accountType));
        }
    }

    /**
     * 处理银行卡数据
     */
    private void processData() {

        mBankCardAdapter = new CommonAdapter<String[]>(SelectBankcardActivity.this, R.layout.item_select_bankcard, list) {
            @Override
            public void convert(final ViewHolder holder, String[] strings) {
                if (strings[3].contains("中国银行")) {
                    holder.setBackgroundRes(R.id.iv_bank_logo, R.drawable.bank_china);
                } else if (strings[3].contains("建设银行")) {
                    holder.setBackgroundRes(R.id.iv_bank_logo, R.drawable.bank_zhongguojianshe);
                } else if (strings[3].contains("交通银行")) {
                    holder.setBackgroundRes(R.id.iv_bank_logo, R.drawable.bank_zhongguojiaotong);
                } else if (strings[3].contains("工商银行")) {
                    holder.setBackgroundRes(R.id.iv_bank_logo, R.drawable.bank_zhongguogongshang);
                } else if (strings[3].contains("民生银行")) {
                    holder.setBackgroundRes(R.id.iv_bank_logo, R.drawable.bank_zhongguominsheng);
                } else if (strings[3].contains("农业银行")) {
                    holder.setBackgroundRes(R.id.iv_bank_logo, R.drawable.bank_nongye);
                } else if (strings[3].contains("中信银行")) {
                    holder.setBackgroundRes(R.id.iv_bank_logo, R.drawable.bank_zhongxin);
                } else if (strings[3].contains("招商银行")) {
                    holder.setBackgroundRes(R.id.iv_bank_logo, R.drawable.bank_zhaoshang);
                } else if (strings[3].contains("华夏银行")) {
                    holder.setBackgroundRes(R.id.iv_bank_logo, R.drawable.bank_huaxia);
                } else if (strings[3].contains("光大银行")) {
                    holder.setBackgroundRes(R.id.iv_bank_logo, R.drawable.bank_guangda);
                } else if (strings[3].contains("浦发银行")) {
                    holder.setBackgroundRes(R.id.iv_bank_logo, R.drawable.bank_pufa);
                } else if (strings[3].contains("兴业银行")) {
                    holder.setBackgroundRes(R.id.iv_bank_logo, R.drawable.bank_xingye);
                } else if (strings[3].contains("北京银行")) {
                    holder.setBackgroundRes(R.id.iv_bank_logo, R.drawable.bank_beijing);
                } else if (strings[3].contains("广发银行")) {
                    holder.setBackgroundRes(R.id.iv_bank_logo, R.drawable.bank_guangfa);
                } else if (strings[3].contains("平安银行")) {
                    holder.setBackgroundRes(R.id.iv_bank_logo, R.drawable.bank_pingan);
                } else if (strings[3].contains("上海银行")) {
                    holder.setBackgroundRes(R.id.iv_bank_logo, R.drawable.bank_shanghai);
                } else if (strings[3].contains("邮储银行")) {
                    holder.setBackgroundRes(R.id.iv_bank_logo, R.drawable.bank_zhongguoyouzheng);
                } else {
                    holder.setBackgroundRes(R.id.iv_bank_logo, R.drawable.ic_bank_moren);
                }

                holder.setText(R.id.tv_bank_name, strings[3]);
                holder.setText(R.id.tv_card_type, "储蓄卡");

                try {
                    holder.setText(R.id.tv_card_num, "*** **** **** " + strings[2].substring(strings[2].length() - 4, strings[2].length()));
                } catch (StringIndexOutOfBoundsException e) {
                    e.printStackTrace();
                }

                if (strings[2].equals(selectCardNum)) {
                    holder.setVisible(R.id.iv_select_right, true);
                } else {
                    holder.setVisible(R.id.iv_select_right, false);
                }

                switch (holder.getmPosition() % 3) {
                    case 0:
                        holder.setBackgroundRes(R.id.rl_card_background, R.drawable.bg_shape_green_38d1c4_4dp);//不要用setBackgroundColor,会出错
                        break;
                    case 1:
                        holder.setBackgroundRes(R.id.rl_card_background, R.drawable.bg_shape_blue_66a6fa_4dp);
                        break;
                    case 2:
                        holder.setBackgroundRes(R.id.rl_card_background, R.drawable.bg_shape_orange_f2a55c_4dp);
                        break;
                    default:
                        break;
                }

                final int p = holder.getmPosition();//选中的位置

                holder.getView(R.id.rl_card_background).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        L.e("选中位置", "银行名称=" + list.get(p)[3]);
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("cardId", list.get(p)[0]);
                        resultIntent.putExtra("personName", list.get(p)[1]);
                        resultIntent.putExtra("cardNum", list.get(p)[2]);
                        resultIntent.putExtra("bankName", list.get(p)[3]);
                        SelectBankcardActivity.this.setResult(RESULT_SELECTBANKCARD, resultIntent);//结果码用于标识返回自哪个Activity
                        SelectBankcardActivity.this.finish();
                    }
                });

            }
        };
        mListView.setAdapter(mBankCardAdapter);

    }

    /**
     * 处理支付宝数据
     */
    private void processAlipayData() {
        mBankCardAdapter = new CommonAdapter<String[]>(SelectBankcardActivity.this, R.layout.item_select_alipay, list) {
            @Override
            public void convert(final ViewHolder holder, String[] strings) {
                holder.setText(R.id.tv_alipay_name, strings[2]);

                try {
                    if (CommonUtils.isInteger(strings[1]) && strings[1].length() == 11) {//十一位整数，默认是手机号
                        holder.setText(R.id.tv_alipay_account, strings[1].substring(0, 3) + " **** " + strings[1].substring(strings[1].length() - 4, strings[1].length()));
                    } else if (strings[1].contains("@") && strings[1].contains(".") && strings[1].indexOf("@") < strings[1].indexOf(".")) {//邮箱---@前三位的字符全部隐藏
                        int index = strings[1].indexOf("@");
                        holder.setText(R.id.tv_alipay_account, "****" + strings[1].substring(index - 3, strings[1].length()));
                    } else {//其他情况
                        holder.setText(R.id.tv_alipay_account, "****" + strings[1].substring(strings[1].length() - 3, strings[1].length()));
                    }
                } catch (StringIndexOutOfBoundsException e) {
                    e.printStackTrace();
                }

                if (strings[1].equals(selectCardNum)) {
                    holder.setVisible(R.id.iv_select_right, true);
                } else {
                    holder.setVisible(R.id.iv_select_right, false);
                }

                final int p = holder.getmPosition();//选中的位置

                holder.getView(R.id.rl_card_background).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        L.e("选中位置", "支付宝账号=" + list.get(p)[1]);
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("cardId", list.get(p)[0]);
                        resultIntent.putExtra("alipayAccount", list.get(p)[1]);
                        resultIntent.putExtra("alipayName", list.get(p)[2]);
                        SelectBankcardActivity.this.setResult(RESULT_SELECTBANKCARD, resultIntent);//结果码用于标识返回自哪个Activity
                        SelectBankcardActivity.this.finish();
                    }
                });

            }
        };
        mListView.setAdapter(mBankCardAdapter);
    }

    @Override
    public void requestSuccess(int requestTag, JSONObject response, int showLoad) throws JSONException {
        super.requestSuccess(requestTag, response, showLoad);
        switch (requestTag) {
            case RequestAction.TAG_MYBANKCARDLIST:
                L.e("我的已绑定银行卡", response.toString());
                int num = Integer.parseInt(response.getString("条数"));
                if (num != 0) {
                    JSONArray jsonArray = response.getJSONArray("数据");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                        String[] s = new String[5];
                        s[0] = jsonObject.getString("id");
                        s[1] = jsonObject.getString("持卡人姓名");
                        s[2] = jsonObject.getString("银行卡号");
                        s[3] = jsonObject.getString("银行名");
                        s[4] = jsonObject.getString("支行名称");
                        list.add(s);
                    }
                    processData();
                } else {
                    L.e("已绑定的银行卡", "为空");
                }
                break;
            case RequestAction.TAG_ALIPAYACCOUNTLIST:
                L.e("已绑定支付宝", response.toString());
                int num1 = Integer.parseInt(response.getString("条数"));
                if (num1 != 0) {
                    JSONArray jsonArray = response.getJSONArray("列表");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                        String[] s = new String[3];
                        s[0] = jsonObject.getString("id");
                        s[1] = jsonObject.getString("支付宝账号");
                        s[2] = jsonObject.getString("支付宝姓名");
                        list.add(s);
                    }
                    processAlipayData();
                } else {
                    L.e("已绑定的支付宝账号", "为空");
                }
                break;
            default:
                break;
        }
    }

}
