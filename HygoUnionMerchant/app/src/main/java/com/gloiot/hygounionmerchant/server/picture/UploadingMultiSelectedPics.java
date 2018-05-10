package com.gloiot.hygounionmerchant.server.picture;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.abcaaa.photopicker.PhotoPicker;
import com.abcaaa.photopicker.PhotoPreview;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.zxy.tiny.Tiny;
import com.zyd.wlwsdk.server.AliOss.AliOss;
import com.zyd.wlwsdk.server.AliOss.AliOssNew;
import com.zyd.wlwsdk.utils.BitmapUtils;
import com.zyd.wlwsdk.utils.ImageCompress;
import com.zyd.wlwsdk.utils.L;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 上传选中图片到阿里云（多张）
 * Created by Dlt on 2018/1/12 16:47
 */
public abstract class UploadingMultiSelectedPics {

    private Context mContext;
    private List<String> photos;
    private int requestCode;
    private ProgressDialog progressDialog;
    int a = 0;//记录上传成功的图片数量

    //图片压缩
    private Tiny.FileCompressOptions compressOptions;

    public UploadingMultiSelectedPics(Context context, List<String> photos, int requestCode) {
        this.mContext = context;
        this.photos = photos;
        this.requestCode = requestCode;
        a = 0;
    }

    public UploadingMultiSelectedPics(Context context, List<String> photos, int requestCode, Tiny.FileCompressOptions Options) {
        this.mContext = context;
        this.photos = photos;
        this.requestCode = requestCode;
        a = 0;
        if (Options == null) {
            // 图片压缩默认300k内
            compressOptions = new Tiny.FileCompressOptions();
            compressOptions.size = 300;
        } else {
            compressOptions = Options;
        }
    }

