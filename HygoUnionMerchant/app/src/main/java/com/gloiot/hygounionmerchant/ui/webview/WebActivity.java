package com.gloiot.hygounionmerchant.ui.webview;

import android.Manifest;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.abcaaa.photopicker.PhotoPicker;
import com.gloiot.hygounionmerchant.R;
import com.gloiot.hygounionmerchant.server.picture.UploadingSelectedPics;
import com.gloiot.hygounionmerchant.ui.BaseActivity;
import com.gloiot.hygounionmerchant.ui.activity.home.shop.ShopScanBarCodeActivity;
import com.gloiot.hygounionmerchant.utils.ConstantUtils;
import com.zxy.tiny.Tiny;
import com.zyd.wlwsdk.thirdpay.AliPay;
import com.zyd.wlwsdk.utils.L;
import com.zyd.wlwsdk.utils.MToast;
import com.zyd.wlwsdk.webview.InnerWebView;
import com.zyd.wlwsdk.webview.JavaScriptInterface;
import com.zyd.wlwsdk.widge.ActionSheet.AndroidActionSheetFragment;
import com.zyd.wlwsdk.zxing.activity.CodeUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Dlt on 2017/8/29 16:10
 */
public class WebActivity extends BaseActivity implements View.OnClickListener, JavaScriptInterface.WebCallback {

    @Bind(R.id.img_title_back)
    ImageView imgTitleBack; // 网页返回按钮
    @Bind(R.id.img_title_close)
    ImageView imgTitleClose; // 网页返回按钮
    @Bind(R.id.img_title_more)
    ImageView imgTitleMore; // 更多按钮
    @Bind(R.id.tv_title)
    TextView tvTitle; // 标题
    @Bind(R.id.pb)
    ProgressBar pb; // 进度条
    @Bind(R.id.tv_time)
    TextView tvTime;
    @Bind(R.id.fl)
    FrameLayout fl;
    @Bind(R.id.v_onpause)
    View v_onpause; // onPause时变暗效果

    WebView webView;
    View errorView;

    public static final int REQUEST_CODE_GOODSPIC = 5;
    public static final int REQUEST_CODE_BARCODE_FOR_BARCODE = 113;

    private String URL = ""; // 网页url
    private String guid = ""; // 网页调用原生方法生成的唯一识别id
    //    private String webviewUrl = ""; // 用于存储上一次浏览的网页地址
    private boolean unableGoBack = false; // 不可返回

    @Override
    public void onResume() {
        super.onResume();
        v_onpause.setVisibility(View.GONE);
//        webView.resumeTimers();
        String state = preferences.getString(ConstantUtils.SP_PAYTYPE, "");
        if (state.equals("成功")) {
            payCallBack();

            editor.putString(ConstantUtils.SP_PAYTYPE, "");
            editor.commit();
        }
    }

    @Override
    public int initResource() {
        return R.layout.activity_web;
    }

    @Override
    public void initData() {
        URL = getIntent().getStringExtra("url");
        if (!URL.contains("http")) {
            URL = "http://" + URL;
        }
        L.e(URL);
        webView = new InnerWebView(this, tvTitle, errorView, pb, this);
        pb.setMax(100); // 设置进度条
        fl.addView(webView); // 添加网页

        webView.loadUrl(URL);
//        webView.loadUrl("file:///android_asset/index.html"); // 本地测试地址
        webView.setWebViewClient(new WebViewClient() {
            //  重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("tel:")) {   // 跳转拨打电话界面
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                    startActivity(intent);
                    return true;
                }

                return !(url.startsWith("http:") || url.startsWith("https:"));
            }

            @Override
            public void onPageCommitVisible(WebView view, String url) {
                super.onPageCommitVisible(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (url.contains(URL)) {
                    imgTitleClose.setVisibility(View.GONE);
                } else {
                    imgTitleClose.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                JavaScriptInterface.mFailingUrl = failingUrl;
                L.e(failingUrl);
                view.loadUrl("file:///android_asset/warning.html");
            }
        });
    }

