/**
 * ImageLoader.java
 * 利用线程池与软引用，开启三级缓存模式：
 * 根据图片路径，先在内存查找是否存在bitmap的引用；若无，查找SD卡是否存在该缓存文件；若无，开启新线程加载网络图片
 * 此类用于ImageView控件图片加载
 * @author liangzz
 * 2014-12-2
 */
package com.canace.mybaby.cache.loader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.canace.mybaby.db.DBFacade;
import com.canace.mybaby.db.model.ImageCache;
import com.canace.mybaby.utils.BitmapHelper;
import com.canace.mybaby.utils.CommonsUtil;
import com.canace.mybaby.utils.FileHelper;
import com.canace.mybaby.utils.MyBabyConstants;
import com.canace.mybaby.utils.PreferenceUtils;

public class ImageLoader {

	private static final String TAG = "ImageLoader";
	private final MemoryCache memoryCache = new MemoryCache();
	private final AbstractFileCache fileCache;
	private final Map<View, String> imageViews = Collections
			.synchronizedMap(new WeakHashMap<View, String>());
	// 线程池
	private final ExecutorService executorService;

	public ImageLoader(Context context) {
		fileCache = new FileCache(context);
		executorService = Executors.newFixedThreadPool(5);
	}

	// 最主要的方法
	public void displayImage(String url, View imageView,
			boolean isLoadOnlyFromCache) {
		imageViews.put(imageView, url);
		// 先从内存缓存中查找

		Bitmap bitmap = memoryCache.get(url);
		if (bitmap != null)
			setImageBitmap(imageView, bitmap);
		// imageView.setImageBitmap(bitmap);
		else if (!isLoadOnlyFromCache) {

			// 若没有的话则开启新线程加载图片
			queuePhoto(url, imageView);
		}
	}

	/**
	 * @param imageView
	 * @param bitmap
	 */
	public void setImageBitmap(View imageView, Bitmap bitmap) {
		// TODO Auto-generated method stub
		((ImageView) imageView).setImageBitmap(bitmap);
	}

	private void queuePhoto(String url, View imageView) {
		PhotoToLoad p = new PhotoToLoad(url, imageView);
		executorService.submit(new PhotosLoader(p));
	}

	private Bitmap getBitmap(String url) {
		File f = fileCache.getFile(url);

		try {
			// 先从文件缓存中查找是否有
			Bitmap b = null;
			if (f != null && f.exists()) {
				b = decodeFile(f);
			}
			if (b != null) {
				return b;
			}
			// 最后从指定的url中下载图片
			Bitmap bitmap = null;
			URL imageUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) imageUrl
					.openConnection();
			conn.setConnectTimeout(30000);
			conn.setReadTimeout(30000);
			conn.setInstanceFollowRedirects(true);
			InputStream is = conn.getInputStream();
			OutputStream os = new FileOutputStream(f);
			saveNewAndClearOldCache(f.getAbsolutePath());
			CopyStream(is, os);
			os.close();
			bitmap = decodeFile(f);
			return bitmap;
		} catch (Exception ex) {
			if (CommonsUtil.DEBUG) {
				Log.i(TAG, "getBitmapFailed");
				ex.printStackTrace();
			}
			return null;
		}
	}

	private void saveNewAndClearOldCache(String absolutePath) {
		// Log.e("imageCachePath", absolutePath);
		ImageCache temp = null;
		ImageCache[] tempCaches = (ImageCache[]) DBFacade.findByFieldName(
				ImageCache.class, "image_path", absolutePath);
		if (null != tempCaches && tempCaches.length > 0) {
			temp = tempCaches[0];
		}
		if (null != temp) {
			return;
		} else {
			int lastCacheId = PreferenceUtils
					.getIntValue(PreferenceUtils.LASTIMAGECACHEID);
			boolean isFirstRound = PreferenceUtils
					.getBooleanValue(PreferenceUtils.ISFIRSTROUND);
			int currentCacheId = 0;
			if (lastCacheId + 1 > MyBabyConstants.IMAGECACHE_COUNTLIMIT) {
				isFirstRound = false;
				PreferenceUtils.saveBooleanValue(PreferenceUtils.ISFIRSTROUND,
						isFirstRound);
				currentCacheId = 1;
			} else {
				currentCacheId = lastCacheId + 1;
			}
			ImageCache imageCache = new ImageCache(currentCacheId);
			PreferenceUtils.saveIntValue(PreferenceUtils.LASTIMAGECACHEID,
					currentCacheId);
			imageCache.setImagePath(absolutePath);
			if (PreferenceUtils.getBooleanValue(PreferenceUtils.ISFIRSTROUND)) {

				DBFacade.save(imageCache);

			} else {

				ImageCache temp2 = (ImageCache) DBFacade.findById(
						ImageCache.class, "cache_id", currentCacheId);
				// Log.e("OldImageCachePath", temp2.getImage_path());
				FileHelper.deleteDirectory(temp2.getImagePath());
				DBFacade.update(imageCache);

			}
		}

	}

	// decode这个图片并且按比例缩放以减少内存消耗
	private Bitmap decodeFile(File f) {
		return BitmapHelper.getScaledBitmapFromSDCard(f.getAbsolutePath(), 300);

	}

	// Task for the queue
	private class PhotoToLoad {
		public String url;
		public View imageView;

		public PhotoToLoad(String u, View imageView2) {
			url = u;
			imageView = imageView2;
		}
	}

	class PhotosLoader implements Runnable {
		PhotoToLoad photoToLoad;

		PhotosLoader(PhotoToLoad photoToLoad) {
			this.photoToLoad = photoToLoad;
		}

		@Override
		public void run() {
			if (imageViewReused(photoToLoad))
				return;
			Bitmap bmp = getBitmap(photoToLoad.url);
			memoryCache.put(photoToLoad.url, bmp);
			if (imageViewReused(photoToLoad))
				return;
			BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
			// 更新的操作放在UI线程中
			Activity a = (Activity) photoToLoad.imageView.getContext();
			a.runOnUiThread(bd);
		}
	}

	/**
	 * 防止图片错位
	 * 
	 * @param photoToLoad
	 * @return
	 */
	boolean imageViewReused(PhotoToLoad photoToLoad) {
		String tag = imageViews.get(photoToLoad.imageView);
		if (tag == null || !tag.equals(photoToLoad.url))
			return true;
		return false;
	}

	// 用于在UI线程中更新界面
	class BitmapDisplayer implements Runnable {
		Bitmap bitmap;
		PhotoToLoad photoToLoad;

		public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
			bitmap = b;
			photoToLoad = p;
		}

		@Override
		public void run() {
			if (imageViewReused(photoToLoad))
				return;
			if (bitmap != null)
				setImageBitmap(photoToLoad.imageView, bitmap);
		}
	}

	public void clearCache() {
		memoryCache.clear();
		// fileCache.clear();
	}

	public static void CopyStream(InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		try {
			byte[] bytes = new byte[buffer_size];
			for (;;) {
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1)
					break;
				os.write(bytes, 0, count);
			}
		} catch (Exception ex) {
			// Log.e("", "CopyStream catch Exception...");
		}
	}
}