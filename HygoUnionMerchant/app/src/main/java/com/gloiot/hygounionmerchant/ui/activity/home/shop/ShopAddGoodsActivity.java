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
import com.gloiot.hygounionmerchant.server.network.RequestAction;
import com.gloiot.hygounionmerchant.server.picture.UploadingSelectedPics;
import com.gloiot.hygounionmerchant.ui.BaseActivity;
import com.gloiot.hygounionmerchant.utils.CommonUtils;
import com.gloiot.hygounionmerchant.utils.ConstantUtils;
import com.gloiot.hygounionmerchant.utils.MaxLengthWatcher;
import com.zyd.wlwsdk.utils.L;
import com.zyd.wlwsdk.utils.MToast;
import com.zyd.wlwsdk.utils.PictureUtlis;
import com.zyd.wlwsdk.utils.SharedPreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 添加商品(用于服务类小铺)
 * Created by Dlt on 2017/8/19 15:27
 */
public class ShopAddGoodsActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.iv_pic)
    ImageView mIvPic;
    @Bind(R.id.tv_uploading_explain)
    TextView mTvUploadingExplain;
    @Bind(R.id.et_goods_title)
    EditText mEtGoodsTitle;
    @Bind(R.id.et_market_price)
    EditText mEtMarketPrice;
    @Bind(R.id.et_supply_price)
    EditText mEtSupplyPrice;
    @Bind(R.id.tv_confirm)
    TextView mTvConfirm;

    public static final int REQUEST_CODE_GOODSPIC = 5;
    private String mGoodsPicUrl, mGoodsName, mMarketPrice, mSupplyPrice;
    private String accountType;

    @Override
    public int initResource() {
        return R.layout.activity_shop_add_goods;
    }

    @Override
    public void initData() {
        accountType = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTTYPE, "");
        CommonUtils.setTitleBar(this, true, "添加商品", "");
        mEtGoodsTitle.addTextChangedListener(new MaxLengthWatcher(50, mEtGoodsTitle));//最多50个字符
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
                        s = s.toString().subSequence(0, s.toString().indexOf(".") + 3);//小数点后最多输入4位
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

    @OnClick({R.id.iv_pic, R.id.tv_confirm})
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
            case R.id.tv_confirm:
                if (verifyData()) {

                    mGoodsName = mEtGoodsTitle.getText().toString();
                    mMarketPrice = mEtMarketPrice.getText().toString();
                    mSupplyPrice = mEtSupplyPrice.getText().toString();

                    String versionCode = CommonUtils.getVersionCode(mContext) + "";

                    requestHandleArrayList.add(requestAction.shopUploadProduct(this, mGoodsPicUrl, mGoodsName,
                            mMarketPrice, mSupplyPrice, versionCode, accountType));


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
                            mGoodsPicUrl = picUrl;
                            ShopAddGoodsActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mTvUploadingExplain.setText("");
                                    PictureUtlis.loadImageViewHolder(mContext, mGoodsPicUrl, R.drawable.but_tianjia, mIvPic);
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
            case RequestAction.TAG_SHOPUPLOADPRODUCT:
                L.e("商品上传", response.toString());
                MToast.showToast(this, "商品添加成功！");
                finish();
                break;
            default:
                break;
        }
    }

    //数据验证
    private Boolean verifyData() {
        if (TextUtils.isEmpty(mGoodsPicUrl)) {
            MToast.showToast(mContext, "请设置商品图片");
            return false;
        } else if (TextUtils.isEmpty(mEtGoodsTitle.getText().toString().trim())) {
            MToast.showToast(mContext, "请输入商品标题");
            return false;
        } else if (TextUtils.isEmpty(mEtMarketPrice.getText().toString().trim())) {
            MToast.showToast(mContext, "请输入市场价");
            return false;
        } else if (Float.parseFloat(mEtMarketPrice.getText().toString().trim()) == 0) {
            MToast.showToast(mContext, "市场价不能为零，请重新输入");
            return false;
        } else if (TextUtils.isEmpty(mEtSupplyPrice.getText().toString().trim())) {
            MToast.showToast(mContext, "请输入供货价");
            return false;
        } else if (Float.parseFloat(mEtSupplyPrice.getText().toString().trim()) > Float.parseFloat(mEtMarketPrice.getText().toString().trim())) {
            MToast.showToast(mContext, "供货价不能大于市场价");
            return false;
        } else {
            return true;
        }
    }

}
