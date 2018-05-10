package com.gloiot.hygounionmerchant.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;

import com.gloiot.chatsdk.DataBase.BaseObserver;
import com.gloiot.chatsdk.DataBase.UnionDBManager;
import com.gloiot.chatsdk.MessageManager;
import com.gloiot.chatsdk.broadcast.BroadcastManager;
import com.gloiot.chatsdk.socket.SocketListener;
import com.gloiot.chatsdk.socket.SocketServer;
import com.gloiot.hygounionmerchant.R;
import com.gloiot.hygounionmerchant.ui.BaseActivity;
import com.gloiot.hygounionmerchant.ui.adapter.MainFragmentAdapter;
import com.gloiot.hygounionmerchant.ui.fragment.MessageFragment;
import com.gloiot.hygounionmerchant.ui.webview.WebActivity;
import com.gloiot.hygounionmerchant.utils.ConstantUtils;
import com.gloiot.hygounionmerchant.utils.NetEvent;
import com.gloiot.hygounionmerchant.widget.fragmentnavigator.BottomNavigatorView;
import com.gloiot.hygounionmerchant.widget.fragmentnavigator.FragmentNavigator;
import com.zyd.wlwsdk.utils.L;
import com.zyd.wlwsdk.utils.MToast;
import com.zyd.wlwsdk.utils.SharedPreferencesUtils;
import com.zyd.wlwsdk.utils.StatusBarUtil;
import com.zyd.wlwsdk.zxing.activity.CodeUtils;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

/**
 * Created by Dlt on 2017/8/12 14:22
 */
public class MainActivity extends BaseActivity implements BottomNavigatorView.OnBottomNavigatorViewItemClickListener, BaseActivity.RequestErrorCallback, NetEvent {

    private static final int DEFAULT_POSITION = 0;
    @Bind(R.id.bottomNavigatorView)
    BottomNavigatorView bottomNavigatorView;

    private FragmentNavigator mNavigator;

    private String accountType;
    private String backResult = "";
    public static final int REQUEST_CODE_BARCODE_FOR_BARCODE = 113;
    public static final int REQUEST_CODE_BARCODE_FOR_RUKU = 114;
    public static final int REQUEST_CODE_BARCODE_FOR_WEBVIEW = 115;//条形码
    public static final int REQUEST_CODE_QRCODE_FOR_WEBVIEW = 116;//二维码

    private Badge badge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNavigator = new FragmentNavigator(getSupportFragmentManager(), new MainFragmentAdapter(), R.id.container);
        mNavigator.setDefaultPosition(DEFAULT_POSITION);
        mNavigator.onCreate(savedInstanceState);

        bottomNavigatorView = (BottomNavigatorView) findViewById(R.id.bottomNavigatorView);
        if (bottomNavigatorView != null) {
            bottomNavigatorView.setOnBottomNavigatorViewItemClickListener(this);
        }

        setCurrentTab(mNavigator.getCurrentPosition());

        socketReConnection();

        BroadcastManager.getInstance(mContext).addAction(MessageManager.NEW_MESSAGE, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                refreshBadge();
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // 回到页面更新未读消息数
        refreshBadge();
    }

    @Override
    public int initResource() {
        return R.layout.activity_main;
    }

    @Override
    public void initData() {
        StatusBarUtil.setColor(this, getResources().getColor(R.color.green_21d1c1), 0);

        accountType = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTTYPE, "");
        setRequestErrorCallback(this);

