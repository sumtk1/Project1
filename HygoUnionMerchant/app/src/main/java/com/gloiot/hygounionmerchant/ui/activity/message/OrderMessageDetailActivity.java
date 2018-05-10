package com.gloiot.hygounionmerchant.ui.activity.message;

import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.gloiot.hygounionmerchant.R;
import com.gloiot.hygounionmerchant.ui.BaseActivity;
import com.gloiot.hygounionmerchant.utils.CommonUtils;

import butterknife.Bind;

/**
 * 订单消息详情
 * Created by Dlt on 2017/12/18 11:23
 */
public class OrderMessageDetailActivity extends BaseActivity {

    @Bind(R.id.sv_layout_shop_supermarket)
    NestedScrollView mSvLayoutShopSupermarket;
    @Bind(R.id.tv_supermarket_total_money)
    TextView mTvSupermarketTotalMoney;
    @Bind(R.id.tv_supermarket_total_amount)
    TextView mTvSupermarketTotalAmount;
    @Bind(R.id.tv_supermarket_purchase_person)
    TextView mTvSupermarketPurchasePerson;
    @Bind(R.id.tv_supermarket_pay_type)
    TextView mTvSupermarketPayType;
    @Bind(R.id.tv_supermarket_purchase_time)
    TextView mTvSupermarketPurchaseTime;
    @Bind(R.id.tv_supermarket_order_num)
    TextView mTvSupermarketOrderNum;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @Bind(R.id.sv_layout_other)
    ScrollView mSvLayoutOther;
    @Bind(R.id.rl_layout_top_travel)
    RelativeLayout mRlLayoutTopTravel;
    @Bind(R.id.iv_image_travel)
    ImageView mIvImageTravel;
    @Bind(R.id.tv_title_travel)
    TextView mTvTitleTravel;
    @Bind(R.id.tv_price_travel)
    TextView mTvPriceTravel;
    @Bind(R.id.tv_place_of_departure_travel)
    TextView mTvPlaceOfDepartureTravel;

    @Bind(R.id.ll_layout_bottom_travel)
    LinearLayout mLlLayoutBottomTravel;
    @Bind(R.id.tv_travel_contacts_name)
    TextView mTvTravelContactsName;
    @Bind(R.id.tv_travel_contacts_phone_num)
    TextView mTvTravelContactsPhoneNum;
    @Bind(R.id.tv_travel_certification_type)
    TextView mTvTravelCertificationType;
    @Bind(R.id.tv_travel_certification_num)
    TextView mTvTravelCertificationNum;
    @Bind(R.id.tv_travel_purchase_time)
    TextView mTvTravelPurchaseTime;
    @Bind(R.id.tv_travel_order_num)
    TextView mTvTravelOrderNum;

    @Bind(R.id.rl_layout_top_hotel)
    RelativeLayout mRlLayoutTopHotel;
    @Bind(R.id.iv_image_hotel)
    ImageView mIvImageHotel;
    @Bind(R.id.tv_title_hotel)
    TextView mTvTitleHotel;
    @Bind(R.id.tv_unit_price_hotel)
    TextView mTvUnitPriceHotel;
    @Bind(R.id.tv_amount_hotel)
    TextView mTvAmountHotel;
    @Bind(R.id.tv_total_price_hotel)
    TextView mTvTotalPriceHotel;

    @Bind(R.id.ll_layout_bottom_hotel)
    LinearLayout mLlLayoutBottomHotel;
    @Bind(R.id.tv_hotel_check_in_time)
    TextView mTvHotelCheckInTime;
    @Bind(R.id.tv_hotel_contacts_name)
    TextView mTvHotelContactsName;
    @Bind(R.id.tv_hotel_contacts_phone_num)
    TextView mTvHotelContactsPhoneNum;
    @Bind(R.id.tv_hotel_purchase_time)
    TextView mTvHotelPurchaseTime;
    @Bind(R.id.tv_hotel_order_num)
    TextView mTvHotelOrderNum;

    @Bind(R.id.rl_layout_top_service)
    RelativeLayout mRlLayoutTopService;
    @Bind(R.id.iv_image_service)
    ImageView mIvImageService;
    @Bind(R.id.tv_title_service)
    TextView mTvTitleService;
    @Bind(R.id.tv_unit_price_service)
    TextView mTvUnitPriceService;
    @Bind(R.id.tv_amount_service)
    TextView mTvAmountService;
    @Bind(R.id.tv_total_price_service)
    TextView mTvTotalPriceService;

    @Bind(R.id.ll_layout_bottom_service)
    LinearLayout mLlLayoutBottomService;
    @Bind(R.id.tv_service_contacts_name)
    TextView mTvServiceContactsName;
    @Bind(R.id.tv_service_contacts_phone_num)
    TextView mTvServiceContactsPhoneNum;
    @Bind(R.id.tv_service_purchase_time)
    TextView mTvServicePurchaseTime;
    @Bind(R.id.tv_service_order_num)
    TextView mTvServiceOrderNum;

    @Override
    public int initResource() {
        return R.layout.activity_order_message_detail;
    }

    @Override
    public void initData() {
        CommonUtils.setTitleBar(this, true, "订单消息详情", "");
    }


}
