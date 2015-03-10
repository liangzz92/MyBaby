/**
 * DetectService.java
 *
 * @author liangzz
 * 2014-12-30
 */
package com.canace.mybaby.cache.scanner;

import java.util.HashMap;
import java.util.Map;

import android.app.IntentService;
import android.content.Intent;

import com.canace.mybaby.db.model.DetectImage;

public class DetectService extends IntentService {

	/**
	 * @param name
	 */
	public DetectService(String name) {
		// TODO Auto-generated constructor stub
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub

	}

	protected static DetectImage[] detectImages = null;

	protected static int waitingCount = 0;
	protected static Map<String, Boolean> waitingQueueMap = new HashMap<String, Boolean>();
	protected static Integer DETECT_WAITING = 0;
	protected static Integer DETECT_SUCCEEDED = 1;
	protected static Integer DETECT_FAILED = 2;

	/**
	 * 判断在线检测服务是否在运行中
	 * 
	 * @return
	 */
	public static boolean isDetecting() {
		return (detectImages != null && detectImages.length > 0)
				|| waitingCount > 0;
	}

	/**
	 * @param detectImage
	 * @return
	 */
	protected static boolean insertWaitingQueueMap(DetectImage detectImage) {
		if (waitingQueueMap.containsKey(detectImage.getIdHashcode())) {
			return false;
		} else {
			waitingQueueMap.put(detectImage.getIdHashcode(), true);
			return true;
		}

	}

	/**
	 * 返回正在等待在线检测的图片数量，可作为缓存服务是否终止的判断条件之一
	 * 
	 * @return
	 */
	public static int getWaitingCount() {
		return waitingCount;
	}
}
