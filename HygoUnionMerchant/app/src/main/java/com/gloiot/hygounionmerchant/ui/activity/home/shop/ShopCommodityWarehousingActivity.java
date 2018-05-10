package com.gloiot.hygounionmerchant.ui.activity.home.shop;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gloiot.hygounionmerchant.R;
import com.gloiot.hygounionmerchant.server.network.RequestAction;
import com.gloiot.hygounionmerchant.ui.BaseActivity;
import com.gloiot.hygounionmerchant.utils.CommonUtils;
import com.gloiot.hygounionmerchant.utils.ConstantUtils;
import com.zyd.wlwsdk.utils.L;
import com.zyd.wlwsdk.utils.MToast;
import com.zyd.wlwsdk.utils.SharedPreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 小铺商品入库
 * Created by Dlt on 2017/8/19 10:18
 */
public class ShopCommodityWarehousingActivity extends BaseActivity implements View.OnClickListener, BaseActivity.RequestErrorCallback {

    @Bind(R.id.ll_no)
    LinearLayout mLlNo;

    @Bind(R.id.rl_have)
    RelativeLayout mRlHave;
    @Bind(R.id.iv_pic)
    ImageView mIvPic;
    @Bind(R.id.tv_goods_bar_code)
    TextView mTvGoodsBarCode;
    @Bind(R.id.tv_small_classify)
    TextView mTvSmallClassify;
    @Bind(R.id.tv_big_classify)
    TextView mTvBigClassify;
    @Bind(R.id.tv_goods_title)
    TextView mTvGoodsTitle;
    @Bind(R.id.tv_market_price)
    TextView mTvMarketPrice;
    @Bind(R.id.tv_supply_price)
    TextView mTvSupplyPrice;
    @Bind(R.id.et_purchase_price)
    EditText mEtPurchasePrice;
    @Bind(R.id.et_amonut)
    EditText mEtAmonut;

    private String accountType;
    private String commodityPicUrl, barCode, barCodeId, bigType, smallType, bigTypeId, smallTypeId, commodityName, marketPrice, supplyPrice;
    private String purchasePrice, amount;

    @OnClick({R.id.tv_confirm})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.tv_confirm:

                if (verifyData()) {
                    requestHandleArrayList.add(requestAction.shopCommodityWarehousing(ShopCommodityWarehousingActivity.this, barCodeId, bigTypeId,
                            smallTypeId, commodityPicUrl, commodityName, marketPrice, supplyPrice, purchasePrice, amount, accountType));
                }

                break;
        }
    }

    @Override
    public int initResource() {
        return R.layout.activity_shop_commodity_warehousing;
    }

    @Override
    public void initData() {
        accountType = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTTYPE, "");
        CommonUtils.setTitleBar(this, true, "商品入库", "");
        Intent intent = getIntent();
        barCode = intent.getStringExtra("barCode");
        requestHandleArrayList.add(requestAction.getShopBarCodeMessage(ShopCommodityWarehousingActivity.this, barCode, accountType));
        setRequestErrorCallback(this);
        setInputListener();
    }

    private void setInputListener() {

        mEtPurchasePrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //小数点后位数控制
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {//可以适当输入多位，位数会在最后计算时四舍五入
                        s = s.toString().subSequence(0, s.toString().indexOf(".") + 3);//小数点后最多输入2位
                        mEtPurchasePrice.setText(s);
                        mEtPurchasePrice.setSelection(s.length());
                    }
                }

                //第一位为点
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    mEtPurchasePrice.setText(s);
                    mEtPurchasePrice.setSelection(2);
                }
                //第一位为零，后面只能输入点
                if (s.toString().startsWith("0") && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        mEtPurchasePrice.setText(s.subSequence(0, 1));
                        mEtPurchasePrice.setSelection(1);
//                        return;
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    public void requestSuccess(int requestTag, JSONObject response, int showLoad) throws JSONException {
        super.requestSuccess(requestTag, response, showLoad);
        switch (requestTag) {

            case RequestAction.TAG_SHOPFINDBARCODEMESSAGE:
                L.e("获取条形码相关信息", response.toString());

                JSONObject jsonObject = response.getJSONObject("数据");
                commodityName = jsonObject.getString("商品名称");
                commodityPicUrl = jsonObject.getString("商品图片");
                marketPrice = jsonObject.getString("市场价");
                supplyPrice = jsonObject.getString("供货价");
                bigTypeId = jsonObject.getString("商品类别id");
                smallTypeId = jsonObject.getString("商品种类id");
                bigType = jsonObject.getString("类别名称");
                smallType = jsonObject.getString("种类名称");
                String shopId = jsonObject.getString("小铺id");
                barCode = jsonObject.getString("条码编号");
                barCodeId = jsonObject.getString("id");

                mLlNo.setVisibility(View.GONE);
                mRlHave.setVisibility(View.VISIBLE);

                CommonUtils.setDisplayImage(mIvPic, commodityPicUrl, 0, R.drawable.default_image);
                mTvGoodsBarCode.setText(barCode);
                mTvBigClassify.setText(bigType);
                mTvSmallClassify.setText(smallType);
                mTvGoodsTitle.setText(commodityName);
                mTvMarketPrice.setText(marketPrice);
                mTvSupplyPrice.setText(supplyPrice);

                break;

            case RequestAction.TAG_SHOPCOMMODITYWAREHOUSING:
                L.e("商品入库", response.toString());
                MToast.showToast(mContext, "入库成功");
                finish();
                break;

        }
    }

    @Override
    public void requestErrorcallback(int requestTag, JSONObject response) throws Exception {
        switch (requestTag) {
            case RequestAction.TAG_SHOPFINDBARCODEMESSAGE:
                L.e("获取条形码相关信息-requestErrorcallback", response.toString());

                if (response.getString("状态").contains("无此条形码信息")) {

                    mLlNo.setVisibility(View.VISIBLE);
                    mRlHave.setVisibility(View.GONE);

                } else {
                    MToast.showToast(mContext, response.getString("状态"));
                }

                break;
            default:
                MToast.showToast(mContext, response.getString("状态"));
                break;
        }
    }

    // 数据验证
    private Boolean verifyData() {

        purchasePrice = mEtPurchasePrice.getText().toString().trim();
        amount = mEtAmonut.getText().toString().trim();

        if (TextUtils.isEmpty(purchasePrice)) {
            MToast.showToast(mContext, "请设置商品进货价");
            return false;
        } else if (Float.parseFloat(purchasePrice) > Float.parseFloat(supplyPrice.trim())) {
            MToast.showToast(mContext, "进货价不能大于供货价");
            return false;
        } else if (TextUtils.isEmpty(amount)) {
            MToast.showToast(mContext, "请输入商品数量");
            return false;
        } else {
            return true;
        }
    }

}
