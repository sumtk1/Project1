package com.gloiot.hygounionmerchant.ui.activity.mine.zizhirenzheng;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.abcaaa.photopicker.PhotoPicker;
import com.gloiot.hygounionmerchant.R;
import com.gloiot.hygounionmerchant.server.network.RequestAction;
import com.gloiot.hygounionmerchant.server.picture.UploadingSelectedPics;
import com.gloiot.hygounionmerchant.ui.BaseActivity;
import com.gloiot.hygounionmerchant.utils.CommonUtils;
import com.gloiot.hygounionmerchant.utils.ConstantUtils;
import com.zyd.wlwsdk.utils.L;
import com.zyd.wlwsdk.utils.MToast;
import com.zyd.wlwsdk.utils.NoDoubleClickUtils;
import com.zyd.wlwsdk.utils.PictureUtlis;
import com.zyd.wlwsdk.utils.SharedPreferencesUtils;
import com.zyd.wlwsdk.widge.MyDialogBuilder;
import com.zyd.wlwsdk.widge.MyNewDialogBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.OnClick;

import static com.gloiot.hygounionmerchant.ui.activity.mine.zizhirenzheng.CertificationActivity.BUSINESS_LICENSE;
import static com.gloiot.hygounionmerchant.ui.activity.mine.zizhirenzheng.CertificationActivity.LEGAL_PERSON_BACK;
import static com.gloiot.hygounionmerchant.ui.activity.mine.zizhirenzheng.CertificationActivity.LEGAL_PERSON_FRONT;
import static com.gloiot.hygounionmerchant.ui.activity.mine.zizhirenzheng.CertificationActivity.LEGAL_PERSON_SHOUCHI;
import static com.gloiot.hygounionmerchant.ui.activity.mine.zizhirenzheng.CertificationActivity.PRINCIPAL_BACK;
import static com.gloiot.hygounionmerchant.ui.activity.mine.zizhirenzheng.CertificationActivity.PRINCIPAL_FRONT;
import static com.gloiot.hygounionmerchant.ui.activity.mine.zizhirenzheng.CertificationActivity.PRINCIPAL_SHOUCHI;

/**
 * 资质认证--上传单张照片
 * Created by Dlt on 2017/10/17 11:48
 */
