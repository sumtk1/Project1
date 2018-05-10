package com.gloiot.hygounionmerchant.ui.activity.income.shop;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.gloiot.hygounionmerchant.R;
import com.gloiot.hygounionmerchant.ui.BaseActivity;
import com.gloiot.hygounionmerchant.ui.adapter.ShopTodayEarningAdapter;
import com.gloiot.hygounionmerchant.utils.CommonUtils;
import com.gloiot.hygounionmerchant.utils.ConstantUtils;
import com.gloiot.hygounionmerchant.widget.OnItemClickListener;
import com.gloiot.hygounionmerchant.widget.recyclerviewline.ListViewDecoration30;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.zyd.wlwsdk.utils.L;
import com.zyd.wlwsdk.utils.MToast;
import com.zyd.wlwsdk.utils.SharedPreferencesUtils;
import com.zyd.wlwsdk.utils.StatusBarUtil;
import com.zyd.wlwsdk.widge.MyDialogBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

import static com.gloiot.hygounionmerchant.R.id.tv_todayearning_daozhang;
import static com.gloiot.hygounionmerchant.R.id.tv_todayearning_jiaoyi;

/**
 * 小铺--今日收益
 * Created by Dlt on 2017/8/22 18:53
 */
public class TodayEarningActivity extends BaseActivity {

    @Bind(tv_todayearning_jiaoyi)
    TextView mTvTodayearningJiaoyi;
    @Bind(tv_todayearning_daozhang)
    TextView mTvTodayearningDaozhang;
    @Bind(R.id.recycler_view)
    SwipeMenuRecyclerView mRecyclerView;
    @Bind(R.id.swipe_layout)
    SwipeRefreshLayout mSwipeLayout;
    @Bind(R.id.tv_no_data)
    TextView mTvNoData;

    private String accountType;
    private List<String[]> list = new ArrayList<>();
    private String jiaoyiMoney, daozhangMoney;
    private MyDialogBuilder myDialogBuilder;
    private String deleteOddNum;//删除位置的交易单号
    private ShopTodayEarningAdapter mMenuAdapter;
    private int page = 0;

    @Override
    public int initResource() {
        return R.layout.activity_today_earning;
    }

    @Override
    public void initData() {
        accountType = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTTYPE, "");
        StatusBarUtil.setColor(this, getResources().getColor(R.color.green_21d1c1), 0);
        CommonUtils.setTitleBar(this, true, "今日收益", "");
        mSwipeLayout.setColorSchemeColors(getResources().getColor(R.color.green_21d1c1));
        initSwipeMenuRecyclerView();
//        setRequestErrorCallback(this);
        mSwipeLayout.setOnRefreshListener(mOnRefreshListener);
        request(0, 0, 1, 0);
    }

    private void initSwipeMenuRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));// 布局管理器。
        mRecyclerView.setHasFixedSize(true);// 如果Item够简单，高度是确定的，打开FixSize将提高性能。
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());// 设置Item默认动画，加也行，不加也行。
        mRecyclerView.addItemDecoration(new ListViewDecoration30(mContext));// 添加分割线。

        // 添加滚动监听。
        mRecyclerView.addOnScrollListener(mOnScrollListener);

        // 为SwipeRecyclerView的Item创建菜单
        // 设置菜单创建器。
//        mSwipeMenuRecyclerView.setSwipeMenuCreator(swipeMenuCreator);
        // 设置菜单Item点击监听。
