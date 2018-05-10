package com.gloiot.hygounionmerchant.ui.activity.home;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gloiot.hygounionmerchant.R;
import com.gloiot.hygounionmerchant.ui.BaseActivity;
import com.gloiot.hygounionmerchant.ui.webview.WebActivity;
import com.gloiot.hygounionmerchant.utils.CommonUtils;
import com.gloiot.hygounionmerchant.utils.ConstantUtils;
import com.zyd.wlwsdk.adapter.ViewHolder;
import com.zyd.wlwsdk.adapter.abslistview.CommonAdapter;
import com.zyd.wlwsdk.utils.L;
import com.zyd.wlwsdk.utils.MToast;
import com.zyd.wlwsdk.utils.SharedPreferencesUtils;
import com.zyd.wlwsdk.utils.StatusBarUtil;
import com.zyd.wlwsdk.widge.pulltorefresh.PullToRefreshLayout;
import com.zyd.wlwsdk.widge.pulltorefresh.PullableListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

import static com.gloiot.hygounionmerchant.ui.activity.home.SearchAuthenticationRecordActivity.RESULT_SEARCH_MESSAGE;

/**
 * 认证记录
 * Created by Dlt on 2017/11/15 16:40
 */
public class AuthenticationRecordActivity extends BaseActivity implements PullToRefreshLayout.OnRefreshListener, BaseActivity.RequestErrorCallback, View.OnClickListener {

    @Bind(R.id.tv_search_bar)
    TextView mTvSearchBar;
    @Bind(R.id.tv_search_hint)
    TextView mTvSearchHint;
    @Bind(R.id.tv_search_content)
    TextView mTvSearchContent;
    @Bind(R.id.pullablelistview)
    PullableListView mPullablelistview;
    @Bind(R.id.pulltorefreshlayout)
    PullToRefreshLayout mPulltorefreshlayout;
    @Bind(R.id.ll_no)
    LinearLayout mLlNo;

    private String accountType = "";
    private List<String[]> list = new ArrayList<>();
    private CommonAdapter mCommonAdapter;
    private int page = 0;
    private String searchContent = "";//搜索框内容
    public static final int REQUEST_SEARCH_MESSAGE = 7;

