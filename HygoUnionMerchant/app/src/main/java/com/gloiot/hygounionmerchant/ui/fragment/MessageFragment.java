package com.gloiot.hygounionmerchant.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gloiot.chatsdk.DataBase.BaseObserver;
import com.gloiot.chatsdk.DataBase.UnionDBManager;
import com.gloiot.chatsdk.DataBase.Widget.DataChange;
import com.gloiot.chatsdk.MessageManager;
import com.gloiot.chatsdk.bean.ConversationListBean;
import com.gloiot.chatsdk.bean.ImMsgBean;
import com.gloiot.chatsdk.broadcast.BroadcastManager;
import com.gloiot.hygounionmerchant.R;
import com.gloiot.hygounionmerchant.ui.BaseFragment;
import com.gloiot.hygounionmerchant.ui.activity.MainActivity;
import com.gloiot.hygounionmerchant.ui.activity.message.BillsMessageActivity;
import com.gloiot.hygounionmerchant.ui.activity.message.SystemMessageActivity;
import com.gloiot.hygounionmerchant.ui.adapter.MessageFragmentAdapter;
import com.gloiot.hygounionmerchant.utils.ConstantUtils;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.zyd.wlwsdk.adapter.recyclerview.OnItemClickListener1;
import com.zyd.wlwsdk.utils.L;
import com.zyd.wlwsdk.utils.MToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 * Created by Dlt on 2017/8/14 17:16
 */
public class MessageFragment extends BaseFragment {
    public static final String TAG = MessageFragment.class.getSimpleName();

    //    @Bind(R.id.recycler_message)
//    SwipeMenuRecyclerView mSwipeMenuRecyclerView;
//    @Bind(R.id.ll_noData)
//    LinearLayout llNoData;
    private SwipeMenuRecyclerView mSwipeMenuRecyclerView;
    private LinearLayout llNoData;
    @Bind(R.id.tv_message_network)
    TextView tvMessageNetwork;
    @Bind(R.id.tv_message_add)
    Button tvMessageAdd;

    private MessageFragmentAdapter mMessageAdapter;
    private List<ConversationListBean> mData = new ArrayList<>();

    public static Fragment newInstance(int position) {
        MessageFragment fragment = new MessageFragment();
        return fragment;
    }

    public MessageFragment() {

    }