        badge();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mNavigator.onSaveInstanceState(outState);
    }

    @Override
    public void onBottomNavigatorViewItemClick(int position, View view) {
        setCurrentTab(position);
    }

    private void setCurrentTab(int position) {
        mNavigator.showFragment(position);
        bottomNavigatorView.select(position);
    }

    private void resetAllTabsAndShow(int position) {
        mNavigator.resetFragments(position, true);
        bottomNavigatorView.select(position);
    }

    @Override
    public void onNetChange(int netMobile) {
        switch (netMobile) {
            case 1://wifi
            case 0://移动数据
            case -1://没有网络
                BroadcastManager.getInstance(this).sendBroadcast("com.gloiot.hygo.判断网络", netMobile + "");
                break;
        }
    }

    @Override
    public void requestSuccess(int requestTag, JSONObject response, int showLoad) throws JSONException {
        super.requestSuccess(requestTag, response, showLoad);
        switch (requestTag) {

//            case RequestAction.TAG_SHOPFINDBARCODEMESSAGE:
//                L.e("获取条形码相关信息", response.toString());
//
//                backResult = "";
//                MToast.showToast(mContext, "您已添加过该条码，请勿重复添加");
//
//                break;

        }
    }


    @Override
    public void requestErrorcallback(int requestTag, JSONObject response) throws Exception {
        switch (requestTag) {
//            case RequestAction.TAG_SHOPFINDBARCODEMESSAGE:
//                L.e("获取条形码相关信息-requestErrorcallback", response.toString());
//
//                if (response.getString("状态").contains("无此条形码信息")) {
//
//                    Intent intent = new Intent(MainActivity.this, ShopAddBarCodeActivity.class);
//                    intent.putExtra("barCodeType", "automatic");//自动
//                    intent.putExtra("barCode", backResult);
//                    startActivity(intent);
//
//                } else {
//                    MToast.showToast(mContext, response.getString("状态"));
//                }
//
//                break;
            default:
                MToast.showToast(mContext, response.getString("状态"));
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        L.e("onActivityResult-----", "requestCode--" + requestCode + ",resultCode---" + resultCode);
        /**
         * 处理二维码扫描结果
         */
//        if (requestCode == REQUEST_CODE_BARCODE_FOR_BARCODE) {//添加条码
//            //处理扫描结果（在界面上显示）
//            if (null != data) {
//                Bundle bundle = data.getExtras();
//                if (bundle == null) {
//                    return;
//                }
//                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
//                    String result = bundle.getString(CodeUtils.RESULT_STRING);
//
//                    backResult = result;
//                    requestHandleArrayList.add(requestAction.getShopBarCodeMessage(MainActivity.this, result, accountType));//查询该条码信息，判断有无添加过
//
//                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
//                    backResult = "";
//                    MToast.showToast(mContext, "解析条形码失败");
//                }
//            }
//        } else if (requestCode == REQUEST_CODE_BARCODE_FOR_RUKU) {//商品入库
//            //处理扫描结果（在界面上显示）
//            if (null != data) {
//                Bundle bundle = data.getExtras();
//                if (bundle == null) {
//                    return;
//                }
//                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
//                    String result = bundle.getString(CodeUtils.RESULT_STRING);
//
//                    Intent intent = new Intent(MainActivity.this, ShopCommodityWarehousingActivity.class);
//                    intent.putExtra("barCode", result);
//                    startActivity(intent);
//
//                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
//                    MToast.showToast(mContext, "解析条形码失败");
//                }
//            }
//        } else

        if (requestCode == REQUEST_CODE_BARCODE_FOR_WEBVIEW) {//条形码---跳网页---商品入库
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);

                    backResult = result;
//                    requestHandleArrayList.add(requestAction.getShopBarCodeMessage(HomeFragment.this, result, accountType));//查询该条码信息，判断有无添加过

                    Intent intent = new Intent(MainActivity.this, WebActivity.class);//使用环游购封装webview
                    intent.putExtra("url", SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_COMMODITYWAREHOUSINGURL, "")
                            + "?account=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_USERACCOUNT, "")
                            + "&shop_type=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTTYPE, "")
                            + "&random_code=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_RANDOMCODE, "")
                            + "&Barcode=" + backResult);
                    startActivity(intent);

                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    backResult = "";
                    MToast.showToast(mContext, "解析条形码失败");
                }
            }
        } else if (requestCode == REQUEST_CODE_QRCODE_FOR_WEBVIEW) {//二维码---跳网页---扫码认证
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);

                    backResult = result;
                    L.e("二维码结果", "==" + backResult);

                    String qrCodeUrl = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_INPUTCODERENZHENGURL, "");//用于判断扫描的二维码是否符合要求

                    if (backResult.contains(qrCodeUrl)) {
                        Intent intent = new Intent(MainActivity.this, WebActivity.class);//使用环游购封装webview
                        intent.putExtra("url", backResult
                                + "&account=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_USERACCOUNT, "")
                                + "&random_code=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_RANDOMCODE, "")
                                + "&shop_type=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTTYPE, ""));
                        startActivity(intent);
                    } else {
                        MToast.showToast(mContext, "无效二维码");
                    }

                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    backResult = "";
                    MToast.showToast(mContext, "解析二维码码失败");
                }
            }
        }
    }

    /**
     * 设置未读消息展示
     */
    private void badge() {
        badge = new QBadgeView(mContext).bindTarget(bottomNavigatorView.getLlTabMessage());
        badge.setBadgeGravity(Gravity.TOP | Gravity.END);
        badge.setGravityOffset(100, -5, false);
        badge.setBadgeBackgroundColor(Color.parseColor("#FF6D63"));
        badge.setBadgeTextSize(10, true);
        badge.setBadgePadding(7, true);
        badge.setOnDragStateChangedListener(new Badge.OnDragStateChangedListener() {
            @Override
            public void onDragStateChanged(int dragState, final Badge badge, View targetView) {
                if (dragState == STATE_SUCCEED) {
                    UnionDBManager.getInstance(mContext, preferences.getString(ConstantUtils.SP_USERACCOUNT, "")).CleanAllReadNum().observeOn(AndroidSchedulers.mainThread()).subscribe(new BaseObserver<Boolean>() {
                        @Override
                        public void onNext(@NonNull Boolean aBoolean) {
                            if (aBoolean) {
                                badge.setBadgeNumber(0); // 将未读消息的条数置零
                                if (mNavigator.getFragment(1) != null) {
                                    ((MessageFragment) mNavigator.getFragment(1)).refresh(); // 刷新聊天界面
                                } else {
//                                    setCurrentTab(1);
//                                    ((MessageFragment) mNavigator.getFragment(1)).refresh(); // 刷新聊天界面
                                }
                            }
                        }
                    });
                }
            }
        });
        refreshBadge();
    }

    public void refreshBadge() {
        if (check_login()) {
            // 获取总未读条数
            UnionDBManager.getInstance(mContext, preferences.getString(ConstantUtils.SP_USERACCOUNT, "")).GetAllReadNum().observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Integer>() {
                @Override
                public void accept(Integer integer) throws Exception {
                    badge.setBadgeNumber(integer);
                }
            });
        }
    }

    /**
     * app重开，socket重连
     */
    private void socketReConnection() {
        String account = preferences.getString(ConstantUtils.SP_USERACCOUNT, "");
        String random = preferences.getString(ConstantUtils.SP_RANDOMCODE, "");

        if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(random)) {
            SocketListener.getInstance().staredData(account, random);
        }
        // 连接
        SocketServer.socketOnConnect();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
