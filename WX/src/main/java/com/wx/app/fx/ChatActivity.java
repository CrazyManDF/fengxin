package com.wx.app.fx;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.VoiceMessageBody;
import com.easemob.util.VoiceRecorder;
import com.wx.app.R;
import com.wx.app.adapter.MessageAdapter;
import com.wx.app.fx.others.LocalUserInfo;

import java.io.File;

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
    private ImageView iv_emoticons_normal;
    private LinearLayout buttonPressToSpeak;
    private Button buttonSetModeKeyboard;
    private View recordingContainer;
    private TextView recordingHint;
    private Drawable[] micImages;

    private Handler micImageHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //切换麦克风图片实现动画
            micImage.setImageDrawable(micImages[msg.what]);
        }
    };
    private ImageView micImage;
    private VoiceRecorder voiceRecorder;

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
        // 动画资源文件,用于录制语音时
        micImages = new Drawable[] {
                getResources().getDrawable(R.drawable.record_animate_01),
                getResources().getDrawable(R.drawable.record_animate_02),
                getResources().getDrawable(R.drawable.record_animate_03),
                getResources().getDrawable(R.drawable.record_animate_04),
                getResources().getDrawable(R.drawable.record_animate_05),
                getResources().getDrawable(R.drawable.record_animate_06),
                getResources().getDrawable(R.drawable.record_animate_07),
                getResources().getDrawable(R.drawable.record_animate_08),
                getResources().getDrawable(R.drawable.record_animate_09),
                getResources().getDrawable(R.drawable.record_animate_10),
                getResources().getDrawable(R.drawable.record_animate_11),
                getResources().getDrawable(R.drawable.record_animate_12),
                getResources().getDrawable(R.drawable.record_animate_13),
                getResources().getDrawable(R.drawable.record_animate_14), };

        listView = (ListView) findViewById(R.id.list);
        buttonSetModeVoice = (Button) findViewById(R.id.btn_set_mode_voice);
        buttonSetModeKeyboard = (Button) findViewById(R.id.btn_set_mode_keyboard);
        btnMore = (Button) findViewById(R.id.btn_more);
        buttonSend = (Button) findViewById(R.id.btn_send);
        mEditTextContent = (EditText) findViewById(R.id.et_sendmessage);
        edittext_layout = (RelativeLayout) findViewById(R.id.edittext_layout);
        iv_emoticons_normal = (ImageView) findViewById(R.id.iv_emoticons_normal);
        buttonPressToSpeak = (LinearLayout) findViewById(R.id.btn_press_to_speak);
        recordingContainer = findViewById(R.id.recording_container);
        recordingHint = (TextView) findViewById(R.id.recording_hint);
        micImage = (ImageView) findViewById(R.id.mic_image);

        voiceRecorder = new VoiceRecorder(micImageHandler);
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

        buttonPressToSpeak.setOnTouchListener(new PressToSpeakListen());


        NewMessageBroadcastReceiver msgReceiver = new NewMessageBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(EMChatManager
                .getInstance().getNewMessageBroadcastAction());
        // 设置广播的优先级别大于Mainacitivity,这样如果消息来的时候正好在chat页面，
        // 直接显示消息，而不是提示消息未读
