/**
 * OnlineFaceDetecter.java
 * 多线程在线人脸检测服务，获取人脸位置、人物性别、年龄、笑容等信息
 * 当人物age<7时，为图片生成ImageItem并存入数据库，作为UI界面显示图片的唯一来源
 * @author liangzz
 * 2014-12-15
 */
package com.canace.mybaby.cache.scanner;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.FileHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.canace.mybaby.cache.loader.FileCache;
import com.canace.mybaby.db.DBFacade;
import com.canace.mybaby.db.model.BabyFaceThumbnail;
import com.canace.mybaby.db.model.DetectImage;
import com.canace.mybaby.db.model.FaceInfo;
import com.canace.mybaby.db.model.ImageItem;
import com.canace.mybaby.db.utils.Model;
import com.canace.mybaby.handler.factory.AbstractFactory;
import com.canace.mybaby.handler.factory.GetFaceInfoFactory;
import com.canace.mybaby.handler.httphandler.HttpHandler;
import com.canace.mybaby.handler.httphandler.HttpHandler.OnDataCollectFinishedListener;
import com.canace.mybaby.handler.parser.JsonParser;
import com.canace.mybaby.handler.utils.PostParameters;
import com.canace.mybaby.utils.BitmapHelper;
import com.canace.mybaby.utils.CommonsUtil;
import com.canace.mybaby.utils.FileHelper;

public class OnlineDetectService extends IntentService {

	/**
	 * @param name
	 */
	public OnlineDetectService() {
		super("OnlineDetectService");
	}

	private static final String TAG = "OnlineFaceDetecter";
	private static final String ACTION_ONLINE_DETECT = "com.canace.mybaby.cache.scanner.action.ONLINE_DETECT";
	private static DetectImage[] detectImages = null;
	private static HandlerThread detectThread = null;
	private static Handler detectHandler = null;
	private static int waitingCount = 0;
	private static int totalCount = 0;
	private static int detectedCount = 0;

	/**
	 * @return the totalCount
	 */
	public static int getTotalCount() {
		return totalCount;
	}

	/**
	 * @return the detectCount
	 */
	public static int getDetectedCount() {
		return detectedCount;
	}

	private static Context mContext;
	private static Map<String, Integer> waitingQueueMap = new HashMap<String, Integer>();

	private static Object syncObject = new Object();

	/**
	 * 判断在线检测服务是否在运行中
	 * 
	 * @return
	 */
	public static boolean isDetecting() {
		return (detectImages != null && detectImages.length > 0)
				|| waitingCount > 0 || totalCount > 0 || detectedCount > 0;
	}

	/**
	 * 在线获取age, smile_score等检测人脸信息 如果有age<7的人脸，生成ImageItem并存入数据库
	 * 
	 * @param context
	 * @param path
	 */
	private static void getFaceInfo(final Context context,
			final DetectImage detectImage) {

		if (detectHandler == null || detectThread == null) {
			initComponents(context);
		}

		detectHandler.post(new Runnable() {

			@Override
			public void run() {

				totalCount++;
				try {
					while (waitingCount > 3) {
						Thread.sleep(300);
					}
					waitingCount++;
					final Bitmap curBitmap = BitmapHelper
							.getScaledBitmapFromSDCard(
									detectImage.getImagePath(), 600);
					ByteArrayOutputStream stream = new ByteArrayOutputStream();
					curBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
					byte[] array = stream.toByteArray();

					AbstractFactory getFaceInfoFacory = new GetFaceInfoFactory();
					HttpHandler httpHandler = getFaceInfoFacory
							.createHttpHandler();
					JsonParser jsonParser = getFaceInfoFacory
							.createJsonParser();
					httpHandler.setMultiEntity(new PostParameters().setImg(
							array).setAttribute(
							"gender,age,race,smiling,glass,pose"));
					httpHandler
							.setOnDataCollectFinishedListener(new OnDataCollectFinishedListener() {

								@Override
								public void onDataCollect(List<Model> models,
										boolean isSuccess) {
									if (CommonsUtil.DEBUG) {
										Log.i(TAG, "online check :"
												+ detectImage.getImagePath());
									}

									// 网络请求抛出异常时models == null
									// 如果没有age<7的faceInfo，models.size() == 0
									if (models != null && models.size() > 0) {
										FaceInfo faceInfo = (FaceInfo) models
												.get(0);
										ImageItem imageItem = new ImageItem();
										imageItem.setFaceInfos(faceInfo
												.getInfos());
										imageItem.setImagePath(detectImage
												.getImagePath());
										imageItem.setSmileScore(faceInfo
												.getSmileScore());
										imageItem.setTimeScore(detectImage
												.getItemTimeScore());
										imageItem.setIdHashcode(detectImage
												.getIdHashcode());
										if (!existInDB(imageItem)) {
											if (CommonsUtil.DEBUG) {
												Log.i(TAG,
														"image item:"
																+ imageItem
																		.getImagePath());
											}
											DBFacade.save(imageItem);
//											createThumbnail(imageItem,
//													curBitmap);
										} else {
											if (CommonsUtil.DEBUG) {
												Log.i(TAG,
														"same image item :"
																+ imageItem
																		.getImagePath());
											}
										}

									}
									if (models == null) {
										waitingQueueMap.put(
												detectImage.getIdHashcode(),
												DetectService.DETECT_FAILED);

									} else {
										waitingQueueMap.put(
												detectImage.getIdHashcode(),
												DetectService.DETECT_SUCCEEDED);
										detectImage.setNeedOnlineDetect(0);
										DBFacade.update(detectImage);
									}

									curBitmap.recycle();
									System.gc();
									waitingCount--;

									detectedCount++;
									if (detectedCount >= totalCount) {
										totalCount = 0;
										detectedCount = 0;
									}

								}

							});
					httpHandler.setJsonParser(jsonParser);
					httpHandler.execute();

				} catch (Exception exception) {
					Log.i(TAG, "GetBitmapFailed" + detectImage.getImagePath());
					exception.printStackTrace();

				}
			}

		});

	}

	

