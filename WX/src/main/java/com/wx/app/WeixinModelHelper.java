package com.wx.app;

import com.wx.app.domain.User;
import com.wx.app.domain.UserDao;
import com.wx.app.utils.WXPreferenceUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.List;
import java.util.Map;

public class WeixinModelHelper {
	
	private Context context = null;
	
	private static final String PREF_USERNAME = "username";
    private static final String PREF_PWD = "pwd";
	
	public WeixinModelHelper(Context context){
		this.context = context;
		WXPreferenceUtils.init(context);
	}

	public boolean saveHXId(String hxId) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.edit().putString(PREF_USERNAME, hxId).commit();
    }

    public String getHXId() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(PREF_USERNAME, null);
    }
    
    public boolean savePassword(String pwd){
    	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
    	return preferences.edit().putString(PREF_PWD, pwd).commit();
    }
    public String getPassword(){
    	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
    	return preferences.getString(PREF_PWD, null);
    }
    
    public void setSettingMsgNotification(boolean paramBoolean){
    	WXPreferenceUtils.getInstance().setSettingMsgNotification(paramBoolean);
    }
    public boolean getSettingMsgNotification(){
    	return WXPreferenceUtils.getInstance().getSettingMsgNotification();
    }

	public boolean getSettingMsgSound() {
		return WXPreferenceUtils.getInstance().getSettingMsgSound();
	}

	public void setSettingMsgSound(boolean paramBoolean) {
		WXPreferenceUtils.getInstance().setSettingMsgSound(paramBoolean);
	}
	
	public boolean getSettingMsgVibrate() {
		return WXPreferenceUtils.getInstance().getSettingMsgVibrate();
	}
	
	public void setSettingMsgVibrate(boolean paramBoolean) {
		WXPreferenceUtils.getInstance().setSettingMsgVibrate(paramBoolean);
	}
	
	public boolean getSettingMsgSpeaker() {
		return WXPreferenceUtils.getInstance().getSettingMsgSpeaker();
	}
	
	public void setSettingMsgSpeaker(boolean paramBoolean) {
		WXPreferenceUtils.getInstance().setSettingMsgSpeaker(paramBoolean);
	}

	public Map<String, User> getContactList() {
		UserDao dao = new UserDao(context);
		return dao.getContactList();
	}

	public boolean saveContactList(List<User> contactList) {
		UserDao dao = new UserDao(context);
		dao.saveContactList(contactList);
		return true;
	}
}

