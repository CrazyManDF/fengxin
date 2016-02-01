package com.wx.app.utils;

import android.os.Environment;

public class CommonUtils {

	/**
	 * 返回应用SDCard目录路径
	 * @return
	 */
	public static String sdcardPath(){
		String sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
		return sdcardPath + "/WX/";
	}
}
