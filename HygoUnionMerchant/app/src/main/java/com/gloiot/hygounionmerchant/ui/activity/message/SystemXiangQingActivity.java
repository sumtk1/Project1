package com.gloiot.hygounionmerchant.ui.activity.message;

import android.widget.LinearLayout;
import android.widget.TextView;

import com.gloiot.hygounionmerchant.R;
import com.gloiot.hygounionmerchant.ui.BaseActivity;
import com.gloiot.hygounionmerchant.utils.CommonUtils;

import butterknife.Bind;

/**
 * 创建人： zengming on 2017/10/19.
 * 功能：系统通知详情界面
 */

public class SystemXiangQingActivity extends BaseActivity {

    @Bind(R.id.tv_xiangqing_title)
    TextView tvXiangqingTitle;
    @Bind(R.id.tv_xiangqing_content)
    TextView tvXiangqingContent;
    @Bind(R.id.tv_xiangqing_name)
    TextView tvXiangqingName;
    @Bind(R.id.tv_xiangqing_time)
    TextView tvXiangqingTime;
    @Bind(R.id.ll_noData)
    LinearLayout llNoData;

    @Override
    public int initResource() {
        return R.layout.activity_message_system_xiangqing;
    }

    @Override
    public void initData() {
        CommonUtils.setTitleBar(this, true, "系统消息详情", "");
    }

}