//        intentFilter.setPriority(5);
        registerReceiver(msgReceiver, intentFilter);
    }

    /**
     * 发送消息按钮
     *
     * @param view
     */
    public void onClick(View view){
        String msg = mEditTextContent.getText().toString();
        sendText(msg);
    }

    /**
     * 显示语音图标按钮
     *
     * @param view
     */
    public void setModeVoice(View view) {
        hideKeyboard();
        view.setVisibility(View.GONE);
        edittext_layout.setVisibility(View.GONE);
        iv_emoticons_normal.setVisibility(View.VISIBLE);
        buttonPressToSpeak.setVisibility(View.VISIBLE);
        buttonSetModeKeyboard.setVisibility(View.VISIBLE);
    }

    /**
     * 显示键盘图标
     *
     * @param view
     */
    public void setModeKeyboard(View view) {
        view.setVisibility(View.GONE);
        edittext_layout.setVisibility(View.VISIBLE);
        buttonSetModeVoice.setVisibility(View.VISIBLE);
        buttonPressToSpeak.setVisibility(View.GONE);
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

            String msgFrom  = intent.getStringExtra("from");
            String msgId = intent.getStringExtra("msgid");
            //消息类型，文本，图片，语音消息等,这里返回的值为msg.type.ordinal()。
            //所以消息type实际为是enum类型
            int msgType = intent.getIntExtra("type", 0);
            Log.d("main", "new message id:" + msgId + " from:" + msgFrom
                    + " type:" + msgType);
            // 收到这个广播的时候，message已经在db和内存里了，可以通过id获取mesage对象
            //更方便的方法是通过msgId直接获取整个message
            EMMessage message = EMChatManager.getInstance().getMessage(msgId);
            messageAdapter.refresh();
        }
    }

    private class PressToSpeakListen implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch(event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    try {
                        v.setPressed(true);
                        recordingContainer.setVisibility(View.VISIBLE);
                        recordingHint.setText(getString(R.string.move_up_to_cancel));
                        recordingHint.setBackgroundColor(Color.TRANSPARENT);

                        voiceRecorder.startRecording(null, toChatUsername, getApplicationContext());
                    }catch (Exception e){
                        e.printStackTrace();
                        v.setPressed(false);
                        if (voiceRecorder != null)
                            voiceRecorder.discardRecording();
                        recordingContainer.setVisibility(View.INVISIBLE);
                        Toast.makeText(ChatActivity.this, R.string.recoding_fail,
                                Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    return true;
                case MotionEvent.ACTION_MOVE:
                    if (event.getY() < 0) {
                        recordingHint.setText(getString(R.string.release_to_cancel));
                        recordingHint.setBackgroundResource(R.drawable.recording_text_hint_bg);
                    }else{
                        recordingHint.setText(getString(R.string.move_up_to_cancel));
                        recordingHint.setBackgroundColor(Color.TRANSPARENT);
                    }
                    return true;
                case MotionEvent.ACTION_UP:
                    v.setPressed(false);
                    recordingContainer.setVisibility(View.INVISIBLE);

                    if (event.getY() < 0) {
                        // discard the recorded audio.
                        voiceRecorder.discardRecording();
                    } else {
                        try {
                            int length = voiceRecorder.stopRecoding();
                            if (length > 0) {

                                Log.d(TAG,voiceRecorder.getVoiceFilePath()+"=="
                                        +voiceRecorder.getVoiceFileName(toChatUsername));
                                sendVoice(voiceRecorder.getVoiceFilePath(),
                                        voiceRecorder.getVoiceFileName(toChatUsername),
                                        Integer.toString(length), false);

                            } else if (length == EMError.INVALID_FILE) {
                                Toast.makeText(getApplicationContext(), "无录音权限",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "录音时间太短",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(ChatActivity.this, "发送失败，请检测服务器是否连接",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                    return true;
                default:
                    recordingContainer.setVisibility(View.INVISIBLE);
                    if (voiceRecorder != null)
                        voiceRecorder.discardRecording();
                    return false;
            }
        }
    }

    /**
     * 发送语音
     *
     * @param filePath
     * @param fileName
     * @param length
     * @param isResend
     */
    private void sendVoice(String filePath, String fileName, String length,
                           boolean isResend) {
        if(!new File(filePath).exists()){
            return;
        }

        final EMMessage message = EMMessage.createSendMessage(EMMessage.Type.VOICE);
        // 如果是群聊，设置chattype,默认是单聊
//        if (chatType == CHATTYPE_GROUP)
//            message.setChatType(EMMessage.ChatType.GroupChat);
        message.setReceipt(toChatUsername);
        message.setAttribute("useravatar", myUserAvatar);
        message.setAttribute("usernick", myUserNick);

        int len = Integer.parseInt(length);
        VoiceMessageBody body = new VoiceMessageBody(new File(filePath), len);

        message.addBody(body);

        conversation.addMessage(message);
        messageAdapter.refresh();

        listView.setSelection(listView.getCount() - 1);
//        setResult(RESULT_OK);
    }
}
