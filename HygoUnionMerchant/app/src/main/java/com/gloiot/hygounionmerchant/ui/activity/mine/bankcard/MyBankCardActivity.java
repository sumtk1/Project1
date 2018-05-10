package com.gloiot.hygounionmerchant.ui.activity.mine.bankcard;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.gloiot.hygounionmerchant.R;
import com.gloiot.hygounionmerchant.server.network.RequestAction;
import com.gloiot.hygounionmerchant.ui.BaseActivity;
import com.gloiot.hygounionmerchant.utils.CommonUtils;
import com.gloiot.hygounionmerchant.utils.ConstantUtils;
import com.gloiot.hygounionmerchant.utils.DialogUtils;
import com.zyd.wlwsdk.adapter.ViewHolder;
import com.zyd.wlwsdk.adapter.abslistview.CommonAdapter;
import com.zyd.wlwsdk.utils.L;
import com.zyd.wlwsdk.utils.MD5Utlis;
import com.zyd.wlwsdk.utils.MToast;
import com.zyd.wlwsdk.utils.SharedPreferencesUtils;
import com.zyd.wlwsdk.widge.swipe.MySwipe;
import com.zyd.wlwsdk.widge.swipe.SwipeLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * 我的银行卡
 * Created by Dlt on 2017/10/24 15:54
 */
public class MyBankCardActivity extends BaseActivity {

    @Bind(R.id.list_view)
    ListView mListView;

    private String accountType;
    private View footerView;
    private List<String[]> list = new ArrayList<String[]>();
    private CommonAdapter mBankCardAdapter;
    private int deletePosition = -1;//记录删除的位置，初始化为-1

    @Override
    public void onResume() {
        super.onResume();
        requestHandleArrayList.add(requestAction.getMyBankcardList(MyBankCardActivity.this, accountType));
    }

    @Override
    public int initResource() {
        return R.layout.activity_my_bank_card;
    }

