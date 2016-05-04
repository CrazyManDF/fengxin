package com.wx.app.adapter;

import android.app.Activity;
import android.app.Service;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.VoiceMessageBody;
import com.wx.app.R;
import com.wx.app.fx.ChatActivity;

import java.io.File;
import java.io.IOException;

/**
 * Created by darren foung on 2016/4/26.
 */
public class VoicePlayClickListener implements View.OnClickListener  {

    private static final String TAG = "VoicePlayClickListener";
    private Activity activity;
    private EMMessage message;
    private ImageView voiceIconView;
    private ImageView iv_read_status;
    private BaseAdapter adapter;
    private VoiceMessageBody voiceBody;

    public static boolean isPlaying = false;
    public static VoicePlayClickListener currentPlayListener = null;
    private MediaPlayer mediaPlayer;
    private AnimationDrawable voiceAnimation;

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

        voiceIconView = v;

    }

    @Override
    public void onClick(View v) {

        if (isPlaying) {
            if (((ChatActivity) activity).playMsgId != null
                    && ((ChatActivity) activity).playMsgId.equals(message.getMsgId())) {
                // 当前语音正在播放
                currentPlayListener.stopPlayVoice();
                return;
            }
            //有语音在播放
            currentPlayListener.stopPlayVoice();
        }
//
        if (message.direct == EMMessage.Direct.SEND) {
            Log.d(TAG, "语音文件url："+voiceBody.getLocalUrl());
            playVoice(voiceBody.getLocalUrl());
        }  else {
            if (message.status == EMMessage.Status.SUCCESS) {
                File file = new File(voiceBody.getLocalUrl());
                if (file.exists() && file.isFile()){
                    playVoice(voiceBody.getLocalUrl());
                }else{
                    System.err.println("file not exist");
                }
            }else if (message.status == EMMessage.Status.INPROGRESS) {
                Toast.makeText(activity, "正在下载语音，稍后点击", Toast.LENGTH_SHORT).show();
            } else if (message.status == EMMessage.Status.FAIL) {
                Toast.makeText(activity, "正在下载语音，稍后点击", Toast.LENGTH_SHORT).show();
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
            }
        }
    }

    public void playVoice(String filePath) {
        if(!new File(filePath).exists()){
            return;
        }

        ((ChatActivity) activity).playMsgId = message.getMsgId();
        AudioManager audioManager = (AudioManager) activity.getSystemService(Service.AUDIO_SERVICE);
        mediaPlayer = new MediaPlayer();
        if (EMChatManager.getInstance().getChatOptions().getUseSpeaker()) {
//            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
//            audioManager.adjustVolume(AudioManager.ADJUST_LOWER,1);
            audioManager.setMode(AudioManager.MODE_NORMAL);
            audioManager.setSpeakerphoneOn(true);

            mediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
        } else {
            audioManager.setSpeakerphoneOn(false);// 关闭扬声器
            // 把声音设定成Earpiece（听筒）出来，设定为正在通话中
            audioManager.setMode(AudioManager.MODE_IN_CALL);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
        }

        try {
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mediaPlayer.release();
                    mediaPlayer = null;
                    stopPlayVoice(); // stop animation
                }
            });

            isPlaying = true;
            currentPlayListener = this;
            mediaPlayer.start();
            showAnimation();
            // 如果是接收的消息
            if (message.direct == EMMessage.Direct.RECEIVE) {

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopPlayVoice() {
        voiceAnimation.stop();
        if(message.direct == EMMessage.Direct.RECEIVE){
            voiceIconView.setImageResource(R.drawable.chatfrom_voice_playing);
        }else{
            voiceIconView.setImageResource(R.drawable.chatto_voice_playing);
        }

        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        isPlaying = false;
        ((ChatActivity)activity).playMsgId = null;
//        adapter.notifyDataSetChanged();
    }

    // show the voice playing animation
    private void showAnimation() {
        if (message.direct == EMMessage.Direct.RECEIVE) {
            voiceIconView.setImageResource(R.drawable.voice_from_icon);
        } else {
            voiceIconView.setImageResource(R.drawable.voice_to_icon);
        }
        voiceAnimation = (AnimationDrawable) voiceIconView.getDrawable();
        voiceAnimation.start();
    }
}
