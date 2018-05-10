package com.gloiot.hygounionmerchant.ui.activity.home.shop;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.gloiot.hygounionmerchant.R;
import com.gloiot.hygounionmerchant.ui.BaseActivity;
import com.gloiot.hygounionmerchant.utils.CommonUtils;
import com.zyd.wlwsdk.adapter.ViewHolder;
import com.zyd.wlwsdk.adapter.abslistview.CommonAdapter;
import com.zyd.wlwsdk.utils.MToast;
import com.zyd.wlwsdk.widge.MyNewDialogBuilder;
import com.zyd.wlwsdk.zxing.activity.CodeUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 小铺商品入库页面（在旧版本有上传过商品的商超类商家专用）
 * Created by Dlt on 2017/8/19 11:13
 */
public class ShopCommodityWarehousingForOldMarketUserActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.iv_pic)
    ImageView mIvPic;
    @Bind(R.id.iv_scan_bar_code)
    ImageView mIvScanBarCode;
    @Bind(R.id.et_goods_bar_code)
    EditText mEtGoodsBarCode;
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

    public static final int REQUEST_CODE_BARCODE = 113;

    private MyNewDialogBuilder myDialogBuilder;
    private List<String[]> list1 = new ArrayList<>();//大分类
    private List<String[]> list2 = new ArrayList<>();//次级分类
    private int selectPosition1 = -1, selectPosition2 = -1;
    private String commodityId, commodityPicUrl, bigType, smallType, bigTypeId, smallTypeId, commodityName, marketPrice, supplyPrice;
    private String barCode, purchasePrice, amount;

    @OnClick({R.id.iv_scan_bar_code, R.id.rl_select_classify, R.id.tv_confirm})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_scan_bar_code:
                checkPermission(new BaseActivity.CheckPermListener() {
                    @Override
                    public void superPermission() {
                        checkPermission(new BaseActivity.CheckPermListener() {
                            @Override
                            public void superPermission() {
                                Intent intent = new Intent(ShopCommodityWarehousingForOldMarketUserActivity.this, ShopScanBarCodeActivity.class);
                                intent.putExtra("flag", "barcode");
                                startActivityForResult(intent, REQUEST_CODE_BARCODE);
                                barCode = "";
                            }
                        }, R.string.perm_VIBRATE, Manifest.permission.VIBRATE);

                    }
                }, R.string.perm_camera, Manifest.permission.CAMERA);
                break;
            case R.id.rl_select_classify:
//                requestHandleArrayList.add(requestAction.getShopCommodityCategory(CommodityWarehousingForOldMarketUserActivity.this, ""));
                break;
            case R.id.tv_confirm:
