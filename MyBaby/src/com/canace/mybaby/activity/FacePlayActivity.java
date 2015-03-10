/**
 * FacePlayActivity.java
 * 视频播放照片集（按时间排序）
 * @author liangzz
 * 2015-2-5
 */
package com.canace.mybaby.activity;

import java.util.logging.FileHandler;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.canace.mybaby.R;
import com.canace.mybaby.db.model.ImageItem;
import com.canace.mybaby.utils.BitmapHelper;
import com.canace.mybaby.utils.FileHelper;
import com.umeng.analytics.MobclickAgent;

public class FacePlayActivity extends MyBabyActivity {

	private ImageItem[] imageItems;
	private Bitmap[] bitmaps;
	private ImageView imageView;
	private ImageView backbutton;
	private Handler mHandler;
	private int mImageIndex;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 全屏
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_face_play);
		initComponents();
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				for(mImageIndex = 0; mImageIndex < imageItems.length; mImageIndex++){
					try {
						runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								Log.i("result", imageItems[mImageIndex].getFaceInfos());
								Log.i("result", imageItems[mImageIndex].getImagePath());
								bitmaps[mImageIndex] = BitmapHelper.getBitmapFromSDCard(FileHelper.getThumbnailPath(imageItems[mImageIndex]));
								if(bitmaps[mImageIndex] != null){
									imageView.setImageBitmap(bitmaps[mImageIndex]);
									imageView.postInvalidate();
								}
								
							}
						});
						Thread.sleep(300);
					} catch (Exception e) {
						e.printStackTrace();
						// TODO: handle exception
					}
				}
			}
		}).start();
		
		
	}

	private void initComponents() {
		
		imageView = (ImageView)findViewById(R.id.imageView);
		backbutton = (ImageView)findViewById(R.id.backbutton);
		backbutton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		imageItems = HomePageActivity.initImageItems();
		bitmaps = new Bitmap[imageItems.length];
		
	}

	@Override
	public void onBackPressed() {
		finish();
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