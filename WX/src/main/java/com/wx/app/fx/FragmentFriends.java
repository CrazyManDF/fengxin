package com.wx.app.fx;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.easemob.chat.EMContactManager;
import com.wx.app.Constant;
import com.wx.app.R;
import com.wx.app.WeixinApplication;
import com.wx.app.domain.User;
import com.wx.app.fx.others.ContactAdapter;
import com.wx.app.widget.Sidebar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FragmentFriends extends Fragment{

	private static final String TAG = "FragmentFriends";
	private List<User> contactList;
	private ListView listView;
	private List<String> blackList;
	private ContactAdapter adapter;
	private TextView tv_total;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = View.inflate(getActivity(), R.layout.fragment_contactlist, null);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		listView = (ListView) getView().findViewById(R.id.list);

		// 黑名单列表
		blackList = EMContactManager.getInstance().getBlackListUsernames();
		// 获取设置contactlist
		contactList = new ArrayList<User>();
		getContactList();

		LayoutInflater inflater = LayoutInflater.from(getActivity());
		View headerView = inflater.inflate(R.layout.item_contact_list_header, null);
		View footerView = inflater.inflate(R.layout. item_contact_list_footer,null);
		listView.addHeaderView(headerView);
		listView.addFooterView(footerView);

		tv_total = (TextView) footerView.findViewById(R.id.tv_total);

		// 设置adapter
		adapter = new ContactAdapter(getActivity(), R.layout.item_contact_list,
				contactList);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Log.d(TAG, "position=" + position + ",size=" + contactList.size());
				if (position != 0 && position != contactList.size() + 1) {
					User user = contactList.get(position - 1);
					startActivity(new Intent(getActivity(), UserInfoActivity.class)
							.putExtra("nick", user.getNick())
							.putExtra("avatar", user.getAvatar())
							.putExtra("sex", user.getSex())
							.putExtra("hxid", user.getUsername()));
				}
			}
		});

		tv_total.setText(String.valueOf(contactList.size()) + getString(R.string.size_friends_suffix));

		Sidebar sidebar = (Sidebar) getView().findViewById(R.id.sidebar);
		sidebar.setListView(listView);
	}

	/*
	获取联系人列表
	 */
	private void getContactList() {
		contactList.clear();
		//获取本地好友资料
		Map<String, User> userMap = WeixinApplication.getInstance().getContactList();
		Set<Map.Entry<String, User>> userEntry = userMap.entrySet();
		Iterator<Map.Entry<String, User>> iterator = userEntry.iterator();
		while(iterator.hasNext()){
			Map.Entry<String, User> entry = iterator.next();
			String key = entry.getKey();
			if(!Constant.NEW_FRIENDS_USERNAME.equals(key)
					&& !Constant.GROUP_USERNAME.equals(key)
					&& !blackList.contains(key)){
				contactList.add(entry.getValue());
			}
		}
		Collections.sort(contactList,new PinyinComparator());
	}

	private class PinyinComparator implements java.util.Comparator<User> {
		@Override
		public int compare(User lhs, User rhs) {
			String headerl = lhs.getHeader();
			String header2 = rhs.getHeader();
			if(isEmpty(headerl) && isEmpty(header2)){
				return 0;
			}
			if(isEmpty(headerl) ){
				return -1;
			}
			if(isEmpty(header2)){
				return 1;
			}
			String char1 = headerl.substring(0,1).toUpperCase();
			String char2 = header2.substring(0,1).toUpperCase();
			return char1.compareTo(char2);
		}
		private boolean isEmpty(String str){
			return "".equals(str.trim());
		}
	}
}
