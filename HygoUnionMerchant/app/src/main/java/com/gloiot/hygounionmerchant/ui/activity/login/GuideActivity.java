package com.gloiot.hygounionmerchant.ui.activity.login;

import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.gloiot.hygounionmerchant.R;
import com.gloiot.hygounionmerchant.ui.BaseActivity;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Dlt on 2017/8/16 9:55
 */
public class GuideActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.vp_guide)
    ViewPager mVpGuide;
    @Bind(R.id.tv_guide_letsgo)
    TextView mTvGuideLetsgo;

    @OnClick(R.id.tv_guide_letsgo)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_guide_letsgo:

                break;
        }
    }

    @Override
    public int initResource() {
        return R.layout.activity_guide;
    }

    @Override
    public void initData() {

    }

}
