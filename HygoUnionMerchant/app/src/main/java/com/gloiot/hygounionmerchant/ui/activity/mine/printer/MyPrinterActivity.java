package com.gloiot.hygounionmerchant.ui.activity.mine.printer;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

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
import butterknife.OnClick;

/**
 * 我的打印机
 * Created by Dlt on 2017/9/19 15:46
 */
public class MyPrinterActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.list_view)
    ListView mListView;
    @Bind(R.id.rl_add_printer)
    RelativeLayout mRlAddPrinter;

    private String accountType;
    private List<String[]> list = new ArrayList<>();
    private CommonAdapter commonAdapter;

    @OnClick(R.id.rl_add_printer)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_add_printer:
                startActivity(new Intent(this, AddPrinterActivity.class));
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        list.clear();
        requestHandleArrayList.add(requestAction.shopGetPrinter(this, accountType));
    }

    @Override
    public int initResource() {
        return R.layout.activity_my_printer;
    }

    @Override
    public void initData() {
        accountType = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTTYPE, "");
        CommonUtils.setTitleBar(this, true, "我的打印机", "");
    }

    @Override
    public void requestSuccess(int requestTag, JSONObject response, int showLoad) throws JSONException {
        super.requestSuccess(requestTag, response, showLoad);
        switch (requestTag) {
            case RequestAction.TAG_SHOPGETPRINTERINFO:
                L.e("获取小铺打印机", response.toString());

                int num = Integer.parseInt(response.getString("条数"));
                if (num != 0) {
                    mRlAddPrinter.setVisibility(View.GONE);
                    mListView.setVisibility(View.VISIBLE);

                    JSONArray jsonArray = response.getJSONArray("列表");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        String[] a = new String[5];
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                        a[0] = jsonObject.getString("GPS卡号");
                        a[1] = jsonObject.getString("设备终端号");
                        a[2] = jsonObject.getString("密钥");
                        a[3] = jsonObject.getString("类别");//正常
                        a[4] = jsonObject.getString("GPS序列号");

                        list.add(a);
                    }
                    processData();

                } else {
                    mListView.setVisibility(View.GONE);
                    mRlAddPrinter.setVisibility(View.VISIBLE);
                }

                break;
        }
    }

    private void processData() {

        commonAdapter = new CommonAdapter<String[]>(MyPrinterActivity.this, R.layout.item_my_printer, list) {
            @Override
            public void convert(final ViewHolder holder, String[] strings) {
                holder.setText(R.id.tv_terminal_num, strings[1]);
                holder.setText(R.id.tv_secret_key, strings[2]);
                holder.setText(R.id.tv_gps_num, strings[0]);
            }
        };
        mListView.setAdapter(commonAdapter);
        mListView.setDivider(new ColorDrawable(Color.parseColor("#f3f3f3")));
        mListView.setDividerHeight(20);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MyPrinterActivity.this, EditPrinterActivity.class);
                intent.putExtra("terminalNum", list.get(position)[1]);
                intent.putExtra("secretKey", list.get(position)[2]);
                intent.putExtra("gpsCardNum", list.get(position)[0]);
                intent.putExtra("gpsSerialNum", list.get(position)[4]);

                startActivity(intent);
            }
        });

    }

}
