package com.gloiot.hygounionmerchant.ui.activity.mine.bankcard;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.gloiot.hygounionmerchant.R;
import com.gloiot.hygounionmerchant.server.network.RequestAction;
import com.gloiot.hygounionmerchant.ui.BaseActivity;
import com.gloiot.hygounionmerchant.utils.CommonUtils;
import com.gloiot.hygounionmerchant.utils.ConstantUtils;
import com.gloiot.hygounionmerchant.utils.DialogUtils;
import com.zyd.wlwsdk.utils.L;
import com.zyd.wlwsdk.utils.MToast;
import com.zyd.wlwsdk.utils.SharedPreferencesUtils;
import com.zyd.wlwsdk.widge.MyDialogBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 添加银行卡
 * Created by Dlt on 2017/10/24 17:34
 */
public class AddBankCardActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.tv_bank_name)
    TextView mTvBankName;
    @Bind(R.id.et_branch_bank_name)
    EditText mEtBranchBankName;
    @Bind(R.id.et_card_num)
    EditText mEtCardNum;
    @Bind(R.id.tv_card_type)
    TextView mTvCardType;
    @Bind(R.id.et_person_name)
    EditText mEtPersonName;
    @Bind(R.id.et_id_num)
    EditText mEtIdNum;
    @Bind(R.id.et_phone_num)
    EditText mEtPhoneNum;
    @Bind(R.id.tv_confirm)
    TextView mTvConfirm;

    private String accountType;
    private String bankName = "", branchName = "", cardNum = "", cardType = "", personName = "", personIDNum = "", personPhoneNum = "";
    private String[] bankList;
    private Map<String, String> bankMap = new HashMap<>();
    private int place = 0;

    @Override
    public int initResource() {
        return R.layout.activity_add_bank_card;
    }

    @Override
    public void initData() {
        accountType = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTTYPE, "");
        CommonUtils.setTitleBar(this, true, "添加银行卡", "");
    }

    @OnClick({R.id.rl_select_bank, R.id.tv_confirm})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_select_bank:
                if (bankList != null) {
                    showSelectWindow();
                } else {
                    requestHandleArrayList.add(requestAction.getBankList(AddBankCardActivity.this, accountType));
                }
                break;
            case R.id.tv_confirm:
                if (verifyData()) {
                    String bankId = bankMap.get(bankName);
                    L.e("bankId", "--" + bankId);
                    requestHandleArrayList.add(requestAction.addBankcard(AddBankCardActivity.this, accountType, bankName, bankId, branchName, cardNum,
                            cardType, personName, personIDNum, personPhoneNum));
                }
                break;
        }
    }

    /**
     * 显示选择银行弹窗
     */
    private void showSelectWindow() {
        DialogUtils.towBtnLoopView(mContext, "选择银行卡类型", bankList, place, new MyDialogBuilder.LoopViewCallBack() {
            @Override
            public void callBack(int data) {
                place = data;
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTvBankName.setText(bankList[place]);
                DialogUtils.dismissDialog();
            }
        });
    }

    @Override
    public void requestSuccess(int requestTag, JSONObject response, int showLoad) throws JSONException {
        super.requestSuccess(requestTag, response, showLoad);
        switch (requestTag) {
            case RequestAction.TAG_BANKLIST:
                L.e("银行数据", response.toString());
                int num = Integer.parseInt(response.getString("条数"));
                if (num != 0) {
                    JSONArray jsonArray = response.getJSONArray("数据");
                    bankList = new String[jsonArray.length()];
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                        bankList[i] = jsonObject.getString("银行名称");
                        bankMap.put(bankList[i], jsonObject.getString("id"));
                    }
                    if (bankList != null) {
                        showSelectWindow();
                    }
                } else {
                    MToast.showToast(mContext, "呀！发生了一个bug...");
                }
                break;
            case RequestAction.TAG_ADDBANKCARD:
                MToast.showToast(this, "添加银行卡成功");
                finish();
                break;
            default:
                break;
        }
    }

    /**
     * 页面数据验证
     *
     * @return
     */
    private boolean verifyData() {

        bankName = mTvBankName.getText().toString().trim();
        branchName = mEtBranchBankName.getText().toString().trim();
        cardNum = mEtCardNum.getText().toString().trim();
        cardType = mTvCardType.getText().toString().trim();
        personName = mEtPersonName.getText().toString().trim();
        personIDNum = mEtIdNum.getText().toString().trim();
        personPhoneNum = mEtPhoneNum.getText().toString().trim();

        if (TextUtils.isEmpty(bankName)) {
            MToast.showToast(mContext, "请选择开卡银行");
            return false;
        } else if (TextUtils.isEmpty(branchName)) {
            MToast.showToast(mContext, "请输入开户行支行名称");
            return false;
        } else if (TextUtils.isEmpty(cardNum)) {
            MToast.showToast(mContext, "请输入银行卡号");
            return false;
        } else if (cardNum.length() < 16 || cardNum.length() > 19) {
            MToast.showToast(mContext, "银行卡号输入错误");
            return false;
        } else if (TextUtils.isEmpty(personName)) {
            MToast.showToast(mContext, "请输入持卡人姓名");
            return false;
        } else if (TextUtils.isEmpty(personIDNum)) {
            MToast.showToast(mContext, "请输入身份证号码");
            return false;
        } else if (personIDNum.length() != 18) {
            MToast.showToast(mContext, "身份证号码输入错误");
            return false;
        } else if (TextUtils.isEmpty(personPhoneNum)) {
            MToast.showToast(mContext, "请输入银行预留号码");
            return false;
        } else if (personPhoneNum.length() != 11) {
            MToast.showToast(mContext, "手机号码输入错误");
            return false;
        } else {
            return true;
        }
    }

}
