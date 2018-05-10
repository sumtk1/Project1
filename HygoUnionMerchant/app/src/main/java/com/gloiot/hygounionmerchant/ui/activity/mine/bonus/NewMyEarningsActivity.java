package com.gloiot.hygounionmerchant.ui.activity.mine.bonus;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gloiot.hygounionmerchant.R;
import com.gloiot.hygounionmerchant.server.network.RequestAction;
import com.gloiot.hygounionmerchant.ui.BaseActivity;
import com.gloiot.hygounionmerchant.ui.activity.mine.alipay.AddAlipayAccountActivity;
import com.gloiot.hygounionmerchant.ui.activity.mine.bankcard.AddBankCardActivity;
import com.gloiot.hygounionmerchant.ui.activity.mine.deposit.DepositActivity;
import com.gloiot.hygounionmerchant.ui.activity.mine.deposit.PayDepositDetailActivity;
import com.gloiot.hygounionmerchant.ui.activity.mine.zizhirenzheng.CertificationActivity;
import com.gloiot.hygounionmerchant.utils.CommonUtils;
import com.gloiot.hygounionmerchant.utils.ConstantUtils;
import com.zyd.wlwsdk.adapter.ViewHolder;
import com.zyd.wlwsdk.adapter.abslistview.CommonAdapter;
import com.zyd.wlwsdk.utils.L;
import com.zyd.wlwsdk.utils.MToast;
import com.zyd.wlwsdk.utils.SharedPreferencesUtils;
import com.zyd.wlwsdk.utils.StatusBarUtil;
import com.zyd.wlwsdk.widge.MyDialogBuilder;
import com.zyd.wlwsdk.widge.MyNewDialogBuilder;
import com.zyd.wlwsdk.widge.MyScrollView;
import com.zyd.wlwsdk.widge.pulltorefresh.PullToRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 我的收益(新页面)
 * Created by Dlt on 2018/1/2 11:42
 */
public class NewMyEarningsActivity extends BaseActivity implements MyScrollView.OnScrollListener, PullToRefreshLayout.OnRefreshListener {

    @Bind(R.id.view_status_bar)
    View mViewStatusBar;
    @Bind(R.id.tv_ketiqu)
    TextView mTvKetiqu;
    @Bind(R.id.tv_zongshouyi)
    TextView mTvZongshouyi;
    @Bind(R.id.tv_weijiesuan)
    TextView mTvWeijiesuan;
    @Bind(R.id.tv_yijiesuan)
    TextView mTvYijiesuan;
    @Bind(R.id.rl_extract_to_bankcard)
    RelativeLayout mRlExtractToBankcard;
    @Bind(R.id.rl_extract_to_alipay)
    RelativeLayout mRlExtractToAlipay;
    @Bind(R.id.rl_deposit)
    RelativeLayout mRlDeposit;
    @Bind(R.id.rl_earnings_records)
    RelativeLayout mRlEarningsRecords;
    @Bind(R.id.list_view)
    ListView mListView;
    @Bind(R.id.view_status_bar1)
    View mViewStatusBar1;
    @Bind(R.id.rl_earnings_records1)
    RelativeLayout mRlEarningsRecords1;
    @Bind(R.id.rl_top)
    LinearLayout mRlTop;
    @Bind(R.id.scroll_view)
    MyScrollView mScrollView;
    @Bind(R.id.pulltorefreshlayout)
    PullToRefreshLayout mPulltorefreshlayout;

    private String accountType;
    private List<String[]> list = new ArrayList<>();
    private CommonAdapter mCommonAdapter;
    private int page = 0;
    private String screenType = "";//筛选类型：取值--“”/“银行卡”/“红利”/“支付宝”
    private String weijiesuan = "", yijiesuan = "", weitiqu = "", ketiqu = "", zongshouyi = "";
    private MyNewDialogBuilder myDialogBuilder;
    private String bankcardBindingState = "";//银行卡绑定状态：已绑定/未绑定
    private String alipayBindingState = "";//支付宝绑定状态：已绑定/未绑定
    private String isCanExtract = "";//是否开放提取
    private String isCanExtractToBankcard = "";
    private String isCanExtractToAlipay = "";
    private boolean isNeedToRequestData = true;//是否需要请求数据（点击提现需要，点击记录列表不需要）

    @Override
    protected void onResume() {
        super.onResume();
        if (isNeedToRequestData) {
            requestHandleArrayList.add(requestAction.getMyAccountIncome(this, accountType));
            request(0, 0, 1, 0, screenType);
        }

    }

    @Override
    public int initResource() {
        return R.layout.activity_new_my_earnings;
    }

