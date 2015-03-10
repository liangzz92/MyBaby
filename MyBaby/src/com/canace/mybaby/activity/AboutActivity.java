/**
 * AboutActivity.java
 * 关于页面，展示应用基本信息
 * @author liangzz
 * 2014-12-2
 */
package com.canace.mybaby.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.canace.mybaby.R;
import com.canace.mybaby.utils.CommonsUtil;
import com.canace.mybaby.utils.MyBabyConstants;
import com.umeng.analytics.MobclickAgent;

public class AboutActivity extends MyBabyActivity {
	private ImageView backImageView;
	private TextView versionTextView;
	private TextView aboutTextView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_about);
		versionTextView = (TextView) findViewById(R.id.version);
		versionTextView.setText(MyBabyConstants.ABOUT_VERSION
				+ CommonsUtil.GetVersion(this));
		aboutTextView = (TextView) findViewById(R.id.text_avatar);
		aboutTextView.setText(MyBabyConstants.RETRACT + MyBabyConstants.ABOUT);
		backImageView = (ImageView) findViewById(R.id.button_back);
		backImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				finish();
			}
		});
	}

	@Override
	public void onBackPressed() {
		backImageView.performClick();
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}
}