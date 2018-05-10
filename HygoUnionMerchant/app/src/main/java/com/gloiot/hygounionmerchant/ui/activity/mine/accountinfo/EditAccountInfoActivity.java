package com.gloiot.hygounionmerchant.ui.activity.mine.accountinfo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.abcaaa.photopicker.PhotoPicker;
import com.gloiot.hygounionmerchant.R;
import com.gloiot.hygounionmerchant.server.network.RequestAction;
import com.gloiot.hygounionmerchant.server.picture.UploadingSelectedPics;
import com.gloiot.hygounionmerchant.ui.BaseActivity;
import com.gloiot.hygounionmerchant.ui.adapter.ShopInfoPicsAdapter;
import com.gloiot.hygounionmerchant.utils.CommonUtils;
import com.gloiot.hygounionmerchant.utils.ConstantUtils;
import com.gloiot.hygounionmerchant.utils.MaxLengthWatcher;
import com.lljjcoder.citypickerview.widget.CityPicker;
import com.zxy.tiny.Tiny;
import com.zyd.wlwsdk.utils.L;
import com.zyd.wlwsdk.utils.MToast;
import com.zyd.wlwsdk.utils.PictureUtlis;
import com.zyd.wlwsdk.utils.SharedPreferencesUtils;
import com.zyd.wlwsdk.widge.MyDialogBuilder;
import com.zyd.wlwsdk.widge.MyNewDialogBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.OnClick;

import static com.gloiot.hygounionmerchant.R.string.location;

/**
 * 编辑账号信息
 * Created by Dlt on 2017/9/20 15:29
 */
public class EditAccountInfoActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.tv_pic_title)
    TextView mTvPicTitle;
    @Bind(R.id.iv_photo)
    ImageView mIvPhoto;
    @Bind(R.id.iv_delete_pic)
    ImageView mIvDeletePic;
    @Bind(R.id.tv_zuoji_title)
    TextView mTvZuojiTitle;
    @Bind(R.id.et_shop_zuojihao)
    EditText mEtShopZuojihao;
    @Bind(R.id.et_shop_quhao)
    EditText mEtShopQuhao;
    @Bind(R.id.tv_quyu_title)
    TextView mTvQuyuTitle;
    @Bind(R.id.tv_shop_area)
    TextView mTvShopArea;
    @Bind(R.id.tv_dizhi_title)
    TextView mTvDizhiTitle;
    @Bind(R.id.et_shop_address)
    EditText mEtShopAddress;
    @Bind(R.id.tv_zuobiao_title)
    TextView mTvZuobiaoTitle;
    @Bind(R.id.tv_shop_coordinate)
    TextView mTvShopCoordinate;
    @Bind(R.id.tv_jianjie_title)
    TextView mTvJianjieTitle;
    @Bind(R.id.et_shop_brief_introduction)
    EditText mEtShopBriefIntroduction;
    @Bind(R.id.iv_toptitle_back)
    ImageView mIvToptitleBack;

    public static final int REQUEST_PICS = 6;

    private String accountType;
    private String shopId, shopName, originalShopBriefIntroduction = "", originalShopPhone = "", originalShopAddress = "", originalShopPic = "", originalShopArea = "",
            shopType, originalLongitude = "", originalLatitude = "";
    private String newShopPic = "", newShopPhone = "", newQuhao = "", newZuojihao = "", newShopArea = "", newShopAddress = "", newShopBriefIntroduction = "",
            newLongitude = "", newLatitude = "";
    private ShopInfoPicsAdapter mShopInfoPicsAdapter;
    private ArrayList<String> selectedPhotos = new ArrayList<>();
    private String mShopProvince = "", mShopCity = "", mShopDistrict = "", mLngLat = "";
    private MyNewDialogBuilder myDialogBuilder;

    @Override
    protected void onResume() {
        super.onResume();
        boolean isStationAreaReset = SharedPreferencesUtils.getBoolean(mContext, ConstantUtils.SP_ISACCOUNTAREARESET, false);
        String stationProvince = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTSHENG, "");
        String statinoCity = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTSHI, "");
        String stationDistrict = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTQU, "");
        mEtShopAddress.setText(SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTLOCATIONDETAILINFO, ""));
        if (isStationAreaReset) {
            mTvShopArea.setText(stationProvince + "-" + statinoCity + "-" + stationDistrict);
            SharedPreferencesUtils.setBoolean(mContext, ConstantUtils.SP_ISACCOUNTAREARESET, false);

//            et_modifyoilstation_location.setText("");//省市区变动时，将详细地址清空，让用户重新输入

        } else {
            if (!TextUtils.isEmpty(originalShopArea)) {
                mTvShopArea.setText(originalShopArea);
            }
        }

        String stationLat = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTLATITUDE, "");//纬度
        String stationLng = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTLONGITUDE, "");//经度

        if (!TextUtils.isEmpty(stationLat) && !TextUtils.isEmpty(stationLng)) {
            mTvShopCoordinate.setText("已标记");
        } else {
            mTvShopCoordinate.setText("");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        String stationDistrict = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTQU, "");
        if (!TextUtils.isEmpty(stationDistrict)) {
            SharedPreferencesUtils.setBoolean(mContext, ConstantUtils.SP_ISACCOUNTAREARESET, true);
        }
        SharedPreferencesUtils.setString(mContext, ConstantUtils.SP_ACCOUNTLOCATIONDETAILINFO, mEtShopAddress.getText().toString());
    }

    @Override
    public int initResource() {
        return R.layout.activity_edit_account_info;
    }

    @Override
    public void initData() {
        accountType = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTTYPE, "");
        CommonUtils.setTitleBar(this, false, accountType + "信息", "保存");
        mIvToptitleBack.setVisibility(View.VISIBLE);

        mTvPicTitle.setText(accountType + "图片");
        mTvZuojiTitle.setText(accountType + "座机");
        mTvQuyuTitle.setText(accountType + "区域");
        mTvShopArea.setHint("请选择" + accountType + "区域");
        mTvDizhiTitle.setText(accountType + "地址");
        mEtShopAddress.setHint("请输入" + accountType + "地址");
        mTvZuobiaoTitle.setText(accountType + "坐标");
        mTvJianjieTitle.setText(accountType + "简介");
        mEtShopBriefIntroduction.setHint("请对" + accountType + "描述...");

        Intent intent = getIntent();
        originalShopPic = intent.getStringExtra("shopPic");
        originalShopPhone = intent.getStringExtra("shopPhone");
        originalShopArea = intent.getStringExtra("shopArea");
        originalShopAddress = intent.getStringExtra("shopAddress");
        originalShopBriefIntroduction = intent.getStringExtra("shopBriefIntroduction");

        originalLongitude = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTLONGITUDE, "");//经度
        originalLatitude = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTLATITUDE, "");//纬度

        newShopPic = originalShopPic;

        SharedPreferencesUtils.setString(mContext, ConstantUtils.SP_ACCOUNTLOCATIONDETAILINFO, originalShopAddress);


