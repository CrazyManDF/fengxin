package com.wx.app;

import android.app.Application;
import android.content.Context;

import com.easemob.EMCallBack;
import com.wx.app.domain.User;

import java.util.Map;

public class WeixinApplication extends Application{

	public static Context applicationContext;
	private static WeixinApplication instance;
	
	private static WeixinCacheHelper wxCache = new WeixinCacheHelper();
	
	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		applicationContext = this;
		wxCache.onInit(applicationContext);
	}
	
	public static WeixinApplication getInstance() {
		return instance;
	}
	
	/**
	 * 设置用户名
	 *
	 * @param username
	 */
	public void setUserName(String username) {
		wxCache.setHXId(username);
	}
	
	/**
	 * 获取当前登陆用户名
	 *
	 * @return
	 */
	public String getUserName() {
	    return wxCache.getHXId();
	}
	
	/**
	 * 设置密码 
	 *
	 * @param pwd
	 */
	public void setPassword(String pwd) {
		wxCache.setPassword(pwd);
	}
	
	/**
	 * 获取密码
	 *
	 * @return
	 */
	public String getPassword() {
	    return wxCache.getPassword();
	}
	
	/**
	 * 退出登录,清空数据
	 */
	public void logout(EMCallBack callback) {
		wxCache.logout(callback);
	}

	/**
	 * 获取内存中好友user list
	 *
	 * @return
	 */
	public Map<String, User> getContactList() {
		return wxCache.getContactList();
	}

	/**
	 * 设置好友user list到内存中
	 *
	 * @param contactMap
	 */
	public void setContactList(Map<String, User> contactMap) {
		wxCache.setContactList(contactMap);
	}

}
