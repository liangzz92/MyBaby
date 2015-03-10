/**
 * FaceInfoJsonParser.java
 * 返回检测结果face数量大于1，表示检测成功
 * 只返回年龄<7的faceInfo列表
 * @author liangzz
 * 2014-12-26
 */
package com.canace.mybaby.handler.parser;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.canace.mybaby.db.model.FaceInfo;
import com.canace.mybaby.db.utils.Model;

public class FaceInfoJsonParser extends JsonParser {

	@Override
	public List<Model> parseModel(String result) {
		JSONObject dataJson;
		List<Model> faceInfos = new ArrayList<Model>();
		try {
			dataJson = new JSONObject(result);
			JSONArray faces = dataJson.getJSONArray("face");
			if (faces != null) {
				for (int i = 0; i < faces.length(); i++) {
					JSONObject attribute = faces.getJSONObject(i)
							.getJSONObject("attribute");
					JSONObject age = attribute.getJSONObject("age");
					int ageValue = age.getInt("value");
					if (true || ageValue <= 7) {
						String fid = faces.getJSONObject(i)
								.getString("face_id");
						JSONObject smile = attribute.getJSONObject("smiling");
						double smilingValue = smile.getDouble("value");
						FaceInfo faceInfo = new FaceInfo();
						faceInfo.setFid(fid);
						faceInfo.setSmileScore(smilingValue);
						faceInfo.setInfos(result);
						faceInfos.add(faceInfo);
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
			faceInfos = null;
		}

		return faceInfos;
	}

	@Override
	public boolean parseStatus(String result) {
		JSONObject dataJson;
		try {
			dataJson = new JSONObject(result);
			if (dataJson.getJSONArray("face").length() > 0) {
				return true;
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}
}
