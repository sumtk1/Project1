package com.gloiot.hygounionmerchant.ui.activity.home.shop;

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
 * 小铺条码管理
 * Created by Dlt on 2017/8/19 10:18
 */
public class ShopBarCodeManagementActivity extends BaseActivity implements PullToRefreshLayout.OnRefreshListener {

    @Bind(R.id.pullablelistview)
    PullableListView mListView;
    @Bind(R.id.pulltorefreshlayout)
    PullToRefreshLayout mPulltorefreshlayout;
    @Bind(R.id.ll_no)
    LinearLayout mLlNo;

    private String accountType;
    private List<String[]> list = new ArrayList<>();
    private CommonAdapter mCommonAdapter;
    private int page = 0;

    @Override
    protected void onResume() {
        super.onResume();
        list.clear();
        request(0, 0, 1, 0);
    }

    @Override
    public int initResource() {
        return R.layout.activity_shop_bar_code_management;
    }

    @Override
    public void initData() {
        accountType = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTTYPE, "");
        CommonUtils.setTitleBar(this, true, "条码管理", "");
        mPulltorefreshlayout.setOnRefreshListener(this);
//        request(0, 0, 1, 0);
    }

    /**
     * @param requestType 请求类型，初始请求=0，刷新=1，加载=2
     * @param page        页数
     * @param requestTag  请求tag,请求成功后用于区分是哪个请求
     * @param showLoad    是否显示请求加载框 0--显示， -1--不显示
     */
    private void request(int requestType, int page, int requestTag, int showLoad) {
        requestHandleArrayList.add(requestAction.getBarCodeManagementList(this, accountType, mPulltorefreshlayout, requestType, page, requestTag, showLoad));
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
//        mCommonAdapter = new CommonAdapter<String[]>(ExtractDetailActivity.this, R.layout.item_wallet_detail, list) {
        mCommonAdapter = new CommonAdapter<String[]>(mContext, R.layout.item_shop_bar_code_management, list) {
            @Override
            public void convert(final ViewHolder holder, String[] strings) {
                holder.setText(R.id.tv_bar_code_num, strings[9]);
                holder.setText(R.id.tv_commodity_name, strings[0]);

            }
        };
        mListView.setAdapter(mCommonAdapter);
        mListView.setDivider(new ColorDrawable(Color.parseColor("#f3f3f3")));
        mListView.setDividerHeight(2);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ShopBarCodeManagementActivity.this, EditBarCodeActivity.class);
                intent.putExtra("id", list.get(position)[10]);
                intent.putExtra("picUrl", list.get(position)[1]);
                intent.putExtra("barCode", list.get(position)[9]);
                intent.putExtra("bigType", list.get(position)[6]);
                intent.putExtra("smallType", list.get(position)[7]);
                intent.putExtra("bigTypeId", list.get(position)[4]);
                intent.putExtra("smallTypeId", list.get(position)[5]);
                intent.putExtra("title", list.get(position)[0]);
                intent.putExtra("market", list.get(position)[3]);
                intent.putExtra("supply", list.get(position)[2]);
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
        L.e("小铺条码管理", response.toString());
        int num = Integer.parseInt(response.getString("条数"));
        if (num != 0) {
            JSONArray jsonArray = response.getJSONArray("数据");
            for (int i = 0; i < jsonArray.length(); i++) {
                String[] a = new String[11];
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                a[0] = jsonObject.getString("商品名称");
                a[1] = jsonObject.getString("商品图片");
                a[2] = jsonObject.getString("供货价");
                a[3] = jsonObject.getString("市场价");
                a[4] = jsonObject.getString("商品类别id");//大类
                a[5] = jsonObject.getString("商品种类id");//小类
                a[6] = jsonObject.getString("类别名称");
                a[7] = jsonObject.getString("种类名称");
                a[8] = jsonObject.getString("小铺id");
                a[9] = jsonObject.getString("条码编号");
                a[10] = jsonObject.getString("id");

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


}
