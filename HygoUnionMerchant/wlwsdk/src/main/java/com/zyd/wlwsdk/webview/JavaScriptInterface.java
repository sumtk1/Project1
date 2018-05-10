/**
 * Summary: js脚本所能执行的函数空间
 * Version 1.0
 * Date: 13-11-20
 * Time: 下午4:40
 * Copyright: Copyright (c) 2013
 */

package com.zyd.wlwsdk.webview;

import android.text.TextUtils;
import android.webkit.WebView;

import com.zyd.wlwsdk.utils.JSONUtlis;
import com.zyd.wlwsdk.utils.L;
import com.zyd.wlwsdk.webview.safewebview.JsCallback;

import org.json.JSONException;
import org.json.JSONObject;

public class JavaScriptInterface {
    public static String mFailingUrl;
    private WebView mWebView;
    private WebCallback webCallback;

    public final static int TAG_QRCODE = 1;         // 扫二维码
    public final static int TAG_CLOSEWEB = 3;       // 关闭网页
    public final static int TAG_UNABLEGOBACK = 4;   // 返回键退出网页
    public final static int TAG_ADDRESSBOOK = 5;    // 获取通讯录
    public final static int TAG_LOGIN = 6;          // 调起登录
    public final static int TAG_SHOPINFO = 7;       // 商品详情
    public final static int TAG_GETPHOTO = 8;       // 获取手机图片
    public final static int TAG_VIDEO = 9;          // 视频播放器
    public final static int TAG_WEBVIEW = 10;       // 重新打开WEBVIEW
    public final static int TAG_APPTYPE = 11;       // APP类型
    public final static int TAG_PAGETYPE = 12;      // 页面类型
    public final static int TAG_APPVERSION = 13;    // APP版本
    public final static int TAG_TITLE = 14;         // 改变webview标题
    public final static int TAG_WXPAY = 101;        // 微信支付
    public final static int TAG_ALIPAY = 102;       // 支付宝支付
    public final static int TAG_PWDBOX = 103;       // 支付密码输入框
    public final static int TAG_GETCODE = 104;       // 扫描条形码


    public JavaScriptInterface(WebView webView, WebCallback webCallback) {
        mWebView = webView;
        this.webCallback = webCallback;
    }


    // 网页请求回调
    public interface WebCallback {
        void webCallback(int type, JSONObject jsonObject);
    }

    // 二维码扫描
    @android.webkit.JavascriptInterface
    public void qrcode(JSONObject jsonObject, final JsCallback jsCallback) {
        try {
            L.e("--", "pay" + jsonObject);
            String key = JSONUtlis.getString(jsonObject, "key");
            webCallback.webCallback(TAG_QRCODE, jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // 支付
    @android.webkit.JavascriptInterface
    public void pay(String jsonObject) {
        L.e("--", "pay" + jsonObject);
        try {
            JSONObject j = new JSONObject(jsonObject);
            String payType = j.getString("pay_type");
            switch (payType) {
                case "wx":
                    webCallback.webCallback(TAG_WXPAY, j);
                    break;
                case "alipay":
                    webCallback.webCallback(TAG_ALIPAY, j);
                    break;
                case "pwd_box":
                    webCallback.webCallback(TAG_PWDBOX, j);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // 刷新
    @android.webkit.JavascriptInterface
    public void refresh() {
        mWebView.loadUrl(TextUtils.isEmpty(mFailingUrl) ? "" : mFailingUrl);
    }


    // func各种方法
    @android.webkit.JavascriptInterface
    public void func(final String jsonObject) {
        try {
            int tag = 0;
            JSONObject j = new JSONObject(jsonObject);
            L.e("------------", j + "");
            switch (j.getString("type")) {
                case "closeweb":
                    // 关闭网页
                    tag = TAG_CLOSEWEB;
                    break;
                case "unablegoback":
                    // 网页不可返回
                    tag = TAG_UNABLEGOBACK;
                    break;
                case "addressBook":
                    // 获取通讯录联系人
                    tag = TAG_ADDRESSBOOK;
                    break;
                case "login":
                    // 跳转原生登录
                    tag = TAG_LOGIN;
                    break;
                case "shopinfo":
                    // 跳转商品详情
                    tag = TAG_SHOPINFO;
                    break;
                case "getphoto":
                    // 获取手机相册或拍照
                    tag = TAG_GETPHOTO;
                    break;
                case "getcode":
                    // 获取条形码
                    tag = TAG_GETCODE;
                    break;
                case "video":
                    // 手机视频播放器
                    tag = TAG_VIDEO;
                    break;
                case "webview":
                    tag = TAG_WEBVIEW;
                    break;
                case "appType":
                    tag = TAG_APPTYPE;
                    break;
                case "PageType":
                    tag = TAG_PAGETYPE;
                    break;
                case "appVersion":
                    tag = TAG_APPVERSION;
                    break;
                case "title":
                    tag = TAG_TITLE;
                    break;
            }
            webCallback.webCallback(tag, j);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}