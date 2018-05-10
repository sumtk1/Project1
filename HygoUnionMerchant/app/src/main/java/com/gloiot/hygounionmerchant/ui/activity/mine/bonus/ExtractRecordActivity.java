package com.gloiot.hygounionmerchant.ui.activity.mine.bonus;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.gloiot.hygounionmerchant.R;
import com.gloiot.hygounionmerchant.ui.BaseActivity;
import com.gloiot.hygounionmerchant.utils.CommonUtils;
import com.gloiot.hygounionmerchant.utils.ConstantUtils;
import com.zyd.wlwsdk.adapter.ViewHolder;
import com.zyd.wlwsdk.adapter.abslistview.CommonAdapter;
import com.zyd.wlwsdk.utils.L;
import com.zyd.wlwsdk.utils.MToast;
import com.zyd.wlwsdk.utils.SharedPreferencesUtils;
import com.zyd.wlwsdk.widge.MyDialogBuilder;
import com.zyd.wlwsdk.widge.pulltorefresh.PullToRefreshLayout;
import com.zyd.wlwsdk.widge.pulltorefresh.PullableListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 提取记录
 * Created by Dlt on 2017/11/2 11:13
 */
public class ExtractRecordActivity extends BaseActivity implements View.OnClickListener, PullToRefreshLayout.OnRefreshListener, BaseActivity.RequestErrorCallback {

    @Bind(R.id.ll_no)
    LinearLayout mLlNo;
    @Bind(R.id.pullablelistview)
    PullableListView mListView;
    @Bind(R.id.pulltorefreshlayout)
    PullToRefreshLayout mPulltorefreshlayout;

    private String accountType;
    private List<String[]> list = new ArrayList<>();
    private CommonAdapter mCommonAdapter;
    private int page = 0;
    private String screenType = "";//筛选类型：取值--“”/“银行卡”/“红利”/“支付宝”

    @Override
    public int initResource() {
        return R.layout.activity_extract_record;
    }

