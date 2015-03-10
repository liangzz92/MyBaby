/**
 * SortByTime.java
 *
 * @author liangzz
 * 2014-12-20
 */
package com.canace.mybaby.utils;

import java.util.Comparator;

import com.canace.mybaby.db.model.ImageItem;

public class SortByTime implements Comparator<Object> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Object lhs, Object rhs) {
		// TODO Auto-generated method stub
		ImageItem one = (ImageItem) lhs;
		ImageItem two = (ImageItem) rhs;
		if (one.getImagePath().equals(two.getImagePath())) {
			return 0; // 过滤有相同imagePath的item
		} else if (one.getTimeScore().equals(two.getTimeScore())) {
			return 1; // 如果时间相同，按添加至数据库的顺序排列
		} else {
			return one.getTimeScore().compareTo(two.getTimeScore());
		}

	}

}
