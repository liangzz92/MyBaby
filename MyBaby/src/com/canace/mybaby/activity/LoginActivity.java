/**
 * LoginActivity.java
 * 用户使用第三方平台登录，或直接跳转至图片列表主界面
 * @author liangzz
 * 2014-12-2
 */
package com.canace.mybaby.activity;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.canace.mybaby.R;
import com.canace.mybaby.db.utils.Model;
import com.canace.mybaby.handler.factory.AbstractFactory;
import com.canace.mybaby.handler.factory.GetUserInfoFactory;
import com.canace.mybaby.handler.httphandler.HttpHandler;
import com.canace.mybaby.handler.httphandler.HttpHandler.OnDataCollectFinishedListener;
import com.canace.mybaby.handler.parser.JsonParser;
import com.canace.mybaby.utils.CommonsUtil;
import com.canace.mybaby.utils.DialogFactory;
import com.canace.mybaby.utils.MyBabyConstants;
import com.canace.mybaby.utils.MyFetchUserListener;
import com.canace.mybaby.utils.PreferenceUtils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.MultiStatus;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeConfig;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.common.SocializeConstants;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.MulStatusListener;
import com.umeng.socialize.controller.listener.SocializeListeners.SocializeClientListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMAuthListener;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.TencentWBSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;

public class LoginActivity extends MyBabyActivity implements OnClickListener {
	private RelativeLayout weiboLoginRelativeLayout;
	private RelativeLayout QQLoginRelativeLayout;
	private RelativeLayout QQWeiboLoginRelativeLayout;
	private Button loginBtn;
	private Button tryBtn;

	private static Context mContext;
	private ProgressDialog mProgressDialog = null;

	public UMAuthListener loginAuthListener = new UMAuthListener() {

		@Override
		public void onStart(SHARE_MEDIA arg0) {
			if (CommonsUtil.DEBUG)
				Log.d("otherLogin", "start");
		}

		@Override
		public void onError(SocializeException arg0, SHARE_MEDIA arg1) {

		}

		@Override
		public void onComplete(Bundle value, final SHARE_MEDIA platform) {

			if (value != null && !TextUtils.isEmpty(value.getString("uid"))) {
				Toast.makeText(mContext, MyBabyConstants.AUTH_SUCCESS,
						Toast.LENGTH_SHORT).show();
				saveUserInfo();
				mController.getConfig().unregisterListener(loginAuthListener);

			} else {
				Toast.makeText(mContext, MyBabyConstants.AUTH_FAILED,
						Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		public void onCancel(SHARE_MEDIA arg0) {

		}
	};
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MyBabyConstants.MSG_FETCH_COMPLETE:

				login();
				break;

			case MyBabyConstants.MSG_LOGIN_SUCCESS:
				dismissProgressDialog();
				Toast.makeText(mContext, MyBabyConstants.LOGIN_SUCCESS,
						Toast.LENGTH_SHORT).show();

				jumpToList();

				break;

			case MyBabyConstants.MSG_FETCH_FAILED:
			case MyBabyConstants.MSG_LOGIN_FAILED:
				dismissProgressDialog();
				Toast.makeText(mContext, MyBabyConstants.LOGIN_FAILED,
						Toast.LENGTH_SHORT).show();

			default:
				break;
			}
		}

	};

	private final UMSocialService mController = UMServiceFactory
			.getUMSocialService("com.umeng.login");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;

		if (PreferenceUtils.getBooleanValue(PreferenceUtils.ISFIRSTLOADING)) {

			PreferenceUtils.saveIntValue(PreferenceUtils.SYSTEM_VERSION,
					CommonsUtil.getSystemVersion());
			CommonsUtil.initUserSettings();
			if (!CommonsUtil.hasShortCut(this)
					&& PreferenceUtils
							.getBooleanValue(PreferenceUtils.HASCREATESHORTCUT)) {
				PreferenceUtils.saveBooleanValue(
						PreferenceUtils.HASCREATESHORTCUT, false);
				DialogFactory.getInstance(mContext).createShortcutDialog()
						.show();// 创建桌面快捷方式
			}

		}

