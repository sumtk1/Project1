package com.gloiot.hygounionmerchant.server.picture;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.zxy.tiny.Tiny;
import com.zyd.wlwsdk.server.AliOss.AliOss;
import com.zyd.wlwsdk.server.AliOss.AliOssNew;
import com.zyd.wlwsdk.utils.BitmapUtils;
import com.zyd.wlwsdk.utils.ImageCompress;
import com.zyd.wlwsdk.utils.L;
import com.zyd.wlwsdk.utils.MToast;

import java.io.File;

/**
 * 上传选中图片（到阿里云）
 * Created by Dlt on 2017/5/16 18:39
 */
public abstract class UploadingSelectedPics {

    private Context mContext;
    private String photo;
    private ProgressDialog progressDialog;

    //图片压缩
    private Tiny.FileCompressOptions compressOptions;

    public UploadingSelectedPics(Context context, String photo) {
        this.mContext = context;
        this.photo = photo;
    }

    public UploadingSelectedPics(Context context, String photo, Tiny.FileCompressOptions Options) {
        this.mContext = context;
        this.photo = photo;
        if (Options == null) {
            // 图片压缩默认300k内
            compressOptions = new Tiny.FileCompressOptions();
            compressOptions.size = 300;
        } else {
            compressOptions = Options;
        }
    }

    /**
     * 上传单张图片不压缩
     *
     * @return
     */
    public UploadingSelectedPics setSinglePic() {
        if (photo != null) {
            Log.e("选中图片路径", "==" + photo);
            final Bitmap image = BitmapFactory.decodeFile(photo);
            progressDialog = new ProgressDialog(mContext);
            progressDialog.setMessage("图片上传中，请稍候...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//                progressDialog.setCancelable(false);
            progressDialog.show();

            new Thread(new Runnable() {
                @Override
                public void run() {
//                    new AliOss(mContext, AliOss.setPicName("app-unionmerchant-img"), BitmapUtils.compressImage(image)) {//这里设置上传的地址
                    new AliOss(mContext, AliOss.setPicName(AliOss.BucketName), BitmapUtils.compressImage(image)) {//这里设置上传的地址
                        @Override
                        protected void uploadProgress(long currentSize, long totalSize) {

                        }

                        @Override
                        public void uploadSuccess(String myPicUrl) {
                            progressDialog.dismiss();
                            setSinglePicSuccess(myPicUrl);
                        }

                        @Override
                        protected void uploadFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                            progressDialog.dismiss();
                            setSinglePicFailure();
                            MToast.showToast(mContext, "上传失败");
                        }
                    }.start();
                }

            }).start();
        }
        return this;
    }

    /**
     * 上传单张图片带压缩
     *
     * @return
     */
    public UploadingSelectedPics setSinglePicWithCompress() {
        if (photo != null) {
            Log.e("选中图片路径", "==" + photo);
            final Bitmap image = BitmapFactory.decodeFile(photo);
            progressDialog = new ProgressDialog(mContext);
            progressDialog.setMessage("图片上传中，请稍候...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//                progressDialog.setCancelable(false);
            progressDialog.show();

            //计算图片压缩目标大小
//            File outputFile = new File(resultList.get(0).getPhotoPath());
            File outputFile = new File(photo);
            long fileSize = outputFile.length();
            double scale = Math.sqrt((float) fileSize / (1024 * compressOptions.size));

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
//            BitmapFactory.decodeFile(resultList.get(0).getPhotoPath(), options);
            BitmapFactory.decodeFile(photo, options);
            int height = options.outHeight;
            int width = options.outWidth;

            compressOptions.width = (int) (width / scale);
            compressOptions.height = (int) (height / scale);
            compressOptions.config = Bitmap.Config.ARGB_8888;


            ImageCompress.Tiny(photo, new ImageCompress.TinyCallback() {
                @Override
                public void onCallback(boolean isSuccess, Bitmap bitmap, final String outfile) {
                    if (isSuccess) {
                        L.e("图片压缩--", "成功");
//                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//                            final byte[] bytes = baos.toByteArray();

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                new AliOssNew(mContext, AliOssNew.setPicName(AliOssNew.BucketName), outfile) {//这里设置上传的地址
                                    @Override
                                    protected void uploadProgress(long currentSize, long totalSize) {

                                    }

                                    @Override
                                    public void uploadSuccess(String myPicUrl) {
                                        L.e("ddddddddd", myPicUrl);
                                        progressDialog.dismiss();
                                        setSinglePicSuccess(myPicUrl);
                                    }

                                    @Override
                                    protected void uploadFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                                        progressDialog.dismiss();
                                        setSinglePicFailure();
//                                        MToast.showToast(mContext, "上传失败");
                                    }
                                }.start();
                            }

                        }).start();
                    } else {
                        L.e("图片压缩--", "失败");
                    }
                }
            }, compressOptions);

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
