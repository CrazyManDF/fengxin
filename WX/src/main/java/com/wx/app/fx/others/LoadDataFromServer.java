package com.wx.app.fx.others;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import internal.org.apache.http.entity.mime.MultipartEntity;
import internal.org.apache.http.entity.mime.content.FileBody;
import internal.org.apache.http.entity.mime.content.StringBody;

public class LoadDataFromServer {
	protected static final String TAG = "LoadDataFromServer";
	protected static final int DATA_FROM_SERVER = 1111;
	private String url;
	private Map<String, String> map = null;
	private Context context;

	public LoadDataFromServer(Context context, String url, Map<String, String> map) {
		this.context = context;
		this.url = url;
		this.map = map;
	}

	public void getData(final DataCallback dataCallback) {

		final Handler handler = new LoadHandler(context, dataCallback);

		new Thread() {
			@Override
			public void run() {
				HttpClient client = new DefaultHttpClient();
				HttpPost post = new HttpPost(url);

				MultipartEntity entity = new MultipartEntity();

				Set<String> keys = map.keySet();
				if (keys != null) {
					Iterator<String> iterator = keys.iterator();
					while (iterator.hasNext()) {
						String key = iterator.next();
						String value = map.get(key);
						if (key.equals("file")) {
							File file = new File(value);
							entity.addPart(key, new FileBody(file));
						} else {
							try {
								entity.addPart(key, new StringBody(value, Charset.forName("UTF-8")));
							} catch (UnsupportedEncodingException e) {
								e.printStackTrace();
							}

						}
					}
				}
				post.setEntity(entity);

				client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30 * 1000);
				client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 30 * 1000);
				try {
					Log.d(TAG, entity.toString());
					HttpResponse response = client.execute(post);
					Log.d(TAG, "StatusCode="+response.getStatusLine().getStatusCode());
					if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
						BufferedReader reader = new BufferedReader(
								new InputStreamReader(response.getEntity().getContent(), Charset.forName("UTF-8")));
						StringBuilder sb = new StringBuilder();
						String s = "";
						while ((s = reader.readLine()) != null) {
							sb.append(s);
						}
						String jsonString = sb.toString();
						Log.d(TAG, "服务器返回的json:"+sb.toString());

						JSONObject jsonObject = JSONObject.parseObject(jsonString);
						Message msg = handler.obtainMessage();
						msg.what = DATA_FROM_SERVER;
						msg.obj = jsonObject;
						handler.sendMessage(msg);
					}

				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			};
		}.start();
	}

	static class LoadHandler extends Handler {

		private DataCallback dataCallback;
		private Context context;

		public LoadHandler(Context context, DataCallback dataCallback) {
			this.dataCallback = dataCallback;
			this.context = context;
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == DATA_FROM_SERVER && dataCallback != null) {
				JSONObject jsonObject = (JSONObject) msg.obj;
				if (jsonObject != null) {
					dataCallback.OnDataCallback(jsonObject);
				}
			}else{
				Toast.makeText(context, "访问服务器出错...", Toast.LENGTH_LONG)
                .show();
			}
		}
	}

	public interface DataCallback {
		public void OnDataCallback(JSONObject jsonObject);
	}
}
