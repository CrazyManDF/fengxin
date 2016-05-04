package com.wx.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.FileMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.VoiceMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.wx.app.Constant;
import com.wx.app.R;
import com.wx.app.fx.ChatActivity;

/**
 * Created by darren foung on 2016/4/20.
 */
public class MessageAdapter extends BaseAdapter {

    private static final int MESSAGE_TYPE_RECV_TXT = 0;
    private static final int MESSAGE_TYPE_SENT_TXT = 1;
    private static final int MESSAGE_TYPE_SENT_VOICE = 2;
    private static final int MESSAGE_TYPE_RECV_VOICE = 3;

    private static final String TAG = "MessageAdapter";
    private String username;
    private Context context;

    private EMConversation conversation;
    private LayoutInflater inflater;
    private Activity activity;

    public MessageAdapter(Context context, String username, int chatType){
        this.username = username;
        this.context = context;
        this.conversation = EMChatManager.getInstance().getConversation(username);

        inflater = LayoutInflater.from(context);
        activity = (Activity) context;
    }

    @Override
    public int getCount() {
        return conversation.getMsgCount();
    }

    @Override
    public EMMessage getItem(int position) {
        return conversation.getMessage(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 4;
    }

    @Override
    public int getItemViewType(int position) {
        EMMessage message = getItem(position);
        if (message.getType() == EMMessage.Type.TXT) {
            return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_TXT
                    : MESSAGE_TYPE_SENT_TXT;
        }
        if (message.getType() == EMMessage.Type.VOICE) {
            return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VOICE
                    : MESSAGE_TYPE_SENT_VOICE;
        }
        return -1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        EMMessage message = getItem(position);
        EMMessage.ChatType chatType = message.getChatType();
        EMMessage.Direct msg_dirct = message.direct;

        String fromusernick = "0000";
        String fromuseravatar = "0000";
        try {
            fromusernick = message.getStringAttribute("usernick");
            fromuseravatar = message.getStringAttribute("useravatar");
        } catch (EaseMobException e) {
            e.printStackTrace();
        }

        // TODO: 2016/4/21

        Log.d(TAG,message.getFrom()+"--getFrom-");

        ViewHolder holder;
        if(convertView == null){
            holder = new ViewHolder();
            convertView = createViewByMessage(message, position);

            if (message.getType() == EMMessage.Type.TXT) {
                try {
                    Log.d(TAG, "=9999TXT==");
//                    holder.pb = (ProgressBar) convertView.findViewById(R.id.pb_sending);
//                    holder.staus_iv = (ImageView) convertView.findViewById(R.id.msg_status);
                    holder.head_iv = (ImageView) convertView.findViewById(R.id.iv_userhead);
                    // 这里是文字内容
                    holder.tv = (TextView) convertView.findViewById(R.id.tv_chatcontent);
                    holder.tv_userId = (TextView) convertView.findViewById(R.id.tv_userid);
                } catch (Exception e) {}
            }else if (message.getType() == EMMessage.Type.VOICE) {
                try {
                    Log.d(TAG, "=9999VOICE==");
                    holder.iv = ((ImageView) convertView.findViewById(R.id.iv_voice));
                    holder.iv_read_status = (ImageView) convertView.findViewById(R.id.iv_unread_voice);
                    holder.head_iv = (ImageView) convertView.findViewById(R.id.iv_userhead);
                    holder.tv = (TextView) convertView.findViewById(R.id.tv_length);
                    holder.pb = (ProgressBar) convertView.findViewById(R.id.pb_sending);
                    holder.staus_iv = (ImageView) convertView.findViewById(R.id.msg_status);
                    holder.tv_userId = (TextView) convertView.findViewById(R.id.tv_userid);
                } catch (Exception e) { }
            }
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        switch (message.getType()) {
            case TXT: // 文本
                if (!message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL, false)) {
                    handleTextMessage(message, holder, position);
                } else {
                    // 语音电话
                }
                break;
            case VOICE: // 语音
                handleVoiceMessage(message, holder, position, convertView);
                break;
        }

        return convertView;
    }

	/*创建消息布局*/
    private View createViewByMessage(EMMessage message, int position) {
        switch (message.getType()) {
            case VOICE:
                return message.direct == EMMessage.Direct.RECEIVE ?
                        inflater.inflate(R.layout.row_received_voice, null)
                        : inflater.inflate(R.layout.row_sent_voice, null);
            default:
                return message.direct == EMMessage.Direct.RECEIVE ?
                        inflater.inflate(R.layout.row_received_message, null)
                        : inflater.inflate(R.layout.row_sent_message, null);
        }
    }

    /**
     * 显示文本消息
     * @param message
     * @param holder
     * @param position
	 */
    private void handleTextMessage(EMMessage message, ViewHolder holder, final int position) {
        TextMessageBody txtBody = (TextMessageBody) message.getBody();

        // 设置内容
        holder.tv.setText(txtBody.getMessage(), TextView.BufferType.NORMAL);

        if (message.direct == EMMessage.Direct.SEND) {
            switch (message.status) {
                case SUCCESS: // 发送成功
                    break;
                case FAIL: // 发送失败

                    break;
                case INPROGRESS: // 发送中
                    break;
                default: // 未发送消息
                    Log.d(TAG, "未发送消息");
                    sendMsgInBackground(message, holder);
            }
        }
    }

	/**
     * 发送消息
     * @param message
     * @param holder
     */
    public void sendMsgInBackground(final EMMessage message, final ViewHolder holder) {

//        holder.staus_iv.setVisibility(View.GONE);
//        holder.pb.setVisibility(View.VISIBLE);

        // 调用sdk发送  异步发送方法
        EMChatManager.getInstance().sendMessage(message, new EMCallBack(){

            @Override
            public void onSuccess() {
                Log.d(TAG, "发送成功");
                updateSendedView(message, holder);
            }

            @Override
            public void onError(int code, String error) {
                Log.d(TAG, "发送失败");
                updateSendedView(message, holder);
            }

            @Override
            public void onProgress(int progress, String status) {
                Log.d(TAG, "发送中");
            }
        });
    }

    /**
     * 更新ui上消息发送状态
     *
     * @param message
     * @param holder
     */
    private void updateSendedView(final EMMessage message, final ViewHolder holder) {

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, message.status+"--");
                if (message.status == EMMessage.Status.SUCCESS) {
                    // if (message.getType() == EMMessage.Type.FILE) {
                    // holder.pb.setVisibility(View.INVISIBLE);
                    // holder.staus_iv.setVisibility(View.INVISIBLE);
                    // } else {
                    // holder.pb.setVisibility(View.GONE);
                    // holder.staus_iv.setVisibility(View.GONE);
                    // }

                } else if (message.status == EMMessage.Status.FAIL) {
                    // if (message.getType() == EMMessage.Type.FILE) {
                    // holder.pb.setVisibility(View.INVISIBLE);
                    // } else {
                    // holder.pb.setVisibility(View.GONE);
                    // }
                    // holder.staus_iv.setVisibility(View.VISIBLE);
                    Toast.makeText(activity, activity.getString(R.string.send_fail)
                                    + activity.getString(R.string.connect_failuer_toast),
                            Toast.LENGTH_SHORT).show();
                }

                notifyDataSetChanged();
            }
        });
    }

    /**
     * 语音消息
     *
     * @param message
     * @param holder
     * @param position
     * @param convertView
     */
    private void handleVoiceMessage(final EMMessage message, final ViewHolder holder,
                                    final int position, View convertView) {
        VoiceMessageBody voiceBody = (VoiceMessageBody) message.getBody();
        holder.tv.setText(voiceBody.getLength() + "\"");
        Log.d(TAG,""+holder.iv+",,"+holder.iv_read_status+",,"+username);
        holder.iv.setOnClickListener(new VoicePlayClickListener(message,
                holder.iv, holder.iv_read_status, this, activity, username));

        if(((ChatActivity)activity).playMsgId != null
                && ((ChatActivity) activity).playMsgId.equals(message.getMsgId())
                && VoicePlayClickListener.isPlaying){
            //正在播放当前语音消息
            Log.d(TAG, "正在播放当前语音消息");
//            if (message.direct == EMMessage.Direct.RECEIVE) {
//                holder.iv.setImageResource(R.drawable.voice_from_icon);
//            } else {
//                holder.iv.setImageResource(R.drawable.voice_to_icon);
//            }
//            AnimationDrawable voiceAnimation = (AnimationDrawable) holder.iv.getDrawable();
//            voiceAnimation.start();

        }else {
            if (message.direct == EMMessage.Direct.RECEIVE) {
                holder.iv.setImageResource(R.drawable.chatfrom_voice_playing);
            } else {
                holder.iv.setImageResource(R.drawable.chatto_voice_playing);
            }
        }

        //处理收到的语音
        if (message.direct == EMMessage.Direct.RECEIVE) {
            if (message.isListened()) {
                // 隐藏语音未听标志
                holder.iv_read_status.setVisibility(View.INVISIBLE);
            } else {
                holder.iv_read_status.setVisibility(View.VISIBLE);
            }

            if (message.status == EMMessage.Status.INPROGRESS) {
                holder.pb.setVisibility(View.VISIBLE);
                System.err.println("!!!! back receive");
                ((FileMessageBody) message.getBody()).setDownloadCallback(new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                holder.pb.setVisibility(View.INVISIBLE);
                                notifyDataSetChanged();
                            }
                        });
                    }

                    @Override
                    public void onError(int i, String s) {
                        activity.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                holder.pb.setVisibility(View.INVISIBLE);
                            }
                        });
                    }

                    @Override
                    public void onProgress(int i, String s) {

                    }
                });
            }else{
                holder.pb.setVisibility(View.INVISIBLE);
            }
            return; // 返回，下面是发送的设置
        }

        // until here, deal with send voice msg
        switch (message.status) {
            case SUCCESS:
                holder.pb.setVisibility(View.GONE);
                holder.staus_iv.setVisibility(View.GONE);
                break;
            case FAIL:
                holder.pb.setVisibility(View.GONE);
                holder.staus_iv.setVisibility(View.VISIBLE);
                break;
            case INPROGRESS:
                holder.pb.setVisibility(View.VISIBLE);
                holder.staus_iv.setVisibility(View.GONE);
                break;
            default:
                sendMsgInBackground(message, holder);
        }
    }

    /**
     * 刷新页面
     */
    public void refresh() {
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        ImageView iv;
        TextView tv;
        ProgressBar pb;
        ImageView staus_iv;
        ImageView head_iv;
        TextView tv_userId;
        ImageView playBtn;
        TextView timeLength;
        TextView size;
        LinearLayout container_status_btn;
        LinearLayout ll_container;
        ImageView iv_read_status;
        // 显示已读回执状态
        TextView tv_ack;
        // 显示送达回执状态
        TextView tv_delivered;

        TextView tv_file_name;
        TextView tv_file_size;
        TextView tv_file_download_state;
    }

}
