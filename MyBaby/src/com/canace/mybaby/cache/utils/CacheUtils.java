/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.canace.mybaby.cache.utils;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.canace.mybaby.utils.CommonsUtil;

/**
 * Collection of utility functions used in this package.
 */
public class CacheUtils {
	private static final String TAG = "cache.Util";
	private static final String MAPS_PACKAGE_NAME = "com.google.android.apps.maps";
	private static final String MAPS_CLASS_NAME = "com.google.android.maps.MapsActivity";

	private CacheUtils() {
	}

	// Rotates the bitmap by the specified degree.
	// If a new bitmap is created, the original bitmap is recycled.
	public static Bitmap rotate(Bitmap b, int degrees) {
		if (degrees != 0 && b != null) {
			Matrix m = new Matrix();
			m.setRotate(degrees, (float) b.getWidth() / 2,
					(float) b.getHeight() / 2);
			try {
				Bitmap b2 = Bitmap.createBitmap(b, 0, 0, b.getWidth(),
						b.getHeight(), m, true);
				if (b != b2) {
					b.recycle();
					b = b2;
				}
			} catch (OutOfMemoryError ex) {
				// We have no memory to rotate. Return the original bitmap.
				if (CommonsUtil.DEBUG) {
					ex.printStackTrace();
				}

			}
		}
		return b;
	}

	public static Bitmap transform(Matrix scaler, Bitmap source,
			int targetWidth, int targetHeight, boolean scaleUp) {
		int deltaX = source.getWidth() - targetWidth;
		int deltaY = source.getHeight() - targetHeight;
		if (!scaleUp && (deltaX < 0 || deltaY < 0)) {
			/*
			 * In this case the bitmap is smaller, at least in one dimension,
			 * than the target. Transform it by placing as much of the image as
			 * possible into the target and leaving the top/bottom or left/right
			 * (or both) black.
			 */
			Bitmap b2 = Bitmap.createBitmap(targetWidth, targetHeight,
					Bitmap.Config.ARGB_8888);
			Canvas c = new Canvas(b2);

			int deltaXHalf = Math.max(0, deltaX / 2);
			int deltaYHalf = Math.max(0, deltaY / 2);
			Rect src = new Rect(deltaXHalf, deltaYHalf, deltaXHalf
					+ Math.min(targetWidth, source.getWidth()), deltaYHalf
					+ Math.min(targetHeight, source.getHeight()));
			int dstX = (targetWidth - src.width()) / 2;
			int dstY = (targetHeight - src.height()) / 2;
			Rect dst = new Rect(dstX, dstY, targetWidth - dstX, targetHeight
					- dstY);
			c.drawBitmap(source, src, dst, null);
			return b2;
		}
		float bitmapWidthF = source.getWidth();
		float bitmapHeightF = source.getHeight();

		float bitmapAspect = bitmapWidthF / bitmapHeightF;
		float viewAspect = (float) targetWidth / targetHeight;

		if (bitmapAspect > viewAspect) {
			float scale = targetHeight / bitmapHeightF;
			if (scale < .9F || scale > 1F) {
				scaler.setScale(scale, scale);
			} else {
				scaler = null;
			}
		} else {
			float scale = targetWidth / bitmapWidthF;
			if (scale < .9F || scale > 1F) {
				scaler.setScale(scale, scale);
			} else {
				scaler = null;
			}
		}

		Bitmap b1;
		if (scaler != null) {
			// this is used for minithumb and crop, so we want to filter here.
			b1 = Bitmap.createBitmap(source, 0, 0, source.getWidth(),
					source.getHeight(), scaler, true);
		} else {
			b1 = source;
		}

		int dx1 = Math.max(0, b1.getWidth() - targetWidth);
		int dy1 = Math.max(0, b1.getHeight() - targetHeight);

		Bitmap b2 = Bitmap.createBitmap(b1, dx1 / 2, dy1 / 2, targetWidth,
				targetHeight);

		if (b1 != source) {
			b1.recycle();
		}

		return b2;
	}

	/**
	 * Creates a centered bitmap of the desired size. Recycles the input.
	 * 
	 * @param source
	 */
	public static Bitmap extractMiniThumb(Bitmap source, int width, int height) {
		return CacheUtils.extractMiniThumb(source, width, height, true);
	}

