/**
 * GetUserInfoFactory.java
 * 
 * @author liangzz
 * 2014-12-2
 */
package com.canace.mybaby.handler.factory;

import com.canace.mybaby.handler.httphandler.GetUserInfoHttpHandler;
import com.canace.mybaby.handler.httphandler.HttpHandler;
import com.canace.mybaby.handler.parser.JsonParser;
import com.canace.mybaby.handler.parser.UserInfoJsonParser;

public class GetUserInfoFactory extends AbstractFactory {

	@Override
	public HttpHandler createHttpHandler() {
		return new GetUserInfoHttpHandler();
	}

	@Override
	public JsonParser createJsonParser() {
		return new UserInfoJsonParser();
	}
}