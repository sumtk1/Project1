package com.gloiot.hygounionmerchant.ui.adapter.warehousing;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gloiot.hygounionmerchant.R;
import com.gloiot.hygounionmerchant.ui.activity.home.ShopWarehousingManagementFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dlt on 2017/7/29 11:35
 */
public class TitleListAdapter extends BaseAdapter {

    private Context context;
    private List<String> list = new ArrayList<String>();

    public TitleListAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public String getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = View.inflate(context, R.layout.item_shop_warehousing_title, null);
        TextView title = (TextView) view.findViewById(R.id.tv_title);
        title.setText(list.get(position));
        if (position == ShopWarehousingManagementFragment.POSITION) {
//            view.setBackgroundColor(context.getResources().getColor(R.color.gray_f8f8f8));
//            title.setTextColor(context.getResources().getColor(R.color.gray_666));

            title.setBackgroundColor(context.getResources().getColor(R.color.white));
            title.setTextColor(context.getResources().getColor(R.color.black_333));

//            view.setBackgroundColor(Color.CYAN);
        }
        return view;
    }
}
