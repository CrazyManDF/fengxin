package com.wx.app.fx;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.wx.app.R;
import com.wx.app.WeixinCacheHelper;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class SettingActivity extends FragmentActivity{

	protected static final String TAG = "SettingActivity";
	private ImageView iv_back;
	private RelativeLayout rl_switch_notification;
	private ImageView iv_switch_open_notification;
	private ImageView iv_switch_close_notification;
	private RelativeLayout rl_switch_sound_all;
	private RelativeLayout rl_switch_sound;
	private ImageView iv_switch_open_sound;
	private ImageView iv_switch_close_sound;
	private RelativeLayout rl_switch_vibrate_all;
	private RelativeLayout rl_switch_vibrate;
	private ImageView iv_switch_open_vibrate;
	private ImageView iv_switch_close_vibrate;
	private RelativeLayout rl_switch_speaker;
	private ImageView iv_switch_open_speaker;
	private ImageView iv_switch_close_speaker;
	private RelativeLayout rl_switch_logout;
	private EMChatOptions chatOptions;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		
		iv_back = (ImageView) findViewById(R.id.iv_back);
		iv_back.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				SettingActivity.this.finish();
			}
		});
		
		rl_switch_notification = (RelativeLayout) findViewById(R.id.rl_switch_notification);
		iv_switch_open_notification = (ImageView) findViewById(R.id.iv_switch_open_notification);
		iv_switch_close_notification = (ImageView) findViewById(R.id.iv_switch_close_notification);
		
		rl_switch_sound_all = (RelativeLayout) findViewById(R.id.rl_switch_sound_all);
		rl_switch_sound = (RelativeLayout) findViewById(R.id.rl_switch_sound);
		iv_switch_open_sound = (ImageView) findViewById(R.id.iv_switch_open_sound);
		iv_switch_close_sound = (ImageView) findViewById(R.id.iv_switch_close_sound);
		
		rl_switch_vibrate_all = (RelativeLayout) findViewById(R.id.rl_switch_vibrate_all);
		rl_switch_vibrate = (RelativeLayout) findViewById(R.id.rl_switch_vibrate);
		iv_switch_open_vibrate = (ImageView) findViewById(R.id.iv_switch_open_vibrate);
		iv_switch_close_vibrate = (ImageView) findViewById(R.id.iv_switch_close_vibrate);
		
		rl_switch_speaker = (RelativeLayout) findViewById(R.id.rl_switch_speaker);
		iv_switch_open_speaker = (ImageView) findViewById(R.id.iv_switch_open_speaker);
		iv_switch_close_speaker = (ImageView) findViewById(R.id.iv_switch_close_speaker);
		
		rl_switch_logout = (RelativeLayout) findViewById(R.id.rl_switch_logout);
		
		chatOptions = EMChatManager.getInstance().getChatOptions();
		if(chatOptions.getNotificationEnable()){
			iv_switch_open_notification.setVisibility(View.VISIBLE);
			iv_switch_close_notification.setVisibility(View.INVISIBLE);
		}else{
			iv_switch_open_notification.setVisibility(View.INVISIBLE);
			iv_switch_close_notification.setVisibility(View.VISIBLE);
		}
		if(chatOptions.getNoticedBySound()){
			iv_switch_open_sound.setVisibility(View.VISIBLE);
			iv_switch_close_sound.setVisibility(View.INVISIBLE);
		}else{
			iv_switch_open_sound.setVisibility(View.INVISIBLE);
			iv_switch_close_sound.setVisibility(View.VISIBLE);
		}
		if(chatOptions.getNoticedByVibrate()){
			iv_switch_open_vibrate.setVisibility(View.VISIBLE);
			iv_switch_close_vibrate.setVisibility(View.INVISIBLE);
		}else{
			iv_switch_open_vibrate.setVisibility(View.INVISIBLE);
			iv_switch_close_vibrate.setVisibility(View.VISIBLE);
		}
		if(chatOptions.getUseSpeaker()){
			iv_switch_open_speaker.setVisibility(View.VISIBLE);
			iv_switch_close_speaker.setVisibility(View.INVISIBLE);
		}else{
			iv_switch_open_speaker.setVisibility(View.INVISIBLE);
			iv_switch_close_speaker.setVisibility(View.VISIBLE);
		}
		
		rl_switch_notification.setOnClickListener(new StateOnClickListener());
		rl_switch_sound.setOnClickListener(new StateOnClickListener());
		rl_switch_vibrate.setOnClickListener(new StateOnClickListener());
		rl_switch_speaker.setOnClickListener(new StateOnClickListener());
		rl_switch_logout.setOnClickListener(new StateOnClickListener());
	}
	
	class StateOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.rl_switch_notification :
				if(iv_switch_open_notification.getVisibility() == View.VISIBLE){
					iv_switch_open_notification.setVisibility(View.INVISIBLE);
					iv_switch_close_notification.setVisibility(View.VISIBLE);
					
					rl_switch_sound_all.setVisibility(View.GONE);
					rl_switch_vibrate_all.setVisibility(View.GONE);
					
					chatOptions.setNotificationEnable(false);
					EMChatManager.getInstance().setChatOptions(chatOptions);
					// 保存到本地
					WeixinCacheHelper.getInstance().getModel().setSettingMsgNotification(false);
				}else{
					iv_switch_open_notification.setVisibility(View.VISIBLE);
					iv_switch_close_notification.setVisibility(View.INVISIBLE);
					rl_switch_sound_all.setVisibility(View.VISIBLE);
					rl_switch_vibrate_all.setVisibility(View.VISIBLE);
					
					chatOptions.setNotificationEnable(true);
					EMChatManager.getInstance().setChatOptions(chatOptions);
					// 保存到本地
					WeixinCacheHelper.getInstance().getModel().setSettingMsgNotification(true);
				}
				break;
			case R.id.rl_switch_sound :
				if(iv_switch_open_sound.getVisibility() == View.VISIBLE){
					iv_switch_open_sound.setVisibility(View.INVISIBLE);
					iv_switch_close_sound.setVisibility(View.VISIBLE);
					chatOptions.setNoticeBySound(false);
					EMChatManager.getInstance().setChatOptions(chatOptions);
					// 保存到本地
					WeixinCacheHelper.getInstance().getModel().setSettingMsgSound(false);
				}else{
					iv_switch_open_sound.setVisibility(View.VISIBLE);
					iv_switch_close_sound.setVisibility(View.INVISIBLE);
					chatOptions.setNoticeBySound(true);
					EMChatManager.getInstance().setChatOptions(chatOptions);
					// 保存到本地
					WeixinCacheHelper.getInstance().getModel().setSettingMsgSound(true);
				}
				break;
			case R.id.rl_switch_vibrate :
				if(iv_switch_open_vibrate.getVisibility() == View.VISIBLE){
					iv_switch_open_vibrate.setVisibility(View.INVISIBLE);
					iv_switch_close_vibrate.setVisibility(View.VISIBLE);
					chatOptions.setNoticedByVibrate(false);
					EMChatManager.getInstance().setChatOptions(chatOptions);
					// 保存到本地
					WeixinCacheHelper.getInstance().getModel().setSettingMsgVibrate(false);
				}else{
					iv_switch_open_vibrate.setVisibility(View.VISIBLE);
					iv_switch_close_vibrate.setVisibility(View.INVISIBLE);
					chatOptions.setNoticedByVibrate(true);
					EMChatManager.getInstance().setChatOptions(chatOptions);
					// 保存到本地
					WeixinCacheHelper.getInstance().getModel().setSettingMsgVibrate(true);
				}
				break;
			case R.id.rl_switch_speaker :
				if(iv_switch_open_speaker.getVisibility() == View.VISIBLE){
					iv_switch_open_speaker.setVisibility(View.INVISIBLE);
					iv_switch_close_speaker.setVisibility(View.VISIBLE);
					chatOptions.setUseSpeaker(false);
					EMChatManager.getInstance().setChatOptions(chatOptions);
					// 保存到本地
					WeixinCacheHelper.getInstance().getModel().setSettingMsgSpeaker(false);
				}else{
					iv_switch_open_speaker.setVisibility(View.VISIBLE);
					iv_switch_close_speaker.setVisibility(View.INVISIBLE);
					chatOptions.setUseSpeaker(true);
					EMChatManager.getInstance().setChatOptions(chatOptions);
					// 保存到本地
					WeixinCacheHelper.getInstance().getModel().setSettingMsgSpeaker(true);
				}
				break;
			case R.id.rl_switch_logout :
				logout();
				break;
			default:
				break;
			}
		}
	}
	
	public void logout(){
		Log.d(TAG, "logout");
		final ProgressDialog dialog = new ProgressDialog(SettingActivity.this);
		dialog.setMessage("正在退出登录...");
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
		WeixinCacheHelper.getInstance().logout(new EMCallBack(){

			@Override
			public void onError(int arg0, String arg1) {}

			@Override
			public void onProgress(int arg0, String arg1) {}

			@Override
			public void onSuccess() {
				dialog.dismiss();
				SettingActivity.this.finish();
				startActivity(new Intent(SettingActivity.this, LoginActivity.class));
			}
		});
	}
	
}
