/**
 * PreferenceUtils.java
 * SharedPreferences工具包，使用前需先调用PreferenceUtils.init(context)初始化
 * 最好在每个activity、service、broadcastReciever等启动时调用CommonsUtils.initDBsettings(context)
 * @author liangzz
 * 2014-12-2
 */
package com.canace.mybaby.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceUtils {
	public static final String PREFERENCE_NAME = "MyBaby";
	public static final String DEVICE_ID = "device_id";
	public static final String USER_ID = "user_id";
	public static final String LOGIN_USID = "login_usid";
	public static final String LOGIN_PLATFORM = "login_platform";
	public static final String USERNAME = "username";
	public static final String AVATARPATH = "avatarPath";
	public static final String LASTIMAGECACHEID = "LastImageCacheId";
	public static final String ISFIRSTROUND = "isFirstRound";
	public static final String ISFIRSTLOADING = "isFirstLoading";
	public static final String ISLOADED = "isLoaded";
	public static final String ISSMARTMODE = "isSmartMode";
	public static final String SYSTEM_VERSION = "systemVersion";
	public static final String HASCREATESHORTCUT = "hasCreateShortCut";
	public static final String GRID_WIDTH = "gridWidth";
	public static final String GRID_HEIGHT = "gridHeight";
	public static final String SORT_TYPE = "sort_type";
	public static final String THUMBNAIL_LAST_ID = "thumbnail_last_id";

	private static SharedPreferences preferences;

	/**
	 * 使用前必须先初始化
	 * 
	 * @param context
	 */
	public static void initPreference(Context context) {
		preferences = context.getSharedPreferences(PREFERENCE_NAME,
				Context.MODE_PRIVATE);
	}

	/**
	 * 获取key对应的字符串value，默认值为空字符串""
	 * 
	 * @param key
	 * @return
	 */
	public static String getStringValue(String key) {

		return preferences.getString(key, "");
	}

	/**
	 * 获取key对应的布尔值value，默认值为true
	 * 
	 * @param key
	 * @return
	 */
	public static boolean getBooleanValue(String key) {

		return preferences.getBoolean(key, true);
	}

	/**
	 * 获取key对应的整型value，默认值为0
	 * 
	 * @param key
	 * @return
	 */
	public static int getIntValue(String key) {

		return preferences.getInt(key, 0);
	}

	/**
	 * 获取key对应的长整型value，默认值为0
	 * 
	 * @param key
	 * @return
	 */
	public static long getLongValue(String key) {

		return preferences.getLong(key, 0);
	}

	/**
	 * 保存key-info键值对，info为字符串
	 * 
	 * @param key
	 * @param info
	 */
	public static void saveStringValue(String key, String info) {

		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(key, info);
		editor.commit();
	}

	/**
	 * 保存key-info键值对，info为布尔值
	 * 
	 * @param key
	 * @param info
	 */
	public static void saveBooleanValue(String key, Boolean info) {

		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean(key, info);
		editor.commit();
	}

	/**
	 * 保存key-info键值对，info为整型数
	 * 
	 * @param key
	 * @param info
	 */
	public static void saveIntValue(String key, int info) {

		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt(key, info);
		editor.commit();
	}

	/**
	 * 保存key-info键值对，info为长整型数字
	 * 
	 * @param key
	 * @param value
	 */
	public static void saveLongValue(String key, long value) {
		SharedPreferences.Editor editor = preferences.edit();
		editor.putLong(key, value);
		editor.commit();
	}
}
