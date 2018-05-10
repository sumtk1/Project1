package com.gloiot.hygounionmerchant.ui.activity.home;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;

import com.gloiot.hygounionmerchant.R;
import com.gloiot.hygounionmerchant.ui.BaseActivity;
import com.gloiot.hygounionmerchant.ui.adapter.ShopProductManagementAdapter;
import com.gloiot.hygounionmerchant.utils.CommonUtils;
import com.zyd.wlwsdk.widge.NoHorizontalScrolledViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * 小铺产品管理
 * Created by Dlt on 2017/8/18 16:06
 */
public class ShopProductManagementActivity extends BaseActivity {

    @Bind(R.id.tl_tabs)
    TabLayout mTlTabs;
    @Bind(R.id.view_pager)
    NoHorizontalScrolledViewPager mViewPager;

    private String[] titles = {"条码管理", "入库管理"};//tab条目中的内容
    private List<Fragment> mFragments = new ArrayList<>();
    private ShopProductManagementAdapter adapter;

    @Override
    public int initResource() {
        return R.layout.activity_shop_product_management;
    }

    @Override
    public void initData() {
        CommonUtils.setTitleBar(this, true, "产品管理", "");
        mFragments.add(ShopBarCodeManagementFragment.newInstance());
        mFragments.add(ShopWarehousingManagementFragment.newInstance());
        adapter = new ShopProductManagementAdapter(getSupportFragmentManager(), titles, mFragments);
        mViewPager.setAdapter(adapter);
        mTlTabs.setTabMode(TabLayout.MODE_FIXED);
        mTlTabs.setupWithViewPager(mViewPager);
    }

}
