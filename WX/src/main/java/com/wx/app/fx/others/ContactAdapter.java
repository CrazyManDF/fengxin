package com.wx.app.fx.others;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.wx.app.Constant;
import com.wx.app.R;
import com.wx.app.domain.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by egrid on 2016/2/2.
 */
public class ContactAdapter extends ArrayAdapter<User> implements SectionIndexer{

	private static final String TAG = "ContactAdapter";
	private List<User> userList;
	private int res;
	private List<User> copyUserList;
	private final LayoutInflater inflater;
	private final LoadUserAvatar avatarLoader;
	private SparseIntArray positionOfSection;
	private SparseIntArray sectionOfPosition;

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
		TextView tv_Header = (TextView) convertView.findViewById(R.id.header);
		View view_temp = (View) convertView.findViewById(R.id.view_temp);

		User user = getItem(position);
		if (user == null)
			Log.d("ContactAdapter", position + "");

		String header = user.getHeader();
		String usernick = user.getNick();
		String useravatar = user.getAvatar();

		if(position != 0 && header.equals(getItem(position-1).getHeader())){
			// TODO: 2016/3/5 此处没完成
			tv_Header.setVisibility(View.GONE);
			view_temp.setVisibility(View.VISIBLE);
		}else{
			tv_Header.setVisibility(View.VISIBLE);
			tv_Header.setText(header);
			view_temp.setVisibility(View.GONE);
		}

		// 显示申请与通知item
		nameTextview.setText(usernick);
		iv_avatar.setImageResource(R.drawable.default_useravatar);
		showUserAvatar(iv_avatar, useravatar);

		return convertView;
	}

	@Override
	public User getItem(int position) {
		return super.getItem(position);
	}

	@Override
	public int getCount() {
		return super.getCount();
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


	@Override
	public Object[] getSections() {

		positionOfSection = new SparseIntArray();
		sectionOfPosition = new SparseIntArray();

		int count = getCount();
		//list第一位为占位，是"";
		ArrayList<String> list = new ArrayList<String>();
		list.add("");
		positionOfSection.put(0, 0);
		sectionOfPosition.put(0, 0);
		//遍历联系人，将header不重复的存入list
		for(int i = 0; i < count; i++){
			String letter = getItem(i).getHeader();
			Log.d(TAG, "contactadapter getsection getHeader:" + letter
					+ " name:" + getItem(i).getUsername());
			int section = list.size() - 1;
			// A1,A2,A3,B1,B2,C1,D1...
			if(list.get(section) != null && !list.get(section).equals(letter)){
				// 当list最后存储的section和这次不同时，添加到list
				Log.d(TAG, "section="+section + "letter=" + letter);
				list.add(letter);  //"",A,B,C,D...
				section ++;
				positionOfSection.put(section, i); //1-0, 2-3,3-5,4-6...
			}
			sectionOfPosition.put(i, section);  //0-1,1-1,2-1,3-2,4-2,5-3,6-4...

		}
		return list.toArray(new String[list.size()]);
	}

	@Override
	public int getPositionForSection(int section) {
		return positionOfSection.get(section);
	}

	@Override
	public int getSectionForPosition(int position) {
		return sectionOfPosition.get(position);
	}

	@Override
	public Filter getFilter() {
		return super.getFilter();
	}
}
