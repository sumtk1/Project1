package com.gloiot.hygounionmerchant.ui.adapter.warehousing;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.gloiot.hygounionmerchant.R;
import com.gloiot.hygounionmerchant.ui.activity.home.ShopCommodityInfoActivity;
import com.gloiot.hygounionmerchant.utils.CommonUtils;
import com.zyd.wlwsdk.adapter.ViewHolder;
import com.zyd.wlwsdk.adapter.abslistview.CommonAdapter;
import com.zyd.wlwsdk.utils.L;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Dlt on 2017/7/29 12:57
 */
public class ContentListAdapter extends BaseAdapter {

    private Context context;
    private List<String> titleList;
    private Map<String, List<String[]>> map = new HashMap<String, List<String[]>>();

    public ContentListAdapter(Context context, List<String> titleList, Map<String, List<String[]>> map) {
        this.context = context;
        this.titleList = titleList;
        this.map = map;
    }

    @Override
    public int getCount() {
        return map.size();
    }

    @Override
    public Object getItem(int position) {
        return map.get(titleList.get(position));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HodeView hodeView = null;
        if (convertView == null) {
            hodeView = new HodeView();
            convertView = View.inflate(context, R.layout.item_shop_warehousing_content, null);
            hodeView.tv_content_top = (TextView) convertView.findViewById(R.id.tv_content_top);
            hodeView.list_view_content = (ListView) convertView.findViewById(R.id.list_view_content);
            convertView.setTag(hodeView);
        } else {
            hodeView = (HodeView) convertView.getTag();
        }


        L.e("内容适配器--getView执行", "postion" + position + ",type" + titleList.get(position));

        List<String[]> tempList = map.get(titleList.get(position));

        for (int i = 0; i < tempList.size(); i++) {
            L.e("tempList", "postion" + position + ",title=" + titleList.get(position) + ",type" + titleList.get(position) + "，名称=" + tempList.get(i)[1]);
        }

//        final CommodityWarehousingItemAdapter adapter = new CommodityWarehousingItemAdapter(context, map.get(titleList.get(position)));
//        hodeView.list_view_content.setAdapter(adapter);
//        CommonUtlis.setListViewHeightBasedOnChildren(hodeView.list_view_content);

        //通用适配器部分
        final CommonAdapter mCommonAdapter;
        mCommonAdapter = new CommonAdapter<String[]>(context, R.layout.item_shop_warehousing_goods_info, map.get(titleList.get(position))) {
            @Override
            public void convert(final ViewHolder holder, String[] strings) {
                ImageView iv_pic = (ImageView) holder.getConvertView().findViewById(R.id.iv_pic);
                CommonUtils.setDisplayImage(iv_pic, strings[2], 0, R.drawable.default_image);
                holder.setText(R.id.tv_goods_title, strings[1]);
                holder.setText(R.id.tv_market_price, strings[4]);
                holder.setText(R.id.tv_amount, strings[10]);
            }
        };
        hodeView.list_view_content.setAdapter(mCommonAdapter);
        CommonUtils.setListViewHeightBasedOnChildren(hodeView.list_view_content);
        hodeView.list_view_content.setDivider(new ColorDrawable(Color.parseColor("#f3f3f3")));
        hodeView.list_view_content.setDividerHeight(2);

        hodeView.tv_content_top.setText(titleList.get(position));
        hodeView.list_view_content.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub

//                MToast.showToast(context, "点击位置=" + position);

                Intent intent = new Intent(context, ShopCommodityInfoActivity.class);

                intent.putExtra("commodityId", ((String[]) mCommonAdapter.getItem(position))[0]);
                L.e("商品id", ((String[]) mCommonAdapter.getItem(position))[0] + ",商品==" + ((String[]) mCommonAdapter.getItem(position))[1]);

//                intent.putExtra("commodityId", adapter.getItem(position)[0]);
//                L.e("商品id", adapter.getItem(position)[0] + ",商品==" + adapter.getItem(position)[1]);
                context.startActivity(intent);

            }
        });


        return convertView;
    }

    class HodeView {
        public TextView tv_content_top;
        public ListView list_view_content;

    }
}
