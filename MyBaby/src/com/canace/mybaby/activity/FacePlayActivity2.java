/**
 * FacePlayActivity.java
 * 视频播放照片集（按时间排序）
 * @author liangzz
 * 2015-2-5
 */
package com.canace.mybaby.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.FileHandler;

import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.canace.mybaby.R;
import com.canace.mybaby.cache.loader.FileCache;
import com.canace.mybaby.db.model.BabyFaceThumbnail;
import com.canace.mybaby.db.model.ImageItem;
import com.canace.mybaby.utils.BitmapHelper;
import com.canace.mybaby.utils.CommonsUtil;
import com.canace.mybaby.utils.FileHelper;
import com.canace.mybaby.utils.MyBabyConstants;
import com.canace.mybaby.utils.PreferenceUtils;
import com.canace.mybaby.utils.SortByTime;
import com.tencent.a.b.p;
import com.umeng.analytics.MobclickAgent;

public class FacePlayActivity2 extends MyBabyActivity {

	protected static final String TAG = "FacePlayActivity1";
	private ImageItem[] imageItems;
	private Bitmap[] bitmaps;
	private ImageView imageView;
	private ImageView thumbImageView;
	private ImageView backbutton;
	private ImageView nextbutton;
	boolean isPrepareing = false;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MyBabyConstants.MSG_PREPARE_COMPLETE:
				stopAnimation();
				faceplay();
				break;