//        mSwipeMenuRecyclerView.setSwipeMenuItemClickListener(menuItemClickListener);
    }

    /**
     * @param requestType 请求类型，初始请求=0，刷新=1，加载=2
     * @param page        页数
     * @param requestTag  请求tag,请求成功后用于区分是哪个请求
     * @param showLoad    是否显示请求加载框 0--显示， -1--不显示
     */
    private void request(int requestType, int page, int requestTag, int showLoad) {
        requestHandleArrayList.add(requestAction.shopTodayEarning(this, requestType, page, requestTag, showLoad, accountType));//20170724
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
        mMenuAdapter = new ShopTodayEarningAdapter(list);
        mMenuAdapter.setOnItemClickListener(onItemClickListener);
        mRecyclerView.setAdapter(mMenuAdapter);
    }

    /**
     * 条目点击监听
     */
    private OnItemClickListener onItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(int position) {
            Intent intent = new Intent(TodayEarningActivity.this, ShopOrderDetailActivity.class);
            intent.putExtra("orderId", list.get(position)[4]);
            startActivity(intent);
        }
    };

    /**
     * 菜单创建器。在Item要创建菜单的时候调用。
     */
//    private SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
//        @Override
//        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
//
//            // MATCH_PARENT 自适应高度，保持和内容一样高；也可以指定菜单具体高度，也可以用WRAP_CONTENT。
//            int height = ViewGroup.LayoutParams.MATCH_PARENT;
//
////            int width = getResources().getDimensionPixelSize(R.dimen.item_height);
//            int width = 200;//单位为px,没有适配性
//
//
//            // 添加右侧的，如果不添加，则右侧不会出现菜单。
//            {
//                SwipeMenuItem deleteItem = new SwipeMenuItem(mContext)
//                        .setBackgroundDrawable(R.color.red_FF6D63)
//                        .setText("撤单") // 文字，还可以设置文字颜色，大小等。。
//                        .setTextColor(Color.WHITE)
//                        .setWidth(width)
//                        .setHeight(height);
//                swipeRightMenu.addMenuItem(deleteItem);// 添加一个按钮到右侧侧菜单。
//
//            }
//        }
//    };

    /**
     * 菜单点击监听
     */