    @OnClick({R.id.img_title_back, R.id.img_title_close, R.id.img_title_more})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_title_more:
                AndroidActionSheetFragment.build(getSupportFragmentManager())
                        .setTag("Activity_WebView")
                        .setItems2(new String[]{"刷新", "投诉", "在浏览器中打开"})
                        .setImages2(new int[]{R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher})
                        .setOnItemClickListenerBottom(new AndroidActionSheetFragment.OnItemClickListener() {
                            @Override
                            public void onItemClick(int position) {
                                switch (position) {
                                    case 0:
                                        webView.loadUrl(webView.getUrl());
                                        break;
                                    case 2:
                                        try {
                                            Uri uri = Uri.parse(webView.getUrl()); //url为你要链接的地址
                                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                            startActivity(intent);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        break;
                                }
                            }
                        }).show();
                break;
            case R.id.img_title_back:
                finishWeb();
                // 网页如果可以返回上一页则返回，否则关闭网页浏览器
                Log.e("web", webView.canGoBack() + "--" + !unableGoBack);
                if (webView.canGoBack() && !unableGoBack) {
//                    webviewUrl = webView.getUrl();
                    webView.goBack();
                } else {
                    finish();
                }
                break;
            case R.id.img_title_close:
                finishWeb();
                // 关闭网页浏览器
                finish();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        finishWeb();
        // 返回键监听 点击返回如果有上一页面不会马上退出
        if (keyCode == KeyEvent.KEYCODE_BACK && !unableGoBack && webView.canGoBack()) {
//            webviewUrl = webView.getUrl();
            webView.goBack();
            return true;
        }
        if (unableGoBack) {
            unableGoBack = false;
        }
        Log.e("web3", unableGoBack + "");
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void webCallback(int type, JSONObject jsonObject) {
        try {
            guid = jsonObject.getString("guid"); // 网页调用原生方法生成的唯一识别id
        } catch (JSONException e) {
            e.printStackTrace();
            MToast.showToast(mContext, "服务器繁忙-1，请稍后再试");
        }
        switch (type) {
            // 扫描二维码
            case JavaScriptInterface.TAG_QRCODE:
                checkPermission(new CheckPermListener() {
                    @Override
                    public void superPermission() {
//                        Intent intent = new Intent(getApplication(), SaoMiaoActivity.class);
//                        startActivityForResult(intent, 111);
                    }
                }, R.string.perm_camera, Manifest.permission.CAMERA, Manifest.permission.VIBRATE);
                break;
            // 微信支付
            case JavaScriptInterface.TAG_WXPAY:
//                new WXPay(mContext, jsonObject.toString()).startPay();
                break;
            // 支付宝支付
            case JavaScriptInterface.TAG_ALIPAY:
                String retrunInfo = null;
                try {
                    retrunInfo = jsonObject.getString("return_info");
                    new AliPay(mContext, retrunInfo) {
                        @Override
                        public void paySuccess() {
                            payCallBack();
                            L.e("支付宝", "支付成功");
                        }

                        @Override
                        public void payError(boolean flag) {
                            if (flag)
                                MToast.showToast(mContext, "支付取消");
                            else
                                MToast.showToast(mContext, "支付失败");
                        }
                    };
                } catch (JSONException e) {
                    e.printStackTrace();
                    MToast.showToast(mContext, "服务器繁忙-1，请稍后再试");
                }
                break;
            // 输入密码框
            case JavaScriptInterface.TAG_PWDBOX:
//                try {
//                    String money = jsonObject.getString("money");
//                    DialogUtlis.oneBtnPwd(mContext, money, new DialogUtlis.PasswordCallback() {
//                        @Override
//                        public void callback(String data) {
//                            webView.loadUrl("javascript:p_golbal['" + guid + "']('{\"密码\":\"" + MD5Utlis.Md5(data) + "\"}')");
//                            guid = "";
//                        }
//                    });
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    MToast.showToast(mContext, "服务器繁忙-1，请稍后再试");
//                }
                break;
            // 关闭网页浏览器
            case JavaScriptInterface.TAG_CLOSEWEB:
                finishWeb();
                finish();
                break;
            // 返回键关闭网页浏览器
            case JavaScriptInterface.TAG_UNABLEGOBACK:
                unableGoBack = true;
                imgTitleClose.setVisibility(View.VISIBLE);
                imgTitleBack.setVisibility(View.GONE);
                finishWeb();
                break;
            // 获取通讯录联系人
            case JavaScriptInterface.TAG_ADDRESSBOOK:
//                checkPermission(new CheckPermListener() {
//                    @Override
//                    public void superPermission() {
//                        startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), TAG_ADDRESSBOOK_BACK);
//                    }
//                }, R.string.perm_contacts, Manifest.permission.READ_CONTACTS);
                break;
            // 跳转登录页
            case JavaScriptInterface.TAG_LOGIN:
//                if (preferences.getString(ConstantUtils.SP_LOGINTYPE, "").equals("成功")) {
//                    webView.loadUrl("javascript:p_golbal['" + guid + "']('{\"状态\":\"失败\"}')");
//                    guid = "";
//                } else {
//                    startActivityForResult(new Intent(WebActivity.this, LoginActivity.class), TAG_LOGIN_BACK);
//                }
                break;
            // 跳转商品详情
            case JavaScriptInterface.TAG_SHOPINFO:
//                String shopid;
//                try {
//                    shopid = jsonObject.getString("shopid");
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    shopid = "";
//                }
//                Intent intent = new Intent(WebActivity.this, ShangPinXiangQingActivity.class);
//                intent.putExtra("id", shopid);
//                startActivity(intent);
                break;
            // 获取手机图片
            case JavaScriptInterface.TAG_GETPHOTO:
                checkPermission(new CheckPermListener() {
                    @Override
                    public void superPermission() {
                        checkPermission(new CheckPermListener() {
                            @Override
                            public void superPermission() {
//                                //控制上传的头像小于200KB
//                                Tiny.FileCompressOptions compressOptions = new Tiny.FileCompressOptions();
//                                compressOptions.size = 200;
//
//                                new ChoosePhoto(mContext, "选择图片", compressOptions) {
//                                    @Override
//                                    protected void setPortraitonSuccess(final String myPicUrl) {
//                                        WebActivity.this.runOnUiThread(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                webView.loadUrl("javascript:p_golbal['" + guid + "']('{\"状态\":\"成功\",\"地址\":\"" + myPicUrl + "\"}')");
//                                                guid = "";
//                                            }
//                                        });
//                                    }
//
//                                    @Override
//                                    protected void setPortraitFailure() {
//                                        webView.loadUrl("javascript:p_golbal['" + guid + "']('{\"状态\":\"失败\"}')");
//                                        guid = "";
//                                    }
//                                }.setPortrait();


                                PhotoPicker.builder()
                                        .setPhotoCount(1)
                                        .setShowCamera(true)
                                        .setShowGif(false)
                                        .setPreviewEnabled(true)
                                        .start(WebActivity.this, REQUEST_CODE_GOODSPIC);


                            }
                        }, R.string.perm_readstorage, Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                }, R.string.perm_camera, Manifest.permission.CAMERA);
                break;

            // 获取条形码
            case JavaScriptInterface.TAG_GETCODE:
                checkPermission(new CheckPermListener() {
                    @Override
                    public void superPermission() {
                        Intent intent = new Intent(WebActivity.this, ShopScanBarCodeActivity.class);
                        intent.putExtra("flag", "barcode");
                        startActivityForResult(intent, REQUEST_CODE_BARCODE_FOR_BARCODE);
                    }
                }, R.string.perm_camera, Manifest.permission.CAMERA, Manifest.permission.VIBRATE);
                break;
            case JavaScriptInterface.TAG_VIDEO:
//                try {
//                    Intent intent2 = new Intent(WebActivity.this, VideoActivity.class);
//                    intent2.putExtra("videoUrl", jsonObject.getString("url"));
//                    startActivity(intent2);
//                } catch (Exception e) {
//
//                }
                break;
            case JavaScriptInterface.TAG_WEBVIEW:
                try {
                    Intent intent2 = new Intent(WebActivity.this, WebActivity.class);
                    intent2.putExtra("url", jsonObject.getString("url"));
                    startActivity(intent2);
                } catch (Exception e) {

                }
                break;
            case JavaScriptInterface.TAG_APPTYPE:
                webView.loadUrl("javascript:p_golbal['" + guid + "']('{\"状态\":\"成功\",\"apptype\":\"" + "" + "\"}')");
                guid = "";
                break;
            case JavaScriptInterface.TAG_PAGETYPE:
                try {
                    String page = jsonObject.getString("page");
                    time = Integer.parseInt(jsonObject.getString("time"));
                    if (page.equals("pay")) {
                        timer = new Timer();
                        TimerTask task = new MyTimerTask();
                        timer.schedule(task, 0, 1000);    // timeTask
                        tvTime.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    tvTime.setVisibility(View.GONE);
                }
                break;
            // app版本号
            case JavaScriptInterface.TAG_APPVERSION:
                webView.loadUrl("javascript:p_golbal['" + guid + "']('{\"状态\":\"成功\",\"版本号\":\"" + ConstantUtils.VERSION + "\"}')");
                guid = "";
                break;
            // 标题
            case JavaScriptInterface.TAG_TITLE:
                try {
                    tvTitle.setText(jsonObject.getString("title"));
                } catch (Exception e) {

                }
                break;
        }
    }

    public class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    time--;
                    tvTime.setText("剩余支付时间：" + formatTime(time * 1000));
                    if (time < 0) {
                        if (timer != null) {
                            timer.cancel();
                            timer = null;
                        }
                        tvTime.setVisibility(View.GONE);
                        webView.loadUrl("javascript:closeWin()");
                    }
                }
            });
        }
    }


    private int time = 0;
    private Timer timer;


    private void finishWeb() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        tvTime.setVisibility(View.GONE);
    }

    /**
     * 毫秒转时分秒
     *
     * @param ms
     * @return
     */
    public static String formatTime(long ms) {
        int ss = 1000;
        int mi = ss * 60;
        int hh = mi * 60;
        int dd = hh * 24;

        long day = ms / dd;
        long hour = (ms - day * dd) / hh;
        long minute = (ms - day * dd - hour * hh) / mi;
        long second = (ms - day * dd - hour * hh - minute * mi) / ss;

        String strMinute = minute < 10 ? "0" + minute : "" + minute;//分钟
        String strSecond = second < 10 ? "0" + second : "" + second;//秒

        return strMinute + " : " + strSecond;
    }

    /**
     * 将支付状态返回给web
     */
    private void payCallBack() {
        L.e("guid", guid);
        webView.loadUrl("javascript:p_golbal['" + guid + "']('{\"状态\":\"成功\"}')");
        guid = "";
    }

    /**
     * 通讯录状态返回给web
     *
     * @param data
     */
    private void contactsCallBack(final Intent data) {
        try {
            // ContentProvider展示数据类似一个单个数据库表
            // ContentResolver实例带的方法可实现找到指定的ContentProvider并获取到ContentProvider的数据
            ContentResolver reContentResolverol = getContentResolver();
            // URI,每个ContentProvider定义一个唯一的公开的URI,用于指定到它的数据集
            if (data == null) {
                return;
            }
            Uri contactData = data.getData();
            // 查询就是输入URI等参数,其中URI是必须的,其他是可选的,如果系统能找到URI对应的ContentProvider将返回一个Cursor对象.
            Cursor cursor = managedQuery(contactData, null, null, null, null);
            cursor.moveToFirst();// 获得DATA表中的名字
            final String username = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            // 条件为联系人ID
            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            // 获得DATA表中的电话号码，条件为联系人ID,因为手机号码可能会有多个
            Cursor phone = reContentResolverol.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = "
                            + contactId, null, null);
            ArrayList<String> phonelist = new ArrayList();
            while (phone.moveToNext()) {
                String usernumber = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                phonelist.add(usernumber);
            }
            final String[] phones = new String[phonelist.size()];
            for (int i = 0; i < phonelist.size(); i++) {
                phones[i] = phonelist.get(i);
            }
            if (phonelist.size() == 1) {
                webView.loadUrl("javascript:p_golbal['" + guid + "']('{\"状态\":\"成功\",\"姓名\":\"" + username + "\",\"电话\":\"" + phones[0] + "\"}')");
                guid = "";
                Log.e("usernumber", "手机号" + phones[0]);
            } else {
                new AlertDialog.Builder(mContext).setTitle("请选择手机号").setSingleChoiceItems(phones, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        webView.loadUrl("javascript:p_golbal['" + guid + "']('{\"状态\":\"成功\",\"姓名\":\"" + username + "\",\"电话\":\"" + phones[which] + "\"}')");
                        guid = "";
                        Log.e("usernumber", "手机号" + phones[which]);
                        dialog.dismiss();
                    }
                }).show();
            }
        } catch (Exception e) {
            MToast.showToast(mContext, "请获取通讯录权限");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        switch (requestCode) {
//            case TAG_ADDRESSBOOK_BACK:
//                checkPermission(new CheckPermListener() {
//                    @Override
//                    public void superPermission() {
//                        contactsCallBack(data);
//                    }
//                }, R.string.perm_contacts, Manifest.permission.READ_CONTACTS);
//                break;
//            case TAG_LOGIN_BACK:
//                try {
//                    // 登录成功
//                    webView.loadUrl("javascript:p_golbal['" + guid + "']('{\"状态\":\"成功\",\"onlyid\":\"" + data.getStringExtra("onlyid") + "\"}')");
//                } catch (Exception e) {
//                    // 解析异常登录失败
//                    webView.loadUrl("javascript:p_golbal['" + guid + "']('{\"状态\":\"失败\"}')");
//                }
//                guid = "";
//                break;


        if (resultCode == RESULT_OK) {

            switch (requestCode) {
                case REQUEST_CODE_GOODSPIC:

                    String photos = null;
                    if (data != null) {
                        photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS).get(0);
                    }

//                    new UploadingSelectedPics(mContext, photos) {
//                        @Override
//                        protected void setSinglePicSuccess(String picUrl) {
//                            final String myPicUrl = picUrl;
//
////                            CommonWebActivity.this.runOnUiThread(new Runnable() {
////                                @Override
////                                public void run() {
////
//////                                    PictureUtlis.loadImageViewHolder(UploadingSelectedPics.mContext, mGoodsPicUrl, R.drawable.but_tianjia, mIvPic);
////
////                                }
////                            });
//
//                            WebActivity.this.runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    webView.loadUrl("javascript:p_golbal['" + guid + "']('{\"状态\":\"成功\",\"地址\":\"" + myPicUrl + "\"}')");
//                                    guid = "";
//                                }
//                            });
//
//                        }
//
//                        @Override
//                        protected void setSinglePicFailure() {
//                            L.e("上传照片", "上传图片失败");
//                            webView.loadUrl("javascript:p_golbal['" + guid + "']('{\"状态\":\"失败\"}')");
//                            guid = "";
//                        }
//                    }.setSinglePic();


                    //控制上传的图片小于200KB
                    Tiny.FileCompressOptions compressOptions = new Tiny.FileCompressOptions();
                    compressOptions.size = 200;
                    new UploadingSelectedPics(mContext, photos,compressOptions) {
                        @Override
                        protected void setSinglePicSuccess(String picUrl) {
                            final String myPicUrl = picUrl;

//                            CommonWebActivity.this.runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//
////                                    PictureUtlis.loadImageViewHolder(UploadingSelectedPics.mContext, mGoodsPicUrl, R.drawable.but_tianjia, mIvPic);
//
//                                }
//                            });

                            WebActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    webView.loadUrl("javascript:p_golbal['" + guid + "']('{\"状态\":\"成功\",\"地址\":\"" + myPicUrl + "\"}')");
                                    guid = "";
                                }
                            });

                        }

                        @Override
                        protected void setSinglePicFailure() {
                            L.e("上传照片", "上传图片失败");
                            webView.loadUrl("javascript:p_golbal['" + guid + "']('{\"状态\":\"失败\"}')");
                            guid = "";
                        }
                    }.setSinglePicWithCompress();

                    break;

                case REQUEST_CODE_BARCODE_FOR_BARCODE:
                    //处理扫描结果（在界面上显示）
                    if (null != data) {
                        Bundle bundle = data.getExtras();
                        if (bundle == null) {
                            return;
                        }
                        if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                            String result = bundle.getString(CodeUtils.RESULT_STRING);

//                            requestHandleArrayList.add(requestAction.getShopBarCodeMessage(MainActivity.this, result, accountType));//查询该条码信息，判断有无添加过

                            webView.loadUrl("javascript:p_golbal['" + guid + "']('{\"状态\":\"成功\",\"条形码\":\"" + result + "\"}')");
                            guid = "";

                        } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
//                            MToast.showToast(mContext, "解析条形码失败");

                            webView.loadUrl("javascript:p_golbal['" + guid + "']('{\"状态\":\"失败\"}')");
                            guid = "";

                        }
                    }
                    break;

            }
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        v_onpause.setVisibility(View.VISIBLE);
//        //暂停WebView在后台的所有活动
//        webView.onPause();
//        //暂停WebView在后台的JS活动
//        webView.pauseTimers();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fl.removeView(webView);
        webView.destroy();
        webView = null;
    }

}
