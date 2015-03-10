/**
 * CommonsUtil.java
 * 常用函数工具包
 * @author liangzz
 * 2014-12-2
 */
package com.canace.mybaby.utils;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Looper;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import com.canace.mybaby.R;
import com.canace.mybaby.activity.WelcomeActivity;
import com.canace.mybaby.db.DBFacade;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;

public class CommonsUtil {

	/**
	 * DEBUG为true时才输出Log信息
	 */
	public static final boolean DEBUG = true;

	public static final int TEXTVIEW_ONDROW = 4;
	public static final int NO_NETWORK = 0;
	public static final int WIFI_NETWORK = 1;
	public static final int MOBILE_NETWORK = 2;

	private static Typeface title_face = null;
	private static Typeface content_face = null;
	private static int netState = -1;

	public static UMSocialService mLoginController = null;

	public static UMSocialService mShareController = UMServiceFactory
			.getUMSocialService("com.umeng.share");

	public static UMSocialService getUMLoginController() {
		if (mLoginController == null) {
			mLoginController = UMServiceFactory
					.getUMSocialService("com.umeng.login");
		}
		return mLoginController;
	}

	/**
	 * 返回assets文件夹内字体包
	 * 
	 * @param context
	 * @return
	 */
	public static Typeface getTitleTypeface(Context context) {
		if (title_face != null) {
			return title_face;
		} else {
			title_face = Typeface.createFromAsset(context.getAssets(),
					"fonts/font_title.TTF");
			return title_face;
		}
	}

	/**
	 * will trim the string
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isEmpty(String s) {
		if (null == s)
			return true;
		if (s.length() == 0)
			return true;
		if (s.trim().length() == 0)
			return true;
		return false;
	}

	/**
	 * 校验Tag Alias 只能是数字,英文字母和中文
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isValidTagAndAlias(String s) {
		Pattern p = Pattern.compile("^[\u4E00-\u9FA50-9a-zA-Z_-]{0,}$");
		Matcher m = p.matcher(s);
		return m.matches();
	}

	/**
	 * 获取手机设备的唯一码
	 */
	public static void fetch_status(Context mContext) {

		DeviceUuidFactory deviceUuidFactory = new DeviceUuidFactory(mContext);
		String device_id = deviceUuidFactory.getDeviceUuid().toString();
		String user_id = device_id.replace("-", "");
		if (CommonsUtil.DEBUG) {
			Log.i("device_id", device_id);
			Log.i("user_id", user_id);
		}
		PreferenceUtils.saveStringValue(PreferenceUtils.DEVICE_ID, device_id);
		PreferenceUtils.saveStringValue(PreferenceUtils.USER_ID, user_id);

		int systemVersion = CommonsUtil.getSystemVersion();
		PreferenceUtils.saveIntValue(PreferenceUtils.SYSTEM_VERSION,
				systemVersion);
	}

	/**
	 * 创建快捷方式
	 * 
	 * @param mContext
	 */
	public static void createDeskShortCut(Context mContext) {

		// 创建快捷方式的Intent
		Intent shortcutIntent = new Intent(
				"com.android.launcher.action.INSTALL_SHORTCUT");
		// 不允许重复创建
		shortcutIntent.putExtra("duplicate", false);
		// 需要显示的名称
		shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME,
				mContext.getString(R.string.app_name));

		// 快捷图片
		Parcelable icon = Intent.ShortcutIconResource.fromContext(
				mContext.getApplicationContext(), R.drawable.logo_icon);

		shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);

		Intent intent = new Intent(mContext.getApplicationContext(),
				WelcomeActivity.class);
		// 设置当应用程序卸载时自动删除桌面上的快捷方式
		intent.setAction("android.intent.action.MAIN");
		intent.addCategory("android.intent.category.LAUNCHER");
		// 点击快捷图片，运行的程序主入口
		shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
		// 发送广播 OK
		mContext.sendBroadcast(shortcutIntent);

	}

	/**
	 * 判断快捷方式是否已经创建
	 */
	public static boolean hasShortCut(Context context) {
		String url = "";
		System.out.println(getSystemVersion());
		int systemVersion = getSystemVersion();
		PreferenceUtils.saveIntValue(PreferenceUtils.SYSTEM_VERSION,
				systemVersion);
		if (systemVersion <= 8) { // 判断如果系统版本小于2.2的
			url = "content://com.android.launcher.settings/favorites?notify=true";
		} else { // 判断如果系统版本大于2.2的
			url = "content://com.android.launcher2.settings/favorites?notify=true";
		}

		ContentResolver resolver = context.getContentResolver();
		Cursor cursor = resolver.query(Uri.parse(url), new String[] { "title",
				"iconResource" }, "title=?", new String[] { context
				.getResources().getString(R.string.app_name) }, null);

		if (cursor != null && cursor.getCount() > 0) {
			cursor.close();
			return true;
		}

		return false;
	}

	/**
	 * 获取系统版本
	 * 
	 * @return
	 */
	public static int getSystemVersion() {
		return android.os.Build.VERSION.SDK_INT;
	}

	/**
	 * 取得应用版本号
	 * 
	 * @param context
	 * @return
	 */
	public static String GetVersion(Context context) {
		try {
			PackageInfo manager = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			return manager.versionName;
		} catch (NameNotFoundException e) {
			return "Unknown";
		}
	}

	/**
	 * 显示提示
	 * 
	 * @param toast
	 * @param context
	 */
	public static void showToast(final String toast, final Context context) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Looper.prepare();
				Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
				Looper.loop();
			}
		}).start();
	}

	/**
	 * 获取状态栏高度
	 * 
	 * @param mContext
	 * @return
	 */
	public static int getStatusBarHeight(Context mContext) {

		Rect frames = new Rect();
		((Activity) mContext).getWindow().getDecorView()
				.getWindowVisibleDisplayFrame(frames);
		int statusBarHeights = frames.top;
		if (CommonsUtil.DEBUG) {
			Log.i("statusBarHeight", "" + statusBarHeights);
		}

		return statusBarHeights;
	}

	/**
	 * 获得屏幕高度
	 * 
	 * @param mContext
	 * @return
	 */
	public static int getScreenHeight(Context mContext) {

		return ((Activity) mContext).getWindowManager().getDefaultDisplay()
				.getHeight();
	}

	/**
	 * 获得屏幕宽度
	 * 
	 * @param mContext
	 * @return
	 */
	public static int getScreenWidth(Context mContext) {

		return ((Activity) mContext).getWindowManager().getDefaultDisplay()
				.getWidth();
	}

	/**
	 * 获取assets文件夹内字体包
	 * 
	 * @param context
	 * @return
	 */
	public static Typeface getContentTypeface(Context context) {

		if (content_face != null) {
			return content_face;
		} else {
			content_face = Typeface.createFromAsset(context.getAssets(),
					"fonts/font_title.TTF");
			return content_face;
		}
	}

	/**
	 * dp转换为px
	 * 
	 * @param context
	 * @param dipValue
	 * @return
	 */
	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	/**
	 * px转换为dip
	 * 
	 * @param context
	 * @param pxValue
	 * @return
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 初始化网路状态
	 * 
	 * @param context
	 */
	public static void initNetState(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo info = connectivity.getActiveNetworkInfo();
			if (info != null) {
				if (info.getState() == NetworkInfo.State.CONNECTED
						&& info.getType() == ConnectivityManager.TYPE_WIFI) {
					netState = WIFI_NETWORK;

				} else if (info.getState() == NetworkInfo.State.CONNECTED
						&& info.getType() == ConnectivityManager.TYPE_MOBILE) {
					netState = MOBILE_NETWORK;
				} else if (info.getState() == NetworkInfo.State.DISCONNECTED) {
					netState = NO_NETWORK;
				}
			} else {
				netState = NO_NETWORK;
			}
		}
	}

	/**
	 * 检测网络状态
	 * 
	 * @param context
	 * @return
	 */
	public static int getNetState(Context context) {

		if (netState == -1) {
			initNetState(context);
		}

		return netState;
	}

	/**
	 * 检测网络是否为已连接状态
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetConnected(Context context) {
		return getNetState(context) != NO_NETWORK;
	}

	/**
	 * 检测是否有SD卡
	 * 
	 * @return
	 */
	public static boolean hasSDCard() {
		String status = Environment.getExternalStorageState();
		if (!status.equals(Environment.MEDIA_MOUNTED)) {
			return false;
		}
		return true;
	}

	/**
	 * 获取存储根目录，有SD卡时返回SD卡根目录，无SD卡时返回系统默认存储data目录
	 * 
	 * @return
	 */
	public static String getRootFilePath() {
		if (hasSDCard()) {
			return Environment.getExternalStorageDirectory().getAbsolutePath();// filePath:/sdcard
		} else {
			return Environment.getDataDirectory().getAbsolutePath() + "/data"; // filePath:
																				// /data/data
		}
	}

	/**
	 * 智能模式：根据用户设置与网络状态，判断是否进行网络请求
	 * 
	 * @param context
	 * @return
	 */
	public static boolean checkOnlineDetectOrNot(Context context) {
		if (PreferenceUtils.getBooleanValue(PreferenceUtils.ISSMARTMODE)
				&& getNetState(context) != WIFI_NETWORK) {
			if (CommonsUtil.DEBUG)
				Log.i("checkOnlineDetectOrNot", "false");
			return false;
		}
		if (CommonsUtil.DEBUG)
			Log.i("checkOnlineDetectOrNot", "true");
		return true;
	}

	/**
	 * 当程序尝试在非WiFi网络状态下进行网络请求时，提醒用户切换到智能模式
	 * 
	 * @param context
	 * @return
	 */
	public static boolean checkToastOrNot(Context context) {

		if (!PreferenceUtils.getBooleanValue(PreferenceUtils.ISSMARTMODE)
				&& getNetState(context) != WIFI_NETWORK) {
			return true;
		}
		return false;
	}

	/**
	 * 初始化用户信息
	 */
	public static void initUserInfo() {
		PreferenceUtils.saveStringValue(PreferenceUtils.USER_ID, "");
		PreferenceUtils.saveBooleanValue(PreferenceUtils.ISLOADED, true);
		PreferenceUtils.saveStringValue(PreferenceUtils.LOGIN_PLATFORM, "");
		PreferenceUtils.saveStringValue(PreferenceUtils.USERNAME, "");
		PreferenceUtils.saveStringValue(PreferenceUtils.AVATARPATH, "");
	}

	/**
	 * 初始化用户设置
	 */
	public static void initUserSettings() {

		PreferenceUtils.saveBooleanValue(PreferenceUtils.ISSMARTMODE, true);
	}

	/**
	 * 检查用户是否已登录
	 * 
	 * @return
	 */
	public static boolean isLogin() {

		return !PreferenceUtils.getStringValue(PreferenceUtils.USER_ID).equals(
				"");
	}

	/**
	 * 根据包名确定用户手机是否已安装QQ客户端
	 * 
	 * @param mContext
	 * @return
	 */
	public static boolean isQQInstalled(Context mContext) {

		try {

			mContext.getPackageManager().getApplicationInfo(
					MyBabyConstants.QQPACKAGENAME, 0);
			return true;
		} catch (PackageManager.NameNotFoundException e) {
			return false;
		}

	}

	/**
	 * 根据包名确定用户手机是否已安装微博客户端
	 * 
	 * @param mContext
	 * @return
	 */
	public static boolean isWeiboInstalled(Context mContext) {

		try {

			mContext.getPackageManager().getApplicationInfo(
					MyBabyConstants.WEIBOPACKAGENAME, 0);
			return true;
		} catch (PackageManager.NameNotFoundException e) {
			return false;
		}

	}

	/**
	 * 通过Intent安装APK文件
	 * 
	 * @param mContext
	 * @param apkfile
	 */
	public static void installApkFile(Context mContext, File apkfile) {

		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
				"application/vnd.android.package-archive");
		mContext.startActivity(i);
	}

	/**
	 * 初始化数据库与SharePreferences连接
	 * 
	 * @param context
	 */
	public static void initDBSettings(Context context) {

		DBFacade.init(context);
		PreferenceUtils.initPreference(context);

	}
}
