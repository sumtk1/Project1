package com.gloiot.hygounionmerchant.ui.activity.home.shop;

import android.Manifest;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.abcaaa.photopicker.PhotoPicker;
import com.gloiot.hygounionmerchant.R;
import com.gloiot.hygounionmerchant.server.picture.UploadingSelectedPics;
import com.gloiot.hygounionmerchant.ui.BaseActivity;
import com.gloiot.hygounionmerchant.utils.CommonUtils;
import com.zyd.wlwsdk.utils.L;
import com.zyd.wlwsdk.utils.MToast;
import com.zyd.wlwsdk.utils.PictureUtlis;
import com.zyd.wlwsdk.widge.MyDialogBuilder;
import com.zyd.wlwsdk.widge.MyNewDialogBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 编辑商品(用于服务类小铺)
 * Created by Dlt on 2017/8/19 15:34
 */
public class ShopModifyGoodsActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.iv_pic)
    ImageView mIvPic;
    @Bind(R.id.tv_uploading_explain)
    TextView mTvUploadingExplain;
    @Bind(R.id.iv_delete_pic)
    ImageView mIvDeletePic;
    @Bind(R.id.tv_goods_title)
    TextView mTvGoodsTitle;
    @Bind(R.id.et_market_price)
    EditText mEtMarketPrice;
    @Bind(R.id.et_supply_price)
    EditText mEtSupplyPrice;

    public static final int REQUEST_CODE_GOODSPIC = 5;

    private String goodsId, goodsPicUrl, title, marketPrice, supplyPrice;
    private MyNewDialogBuilder myDialogBuilder;

    @Override
    public int initResource() {
        return R.layout.activity_shop_modify_goods;
    }

    @Override
    public void initData() {
        CommonUtils.setTitleBar(this, true, "编辑商品", "");
        Intent intent = getIntent();
        goodsId = intent.getStringExtra("id");
        goodsPicUrl = intent.getStringExtra("picUrl");
        title = intent.getStringExtra("title");
        marketPrice = intent.getStringExtra("market");
        supplyPrice = intent.getStringExtra("supply");

        CommonUtils.setDisplayImage(mIvPic, goodsPicUrl, 0, R.drawable.default_image);
        mTvUploadingExplain.setVisibility(View.GONE);
        mTvGoodsTitle.setText(title);
        mEtMarketPrice.setText(marketPrice);
        mEtMarketPrice.setSelection(marketPrice.length());
        mEtSupplyPrice.setText(supplyPrice);

        setInputListener();
    }

    private void setInputListener() {

        mEtMarketPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //小数点后位数控制
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {//可以适当输入多位，位数会在最后计算时四舍五入
                        s = s.toString().subSequence(0, s.toString().indexOf(".") + 3);//小数点后最多输入2位
                        mEtMarketPrice.setText(s);
                        mEtMarketPrice.setSelection(s.length());
                    }
                }
                //第一位为点
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    mEtMarketPrice.setText(s);
                    mEtMarketPrice.setSelection(2);
                }
                //第一位为零，后面只能输入点
                if (s.toString().startsWith("0") && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        mEtMarketPrice.setText(s.subSequence(0, 1));
                        mEtMarketPrice.setSelection(1);
//                        return;
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mEtSupplyPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //小数点后位数控制
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {//可以适当输入多位，位数会在最后计算时四舍五入
                        s = s.toString().subSequence(0, s.toString().indexOf(".") + 3);//小数点后最多输入2位
                        mEtSupplyPrice.setText(s);
                        mEtSupplyPrice.setSelection(s.length());
                    }
                }
                //第一位为点
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    mEtSupplyPrice.setText(s);
                    mEtSupplyPrice.setSelection(2);
                }
                //第一位为零，后面只能输入点
                if (s.toString().startsWith("0") && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        mEtSupplyPrice.setText(s.subSequence(0, 1));
                        mEtSupplyPrice.setSelection(1);
//                        return;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @OnClick({R.id.iv_pic, R.id.iv_delete_pic, R.id.tv_delete, R.id.tv_confirm})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_pic:
                checkPermission(new CheckPermListener() {
                    @Override
                    public void superPermission() {
                        selectPic();
                    }
                }, R.string.camera_updatepics, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                break;
            case R.id.iv_delete_pic:
                goodsPicUrl = "";
                CommonUtils.setDisplayImage(mIvPic, goodsPicUrl, 0, R.drawable.ic_shangchuantup);
                mTvUploadingExplain.setVisibility(View.VISIBLE);
                mIvDeletePic.setVisibility(View.GONE);
                break;
            case R.id.tv_delete:

                myDialogBuilder = MyNewDialogBuilder.getInstance(mContext);
                myDialogBuilder
                        .withTitie("删除商品")
                        .withCenterContent("确认删除此商品")
                        .withEffects(MyDialogBuilder.SlideTop, MyDialogBuilder.SlideTopDismiss)
                        .setTwoBtn("取消", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                myDialogBuilder.dismiss();
                            }
                        }, "确认", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                myDialogBuilder.dismissNoAnimator();

//                                requestHandleArrayList.add(requestAction.shopDeleteGoods(ModifyGoodsActivity.this, goodsId));
                            }
                        })
                        .show();


                break;
            case R.id.tv_confirm:
                if (verifyData()) {

                    myDialogBuilder = MyNewDialogBuilder.getInstance(mContext);
                    myDialogBuilder
                            .withTitie("修改商品")
                            .withCenterContent("确认修改此商品")
                            .withEffects(MyDialogBuilder.SlideTop, MyDialogBuilder.SlideTopDismiss)
                            .setTwoBtn("取消", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    myDialogBuilder.dismiss();
                                }
                            }, "确认", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    myDialogBuilder.dismissNoAnimator();

