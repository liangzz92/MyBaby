/**
 * MyBabyConstants.java
 * 应用范围内常量，如第三方平台APPKEY、网络请求接口、字符串常量等
 * @author liangzz
 * 2014-12-2
 */
package com.canace.mybaby.utils;

import java.util.HashMap;
import java.util.Map;

public class MyBabyConstants {

	public static String DB_NAME = "mybaby_database.db";
	public static int DB_VERSION = 1;
	public static String FACEPP_API_KEY = "7595fd6630678a0ffa31007694af2046";
	public static String FACEPP_API_SECRET = "4E2iotQaosRdDKf9OdlWHnASf_bjMqC9";
	public static final int IMAGECACHE_COUNTLIMIT = 200;
	public static final String QQAPPID = "1103569558";
	public static final String QQAPPKEY = "6k4e2s7nYICN781P";
	public static final int MSG_FETCH_FAILED = 77;
	public static final String LOGIN_FAILED = "登录失败，请重试";
	public static final String LOGIN_SUCCESS = "登录成功";
	public static final int MSG_LOGIN_SUCCESS = 76;
	public static final int MSG_LOGIN_FAILED = 75;
	public static final String AUTH_SUCCESS = "授权成功";
	public static final String AUTH_FAILED = "授权失败，请重试";
	public static final String MOST_NEW = "当前已是最新版本";
	public static final String NO_NETWORK_STRING = "请检查您的网络连接";

	public static final int MSG_FETCH_COMPLETE = 88;

	public static final String LOADING = "努力加载中……";
	public static final String PLEASE_WAIT = "请稍候……";
	public static final String ABOUT_VERSION = "宝贝相册：V ";
	public static final String RETRACT = "\u3000\u3000";
	public static final String ABOUT = "宝贝相册是一款专为儿童定制的智能相册，利用人脸检测、人脸分析、人脸识别等技术，为用户提供按照时间、人物、照片质量等多维度智能分类与排序功能，家长们无须再为在众多照片中挑选宝贝美照而犯愁；同时自动进行云备份，家长们不用再担心丢失宝贝珍贵照片。本应用是管理宝贝照片最方便好用的工具。";

	private static Map<String, String> login_platform_map = null;

	public static Map<String, String> getPlatformMap() {
		if (login_platform_map == null) {
			login_platform_map = new HashMap<String, String>();
			login_platform_map.put("qq", "4");
			login_platform_map.put("sina", "1");
			login_platform_map.put("tencent", "2");
			login_platform_map.put("douban", "6");
			login_platform_map.put("qzone", "3");
			login_platform_map.put("renren", "5");
		}
		return login_platform_map;
	}

	public static final String HOST_URL = "http://120.88.9.44";

	public static final String SETUSERINFO_URL = HOST_URL
			+ "/mybaby/setUserInfo.php";
	public static final String GETUSERINFO_URL = HOST_URL
			+ "/mybaby/getUserInfo.php";

	public static final int DEFAULT_LIST_SIZE = 10;
	public static final String WEIXIN_APPID = "wx9b5bd735eacb8625";
	public static final String WEIXIN_APPKEY = "fdf91c9559cbdd4b98d096c921ec8dc7";
	public static final String MYBABY_SINAWEIBO = "1988796283";
	public static final String MYBABY_TENCENTWEIBO = "liangzz000";
	public static final String MYBABY_WEBSITE = "http://120.88.9.44/mybaby";
	public static final int OFFSCREENPAGELIMIT = 50;
	public static final String CENTIGRATE = "℃";
	public static final String LOGOUT = "退出当前帐号";
	public static final String TO_LOGIN = "注册/登录";
	public static final String NOW_LOGIN = "正在登录";
	public static final int TRY_TIME = 3;
	public static final String QQPACKAGENAME = "com.tencent.mobileqq";
	public static final String INSTALLQQFIRST = "请先安装QQ客户端";
	public static final int SUMMARYLENGTHLIMIT = 150;
	public static final String TRY = "立即体验";
	public static final String RETRY = "继续体验";
	public static final int SHARECONTENTLENGTHLIMIT = 105; // 新浪微博限制字数140 -
															// shareURL字符数35
	public static final String FILE_DELETED = "该照片已被删除";
	public static final String WEIBOPACKAGENAME = "com.sina.weibo";
	public static final String INSTALLWEIBOFIRST = "请先安装微博客户端";
	public static final int MSG_PREPARE_COMPLETE = 89;

}