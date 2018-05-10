package com.gloiot.hygounionmerchant.ui.activity.message;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.gloiot.chatsdk.DataBase.BaseObserver;
import com.gloiot.chatsdk.DataBase.UnionDBManager;
import com.gloiot.chatsdk.MessageManager;
import com.gloiot.chatsdk.bean.ImMsgBean;
import com.gloiot.chatsdk.broadcast.BroadcastManager;
import com.gloiot.chatsdk.utlis.Constant;
import com.gloiot.hygounionmerchant.R;
import com.gloiot.hygounionmerchant.ui.BaseActivity;
import com.gloiot.hygounionmerchant.ui.activity.mine.bonus.NewExtractRecordDetailActivity;
import com.gloiot.hygounionmerchant.utils.CommonUtils;
import com.gloiot.hygounionmerchant.utils.ConstantUtils;
import com.zyd.wlwsdk.adapter.ViewHolder;
import com.zyd.wlwsdk.adapter.recyclerview.CommonAdapter;
import com.zyd.wlwsdk.utils.L;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;

/**
 * 账单消息
 * Created by Dlt on 2018/1/15 17:54
 */
public class BillsMessageActivity extends BaseActivity {

    @Bind(R.id.ll_noData)
    LinearLayout llNoData;
    @Bind(R.id.rv_system_message)
    RecyclerView rvSystemMessage;

    private CommonAdapter mAdapter;
    private List<ImMsgBean> list;

    @Override
    public int initResource() {
        return R.layout.activity_bills_message;
    }

    @Override
    public void initData() {
        CommonUtils.setTitleBar(this, true, "账单消息", "");

        setAdapter();
        getData(true);

        BroadcastManager.getInstance(mContext).addAction(MessageManager.NEW_MESSAGE, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // TODO 收到系统通知回调
                ImMsgBean imMsgBean = (ImMsgBean) intent.getExtras().getSerializable("data");
                if (Constant.MESSAGE_ZHANGDAN.equals(imMsgBean.getSendid())) {
                    getData(true);
                }
            }
        });
    }

    /**
     * 获取数据
     *
     * @param isNewest 判断是加载还是刷新
     */
    private void getData(final boolean isNewest) {

        int num = 0;
        if (!isNewest) num = list.size();

        final int finalNum = num;
        UnionDBManager.getInstance(mContext, preferences.getString(ConstantUtils.SP_USERACCOUNT, "")).queryChatMsg(Constant.MESSAGE_ZHANGDAN, finalNum)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<List<ImMsgBean>>() {
                    @Override
                    public void onNext(@NonNull List<ImMsgBean> imMsgBeen) {

                        if (isNewest) {
                            list.clear();
                            if (imMsgBeen.size() == 0) {
                                rvSystemMessage.setVisibility(View.GONE);
                                llNoData.setVisibility(View.VISIBLE);
                            } else {
                                rvSystemMessage.setVisibility(View.VISIBLE);
                                llNoData.setVisibility(View.GONE);
                            }
                        }

                        list.addAll(imMsgBeen);

                        if (isNewest)
                            mAdapter.notifyDataSetChanged();
                        else
                            mAdapter.notifyItemRangeInserted(finalNum, list.size());
                    }
                });
    }

    private void setAdapter() {
        list = new ArrayList<>();
        mAdapter = new CommonAdapter<ImMsgBean>(mContext, R.layout.item_message_bill_new, list) {
            @Override
            public void convert(final ViewHolder holder, final ImMsgBean imMsgBean) {
                holder.setText(R.id.tv_system_time, imMsgBean.getSendTime());
                if (imMsgBean.isRead()) {
                    holder.setVisible(R.id.tv_system_read, false);
                } else {
                    holder.setVisible(R.id.tv_system_read, true);
                }
                try {
                    final JSONObject jsonObject = new JSONObject(imMsgBean.getMessage());

                    if (imMsgBean.getMsgtype().contains("图文")) {

                        View view = holder.getConvertView();
                        ImageView ivPic = (ImageView) view.findViewById(R.id.iv_pic);
                        CommonUtils.setDisplayImage(ivPic, jsonObject.getString("img"), 0, R.drawable.bg_shape_gray_efefef);

                        if (jsonObject.getString("title").contains("失败")) {
                            holder.setTextColor(R.id.tv_title, Color.parseColor("#ff436f"));
                        } else {
                            holder.setTextColor(R.id.tv_title, Color.parseColor("#333333"));
                        }
                        holder.setText(R.id.tv_title, jsonObject.getString("title"));
                        holder.setText(R.id.tv_content, jsonObject.getString("content"));
                    } else if (imMsgBean.getMsgtype().contains("文本")) {

                    }

                    holder.getView(R.id.rl_bill).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                L.e("zhangdan--id", "==" + jsonObject.getString("id"));

                                Intent intent = new Intent(BillsMessageActivity.this, NewExtractRecordDetailActivity.class);
                                intent.putExtra("type", jsonObject.getString("billType"));
                                intent.putExtra("id", jsonObject.getString("id"));
                                startActivity(intent);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if (!imMsgBean.isRead()) { // 当前的消息为未读时，将该条消息已读
                                UnionDBManager.getInstance(mContext)
                                        .MessageRead(imMsgBean.getSendid(), imMsgBean.getMsgid())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new BaseObserver<Boolean>() {
                                            @Override
                                            public void onNext(@NonNull Boolean aBoolean) {
                                                if (aBoolean) {
                                                    imMsgBean.setRead(true);
                                                    mAdapter.notifyItemChanged(holder.getmPosition());
                                                }
                                                Log.e("---MessageRead", "---" + imMsgBean.toString());
                                            }
                                        });
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        LinearLayoutManager manager = new LinearLayoutManager(this);
        rvSystemMessage.setLayoutManager(manager);
        rvSystemMessage.setAdapter(mAdapter);

        // 上拉加载数据
        rvSystemMessage.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) { //当停止滚动时
                    if (!recyclerView.canScrollVertically(1)) { //滑动到底部
                        Log.e("---addOnScrollListener", "---滑动到底部");
                        if (list.size() != 0 && list.size() % 20 == 0) { //数据不为空
                            Log.e("---addOnScrollListener", "---可以加载数据");
                            getData(false);
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        //清除系统通知的未读数
        UnionDBManager.getInstance(mContext).NoReadNumClean(Constant.MESSAGE_ZHANGDAN)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<Boolean>() {
                    @Override
                    public void onNext(@NonNull Boolean aBoolean) {
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BroadcastManager.getInstance(mContext).destroy(MessageManager.NEW_MESSAGE);
    }

}
