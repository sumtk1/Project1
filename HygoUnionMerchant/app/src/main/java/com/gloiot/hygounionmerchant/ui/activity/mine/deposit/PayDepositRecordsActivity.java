package com.gloiot.hygounionmerchant.ui.activity.mine.deposit;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import com.zyd.wlwsdk.widge.pulltorefresh.PullToRefreshLayout;
import com.zyd.wlwsdk.widge.pulltorefresh.PullableListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * 保证金缴纳记录
 * Created by Dlt on 2018/1/5 11:07
 */
public class PayDepositRecordsActivity extends BaseActivity implements PullToRefreshLayout.OnRefreshListener, BaseActivity.RequestErrorCallback {

    @Bind(R.id.ll_no)
    LinearLayout mLlNo;
    @Bind(R.id.pullablelistview)
    PullableListView mListView;
    @Bind(R.id.pulltorefreshlayout)
    PullToRefreshLayout mPulltorefreshlayout;

    private String accountType = "";
    private List<String[]> list = new ArrayList<>();
    private CommonAdapter mCommonAdapter;
    private int page = 0;

    @Override
    public int initResource() {
        return R.layout.activity_pay_deposit_records;
    }

    @Override
    public void initData() {
        accountType = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTTYPE, "");
        CommonUtils.setTitleBar(this, true, "缴纳记录", "");
        mPulltorefreshlayout.setOnRefreshListener(this);
        request(0, 0, 1, 0);
        setRequestErrorCallback(this);
    }

    /**
     * @param requestType 请求类型，初始请求=0，刷新=1，加载=2
     * @param page        页数
     * @param requestTag  请求tag,请求成功后用于区分是哪个请求
     * @param showLoad    是否显示请求加载框 0--显示， -1--不显示
     */
    private void request(int requestType, int page, int requestTag, int showLoad) {
        requestHandleArrayList.add(requestAction.getPayTheDepositRecords(this, mPulltorefreshlayout, requestType, page, requestTag, showLoad, accountType));
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        if (!list.isEmpty()) {
            mCommonAdapter.notifyDataSetChanged();
        }
        list.clear();
        request(1, 0, 2, -1);
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
        if (page > 0) {
            request(2, page + 1, 3, -1);
        } else {
            MToast.showToast(mContext, "已无数据加载");
            pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.FAIL);
        }
    }

    private void setAdapter() {

        mCommonAdapter = new CommonAdapter<String[]>(PayDepositRecordsActivity.this, R.layout.item_pay_deposit_records, list) {
            @Override
            public void convert(final ViewHolder holder, String[] strings) {
                holder.setText(R.id.tv_explain, strings[3]);
                holder.setText(R.id.tv_time, strings[2]);
//                try {
//                    if (strings[1].substring(0, 1).equals("-")) {
//                        holder.setTextColor(R.id.tv_money, Color.parseColor("#ff8268"));
//                        holder.setText(R.id.tv_money, strings[1]);
//                    } else {
//                        holder.setTextColor(R.id.tv_money, Color.parseColor("#21d1c1"));
//                        holder.setText(R.id.tv_money, "+" + strings[1]);
//                    }
//                } catch (StringIndexOutOfBoundsException e) {
//                    e.printStackTrace();
//                }

                if (strings[4].equals("提现成功")) {
                    holder.setTextColor(R.id.tv_money, Color.parseColor("#21d1c1"));
//                    holder.setText(R.id.tv_money, "+" + strings[1]);
                    holder.setText(R.id.tv_money, strings[1]);
                } else if (strings[4].equals("提现失败")) {
                    holder.setTextColor(R.id.tv_money, Color.parseColor("#ff8268"));
//                    holder.setText(R.id.tv_money, "-" + strings[1]);
                    holder.setText(R.id.tv_money, strings[1]);
                }
            }
        };
        mListView.setAdapter(mCommonAdapter);
        mListView.setDivider(new ColorDrawable(Color.parseColor("#e5e5e5")));
        mListView.setDividerHeight(1);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(PayDepositRecordsActivity.this, PayDepositDetailActivity.class);
                intent.putExtra("id", list.get(position)[0]);
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
        L.e("保证金缴纳记录", response.toString());
        int num = Integer.parseInt(response.getString("条数"));
        if (num != 0) {
            JSONArray jsonArray = response.getJSONArray("列表");
            for (int i = 0; i < jsonArray.length(); i++) {
                String[] a = new String[5];
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                a[0] = jsonObject.getString("id");
                a[1] = jsonObject.getString("积分");
                a[2] = jsonObject.getString("提现时间");
                a[3] = jsonObject.getString("转入银行");
                a[4] = jsonObject.getString("提现状态");
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
                L.e("缴纳记录失败", response.toString());
                if (response.getString("状态").contains("无相关数据")) {
                    mLlNo.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

}
