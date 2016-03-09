package com.wx.app.fx;

import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.chat.ConnectionListener;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactListener;
import com.easemob.chat.EMContactManager;
import com.easemob.util.NetUtils;
import com.wx.app.Constant;
import com.wx.app.R;
import com.wx.app.WeixinApplication;
import com.wx.app.db.InviteMessgeDao;
import com.wx.app.domain.InviteMessage;
import com.wx.app.domain.User;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.bitlet.weupnp.Main;

import java.util.List;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    private ImageView iv_search;
    private ImageView iv_add;
    private RelativeLayout fragment_contaner;
    private RelativeLayout re_weixin;
    private RelativeLayout re_contact_list;
    private RelativeLayout re_find;
    private RelativeLayout re_me;
    public FragmentCoversation fragCoversation;
    private Fragment fragFind;
    private Fragment fragFriends;
    private Fragment fragProfile;
    private ImageView iv_weixin_tab;
    private TextView tv_weixin_tab;
    private TextView unread_msg_number;
    private ImageView iv_contact_tab;
    private TextView tv_contact_tab;
    private TextView unread_contact_number;
    private ImageView iv_find_tab;
    private TextView tv_find_tab;
    private TextView unread_find_number;
    private ImageView iv_me_tab;
    private TextView tv_me_tab;
    private TextView unread_me_number;

    private int currentTabIndex = 0;
    private Fragment[] fragments;
    private TextView[] textViews;
    private ImageView[] imageViews;
    private InviteMessgeDao inviteMessgeDao;
    private AlertDialog.Builder accountRemoveBuilder;
    private AlertDialog.Builder conflictBuilder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_temp);

        initView();
        initListener();
    }

    public void initView() {
        iv_search = (ImageView) findViewById(R.id.iv_search);
        iv_add = (ImageView) findViewById(R.id.iv_add);

        fragment_contaner = (RelativeLayout) findViewById(R.id.fragment_contaner);

        re_weixin = (RelativeLayout) findViewById(R.id.re_weixin);
        re_contact_list = (RelativeLayout) findViewById(R.id.re_contact_list);
        re_find = (RelativeLayout) findViewById(R.id.re_find);
        re_me = (RelativeLayout) findViewById(R.id.re_me);

        imageViews = new ImageView[4];
        imageViews[0] = (ImageView) findViewById(R.id.iv_weixin_tab);
        imageViews[1] = (ImageView) findViewById(R.id.iv_contact_tab);
        imageViews[2] = (ImageView) findViewById(R.id.iv_find_tab);
        imageViews[3] = (ImageView) findViewById(R.id.iv_me_tab);
        imageViews[0].setSelected(true);

        textViews = new TextView[4];
        textViews[0] = (TextView) findViewById(R.id.tv_weixin_tab);
        textViews[1] = (TextView) findViewById(R.id.tv_contact_tab);
        textViews[2] = (TextView) findViewById(R.id.tv_find_tab);
        textViews[3] = (TextView) findViewById(R.id.tv_me_tab);
        textViews[0].setTextColor(0xFF45C01A);

        unread_msg_number = (TextView) findViewById(R.id.unread_msg_number);
        unread_contact_number = (TextView) findViewById(R.id.unread_contact_number);
        unread_find_number = (TextView) findViewById(R.id.unread_find_number);
        unread_me_number = (TextView) findViewById(R.id.unread_me_number);

        re_weixin.setOnClickListener(new onTabClicked());
        re_contact_list.setOnClickListener(new onTabClicked());
        re_find.setOnClickListener(new onTabClicked());
        re_me.setOnClickListener(new onTabClicked());

        fragCoversation = new FragmentCoversation();
        fragFriends = new FragmentFriends();
        fragFind = new FragmentFind();
        fragProfile = new FragmentProfile();
        fragments = new Fragment[]{fragCoversation, fragFriends, fragFind, fragProfile};

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.fragment_contaner, fragCoversation, "FRAG_COVERSATION");
        transaction.add(R.id.fragment_contaner, fragFind, "FRAG_FIND");
        transaction.add(R.id.fragment_contaner, fragFriends, "FRAG_FRIENDS");
        transaction.add(R.id.fragment_contaner, fragProfile, "FRAG_PROFILE");
        transaction.show(fragCoversation).hide(fragFind).hide(fragFriends).hide(fragProfile);
        transaction.commit();

        inviteMessgeDao = new InviteMessgeDao(MainActivity.this);

//        IntentFilter cmdMessageIntentFilter = new IntentFilter(EMChatManager
//                .getInstance().getCmdMessageBroadcastAction());
//        cmdMessageIntentFilter.setPriority(3);
//        registerReceiver(cmdMessageReceiver, cmdMessageIntentFilter);
//
//        NewMessageBroadcastReceiver msgReceiver = new NewMessageBroadcastReceiver();
//        IntentFilter intentFilter = new IntentFilter(EMChatManager
//                .getInstance().getNewMessageBroadcastAction());
//        intentFilter.setPriority(3);
//        registerReceiver(msgReceiver, intentFilter);

        // TODO: 2016/3/7 不能收到好友请求消息消息
//        // setContactListener监听联系人的变化等
        EMContactManager.getInstance().setContactListener(new MyContactListener());
//        // 注册一个监听连接状态的listener
        EMChatManager.getInstance().addConnectionListener(new MyConnectionListener());
