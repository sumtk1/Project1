package com.gloiot.hygounionmerchant.ui.activity.mine.alipay;

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
 * 我的支付宝
 * Created by Dlt on 2017/12/21 16:22
 */
public class MyAliPayActivity extends BaseActivity {

    @Bind(R.id.list_view)
    ListView mListView;

    private String accountType = "";
    private View footerView;
    private List<String[]> list = new ArrayList<String[]>();
    private CommonAdapter mAdapter;
    private int deletePosition = -1;//记录删除的位置，初始化为-1

    @Override
    public void onResume() {
        super.onResume();
        requestHandleArrayList.add(requestAction.getAlipayAccountList(MyAliPayActivity.this, accountType));
    }

    @Override
    public int initResource() {
        return R.layout.activity_my_ali_pay;
    }

    @Override
    public void initData() {
        accountType = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTTYPE, "");
        CommonUtils.setTitleBar(this, true, "我的支付宝", "");
        footerView = View.inflate(mContext, R.layout.layout_footer_add_alipay, null);
        mListView.addFooterView(footerView);
        mListView.setDividerHeight(0);
        mListView.setAdapter(mAdapter);
        //添加支付宝账号
        footerView.findViewById(R.id.tv_add_alipay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyAliPayActivity.this, AddAlipayAccountActivity.class));
            }

        });
    }

    private void processData(Boolean isChanged) {

        if (isChanged) {
            mAdapter.notifyDataSetChanged();
        } else {
            mAdapter = new CommonAdapter<String[]>(MyAliPayActivity.this, R.layout.item_alipay, list) {
                @Override
                public void convert(final ViewHolder holder, String[] strings) {

                    holder.setText(R.id.tv_alipay_name, strings[2]);

                    try {
                        if (CommonUtils.isInteger(strings[1]) && strings[1].length() == 11) {//十一位整数，默认是手机号
                            holder.setText(R.id.tv_alipay_account, strings[1].substring(0, 3) + " **** " + strings[1].substring(strings[1].length() - 4, strings[1].length()));
                        } else if (strings[1].contains("@") && strings[1].contains(".") && strings[1].indexOf("@") < strings[1].indexOf(".")) {//邮箱---@前三位的字符全部隐藏
                            int index = strings[1].indexOf("@");
                            holder.setText(R.id.tv_alipay_account, "****" + strings[1].substring(index - 3, strings[1].length()));
                            L.e("邮箱", "position--" + holder.getmPosition() + ",@-index=" + index);
                        } else {//其他情况
                            holder.setText(R.id.tv_alipay_account, "****" + strings[1].substring(strings[1].length() - 3, strings[1].length()));
                            L.e("其他", "position--" + holder.getmPosition());
                        }
                    } catch (StringIndexOutOfBoundsException e) {
                        e.printStackTrace();
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
            mListView.setAdapter(mAdapter);
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
                            requestHandleArrayList.add(requestAction.removeAlipayAccount(MyAliPayActivity.this,
                                    list.get(p)[1], MD5Utlis.Md5(data), list.get(p)[0], accountType));

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
            case RequestAction.TAG_ALIPAYACCOUNTLIST:
                L.e("已绑定支付宝", response.toString());
                if (!list.isEmpty()) {
                    list.clear();
                    mAdapter.notifyDataSetChanged();
                }
                int num = Integer.parseInt(response.getString("条数"));
                if (num != 0) {
                    JSONArray jsonArray = response.getJSONArray("列表");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                        String[] s = new String[3];
                        s[0] = jsonObject.getString("id");
                        s[1] = jsonObject.getString("支付宝账号");
                        s[2] = jsonObject.getString("支付宝姓名");
                        list.add(s);
                    }
                    processData(false);
                } else {
                    L.e("已绑定的支付宝账号", "为空");
                }
                break;
            case RequestAction.TAG_DELETEALIPAYACCOUNT:
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