//        if (accountType.equals("小铺")) {
//
//
//        } else if (accountType.equals("酒店")) {
//
//
//        } else if (accountType.equals("旅行社")) {
//
//        }

        CommonUtils.setDisplayImage(mIvPhoto, originalShopPic, 0, R.drawable.default_image);

        if (!TextUtils.isEmpty(originalShopArea)) {
            if (originalShopArea.contains("-")) {
                String[] s = originalShopArea.split("-");
//                for (int i = 0; i < s.length; i++) {
//                 L.e("s--","s"+i+"-"+ s[i]);
//                }
                if (s.length == 3) {//不确定会不会出问题，看测试的数据
                    mShopProvince = s[0];
                    mShopCity = s[1];
                    mShopDistrict = s[2];
                    L.e("省市区", mShopProvince + mShopCity + mShopDistrict);
                    SharedPreferencesUtils.setString(mContext, ConstantUtils.SP_ACCOUNTSHENG, mShopProvince);
                    SharedPreferencesUtils.setString(mContext, ConstantUtils.SP_ACCOUNTSHI, mShopCity);
                    SharedPreferencesUtils.setString(mContext, ConstantUtils.SP_ACCOUNTQU, mShopDistrict);
                }
            }
        }

        try {
            if (originalShopPhone.contains("-")) {
                String[] phone = originalShopPhone.split("-");
                mEtShopQuhao.setText(phone[0]);
                mEtShopZuojihao.setText(phone[1]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mEtShopAddress.addTextChangedListener(new MaxLengthWatcher(50, mEtShopAddress));//最多50个字符

//        mTvShopArea.setText(originalShopArea);
//        mEtShopAddress.setText(originalShopAddress);
        mEtShopBriefIntroduction.setText(originalShopBriefIntroduction);

//        mEtShopAddress.setSelection(originalShopAddress.length());

    }

    @OnClick({R.id.iv_toptitle_back, R.id.tv_toptitle_right, R.id.iv_photo, R.id.iv_delete_pic, R.id.tv_shop_area, R.id.tv_shop_coordinate})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_toptitle_back://返回
                onBackTouched();
                break;
            case R.id.tv_toptitle_right://保存
                if (verifyData()) {
                    requestHandleArrayList.add(requestAction.updateAccountInfo(EditAccountInfoActivity.this, newShopPic, newShopPhone, newShopArea,
                            newShopAddress, newShopBriefIntroduction, newLongitude, newLatitude, accountType));
                }
                break;
            case R.id.iv_photo:
                checkPermission(new CheckPermListener() {
                    @Override
                    public void superPermission() {
                        selectPic();
                    }
                }, R.string.camera_updatepics, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                break;
            case R.id.iv_delete_pic:
                newShopPic = "";
                CommonUtils.setDisplayImage(mIvPhoto, newShopPic, 0, R.drawable.ic_shangchuantup);
                mIvDeletePic.setVisibility(View.GONE);
                break;
            case R.id.tv_shop_area:
                CommonUtils.hideInput(mContext, mTvShopArea);

                CityPicker cityPicker = new CityPicker.Builder(EditAccountInfoActivity.this)
                        .textSize(20)
                        .title("选择区域")
                        .backgroundPop(0xa0000000)
                        .titleBackgroundColor("#449FFB")
                        .titleTextColor("#ffffff")
                        .backgroundPop(0xa0000000)
                        .confirTextColor("#ffffff")
                        .cancelTextColor("#ffffff")
                        .province(mShopProvince)
                        .city(mShopCity)
                        .district(mShopDistrict)
                        .textColor(Color.parseColor("#000000"))
                        .provinceCyclic(false)
                        .cityCyclic(false)
                        .districtCyclic(false)
                        .visibleItemsCount(7)
                        .itemPadding(10)
                        .onlyShowProvinceAndCity(false)
                        .build();
                cityPicker.show();

                //监听方法，获取选择结果
                cityPicker.setOnCityItemClickListener(new CityPicker.OnCityItemClickListener() {
                    @Override
                    public void onSelected(String... citySelected) {
                        //省份
                        mShopProvince = citySelected[0];
                        //城市
                        mShopCity = citySelected[1];
                        //区县（如果设定了两级联动，那么该项返回空）
                        mShopDistrict = citySelected[2];
                        //邮编
                        String code = citySelected[3];

                        mTvShopArea.setText(mShopProvince + "-" + mShopCity + "-" + mShopDistrict);
                    }

                    @Override
                    public void onCancel() {
//                        Toast.makeText(BianjiShouhuoDizhiActivity.this, "已取消", Toast.LENGTH_LONG).show();
                    }
                });
                break;
            case R.id.tv_shop_coordinate:
                if (TextUtils.isEmpty(mTvShopArea.getText().toString().trim())) {
                    MToast.showToast(mContext, "请选择" + accountType + "区域");
                } else {
                    if (TextUtils.isEmpty(mEtShopAddress.getText().toString().trim())) {
                        MToast.showToast(mContext, "请输入" + accountType + "地址");
                    } else {
                        getMapLatLng();
                    }
                }
                break;
        }
    }

    /**
     * 返回按钮事件
     */
    private void onBackTouched() {
        if (isDataChanged()) {
            myDialogBuilder = MyNewDialogBuilder.getInstance(mContext);
            myDialogBuilder
                    .withTitie("提示")
                    .withContene("请保存您的修改信息，返回将放弃修改?")
                    .withEffects(MyDialogBuilder.SlideTop, MyDialogBuilder.SlideTopDismiss)
                    .setTwoBtn("返回", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            myDialogBuilder.dismiss();

                            View view = getWindow().peekDecorView();
                            if (view != null) {
                                InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
                            }
                            finish();
                        }
                    }, "保存", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            myDialogBuilder.dismissNoAnimator();

                            if (verifyData()) {
                                requestHandleArrayList.add(requestAction.updateAccountInfo(EditAccountInfoActivity.this, newShopPic, newShopPhone,
                                        newShopArea, newShopAddress, newShopBriefIntroduction, newLongitude, newLatitude, accountType));
                            }

                        }
                    })
                    .show();

        } else {
            View view = getWindow().peekDecorView();
            if (view != null) {
                InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            finish();
        }
    }

    //返回或提交保存时获取数据
    private void getDataOnBackOrCommit() {

        newQuhao = mEtShopQuhao.getText().toString().trim();
        newZuojihao = mEtShopZuojihao.getText().toString().trim();
        newShopPhone = newQuhao + "-" + newZuojihao;
        newShopArea = mTvShopArea.getText().toString().trim();
        newShopAddress = mEtShopAddress.getText().toString().trim();
        newShopBriefIntroduction = mEtShopBriefIntroduction.getText().toString().trim();

        mLngLat = mTvShopCoordinate.getText().toString().trim();
        newLongitude = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTLONGITUDE, "");//经度
        newLatitude = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTLATITUDE, "");//纬度

    }

    /**
     * 判断数据是否有过更改
     *
     * @return true--有过更改，false--没有更改
     */
    private boolean isDataChanged() {
        getDataOnBackOrCommit();
        if (originalShopPic.equals(newShopPic) && originalShopPhone.equals(newShopPhone) && originalShopArea.equals(newShopArea) &&
                originalShopAddress.equals(newShopAddress) && originalShopBriefIntroduction.equals(newShopBriefIntroduction) &&
                originalLongitude.equals(newLongitude) && originalLatitude.equals(newLatitude)) {
            return false;
        } else {
            return true;
        }
    }

    //数据验证
    private Boolean verifyData() {
        getDataOnBackOrCommit();

        if (TextUtils.isEmpty(newShopPic)) {
            MToast.showToast(mContext, accountType + "图片不能为空");
            return false;
        } else if (TextUtils.isEmpty(newShopArea)) {
            MToast.showToast(mContext, "请选择" + accountType + "区域");
            return false;
        } else if (TextUtils.isEmpty(newShopAddress)) {
            MToast.showToast(mContext, accountType + "地址不能为空");
            return false;
        } else if (TextUtils.isEmpty(mLngLat)) {
            MToast.showToast(mContext, "请标记" + accountType + "位置");
            return false;
        } else {
            if (accountType.equals("小铺")) {
                return true;
            } else {
                if (TextUtils.isEmpty(newQuhao)) {
                    MToast.showToast(mContext, "请输入区号");
                    return false;
                } else if (TextUtils.isEmpty(newZuojihao)) {
                    MToast.showToast(mContext, "请输入座机号");
                    return false;
                }
                return true;
            }
        }
    }

    private void selectPic() {
        PhotoPicker.builder()
                .setPhotoCount(1)
                .setShowCamera(true)
                .setShowGif(false)
                .setPreviewEnabled(true)
                .start(this, REQUEST_PICS);
    }

    //获取地图坐标
    private void getMapLatLng() {
        checkPermission(new CheckPermListener() {
            @Override
            public void superPermission() {//应该是只有定位时才需要这个权限

//                Intent intent = new Intent(EditAccountInfoActivity.this, GetLocationActivity.class);//测试地图配置(定位)

                Intent intent = new Intent(EditAccountInfoActivity.this, MapLatLngActivity.class);

                if (mTvShopCoordinate.getText().toString().equals("已标记") && originalShopAddress.equals(mEtShopAddress.getText().toString())
                        && originalShopArea.equals(mTvShopArea.getText().toString())) {
                    intent.putExtra("type", "nidili");//逆地理编码
                } else {
                    String locationKeywords = mEtShopAddress.getText().toString();
                    intent.putExtra("type", "dili");//地理编码
                    intent.putExtra("keywords", locationKeywords);//搜索关键字
                }
                startActivity(intent);
            }
        }, location, Manifest.permission.ACCESS_COARSE_LOCATION);
    }

    @Override
    public void requestSuccess(int requestTag, JSONObject response, int showLoad) throws JSONException {
        super.requestSuccess(requestTag, response, showLoad);
        switch (requestTag) {
            case RequestAction.TAG_UPDATEACCOUNTINFO://修改账号（商户）信息
                L.e("修改商户信息", response.toString());
                MToast.showToast(mContext, "修改成功");
                finish();
                break;
        }
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
                case REQUEST_PICS:
//
//                    new UploadingSelectedPics(mContext, photos) {
//                        @Override
//                        protected void setSinglePicSuccess(String picUrl) {
//                            newShopPic = picUrl;
//                            EditAccountInfoActivity.this.runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    PictureUtlis.loadImageViewHolder(mContext, newShopPic, R.drawable.default_image, mIvPhoto);
//                                    mIvDeletePic.setVisibility(View.VISIBLE);
//                                }
//                            });
//                        }
//
//                        @Override
//                        protected void setSinglePicFailure() {
//                            L.e("上传照片", "上传图片失败");
//                        }
//                    }.setSinglePic();


                    //控制上传的图片小于300KB
                    Tiny.FileCompressOptions compressOptions = new Tiny.FileCompressOptions();
                    compressOptions.size = 300;
                    compressOptions.config = Bitmap.Config.ARGB_8888;

                    new UploadingSelectedPics(mContext, photos, compressOptions) {
                        @Override
                        protected void setSinglePicSuccess(String picUrl) {
                            newShopPic = picUrl;
                            EditAccountInfoActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    PictureUtlis.loadImageViewHolder(mContext, newShopPic, R.drawable.default_image, mIvPhoto);
                                    mIvDeletePic.setVisibility(View.VISIBLE);
                                }
                            });
                        }

                        @Override
                        protected void setSinglePicFailure() {
                            L.e("上传照片", "上传图片失败");
                        }
                    }.setSinglePicWithCompress();

                    break;
            }
        }
    }

    //重写系统返回键事件
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {//其中event.getRepeatCount() == 0 是重复次数，点返回键时，防止点的过快，触发两次后退事件，做此设置
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {//保留这个判断，增强程序健壮性
            onBackTouched();
        }
        return false;
    }

}
