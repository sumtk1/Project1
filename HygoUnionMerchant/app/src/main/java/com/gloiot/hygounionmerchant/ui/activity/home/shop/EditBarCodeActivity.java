package com.gloiot.hygounionmerchant.ui.activity.home.shop;

import android.Manifest;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.abcaaa.photopicker.PhotoPicker;
import com.gloiot.hygounionmerchant.R;
import com.gloiot.hygounionmerchant.server.network.RequestAction;
import com.gloiot.hygounionmerchant.server.picture.UploadingSelectedPics;
import com.gloiot.hygounionmerchant.ui.BaseActivity;
import com.gloiot.hygounionmerchant.utils.CommonUtils;
import com.gloiot.hygounionmerchant.utils.ConstantUtils;
import com.gloiot.hygounionmerchant.utils.MaxLengthWatcher;
import com.zyd.wlwsdk.adapter.ViewHolder;
import com.zyd.wlwsdk.adapter.abslistview.CommonAdapter;
import com.zyd.wlwsdk.utils.L;
import com.zyd.wlwsdk.utils.MToast;
import com.zyd.wlwsdk.utils.PictureUtlis;
import com.zyd.wlwsdk.utils.SharedPreferencesUtils;
import com.zyd.wlwsdk.widge.MyDialogBuilder;
import com.zyd.wlwsdk.widge.MyNewDialogBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 编辑条码（用于商超类小铺）
 * Created by Dlt on 2017/8/19 16:02
 */