    @Override
    public void initData() {
        accountType = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTTYPE, "");
        StatusBarUtil.transparencyBar(this);
        if (Build.VERSION.SDK_INT >= 21) {
            mViewStatusBar.setVisibility(View.VISIBLE);
        }
        CommonUtils.setTitleBar(this, true, "我的收益", "");

        mListView.setFocusable(false);

        mPulltorefreshlayout.setOnRefreshListener(this);
        mScrollView.setOnScrollListener(this);
        mScrollView.smoothScrollTo(0, 20);

    }

    /**
     * @param requestType 请求类型，初始请求=0，刷新=1，加载=2
     * @param page        页数
     * @param requestTag  请求tag,请求成功后用于区分是哪个请求
     * @param showLoad    是否显示请求加载框 0--显示， -1--不显示
     * @param screenType  筛选类型：取值---""/银行卡/红利/支付宝
     */
    private void request(int requestType, int page, int requestTag, int showLoad, String screenType) {
//        requestHandleArrayList.add(requestAction.getExtractDetail(this, mPulltorefreshlayout, requestType, page,
//                requestTag, showLoad, accountType, screenType));

        requestHandleArrayList.add(requestAction.getMyEarningsList(this, mPulltorefreshlayout, requestType, page,
                requestTag, showLoad, accountType));
    }

    @OnClick({R.id.rl_extract_to_bankcard, R.id.rl_extract_to_alipay, R.id.rl_deposit})
    public void onViewClicked(View view) {
        String zizhirenzhengState;
        switch (view.getId()) {
            case R.id.rl_extract_to_bankcard://提取到银行卡
                //资质认证状态：去认证/认证中/已认证
                zizhirenzhengState = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ZIZHIRENZHENGSTATE, "");
                if (zizhirenzhengState.equals("去认证")) {
                    myDialogBuilder = MyNewDialogBuilder.getInstance(mContext);
                    myDialogBuilder
                            .withTitie("提示")
                            .withContene("您还没有进行资质认证，认证后方可进行提取")
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
                                    Intent intent = new Intent(NewMyEarningsActivity.this, CertificationActivity.class);
                                    startActivity(intent);
                                    isNeedToRequestData = false;
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
//                        if (Float.parseFloat(ketiqu) < 100) {
//                            MToast.showToast(mContext, "可提取收益不足");
//                        } else {
                        requestHandleArrayList.add(requestAction.getMyBankcardList(NewMyEarningsActivity.this, accountType));
//                        }
                    } else {
                        MToast.showToast(mContext, "功能正在优化中");
                    }

                }
                break;
            case R.id.rl_extract_to_alipay://提取到支付宝
                //资质认证状态：去认证/认证中/已认证
                zizhirenzhengState = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ZIZHIRENZHENGSTATE, "");
                if (zizhirenzhengState.equals("去认证")) {
                    myDialogBuilder = MyNewDialogBuilder.getInstance(mContext);
                    myDialogBuilder
                            .withTitie("提示")
                            .withContene("您还没有进行资质认证，认证后方可进行提取")
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
                                    Intent intent = new Intent(NewMyEarningsActivity.this, CertificationActivity.class);
                                    startActivity(intent);
                                    isNeedToRequestData = false;
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
//                        if (Float.parseFloat(ketiqu) < 100) {
//                            MToast.showToast(mContext, "可提取收益不足");
//                        } else {

                        if (alipayBindingState.equals("已绑定")) {
                            Intent intent = new Intent(NewMyEarningsActivity.this, ExtractToBankcardActivity.class);
                            intent.putExtra("type", "alipay");
                            intent.putExtra("ketiqu", ketiqu);
                            startActivity(intent);
                            isNeedToRequestData = true;
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
                                            Intent intent = new Intent(NewMyEarningsActivity.this, AddAlipayAccountActivity.class);
                                            startActivity(intent);
                                            isNeedToRequestData = false;
                                        }
                                    })
                                    .show();
                        }

