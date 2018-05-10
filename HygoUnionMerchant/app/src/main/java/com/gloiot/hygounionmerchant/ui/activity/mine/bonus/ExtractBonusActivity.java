package com.gloiot.hygounionmerchant.ui.activity.mine.bonus;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gloiot.hygounionmerchant.R;
import com.gloiot.hygounionmerchant.server.network.RequestAction;
import com.gloiot.hygounionmerchant.ui.BaseActivity;
import com.gloiot.hygounionmerchant.ui.activity.login.ForgetPwd1Activity;
import com.gloiot.hygounionmerchant.ui.activity.mine.BindingHygoAccountActivity;
import com.gloiot.hygounionmerchant.utils.CommonUtils;
import com.gloiot.hygounionmerchant.utils.ConstantUtils;
import com.zyd.wlwsdk.utils.L;
import com.zyd.wlwsdk.utils.MD5Utlis;
import com.zyd.wlwsdk.utils.MToast;
import com.zyd.wlwsdk.utils.SharedPreferencesUtils;
import com.zyd.wlwsdk.utils.StatusBarUtil;
import com.zyd.wlwsdk.widge.MyDialogBuilder;
import com.zyd.wlwsdk.widge.MyNewDialogBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 提取红利(我的收益)
 * Created by Dlt on 2017/8/24 14:37
 */
public class ExtractBonusActivity extends BaseActivity implements View.OnClickListener, BaseActivity.RequestErrorCallback {

    @Bind(R.id.tv_weijiesuan)
    TextView mTvWeijiesuan;
    @Bind(R.id.tv_yijiesuan)
    TextView mTvYijiesuan;
    @Bind(R.id.tv_weitiqu)
    TextView mTvWeitiqu;
    @Bind(R.id.ll_layout_weitiqu)
    LinearLayout mLlLayoutWeitiqu;
    @Bind(R.id.tv_available_bonus)
    TextView mTvAvailableBonus;
    @Bind(R.id.rl_middle)
    RelativeLayout mRlMiddle;
    @Bind(R.id.rl_extract_hygo)
    RelativeLayout mRlExtractHygo;
    @Bind(R.id.tv_extract_detail)
    TextView mTvExtractDetail;

    private String accountType;
    private MyNewDialogBuilder myDialogBuilder;
    private String bindingHygoState;
    private String accountPayPwdState = "", extractBonus;
    private String weijiesuan, yijiesuan, weitiqu, ketiqu;

