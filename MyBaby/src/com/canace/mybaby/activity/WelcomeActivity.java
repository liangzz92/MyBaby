/**
 * WelcomeActivity.java
 * 欢迎闪屏页，第一次打开应用时显示AUTO_HIDE_DELAY_MILLIS
 * @author liangzz
 * 2014-12-2
 */
package com.canace.mybaby.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.canace.mybaby.R;
import com.canace.mybaby.utils.CrashHandler;
import com.canace.mybaby.utils.PreferenceUtils;
import com.umeng.analytics.MobclickAgent;

public class WelcomeActivity extends MyBabyActivity {

	private static final int AUTO_HIDE_DELAY_MILLIS = 2000;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 全屏
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_welcome);
		CrashHandler.getInstance().init(getBaseContext(),
				"http://bug.gzayong.info:9394/report");

		Handler login = new Handler();

		if (PreferenceUtils.getBooleanValue(PreferenceUtils.ISFIRSTLOADING)) {
			login.postDelayed(new SplashHandler(), AUTO_HIDE_DELAY_MILLIS); // 闪屏页

		} else {
			login.postDelayed(new SplashHandler(), 0);

		}

	}

	@Override
	public void onBackPressed() {

	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	class SplashHandler implements Runnable {
		@Override
		public void run() // 跳转到另一个页面
		{
			Intent intent = new Intent();
			if (PreferenceUtils.getBooleanValue(PreferenceUtils.ISLOADED)) {
				intent.setClass(WelcomeActivity.this, LoginActivity.class);
			} else {
				intent.setClass(WelcomeActivity.this, HomePageActivity.class);
			}
			startActivity(intent);
			finish();
		}
	}
}