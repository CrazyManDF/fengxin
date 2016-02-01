package com.wx.app.fx;

import com.wx.app.R;
import com.wx.app.domain.User;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class FragmentFriends extends Fragment{

	private List<User> contactList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = View.inflate(getActivity(), R.layout.fragment_contactlist, null);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		contactList = new ArrayList<User>();

		getContactList();
	}

	/*
	获取联系人列表
	 */
	private void getContactList() {
		contactList.clear();
		//TODO 获取本地好友资料
	}
}
