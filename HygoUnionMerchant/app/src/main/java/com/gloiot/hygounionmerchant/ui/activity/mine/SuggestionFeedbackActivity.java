package com.gloiot.hygounionmerchant.ui.activity.mine;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.abcaaa.photopicker.PhotoPicker;
import com.abcaaa.photopicker.PhotoPreview;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.gloiot.hygounionmerchant.R;
import com.gloiot.hygounionmerchant.server.network.RequestAction;
import com.gloiot.hygounionmerchant.ui.BaseActivity;
import com.gloiot.hygounionmerchant.ui.adapter.ShopInfoPicsAdapter;
import com.gloiot.hygounionmerchant.utils.CommonUtils;
import com.gloiot.hygounionmerchant.utils.ConstantUtils;
import com.gloiot.hygounionmerchant.utils.MaxLengthWatcher;
import com.gloiot.hygounionmerchant.utils.recyclerlistener.RecyclerItemClickListener;
import com.gloiot.hygounionmerchant.widget.SingleChoiceListViewPopupWindow;
import com.jaouan.compoundlayout.CompoundLayout;
import com.jaouan.compoundlayout.RadioLayout;
import com.zyd.wlwsdk.server.AliOss.AliOss;
import com.zyd.wlwsdk.utils.BitmapUtils;
import com.zyd.wlwsdk.utils.L;
import com.zyd.wlwsdk.utils.MToast;
import com.zyd.wlwsdk.utils.SharedPreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 意见反馈
 * Created by Dlt on 2017/9/19 17:03
 */
public class SuggestionFeedbackActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.tv_function)
    TextView mTvFunction;
    @Bind(R.id.radio_layout_function)
    RadioLayout mRadioLayoutFunction;
    @Bind(R.id.tv_other)
    TextView mTvOther;
    @Bind(R.id.radio_layout_other)
    RadioLayout mRadioLayoutOther;
    @Bind(R.id.et_problem)
    EditText mEtProblem;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.tv_confirm)
    TextView mTvConfirm;

    private String accountType;
    private int selectNum = 0;
    private ShopInfoPicsAdapter mShopInfoPicsAdapter;
    private ArrayList<String> selectedPhotos = new ArrayList<>();
    private List<String> changeList = new ArrayList<>();// 上传或删除的图片集合
    private String allPics = "";//所有图片的字符串拼接
    private String problemType, feedbackProblem;

    private ProgressDialog progressDialog;

    private SingleChoiceListViewPopupWindow popupWindow;
    private int selectPosition = -1;
    private List<String[]> list = new ArrayList<>();

    @OnClick(R.id.tv_confirm)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_confirm:
                if (selectNum == 0) {
                    MToast.showToast(mContext, "请选择反馈类别");
                } else {
                    if (selectNum == 1) {
                        problemType = "功能异常";
                    } else if (selectNum == 2) {
                        problemType = "其他问题";
                    }
                    feedbackProblem = mEtProblem.getText().toString().trim();
                    if (TextUtils.isEmpty(feedbackProblem)) {
                        MToast.showToast(mContext, "请填写您要反馈的问题");
                    } else {
                        requestHandleArrayList.add(requestAction.suggestionFeedback(this, problemType, feedbackProblem, allPics, accountType));
                    }

                }


                //---------------------------测试单选popupwindow-----------------
