package com.wx.app.fx;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.easemob.chat.EMChatManager;

public class BaseActivity extends FragmentActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// onresume时，取消notification显示
		EMChatManager.getInstance().activityResumed();

	}


}
