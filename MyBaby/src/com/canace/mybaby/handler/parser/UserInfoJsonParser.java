/**
 * UserInfoJsonParser.java
 * 解析返回用户信息，当请求状态为成功时保存用户设置
 * @author liangzz
 * 2014-12-2
 */
package com.canace.mybaby.handler.parser;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.canace.mybaby.db.utils.Model;
import com.canace.mybaby.utils.PreferenceUtils;

public class UserInfoJsonParser extends JsonParser {

	@Override
	public List<Model> parseModel(String result) {
		JSONObject dataJson;

		try {
			dataJson = new JSONObject(result);
			JSONObject response = dataJson.getJSONObject("result");
			if (!response.toString().equals("null")) {
				String uid = response.getString("userId");

				PreferenceUtils.saveStringValue(PreferenceUtils.USER_ID, uid);
				String isSmartModeString = response.getString("isSmartMode");
				boolean isSmartMode = true;
				if (!isSmartModeString.equals("1")) {
					isSmartMode = false;
				}
				PreferenceUtils.saveBooleanValue(PreferenceUtils.ISSMARTMODE,
						isSmartMode);

				System.out.println(uid + isSmartMode);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

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
