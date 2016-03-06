package com.wx.app.utils;

import android.content.Context;
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

	public static int dp2px(Context context, int dp){
		float density = context.getResources().getDisplayMetrics().density;
		return (int) (dp * density + 0.5);
	}

	public static int px2dp(Context context, int px){
		float density = context.getResources().getDisplayMetrics().density;
		return (int) (px / density + 0.5);
	}
}
