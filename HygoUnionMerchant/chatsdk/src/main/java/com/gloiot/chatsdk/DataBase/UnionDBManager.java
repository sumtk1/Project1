package com.gloiot.chatsdk.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.gloiot.chatsdk.DataBase.Widget.DataChange;
import com.gloiot.chatsdk.bean.ConversationListBean;
import com.gloiot.chatsdk.bean.ImMsgBean;
import com.zyd.wlwsdk.utils.L;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;

/**
 * 创建人： zengming on 2017/10/17.
 * 功能：异业联盟数据库管理类
 */

public class UnionDBManager implements IUnionDBManager {

    private static final String TAG = "UnionDBManager";
    private static final String SQL_LITE_NAME = "UnionIm"; // 数据库名
    private static Context mContext;
    private static UnionDBManager instance;
    private static DBHelper unionHelper;
    private static SQLiteDatabase sqLiteDatabase;
    private static String path = null;


    private UnionDBManager(Context context) {
        mContext = context.getApplicationContext();
    }

    public static UnionDBManager getInstance(Context context) {
        if (instance == null) {
            instance = new UnionDBManager(context);
        }
        return instance;
    }

    public static UnionDBManager  getInstance(Context context, String userId) {
        if (instance == null) {
            instance = new UnionDBManager(context);
        }
        //创建数据库文件目录等
        if (path == null) {
            instance.getDataBasePath(mContext, userId, SQL_LITE_NAME);
        }
        //实例化DBHelper对象
        if (unionHelper == null) {
            unionHelper = new DBHelper(mContext, path, 1);
        }
        //return SQLiteDatabase 对象
        if (sqLiteDatabase == null) {
            sqLiteDatabase = unionHelper.getWritableDatabase();
        }
        return instance;
    }

    /**
     * 创建指定目录数据库
     *
     * @param context
     * @param name
     * @return
     */
    private synchronized String getDataBasePath(Context context, String userId, String name) {
        //用户ID为空时进入默认名称的文件夹
        if (userId == null || userId.length() == 0)
            userId = "default";

        File file = new File(context.getFilesDir().getAbsolutePath() + File.separator + userId + File.separator + name);

        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        L.e("数据库位置", context.getFilesDir().getAbsolutePath() + File.separator + userId + File.separator + name);
        path = file.getAbsolutePath();
        return path;
    }


