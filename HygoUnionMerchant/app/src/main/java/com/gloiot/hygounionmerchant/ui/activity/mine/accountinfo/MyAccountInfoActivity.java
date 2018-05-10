package com.gloiot.hygounionmerchant.ui.activity.mine.accountinfo;

import android.content.Intent;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gloiot.hygounionmerchant.R;
import com.gloiot.hygounionmerchant.server.network.RequestAction;
import com.gloiot.hygounionmerchant.ui.BaseActivity;
import com.gloiot.hygounionmerchant.ui.adapter.HorizontalMyAccountCommodityAdapter;
import com.gloiot.hygounionmerchant.utils.CommonUtils;
import com.gloiot.hygounionmerchant.utils.ConstantUtils;
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
 * 我的小铺/酒店/旅行社
 * Created by Dlt on 2017/8/24 14:00
 */
public class MyAccountInfoActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.iv_toptitle_more_img)
    ImageView mIvToptitleMoreImg;
    @Bind(R.id.iv_top_pic)
    ImageView mIvTopPic;
    @Bind(R.id.tv_top_name)
    TextView mTvTopName;
    @Bind(R.id.tv_brief_introduction_title)
    TextView mTvBriefIntroductionTitle;
    @Bind(R.id.tv_brief_introduction)
    TextView mTvBriefIntroduction;
    @Bind(R.id.tv_location)
    TextView mTvLocation;
    @Bind(R.id.tv_phone)
    TextView mTvPhone;
    @Bind(R.id.tv_commodity_title)
    TextView mTvCommodityTitle;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private String accountType;
    private List<String[]> commodityList = new ArrayList<>();
    private HorizontalMyAccountCommodityAdapter mCommodityAdapter;
    private String shopId, shopName, shopBriefIntroduction, shopPhone, shopAddress, shopPic, shopArea, shopType;

    @OnClick({R.id.iv_toptitle_more_img, R.id.tv_brief_introduction, R.id.tv_location, R.id.tv_phone})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_toptitle_more_img:
                Intent intent = new Intent(this, EditAccountInfoActivity.class);
                intent.putExtra("shopPic", shopPic);
                intent.putExtra("shopPhone", shopPhone);
                intent.putExtra("shopArea", shopArea);
                intent.putExtra("shopAddress", shopAddress);
                intent.putExtra("shopBriefIntroduction", shopBriefIntroduction);
                startActivity(intent);
                break;
            case R.id.tv_brief_introduction:
            case R.id.tv_location:
            case R.id.tv_phone:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        commodityList.clear();
        requestHandleArrayList.add(requestAction.getMyAccountInfo(this, accountType));
    }

    @Override
    public int initResource() {
        return R.layout.activity_my_account_info;
    }

    @Override
    public void initData() {
        accountType = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTTYPE, "");
        mIvToptitleMoreImg.setImageResource(R.drawable.ic_bianjiyouzhanxinxi);
        if (accountType.equals("小铺")) {
            CommonUtils.setTitleBar(this, true, "我的小铺", "");
            mTvBriefIntroductionTitle.setText("小铺简介");
            mTvCommodityTitle.setText("小铺商品");
        } else if (accountType.equals("酒店")) {
            CommonUtils.setTitleBar(this, true, "我的酒店", "");
            mTvBriefIntroductionTitle.setText("酒店简介");
            mTvCommodityTitle.setText("酒店房型");
        } else if (accountType.equals("旅行社")) {
            CommonUtils.setTitleBar(this, true, "我的旅行社", "");
            mTvBriefIntroductionTitle.setText("旅行社简介");
            mTvCommodityTitle.setText("旅行社产品");
        }
    }

    @Override
    public void requestSuccess(int requestTag, JSONObject response, int showLoad) throws JSONException {
        super.requestSuccess(requestTag, response, showLoad);
        switch (requestTag) {
            case RequestAction.TAG_MYACCOUNTINFO:
                L.e("我的小铺/酒店/旅行社", response.toString());

                shopName = response.getString("商户名称");
                shopPhone = response.getString("商户电话");
                shopAddress = response.getString("商户地址");
                shopPic = response.getString("商户图片");
                shopArea = response.getString("商户区域");

                String latLngState = response.getString("标记状态");
                if (latLngState.equals("已标记")) {
                    SharedPreferencesUtils.setString(mContext, ConstantUtils.SP_ACCOUNTLONGITUDE, response.getString("商户经度"));
                    SharedPreferencesUtils.setString(mContext, ConstantUtils.SP_ACCOUNTLATITUDE, response.getString("商户纬度"));
                } else {
                    SharedPreferencesUtils.setString(mContext, ConstantUtils.SP_ACCOUNTLONGITUDE, "");
                    SharedPreferencesUtils.setString(mContext, ConstantUtils.SP_ACCOUNTLATITUDE, "");
                }

                shopBriefIntroduction = response.getString("商户介绍");

//                CommonUtlis.setDisplayImage(mIvShopPic, shopPic, 0, R.drawable.default_image);//默认格式貌似是 fitXY不符合要求

                Glide.with(mContext).load(shopPic).fitCenter().crossFade().placeholder(R.drawable.default_image).into(mIvTopPic);

                mTvTopName.setText(shopName);
                mTvBriefIntroduction.setText(shopBriefIntroduction);
                mTvPhone.setText(shopPhone);

                String processArea = "";

                if (!TextUtils.isEmpty(shopArea)) {
                    if (shopArea.contains("-")) {
                        String[] s = shopArea.split("-");
//                for (int i = 0; i < s.length; i++) {
//                 L.e("s--","s"+i+"-"+ s[i]);
//                }
                        if (s.length == 3) {//不确定会不会出问题，看测试的数据
                            String sheng = s[0];
                            String shi = s[1];
                            String qu = s[2];
                            processArea = sheng + shi + qu;
                        }
                    }
                }

                mTvLocation.setText(processArea + shopAddress);

                int num = Integer.parseInt(response.getString("商品数量"));
                if (num != 0) {
                    JSONArray jsonArray = response.getJSONArray("商品列表");
                    for (int i = 0; i < jsonArray.length(); i++) {
//                        String[] a = new String[8];
//                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
//                        a[0] = jsonObject.getString("id");
//                        a[1] = jsonObject.getString("商品名称");
//                        a[2] = jsonObject.getString("商品图片");
//                        a[3] = jsonObject.getString("供货价");
//                        a[4] = jsonObject.getString("市场价");
//                        a[5] = jsonObject.getString("录入时间");
//                        a[6] = jsonObject.getString("让利价");
//                        a[7] = jsonObject.getString("结算比");
//                        commodityList.add(a);

//                        if (accountType.equals("酒店")) {
//                            String[] b = new String[3];
//                            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
//                            b[0] = jsonObject.getString("客房图片");
//                            b[1] = jsonObject.getString("标题");
//                            b[2] = jsonObject.getString("客房价格");
//                            commodityList.add(b);
//                        } else if (accountType.equals("旅行社")) {
//                            String[] c = new String[4];
//                            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
//                            c[0] = jsonObject.getString("id");
//                            c[1] = jsonObject.getString("标题");
//                            c[2] = jsonObject.getString("图片");
//                            c[3] = jsonObject.getString("价格");
//                            commodityList.add(c);
//                        }

                        //20170828修改，所有类型的商铺都返一样的值
                        String[] c = new String[4];
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                        c[0] = jsonObject.getString("id");
                        c[1] = jsonObject.getString("标题");
                        c[2] = jsonObject.getString("图片");
                        c[3] = jsonObject.getString("价格");
                        commodityList.add(c);

                    }
                    processData();
                } else {

                }

                break;
        }
    }

    private void processData() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(layoutManager);// 布局管理器。

        mRecyclerView.setHasFixedSize(true);// 如果Item够简单，高度是确定的，打开FixSize将提高性能。
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());// 设置Item默认动画，加也行，不加也行。

        mCommodityAdapter = new HorizontalMyAccountCommodityAdapter(mContext, commodityList, accountType);
