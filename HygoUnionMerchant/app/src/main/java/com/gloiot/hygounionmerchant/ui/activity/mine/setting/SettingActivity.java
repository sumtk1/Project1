package com.gloiot.hygounionmerchant.ui.activity.mine.setting;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.gloiot.chatsdk.DataBase.UnionDBManager;
import com.gloiot.chatsdk.socket.SocketListener;
import com.gloiot.hygounionmerchant.R;
import com.gloiot.hygounionmerchant.server.network.RequestAction;
import com.gloiot.hygounionmerchant.ui.App;
import com.gloiot.hygounionmerchant.ui.BaseActivity;
import com.gloiot.hygounionmerchant.ui.activity.login.SelectLoginTypeActivity;
import com.gloiot.hygounionmerchant.utils.CommonUtils;
import com.gloiot.hygounionmerchant.utils.ConstantUtils;
import com.zyd.wlwsdk.utils.CacheManagerUtils;
import com.zyd.wlwsdk.utils.L;
import com.zyd.wlwsdk.utils.MToast;
import com.zyd.wlwsdk.utils.SharedPreferencesUtils;
import com.zyd.wlwsdk.widge.MyDialogBuilder;
import com.zyd.wlwsdk.widge.MyNewDialogBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 我的--设置
 * Created by Dlt on 2017/8/24 15:09
 */
public class SettingActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.tv_sign_updates)
    TextView mTvSignUpdates;
    @Bind(R.id.tv_cache_size)
    TextView mTvCacheSize;

    private String accountType;
    private MyNewDialogBuilder myDialogBuilder;

    @Override
    public int initResource() {
        return R.layout.activity_setting;
    }

    @Override
    public void initData() {
        accountType = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTTYPE, "");
        CommonUtils.setTitleBar(this, true, "设置", "");
        try {
            mTvCacheSize.setText(CacheManagerUtils.getTotalCacheSize(mContext));
        } catch (Exception e) {
            mTvCacheSize.setText("0K");
            e.printStackTrace();
        }
    }

    @OnClick({R.id.rl_setting_about, R.id.rl_setting_check_for_updates, R.id.rl_setting_clear_cache, R.id.tv_logout})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_setting_about:
                startActivity(new Intent(SettingActivity.this, AboutActivity.class));
                break;
            case R.id.rl_setting_check_for_updates:
                String versionName = CommonUtils.getVersionName(mContext);
                requestHandleArrayList.add(requestAction.checkForUpdates(SettingActivity.this, versionName, accountType));
                break;
            case R.id.rl_setting_clear_cache:
                CacheManagerUtils.clearAllCache(mContext);
                try {
                    mTvCacheSize.setText(CacheManagerUtils.getTotalCacheSize(mContext));
                    MToast.showToast(mContext, "缓存清理完毕");
                } catch (Exception e) {
                    mTvCacheSize.setText("0K");
                    e.printStackTrace();
                }
                break;
            case R.id.tv_logout:
                myDialogBuilder = MyNewDialogBuilder.getInstance(mContext);
                myDialogBuilder
                        .withTitie("提示")
                        .withCenterContent("您确定要退出登录吗")
                        .withEffects(MyDialogBuilder.SlideTop, MyDialogBuilder.SlideTopDismiss)
                        .setTwoBtn("取消", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                myDialogBuilder.dismiss();
                            }
                        }, "确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                myDialogBuilder.dismissNoAnimator();
                                SharedPreferencesUtils.setString(mContext, ConstantUtils.SP_LOGINSTATE, "失败");//登录状态
                                CommonUtils.clearPersonalData();//清除用户个人数据
                                App.clearActivity();
                                startActivity(new Intent(SettingActivity.this, SelectLoginTypeActivity.class));
                                SocketListener.getInstance().signoutRenZheng();
                                UnionDBManager.getInstance(mContext, preferences.getString(ConstantUtils.SP_USERACCOUNT, "")).ClearData();
                            }
                        })
                        .show();
                break;
        }
    }

    @Override
    public void requestSuccess(int requestTag, JSONObject response, int showLoad) throws JSONException {
        super.requestSuccess(requestTag, response, showLoad);
        switch (requestTag) {
            case RequestAction.TAG_CHECKFORUPDATES://检测更新
                L.e("检测更新", response.toString() + "-------");
                String title = response.getString("标题");
                String content = response.getString("内容");
                myDialogBuilder = MyNewDialogBuilder.getInstance(mContext);
                myDialogBuilder
                        .withTitie(title)
                        .withContene(content)
                        .withEffects(MyDialogBuilder.SlideTop, MyDialogBuilder.SlideTopDismiss)
                        .setTwoBtn("以后再说", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                myDialogBuilder.dismiss();
                            }
                        }, "立即更新", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                myDialogBuilder.dismissNoAnimator();

                                myDialogBuilder = MyNewDialogBuilder.getInstance(mContext);
                                myDialogBuilder
//                                        .withTitie("提示")
                                        .withCenterContent("请前往您的应用市场进行版本更新")
                                        .withEffects(MyDialogBuilder.SlideTop, MyDialogBuilder.SlideTopDismiss)
                                        .setOneBtn("确 定", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                myDialogBuilder.dismissNoAnimator();
                                            }
                                        })
                                        .show();

                            }
                        })
                        .show();
                break;
        }
    }

}
