package com.gloiot.hygounionmerchant.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.gloiot.chatsdk.DataBase.UnionDBManager;
import com.gloiot.chatsdk.socket.SocketListener;
import com.gloiot.hygounionmerchant.R;
import com.gloiot.hygounionmerchant.server.network.RequestAction;
import com.gloiot.hygounionmerchant.ui.activity.login.SelectLoginTypeActivity;
import com.gloiot.hygounionmerchant.utils.CommonUtils;
import com.gloiot.hygounionmerchant.utils.ConstantUtils;
import com.gloiot.hygounionmerchant.utils.DialogUtils;
import com.loopj.android.http.RequestHandle;
import com.zyd.wlwsdk.server.network.OnDataListener;
import com.zyd.wlwsdk.utils.L;
import com.zyd.wlwsdk.utils.MToast;
import com.zyd.wlwsdk.utils.SharedPreferencesUtils;
import com.zyd.wlwsdk.widge.LoadDialog;
import com.zyd.wlwsdk.widge.MyDialogBuilder;
import com.zyd.wlwsdk.widge.MyNewDialogBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by Dlt on 2017/8/12 16:11
 */
public class BaseFragment extends Fragment implements OnDataListener, EasyPermissions.PermissionCallbacks {

    private static final String TAG = "BaseFragment";
    protected Context mContext;
    protected SharedPreferences preferences;
    protected SharedPreferences.Editor editor;
    protected RequestAction requestAction;
    protected ArrayList<RequestHandle> requestHandleArrayList = new ArrayList<>();
    private MyNewDialogBuilder myDialogBuilder;

    @Override
    public void onResume() {
        super.onResume();
        MyDialogBuilder.getInstance(mContext).dismissNoAnimator();
        MyNewDialogBuilder.getInstance(mContext).dismissNoAnimator();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = SharedPreferencesUtils.getInstance().getSharedPreferences();
        editor = preferences.edit();
        mContext = getActivity();
        requestAction = new RequestAction();
//        StatusBarUtil.setColor(getActivity(), getResources().getColor(R.color.white), 66);
    }