    /**
     * 插入数据（userId主键存在则更新）
     *
     * @param imMsgBean
     * @param type                消息接受/消息发送    0为接收、1为发送
     */
    @Override
    public synchronized Observable<ImMsgBean> insertConversationList(final ImMsgBean imMsgBean, final int type) {
        return Observable.create(new ObservableOnSubscribe<ImMsgBean>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<ImMsgBean> e) throws Exception {
                ConversationListBean bean = new DataChange().MessageToConversationList(imMsgBean);
                ContentValues contentValues = new ContentValues();

                //发送消息插入接受者id行，接收消息插入发送者id行
                // （反正就是会话列表都按照聊天对象更新数据库存储数据）
                String rowName = "";
                if (type == 0) {
                    rowName = bean.getSendId();
                    contentValues.put("sendId", rowName);
                } else if (type == 1) {
                    rowName = bean.getReceiveId();
                    contentValues.put("sendId", rowName);
                }

                contentValues.put("sessionType", bean.getSessionType());
                contentValues.put("msgType", bean.getMsgType());
                contentValues.put("message", bean.getMessage());
                contentValues.put("pushData", bean.getPushData());
                contentValues.put("sendTime", bean.getSendTime());
                contentValues.put("noReadNum", bean.getNoReadNum());
                contentValues.put("isNoDisturb", bean.isNoDisturb());
                contentValues.put("isTop", bean.isTop());
                contentValues.put("timestamp", bean.getTimestamp());
                if (bean.getExtra() != null)
                    contentValues.put("extra", bean.getExtra());

                //说明：该方法做了判断表是否存在操作，移植时可自行修改
                //判断本地数据是否存在  sendId
                boolean flag = false;
                Cursor cursor = sqLiteDatabase.rawQuery("SELECT sendId FROM DB_ConversationList WHERE sendId == ?", new String[]{rowName});
                while (cursor.moveToNext()) {
                    flag = true;
                    L.e(TAG, "insertConversationList: 2");
                    break;
                }
                cursor.close();

                //插入新数据时默认为   无免打扰、不置顶，未读消息数为1
                // 更新数据时不更新    是否免打扰、是否置顶；但刷新温度消息数
                if (flag) {
                    L.e("----", "数据存在");

                    //TODO 这里需要后期根据实际情况修改（发送消息时候本地的操作逻辑）
                    //是否修改未读消息数
                    if (type == 0)
                        contentValues.put("noReadNum", NoReadNumAddOne(rowName));

                    long i = sqLiteDatabase.update("DB_ConversationList", contentValues, "sendId=?", new String[]{rowName});
                    if (i > 0) {
                        L.e(TAG, "insertConversationList: 3 1");
                        imMsgBean.setMsgtype(imMsgBean.getMsgtype() + "_" + type);
                        e.onNext(imMsgBean);
                        e.onComplete();
                    } else {
                        L.e(TAG, "insertConversationList: 3 2");
                        e.onError(new Throwable("数据存在,更新失败"));
                        e.onComplete();
                    }
                } else {
                    L.e("----", "数据不存在");
                    //插入时才操作
                    contentValues.put("isNoDisturb", 0);
                    contentValues.put("noReadNum", 1);
                    contentValues.put("isTop", 0);

                    long i = sqLiteDatabase.insert("DB_ConversationList", null, contentValues);
                    if (i > 0) {
                        L.e(TAG, "insert_数据不存在 : SUCCESS");
                        imMsgBean.setMsgtype(imMsgBean.getMsgtype() + "_" + type);
                        e.onNext(imMsgBean);
                        e.onComplete();
                    } else {
                        L.e(TAG, "insert_数据不存在 : FAIL");
                        e.onError(new Throwable("数据不存在,插入失败"));
                        e.onComplete();
                    }
                }
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 查询会话列表数据
     *
     * @return
     */
    @Override
    public Observable<List<ConversationListBean>> queryConversationList() {
        return Observable.create(new ObservableOnSubscribe<List<ConversationListBean>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<ConversationListBean>> e) throws Exception {
                List<ConversationListBean> mData = new ArrayList<>();
                ConversationListBean bean;

                Cursor cursor = sqLiteDatabase.query("DB_ConversationList", new String[]{"sendId", "sessionType", "msgType", "message",
                        "pushData", "sendTime", "isNoDisturb", "noReadNum", "isTop", "timestamp", "extra"}, null, null, null, null, "isTop desc, timeStamp desc");
                System.out.println("查到的数据为：");
                while (cursor.moveToNext()) {
                    bean = new ConversationListBean();
                    bean.setSendId(cursor.getString(cursor.getColumnIndex("sendId")));
                    bean.setSessionType(cursor.getString(cursor.getColumnIndex("sessionType")));
                    bean.setMsgType(cursor.getString(cursor.getColumnIndex("msgType")));
                    bean.setMessage(cursor.getString(cursor.getColumnIndex("message")));
                    bean.setPushData(cursor.getString(cursor.getColumnIndex("pushData")));
                    bean.setSendTime(cursor.getString(cursor.getColumnIndex("sendTime")));
                    bean.setNoReadNum(cursor.getInt(cursor.getColumnIndex("noReadNum")));
                    bean.setNoDisturb(cursor.getString(cursor.getColumnIndex("isNoDisturb")).equals("1"));
                    bean.setTop(cursor.getString(cursor.getColumnIndex("isTop")).equals("1"));
                    bean.setTimestamp(cursor.getInt(cursor.getColumnIndex("timestamp")));
                    bean.setExtra(cursor.getString(cursor.getColumnIndex("extra")));

                    System.out.println("-->" + bean.toString());
                    mData.add(bean);
                }
                cursor.close();
                e.onNext(mData);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public synchronized Observable<Boolean> deleteConversationList(final String sendId) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Boolean> e) throws Exception {
                long i = sqLiteDatabase.delete("DB_ConversationList", "sendId = ?", new String[]{sendId});
                if (i > 0) {
                    e.onNext(true);
                } else {
                    e.onNext(false);
                }
                e.onComplete();
                Log.e(TAG, "删除sendId为" + sendId + "的数据");
            }
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public synchronized Observable<Boolean> deleteConversationList(final List<String> sendIds) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Boolean> e) throws Exception {
                for(String sendId : sendIds) {
                    long i = sqLiteDatabase.delete("DB_ConversationList", "sendId = ?", new String[]{sendId});
                    if (i > 0) {
                        e.onNext(true);
                    } else {
                        e.onNext(false);
                    }
                    e.onComplete();
                    Log.e(TAG, "删除sendId为" + sendId + "的数据");
                }
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 插入消息
     *
     * @param imMsgBean
     * @param type                消息接受/消息发送    0为接收、1为发送
     */
    @Override
    public synchronized Observable<ImMsgBean> insertChatMsg(final ImMsgBean imMsgBean, final int type) {
        return Observable.create(new ObservableOnSubscribe<ImMsgBean>() {
            @Override
            public void subscribe(@NonNull final ObservableEmitter<ImMsgBean> e) throws Exception {
                if (imMsgBean == null)
                    return;

                String tableName = "";
                ContentValues contentValues = new ContentValues();

                if (type == 0) {
                    //单聊创建表
                    tableName = "DB_" + imMsgBean.getSendid();
                } else if (type == 1) {
                    //单聊创建表
                    tableName = "DB_" + imMsgBean.getReceiveid();
                }
                CreateChatTable(tableName);

                contentValues.put("msgId", imMsgBean.getMsgid());
                contentValues.put("sendId", imMsgBean.getSendid());
                contentValues.put("receiveId", imMsgBean.getReceiveid());
                contentValues.put("sessionType", imMsgBean.getSessiontype());
                contentValues.put("msgType", imMsgBean.getMsgtype() + "_" + type);
                contentValues.put("message", imMsgBean.getMessage());
                contentValues.put("pushData", imMsgBean.getPushdata());
                contentValues.put("sendTime", imMsgBean.getSendTime());
                contentValues.put("isRead", false); // 接收到消息，默认为未读
                if (imMsgBean.getExtra() != null)
                    contentValues.put("extra", imMsgBean.getExtra());

                //消息为发送消息时给予默认的发送成功失败
                //0为发送中，1为发送成功, 2为发送失败。
                // 消息发送后，移动端收到后台的信息接受成功提示后，通过方法修改messageState值，修改为1
                if (type == 1) {
                    contentValues.put("messageState", 0);
                }

                long i = sqLiteDatabase.insert(tableName, null, contentValues);
                if (i > 0) {
                    insertConversationList(imMsgBean, type).subscribe(new BaseObserver<ImMsgBean>(){
                        @Override
                        public void onNext(@NonNull ImMsgBean imMsgBean) {
                            e.onNext(imMsgBean);
                            e.onComplete();
                        }
                    });
                }
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 查询消息
     * @param tableName   查询的表
     * @param number      为当前已有条数
     * @return
     */
    @Override
    public Observable<List<ImMsgBean>> queryChatMsg(final String tableName, final int number) {
        return Observable.create(new ObservableOnSubscribe<List<ImMsgBean>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<ImMsgBean>> e) throws Exception {
                CreateChatTable("DB_" + tableName);

                List<ImMsgBean> messages = new ArrayList<>();
                ImMsgBean bean;

                Cursor cursor = sqLiteDatabase.query("DB_" + tableName, new String[]{"id", "msgId", "sendId", "receiveId", "sessionType",
                        "msgType", "message", "pushData", "sendTime", "messageState", "isRead", "extra"}, null, null, null, null, "sendTime desc, id desc", number + ", 20");

                System.out.println("查到的数据为：");
                while (cursor.moveToNext()) {
                    bean = new ImMsgBean();
                    bean.setId(cursor.getInt(cursor.getColumnIndex("id")));
                    bean.setMsgid(cursor.getString(cursor.getColumnIndex("msgId")));
                    bean.setSendid(cursor.getString(cursor.getColumnIndex("sendId")));
                    bean.setReceiveid(cursor.getString(cursor.getColumnIndex("receiveId")));
                    bean.setSessiontype(cursor.getString(cursor.getColumnIndex("sessionType")));
                    bean.setMsgtype(cursor.getString(cursor.getColumnIndex("msgType")));
                    bean.setMessage(cursor.getString(cursor.getColumnIndex("message")));
                    bean.setPushdata(cursor.getString(cursor.getColumnIndex("pushData")));
                    bean.setSendTime(cursor.getString(cursor.getColumnIndex("sendTime")));
                    bean.setMessageState(cursor.getInt(cursor.getColumnIndex("messageState")));
                    bean.setRead(cursor.getString(cursor.getColumnIndex("isRead")).equals("1"));
                    bean.setExtra(cursor.getString(cursor.getColumnIndex("extra")));

                    System.out.println("-->" + bean.toString());
                    messages.add(bean);
                }
                cursor.close();
                e.onNext(messages);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<Boolean> deleteChatMsg(final String tableName, final List<ImMsgBean> deletes) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Boolean> e) throws Exception {
                for(ImMsgBean bean : deletes) {
                    long i = sqLiteDatabase.delete("DB_" + tableName, "id = ?", new String[]{bean.getId() + ""});
                    if (i > 0) {
                        e.onNext(true);
                    } else {
                        e.onNext(false);
                    }
                    e.onComplete();
                    Log.e(TAG, "删除id为" + bean.getId() + "的数据");
                }
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 清除所有未读数
     */
    @Override
    public synchronized Observable<Boolean> CleanAllReadNum() {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Boolean> e) throws Exception {
                ContentValues contentValues = new ContentValues();
                contentValues.put("noReadNum", 0);
                long i = sqLiteDatabase.update("DB_ConversationList", contentValues, "noReadNum<>?", new String[]{"0"});
                if (i > 0) {
                    e.onNext(true);
                } else {
                    e.onNext(false);
                }
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 获取所有未读数
     *
     * @return
     */
    @Override
    public Observable<Integer> GetAllReadNum() {
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
                int num = 0;
                Cursor cursor = sqLiteDatabase.query("DB_ConversationList", new String[]{"sum(noReadNum) as num"}, null, null, null, null, null);
                while (cursor.moveToNext()) {
                    num = cursor.getInt(cursor.getColumnIndex("num"));

                }
                cursor.close();
                e.onNext(num);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 消息未读消息数置 0
     *
     * @param sendId
     */
    @Override
    public synchronized Observable<Boolean> NoReadNumClean(final String sendId) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Boolean> e) throws Exception {
                ContentValues contentValues = new ContentValues();
                contentValues.put("noReadNum", 0);
                long i = sqLiteDatabase.update("DB_ConversationList", contentValues, "sendId=?", new String[]{sendId});
                if (i > 0) {
                    e.onNext(true);
                } else {
                    e.onNext(false);
                }
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 会话列表某类消息未读消息数+1
     *
     * @param sendId
     * @return
     */
    public synchronized int NoReadNumAddOne(final String sendId) {
        int num = 0;
        Cursor cursor = sqLiteDatabase.query("DB_ConversationList", new String[]{"noReadNum"}, "sendId=?", new String[]{sendId}, null, null, null);
        while (cursor.moveToNext()) {
            num = cursor.getInt(cursor.getColumnIndex("noReadNum")) + 1;
        }
        cursor.close();

        return num;
    }

    /**
     * 消息已读
     * @param tableName
     * @param msgId   消息id
     * @return
     */
    @Override
    public synchronized Observable<Boolean> MessageRead(final String tableName, final String msgId) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Boolean> e) throws Exception {
                ContentValues contentValues = new ContentValues();
                contentValues.put("isRead", true);
                long i = sqLiteDatabase.update("DB_" + tableName, contentValues, "msgId=?", new String[]{msgId});
                if (i > 0) {
                    e.onNext(true);
                } else {
                    e.onNext(false);
                }
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 单聊根据聊天对象创建表
     *
     * @param tableName 表名，即sendid
     */
    public void CreateChatTable(String tableName) {
        try {
            sqLiteDatabase.execSQL("CREATE TABLE if not exists " + tableName + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +         //id，做主键，自增
                    "msgId VARCHAR UNIQUE, " +                         //消息id
                    "sendId VARCHAR, " +                               //发送者id（其实可以不要，避免造成数据冗余）
                    "receiveId VARCHAR, " +                            //接受者id（其实可以不要，避免造成数据冗余）
                    "sessionType VARCHAR, " +                          //聊天类型（单聊，群聊...）（其实可以不要，避免造成数据冗余）
                    "msgType VARCHAR, " +                              //消息类型
                    "message TEXT, " +                                 //消息体（String类型的json对象）
                    "pushData VARCHAR, " +                             //通知消息
                    "sendTime datetime, " +                            //发送时间
                    "messageState INTEGER default -1, " +              //消息状态
                    "isRead boolean, " +                               //消息是否已读
                    "extra TEXT default '')");                         //附属消息（没啥用的）
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 退出登录时调用
     */
    public void ClearData() {
        //关闭数据库
        if (sqLiteDatabase != null) {
            sqLiteDatabase.close();
            sqLiteDatabase = null;
        }
        if (unionHelper != null) {
            unionHelper.close();
            unionHelper = null;
        }
        if (path != null)
            path = null;
    }

}
