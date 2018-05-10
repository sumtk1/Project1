package com.gloiot.hygounionmerchant.ui.webview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.FrameLayout;

import com.abcaaa.photopicker.PhotoPicker;
import com.gloiot.hygounionmerchant.R;
import com.gloiot.hygounionmerchant.server.picture.UploadingSelectedPics;
import com.gloiot.hygounionmerchant.utils.ConstantUtils;
import com.zyd.wlwsdk.utils.L;
import com.zyd.wlwsdk.utils.SharedPreferencesUtils;
import com.zyd.wlwsdk.utils.StatusBarUtil;

import static com.gloiot.hygounionmerchant.ui.webview.AndroidInterface.REQUEST_CODE_GOODSPIC;
import static com.zyd.wlwsdk.utils.SharedPreferencesUtils.mContext;

/**
 * 用于fragment
 * Created by Dlt on 2017/8/25 11:42
 */
public class CommonWebActivity extends AppCompatActivity {

    private FrameLayout mFrameLayout;
    public static final String TYPE_KEY = "type_key";
    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_web);
        mFrameLayout = (FrameLayout) this.findViewById(R.id.container_framelayout);

        StatusBarUtil.setColor(this, getResources().getColor(R.color.black_2e2e32), 0);

        int key = getIntent().getIntExtra(TYPE_KEY, -1);
        mFragmentManager = this.getSupportFragmentManager();
        openFragment(key);



    }

    private AgentWebFragment mAgentWebFragment;

    private void openFragment(int key) {

        FragmentTransaction ft = mFragmentManager.beginTransaction();
        Bundle mBundle = null;


        switch (key) {

            /*Fragment 使用AgenWebt*/
            case 0://产品管理
                ft.add(R.id.container_framelayout, mAgentWebFragment = AgentWebFragment.getInstance(mBundle = new Bundle()), AgentWebFragment.class.getName());
//                mBundle.putString(AgentWebFragment.URL_KEY, "https://m.vip.com/?source=www&jump_https=1");
                mBundle.putString(AgentWebFragment.URL_KEY, SharedPreferencesUtils.getString(this, ConstantUtils.SP_PRODUCTMANAGEMENTURL, "")
                        + "?account=" + SharedPreferencesUtils.getString(this, ConstantUtils.SP_USERACCOUNT, "")
                        + "&shop_type=" + SharedPreferencesUtils.getString(this, ConstantUtils.SP_ACCOUNTTYPE, "")
                        + "&random_code=" + SharedPreferencesUtils.getString(this, ConstantUtils.SP_RANDOMCODE, ""));
                L.e("产品管理url", SharedPreferencesUtils.getString(this, ConstantUtils.SP_PRODUCTMANAGEMENTURL, "")
                        + "?account=" + SharedPreferencesUtils.getString(this, ConstantUtils.SP_USERACCOUNT, "")
                        + "&shop_type=" + SharedPreferencesUtils.getString(this, ConstantUtils.SP_ACCOUNTTYPE, "")
                        + "&random_code=" + SharedPreferencesUtils.getString(this, ConstantUtils.SP_RANDOMCODE, ""));
                break;

            case 1://订单管理
                ft.add(R.id.container_framelayout, mAgentWebFragment = AgentWebFragment.getInstance(mBundle = new Bundle()), AgentWebFragment.class.getName());
                mBundle.putString(AgentWebFragment.URL_KEY, SharedPreferencesUtils.getString(this, ConstantUtils.SP_ORDERMANAGEMENTURL, "")
                        + "?account=" + SharedPreferencesUtils.getString(this, ConstantUtils.SP_USERACCOUNT, "")
                        + "&shop_type=" + SharedPreferencesUtils.getString(this, ConstantUtils.SP_ACCOUNTTYPE, "")
                        + "&random_code=" + SharedPreferencesUtils.getString(this, ConstantUtils.SP_RANDOMCODE, ""));
                L.e("订单管理url", SharedPreferencesUtils.getString(this, ConstantUtils.SP_ORDERMANAGEMENTURL, "")
                        + "?account=" + SharedPreferencesUtils.getString(this, ConstantUtils.SP_USERACCOUNT, "")
                        + "&shop_type=" + SharedPreferencesUtils.getString(this, ConstantUtils.SP_ACCOUNTTYPE, "")
                        + "&random_code=" + SharedPreferencesUtils.getString(this, ConstantUtils.SP_RANDOMCODE, ""));
                break;

            case 2://产品上传
                ft.add(R.id.container_framelayout, mAgentWebFragment = AgentWebFragment.getInstance(mBundle = new Bundle()), AgentWebFragment.class.getName());
                mBundle.putString(AgentWebFragment.URL_KEY, SharedPreferencesUtils.getString(this, ConstantUtils.SP_PRODUCTUPLOADURL, "")
                        + "?account=" + SharedPreferencesUtils.getString(this, ConstantUtils.SP_USERACCOUNT, "")
                        + "&shop_type=" + SharedPreferencesUtils.getString(this, ConstantUtils.SP_ACCOUNTTYPE, "")
                        + "&random_code=" + SharedPreferencesUtils.getString(this, ConstantUtils.SP_RANDOMCODE, ""));
                L.e("产品上传url", SharedPreferencesUtils.getString(this, ConstantUtils.SP_PRODUCTUPLOADURL, "")
                        + "?account=" + SharedPreferencesUtils.getString(this, ConstantUtils.SP_USERACCOUNT, "")
                        + "&shop_type=" + SharedPreferencesUtils.getString(this, ConstantUtils.SP_ACCOUNTTYPE, "")
                        + "&random_code=" + SharedPreferencesUtils.getString(this, ConstantUtils.SP_RANDOMCODE, ""));
                break;

        }
        ft.commit();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mAgentWebFragment.onActivityResult(requestCode, resultCode, data);
        Log.i("Info", "activity result");

        if (resultCode == RESULT_OK) {
            String photos = null;
            if (data != null) {
                photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS).get(0);
            }
            switch (requestCode) {
                case  REQUEST_CODE_GOODSPIC:

                    Log.e("ddddddddd1", "-----");
                    new UploadingSelectedPics(mContext, photos) {
                        @Override
                        protected void setSinglePicSuccess(String picUrl) {
//                            mGoodsPicUrl = picUrl;

                            Log.e("ddddddddd2", picUrl);
//                            CommonWebActivity.this.runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//
////                                    PictureUtlis.loadImageViewHolder(UploadingSelectedPics.mContext, mGoodsPicUrl, R.drawable.but_tianjia, mIvPic);
//
//                                }
//                            });
                        }

                        @Override
                        protected void setSinglePicFailure() {
                            L.e("上传照片", "上传图片失败");
                        }
                    }.setSinglePic();
                    break;

            }
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        AgentWebFragment mAgentWebFragment = this.mAgentWebFragment;
        if (mAgentWebFragment != null) {
            FragmentKeyDown mFragmentKeyDown = mAgentWebFragment;
            if (mFragmentKeyDown.onFragmentKeyDown(keyCode, event))
                return true;
            else
                return super.onKeyDown(keyCode, event);
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

}
