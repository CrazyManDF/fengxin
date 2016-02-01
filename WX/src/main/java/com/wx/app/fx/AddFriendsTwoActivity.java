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
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.wx.app.Constant;
import com.wx.app.R;
import com.wx.app.fx.others.LoadDataFromServer;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by darren foung on 2016/1/17.
 */
public class AddFriendsTwoActivity extends FragmentActivity {

    private static final String TAG = "AddFriendsTwoActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfriends_two);

        final RelativeLayout rl_search = (RelativeLayout) findViewById(R.id.rl_search);
        final TextView tv_find = (TextView) rl_search.findViewById(R.id.tv_find);
        final EditText et_search = (EditText) findViewById(R.id.et_search);
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG, s.toString());
                if (s.length() > 0) {
                    rl_search.setVisibility(View.VISIBLE);
                    tv_find.setText(et_search.getText().toString().trim());
                } else {
                    rl_search.setVisibility(View.GONE);
                    tv_find.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        rl_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uid = et_search.getText().toString().trim();
                if (TextUtils.isEmpty(uid)) {
                    return;
                }
                searchUser(uid);
            }
        });
    }

    private void searchUser(String uid) {
        final ProgressDialog dialog = new ProgressDialog(AddFriendsTwoActivity.this);
        dialog.setMessage("正在查找联系人...");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.show();
        Map<String, String> map = new HashMap<String, String>();
        map.put("uid", uid);
        LoadDataFromServer task = new LoadDataFromServer(AddFriendsTwoActivity.this,
                Constant.URL_Search_User, map);
        task.getData(new LoadDataFromServer.DataCallback() {
            @Override
            public void OnDataCallback(JSONObject data) {
                try {
                    dialog.dismiss();
                    int code = data.getInteger("code");
                    if (code == 1) {
                        JSONObject json = data.getJSONObject("user");
                        String nick = json.getString("nick");
                        String avatar = json.getString("avatar");
                        String sex = json.getString("sex");

                        String hxid = json.getString("hxid");

                        Intent intent = new Intent();
                        intent.putExtra("hxid", hxid);
                        intent.putExtra("avatar", avatar);
                        intent.putExtra("nick", nick);

                        intent.putExtra("sex", sex);
                        intent.setClass(AddFriendsTwoActivity.this,
                                UserInfoActivity.class);
                        startActivity(intent);
                    } else if (code == 2) {

                        Toast.makeText(AddFriendsTwoActivity.this, "用户不存在",
                                Toast.LENGTH_SHORT).show();
                    } else if (code == 3) {

                        Toast.makeText(AddFriendsTwoActivity.this,
                                "服务器查询错误...", Toast.LENGTH_SHORT).show();
                    } else {

                        Toast.makeText(AddFriendsTwoActivity.this,
                                "服务器繁忙请重试...", Toast.LENGTH_SHORT).show();
                    }
                }catch (JSONException e) {
                    dialog.dismiss();
                    Toast.makeText(AddFriendsTwoActivity.this, "数据解析错误...",
                            Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
    }

    public void back(View view) {
        AddFriendsTwoActivity.this.finish();
    }
}
