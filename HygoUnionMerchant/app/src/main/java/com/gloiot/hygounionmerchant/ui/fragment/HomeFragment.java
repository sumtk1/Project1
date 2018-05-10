package com.gloiot.hygounionmerchant.ui.fragment;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gloiot.hygounionmerchant.R;
import com.gloiot.hygounionmerchant.server.network.RequestAction;
import com.gloiot.hygounionmerchant.ui.BaseFragment;
import com.gloiot.hygounionmerchant.ui.activity.home.shop.ShopMyCommodityForOldMarketUserActivity;
import com.gloiot.hygounionmerchant.ui.activity.home.shop.ShopScanBarCodeActivity;
import com.gloiot.hygounionmerchant.ui.webview.WebActivity;
import com.gloiot.hygounionmerchant.utils.ConstantUtils;
import com.gloiot.hygounionmerchant.utils.GlideImageLoaderForBanner;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.zyd.wlwsdk.utils.L;
import com.zyd.wlwsdk.utils.SharedPreferencesUtils;
import com.zyd.wlwsdk.widge.MyNewDialogBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.gloiot.hygounionmerchant.ui.activity.MainActivity.REQUEST_CODE_BARCODE_FOR_WEBVIEW;

/**
 * Created by Dlt on 2017/8/14 17:16
 */
public class HomeFragment extends BaseFragment implements View.OnClickListener {

    public static final String TAG = HomeFragment.class.getSimpleName();
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.shop_banner)
    Banner mShopBanner;

