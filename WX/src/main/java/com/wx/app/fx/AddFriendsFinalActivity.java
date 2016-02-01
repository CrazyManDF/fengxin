package com.wx.app.fx;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.easemob.chat.EMContact;
import com.easemob.chat.EMContactManager;
import com.easemob.exceptions.EaseMobException;
import com.wx.app.R;
import com.wx.app.WeixinApplication;
import com.wx.app.fx.others.LocalUserInfo;

/**
 * Created by darren foung on 2016/1/21.
 */
public class AddFriendsFinalActivity extends FragmentActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfriends_final);
        final String hxid = getIntent().getStringExtra("hxid");
        Button tv_send= (Button) this.findViewById(R.id.tv_send);
        final EditText et_reason= (EditText) this.findViewById(R.id.et_reason);

        tv_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addContact(hxid, et_reason.getText().toString().trim());
            }
        });
    }

    private void addContact(final String hxid, final String reason) {
        if(hxid == null || hxid.equals("")){
            return;
        }
        if(WeixinApplication.getInstance().getUserName().equals(hxid)){
            // TODO 不能添加自己
            return;
        }
        // TODO 判断是否是已经添加的好友

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在发送请求...");
        progressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String name = LocalUserInfo.getInstance(AddFriendsFinalActivity.this)
                        .getUerInfo("nick");
                String avatar = LocalUserInfo.getInstance(AddFriendsFinalActivity.this)
                        .getUerInfo("avatar");
                long time = System.currentTimeMillis();

                String tempReason = reason;
                if(tempReason == null || tempReason.equals("")){
                    tempReason = "请求加你为好友";
                }
                String sendReason = name + "66split88" + avatar + "66split88"
                        + String.valueOf(time) + "66split88" + tempReason;
                try {
                    EMContactManager.getInstance().addContact(hxid, sendReason);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            Toast.makeText(AddFriendsFinalActivity.this,
                                    "发送请求成功,等待对方验证", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                } catch (final EaseMobException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            Toast.makeText(AddFriendsFinalActivity.this,
                                    "请求添加好友失败" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    public void back(View view ){
        finish();
    }
}
