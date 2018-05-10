package com.gloiot.hygounionmerchant.ui.fragment;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.gloiot.hygounionmerchant.R;
import com.gloiot.hygounionmerchant.server.network.RequestAction;
import com.gloiot.hygounionmerchant.ui.BaseFragment;
import com.gloiot.hygounionmerchant.ui.activity.home.AuthenticationRecordActivity;
import com.gloiot.hygounionmerchant.ui.activity.home.OfflineGatheringRecordActivity;
import com.gloiot.hygounionmerchant.ui.activity.home.shop.ShopScanBarCodeActivity;
import com.gloiot.hygounionmerchant.ui.activity.income.shop.SettlementActivity;
import com.gloiot.hygounionmerchant.ui.activity.income.shop.StatisticsActivity;
import com.gloiot.hygounionmerchant.ui.activity.income.travelandhotel.TravelAndHotelStatisticsActivity;
import com.gloiot.hygounionmerchant.ui.webview.WebActivity;
import com.gloiot.hygounionmerchant.utils.ConstantUtils;
import com.zyd.wlwsdk.utils.L;
import com.zyd.wlwsdk.utils.NoDoubleClickUtils;
import com.zyd.wlwsdk.utils.SharedPreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.gloiot.hygounionmerchant.ui.activity.MainActivity.REQUEST_CODE_BARCODE_FOR_WEBVIEW;
import static com.gloiot.hygounionmerchant.ui.activity.MainActivity.REQUEST_CODE_QRCODE_FOR_WEBVIEW;

/**
 * Created by Dlt on 2017/11/13 10:33
 */
public class NewHomeFragment extends BaseFragment implements View.OnClickListener {

