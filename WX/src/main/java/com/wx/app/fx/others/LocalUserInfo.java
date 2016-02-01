package com.wx.app.fx.others;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by darren foung on 2016/1/23.
 */
public class LocalUserInfo {

    private static SharedPreferences sharedPreferences;
    private final String PREFERENCE_NAME = "local_user_info";
    private static SharedPreferences.Editor editor;
    private static LocalUserInfo instance;

    private LocalUserInfo(Context context){
            sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME,
                    Context.MODE_PRIVATE);
    }

    public static LocalUserInfo getInstance(Context context){
        if(sharedPreferences == null){
            instance = new LocalUserInfo(context);
        }
        editor = sharedPreferences.edit();
        return instance;
    }

    public void setUserInfo(String name, String value){
        editor.putString(name, value);
        editor.commit();
    }

    public String getUerInfo(String name){
        return sharedPreferences.getString(name, "");
    }

}