    @Override
    protected void onResume() {
        super.onResume();
        bindingHygoState = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_BINDINGHYGOSTATE, "");
        accountPayPwdState = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTPAYPWDSTATE, "");
    }

    @Override
    public int initResource() {
        return R.layout.activity_extract_bonus;
    }

    @Override
    public void initData() {
        accountType = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTTYPE, "");
        StatusBarUtil.setColor(this, getResources().getColor(R.color.green_21d1c1), 0);
        CommonUtils.setTitleBar(this, true, "我的收益", "");
        if (accountType.equals("小铺")) {
            mLlLayoutWeitiqu.setVisibility(View.VISIBLE);
        } else {
            mLlLayoutWeitiqu.setVisibility(View.GONE);
        }
        requestHandleArrayList.add(requestAction.getMyAccountIncome(this, accountType));
        setRequestErrorCallback(this);
    }

    @OnClick({R.id.rl_extract_hygo, R.id.tv_extract_detail})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_extract_hygo:
                if (bindingHygoState.equals("未绑定")) {

                    myDialogBuilder = MyNewDialogBuilder.getInstance(mContext);
                    myDialogBuilder
                            .withTitie("未绑定环游购账户")
                            .withCenterContent("您还没有绑定环游购账户，绑定后方可提取")
                            .withEffects(MyDialogBuilder.SlideTop, MyDialogBuilder.SlideTopDismiss)
                            .setTwoBtn("知道了", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    myDialogBuilder.dismiss();
                                }
                            }, "去绑定", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    myDialogBuilder.dismissNoAnimator();
                                    startActivity(new Intent(ExtractBonusActivity.this, BindingHygoAccountActivity.class));
                                }
                            })
                            .show();

                } else {
                    extractBonus = "";
                    showExtractBonusDialog();
                }

                break;
            case R.id.tv_extract_detail:
                startActivity(new Intent(ExtractBonusActivity.this, ExtractDetailActivity.class));
                break;
        }
    }


    //显示提取积分弹窗
    private void showExtractBonusDialog() {
        myDialogBuilder = MyNewDialogBuilder.getInstance(mContext);
        myDialogBuilder.setCustomView(R.layout.layout_extract_to_bonus);
        myDialogBuilder
                .withTitie("提取到红利账户")
                .withEffects(MyDialogBuilder.SlideTop, MyDialogBuilder.SlideTopDismiss)
//                .setBtnClick("提取", MyDialogBuilder.BtnNormal, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        View view = myDialogBuilder.getCustomView();
//                        EditText bonus = (EditText) view.findViewById(R.id.et_extract_bonus);
//                        EditText payPwd = (EditText) view.findViewById(R.id.et_pay_pwd);
////                        payPwd.addTextChangedListener(new MaxLengthWatcher(6, payPwd));//最多6位
//                        if (TextUtils.isEmpty(bonus.getText().toString().trim())) {
//                            MToast.showToast(mContext, "请输入提取积分");
//                        } else if (bonus.getText().toString().trim().length() < 3) {
//                            MToast.showToast(mContext, "积分只能整百提取");
//                        } else if (!bonus.getText().toString().trim().substring(bonus.getText().toString().trim().length() - 2,
//                                bonus.getText().toString().trim().length()).equals("00")) {
//                            MToast.showToast(mContext, "积分只能整百提取");
//                        } else if (Float.parseFloat(bonus.getText().toString().trim()) > Float.parseFloat(ketiqu)) {
//                            MToast.showToast(mContext, "积分不足");
//                        } else if (TextUtils.isEmpty(payPwd.getText().toString().trim())) {
//                            MToast.showToast(mContext, "请输入支付密码");
//                        } else if (payPwd.getText().toString().length() != 6) {
//                            MToast.showToast(mContext, "您输入的支付密码有误");
//                        } else {
//                            myDialogBuilder.dismissNoAnimator();
//                            //调接口请求
//                            extractBonus = bonus.getText().toString().trim();
//                            String pwd = MD5Utlis.Md5(payPwd.getText().toString().trim());
//                            requestHandleArrayList.add(requestAction.extractBonusToHygo(ExtractBonusActivity.this, extractBonus, pwd, accountType));
//                        }
//                    }
//                })
                .setTwoBtnSpecial("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myDialogBuilder.dismiss();
                    }
                }, "提取", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        View view = myDialogBuilder.getCustomView();
                        EditText bonus = (EditText) view.findViewById(R.id.et_extract_bonus);
                        EditText payPwd = (EditText) view.findViewById(R.id.et_pay_pwd);
