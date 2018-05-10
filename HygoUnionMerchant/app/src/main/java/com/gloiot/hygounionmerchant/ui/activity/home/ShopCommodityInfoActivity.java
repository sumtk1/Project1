package com.gloiot.hygounionmerchant.ui.activity.home;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.gloiot.hygounionmerchant.R;
import com.gloiot.hygounionmerchant.server.network.RequestAction;
import com.gloiot.hygounionmerchant.ui.BaseActivity;
import com.gloiot.hygounionmerchant.ui.activity.home.shop.SoldOutRecordActivity;
import com.gloiot.hygounionmerchant.utils.CommonUtils;
import com.gloiot.hygounionmerchant.utils.ConstantUtils;
import com.zyd.wlwsdk.adapter.ViewHolder;
import com.zyd.wlwsdk.adapter.abslistview.CommonAdapter;
import com.zyd.wlwsdk.utils.L;
import com.zyd.wlwsdk.utils.MToast;
import com.zyd.wlwsdk.utils.SharedPreferencesUtils;
import com.zyd.wlwsdk.widge.MyDialogBuilder;
import com.zyd.wlwsdk.widge.MyNewDialogBuilder;
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
 * 小铺商品信息
 * Created by Dlt on 2017/8/18 18:49
 */
public class ShopCommodityInfoActivity extends BaseActivity implements View.OnClickListener, PullToRefreshLayout.OnRefreshListener {

    @Bind(R.id.tv_listview_no)
    TextView mTvNoData;
    @Bind(R.id.pullablelistview)
    PullableListView mListView;
    @Bind(R.id.pulltorefreshlayout)
    PullToRefreshLayout mPulltorefreshlayout;

    private String accountType;
    private String commodityId;
    private List<String[]> list = new ArrayList<>();
    private int page = 0;
    private CommonAdapter mCommonAdapter;
    private int deletePosition = -1;//记录删除的位置，初始化为-1
    private String deleteId;//删除商品的Id
    private MyNewDialogBuilder myDialogBuilder;


    @OnClick(R.id.tv_toptitle_right)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_toptitle_right:
                Intent intent = new Intent(this, SoldOutRecordActivity.class);
                intent.putExtra("commodityId", commodityId);
                startActivity(intent);
                break;
        }
    }

    @Override
    public int initResource() {
        return R.layout.layout_common_mingxi_mt20;
    }

    @Override
    public void initData() {
        accountType = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTTYPE, "");
        CommonUtils.setTitleBar(this, true, "商品信息", "下架记录");
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
        requestHandleArrayList.add(requestAction.getShopCommodityInfoList(this, commodityId, accountType, mPulltorefreshlayout,
                requestType, page, requestTag, showLoad));
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
        mCommonAdapter = new CommonAdapter<String[]>(ShopCommodityInfoActivity.this, R.layout.item_shop_commdity_info, list) {
            @Override
            public void convert(final ViewHolder holder, String[] strings) {

                holder.setText(R.id.tv_name, strings[0]);
                holder.setText(R.id.tv_price, strings[2]);
                holder.setText(R.id.tv_amount, strings[5]);
                holder.setText(R.id.tv_time, "下架时间  " + strings[3]);
                holder.setText(R.id.tv_batch_num, "批次号  " + strings[4]);

                final int p = holder.getmPosition();//选中的位置
                TextView tvSoldOut = holder.getView(R.id.tv_soldout);

                tvSoldOut.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        myDialogBuilder = MyNewDialogBuilder.getInstance(mContext);
                        myDialogBuilder
                                .withTitie("下架商品")
                                .withCenterContent("确认下架此商品")
                                .withEffects(MyDialogBuilder.SlideTop, MyDialogBuilder.SlideTopDismiss)
                                .setTwoBtn("取消", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        myDialogBuilder.dismiss();
                                    }
                                }, "确认", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        myDialogBuilder.dismissNoAnimator();

                                        deleteId = list.get(p)[0];//商品id

                                        deletePosition = p;

                                        requestHandleArrayList.add(requestAction.shopSoldOutGoods(ShopCommodityInfoActivity.this, deleteId,
                                                list.get(p)[4], accountType));
                                    }
                                })
                                .show();

                    }
                });

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
            case RequestAction.TAG_SHOPCOMMODITYSOLDOUT:
                MToast.showToast(mContext, "商品下架成功");//然后需要刷新一遍数据,不需要再次调接口
                if (deletePosition != -1) {//为了保险，其实只要删除成功，这里肯定不等于-1
                    list.remove(deletePosition);

                    mCommonAdapter.notifyDataSetChanged();
                }
                deletePosition = -1;

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
        L.e("我的商品信息列表", response.toString());
        int num = Integer.parseInt(response.getString("条数"));
        if (num != 0) {
            JSONArray jsonArray = response.getJSONArray("数据");
            for (int i = 0; i < jsonArray.length(); i++) {
                String[] a = new String[6];
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                a[0] = jsonObject.getString("id");
                a[1] = jsonObject.getString("商品名称");
                a[2] = jsonObject.getString("进货价");
                a[3] = jsonObject.getString("录入时间");
                a[4] = jsonObject.getString("批次号");
                a[5] = jsonObject.getString("数量");
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
