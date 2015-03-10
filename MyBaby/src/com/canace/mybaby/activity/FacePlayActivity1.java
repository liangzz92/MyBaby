/**
 * FacePlayActivity.java
 * 视频播放照片集（按时间排序）
 * @author liangzz
 * 2015-2-5
 */
package com.canace.mybaby.activity;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.FileHandler;

import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.canace.mybaby.R;
import com.canace.mybaby.db.model.BabyFaceThumbnail;
import com.canace.mybaby.db.model.ImageItem;
import com.canace.mybaby.utils.BitmapHelper;
import com.canace.mybaby.utils.FileHelper;
import com.canace.mybaby.utils.PreferenceUtils;
import com.umeng.analytics.MobclickAgent;

public class FacePlayActivity1 extends MyBabyActivity {

	protected static final String TAG = "FacePlayActivity1";
	private ImageItem[] imageItems;
	private Bitmap[] bitmaps;
	private ImageView imageView;
	private ImageView backbutton;
	private Handler mHandler;
	private int mImageIndex;
	private int lastIndex = 0;

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
				for (mImageIndex = 0; mImageIndex < imageItems.length; mImageIndex++) {
					Log.i(TAG, "TIME" + imageItems[mImageIndex].getTimeScore());
					String thumbnailPath = getThumbnailPath(imageItems[mImageIndex], true);
					if (!thumbnailPath.equals("")) {

						Log.i("result", imageItems[mImageIndex].getFaceInfos());
						Log.i("result", imageItems[mImageIndex].getImagePath());
						bitmaps[mImageIndex] = BitmapHelper.getBitmapFromSDCard(FileHelper
								.getThumbnailPath(imageItems[mImageIndex]));

						if (bitmaps[mImageIndex] != null) {

							if(lastIndex != 0){
								Bitmap originBitmap = BitmapHelper.getBitmapFromSDCard(FileHelper.getThumbnailPath(imageItems[lastIndex]));
								changeBitmap(originBitmap, bitmaps[mImageIndex], 50);
								lastIndex = mImageIndex;
							}
							else{
								try{
									runOnUiThread(new Runnable() {
										
										@Override
										public void run() {
											// TODO Auto-generated method stub
											imageView.setImageBitmap(bitmaps[mImageIndex]);
											imageView.postInvalidate();
										}
									});
									if(mImageIndex > 0 && mImageIndex < imageItems.length - 1 && bitmaps[mImageIndex - 1] != null){
										bitmaps[mImageIndex-1].recycle();
									}
									Thread.sleep(300);
								} catch (Exception e) {
									e.printStackTrace();
									// TODO: handle exception
								}
							
							}
							

						}
						
					}
				}
			}

		}).start();

	}

	private void changeBitmap(Bitmap originBitmap,
			final Bitmap curBitmap, int scale) {
		// TODO Auto-generated method stub
		if(scale-- <= 0){
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					imageView.setImageBitmap(curBitmap);
					imageView.postInvalidate();
				}
			});
		}
		
	}

	protected String getThumbnailPath(ImageItem imageItem, boolean force) {
		// TODO Auto-generated method stub
		if (force || PreferenceUtils.getIntValue(PreferenceUtils.THUMBNAIL_LAST_ID) < imageItem
				.getId()) {
			PreferenceUtils.saveIntValue(PreferenceUtils.THUMBNAIL_LAST_ID,
					imageItem.getId());
			return createThumbnail(imageItem);
		} else if (FileHelper.isFileExist(FileHelper
				.getThumbnailPath(imageItem))) {
			return FileHelper.getThumbnailPath(imageItem);
		}

		return "";
	}

	/**
	 * 生成宝贝脸部缩略图并序列化存储
	 * 
	 * @param imageItem
	 * @param curBitmap
	 */
	private static String createThumbnail(ImageItem imageItem) {
		// TODO Auto-generated method stub
		BabyFaceThumbnail babyFaceThumbnail = new BabyFaceThumbnail();
		babyFaceThumbnail.setFaceInfos(imageItem.getFaceInfos());
		babyFaceThumbnail.setImagePath(imageItem.getImagePath());
		float x, y, w, h;
		// get the center point
		JSONObject rst;
		try {
			rst = new JSONObject(imageItem.getFaceInfos());
			float pitch_angle = (float) -rst.getJSONArray("face")
					.getJSONObject(0).getJSONObject("attribute")
					.getJSONObject("pose").getJSONObject("pitch_angle")
					.getDouble("value");
			float yaw_angle = (float) -rst.getJSONArray("face")
					.getJSONObject(0).getJSONObject("attribute")
					.getJSONObject("pose").getJSONObject("yaw_angle")
					.getDouble("value");
			if (pitch_angle < 3 && yaw_angle < 3) {
				x = (float) rst.getJSONArray("face").getJSONObject(0)
						.getJSONObject("position").getJSONObject("nose")
						.getDouble("x");
				y = (float) rst.getJSONArray("face").getJSONObject(0)
						.getJSONObject("position").getJSONObject("nose")
						.getDouble("y");

				// get face size
				w = (float) rst.getJSONArray("face").getJSONObject(0)
						.getJSONObject("position").getDouble("width");
				h = (float) rst.getJSONArray("face").getJSONObject(0)
						.getJSONObject("position").getDouble("height");

				Bitmap curBitmap = BitmapHelper.getScaledBitmapFromSDCard(
						imageItem.getImagePath(), 600);
				// change percent value to the real size
				x = x / 100 * curBitmap.getWidth();
				w = w / 100 * curBitmap.getWidth() * 0.7f;
				y = y / 100 * curBitmap.getHeight();
				h = h / 100 * curBitmap.getHeight() * 0.9f;
				if (x - w < 0) {
					Log.e("error", "x = " + x + "w = " + w);
					Log.e("error", imageItem.getFaceInfos());
				}

				Matrix matrix = new Matrix();

				Log.i("result", rst.toString());
				// matrix.setRotate((float) -15.7514, img.getWidth() / 2,
				// img.getHeight() / 2);
				matrix.setRotate((float) -rst.getJSONArray("face")
						.getJSONObject(0).getJSONObject("attribute")
						.getJSONObject("pose").getJSONObject("roll_angle")
						.getDouble("value"), x, y);

				curBitmap = Bitmap.createBitmap(curBitmap, 0, 0,
						curBitmap.getWidth(), curBitmap.getHeight(), matrix,
						false);
				Bitmap thumbnailBitmap = Bitmap.createBitmap(curBitmap,
						(int) Math.max(0, Math.floor(x - w)),
						(int) Math.max(0, Math.floor(y - h)),
						(int) Math.floor(2 * w), (int) Math.floor(2 * h));
				babyFaceThumbnail.setThumbnailBitmap(thumbnailBitmap);
				String thumbnail_path = FileHelper.getThumbnailPath(imageItem);
				FileOutputStream foStream = new FileOutputStream(thumbnail_path);
				babyFaceThumbnail.getThumbnailBitmap().compress(
						CompressFormat.JPEG, 100, foStream);
				// ObjectOutputStream oos = new ObjectOutputStream(foStream);
				// oos.writeObject(babyFaceThumbnail);
				// oos.flush();
				foStream.flush();
				// oos.close();
				foStream.close();
				return thumbnail_path;
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	private void initComponents() {

		imageView = (ImageView) findViewById(R.id.imageView);
		backbutton = (ImageView) findViewById(R.id.backbutton);
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