	/**
	 * @param detectImage
	 * @return
	 */
	protected static boolean insertWaitingQueueMap(DetectImage detectImage) {
		if (!waitingQueueMap.containsKey(detectImage.getIdHashcode())) {
			waitingQueueMap.put(detectImage.getIdHashcode(),
					DetectService.DETECT_WAITING);
			return true;
		} else if (waitingQueueMap.get(detectImage.getIdHashcode()).equals(
				DetectService.DETECT_FAILED)) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * 在插入数据库前判断是否有重复元素
	 * 
	 * @param imageItem
	 * @return
	 */
	private static boolean existInDB(ImageItem imageItem) {
		ImageItem[] imageItems = (ImageItem[]) DBFacade.findByFieldName(
				ImageItem.class, "id_hashcode", imageItem.getIdHashcode());
		if (imageItems != null && imageItems.length > 0) {
			return true;
		}
		return false;
	}

	/**
	 * 初始化类成员变量及数据库连接
	 */
	private static void initComponents(Context context) {

		if (detectThread == null) {
			detectThread = new HandlerThread("detect-online");
			detectThread.start();
		}
		if (detectHandler == null) {
			detectHandler = new Handler(detectThread.getLooper());
		}

		if (context != null) {
			mContext = context;
			CommonsUtil.initDBSettings(mContext);
		}

	}

	/**
	 * 开启线程对单一图片进行在线检测, 在本地检测出有人脸信息后调用
	 * 
	 * @param context
	 * @param detectImage
	 */
	public static void startOnlineDetect(Context context,
			DetectImage detectImage) {
		if (CommonsUtil.checkOnlineDetectOrNot(context)) {
			if (insertWaitingQueueMap(detectImage)) {
				getFaceInfo(context, detectImage);
			}
		}
	}

	/**
	 * 检测detectImage是否已在队列中
	 * 
	 * @param detectImage
	 * @return
	 */
	public static boolean checkDuplicate(DetectImage detectImage) {
		if (detectImages == null) {
			return true;
		} else {
			for (int i = 0; i < detectImages.length; i++) {
				if (detectImages[i].getId().equals(detectImage.getId())) {
					return false;
				}
			}
			return true;
		}
	}

	/**
	 * 从数据库获取所有需要在线检测的图片列表，开启多线程进行网络访问, 在本地检测全部结束以及网络状态变化的情况下调用
	 * 
	 * @param context
	 */
	public static void startOnlineDetect(Context context) {

		mContext = context;
		final Intent intent = new Intent(ACTION_ONLINE_DETECT, null, context,
				OnlineDetectService.class);
		context.startService(intent);

	}

	/**
	 * 从需要在线检测的图片中筛选出已本地检测的图片
	 * 
	 * @return
	 */
	public static DetectImage[] filterHasLocalDetectedImages() {
		List<DetectImage> tmpDetectImages = new ArrayList<DetectImage>();
		for (int i = 0; i < detectImages.length; i++) {
			if (detectImages[i].getHasLocalDetect().equals(1)) {
				tmpDetectImages.add(detectImages[i]);
			}
		}
		DetectImage[] ret_detectImages = new DetectImage[tmpDetectImages.size()];
		for (int i = 0; i < tmpDetectImages.size(); i++) {
			ret_detectImages[i] = tmpDetectImages.get(i);
		}
		return ret_detectImages;
	}

	/**
	 * 返回正在等待在线检测的图片数量，可作为缓存服务是否终止的判断条件之一
	 * 
	 * @return
	 */
	public static int getWaitingCount() {
		return waitingCount;
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		synchronized (syncObject) {

			detectImages = (DetectImage[]) DBFacade.findByFieldName(
					DetectImage.class, "needOnlineDetect", 1);
			// detectImages = filterHasLocalDetectedImages();
			if (CommonsUtil.DEBUG) {
				Log.i(TAG, "detectImages.length = " + detectImages.length);
			}

			if (detectImages != null && detectImages.length > 0) {

				for (int i = 0; i < detectImages.length; i++) {
					if (mContext != null
							&& CommonsUtil.checkOnlineDetectOrNot(mContext)) {
						if (insertWaitingQueueMap(detectImages[i])) {
							getFaceInfo(mContext, detectImages[i]);
						}
					}

				}

			}

			detectImages = null;
		}
	}
}
