package com.gloiot.hygounionmerchant.ui.adapter.warehousing;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gloiot.hygounionmerchant.R;
import com.gloiot.hygounionmerchant.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 商品入库管理页面，内容子项适配器
 * Created by Dlt on 2017/8/4 14:15
 */
public class CommodityWarehousingItemAdapter extends BaseAdapter {

    private Context context;
    private List<String[]> list = new ArrayList<String[]>();

    public CommodityWarehousingItemAdapter(Context context, List<String[]> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public String[] getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = View.inflate(context, R.layout.item_shop_warehousing_goods_info, null);

        ImageView iv_pic = (ImageView) view.findViewById(R.id.iv_pic);
        TextView tv_goods_title = (TextView) view.findViewById(R.id.tv_goods_title);
        TextView tv_market_price = (TextView) view.findViewById(R.id.tv_market_price);
        TextView tv_amount = (TextView) view.findViewById(R.id.tv_amount);

        CommonUtils.setDisplayImage(iv_pic, list.get(position)[2], 0, R.drawable.default_image);
        tv_goods_title.setText(list.get(position)[1]);
        tv_market_price.setText(list.get(position)[4]);
        tv_amount.setText(list.get(position)[10]);

        return view;
    }
}