	public static Bitmap extractMiniThumb(Bitmap source, int width, int height,
			boolean recycle) {
		if (source == null) {
			return null;
		}

		float scale;
		if (source.getWidth() < source.getHeight()) {
			scale = width / (float) source.getWidth();
		} else {
			scale = height / (float) source.getHeight();
		}
		Matrix matrix = new Matrix();
		matrix.setScale(scale, scale);
		Bitmap miniThumbnail = transform(matrix, source, width, height, false);

		if (recycle && miniThumbnail != source) {
			source.recycle();
		}
		return miniThumbnail;
	}

	public static <T> int indexOf(T[] array, T s) {
		for (int i = 0; i < array.length; i++) {
			if (array[i].equals(s)) {
				return i;
			}
		}
		return -1;
	}

	public static void closeSilently(Closeable c) {
		if (c == null)
			return;
		try {
			c.close();
		} catch (Throwable t) {
			// do nothing
			if (CommonsUtil.DEBUG) {
				t.printStackTrace();
			}

		}
	}

	public static void closeSilently(ParcelFileDescriptor c) {
		if (c == null)
			return;
		try {
			c.close();
		} catch (Throwable t) {
			// do nothing
			if (CommonsUtil.DEBUG) {
				t.printStackTrace();
			}
		}
	}

	public static void Assert(boolean cond) {
		if (!cond) {
			throw new AssertionError();
		}
	}

	public static boolean equals(String a, String b) {
		// return true if both string are null or the content equals
		return a == b || a.equals(b);
	}

	private static class BackgroundJob extends
			MonitoredActivity.LifeCycleAdapter implements Runnable {

		private final MonitoredActivity mActivity;
		private final ProgressDialog mDialog;
		private final Runnable mJob;
		private final Handler mHandler;
		private final Runnable mCleanupRunner = new Runnable() {
			@Override
			public void run() {
				mActivity.removeLifeCycleListener(BackgroundJob.this);
				if (mDialog.getWindow() != null)
					mDialog.dismiss();
			}
		};

		public BackgroundJob(MonitoredActivity activity, Runnable job,
				ProgressDialog dialog, Handler handler) {
			mActivity = activity;
			mDialog = dialog;
			mJob = job;
			mActivity.addLifeCycleListener(this);
			mHandler = handler;
		}

		@Override
		public void run() {
			try {
				mJob.run();
			} finally {
				mHandler.post(mCleanupRunner);
			}
		}

		@Override
		public void onActivityDestroyed(MonitoredActivity activity) {
			// We get here only when the onDestroyed being called before
			// the mCleanupRunner. So, run it now and remove it from the queue
			mCleanupRunner.run();
			mHandler.removeCallbacks(mCleanupRunner);
		}

		@Override
		public void onActivityStopped(MonitoredActivity activity) {
			mDialog.hide();
		}

		@Override
		public void onActivityStarted(MonitoredActivity activity) {
			mDialog.show();
		}
	}

	public static void startBackgroundJob(MonitoredActivity activity,
			String title, String message, Runnable job, Handler handler) {
		// Make the progress dialog uncancelable, so that we can gurantee
		// the thread will be done before the activity getting destroyed.
		ProgressDialog dialog = ProgressDialog.show(activity, title, message,
				true, false);
		new Thread(new BackgroundJob(activity, job, dialog, handler)).start();
	}

	// Returns an intent which is used for "set as" menu items.
	public static Intent createSetAsIntent(Uri uri, String mimeType) {
		// Infer MIME type if missing for file URLs.
		if (uri.getScheme().equals("file")) {
			String path = uri.getPath();
			int lastDotIndex = path.lastIndexOf('.');
			if (lastDotIndex != -1) {
				mimeType = MimeTypeMap.getSingleton()
						.getMimeTypeFromExtension(
								uri.getPath().substring(lastDotIndex + 1)
										.toLowerCase());
			}
		}

		Intent intent = new Intent(Intent.ACTION_ATTACH_DATA);
		intent.setDataAndType(uri, mimeType);
		intent.putExtra("mimeType", mimeType);
		return intent;
	}

	// Opens Maps application for a map with given latlong. There is a bug
	// which crashes the Browser when opening this kind of URL. So, we open
	// it in GMM instead. For those platforms which have no GMM installed,
	// the default Maps application will be chosen.

