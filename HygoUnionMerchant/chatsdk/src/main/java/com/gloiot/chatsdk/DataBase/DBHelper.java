package com.gloiot.chatsdk.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by zhan on 2017/5/16.
 * 作用：创建数据库,返回的HygoHelper对象用于SQLiteDatabase对象获取
 */

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context, String name, int version) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE if not exists DB_ConversationList (" +
                "sendId VARCHAR PRIMARY KEY, " +    //发送者id，主键
                "sessionType VARCHAR, " +           //聊天类型（单聊，群聊...）
                "msgType VARCHAR, " +               //消息类型
                "message TEXT, " +                  //消息体（String类型的json对象）
                "pushData VARCHAR, " +              //通知消息
                "sendTime VARCHAR, " +              //发送时间
                "noReadNum INTEGER DEFAULT 0, " +   //未读消息数
                "isNoDisturb BOOLEAN, " +           //是否免打扰
                "isTop BOOLEAN, " +                 //是否置顶
                "timestamp INTEGER, " +             //时间戳（用于查询排序）
                "extra TEXT default '')");          //附属消息（没啥用的）
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
