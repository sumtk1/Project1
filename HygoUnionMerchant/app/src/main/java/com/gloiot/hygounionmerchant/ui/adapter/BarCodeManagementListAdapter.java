package com.gloiot.hygounionmerchant.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gloiot.hygounionmerchant.R;
import com.gloiot.hygounionmerchant.widget.OnItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuAdapter;

import java.util.List;

/**
 * 条码管理页面列表适配器
 * Created by Dlt on 2017/8/3 11:41
 */
public class BarCodeManagementListAdapter extends SwipeMenuAdapter<BarCodeManagementListAdapter.ViewHolder> {
    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tv_bar_code_num, tv_commodity_name;

        OnItemClickListener mOnItemClickListener;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            tv_bar_code_num = (TextView) itemView.findViewById(R.id.tv_bar_code_num);
            tv_commodity_name = (TextView) itemView.findViewById(R.id.tv_commodity_name);
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.mOnItemClickListener = onItemClickListener;
        }

        public void setData(String barCodeNum, String CommodityName) {
            this.tv_bar_code_num.setText(barCodeNum);
            this.tv_commodity_name.setText(CommodityName);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(getAdapterPosition());
            }
        }
    }

    private List<String[]> list;
    private OnItemClickListener mOnItemClickListener;

    public BarCodeManagementListAdapter(List<String[]> list) {
        this.list = list;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public View onCreateContentView(ViewGroup parent, int viewType) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shop_bar_code_management, parent, false);
    }

    @Override
    public BarCodeManagementListAdapter.ViewHolder onCompatCreateViewHolder(View realContentView, int viewType) {
        return new ViewHolder(realContentView);
    }

    @Override
    public void onBindViewHolder(BarCodeManagementListAdapter.ViewHolder holder, int position) {
        holder.setData(list.get(position)[9], list.get(position)[0]);
        holder.setOnItemClickListener(mOnItemClickListener);
    }
}
