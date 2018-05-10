package com.gloiot.hygounionmerchant.server.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 认证记录搜索历史
 * Created by Dlt on 2017/11/18 15:12
 */
public class AuthenticationRecordSearchHistorySQLiteOpenHelper extends SQLiteOpenHelper {

    private static String name = "authenticationrecordsearchhistory.db";
    private static Integer version = 1;

    public AuthenticationRecordSearchHistorySQLiteOpenHelper(Context context) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table records(id integer primary key autoincrement,name varchar(200))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}