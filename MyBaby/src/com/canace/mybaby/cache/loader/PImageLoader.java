/**
 * PImageLoader.java
 * 利用线程池与软引用，开启三级缓存模式：
 * 根据图片路径，先在内存查找是否存在bitmap的引用；若无，查找SD卡是否存在该缓存文件；若无，开启新线程加载网络图片
 * 此类用于PView控件图片加载
 * @author liangzz
 * 2014-12-2
 */
package com.canace.mybaby.cache.loader;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;

import com.canace.mybaby.view.PView;

public class PImageLoader extends ImageLoader {

	public PImageLoader(Context context) {
		super(context);
	}

	/**
	 * @param imageView
	 * @param bitmap
	 */
	@Override
	public void setImageBitmap(View imageView, Bitmap bitmap) {
		// TODO Auto-generated method stub
		((PView) imageView).setImageBitmap(bitmap);
	}
}