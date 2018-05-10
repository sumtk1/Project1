package com.gloiot.hygounionmerchant.ui.activity.home;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.TabLayout;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.gloiot.hygounionmerchant.R;
import com.gloiot.hygounionmerchant.ui.BaseActivity;
import com.gloiot.hygounionmerchant.utils.CommonUtils;
import com.gloiot.hygounionmerchant.utils.ConstantUtils;
import com.gloiot.hygounionmerchant.utils.DialogUtils;
import com.gloiot.hygounionmerchant.widget.SelectTimeQuantumPopupWindow;
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
 * 线下收款记录
 * Created by Dlt on 2017/11/15 10:56
 */
public class OfflineGatheringRecordActivity extends BaseActivity implements PullToRefreshLayout.OnRefreshListener {

    @Bind(R.id.tl_tabs)
    TabLayout mTlTabs;
    @Bind(R.id.pullablelistview)
    PullableListView mPullablelistview;
    @Bind(R.id.pulltorefreshlayout)
    PullToRefreshLayout mPulltorefreshlayout;
    @Bind(R.id.tv_no_data)
    TextView mTvNoData;

    private String flag = "";//取值：supermarket/service
    private String orderType = "";//取值：“套餐购买”/“扫码支付”------>改为都传扫码支付
    private String accountType;
    private List<String[]> list = new ArrayList<>();
    private CommonAdapter mCommonAdapter;
    private int page = 0;
    private SelectTimeQuantumPopupWindow selectWindow;
    private String startDate = "", endDate = "";
    private String queryStartTime = "", queryEndTime = "";//查询的开始时间，结束时间

    @Override
    public int initResource() {
        return R.layout.activity_offline_gathering_record;
    }

    @Override
    public void initData() {
        accountType = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTTYPE, "");
        CommonUtils.setTitleBar(this, true, "线下收款记录", "");
        Intent intent = getIntent();
        flag = intent.getStringExtra("flag");
        if (flag.equals("supermarket")) {
            orderType = "扫码支付";
        } else if (flag.equals("service")) {
//            orderType = "套餐购买";
            orderType = "扫码支付";
        }

        mTlTabs.getTabAt(0).select();

        mTlTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                L.e("onTabSelected", "tab:" + tab.toString() + ",tab.position" + tab.getPosition());