//                list.clear();
//                mockData();
//                Point position = CommonUtils.getNavigationBarSize(mContext);//虚拟键适配PopupWindow显示位置
//                popupWindow = new SingleChoiceListViewPopupWindow(mContext, popupWindowOnClick);
//                popupWindow.showAtLocation(mTvConfirm, Gravity.BOTTOM, 0, position.y);
//                final ListView listView = popupWindow.getListView();
//                final CommonAdapter commonAdapter = new CommonAdapter<String[]>(mContext, R.layout.item_popupwindow_singlechoice, list) {
//                    @Override
//                    public void convert(final ViewHolder holder, final String[] strings) {
//
//                        holder.setText(R.id.tv_text, strings[0]);
//
//                        final TextView textView = holder.getView(R.id.tv_text);
//                        final CheckBox checkBox = holder.getView(R.id.check_box);
//
//
//
//                    }
//                };
//                listView.setAdapter(commonAdapter);
//                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                        selectPosition = position;
//
//
//                    }
//                });
//                popupWindow.setMaxHeight(listView);


                break;
        }
    }

    private void mockData() {
        String[] s1 = {"小铺"};
        String[] s2 = {"旅游"};
        String[] s3 = {"酒店"};
        list.add(s1);
        list.add(s2);
        list.add(s3);
    }

    //为弹出窗口实现监听类--选择银行
    private View.OnClickListener popupWindowOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            popupWindow.dismiss();
            switch (v.getId()) {
                // 取消
                case R.id.tv_cancel:
                    selectPosition = -1;
                    break;
                // 确认
                case R.id.tv_confirm:

                    L.e("confirm", "selectPosition=" + selectPosition);

                    if (selectPosition < 0) {
                        return;
                    }
//                    String whichbank = banklist.get(selectPosition)[1].toString();
//                    tv_certificateBankcard_selectbank.setText(whichbank);
                    selectPosition = -1;
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public int initResource() {
        return R.layout.activity_suggestion_feedback;
    }

    @Override
    public void initData() {
        CommonUtils.setTitleBar(this, true, "意见反馈", "");
        accountType = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTTYPE, "");
        mEtProblem.addTextChangedListener(new MaxLengthWatcher(200, mEtProblem));//最多200个字符
        mRadioLayoutFunction.setOnCheckedChangeListener(new CompoundLayout.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundLayout compoundLayout, boolean checked) {
                if (checked) {
                    selectNum = 1;
                    mTvFunction.setBackgroundResource(R.drawable.bg_radiobutton_blue);
                    mTvFunction.setTextColor(getResources().getColor(R.color.blue_449ffb));
                    L.e("功能异常1", "num=" + selectNum);
                } else {
                    mTvFunction.setBackgroundResource(R.drawable.bg_radiobutton_gray);
                    mTvFunction.setTextColor(getResources().getColor(R.color.gray_666));
                    L.e("功能异常2", "num=" + selectNum);
                }
            }
        });

        mRadioLayoutOther.setOnCheckedChangeListener(new CompoundLayout.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundLayout compoundLayout, boolean checked) {
                if (checked) {
                    selectNum = 2;
                    mTvOther.setBackgroundResource(R.drawable.bg_radiobutton_blue);
                    mTvOther.setTextColor(getResources().getColor(R.color.blue_449ffb));
                    L.e("其他异常1", "num=" + selectNum);
                } else {
                    mTvOther.setBackgroundResource(R.drawable.bg_radiobutton_gray);
                    mTvOther.setTextColor(getResources().getColor(R.color.gray_666));
                    L.e("其他异常2", "num=" + selectNum);
                }
            }
        });

        mShopInfoPicsAdapter = new ShopInfoPicsAdapter(mContext, selectedPhotos, 1);

        for (int i = 0; i < selectedPhotos.size(); i++) {
            L.e("11111---selectedPhotos", "i=" + i + "===" + selectedPhotos.get(i));
        }

        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(4, OrientationHelper.VERTICAL));
        mRecyclerView.setAdapter(mShopInfoPicsAdapter);
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (mShopInfoPicsAdapter.getItemViewType(position) == ShopInfoPicsAdapter.TYPE_ADD) {
                            checkPermission(new CheckPermListener() {
                                @Override
                                public void superPermission() {
                                    PhotoPicker.builder()
                                            .setPhotoCount(ShopInfoPicsAdapter.MAX)
                                            .setShowCamera(true)
                                            .setPreviewEnabled(false)
                                            .setSelected(selectedPhotos)
                                            .start(SuggestionFeedbackActivity.this);
                                }
                            }, R.string.camera_updatepics, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE);

                        } else {

                            PhotoPreview.builder()
                                    .setPhotos(selectedPhotos)
                                    .setCurrentItem(position)
                                    .start(SuggestionFeedbackActivity.this);
                        }
                    }
                }));
    }

    int a = 0;//记录上传成功的图片数量

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK &&
                (requestCode == PhotoPicker.REQUEST_CODE || requestCode == PhotoPreview.REQUEST_CODE)) {
            List<String> photos = null;
            if (data != null) {
                photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
            }

            selectedPhotos.clear();
            allPics = "";

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
//                                new AliOss(SuggestionFeedbackActivity.this, AliOss.setPicName("app-unionmerchant-img"), BitmapUtils.compressImage(image)) {//这里设置上传的地址
                                new AliOss(SuggestionFeedbackActivity.this, AliOss.setPicName(AliOss.BucketName), BitmapUtils.compressImage(image)) {//这里设置上传的地址
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
                                            changeList = picsUrl;
                                            SuggestionFeedbackActivity.this.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    for (int i = 0; i < changeList.size(); i++) {//只把新增的图片上传，而不是所有上传
                                                        allPics += changeList.get(i) + "|";
                                                    }
                                                    if (allPics.substring(0, 4).equals("null")) {
                                                        allPics = allPics.substring(4, allPics.length() - 1);//因为默认最前面会有一个null，最后面会有一个逗号
                                                    } else {
                                                        allPics = allPics.substring(0, allPics.length() - 1);//最后面会有一个逗号
                                                    }

                                                    selectedPhotos.addAll(changeList);

                                                    for (int i = 0; i < changeList.size(); i++) {
                                                        L.e("changeList", "i=" + i + ",---" + changeList.get(i));
                                                    }
                                                    mShopInfoPicsAdapter.notifyDataSetChanged();

                                                }
                                            });


                                            a = 0;//重置


                                        } else {
//                                            progressDialog.dismiss();


                                        }
                                    }

                                    @Override
                                    protected void uploadFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                                        a++;
                                    }
                                }.start();

                            }

                        }).start();
                    }

                } else if (requestCode == PhotoPreview.REQUEST_CODE) {

                    for (int i = 0; i < photos.size(); i++) {
                        L.e("选中图片初始路径--预览", "==" + photos.get(i));
                    }
                    final List<String> previewBackList = photos; //预览返回的列表

                    SuggestionFeedbackActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < previewBackList.size(); i++) {
                                allPics += previewBackList.get(i) + "|";
                            }
                            L.e("allpics", "aaaaaaa=" + allPics);
                            try {
                                if (allPics.substring(0, 4).equals("null")) {
                                    allPics = allPics.substring(4, allPics.length() - 1);//因为默认最前面会有一个null，最后面会有一个逗号
                                } else {
                                    allPics = allPics.substring(0, allPics.length() - 1);//最后面会有一个逗号
                                }
                            } catch (StringIndexOutOfBoundsException e) {
                                e.printStackTrace();
                            }


                            selectedPhotos.addAll(previewBackList);

                            mShopInfoPicsAdapter.notifyDataSetChanged();

                        }
                    });

                }

            }


