package com.wx.app.fx;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.wx.app.R;
import com.wx.app.db.InviteMessgeDao;
import com.wx.app.domain.InviteMessage;
import com.wx.app.fx.others.NewFriendsAdapter;

import java.util.List;

public class NewFriendsActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friends);

        ImageView iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewFriendsActivity.this.finish();
            }
        });

        TextView tv_add_new_friend = (TextView) findViewById(R.id.tv_add_new_friend);
        tv_add_new_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NewFriendsActivity.this, AddFriendsOneActivity.class));
            }
        });

        TextView tv_search = (TextView) findViewById(R.id.tv_search);
        tv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NewFriendsActivity.this, AddFriendsTwoActivity.class));
            }
        });

        ListView lv_request_friends = (ListView) findViewById(R.id.lv_request_friends);
        InviteMessgeDao messgeDao = new InviteMessgeDao(NewFriendsActivity.this);
        List<InviteMessage> messages =  messgeDao.getMessagesList();
        NewFriendsAdapter adapter = new NewFriendsAdapter(NewFriendsActivity.this, messages);
        lv_request_friends.setAdapter(adapter);

        

    }
}