//                        }
                    } else {
                        MToast.showToast(mContext, "功能正在优化中");
                    }

                }
                break;
            case R.id.rl_deposit://缴纳保证金
                startActivity(new Intent(NewMyEarningsActivity.this, DepositActivity.class));
                break;
        }
    }

    @Override
    public void onScroll(int scrollY) {
        if (scrollY >= mRlEarningsRecords.getTop() - mViewStatusBar1.getBottom()) {
            L.e("onScroll", "onScroll   " + scrollY);
            mRlEarningsRecords1.setVisibility(View.VISIBLE);

        } else {
            mRlEarningsRecords1.setVisibility(View.GONE);

        }

        if (scrollY > mRlTop.getTop()) {
            if (Build.VERSION.SDK_INT >= 21) {
                mViewStatusBar1.setVisibility(View.VISIBLE);
            }
        } else {
            if (Build.VERSION.SDK_INT >= 21) {
                mViewStatusBar1.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        request(1, 0, 2, -1, screenType);
//        requestHandleArrayList.add(requestAction.getMyAccountIncome(this, accountType));//下拉刷新全部数据
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
        if (page > 0) {
            request(2, page + 1, 3, -1, screenType);
        } else {
            MToast.showToast(mContext, "已无数据加载");
            pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.FAIL);
        }
    }

    @Override
    public void requestSuccess(int requestTag, JSONObject response, int showLoad) throws JSONException {
        super.requestSuccess(requestTag, response, showLoad);
        switch (requestTag) {
            case RequestAction.TAG_MYACCOUNTINCOME:
                L.e("我的收益", response.toString());
                isCanExtract = response.getString("开放提取");//选项有“是”/“否”
//                isQuotaExtract = response.getString("定额提取");//选项有“是”/“否”
//                quotaMoney = response.getString("定额金额");
                bankcardBindingState = response.getString("银行卡");//已绑定/未绑定
                alipayBindingState = response.getString("支付宝");//已绑定/未绑定

                isCanExtractToBankcard = response.getString("银行卡通道");//取值：是/否
                isCanExtractToAlipay = response.getString("支付宝通道");//取值：是/否
                if (isCanExtractToBankcard.equals("是")) {
                    mRlExtractToBankcard.setVisibility(View.VISIBLE);
                } else {
                    mRlExtractToBankcard.setVisibility(View.GONE);
                }

                if (isCanExtractToAlipay.equals("是")) {
                    mRlExtractToAlipay.setVisibility(View.VISIBLE);
                } else {
                    mRlExtractToAlipay.setVisibility(View.GONE);
                }

                weijiesuan = response.getString("未结算");
                mTvWeijiesuan.setText(weijiesuan);
                yijiesuan = response.getString("已结算");
                mTvYijiesuan.setText(yijiesuan);
                weitiqu = response.getString("未提取");
                ketiqu = response.getString("可提取");
                mTvKetiqu.setText(ketiqu);
                zongshouyi = response.getString("总收益");
                mTvZongshouyi.setText(zongshouyi);
                break;
            case RequestAction.TAG_MYBANKCARDLIST:
                L.e("我的已绑定银行卡", response.toString());
                int num = Integer.parseInt(response.getString("条数"));
                if (num != 0) {
                    Intent intent = new Intent(NewMyEarningsActivity.this, ExtractToBankcardActivity.class);
                    intent.putExtra("type", "bankcard");
                    intent.putExtra("ketiqu", ketiqu);
                    startActivity(intent);
                    isNeedToRequestData = true;
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
                                    Intent intent = new Intent(NewMyEarningsActivity.this, AddBankCardActivity.class);
                                    startActivity(intent);
                                    isNeedToRequestData = false;
                                }
                            })
                            .show();
                }
                break;
            case 1:
                processResponseData(response, false);
                break;
            case 2:
                processResponseData(response, false);
                break;
            case 3:
                processResponseData(response, true);
                break;
            default:
                break;
        }

    }

    /**
     * 处理请求返回数据
     *
     * @param response
     * @param isLoadMore
     */
    private void processResponseData(JSONObject response, boolean isLoadMore) throws JSONException {
        L.e("积分提取记录", response.toString());
        if (response.getInt("页数") == 1) {
            list.clear();
            //恢复界面
            mScrollView.smoothScrollTo(0, 20);
            mRlEarningsRecords1.setVisibility(View.GONE);
            if (Build.VERSION.SDK_INT >= 21) {
                mViewStatusBar1.setVisibility(View.GONE);
            }
        }

        int num = Integer.parseInt(response.getString("条数"));
        if (num != 0) {
            JSONArray jsonArray = response.getJSONArray("列表");
            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject = (JSONObject) jsonArray.get(i);

                String[] a = new String[6];
                a[0] = jsonObject.getString("id");
                a[1] = jsonObject.getString("积分");
                a[2] = jsonObject.getString("提现时间");
                a[3] = jsonObject.getString("提现状态");
                a[4] = jsonObject.getString("类别");  // 平台结算 保证金  银行卡  支付宝
                a[5] = jsonObject.getString("转入银行");

                list.add(a);
            }
            page = num == 10 ? Integer.parseInt(response.getString("页数")) : 0;
//            page = response.getInt("页数");//每页多少条数据不定，下拉就去请求
            mListView.setVisibility(View.VISIBLE);

            if (isLoadMore) {
                mPulltorefreshlayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                mCommonAdapter.notifyDataSetChanged();
            } else {
                setAdapter();
            }

        } else {

            if (isLoadMore) {
                MToast.showToast(mContext, "已无数据加载");
                mPulltorefreshlayout.loadmoreFinish(PullToRefreshLayout.FAIL);
            } else {
//                mListView.setVisibility(View.GONE);
            }
        }
        if (mCommonAdapter != null) {
            mCommonAdapter.notifyDataSetChanged();
        }
        CommonUtils.setListViewHeightBasedOnChildren(mListView);
    }

    private void setAdapter() {
        mCommonAdapter = new CommonAdapter<String[]>(NewMyEarningsActivity.this, R.layout.item_extract_record, list) {
            @Override
            public void convert(final ViewHolder holder, String[] strings) {

                holder.setText(R.id.tv_account_name, strings[5]);

                holder.setText(R.id.tv_time, strings[2]);
                holder.setText(R.id.tv_extract_money, strings[1]);

                //区分不同条目数据:银行卡/支付宝/保证金
                if (strings[4].equals("保证金")) {
                    if (strings[3].contains("成功")) {
                        holder.setTextColor(R.id.tv_extract_money, Color.parseColor("#21d1c1"));
                        holder.setText(R.id.tv_extract_state, "");
                        holder.setTextColor(R.id.tv_extract_state, Color.parseColor("#21d1c1"));
                        holder.setBackgroundRes(R.id.tv_extract_state, R.drawable.bg_btn_white);
                    } else {
                        holder.setTextColor(R.id.tv_extract_money, Color.parseColor("#ff8268"));
                        holder.setText(R.id.tv_extract_state, "");
                        holder.setTextColor(R.id.tv_extract_state, Color.parseColor("#ff8268"));
                        holder.setBackgroundRes(R.id.tv_extract_state, R.drawable.bg_btn_white);
                    }
                } else {
                    if (strings[3].equals("提现成功")) {

                        holder.setTextColor(R.id.tv_extract_money, Color.parseColor("#21d1c1"));
                        holder.setText(R.id.tv_extract_state, "");
                        holder.setTextColor(R.id.tv_extract_state, Color.parseColor("#21d1c1"));
                        holder.setBackgroundRes(R.id.tv_extract_state, R.drawable.bg_btn_white);

                    } else if (strings[3].equals("发起提现") || strings[3].equals("审核通过") || strings[3].equals("处理中")) {

                        holder.setTextColor(R.id.tv_extract_money, Color.parseColor("#999999"));
                        holder.setText(R.id.tv_extract_state, "处理中");
                        holder.setTextColor(R.id.tv_extract_state, Color.parseColor("#999999"));
                        holder.setBackgroundRes(R.id.tv_extract_state, R.drawable.bg_shape_biankuang_gray_999_10dp);

                    } else if (strings[3].equals("提现失败") || strings[3].equals("审核未通过")) {
                        holder.setTextColor(R.id.tv_extract_money, Color.parseColor("#ff8268"));
                        holder.setText(R.id.tv_extract_state, "提现失败");
                        holder.setTextColor(R.id.tv_extract_state, Color.parseColor("#ff8268"));
                        holder.setBackgroundRes(R.id.tv_extract_state, R.drawable.bg_shape_biankuang_red_ff7676_10dp);
                    }
                }
            }
        };
        mListView.setAdapter(mCommonAdapter);
        mListView.setDivider(new ColorDrawable(Color.parseColor("#e5e5e5")));
        mListView.setDividerHeight(2);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent;
                if (list.get(position)[4].equals("保证金")) {
                    intent = new Intent(NewMyEarningsActivity.this, PayDepositDetailActivity.class);
                    intent.putExtra("id", list.get(position)[0]);
                    startActivity(intent);
                } else if (list.get(position)[4].equals("平台结算")) {

                } else {
                    intent = new Intent(NewMyEarningsActivity.this, NewExtractRecordDetailActivity.class);
//                    intent.putExtra("state", list.get(position)[3]);
//                    intent.putExtra("money", list.get(position)[1]);
//                    intent.putExtra("where", list.get(position)[10]);
//                    intent.putExtra("type", list.get(position)[4]);
//                    intent.putExtra("shouxufei", list.get(position)[6]);
//                    intent.putExtra("time", list.get(position)[2]);
//                    intent.putExtra("ordernum", list.get(position)[7]);
//                    intent.putExtra("beizhu", list.get(position)[11]);

                    intent.putExtra("type", list.get(position)[4]);
                    intent.putExtra("id", list.get(position)[0]);
                    startActivity(intent);
                }
                isNeedToRequestData = false;
            }
        });

    }

}