//        // 通知sdk，UI 已经初始化完毕，注册了相应的receiver和listener, 可以接受broadcast了
        EMChat.getInstance().setAppInited();
    }

    private void initListener() {

        final AddPopWindow addPopup = new AddPopWindow(MainActivity.this);
        iv_add.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                addPopup.showPopupWindow(iv_add);
            }
        });
    }

    class onTabClicked implements OnClickListener {

        @Override
        public void onClick(View v) {
            int index = 0;
            switch (v.getId()) {
                case R.id.re_weixin:
                    index = 0;
                    break;
                case R.id.re_contact_list:
                    index = 1;
                    break;
                case R.id.re_find:
                    index = 2;
                    break;
                case R.id.re_me:
                    index = 3;
                    break;
                default:
                    break;
            }

            if (currentTabIndex != index) {
                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction trasaction = manager.beginTransaction();
                if (!fragments[index].isAdded()) {
                    trasaction.add(R.id.fragment_contaner, fragments[index]);
                }
                trasaction.hide(fragments[currentTabIndex]).show(fragments[index]);
                trasaction.commit();

                imageViews[currentTabIndex].setSelected(false);
                imageViews[index].setSelected(true);
                textViews[currentTabIndex].setTextColor(0xFF999999);
                textViews[index].setTextColor(0xFF45C01A);
                currentTabIndex = index;
            }

        }
    }

    private BroadcastReceiver cmdMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            abortBroadcast();
        }
    };

    private class NewMessageBroadcastReceiver extends  BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "================");
        }
    }

    private class MyContactListener implements EMContactListener  {

        @Override
        public void onContactAdded(List<String> list) {
            Log.d(TAG, "onContactAdded()======="+list.size());
            for(String str : list){
                Log.d(TAG, str);
            }
        }

        @Override
        public void onContactDeleted(List<String> list) {
            Log.d(TAG, "onContactDeleted()=======");
        }

        @Override
        public void onContactInvited(String username, String reason) {
            List<InviteMessage> messages = inviteMessgeDao.getMessagesList();
            for (InviteMessage msg : messages) {
                // TODO: 2016/1/24 处理一下
//                if(){
                Log.d(TAG, msg.getId()+"++");
//                }
            }
            // 封装请求
            InviteMessage msg = new InviteMessage();
            msg.setFrom(username);
            msg.setReason(reason);
            msg.setStatus(InviteMessage.InviteMessageStatus.BEINVITED);
            msg.setTime(System.currentTimeMillis());
            Log.d(TAG, username + "请求加你==为好友,reason: " + reason);
            notifyNewInviteMessage(msg);
        }

        @Override
        public void onContactAgreed(String s) {
            Log.d(TAG, "onContactAgreed()=======");
        }

        @Override
        public void onContactRefused(String s) {
            Log.d(TAG, "onContactRefused()=======");
        }
    }

    private void notifyNewInviteMessage(InviteMessage msg) {
        saveInviteMsg(msg);
        // TODO: 2016/1/24  提示信息
        // TODO: 2016/1/24 刷新界面
    }

    /**
     * 保存邀请等msg
     *
     * @param msg
     */
    private void saveInviteMsg(InviteMessage msg) {
        // 保存msg
        inviteMessgeDao.saveMessage(msg);
        // 未读数加 1
        User user = WeixinApplication.getInstance().getContactList()
                .get(Constant.NEW_FRIENDS_USERNAME);
//        if (user.getUnreadMsgCount() == 0){
            user.setUnreadMsgCount(user.getUnreadMsgCount() + 1);
//        }
    }

    private class MyConnectionListener implements EMConnectionListener {

        @Override
        public void onConnected() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    fragCoversation.errorItem.setVisibility(View.GONE);
                }
            });
        }

        @Override
        public void onDisconnected(final int error) {
            final String st1 = getResources().getString(
                    R.string.Less_than_chat_server_connection);
            final String st2 = getResources().getString(
                    R.string.the_current_network);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (error == EMError.USER_REMOVED) {
                        // 显示帐号已经被移除
                        showAccountRemovedDialog();
                    } else if (error == EMError.CONNECTION_CONFLICT) {
                        // 显示帐号在其他设备登陆dialog
                        showConflictDialog();
                    } else {
                        fragCoversation.errorItem.setVisibility(View.VISIBLE);
                        if (NetUtils.hasNetwork(MainActivity.this))
                            fragCoversation.errorText.setText(st1);
                        else
                            fragCoversation.errorText.setText(st2);

                    }
                }
            });
        }
    }

    /* 显示帐号在其他设备登陆dialog */
    private void showConflictDialog() {
        WeixinApplication.getInstance().logout(null);
        String str = getResources().getString(R.string.Logoff_notification);
        if(!MainActivity.this.isFinishing()){
            if(conflictBuilder == null){
                conflictBuilder = new AlertDialog.Builder(MainActivity.this);
            }
            conflictBuilder.setTitle(str);
            conflictBuilder.setMessage(getString(R.string.connect_conflict));
            conflictBuilder.setCancelable(false);
            conflictBuilder.setPositiveButton(R.string.ok,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                            MainActivity.this.finish();
                        }
                    });
            conflictBuilder.create().show();

        }
    }

    /* 显示帐号已经被移除 */
    private void showAccountRemovedDialog() {
        WeixinApplication.getInstance().logout(null);
        String st5 = getResources().getString(R.string.Remove_the_notification);
        if(!MainActivity.this.isFinishing()){
            if(accountRemoveBuilder == null ){
                accountRemoveBuilder = new AlertDialog.Builder(MainActivity.this);
            }
            accountRemoveBuilder.setTitle(st5);
            accountRemoveBuilder.setMessage(getResources().getString(R.string.em_user_remove));
            accountRemoveBuilder.setCancelable(false);
            accountRemoveBuilder.setPositiveButton(R.string.ok,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            accountRemoveBuilder = null;
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                            MainActivity.this.finish();
                        }
                    });
            accountRemoveBuilder.create().show();
        }
    }
}
