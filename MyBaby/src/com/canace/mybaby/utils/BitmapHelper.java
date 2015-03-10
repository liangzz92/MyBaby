/**
 * BitmapHelper.java
 * Bitmap工具包，实现bitmap相关存取方法
 * @author liangzz
 * 2014-12-26
 */
package com.canace.mybaby.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BitmapHelper {

	/**
	 * 从项目媒体文件夹获取bitmap
	 * 
	 * @param context
	 * @param fileName
	 * @return
	 */
	public static Bitmap getBitmapFromAssets(Context context, String fileName) {
		Bitmap image = null;
		try {
			AssetManager am = context.getAssets();
			InputStream is = am.open(fileName);
			image = BitmapFactory.decodeStream(is);
			is.close();
		} catch (Exception e) {

		}
		return image;
	}

	/**
	 * 从SD卡按原始大小获取bitmap，容易抛出OutOfMemory异常，不建议使用
	 * 
	 * @param filePath
	 * @return
	 */
	public static Bitmap getBitmapFromSDCard(String filePath) {

		BitmapFactory.Options options = new BitmapFactory.Options();
		return BitmapFactory.decodeFile(filePath, options);
	}

	/**
	 * 从SD卡获取bitmap，以指定宽度正比例压缩图片，建议使用
	 * 
	 * @param fileName
	 * @param dstWidth
	 *            建议dstWidth <= 600
	 * @return
	 */
	public static Bitmap getScaledBitmapFromSDCard(String fileName, int dstWidth) {
		BitmapFactory.Options localOptions = new BitmapFactory.Options();
		localOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(fileName, localOptions);
		int originWidth = localOptions.outWidth;
		int originHeight = localOptions.outHeight;

		localOptions.inSampleSize = originWidth > originHeight ? originWidth
				/ dstWidth : originHeight / dstWidth;
		localOptions.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(fileName, localOptions);
	}

	/**
	 * 
	 * @param filePath
	 * @param bm
	 */
	public static void saveBitmapToSDCard(String filePath, Bitmap bm) {

		try {
			FileOutputStream out = new FileOutputStream(filePath);
			bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
			out.flush();
			out.close();

		} catch (FileNotFoundException e) {
			if (CommonsUtil.DEBUG) {
				e.printStackTrace();
			}

		} catch (IOException e) {
			if (CommonsUtil.DEBUG) {
				e.printStackTrace();
			}
		}

	}
}
