package com.gloiot.hygounionmerchant.ui.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gloiot.hygounionmerchant.R;
import com.gloiot.hygounionmerchant.ui.BaseFragment;
import com.gloiot.hygounionmerchant.ui.activity.mine.BindingHygoAccountActivity;
import com.gloiot.hygounionmerchant.ui.activity.mine.SuggestionFeedbackActivity;
import com.gloiot.hygounionmerchant.ui.activity.mine.accountinfo.MyAccountInfoActivity;
import com.gloiot.hygounionmerchant.ui.activity.mine.alipay.MyAliPayActivity;
import com.gloiot.hygounionmerchant.ui.activity.mine.bankcard.MyBankCardActivity;
import com.gloiot.hygounionmerchant.ui.activity.mine.bonus.NewMyEarningsActivity;
import com.gloiot.hygounionmerchant.ui.activity.mine.printer.MyPrinterActivity;
import com.gloiot.hygounionmerchant.ui.activity.mine.setting.AccountSecurityActivity;
import com.gloiot.hygounionmerchant.ui.activity.mine.setting.SettingActivity;
import com.gloiot.hygounionmerchant.ui.activity.mine.zizhirenzheng.CertificationActivity;
import com.gloiot.hygounionmerchant.ui.webview.WebActivity;
import com.gloiot.hygounionmerchant.utils.CommonUtils;
import com.gloiot.hygounionmerchant.utils.ConstantUtils;
import com.zyd.wlwsdk.utils.L;
import com.zyd.wlwsdk.utils.NoDoubleClickUtils;
import com.zyd.wlwsdk.utils.SharedPreferencesUtils;
import com.zyd.wlwsdk.widge.MyDialogBuilder;
import com.zyd.wlwsdk.widge.MyNewDialogBuilder;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Dlt on 2017/8/14 17:18
 */
public class MyFragment extends BaseFragment implements View.OnClickListener {