                switch (tab.getPosition()) {
                    case 0://最近
                        queryStartTime = "";
                        queryEndTime = "";
                        if (!list.isEmpty()) {
                            list.clear();
                            mCommonAdapter.notifyDataSetChanged();
                        }
                        page = 0;
                        request(0, 0, 1, 0);
                        break;
                    case 1://时间段
                        Point position = CommonUtils.getNavigationBarSize(mContext);//虚拟键适配PopupWindow显示位置
                        selectWindow = new SelectTimeQuantumPopupWindow(mContext, popupWindowOnClick);
                        selectWindow.showAtLocation(mTlTabs, Gravity.BOTTOM, 0, position.y);
                        break;
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

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
        requestHandleArrayList.add(requestAction.getOfflineGatheringRecords(this, mPulltorefreshlayout,
                requestType, page, requestTag, showLoad, queryStartTime, queryEndTime, orderType, accountType));
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        if (!list.isEmpty()) {
            list.clear();
            mCommonAdapter.notifyDataSetChanged();
        }
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

    //为弹出窗口实现监听类
    private View.OnClickListener popupWindowOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_close:// 取消
                    startDate = "";
                    endDate = "";
                    selectWindow.dismiss();
                    break;
                case R.id.tv_start_time://开始时间
                    DialogUtils.towBtnDate(mContext, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //# main(1)

                            //java.lang.NullPointerException

                            //Attempt to invoke virtual method 'int android.widget.DatePicker.getMonth()' on a null object reference

                            android.widget.DatePicker datePicker = (android.widget.DatePicker) DialogUtils.myDialogBuilder.getCustomView().findViewById(R.id.datePicker);
                            if (datePicker != null) {
                                int month = datePicker.getMonth() + 1;
                                String day = datePicker.getDayOfMonth() < 10 ? "0" + datePicker.getDayOfMonth() : datePicker.getDayOfMonth() + "";
                                startDate = datePicker.getYear() + "-" + month + "-" + day;
                                selectWindow.setStartTime(startDate);
                                DialogUtils.myDialogBuilder.dismiss();
                            }
                        }
                    });
                    break;
                case R.id.tv_end_time://结束时间
                    DialogUtils.towBtnDate(mContext, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //# main(1)

                            //java.lang.NullPointerException

                            //Attempt to invoke virtual method 'int android.widget.DatePicker.getMonth()' on a null object reference

                            android.widget.DatePicker datePicker = (android.widget.DatePicker) DialogUtils.myDialogBuilder.getCustomView().findViewById(R.id.datePicker);
                            if (datePicker != null) {
                                int month = datePicker.getMonth() + 1;
                                String day = datePicker.getDayOfMonth() < 10 ? "0" + datePicker.getDayOfMonth() : datePicker.getDayOfMonth() + "";
                                endDate = datePicker.getYear() + "-" + month + "-" + day;
                                selectWindow.setEndTime(endDate);
                                DialogUtils.myDialogBuilder.dismiss();
                            }
                        }
                    });
                    break;
                case R.id.tv_reset://重置
                    startDate = "";
                    endDate = "";
                    selectWindow.setStartTime("");
                    selectWindow.setEndTime("");
                    break;
                case R.id.tv_complete://完成
                    L.e("比较时间", "startDate--" + startDate + ",  endDate--" + endDate);
                    if (TextUtils.isEmpty(selectWindow.getStartTime())) {
                        MToast.showToast(mContext, "开始时间不能为空");
                    } else if (TextUtils.isEmpty(selectWindow.getEndTime())) {
                        MToast.showToast(mContext, "结束时间不能为空");
                    } else if (CommonUtils.dateCompare(startDate, endDate)) {
//                        MToast.showToast(mContext, "结束时间应晚于开始时间");
                        MToast.showToast(mContext, "开始时间不能大于/等于结束时间");
                    } else {
                        selectWindow.dismiss();
                        //发起请求
                        queryStartTime = startDate;
                        queryEndTime = endDate;

                        if (!list.isEmpty()) {
                            list.clear();
                            mCommonAdapter.notifyDataSetChanged();
                        }
                        page = 0;

                        request(0, 0, 1, 0);
                        startDate = "";
                        endDate = "";
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private void setAdapter() {

        mCommonAdapter = new CommonAdapter<String[]>(OfflineGatheringRecordActivity.this, R.layout.item_offline_gathering_record, list) {
            @Override
            public void convert(final ViewHolder holder, String[] strings) {
                holder.setText(R.id.tv_odd_num, "交易单号：" + strings[1]);
                holder.setText(R.id.tv_time, strings[4]);
                holder.setText(R.id.tv_jiaoyi, strings[2]);
                holder.setText(R.id.tv_daozhang, strings[3]);
            }
        };
        mPullablelistview.setAdapter(mCommonAdapter);

        //设置分隔线
        mPullablelistview.setDivider(new ColorDrawable(Color.rgb(243, 243, 243)));//#F3F3F3
        mPullablelistview.setDividerHeight(16);

        mPullablelistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(OfflineGatheringRecordActivity.this, OfflineGatheringRecordDetailActivity.class);
                intent.putExtra("orderId", list.get(position)[0]);
                intent.putExtra("orderType", orderType);
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
        L.e("线下收款记录", response.toString());
        int num = Integer.parseInt(response.getString("条数"));

        if (num != 0) {
            JSONArray jsonArray = response.getJSONArray("数据");
            for (int i = 0; i < jsonArray.length(); i++) {
                String[] a = new String[5];
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                a[0] = jsonObject.getString("id");
                a[1] = jsonObject.getString("交易单号");
                a[2] = jsonObject.getString("交易金额");
                a[3] = jsonObject.getString("到账金额");
                a[4] = jsonObject.getString("录入时间");
                list.add(a);
            }
            page = num == 10 ? Integer.parseInt(response.getString("页数")) : 0;
            mPullablelistview.setVisibility(View.VISIBLE);
            mTvNoData.setText("");
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