//                        payPwd.addTextChangedListener(new MaxLengthWatcher(6, payPwd));//最多6位
                        if (TextUtils.isEmpty(bonus.getText().toString().trim())) {
                            MToast.showToast(mContext, "请输入提取积分");
                        } else if (bonus.getText().toString().trim().length() < 3) {
                            MToast.showToast(mContext, "积分只能整百提取");
                        } else if (!bonus.getText().toString().trim().substring(bonus.getText().toString().trim().length() - 2,
                                bonus.getText().toString().trim().length()).equals("00")) {
                            MToast.showToast(mContext, "积分只能整百提取");
                        } else if (Float.parseFloat(bonus.getText().toString().trim()) > Float.parseFloat(ketiqu)) {
                            MToast.showToast(mContext, "积分不足");
                        } else if (TextUtils.isEmpty(payPwd.getText().toString().trim())) {
                            MToast.showToast(mContext, "请输入支付密码");
                        } else if (payPwd.getText().toString().length() != 6) {
                            MToast.showToast(mContext, "您输入的支付密码有误");
                        } else {
                            myDialogBuilder.dismissNoAnimator();
                            //调接口请求
                            extractBonus = bonus.getText().toString().trim();
                            String pwd = MD5Utlis.Md5(payPwd.getText().toString().trim());
                            requestHandleArrayList.add(requestAction.extractBonusToHygo(ExtractBonusActivity.this, extractBonus, pwd, accountType));
                        }
                    }
                })
                .show();
    }

    @Override
    public void requestSuccess(int requestTag, JSONObject response, int showLoad) throws JSONException {
        super.requestSuccess(requestTag, response, showLoad);
        switch (requestTag) {
            case RequestAction.TAG_MYACCOUNTINCOME:
                L.e("我的收益", response.toString());

                String isCanExtract = response.getString("开放提取");//选项有“是”/“否”
                if (isCanExtract.equals("是")) {
                    mRlMiddle.setVisibility(View.VISIBLE);
                    mRlExtractHygo.setVisibility(View.VISIBLE);
                    mTvExtractDetail.setVisibility(View.VISIBLE);
                } else {
                    mRlMiddle.setVisibility(View.GONE);
                    mRlExtractHygo.setVisibility(View.GONE);
                    mTvExtractDetail.setVisibility(View.GONE);
                }

                weijiesuan = response.getString("未结算");
                yijiesuan = response.getString("已结算");
                weitiqu = response.getString("未提取");
                ketiqu = response.getString("可提取");
                mTvWeijiesuan.setText(weijiesuan);
                mTvYijiesuan.setText(yijiesuan);
                mTvWeitiqu.setText(weitiqu);
                mTvAvailableBonus.setText(ketiqu);
                break;
            case RequestAction.TAG_EXTRACTBONUSTOHYGO:
                L.e("提取积分到环游购", response.toString());
                MToast.showToast(mContext, "提取成功");

//                float yijiesuanF = Float.parseFloat(yijiesuan);
                float ketiquF = Float.parseFloat(ketiqu);
                float weitiquF = Float.parseFloat(weitiqu);
                float extractBonusF = Float.parseFloat(extractBonus);

                //需要再做数据格式化，两位小数。

//                String yijiesuanC = (yijiesuanF - extractBonusF) + "";
                String ketiquC = (ketiquF - extractBonusF) + "";
                String weitiquC = (weitiquF - extractBonusF) + "";

//                double yijiesuanD = Double.parseDouble(yijiesuanC);
                double ketiquD = Double.parseDouble(ketiquC);
                double weitiquD = Double.parseDouble(weitiquC);

//                yijiesuan = String.format("%.2f", yijiesuanD);
                ketiqu = String.format("%.2f", ketiquD);
                weitiqu = String.format("%.2f", weitiquD);

//                mTvYijiesuan.setText(yijiesuan);
                mTvAvailableBonus.setText(ketiqu);
                mTvWeitiqu.setText(weitiqu);

                break;
        }
    }

    @Override
    public void requestErrorcallback(int requestTag, JSONObject response) throws Exception {
        switch (requestTag) {
            case RequestAction.TAG_EXTRACTBONUSTOHYGO:
                L.e("提取积分错误", response.toString());
                extractBonus = "";
                if (response.getString("状态").equals("支付密码错误")) {

                    myDialogBuilder = MyNewDialogBuilder.getInstance(mContext);
                    myDialogBuilder
                            .withTitie("支付密码错误")
                            .withCenterContent("支付密码输入错误，请检查")
                            .withEffects(MyDialogBuilder.SlideTop, MyDialogBuilder.SlideTopDismiss)
                            .setTwoBtn("重试", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    myDialogBuilder.dismiss();

                                    showExtractBonusDialog();

                                }
                            }, "忘记密码", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    myDialogBuilder.dismissNoAnimator();
                                    Intent intent = new Intent(ExtractBonusActivity.this, ForgetPwd1Activity.class);
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
