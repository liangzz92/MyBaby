/**
 * MyContentTextView.java
 * 重写TextView控件，实现文本统一设置字体
 * @author liangzz
 * 2014-12-2
 */
package com.canace.mybaby.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.canace.mybaby.utils.CommonsUtil;
import com.canace.mybaby.utils.PreferenceUtils;

public class MyContentTextView extends TextView {

	public MyContentTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initTypeFace(context);
	}

	public MyContentTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initTypeFace(context);

	}

	public MyContentTextView(Context context) {
		super(context);
		initTypeFace(context);

	}

	private void initTypeFace(Context context) {
		if (PreferenceUtils.getIntValue(PreferenceUtils.SYSTEM_VERSION) > 8) {
			setTypeface(CommonsUtil.getContentTypeface(context));
		}
	}

}