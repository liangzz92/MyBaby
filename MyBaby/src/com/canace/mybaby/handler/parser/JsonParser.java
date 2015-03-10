/**
 * JsonParser.java
 * Json解析，包括请求完成状态与返回数据两部分
 * @author liangzz
 * 2014-12-2
 */
package com.canace.mybaby.handler.parser;

import java.util.List;

import com.canace.mybaby.db.utils.Model;

public abstract class JsonParser {

	public abstract List<Model> parseModel(String result);

	public abstract boolean parseStatus(String result);

}
