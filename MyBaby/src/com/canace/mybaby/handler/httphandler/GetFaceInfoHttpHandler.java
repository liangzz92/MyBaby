/**
 * GetFaceInfoHttpHandler.java
 * 调用face++开源代码中的HttpRequests进行网络请求，将返回结果传给FaceInfoJsonParser解析
 * @author liangzz
 * 2014-12-26
 */
package com.canace.mybaby.handler.httphandler;

import java.util.List;

import com.canace.mybaby.db.utils.Model;
import com.canace.mybaby.handler.utils.HttpRequests;
import com.canace.mybaby.utils.CommonsUtil;
import com.canace.mybaby.utils.MyBabyConstants;

public class GetFaceInfoHttpHandler extends HttpHandler {

	private final HttpRequests httpRequests = new HttpRequests(
			MyBabyConstants.FACEPP_API_KEY, MyBabyConstants.FACEPP_API_SECRET,
			true, true);

	public GetFaceInfoHttpHandler() {

	}

	@Override
	public void execute() {
		new Thread(new Runnable() {

			@Override
			public void run() {

				try {

					String result = httpRequests
							.detectionDetect(postParameters).toString();

					List<Model> data = mJsonParser.parseModel(result);
					boolean isSuccess = mJsonParser.parseStatus(result);
					mOnDataCollectFinishedListener.onDataCollect(data,
							isSuccess);

				} catch (Exception e) {
					if (CommonsUtil.DEBUG) {
						e.printStackTrace();
					}
					mOnDataCollectFinishedListener.onDataCollect(null, false);
				}
			}
		}).start();

	}
}