//                if (verifyData()) {
//                    requestHandleArrayList.add(requestAction.shopCompleteCommodityInfo(CommodityWarehousingForOldMarketUserActivity.this, commodityId, barCode, bigTypeId, smallTypeId, commodityPicUrl,
//                            commodityName, marketPrice, supplyPrice, purchasePrice, amount));
//                }
                break;
        }
    }

    @Override
    public int initResource() {
        return R.layout.activity_shop_commodity_warehousing_for_old_market_user;
    }

    @Override
    public void initData() {
        CommonUtils.setTitleBar(this, true, "商品入库", "");
        Intent intent = getIntent();
        commodityId = intent.getStringExtra("id");
        commodityPicUrl = intent.getStringExtra("picUrl");
        commodityName = intent.getStringExtra("title");
        marketPrice = intent.getStringExtra("market");
        supplyPrice = intent.getStringExtra("supply");
        CommonUtils.setDisplayImage(mIvPic, commodityPicUrl, 0, R.drawable.default_image);
        mTvGoodsTitle.setText(commodityName);
        mTvMarketPrice.setText(marketPrice);
        mTvSupplyPrice.setText(supplyPrice);
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

    /**
     * 显示选择类别弹窗
     *
     * @param title     标题
     * @param list      数据集
     * @param isBigType 数据是否来自大类
     */
    private void showSelectCategoryDialog(String title, List<String[]> list, final boolean isBigType) {
        myDialogBuilder = MyNewDialogBuilder.getInstance(mContext);
        final ListView listView = myDialogBuilder.setListViewSingle(mContext);
        final CommonAdapter commonAdapter = new CommonAdapter<String[]>(mContext, R.layout.item_popupwindow_singletext_new, list) {
            @Override
            public void convert(ViewHolder holder, final String[] strings) {
                if (isBigType) {
                    holder.setText(R.id.tv_popsingletext_text, strings[2]);
                } else {
                    holder.setText(R.id.tv_popsingletext_text, strings[1]);
                }
            }
        };
        listView.setAdapter(commonAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isBigType) {
                    selectPosition1 = position;
                } else {
                    selectPosition2 = position;
                }

            }
        });
        myDialogBuilder.setMaxHeight(listView);
        myDialogBuilder
                .withTitie(title)
                .withEffects(MyNewDialogBuilder.SlideTop, MyNewDialogBuilder.SlideTopDismiss)
                .setTwoBtnSpecial("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myDialogBuilder.dismiss();
                        list1.clear();
                        list2.clear();

                    }
                }, "确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myDialogBuilder.dismissNoAnimator();
                        if (isBigType) {
//                            requestHandleArrayList.add(requestAction.getShopCommodityCategory(ShopCommodityWarehousingForOldMarketUserActivity.this, list1.get(selectPosition1)[0]));
                        } else {

                            bigType = list1.get(selectPosition1)[2];
                            smallType = list2.get(selectPosition2)[1];
                            bigTypeId = list1.get(selectPosition1)[0];
                            smallTypeId = list2.get(selectPosition2)[0];

                            mTvBigClassify.setText(bigType);
                            mTvSmallClassify.setText(smallType);

                        }

                    }
                })
                .show();
    }

    @Override
    public void requestSuccess(int requestTag, JSONObject response, int showLoad) throws JSONException {
        super.requestSuccess(requestTag, response, showLoad);
        switch (requestTag) {

//            case RequestAction.TAG_SHOPCOMMODITYCATEGORY://商品类别
//                L.e("商品类别", response.toString());
//
//                int num1 = Integer.parseInt(response.getString("类别条数"));
//                if (num1 != 0) {
//                    JSONArray jsonArray = response.getJSONArray("类别列表");
//                    for (int i = 0; i < jsonArray.length(); i++) {
//                        String[] a = new String[3];
//                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
//                        a[0] = jsonObject.getString("id");
//                        a[1] = jsonObject.getString("行业类别");
//                        a[2] = jsonObject.getString("类别名称");
//                        list1.add(a);
//                    }
//                    showSelectCategoryDialog("选择商品类别", list1, true);
//                } else {
//                    int num2 = Integer.parseInt(response.getString("种类条数"));
//                    if (num2 != 0) {
//                        JSONArray jsonArray = response.getJSONArray("种类列表");
//                        for (int i = 0; i < jsonArray.length(); i++) {
//                            String[] a = new String[2];
//                            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
//                            a[0] = jsonObject.getString("id");
//                            a[1] = jsonObject.getString("种类");
//                            list2.add(a);
//                        }
//                        showSelectCategoryDialog(list1.get(selectPosition1)[2], list2, false);
//                    } else {
//                        MToast.showToast(mContext, "商品种类为空");
//                    }
//
//                }
//
//                break;
//            case RequestAction.TAG_SHOPCOMPLETECOMMODITYINFO://补全商品信息
//                MToast.showToast(mContext, "商品信息补全成功");
//                finish();
//                break;

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        /**
         * 处理二维码扫描结果
         */
        if (requestCode == REQUEST_CODE_BARCODE) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);

                    barCode = result;

                    mEtGoodsBarCode.setText(barCode);
                    mEtGoodsBarCode.setSelection(barCode.length());

                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    barCode = "";
                    MToast.showToast(mContext, "解析条形码失败");
                }
            }
        }
    }

    // 数据验证
    private Boolean verifyData() {
        barCode = mEtGoodsBarCode.getText().toString().trim();
        purchasePrice = mEtPurchasePrice.getText().toString().trim();
        amount = mEtAmonut.getText().toString().trim();

        if (TextUtils.isEmpty(barCode)) {
            MToast.showToast(mContext, "商品条形码不能为空");
            return false;
        } else if (TextUtils.isEmpty(purchasePrice)) {
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