    public static final String TAG = NewHomeFragment.class.getSimpleName();
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.sv_layout_hotel_or_travel)
    ScrollView mSvLayoutHotelOrTravel;
    @Bind(R.id.sv_layout_supermarket)
    ScrollView mSvLayoutSupermarket;
    @Bind(R.id.sv_layout_not_supermarket)
    ScrollView mSvLayoutNotSupermarket;

    private String accountType, shopType;
    private String accountName;
    private String productManagementUrl, orderManagementUrl, productUploadUrl, commodityWarehousingUrl, userManagementUrl,
            inputCodeRenzhengUrl, renzhengRecordUrl, gatheringRecordUrl, renzhengDetailUrl, qrcodeUrl;
    private String backResult = "";

    public static Fragment newInstance(int position) {
        NewHomeFragment fragment = new NewHomeFragment();
        return fragment;
    }

    public NewHomeFragment() {

    }

    @Override
    public void onResume() {
        super.onResume();
        L.e("NewHomeFragment", "onResume");

        requestHandleArrayList.add(requestAction.getHomeData(NewHomeFragment.this, accountType));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_home, container, false);
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

        accountType = SharedPreferencesUtils.getString(getActivity(), ConstantUtils.SP_ACCOUNTTYPE, "");
        if (accountType.equals("小铺")) {
            shopType = SharedPreferencesUtils.getString(getActivity(), ConstantUtils.SP_SHOPTYPE, "");
            if (shopType.equals("商超")) {
                mSvLayoutSupermarket.setVisibility(View.VISIBLE);
                mSvLayoutHotelOrTravel.setVisibility(View.GONE);
                mSvLayoutNotSupermarket.setVisibility(View.GONE);
            } else if (shopType.equals("服务")) {
                mSvLayoutNotSupermarket.setVisibility(View.VISIBLE);
                mSvLayoutSupermarket.setVisibility(View.GONE);
                mSvLayoutHotelOrTravel.setVisibility(View.GONE);
            }
        } else if (accountType.equals("旅行社")) {
            mSvLayoutHotelOrTravel.setVisibility(View.VISIBLE);
            mSvLayoutSupermarket.setVisibility(View.GONE);
            mSvLayoutNotSupermarket.setVisibility(View.GONE);
        } else if (accountType.equals("酒店")) {
            mSvLayoutHotelOrTravel.setVisibility(View.VISIBLE);
            mSvLayoutSupermarket.setVisibility(View.GONE);
            mSvLayoutNotSupermarket.setVisibility(View.GONE);
        }

    }

    @OnClick({R.id.ll_travel_or_hotel_input_code, R.id.ll_travel_or_hotel_scan_code, R.id.ll_travel_or_hotel_renzheng_record, R.id.ll_travel_or_hotel_uploading_commodity,
            R.id.ll_travel_or_hotel_commodity_management, R.id.ll_travel_or_hotel_customer_order, R.id.ll_travel_or_hotel_statistics, R.id.ll_supermarket_offline_gathering,
            R.id.ll_supermarket_gathering_record, R.id.ll_supermarket_add_barcode, R.id.ll_supermarket_uploading_commodity, R.id.ll_supermarket_barcode_management,
            R.id.ll_supermarket_ruku_management, R.id.ll_supermarket_settlement, R.id.ll_supermarket_statistics, R.id.ll_service_input_code, R.id.ll_service_scan_code,
            R.id.ll_service_offline_gathering, R.id.ll_service_renzheng_record, R.id.ll_service_uploading_commodity, R.id.ll_service_commodity_management,
            R.id.ll_service_customer_order, R.id.ll_service_settlement, R.id.ll_service_offline_gathering_record, R.id.ll_service_statistics})
    @Override
    public void onClick(View v) {
        if (NoDoubleClickUtils.isDoubleClick()) return; // 防止连续点击
        Intent intent;
        switch (v.getId()) {
            //---------------------------------旅行社/酒店------------------------------
            case R.id.ll_travel_or_hotel_input_code://输码认证
//                startActivity(new Intent(getActivity(), InputCodeAuthenticationActivity.class));//用网页来做

                intent = new Intent(getActivity(), WebActivity.class);//使用环游购封装webview
                intent.putExtra("url", SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_INPUTCODERENZHENGURL, "")
                        + "?account=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_USERACCOUNT, "")
                        + "&shop_type=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTTYPE, "")
                        + "&random_code=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_RANDOMCODE, ""));
                startActivity(intent);
                break;
            case R.id.ll_travel_or_hotel_scan_code://扫码认证(二维码)
                checkPermission(new CheckPermListener() {
                    @Override
                    public void superPermission() {
                        Intent intent = new Intent(getActivity(), ShopScanBarCodeActivity.class);
                        intent.putExtra("flag", "QRcode");
                        getActivity().startActivityForResult(intent, REQUEST_CODE_QRCODE_FOR_WEBVIEW);// 这个getActivity()不能少。否则请求码错乱。
                        backResult = "";
                    }
                }, R.string.perm_camera, Manifest.permission.CAMERA, Manifest.permission.VIBRATE);
                break;
            case R.id.ll_travel_or_hotel_renzheng_record://认证记录
                startActivity(new Intent(getActivity(), AuthenticationRecordActivity.class));
                break;
            case R.id.ll_travel_or_hotel_uploading_commodity://上传产品
                intent = new Intent(getActivity(), WebActivity.class);//使用环游购封装webview
                intent.putExtra("url", SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_PRODUCTUPLOADURL, "")
                        + "?account=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_USERACCOUNT, "")
                        + "&shop_type=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTTYPE, "")
                        + "&random_code=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_RANDOMCODE, ""));
                startActivity(intent);
                break;
            case R.id.ll_travel_or_hotel_commodity_management://产品管理
                intent = new Intent(getActivity(), WebActivity.class);//使用环游购封装webview
                intent.putExtra("url", SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_PRODUCTMANAGEMENTURL, "")
                        + "?account=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_USERACCOUNT, "")
                        + "&shop_type=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTTYPE, "")
                        + "&random_code=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_RANDOMCODE, ""));
                startActivity(intent);
                break;
            case R.id.ll_travel_or_hotel_customer_order://客户订单
                intent = new Intent(getActivity(), WebActivity.class);//使用环游购封装webview
                intent.putExtra("url", SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ORDERMANAGEMENTURL, "")
                        + "?account=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_USERACCOUNT, "")
                        + "&shop_type=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTTYPE, "")
                        + "&random_code=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_RANDOMCODE, ""));
                startActivity(intent);
                break;
            case R.id.ll_travel_or_hotel_statistics://统计
                startActivity(new Intent(getActivity(), TravelAndHotelStatisticsActivity.class));
                break;
            //---------------------------------小铺-商超------------------------------
            case R.id.ll_supermarket_offline_gathering://线下收款
                intent = new Intent(getActivity(), WebActivity.class);//使用环游购封装webview
                intent.putExtra("url", ConstantUtils.BASEURL + "/merchant/shopM_codeGoodsList.html"
                        + "?loginName=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_USERACCOUNT, "")
                        + "&rondom=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_RANDOMCODE, ""));
                startActivity(intent);

                L.e("商超URL", ConstantUtils.BASEURL + "/merchant/shopM_codeGoodsList.html"
                        + "?loginName=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_USERACCOUNT, "")
                        + "&rondom=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_RANDOMCODE, ""));
                break;
            case R.id.ll_supermarket_gathering_record://收款记录
                intent = new Intent(getActivity(), OfflineGatheringRecordActivity.class);
                intent.putExtra("flag", "supermarket");
                startActivity(intent);
                break;
            case R.id.ll_supermarket_add_barcode://添加条码
                intent = new Intent(getActivity(), WebActivity.class);//使用环游购封装webview
                intent.putExtra("url", SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_PRODUCTUPLOADURL, "")
                        + "?account=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_USERACCOUNT, "")
                        + "&shop_type=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTTYPE, "")
                        + "&random_code=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_RANDOMCODE, ""));
                startActivity(intent);
                break;
            case R.id.ll_supermarket_uploading_commodity://上传商品---------------是原来的商品入库吗？

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
            case R.id.ll_supermarket_barcode_management://条码管理
                intent = new Intent(getActivity(), WebActivity.class);//使用环游购封装webview
                intent.putExtra("url", SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_PRODUCTMANAGEMENTURL, "")
                        + "?account=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_USERACCOUNT, "")
                        + "&shop_type=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTTYPE, "")
                        + "&random_code=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_RANDOMCODE, ""));
                startActivity(intent);
                break;
            case R.id.ll_supermarket_ruku_management://入库管理
                intent = new Intent(getActivity(), WebActivity.class);//使用环游购封装webview
                intent.putExtra("url", SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ORDERMANAGEMENTURL, "")
                        + "?account=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_USERACCOUNT, "")
                        + "&shop_type=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTTYPE, "")
                        + "&random_code=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_RANDOMCODE, ""));
                startActivity(intent);
                break;
            case R.id.ll_supermarket_settlement://结算
                startActivity(new Intent(getActivity(), SettlementActivity.class));
                break;
            case R.id.ll_supermarket_statistics://统计
                startActivity(new Intent(getActivity(), StatisticsActivity.class));
                break;
            //---------------------------------小铺-非商超------------------------------
            case R.id.ll_service_input_code://输码认证
//                startActivity(new Intent(getActivity(), InputCodeAuthenticationActivity.class));//用网页来做

                intent = new Intent(getActivity(), WebActivity.class);//使用环游购封装webview
                intent.putExtra("url", SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_INPUTCODERENZHENGURL, "")
                        + "?account=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_USERACCOUNT, "")
                        + "&shop_type=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTTYPE, "")
                        + "&random_code=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_RANDOMCODE, ""));
                startActivity(intent);
                break;
            case R.id.ll_service_scan_code://扫码认证(二维码)
                checkPermission(new CheckPermListener() {
                    @Override
                    public void superPermission() {
                        Intent intent = new Intent(getActivity(), ShopScanBarCodeActivity.class);
                        intent.putExtra("flag", "QRcode");
                        getActivity().startActivityForResult(intent, REQUEST_CODE_QRCODE_FOR_WEBVIEW);// 这个getActivity()不能少。否则请求码错乱。
                        backResult = "";
                    }
                }, R.string.perm_camera, Manifest.permission.CAMERA, Manifest.permission.VIBRATE);
                break;
            case R.id.ll_service_offline_gathering://线下收款
                intent = new Intent(getActivity(), WebActivity.class);//使用环游购封装webview
                intent.putExtra("url", ConstantUtils.BASEURL + "/merchant/shopM_serGoodsList.html"
                        + "?loginName=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_USERACCOUNT, "")
                        + "&rondom=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_RANDOMCODE, ""));
                startActivity(intent);

                L.e("服务URL", ConstantUtils.BASEURL + "/merchant/shopM_serGoodsList.html"
                        + "?loginName=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_USERACCOUNT, "")
                        + "&rondom=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_RANDOMCODE, ""));
                break;
            case R.id.ll_service_renzheng_record://认证记录
                startActivity(new Intent(getActivity(), AuthenticationRecordActivity.class));
                break;
            case R.id.ll_service_uploading_commodity://上传产品
                intent = new Intent(getActivity(), WebActivity.class);//使用环游购封装webview
                intent.putExtra("url", SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_PRODUCTUPLOADURL, "")
                        + "?account=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_USERACCOUNT, "")
                        + "&shop_type=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTTYPE, "")
                        + "&random_code=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_RANDOMCODE, ""));
                startActivity(intent);
                break;
            case R.id.ll_service_commodity_management://产品管理
                intent = new Intent(getActivity(), WebActivity.class);//使用环游购封装webview
                intent.putExtra("url", SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_PRODUCTMANAGEMENTURL, "")
                        + "?account=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_USERACCOUNT, "")
                        + "&shop_type=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTTYPE, "")
                        + "&random_code=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_RANDOMCODE, ""));
                startActivity(intent);
                break;
            case R.id.ll_service_customer_order://客户订单
                intent = new Intent(getActivity(), WebActivity.class);//使用环游购封装webview
                intent.putExtra("url", SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ORDERMANAGEMENTURL, "")
                        + "?account=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_USERACCOUNT, "")
                        + "&shop_type=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTTYPE, "")
                        + "&random_code=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_RANDOMCODE, ""));
                startActivity(intent);
                break;
            case R.id.ll_service_settlement://结算
                startActivity(new Intent(getActivity(), SettlementActivity.class));
                break;
            case R.id.ll_service_offline_gathering_record://线下收款记录
                intent = new Intent(getActivity(), OfflineGatheringRecordActivity.class);
                intent.putExtra("flag", "service");
                startActivity(intent);
                break;
            case R.id.ll_service_statistics://统计
                startActivity(new Intent(getActivity(), StatisticsActivity.class));
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
                inputCodeRenzhengUrl = response.getString("输码认证地址");
                renzhengRecordUrl = response.getString("认证记录地址");
                gatheringRecordUrl = response.getString("收款记录地址");
                renzhengDetailUrl = response.getString("认证详情地址");
//                qrcodeUrl = response.getString("二维码地址");
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
                SharedPreferencesUtils.setString(mContext, ConstantUtils.SP_INPUTCODERENZHENGURL, inputCodeRenzhengUrl);
                SharedPreferencesUtils.setString(mContext, ConstantUtils.SP_RENZHENGRECORDURL, renzhengRecordUrl);
                SharedPreferencesUtils.setString(mContext, ConstantUtils.SP_GATHERINGRECORDURL, gatheringRecordUrl);
                SharedPreferencesUtils.setString(mContext, ConstantUtils.SP_RENZHENGDETAILURL, renzhengDetailUrl);
//                SharedPreferencesUtils.setString(mContext, ConstantUtils.SP_QRCODEURL, qrcodeUrl);

                String xinxibuquan = response.getString("信息补全");

                if (accountType.equals("小铺")) {
                    SharedPreferencesUtils.setString(mContext, ConstantUtils.SP_SHOPISNEEDTOCOMPLETECOMMODITYINFO, xinxibuquan);
                    shopType = response.getString("小铺商户类别");
                    SharedPreferencesUtils.setString(mContext, ConstantUtils.SP_SHOPTYPE, shopType);//小铺类别。取值：服务/商超
                }
                break;

        }
    }

}
