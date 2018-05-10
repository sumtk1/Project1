package com.gloiot.chatsdk.DataBase;

import com.gloiot.chatsdk.bean.ConversationListBean;
import com.gloiot.chatsdk.bean.ImMsgBean;

import java.util.List;

import io.reactivex.Observable;

/**
 * 创建人： zengming on 2017/10/17.
 * 功能：
 */

public interface IUnionDBManager {

    //插入会话列表数据
    Observable<ImMsgBean> insertConversationList(ImMsgBean imMsgBean, int type);
    //查询会话列表数据
    Observable<List<ConversationListBean>> queryConversationList();
    //删除会话列表数据
    Observable<Boolean> deleteConversationList(String sendId);
    Observable<Boolean> deleteConversationList(List<String> sendIds);

    //插入消息数据
    Observable<ImMsgBean> insertChatMsg(ImMsgBean imMsgBean, int type);
    //查询消息数据
    Observable<List<ImMsgBean>> queryChatMsg(String tableName, int number);
    //删除会话列表数据
    Observable<Boolean> deleteChatMsg(String tableName, List<ImMsgBean> deletes);

    //清除所有未读消息数
    Observable<Boolean> CleanAllReadNum();
    //获取所有未读消息数
    Observable<Integer> GetAllReadNum();
    //会话列表某类消息未读消息数置 0
    Observable<Boolean> NoReadNumClean(String sendId);

    //某个消息表的某一条消息已读
    Observable<Boolean> MessageRead(String tableName, String msgId);


}
