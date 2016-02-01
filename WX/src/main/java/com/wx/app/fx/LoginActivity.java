package com.wx.app.fx;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.exceptions.EaseMobException;
import com.wx.app.Constant;
import com.wx.app.R;
import com.wx.app.WeixinApplication;
import com.wx.app.fx.others.LoadDataFromServer;
import com.wx.app.fx.others.LoadDataFromServer.DataCallback;
import com.wx.app.fx.others.LocalUserInfo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends FragmentActivity {

    public static final String TAG = "LoginActivity";

    private Button btn_qtregister; // 注册按钮
    private Button btn_signin; // 登陆按钮
    private EditText et_telephone; // 电话号码
    private EditText et_password; // 登陆密码
    private ProgressDialog landingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        landingDialog = new ProgressDialog(LoginActivity.this);
        landingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        landingDialog.setMessage(getString(R.string.Is_landing));

        et_telephone = (EditText) findViewById(R.id.et_telephone);
        et_password = (EditText) findViewById(R.id.et_password);
        et_telephone.addTextChangedListener(new TextChange());
        et_password.addTextChangedListener(new TextChange());

        btn_qtregister = (Button) findViewById(R.id.btn_qtregister);
        btn_qtregister.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        btn_signin = (Button) findViewById(R.id.btn_signin);
        btn_signin.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                landingDialog.show();

                String usertel = et_telephone.getText().toString().trim();
                String password = et_password.getText().toString().trim();

                Map<String, String> map = new HashMap<String, String>();
                map.put("usertel", usertel);
                map.put("password", password);

                LoadDataFromServer loadData = new LoadDataFromServer(LoginActivity.this, Constant.URL_Login, map);
                loadData.getData(new DataCallback() {

                    @Override
                    public void OnDataCallback(JSONObject data) {
                        try {
                            int code = data.getInteger("code");
                            if (code == 1) {
                                JSONObject json = data.getJSONObject("user");
                                signIn(json);
                            } else if (code == 2) {
                                landingDialog.dismiss();
                                Toast.makeText(LoginActivity.this, "账号或密码错误...", Toast.LENGTH_SHORT).show();
                            } else if (code == 3) {
                                landingDialog.dismiss();
                                Toast.makeText(LoginActivity.this, "服务器端注册失败...", Toast.LENGTH_SHORT).show();
                            } else {
                                landingDialog.dismiss();
                                Toast.makeText(LoginActivity.this, "服务器繁忙请重试...", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            landingDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "数据解析错误...", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }

                });
            }
        });
    }

    class TextChange implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            boolean sign1 = et_telephone.getText().length() > 0;
            boolean sign2 = et_password.getText().length() > 0;

            if (sign1 && sign2) {
                btn_signin.setTextColor(0xFFFFFFFF);
                btn_signin.setEnabled(true);
            } else {
                btn_signin.setTextColor(0xFFD0EFC6);
                btn_signin.setEnabled(false);
            }
        }
    }

    protected void signIn(final JSONObject json) {

        Log.d(TAG, json.toJSONString());
        final String nick = json.getString("nick");
        final String hxid = json.getString("hxid");
        final String password = json.getString("password");
        // 调用sdk登陆方法登陆聊天服务器
        EMChatManager.getInstance().login(hxid, password, new EMCallBack() {

            @Override
            public void onError(int code, final String message) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        landingDialog.dismiss();
                        Toast.makeText(getApplicationContext(), getString(R.string.Login_failed) + message,
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onSuccess() {
                try {
                    WeixinApplication.getInstance().setUserName(hxid);
                    WeixinApplication.getInstance().setPassword(password);

                    // TODO 处理好友和群组
                    EMChatManager.getInstance().loadAllConversations();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            processContactsAndGroups(json);
                        }
                    });

                    // 进入主页面
                    if (!LoginActivity.this.isFinishing()) {
                        landingDialog.dismiss();
                    }
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    LoginActivity.this.finish();
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            landingDialog.dismiss();
                            WeixinApplication.getInstance().logout(null);
                            Toast.makeText(getApplicationContext(),
                                    R.string.login_failure_failed,
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }
            }
        });
    }

    private void processContactsAndGroups(final JSONObject json) {
        // todo demo中简单的处理成每次登陆都去获取好友username，开发者自己根据情况而定
        try {
            List<String> usernames = EMChatManager.getInstance().getContactUserNames();
            //有好友数据
            if (usernames != null && usernames.size() > 0) {
                String totaluser = usernames.get(0);
                for (int i = 1; i < usernames.size(); i++) {
                    String split = "66split88";
                    totaluser += split + usernames.get(i);
                }
                // TODO: 2016/1/28 判断区别 通知申请  群聊天
                Log.d("totaluser---->>>>>", totaluser);
                Map<String, String> map = new HashMap<String, String>();
                map.put("uids", totaluser);

                LoadDataFromServer task = new LoadDataFromServer(LoginActivity.this,
                        Constant.URL_Friends, map);
                task.getData(new DataCallback() {
                    @Override
                    public void OnDataCallback(JSONObject data) {
                        int code = data.getInteger("code");
                        if (code == 1) {
                            JSONArray jsonArray = data.getJSONArray("friends");

                            saveMyInfo(json);
                            saveFriends(jsonArray);

                        } else if(code == 2){
                            landingDialog.dismiss();
                            Toast.makeText(LoginActivity.this,
                                    "获取好友列表失败,请重试...", Toast.LENGTH_SHORT)
                                    .show();
                        } else {
                            landingDialog.dismiss();
                            Toast.makeText(LoginActivity.this,
                                    "服务器繁忙请重试...", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                });
            }else{
                saveMyInfo(json);

            }
        } catch (EaseMobException e) {
            e.printStackTrace();
        }
    }

    private void saveFriends(JSONArray jsonArray) {
// TODO: 2016/1/28 保存好友信息 
    }

    private void saveMyInfo(JSONObject json) {
        String hxid = json.getString("hxid");
        String fxid = json.getString("fxid");
        String nick = json.getString("nick");
        String avatar = json.getString("avatar");
        String password = json.getString("password");
        String sex = json.getString("sex");
        String region = json.getString("region");
        String sign = json.getString("sign");
        String tel = json.getString("tel");

        LocalUserInfo.getInstance(LoginActivity.this).setUserInfo("hxid", hxid);
        LocalUserInfo.getInstance(LoginActivity.this).setUserInfo("fxid", fxid);
        LocalUserInfo.getInstance(LoginActivity.this).setUserInfo("nick", nick);
        LocalUserInfo.getInstance(LoginActivity.this).setUserInfo("avatar", avatar);
        LocalUserInfo.getInstance(LoginActivity.this).setUserInfo("password", password);
        LocalUserInfo.getInstance(LoginActivity.this).setUserInfo("sex", sex);
        LocalUserInfo.getInstance(LoginActivity.this).setUserInfo("region", region);
        LocalUserInfo.getInstance(LoginActivity.this).setUserInfo("sign", sign);
        LocalUserInfo.getInstance(LoginActivity.this).setUserInfo("tel", tel);

    }

}
