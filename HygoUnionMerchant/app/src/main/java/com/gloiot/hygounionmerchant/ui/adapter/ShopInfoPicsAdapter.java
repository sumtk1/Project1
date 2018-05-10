package com.gloiot.hygounionmerchant.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.abcaaa.photopicker.utils.AndroidLifecycleUtils;
import com.bumptech.glide.Glide;
import com.gloiot.hygounionmerchant.R;

import java.util.ArrayList;

/**
 * Created by Dlt on 2017/9/19 18:02
 */
public class ShopInfoPicsAdapter extends RecyclerView.Adapter<ShopInfoPicsAdapter.ViewHolder> {

    public final static int TYPE_ADD = 1;
    public final static int TYPE_PHOTO = 2;
    public static int MAX = 5;

    private Context mContext;
    private LayoutInflater inflater;
    private ArrayList<String> photoPaths = new ArrayList<String>();

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivPhoto;
        private ImageView ivDelete;

        public ViewHolder(View itemView) {
            super(itemView);
            ivPhoto = (ImageView) itemView.findViewById(R.id.iv_photo);
            ivDelete = (ImageView) itemView.findViewById(R.id.iv_delete);

        }
    }

    public ShopInfoPicsAdapter(Context mContext, ArrayList<String> photoPaths, int mount) {
        this.mContext = mContext;
        this.photoPaths = photoPaths;
        MAX = mount;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        switch (viewType) {
            case TYPE_ADD:
                itemView = inflater.inflate(R.layout.layout_shopinfo_add, parent, false);
                break;
            case TYPE_PHOTO:
                itemView = inflater.inflate(R.layout.layout_shopinfo_photo, parent, false);
                break;
        }
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        if (getItemViewType(position) == TYPE_PHOTO) {
//            Uri uri = Uri.fromFile(new File(photoPaths.get(position)));

            String uri = photoPaths.get(position);

            boolean canLoadImage = AndroidLifecycleUtils.canLoadImage(holder.ivPhoto.getContext());

            if (canLoadImage) {
                Glide.with(mContext)
                        .load(uri)
                        .centerCrop()
                        .thumbnail(0.1f)
                        .placeholder(R.drawable.__picker_ic_photo_black_48dp)
                        .error(R.drawable.ic_jiazaizhong)
                        .into(holder.ivPhoto);
            }

        }
    }

    @Override
    public int getItemCount() {
        int count = photoPaths.size() + 1;
        if (count > MAX) {
            count = MAX;
        }
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == photoPaths.size() && position != MAX) ? TYPE_ADD : TYPE_PHOTO;
    }

}
