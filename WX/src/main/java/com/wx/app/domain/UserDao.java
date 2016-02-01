package com.wx.app.domain;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.easemob.util.HanziToPinyin;
import com.wx.app.Constant;
import com.wx.app.db.DBOpenHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by darren foung on 2016/1/24.
 */
public class UserDao {

    public static final String TABLE_NAME = "uers";
    public static final String COLUMN_NAME_ID = "username";
    public static final String COLUMN_NAME_NICK = "nick";
    public static final String COLUMN_NAME_SEX = "sex";
    public static final String COLUMN_NAME_AVATAR = "avatar";
    public static final String COLUMN_NAME_SIGN = "sign";
    public static final String COLUMN_NAME_TEL = "tel";
    public static final String COLUMN_NAME_FXID = "fxid";
    public static final String COLUMN_NAME_REGION = "region";
    public static final String COLUMN_NAME_BEIZHU = "beizhu";
    public static final String COLUMN_NAME_IS_STRANGER = "is_stranger";

    private final DBOpenHelper dbOpenHelper;

    public UserDao(Context context){
        dbOpenHelper = DBOpenHelper.getInstance(context);
    }

    /**
     * 保存好友list
     *
     * @param contactList
     */
    public void saveContactList(List<User> contactList){
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        if(db.isOpen()){
            db.delete(TABLE_NAME, null, null);
            for(User user : contactList){
                ContentValues values = new ContentValues();
                values.put(COLUMN_NAME_ID, user.getUsername());
                if(user.getNick() != null){
                    values.put(COLUMN_NAME_NICK, user.getNick());
                }
                if (user.getBeizhu() != null) {
                    values.put(COLUMN_NAME_BEIZHU, user.getBeizhu());
                }
                if (user.getTel() != null) {
                    values.put(COLUMN_NAME_TEL, user.getTel());
                }
                if (user.getSex() != null) {
                    values.put(COLUMN_NAME_SEX, user.getSex());
                }
                if (user.getAvatar() != null) {
                    values.put(COLUMN_NAME_AVATAR, user.getAvatar());
                }
                if (user.getSign() != null) {
                    values.put(COLUMN_NAME_SIGN, user.getSign());
                }
                if (user.getFxid() != null) {
                    values.put(COLUMN_NAME_FXID, user.getFxid());
                }
                if (user.getRegion()!= null) {
                    values.put(COLUMN_NAME_REGION, user.getRegion());
                }
                db.replace(TABLE_NAME, null, values);
            }
        }
    }

    public Map<String, User> getContactList() {
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Map<String, User> userMap = new HashMap<String, User>();
        if(db.isOpen()){
            Cursor cursor = db.rawQuery("select * from " + TABLE_NAME + " desc ", null);
            while(cursor.moveToNext()){
                String username = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_ID));
                String nick = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_NICK));
                String avatar = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_AVATAR));
                String tel = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_TEL));
                String sign = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_SIGN));
                String sex = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_SEX));
                String region = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_REGION));
                String beizhu = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_BEIZHU));
                String fxid = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_FXID));

                User user = new User();
                user.setUsername(username);
                user.setNick(nick);
                user.setBeizhu(beizhu);
                user.setFxid(fxid);
                user.setRegion(region);
                user.setSex(sex);
                user.setSign(sign);
                user.setTel(tel);
                user.setAvatar(avatar);
                String headerName = null;
                if(!TextUtils.isEmpty(user.getNick())){
                    headerName = user.getNick();
                }else{
                    headerName = user.getUsername();
                }

                if(username.equals(Constant.NEW_FRIENDS_USERNAME)
                        || username.equals(Constant.GROUP_USERNAME)){
                    user.setHeader("");
                }else if (Character.isDigit(headerName.charAt(0))){
                    user.setHeader("#");
                }else{
                    user.setHeader(HanziToPinyin.getInstance()
                            .get(headerName.substring(0,1)).get(0)
                    .target.substring(0, 1).toUpperCase());
                    char header = user.getHeader().toLowerCase().charAt(0);
                    if(header < 'a' || header > 'z'){
                        user.setHeader("#");
                    }
                }
                userMap.put(username, user);
            }
            cursor.close();
        }
        return userMap;
    }
    /**
     * 删除一个联系人
     * @param username
     */
    public void deleteContact(String username){
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        if(db.isOpen()){
            db.delete(TABLE_NAME, COLUMN_NAME_ID + " =  ?", new String[]{username});
        }
    }

    /**
     * 保存一个联系人
     * @param user
     */
    public void saveContact(User user){
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME_ID, user.getUsername());
        if(user.getNick() != null){
            values.put(COLUMN_NAME_NICK,user.getNick());
        }
        if (user.getBeizhu() != null) {
            values.put(COLUMN_NAME_BEIZHU, user.getBeizhu());
        }
        if (user.getTel() != null) {
            values.put(COLUMN_NAME_TEL, user.getTel());
        }
        if (user.getSex() != null) {
            values.put(COLUMN_NAME_SEX, user.getSex());
        }
        if (user.getAvatar() != null) {
            values.put(COLUMN_NAME_AVATAR, user.getAvatar());
        }
        if (user.getSign() != null) {
            values.put(COLUMN_NAME_SIGN, user.getSign());
        }
        if (user.getFxid() != null) {
            values.put(COLUMN_NAME_FXID, user.getFxid());
        }
        if (user.getRegion()!= null) {
            values.put(COLUMN_NAME_REGION, user.getRegion());
        }
        if(db.isOpen()){
            db.replace(TABLE_NAME, null, values);
        }
    }

}
