package com.gloiot.chatsdk.bean;

/**
 * 作用：会话列表消息实体
 */
public class ConversationListBean {

    // 异业联盟会话类型
    public static final int UNION_IM_TYPE_SYSTEM = 1;  //系统消息
    public static final int UNION_IM_TYPE_CHANPIN= 2;  //产品消息
    public static final int UNION_IM_TYPE_ZHANGDAN = 3;//账单消息
    public static final int UNION_IM_TYPE_DINGDAN = 4; //订单消息
    public static final int UNION_IM_TYPE_ZIZHIRENZHENG = 5; //资质认证消息

    private String sendId;       // 消息发送者ID
    private String receiveId;    // 消息接收者ID
    private String sessionType;  // 会话类型
    private String msgType;      // 消息类型
    private String message;      // 消息体（String类型的json对象）
    private String pushData;     // 接收消息提示内容（String类型的json对象）
    private String sendTime;     // 消息发送时间
    private int noReadNum;       // 未读消息数
    private boolean isNoDisturb; // 消息是否免打扰
    private boolean isTop;       // 消息是否置顶
    private int timestamp;       // 时间戳
    private String extra;        // 附加字段

    public String getSendId() {
        return sendId;
    }

    public void setSendId(String sendId) {
        this.sendId = sendId;
    }

    public String getReceiveId() {
        return receiveId;
    }

    public void setReceiveId(String receiveId) {
        this.receiveId = receiveId;
    }

    public String getSessionType() {
        return sessionType;
    }

    public void setSessionType(String sessionType) {
        this.sessionType = sessionType;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPushData() {
        return pushData;
    }

    public void setPushData(String pushData) {
        this.pushData = pushData;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public int getNoReadNum() {
        return noReadNum;
    }

    public void setNoReadNum(int noReadNum) {
        this.noReadNum = noReadNum;
    }

    public boolean isNoDisturb() {
        return isNoDisturb;
    }

    public void setNoDisturb(boolean noDisturb) {
        isNoDisturb = noDisturb;
    }

    public boolean isTop() {
        return isTop;
    }

    public void setTop(boolean top) {
        isTop = top;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    @Override
    public String toString() {
        return "ConversationListBean{" +
                "sendId='" + sendId + '\'' +
                ", receiveId='" + receiveId + '\'' +
                ", sessionType='" + sessionType + '\'' +
                ", msgType='" + msgType + '\'' +
                ", message='" + message + '\'' +
                ", pushData='" + pushData + '\'' +
                ", sendTime='" + sendTime + '\'' +
                ", noReadNum=" + noReadNum +
                ", isNoDisturb=" + isNoDisturb +
                ", isTop=" + isTop +
                ", timestamp=" + timestamp +
                ", extra='" + extra + '\'' +
                '}';
    }
}
