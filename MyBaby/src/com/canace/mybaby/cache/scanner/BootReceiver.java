/**
 * BootReceiver.java
 * 接收系统开机、挂载/卸载SD卡、媒体文件扫描过程开始及结束等广播，并一一作出反馈
 * 主要用于自动重启CacheService服务及实时监测媒体文件状态变化
 * 
 * @author liangzz
 * 2014-12-15
 */
package com.canace.mybaby.cache.scanner;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Video;
import android.util.Log;

import com.canace.mybaby.cache.model.LocalDataSource;
import com.canace.mybaby.utils.CommonsUtil;

public class BootReceiver extends BroadcastReceiver {
	private static final String TAG = "BootReceiver";
	private final Handler mHandler = new Handler();
	private boolean mListenersInitialized = false;

	@Override
	public void onReceive(final Context context, Intent intent) {
		CommonsUtil.initDBSettings(context);
		final String action = intent.getAction();
		Log.i(TAG, "Got intent with action " + action);
		if (Intent.ACTION_MEDIA_SCANNER_FINISHED.equals(action)) {
			CacheService.markDirty(context);
			CacheService.startCache(context, true);
		} else if (Intent.ACTION_MEDIA_MOUNTED.equals(action)) {
			if (!mListenersInitialized) {
				// We add special listeners for the MediaProvider
				mListenersInitialized = true;
				final Handler handler = mHandler;
				final ContentObserver localObserver = new ContentObserver(
						handler) {
					@Override
					public void onChange(boolean selfChange) {
						if (!LocalDataSource.sObserverActive) {
							CacheService.senseDirty(context, null);
						}
					}
				};
				// Start listening perpetually.
				Uri uriImages = Images.Media.EXTERNAL_CONTENT_URI;
				Uri uriVideos = Video.Media.EXTERNAL_CONTENT_URI;
				ContentResolver cr = context.getContentResolver();
				cr.registerContentObserver(uriImages, false, localObserver);
				cr.registerContentObserver(uriVideos, false, localObserver);
			}
		} else if (action.equals(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)) {
			final Uri fileUri = intent.getData();
			final long bucketId = LocalDataSource
					.parseBucketIdFromFileUri(fileUri.toString());
			if (!CacheService.isPresentInCache(bucketId)) {
				CacheService.markDirty(context);
			}
		} else if (action.equals(Intent.ACTION_MEDIA_EJECT)) {
			LocalDataSource.sThumbnailCache.close();
			LocalDataSource.sThumbnailCacheVideo.close();
			CacheService.sAlbumCache.close();
			CacheService.sMetaAlbumCache.close();
			CacheService.sSkipThumbnailIds.flush();
		}
	}
}
