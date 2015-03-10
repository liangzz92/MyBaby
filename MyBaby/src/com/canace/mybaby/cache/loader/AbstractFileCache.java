/**
 * AbstractFileCache.java
 * 下载文件缓存机制
 * @author liangzz
 * 2014-12-2
 */
package com.canace.mybaby.cache.loader;

import java.io.File;

import android.content.Context;

import com.canace.mybaby.utils.FileHelper;

public abstract class AbstractFileCache {

	private final String dirString;

	public AbstractFileCache(Context context) {

		dirString = getCacheDir();
		FileHelper.createDirectory(dirString);

	}

	public File getFile(String url) {
		File f = new File(getSavePath(url));
		return f;
	}

	public abstract String getSavePath(String url);

	public abstract String getCacheDir();

	public void clear() {
		FileHelper.deleteDirectory(dirString);
	}

}
