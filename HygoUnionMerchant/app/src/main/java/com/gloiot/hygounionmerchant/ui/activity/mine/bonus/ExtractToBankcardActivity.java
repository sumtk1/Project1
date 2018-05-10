package com.gloiot.hygounionmerchant.ui.activity.mine.bonus;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gloiot.hygounionmerchant.R;
import com.gloiot.hygounionmerchant.server.network.RequestAction;
import com.gloiot.hygounionmerchant.ui.BaseActivity;
import com.gloiot.hygounionmerchant.ui.activity.login.ForgetPwd1Activity;
import com.gloiot.hygounionmerchant.utils.CommonUtils;
import com.gloiot.hygounionmerchant.utils.ConstantUtils;
import com.gloiot.hygounionmerchant.utils.DialogUtils;
import com.zyd.wlwsdk.utils.L;
import com.zyd.wlwsdk.utils.MD5Utlis;
import com.zyd.wlwsdk.utils.MToast;
import com.zyd.wlwsdk.utils.NoDoubleClickUtils;
import com.zyd.wlwsdk.utils.SharedPreferencesUtils;
import com.zyd.wlwsdk.widge.MyDialogBuilder;
import com.zyd.wlwsdk.widge.MyNewDialogBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.OnClick;

import static com.gloiot.hygounionmerchant.ui.activity.mine.bonus.SelectBankcardActivity.RESULT_SELECTBANKCARD;

/**
 * 提取到银行卡/支付宝
 * Created by Dlt on 2017/10/31 17:43
 */
public class ExtractToBankcardActivity extends BaseActivity implements View.OnClickListener, BaseActivity.RequestErrorCallback {

    @Bind(R.id.rl_bank_card)
    RelativeLayout mRlBankCard;
    @Bind(R.id.iv_bank_logo)
    ImageView mIvBankLogo;
    @Bind(R.id.tv_bank_name)
    TextView mTvBankName;
    @Bind(R.id.tv_card_type)
    TextView mTvCardType;
    @Bind(R.id.tv_card_num)
    TextView mTvCardNum;
    @Bind(R.id.rl_alipay)
    RelativeLayout mRlAlipay;
    @Bind(R.id.tv_alipay_account)
    TextView mTvAlipayAccount;
    @Bind(R.id.tv_alipay_name)
    TextView mTvAlipayName;
    @Bind(R.id.et_tixian_money)
    EditText mEtTixianMoney;
    @Bind(R.id.tv_ketixian_yu_e)
    TextView mTvKetixianYuE;
    @Bind(R.id.tv_explain)
    TextView mTvExplain;
    @Bind(R.id.tv_confirm)
    TextView mTvConfirm;

    private String accountType = "";
    private MyNewDialogBuilder myDialogBuilder;
    public static final int SELECT_BANKCARD = 1;
    private String cardId = "", personName = "", cardNum = "", bankName = "", ketiqu = "";
    private String alipayId = "", alipayAccount = "", alipayName = "";
    private String extractBonus;
    private String shouxufeibili = "";
    private String isQuotaExtract = "";//是否定额提取
    private String quotaMoney = "";//定额金额
    private String extractType = "";//提取方式：bankcard/alipay

    @Override
    public int initResource() {
        return R.layout.activity_extract_to_bankcard;
    }

    @Override
    public void initData() {
        accountType = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTTYPE, "");
        Intent intent = getIntent();
        extractType = intent.getStringExtra("type");
        ketiqu = intent.getStringExtra("ketiqu");
//        isQuotaExtract = intent.getStringExtra("isQuotaExtract");
//        quotaMoney = intent.getStringExtra("quotaMoney");
        mTvKetixianYuE.setText("可提现余额  " + ketiqu);

        if (extractType.equals("bankcard")) {
            CommonUtils.setTitleBar(this, true, "提取到银行卡", "");
            requestHandleArrayList.add(requestAction.getExtractExplain(ExtractToBankcardActivity.this, "银行卡", accountType));
        } else if (extractType.equals("alipay")) {
            CommonUtils.setTitleBar(this, true, "提取到支付宝", "");
            requestHandleArrayList.add(requestAction.getExtractExplain(ExtractToBankcardActivity.this, "支付宝", accountType));
        }

