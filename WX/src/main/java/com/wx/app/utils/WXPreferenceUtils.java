package com.wx.app.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class WXPreferenceUtils {

	public static final String PREFERENCE_NAME = "saveInfo";
	private SharedPreferences sharedPreferences;
	private static WXPreferenceUtils preferenceUtils;
	private Editor editor;

	private String SHARED_KEY_SETTING_NOTIFICATION = "shared_key_setting_notification";
	private String SHARED_KEY_SETTING_SOUND = "shared_key_setting_sound";
	private String SHARED_KEY_SETTING_VIBRATE = "shared_key_setting_vibrate";
	private String SHARED_KEY_SETTING_SPEAKER = "shared_key_setting_speaker";
	
	
	private WXPreferenceUtils(Context context) {
		sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();
	}

	public static WXPreferenceUtils getInstance() {
		if (preferenceUtils == null) {
			throw new RuntimeException("please init first!");
		}
		return preferenceUtils;
	}

	public static synchronized void init(Context context) {
		if (preferenceUtils == null) {
			preferenceUtils = new WXPreferenceUtils(context);
		}
	}

	public boolean getSettingMsgNotification() {
		return sharedPreferences.getBoolean(SHARED_KEY_SETTING_NOTIFICATION, true);
	}

	public void setSettingMsgNotification(boolean paramBoolean) {
		editor.putBoolean(SHARED_KEY_SETTING_NOTIFICATION, paramBoolean);
		editor.commit();
	}

	public boolean getSettingMsgSound() {
		return sharedPreferences.getBoolean(SHARED_KEY_SETTING_SOUND, true);
	}

	public void setSettingMsgSound(boolean paramBoolean) {
		editor.putBoolean(SHARED_KEY_SETTING_SOUND, paramBoolean);
		editor.commit();
	}
	
	public boolean getSettingMsgVibrate() {
		return sharedPreferences.getBoolean(SHARED_KEY_SETTING_VIBRATE, true);
	}
	
	public void setSettingMsgVibrate(boolean paramBoolean) {
		editor.putBoolean(SHARED_KEY_SETTING_VIBRATE, paramBoolean);
		editor.commit();
	}
	
	public boolean getSettingMsgSpeaker() {
		return sharedPreferences.getBoolean(SHARED_KEY_SETTING_SPEAKER, true);
	}
	
	public void setSettingMsgSpeaker(boolean paramBoolean) {
		editor.putBoolean(SHARED_KEY_SETTING_SPEAKER, paramBoolean);
		editor.commit();
	}
}
