/**
 * LocalDetectService.java
 * 扫描媒体文件结束后启动本地检测服务
 * 将人物照片信息存入数据库并开启在线检测服务获取更多人脸信息
 * @author liangzz
 * 2014-12-15
 */
package com.canace.mybaby.cache.scanner;

import java.util.HashMap;
import java.util.Map;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.canace.mybaby.db.DBFacade;
import com.canace.mybaby.db.model.DetectImage;
import com.canace.mybaby.utils.BitmapHelper;
import com.canace.mybaby.utils.CommonsUtil;
import com.canace.mybaby.utils.MyBabyConstants;
import com.faceplusplus.api.FaceDetecter;
import com.faceplusplus.api.FaceDetecter.Face;

public class LocalDetectService extends IntentService {

	/**
	 * @param name
	 */
	public LocalDetectService() {
		super("LocalFaceDetecter");
	}

	private static final String TAG = "LocalFaceDetecter";
	private static final String ACTION_LOCAL_DETECT = "com.canace.mybaby.cache.scanner.action.LOCAL_DETECT";
	private static DetectImage[] detectImages = null;

	private static HandlerThread detectThread = null;
	private static Handler detectHandler = null;
	private static FaceDetecter detecter = null;

	private static int totalCount = 0;
	private static int detectedCount = 0;

	/**
	 * @return the totalCount
	 */
	public static int getTotalCount() {
		return totalCount;
	}

	/**
	 * @return the detectedCount
	 */
	public static int getDetectedCount() {
		return detectedCount;
	}

	private static Object syncObject = new Object();
	private static int waitingCount = 0;
	private static Map<String, Integer> waitingQueueMap = new HashMap<String, Integer>();
	private static Context mContext;

	public static boolean isDetecting() {
		return (detectImages != null && detectImages.length > 0)
				|| waitingCount > 0 || totalCount > 0 || detectedCount > 0;
	}

	/**
	 * @param context
	 * @param path
	 */
	private static void getFaceInfo(final Context context,
			final DetectImage localDetectImage) {
		// TODO Auto-generated method stub

		if (detectHandler == null || detectThread == null) {
			initComponents(context);
		}

		detectHandler.post(new Runnable() {

			@Override
			public void run() {
				Face[] faceinfo = null;
				Bitmap curBitmap = null;
				try {
					while (waitingCount > 3) {
						Thread.sleep(300);
					}
					waitingCount++;
					curBitmap = BitmapHelper.getScaledBitmapFromSDCard(
							localDetectImage.getImagePath(), 600);

					faceinfo = detecter.findFaces(curBitmap);// 进行人脸检测
				} catch (Exception exception) {
					if (CommonsUtil.DEBUG) {
						Log.i(TAG,
								"GetBitmapFailed"
										+ localDetectImage.getImagePath());
						exception.printStackTrace();
					}
					waitingQueueMap.put(localDetectImage.getIdHashcode(),
							DetectService.DETECT_FAILED);

				}

				if (faceinfo != null && faceinfo.length >= 1) {
					localDetectImage.setHasLocalDetect(1);
					localDetectImage.setNeedOnlineDetect(1);
					DBFacade.update(localDetectImage);
					waitingQueueMap.put(localDetectImage.getIdHashcode(),
							DetectService.DETECT_SUCCEEDED);

					if (CommonsUtil.DEBUG) {
						Log.i(TAG,
								"Image has face:"
										+ localDetectImage.getImagePath());
					}
					OnlineDetectService.startOnlineDetect(context,
							localDetectImage);
				} else {
					localDetectImage.setHasLocalDetect(1);
					localDetectImage.setNeedOnlineDetect(0);
					DBFacade.update(localDetectImage);
					waitingQueueMap.put(localDetectImage.getIdHashcode(),
							DetectService.DETECT_SUCCEEDED);

				}
				if (curBitmap != null) {
					curBitmap.recycle();
					System.gc();
				}

				waitingCount--;
				detectedCount++;
				if (detectedCount >= totalCount) {
					totalCount = 0;
					detectedCount = 0;
				}

			}
		});
	}

	/**
	 * @param localDetectImage
	 * @return
	 */
	private static boolean insertWaintingQueueMap(DetectImage localDetectImage) {
		if (!waitingQueueMap.containsKey(localDetectImage.getIdHashcode())) {
			waitingQueueMap.put(localDetectImage.getIdHashcode(),
					DetectService.DETECT_WAITING);
			return true;
		} else if (waitingQueueMap.get(localDetectImage.getIdHashcode())
				.equals(DetectService.DETECT_FAILED)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Initialize Detecter components and Database connection.
	 */
	private static void initComponents(Context context) {
		// TODO Auto-generated method stub
		if (detectThread == null) {
			detectThread = new HandlerThread("detect-local");
			detectThread.start();
		}
		if (detectHandler == null) {
			detectHandler = new Handler(detectThread.getLooper());
		}

		if (detecter == null) {
			detecter = new FaceDetecter();
		}

		if (context != null) {
			mContext = context;
			CommonsUtil.initDBSettings(mContext);
			detecter.init(mContext, MyBabyConstants.FACEPP_API_KEY);
		}

	}

	/**
	 * 从数据库获取所有需要本地检测的图片列表，开启多线程进行检测，在扫描完成后调用
	 */
	public static void startLocalDetect(Context context) {
		mContext = context;
		final Intent intent = new Intent(ACTION_LOCAL_DETECT, null, context,
				LocalDetectService.class);
		context.startService(intent);

	}

	/**
	 * Let totalCount plus 1 when scanning local media items.
	 */
	public static void addTotalCount() {
		totalCount++;
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		synchronized (syncObject) {

			detectImages = (DetectImage[]) DBFacade.findByFieldName(
					DetectImage.class, "hasLocalDetect", 0);
			if (CommonsUtil.DEBUG) {
				Log.i(TAG, "detectImages.length = " + detectImages.length);
			}
			if (detectImages != null && detectImages.length > 0) {
				totalCount = detectImages.length;
				detectedCount = 0;
				for (int i = 0; i < detectImages.length; i++) {
					if (insertWaintingQueueMap(detectImages[i])) {
						getFaceInfo(mContext, detectImages[i]);
					}
				}

			}

			detectImages = null;
			OnlineDetectService.startOnlineDetect(mContext);
		}

	}

	/**
	 * @return
	 */
	public static int getWaitingCount() {
		// TODO Auto-generated method stub
		return waitingCount;
	}

}