    @Override
    public void onPause() {
        super.onPause();
        cancelRequestHandle();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /**
     * 请求开始
     *
     * @param requestTag 请求标志
     */
    @Override
    public void onStart(int requestTag, int showLoad) {
        L.d(TAG, "onStart: " + requestTag);
        if (showLoad == 0 || showLoad == 1) {
            LoadDialog.show(mContext, requestHandleArrayList);
        }
    }

    /**
     * 请求成功(过滤 状态=成功)
     *
     * @param requestTag 请求标志
     * @param response   请求返回
     * @throws JSONException
     */
    @Override
    public void requestSuccess(int requestTag, JSONObject response, int showLoad) throws JSONException {

    }

    /**
     * 请求成功
     *
     * @param requestTag 请求标志
     * @param response   请求返回
     */
    @Override
    public void onSuccess(int requestTag, JSONObject response, int showLoad) {
        L.d(TAG, "requestTag: " + requestTag + " onSuccess: " + response);
        L.e(TAG, "onSuccess: " + response);
        if (showLoad == 0 || showLoad == 2) {
            LoadDialog.dismiss(mContext);
        }
        try {
            if (response.getString("状态").equals("成功")) {
                requestSuccess(requestTag, response, showLoad);
            } else if (response.getString("状态").equals("随机码不正确")) {
                myDialogBuilder = MyNewDialogBuilder.getInstance(mContext);
                myDialogBuilder.setCancelable(false);//设置返回键不可点击
                myDialogBuilder
                        .withTitie("提示")
                        .withCenterContent("该账号在其他设备登录\n请您重新登录")
                        .withEffects(MyDialogBuilder.SlideTop, MyDialogBuilder.SlideTopDismiss)
                        .setOneBtn("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                myDialogBuilder.dismissNoAnimator();
                                SharedPreferencesUtils.setString(mContext, ConstantUtils.SP_LOGINSTATE, "失败");//设置登录状态
                                CommonUtils.clearPersonalData();//清除用户个人数据
                                App.clearActivity();
                                startActivity(new Intent(mContext, SelectLoginTypeActivity.class));
                                SocketListener.getInstance().signoutRenZheng();
                                UnionDBManager.getInstance(mContext, preferences.getString(ConstantUtils.SP_USERACCOUNT, "")).ClearData();
                            }
                        })
                        .show();
            } else if (response.getString("状态").equals("失败")) {
//                if (requestErrorCallback != null) {
//                    requestErrorCallback.requestErrorcallback(requestTag, response);
//                }
                MToast.showToast(mContext, "网络开小差了");
            } else {
//                if (requestErrorCallback != null) {
//                    requestErrorCallback.requestErrorcallback(requestTag, response);
//                }
                MToast.showToast(mContext, response.getString("状态"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            DialogUtils.oneBtnNormal(mContext, "数据异常-1\n请稍后再试");
        } catch (Exception e) {
            e.printStackTrace();
            DialogUtils.oneBtnNormal(mContext, "数据异常-2\n请稍后再试");
        }
    }

    /**
     * 请求失败
     *
     * @param requestTag    请求标志
     * @param errorResponse 错误请求返回
     */
    @Override
    public void onFailure(int requestTag, JSONObject errorResponse, int showLoad) {
        L.e(TAG, "onFailure: " + requestTag + " errorResponse: " + errorResponse);
        MToast.showToast(mContext, "请求超时,请检查你的网络!");
        LoadDialog.dismiss(mContext);
    }

    @Override
    public void onCancel(int requestTag, int showLoad) {
        L.e(TAG, "onCancel: " + requestTag);
    }

    /**
     * 取消网络请求
     */
    public void cancelRequestHandle() {
        if (requestHandleArrayList.size() != 0) {
            for (int i = 0; i < requestHandleArrayList.size(); i++) {
                requestHandleArrayList.get(i).cancel(true);
            }
        }
    }

    //判断是否登录
    public boolean check_login() {
        return preferences.getString(ConstantUtils.SP_LOGINSTATE, "").equals("成功");
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * 权限回调接口
     */
    private CheckPermListener mListener;
    private String[] mPerms;

    public interface CheckPermListener {
        //权限通过后的回调方法
        void superPermission();
    }

    public void checkPermission(CheckPermListener listener, int resString, String... mPerms) {
        this.mListener = listener;
        this.mPerms = mPerms;
        if (EasyPermissions.hasPermissions(mContext, mPerms)) {
            if (mListener != null)
                mListener.superPermission();
        } else {
            EasyPermissions.requestPermissions(this, getString(resString), 123, mPerms);
        }
    }

    /**
     * 用户权限处理,
     * 如果全部获取, 则直接过.
     * 如果权限缺失, 则提示Dialog.
     *
     * @param requestCode  请求码
     * @param permissions  权限
     * @param grantResults 结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        if (requestCode == EasyPermissions.SETTINGS_REQ_CODE) {
//            //设置返回
//        }

//        if (requestCode == 123) {
//            //设置返回
//            if (EasyPermissions.hasPermissions(mContext, mPerms)) {
//                if (mListener != null)
//                    mListener.superPermission();
//            }
//        }

        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            //设置返回
            if (pub.devrel.easypermissions.EasyPermissions.hasPermissions(mContext, mPerms)) {
                if (mListener != null)
                    mListener.superPermission();
            }
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        //同意了某些权限可能不是全部
        if (EasyPermissions.hasPermissions(mContext, mPerms)) {
            if (mListener != null)
                mListener.superPermission();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

//        EasyPermissions.checkDeniedPermissionsNeverAskAgain(this,
//                getString(R.string.perm_tip),
//                R.string.setting, R.string.cancel, null, perms);

        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this)
                    .setTitle("设置")
                    .setRationale(R.string.perm_tip)
                    .build().show();
        }
    }

}