//        mCommodityAdapter.setOnItemClickListener(new OnItemClickListener() {
//
//            @Override
//            public void onItemClick(int position) {
//
//                L.e("点击", "  position=" + position);
//                switch (mCommodityAdapter.getItemViewType(position)) {
//
//                    case HorizontalMyAccountCommodityAdapter.TYPE_PHOTO:
//
//
//
////                        if (accountType.equals("商超")) {
////                            Intent intent = new Intent(MyAccountInfoActivity.this, CommodityInfoActivity.class);
////                            intent.putExtra("commodityId", commodityList.get(position)[0]);
////                            startActivity(intent);
////                        } else {
////                            Intent intent = new Intent(MyAccountInfoActivity.this, ModifyGoodsActivity.class);
////                            intent.putExtra("id", commodityList.get(position)[0]);
////                            intent.putExtra("picUrl", commodityList.get(position)[2]);
////                            intent.putExtra("title", commodityList.get(position)[1]);
////                            intent.putExtra("market", commodityList.get(position)[4]);
////                            intent.putExtra("supply", commodityList.get(position)[3]);
////                            startActivity(intent);
////                        }
//
//                        break;
//                    case HorizontalMyAccountCommodityAdapter.TYPE_MORE:
//
////                        if (accountType.equals("商超")) {
////                            startActivity(new Intent(MyAccountInfoActivity.this, WarehousingManagementActivity.class));
////                        } else {
////                            startActivity(new Intent(MyAccountInfoActivity.this, MyGoodsActivity.class));
////                        }
//
//
//                        break;
//
//                }
//            }
//        });
        mRecyclerView.setAdapter(mCommodityAdapter);

    }

}