public class UploadingSinglePhotoActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.iv_toptitle_back)
    ImageView mIvToptitleBack;
    @Bind(R.id.tv_toptitle_right)
    TextView mTvToptitleRight;
    @Bind(R.id.iv_photo)
    ImageView mIvPhoto;
    @Bind(R.id.tv_warm_prompt)
    TextView mTvWarmPrompt;
    @Bind(R.id.tv_confirm)
    TextView mTvConfirm;

    private String accountType;
    public static final int REQUEST_PICS = 6;
    public static final int RESULT_SINGLEPHOTO = 7;
    private int flag;
    private String photoUrl = "";
    private boolean isHaveUploadedPhoto = false;//是否已经有上传的照片

    @Override
    public int initResource() {
        return R.layout.activity_uploading_single_photo;
    }

    @Override
    public void initData() {
        accountType = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTTYPE, "");

        Intent intent = getIntent();
        flag = intent.getIntExtra("flag", 0);
        photoUrl = intent.getStringExtra("photoUrl");

        switch (flag) {
            //负责人
            case PRINCIPAL_FRONT:
                CommonUtils.setTitleBar(this, false, "负责人身份证正面", "");
                if (!photoUrl.isEmpty()) {
                    CommonUtils.setDisplayImage(mIvPhoto, photoUrl, 0, R.drawable.pic_loading);
                } else {
                    CommonUtils.setDisplayImage(mIvPhoto, "", 0, R.drawable.pic_shenfenzhegnzhegnmian);
                }
//                mTvWarmPrompt.setText("1.需提供负责人有效身份证件，所有信息轮廓清晰可见\n2.务必保证信息内容真实有效，不得做任何涂改、遮挡");
                getWarmPrompt("商户负责人身份证正面");
                break;
            case PRINCIPAL_BACK:
                CommonUtils.setTitleBar(this, false, "负责人身份证反面", "");
                if (!photoUrl.isEmpty()) {
                    CommonUtils.setDisplayImage(mIvPhoto, photoUrl, 0, R.drawable.pic_loading);
                } else {
                    CommonUtils.setDisplayImage(mIvPhoto, "", 0, R.drawable.pic_shenfenzhengfanmian);
                }
//                mTvWarmPrompt.setText("1.需提供负责人有效身份证件，所有信息轮廓清晰可见\n2.不得做任何涂改、遮挡，过期证件无效");
                getWarmPrompt("商户负责人身份证反面");
                break;
            case PRINCIPAL_SHOUCHI:
                CommonUtils.setTitleBar(this, false, "负责人手持身份证", "");
                if (!photoUrl.isEmpty()) {
                    CommonUtils.setDisplayImage(mIvPhoto, photoUrl, 0, R.drawable.pic_loading);
                } else {
                    CommonUtils.setDisplayImage(mIvPhoto, "", 0, R.drawable.pic_shouchishenfenzheng);
                }
//                mTvWarmPrompt.setText("1.负责人手持证件人面部无遮挡，五官清晰可见\n2.身份证各项信息及头像均清晰可见，无遮挡");
                getWarmPrompt("商户负责人手持身份证照片");
                break;
            //法人
            case LEGAL_PERSON_FRONT:
                CommonUtils.setTitleBar(this, false, "法人身份证正面", "");
                if (!photoUrl.isEmpty()) {
                    CommonUtils.setDisplayImage(mIvPhoto, photoUrl, 0, R.drawable.pic_loading);
                } else {
                    CommonUtils.setDisplayImage(mIvPhoto, "", 0, R.drawable.pic_shenfenzhegnzhegnmian);
                }
//                mTvWarmPrompt.setText("1.需提供法人有效身份证件，所有信息轮廓清晰可见\n2.务必保证信息内容真实有效，不得做任何涂改、遮挡");
                getWarmPrompt("商户法人身份证正面");
                break;
            case LEGAL_PERSON_BACK:
                CommonUtils.setTitleBar(this, false, "法人身份证反面", "");
                if (!photoUrl.isEmpty()) {
                    CommonUtils.setDisplayImage(mIvPhoto, photoUrl, 0, R.drawable.pic_loading);
                } else {
                    CommonUtils.setDisplayImage(mIvPhoto, "", 0, R.drawable.pic_shenfenzhengfanmian);
                }
//                mTvWarmPrompt.setText("1.需提供法人有效身份证件，所有信息轮廓清晰可见\n2.不得做任何涂改、遮挡，过期证件无效");
                getWarmPrompt("商户法人身份证反面");
                break;
            case LEGAL_PERSON_SHOUCHI:
                CommonUtils.setTitleBar(this, false, "法人手持身份证", "");
                if (!photoUrl.isEmpty()) {
                    CommonUtils.setDisplayImage(mIvPhoto, photoUrl, 0, R.drawable.pic_loading);
                } else {
                    CommonUtils.setDisplayImage(mIvPhoto, "", 0, R.drawable.pic_shouchishenfenzheng);
                }
//                mTvWarmPrompt.setText("1.法人手持证件人面部无遮挡，五官清晰可见\n2.身份证各项信息及头像均清晰可见，无遮挡");
                getWarmPrompt("商户法人手持身份证照片");
                break;
            //营业执照
            case BUSINESS_LICENSE:
                CommonUtils.setTitleBar(this, false, "营业执照上传", "");
                if (!photoUrl.isEmpty()) {
                    CommonUtils.setDisplayImage(mIvPhoto, photoUrl, 0, R.drawable.pic_loading);
                } else {
                    CommonUtils.setDisplayImage(mIvPhoto, "", 0, R.drawable.pic_yinyizhizhaoshagnchuan);
                }
//                mTvWarmPrompt.setText("请上传清晰彩色原件扫描或者数码照片，如复印需要加盖公章，确保信息展示完整、清晰，并真实有效");
                getWarmPrompt("商户营业执照照片");
                break;
        }
        mIvToptitleBack.setVisibility(View.VISIBLE);

        if (photoUrl.isEmpty()) {
            mTvConfirm.setText("选择图片");
        } else {
            mTvConfirm.setText("重新选择");
        }
    }

    /**
     * 获取相应的温馨提示
     *
     * @param flagString
     */
    private void getWarmPrompt(String flagString) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("提示类别", flagString);
        requestHandleArrayList.add(requestAction.getZizhiWarmPrompt(UploadingSinglePhotoActivity.this, accountType, hashMap));
    }

    @OnClick({R.id.iv_toptitle_back, R.id.tv_toptitle_right, R.id.tv_confirm})
    @Override
    public void onClick(View v) {
        if (NoDoubleClickUtils.isDoubleClick()) return; // 防止连续点击
        switch (v.getId()) {
            case R.id.iv_toptitle_back:
                onBackTouched();
                break;
            case R.id.tv_toptitle_right:
//                if (isHaveUploadedPhoto) {
//                    Intent resultIntent = new Intent();
//                    resultIntent.putExtra("photoUrl", photoUrl);
//                    UploadingSinglePhotoActivity.this.setResult(RESULT_SINGLEPHOTO, resultIntent);//结果码用于标识返回自哪个Activity
//                    UploadingSinglePhotoActivity.this.finish();
//                }

                onCommitClick();
                break;
            case R.id.tv_confirm:

                checkPermission(new CheckPermListener() {
                    @Override
                    public void superPermission() {
                        selectPic();
                    }
                }, R.string.camera_updatepics, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                break;

        }

    }

    /**
     * 选取照片
     */
    private void selectPic() {
        PhotoPicker.builder()
                .setPhotoCount(1)
                .setShowCamera(true)
                .setShowGif(false)
                .setPreviewEnabled(true)
                .start(this, REQUEST_PICS);
    }

    /**
     * 确定按钮点击事件
     */
    private void onCommitClick() {
        if (isHaveUploadedPhoto) {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("照片路径", photoUrl);
            switch (flag) {
                //负责人
                case PRINCIPAL_FRONT:
                    hashMap.put("照片类别", "商户负责人身份证正面");
                    break;
                case PRINCIPAL_BACK:
                    hashMap.put("照片类别", "商户负责人身份证反面");
                    break;
                case PRINCIPAL_SHOUCHI:
                    hashMap.put("照片类别", "商户负责人手持身份证照片");
                    break;
                //法人
                case LEGAL_PERSON_FRONT:
                    hashMap.put("照片类别", "商户法人身份证正面");
                    break;
                case LEGAL_PERSON_BACK:
                    hashMap.put("照片类别", "商户法人身份证反面");
                    break;
                case LEGAL_PERSON_SHOUCHI:
                    hashMap.put("照片类别", "商户法人手持身份证照片");
                    break;
                //营业执照
                case BUSINESS_LICENSE:
                    hashMap.put("照片类别", "商户营业执照照片");
                    break;
            }
            uploadingPhoto(hashMap);

        }
    }

    /**
     * 上传资质认证照片
     *
     * @param hashMap
     */
    private void uploadingPhoto(HashMap<String, Object> hashMap) {
        requestHandleArrayList.add(requestAction.uploadingZizhiPhoto(UploadingSinglePhotoActivity.this, accountType, hashMap));
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

                    new UploadingSelectedPics(mContext, photos) {
                        @Override
                        protected void setSinglePicSuccess(String picUrl) {
                            photoUrl = picUrl;
                            UploadingSinglePhotoActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    PictureUtlis.loadImageViewHolder(mContext, photoUrl, R.drawable.default_image, mIvPhoto);
                                    isHaveUploadedPhoto = true;
                                    mTvToptitleRight.setText("确定");
                                    mTvToptitleRight.setTextColor(Color.parseColor("#449FFB"));
                                    mTvConfirm.setText("重新选择");
                                }
                            });
                        }

                        @Override
                        protected void setSinglePicFailure() {
                            L.e("上传照片", "上传图片失败");
                            MToast.showToast(mContext, "上传照片失败，请稍后再试");
                        }
                    }.setSinglePic();


