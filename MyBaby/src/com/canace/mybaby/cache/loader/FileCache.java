/**
 * FileCache.java
 * 下载文件缓存机制
 * @author liangzz
 * 2014-12-2
 */
package com.canace.mybaby.cache.loader;

import android.content.Context;

import com.canace.mybaby.db.model.ImageItem;
import com.canace.mybaby.utils.FileHelper;

public class FileCache extends AbstractFileCache {

	public FileCache(Context context) {
		super(context);

	}

	@Override
	public String getSavePath(String url) {
		// String filename = String.valueOf(url.hashCode());
		// return getCacheDir() + filename;
		return url;
	}

	/**
	 * 返回网络加载图片缓存路径
	 */
	@Override
	public String getCacheDir() {
		return FileHelper.getCacheRootDirectory() + "images";
	}
}
