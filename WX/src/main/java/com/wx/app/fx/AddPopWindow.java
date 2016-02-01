package com.wx.app.fx;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.wx.app.R;

/**
 * Created by darren foung on 2016/1/17.
 */
public class AddPopWindow extends PopupWindow {

    public AddPopWindow(final Activity context){
        View contentView = View.inflate(context, R.layout.popupwindow_add, null);
        this.setContentView(contentView);
        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(new ColorDrawable(0x00FF0000));
//        this.setOutsideTouchable(true); // 没有作用，设置setBackgroundDrawable才有作用
        this.setFocusable(true); //获取屏幕焦点
        this.setAnimationStyle(R.style.AnimationPreview);
//        this.update();

        RelativeLayout re_addfriends =(RelativeLayout) contentView.findViewById(R.id.re_addfriends);
        RelativeLayout re_chatroom =(RelativeLayout) contentView.findViewById(R.id.re_chatroom);
        re_addfriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, AddFriendsOneActivity.class));
                AddPopWindow.this.dismiss();
            }
        });
    }

    /**
     * 显示popupWindow
     *
     * @param parent
     */
    public void showPopupWindow(View parent) {
        if (!this.isShowing()) {
            // 以下拉方式显示popupwindow
            Log.d("AddPopWindow","***");
            this.showAsDropDown(parent, 0, 0);
        }
//        else { //点击popup外面会自动dismiss
//            Log.d("AddPopWindow","====");
//            this.dismiss();
//        }
    }
}