    public static final String TAG = MyFragment.class.getSimpleName();
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.iv_image)
    ImageView mIvImage;
    @Bind(R.id.tv_name)
    TextView mTvName;
    @Bind(R.id.tv_yirenzheng)
    TextView mTvYirenzheng;
    @Bind(R.id.tv_phone_num)
    TextView mTvPhoneNum;
    @Bind(R.id.iv_account_image)
    ImageView mIvAccountImage;
    @Bind(R.id.tv_account_type)
    TextView mTvAccountType;
    @Bind(R.id.tv_bangding_state)
    TextView mTvBangdingState;
    @Bind(R.id.rl_bingding_hygo)
    RelativeLayout mRlBingdingHygo;
    @Bind(R.id.iv_binding_arrow)
    ImageView mIvBindingArrow;
    @Bind(R.id.rl_printer)
    RelativeLayout mRlPrinter;
    @Bind(R.id.rl_bank_card)
    RelativeLayout mRlBankCard;
    @Bind(R.id.rl_alipay)
    RelativeLayout mRlAlipay;
    @Bind(R.id.rl_my_income)
    RelativeLayout mRlMyIncome;
    @Bind(R.id.rl_user_management)
    RelativeLayout mRlUserManagement;
    @Bind(R.id.iv_zizhirenzheng_arrow)
    ImageView mIvZizhirenzhengArrow;
    @Bind(R.id.tv_zizhirenzheng_state)
    TextView mTvZizhirenzhengState;
    @Bind(R.id.rl_my_zizhirenzheng)
    RelativeLayout mRlMyZizhirenzheng;

    private String accountType = "", identityType = "", zizhirenzhengState = "";
    private MyNewDialogBuilder myDialogBuilder;

    public static Fragment newInstance(int position) {
        MyFragment fragment = new MyFragment();
        return fragment;
    }

    public MyFragment() {

    }

    @Override
    public void onResume() {
        super.onResume();
//        String bindingHygoState = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_BINDINGHYGOSTATE, "");
//        mTvBangdingState.setText(bindingHygoState);
//        if (bindingHygoState.equals("未绑定")) {
//            mRlBingdingHygo.setClickable(true);
//            mIvBindingArrow.setVisibility(View.VISIBLE);
//        } else {
//            mRlBingdingHygo.setClickable(false);
//            mIvBindingArrow.setVisibility(View.GONE);
//        }

        zizhirenzhengState = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ZIZHIRENZHENGSTATE, "");
        if (identityType.equals("负责人")) {

            if (zizhirenzhengState.equals("去认证")) {
                mRlMyZizhirenzheng.setVisibility(View.VISIBLE);
                mRlMyZizhirenzheng.setClickable(true);
                mIvZizhirenzhengArrow.setVisibility(View.VISIBLE);
                mTvZizhirenzhengState.setText("去认证");
                mTvZizhirenzhengState.setTextColor(Color.parseColor("#cccccc"));
                mTvYirenzheng.setVisibility(View.GONE);
            } else if (zizhirenzhengState.equals("认证中")) {
                mRlMyZizhirenzheng.setVisibility(View.VISIBLE);
                mRlMyZizhirenzheng.setClickable(false);
                mIvZizhirenzhengArrow.setVisibility(View.GONE);
                mTvZizhirenzhengState.setText("认证中");
                mTvZizhirenzhengState.setTextColor(Color.parseColor("#21d1c1"));
                mTvYirenzheng.setVisibility(View.GONE);
            } else if (zizhirenzhengState.equals("已认证")) {
                mRlMyZizhirenzheng.setVisibility(View.GONE);
                mTvYirenzheng.setVisibility(View.VISIBLE);
            }

        } else {
            mRlMyZizhirenzheng.setVisibility(View.GONE);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void initData() {
        accountType = SharedPreferencesUtils.getString(getActivity(), ConstantUtils.SP_ACCOUNTTYPE, "");
        identityType = SharedPreferencesUtils.getString(getActivity(), ConstantUtils.SP_IDENTITYTYPE, "");
        if (identityType.equals("负责人")) {
            mRlMyIncome.setVisibility(View.VISIBLE);
            mRlBankCard.setVisibility(View.VISIBLE);
            mRlAlipay.setVisibility(View.VISIBLE);
            mRlUserManagement.setVisibility(View.VISIBLE);
//            mRlBingdingHygo.setVisibility(View.VISIBLE);
        } else {
            mRlMyIncome.setVisibility(View.GONE);
            mRlBankCard.setVisibility(View.GONE);
            mRlAlipay.setVisibility(View.GONE);
            mRlUserManagement.setVisibility(View.GONE);
//            mRlBingdingHygo.setVisibility(View.GONE);
        }
        if (accountType.equals("小铺")) {
            CommonUtils.setDisplayImage(mIvAccountImage, "", 0, R.drawable.ic_wodxiaop);
            mTvAccountType.setText("我的小铺");
            mRlPrinter.setVisibility(View.VISIBLE);
        } else if (accountType.equals("旅行社")) {
            CommonUtils.setDisplayImage(mIvAccountImage, "", 0, R.drawable.ic_wodlvxingshe);
            mTvAccountType.setText("我的旅行社");
            mRlPrinter.setVisibility(View.GONE);
        } else if (accountType.equals("酒店")) {
            CommonUtils.setDisplayImage(mIvAccountImage, "", 0, R.drawable.ic_wodjiudian);
            mTvAccountType.setText("我的酒店");
            mRlPrinter.setVisibility(View.GONE);
        }
        String accountPortrait = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTPORTRAIT, "");
        String accountNickName = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTNICKNAME, "");
        String account = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_USERACCOUNT, "");
        if (!TextUtils.isEmpty(accountPortrait) && !accountPortrait.equals("null")) {
            CommonUtils.setDisplayImage(mIvImage, accountPortrait, 120, R.drawable.ic_morentouxiang);
            L.e("头像--", "不为空");
        } else {
            CommonUtils.setDisplayImage(mIvImage, "", 120, R.drawable.ic_morentouxiang);
            L.e("头像--", "为空");
        }

        mTvName.setText(accountNickName);
        mTvPhoneNum.setText(account);

