package com.wx.app.fx;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.wx.app.R;

public class ChatActivity extends Activity {

    private static final String TAG = "ChatActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
    }

    public void back(View view){
        Log.d(TAG, "onClick  back");
    }
}