//    @Bind(R.id.ll_travel_or_hotel)
//    LinearLayout mLlTravelOrHotel;
//    @Bind(R.id.ll_product_management)
//    LinearLayout mLlProductManagement;
//    @Bind(R.id.ll_customer_order)
//    LinearLayout mLlCustomerOrder;
//
//    @Bind(R.id.ll_shop_service)
//    LinearLayout mLlShopService;
//    @Bind(R.id.ll_shop_my_commodity)
//    LinearLayout mLlShopMyCommodity;
//
//    @Bind(R.id.ll_shop_supermarket)
//    LinearLayout mLlShopSupermarket;
//    @Bind(R.id.ll_shop_bar_code)
//    LinearLayout mLlShopBarCode;
//    @Bind(R.id.ll_shop_warehousing)
//    LinearLayout mLlShopWarehousing;

    @Bind(R.id.ll_travel_or_hotel)
    LinearLayout mLlTravelOrHotel;

    @Bind(R.id.ll_shop_service)
    LinearLayout mLlShopService;

    @Bind(R.id.ll_shop_supermarket)
    LinearLayout mLlShopSupermarket;

    @Bind(R.id.tv_complete_commodity_info)
    TextView mTvCompleteCommodityInfo;


    private MyNewDialogBuilder myDialogBuilder;
    private String accountPayPwdState = "";
    private String backResult = "";
    private String accountType, shopType;
    private List<String> picsList = new ArrayList<>();
    private List<String> urlList = new ArrayList<>();
    private String accountName;
    private String productManagementUrl, orderManagementUrl, productUploadUrl, commodityWarehousingUrl, userManagementUrl;

    public static Fragment newInstance(int position) {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    public HomeFragment() {

    }

    @Override
    public void onResume() {
        super.onResume();
        L.e("HomeFragment", "onResume");
//
//        picsList.clear();
//        urlList.clear();
//
//        initData();

        picsList.clear();
        urlList.clear();
        requestHandleArrayList.add(requestAction.getHomeData(HomeFragment.this, accountType));

        accountPayPwdState = SharedPreferencesUtils.getString(getActivity(), ConstantUtils.SP_ACCOUNTPAYPWDSTATE, "");
//        if (accountPayPwdState.equals("已设置")) {
//
//        } else if (accountPayPwdState.equals("未设置")) {
//            myDialogBuilder = MyNewDialogBuilder.getInstance(mContext);
////            myDialogBuilder.setCancelable(false);//设置返回键不可点击 //20170824测试时先让其可以取消
//            myDialogBuilder
//                    .withTitie("设置支付密码")
//                    .withCenterContent("您还没有设置支付密码，设置后方可操作")
//                    .withEffects(MyDialogBuilder.SlideTop, MyDialogBuilder.SlideTopDismiss)
//                    .setOneBtn("去设置", new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            myDialogBuilder.dismissNoAnimator();
//                            startActivity(new Intent(getActivity(), SetPayPwdActivity.class));
//                        }
//                    })
//                    .show();
//        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void initData() {

//        requestHandleArrayList.add(requestAction.getShopHomePageData(this));
        accountType = SharedPreferencesUtils.getString(getActivity(), ConstantUtils.SP_ACCOUNTTYPE, "");
        if (accountType.equals("小铺")) {
//            mTvTitle.setText("异业小铺");
            mLlTravelOrHotel.setVisibility(View.GONE);

            shopType = SharedPreferencesUtils.getString(getActivity(), ConstantUtils.SP_SHOPTYPE, "");
            if (shopType.equals("商超")) {
                mLlShopService.setVisibility(View.GONE);
                mLlShopSupermarket.setVisibility(View.VISIBLE);

                String isNeedToCompleteCommodityInfo = SharedPreferencesUtils.getString(getActivity(),
                        ConstantUtils.SP_SHOPISNEEDTOCOMPLETECOMMODITYINFO, "");//是否需要完善商品信息

                if (isNeedToCompleteCommodityInfo.equals("是")) {
                    mTvCompleteCommodityInfo.setVisibility(View.VISIBLE);
                } else {
                    mTvCompleteCommodityInfo.setVisibility(View.GONE);
                }

            } else if (shopType.equals("服务")) {
                mLlShopSupermarket.setVisibility(View.GONE);
                mTvCompleteCommodityInfo.setVisibility(View.GONE);
                mLlShopService.setVisibility(View.VISIBLE);
            }
        } else if (accountType.equals("旅行社")) {
//            mTvTitle.setText("异业旅游");
            mLlShopService.setVisibility(View.GONE);
            mLlShopSupermarket.setVisibility(View.GONE);
            mTvCompleteCommodityInfo.setVisibility(View.GONE);
            mLlTravelOrHotel.setVisibility(View.VISIBLE);
        } else if (accountType.equals("酒店")) {
//            mTvTitle.setText("异业酒店");
            mLlShopService.setVisibility(View.GONE);
            mLlShopSupermarket.setVisibility(View.GONE);
            mTvCompleteCommodityInfo.setVisibility(View.GONE);
            mLlTravelOrHotel.setVisibility(View.VISIBLE);
        }

    }

    private void setBannerdata() {
        L.e("轮播执行", "start");
        //设置banner样式
        mShopBanner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
//        //设置图片加载器
        mShopBanner.setImageLoader(new GlideImageLoaderForBanner());
        //设置图片集合
        mShopBanner.setImages(picsList);//可以选择设置图片网址，或者资源文件，默认用Glide加载
//        //设置banner动画效果
        mShopBanner.setBannerAnimation(Transformer.DepthPage);
        //设置标题集合（当banner样式有显示title时）
//        mShopBanner.setBannerTitles(titleList);
        //设置自动轮播，默认为true
        mShopBanner.isAutoPlay(true);
        //设置轮播时间
        mShopBanner.setDelayTime(2000);
        //设置指示器位置（当banner模式中有指示器时）
        mShopBanner.setIndicatorGravity(BannerConfig.CENTER);
        //banner设置方法全部调用完毕时最后调用
        mShopBanner.start();
        L.e("轮播执行", "over");
    }

    @OnClick({R.id.ll_product_management, R.id.ll_customer_order, R.id.ll_product_uploading, R.id.ll_shop_service_commodity, R.id.ll_shop_service_order,
            R.id.ll_shop_service_uploading, R.id.ll_shop_bar_code_management, R.id.ll_shop_warehousing_management, R.id.ll_shop_add_bar_code,
            R.id.ll_shop_commodity_warehousing, R.id.tv_complete_commodity_info})
    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            //-------------------旅游/酒店----------------------
            case R.id.ll_product_management://产品管理(旅游/酒店)
//                startActivity(new Intent(getActivity(), ShopProductManagementActivity.class));

//                startActivity(new Intent(getActivity(), CommonWebActivity.class).putExtra(TYPE_KEY, 0));//使用AgentWeb

                intent = new Intent(getActivity(), WebActivity.class);//使用环游购封装webview
                intent.putExtra("url", SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_PRODUCTMANAGEMENTURL, "")
                        + "?account=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_USERACCOUNT, "")
                        + "&shop_type=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTTYPE, "")
                        + "&random_code=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_RANDOMCODE, ""));
                startActivity(intent);

                break;
            case R.id.ll_customer_order://客户订单(旅游/酒店)

//                startActivity(new Intent(getActivity(), CommonWebActivity.class).putExtra(TYPE_KEY, 1));//使用AgentWeb

                intent = new Intent(getActivity(), WebActivity.class);//使用环游购封装webview
                intent.putExtra("url", SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ORDERMANAGEMENTURL, "")
                        + "?account=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_USERACCOUNT, "")
                        + "&shop_type=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTTYPE, "")
                        + "&random_code=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_RANDOMCODE, ""));
                startActivity(intent);

                break;
            case R.id.ll_product_uploading://上传商品

                intent = new Intent(getActivity(), WebActivity.class);//使用环游购封装webview
                intent.putExtra("url", SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_PRODUCTUPLOADURL, "")
                        + "?account=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_USERACCOUNT, "")
                        + "&shop_type=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTTYPE, "")
                        + "&random_code=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_RANDOMCODE, ""));
                startActivity(intent);

                break;

            //-------------------服务类小铺----------------------
            case R.id.ll_shop_service_commodity://商品管理

                intent = new Intent(getActivity(), WebActivity.class);//使用环游购封装webview
                intent.putExtra("url", SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_PRODUCTMANAGEMENTURL, "")
                        + "?account=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_USERACCOUNT, "")
                        + "&shop_type=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTTYPE, "")
                        + "&random_code=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_RANDOMCODE, ""));
                startActivity(intent);

                break;
            case R.id.ll_shop_service_order://客户订单

                intent = new Intent(getActivity(), WebActivity.class);//使用环游购封装webview
                intent.putExtra("url", SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ORDERMANAGEMENTURL, "")
                        + "?account=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_USERACCOUNT, "")
                        + "&shop_type=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTTYPE, "")
                        + "&random_code=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_RANDOMCODE, ""));
                startActivity(intent);

                break;
            case R.id.ll_shop_service_uploading://上传商品

                intent = new Intent(getActivity(), WebActivity.class);//使用环游购封装webview
                intent.putExtra("url", SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_PRODUCTUPLOADURL, "")
                        + "?account=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_USERACCOUNT, "")
                        + "&shop_type=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTTYPE, "")
                        + "&random_code=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_RANDOMCODE, ""));
                startActivity(intent);

                break;

            //-------------------商超类小铺----------------------
            case R.id.ll_shop_bar_code_management://条码管理

                intent = new Intent(getActivity(), WebActivity.class);//使用环游购封装webview
                intent.putExtra("url", SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_PRODUCTMANAGEMENTURL, "")
                        + "?account=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_USERACCOUNT, "")
                        + "&shop_type=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTTYPE, "")
                        + "&random_code=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_RANDOMCODE, ""));
                startActivity(intent);

                break;
            case R.id.ll_shop_warehousing_management://入库管理

                intent = new Intent(getActivity(), WebActivity.class);//使用环游购封装webview
                intent.putExtra("url", SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ORDERMANAGEMENTURL, "")
                        + "?account=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_USERACCOUNT, "")
                        + "&shop_type=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTTYPE, "")
                        + "&random_code=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_RANDOMCODE, ""));
                startActivity(intent);

                break;
            case R.id.ll_shop_add_bar_code://添加条码

                intent = new Intent(getActivity(), WebActivity.class);//使用环游购封装webview
                intent.putExtra("url", SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_PRODUCTUPLOADURL, "")
                        + "?account=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_USERACCOUNT, "")
                        + "&shop_type=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTTYPE, "")
                        + "&random_code=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_RANDOMCODE, ""));
                startActivity(intent);

                break;
            case R.id.ll_shop_commodity_warehousing://商品入库

                checkPermission(new CheckPermListener() {
                    @Override
                    public void superPermission() {
                        Intent intent = new Intent(getActivity(), ShopScanBarCodeActivity.class);
                        intent.putExtra("flag", "barcode");
                        getActivity().startActivityForResult(intent, REQUEST_CODE_BARCODE_FOR_WEBVIEW);// 这个getActivity()不能少。否则请求码错乱。
                        backResult = "";

                    }
                }, R.string.perm_camera, Manifest.permission.CAMERA, Manifest.permission.VIBRATE);

                break;

//            case R.id.ll_shop_my_commodity://我的商品（服务类小铺）
//                startActivity(new Intent(getActivity(), ShopMyGoodsActivity.class));
//                break;
//            case R.id.ll_shop_bar_code://条码管理（商超类小铺）
//                startActivity(new Intent(getActivity(), ShopBarCodeManagementActivity.class));
//                break;
//            case R.id.ll_shop_warehousing://入库管理（商超类小铺）
//                startActivity(new Intent(getActivity(), ShopWarehousingManagementActivity.class));
//                break;
            case R.id.tv_complete_commodity_info://完善商品信息（在旧版本异业小铺上传过商品的商超类小铺）
                startActivity(new Intent(getActivity(), ShopMyCommodityForOldMarketUserActivity.class));
                break;
        }
    }

    @Override
    public void requestSuccess(int requestTag, JSONObject response, int showLoad) throws JSONException {
        super.requestSuccess(requestTag, response, showLoad);
        switch (requestTag) {
            case RequestAction.TAG_HOMEDATA://首页数据
                L.e("首页数据", response.toString());

                accountName = response.getString("商户名称");
                mTvTitle.setText(accountName);
//                accountType = response.getString("商户类别");
                productManagementUrl = response.getString("产品管理地址");
                productUploadUrl = response.getString("产品上传地址");
                orderManagementUrl = response.getString("订单管理地址");
                commodityWarehousingUrl = response.getString("商品入库地址");
                userManagementUrl = response.getString("用户管理地址");
                SharedPreferencesUtils.setString(mContext, ConstantUtils.SP_ACCOUNTNAME, accountName);
                SharedPreferencesUtils.setString(mContext, ConstantUtils.SP_IDENTITYTYPE, response.getString("身份类别"));//负责人/员工
                SharedPreferencesUtils.setString(mContext, ConstantUtils.SP_HYGOACCOUNT, response.getString("环游购账号"));//绑定的环游购账号
                SharedPreferencesUtils.setString(mContext, ConstantUtils.SP_HYGOPHONENUM, response.getString("环游购手机号"));
                SharedPreferencesUtils.setString(mContext, ConstantUtils.SP_HYGONAME, response.getString("环游购姓名"));

                SharedPreferencesUtils.setString(mContext, ConstantUtils.SP_ZIZHIRENZHENGSTATE, response.getString("资质认证状态"));//资质认证状态（取值：去认证/认证中/已认证）
                SharedPreferencesUtils.setString(mContext, ConstantUtils.SP_PRODUCTMANAGEMENTURL, productManagementUrl);
                SharedPreferencesUtils.setString(mContext, ConstantUtils.SP_PRODUCTUPLOADURL, productUploadUrl);
                SharedPreferencesUtils.setString(mContext, ConstantUtils.SP_ORDERMANAGEMENTURL, orderManagementUrl);
                SharedPreferencesUtils.setString(mContext, ConstantUtils.SP_COMMODITYWAREHOUSINGURL, commodityWarehousingUrl);
                SharedPreferencesUtils.setString(mContext, ConstantUtils.SP_USERMANAGEMENTURL, userManagementUrl);

                String xinxibuquan = response.getString("信息补全");

                if (accountType.equals("小铺")) {
                    SharedPreferencesUtils.setString(mContext, ConstantUtils.SP_SHOPISNEEDTOCOMPLETECOMMODITYINFO, xinxibuquan);

                    shopType = response.getString("小铺商户类别");
                    SharedPreferencesUtils.setString(mContext, ConstantUtils.SP_SHOPTYPE, shopType);//小铺类别。取值：服务/商超

                }

                if (xinxibuquan.equals("是")) {
                    mTvCompleteCommodityInfo.setVisibility(View.VISIBLE);
                } else {
                    mTvCompleteCommodityInfo.setVisibility(View.GONE);
                }
                picsList.clear();
                urlList.clear();

                int num = Integer.parseInt(response.getString("条数"));
                if (num > 0) {
                    JSONArray jsonArray = response.getJSONArray("列表");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                        String a = jsonObject.getString("图片");
                        String b = jsonObject.getString("跳转地址");
                        picsList.add(a);
                        urlList.add(b);
                    }

                    setBannerdata();
                }

                break;

        }
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
////        super.onActivityResult(requestCode, resultCode, data);
//        /**
//         * 处理二维码扫描结果
//         */
//        if (requestCode == REQUEST_CODE_BARCODE_FOR_WEBVIEW) {//网页商品入库
//            //处理扫描结果（在界面上显示）
//            if (null != data) {
//                Bundle bundle = data.getExtras();
//                if (bundle == null) {
//                    return;
//                }
//                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
//                    String result = bundle.getString(CodeUtils.RESULT_STRING);
//
//                    backResult = result;
////                    requestHandleArrayList.add(requestAction.getShopBarCodeMessage(HomeFragment.this, result, accountType));//查询该条码信息，判断有无添加过
//
//                    Intent intent = new Intent(getActivity(), WebActivity.class);//使用环游购封装webview
//                    intent.putExtra("url", SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_COMMODITYWAREHOUSINGURL, "")
//                            + "?account=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_USERACCOUNT, "")
//                            + "&shop_type=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTTYPE, "")
//                            + "&random_code=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_RANDOMCODE, "")
//                            + "&Barcode=" + backResult);
//                    startActivity(intent);
//
//                    L.e("商超类入库URL", SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_COMMODITYWAREHOUSINGURL, "")
//                            + "?account=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_USERACCOUNT, "")
//                            + "&shop_type=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTTYPE, "")
//                            + "&random_code=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_RANDOMCODE, "")
//                            + "&Barcode=" + backResult);
//
//                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
//                    backResult = "";
//                    MToast.showToast(mContext, "解析条形码失败");
//                }
//            }
//        }
//
//    }
}