    @Override
    public void initData() {
        accountType = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTTYPE, "");
        CommonUtils.setTitleBar(this, true, "我的银行卡", "");
        footerView = View.inflate(mContext, R.layout.layout_footer_add_bankcard, null);
        mListView.addFooterView(footerView);
        mListView.setDividerHeight(0);
        mListView.setAdapter(mBankCardAdapter);
        //添加银行卡
        footerView.findViewById(R.id.tv_add_bankcard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyBankCardActivity.this, AddBankCardActivity.class));
            }

        });

    }

    private void processData(Boolean isChanged) {

        if (isChanged) {
            mBankCardAdapter.notifyDataSetChanged();
        } else {
            mBankCardAdapter = new CommonAdapter<String[]>(MyBankCardActivity.this, R.layout.item_bank_card, list) {
                @Override
                public void convert(final ViewHolder holder, String[] strings) {

                    if (strings[3].contains("中国银行")) {
                        holder.setBackgroundRes(R.id.iv_bank_logo, R.drawable.bank_china);
                    } else if (strings[3].contains("建设银行")) {
                        holder.setBackgroundRes(R.id.iv_bank_logo, R.drawable.bank_zhongguojianshe);
                    } else if (strings[3].contains("交通银行")) {
                        holder.setBackgroundRes(R.id.iv_bank_logo, R.drawable.bank_zhongguojiaotong);
                    } else if (strings[3].contains("工商银行")) {
                        holder.setBackgroundRes(R.id.iv_bank_logo, R.drawable.bank_zhongguogongshang);
                    } else if (strings[3].contains("民生银行")) {
                        holder.setBackgroundRes(R.id.iv_bank_logo, R.drawable.bank_zhongguominsheng);
                    } else if (strings[3].contains("农业银行")) {
                        holder.setBackgroundRes(R.id.iv_bank_logo, R.drawable.bank_nongye);
                    } else if (strings[3].contains("中信银行")) {
                        holder.setBackgroundRes(R.id.iv_bank_logo, R.drawable.bank_zhongxin);
                    } else if (strings[3].contains("招商银行")) {
                        holder.setBackgroundRes(R.id.iv_bank_logo, R.drawable.bank_zhaoshang);
                    } else if (strings[3].contains("华夏银行")) {
                        holder.setBackgroundRes(R.id.iv_bank_logo, R.drawable.bank_huaxia);
                    } else if (strings[3].contains("光大银行")) {
                        holder.setBackgroundRes(R.id.iv_bank_logo, R.drawable.bank_guangda);
                    } else if (strings[3].contains("浦发银行")) {
                        holder.setBackgroundRes(R.id.iv_bank_logo, R.drawable.bank_pufa);
                    } else if (strings[3].contains("兴业银行")) {
                        holder.setBackgroundRes(R.id.iv_bank_logo, R.drawable.bank_xingye);
                    } else if (strings[3].contains("北京银行")) {
                        holder.setBackgroundRes(R.id.iv_bank_logo, R.drawable.bank_beijing);
                    } else if (strings[3].contains("广发银行")) {
                        holder.setBackgroundRes(R.id.iv_bank_logo, R.drawable.bank_guangfa);
                    } else if (strings[3].contains("平安银行")) {
                        holder.setBackgroundRes(R.id.iv_bank_logo, R.drawable.bank_pingan);
                    } else if (strings[3].contains("上海银行")) {
                        holder.setBackgroundRes(R.id.iv_bank_logo, R.drawable.bank_shanghai);
                    } else if (strings[3].contains("邮储银行")) {
                        holder.setBackgroundRes(R.id.iv_bank_logo, R.drawable.bank_zhongguoyouzheng);
                    } else {
                        holder.setBackgroundRes(R.id.iv_bank_logo, R.drawable.ic_bank_moren);
                    }

                    holder.setText(R.id.tv_bank_name, strings[3]);
                    holder.setText(R.id.tv_card_type, "储蓄卡");

                    try {
                        holder.setText(R.id.tv_card_num, "*** **** **** " + strings[2].substring(strings[2].length() - 4, strings[2].length()));
                    } catch (StringIndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }

                    switch (holder.getmPosition() % 3) {
                        case 0:
                            holder.setBackgroundRes(R.id.rl_card_background, R.drawable.bg_shape_green_38d1c4_4dp);//不要用setBackgroundColor,会出错
                            break;
                        case 1:
                            holder.setBackgroundRes(R.id.rl_card_background, R.drawable.bg_shape_blue_66a6fa_4dp);
                            break;
                        case 2:
                            holder.setBackgroundRes(R.id.rl_card_background, R.drawable.bg_shape_orange_f2a55c_4dp);
                            break;
                        default:
                            break;
                    }

                    final int p = holder.getmPosition();//选中的位置
                    Button btn_delete = holder.getView(R.id.btn_delete);
                    SwipeLayout s = (SwipeLayout) holder.getConvertView();
                    s.close(false, false);
                    s.getFrontView().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                    s.setSwipeListener(MySwipe.mSwipeListener);
                    btn_delete.setTag(p);
                    btn_delete.setOnClickListener(onActionClick);
                }
            };
            mListView.setAdapter(mBankCardAdapter);
        }
    }

    View.OnClickListener onActionClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final int p = (int) v.getTag();
            int id = v.getId();
            if (id == R.id.btn_delete) {   //解绑
                MySwipe.closeAllLayout();

                DialogUtils.oneBtnPwd1(mContext, "输入支付密码验证身份", new DialogUtils.PasswordCallback() {
                    @Override
                    public void callback(String data) {
                        deletePosition = p;
                        try {
                            requestHandleArrayList.add(requestAction.removeBankcard(MyBankCardActivity.this, accountType,
                                    list.get(p)[3], list.get(p)[0], list.get(p)[2], MD5Utlis.Md5(data)));
                        } catch (IndexOutOfBoundsException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });

            }
        }
    };

    @Override
    public void requestSuccess(int requestTag, JSONObject response, int showLoad) throws JSONException {
        super.requestSuccess(requestTag, response, showLoad);
        switch (requestTag) {
            case RequestAction.TAG_MYBANKCARDLIST:
                L.e("我的已绑定银行卡", response.toString());
                if (!list.isEmpty()) {
                    list.clear();
                    mBankCardAdapter.notifyDataSetChanged();
                }
                int num = Integer.parseInt(response.getString("条数"));
                if (num != 0) {
                    JSONArray jsonArray = response.getJSONArray("数据");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                        String[] s = new String[5];
                        s[0] = jsonObject.getString("id");
                        s[1] = jsonObject.getString("持卡人姓名");
                        s[2] = jsonObject.getString("银行卡号");
                        s[3] = jsonObject.getString("银行名");
                        s[4] = jsonObject.getString("支行名称");
                        list.add(s);
                    }
                    processData(false);
                } else {
                    L.e("已绑定的银行卡", "为空");
                }
                break;
            case RequestAction.TAG_REMOVEBANKCARD:
                MToast.showToast(mContext, response.getString("状态"));
                if (deletePosition != -1) {//为了保险，其实只要删除成功，这里肯定不等于-1
                    list.remove(deletePosition);
                    processData(true);
                }
                deletePosition = -1;
                break;
            default:
                break;
        }
    }

}
