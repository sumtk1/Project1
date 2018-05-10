package com.gloiot.hygounionmerchant.ui.webview;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gloiot.hygounionmerchant.R;
import com.just.library.AgentWeb;
import com.just.library.ChromeClientCallbackManager;
import com.just.library.LogUtils;
import com.zyd.wlwsdk.autolayout.AutoLayoutActivity;
import com.zyd.wlwsdk.utils.L;
import com.zyd.wlwsdk.utils.StatusBarUtil;

public class BaseWebviewActivity extends AutoLayoutActivity {

    protected AgentWeb mAgentWeb;
    private LinearLayout mLinearLayout;
    private TextView mTitleTextView;
    private AlertDialog mAlertDialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_base_webview);


        mLinearLayout = (LinearLayout) this.findViewById(R.id.container);
        mTitleTextView = (TextView) this.findViewById(R.id.tv_toptitle_title);

        this.findViewById(R.id.iv_toptitle_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //                showDialog();
                BaseWebviewActivity.this.finish();
            }
        });


        long p = System.currentTimeMillis();

        mAgentWeb = AgentWeb.with(this)//
                .setAgentWebParent(mLinearLayout, new LinearLayout.LayoutParams(-1, -1))//
                .useDefaultIndicator()//
                .defaultProgressBarColor()
                .setReceivedTitleCallback(mCallback)
                .setWebChromeClient(mWebChromeClient)
                .setWebViewClient(mWebViewClient)
                .setSecutityType(AgentWeb.SecurityType.strict)
                .setWebLayout(new WebLayout(this))
                .createAgentWeb()//
                .ready()
                .go(getUrl());

        //mAgentWeb.getLoader().loadUrl(getUrl());

        long n = System.currentTimeMillis();
        L.e("Webview初始化耗时", "init used time:" + (n - p));


//        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.white));

        StatusBarUtil.setColor(this, getResources().getColor(R.color.black), 66);

    }

    private WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            //do you  work
            Log.i("Info", "BaseWebActivity onPageStarted");
        }
    };
    private WebChromeClient mWebChromeClient = new WebChromeClient() {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            //do you work
//            Log.i("Info","progress:"+newProgress);
        }
    };

    public String getUrl() {

//        return "https://m.jd.com/";
//        return "https://baidu.com";
//        return UrlUtlis.SHIYONGSHUOMINGURL;

//        L.e("url:", UrlUtlis.BASEURL + "/shop/appShop_selectPay.html?loginName=" + SharedPreferencesUtils.getString(this, ConstantUtlis.SP_USEACCOUNT, "")
//                + "&rondom=" + SharedPreferencesUtils.getString(this, ConstantUtlis.SP_RANDOMCODE, ""));

//        return UrlUtlis.BASEURL + "/shop/appShop_selectPay.html?loginName=" + SharedPreferencesUtils.getString(this, ConstantUtlis.SP_USEACCOUNT, "")
//                + "&rondom=" + SharedPreferencesUtils.getString(this, ConstantUtlis.SP_RANDOMCODE, "");


        return "https://m.jd.com/";

    }

    private ChromeClientCallbackManager.ReceivedTitleCallback mCallback = new ChromeClientCallbackManager.ReceivedTitleCallback() {
        @Override
        public void onReceivedTitle(WebView view, String title) {
            if (mTitleTextView != null)
                mTitleTextView.setText(title);
        }
    };


    private void showDialog() {

        if (mAlertDialog == null)
            mAlertDialog = new AlertDialog.Builder(this)
                    .setMessage("您确定要关闭该页面吗?")
                    .setNegativeButton("再逛逛", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (mAlertDialog != null)
                                mAlertDialog.dismiss();
                        }
                    })//
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            if (mAlertDialog != null)
                                mAlertDialog.dismiss();

                            BaseWebviewActivity.this.finish();
                        }
                    }).create();
        mAlertDialog.show();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (mAgentWeb.handleKeyEvent(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        mAgentWeb.getWebLifeCycle().onPause();
        super.onPause();

    }

    @Override
    protected void onResume() {
        mAgentWeb.getWebLifeCycle().onResume();
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        LogUtils.i("Info", "result:" + requestCode + " result:" + resultCode);
        mAgentWeb.uploadFileResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //mAgentWeb.destroy();
        mAgentWeb.getWebLifeCycle().onDestroy();
    }

}
