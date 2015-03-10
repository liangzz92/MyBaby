/**
 * MediaTypeJudger.java
 * 利用Java反射机制，获取MediaFile属性并据此判断媒体文件类型
 * 调用方式如：new MediaTypeJuder().isImageType(getMediaFileType(filePath));
 * @author liangzz
 * 2014-12-8
 */
package com.canace.mybaby.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MediaTypeJudger {

	Class<?> mMediaFile, mMediaFileType;
	Method getFileTypeMethod, isAudioFileTypeMethod, isVideoFileTypeMethod,
			isImageFileTypeMethod;
	String methodName = "getBoolean";
	String getFileType = "getFileType";

	String isAudioFileType = "isAudioFileType";
	String isVideoFileType = "isVideoFileType";
	String isImageFileType = "isImageFileType";

	Field fileType;

	public MediaTypeJudger() {
		initReflect();
	}

	public void initReflect() {
		try {
			mMediaFile = Class.forName("android.media.MediaFile");
			mMediaFileType = Class
					.forName("android.media.MediaFile$MediaFileType");

			fileType = mMediaFileType.getField("fileType");

			getFileTypeMethod = mMediaFile.getMethod(getFileType, String.class);

			isAudioFileTypeMethod = mMediaFile.getMethod(isAudioFileType,
					int.class);
			isVideoFileTypeMethod = mMediaFile.getMethod(isVideoFileType,
					int.class);
			isImageFileTypeMethod = mMediaFile.getMethod(isImageFileType,
					int.class);

		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}

	}

	public int getMediaFileType(String path) {

		int type = 0;

		try {
			Object obj = getFileTypeMethod.invoke(mMediaFile, path);
			if (obj == null) {
				type = -1;
			} else {
				type = fileType.getInt(obj);
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		return type;
	}

	public boolean isAudioFile(int fileType) {
		boolean isAudioFile = false;
		try {
			isAudioFile = (Boolean) isAudioFileTypeMethod.invoke(mMediaFile,
					fileType);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return isAudioFile;
	}

	public boolean isVideoFile(int fileType) {
		boolean isVideoFile = false;
		try {
			isVideoFile = (Boolean) isVideoFileTypeMethod.invoke(mMediaFile,
					fileType);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return isVideoFile;
	}

	public boolean isImageFile(int fileType) {
		boolean isImageFile = false;
		try {
			isImageFile = (Boolean) isImageFileTypeMethod.invoke(mMediaFile,
					fileType);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return isImageFile;
	}

}