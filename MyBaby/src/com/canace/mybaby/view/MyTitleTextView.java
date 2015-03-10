/**
 * MyTitleTextView.java
 * 重写MyContentTextView空间，实现标题字体统一加粗
 * @author liangzz
 * 2014-12-2
 */
package com.canace.mybaby.view;

import android.content.Context;
import android.util.AttributeSet;

public class MyTitleTextView extends MyContentTextView {

	public MyTitleTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		getPaint().setFakeBoldText(true);
	}

	public MyTitleTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		getPaint().setFakeBoldText(true);

	}

	public MyTitleTextView(Context context) {
		super(context);
		getPaint().setFakeBoldText(true);
	}

}