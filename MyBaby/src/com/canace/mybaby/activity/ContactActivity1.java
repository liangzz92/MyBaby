/**
 * ContactActivity1.java
 * 使用友盟用户反馈组件，但自定义反馈页面
 * @author liangzz
 * 2014-12-2
 */
package com.canace.mybaby.activity;

import android.os.Bundle;
import android.view.Window;

import com.umeng.analytics.MobclickAgent;

public class ContactActivity1 extends com.umeng.fb.ContactActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);

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