        setInputListener();
        setRequestErrorCallback(this);

    }

    private void setInputListener() {

        mEtTixianMoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //小数点后位数控制
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {//可以适当输入多位，位数会在最后计算时四舍五入
                        s = s.toString().subSequence(0, s.toString().indexOf(".") + 3);//小数点后最多输入2位
                        mEtTixianMoney.setText(s);
                        mEtTixianMoney.setSelection(s.length());
                    }
                }
                //第一位为点
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    mEtTixianMoney.setText(s);
                    mEtTixianMoney.setSelection(2);
                }
                //第一位为零，后面只能输入点
                if (s.toString().startsWith("0") && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        mEtTixianMoney.setText(s.subSequence(0, 1));
                        mEtTixianMoney.setSelection(1);
//                        return;
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    /**
     * 设置银行卡的数据及显示
     */
    private void setBankcardData() {
        if (bankName.contains("中国银行")) {
            mIvBankLogo.setBackgroundResource(R.drawable.bank_china);
        } else if (bankName.contains("建设银行")) {
            mIvBankLogo.setBackgroundResource(R.drawable.bank_zhongguojianshe);
        } else if (bankName.contains("交通银行")) {
            mIvBankLogo.setBackgroundResource(R.drawable.bank_zhongguojiaotong);
        } else if (bankName.contains("工商银行")) {
            mIvBankLogo.setBackgroundResource(R.drawable.bank_zhongguogongshang);
        } else if (bankName.contains("民生银行")) {
            mIvBankLogo.setBackgroundResource(R.drawable.bank_zhongguominsheng);
        } else if (bankName.contains("农业银行")) {
            mIvBankLogo.setBackgroundResource(R.drawable.bank_nongye);
        } else if (bankName.contains("中信银行")) {
            mIvBankLogo.setBackgroundResource(R.drawable.bank_zhongxin);
        } else if (bankName.contains("招商银行")) {
            mIvBankLogo.setBackgroundResource(R.drawable.bank_zhaoshang);
        } else if (bankName.contains("华夏银行")) {
            mIvBankLogo.setBackgroundResource(R.drawable.bank_huaxia);
        } else if (bankName.contains("光大银行")) {
            mIvBankLogo.setBackgroundResource(R.drawable.bank_guangda);
        } else if (bankName.contains("浦发银行")) {
            mIvBankLogo.setBackgroundResource(R.drawable.bank_pufa);
        } else if (bankName.contains("兴业银行")) {
            mIvBankLogo.setBackgroundResource(R.drawable.bank_xingye);
        } else if (bankName.contains("北京银行")) {
            mIvBankLogo.setBackgroundResource(R.drawable.bank_beijing);
        } else if (bankName.contains("广发银行")) {
            mIvBankLogo.setBackgroundResource(R.drawable.bank_guangfa);
        } else if (bankName.contains("平安银行")) {
            mIvBankLogo.setBackgroundResource(R.drawable.bank_pingan);
        } else if (bankName.contains("上海银行")) {
            mIvBankLogo.setBackgroundResource(R.drawable.bank_shanghai);
        } else if (bankName.contains("邮储银行")) {
            mIvBankLogo.setBackgroundResource(R.drawable.bank_zhongguoyouzheng);
        } else {
            mIvBankLogo.setBackgroundResource(R.drawable.ic_bank_moren);
        }
        mTvBankName.setText(bankName);
        mTvCardType.setText("储蓄卡");
        try {
            mTvCardNum.setText("*** **** **** " + cardNum.substring(cardNum.length() - 4, cardNum.length()));
        } catch (StringIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置支付宝的数据及显示
     */
    private void setAlipayData() {
        mTvAlipayName.setText(alipayName);

        try {
            if (CommonUtils.isInteger(alipayAccount) && alipayAccount.length() == 11) {//十一位整数，默认是手机号
                mTvAlipayAccount.setText(alipayAccount.substring(0, 3) + " **** " + alipayAccount.substring(alipayAccount.length() - 4, alipayAccount.length()));
            } else if (alipayAccount.contains("@") && alipayAccount.contains(".") && alipayAccount.indexOf("@") < alipayAccount.indexOf(".")) {//邮箱---@前三位的字符全部隐藏
                int index = alipayAccount.indexOf("@");
                mTvAlipayAccount.setText("****" + alipayAccount.substring(index - 3, alipayAccount.length()));
            } else {//其他情况
                mTvAlipayAccount.setText("****" + alipayAccount.substring(alipayAccount.length() - 3, alipayAccount.length()));
            }
        } catch (StringIndexOutOfBoundsException e) {
            e.printStackTrace();
            L.e("支付宝账号异常", "角标越界");
        }
    }

    @OnClick({R.id.rl_bank_card, R.id.rl_alipay, R.id.tv_confirm, R.id.tv_extract_records})
    @Override
    public void onClick(View v) {
        if (NoDoubleClickUtils.isDoubleClick()) return; // 防止连续点击
        Intent intent;
        switch (v.getId()) {
            case R.id.rl_bank_card:
                intent = new Intent(ExtractToBankcardActivity.this, SelectBankcardActivity.class);
                intent.putExtra("type", "bankcard");
                intent.putExtra("cardNum", cardNum);
                startActivityForResult(intent, SELECT_BANKCARD);
                break;
            case R.id.rl_alipay:
                intent = new Intent(ExtractToBankcardActivity.this, SelectBankcardActivity.class);
                intent.putExtra("type", "alipay");
                intent.putExtra("cardNum", alipayAccount);
                startActivityForResult(intent, SELECT_BANKCARD);
                break;
            case R.id.tv_confirm:

                extractBonus = mEtTixianMoney.getText().toString().trim();
//                if (TextUtils.isEmpty(extractBonus)) {
//                    MToast.showToast(mContext, "请输入提现金额");
//                } else if (extractBonus.length() < 3) {
//                    MToast.showToast(mContext, "只能整百提取");
//                } else if (!extractBonus.substring(extractBonus.length() - 2, extractBonus.length()).equals("00")) {
//                    MToast.showToast(mContext, "只能整百提取");
//                } else if (Float.parseFloat(extractBonus) > Float.parseFloat(ketiqu)) {
//                    MToast.showToast(mContext, "可提现金额不足");
//                } else {
//                    inputPWDToExtract();
//                }

                //需求怎么写就怎么来，别管合不合理是否周全，不逼逼
                if (TextUtils.isEmpty(extractBonus)) {
                    MToast.showToast(mContext, "请输入提现金额");
                } else if (extractBonus.length() < 3) {
                    MToast.showToast(mContext, "请输入整百积分");
                } else if (!extractBonus.substring(extractBonus.length() - 2, extractBonus.length()).equals("00")) {
                    MToast.showToast(mContext, "请输入整百积分");
                } else {
                    inputPWDToExtract();
                }

                break;
            case R.id.tv_extract_records:
                intent = new Intent(ExtractToBankcardActivity.this, ExtractRecordActivity.class);
                intent.putExtra("extractType", extractType);
                startActivity(intent);
                break;
        }
    }

    /**
     * 输入支付密码去提取
     */
    private void inputPWDToExtract() {
        DialogUtils.oneBtnPwd(this, "¥" + extractBonus, new DialogUtils.PasswordCallback() {
            @Override
            public void callback(String data) {
                if (TextUtils.isEmpty(data) || data.length() < 6) {
                    MToast.showToast(mContext, "请输入支付密码");
                } else {
                    if (extractType.equals("bankcard")) {
                        requestHandleArrayList.add(requestAction.extractToBankcard(ExtractToBankcardActivity.this, accountType, extractBonus,
                                MD5Utlis.Md5(data), cardNum, bankName, personName));
                    } else if (extractType.equals("alipay")) {
                        requestHandleArrayList.add(requestAction.extractToAlipayAccount(ExtractToBankcardActivity.this, extractBonus,
                                alipayAccount, alipayName, MD5Utlis.Md5(data), accountType));
                    }
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            switch (resultCode) {
                case RESULT_SELECTBANKCARD:
                    switch (requestCode) {
                        case SELECT_BANKCARD:
                            if (extractType.equals("bankcard")) {
                                cardId = data.getStringExtra("cardId");
                                personName = data.getStringExtra("personName");
                                cardNum = data.getStringExtra("cardNum");
                                bankName = data.getStringExtra("bankName");
                                setBankcardData();
                            } else if (extractType.equals("alipay")) {
                                alipayAccount = data.getStringExtra("alipayAccount");
                                alipayName = data.getStringExtra("alipayName");
                                setAlipayData();
                            }
                            break;
                    }
                    break;
            }

        }

    }

    @Override
    public void requestSuccess(int requestTag, JSONObject response, int showLoad) throws JSONException {
        super.requestSuccess(requestTag, response, showLoad);
        switch (requestTag) {
            case RequestAction.TAG_EXTRACTEXPLAIN:
                L.e("提现说明", response.toString());
                String tishiyu = response.getString("提示语");
                shouxufeibili = response.getString("手续费比例");
                mTvExplain.setText(tishiyu);

                isQuotaExtract = response.getString("定额提取");
                quotaMoney = response.getString("定额金额");

                if (isQuotaExtract.equals("是")) {//定额提取
                    mEtTixianMoney.setFocusable(false);
                    mEtTixianMoney.setFocusableInTouchMode(false);
                    mEtTixianMoney.setText(quotaMoney);
                } else {//非定额提取
                    mEtTixianMoney.setFocusableInTouchMode(true);
                    mEtTixianMoney.setFocusable(true);
                    mEtTixianMoney.requestFocus();
                }

                if (extractType.equals("bankcard")) {
                    JSONObject jsonObject = response.getJSONObject("银行卡");
                    cardId = jsonObject.getString("id");
                    personName = jsonObject.getString("持卡人姓名");
                    cardNum = jsonObject.getString("银行卡号");
                    bankName = jsonObject.getString("银行名");

                    mRlBankCard.setVisibility(View.VISIBLE);
                    setBankcardData();
                    mTvConfirm.setVisibility(View.VISIBLE);
                } else if (extractType.equals("alipay")) {
                    JSONObject jsonObject = response.getJSONObject("支付宝");
                    alipayId = jsonObject.getString("id");
                    alipayAccount = jsonObject.getString("支付宝账号");
                    alipayName = jsonObject.getString("支付宝姓名");

                    mRlAlipay.setVisibility(View.VISIBLE);
                    setAlipayData();
                    mTvConfirm.setVisibility(View.VISIBLE);
                }
                break;
            case RequestAction.TAG_EXTRACTTOBANKCARD:
                L.e("提现到银行卡", response.toString());
                Intent intent = new Intent(ExtractToBankcardActivity.this, ExtractFinishActivity.class);
                intent.putExtra("type", "bankcard");
                intent.putExtra("bankName", bankName);
                intent.putExtra("cardNum", cardNum);
                intent.putExtra("extractBonus", extractBonus);
//                intent.putExtra("shouxufei", Float.parseFloat(extractBonus) * Float.parseFloat(shouxufeibili) + "");
                intent.putExtra("shouxufei", response.getString("手续费"));
                startActivity(intent);
                finish();
                break;
            case RequestAction.TAG_EXTRACTTOALIPAYACCOUNT:
                L.e("提现到支付宝", response.toString());
                Intent intent1 = new Intent(ExtractToBankcardActivity.this, ExtractFinishActivity.class);
                intent1.putExtra("type", "alipay");
                intent1.putExtra("alipayAccount", response.getString("支付宝账号"));
                intent1.putExtra("extractBonus", response.getString("提取金额"));
                intent1.putExtra("shouxufei", response.getString("手续费"));
                startActivity(intent1);
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void requestErrorcallback(int requestTag, JSONObject response) throws Exception {
        switch (requestTag) {
            case RequestAction.TAG_EXTRACTTOBANKCARD:
            case RequestAction.TAG_EXTRACTTOALIPAYACCOUNT:
                L.e("提取积分错误", response.toString());
                if (response.getString("状态").contains("支付密码错误")) {

                    myDialogBuilder = MyNewDialogBuilder.getInstance(mContext);
                    myDialogBuilder
                            .withTitie("支付密码错误")
                            .withCenterContent("支付密码输入错误，请检查")
                            .withEffects(MyDialogBuilder.SlideTop, MyDialogBuilder.SlideTopDismiss)
                            .setTwoBtn("重试", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    myDialogBuilder.dismiss();

                                    inputPWDToExtract();

                                }
                            }, "忘记密码", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    myDialogBuilder.dismissNoAnimator();
                                    Intent intent = new Intent(ExtractToBankcardActivity.this, ForgetPwd1Activity.class);
                                    intent.putExtra("pwdType", "pay");
                                    startActivity(intent);
                                }
                            })
                            .show();

                } else {
//                    MToast.showToast(mContext, response.getString("状态"));
                }

                break;
            default:
//                MToast.showToast(mContext, response.getString("状态"));
                break;
        }
    }

}
