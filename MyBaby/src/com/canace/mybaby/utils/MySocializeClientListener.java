/**
 * MySocializeClientListener.java
 * 设置页面：注销事件添加MySocializeClientListener
 * 完成后发出message通知主线程进行后续操作
 * @author liangzz
 * 2014-12-2
 */
package com.canace.mybaby.utils;

import android.os.Handler;
import android.util.Log;

import com.canace.mybaby.activity.SettingActivity;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.listener.SocializeListeners.SocializeClientListener;

public class MySocializeClientListener implements SocializeClientListener {

	private final Handler mHandler;

	public MySocializeClientListener(Handler handler) {
		mHandler = handler;
	}

	@Override
	public void onComplete(int status, SocializeEntity arg1) {
		// TODO Auto-generated method stub
		mHandler.sendEmptyMessage(SettingActivity.DELETE_AUTH);
		if (status == 200) {
			if (CommonsUtil.DEBUG)
				Log.d("删除授权", "删除成功.");
		} else {
			if (CommonsUtil.DEBUG)
				Log.d("删除授权", "删除失败.");
		}
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub

	}

}