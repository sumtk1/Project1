package com.gloiot.hygounionmerchant.ui.activity.mine.bonus;

import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gloiot.hygounionmerchant.R;
import com.gloiot.hygounionmerchant.server.network.RequestAction;
import com.gloiot.hygounionmerchant.ui.BaseActivity;
import com.gloiot.hygounionmerchant.ui.activity.mine.alipay.AddAlipayAccountActivity;
import com.gloiot.hygounionmerchant.ui.activity.mine.bankcard.AddBankCardActivity;
import com.gloiot.hygounionmerchant.ui.activity.mine.zizhirenzheng.CertificationActivity;
import com.gloiot.hygounionmerchant.utils.CommonUtils;
import com.gloiot.hygounionmerchant.utils.ConstantUtils;
import com.zyd.wlwsdk.utils.L;
import com.zyd.wlwsdk.utils.MToast;
import com.zyd.wlwsdk.utils.SharedPreferencesUtils;
import com.zyd.wlwsdk.widge.MyDialogBuilder;
import com.zyd.wlwsdk.widge.MyNewDialogBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 我的收益(新页面)
 * Created by Dlt on 2017/10/31 15:29
 */
public class MyEarningsActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.tv_weijiesuan)
    TextView mTvWeijiesuan;
    @Bind(R.id.tv_yijiesuan)
    TextView mTvYijiesuan;
    @Bind(R.id.rl_layout_weitiqu)
    RelativeLayout mRlLayoutWeitiqu;
    @Bind(R.id.rl_layout_ketiqu)
    RelativeLayout mRlLayoutKetiqu;
    @Bind(R.id.tv_weitiqu)
    TextView mTvWeitiqu;
    @Bind(R.id.tv_ketiqu)
    TextView mTvKetiqu;
    @Bind(R.id.tv_explain)
    TextView mTvExplain;
    @Bind(R.id.tv_extract)
    TextView mTvExtract;
    @Bind(R.id.tv_extract_alipay)
    TextView mTvExtractAlipay;
    @Bind(R.id.tv_extract_detail)
    TextView mTvExtractDetail;

    private String accountType;
    private String weijiesuan = "", yijiesuan = "", weitiqu = "", ketiqu = "";
    private MyNewDialogBuilder myDialogBuilder;
    private String bankcardBindingState = "";//银行卡绑定状态：已绑定/未绑定
    private String alipayBindingState = "";//支付宝绑定状态：已绑定/未绑定
    private String isCanExtract = "";//是否开放提取
    private String isQuotaExtract = "";//是否定额提取
    private String quotaMoney = "";//定额金额
    private String isCanExtractToBankcard = "";
    private String isCanExtractToAlipay = "";

    @Override
    protected void onResume() {
        super.onResume();
        requestHandleArrayList.add(requestAction.getMyAccountIncome(this, accountType));
    }

    @Override
    public int initResource() {
        return R.layout.activity_my_earnings;
    }

    @Override
    public void initData() {
        accountType = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTTYPE, "");
        CommonUtils.setTitleBar(this, true, "我的收益", "");
        if (accountType.equals("小铺")) {
            mRlLayoutWeitiqu.setVisibility(View.VISIBLE);
        } else {
            mRlLayoutWeitiqu.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.tv_extract, R.id.tv_extract_alipay, R.id.tv_extract_detail})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_extract://提取到银行卡
                if (isCanExtract.equals("是")) {
                    if (Float.parseFloat(ketiqu) < 100) {
                        MToast.showToast(mContext, "可提取收益不足");
                    } else {
                        requestHandleArrayList.add(requestAction.getMyBankcardList(MyEarningsActivity.this, accountType));
                    }
                } else {
                    MToast.showToast(mContext, "功能正在优化中");
                }
                break;
            case R.id.tv_extract_alipay://提取到支付宝
                //资质认证状态：去认证/认证中/已认证
                String zizhirenzhengState = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ZIZHIRENZHENGSTATE, "");
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
                                    Intent intent = new Intent(MyEarningsActivity.this, CertificationActivity.class);
                                    startActivity(intent);
                                }
                            })
                            .show();
                } else if (zizhirenzhengState.equals("认证中")) {
                    myDialogBuilder = MyNewDialogBuilder.getInstance(mContext);
                    myDialogBuilder
                            .withTitie("提示")
//                            .withContene("您的资质认证信息正在审核中，认证通过后方可绑定支付宝")   //需求怎么写怎么来
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

                    if (isCanExtract.equals("是")) {
                        if (Float.parseFloat(ketiqu) < 100) {
                            MToast.showToast(mContext, "可提取收益不足");
                        } else {

                            if (alipayBindingState.equals("已绑定")) {
                                Intent intent = new Intent(MyEarningsActivity.this, ExtractToBankcardActivity.class);
                                intent.putExtra("type", "alipay");
                                intent.putExtra("ketiqu", ketiqu);
                                intent.putExtra("isQuotaExtract", isQuotaExtract);
                                intent.putExtra("quotaMoney", quotaMoney);
                                startActivity(intent);
                            } else {
                                myDialogBuilder = MyNewDialogBuilder.getInstance(mContext);
                                myDialogBuilder
                                        .withTitie("提示")
                                        .withCenterContent("您还没有绑定的支付宝")
                                        .withEffects(MyDialogBuilder.SlideTop, MyDialogBuilder.SlideTopDismiss)
                                        .setTwoBtn("取消", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                myDialogBuilder.dismiss();

                                            }
                                        }, "去绑定", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                myDialogBuilder.dismissNoAnimator();
                                                Intent intent = new Intent(MyEarningsActivity.this, AddAlipayAccountActivity.class);
                                                startActivity(intent);
                                            }
                                        })
                                        .show();
                            }

                        }
                    } else {
                        MToast.showToast(mContext, "功能正在优化中");
                    }

                }
                break;
            case R.id.tv_extract_detail:
                Intent intent = new Intent(MyEarningsActivity.this, ExtractRecordActivity.class);
                intent.putExtra("extractType", "");
                startActivity(intent);
                break;
        }
    }

    @Override
    public void requestSuccess(int requestTag, JSONObject response, int showLoad) throws JSONException {
        super.requestSuccess(requestTag, response, showLoad);
        switch (requestTag) {
            case RequestAction.TAG_MYACCOUNTINCOME:
                L.e("我的收益", response.toString());
                isCanExtract = response.getString("开放提取");//选项有“是”/“否”
                isQuotaExtract = response.getString("定额提取");//选项有“是”/“否”
                quotaMoney = response.getString("定额金额");
                bankcardBindingState = response.getString("银行卡");//已绑定/未绑定
                alipayBindingState = response.getString("支付宝");//已绑定/未绑定

                weijiesuan = response.getString("未结算");
                yijiesuan = response.getString("已结算");
                weitiqu = response.getString("未提取");
                ketiqu = response.getString("可提取");
                mTvWeijiesuan.setText(weijiesuan);
                mTvYijiesuan.setText(yijiesuan);
                mTvWeitiqu.setText(weitiqu);
                mTvKetiqu.setText(ketiqu);

                isCanExtractToBankcard = response.getString("银行卡通道");//取值：是/否
                isCanExtractToAlipay = response.getString("支付宝通道");//取值：是/否
                if (isCanExtractToBankcard.equals("是")) {
                    mTvExtract.setVisibility(View.VISIBLE);
                } else {
                    mTvExtract.setVisibility(View.GONE);
                }

                if (isCanExtractToAlipay.equals("是")) {
                    mTvExtractAlipay.setVisibility(View.VISIBLE);
                } else {
                    mTvExtractAlipay.setVisibility(View.GONE);
                }
                break;
            case RequestAction.TAG_MYBANKCARDLIST:
                L.e("我的已绑定银行卡", response.toString());
                int num = Integer.parseInt(response.getString("条数"));
                if (num != 0) {
                    Intent intent = new Intent(MyEarningsActivity.this, ExtractToBankcardActivity.class);
                    intent.putExtra("type", "bankcard");
                    intent.putExtra("ketiqu", ketiqu);
                    intent.putExtra("isQuotaExtract", isQuotaExtract);
                    intent.putExtra("quotaMoney", quotaMoney);
                    startActivity(intent);
                } else {
                    myDialogBuilder = MyNewDialogBuilder.getInstance(mContext);
                    myDialogBuilder
                            .withTitie("提示")
                            .withCenterContent("您还没有绑定的银行卡")
                            .withEffects(MyDialogBuilder.SlideTop, MyDialogBuilder.SlideTopDismiss)
                            .setTwoBtn("取消", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    myDialogBuilder.dismiss();

                                }
                            }, "去绑定", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    myDialogBuilder.dismissNoAnimator();
                                    Intent intent = new Intent(MyEarningsActivity.this, AddBankCardActivity.class);
                                    startActivity(intent);
                                }
                            })
                            .show();
                }
                break;
            default:
                break;
        }
    }

}