//        mRlBingdingHygo.setClickable(false);
//        mIvBindingArrow.setVisibility(View.GONE);
//        String bindingHygoAccount = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_HYGOACCOUNT, "");
//        mTvBangdingState.setText(bindingHygoAccount);

    }

    @OnClick({R.id.rl_my_zizhirenzheng, R.id.rl_my_account, R.id.rl_my_income, R.id.rl_printer, R.id.rl_bank_card, R.id.rl_alipay, R.id.rl_user_management,
            R.id.rl_account_management, R.id.rl_bingding_hygo, R.id.rl_setting, R.id.rl_suggestion})
    @Override
    public void onClick(View v) {
        if (NoDoubleClickUtils.isDoubleClick()) return;
        switch (v.getId()) {
            case R.id.rl_my_zizhirenzheng://资质认证
                startActivity(new Intent(getActivity(), CertificationActivity.class));
                break;
            case R.id.rl_my_account://我的小铺/酒店/旅行社
                startActivity(new Intent(getActivity(), MyAccountInfoActivity.class));
                break;
            case R.id.rl_my_income://我的收益
//                startActivity(new Intent(getActivity(), ExtractBonusActivity.class));

//                startActivity(new Intent(getActivity(), MyEarningsActivity.class));//20171031我的收益新页面

                startActivity(new Intent(getActivity(), NewMyEarningsActivity.class));//20180102我的收益新页面
                break;
            case R.id.rl_printer://打印机
                startActivity(new Intent(getActivity(), MyPrinterActivity.class));
                break;
            case R.id.rl_bank_card://银行卡
                startActivity(new Intent(getActivity(), MyBankCardActivity.class));
                break;
            case R.id.rl_alipay://支付宝
                if (zizhirenzhengState.equals("去认证")) {
                    myDialogBuilder = MyNewDialogBuilder.getInstance(mContext);
                    myDialogBuilder
                            .withTitie("提示")
                            .withContene("您还没有进行资质认证，认证后方可绑定支付宝")
                            .withEffects(MyDialogBuilder.SlideTop, MyDialogBuilder.SlideTopDismiss)
                            .setTwoBtn("知道了", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    myDialogBuilder.dismiss();

                                }
                            }, "去认证", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    myDialogBuilder.dismissNoAnimator();
                                    Intent intent = new Intent(getActivity(), CertificationActivity.class);
                                    startActivity(intent);
                                }
                            })
                            .show();
                } else if (zizhirenzhengState.equals("认证中")) {
                    myDialogBuilder = MyNewDialogBuilder.getInstance(mContext);
                    myDialogBuilder
                            .withTitie("提示")
                            .withContene("你的资质认证正在审核中,请耐心等待")
                            .withEffects(MyDialogBuilder.SlideTop, MyDialogBuilder.SlideTopDismiss)
                            .setOneBtn("知道了", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    myDialogBuilder.dismiss();
                                }
                            })
                            .show();
                } else if (zizhirenzhengState.equals("已认证")) {
                    startActivity(new Intent(getActivity(), MyAliPayActivity.class));
                }
                break;
            case R.id.rl_user_management://用户管理（账号角色分为负责人和普通用户）
                Intent intent = new Intent(getActivity(), WebActivity.class);//使用环游购封装webview
                intent.putExtra("url", SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_USERMANAGEMENTURL, "")
                        + "?account=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_USERACCOUNT, "")
                        + "&shop_type=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTTYPE, "")
                        + "&random_code=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_RANDOMCODE, ""));
                startActivity(intent);
                break;
            case R.id.rl_account_management://账户管理
                startActivity(new Intent(getActivity(), AccountSecurityActivity.class));
                break;
            case R.id.rl_bingding_hygo://绑定环游购
                startActivity(new Intent(getActivity(), BindingHygoAccountActivity.class));
                break;
            case R.id.rl_setting://设置
                startActivity(new Intent(getActivity(), SettingActivity.class));
                break;
            case R.id.rl_suggestion://意见反馈
                startActivity(new Intent(getActivity(), SuggestionFeedbackActivity.class));
                break;
        }
    }

}
