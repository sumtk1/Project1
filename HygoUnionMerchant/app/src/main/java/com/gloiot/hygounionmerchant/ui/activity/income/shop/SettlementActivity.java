package com.gloiot.hygounionmerchant.ui.activity.income.shop;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import butterknife.OnClick;

import static com.gloiot.hygounionmerchant.R.id.tv_settlement_lasttime;

/**
 * 小铺--结算
 * Created by Dlt on 2017/8/22 19:28
 */
public class SettlementActivity extends BaseActivity implements View.OnClickListener, PullToRefreshLayout.OnRefreshListener {

    @Bind(R.id.rl_have)
    RelativeLayout mRlHave;
    @Bind(tv_settlement_lasttime)
    TextView mTvSettlementLasttime;
    @Bind(R.id.pulltorefreshlayout)
    PullToRefreshLayout mPulltorefreshlayout;
    @Bind(R.id.pullablelistview)
    PullableListView mPullablelistview;

    @Bind(R.id.ll_no)
    LinearLayout mLlNo;

    private String accountType;
    private List<String[]> list = new ArrayList<>();
    private CommonAdapter mSettlementListAdapter;
    private String lastSettlementTime;
    private int page = 0;

    @OnClick({R.id.tv_settlement_gosettle, R.id.tv_go_to_jiesuan})
    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.tv_settlement_gosettle:

                intent = new Intent(SettlementActivity.this, SettlementOverallActivity.class);
                intent.putExtra("上次结算时间", lastSettlementTime);
                startActivity(intent);

                break;
            case R.id.tv_go_to_jiesuan:
                intent = new Intent(SettlementActivity.this, SettlementOverallActivity.class);
                intent.putExtra("上次结算时间", lastSettlementTime);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        list.clear();
        request(0, 0, 1, 0);
    }

    @Override
    public int initResource() {
        return R.layout.activity_settlement;
    }

    @Override
    public void initData() {
        accountType = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTTYPE, "");
        CommonUtils.setTitleBar(this, true, "结算", "");
        mPulltorefreshlayout.setOnRefreshListener(this);
    }

    /**
     * @param requestType 请求类型，初始请求=0，刷新=1，加载=2
     * @param page        页数
     * @param requestTag  请求tag,请求成功后用于区分是哪个请求
     * @param showLoad    是否显示请求加载框 0--显示， -1--不显示
     */
    private void request(int requestType, int page, int requestTag, int showLoad) {
        requestHandleArrayList.add(requestAction.getShopJiesuanList(this, accountType, mPulltorefreshlayout, requestType, page, requestTag, showLoad));
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        if (!list.isEmpty()) {
            mSettlementListAdapter.notifyDataSetChanged();
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
        mSettlementListAdapter = new CommonAdapter<String[]>(SettlementActivity.this, R.layout.item_shop_settlement, list) {
            @Override
            public void convert(final ViewHolder holder, String[] strings) {
                holder.setText(R.id.tv_settlement_oddNum, strings[6]);
                holder.setText(R.id.tv_settlement_starttime, strings[3]);
                holder.setText(R.id.tv_settlement_endtime, strings[4]);
                holder.setText(R.id.tv_jiaoyi, strings[1]);
                holder.setText(R.id.tv_daozhang, strings[2]);
            }
        };
        mPullablelistview.setAdapter(mSettlementListAdapter);

        //设置分隔线
        mPullablelistview.setDivider(new ColorDrawable(Color.rgb(243, 243, 243)));//#F3F3F3
        mPullablelistview.setDividerHeight(16);

        mPullablelistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(SettlementActivity.this, SettlementDetailActivity.class);
                intent.putExtra("oddNum", list.get(position)[6]);
                intent.putExtra("start", list.get(position)[3]);
                intent.putExtra("end", list.get(position)[4]);
                intent.putExtra("jiaoyi", list.get(position)[1]);
                intent.putExtra("daozhang", list.get(position)[2]);
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
            default:
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
        L.e("结算单列表", response.toString());
        lastSettlementTime = response.getString("上次结算时间");
        mTvSettlementLasttime.setText(lastSettlementTime);
        int num = Integer.parseInt(response.getString("条数"));
        if (num != 0) {
            JSONArray jsonArray = response.getJSONArray("列表");
            for (int i = 0; i < jsonArray.length(); i++) {
                String[] a = new String[7];
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                a[0] = jsonObject.getString("id");
                a[1] = jsonObject.getString("应结积分");
                a[2] = jsonObject.getString("到账积分");
                a[3] = jsonObject.getString("开始时间");
                a[4] = jsonObject.getString("结算时间");
                a[5] = jsonObject.getString("结算笔数");
                a[6] = jsonObject.getString("结算单号");
                list.add(a);
            }
            page = num == 10 ? Integer.parseInt(response.getString("页数")) : 0;
            mRlHave.setVisibility(View.VISIBLE);
            mLlNo.setVisibility(View.GONE);
            if (isLoadMore) {
                mPulltorefreshlayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                mSettlementListAdapter.notifyDataSetChanged();
            } else {
                setAdapter();
            }

        } else {

            if (isLoadMore) {
                MToast.showToast(mContext, "已无数据加载");
                mPulltorefreshlayout.loadmoreFinish(PullToRefreshLayout.FAIL);
            } else {
                mRlHave.setVisibility(View.GONE);
                mLlNo.setVisibility(View.VISIBLE);
            }
        }
    }

}
