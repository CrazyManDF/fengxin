package com.wx.app.fx;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.wx.app.Constant;
import com.wx.app.R;
import com.wx.app.WeixinApplication;
import com.wx.app.fx.others.LoadUserAvatar;

public class UserInfoActivity extends Activity {

    private boolean isFriend = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);

        Button btn_sendmsg = (Button) this.findViewById(R.id.btn_sendmsg);
        ImageView iv_avatar = (ImageView) this.findViewById(R.id.iv_avatar);
        ImageView iv_sex = (ImageView) this.findViewById(R.id.iv_sex);
        TextView tv_name = (TextView) this.findViewById(R.id.tv_name);

        final String hxid = this.getIntent().getStringExtra("hxid");
        final String nick = this.getIntent().getStringExtra("nick");
        final String avatar = this.getIntent().getStringExtra("avatar");
        String sex = this.getIntent().getStringExtra("sex");
        if (nick != null && avatar != null && sex != null && hxid != null) {
            tv_name.setText(nick);
            if (sex.equals("1")) {
                iv_sex.setImageResource(R.drawable.ic_sex_male);
            } else if (sex.equals("2")) {
                iv_sex.setImageResource(R.drawable.ic_sex_female);
            } else {
                iv_sex.setVisibility(View.GONE);
            }
            // 判断是否是好友，显示"发送消息"
            if(WeixinApplication.getInstance().getContactList()
                    .containsKey(hxid)){
                isFriend = true;
                btn_sendmsg.setText("发消息");
            }
            showUserAvatar(iv_avatar, avatar);
        }
        btn_sendmsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO 若是朋友则跳转
                if(isFriend){
                    Intent intent = new Intent();
                    intent.putExtra("userId", hxid);
                    intent.putExtra("userNick", nick);
                    intent.setClass(UserInfoActivity.this, ChatActivity.class);
                    startActivity(intent);
                }else{
                    // 非朋友则添加通讯录
                    Intent intent = new Intent();
                    intent.putExtra("hxid", hxid);
                    intent.setClass(UserInfoActivity.this,
                            AddFriendsFinalActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void showUserAvatar(ImageView imageView, String avatar) {
        LoadUserAvatar avatarLoader = new LoadUserAvatar(this, "/sdcard/fengxin/");

        final String url_avatar = Constant.URL_Avatar + avatar;
        imageView.setTag(url_avatar);
        if (url_avatar != null && !url_avatar.equals("")) {
            Bitmap bitmap = avatarLoader.loadImage(imageView, url_avatar,
                    new LoadUserAvatar.ImageDownloadedCallBack() {
                        @Override
                        public void onImageDownloaded(ImageView imageView, Bitmap bitmap) {
                            if(imageView.getTag() == url_avatar){
                                imageView.setImageBitmap(bitmap);
                            }
                        }
                    });
            if(bitmap != null){
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    public void back(View view) {
        finish();
    }
}
