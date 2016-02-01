package com.wx.app.fx;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.exceptions.EaseMobException;
import com.wx.app.Constant;
import com.wx.app.R;
import com.wx.app.WeixinApplication;
import com.wx.app.fx.others.LoadDataFromServer;
import com.wx.app.fx.others.LoadDataFromServer.DataCallback;
import com.wx.app.utils.CommonUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.Html;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends FragmentActivity {

	protected static final String TAG = "RegisterActivity";

	private TextView tv_xieyi;
	private ImageView iv_hide;
	private ImageView iv_show;
	private ImageView iv_photo;
	private EditText et_usernick;
	private EditText et_usertel;
	private EditText et_password;
	private Button btn_register;
	private ProgressDialog dialog;

	private String imageName; // 拍照存档名字
	private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
	private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
	private static final int PHOTO_REQUEST_CUT = 3;// 结果

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		iv_photo = (ImageView) findViewById(R.id.iv_photo);
		iv_hide = (ImageView) findViewById(R.id.iv_hide);
		iv_show = (ImageView) findViewById(R.id.iv_show);

		et_password = (EditText) findViewById(R.id.et_password);
		et_usernick = (EditText) findViewById(R.id.et_usernick);
		et_usertel = (EditText) findViewById(R.id.et_usertel);

		tv_xieyi = (TextView) findViewById(R.id.tv_xieyi);
		btn_register = (Button) findViewById(R.id.btn_register);
		dialog = new ProgressDialog(RegisterActivity.this);

		String xieyi = "<font color=\"" + "#AAAAAA" + "\">点击上面的\"注册\"按钮,即表示你同意</font>" + "<U><font color=\"" + "#576B95"
				+ "\">《软件许可及服务协议》</font></U>";
		tv_xieyi.setText(Html.fromHtml(xieyi));

		et_password.addTextChangedListener(new TextChange());
		et_usernick.addTextChangedListener(new TextChange());
		et_usertel.addTextChangedListener(new TextChange());

		iv_hide.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				iv_hide.setVisibility(View.GONE);
				iv_show.setVisibility(View.VISIBLE);
				et_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
				// 光标移至末尾
				// CharSequence password = tv_password.getText();
				Editable password = et_password.getEditableText();
				Selection.setSelection(password, password.length());
			}
		});
		iv_show.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				iv_show.setVisibility(View.GONE);
				iv_hide.setVisibility(View.VISIBLE);
				et_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
				// 光标移至末尾
				Editable password = et_password.getEditableText();
				Selection.setSelection(password, password.length());
			}
		});

		iv_photo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showCamera();
			}
		});

		btn_register.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.setMessage("正在注册");
				dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				dialog.show();

				String usernick = et_usernick.getText().toString().trim();
				final String password = et_password.getText().toString().trim();
				String usertel = et_usertel.getText().toString().trim();

				Map<String, String> map = new HashMap<String, String>();
				map.put("usernick", usernick);
				map.put("usertel", usertel);
				map.put("password", password);
				if ((new File(CommonUtils.sdcardPath() + imageName)).exists()) {
					map.put("file", CommonUtils.sdcardPath() + imageName);
					map.put("image", imageName);
				} else {
					map.put("image", "false");
				}
				Log.d(TAG, map.toString());
				LoadDataFromServer loadData = new LoadDataFromServer(RegisterActivity.this, Constant.URL_Register_Tel,
						map);
				loadData.getData(new DataCallback() {

					@Override
					public void OnDataCallback(JSONObject jsonObject) {
						try {
							int code = jsonObject.getInteger("code");
							switch (code) {
							case 1:
								String hxid = jsonObject.getString("hxid");
								register(hxid, password);
								break;
							case 2:
								dialog.dismiss();
								Toast.makeText(RegisterActivity.this, "该手机号码已被注册...", Toast.LENGTH_SHORT).show();
								break;
							case 3:
								dialog.dismiss();
								Toast.makeText(RegisterActivity.this, "服务器端注册失败...", Toast.LENGTH_SHORT).show();
								break;
							case 4:
								dialog.dismiss();
								Toast.makeText(RegisterActivity.this, "头像传输失败...", Toast.LENGTH_SHORT).show();
								break;
							case 5:
								dialog.dismiss();
								Toast.makeText(RegisterActivity.this, "返回环信id失败...", Toast.LENGTH_SHORT).show();
								break;
							default:
								dialog.dismiss();
								Toast.makeText(RegisterActivity.this, "服务器繁忙请重试...", Toast.LENGTH_SHORT).show();
								break;
							}
						} catch (JSONException e) {
							dialog.dismiss();
							Toast.makeText(RegisterActivity.this, "数据解析错误...", Toast.LENGTH_SHORT).show();
							e.printStackTrace();
						}
					}
				});
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case PHOTO_REQUEST_TAKEPHOTO:
				startPhotoZoom(Uri.fromFile(new File(CommonUtils.sdcardPath(), imageName)), 480);
				break;
			case PHOTO_REQUEST_GALLERY:
				if (data != null) {
					startPhotoZoom(data.getData(), 480);
				}
				break;
			case PHOTO_REQUEST_CUT:
				Log.d(TAG, "case PHOTO_REQUEST_CUT:");
				Bitmap bitmap = BitmapFactory.decodeFile(CommonUtils.sdcardPath() + imageName);
				iv_photo.setImageBitmap(bitmap);
				break;
			}
		}
	}

	public void startPhotoZoom(Uri uri1, int size) {
		Intent intent = new Intent();
		intent.setAction("com.android.camera.action.CROP");
		intent.setDataAndType(uri1, "image/*");

		// 显示的View可以剪裁
		intent.putExtra("crop", "true");
		// 宽高比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// 剪裁图片的宽高
		intent.putExtra("outputX", size);
		intent.putExtra("outputY", size);
		// 不返回数据
		intent.putExtra("return-data", false);
		// 取消人脸识别
		intent.putExtra("noFaceDetection", true);

		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(CommonUtils.sdcardPath(), imageName)));
		intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());

		Log.d(TAG, "" + Bitmap.CompressFormat.PNG.toString());
		startActivityForResult(intent, PHOTO_REQUEST_CUT);
	}

	/* 选择图片 */
	public void showCamera() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dlg = builder.create();
		dlg.show();
		Window window = dlg.getWindow();
		window.setContentView(R.layout.alertdialog);

		// 拍照
		TextView tv_content1 = (TextView) window.findViewById(R.id.tv_content1);
		tv_content1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				imageName = getNowTime() + ".png";
				Intent intent = new Intent();
				intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
				Log.d(TAG, "" + CommonUtils.sdcardPath() + ",imageName=" + imageName);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(CommonUtils.sdcardPath(), imageName)));
				startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO);
				dlg.cancel();
			}
		});

		// 相册
		TextView tv_content2 = (TextView) window.findViewById(R.id.tv_content2);
		tv_content2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				imageName = getNowTime() + ".png";
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_PICK);
				intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
				startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
				dlg.cancel();

			}
		});
	}

	public String getNowTime() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat("MMddHHmmssSS");
		return dateFormat.format(date);
	}

	class TextChange implements TextWatcher {

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			// Log.d(TAG,
			// "beforeTextChanged：s="+s+",start="+start+",count="+count+",after="+after);
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			Log.d(TAG, "onTextChanged：s=" + s + ",start=" + start + ",before=" + before + ",count=" + count);
			boolean sign1 = et_usernick.getText().length() > 0;
			boolean sign2 = et_usertel.getText().length() > 0;
			boolean sign3 = et_password.getText().length() > 0;
			if (sign1 & sign2 & sign3) {
				btn_register.setTextColor(0xFFFFFFFF);
				btn_register.setEnabled(true);
			} else {
				btn_register.setTextColor(0xFFD0EFC6);
				btn_register.setEnabled(false);
			}
		}

		@Override
		public void afterTextChanged(Editable s) {
			// Log.d(TAG, "afterTextChanged：s="+s);
		}
	}

	/**
	 * 注册新用户
	 * 
	 * @param hxid
	 * @param password
	 */
	public void register(final String hxid, final String password) {
		
		if(!TextUtils.isEmpty(hxid) && !TextUtils.isEmpty(password)){
			final String st6 = getResources().getString(R.string.Registered_successfully);
			final String st7 = getResources().getString(R.string.network_anomalies);
			final String st8 = getResources().getString(R.string.User_already_exists);
			final String st9 = getResources().getString(R.string.registration_failed_without_permission);
			final String st10 = getResources().getString(R.string.Registration_failed);
			
			new Thread(new Runnable(){
				@Override
				public void run() {
					try {
						// 调用sdk注册方法
						Log.d(TAG, "开始=====");
						EMChatManager.getInstance().createAccountOnServer(hxid, password);
						Log.d(TAG, "结束=====");
						runOnUiThread(new Runnable(){

							@Override
							public void run() {
								if(!RegisterActivity.this.isFinishing()){
									dialog.dismiss();
								}
								// 保存用户名(缓存，本地)
								WeixinApplication.getInstance().setUserName(hxid);
								
								 Toast.makeText(getApplicationContext(), st6, 0).show();
								 finish();  //关闭注册activity
							}
						});
					} catch (final EaseMobException e) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                if (!RegisterActivity.this.isFinishing())
                                    dialog.dismiss();
                                int errorCode = e.getErrorCode();
                                Log.d(TAG, "errorCode="+errorCode);
                                if (errorCode == EMError.NONETWORK_ERROR) {
                                    Toast.makeText(getApplicationContext(),
                                            st7, Toast.LENGTH_SHORT).show();
                                } else if (errorCode == EMError.USER_ALREADY_EXISTS) {
                                    Toast.makeText(getApplicationContext(),
                                            st8, Toast.LENGTH_SHORT).show();
                                } else if (errorCode == EMError.UNAUTHORIZED) {
                                    Toast.makeText(getApplicationContext(),
                                            st9, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(),
                                            st10 + e.getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
					}
				}
			}).start();
		}
	}
}
