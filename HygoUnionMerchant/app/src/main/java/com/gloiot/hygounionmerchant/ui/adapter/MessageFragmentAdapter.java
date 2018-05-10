package com.gloiot.hygounionmerchant.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gloiot.chatsdk.DataBase.BaseObserver;
import com.gloiot.chatsdk.DataBase.UnionDBManager;
import com.gloiot.chatsdk.MessageManager;
import com.gloiot.chatsdk.bean.ConversationListBean;
import com.gloiot.chatsdk.bean.ImMsgBean;
import com.gloiot.chatsdk.broadcast.BroadcastManager;
import com.gloiot.hygounionmerchant.R;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuAdapter;
import com.zyd.wlwsdk.adapter.recyclerview.OnItemClickListener1;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

/**
 * 创建人： zengming on 2017/10/17.
 * MessageFragment消息列表的适配器
 */
public class MessageFragmentAdapter extends SwipeMenuAdapter<MessageFragmentAdapter.ViewHolder> {

    public Context mContext;
    public List<ConversationListBean> mSocialMessageList;
    private OnItemClickListener1 mOnItemClickListener;
    private String sendId = "";

    public MessageFragmentAdapter(Context context, List<ConversationListBean> list, String sendId) {
        this.mContext = context;
        this.mSocialMessageList = list;
        this.sendId = sendId;
    }

    public void setOnItemClickListener(OnItemClickListener1 onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return mSocialMessageList == null ? 0 : mSocialMessageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if ("系统消息".equals(mSocialMessageList.get(position).getSessionType()))
            return ConversationListBean.UNION_IM_TYPE_SYSTEM;
        else if ("产品消息".equals(mSocialMessageList.get(position).getSessionType()))
            return ConversationListBean.UNION_IM_TYPE_CHANPIN;
        else if ("账单通知".equals(mSocialMessageList.get(position).getSessionType()))
            return ConversationListBean.UNION_IM_TYPE_ZHANGDAN;
        else if ("订单消息".equals(mSocialMessageList.get(position).getSessionType()))
            return ConversationListBean.UNION_IM_TYPE_DINGDAN;
        else if ("资质认证通知".equals(mSocialMessageList.get(position).getSessionType()))
            return ConversationListBean.UNION_IM_TYPE_ZIZHIRENZHENG;
        else
            return ConversationListBean.UNION_IM_TYPE_SYSTEM;
    }

    @Override
    public View onCreateContentView(ViewGroup parent, int viewType) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_fragment, parent, false);
    }

    @Override
    public ViewHolder onCompatCreateViewHolder(View realContentView, int viewType) {
        return new ViewHolder(realContentView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(mSocialMessageList.get(position));
        holder.setOnItemClickListener(mOnItemClickListener);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        RelativeLayout rlImageScope;
        ImageView ivMessageIcon;
        TextView tvMessageTitle;
        TextView tvMessageContent;
        TextView tvMessageTime;
        ImageView ivMessageNoDisturb;
        Badge badge;

        OnItemClickListener1 mOnItemClickListener;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            rlImageScope = (RelativeLayout) itemView.findViewById(R.id.rl_image_scope);
            ivMessageIcon = (ImageView) itemView.findViewById(R.id.iv_message_icon);
            tvMessageTitle = (TextView) itemView.findViewById(R.id.tv_message_title);
            tvMessageContent = (TextView) itemView.findViewById(R.id.tv_message_content);
            tvMessageTime = (TextView) itemView.findViewById(R.id.tv_message_time);
            ivMessageNoDisturb = (ImageView) itemView.findViewById(R.id.iv_message_no_disturb);
            badge = new QBadgeView(mContext).bindTarget(rlImageScope);
//            badge.setGravityOffset(-10, -6, false);
            badge.setBadgeGravity(Gravity.TOP | Gravity.END);
            badge.setBadgeBackgroundColor(Color.parseColor("#FF6D63"));
            badge.setBadgeTextSize(10, true);
            badge.setBadgePadding(6, true);
            badge.setOnDragStateChangedListener(new Badge.OnDragStateChangedListener() {
                @Override
                public void onDragStateChanged(int dragState, Badge badge, View targetView) {
                    if (dragState == STATE_SUCCEED) {
                        UnionDBManager.getInstance(mContext).NoReadNumClean(mSocialMessageList.get(getAdapterPosition()).getSendId())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new BaseObserver<Boolean>() {
                                    @Override
                                    public void onNext(@NonNull Boolean aBoolean) {
                                        if (aBoolean) {
                                            BroadcastManager.getInstance(mContext).sendBroadcast(MessageManager.NEW_MESSAGE,
                                                    new ImMsgBean(-10, "", "", "", "", "", "", "", "", -10, false, ""));
                                        }
                                    }
                                });//清除未读数
                    }
                }
            });
        }

        public void setOnItemClickListener(OnItemClickListener1 onItemClickListener) {
            this.mOnItemClickListener = onItemClickListener;
        }

        public void setData(ConversationListBean bean) {
            if ("系统消息".equals(bean.getSessionType())) {
                this.tvMessageTitle.setText("系统消息");
                Glide.with(mContext).load(R.drawable.ic_tongzhi_xitong).into(ivMessageIcon);
            } else if ("产品通知".equals(bean.getSessionType())) {
                this.tvMessageTitle.setText("产品消息");
                Glide.with(mContext).load(R.drawable.ic_tongzhi_chanpin).into(ivMessageIcon);
            } else if ("账单通知".equals(bean.getSessionType())) {
                this.tvMessageTitle.setText("账单消息");
                Glide.with(mContext).load(R.drawable.ic_tongzhi_zhangdan).into(ivMessageIcon);
            } else if ("订单通知".equals(bean.getSessionType())) {
                this.tvMessageTitle.setText("订单消息");
                Glide.with(mContext).load(R.drawable.ic_tongzhi_digndan).into(ivMessageIcon);
            }

            this.tvMessageContent.setText(bean.getPushData());
            this.tvMessageTime.setText(bean.getSendTime());
//            this.tvMessageTime.setText(TimeUtils.setMessageListTime(conversationBean.getSendTime()));

            if (bean.isNoDisturb()) {
                this.ivMessageNoDisturb.setVisibility(View.VISIBLE);
            } else {
                this.ivMessageNoDisturb.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(bean.getNoReadNum() + "")) {
                this.badge.setBadgeNumber(bean.getNoReadNum());
            }

        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(getAdapterPosition());
            }
        }
    }

}