			default:
				break;
			}
		}

	};
	private int mImageIndex = 0;
	private float ref_eye_spacing = 0;
	private float ref_width = 0;
	private float ref_height = 0;
	private int lastIndex = 0;
	private String originBitmapPath = "";
	private Bitmap currentBitmap = null;
	private ProgressDialog mProgressDialog = null;
	private List<String> bitmapPathList = new ArrayList<String>();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_face_play1);
		initComponents();
		prepareBitmaps();

	}

	private void prepareBitmaps() {
		// TODO Auto-generated method stub
		startAnimation();
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				while (mImageIndex < imageItems.length) {
					Log.i(TAG, "TIME" + imageItems[mImageIndex].getTimeScore());
					Log.i("result", imageItems[mImageIndex].getFaceInfos());
					Log.i("result", imageItems[mImageIndex].getImagePath());
					String thumbnailPath = FileHelper
							.getThumbnailPath(imageItems[mImageIndex]);

					try {
						JSONObject rst = new JSONObject(imageItems[mImageIndex]
								.getFaceInfos());
						float pitch_angle = (float) -rst.getJSONArray("face")
								.getJSONObject(0).getJSONObject("attribute")
								.getJSONObject("pose")
								.getJSONObject("pitch_angle")
								.getDouble("value");
						float yaw_angle = (float) -rst.getJSONArray("face")
								.getJSONObject(0).getJSONObject("attribute")
								.getJSONObject("pose")
								.getJSONObject("yaw_angle").getDouble("value");
						Log.i("pitch_angle", pitch_angle + "");
						Log.i("yaw_angle", yaw_angle + "");
						if (true || Math.abs(pitch_angle) < 3
								&& Math.abs(yaw_angle) < 3) {
							if (!FileHelper.isFileExist(thumbnailPath)) {
								Bitmap img = BitmapHelper
										.getScaledBitmapFromSDCard(
												imageItems[mImageIndex]
														.getImagePath(), 600);

								// create a new canvas
								Bitmap bitmap = Bitmap.createBitmap(
										img.getWidth(), img.getHeight(),
										img.getConfig());
								Canvas canvas = new Canvas(bitmap);

								float x, y, w, h;
								// get the center point
								x = ((float) rst.getJSONArray("face")
										.getJSONObject(0)
										.getJSONObject("position")
										.getJSONObject("eye_left")
										.getDouble("x") + (float) rst
										.getJSONArray("face").getJSONObject(0)
										.getJSONObject("position")
										.getJSONObject("eye_right")
										.getDouble("x")) / 2;
								y = ((float) rst.getJSONArray("face")
										.getJSONObject(0)
										.getJSONObject("position")
										.getJSONObject("eye_left")
										.getDouble("y") + (float) rst
										.getJSONArray("face").getJSONObject(0)
										.getJSONObject("position")
										.getJSONObject("eye_right")
										.getDouble("y")) / 2;

								// get face size
								w = (float) rst.getJSONArray("face")
										.getJSONObject(0)
										.getJSONObject("position")
										.getDouble("width");
								h = (float) rst.getJSONArray("face")
										.getJSONObject(0)
										.getJSONObject("position")
										.getDouble("height");

								// float scale_ratio = x * 2 / ref_width;

								// change percent value to the real size
								x = x / 100 * img.getWidth();
								w = w / 100 * img.getWidth() * 0.7f;
								y = y / 100 * img.getHeight();
								h = h / 100 * img.getHeight() * 0.8f;

								if (ref_eye_spacing == 0) {
									ref_eye_spacing = 2 * x;
									ref_width = 2 * w;
									ref_height = 2 * h;
								}

								Matrix matrix = new Matrix();
								matrix.setRotate(
										(float) -rst.getJSONArray("face")
												.getJSONObject(0)
												.getJSONObject("attribute")
												.getJSONObject("pose")
												.getJSONObject("roll_angle")
												.getDouble("value"), x, y);

								canvas.drawBitmap(img, matrix, null);

								// save new image
								img = bitmap;

								// runOnUiThread(new Runnable() {
								//
								// @Override
								// public void run() {
								// // TODO Auto-generated method stub
								// imageView.setImageBitmap(img);
								// imageView.postInvalidate();
								// }
								// });

								Bitmap thumbnailBitmap = Bitmap.createBitmap(
										img,
										(int) Math.max(0, Math.floor(x - w)),
										(int) Math.max(0, Math.floor(y - h)),
										(int) Math.min(Math.floor(2 * w),
												img.getWidth() - x + w),
										(int) Math.min(Math.floor(2 * h),
												img.getHeight() - y + h));

								if (ref_eye_spacing == 0) {
									ref_eye_spacing = 2 * x;
									ref_width = thumbnailBitmap.getWidth();
									ref_height = thumbnailBitmap.getHeight();
								}
								// 获得图片的宽高
								int width = thumbnailBitmap.getWidth();
								int height = thumbnailBitmap.getHeight();

								// 计算缩放比例
								float scaleWidth = ((float) ref_width) / width;
								float scaleHeight = ((float) ref_height)
										/ height;
								// 取得想要缩放的matrix参数
								Matrix matrix1 = new Matrix();
								matrix1.postScale(scaleWidth, scaleHeight);
								// 得到新的图片
								thumbnailBitmap = Bitmap.createBitmap(
										thumbnailBitmap, 0, 0, width, height,
										matrix1, true);

								thumbnailBitmap = Bitmap.createScaledBitmap(
										thumbnailBitmap, (int) ref_width,
										(int) ref_height, false);
								Log.i("width", "" + thumbnailBitmap.getWidth());
								Log.i("height",
										"" + thumbnailBitmap.getHeight());
								BitmapHelper.saveBitmapToSDCard(thumbnailPath,
										thumbnailBitmap);
								Log.i("thumbnailPath", thumbnailPath);

							}
							if (!originBitmapPath.equals("")) {
								changeBitmap_mean(originBitmapPath,
										thumbnailPath);
							}
							bitmapPathList.add(thumbnailPath);
							Log.i("bitmapPathList", thumbnailPath);
							originBitmapPath = thumbnailPath;
						}

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					mImageIndex++;

				}

				mHandler.sendEmptyMessage(MyBabyConstants.MSG_PREPARE_COMPLETE);
			}
		}).start();

		nextbutton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				faceplay();
			}
		});
	}

	protected void faceplay() {
		// TODO Auto-generated method stub
		if (!isPrepareing) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						for (int i = 0; i < bitmapPathList.size(); i++) {
							final int index = i;
							runOnUiThread(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub

									Log.i("faceplay", bitmapPathList.get(index));
									currentBitmap = BitmapHelper
											.getBitmapFromSDCard(bitmapPathList
													.get(index));
									thumbImageView
											.setImageBitmap(currentBitmap);
									thumbImageView.postInvalidate();
								}

							});
							Thread.sleep(1000 / 30);
						}
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}).start();

		}
	}

	private void changeBitmap_mean(String originBitmapPath, String thumbnailPath) {
		Bitmap originBitmap = BitmapHelper
				.getBitmapFromSDCard(originBitmapPath);
		Log.i("originBitmap", originBitmap.getWidth() + "");
		Log.i("originBitmap", originBitmap.getHeight() + "");
		Bitmap curBitmap = BitmapHelper.getBitmapFromSDCard(thumbnailPath);
		Log.i("curBitmap", curBitmap.getWidth() + "");
		Log.i("curBitmap", curBitmap.getHeight() + "");
		float delta = (float) 1 / 90;
		Bitmap tmpBitmap = originBitmap.copy(originBitmap.getConfig(), true);
		float t = (float) 1 / 90;
		while (t < 1) {

			String savePath = FileHelper.getImagesCacheDirectory()
					+ originBitmapPath.substring(originBitmapPath
							.lastIndexOf('/') + 1)
					+ "_"
					+ thumbnailPath
							.substring(thumbnailPath.lastIndexOf('/') + 1)
					+ "_" + (int) (t * 100) + ".jpg";

			if (!FileHelper.isFileExist(savePath)) {
				for (int i = 0; i < originBitmap.getWidth(); i++) {
					for (int j = 0; j < originBitmap.getHeight(); j++) {
						int origin_color = originBitmap.getPixel(i, j);
						int cur_color = curBitmap.getPixel(i, j);
						int alpha = (int) ((1 - t) * Color.alpha(origin_color) + t
								* Color.alpha(cur_color));
						int red = (int) ((1 - t) * Color.red(origin_color) + t
								* Color.red(cur_color));
						int green = (int) ((1 - t) * Color.green(origin_color) + t
								* Color.green(cur_color));
						int blue = (int) ((1 - t) * Color.blue(origin_color) + t
								* Color.blue(cur_color));
						tmpBitmap.setPixel(i, j,
								Color.argb(alpha, red, green, blue));

					}
				}

				BitmapHelper.saveBitmapToSDCard(savePath, tmpBitmap);
				Log.i("tmpBitmap", savePath);
			}

			bitmapPathList.add(savePath);
			t += delta;
		}
		originBitmap.recycle();
		curBitmap.recycle();
		tmpBitmap.recycle();

	}

	private void changeBitmap_morphing(Bitmap originBitmap, Bitmap curBitmap) {
		// TODO Auto-generated method stub

		float delta = (float) 0.1;

		int width = originBitmap.getWidth();
		int height = originBitmap.getHeight();
		Bitmap cBitmap = RGB2GRAY(originBitmap);
		Bitmap dBitmap = RGB2GRAY(curBitmap);
		HSI[][] A_HSI = new HSI[width][height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				HSI tmp = new HSI();
				int rgb = originBitmap.getPixel(i, j);
				tmp.RGB2HSI(Color.red(rgb), Color.green(rgb), Color.blue(rgb));
				A_HSI[i][j] = tmp;
			}

		}

		HSI[][] B_HSI = new HSI[width][height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				HSI tmp = new HSI();
				int rgb = curBitmap.getPixel(i, j);
				tmp.RGB2HSI(Color.red(rgb), Color.green(rgb), Color.blue(rgb));
				B_HSI[i][j] = tmp;
			}

		}

		float t = 0;
		while (t <= 1) {
			currentBitmap = Bitmap.createBitmap(width, height,
					originBitmap.getConfig());
			if (t < 0.5) {
				for (int i = 0; i < width; i++) {
					for (int j = 0; j < height; j++) {
						int r, g, b;
						if (A_HSI[i][j].H < 120) {
							float H = A_HSI[i][j].H;
							r = (int) ((1 - t)
									* A_HSI[i][j].I
									* (1 + (1 - 2 * t) * A_HSI[i][j].S
											* Math.cos(H) / Math.cos(60 - H)) * A_HSI[i][j].TOTAL);
							b = (int) ((1 - t) * A_HSI[i][j].I
									* (1 - (1 - 2 * t) * A_HSI[i][j].S) * A_HSI[i][j].TOTAL);
							g = A_HSI[i][j].TOTAL - r - b;
						} else if (A_HSI[i][j].H < 240) {
							float H = A_HSI[i][j].H - 120;
							r = (int) ((1 - t) * A_HSI[i][j].I
									* (1 - (1 - 2 * t) * A_HSI[i][j].S) * A_HSI[i][j].TOTAL);
							g = (int) ((1 - t)
									* A_HSI[i][j].I
									* (1 + (1 - 2 * t) * A_HSI[i][j].S
											* Math.cos(H) / Math.cos(60 - H)) * A_HSI[i][j].TOTAL);
							b = A_HSI[i][j].TOTAL - r - g;
						} else {
							float H = A_HSI[i][j].H - 240;
							g = (int) ((1 - t) * A_HSI[i][j].I
									* (1 - (1 - 2 * t) * A_HSI[i][j].S) * A_HSI[i][j].TOTAL);
							b = (int) ((1 - t)
									* A_HSI[i][j].I
									* (1 + (1 - 2 * t) * A_HSI[i][j].S
											* Math.cos(H) / Math.cos(60 - H)) * A_HSI[i][j].TOTAL);
							r = A_HSI[i][j].TOTAL - b - g;
						}
						int dColor = cBitmap.getPixel(i, j);
						r += (int) (t * Color.red(dColor));
						g += (int) (t * Color.green(dColor));
						b += (int) (t * Color.blue(dColor));
						currentBitmap.setPixel(i, j, Color.rgb(r, g, b));
					}
				}
			} else {
				for (int i = 0; i < width; i++) {
					for (int j = 0; j < height; j++) {
						int r, g, b;
						if (B_HSI[i][j].H < 120) {
							float H = B_HSI[i][j].H;
							r = (int) (t
									* B_HSI[i][j].I
									* (1 - (1 - 2 * t) * B_HSI[i][j].S
											* Math.cos(H) / Math.cos(60 - H)) * B_HSI[i][j].TOTAL);
							b = (int) (t * B_HSI[i][j].I
									* (1 + (1 - 2 * t) * B_HSI[i][j].S) * B_HSI[i][j].TOTAL);
							g = B_HSI[i][j].TOTAL - r - b;
						} else if (B_HSI[i][j].H < 240) {
							float H = B_HSI[i][j].H - 120;
							r = (int) (t * B_HSI[i][j].I
									* (1 + (1 - 2 * t) * B_HSI[i][j].S) * B_HSI[i][j].TOTAL);
							g = (int) (t
									* B_HSI[i][j].I
									* (1 - (1 - 2 * t) * B_HSI[i][j].S
											* Math.cos(H) / Math.cos(60 - H)) * B_HSI[i][j].TOTAL);
							b = B_HSI[i][j].TOTAL - r - g;
						} else {
							float H = B_HSI[i][j].H - 240;
							g = (int) (t * B_HSI[i][j].I
									* (1 + (1 - 2 * t) * B_HSI[i][j].S) * B_HSI[i][j].TOTAL);
							b = (int) (t
									* B_HSI[i][j].I
									* (1 - (1 - 2 * t) * B_HSI[i][j].S
											* Math.cos(H) / Math.cos(60 - H)) * B_HSI[i][j].TOTAL);
							r = B_HSI[i][j].TOTAL - b - g;
						}
						int dColor = dBitmap.getPixel(i, j);
						r += (int) ((1 - t) * Color.red(dColor));
						g += (int) ((1 - t) * Color.green(dColor));
						b += (int) ((1 - t) * Color.blue(dColor));
						currentBitmap.setPixel(i, j, Color.rgb(r, g, b));
					}
				}
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						thumbImageView.setImageBitmap(currentBitmap);
						thumbImageView.postInvalidate();
					}
				});

				t += delta;
			}
		}

	}

	public Bitmap RGB2GRAY(Bitmap originBitmap) {
		int width = originBitmap.getWidth();
		int height = originBitmap.getHeight();
		Bitmap cBitmap = Bitmap.createBitmap(width, height,
				originBitmap.getConfig());
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				int color = originBitmap.getPixel(i, j);
				int a = Color.alpha(color);
				int x = (int) (0.3 * Color.red(color) + 0.59
						* Color.green(color) + 0.11 * Color.blue(color));
				cBitmap.setPixel(i, j, Color.argb(a, x, x, x));
			}
		}

		return cBitmap;

	}

	public class GrayBitmap {
		public int width;
		public int height;
		public float[][] pixels;

		public GrayBitmap(Bitmap originBitmap) {
			width = originBitmap.getWidth();
			height = originBitmap.getHeight();
			pixels = new float[width][height];
			for (int i = 0; i < width; i++) {
				for (int j = 0; j < height; j++) {
					int color = originBitmap.getPixel(i, j);
					pixels[i][j] = (float) ((0.3 * Color.red(color) + 0.59
							* Color.green(color) + 0.11 * Color.blue(color)) / 255);
				}
			}
		}
	}

	public class HSI {
		public int TOTAL;
		public float H;
		public float S;
		public float I;

		public void RGB2HSI(int r, int g, int b) {
			TOTAL = (r + g + b);
			float R = (float) r / TOTAL;
			float G = (float) g / TOTAL;
			float B = (float) b / TOTAL;
			// For H
			H = (float) Math.acos(0.5 * (2 * R - G - B)
					/ Math.sqrt(Math.pow(R - G, 2) + (R - G) * (G - B)));
			if (B > G) {
				H = 360 - H;
			}

			// For S
			float min = Math.min(Math.min(R, G), B);
			S = 1 - 3 * min / (R + G + B);

			// For I
			I = (R + G + B) / 3;
		}

	}

	private String getThumbnailPath(ImageItem imageItem, boolean force) {
		// TODO Auto-generated method stub
		if (force
				|| PreferenceUtils
						.getIntValue(PreferenceUtils.THUMBNAIL_LAST_ID) < imageItem
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

		thumbImageView = (ImageView) findViewById(R.id.thumbnail);
		nextbutton = (ImageView) findViewById(R.id.sharebutton);

		backbutton = (ImageView) findViewById(R.id.backbutton);
		backbutton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		imageItems = HomePageActivity.initImageItems();
		sortImageItems();
		bitmaps = new Bitmap[imageItems.length];

	}

	private void sortImageItems() {
		// TODO Auto-generated method stub
		SortedSet<ImageItem> sortedImageItems = new TreeSet<ImageItem>(
				new SortByTime());
		if (imageItems != null && sortedImageItems != null) {
			for (int i = 0; i < imageItems.length; i++) {
				sortedImageItems.add(imageItems[i]);
			}
			Iterator<ImageItem> iterator = sortedImageItems.iterator();
			int j = 0;
			while (iterator.hasNext()) {
				imageItems[j++] = iterator.next();
				if (CommonsUtil.DEBUG) {
					Log.i("ascent", imageItems[j - 1].getTimeScore() + "");
				}
			}

		}
	}

	private void startAnimation() {
		thumbImageView.setImageResource(R.drawable.refreshing);
		Animation animRoute = new RotateAnimation(0.0f, +360.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);// 设置位移动画
		animRoute.setDuration(1000);// 持续1秒
		animRoute.setRepeatMode(Animation.RESTART);// 重复
		animRoute.setRepeatCount(Animation.INFINITE);// 无限次

		// 旋转一次不停顿一下，需要以下两行代码
		LinearInterpolator lir = new LinearInterpolator();
		animRoute.setInterpolator(lir);
		thumbImageView.startAnimation(animRoute);
		isPrepareing = true;
	}

	private void stopAnimation() {
		if (thumbImageView != null) {
			thumbImageView.clearAnimation();
			isPrepareing = false;
		}
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