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
 * 资质认证--上传行业许可证的适配器
 * Created by Dlt on 2017/10/24 14:38
 */
public class CertificationIndustryLicenceAdapter extends RecyclerView.Adapter<CertificationIndustryLicenceAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivPhoto;

        public ViewHolder(View itemView) {
            super(itemView);
            ivPhoto = (ImageView) itemView.findViewById(R.id.iv_photo);

        }
    }

    public final static int TYPE_ADD = 1;
    public final static int TYPE_PHOTO = 2;
    public static int MAX = 3;//最多几张

    private Context mContext;
    private LayoutInflater inflater;
    private ArrayList<String> photoPaths = new ArrayList<String>();

    public CertificationIndustryLicenceAdapter(Context mContext, ArrayList<String> photoPaths, int mount) {
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
                itemView = inflater.inflate(R.layout.layout_zizhi_otherlincence_add, parent, false);
                break;
            case TYPE_PHOTO:
                itemView = inflater.inflate(R.layout.layout_zizhi_otherlincence_photo, parent, false);
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
                        .placeholder(R.drawable.pic_loading)
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
