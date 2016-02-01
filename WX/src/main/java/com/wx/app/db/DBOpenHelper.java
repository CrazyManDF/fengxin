package com.wx.app.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.wx.app.WeixinCacheHelper;
import com.wx.app.domain.UserDao;

/**
 * Created by darren foung on 2016/1/24.
 */
public class DBOpenHelper extends SQLiteOpenHelper {

    private static final int  DATABASE_VERSION = 1;

    private final String CREATE_TABLE_INVITEMESSAGE ="CREATE TABLE "
            + InviteMessgeDao.TABLE_NAME + " ("
            + InviteMessgeDao.COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + InviteMessgeDao.COLUMN_NAME_REASON + " TEXT,"
            + InviteMessgeDao.COLUMN_NAME_FROM + " TEXT,"
            + InviteMessgeDao.COLUMN_NAME_STATUS + " INTEGER,"
            + InviteMessgeDao.COLUMN_NAME_TIME + " TEXT,"
            + InviteMessgeDao.COLUMN_NAME_GROUP_ID + " TEXT,"
            + InviteMessgeDao.COLUMN_NAME_GROUP_NAME + ");";

    private final String CREATE_TABLE_USER ="CREATE TABLE "
            + UserDao.TABLE_NAME + " ("
            + UserDao.COLUMN_NAME_ID + " TEXT PRIMARY KEY,"
            + UserDao.COLUMN_NAME_NICK + " TEXT,"
            + UserDao.COLUMN_NAME_SEX + " TEXT,"
            + UserDao.COLUMN_NAME_AVATAR + " TEXT,"
            + UserDao.COLUMN_NAME_FXID + " TEXT,"
            + UserDao.COLUMN_NAME_REGION + " TEXT,"
            + UserDao.COLUMN_NAME_TEL + " TEXT,"
            + UserDao.COLUMN_NAME_BEIZHU + " TEXT,"
            + UserDao.COLUMN_NAME_FXID + " TEXT);";

    private static DBOpenHelper instance = null;

    private DBOpenHelper(Context context) {
        super(context, getUserDatabaseName(), null, DATABASE_VERSION);
    }

    public static DBOpenHelper getInstance(Context context){
        if(instance == null){
            instance = new DBOpenHelper(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_INVITEMESSAGE);
        db.execSQL(CREATE_TABLE_USER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private static String getUserDatabaseName() {
        return WeixinCacheHelper.getInstance().getHXId() + "_weixin.db";
    }

    public void closeDB(){
        if(instance != null){
            try {
                SQLiteDatabase db = instance.getWritableDatabase();
                if (db.isOpen()) {
                    db.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                instance = null;
            }
        }
    }
}