    @Override
    public void initData() {
        accountType = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTTYPE, "");
        Intent intent = getIntent();
        String extractType = intent.getStringExtra("extractType");
        if (extractType.equals("")) {
            CommonUtils.setTitleBar(this, true, "提取记录", "筛选");
            screenType = "";
        } else if (extractType.equals("bankcard")) {
            CommonUtils.setTitleBar(this, true, "提取记录", "");
            screenType = "银行卡";
        } else if (extractType.equals("alipay")) {
            CommonUtils.setTitleBar(this, true, "提取记录", "");
            screenType = "支付宝";
        }
        mPulltorefreshlayout.setOnRefreshListener(this);
        request(0, 0, 1, 0, screenType);
        setRequestErrorCallback(this);
    }

    @OnClick(R.id.tv_toptitle_right)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_toptitle_right:
                String title = "提取类型筛选";
                String[] datas = {"全部", "提取到银行卡", "提取到环游购红利账户", "提取到支付宝"};
                final MyDialogBuilder myDialogBuilder = MyDialogBuilder.getInstance(mContext);
                myDialogBuilder.withTitie(TextUtils.isEmpty(title) ? "选择图片" : title)
                        .withEffects(MyDialogBuilder.SlideTop, MyDialogBuilder.SlideTopDismiss)
                        .setBtn1Bg(com.zyd.wlwsdk.R.drawable.btn_dialog_left)
                        .setBtn1Text("取消")
                        .setSingleChoice(mContext, datas, com.zyd.wlwsdk.R.layout.item_textcenter, new MyDialogBuilder.SingleChoiceConvert() {
                            @Override
                            public void convert(ViewHolder holder, String s) {
                                holder.setText(com.zyd.wlwsdk.R.id.tv_item_center, s);
                            }
                        }, new MyDialogBuilder.SingleChoiceOnItemClick() {
                            @Override
                            public void onItemClick(String data) {
                                switch (data) {
                                    case "全部":
                                        screenType = "";
                                        if (!list.isEmpty()) {
                                            list.clear();
                                            mCommonAdapter.notifyDataSetChanged();
                                        }
                                        page = 0;
                                        request(0, 0, 1, 0, screenType);
                                        break;
                                    case "提取到银行卡":
                                        screenType = "银行卡";
                                        if (!list.isEmpty()) {
                                            list.clear();
                                            mCommonAdapter.notifyDataSetChanged();
                                        }
                                        page = 0;
                                        request(0, 0, 1, 0, screenType);
                                        break;
                                    case "提取到环游购红利账户":
                                        screenType = "红利";
                                        if (!list.isEmpty()) {
                                            list.clear();
                                            mCommonAdapter.notifyDataSetChanged();
                                        }
                                        page = 0;
                                        request(0, 0, 1, 0, screenType);
                                        break;
                                    case "提取到支付宝":
                                        screenType = "支付宝";
                                        if (!list.isEmpty()) {
                                            list.clear();
                                            mCommonAdapter.notifyDataSetChanged();
                                        }
                                        page = 0;
                                        request(0, 0, 1, 0, screenType);
                                        break;
                                }
                            }
                        })
                        .setBtn1(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                myDialogBuilder.dismiss();
                            }
                        }).show();

                break;
        }
    }

    /**
     * @param requestType 请求类型，初始请求=0，刷新=1，加载=2
     * @param page        页数
     * @param requestTag  请求tag,请求成功后用于区分是哪个请求
     * @param showLoad    是否显示请求加载框 0--显示， -1--不显示
     * @param screenType  筛选类型：取值---""/银行卡/红利/支付宝
     */
    private void request(int requestType, int page, int requestTag, int showLoad, String screenType) {
        requestHandleArrayList.add(requestAction.getExtractDetail(this, mPulltorefreshlayout, requestType, page, requestTag, showLoad, accountType, screenType));
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        if (!list.isEmpty()) {
            mCommonAdapter.notifyDataSetChanged();
        }
        list.clear();
        request(1, 0, 2, -1, screenType);
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
        if (page > 0) {
            request(2, page + 1, 3, -1, screenType);
        } else {
            MToast.showToast(mContext, "已无数据加载");
            pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.FAIL);
        }
    }

    private void setAdapter() {
//        mCommonAdapter = new CommonAdapter<String[]>(ExtractDetailActivity.this, R.layout.item_wallet_detail, list) {
//        mCommonAdapter = new CommonAdapter<String[]>(ExtractRecordActivity.this, R.layout.item_extract_detail, list) {
        mCommonAdapter = new CommonAdapter<String[]>(ExtractRecordActivity.this, R.layout.item_extract_record, list) {
            @Override
            public void convert(final ViewHolder holder, String[] strings) {

//                if (strings[4].contains("银行卡")) {
//                    holder.setText(R.id.tv_account_name, strings[5]);
//                } else if (strings[4].contains("红利")) {
//                    holder.setText(R.id.tv_account_name, "环游购红利账户");
//                }

                holder.setText(R.id.tv_account_name, strings[5]);

                holder.setText(R.id.tv_time, strings[2]);
                holder.setText(R.id.tv_extract_money, strings[1]);

                if (strings[3].equals("提现成功")) {

//                    holder.setTextColor(R.id.tv_extract_money, Color.parseColor("#449ffb"));
                    holder.setTextColor(R.id.tv_extract_money, Color.parseColor("#21d1c1"));
                    holder.setText(R.id.tv_extract_state, "");
//                    holder.setTextColor(R.id.tv_extract_state, Color.parseColor("#449ffb"));
                    holder.setTextColor(R.id.tv_extract_state, Color.parseColor("#21d1c1"));
                    holder.setBackgroundRes(R.id.tv_extract_state, R.drawable.bg_btn_white);

                } else if (strings[3].equals("发起提现") || strings[3].equals("审核通过") || strings[3].equals("处理中")) {

                    holder.setTextColor(R.id.tv_extract_money, Color.parseColor("#999999"));
                    holder.setText(R.id.tv_extract_state, "处理中");
                    holder.setTextColor(R.id.tv_extract_state, Color.parseColor("#999999"));
                    holder.setBackgroundRes(R.id.tv_extract_state, R.drawable.bg_shape_biankuang_gray_999_10dp);

                } else if (strings[3].equals("提现失败") || strings[3].equals("审核未通过")) {
                    holder.setTextColor(R.id.tv_extract_money, Color.parseColor("#ff8268"));
                    holder.setText(R.id.tv_extract_state, "提现失败");
                    holder.setTextColor(R.id.tv_extract_state, Color.parseColor("#ff8268"));
                    holder.setBackgroundRes(R.id.tv_extract_state, R.drawable.bg_shape_biankuang_red_ff7676_10dp);
                }

            }
        };
        mListView.setAdapter(mCommonAdapter);
        mListView.setDivider(new ColorDrawable(Color.parseColor("#e5e5e5")));
        mListView.setDividerHeight(2);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ExtractRecordActivity.this, ExtractRecordDetailActivity.class);
                intent.putExtra("state", list.get(position)[3]);
                intent.putExtra("money", list.get(position)[1]);
                intent.putExtra("where", list.get(position)[10]);
                intent.putExtra("type", list.get(position)[4]);
                intent.putExtra("shouxufei", list.get(position)[6]);
                intent.putExtra("time", list.get(position)[2]);
                intent.putExtra("ordernum", list.get(position)[7]);
                intent.putExtra("beizhu", list.get(position)[11]);
                startActivity(intent);
            }
        });

    }

    @Override
    public void requestSuccess(int requestTag, JSONObject response, int showLoad) throws JSONException {
        super.requestSuccess(requestTag, response, showLoad);
        switch (requestTag) {
            case 1:
                processResponseData(response, false);
                break;
            case 2:
                processResponseData(response, false);
                break;
            case 3:
                processResponseData(response, true);
                break;

        }

    }

    /**
     * 处理请求返回数据
     *
     * @param response
     * @param isLoadMore
     */
    private void processResponseData(JSONObject response, boolean isLoadMore) throws JSONException {
        L.e("积分提取记录", response.toString());
        int num = Integer.parseInt(response.getString("条数"));
        if (num != 0) {
            JSONArray jsonArray = response.getJSONArray("列表");
            for (int i = 0; i < jsonArray.length(); i++) {
                String[] a = new String[12];
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                a[0] = jsonObject.getString("id");
                a[1] = jsonObject.getString("积分");
                a[2] = jsonObject.getString("提现时间");
                a[3] = jsonObject.getString("提现状态");
                a[4] = jsonObject.getString("提取方式");
                a[5] = jsonObject.getString("转入银行");
                a[6] = jsonObject.getString("手续费");
                a[7] = jsonObject.getString("提现单号");
                a[8] = jsonObject.getString("收款人账号");
                a[9] = jsonObject.getString("转入银行卡号");
                a[10] = jsonObject.getString("提取去向");
                a[11] = jsonObject.getString("备注");

                list.add(a);
            }
            page = num == 10 ? Integer.parseInt(response.getString("页数")) : 0;
            mListView.setVisibility(View.VISIBLE);
            mLlNo.setVisibility(View.GONE);

            if (isLoadMore) {
                mPulltorefreshlayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                mCommonAdapter.notifyDataSetChanged();
            } else {
                setAdapter();
            }

        } else {

            if (isLoadMore) {
                MToast.showToast(mContext, "已无数据加载");
                mPulltorefreshlayout.loadmoreFinish(PullToRefreshLayout.FAIL);
            } else {
                mListView.setVisibility(View.GONE);
                mLlNo.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void requestErrorcallback(int requestTag, JSONObject response) throws Exception {
        switch (requestTag) {
            case 1:
            case 2:
            case 3:
                L.e("提取记录失败", response.toString());

                if (response.getString("状态").contains("无相关数据")) {
                    mLlNo.setVisibility(View.VISIBLE);
                }

                break;
        }
    }

}
