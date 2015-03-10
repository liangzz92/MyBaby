/**
 * CrashHandler.java
 * UncaughtException处理类,当程序发生Uncaught异常的时候,由该类来接管程序,并记录发送错误报告.
 * 使用方式：在程序启动之初添加以下代码： CrashHandler.getInstance().init(getBaseContext(),
				"http://bug.gzayong.info:9394/report");
 * 使用浏览器查看抛出异常的记录：http://bug.gzayong.info:9394/query?output=html&app=XXX
 * 
 */
package com.canace.mybaby.utils;

/**
 * CrashHandler.java 	Version <1.00>	2013-1-16
 *
 * Copyright(C) 2009-2012  All rights reserved. 
 * Lu Zhiyong is a student majoring in Software Engineering (Communication Software), 
 * from the School of Software, SUN YAT-SEN UNIVERSITY, GZ 510006, P. R. China.
 *
 * Blog: http://www.gzayong.info
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候,由该类来接管程序,并记录发送错误报告.
 * 
 * @author YONG
 * 
 */
public class CrashHandler implements UncaughtExceptionHandler {
	private static final String TAG = "CrashHandler";

	/**
	 * 系统默认的UncaughtException处理类
	 */
	private Thread.UncaughtExceptionHandler mDefaultHandler;

	private static CrashHandler INSTANCE = new CrashHandler();
	private Context mContext;
	private String url;
	private String logFileSavePath;

	/**
	 * 用来存储设备信息和异常信息
	 */
	private final Map<String, String> infos = new HashMap<String, String>();

	/**
	 * 用于格式化日期,作为日志文件名的一部分
	 */
	private final SimpleDateFormat format = new SimpleDateFormat(
			"yyyy-MM-dd-HH-mm-ss");

	private CrashHandler() {
		logFileSavePath = "";
	}

	public static CrashHandler getInstance() {
		return INSTANCE;
	}

	public void init(Context context, String errCollectUrl) {
		mContext = context;
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler(); // 获取系统默认的UncaughtException处理器
		Thread.setDefaultUncaughtExceptionHandler(this); // 设置该CrashHandler为程序的默认处理器
		url = errCollectUrl;
	}

	/**
	 * 当UncaughtException发生时会转入该重写的方法来处理
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handleException(ex) && mDefaultHandler != null) {
			// 如果自定义的没有处理则让系统默认的异常处理器来处理
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
			try {
				Thread.sleep(3000); // 如果处理了，让程序继续运行3秒再退出，保证文件保存并上传到服务器
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(1);
		}
	}

	/**
	 * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
	 * 
	 * @param ex
	 *            异常信息
	 * @return true 如果处理了该异常信息;否则返回false.
	 */
	public boolean handleException(final Throwable ex) {
		if (ex == null)
			return false;
		new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				Toast.makeText(mContext, "很抱歉,程序出现异常,即将退出", Toast.LENGTH_SHORT)
						.show();
				Looper.loop();
			}
		}.start();

		collectDeviceInfo(mContext);

		new Thread() {
			@Override
			public void run() {
				String filename = saveCrashInfo2File(ex);
				send2Server(filename);
			};
		}.start();
		return true;
	}

	/**
	 * 收集设备参数信息
	 * 
	 * @param context
	 */
	public void collectDeviceInfo(Context context) {
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo info = pm.getPackageInfo(context.getPackageName(),
					PackageManager.GET_ACTIVITIES);
			if (info != null) {
				infos.put("appName", info.applicationInfo.name);
				infos.put("versionName", info.versionName == null ? "null"
						: info.versionName);
				infos.put("versionCode", info.versionCode + "");
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				infos.put(field.getName(), field.get("").toString());
				Log.d(TAG, field.getName() + ":" + field.get(""));
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	private StringBuffer deviceInfo;
	private StringBuffer errorMessage;

	private String saveCrashInfo2File(Throwable ex) {
		deviceInfo = new StringBuffer();
		errorMessage = new StringBuffer();
		for (Map.Entry<String, String> entry : infos.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			deviceInfo.append(key + "=" + value + "\n");
		}
		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause.printStackTrace();
			cause = cause.getCause();
		}
		printWriter.close();
		String result = writer.toString();
		errorMessage.append(result);

		long timetamp = System.currentTimeMillis();
		String time = format.format(new Date());
		String fileName = "crash-" + time + "-" + timetamp + ".txt";
		try {
			File dir = new File(logFileSavePath + File.separator + "crash");
			Log.i(TAG, dir.toString());
			if (!dir.exists())
				dir.mkdirs();
			FileOutputStream fos = new FileOutputStream(new File(dir, fileName));
			fos.write(deviceInfo.toString().getBytes());
			fos.write(errorMessage.toString().getBytes());
			fos.close();
			return fileName;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void send2Server(String filename) {
		if (url == null || url.length() == 0)
			return;
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);
		try {
			JSONObject object = new JSONObject();
			object.put("app", mContext.getPackageName());
			object.put("deviceInfo", deviceInfo.toString());
			object.put("msg", errorMessage.toString());
			BasicNameValuePair pair = new BasicNameValuePair("errorContent",
					object.toString());
			List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
			params.add(pair);
			post.setEntity(new UrlEncodedFormEntity(params, "utf-8"));
			HttpResponse response = client.execute(post);
			if (response.getStatusLine().getStatusCode() == 200) {
				Log.e(TAG, "Upload Success");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setLogFileSavePath(String logFileSavePath) {
		this.logFileSavePath = logFileSavePath;
	}
}
