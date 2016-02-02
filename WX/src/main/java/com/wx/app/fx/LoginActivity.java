package com.wx.app.fx;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMGroupManager;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.HanziToPinyin;
import com.wx.app.Constant;
import com.wx.app.R;
import com.wx.app.WeixinApplication;
import com.wx.app.domain.User;
import com.wx.app.domain.UserDao;
import com.wx.app.fx.others.LoadDataFromServer;
import com.wx.app.fx.others.LoadDataFromServer.DataCallback;
import com.wx.app.fx.others.LocalUserInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        Log.d(TAG, "signIn="+json.toJSONString());
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
                    // 登陆成功，保存用户名密码
                    WeixinApplication.getInstance().setUserName(hxid);
                    WeixinApplication.getInstance().setPassword(password);

                    runOnUiThread(new Runnable() {
                        public void run() {
                            landingDialog.setMessage(getString(R.string.list_is_for));
                        }
                    });
                    // TODO 处理好友和群组,怎么运作的？？
                    EMGroupManager.getInstance().loadAllGroups();
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
            //根据EMC返回的好友数据，申请服务器查询好友资料
            List<String> usernames = EMChatManager.getInstance().getContactUserNames();
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
        Log.d(TAG, jsonArray.toJSONString());

        Map<String, User>  map = new HashMap<String, User>();
        if(jsonArray != null){
            for(int i=0; i < jsonArray.size(); i++){
                //{"sign":"0","region":"0","hxid":"11236867","acount":"0.00",
                // "time":"2016-01-24 11:26:56","sex":"0","nick":"erer","fxid":"0",
                // "tel":"1502","money":"10.00","avatar":"0","password":"1502fei","paypw":""},
                JSONObject object = jsonArray.getJSONObject(i);
                String hxid = object.getString("hxid");
                String fxid = object.getString("fxid");
                String nick = object.getString("nick");
                String avatar = object.getString("avatar");
                String sex = object.getString("sex");
                String region = object.getString("region");
                String sign = object.getString("sign");
                String tel = object.getString("tel");

                User user = new User();
                user.setFxid(fxid);
                user.setUsername(hxid);
                user.setBeizhu("");
                user.setNick(nick);
                user.setRegion(region);
                user.setSex(sex);
                user.setTel(tel);
                user.setSign(sign);
                user.setAvatar(avatar);
                setUserHearder(hxid, user);
                map.put(hxid, user);
            }
        }
        // TODO: 2016/2/2 catch

        // 添加user"申请与通知"
        User newFriends = new User();
        newFriends.setUsername(Constant.NEW_FRIENDS_USERNAME);
        String strChat = getResources().getString(
                R.string.Application_and_notify);
        newFriends.setNick(strChat);
        newFriends.setBeizhu("");
        newFriends.setFxid("");
        newFriends.setHeader("");
        newFriends.setRegion("");
        newFriends.setSex("");
        newFriends.setTel("");
        newFriends.setSign("");
        newFriends.setAvatar("");
        map.put(Constant.NEW_FRIENDS_USERNAME, newFriends);
        // 添加"群聊"
        User groupUser = new User();
        String strGroup = getResources().getString(R.string.group_chat);
        groupUser.setUsername(Constant.GROUP_USERNAME);
        groupUser.setNick(strGroup);
        groupUser.setHeader("");
        groupUser.setNick(strChat);
        groupUser.setBeizhu("");
        groupUser.setFxid("");
        groupUser.setHeader("");
        groupUser.setRegion("");
        groupUser.setSex("");
        groupUser.setTel("");
        groupUser.setSign("");
        groupUser.setAvatar("");
        map.put(Constant.GROUP_USERNAME, groupUser);
        // 存入内存
        WeixinApplication.getInstance().setContactList(map);
        // 存入db
        UserDao userDao = new UserDao(LoginActivity.this);
        List<User> users = new ArrayList<User>(map.values());
        userDao.saveContactList(users);

        // 获取黑名单列表
        try {
            List<String> blackList = EMContactManager.getInstance()
                    .getBlackListUsernamesFromServer();
            EMContactManager.getInstance().saveBlackList(blackList);

            // 获取群聊列表(群聊里只有groupid和groupname等简单信息，不包含members),
            // sdk会把群组存入到内存和db中
            EMGroupManager.getInstance().getGroupsFromServer();
//            addContact("11223354");

        } catch (EaseMobException e) {
            e.printStackTrace();
        }
    }

    /*设置hearder属性，方便通讯中对联系人按header分类显示，
    以及通过右侧ABCD...字母栏快速定位联系人*/
    private void setUserHearder(String hxid, User user) {
        String headerName = null;
        if(!TextUtils.isEmpty(user.getNick())){
            headerName = user.getNick();
        }else{
            headerName = user.getUsername();
        }
        headerName = headerName.trim();

        if(hxid.equals(Constant.NEW_FRIENDS_USERNAME)){
            user.setHeader("");  //新朋友
        }else if(Character.isDigit(headerName.charAt(0))){
            user.setHeader("#"); //数字
        }else{
            //headerName的首字母英文大写
            String upperPinyin = HanziToPinyin.getInstance().get(headerName.substring(0,1))
                    .get(0).target.substring(0,1).toUpperCase();
            user.setHeader(upperPinyin);

//            char header = user.getHeader().toLowerCase().charAt(0);
//            if (header < 'a' || header > 'z') {
//                user.setHeader("#");
//            }
        }
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
