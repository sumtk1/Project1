package com.gloiot.hygounionmerchant.ui.activity.mine.zizhirenzheng;

import android.view.View;

import com.gloiot.hygounionmerchant.R;
import com.gloiot.hygounionmerchant.ui.BaseActivity;
import com.gloiot.hygounionmerchant.utils.CommonUtils;

import butterknife.OnClick;

/**
 * 资质认证--完成
 * Created by Dlt on 2017/10/27 11:49
 */
public class CertificateFinishActivity extends BaseActivity implements View.OnClickListener {

    @Override
    public int initResource() {
        return R.layout.activity_certification_finish;
    }

    @Override
    public void initData() {
        CommonUtils.setTitleBar(this, true, "资质认证", "");
    }

    @OnClick(R.id.tv_confirm)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_confirm:
                finish();
                break;
        }
    }

}
