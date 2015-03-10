/**
 * FeedBackAgent1.java
 * 使用友盟用户反馈组件，但自定义反馈页面
 * @author liangzz
 * 2014-12-2
 */
package com.canace.mybaby.activity;

import android.content.Context;
import android.content.Intent;

public class FeedBackAgent1 extends com.umeng.fb.FeedbackAgent {

	private final Context mContext;

	public FeedBackAgent1(Context arg0) {
		super(arg0);
		mContext = arg0;

	}

	@Override
	public void startFeedbackActivity() {
		mContext.startActivity(new Intent(mContext, ConversationActivity1.class));
	}
}