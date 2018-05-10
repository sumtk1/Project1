package com.gloiot.hygounionmerchant.ui.activity.mine.setting;

import android.widget.TextView;

import com.gloiot.hygounionmerchant.R;
import com.gloiot.hygounionmerchant.server.network.RequestAction;
import com.gloiot.hygounionmerchant.ui.BaseActivity;
import com.gloiot.hygounionmerchant.utils.CommonUtils;
import com.gloiot.hygounionmerchant.utils.ConstantUtils;
import com.zyd.wlwsdk.utils.L;
import com.zyd.wlwsdk.utils.SharedPreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;

/**
 * 关于
 * Created by Dlt on 2017/8/24 15:54
 */
public class AboutActivity extends BaseActivity {

    @Bind(R.id.tv_version_name)
    TextView mTvVersionName;
    @Bind(R.id.tv_brief_introduction)
    TextView mTvBriefIntroduction;

    private String accountType;

    @Override
    public int initResource() {
        return R.layout.activity_about;
    }

    @Override
    public void initData() {
        accountType = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTTYPE, "");
        CommonUtils.setTitleBar(this, true, "关于", "");
        String versionName = CommonUtils.getVersionName(mContext);
        requestHandleArrayList.add(requestAction.getAppVersionInfo(this, versionName, accountType));
        mTvVersionName.setText("版本" + CommonUtils.getVersionName(mContext));
    }

    @Override
    public void requestSuccess(int requestTag, JSONObject response, int showLoad) throws JSONException {
        super.requestSuccess(requestTag, response, showLoad);
        switch (requestTag) {
            case RequestAction.TAG_APPVERSIONINFO:
                L.e("获取软件版本信息", response.toString());
                String appVersionName = response.getString("app版本");
                String briefIntroduction = response.getString("简介");
                mTvBriefIntroduction.setText(briefIntroduction);
                break;
        }
    }
}