public class EditBarCodeActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.iv_pic)
    ImageView mIvPic;
    @Bind(R.id.tv_uploading_explain)
    TextView mTvUploadingExplain;
    @Bind(R.id.iv_delete_pic)
    ImageView mIvDeletePic;
    @Bind(R.id.tv_goods_bar_code)
    TextView mTvGoodsBarCode;
    @Bind(R.id.tv_small_classify)
    TextView mTvSmallClassify;
    @Bind(R.id.tv_big_classify)
    TextView mTvBigClassify;
    @Bind(R.id.et_goods_title)
    EditText mEtGoodsTitle;
    @Bind(R.id.et_market_price)
    EditText mEtMarketPrice;
    @Bind(R.id.et_supply_price)
    EditText mEtSupplyPrice;

    public static final int REQUEST_CODE_GOODSPIC = 5;

    private String accountType;
    private String barCodeId, goodsPicUrl, barCode, bigType, smallType, bigTypeId, smallTypeId, title, marketPrice, supplyPrice;
    private MyNewDialogBuilder myDialogBuilder;
    private List<String[]> list1 = new ArrayList<>();//大分类
    private List<String[]> list2 = new ArrayList<>();//次级分类
    private int selectPosition1 = -1, selectPosition2 = -1;

    @Override
    public int initResource() {
        return R.layout.activity_edit_bar_code;
    }

    @Override
    public void initData() {
        accountType = SharedPreferencesUtils.getString(mContext, ConstantUtils.SP_ACCOUNTTYPE, "");
        CommonUtils.setTitleBar(this, true, "编辑条码", "");

        Intent intent = getIntent();
        barCodeId = intent.getStringExtra("id");
        goodsPicUrl = intent.getStringExtra("picUrl");
        barCode = intent.getStringExtra("barCode");
        bigType = intent.getStringExtra("bigType");
        smallType = intent.getStringExtra("smallType");
        bigTypeId = intent.getStringExtra("bigTypeId");
        smallTypeId = intent.getStringExtra("smallTypeId");
        title = intent.getStringExtra("title");
        marketPrice = intent.getStringExtra("market");
        supplyPrice = intent.getStringExtra("supply");

        CommonUtils.setDisplayImage(mIvPic, goodsPicUrl, 0, R.drawable.default_image);
        mTvUploadingExplain.setVisibility(View.GONE);
        mTvGoodsBarCode.setText(barCode);
        mTvBigClassify.setText(bigType);
        mTvSmallClassify.setText(smallType);
        mEtGoodsTitle.setText(title);
        mEtGoodsTitle.setSelection(title.length());
        mEtMarketPrice.setText(marketPrice);
        mEtSupplyPrice.setText(supplyPrice);

        mEtGoodsTitle.addTextChangedListener(new MaxLengthWatcher(50, mEtGoodsTitle));//最多50个字符
        setInputListener();
    }

    private void setInputListener() {

        mEtMarketPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //小数点后位数控制
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {//可以适当输入多位，位数会在最后计算时四舍五入
                        s = s.toString().subSequence(0, s.toString().indexOf(".") + 3);//小数点后最多输入2位
                        mEtMarketPrice.setText(s);
                        mEtMarketPrice.setSelection(s.length());
                    }
                }
                //第一位为点
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    mEtMarketPrice.setText(s);
                    mEtMarketPrice.setSelection(2);
                }
                //第一位为零，后面只能输入点
                if (s.toString().startsWith("0") && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        mEtMarketPrice.setText(s.subSequence(0, 1));
                        mEtMarketPrice.setSelection(1);
//                        return;
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mEtSupplyPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //小数点后位数控制
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {//可以适当输入多位，位数会在最后计算时四舍五入
                        s = s.toString().subSequence(0, s.toString().indexOf(".") + 3);//小数点后最多输入2位
                        mEtSupplyPrice.setText(s);
                        mEtSupplyPrice.setSelection(s.length());
                    }
                }
                //第一位为点
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    mEtSupplyPrice.setText(s);
                    mEtSupplyPrice.setSelection(2);
                }
                //第一位为零，后面只能输入点
                if (s.toString().startsWith("0") && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        mEtSupplyPrice.setText(s.subSequence(0, 1));
                        mEtSupplyPrice.setSelection(1);
//                        return;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @OnClick({R.id.iv_pic, R.id.iv_delete_pic, R.id.rl_select_classify, R.id.tv_delete, R.id.tv_confirm})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_pic:
                checkPermission(new BaseActivity.CheckPermListener() {
                    @Override
                    public void superPermission() {
                        selectPic();
                    }
                }, R.string.camera_updatepics, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                break;
            case R.id.iv_delete_pic:
                goodsPicUrl = "";
                CommonUtils.setDisplayImage(mIvPic, goodsPicUrl, 0, R.drawable.ic_shangchuantup);
                mTvUploadingExplain.setVisibility(View.VISIBLE);
                mIvDeletePic.setVisibility(View.GONE);
                break;
            case R.id.rl_select_classify:
                requestHandleArrayList.add(requestAction.getShopCommodityCategory(EditBarCodeActivity.this, "", accountType));
                break;
            case R.id.tv_delete:
                requestHandleArrayList.add(requestAction.shopDeleteBarCode(EditBarCodeActivity.this, barCodeId, accountType));
                break;
            case R.id.tv_confirm:
                if (verifyData()) {

                    myDialogBuilder = MyNewDialogBuilder.getInstance(mContext);
                    myDialogBuilder
                            .withTitie("修改条码")
                            .withCenterContent("确认修改此条码")
                            .withEffects(MyDialogBuilder.SlideTop, MyDialogBuilder.SlideTopDismiss)
                            .setTwoBtn("取消", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    myDialogBuilder.dismiss();
                                }
                            }, "确认", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    myDialogBuilder.dismissNoAnimator();

                                    requestHandleArrayList.add(requestAction.shopEditBarCode(EditBarCodeActivity.this, barCodeId, bigTypeId, smallTypeId,
                                            goodsPicUrl, title, marketPrice, supplyPrice, accountType));

                                }
                            })
                            .show();

                }
                break;
        }
    }

    private void selectPic() {
        PhotoPicker.builder()
                .setPhotoCount(1)
                .setShowCamera(true)
                .setShowGif(false)
                .setPreviewEnabled(true)
                .start(this, REQUEST_CODE_GOODSPIC);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            String photos = null;
            if (data != null) {
                photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS).get(0);
            }
            switch (requestCode) {
                case REQUEST_CODE_GOODSPIC:

                    new UploadingSelectedPics(mContext, photos) {
                        @Override
                        protected void setSinglePicSuccess(String picUrl) {
                            goodsPicUrl = picUrl;
                            EditBarCodeActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mTvUploadingExplain.setVisibility(View.GONE);
                                    PictureUtlis.loadImageViewHolder(mContext, goodsPicUrl, R.drawable.but_tianjia, mIvPic);
                                    mIvDeletePic.setVisibility(View.VISIBLE);
                                }
                            });
                        }

                        @Override
                        protected void setSinglePicFailure() {
                            L.e("上传照片", "上传商品图片失败");
                        }
                    }.setSinglePic();
                    break;

            }
        }

    }

    /**
     * 显示选择类别弹窗
     *
     * @param title     标题
     * @param list      数据集
     * @param isBigType 数据是否来自大类
     */
    private void showSelectCategoryDialog(String title, List<String[]> list, final boolean isBigType) {
        myDialogBuilder = MyNewDialogBuilder.getInstance(mContext);
        final ListView listView = myDialogBuilder.setListViewSingle(mContext);
        final CommonAdapter commonAdapter = new CommonAdapter<String[]>(mContext, R.layout.item_popupwindow_singletext_new, list) {
            @Override
            public void convert(ViewHolder holder, final String[] strings) {
                if (isBigType) {
                    holder.setText(R.id.tv_popsingletext_text, strings[2]);
                } else {
                    holder.setText(R.id.tv_popsingletext_text, strings[1]);
                }
            }
        };
        listView.setAdapter(commonAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isBigType) {
                    selectPosition1 = position;
                } else {
                    selectPosition2 = position;
                }

            }
        });
        myDialogBuilder.setMaxHeight(listView);
        myDialogBuilder
                .withTitie(title)
                .withEffects(MyNewDialogBuilder.SlideTop, MyNewDialogBuilder.SlideTopDismiss)
                .setTwoBtnSpecial("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myDialogBuilder.dismiss();
                        list1.clear();
                        list2.clear();

                    }
                }, "确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myDialogBuilder.dismissNoAnimator();
                        if (isBigType) {
                            requestHandleArrayList.add(requestAction.getShopCommodityCategory(EditBarCodeActivity.this, list1.get(selectPosition1)[0], accountType));
                        } else {

                            bigType = list1.get(selectPosition1)[2];
                            smallType = list2.get(selectPosition2)[1];
                            bigTypeId = list1.get(selectPosition1)[0];
                            smallTypeId = list2.get(selectPosition2)[0];

                            mTvBigClassify.setText(bigType);
                            mTvSmallClassify.setText(smallType);

                        }

                    }
                })
                .show();
    }

    @Override
    public void requestSuccess(int requestTag, JSONObject response, int showLoad) throws JSONException {
        super.requestSuccess(requestTag, response, showLoad);
        switch (requestTag) {
            case RequestAction.TAG_SHOPCOMMODITYCATEGORY://商品类别
                L.e("商品类别", response.toString());

                int num1 = Integer.parseInt(response.getString("类别条数"));
                if (num1 != 0) {
                    JSONArray jsonArray = response.getJSONArray("类别列表");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        String[] a = new String[3];
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                        a[0] = jsonObject.getString("id");
                        a[1] = jsonObject.getString("行业类别");
                        a[2] = jsonObject.getString("类别名称");
                        list1.add(a);
                    }
                    showSelectCategoryDialog("选择商品类别", list1, true);
                } else {
                    int num2 = Integer.parseInt(response.getString("种类条数"));
                    if (num2 != 0) {
                        JSONArray jsonArray = response.getJSONArray("种类列表");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            String[] a = new String[2];
                            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                            a[0] = jsonObject.getString("id");
                            a[1] = jsonObject.getString("种类");
                            list2.add(a);
                        }
                        showSelectCategoryDialog(list1.get(selectPosition1)[2], list2, false);
                    } else {
                        MToast.showToast(mContext, "商品种类为空");
                    }

                }

                break;
            case RequestAction.TAG_SHOPEDITBARCODE://编辑
                MToast.showToast(mContext, "条码修改成功");
                finish();
                break;
            case RequestAction.TAG_SHOPDELETEBARCODE://删除
                MToast.showToast(mContext, "删除条码成功");
                finish();
                break;

        }
    }

    //数据验证
    private Boolean verifyData() {
        bigType = mTvBigClassify.getText().toString().trim();
        smallType = mTvSmallClassify.getText().toString().trim();
        title = mEtGoodsTitle.getText().toString().trim();
        marketPrice = mEtMarketPrice.getText().toString().trim();
        supplyPrice = mEtSupplyPrice.getText().toString().trim();

        if (TextUtils.isEmpty(goodsPicUrl)) {
            MToast.showToast(mContext, "请设置商品图片");
            return false;
        } else if (TextUtils.isEmpty(title)) {
            MToast.showToast(mContext, "请输入商品名称");
            return false;
        } else if (TextUtils.isEmpty(marketPrice)) {
            MToast.showToast(mContext, "请输入市场价");
            return false;
        } else if (Float.parseFloat(marketPrice) == 0) {
            MToast.showToast(mContext, "市场价不能为零，请重新输入");
            return false;
        } else if (TextUtils.isEmpty(supplyPrice)) {
            MToast.showToast(mContext, "请输入供货价");
            return false;
        } else if (Float.parseFloat(supplyPrice) > Float.parseFloat(marketPrice)) {
            MToast.showToast(mContext, "供货价不能大于市场价");
            return false;
        } else {
            return true;
        }

    }
}
