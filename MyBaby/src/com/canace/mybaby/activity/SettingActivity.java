/**
 * SettingActivity.java
 * 用户设置页面，包括个人信息管理、应用信息、升级与反馈等页面的入口
 * @author liangzz
 * 2014-12-2
 */
package com.canace.mybaby.activity;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.canace.mybaby.R;
import com.canace.mybaby.db.utils.Model;
import com.canace.mybaby.handler.factory.AbstractFactory;
import com.canace.mybaby.handler.factory.SetUserInfoFactory;
import com.canace.mybaby.handler.httphandler.HttpHandler;
import com.canace.mybaby.handler.httphandler.HttpHandler.OnDataCollectFinishedListener;
import com.canace.mybaby.handler.parser.JsonParser;
import com.canace.mybaby.utils.CommonsUtil;
import com.canace.mybaby.utils.MyBabyConstants;
import com.canace.mybaby.utils.MyFetchUserListener;
import com.canace.mybaby.utils.MySocializeClientListener;
import com.canace.mybaby.utils.PreferenceUtils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.common.SocializeConstants;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SocializeClientListener;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.TencentWBSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

public class SettingActivity extends MyBabyActivity {

	public final int MSG_UPDATE_AVATAR = 1;
	public final static int DELETE_AUTH = 0;

	private int deleteAuthCount = 0;
	private final int PLATFORM_COUNT = 6;

	private Context mContext;
	private ImageView backImageView;
	private ImageView avatarImageView;
	private Bitmap mAvatarBitmap;
	private RelativeLayout aboutRelativeLayout;
	private Button logoutButton;
	private ToggleButton displayToggleButton;
	private RelativeLayout checkUpdateRelativeLayout;
	private RelativeLayout feedbackRelativeLayout;
	private RelativeLayout personalRelativeLayout;
	private boolean isLogoutClicked = false;

	final UMSocialService mController = UMServiceFactory
			.getUMSocialService("com.umeng.login");

	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_UPDATE_AVATAR:
				avatarImageView.setImageBitmap(mAvatarBitmap);
				break;

