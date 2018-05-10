package com.gloiot.hygounionmerchant.ui.activity.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

import com.gloiot.hygounionmerchant.R;
import com.gloiot.hygounionmerchant.ui.activity.MainActivity;
import com.gloiot.hygounionmerchant.utils.CommonUtils;
import com.gloiot.hygounionmerchant.utils.ConstantUtils;
import com.zyd.wlwsdk.autolayout.AutoLayoutActivity;
import com.zyd.wlwsdk.utils.L;
import com.zyd.wlwsdk.utils.SharedPreferencesUtils;

import java.util.HashMap;

/**
 * Created by Dlt on 2017/8/16 9:46
 */
public class SplashActivity extends AutoLayoutActivity {

    private String phoneId, phoneName, phoneType;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 设置无标题
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        setContentView(R.layout.activity_splash);
        mContext = this;

        //如果程序在运行，点home键后，点击程序图标，防止再次启动该activity
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }

        getPhoneInfo();

        String versionName = CommonUtils.getVersionName(mContext);
        SharedPreferencesUtils.setString(mContext, ConstantUtils.SP_VERSIONNAME, versionName);
        SharedPreferencesUtils.setString(mContext, ConstantUtils.VERSION, versionName);
        L.e("版本名", versionName);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                String type = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_LOGINSTATE, "");//登录状态
//                String authenticationState = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_AUTHENTIFICATIONSTATE, "");//认证状态
////                boolean isGuideshowed = SharedPreferencesUtils.getBoolean(mContext, ConstantUtils.SP_ISGUIDEPAGESHOWED, false);
//
//                boolean isGuideshowed = true;//第一个版本没时间做引导页了。20170717.
//
//                if (isGuideshowed) {
                if (type.equals("成功")) {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
//                        SharedPreferencesUtils.setBoolean(mContext, ConstantUtils.SP_ISFROMLOGINTOMAIN, true);//从登录页进入主页
                    SplashActivity.this.finish();

                } else {
                    Intent intent = new Intent(SplashActivity.this, SelectLoginTypeActivity.class);
                    startActivity(intent);
                    SplashActivity.this.finish();
                }
//                } else {
//                    startActivity(new Intent(SplashActivity.this, GuideActivity.class));
//                    SplashActivity.this.finish();
//                }

//                startActivity(new Intent(SplashActivity.this, SelectLoginTypeActivity.class));
//                SplashActivity.this.finish();

            }
        }, 1000);

    }

    private void getPhoneInfo() {
        if (SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_PHENEID, "").isEmpty()) {
//            phoneId = ((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).getDeviceId();
            phoneId = "";
            phoneName = "Android " + android.os.Build.MODEL;
            phoneType = "Android";

            SharedPreferencesUtils.setInt(mContext, ConstantUtils.SP_PHONEHEIGHT, CommonUtils.getScreenHeight(this));
            SharedPreferencesUtils.setString(mContext, ConstantUtils.SP_PHENEID, phoneId);
            SharedPreferencesUtils.setString(mContext, ConstantUtils.SP_PHONEINFO_KV, "\n手机ID=" + phoneId + "\n手机名称=" + phoneName + "\n手机型号=" + phoneType);

//            String versionName = CommonUtils.getVersionName(mContext);

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("手机ID", phoneId);
            hashMap.put("手机名称", phoneName);
            hashMap.put("手机型号", phoneType);
//            hashMap.put("版本号", versionName);
            CommonUtils.saveMap(ConstantUtils.SP_PHONEINFO_JSON, hashMap);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

}
