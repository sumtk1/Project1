package com.zyd.wlwsdk.webview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.GeolocationPermissions;
import android.webkit.JsPromptResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zyd.wlwsdk.webview.safewebview.SafeWebView;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by JinzLin on 2016/10/7.
 */

public class InnerWebView extends SafeWebView {


    private TextView tv_title;
    private View errorView;
    private ProgressBar pb;
    private Context context;
    private JavaScriptInterface.WebCallback webCallback;

    private Activity webActivity;
    private View customView;
    private FrameLayout fullscreenContainer;
    private WebChromeClient.CustomViewCallback customViewCallback;
    private InnerWebView innerWebView;
    protected static final FrameLayout.LayoutParams COVER_SCREEN_PARAMS = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);


    public InnerWebView(Context context, TextView tv_title, View errorView, ProgressBar pb, JavaScriptInterface.WebCallback webCallback) {
        super(context);
        this.context = context;
        this.tv_title = tv_title;
        this.errorView = errorView;
        this.pb = pb;
        this.webCallback = webCallback;
        innerWebView = this;
        webActivity = (Activity) context;

        WebSettings ws = getSettings();
        ws.setDefaultTextEncodingName("utf-8");
//        ws.setAppCacheEnabled(true); // 开启缓存机制
//        ws.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); // 设置缓存模式


        //启用数据库
        ws.setDatabaseEnabled(true);
        String dir = context.getDir("database", Context.MODE_PRIVATE).getPath();
        //启用地理定位
        ws.setGeolocationEnabled(true);
        //设置定位的数据库路径
        ws.setGeolocationDatabasePath(dir);
        //最重要的方法，一定要设置，这就是出不来的主要原因
        //配置权限（同样在WebChromeClient中实现）
        ws.setDomStorageEnabled(true); // 开启 DOM storage API 功能
        ws.setJavaScriptEnabled(true);  //支持js
        ws.setAllowFileAccess(true);  //设置可以访问文件
        ws.setUseWideViewPort(false);  //将图片调整到适合webview的大小
        ws.setSupportZoom(true);  //支持缩放
        ws.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN); //支持内容重新布局
//        ws.supportMultipleWindows();  //多窗口
//        ws.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);  //关闭webview中缓存
        ws.setNeedInitialFocus(true); //当webview调用requestFocus时为webview设置节点
//        ws.setBuiltInZoomControls(true); //设置支持缩放
        ws.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        ws.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        ws.setLoadsImagesAutomatically(true);  //支持自动加载图片
        ws.setCacheMode(WebSettings.LOAD_NO_CACHE); // 不加载缓存内容

        //安卓5.0以后，默认不允许混合模式，https中不能加载http的资源,需要加上这个设置
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ws.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        addJavascriptInterface(new JavaScriptInterface(this, webCallback), "Android");
        setWebChromeClient(new InnerWebChromeClient());
        setWebViewClient(new InnerWebViewClient());

    }


    public class InnerWebChromeClient extends SafeWebView.SafeWebChromeClient {

        boolean mIsInjectedJS;

        @Override
        public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
            callback.invoke(origin, true, false);
            super.onGeolocationPermissionsShowPrompt(origin, callback);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            tv_title.setText(title);
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress); // 务必放在方法体的第一行执行；
            //为什么要在这里注入JS
            //1 OnPageStarted中注入有可能全局注入不成功，导致页面脚本上所有接口任何时候都不可用
            //2 OnPageFinished中注入，虽然最后都会全局注入成功，但是完成时间有可能太晚，当页面在初始化调用接口函数时会等待时间过长
            //3 在进度变化时注入，刚好可以在上面两个问题中得到一个折中处理
            //为什么是进度大于25%才进行注入，因为从测试看来只有进度大于这个数字页面才真正得到框架刷新加载，保证100%注入成功
            if (newProgress <= 25) {
                mIsInjectedJS = false;
            } else if (!mIsInjectedJS) {
                try {
                    InputStream is = context.getAssets().open("index.txt");
                    int size = is.available();
                    byte[] buffer = new byte[size];
                    is.read(buffer);
                    is.close();
                    String text = new String(buffer, "UTF-8");
                    view.loadUrl("javascript:" + text);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                mIsInjectedJS = true;
            }

            pb.setProgress(newProgress);
            if (newProgress == 100) {
                pb.setVisibility(View.GONE);
            } else {
                pb.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
            return super.onJsPrompt(view, url, message, defaultValue, result); // 务必放在方法体的最后一行执行，或者用if判断也行；
        }

        /*** 视频播放相关的方法 **/
        @Override
        public View getVideoLoadingProgressView() {
            FrameLayout frameLayout = new FrameLayout(webActivity);
            frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            return frameLayout;
        }

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            showCustomView(view, callback);
        }

        @Override
        public void onHideCustomView() {
            hideCustomView();
        }
    }

    /**
     * 视频播放全屏
     **/
    private void showCustomView(View view, WebChromeClient.CustomViewCallback callback) {
        // if a view already exists then immediately terminate the new one
        if (customView != null) {
            callback.onCustomViewHidden();
            return;
        }

        webActivity.getWindow().getDecorView();
        FrameLayout decor = (FrameLayout) webActivity.getWindow().getDecorView();
        fullscreenContainer = new InnerWebView.FullscreenHolder(webActivity);
        fullscreenContainer.addView(view, COVER_SCREEN_PARAMS);
        decor.addView(fullscreenContainer, COVER_SCREEN_PARAMS);
        customView = view;
        setStatusBarVisibility(false);
        customViewCallback = callback;
    }

    /**
     * 隐藏视频全屏
     */
    private void hideCustomView() {
        if (customView == null) {
            return;
        }
        setStatusBarVisibility(true);
        FrameLayout decor = (FrameLayout) webActivity.getWindow().getDecorView();
        decor.removeView(fullscreenContainer);
        fullscreenContainer = null;
        customView = null;
        customViewCallback.onCustomViewHidden();
        innerWebView.setVisibility(View.VISIBLE);
    }

    /**
     * 全屏容器界面
     */
    static class FullscreenHolder extends FrameLayout {
        public FullscreenHolder(Context ctx) {
            super(ctx);
            setBackgroundColor(ctx.getResources().getColor(android.R.color.black));
        }

        @Override
        public boolean onTouchEvent(MotionEvent evt) {
            return true;
        }
    }

    private void setStatusBarVisibility(boolean visible) {
        int flag = visible ? 0 : WindowManager.LayoutParams.FLAG_FULLSCREEN;
        webActivity.getWindow().setFlags(flag, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private class InnerWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.equals(UnsafeWebActivity.HTML)) {
                view.getContext().startActivity(new Intent(view.getContext(), UnsafeWebActivity.class));
                return true;
            }

            return super.shouldOverrideUrlLoading(view, url);
        }


        // 加载失败显示的界面
        @Override
        public void onReceivedError(final WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            errorView.setVisibility(View.VISIBLE);
            view.loadUrl("file:///android_asset/warning.html");
        }

    }

}
