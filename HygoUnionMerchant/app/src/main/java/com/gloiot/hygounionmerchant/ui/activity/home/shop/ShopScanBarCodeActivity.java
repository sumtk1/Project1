package com.gloiot.hygounionmerchant.ui.activity.home.shop;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gloiot.hygounionmerchant.R;
import com.gloiot.hygounionmerchant.ui.BaseActivity;
import com.gloiot.hygounionmerchant.utils.CommonUtils;
import com.zyd.wlwsdk.utils.MToast;
import com.zyd.wlwsdk.widge.MyNewDialogBuilder;
import com.zyd.wlwsdk.zxing.activity.CaptureFragment;
import com.zyd.wlwsdk.zxing.activity.CodeUtils;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 小铺扫描条形码页面
 * Created by Dlt on 2017/8/19 13:50
 */
public class ShopScanBarCodeActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.iv_toptitle_more_img)
    ImageView mIvToptitleMoreImg;
    @Bind(R.id.fl_my_container)
    FrameLayout mFlMyContainer;
    @Bind(R.id.ll_manual_input)
    LinearLayout mLlManualInput;
    @Bind(R.id.tv_hint_message)
    TextView mTvHintMessage;

    private CaptureFragment captureFragment;
    private boolean isFlashlightOpen = false;//闪光灯是否打开
    private MyNewDialogBuilder myDialogBuilder;

    private String backResult = "";
    private String flag = "";//取值：QRcode/barcode

    @Override
    public int initResource() {
        return R.layout.activity_shop_scan_bar_code;
    }

    @Override
    public void initData() {
        captureFragment = new CaptureFragment();
        // 为二维码扫描界面设置定制化界面
        CodeUtils.setFragmentArgs(captureFragment, R.layout.my_camera);
        captureFragment.setAnalyzeCallback(analyzeCallback);
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_my_container, captureFragment).commit();
        CommonUtils.setTitleBar(this, true, "扫一扫", "");
        mIvToptitleMoreImg.setImageResource(R.drawable.ic_shoudiantong);
        Intent intent = getIntent();
        flag = intent.getStringExtra("flag");
        if (flag.equals("QRcode")) {
            mTvHintMessage.setText("将二维码放入框内，即可自动扫描");
            mLlManualInput.setVisibility(View.GONE);
        } else if (flag.equals("barcode")) {
            mTvHintMessage.setText("将条形码放入框内，即可自动扫描");
            mLlManualInput.setVisibility(View.VISIBLE);
        }
    }

    @OnClick({R.id.iv_toptitle_more_img, R.id.ll_manual_input})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_toptitle_more_img:
                if (!isFlashlightOpen) {
                    CodeUtils.isLightEnable(true);
                    isFlashlightOpen = true;
                    mIvToptitleMoreImg.setImageResource(R.drawable.ic_shoudiantong_sel);
                } else {
                    CodeUtils.isLightEnable(false);
                    isFlashlightOpen = false;
                    mIvToptitleMoreImg.setImageResource(R.drawable.ic_shoudiantong);
                }
                break;
            case R.id.ll_manual_input:
//                Intent intent = new Intent(ScanBarCodeActivity.this, AddBarCodeActivity.class);
//                intent.putExtra("barCodeType", "manual");
//                startActivity(intent);
//                finish();


                myDialogBuilder = MyNewDialogBuilder.getInstance(mContext);
                myDialogBuilder.setCustomView(R.layout.layout_shop_barcode_edittext);
                myDialogBuilder
                        .withCenterContent("输入条形码，获取商品信息")
                        .withEffects(MyNewDialogBuilder.SlideTop, MyNewDialogBuilder.SlideTopDismiss)
                        .setTwoBtnSpecial("取消", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                myDialogBuilder.dismiss();

                            }
                        }, "确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                View view = myDialogBuilder.getCustomView();
                                EditText editText = (EditText) view.findViewById(R.id.et_input);
//                                editText.addTextChangedListener(new MaxLengthWatcher(13, editText));//最多13个字符

                                String result = editText.getText().toString().trim();

                                if (TextUtils.isEmpty(result)) {
                                    MToast.showToast(mContext, "条形码不能为空");
                                } else {

                                    myDialogBuilder.dismissNoAnimator();

                                    Intent resultIntent = new Intent();
                                    Bundle bundle = new Bundle();
                                    bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_SUCCESS);
                                    bundle.putString(CodeUtils.RESULT_STRING, result);
                                    resultIntent.putExtras(bundle);
                                    ShopScanBarCodeActivity.this.setResult(RESULT_OK, resultIntent);
                                    ShopScanBarCodeActivity.this.finish();

//                                    backResult = result;
//                                    requestHandleArrayList.add(requestAction.getShopBarCodeMessage(ScanBarCodeActivity.this, result));//查询该条码信息，判断有无添加过
                                }

                            }
                        })
                        .show();


                break;
        }
    }


    /**
     * 二维码解析回调函数
     */
    CodeUtils.AnalyzeCallback analyzeCallback = new CodeUtils.AnalyzeCallback() {
        @Override
        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
            Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_SUCCESS);
            bundle.putString(CodeUtils.RESULT_STRING, result);
            resultIntent.putExtras(bundle);
            ShopScanBarCodeActivity.this.setResult(RESULT_OK, resultIntent);
            ShopScanBarCodeActivity.this.finish();

        }

        @Override
        public void onAnalyzeFailed() {
            Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_FAILED);
            bundle.putString(CodeUtils.RESULT_STRING, "");
            resultIntent.putExtras(bundle);
            ShopScanBarCodeActivity.this.setResult(RESULT_OK, resultIntent);
            ShopScanBarCodeActivity.this.finish();
        }
    };

}
