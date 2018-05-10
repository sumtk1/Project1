package com.gloiot.hygounionmerchant.ui.activity.income.travelandhotel;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.gloiot.hygounionmerchant.R;
import com.gloiot.hygounionmerchant.ui.BaseActivity;
import com.gloiot.hygounionmerchant.utils.CommonUtils;
import com.gloiot.hygounionmerchant.utils.ConstantUtils;
import com.zyd.wlwsdk.adapter.ViewHolder;
import com.zyd.wlwsdk.adapter.abslistview.CommonAdapter;
import com.zyd.wlwsdk.utils.L;
import com.zyd.wlwsdk.utils.MToast;
import com.zyd.wlwsdk.utils.SharedPreferencesUtils;
import com.zyd.wlwsdk.utils.StatusBarUtil;
import com.zyd.wlwsdk.widge.pulltorefresh.PullToRefreshLayout;
import com.zyd.wlwsdk.widge.pulltorefresh.PullableListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * 酒店/旅游--统计--收益明细
 * Created by Dlt on 2017/8/24 10:36
 */
public class TravelAndHotelIncomeDetailActivity extends BaseActivity implements PullToRefreshLayout.OnRefreshListener {

    @Bind(R.id.tv_start_time)
    TextView mTvStartTime;
    @Bind(R.id.tv_end_time)
    TextView mTvEndTime;
    @Bind(R.id.tv_money_daozhang)
    TextView mTvMoneyDaozhang;
    @Bind(R.id.pullablelistview)
    PullableListView mPullablelistview;
    @Bind(R.id.pulltorefreshlayout)
    PullToRefreshLayout mPulltorefreshlayout;
    @Bind(R.id.tv_no_data)
    TextView mTvNoData;

    private String accountType;
    private String year, month;
    private String currentQueryType = "", currentQueryCondition = "";//当前查询类别，当前查询条件
    private List<String[]> list = new ArrayList<>();
    private CommonAdapter mCommonAdapter;
    private int page = 0;

    @Override
    public int initResource() {
        return R.layout.activity_travel_and_hotel_income_detail;
    }

    @Override
    public void initData() {
        StatusBarUtil.setColor(this, getResources().getColor(R.color.green_21d1c1), 0);
        accountType = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTTYPE, "");
        CommonUtils.setTitleBar(this, true, "收益明细（元）", "");

        Intent intent = getIntent();
//        year = intent.getStringExtra("year");
//        month = intent.getStringExtra("month");

        currentQueryType = intent.getStringExtra("currentQueryType");
        currentQueryCondition = intent.getStringExtra("currentQueryCondition");

        mPulltorefreshlayout.setOnRefreshListener(this);
        request(0, 0, 1, 0);
    }

    /**
     * @param requestType 请求类型，初始请求=0，刷新=1，加载=2
     * @param page        页数
     * @param requestTag  请求tag,请求成功后用于区分是哪个请求
     * @param showLoad    是否显示请求加载框 0--显示， -1--不显示
     */
    private void request(int requestType, int page, int requestTag, int showLoad) {
        requestHandleArrayList.add(requestAction.getTravelAndHotelMonthIncomeDetail(this, mPulltorefreshlayout,
                requestType, page, requestTag, showLoad, currentQueryType, currentQueryCondition, accountType));
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        if (!list.isEmpty()) {
            mCommonAdapter.notifyDataSetChanged();
        }
        list.clear();
        page = 0;
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

        mCommonAdapter = new CommonAdapter<String[]>(TravelAndHotelIncomeDetailActivity.this, R.layout.item_travel_and_hotel_income_detail, list) {
            @Override
            public void convert(final ViewHolder holder, String[] strings) {

                holder.setText(R.id.tv_oddNum, "交易单号：" + strings[0]);
                holder.setText(R.id.tv_time, strings[1]);
                holder.setText(R.id.tv_title, strings[3]);
                holder.setText(R.id.tv_money, strings[2]);

            }
        };
        mPullablelistview.setAdapter(mCommonAdapter);

        //设置分隔线
        mPullablelistview.setDivider(new ColorDrawable(Color.rgb(243, 243, 243)));//#F3F3F3
        mPullablelistview.setDividerHeight(16);

        mPullablelistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//                Intent intent = new Intent(SettlementDetailActivity.this, OrderDetailActivity.class);
//                intent.putExtra("orderId", list.get(position)[0]);
//                intent.putExtra("orderNum", list.get(position)[3]);
//                intent.putExtra("time", list.get(position)[4]);
//                intent.putExtra("jiaoyi", list.get(position)[1]);
//                intent.putExtra("daozhang", list.get(position)[2]);
//
//                startActivity(intent);
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
        L.e("酒店/旅游月份收益明细", response.toString());
        String monthTotalIncome = response.getString("本月收益到账总金额");
        String endTime = response.getString("最大日期");
        String startTime = response.getString("最小日期");

        mTvMoneyDaozhang.setText(monthTotalIncome);
        mTvStartTime.setText(startTime);
        mTvEndTime.setText(endTime);

        int num = Integer.parseInt(response.getString("条数"));

        if (num != 0) {
            JSONArray jsonArray = response.getJSONArray("收益列表");
            for (int i = 0; i < jsonArray.length(); i++) {
                String[] a = new String[5];
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
//                a[0] = jsonObject.getString("交易单号");
//                a[1] = jsonObject.getString("录入时间");
//                a[2] = jsonObject.getString("金额");
//                a[3] = jsonObject.getString("套餐名称");

                a[0] = jsonObject.getString("交易单号");
                a[1] = jsonObject.getString("录入时间");
                a[2] = jsonObject.getString("金额");
                a[3] = jsonObject.getString("套餐名称");
                a[4] = jsonObject.getString("id");
                list.add(a);
            }
            page = num == 10 ? Integer.parseInt(response.getString("页数")) : 0;
            mPullablelistview.setVisibility(View.VISIBLE);
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
                mPullablelistview.setVisibility(View.GONE);
                mTvNoData.setText("无数据");
            }
        }
    }

}
