/**
 * SetUserInfoFactory.java
 * 
 * @author liangzz
 * 2014-12-2
 */
package com.canace.mybaby.handler.factory;

import com.canace.mybaby.handler.httphandler.HttpHandler;
import com.canace.mybaby.handler.httphandler.SetUserInfoHttpHandler;
import com.canace.mybaby.handler.parser.CommonStatusJsonParser;
import com.canace.mybaby.handler.parser.JsonParser;

public class SetUserInfoFactory extends AbstractFactory {

	@Override
	public HttpHandler createHttpHandler() {
		return new SetUserInfoHttpHandler();
	}

	@Override
	public JsonParser createJsonParser() {
		return new CommonStatusJsonParser();
	}
}