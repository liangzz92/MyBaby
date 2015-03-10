package com.canace.mybaby.cache.utils;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.URISyntaxException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Process;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import com.canace.mybaby.cache.model.DiskCache;
import com.canace.mybaby.cache.model.LocalDataSource;
import com.canace.mybaby.cache.model.MediaItem;
import com.canace.mybaby.cache.model.MediaSet;
import com.canace.mybaby.cache.scanner.CacheService;
import com.canace.mybaby.utils.CommonsUtil;

public final class MediaItemTexture extends Texture {
	public static final int MAX_FACES = 1;
	private static final String TAG = "MediaItemTexture";
	private static final int CACHE_HEADER_SIZE = 12;

	private final Config mConfig;
	private final MediaItem mItem;
	private final Context mContext;
	private boolean mIsRetrying;
	private final boolean mCached;

	public static final class Config {
		public int thumbnailWidth;
		public int thumbnailHeight;
	}

	public MediaItemTexture(Context context, Config config, MediaItem item) {
		mConfig = config;
		mContext = context;
		mItem = item;
		mCached = computeCache();
	}

	private boolean computeCache() {
		final Config config = mConfig;
		final MediaItem item = mItem;
		DiskCache cache = null;
		MediaSet parentMediaSet = item.mParentMediaSet;
		if (config != null && parentMediaSet != null
				&& parentMediaSet.mDataSource != null) {
			cache = parentMediaSet.mDataSource.getThumbnailCache();
			if (cache == LocalDataSource.sThumbnailCache) {
				if (item.mMimeType != null && item.mMimeType.contains("video")) {
					cache = LocalDataSource.sThumbnailCacheVideo;
				}
			}
		}
		if (cache == null) {
			return false;
		}
		synchronized (cache) {
			long id = parentMediaSet.mPicasaAlbumId == Shared.INVALID ? CacheUtils
					.Crc64Long(item.mFilePath) : item.mId;
			return cache.isDataAvailable(id, item.mDateModifiedInSec * 1000);
		}
	}

