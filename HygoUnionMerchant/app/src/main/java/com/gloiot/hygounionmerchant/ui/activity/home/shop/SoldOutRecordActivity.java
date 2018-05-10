package com.gloiot.hygounionmerchant.ui.activity.home.shop;

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

/**
 * 下架记录
 * Created by Dlt on 2017/8/19 16:14
 */
public class SoldOutRecordActivity extends BaseActivity implements PullToRefreshLayout.OnRefreshListener {

    @Bind(R.id.tv_listview_no)
    TextView mTvNoData;
    @Bind(R.id.pullablelistview)
    PullableListView mListView;
    @Bind(R.id.pulltorefreshlayout)
    PullToRefreshLayout mPulltorefreshlayout;

    private String accountType;
    private String commodityId;
    private List<String[]> list = new ArrayList<>();
    private CommonAdapter mCommonAdapter;
    private int page = 0;

    @Override
    public int initResource() {
        return R.layout.layout_common_mingxi_mt20;
    }

    @Override
    public void initData() {
        accountType = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTTYPE, "");
        CommonUtils.setTitleBar(this, true, "下架记录", "");

        Intent intent = getIntent();
        commodityId = intent.getStringExtra("commodityId");

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
        requestHandleArrayList.add(requestAction.getShopCommoditySoldOutList(this, commodityId, accountType,
                mPulltorefreshlayout, requestType, page, requestTag, showLoad));
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
        mCommonAdapter = new CommonAdapter<String[]>(SoldOutRecordActivity.this, R.layout.item_shop_commodity_sold_out, list) {
            @Override
            public void convert(final ViewHolder holder, String[] strings) {

                holder.setText(R.id.tv_name, strings[0]);
                holder.setText(R.id.tv_price, strings[1]);
                holder.setText(R.id.tv_amount, strings[4]);
                holder.setText(R.id.tv_time, "下架时间  " + strings[2]);
                holder.setText(R.id.tv_batch_num, "批次号  " + strings[3]);

            }
        };
        mListView.setAdapter(mCommonAdapter);
        mListView.setDivider(new ColorDrawable(Color.parseColor("#f3f3f3")));
        mListView.setDividerHeight(2);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(ExtractDetailActivity.this, WalletDetailActivity.class);
//                intent.putExtra("id", list.get(position)[6]);
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
        L.e("小铺商品下架记录", response.toString());
        int num = Integer.parseInt(response.getString("条数"));
        if (num != 0) {
            JSONArray jsonArray = response.getJSONArray("数据");
            for (int i = 0; i < jsonArray.length(); i++) {
                String[] a = new String[5];
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                a[0] = jsonObject.getString("商品名称");
                a[1] = jsonObject.getString("进货价");
                a[2] = jsonObject.getString("录入时间");
                a[3] = jsonObject.getString("批次号");
                a[4] = jsonObject.getString("数量");
                list.add(a);
            }
            page = num == 10 ? Integer.parseInt(response.getString("页数")) : 0;
            mListView.setVisibility(View.VISIBLE);
            mTvNoData.setVisibility(View.GONE);

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
                mTvNoData.setVisibility(View.VISIBLE);
                mTvNoData.setText("无数据");
            }
        }
    }

}
