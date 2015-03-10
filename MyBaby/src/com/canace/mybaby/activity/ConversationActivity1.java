/**
 * ConversationActivity1.java
 * 使用友盟用户反馈组件，但自定义反馈页面
 * @author liangzz
 * 2014-12-2
 */
package com.canace.mybaby.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;

import com.canace.mybaby.R;
import com.umeng.analytics.MobclickAgent;

public class ConversationActivity1 extends com.umeng.fb.ConversationActivity {
	private ImageView contactImageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		contactImageView = (ImageView) findViewById(R.id.contact);
		contactImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(ConversationActivity1.this,
						ContactActivity1.class);
				startActivity(intent);
			}
		});
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

}