			case DELETE_AUTH:
				deleteAuthCount++;
				if (deleteAuthCount == PLATFORM_COUNT) {
					deleteAuthCount = 0;
					isLogoutClicked = false;
					Intent intent = new Intent(mContext, LoginActivity.class);
					startActivity(intent);
					finish();
				}

			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_settings);

		initViews();
		setListener();

	}

	private void setListener() {

		backImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(mContext, HomePageActivity.class);
				startActivity(intent);
				finish();
			}
		});

		displayToggleButton
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton arg0,
							boolean isChecked) {

						if (isChecked) {
							displayToggleButton
									.setBackgroundResource(R.drawable.on);
							PreferenceUtils.saveBooleanValue(
									PreferenceUtils.ISSMARTMODE, true);
							Toast.makeText(mContext, "智能模式已开启",
									Toast.LENGTH_SHORT).show();
						} else {
							displayToggleButton
									.setBackgroundResource(R.drawable.off);
							PreferenceUtils.saveBooleanValue(
									PreferenceUtils.ISSMARTMODE, false);
							Toast.makeText(mContext, "智能模式已关闭",
									Toast.LENGTH_SHORT).show();
						}
					}
				});

		aboutRelativeLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Intent intent = new Intent(mContext, AboutActivity.class);
				startActivity(intent);
			}
		});
		logoutButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (!isLogoutClicked) {
					if (CommonsUtil.isLogin()) {
						mController.loginout(mContext,
								new SocializeClientListener() {

									@Override
									public void onStart() {

										Toast.makeText(mContext, "正在注销...",
												Toast.LENGTH_SHORT).show();
									}

									@Override
									public void onComplete(int status,
											SocializeEntity entity) {

										if (status == 200) {
											Toast.makeText(mContext, "注销成功",
													Toast.LENGTH_SHORT).show();
											CommonsUtil.initUserInfo();
											CommonsUtil.initUserSettings();
											deleteAuth();

										} else {
											Toast.makeText(mContext, "注销失败",
													Toast.LENGTH_SHORT).show();
											isLogoutClicked = false;
										}
									}

								});
					} else {
						CommonsUtil.initUserInfo();
						CommonsUtil.initUserSettings();
						Intent intent = new Intent(SettingActivity.this,
								LoginActivity.class);
						startActivity(intent);
						finish();
					}

					isLogoutClicked = true;
				}

			}
		});
		checkUpdateRelativeLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {

					@Override
					public void onUpdateReturned(int updateStatus,
							UpdateResponse response) {

						if (updateStatus == UpdateStatus.Yes) {

							UmengUpdateAgent.showUpdateDialog(mContext,
									response);
						} else if (updateStatus == UpdateStatus.No) {
							Toast.makeText(mContext, MyBabyConstants.MOST_NEW,
									Toast.LENGTH_SHORT).show();
						}

					}
				});
				UmengUpdateAgent.forceUpdate(mContext);

			}
		});
		feedbackRelativeLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				FeedBackAgent1 agent = new FeedBackAgent1(mContext);
				agent.startFeedbackActivity();
			}
		});
		personalRelativeLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if (CommonsUtil.isLogin()) {
					mController.getConfig().setSsoHandler(new SinaSsoHandler());
					// 参数1为当前Activity，参数2为开发者在QQ互联申请的APP ID，参数3为开发者在QQ互联申请的APP
					// kEY.
					UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(
							SettingActivity.this, MyBabyConstants.QQAPPID,
							MyBabyConstants.QQAPPKEY);
					qqSsoHandler.addToSocialSDK();
					// 参数1为当前Activity，参数2为开发者在QQ互联申请的APP ID，参数3为开发者在QQ互联申请的APP
					// kEY.
					QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(
							SettingActivity.this, MyBabyConstants.QQAPPID,
							MyBabyConstants.QQAPPKEY);
					qZoneSsoHandler.addToSocialSDK();
					mController.getConfig().setSsoHandler(
							new TencentWBSsoHandler());

					mController.openUserCenter(mContext,
							SocializeConstants.FLAG_USER_CENTER_LOGIN_VERIFY);
				} else {
					Intent intent = new Intent(SettingActivity.this,
							LoginActivity.class);
					startActivity(intent);
					finish();
				}
			}
		});
	}

	private void deleteAuth() {

		mController.deleteOauth(mContext, SHARE_MEDIA.SINA,
				new MySocializeClientListener(mHandler));
		mController.deleteOauth(mContext, SHARE_MEDIA.QQ,
				new MySocializeClientListener(mHandler));
		mController.deleteOauth(mContext, SHARE_MEDIA.QZONE,
				new MySocializeClientListener(mHandler));
		mController.deleteOauth(mContext, SHARE_MEDIA.RENREN,
				new MySocializeClientListener(mHandler));
		mController.deleteOauth(mContext, SHARE_MEDIA.TENCENT,
				new MySocializeClientListener(mHandler));
		mController.deleteOauth(mContext, SHARE_MEDIA.DOUBAN,
				new MySocializeClientListener(mHandler));

	}

	protected void saveSettings() {

		AbstractFactory factory = new SetUserInfoFactory();
		HttpHandler httpHandler = factory.createHttpHandler();
		JsonParser jsonParser = factory.createJsonParser();
		try {
			if (CommonsUtil.DEBUG) {
				Log.i("userId",
						PreferenceUtils.getStringValue(PreferenceUtils.USER_ID));
				Log.i("isSmartMode",
						PreferenceUtils
								.getBooleanValue(PreferenceUtils.ISSMARTMODE)
								+ "");
			}
			String isSmartMode;
			if (PreferenceUtils.getBooleanValue(PreferenceUtils.ISSMARTMODE)) {
				isSmartMode = "1";
			} else {
				isSmartMode = "0";
			}
			httpHandler.setHttpEntity(new UrlEncodedFormEntity(Arrays.asList(
					new BasicNameValuePair("userId", PreferenceUtils
							.getStringValue(PreferenceUtils.USER_ID)),
					new BasicNameValuePair("isSmartMode", isSmartMode)),
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
							if (CommonsUtil.DEBUG)
								Log.d("saveSettings", "ok");
						} else {
							if (CommonsUtil.DEBUG)
								Log.d("saveSettings", "fail");

						}

					}
				});
		httpHandler.setJsonParser(jsonParser);
		httpHandler.execute();

	}

	private void initViews() {

		mContext = this;
		backImageView = (ImageView) findViewById(R.id.button_back);
		avatarImageView = (ImageView) findViewById(R.id.img_personal);
		setImageFromImageURL(PreferenceUtils
				.getStringValue(PreferenceUtils.AVATARPATH));

		displayToggleButton = (ToggleButton) findViewById(R.id.smart_mode);
		if (!PreferenceUtils.getBooleanValue(PreferenceUtils.ISSMARTMODE)) {
			displayToggleButton.setChecked(false);
			displayToggleButton.setBackgroundResource(R.drawable.off);
		}

		aboutRelativeLayout = (RelativeLayout) findViewById(R.id.aboutsetting);
		logoutButton = (Button) findViewById(R.id.logoutsetting);
		if (CommonsUtil.isLogin()) {
			logoutButton.setText(MyBabyConstants.LOGOUT);
		} else {
			logoutButton.setText(MyBabyConstants.TO_LOGIN);
		}
		checkUpdateRelativeLayout = (RelativeLayout) findViewById(R.id.checksetting);
		feedbackRelativeLayout = (RelativeLayout) findViewById(R.id.feedbacksetting);
		personalRelativeLayout = (RelativeLayout) findViewById(R.id.layout_personal);
	}

	public void setImageFromImageURL(final String mImageURL) {
		if (CommonsUtil.DEBUG)
			Log.i("mImageURL", mImageURL);
		if (!mImageURL.equals("")) {
			new Thread(new Runnable() {

				@Override
				public void run() {

					try {

						URL pictureUrl = new URL(mImageURL);
						InputStream in = pictureUrl.openStream();
						mAvatarBitmap = BitmapFactory.decodeStream(in);
						in.close();
						if (mHandler != null) {
							mHandler.sendEmptyMessage(MSG_UPDATE_AVATAR);
						}
					} catch (ClientProtocolException e) {

						e.printStackTrace();
					} catch (IOException e) {

						e.printStackTrace();
					}
				}
			}).start();
		} else {
			avatarImageView
					.setImageResource(R.drawable.umeng_socialize_default_avatar);
		}

	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(mContext, HomePageActivity.class);
		startActivity(intent);
		finish();
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		mController.getUserInfo(mContext, new MyFetchUserListener(mHandler,
				this, false));
		if (CommonsUtil.DEBUG) {
			Log.i("user_id",
					PreferenceUtils.getStringValue(PreferenceUtils.USER_ID));
		}

		if (!PreferenceUtils.getStringValue(PreferenceUtils.USER_ID).equals("")) {
			setImageFromImageURL(PreferenceUtils
					.getStringValue(PreferenceUtils.AVATARPATH));
			saveSettings();
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

}