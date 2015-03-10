/**
 * CommonStatusJsonParser.java
 * 只解析返回状态
 * @author liangzz
 * 2014-12-2
 */
package com.canace.mybaby.handler.parser;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.canace.mybaby.db.utils.Model;

public class CommonStatusJsonParser extends JsonParser {

	@Override
	public List<Model> parseModel(String result) {
		return null;
	}

	@Override
	public boolean parseStatus(String result) {
		JSONObject dataJson;
		try {
			dataJson = new JSONObject(result);
			if (dataJson.getString("status").equals("ok")) {
				return true;
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}

}