//                                    requestHandleArrayList.add(requestAction.shopEditGoods(ModifyGoodsActivity.this,
//                                            goodsPicUrl, title, marketPrice, supplyPrice, goodsId));

                                }
                            })
                            .show();

                }

                break;
        }
    }

    private void selectPic() {
        PhotoPicker.builder()
                .setPhotoCount(1)
                .setShowCamera(true)
                .setShowGif(false)
                .setPreviewEnabled(true)
                .start(this, REQUEST_CODE_GOODSPIC);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            String photos = null;
            if (data != null) {
                photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS).get(0);
            }
            switch (requestCode) {
                case REQUEST_CODE_GOODSPIC:

                    new UploadingSelectedPics(mContext, photos) {
                        @Override
                        protected void setSinglePicSuccess(String picUrl) {
                            goodsPicUrl = picUrl;
                            ShopModifyGoodsActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mTvUploadingExplain.setVisibility(View.GONE);
                                    PictureUtlis.loadImageViewHolder(mContext, goodsPicUrl, R.drawable.but_tianjia, mIvPic);
                                    mIvDeletePic.setVisibility(View.VISIBLE);
                                }
                            });
                        }

                        @Override
                        protected void setSinglePicFailure() {
                            L.e("上传照片", "上传商品图片失败");
                        }
                    }.setSinglePic();
                    break;

            }
        }

    }

    @Override
    public void requestSuccess(int requestTag, JSONObject response, int showLoad) throws JSONException {
        super.requestSuccess(requestTag, response, showLoad);
        switch (requestTag) {
//            case RequestAction.TAG_SHOPEDITPRODUCT://编辑
//                MToast.showToast(mContext, "商品信息修改成功");
//                finish();
//                break;
//            case RequestAction.TAG_SHOPDELETEPRODUCT://删除
//                MToast.showToast(mContext, "商品删除成功");
//                finish();
//                break;

        }
    }

    //数据验证
    private Boolean verifyData() {
        marketPrice = mEtMarketPrice.getText().toString().trim();
        supplyPrice = mEtSupplyPrice.getText().toString().trim();

        if (TextUtils.isEmpty(goodsPicUrl)) {
            MToast.showToast(mContext, "请设置商品图片");
            return false;
        } else if (TextUtils.isEmpty(marketPrice)) {
            MToast.showToast(mContext, "请输入市场价");
            return false;
        } else if (Float.parseFloat(marketPrice) == 0) {
            MToast.showToast(mContext, "市场价不能为零，请重新输入");
            return false;
        } else if (TextUtils.isEmpty(supplyPrice)) {
            MToast.showToast(mContext, "请输入供货价");
            return false;
        } else if (Float.parseFloat(supplyPrice) > Float.parseFloat(marketPrice)) {
            MToast.showToast(mContext, "供货价不能大于市场价");
            return false;
        } else {
            return true;
        }

    }

}
