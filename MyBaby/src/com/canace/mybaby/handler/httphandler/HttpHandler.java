/**
 * HttpHandler.java
 * 用于网络POST请求，用法：
 * 在调用execute()方法前先按需调用setHttpEntity(), setOnDataCollectFinishedListener(), setJsonParser()
 * @author liangzz
 * 2014-12-8
 */
package com.canace.mybaby.handler.httphandler;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;

import com.canace.mybaby.db.utils.Model;
import com.canace.mybaby.handler.parser.JsonParser;
import com.canace.mybaby.handler.utils.PostParameters;

public abstract class HttpHandler {

	private HttpClient mDefaultHttpClient;
	private HttpPost mHttpPost;
	protected String mURLString;
	private HttpEntity mHttpEntity;
	protected PostParameters postParameters;
	protected OnDataCollectFinishedListener mOnDataCollectFinishedListener;
	protected JsonParser mJsonParser;

	static final private int TIMEOUT = 30000;
	static final private int SYNC_TIMEOUT = 60000;

	public interface OnDataCollectFinishedListener {
		public void onDataCollect(List<Model> models, boolean isSuccess);
	}

	public void execute() {

		new Thread(new Runnable() {

			@Override
			public void run() {

				mDefaultHttpClient = initHttp();
				mHttpPost = initPost();
				try {
					HttpResponse httpResponse = mDefaultHttpClient
							.execute(mHttpPost);
					if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

						String result = EntityUtils.toString(
								httpResponse.getEntity(), "UTF-8");

						List<Model> data = mJsonParser.parseModel(result);
						boolean isSuccess = mJsonParser.parseStatus(result);
						mOnDataCollectFinishedListener.onDataCollect(data,
								isSuccess);
					} else {
						mOnDataCollectFinishedListener.onDataCollect(null,
								false);
					}
				} catch (ClientProtocolException e) {

					e.printStackTrace();
					mOnDataCollectFinishedListener.onDataCollect(null, false);
				} catch (IOException e) {
					e.printStackTrace();
					mOnDataCollectFinishedListener.onDataCollect(null, false);
				}
			}
		}).start();
	}

	public void setHttpEntity(HttpEntity httpEntity) {
		mHttpEntity = httpEntity;
	}

	public void setMultiEntity(PostParameters postParameters) {
		this.postParameters = postParameters;
	}

	protected HttpClient initHttp() {
		HttpClient client = new DefaultHttpClient();
		client.getParams().setIntParameter(HttpConnectionParams.SO_TIMEOUT,
				SYNC_TIMEOUT);
		client.getParams().setIntParameter(
				HttpConnectionParams.CONNECTION_TIMEOUT, TIMEOUT);
		return client;
	}

	protected HttpPost initPost() {
		HttpPost httpPost = new HttpPost(mURLString);
		httpPost.setEntity(mHttpEntity);
		return httpPost;
	}

	/**
	 * 
	 * @param onDataCollectFinishedListener
	 */
	public void setOnDataCollectFinishedListener(
			OnDataCollectFinishedListener onDataCollectFinishedListener) {
		mOnDataCollectFinishedListener = onDataCollectFinishedListener;

	}

	/**
	 * 
	 * @param jsonParser
	 */
	public void setJsonParser(JsonParser jsonParser) {
		mJsonParser = jsonParser;

	}
}