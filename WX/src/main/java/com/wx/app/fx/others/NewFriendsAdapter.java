package com.wx.app.fx.others;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.wx.app.R;
import com.wx.app.domain.InviteMessage;
import com.wx.app.fx.NewFriendsActivity;

import java.util.List;

/**
 * Created by darren foung on 2016/3/7.
 */
public class NewFriendsAdapter extends BaseAdapter {

    private static final String TAG = "NewFriendsAdapter";
    private Context context;
    private List<InviteMessage> messages;

    public NewFriendsAdapter(Context context, List<InviteMessage> messages){
        this.context = context;
        this.messages = messages;
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public InviteMessage getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null){
            convertView = View.inflate(context, R.layout.item_newfriendsmsag, null);
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.iv_11);
            holder.userName = (TextView) convertView.findViewById(R.id.iv_12);
            holder.reason = (TextView) convertView.findViewById(R.id.iv_13);
            holder.agreeBtn = (Button) convertView.findViewById(R.id.btn_agree);
            convertView.setTag(holder);
        }
        holder = (ViewHolder) convertView.getTag();
        InviteMessage inviteMessage = getItem(position);
        holder.userName.setText(inviteMessage.getId());
        holder.reason.setText(inviteMessage.getReason());
        holder.agreeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "");
            }
        });

        return null;
    }

    static class ViewHolder {
        public ImageView imageView;
        public TextView userName;
        public TextView reason;
        public Button agreeBtn;

    }
}
