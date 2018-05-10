package com.gloiot.hygounionmerchant.ui.activity.home.shop;

import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.gloiot.hygounionmerchant.R;
import com.gloiot.hygounionmerchant.server.network.RequestAction;
import com.gloiot.hygounionmerchant.ui.BaseActivity;
import com.gloiot.hygounionmerchant.ui.adapter.warehousing.ContentListAdapter;
import com.gloiot.hygounionmerchant.ui.adapter.warehousing.TitleListAdapter;
import com.gloiot.hygounionmerchant.utils.CommonUtils;
import com.gloiot.hygounionmerchant.utils.ConstantUtils;
import com.zyd.wlwsdk.utils.L;
import com.zyd.wlwsdk.utils.SharedPreferencesUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;

/**
 * 小铺入库管理
 * Created by Dlt on 2017/8/19 10:35
 */
public class ShopWarehousingManagementActivity extends BaseActivity {

    @Bind(R.id.ll_hava)
    LinearLayout mLlHava;
    @Bind(R.id.list_view_title)
    ListView mListViewTitle;
    @Bind(R.id.list_view_content)
    ListView mListViewContent;

    @Bind(R.id.ll_no)
    LinearLayout mLlNo;

    private String accountType;
    private List<String> titleList = new ArrayList<String>();
    private Map<String, List<String[]>> contentMap = new HashMap<String, List<String[]>>();
    private TitleListAdapter titleAdapter;
    private ContentListAdapter contentAdapter;
    public static int POSITION = 0;

    private boolean flag = false;
    private int CURRENTID = 0;


    @Override
    public void onResume() {
        super.onResume();
        titleList.clear();
        contentMap.clear();

        requestHandleArrayList.add(requestAction.getShopCommodityWarehousingList(this, accountType));

    }

    @Override
    public int initResource() {
        return R.layout.activity_shop_warehousing_management;
    }

    @Override
    public void initData() {
        accountType = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTTYPE, "");
        CommonUtils.setTitleBar(this, true, "入库管理", "");
    }

    /**
     * 数据处理
     */
    private void processData() {
        titleAdapter = new TitleListAdapter(mContext, titleList);
        mListViewTitle.setAdapter(titleAdapter);
        contentAdapter = new ContentListAdapter(mContext, titleList, contentMap);
        mListViewContent.setAdapter(contentAdapter);

        contentAdapter.notifyDataSetChanged();
        mListViewContent.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem != CURRENTID) {
                    CURRENTID = firstVisibleItem;
                    if (CURRENTID != POSITION)
                        POSITION = CURRENTID;
                    titleAdapter.notifyDataSetChanged();
                }
            }
        });

        mListViewTitle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                POSITION = position;
                titleAdapter.notifyDataSetChanged();
                contentAdapter.notifyDataSetChanged();
                mListViewContent.setSelection(position);
                mListViewContent.smoothScrollToPositionFromTop(position, 0, 100);
            }
        });

    }

    @Override
    public void requestSuccess(int requestTag, JSONObject response, int showLoad) throws JSONException {
        super.requestSuccess(requestTag, response, showLoad);
        switch (requestTag) {

            case RequestAction.TAG_SHOPCOMMODITYWAREHOUSINGLIST:
                L.e("商品入库列表", response.toString());
                int num = Integer.parseInt(response.getString("条数"));
                if (num != 0) {

                    mLlNo.setVisibility(View.GONE);
                    mLlHava.setVisibility(View.VISIBLE);

                    JSONArray jsonArray = response.getJSONArray("列表");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                        String bigCategory = jsonObject.getString("种类");
                        titleList.add(bigCategory);

                        List<String[]> contentList = new ArrayList<>();

                        JSONArray contentArray = jsonObject.getJSONArray("列表");
                        for (int j = 0; j < contentArray.length(); j++) {
                            String[] s = new String[11];
                            JSONObject contentObject = (JSONObject) contentArray.get(j);
                            s[0] = contentObject.getString("id");
                            s[1] = contentObject.getString("商品名称");
                            s[2] = contentObject.getString("商品图片");
                            s[3] = contentObject.getString("供货价");
                            s[4] = contentObject.getString("市场价");
                            s[5] = contentObject.getString("进货价");
                            s[6] = contentObject.getString("录入时间");
                            s[7] = contentObject.getString("让利价");
                            s[8] = contentObject.getString("结算比");
                            s[9] = contentObject.getString("商品种类");//种类id
                            s[10] = contentObject.getString("数量");

                            L.e("test--", "i=" + i + ",j=" + j + ",bigType" + bigCategory + ",商品名称" + s[1]);

                            contentList.add(s);
                        }

                        contentMap.put(bigCategory, contentList);

                    }

                    processData();


                } else {
                    //应该有一个空布局
                    mLlHava.setVisibility(View.GONE);
                    mLlNo.setVisibility(View.VISIBLE);
                }

                break;

        }
    }


}
