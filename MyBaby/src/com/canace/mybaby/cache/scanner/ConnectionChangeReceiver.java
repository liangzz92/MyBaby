/**
 * ConnectionChangeReceiver.java
 * 监测手机网络连接状态变化，根据用户设置判断是否重启在线监测服务
 * @author liangzz
 * 2014-12-2
 */
package com.canace.mybaby.cache.scanner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;
import android.widget.Toast;

import com.canace.mybaby.activity.MyBabyActivity;
import com.canace.mybaby.utils.CommonsUtil;

public class ConnectionChangeReceiver extends BroadcastReceiver {
	private static final String TAG = "ConnectionChangeReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		CommonsUtil.initDBSettings(context);
		String action = intent.getAction();
		if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
			if (CommonsUtil.DEBUG) {
				Log.d(TAG, "网络状态改变");
			}
			CommonsUtil.initNetState(context);
			if (!MyBabyActivity.mPause) {
				if (CommonsUtil.checkToastOrNot(context)) {
					Toast.makeText(context, "已切换到2G/3G网络，开启智能模式可为您节省流量",
							Toast.LENGTH_SHORT).show();
				}

			}
			if (CommonsUtil.checkOnlineDetectOrNot(context)) {
				OnlineDetectService.startOnlineDetect(context);
			}

		}

	}
}