package com.gloiot.hygounionmerchant.ui.activity.mine.deposit;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.gloiot.hygounionmerchant.R;
import com.gloiot.hygounionmerchant.server.network.RequestAction;
import com.gloiot.hygounionmerchant.ui.BaseActivity;
import com.gloiot.hygounionmerchant.utils.CommonUtils;
import com.gloiot.hygounionmerchant.utils.ConstantUtils;
import com.gloiot.hygounionmerchant.widget.ListViewForScrollView;
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
import butterknife.OnClick;

/**
 * 保证金
 * Created by Dlt on 2018/1/3 14:15
 */
public class DepositActivity extends BaseActivity {

    @Bind(R.id.tv_toptitle_right)
    TextView mTvToptitleRight;
    @Bind(R.id.tv_money)
    TextView mTvMoney;
    @Bind(R.id.tv_pay_state)
    TextView mTvPayState;
    @Bind(R.id.tv_warm_prompt)
    TextView mTvWarmPrompt;
    @Bind(R.id.ll_message_uncomplete)
    LinearLayout mLlMessageUncomplete;
    @Bind(R.id.tv_message_complete)
    TextView mTvMessageComplete;
    @Bind(R.id.tv_remind)
    TextView mTvRemind;
    @Bind(R.id.list_view)
    ListViewForScrollView mListView;
    @Bind(R.id.tv_pay_records)
    TextView mTvPayRecords;
    @Bind(R.id.tv_go_to_pay)
    TextView mTvGoToPay;
    @Bind(R.id.scroll_view)
    ScrollView mScrollView;

    private String accountType = "";
    private List<String[]> list = new ArrayList<>();
    private CommonAdapter commonAdapter;
    private String daijiaona = "", yijiaona = "", ketiqu = "", depositNoticeUrl = "";

    @Override
    protected void onResume() {
        super.onResume();
        requestHandleArrayList.add(requestAction.getDepositDetail(DepositActivity.this, accountType));
    }

    @Override
    public int initResource() {
        return R.layout.activity_deposit;
    }

    @Override
    public void initData() {
        accountType = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTTYPE, "");
        CommonUtils.setTitleBar(this, true, "信誉保证金", "");
    }

    @OnClick({R.id.tv_toptitle_right, R.id.tv_pay_records, R.id.tv_go_to_pay})
    public void onViewClicked(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.tv_toptitle_right://缴纳记录
                startActivity(new Intent(DepositActivity.this, PayDepositRecordsActivity.class));
                break;
            case R.id.tv_pay_records://缴纳记录
                startActivity(new Intent(DepositActivity.this, PayDepositRecordsActivity.class));
                break;
            case R.id.tv_go_to_pay://去缴纳
                intent = new Intent(DepositActivity.this, PayDepositActivity.class);
                intent.putExtra("daijiaona", daijiaona);
                intent.putExtra("yijiaona", yijiaona);
                intent.putExtra("ketiqu", ketiqu);
                intent.putExtra("url", depositNoticeUrl);
                startActivity(intent);
                break;
        }
    }

    private void processData() {
        commonAdapter = new CommonAdapter<String[]>(DepositActivity.this, R.layout.item_deposit_privilege, list) {
            @Override
            public void convert(ViewHolder holder, String[] strings) {
                ImageView imageView = holder.getView(R.id.iv_image);
                CommonUtils.setDisplayImage(imageView, strings[2], 0, R.drawable.default_image);
                holder.setText(R.id.tv_title, strings[0]);
                holder.setText(R.id.tv_content, strings[1]);
            }
        };

        mListView.setAdapter(commonAdapter);
        mListView.setDivider(null);
        mScrollView.smoothScrollTo(0, 0);
    }

    @Override
    public void requestSuccess(int requestTag, JSONObject response, int showLoad) throws JSONException {
        super.requestSuccess(requestTag, response, showLoad);
        switch (requestTag) {
            case RequestAction.TAG_DEPOSITDETAIL:
                L.e("保证金信息", response.toString());
                String payState = response.getString("缴纳状态");//取值：未缴纳/缴纳中/已缴完
                String depositExplain = response.getString("保证金说明");
                String depositLimit = response.getString("保证金额度");
                ketiqu = response.getString("可提取收益");
                daijiaona = response.getString("待缴纳金额");
                yijiaona = response.getString("已缴纳金额");
                depositNoticeUrl = response.getString("保证金须知地址");
                String warmMessage = response.getString("主标题");

                if (payState.equals("未缴纳")) {
                    mTvGoToPay.setVisibility(View.VISIBLE);
//                    mTvPayRecords.setVisibility(View.GONE);
                    mTvToptitleRight.setText("");
                    mTvRemind.setVisibility(View.VISIBLE);
                    mTvRemind.setText(depositExplain);

                    mLlMessageUncomplete.setVisibility(View.VISIBLE);
                    mTvMessageComplete.setVisibility(View.GONE);

                    mTvMoney.setText(depositLimit);
                    mTvPayState.setText("需缴纳（元）");
                    mTvWarmPrompt.setText(warmMessage);

                } else if (payState.equals("缴纳中")) {
                    mTvGoToPay.setVisibility(View.VISIBLE);
//                    mTvPayRecords.setVisibility(View.GONE);
                    mTvToptitleRight.setText("");
                    mTvRemind.setVisibility(View.VISIBLE);
                    mTvRemind.setText(depositExplain);

                    mLlMessageUncomplete.setVisibility(View.VISIBLE);
                    mTvMessageComplete.setVisibility(View.GONE);

                    mTvMoney.setText(daijiaona);
                    mTvPayState.setText("待缴纳（元）");
                    mTvWarmPrompt.setText(warmMessage);

                } else if (payState.equals("已缴完")) {
                    mTvGoToPay.setVisibility(View.GONE);
//                    mTvPayRecords.setVisibility(View.VISIBLE);
                    mTvToptitleRight.setText("缴纳记录");
                    mTvRemind.setVisibility(View.GONE);

                    mLlMessageUncomplete.setVisibility(View.GONE);
                    mTvMessageComplete.setVisibility(View.VISIBLE);

                    mTvMessageComplete.setText(warmMessage);
                }

                if (!list.isEmpty()) {
                    list.clear();
                    commonAdapter.notifyDataSetChanged();
                }
                JSONArray jsonArray = response.getJSONArray("标题列表");
                int num = jsonArray.length();
                if (num != 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                        String[] s = new String[3];
                        s[0] = jsonObject.getString("标题");
                        s[1] = jsonObject.getString("副标题");
                        s[2] = jsonObject.getString("图片");
                        list.add(s);
                    }
                    processData();
                }
                break;
        }
    }

}
