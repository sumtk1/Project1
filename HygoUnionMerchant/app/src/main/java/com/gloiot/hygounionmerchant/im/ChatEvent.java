package com.gloiot.hygounionmerchant.im;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Looper;
import android.view.View;

import com.gloiot.chatsdk.DataBase.UnionDBManager;
import com.gloiot.chatsdk.MessageManager;
import com.gloiot.chatsdk.SocketEvent;
import com.gloiot.chatsdk.broadcast.BroadcastManager;
import com.gloiot.chatsdk.socket.SocketListener;
import com.gloiot.chatsdk.utlis.Constant;
import com.gloiot.hygounionmerchant.ui.App;
import com.gloiot.hygounionmerchant.ui.activity.login.SelectLoginTypeActivity;
import com.gloiot.hygounionmerchant.utils.CommonUtils;
import com.gloiot.hygounionmerchant.utils.ConstantUtils;
import com.zyd.wlwsdk.utils.L;
import com.zyd.wlwsdk.utils.MToast;
import com.zyd.wlwsdk.utils.SharedPreferencesUtils;
import com.zyd.wlwsdk.widge.MyDialogBuilder;
import com.zyd.wlwsdk.widge.MyNewDialogBuilder;

/**
 * Created by jinzlin on 17/5/23.
 */

public class ChatEvent implements SocketEvent.ConnectionStatusListener {

    private static ChatEvent instance;
    private Context context;
    private SharedPreferences preferences;


    public ChatEvent(Context context) {
        this.context = context;
        preferences = SharedPreferencesUtils.getInstance().getSharedPreferences();
        initListener();
    }

    public static void init(Context context) {
        if (instance == null) {
            synchronized (ChatEvent.class) {
                if (instance == null) {
                    instance = new ChatEvent(context);
                }
            }
        }
    }

    public static ChatEvent getInstance() {
        return instance;
    }


    private void initListener() {
        SocketEvent.getInstance().setConnectionStatusListener(this);
    }

    @Override
    public void onChanged(String result) {
        switch (result) {
            case Constant.RENZHENG_FAILURE:
                // 认证失败
                BroadcastManager.getInstance(context).sendBroadcast(MessageManager.LINK_CHANGED, MessageManager.LINK_CHANGED_FAULT);
                break;
            case Constant.RENZHENG_RANDOM_ERROR:
                // 随机码不正确 其他设备登录
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final Context nowContext = App.getInstance().mActivityStack.getLastActivity();
                        ((Activity) nowContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                CommonUtils.clearPersonalData();//清除账号个人数据，重置登录状态等
//                                DialogUtils.oneBtnNormal(nowContext, "该账号在其他设备登录\n请重新登录", "确定", false, new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        DialogUtils.dismissDialog();
//                                        Intent intent = new Intent(nowContext, LoginActivity.class);
//                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                        nowContext.startActivity(intent);
//                                        SocketListener.getInstance().signoutRenZheng();
//                                        IMDBManager.getInstance(nowContext, preferences.getString(ConstantUtlis.SP_USERPHONE, "")).ClearnData();
//                                        ((Activity) nowContext).overridePendingTransition(R.anim.activity_open, 0);
//                                    }
//                                });

                                L.e("消息随机码不正确", "异地登录，强制下线");

                                final MyNewDialogBuilder myDialogBuilder = MyNewDialogBuilder.getInstance(nowContext);
                                myDialogBuilder.setCancelable(false);//设置返回键不可点击
                                myDialogBuilder
                                        .withTitie("提示")
                                        .withCenterContent("该账号在其他设备登录\n请您重新登录")
                                        .withEffects(MyDialogBuilder.SlideTop, MyDialogBuilder.SlideTopDismiss)
                                        .setOneBtn("确定", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                myDialogBuilder.dismissNoAnimator();
                                                SharedPreferencesUtils.setString(nowContext, ConstantUtils.SP_LOGINSTATE, "失败");//设置登录状态
                                                CommonUtils.clearPersonalData();//清除用户个人数据
                                                App.clearActivity();
                                                nowContext.startActivity(new Intent(nowContext, SelectLoginTypeActivity.class));
                                                SocketListener.getInstance().signoutRenZheng();
                                                UnionDBManager.getInstance(nowContext, preferences.getString(ConstantUtils.SP_USERACCOUNT, "")).ClearData();
                                            }
                                        })
                                        .show();
                            }
                        });
                    }
                }).start();
                BroadcastManager.getInstance(context).sendBroadcast(MessageManager.LINK_CHANGED, MessageManager.LINK_CHANGED_DONW);
                break;
            case Constant.RENZHENG_SUCCESS:
                // 认证成功
                BroadcastManager.getInstance(context).sendBroadcast(MessageManager.LINK_CHANGED, MessageManager.LINK_CHANGED_SUCCEED);
                break;
            default:
                BroadcastManager.getInstance(context).sendBroadcast(MessageManager.LINK_CHANGED, MessageManager.LINK_CHANGED_FAULT);
                t(result + "");
        }
    }

    public void t(final String s) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                MToast.showToast(context, s);
                Looper.loop();
            }
        }).start();
    }
}
