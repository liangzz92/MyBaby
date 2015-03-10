/**
 * MyBabyActivity.java
 * 应用Activity基类，进行数据库连接、检查更新等基础操作
 * @author liangzz
 * 2014-12-2
 */
package com.canace.mybaby.activity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.Window;
import android.widget.Toast;

import com.canace.mybaby.utils.CommonsUtil;
import com.umeng.fb.FeedbackAgent;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

public class MyBabyActivity extends Activity {

	public static boolean mPause;
	private long exitTime = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		// 固定屏幕为竖直方向，禁止Activity自动根据物理传感器进行横竖屏切换
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		CommonsUtil.initDBSettings(this);
		FeedbackAgent agent = new FeedbackAgent(this);
		agent.sync();
		UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {

			@Override
			public void onUpdateReturned(int updateStatus,
					UpdateResponse response) {
				if (updateStatus == UpdateStatus.Yes) {

					UmengUpdateAgent.showUpdateDialog(MyBabyActivity.this,
							response);
				}

			}
		});
		UmengUpdateAgent.update(this);
	}

	@Override
	public void onBackPressed() {

		if ((System.currentTimeMillis() - exitTime) > 2000) {

			Toast.makeText(getApplicationContext(), "再按一次退出程序",
					Toast.LENGTH_SHORT).show();
			exitTime = System.currentTimeMillis();
		} else {
			finish();

		}

	}

	@Override
	protected void onResume() {
		mPause = false;
		super.onResume();
		CommonsUtil.initDBSettings(this);

	}

	@Override
	protected void onPause() {
		mPause = true;
		super.onPause();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}

	public boolean isPaused() {
		return mPause;
	}

}
