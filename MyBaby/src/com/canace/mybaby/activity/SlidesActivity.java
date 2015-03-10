/**
 * SlidesActivity.java
 * 查看大图，可左右滑动切换图片，以及分享图片到第三方平台
 * @author liangzz
 * 2014-12-2
 */
package com.canace.mybaby.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import com.canace.mybaby.R;
import com.canace.mybaby.adapter.ImagePagerAdapter;
import com.canace.mybaby.db.model.ImageItem;
import com.canace.mybaby.utils.BitmapHelper;
import com.canace.mybaby.utils.CommonsUtil;
import com.canace.mybaby.utils.MyBabyConstants;
import com.canace.mybaby.view.ImageViewPager;
import com.canace.mybaby.view.PView;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeConfig;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.TencentWBSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;

public class SlidesActivity extends MyBabyActivity {

	public static final int RESULT_SLIDESACTIVITY = 0;
	protected static final String TAG = "SlidesActivity";
	private final int DEFAULT_SCROLL_ROWS = 5;

	private ImageViewPager imagesViewPager;
	private ImageView loadingImageView;
	private ImageView backImageView;
	private ImageView shareImageView;
	private ImagePagerAdapter imagePagerAdapter;
	private int fromItem;
	private int fromFirstVisiblePosition;
	private Context mContext;
	private final UMSocialService mController = UMServiceFactory
			.getUMSocialService("com.umeng.share");
	private ImageItem[] imageItems;
	protected List<PView> largeImagesPViews = new ArrayList<PView>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_slides);

		initComponents();

	}

	/**
	 * 
	 */
	private void initComponents() {
		// TODO Auto-generated method stub
		mContext = this;
		fromItem = getIntent().getIntExtra("fromItem", 0);
		fromFirstVisiblePosition = getIntent().getIntExtra(
				"firstVisiblePosition", 0);
		imageItems = HomePageActivity.getImageItems();
		if (largeImagesPViews.size() < imageItems.length) {
			for (int i = largeImagesPViews.size(); i < imageItems.length; i++) {
				largeImagesPViews.add(new PView(mContext));
			}
		}
		imagesViewPager = (ImageViewPager) findViewById(R.id.vPager);
		loadingImageView = (ImageView) findViewById(R.id.loading);
		startAnimation(loadingImageView);

		backImageView = (ImageView) findViewById(R.id.backbutton);
		shareImageView = (ImageView) findViewById(R.id.sharebutton);
		backImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finishAndReturnResult();
			}
		});

		shareImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// 添加微信平台
				UMWXHandler wxHandler = new UMWXHandler(mContext,
						MyBabyConstants.WEIXIN_APPID,
						MyBabyConstants.WEIXIN_APPKEY);
				wxHandler.setTargetUrl(MyBabyConstants.MYBABY_WEBSITE);
				wxHandler.addToSocialSDK();
				// 添加微信朋友圈
				UMWXHandler wxCircleHandler = new UMWXHandler(mContext,
						MyBabyConstants.WEIXIN_APPID,
						MyBabyConstants.WEIXIN_APPKEY);
				wxCircleHandler.setToCircle(true);
				wxCircleHandler.setTargetUrl(MyBabyConstants.MYBABY_WEBSITE);
				wxCircleHandler.addToSocialSDK();

				UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(
						SlidesActivity.this, MyBabyConstants.QQAPPID,
						MyBabyConstants.QQAPPKEY);
				qqSsoHandler.addToSocialSDK();

				QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(
						SlidesActivity.this, MyBabyConstants.QQAPPID,
						MyBabyConstants.QQAPPKEY);

				qZoneSsoHandler.addToSocialSDK();

				// 设置新浪SSO handler
				mController.getConfig().setSsoHandler(new SinaSsoHandler());
				// 设置腾讯微博SSO handler
				mController.getConfig()
						.setSsoHandler(new TencentWBSsoHandler());

				try {
					UMImage mUMImgBitmap = new UMImage(
							mContext,
							BitmapHelper.getScaledBitmapFromSDCard(
									imageItems[imagesViewPager.getCurrentItem()]
											.getImagePath(), 600));

					QQShareContent qqShareContent = new QQShareContent(
							mUMImgBitmap);
					// 设置分享title
					qqShareContent.setTitle("我的宝贝");
					// 设置分享文字
					qqShareContent.setShareContent(MyBabyConstants.ABOUT);
					// 设置点击分享内容的跳转链接
					qqShareContent.setTargetUrl(MyBabyConstants.MYBABY_WEBSITE);
					mController.setShareMedia(qqShareContent);

					QZoneShareContent qzone = new QZoneShareContent(
							mUMImgBitmap);
					// 设置分享文字
					qzone.setShareContent(MyBabyConstants.ABOUT);
					// 设置点击消息的跳转URL
					qzone.setTargetUrl(MyBabyConstants.MYBABY_WEBSITE);
					// 设置分享内容的标题
					qzone.setTitle("我的宝贝");
					mController.setShareMedia(qzone);

					// 设置分享图片
					mController.setShareMedia(mUMImgBitmap);
					mController.setShareContent("我的宝贝");
					mController.setAppWebSite(MyBabyConstants.MYBABY_WEBSITE);
				} catch (Exception exception) {
					if (CommonsUtil.DEBUG) {
						Log.i(TAG,
								"GetBitmapFailed"
										+ imageItems[imagesViewPager
												.getCurrentItem()]
												.getImagePath());
					}
				}

				mController.setAppWebSite(SHARE_MEDIA.RENREN,
						MyBabyConstants.MYBABY_WEBSITE);

				SocializeConfig config = mController.getConfig();
				config.registerListener(new SnsPostListener() {

					@Override
					public void onStart() {

					}

					@Override
					public void onComplete(SHARE_MEDIA arg0, int arg1,
							SocializeEntity arg2) {

						if (arg1 == 200) {
							Toast.makeText(mContext, "分享成功", Toast.LENGTH_SHORT)
									.show();
						}
					}
				});

				// 分享面板中的平台将按照如下顺序进行排序,
				// 微信、微信朋友圈、新浪、腾讯微博、QQ、QQ空间、人人、豆瓣、邮件、短信......
				config.setPlatformOrder(SHARE_MEDIA.WEIXIN,
						SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.SINA,
						SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.TENCENT,
						SHARE_MEDIA.RENREN, SHARE_MEDIA.DOUBAN,
						SHARE_MEDIA.EMAIL, SHARE_MEDIA.SMS,
						SHARE_MEDIA.GOOGLEPLUS);

				mController.openShare(SlidesActivity.this, false);

			}
		});

		imagePagerAdapter = new ImagePagerAdapter(largeImagesPViews);
		imagePagerAdapter.setImageItems(imageItems);
		imagesViewPager.setAdapter(imagePagerAdapter);

		imagesViewPager.setCurrentItem(fromItem);
		// Set up the user interaction to manually show or hide the system UI.
		imagesViewPager
				.setOnClickListener(new ImageViewPager.OnClickListener() {

					@Override
					public void onClick(View view) {
						finishAndReturnResult();
					}

				});

	}

	/**
	 * @param loadingImageView
	 */
	private void startAnimation(ImageView loadingImageView) {
		// TODO Auto-generated method stub
		Animation animRoute = new RotateAnimation(0.0f, +360.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);// 设置位移动画
		animRoute.setDuration(1000);// 持续1秒
		animRoute.setRepeatMode(Animation.RESTART);// 重复
		animRoute.setRepeatCount(Animation.INFINITE);// 无限次

		// 旋转一次不停顿一下，需要以下两行代码
		LinearInterpolator lir = new LinearInterpolator();
		animRoute.setInterpolator(lir);
		loadingImageView.startAnimation(animRoute);
	}

	@Override
	public void onBackPressed() {
		finishAndReturnResult();
	}

	private void finishAndReturnResult() {
		Intent mIntent = new Intent();
		if (imagesViewPager.getCurrentItem() / 3 - fromFirstVisiblePosition / 3 > DEFAULT_SCROLL_ROWS
				|| imagesViewPager.getCurrentItem() < fromFirstVisiblePosition) {
			mIntent.putExtra("currentItem", imagesViewPager.getCurrentItem());
		} else {
			mIntent.putExtra("currentItem", -1);
		}
		this.setResult(RESULT_SLIDESACTIVITY, mIntent);
		finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		/** 使用SSO授权必须添加如下代码 */
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
