package com.wx.app.fx.others;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wx.app.Constant;
import com.wx.app.R;
import com.wx.app.domain.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by egrid on 2016/2/2.
 */
public class ContactAdapter extends ArrayAdapter<User>{

	private List<User> userList;
	private int res;
	private List<User> copyUserList;
	private final LayoutInflater inflater;
	private final LoadUserAvatar avatarLoader;

	public ContactAdapter(Context context, int resource, List<User> objects) {
		super(context,resource,objects);
		this.res = resource;
		this.userList = objects;
		copyUserList = new ArrayList<User>();
		copyUserList.addAll(objects);
		inflater = LayoutInflater.from(context);

		avatarLoader = new LoadUserAvatar(context, "/sdcard/WX/");
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			convertView = inflater.inflate(R.layout.item_contact_list,null);
		}
		ImageView iv_avatar = (ImageView) convertView.findViewById(R.id.iv_avatar);
		TextView nameTextview = (TextView) convertView.findViewById(R.id.tv_name);
		TextView tvHeader = (TextView) convertView.findViewById(R.id.header);
		View view_temp = (View) convertView.findViewById(R.id.view_temp);

		User user = getItem(position);
		if (user == null)
			Log.d("ContactAdapter", position + "");

		String header = user.getHeader();
		String usernick = user.getNick();
		String useravatar = user.getAvatar();

//		if(header != null && position == 0
//				&& header.equals(getItem(copyUserList.size()-1).getHeader())){
//
//		}

		// 显示申请与通知item
		nameTextview.setText(usernick);
		iv_avatar.setImageResource(R.drawable.default_useravatar);
		showUserAvatar(iv_avatar, useravatar);

		return convertView;
	}

	private void showUserAvatar(ImageView iv_avatar, String useravatar) {
		if(useravatar == null || useravatar == ""){
			return;
		}
		final String avatar_url = Constant.URL_Avatar + useravatar;
		iv_avatar.setTag(avatar_url);
		if(avatar_url != null && !avatar_url.equals("")){
			Bitmap bitmap = avatarLoader.loadImage(iv_avatar, avatar_url,
					new LoadUserAvatar.ImageDownloadedCallBack() {

						@Override
						public void onImageDownloaded(ImageView imageView,
													  Bitmap bitmap) {
							if (imageView.getTag() == avatar_url) {
								imageView.setImageBitmap(bitmap);

							}
						}

					});
			if (bitmap != null)
				iv_avatar.setImageBitmap(bitmap);
		}
	}

}
