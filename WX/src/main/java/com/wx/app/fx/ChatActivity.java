package com.wx.app.fx;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.wx.app.R;
import com.wx.app.adapter.MessageAdapter;
import com.wx.app.fx.others.LocalUserInfo;

public class ChatActivity extends BaseActivity {

    public static final int CHATTYPE_SINGLE = 1;
    public static final int CHATTYPE_GROUP = 2;

    private static final String TAG = "ChatActivity";
    private ListView listView;
    private Button buttonSetModeVoice;
    private Button btnMore;
    private Button buttonSend;
    private EditText mEditTextContent;
    private RelativeLayout edittext_layout;
    private InputMethodManager inputMethodManager;
    private String myUserNick;
    private String myUserAvatar;
    private EMConversation conversation;
    private int chatType;
    private String toChatUsername;
    private MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        myUserNick = LocalUserInfo.getInstance(ChatActivity.this).getUerInfo("nick");
        myUserAvatar = LocalUserInfo.getInstance(ChatActivity.this).getUerInfo("avatar");

        initView();
        setUpView();
    }

    private void initView() {
        listView = (ListView) findViewById(R.id.list);
        buttonSetModeVoice = (Button) findViewById(R.id.btn_set_mode_voice);
        btnMore = (Button) findViewById(R.id.btn_more);
        buttonSend = (Button) findViewById(R.id.btn_send);
        mEditTextContent = (EditText) findViewById(R.id.et_sendmessage);
        edittext_layout = (RelativeLayout) findViewById(R.id.edittext_layout);
    }

    private void setUpView() {
        //进入聊天窗口  隐藏输入法
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        edittext_layout.requestFocus();

        //获取聊天对象信息
        chatType = getIntent().getIntExtra("chatType", CHATTYPE_SINGLE);
        if(chatType == CHATTYPE_SINGLE){
            toChatUsername = getIntent().getStringExtra("userId");
            String toChatUserNick = getIntent().getStringExtra("userNick");
            ((TextView) findViewById(R.id.name)).setText(toChatUserNick);
        } else {
            // TODO: 2016/4/20 群聊
        }

        //获取聊天会话
        conversation = EMChatManager.getInstance().getConversation(toChatUsername);

        //设置消息界面
        messageAdapter = new MessageAdapter(this, toChatUsername, chatType);
        listView.setAdapter(messageAdapter);

        // 设置文本消息框
        mEditTextContent.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    edittext_layout.setBackgroundResource(R.drawable.input_bar_bg_active);
                } else {
                    edittext_layout.setBackgroundResource(R.drawable.input_bar_bg_normal);
                }
            }
        });

        mEditTextContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mEditTextContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(TextUtils.isEmpty(s)){
                    btnMore.setVisibility(View.VISIBLE);
                    buttonSend.setVisibility(View.GONE);
                }else{
                    btnMore.setVisibility(View.GONE);
                    buttonSend.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                return false;
            }
        });
    }

    public void onClick(View view){
        String msg = mEditTextContent.getText().toString();
        sendText(msg);
    }

	/**
     * 发送文本消息
     * @param msg
     */
    private void sendText(String msg) {
        if (msg.length() > 0) {
            EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
            // 如果是群聊，设置chattype,默认是单聊
            if (chatType == CHATTYPE_GROUP)
                message.setChatType(EMMessage.ChatType.GroupChat);

            TextMessageBody txtBody = new TextMessageBody(msg);
            // 设置消息body
            message.addBody(txtBody);
            // 设置要发给谁,用户username或者群聊groupid
            message.setReceipt(toChatUsername);
            message.setAttribute("useravatar", myUserAvatar);
            message.setAttribute("usernick", myUserNick);
            // 把messgage加到conversation中
            conversation.addMessage(message);
            //通知adapter有消息变动，adapter会根据加入的这条message显示消息和调用sdk的发送方法
            messageAdapter.refresh();
            mEditTextContent.setText("");
//            setResult(RESULT_OK);
        }
    }

    private void hideKeyboard() {
        // 若是==SOFT_INPUT_STATE_VISIBLE不行
        if(getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN){
            if(getCurrentFocus() != null){
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }


    /**
     * 消息广播接收者
     *
     */
    private class NewMessageBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String username = intent.getStringExtra("from");
            String msgid = intent.getStringExtra("msgid");


        }
    }

}