//    private OnSwipeMenuItemClickListener menuItemClickListener = new OnSwipeMenuItemClickListener() {
//        /**
//         * Item的菜单被点击的时候调用。
//         * @param closeable       closeable. 用来关闭菜单。
//         * @param adapterPosition adapterPosition. 这个菜单所在的item在Adapter中position。
//         * @param menuPosition    menuPosition. 这个菜单的position。比如你为某个Item创建了2个MenuItem，那么这个position可能是是 0、1，
//         * @param direction       如果是左侧菜单，值是：SwipeMenuRecyclerView#LEFT_DIRECTION，如果是右侧菜单，值是：SwipeMenuRecyclerView#RIGHT_DIRECTION.
//         */
//        @Override
//        public void onItemClick(Closeable closeable, int adapterPosition, int menuPosition, int direction) {
//            closeable.smoothCloseMenu();// 关闭被点击的菜单。
//
//            // TODO 如果是删除：推荐调用Adapter.notifyItemRemoved(position)，不推荐Adapter.notifyDataSetChanged();
//            if (menuPosition == 0) {// 撤单按钮被点击。
//                final int p = adapterPosition;
//                myDialogBuilder = MyDialogBuilder.getInstance(mContext);
//                myDialogBuilder.withIcon(R.mipmap.iconfont_gantanhao)
//                        .withContene("确定要撤单吗?")
//                        .withEffects(MyDialogBuilder.SlideTop, MyDialogBuilder.SlideTopDismiss)
//                        .setBtnClick("确定", MyDialogBuilder.BtnNormal, new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                myDialogBuilder.dismissNoAnimator();
//
//                                deleteOddNum = list.get(p)[0];//商品订单号
//
//                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                                Date curDate = new Date(System.currentTimeMillis());
//                                String currentTime = formatter.format(curDate);//获取手机系统时间是个漏洞，用户可手动更改时间。
//
////                                requestHandleArrayList.add(requestAction.rongyunRevoke(TodayearningActivity.this, deleteOddNum, currentTime));
//
//                            }
//                        })
//                        .setBtnClick("取消", MyDialogBuilder.BtnCancel, new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                myDialogBuilder.dismiss();
//                            }
//                        })
//                        .show();
//            }
//        }
//    };
    @Override
    public void requestSuccess(int requestTag, JSONObject response, int showLoad) throws JSONException {
        super.requestSuccess(requestTag, response, showLoad);
        switch (requestTag) {

            case 1:
                processResponseData(response, false);
                break;
            case 2:
                mSwipeLayout.setRefreshing(false);
                processResponseData(response, false);
                break;
            case 3:
                processResponseData(response, true);
                break;

//            case RequestAction.TAG_RONGYUNREVOKE:
//
////                log.e("融云撤单", "--" + response.toString());
//
//                myDialogBuilder = MyDialogBuilder.getInstance(mContext);
//                myDialogBuilder.setCancelable(false);
//                myDialogBuilder.withTitie("撤单")
//                        .withEffects(MyDialogBuilder.SlideTop, MyDialogBuilder.SlideTopDismiss)
//                        .setEtClick(1, null, "请输入客户收到的验证码", MyDialogBuilder.EtNum)//注意选数字格式 EtNum
//                        .setBtnClick("确定", MyDialogBuilder.BtnNormal, new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                EditText et_pwd = (EditText) myDialogBuilder.getDialogView().findViewById(100 + 1);
//                                String yzm = et_pwd.getText().toString().trim();
//                                if (TextUtils.isEmpty(yzm)) {
//                                    MToast.showToast(mContext, "请输入验证码");
//                                } else {
//                                    myDialogBuilder.dismissNoAnimator();
//
//                                    requestHandleArrayList.add(requestAction.revoke(Todayearning1Activity.this, deleteOddNum, yzm));
//
//                                }
//                            }
//                        })
//                        .setBtnClick("取消", MyDialogBuilder.BtnCancel, new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                myDialogBuilder.dismiss();
//                            }
//                        })
//                        .show();
//
//                break;
//            case RequestAction.TAG_REVOKE:
//                MToast.showToast(mContext, "撤单成功");//然后需要刷新一遍数据,不需要再次调接口。因为头部除列表外有数据同步更新，所以还是要重新请求。
////                if (deletePosition != -1) {//为了保险，其实只要删除成功，这里肯定不等于-1
////                    list.remove(deletePosition);
////                    processData(true);
////                }
////                deletePosition = -1;
//
////                list.clear();
////                requestHandleArrayList.add(requestAction.todayearning(this));
//
//
//                if (!list.isEmpty()) {
//                    mMenuAdapter.notifyDataSetChanged();
//                }
//                list.clear();
//                request(1, 0, 2, -1);
//
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
        L.e("今日收益列表", response.toString());

        jiaoyiMoney = response.getString("总金额");
        daozhangMoney = response.getString("到账金额");
        mTvTodayearningJiaoyi.setText(jiaoyiMoney);
        mTvTodayearningDaozhang.setText(daozhangMoney);

        int num = Integer.parseInt(response.getString("条数"));
        if (num != 0) {
            JSONArray jsonArray = response.getJSONArray("列表");
            for (int i = 0; i < jsonArray.length(); i++) {
                String[] a = new String[5];
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                a[0] = jsonObject.getString("交易单号");
                a[1] = jsonObject.getString("录入时间");
                a[2] = jsonObject.getString("交易金额");
                a[3] = jsonObject.getString("到账金额");
                a[4] = jsonObject.getString("id");
                list.add(a);
            }
            page = num == 10 ? Integer.parseInt(response.getString("页数")) : 0;
            mRecyclerView.setVisibility(View.VISIBLE);
            mTvNoData.setVisibility(View.GONE);

            if (isLoadMore) {

                mMenuAdapter.notifyDataSetChanged();
            } else {
                processData();
            }

        } else {

            if (isLoadMore) {
                MToast.showToast(mContext, "已无数据加载");

            } else {
                mRecyclerView.setVisibility(View.GONE);
                mTvNoData.setVisibility(View.VISIBLE);
                mTvNoData.setText("无数据");
            }
        }
    }

}