	public static void openMaps(Context context, double latitude,
			double longitude) {
		try {
			// Try to open the GMM first

			// We don't use "geo:latitude,longitude" because it only centers
			// the MapView to the specified location, but we need a marker
			// for further operations (routing to/from).
			// The q=(lat, lng) syntax is suggested by geo-team.
			String url = String.format(
					"http://maps.google.com/maps?f=q&q=(%s,%s)", latitude,
					longitude);
			ComponentName compName = new ComponentName(MAPS_PACKAGE_NAME,
					MAPS_CLASS_NAME);
			Intent mapsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url))
					.setComponent(compName);
			context.startActivity(mapsIntent);
		} catch (ActivityNotFoundException e) {
			// Use the "geo intent" if no GMM is installed
			if (CommonsUtil.DEBUG) {
				e.printStackTrace();
				Log.e(TAG, "GMM activity not found!", e);
			}
			String url = String.format("geo:%s,%s", latitude, longitude);
			Intent mapsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			context.startActivity(mapsIntent);
		}
	}

	public static final void writeUTF(DataOutputStream dos, String string)
			throws IOException {
		if (string == null) {
			dos.writeUTF(new String());
		} else {
			dos.writeUTF(string);
		}
	}

	public static final String readUTF(DataInputStream dis) throws IOException {
		String retVal = dis.readUTF();
		if (retVal.length() == 0)
			return null;
		return retVal;
	}

	public static final Bitmap resizeBitmap(Bitmap bitmap, int maxSize) {
		int srcWidth = bitmap.getWidth();
		int srcHeight = bitmap.getHeight();
		int width = maxSize;
		int height = maxSize;
		boolean needsResize = false;
		if (srcWidth > srcHeight) {
			if (srcWidth > maxSize) {
				needsResize = true;
				height = ((maxSize * srcHeight) / srcWidth);
			}
		} else {
			if (srcHeight > maxSize) {
				needsResize = true;
				width = ((maxSize * srcWidth) / srcHeight);
			}
		}
		if (needsResize) {
			Bitmap retVal = Bitmap.createScaledBitmap(bitmap, width, height,
					true);
			return retVal;
		} else {
			return bitmap;
		}
	}

	private static final long POLY64REV = 0x95AC9329AC4BC9B5L;
	private static final long INITIALCRC = 0xFFFFFFFFFFFFFFFFL;

	private static boolean init = false;
	private static long[] CRCTable = new long[256];

	/**
	 * A function thats returns a 64-bit crc for string
	 * 
	 * @param in
	 *            : input string
	 * @return 64-bit crc value
	 */
	public static final long Crc64Long(String in) {
		if (in == null || in.length() == 0) {
			return 0;
		}
		// http://bioinf.cs.ucl.ac.uk/downloads/crc64/crc64.c
		long crc = INITIALCRC, part;
		if (!init) {
			for (int i = 0; i < 256; i++) {
				part = i;
				for (int j = 0; j < 8; j++) {
					int value = ((int) part & 1);
					if (value != 0)
						part = (part >> 1) ^ POLY64REV;
					else
						part >>= 1;
				}
				CRCTable[i] = part;
			}
			init = true;
		}
		int length = in.length();
		for (int k = 0; k < length; ++k) {
			char c = in.charAt(k);
			crc = CRCTable[(((int) crc) ^ c) & 0xff] ^ (crc >> 8);
		}
		return crc;
	}

	/**
	 * A function that returns a human readable hex string of a Crx64
	 * 
	 * @param in
	 *            : input string
	 * @return hex string of the 64-bit CRC value
	 */
	public static final String Crc64(String in) {
		if (in == null)
			return null;
		long crc = Crc64Long(in);
		/*
		 * The output is done in two parts to avoid problems with
		 * architecture-dependent word order
		 */
		int low = ((int) crc) & 0xffffffff;
		int high = ((int) (crc >> 32)) & 0xffffffff;
		String outVal = Integer.toHexString(high) + Integer.toHexString(low);
		return outVal;
	}

	public static String getBucketNameFromUri(Uri uri) {
		String string = "";
		if (string == null || string.length() == 0) {
			List<String> paths = uri.getPathSegments();
			int numPaths = paths.size();
			if (numPaths > 1) {
				string = paths.get(paths.size() - 2);
			}
			if (string == null)
				string = "";
		}
		return string;
	}

	// Copies src file to dst file.
	// If the dst file does not exist, it is created
	public static void Copy(File src, File dst) throws IOException {
		InputStream in = new FileInputStream(src);
		OutputStream out = new FileOutputStream(dst);

		// Transfer bytes from in to out
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
	}
}
