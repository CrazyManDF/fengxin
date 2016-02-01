package com.wx.app;

import com.easemob.EMCallBack;
import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.wx.app.domain.User;
import com.wx.app.domain.UserDao;

import android.content.Context;
import android.util.Log;

import java.util.List;
import java.util.Map;

public class WeixinCacheHelper {

	private static final String TAG = "WeixinCacheHelper";

	private Context appContext = null;
	private static WeixinCacheHelper cacheHelper = null;
	protected WeixinModelHelper wxModel = null;

	private Map<String, User> contactList ;
	/**
	 * HuanXin ID in cache
	 */
	protected String hxId = null;
	protected String password = null;

	public WeixinCacheHelper() {
		cacheHelper = this;
	}

	public static WeixinCacheHelper getInstance() {
		return cacheHelper;
	}

	public boolean onInit(Context context) {
		appContext = context;
		wxModel = new WeixinModelHelper(context);

		// 初始化环信SDK,一定要先调用init()
		EMChat.getInstance().init(context);
		// 设置sandbox测试环境
		// 建议开发者开发时设置此模式
		// if(hxModel.isSandboxMode()){
		// EMChat.getInstance().setEnv(EMEnvMode.EMSandboxMode);
		// }
		// if(hxModel.isDebugMode()){
		// set debug mode in development process
		// EMChat.getInstance().setDebugMode(true);
		// }
		Log.d(TAG, "initialize EMChat SDK");

		initListener();

		return true;
	}

	public void initListener() {
		Log.d(TAG, "init listener");

		// create the global connection listener
		EMConnectionListener connectionListener = new EMConnectionListener() {
			@Override
			public void onDisconnected(int error) {
				Log.d(TAG, "erro=" + error);
				if (error == EMError.USER_REMOVED) {
					Log.d(TAG, "USER_REMOVED");
					onCurrentAccountRemoved();
				} else if (error == EMError.CONNECTION_CONFLICT) {
					Log.d(TAG, "CONNECTION_CONFLICT");
					onConnectionConflict();
				} else {
					Log.d(TAG, "onConnectionDisconnected");
					onConnectionDisconnected(error);
				}
			}

			@Override
			public void onConnected() {
				Log.d(TAG, "onConnectionConnected");
				onConnectionConnected();
			}
		};
		EMChatManager.getInstance().addConnectionListener(connectionListener);
	}

	public WeixinModelHelper getModel(){
		return (WeixinModelHelper) wxModel;
	}
	
	public void onCurrentAccountRemoved() {
		// Intent intent = new Intent(appContext, MainActivity.class);
		// intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// intent.putExtra(Constant.ACCOUNT_REMOVED, true);
		// appContext.startActivity(intent);
	}

	protected void onConnectionConflict() {
		// Intent intent = new Intent(appContext, MainActivity.class);
		// intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// intent.putExtra("conflict", true);
		// appContext.startActivity(intent);
	}

	protected void onConnectionDisconnected(int error) {
	}

	protected void onConnectionConnected() {
	}

	public void setHXId(String hxId) {
		if (hxId != null) {
			if (wxModel.saveHXId(hxId)) {
				this.hxId = hxId;
			}
		}
	}

	public String getHXId() {
		if (hxId == null) {
			hxId = wxModel.getHXId();
		}
		return hxId;
	}

	public String getPassword() {
		if (password == null) {
			password = wxModel.getPassword();
		}
		return password;
	}

	/**
	 * 设置好友user list到内存中
	 *
	 * @param contactList
	 */
	public void setContactList(Map<String, User> contactList) {
		this.contactList = contactList;
		// TODO: 2016/1/24 没保存到数据库
	}

	/**
	 * 获取内存中好友user list
	 *
	 * @return
	 */
	public Map<String, User> getContactList() {
		if(getHXId() != null && contactList == null) {
			contactList = getModel().getContactList();
		}
		return contactList;
	}

	public void setPassword(String password) {
		if (wxModel.savePassword(password)) {
			this.password = password;
		}
	}

	public boolean isLogined() {
		if (wxModel.getHXId() != null && wxModel.getPassword() != null) {
			return true;
		}
		return false;
	}

	public void logout(final EMCallBack callback) {
		EMChatManager.getInstance().logout(new EMCallBack() {

			@Override
			public void onError(int arg0, String arg1) {
			}

			@Override
			public void onProgress(int arg0, String arg1) {
				if(callback != null){
					callback.onProgress(arg0, arg1);
				}
			}

			@Override
			public void onSuccess() {
				setPassword(null);
				// TODO 关闭数据库
				if(callback != null){
					callback.onSuccess();
				}
			}
		});
	}


}
