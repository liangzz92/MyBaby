/**
 * MyFetchUserListener.java
 * 设置页面及登录页面：获取登录用户信息事件添加MyFetchUserListener
 * 完成后发出message通知主线程进行后续操作
 * @author liangzz
 * 2014-12-2
 */
package com.canace.mybaby.utils;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.umeng.socialize.bean.SocializeUser;
import com.umeng.socialize.controller.listener.SocializeListeners.FetchUserListener;

public class MyFetchUserListener implements FetchUserListener {

	private final Handler mHandler;
	private final Context mContext;
	private final boolean jump;

	public MyFetchUserListener(Handler handler, Context context, boolean jump) {
		mHandler = handler;
		mContext = context;
		this.jump = jump;
	}

	@Override
	public void onComplete(int status, SocializeUser user) {
		if (user != null && user.mLoginAccount != null
				&& !TextUtils.isEmpty(user.mLoginAccount.getAccountIconUrl())) {

			PreferenceUtils.saveStringValue(PreferenceUtils.USERNAME,
					user.mLoginAccount.getUserName());

			PreferenceUtils.saveStringValue(PreferenceUtils.LOGIN_USID,
					user.mLoginAccount.getUsid());

			PreferenceUtils.saveStringValue(
					PreferenceUtils.LOGIN_PLATFORM,
					MyBabyConstants.getPlatformMap()
							.get(user.mLoginAccount.getPlatform()).toString());

			PreferenceUtils.saveStringValue(PreferenceUtils.AVATARPATH,
					user.mLoginAccount.getAccountIconUrl());
			if (CommonsUtil.DEBUG) {
				Log.d("username", user.mLoginAccount.getUserName());
				Log.d("usid", user.mLoginAccount.getUsid());
				Log.d("platform", user.mLoginAccount.getPlatform());
				Log.d("icon", user.mLoginAccount.getAccountIconUrl());
			}
			if (jump) {
				mHandler.sendEmptyMessage(MyBabyConstants.MSG_FETCH_COMPLETE);
			}

		} else {

			mHandler.sendEmptyMessage(MyBabyConstants.MSG_FETCH_FAILED);
		}

	}

	@Override
	public void onStart() {
		if (CommonsUtil.DEBUG) {
			Log.d("TestData", "获取用户信息开始...");
		}
	}
}