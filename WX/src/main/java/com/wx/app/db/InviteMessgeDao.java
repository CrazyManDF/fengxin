package com.wx.app.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wx.app.domain.InviteMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by darren foung on 2016/1/24.
 */
public class InviteMessgeDao {

    public static final String TABLE_NAME = "new_friends_msgs";

    public static final String COLUMN_NAME_ID = "id";
    public static final String COLUMN_NAME_FROM = "username";
    public static final String COLUMN_NAME_GROUP_ID = "groupid";
    public static final String COLUMN_NAME_GROUP_NAME = "groupname";

    public static final String COLUMN_NAME_TIME = "time";
    public static final String COLUMN_NAME_REASON = "reason";
    public static final String COLUMN_NAME_STATUS = "status";

    private final DBOpenHelper dbHelper;

    public InviteMessgeDao(Context context){
        dbHelper = DBOpenHelper.getInstance(context);
    }

    /**
     * 保存message
     * @param message
     * @return  返回这条messaged在db中的id
     */
    public  Integer saveMessage(InviteMessage message){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int id = -1;
        if(db.isOpen()){
            db.execSQL("insert into " + TABLE_NAME + " values ("
                            + message.getId() + ","
                            + message.getReason() + ","
                            + message.getFrom() + ","
                            + message.getStatus().ordinal() + ","
                            + message.getTime() + ","
                            + message.getGroupId() + ","
                            + message.getGroupName() + ");"
            );
            Cursor cursor = db.rawQuery("select last_insert_rowid() from " + TABLE_NAME, null);
            if(cursor.moveToNext()){ // TODO: 2016/1/24 movetofirst
                id = cursor.getInt(0);
            }
            cursor.close();
        }
        return id;
    }


    /**
     * 删除来自from用户的消息
     * @param from
     */
    public void deleteMessage(String from){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if(db.isOpen()){
            db.delete(TABLE_NAME, COLUMN_NAME_FROM + " = ?",
                    new String[]{from});
        }
    }


    /**
     * 获取messges
     * @return
     */
    public List<InviteMessage> getMessagesList(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        List<InviteMessage> messageList = new ArrayList<InviteMessage>();
        if(db.isOpen()){
            Cursor cursor = db.rawQuery("select * from " + TABLE_NAME + " desc ", null);
            while(cursor.moveToNext()){
                InviteMessage msg = new InviteMessage();
                int id = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_ID));
                String from = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_FROM));
                String groupid = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_GROUP_ID));
                String groupname = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_GROUP_NAME));
                String reason = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_REASON));
                long time = cursor.getLong(cursor.getColumnIndex(COLUMN_NAME_TIME));
                int status = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_STATUS));

                msg.setId(id);
                msg.setFrom(from);
                msg.setGroupId(groupid);
                msg.setGroupName(groupname);
                msg.setReason(reason);
                msg.setTime(time);
                if(status == InviteMessage.InviteMessageStatus.BEINVITED.ordinal()){
                    msg.setStatus(InviteMessage.InviteMessageStatus.BEINVITED);
                }else if(status == InviteMessage.InviteMessageStatus.BEAGREED.ordinal())
                    msg.setStatus(InviteMessage.InviteMessageStatus.BEAGREED);
                else if(status == InviteMessage.InviteMessageStatus.BEREFUSED.ordinal())
                    msg.setStatus(InviteMessage.InviteMessageStatus.BEREFUSED);
                else if(status == InviteMessage.InviteMessageStatus.AGREED.ordinal())
                    msg.setStatus(InviteMessage.InviteMessageStatus.AGREED);
                else if(status == InviteMessage.InviteMessageStatus.REFUSED.ordinal())
                    msg.setStatus(InviteMessage.InviteMessageStatus.REFUSED);
                else if(status == InviteMessage.InviteMessageStatus.BEAPPLYED.ordinal()){
                    msg.setStatus(InviteMessage.InviteMessageStatus.BEAPPLYED);
                }
                messageList.add(msg);
            }
            cursor.close();
        }
        return messageList;
    }

    /**
     * 更新message
     * @param msgId
     * @param message
     */
    public void updateMessage(int msgId, InviteMessage message){

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if(db.isOpen()){
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME_ID,message.getId());
            values.put(COLUMN_NAME_FROM,message.getFrom());
            values.put(COLUMN_NAME_GROUP_ID,message.getGroupId());
            values.put(COLUMN_NAME_GROUP_NAME,message.getGroupName());
            values.put(COLUMN_NAME_TIME,message.getTime());
            values.put(COLUMN_NAME_REASON,message.getReason());
            values.put(COLUMN_NAME_STATUS,message.getStatus().ordinal());
            db.update(TABLE_NAME, values, COLUMN_NAME_ID + " = ?",
                    new String[]{String.valueOf(msgId)});
        }
    }

    public void deleteALLMessage(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if(db.isOpen()){
            db.execSQL("delete from " + TABLE_NAME);
        }
        if(db != null){
            db.close();
        }
    }

}
