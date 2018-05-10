package com.gloiot.hygounionmerchant.server.picture;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.zyd.wlwsdk.server.AliOss.AliOss;
import com.zyd.wlwsdk.utils.BitmapUtils;

/**
 * 上传选中图片（到阿里云）--无进度提示框
 * Created by Dlt on 2017/8/29 16:06
 */
public abstract class UploadingSelectedPicsForWebview {


    private Context mContext;
    private String photo;
    private ProgressDialog progressDialog;

    public UploadingSelectedPicsForWebview(Context context, String photo) {
        this.mContext = context;
        this.photo = photo;
    }

    public UploadingSelectedPicsForWebview setSinglePic() {
        if (photo != null) {
            Log.e("选中图片路径", "==" + photo);
            final Bitmap image = BitmapFactory.decodeFile(photo);
//            progressDialog = new ProgressDialog(mContext);
//            progressDialog.setMessage("图片上传中，请稍候...");
//            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
////                progressDialog.setCancelable(false);
//            progressDialog.show();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    new AliOss(mContext, AliOss.setPicName("app-unionmerchant-img"), BitmapUtils.compressImage(image)) {//这里设置上传的地址
                        @Override
                        protected void uploadProgress(long currentSize, long totalSize) {

                        }

                        @Override
                        public void uploadSuccess(String myPicUrl) {
                            Log.e("ddddddddd", myPicUrl);
//                            progressDialog.dismiss();
                            setSinglePicSuccess(myPicUrl);
                        }

                        @Override
                        protected void uploadFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                            setSinglePicFailure();
                        }
                    }.start();
                }

            }).start();
        }
        return this;
    }

    /**
     * 选择上传单张图片成功
     *
     * @param picUrl
     */
    protected abstract void setSinglePicSuccess(String picUrl);

    /**
     * 选择上传单张图片失败
     */
    protected abstract void setSinglePicFailure();

}
