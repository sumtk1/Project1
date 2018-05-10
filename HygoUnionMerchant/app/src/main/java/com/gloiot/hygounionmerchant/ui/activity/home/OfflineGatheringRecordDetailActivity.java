package com.gloiot.hygounionmerchant.ui.activity.home;

import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gloiot.hygounionmerchant.R;
import com.gloiot.hygounionmerchant.server.network.RequestAction;
import com.gloiot.hygounionmerchant.ui.BaseActivity;
import com.gloiot.hygounionmerchant.utils.CommonUtils;
import com.gloiot.hygounionmerchant.utils.ConstantUtils;
import com.gloiot.hygounionmerchant.widget.recyclerviewline.ListViewDecoration;
import com.zyd.wlwsdk.adapter.ViewHolder;
import com.zyd.wlwsdk.adapter.recyclerview.CommonAdapter;
import com.zyd.wlwsdk.utils.L;
import com.zyd.wlwsdk.utils.SharedPreferencesUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 线下收款记录详情
 * Created by Dlt on 2017/11/16 10:46
 */
public class OfflineGatheringRecordDetailActivity extends BaseActivity {

    @Bind(R.id.tv_toptitle_right)
    TextView mTvToptitleRight;
    @Bind(R.id.tv_trasaction_num)
    TextView mTvTrasactionNum;
    @Bind(R.id.tv_time)
    TextView mTvTime;
    @Bind(R.id.tv_jiaoyi)
    TextView mTvJiaoyi;
    @Bind(R.id.tv_daozhang)
    TextView mTvDaozhang;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private String orderType = "";
    private String accountType;
    private String deviceBrand;
    private CommonAdapter mCommonAdapter;
    private List<String[]> list = new ArrayList<>();

    @Override
    public int initResource() {
        return R.layout.activity_offline_gathering_record_detail;
    }

    @Override
    public void initData() {
        accountType = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTTYPE, "");
        CommonUtils.setTitleBar(this, true, "线下收款记录详情", "");
        Intent intent = getIntent();
        String orderId = intent.getStringExtra("orderId");
        orderType = intent.getStringExtra("orderType");
        requestHandleArrayList.add(requestAction.getOfflineGatheringRecordDetail(this, orderId, accountType));//20171129修改接口，不传订单类别
        deviceBrand = Build.BRAND;
    }

    @OnClick(R.id.tv_toptitle_right)
    public void onViewClicked() {
    }

    @Override
    public void requestSuccess(int requestTag, JSONObject response, int showLoad) throws JSONException {
        super.requestSuccess(requestTag, response, showLoad);
        switch (requestTag) {
            case RequestAction.TAG_OFFLINEGATHERINGRECORDDETAIL://线下收款详情
                L.e("线下收款详情", response.toString() + "--");

                String orderNum = response.getString("交易单号");
                String time = response.getString("录入时间");
                String totalMoney = response.getString("交易金额");
                String daozhangMoney = response.getString("到账金额");

                mTvTrasactionNum.setText("交易单号：" + orderNum);
                mTvTime.setText(time);
                mTvJiaoyi.setText(totalMoney);
                mTvDaozhang.setText(daozhangMoney);

                int num = Integer.parseInt(response.getString("订单明细条数"));

                if (num != 0) {
                    JSONArray jsonArray = response.getJSONArray("订单明细");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        String[] a = new String[4];
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                        a[0] = jsonObject.getString("商品名称");
                        a[1] = jsonObject.getString("商品图片");
                        a[2] = jsonObject.getString("市场价");
                        a[3] = jsonObject.getString("数量");
                        list.add(a);
                    }
                    setAdapter();
                } else {

                }
                break;
            default:
                break;
        }
    }

    private void setAdapter() {

        mCommonAdapter = new CommonAdapter<String[]>(OfflineGatheringRecordDetailActivity.this, R.layout.item_shop_order_detail, list) {
            @Override
            public void convert(final ViewHolder holder, String[] strings) {

                View view = holder.getConvertView();
                ImageView ivPic = (ImageView) view.findViewById(R.id.iv_pic);
                CommonUtils.setDisplayImage(ivPic, strings[1], 0, R.mipmap.ic_launcher);

                holder.setText(R.id.tv_title, strings[0]);
                holder.setText(R.id.tv_unit_price, strings[2]);
                holder.setText(R.id.tv_amount, strings[3]);

            }
        };
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));// 布局管理器。
        mRecyclerView.setHasFixedSize(true);// 如果Item够简单，高度是确定的，打开FixSize将提高性能。
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());// 设置Item默认动画，加也行，不加也行。
        mRecyclerView.addItemDecoration(new ListViewDecoration(mContext));// 添加分割线。

        mRecyclerView.setAdapter(mCommonAdapter);
    }

}