    @OnClick(R.id.tv_search_bar)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_search_bar:
                Intent intent = new Intent(AuthenticationRecordActivity.this, SearchAuthenticationRecordActivity.class);
//                Intent intent = new Intent(AuthenticationRecordActivity.this, NewSearchAuthenticationRecordActivity.class);
                startActivityForResult(intent, REQUEST_SEARCH_MESSAGE);
                break;
        }
    }

    @Override
    public int initResource() {
        return R.layout.activity_authentication_record;
    }

    @Override
    public void initData() {
        accountType = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTTYPE, "");
        StatusBarUtil.setColor(this, getResources().getColor(R.color.green_21d1c1), 0);
        CommonUtils.setTitleBar(this, true, "认证记录", "");
        mPulltorefreshlayout.setOnRefreshListener(this);
        request(0, 0, 1, 0);
        setRequestErrorCallback(this);
    }

    /**
     * @param requestType 请求类型，初始请求=0，刷新=1，加载=2
     * @param page        页数
     * @param requestTag  请求tag,请求成功后用于区分是哪个请求
     * @param showLoad    是否显示请求加载框 0--显示， -1--不显示
     */
    private void request(int requestType, int page, int requestTag, int showLoad) {
        requestHandleArrayList.add(requestAction.getAuthenticationRecords(this, mPulltorefreshlayout,
                requestType, page, requestTag, showLoad, searchContent, accountType));
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        if (!list.isEmpty()) {
            list.clear();
            mCommonAdapter.notifyDataSetChanged();
        }
        page = 0;
        request(1, 0, 2, -1);
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
        if (page > 0) {
            request(2, page + 1, 3, -1);
        } else {
            MToast.showToast(mContext, "已无数据加载");
            pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.FAIL);
        }
    }

    private void setAdapter() {

        mCommonAdapter = new CommonAdapter<String[]>(AuthenticationRecordActivity.this, R.layout.item_authentication_record, list) {
            @Override
            public void convert(final ViewHolder holder, String[] strings) {

                View view = holder.getConvertView();
                ImageView ivPic = (ImageView) view.findViewById(R.id.iv_pic);
                CommonUtils.setDisplayImage(ivPic, strings[2], 0, R.mipmap.ic_launcher);

                if (accountType.equals("小铺")) {
                    holder.setText(R.id.tv_title, strings[1]);
                    holder.setVisible(R.id.ll_unit_price, true);
                    holder.setVisible(R.id.ll_totle_price_with_text, true);
                    holder.setVisible(R.id.ll_totle_price_without_text, false);
                    holder.setText(R.id.tv_unit_price, strings[4]);
                    holder.setText(R.id.tv_amount, strings[3]);
                    holder.setText(R.id.tv_total_price_with_text, strings[8]);
                    holder.setText(R.id.tv_use_num, strings[5]);
                    if (TextUtils.isEmpty(strings[6]) || strings[6].equals("null") || strings[6].contains("undefine")) {
                        holder.setVisible(R.id.tv_text_yzm, false);
                    } else {
                        holder.setVisible(R.id.tv_text_yzm, true);
                        holder.setText(R.id.tv_yzm, strings[6]);
                    }
                    holder.setText(R.id.tv_time, "认证时间：" + strings[7]);
                } else if (accountType.equals("旅行社")) {
                    holder.setText(R.id.tv_title, strings[1]);
                    holder.setVisible(R.id.ll_unit_price, false);
                    holder.setVisible(R.id.ll_totle_price_with_text, false);
                    holder.setVisible(R.id.ll_totle_price_without_text, true);
                    holder.setText(R.id.tv_total_price_without_text, strings[10]);
                    holder.setText(R.id.tv_use_num, strings[5]);
                    if (TextUtils.isEmpty(strings[6]) || strings[6].equals("null") || strings[6].contains("undefine")) {
                        holder.setVisible(R.id.tv_text_yzm, false);
                    } else {
                        holder.setVisible(R.id.tv_text_yzm, true);
                        holder.setText(R.id.tv_yzm, strings[6]);
                    }
                    holder.setText(R.id.tv_time, "认证时间：" + strings[7]);
                } else if (accountType.equals("酒店")) {
                    holder.setText(R.id.tv_title, strings[1]);
                    holder.setVisible(R.id.ll_unit_price, true);
                    holder.setVisible(R.id.ll_totle_price_with_text, true);
                    holder.setVisible(R.id.ll_totle_price_without_text, false);
                    holder.setText(R.id.tv_unit_price, strings[3]);
                    holder.setText(R.id.tv_amount, strings[7]);
                    holder.setText(R.id.tv_total_price_with_text, strings[12]);
                    holder.setText(R.id.tv_use_num, strings[9]);
                    if (TextUtils.isEmpty(strings[10]) || strings[10].equals("null") || strings[10].contains("undefine")) {
                        holder.setVisible(R.id.tv_text_yzm, false);
                    } else {
                        holder.setVisible(R.id.tv_text_yzm, true);
                        holder.setText(R.id.tv_yzm, strings[10]);
                    }
                    holder.setText(R.id.tv_time, "认证时间：" + strings[11]);
                }
            }
        };
        mPullablelistview.setAdapter(mCommonAdapter);

        //设置分隔线
        mPullablelistview.setDivider(new ColorDrawable(Color.rgb(243, 243, 243)));//#F3F3F3
        mPullablelistview.setDividerHeight(2);

        mPullablelistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mContext, WebActivity.class);
                intent.putExtra("url", SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_RENZHENGDETAILURL, "")
                        + "?account=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_USERACCOUNT, "")
                        + "&random_code=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_RANDOMCODE, "")
                        + "&shop_type=" + SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTTYPE, "")
                        + "&orderId=" + list.get(position)[0]);
                startActivity(intent);
            }
        });

    }

    @Override
    public void requestSuccess(int requestTag, JSONObject response, int showLoad) throws JSONException {
        super.requestSuccess(requestTag, response, showLoad);
        switch (requestTag) {
            case 1:
                processResponseData(response, false);
                break;
            case 2:
                processResponseData(response, false);
                break;
            case 3:
                processResponseData(response, true);
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
        L.e("认证记录", response.toString());
        int num = Integer.parseInt(response.getString("条数"));

        if (num != 0) {
            JSONArray jsonArray = response.getJSONArray("数据");
            for (int i = 0; i < jsonArray.length(); i++) {
                if (accountType.equals("小铺")) {
                    String[] a = new String[9];
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    a[0] = jsonObject.getString("券id");
                    a[1] = jsonObject.getString("标题");
                    a[2] = jsonObject.getString("图片");
                    a[3] = jsonObject.getString("数量");
                    a[4] = jsonObject.getString("单价");
                    a[5] = jsonObject.getString("使用码");
                    a[6] = jsonObject.getString("验证码");
                    a[7] = jsonObject.getString("认证时间");
                    a[8] = jsonObject.getString("价格");
                    list.add(a);
                } else if (accountType.equals("旅行社")) {
                    String[] a = new String[11];
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    a[0] = jsonObject.getString("券id");
                    a[1] = jsonObject.getString("标题");
                    a[2] = jsonObject.getString("图片");
                    a[3] = jsonObject.getString("购买时间");
                    a[4] = jsonObject.getString("出发名");
                    a[5] = jsonObject.getString("使用码");
                    a[6] = jsonObject.getString("验证码");
                    a[7] = jsonObject.getString("认证时间");
                    a[8] = jsonObject.getString("数量");
                    a[9] = jsonObject.getString("单价");
                    a[10] = jsonObject.getString("价格");
                    list.add(a);
                } else if (accountType.equals("酒店")) {
                    String[] a = new String[13];
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    a[0] = jsonObject.getString("券id");
                    a[1] = jsonObject.getString("标题");
                    a[2] = jsonObject.getString("图片");
                    a[3] = jsonObject.getString("单价");
                    a[4] = jsonObject.getString("购买时间");
                    a[5] = jsonObject.getString("入住时间");
                    a[6] = jsonObject.getString("退房时间");
                    a[7] = jsonObject.getString("数量");
                    a[8] = jsonObject.getString("房型");
                    a[9] = jsonObject.getString("使用码");
                    a[10] = jsonObject.getString("验证码");
                    a[11] = jsonObject.getString("认证时间");
                    a[12] = jsonObject.getString("价格");
                    list.add(a);
                }
            }
            page = num == 10 ? Integer.parseInt(response.getString("页数")) : 0;
            mPullablelistview.setVisibility(View.VISIBLE);
            mLlNo.setVisibility(View.GONE);
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
                mPullablelistview.setVisibility(View.GONE);
                mLlNo.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void requestErrorcallback(int requestTag, JSONObject response) throws Exception {
        switch (requestTag) {
            case 1:
            case 2:
            case 3:
                L.e("认证记录", response.toString());
                if (response.getString("状态").contains("无认证记录")) {

                    mLlNo.setVisibility(View.VISIBLE);

                } else {
//                    MToast.showToast(mContext, response.getString("状态"));
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        searchContent = "";
        if (data != null) {
            searchContent = data.getStringExtra("searchContent");
        }
        switch (resultCode) {
            case RESULT_SEARCH_MESSAGE:
                switch (requestCode) {
                    case REQUEST_SEARCH_MESSAGE:
                        if (!searchContent.isEmpty()) {
                            mTvSearchContent.setText(searchContent);
                            mTvSearchHint.setVisibility(View.GONE);

                            if (!list.isEmpty()) {
                                list.clear();
                                mCommonAdapter.notifyDataSetChanged();
                            }
                            page = 0;
                            request(0, 0, 1, 0);
                        } else {
                            mTvSearchContent.setText("");
                            mTvSearchHint.setVisibility(View.VISIBLE);

                            if (!list.isEmpty()) {
                                list.clear();
                                mCommonAdapter.notifyDataSetChanged();
                            }
                            page = 0;
                            request(0, 0, 1, 0);
                        }
                        break;

                }
                break;
        }
    }

}
