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
 * 小铺今日收益列表（RecyclerView）适配器(20170724 改为小铺需要的样式)
 * Created by Dlt on 2017/8/22 19:09
 */
public class ShopTodayEarningAdapter extends SwipeMenuAdapter<ShopTodayEarningAdapter.ViewHolder> {

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_oddNum, tv_time, tv_jiaoyi, tv_daozhang;
        OnItemClickListener mOnItemClickListener;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            tv_oddNum = (TextView) itemView.findViewById(R.id.tv_oddNum);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            tv_jiaoyi = (TextView) itemView.findViewById(R.id.tv_jiaoyi);
            tv_daozhang = (TextView) itemView.findViewById(R.id.tv_daozhang);
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.mOnItemClickListener = onItemClickListener;
        }

        public void setData(String oddNum, String time, String jiaoyi, String daozhang) {
            this.tv_oddNum.setText("交易单号：" + oddNum);
            this.tv_time.setText(time);
            this.tv_jiaoyi.setText(jiaoyi);
            this.tv_daozhang.setText(daozhang);
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

    public ShopTodayEarningAdapter(List<String[]> list) {
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
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trade_shop_today_earning, parent, false);
    }

    @Override
    public ShopTodayEarningAdapter.ViewHolder onCompatCreateViewHolder(View realContentView, int viewType) {
        return new ViewHolder(realContentView);
    }

    @Override
    public void onBindViewHolder(ShopTodayEarningAdapter.ViewHolder holder, int position) {
        holder.setData(list.get(position)[0], list.get(position)[1], list.get(position)[2], list.get(position)[3]);
        holder.setOnItemClickListener(mOnItemClickListener);
    }

}
