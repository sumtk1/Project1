package com.gloiot.hygounionmerchant.ui.activity.mine.printer;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.gloiot.hygounionmerchant.R;
import com.gloiot.hygounionmerchant.server.network.RequestAction;
import com.gloiot.hygounionmerchant.ui.BaseActivity;
import com.gloiot.hygounionmerchant.utils.CommonUtils;
import com.gloiot.hygounionmerchant.utils.ConstantUtils;
import com.gloiot.hygounionmerchant.utils.VerifyButtonEnableWatcher;
import com.zyd.wlwsdk.utils.L;
import com.zyd.wlwsdk.utils.MToast;
import com.zyd.wlwsdk.utils.SharedPreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 添加打印机
 * Created by Dlt on 2017/9/19 16:04
 */
public class AddPrinterActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.et_terminal_num)
    EditText mEtTerminalNum;
    @Bind(R.id.et_secret_key)
    EditText mEtSecretKey;
    @Bind(R.id.et_gps_card_num)
    EditText mEtGpsCardNum;
    @Bind(R.id.et_gps_serial_num)
    EditText mEtGpsSerialNum;
    @Bind(R.id.tv_add)
    TextView mTvAdd;

    private String accountType;

    @OnClick(R.id.tv_add)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_add:

                String terminalNum = mEtTerminalNum.getText().toString().trim();
                String secretKey = mEtSecretKey.getText().toString().trim();
                String gpsCardNum = mEtGpsCardNum.getText().toString().trim();
                String gpsSerialNum = mEtGpsSerialNum.getText().toString().trim();

                requestHandleArrayList.add(requestAction.shopAddPrinter(this, terminalNum, secretKey, gpsCardNum, gpsSerialNum, accountType));

                break;
        }
    }

    @Override
    public int initResource() {
        return R.layout.activity_add_printer;
    }

    @Override
    public void initData() {
        accountType = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTTYPE, "");
        CommonUtils.setTitleBar(this, true, "添加打印机", "");
        setTextWatcher();
    }

    private void setTextWatcher() {
        mEtTerminalNum.addTextChangedListener(new VerifyButtonEnableWatcher(mContext, mTvAdd) {
            @Override
            public boolean verifyCondition(CharSequence s) {
                return mEtSecretKey.getText().toString().trim().length() >= 6 && mEtGpsCardNum.getText().toString().trim().length() >= 6 &&
                        mEtGpsSerialNum.getText().toString().trim().length() >= 6 && s.length() > 6;
            }
        });

        mEtSecretKey.addTextChangedListener(new VerifyButtonEnableWatcher(mContext, mTvAdd) {
            @Override
            public boolean verifyCondition(CharSequence s) {
                return mEtTerminalNum.getText().toString().trim().length() >= 6 && mEtGpsCardNum.getText().toString().trim().length() >= 6 &&
                        mEtGpsSerialNum.getText().toString().trim().length() >= 6 && s.length() > 6;
            }
        });

        mEtGpsCardNum.addTextChangedListener(new VerifyButtonEnableWatcher(mContext, mTvAdd) {
            @Override
            public boolean verifyCondition(CharSequence s) {
                return mEtTerminalNum.getText().toString().trim().length() >= 6 && mEtSecretKey.getText().toString().trim().length() >= 6 &&
                        mEtGpsSerialNum.getText().toString().trim().length() >= 6 && s.length() > 6;
            }
        });

        mEtGpsSerialNum.addTextChangedListener(new VerifyButtonEnableWatcher(mContext, mTvAdd) {
            @Override
            public boolean verifyCondition(CharSequence s) {
                return mEtTerminalNum.getText().toString().trim().length() >= 6 && mEtSecretKey.getText().toString().trim().length() >= 6 &&
                        mEtGpsCardNum.getText().toString().trim().length() >= 6 && s.length() > 6;
            }
        });

    }

    @Override
    public void requestSuccess(int requestTag, JSONObject response, int showLoad) throws JSONException {
        super.requestSuccess(requestTag, response, showLoad);
        switch (requestTag) {
            case RequestAction.TAG_SHOPADDPRINTER:
                L.e("添加小铺打印机", response.toString());

                MToast.showToast(mContext, "添加打印机成功");
                finish();
                break;
        }
    }

}
