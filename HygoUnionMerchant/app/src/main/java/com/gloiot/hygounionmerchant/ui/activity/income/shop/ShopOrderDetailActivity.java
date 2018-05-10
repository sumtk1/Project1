package com.gloiot.hygounionmerchant.ui.activity.income.shop;

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
 * 小铺--订单详情
 * Created by Dlt on 2017/9/7 15:00
 */
public class ShopOrderDetailActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.tv_trasaction_num)
    TextView mTvTrasactionNum;
    @Bind(R.id.tv_time)
    TextView mTvTime;
    @Bind(R.id.tv_jiaoyi)
    TextView mTvJiaoyi;
    @Bind(R.id.tv_daozhang)
    TextView mTvDaozhang;
    @Bind(R.id.iv_print)
    ImageView mIvPrint;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private String accountType;
    private String shopName, totalAmount, totalMoney, orderNum;
    private CommonAdapter mCommonAdapter;
    private List<String[]> list = new ArrayList<>();

    private List<String[]> shopPrinterList = new ArrayList<>();//小铺打印机列表数据
    private boolean isFirstRequestForPrinter = true;//是否第一次请求打印机接口
    private String deviceBrand;

    @OnClick(R.id.iv_print)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_print:

                break;
        }
    }

    @Override
    public int initResource() {
        return R.layout.activity_shop_order_detail;
    }

    @Override
    public void initData() {
        accountType = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTTYPE, "");
        CommonUtils.setTitleBar(this, true, "订单详情", "");
        Intent intent = getIntent();
        String orderId = intent.getStringExtra("orderId");
        requestHandleArrayList.add(requestAction.getShopOrderDetail(this, orderId, accountType));
        deviceBrand = Build.BRAND;
    }

    @Override
    public void requestSuccess(int requestTag, JSONObject response, int showLoad) throws JSONException {
        super.requestSuccess(requestTag, response, showLoad);
        switch (requestTag) {
//            case RequestAction.TAG_SHOPORDERPRODUCTLIST:
//                L.e("订单详情", response.toString());
//
//                int num = Integer.parseInt(response.getString("条数"));
//                totalAmount = response.getString("商品数量");
//
//                if (num != 0) {
//                    JSONArray jsonArray = response.getJSONArray("商品列表");
//                    for (int i = 0; i < jsonArray.length(); i++) {
//                        String[] a = new String[6];
//                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
//                        a[0] = jsonObject.getString("商品名称");
//                        a[1] = jsonObject.getString("商品图片");
//                        a[2] = jsonObject.getString("供货价");
//                        a[3] = jsonObject.getString("市场价");
//                        a[4] = jsonObject.getString("数量");
//                        a[5] = jsonObject.getString("总价");
//                        list.add(a);
//                    }
//                    mIvToptitleMoreImg.setImageResource(R.drawable.ic_dayinji_white);//有数据的时候才显示打印按钮
//                    setAdapter();
//                } else {
//
//                }
//
//                break;
//            case RequestAction.TAG_SHOPGETPRINTERINFO:
//                L.e("获取小铺打印机", response.toString());
//                int num1 = Integer.parseInt(response.getString("条数"));
//                if (num1 != 0) {
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
//                    requestHandleArrayList.add(requestAction.printShopSettlementDetail(OrderDetailActivity.this, orderNum));
//
//                } else {
//                    MToast.showToast(mContext, "您还没有添加打印机");
//                }
//                isFirstRequestForPrinter = false;
//                break;
//
//            case RequestAction.TAG_SHOPORDERPRINT://小铺订单打印
//                L.e("小铺打印订单", response.toString() + "--");
//                break;

            case RequestAction.TAG_SHOPORDERDETAIL://小铺订单详情
                L.e("小铺订单详情", response.toString() + "--");

                String orderNum = response.getString("交易订单号");
                String time = response.getString("时间");
                String totalMoney = response.getString("总金额");
                String daozhangMoney = response.getString("到账金额");

                mTvTrasactionNum.setText("交易单号：" + orderNum);
                mTvTime.setText(time);
                mTvJiaoyi.setText(totalMoney);
                mTvDaozhang.setText(daozhangMoney);

                int num = Integer.parseInt(response.getString("条数"));
                totalAmount = response.getString("商品数量");

                if (num != 0) {
                    JSONArray jsonArray = response.getJSONArray("商品列表");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        String[] a = new String[5];
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                        a[0] = jsonObject.getString("商品名称");
                        a[1] = jsonObject.getString("商品图片");
                        a[2] = jsonObject.getString("供货价");
                        a[3] = jsonObject.getString("市场价");
                        a[4] = jsonObject.getString("数量");
                        list.add(a);
                    }
//                    mIvPrint.setVisibility(View.VISIBLE);

                    mIvPrint.setVisibility(View.GONE);//打印功能暂时不做
                    setAdapter();
                } else {
                    mIvPrint.setVisibility(View.GONE);
                }

                break;

            default:
                break;
        }
    }

    private void setAdapter() {

        mCommonAdapter = new CommonAdapter<String[]>(ShopOrderDetailActivity.this, R.layout.item_shop_order_detail, list) {
            @Override
            public void convert(final ViewHolder holder, String[] strings) {

                View view = holder.getConvertView();
                ImageView ivPic = (ImageView) view.findViewById(R.id.iv_pic);
                CommonUtils.setDisplayImage(ivPic, strings[1], 0, R.mipmap.ic_launcher);

                holder.setText(R.id.tv_title, strings[0]);
                holder.setText(R.id.tv_unit_price, strings[3]);
                holder.setText(R.id.tv_amount, strings[4]);

            }
        };
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));// 布局管理器。
        mRecyclerView.setHasFixedSize(true);// 如果Item够简单，高度是确定的，打开FixSize将提高性能。
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());// 设置Item默认动画，加也行，不加也行。
        mRecyclerView.addItemDecoration(new ListViewDecoration(mContext));// 添加分割线。

        mRecyclerView.setAdapter(mCommonAdapter);
    }

}
