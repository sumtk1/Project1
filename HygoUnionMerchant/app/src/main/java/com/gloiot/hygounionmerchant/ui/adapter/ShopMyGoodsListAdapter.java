package com.gloiot.hygounionmerchant.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gloiot.hygounionmerchant.R;
import com.gloiot.hygounionmerchant.utils.CommonUtils;
import com.gloiot.hygounionmerchant.widget.OnItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuAdapter;

import java.util.List;

/**
 * 小铺我的商品列表适配器
 * Created by Dlt on 2017/8/19 10:57
 */
public class ShopMyGoodsListAdapter extends SwipeMenuAdapter<ShopMyGoodsListAdapter.ViewHolder> {

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView iv_pic;
        TextView tv_title, tv_market, tv_supply, tv_ratio;

        OnItemClickListener mOnItemClickListener;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            iv_pic = (ImageView) itemView.findViewById(R.id.iv_pic);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            tv_market = (TextView) itemView.findViewById(R.id.tv_market);
            tv_supply = (TextView) itemView.findViewById(R.id.tv_supply);
            tv_ratio = (TextView) itemView.findViewById(R.id.tv_ratio);
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.mOnItemClickListener = onItemClickListener;
        }

        public void setData(String picUrl, String title, String martet, String supply, String ratio) {
            CommonUtils.setDisplayImage(this.iv_pic, picUrl, 0, R.drawable.default_image);
            this.tv_title.setText(title);
            this.tv_market.setText(martet);
            this.tv_supply.setText(supply);
            this.tv_ratio.setText(ratio + "%");
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

    public ShopMyGoodsListAdapter(List<String[]> list) {
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
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shop_my_goods, parent, false);
    }

    @Override
    public ShopMyGoodsListAdapter.ViewHolder onCompatCreateViewHolder(View realContentView, int viewType) {
        return new ViewHolder(realContentView);
    }

    @Override
    public void onBindViewHolder(ShopMyGoodsListAdapter.ViewHolder holder, int position) {
        holder.setData(list.get(position)[2], list.get(position)[1], list.get(position)[4],
                list.get(position)[3], list.get(position)[7]);
        holder.setOnItemClickListener(mOnItemClickListener);
    }

}
