package com.wx.app.adapter;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.VoiceMessageBody;
import com.wx.app.fx.ChatActivity;

import java.io.File;

/**
 * Created by darren foung on 2016/4/26.
 */
public class VoicePlayClickListener implements View.OnClickListener  {

    private Activity activity;
    private EMMessage message;
    private ImageView voiceIconView;
    private ImageView iv_read_status;
    private BaseAdapter adapter;
    private VoiceMessageBody voiceBody;

    public static boolean isPlaying = false;
    public static VoicePlayClickListener currentPlayListener = null;

    /**
     *
     * @param message
     * @param v
     * @param iv_read_status
     * @param activity
     */
    public VoicePlayClickListener(EMMessage message, ImageView v, ImageView iv_read_status,
                                  BaseAdapter adapter, Activity activity, String username) {
        this.activity = activity;
        this.message = message;
        this.iv_read_status = iv_read_status;
        this.adapter = adapter;
        this.voiceBody = (VoiceMessageBody) message.getBody();

    }

    @Override
    public void onClick(View v) {
//        if (isPlaying) {
//            if (((ChatActivity) activity).playMsgId != null
//                    && ((ChatActivity) activity).playMsgId.equals(message.getMsgId())) {
//                currentPlayListener.stopPlayVoice();
//                return;
//            }
//            currentPlayListener.stopPlayVoice();
//        }
//
//        if (message.direct == EMMessage.Direct.SEND) {
//            playVoice(voiceBody.getLocalUrl());
//        }  else {
//            if (message.status == EMMessage.Status.SUCCESS) {
//                File file = new File(voiceBody.getLocalUrl());
//                if (file.exists() && file.isFile()){
//                    playVoice(voiceBody.getLocalUrl());
//                }else{
//                    System.err.println("file not exist");
//                }
//            }else if (message.status == EMMessage.Status.INPROGRESS) {
//                Toast.makeText(activity, "正在下载语音，稍后点击", Toast.LENGTH_SHORT).show();
//            } else if (message.status == EMMessage.Status.FAIL) {
//                Toast.makeText(activity, "正在下载语音，稍后点击", Toast.LENGTH_SHORT).show();
//                new AsyncTask<Void, Void, Void>(){
//
//                    @Override
//                    protected void onPostExecute(Void aVoid) {
//                        super.onPostExecute(aVoid);
//                        adapter.notifyDataSetChanged();
//                    }
//
//                    @Override
//                    protected Void doInBackground(Void... params) {
//                        EMChatManager.getInstance().asyncFetchMessage(message);
//                        return null;
//                    }
//
//                }.execute();
//            }
//        }
    }
}