//            new UploadingMultiSelectedPics(mContext, photos, requestCode, null) {
//                @Override
//                protected void setMultiPicsSuccess(List<String> picsUrl) {
//
//                    changeList = picsUrl;
//                    SuggestionFeedbackActivity.this.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            for (int i = 0; i < changeList.size(); i++) {//只把新增的图片上传，而不是所有上传
//                                allPics += changeList.get(i) + "|";
//                            }
//                            if (allPics.substring(0, 4).equals("null")) {
//                                allPics = allPics.substring(4, allPics.length() - 1);//因为默认最前面会有一个null，最后面会有一个逗号
//                            } else {
//                                allPics = allPics.substring(0, allPics.length() - 1);//最后面会有一个逗号
//                            }
//
//                            selectedPhotos.addAll(changeList);
//
//                            for (int i = 0; i < changeList.size(); i++) {
//                                L.e("changeList", "i=" + i + ",---" + changeList.get(i));
//                            }
//                            mShopInfoPicsAdapter.notifyDataSetChanged();
//
//                        }
//                    });
//
//                }
//
//                @Override
//                protected void setMultiPicsFailure() {
//
//                }
//
//                @Override
//                protected void processPreviewPhoto(List<String> photos) {
//
//                    for (int i = 0; i < photos.size(); i++) {
//                        L.e("选中图片初始路径--预览", "==" + photos.get(i));
//                    }
//                    final List<String> previewBackList = photos; //预览返回的列表
//
//                    SuggestionFeedbackActivity.this.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            for (int i = 0; i < previewBackList.size(); i++) {
//                                allPics += previewBackList.get(i) + "|";
//                            }
//                            L.e("allpics", "aaaaaaa=" + allPics);
//                            try {
//                                if (allPics.substring(0, 4).equals("null")) {
//                                    allPics = allPics.substring(4, allPics.length() - 1);//因为默认最前面会有一个null，最后面会有一个逗号
//                                } else {
//                                    allPics = allPics.substring(0, allPics.length() - 1);//最后面会有一个逗号
//                                }
//                            } catch (StringIndexOutOfBoundsException e) {
//                                e.printStackTrace();
//                            }
//
//
//                            selectedPhotos.addAll(previewBackList);
//
//                            mShopInfoPicsAdapter.notifyDataSetChanged();
//
//                        }
//                    });
//                }
//            }.setMultiPicsWithCompress();


        }

    }

    @Override
    public void requestSuccess(int requestTag, JSONObject response, int showLoad) throws JSONException {
        super.requestSuccess(requestTag, response, showLoad);
        switch (requestTag) {
            case RequestAction.TAG_SUGGESTIONFEEDBACK:
                L.e("意见反馈", response.toString());
                MToast.showToast(mContext, "提交成功");
                finish();
                break;
        }
    }

}
