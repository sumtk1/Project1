package com.gloiot.hygounionmerchant.ui.activity.income.shop;

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
import com.zyd.wlwsdk.widge.pulltorefresh.PullToRefreshLayout;
import com.zyd.wlwsdk.widge.pulltorefresh.PullableListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

import static com.gloiot.hygounionmerchant.R.id.tv_daozhang;
import static com.gloiot.hygounionmerchant.R.id.tv_jiaoyi;
import static com.gloiot.hygounionmerchant.R.id.tv_settlementdetail_endtime;
import static com.gloiot.hygounionmerchant.R.id.tv_settlementdetail_starttime;

/**
 * 小铺--结算明细
 * Created by Dlt on 2017/8/23 10:43
 */
public class SettlementDetailActivity extends BaseActivity implements View.OnClickListener, PullToRefreshLayout.OnRefreshListener {

    @Bind(R.id.tv_oddNum)
    TextView mTvOddNum;
    @Bind(tv_settlementdetail_starttime)
    TextView mTvSettlementdetailStarttime;
    @Bind(tv_settlementdetail_endtime)
    TextView mTvSettlementdetailEndtime;
    @Bind(tv_jiaoyi)
    TextView mTvJiaoyi;
    @Bind(tv_daozhang)
    TextView mTvDaozhang;
    @Bind(R.id.tv_totalNum)
    TextView mTvTotalNum;
    @Bind(R.id.pullablelistview)
    PullableListView mPullablelistview;
    @Bind(R.id.pulltorefreshlayout)
    PullToRefreshLayout mPulltorefreshlayout;

    private String accountType;
    private String settlementOddNum, name, start, end, totalNums, jiaoyi, daozhang;
    private List<String[]> list = new ArrayList<>();
    private CommonAdapter mCommonAdapter;
    private int page = 0;

    @Override
    public void onClick(View v) {

    }

    @Override
    public int initResource() {
        return R.layout.activity_settlement_detail;
    }

    @Override
    public void initData() {
        accountType = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTTYPE, "");
        CommonUtils.setTitleBar(this, true, "结算明细", "");

        Intent intent = getIntent();
        settlementOddNum = intent.getStringExtra("oddNum");
        start = intent.getStringExtra("start");
        end = intent.getStringExtra("end");
        jiaoyi = intent.getStringExtra("jiaoyi");
        daozhang = intent.getStringExtra("daozhang");

        mTvOddNum.setText(settlementOddNum);
        mTvJiaoyi.setText(jiaoyi);
        mTvDaozhang.setText(daozhang);
        mTvSettlementdetailStarttime.setText(start);
        mTvSettlementdetailEndtime.setText(end);

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
        requestHandleArrayList.add(requestAction.getShopSettlementItemDetail(this, mPulltorefreshlayout,
                requestType, page, requestTag, showLoad, settlementOddNum, accountType));
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

        mCommonAdapter = new CommonAdapter<String[]>(SettlementDetailActivity.this, R.layout.item_shop_settlementdetail, list) {
            @Override
            public void convert(final ViewHolder holder, String[] strings) {

                holder.setText(R.id.tv_oddNum, "交易单号" + strings[3]);
                holder.setText(R.id.tv_time, strings[4]);
                holder.setText(R.id.tv_jiaoyi, strings[1]);
                holder.setText(R.id.tv_daozhang, strings[2]);

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

                Intent intent = new Intent(SettlementDetailActivity.this, ShopOrderDetailActivity.class);
                intent.putExtra("orderId", list.get(position)[0]);
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
//            case RequestAction.TAG_SHOPGETPRINTERINFO:
//                L.e("获取小铺打印机", response.toString());
//
//                int num = Integer.parseInt(response.getString("条数"));
//                if (num != 0) {
//                    JSONArray jsonArray = response.getJSONArray("列表");
//                    for (int i = 0; i < jsonArray.length(); i++) {
//                        String[] a = new String[4];
//                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
//                        a[0] = jsonObject.getString("gps卡号");
//                        a[1] = jsonObject.getString("设备终端号");
//                        a[2] = jsonObject.getString("密钥");
//                        a[3] = jsonObject.getString("类别");
//
//                        shopPrinterList.add(a);
//                    }
//
//                    requestHandleArrayList.add(requestAction.printShopSettlementDetail(SettlementDetailActivity.this, settlementOddNum));
//
//                } else {
//                    MToast.showToast(mContext, "您还没有添加打印机");
//                }
//                isFirstRequestForPrinter = false;
//                break;
//            case RequestAction.TAG_SHOPSETTLEMENTPRINT://小铺结算打印
//                L.e("小铺打印结算单", response.toString() + "--");
//                break;

        }
    }

    /**
     * 处理请求返回数据
     *
     * @param response
     * @param isLoadMore
     */
    private void processResponseData(JSONObject response, boolean isLoadMore) throws JSONException {
        L.e("结算详细", response.toString());
        int num = Integer.parseInt(response.getString("条数"));
        totalNums = Integer.parseInt(response.getString("累计笔数")) + "";

        settlementOddNum = response.getString("结算单号");
        jiaoyi = response.getString("交易金额");
        daozhang = response.getString("到账金额");
        start = response.getString("开始时间");
        end = response.getString("结束时间");

        mTvOddNum.setText(settlementOddNum);
        mTvJiaoyi.setText(jiaoyi);
        mTvDaozhang.setText(daozhang);
        mTvSettlementdetailStarttime.setText(start);
        mTvSettlementdetailEndtime.setText(end);

        mTvTotalNum.setText(totalNums + "笔");
        if (num != 0) {
            JSONArray jsonArray = response.getJSONArray("订单列表");
            for (int i = 0; i < jsonArray.length(); i++) {
                String[] a = new String[6];
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                a[0] = jsonObject.getString("id");
                a[1] = jsonObject.getString("交易金额");
                a[2] = jsonObject.getString("到账金额");
                a[3] = jsonObject.getString("交易单号");
                a[4] = jsonObject.getString("录入时间");
                a[5] = jsonObject.getString("商品数量");
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
            }
        }
    }

}
