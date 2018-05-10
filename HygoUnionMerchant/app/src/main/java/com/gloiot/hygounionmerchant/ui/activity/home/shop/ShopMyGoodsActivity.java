package com.gloiot.hygounionmerchant.ui.activity.home.shop;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.gloiot.hygounionmerchant.R;
import com.gloiot.hygounionmerchant.ui.BaseActivity;
import com.gloiot.hygounionmerchant.ui.adapter.ShopMyGoodsListAdapter;
import com.gloiot.hygounionmerchant.utils.CommonUtils;
import com.gloiot.hygounionmerchant.utils.ConstantUtils;
import com.gloiot.hygounionmerchant.widget.OnItemClickListener;
import com.gloiot.hygounionmerchant.widget.recyclerviewline.ListViewDecoration30;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.zyd.wlwsdk.utils.L;
import com.zyd.wlwsdk.utils.MToast;
import com.zyd.wlwsdk.utils.SharedPreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * 我的商品列表(用于服务类小铺)
 * Created by Dlt on 2017/8/19 15:49
 */
public class ShopMyGoodsActivity extends BaseActivity  {

    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.recycler_view)
    SwipeMenuRecyclerView mRecyclerView;
    @Bind(R.id.tv_nodata)
    TextView mTvNodata;

    private List<String[]> list = new ArrayList<>();
    private int page = 0;
    private ShopMyGoodsListAdapter mMenuAdapter;
    private String accountType;

    @Override
    protected void onResume() {
        super.onResume();
        list.clear();
        request(0, 0, 1, 0);
    }

    @Override
    public int initResource() {
        return R.layout.activity_shop_my_goods;
    }

    @Override
    public void initData() {
        accountType = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTTYPE, "");
        CommonUtils.setTitleBar(this, true, "我的商品", "");
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.blue_3192f6));

        initSwipeMenuRecyclerView();
        mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
    }

    private void initSwipeMenuRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));// 布局管理器。
        mRecyclerView.setHasFixedSize(true);// 如果Item够简单，高度是确定的，打开FixSize将提高性能。
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());// 设置Item默认动画，加也行，不加也行。
        mRecyclerView.addItemDecoration(new ListViewDecoration30(mContext));// 添加分割线。

        // 添加滚动监听。
        mRecyclerView.addOnScrollListener(mOnScrollListener);

    }

    /**
     * @param requestType 请求类型，初始请求=0，刷新=1，加载=2
     * @param page        页数
     * @param requestTag  请求tag,请求成功后用于区分是哪个请求
     * @param showLoad    是否显示请求加载框 0--显示， -1--不显示
     */
    private void request(int requestType, int page, int requestTag, int showLoad) {
        requestHandleArrayList.add(requestAction.shopGetMyGoodsList(this, requestType, page, requestTag, showLoad,accountType));
    }

    /**
     * 刷新监听。
     */
    private SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            if (!list.isEmpty()) {
                mMenuAdapter.notifyDataSetChanged();
            }
            list.clear();
            request(1, 0, 2, -1);
        }
    };

    /**
     * 加载更多
     */
    private RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (!recyclerView.canScrollVertically(1)) {// 手指不能向上滑动了
                // TODO 这里有个注意的地方，如果你刚进来时没有数据，但是设置了适配器，这个时候就会触发加载更多，需要开发者判断下是否有数据，如果有数据才去加载更多。

                if (list.size() != 0) {
                    if (page > 0) {
                        request(2, page + 1, 3, 0);
                    } else {
                        if (list.size() > 10) {
                            MToast.showToast(mContext, "已无数据加载");
                        }
                        L.e("加载更多执行", "page==0");
                    }
                }

            }
        }
    };

    private void processData() {
        mMenuAdapter = new ShopMyGoodsListAdapter(list);
        mMenuAdapter.setOnItemClickListener(onItemClickListener);
        mRecyclerView.setAdapter(mMenuAdapter);
    }

    /**
     * 条目点击监听
     */
    private OnItemClickListener onItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(int position) {
            Intent intent = new Intent(ShopMyGoodsActivity.this, ShopModifyGoodsActivity.class);
            intent.putExtra("id", list.get(position)[0]);
            intent.putExtra("picUrl", list.get(position)[2]);
            intent.putExtra("title", list.get(position)[1]);
            intent.putExtra("market", list.get(position)[4]);
            intent.putExtra("supply", list.get(position)[3]);
            startActivity(intent);
        }
    };

    @Override
    public void requestSuccess(int requestTag, JSONObject response, int showLoad) throws JSONException {
        super.requestSuccess(requestTag, response, showLoad);
        switch (requestTag) {

            case 1:
                L.e("服务类小铺我的商品列表", response.toString());
                processResponseData(response, false);
                break;
            case 2:
                mSwipeRefreshLayout.setRefreshing(false);
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
        L.e("服务类小铺我的商品列表", response.toString());

//        int num = Integer.parseInt(response.getString("条数"));
//        if (num != 0) {
//            JSONArray jsonArray = response.getJSONArray("商品列表");
//            for (int i = 0; i < jsonArray.length(); i++) {
//                String[] a = new String[8];
//                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
//                a[0] = jsonObject.getString("id");
//                a[1] = jsonObject.getString("商品名称");
//                a[2] = jsonObject.getString("商品图片");
//                a[3] = jsonObject.getString("供货价");
//                a[4] = jsonObject.getString("市场价");
//                a[5] = jsonObject.getString("录入时间");
//                a[6] = jsonObject.getString("让利价");
//                a[7] = jsonObject.getString("结算比");
//                list.add(a);
//            }
//            page = num == 10 ? Integer.parseInt(response.getString("页数")) : 0;
//            mRecyclerView.setVisibility(View.VISIBLE);
//            mTvNodata.setVisibility(View.GONE);
//
//            if (isLoadMore) {
//
//                mMenuAdapter.notifyDataSetChanged();
//            } else {
//                processData();
//            }
//
//        } else {
//
//            if (isLoadMore) {
//                MToast.showToast(mContext, "已无数据加载");
//
//            } else {
//                mRecyclerView.setVisibility(View.GONE);
//                mTvNodata.setVisibility(View.VISIBLE);
//                mTvNodata.setText("无数据");
//            }
//        }
    }

}
