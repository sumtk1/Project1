package com.gloiot.chatsdk;

import android.content.Context;

import com.gloiot.chatsdk.DataBase.BaseObserver;
import com.gloiot.chatsdk.DataBase.UnionDBManager;
import com.gloiot.chatsdk.DataBase.Widget.DataChange;
import com.gloiot.chatsdk.bean.ImMsgBean;
import com.gloiot.chatsdk.broadcast.BroadcastManager;

import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;


/**
 * Created by jinzlin on 17/5/18.
 * 消息管理
 */

public class MessageManager {

    private static MessageManager instance;
    private Context context;

    public static final String NEW_MESSAGE = "UNION_NEW_MESSAGE";
    public static final String LINK_CHANGED = "UNION_LINK_CHANGED";

    public static final String LINK_CHANGED_SUCCEED = "UNION_LINK_CHANGED_SUCCEED";
    public static final String LINK_CHANGED_FAULT = "UNION_LINK_CHANGED_FAULT";
    public static final String LINK_CHANGED_DONW = "UNION_LINK_CHANGED_DONW";

    private MessageManager(Context context) {
        this.context = context;
    }

    public static MessageManager getInstance(Context context) {
        if (instance == null) {
            instance = new MessageManager(context);
        }
        return instance;
    }
    private JSONObject jsonObject;

    public synchronized void setMessage(final String data) {
        try {
            jsonObject = new JSONObject(data);

            UnionDBManager.getInstance(context).insertChatMsg((new DataChange()).JsonToSystemBean(jsonObject), 0)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new BaseObserver<ImMsgBean>(){
                        @Override
                        public void onNext(@NonNull ImMsgBean imMsgBean) {
                            BroadcastManager.getInstance(context).sendBroadcast(MessageManager.NEW_MESSAGE, imMsgBean);
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