	@Override
	public boolean isUncachedVideo() {
		if (isCached())
			return false;
		if (mItem.mParentMediaSet == null || mItem.mMimeType == null)
			return false;
		if (mItem.mParentMediaSet.mPicasaAlbumId == Shared.INVALID
				&& mItem.mMimeType.contains("video")) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean isCached() {
		return mCached;
	}

	@Override
	protected Bitmap load(View view) {

		final Config config = mConfig;
		final MediaItem item = mItem;

		// Special case for non-MediaStore content URIs, do not cache the
		// thumbnail.
		String uriString = item.mContentUri;
		if (uriString != null) {
			Uri uri = Uri.parse(uriString);
			if (uri.getScheme().equals("content")
					&& !uri.getAuthority().equals("media")) {
				try {
					return UriTexture.createFromUri(mContext,
							item.mThumbnailUri, 128, 128, 0, null);
				} catch (IOException e) {
					if (CommonsUtil.DEBUG) {
						e.printStackTrace();
					}
					return null;
				} catch (URISyntaxException e) {
					if (CommonsUtil.DEBUG) {
						e.printStackTrace();
					}
					return null;
				}
			}
		}

		// Look up the thumbnail in the disk cache.
		if (config == null) {
			Bitmap retVal = null;
			try {
				if (mItem.getMediaType() == MediaItem.MEDIA_TYPE_IMAGE) {
					Process.setThreadPriority(Process.THREAD_PRIORITY_DEFAULT);
					try {
						// We first dirty the cache if the timestamp has changed
						DiskCache cache = null;
						MediaSet parentMediaSet = item.mParentMediaSet;
						if (parentMediaSet != null
								&& parentMediaSet.mDataSource != null) {
							cache = parentMediaSet.mDataSource
									.getThumbnailCache();
							if (cache == LocalDataSource.sThumbnailCache) {
								if (item.mMimeType != null
										&& item.mMimeType.contains("video")) {
									cache = LocalDataSource.sThumbnailCacheVideo;
								}
								final long crc64 = CacheUtils
										.Crc64Long(item.mFilePath);
								if (!cache.isDataAvailable(crc64,
										item.mDateModifiedInSec * 1000)) {
									UriTexture.invalidateCache(crc64,
											UriTexture.MAX_RESOLUTION);
								}
							}
						}
						retVal = UriTexture.createFromUri(mContext,
								mItem.mContentUri, UriTexture.MAX_RESOLUTION,
								UriTexture.MAX_RESOLUTION,
								CacheUtils.Crc64Long(item.mFilePath), null);
					} catch (IOException e) {
						if (CommonsUtil.DEBUG) {
							e.printStackTrace();
						}
					} catch (URISyntaxException e) {
						if (CommonsUtil.DEBUG) {
							e.printStackTrace();
						}
					}
					Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
				} else {
					Process.setThreadPriority(Process.THREAD_PRIORITY_DEFAULT);
					new Thread() {
						@Override
						public void run() {
							try {
								Thread.sleep(5000);
							} catch (InterruptedException e) {
								if (CommonsUtil.DEBUG) {
									e.printStackTrace();
								}
							}
							try {
								MediaStore.Video.Thumbnails
										.cancelThumbnailRequest(
												mContext.getContentResolver(),
												mItem.mId);
							} catch (Exception e) {
								if (CommonsUtil.DEBUG) {
									e.printStackTrace();
								}
							}
						}
					}.start();
					retVal = MediaStore.Video.Thumbnails.getThumbnail(
							mContext.getContentResolver(), mItem.mId,
							MediaStore.Video.Thumbnails.MINI_KIND, null);
					Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
				}
			} catch (OutOfMemoryError e) {
				if (CommonsUtil.DEBUG) {
					Log.i(TAG, "Bitmap creation fail, outofmemory");
					// view.handleLowMemory();
					e.printStackTrace();
				}
				try {
					if (!mIsRetrying) {
						Thread.sleep(1000);
						mIsRetrying = true;
						retVal = load(view);
					}
				} catch (InterruptedException eInterrupted) {
					if (CommonsUtil.DEBUG) {
						eInterrupted.printStackTrace();
					}
				}
			}
			return retVal;
		} else {
			byte[] data = null;
			MediaSet parentMediaSet = item.mParentMediaSet;
			if (parentMediaSet != null
					&& parentMediaSet.mPicasaAlbumId != Shared.INVALID) {
				DiskCache thumbnailCache = parentMediaSet.mDataSource
						.getThumbnailCache();
				data = thumbnailCache.get(item.mId, 0);
				if (data == null) {
					// We need to generate the cache.
					try {
						Bitmap retVal = UriTexture.createFromUri(mContext,
								item.mThumbnailUri, 256, 256, 0, null);
						data = CacheService.writeBitmapToCache(thumbnailCache,
								item.mId, item.mId, retVal,
								config.thumbnailWidth, config.thumbnailHeight,
								item.mDateModifiedInSec * 1000);
					} catch (IOException e) {
						if (CommonsUtil.DEBUG) {
							e.printStackTrace();
						}
						return null;
					} catch (URISyntaxException e) {
						if (CommonsUtil.DEBUG) {
							e.printStackTrace();
						}
						return null;
					}
				}
			} else {
				data = CacheService.queryThumbnail(mContext,
						CacheUtils.Crc64Long(item.mFilePath), item.mId,
						item.getMediaType() == MediaItem.MEDIA_TYPE_VIDEO,
						item.mDateModifiedInSec * 1000);
			}
			if (data != null) {
				try {
					// Parse record header.
					final ByteArrayInputStream cacheInput = new ByteArrayInputStream(
							data);
					final DataInputStream dataInput = new DataInputStream(
							cacheInput);
					item.mThumbnailId = dataInput.readLong();
					item.mThumbnailFocusX = dataInput.readShort();
					item.mThumbnailFocusY = dataInput.readShort();
					// Decode the thumbnail.
					final BitmapFactory.Options options = new BitmapFactory.Options();
					options.inDither = false;
					options.inScaled = false;
					options.inPreferredConfig = Bitmap.Config.RGB_565;
					final Bitmap bitmap = BitmapFactory.decodeByteArray(data,
							CACHE_HEADER_SIZE, data.length - CACHE_HEADER_SIZE,
							options);
					return bitmap;
				} catch (IOException e) {
					// Fall through to regenerate the cached thumbnail.
					if (CommonsUtil.DEBUG) {
						e.printStackTrace();
					}
				}
			}
		}
		return null;
	}
}