//                    //控制上传的图片小于5M
//                    Tiny.FileCompressOptions compressOptions = new Tiny.FileCompressOptions();
//                    compressOptions.size = 5000;
//                    compressOptions.config = Bitmap.Config.ARGB_8888;
//
//                    new UploadingSelectedPics(mContext, photos, compressOptions) {
//                        @Override
//                        protected void setSinglePicSuccess(String picUrl) {
//                            photoUrl = picUrl;
//                            UploadingSinglePhotoActivity.this.runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    PictureUtlis.loadImageViewHolder(mContext, photoUrl, R.drawable.default_image, mIvPhoto);
//                                    isHaveUploadedPhoto = true;
//                                    mTvToptitleRight.setText("确定");
//                                    mTvConfirm.setText("重新选择");
//                                }
//                            });
//                        }
//
//                        @Override
//                        protected void setSinglePicFailure() {
//                            L.e("上传照片", "上传图片失败");
//                        }
//                    }.setSinglePicWithCompress();

                    break;
            }
        }
    }


    @Override
    public void requestSuccess(int requestTag, JSONObject response, int showLoad) throws JSONException {
        super.requestSuccess(requestTag, response, showLoad);
        switch (requestTag) {
            case RequestAction.TAG_ZIZHIWARMPROMPT://温馨提示
                L.e("温馨提示", response.toString());
                mTvWarmPrompt.setText(response.getString("温馨提示"));
                break;
            case RequestAction.TAG_ZIZHIUPLOADINGPHOTO://上传照片
                Intent resultIntent = new Intent();
                resultIntent.putExtra("photoUrl", photoUrl);
                UploadingSinglePhotoActivity.this.setResult(RESULT_SINGLEPHOTO, resultIntent);//结果码用于标识返回自哪个Activity
                UploadingSinglePhotoActivity.this.finish();
                break;
            default:
                break;
        }
    }

    /**
     * 返回按钮事件
     */
    private void onBackTouched() {
        if (isHaveUploadedPhoto) {

            final MyNewDialogBuilder myDialogBuilder = MyNewDialogBuilder.getInstance(mContext);
            myDialogBuilder
                    .withContene("请保存您的修改信息，返回将放弃修改!")
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

                            onCommitClick();
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

    //重写系统返回键事件
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {//其中event.getRepeatCount() == 0 是重复次数，点返回键时，防止点的过快，触发两次后退事件，做此设置
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {//保留这个判断，增强程序健壮性
            onBackTouched();
        }
        return false;
    }

}