		setContentView(R.layout.activity_login);
		initViews();
		setListeners();

	}

	private void setListeners() {

		weiboLoginRelativeLayout.setOnClickListener(this);
		QQLoginRelativeLayout.setOnClickListener(this);
		QQWeiboLoginRelativeLayout.setOnClickListener(this);
		loginBtn.setOnClickListener(this);
		tryBtn.setOnClickListener(this);
	}

	private void initViews() {

		weiboLoginRelativeLayout = (RelativeLayout) findViewById(R.id.weiboLogin);
		QQLoginRelativeLayout = (RelativeLayout) findViewById(R.id.QQLogin);
		QQWeiboLoginRelativeLayout = (RelativeLayout) findViewById(R.id.QQWeiboLogin);
		loginBtn = (Button) findViewById(R.id.loginBtn);
		tryBtn = (Button) findViewById(R.id.tryBtn);
		if (PreferenceUtils.getBooleanValue(PreferenceUtils.ISFIRSTLOADING)) {

			tryBtn.setText(MyBabyConstants.TRY);
		} else {
			tryBtn.setText(MyBabyConstants.RETRY);
		}
		mContext = this;
	}

	@Override
	public void onClick(View v) {

		if (v == tryBtn) {

			jumpToList();

		} else {
			if (!CommonsUtil.isNetConnected(mContext)) {
				Toast.makeText(mContext, MyBabyConstants.NO_NETWORK_STRING,
						Toast.LENGTH_SHORT).show();
			} else {
				try {
					SocializeConfig config = mController.getConfig();

					// 设置SSO handler
					config.setSsoHandler(new SinaSsoHandler());
					// 参数1为当前Activity，参数2为开发者在QQ互联申请的APP ID，参数3为开发者在QQ互联申请的APP
					// kEY.
					UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(
							LoginActivity.this, MyBabyConstants.QQAPPID,
							MyBabyConstants.QQAPPKEY);
					qqSsoHandler.addToSocialSDK();

					config.setSsoHandler(new TencentWBSsoHandler());
					if (v == weiboLoginRelativeLayout) {

						if (CommonsUtil.isWeiboInstalled(mContext)) {
							// 添加关注对象(平台，关注用户的uid)
							config.addFollow(SHARE_MEDIA.SINA,
									MyBabyConstants.MYBABY_SINAWEIBO);
							config.setOauthDialogFollowListener(new MyMulStatusListener());

							new Thread(new Runnable() {

								@Override
								public void run() {
									Looper.prepare();
									mController.login(mContext,
											SHARE_MEDIA.SINA,
											new MySocializeClientListener());
								}
							}).start();
						} else {
							Toast.makeText(mContext,
									MyBabyConstants.INSTALLWEIBOFIRST,
									Toast.LENGTH_SHORT).show();
						}

					} else if (v == QQLoginRelativeLayout) {
						if (CommonsUtil.isQQInstalled(mContext)) {

							new Thread(new Runnable() {

								@Override
								public void run() {
									Looper.prepare();
									mController.login(mContext, SHARE_MEDIA.QQ,
											new MySocializeClientListener());
								}
							}).start();

						} else {
							Toast.makeText(mContext,
									MyBabyConstants.INSTALLQQFIRST,
									Toast.LENGTH_SHORT).show();
						}

					} else if (v == QQWeiboLoginRelativeLayout) {

						config.addFollow(SHARE_MEDIA.TENCENT,
								MyBabyConstants.MYBABY_TENCENTWEIBO);
						// 添加follow 时的回调
						config.setOauthDialogFollowListener(new MyMulStatusListener());
						mController.login(mContext, SHARE_MEDIA.TENCENT,
								new MySocializeClientListener());

					} else if (v == loginBtn) {

						config.registerListener(loginAuthListener);
						mController
								.openUserCenter(
										LoginActivity.this,
										SocializeConstants.FLAG_USER_CENTER_LOGIN_VERIFY
												| SocializeConstants.FLAG_USER_CENTER_HIDE_LOGININFO);

					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}
	}

	public void saveUserInfo() {

		mController.getUserInfo(mContext, new MyFetchUserListener(mHandler,
				this, true));

	}

	private void login() {

		AbstractFactory factory = new GetUserInfoFactory();
		HttpHandler httpHandler = factory.createHttpHandler();
		JsonParser jsonParser = factory.createJsonParser();
		try {
			if (CommonsUtil.DEBUG) {
				Log.i("uid", PreferenceUtils
						.getStringValue(PreferenceUtils.LOGIN_USID));
				Log.i("platform", PreferenceUtils
						.getStringValue(PreferenceUtils.LOGIN_PLATFORM));
			}

			httpHandler.setHttpEntity(new UrlEncodedFormEntity(Arrays.asList(
					new BasicNameValuePair("uid", PreferenceUtils
							.getStringValue(PreferenceUtils.LOGIN_USID)),
					new BasicNameValuePair("terminal", "1"),
					new BasicNameValuePair("platform", PreferenceUtils
							.getStringValue(PreferenceUtils.LOGIN_PLATFORM))),
					HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
		}
		httpHandler
				.setOnDataCollectFinishedListener(new OnDataCollectFinishedListener() {

					@Override
					public void onDataCollect(List<Model> models,
							boolean isSuccess) {

						if (isSuccess) {
							mHandler.sendEmptyMessage(MyBabyConstants.MSG_LOGIN_SUCCESS);
						} else { // 登录失败
							mHandler.sendEmptyMessage(MyBabyConstants.MSG_LOGIN_FAILED);
						}
					}
				});
		httpHandler.setJsonParser(jsonParser);
		httpHandler.execute();
	}

	private class MySocializeClientListener implements SocializeClientListener {

		@Override
		public void onComplete(int status, SocializeEntity arg1) {
			if (CommonsUtil.DEBUG)
				Log.i("login", status + "");

			if (status == 200) {

				saveUserInfo();

			} else {
				dismissProgressDialog();
				Toast.makeText(mContext, MyBabyConstants.LOGIN_FAILED,
						Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		public void onStart() {

			showProgressDialog();
		}

	}

	private class MyMulStatusListener implements MulStatusListener {

		@Override
		public void onComplete(MultiStatus arg0, int arg1, SocializeEntity arg2) {

			if (arg1 == 200) {// follow 成功
				if (CommonsUtil.DEBUG)
					Log.d("TestData", "Follow Success");
			}
		}

		@Override
		public void onStart() {
			if (CommonsUtil.DEBUG)
				Log.d("TestData", "Follow Start");
		}

	}

	public void jumpToList() {
		if (CommonsUtil.DEBUG)
			Log.i("isloaded", "false");
		PreferenceUtils.saveBooleanValue(PreferenceUtils.ISFIRSTLOADING, false);
		PreferenceUtils.saveBooleanValue(PreferenceUtils.ISLOADED, false);
		startActivity(new Intent(LoginActivity.this, HomePageActivity.class));
		LoginActivity.this.finish();
	}

	private void showProgressDialog() {
		if (mProgressDialog == null) {
			mProgressDialog = new ProgressDialog(mContext);
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mProgressDialog.setTitle(MyBabyConstants.NOW_LOGIN);
			mProgressDialog.setMessage(MyBabyConstants.PLEASE_WAIT);
			mProgressDialog.show();
		}

	}

	private void dismissProgressDialog() {
		if (mProgressDialog != null) {
			mProgressDialog.cancel();
		}
		mProgressDialog = null;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		/** 使用SSO授权必须添加如下代码 */

		if (CommonsUtil.DEBUG)
			Log.i("resultCode", resultCode + "");
		if (resultCode == 200) {
			showProgressDialog();
		}
		UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(
				requestCode);
		if (ssoHandler != null) {
			ssoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
