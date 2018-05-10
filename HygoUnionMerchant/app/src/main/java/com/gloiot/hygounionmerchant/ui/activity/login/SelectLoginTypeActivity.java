package com.gloiot.hygounionmerchant.ui.activity.login;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.gloiot.hygounionmerchant.R;
import com.gloiot.hygounionmerchant.server.network.RequestAction;
import com.gloiot.hygounionmerchant.ui.BaseActivity;
import com.gloiot.hygounionmerchant.utils.CommonUtils;
import com.gloiot.hygounionmerchant.utils.ConstantUtils;
import com.zyd.wlwsdk.adapter.ViewHolder;
import com.zyd.wlwsdk.adapter.abslistview.CommonAdapter;
import com.zyd.wlwsdk.utils.L;
import com.zyd.wlwsdk.utils.SharedPreferencesUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * 选择登录方式
 * Created by Dlt on 2017/8/12 16:06
 */
public class SelectLoginTypeActivity extends BaseActivity {

    @Bind(R.id.list_view)
    ListView mListView;

    private List<String[]> list = new ArrayList<>();
    private CommonAdapter commonAdapter;

    public static Activity selectLoginTypeActivity;

    @Override
    public int initResource() {
        return R.layout.activity_select_login_type;
    }

    @Override
    public void initData() {
        CommonUtils.setTitleBar(this, false, "选择登录类型", "");
        selectLoginTypeActivity = this;

        requestHandleArrayList.add(requestAction.getLoginType(SelectLoginTypeActivity.this));

//        mockData();
//        processData();
    }

    private void mockData() {
        String[] s1 = {"小铺", "中小型商铺的优秀管家", "http://qqwlw.oss-cn-shenzhen.aliyuncs.com/app-minishop-img/yiyeshop201708050241232137176525.jpg"};
        String[] s2 = {"旅游", "身未动，心已远", "http://qqwlw.oss-cn-shenzhen.aliyuncs.com/app-minishop-img/yiyeshop201708050241231133683741.jpg"};
        String[] s3 = {"酒店", "您的陪伴是我们最大的动力", "http://zykshop.qqjlb.cn/zyypayPics/zyyPay201705160612231802700228.jpg"};
        list.add(s1);
        list.add(s2);
        list.add(s3);
    }

    private void processData() {
        commonAdapter = new CommonAdapter<String[]>(SelectLoginTypeActivity.this, R.layout.item_select_login_type, list) {
            @Override
            public void convert(ViewHolder holder, String[] strings) {
                ImageView imageView = holder.getView(R.id.iv_left);
                CommonUtils.setDisplayImage(imageView, strings[1], 0, R.drawable.default_image);
                holder.setText(R.id.tv_title, strings[2]);
                holder.setText(R.id.tv_content, strings[3]);
            }
        };

        mListView.setAdapter(commonAdapter);
        mListView.setDivider(null);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SelectLoginTypeActivity.this, LoginActivity.class);
                intent.putExtra("title", list.get(position)[2]);
                intent.putExtra("loginType", list.get(position)[0]);
                startActivity(intent);

                SharedPreferencesUtils.setString(mContext, ConstantUtils.SP_LOGINTYPE, list.get(position)[0]);
            }
        });

    }

    @Override
    public void requestSuccess(int requestTag, JSONObject response, int showLoad) throws JSONException {
        super.requestSuccess(requestTag, response, showLoad);
        switch (requestTag) {
            case RequestAction.TAG_LOGINTYPE:
                L.e("获取登录类型：", response + "");

                int num = Integer.parseInt(response.getString("条数"));
                if (num != 0) {
                    JSONArray jsonArray = response.getJSONArray("列表");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                        String[] s = new String[5];
                        s[0] = jsonObject.getString("类别");
                        s[1] = jsonObject.getString("图标地址");
                        s[2] = jsonObject.getString("标题");
                        s[3] = jsonObject.getString("副标题");
                        s[4] = jsonObject.getString("排序");
                        list.add(s);
                    }
                    processData();
                } else {

                }

                break;
        }
    }

}
