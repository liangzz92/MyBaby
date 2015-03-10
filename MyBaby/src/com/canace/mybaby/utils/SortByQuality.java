/**
 * SortByQuality.java
 *
 * @author liangzz
 * 2014-12-20
 */
package com.canace.mybaby.utils;

import java.util.Comparator;

import com.canace.mybaby.db.model.ImageItem;

public class SortByQuality implements Comparator<Object> {

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
		} else if (one.getQualityScore().equals(two.getQualityScore())) {
			return 1; // 如果quality_score相同，按添加至数据库的顺序排列
		} else {
			return one.getQualityScore().compareTo(two.getQualityScore());
		}
	}

}
