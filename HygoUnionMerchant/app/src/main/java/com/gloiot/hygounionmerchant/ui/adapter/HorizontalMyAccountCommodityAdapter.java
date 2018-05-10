package com.gloiot.hygounionmerchant.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gloiot.hygounionmerchant.R;
import com.gloiot.hygounionmerchant.utils.CommonUtils;
import com.gloiot.hygounionmerchant.widget.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 横向我的小铺/酒店/旅行社商品适配器
 * Created by Dlt on 2017/8/24 14:26
 */
public class HorizontalMyAccountCommodityAdapter extends RecyclerView.Adapter<HorizontalMyAccountCommodityAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView ivPhoto;
        private TextView tvName, tvPrice;

        OnItemClickListener mOnItemClickListener;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            ivPhoto = (ImageView) itemView.findViewById(R.id.iv_pic);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvPrice = (TextView) itemView.findViewById(R.id.tv_unit_price);
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.mOnItemClickListener = onItemClickListener;
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(getAdapterPosition());
            }
        }
    }


    public final static int TYPE_MORE = 1;
    public final static int TYPE_PHOTO = 2;
    public final static int MAX = 11;

    private Context mContext;
    private LayoutInflater inflater;
    private List<String[]> list = new ArrayList<>();
    private OnItemClickListener mOnItemClickListener;
    private String accountType;

    public HorizontalMyAccountCommodityAdapter(Context mContext, List<String[]> list, String accountType) {
        this.mContext = mContext;
        this.list = list;
        this.accountType = accountType;
        inflater = LayoutInflater.from(mContext);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        switch (viewType) {
            case TYPE_MORE:
                itemView = inflater.inflate(R.layout.layout_myaccount_pics_more, parent, false);
                break;
            case TYPE_PHOTO:
                itemView = inflater.inflate(R.layout.layout_myaccount_pics, parent, false);
                break;
        }
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        if (getItemViewType(position) == TYPE_PHOTO) {

//            if (accountType.equals("小铺")) {
//                CommonUtils.setDisplayImage(holder.ivPhoto, list.get(position)[2], 0, R.mipmap.ic_launcher);
//                holder.tvName.setText(list.get(position)[1]);
//                holder.tvPrice.setText(list.get(position)[4]);
//            } else if (accountType.equals("酒店")) {
//                CommonUtils.setDisplayImage(holder.ivPhoto, list.get(position)[0], 0, R.mipmap.ic_launcher);
//                holder.tvName.setText(list.get(position)[1]);
//                holder.tvPrice.setText(list.get(position)[2]);
//            } else if (accountType.equals("旅行社")) {
//                CommonUtils.setDisplayImage(holder.ivPhoto, list.get(position)[2], 0, R.mipmap.ic_launcher);
//                holder.tvName.setText(list.get(position)[1]);
//                holder.tvPrice.setText(list.get(position)[3]);
//            }

            //20170828修改，所有类型的商铺都返一样的值
            CommonUtils.setDisplayImage(holder.ivPhoto, list.get(position)[2], 0, R.mipmap.ic_launcher);
            holder.tvName.setText(list.get(position)[1]);
            holder.tvPrice.setText(list.get(position)[3]);

        }

        holder.setOnItemClickListener(mOnItemClickListener);

    }

    @Override
    public int getItemCount() {
        int count = list.size() + 1;
        if (count > MAX) {
            count = MAX;
        }
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == list.size() && position != MAX) ? TYPE_MORE : TYPE_PHOTO;
    }

}