    /**
     * 上传多张图片不压缩
     *
     * @return
     */
    public UploadingMultiSelectedPics setMultiPics() {
        if (photos != null) {
            if (requestCode == PhotoPicker.REQUEST_CODE) {
                final List<Bitmap> images = new ArrayList<>();
                for (int i = 0; i < photos.size(); i++) {
                    L.e("选中图片初始路径--选择上传", "==" + photos.get(i));
                    Bitmap image = BitmapFactory.decodeFile(photos.get(i));
                    images.add(image);
                }
                final List<String> picsUrl = new ArrayList<>();//上传阿里云成功后返回一个图片url，加入到这个集合
                progressDialog = new ProgressDialog(mContext);
                progressDialog.setMessage("图片上传中，请稍候...");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//                progressDialog.setCancelable(false);
                progressDialog.show();

                L.e("test", "111111111111");
                for (final Bitmap image : images) {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            new AliOss(mContext, AliOss.setPicName(AliOss.BucketName), BitmapUtils.compressImage(image)) {//这里设置上传的地址
                                @Override
                                protected void uploadProgress(long currentSize, long totalSize) {

                                }

                                @Override
                                public void uploadSuccess(String myPicUrl) {
                                    L.e("上传阿里云成功-----imagesUrl", "==" + myPicUrl);
                                    picsUrl.add(myPicUrl);
                                    a++;
                                    L.e("test", "222222222" + ",a=" + a + ",images.size()=" + images.size());
                                    if (a == images.size()) {
                                        progressDialog.dismiss();

//                                        changeList = picsUrl;
//                                        SuggestionFeedbackActivity.this.runOnUiThread(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                for (int i = 0; i < changeList.size(); i++) {//只把新增的图片上传，而不是所有上传
//                                                    allPics += changeList.get(i) + "|";
//                                                }
//                                                if (allPics.substring(0, 4).equals("null")) {
//                                                    allPics = allPics.substring(4, allPics.length() - 1);//因为默认最前面会有一个null，最后面会有一个逗号
//                                                } else {
//                                                    allPics = allPics.substring(0, allPics.length() - 1);//最后面会有一个逗号
//                                                }
//
//                                                selectedPhotos.addAll(changeList);
//
//                                                for (int i = 0; i < changeList.size(); i++) {
//                                                    L.e("changeList", "i=" + i + ",---" + changeList.get(i));
//                                                }
//                                                mShopInfoPicsAdapter.notifyDataSetChanged();
//
//                                            }
//                                        });

                                        setMultiPicsSuccess(picsUrl);
                                        a = 0;//重置


                                    } else {
//                                            progressDialog.dismiss();

                                    }
                                }

                                @Override
                                protected void uploadFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                                    a++;
                                    setMultiPicsFailure();
                                }
                            }.start();

                        }

                    }).start();
                }

            } else if (requestCode == PhotoPreview.REQUEST_CODE) {

                for (int i = 0; i < photos.size(); i++) {
                    L.e("选中图片初始路径--预览", "==" + photos.get(i));
                }
//                final List<String> previewBackList = photos; //预览返回的列表
//
//                SuggestionFeedbackActivity.this.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        for (int i = 0; i < previewBackList.size(); i++) {
//                            allPics += previewBackList.get(i) + "|";
//                        }
//                        L.e("allpics", "aaaaaaa=" + allPics);
//                        try {
//                            if (allPics.substring(0, 4).equals("null")) {
//                                allPics = allPics.substring(4, allPics.length() - 1);//因为默认最前面会有一个null，最后面会有一个逗号
//                            } else {
//                                allPics = allPics.substring(0, allPics.length() - 1);//最后面会有一个逗号
//                            }
//                        } catch (StringIndexOutOfBoundsException e) {
//                            e.printStackTrace();
//                        }
//
//
//                        selectedPhotos.addAll(previewBackList);
//
//                        mShopInfoPicsAdapter.notifyDataSetChanged();
//
//                    }
//                });

                processPreviewPhoto(photos);

            }
            return this;
        }
        return this;
    }

    /**
     * 上传多张图片带压缩
     *
     * @return
     */
    public UploadingMultiSelectedPics setMultiPicsWithCompress() {
        if (photos != null) {
            if (requestCode == PhotoPicker.REQUEST_CODE) {

                for (int i = 0; i < photos.size(); i++) {
                    L.e("选中图片初始路径--选择上传", "==" + photos.get(i));
                }
                final List<String> picsUrl = new ArrayList<>();//上传阿里云成功后返回一个图片url，加入到这个集合

                progressDialog = new ProgressDialog(mContext);
                progressDialog.setMessage("图片上传中，请稍候...");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//                progressDialog.setCancelable(false);
                progressDialog.show();

                for (final String photo : photos) {

                    //计算图片压缩目标大小
                    File outputFile = new File(photo);
                    long fileSize = outputFile.length();
                    double scale = Math.sqrt((float) fileSize / (1024 * compressOptions.size));

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
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

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        new AliOssNew(mContext, AliOssNew.setPicName(AliOssNew.BucketName), outfile) {//这里设置上传的地址
                                            @Override
                                            protected void uploadProgress(long currentSize, long totalSize) {

                                            }

                                            @Override
                                            public void uploadSuccess(String myPicUrl) {
//
//                                                progressDialog.dismiss();
//                                                setSinglePicSuccess(myPicUrl);

                                                L.e("上传阿里云成功-----imagesUrl", "==" + myPicUrl);
                                                picsUrl.add(myPicUrl);
                                                a++;
                                                L.e("test", "222222222" + ",a=" + a + ",images.size()=" + photos.size());
                                                if (a == photos.size()) {
                                                    progressDialog.dismiss();

//                                        changeList = picsUrl;
//                                        SuggestionFeedbackActivity.this.runOnUiThread(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                for (int i = 0; i < changeList.size(); i++) {//只把新增的图片上传，而不是所有上传
//                                                    allPics += changeList.get(i) + "|";
//                                                }
//                                                if (allPics.substring(0, 4).equals("null")) {
//                                                    allPics = allPics.substring(4, allPics.length() - 1);//因为默认最前面会有一个null，最后面会有一个逗号
//                                                } else {
//                                                    allPics = allPics.substring(0, allPics.length() - 1);//最后面会有一个逗号
//                                                }
//
//                                                selectedPhotos.addAll(changeList);
//
//                                                for (int i = 0; i < changeList.size(); i++) {
//                                                    L.e("changeList", "i=" + i + ",---" + changeList.get(i));
//                                                }
//                                                mShopInfoPicsAdapter.notifyDataSetChanged();
//
//                                            }
//                                        });

                                                    setMultiPicsSuccess(picsUrl);
                                                    a = 0;//重置


                                                } else {
//                                            progressDialog.dismiss();

                                                }


                                            }

                                            @Override
                                            protected void uploadFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
//                                                progressDialog.dismiss();
//                                                setSinglePicFailure();

                                                a++;
                                                setMultiPicsFailure();
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

            } else if (requestCode == PhotoPreview.REQUEST_CODE) {

                for (int i = 0; i < photos.size(); i++) {
                    L.e("选中图片初始路径--预览", "==" + photos.get(i));
                }
//                final List<String> previewBackList = photos; //预览返回的列表
//
//                SuggestionFeedbackActivity.this.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        for (int i = 0; i < previewBackList.size(); i++) {
//                            allPics += previewBackList.get(i) + "|";
//                        }
//                        L.e("allpics", "aaaaaaa=" + allPics);
//                        try {
//                            if (allPics.substring(0, 4).equals("null")) {
//                                allPics = allPics.substring(4, allPics.length() - 1);//因为默认最前面会有一个null，最后面会有一个逗号
//                            } else {
//                                allPics = allPics.substring(0, allPics.length() - 1);//最后面会有一个逗号
//                            }
//                        } catch (StringIndexOutOfBoundsException e) {
//                            e.printStackTrace();
//                        }
//
//
//                        selectedPhotos.addAll(previewBackList);
//
//                        mShopInfoPicsAdapter.notifyDataSetChanged();
//
//                    }
//                });

                processPreviewPhoto(photos);
            }
            return this;
        }
        return this;
    }

    /**
     * 选择上传多张图片成功
     *
     * @param picsUrl
     */
    protected abstract void setMultiPicsSuccess(List<String> picsUrl);

    /**
     * 选择上传多张图片失败
     */
    protected abstract void setMultiPicsFailure();

    /**
     * 处理预览图片
     *
     * @param photos
     */
    protected abstract void processPreviewPhoto(List<String> photos);

}
