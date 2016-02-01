package com.wx.app.activity;

import java.io.File;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.wx.app.R;
import com.wx.app.WeixinCacheHelper;
import com.wx.app.fx.LoginActivity;
import com.wx.app.fx.MainActivity;
import com.wx.app.utils.CommonUtils;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.animation.AlphaAnimation;

public class SplashActivity extends Activity {

	private final int SLEEP_TIME = 2000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final View view = View.inflate(this, R.layout.activity_splash, null);
		setContentView(view);

		// 创建应用目录
		initFile();

		// 渐变动画
		AlphaAnimation alphaAnimation = new AlphaAnimation(0.3f, 1.0f);
		alphaAnimation.setDuration(1500);
		view.startAnimation(alphaAnimation);
	}

	@Override
	protected void onStart() {
		super.onStart();

		new Thread(new Runnable() {

			@Override
			public void run() {
				if (WeixinCacheHelper.getInstance().isLogined()) {
					long start = System.currentTimeMillis();
					// TODO 加载本地群和会话
					long cost = System.currentTimeMillis() - start;
					if(cost < SLEEP_TIME){
						SystemClock.sleep(SLEEP_TIME-cost);
					}
					
					startActivity(new Intent(SplashActivity.this, MainActivity.class));
					finish();
				} else {
					SystemClock.sleep(SLEEP_TIME);
					// 启动登陆界面
					Intent loginIntent = new Intent();
					loginIntent.setClass(SplashActivity.this, LoginActivity.class);
					startActivity(loginIntent);
					finish();
				}
			}
		}).start();

	}

	/**
	 * 获取当前应用程序的版本号
	 */
	public String getAppVersion() {
		PackageManager packageManager = getPackageManager();
		try {
			PackageInfo info = packageManager.getPackageInfo(getPackageName(), 0);
			String versionName = info.versionName;
			return versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return "版本号错误";
		}
	}

	public void initFile() {
		File appDir = new File(CommonUtils.sdcardPath());
		if (!appDir.exists()) {
			appDir.mkdirs();
		}
	}
}