    @Override
    public void onResume() {
        super.onResume();
        if (check_login()) {
            getData();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        mSwipeMenuRecyclerView = (SwipeMenuRecyclerView) view.findViewById(R.id.recycler_message);
        llNoData = (LinearLayout) view.findViewById(R.id.ll_noData);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
        BroadcastManager.getInstance(mContext).addAction(MessageManager.LINK_CHANGED, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (check_login()) {
                    try {
                        switch (intent.getStringExtra("data")) {
                            case MessageManager.LINK_CHANGED_SUCCEED:
                                tvMessageNetwork.setVisibility(View.GONE);
                                break;
                            case MessageManager.LINK_CHANGED_FAULT:
                                tvMessageNetwork.setVisibility(View.VISIBLE);
                                break;
                            case MessageManager.LINK_CHANGED_DONW:
                                tvMessageNetwork.setVisibility(View.VISIBLE);
                                break;
                            default:
                        }
                    } catch (Exception e) {

                    }
                }
            }
        });
        BroadcastManager.getInstance(mContext).addAction(MessageManager.NEW_MESSAGE, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ((MainActivity) mContext).refreshBadge(); // 更新MainActivity的未读消息展示

                getData();
                L.e("test", "---------0");
            }
        });
    }

    private void initData() {
        final int[] i = {0};
        tvMessageAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i[0]++;
                if (i[0] % 2 == 0) {
                    setData1();
                } else {
                    setData2();
                }
            }
        });

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        mSwipeMenuRecyclerView.setLayoutManager(manager);// 布局管理器。
        mSwipeMenuRecyclerView.setHasFixedSize(true);// 如果Item够简单，高度是确定的，打开FixSize将提高性能。
        mSwipeMenuRecyclerView.setItemAnimator(new DefaultItemAnimator());// 设置Item默认动画，加也行，不加也行。

        mMessageAdapter = new MessageFragmentAdapter(mContext, mData, preferences.getString(ConstantUtils.SP_USERACCOUNT, ""));
        mMessageAdapter.setOnItemClickListener(onItemClickListener);
        mSwipeMenuRecyclerView.setAdapter(mMessageAdapter);

        getData();
    }

    private void setData1() {
        final String data = "{\n" +
                "    \"msgId\":\"uuid" + UUID.randomUUID().toString() + "\",\n" +
                "    \"sendId\":\"001\",\n" +
                "    \"receiveId\":\"13430629179\",\n" +
                "    \"sessionType\":\"系统通知\",\n" +
                "    \"msgType\":\"文本\",\n" +
                "    \"message\":{\n" +
                "        \"title\":\"111私聊\",\n" +
                "        \"func\":\"system_xiangqing\",\n" +
                "        \"img\":\"https://image.baidu.com/search/detail?ct=503316480&z=0&ipn=false&word=%E7%BE%8E%E5%A5%B3%E5%9B%BE%E7%89%87&step_word=&hs=0&pn=10&spn=0&di=138297083430&pi=0&rn=1&tn=baiduimagedetail&is=0%2C0&istype=0&ie=utf-8&oe=utf-8&in=&cl=2&lm=-1&st=undefined&cs=2656881601%2C2258550211&os=2502486083%2C4019681401&simid=3329653517%2C426630896&adpicid=0&lpn=0&ln=3888&fr=&fmq=1495424694315_R&fm=&ic=undefined&s=undefined&se=&sme=&tab=0&width=undefined&height=undefined&face=undefined&ist=&jit=&cg=girl&bdtype=0&oriquery=&objurl=http%3A%2F%2Fd.5857.com%2Fxgmn_150416%2Fdesk_007.jpg&fromurl=ippr_z2C%24qAzdH3FAzdH3F1jfh_z%26e3Bnlb0_z%26e3Bv54AzdH3FowssAzdH3F9d0bm_z%26e3Bip4s&gsm=0&rpstart=0&rpnum=0\",\n" +
                "        \"content\":\"文本内容\",\n" +
                "        \"url\":\"https://image.baidu.com/search/detail?ct=503316480&z=0&ipn=false&word=%E7%BE%8E%E5%A5%B3%E5%9B%BE%E7%89%87&step_word=&hs=0&pn=11&spn=0&di=31828065860&pi=0&rn=1&tn=baiduimagedetail&is=0%2C0&istype=0&ie=utf-8&oe=utf-8&in=&cl=2&lm=-1&st=undefined&cs=3701833110%2C928480625&os=2419732071%2C3745816240&simid=0%2C0&adpicid=0&lpn=0&ln=3888&fr=&fmq=1495424694315_R&fm=&ic=undefined&s=undefined&se=&sme=&tab=0&width=undefined&height=undefined&face=undefined&ist=&jit=&cg=girl&bdtype=11&oriquery=&objurl=http%3A%2F%2Fd.5857.com%2Fdfqz_170427%2Fdesk_001.jpg&fromurl=ippr_z2C%24qAzdH3FAzdH3Fooo_z%26e3Bcbc0_z%26e3Bv54AzdH3Ff3kzAzdH3Fmbl9l_z%26e3Bip4s&gsm=0&rpstart=0&rpnum=0\"\n" +
                "    },\n" +
                "    \"pushData\":\"通知内容123" + 11111 + "\",\n" +
                "    \"sendTime\":\"2017-10-18 23:10:30\"\n" +
                "}";

        try {
            UnionDBManager.getInstance(mContext).insertChatMsg((new DataChange()).JsonToSystemBean(new JSONObject(data)), 0)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new BaseObserver<ImMsgBean>() {
                        @Override
                        public void onNext(@NonNull ImMsgBean imMsgBean) {
                            BroadcastManager.getInstance(mContext).sendBroadcast(MessageManager.NEW_MESSAGE, imMsgBean);
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setData2() {
        final String data = "{\n" +
                "    \"msgId\":\"uuid" + UUID.randomUUID().toString() + "\",\n" +
                "    \"sendId\":\"002\",\n" +
                "    \"receiveId\":\"13430629179\",\n" +
                "    \"sessionType\":\"产品通知\",\n" +
                "    \"msgType\":\"图文\",\n" +
                "    \"message\":{\n" +
                "        \"title\":\"新产品上线了\",\n" +
                "        \"func\":\"changpin_xiangqing\",\n" +
                "        \"img\":\"https://image.baidu.com/search/detail?ct=503316480&z=0&ipn=false&word=%E7%BE%8E%E5%A5%B3%E5%9B%BE%E7%89%87&step_word=&hs=0&pn=10&spn=0&di=138297083430&pi=0&rn=1&tn=baiduimagedetail&is=0%2C0&istype=0&ie=utf-8&oe=utf-8&in=&cl=2&lm=-1&st=undefined&cs=2656881601%2C2258550211&os=2502486083%2C4019681401&simid=3329653517%2C426630896&adpicid=0&lpn=0&ln=3888&fr=&fmq=1495424694315_R&fm=&ic=undefined&s=undefined&se=&sme=&tab=0&width=undefined&height=undefined&face=undefined&ist=&jit=&cg=girl&bdtype=0&oriquery=&objurl=http%3A%2F%2Fd.5857.com%2Fxgmn_150416%2Fdesk_007.jpg&fromurl=ippr_z2C%24qAzdH3FAzdH3F1jfh_z%26e3Bnlb0_z%26e3Bv54AzdH3FowssAzdH3F9d0bm_z%26e3Bip4s&gsm=0&rpstart=0&rpnum=0\",\n" +
                "        \"content\":\"文本内容\",\n" +
                "        \"url\":\"https://image.baidu.com/search/detail?ct=503316480&z=0&ipn=false&word=%E7%BE%8E%E5%A5%B3%E5%9B%BE%E7%89%87&step_word=&hs=0&pn=11&spn=0&di=31828065860&pi=0&rn=1&tn=baiduimagedetail&is=0%2C0&istype=0&ie=utf-8&oe=utf-8&in=&cl=2&lm=-1&st=undefined&cs=3701833110%2C928480625&os=2419732071%2C3745816240&simid=0%2C0&adpicid=0&lpn=0&ln=3888&fr=&fmq=1495424694315_R&fm=&ic=undefined&s=undefined&se=&sme=&tab=0&width=undefined&height=undefined&face=undefined&ist=&jit=&cg=girl&bdtype=11&oriquery=&objurl=http%3A%2F%2Fd.5857.com%2Fdfqz_170427%2Fdesk_001.jpg&fromurl=ippr_z2C%24qAzdH3FAzdH3Fooo_z%26e3Bcbc0_z%26e3Bv54AzdH3Ff3kzAzdH3Fmbl9l_z%26e3Bip4s&gsm=0&rpstart=0&rpnum=0\"\n" +
                "    },\n" +
                "    \"pushData\":\"查看新产品" + 11111 + "\",\n" +
                "    \"sendTime\":\"2017-10-19 23:10:30\"\n" +
                "}";

        try {
            UnionDBManager.getInstance(mContext).insertChatMsg((new DataChange()).JsonToSystemBean(new JSONObject(data)), 0)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new BaseObserver<ImMsgBean>() {
                        @Override
                        public void onNext(@NonNull ImMsgBean imMsgBean) {
                            BroadcastManager.getInstance(mContext).sendBroadcast(MessageManager.NEW_MESSAGE, imMsgBean);
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取数据
     */
    private void getData() {
        if (check_login()) {
            try {
                llNoData.setVisibility(View.GONE);
                mSwipeMenuRecyclerView.setVisibility(View.VISIBLE);

                L.e("test", "---------1");

                //20171106 本期先把消息模块屏蔽掉
                //            llNoData.setVisibility(View.VISIBLE);
                //            mSwipeMenuRecyclerView.setVisibility(View.GONE);

                mData.clear();
                ConversationListBean bean1 = new ConversationListBean();
                bean1.setSendId("");
                bean1.setReceiveId("");
                bean1.setSessionType("系统消息");
                bean1.setPushData("暂无消息");
                bean1.setExtra("");
                bean1.setMessage("");
                bean1.setSendTime("");
                mData.add(0, bean1);

                ConversationListBean bean2 = new ConversationListBean();
                bean2.setSendId("");
                bean2.setReceiveId("");
//                bean2.setSessionType("产品通知");
                bean2.setSessionType("账单通知");
                bean2.setPushData("暂无消息");
                bean2.setExtra("");
                bean2.setMessage("");
                bean2.setSendTime("");
                mData.add(1, bean2);

//                ConversationListBean bean3 = new ConversationListBean();
//                bean3.setSendId("");
//                bean3.setReceiveId("");
//                bean3.setSessionType("账单通知");
//                bean3.setPushData("暂无消息");
//                bean3.setExtra("");
//                bean3.setMessage("");
//                bean3.setSendTime("");
//                mData.add(2, bean3);

//                ConversationListBean bean4 = new ConversationListBean();
//                bean4.setSendId("");
//                bean4.setReceiveId("");
//                bean4.setSessionType("订单通知");
//                bean4.setPushData("暂无消息");
//                bean4.setExtra("");
//                bean4.setMessage("");
//                bean4.setSendTime("");
//                mData.add(3, bean4);

                //现有的需求没有这一种类型的消息，归为系统消息
                //            ConversationListBean bean5 = new ConversationListBean();
                //            bean5.setSendId("");
                //            bean5.setReceiveId("");
                //            bean5.setSessionType("资质认证通知");
                //            bean5.setPushData("暂无消息");
                //            bean5.setExtra("");
                //            bean5.setMessage("");
                //            bean5.setSendTime("");
                //            mData.add(4, bean5);

                L.e("test", "---------2");

                UnionDBManager.getInstance(mContext, preferences.getString(ConstantUtils.SP_USERACCOUNT, ""))
                        .queryConversationList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<List<ConversationListBean>>() {
                            @Override
                            public void accept(List<ConversationListBean> conversationListBeen) throws Exception {

                                L.e("00000000", conversationListBeen.toString());
                                for (final ConversationListBean bean : conversationListBeen) {
                                    switch (bean.getSessionType()) {
                                        case "系统消息":
                                            mData.remove(0);
                                            mData.add(0, bean);
                                            break;
//                                        20171130 本期先隐藏这几种消息类型
//                                        case "产品通知":
                                        case "账单通知":
                                            mData.remove(1);
                                            mData.add(1, bean);
                                            break;
//                                        case "账单通知":
//                                            mData.remove(2);
//                                            mData.add(2, bean);
//                                            break;
//                                        case "订单通知":
//                                            mData.remove(3);
//                                            mData.add(3, bean);
//                                            break;

                                        //归为系统消息
//                                    case "资质认证通知":
//                                        mData.remove(4);
//                                        mData.add(4, bean);
//                                        break;
                                    }
                                }
                                mMessageAdapter.notifyDataSetChanged();
                                L.e("test", "---------3");
                            }
                        });
            } catch (Exception e) {
                L.e("Exception", "" + e);
            }
        } else {
            llNoData.setVisibility(View.VISIBLE);
            mSwipeMenuRecyclerView.setVisibility(View.GONE);
        }
        mMessageAdapter.notifyDataSetChanged();
    }

    /**
     * 条目点击监听
     */
    private OnItemClickListener1 onItemClickListener = new OnItemClickListener1() {
        @Override
        public void onItemClick(final int position) {
            //清除未读数
            UnionDBManager.getInstance(mContext).NoReadNumClean(mData.get(position).getSendId())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new BaseObserver<Boolean>() {
                        @Override
                        public void onNext(@NonNull Boolean aBoolean) {
                            if (aBoolean) {
                                mData.get(position).setNoReadNum(0);
                                mMessageAdapter.notifyItemChanged(position);
                            }

                            /* 清除完后，去更新 */
                            ((MainActivity) mContext).refreshBadge();
                        }
                    });
            switch (position) {
                case 0:
                    startActivity(new Intent(mContext, SystemMessageActivity.class));
                    break;
                case 1:
//                    MToast.showToast(mContext, "产品消息");
                    startActivity(new Intent(mContext, BillsMessageActivity.class));
                    break;
                case 2:
                    MToast.showToast(mContext, "账单消息");
                    break;
                case 3:
                    MToast.showToast(mContext, "订单消息");
                    break;
                case 4:
                    MToast.showToast(mContext, "资质认证消息");
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 刷新页面
     */
    public void refresh() {
        getData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        BroadcastManager.getInstance(mContext).destroy(MessageManager.NEW_MESSAGE);
        ButterKnife.unbind(this);
    }
}
