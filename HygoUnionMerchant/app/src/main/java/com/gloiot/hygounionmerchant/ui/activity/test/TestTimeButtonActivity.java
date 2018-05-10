package com.gloiot.hygounionmerchant.ui.activity.test;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.gloiot.hygounionmerchant.R;
import com.gloiot.hygounionmerchant.ui.BaseActivity;
import com.gloiot.hygounionmerchant.utils.CommonUtils;
import com.gloiot.hygounionmerchant.widget.TimeButton;

import butterknife.Bind;
import butterknife.OnClick;

public class TestTimeButtonActivity extends BaseActivity {

    @Bind(R.id.time_button)
    TimeButton mTimeButton;
    @Bind(R.id.tv_reset)
    TextView mTvReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTimeButton.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        mTimeButton.onDestroy();
        super.onDestroy();
    }

    @Override
    public int initResource() {
        return R.layout.activity_test_time_button;
    }

    @Override
    public void initData() {
        CommonUtils.setTitleBar(this, true, "测试重置验证码倒计时", "");
        mTimeButton.setTextAfter("秒后重新获取").setTextBefore("获取验证码").setLenght(60 * 1000);
    }

    @OnClick({R.id.time_button, R.id.tv_reset})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.time_button:
                mTimeButton.setCondition(true);
                break;
            case R.id.tv_reset:

                mTimeButton.resetState();

                break;
        }
    }
}
