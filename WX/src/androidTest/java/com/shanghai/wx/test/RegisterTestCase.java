package com.shanghai.wx.test;

import com.wx.app.fx.RegisterActivity;

import android.test.ActivityInstrumentationTestCase2;

public class RegisterTestCase extends ActivityInstrumentationTestCase2 {

	private RegisterActivity registerActivity;

	public RegisterTestCase() {
		super("com.wx.app.activity.fx", RegisterActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
		registerActivity = (RegisterActivity) getActivity();
	}

	public void testRegister(){
		registerActivity.register("11236130", "1103fei");
//		EMChatManager.getInstance().createAccountOnServer("11236130", "1103fei");
	}
}
