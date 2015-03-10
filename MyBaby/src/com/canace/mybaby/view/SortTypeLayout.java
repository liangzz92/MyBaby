/**
 * SortTypeLayout.java
 * 组合排序选项的ImageView（箭头的方向代表排序顺序）与TextView（代表排序方法）
 * @author liangzz
 * 2014-12-24
 */
package com.canace.mybaby.view;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.canace.mybaby.R;
import com.canace.mybaby.activity.HomePageActivity;
import com.canace.mybaby.utils.CommonsUtil;

public class SortTypeLayout {

	public ImageView sortTypeImageView;
	public TextView sortTypeTextView;

	public SortTypeLayout(ImageView sortTypeImageView, TextView sortTypeTextView) {
		this.sortTypeImageView = sortTypeImageView;
		this.sortTypeTextView = sortTypeTextView;
	}

	/**
	 * 当前排序选项状态为未选中
	 */
	public void initLayout() {
		sortTypeTextView.setTextColor(Color.BLACK);
		sortTypeImageView.setVisibility(View.INVISIBLE);
	}

	/**
	 * 选中并高亮当前排序选项，根据sortOrder显示箭头方向
	 * 
	 * @param currentSortOrder
	 * @param currentSortOrder
	 */
	@SuppressLint("NewApi")
	public void select(int colorId, int currentSortOrder) {
		// TODO Auto-generated method stub
		sortTypeTextView.setTextColor(colorId);
		sortTypeImageView.setVisibility(View.VISIBLE);
		if (currentSortOrder == HomePageActivity.SORT_ASCENT) {
			if (CommonsUtil.getSystemVersion() >= 11) {
				sortTypeImageView.setRotation(180);
			} else {
				sortTypeImageView.setImageResource(R.drawable.arrow_down);
			}
			HomePageActivity.setCurrentSortOrder(HomePageActivity.SORT_DESCENT);
		} else {
			if (CommonsUtil.getSystemVersion() >= 11) {
				sortTypeImageView.setRotation(0);
			} else {
				sortTypeImageView.setImageResource(R.drawable.arrow_up);
			}
			HomePageActivity.setCurrentSortOrder(HomePageActivity.SORT_ASCENT);
		}
	}
}
