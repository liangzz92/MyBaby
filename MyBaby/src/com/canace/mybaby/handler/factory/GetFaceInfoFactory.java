/**
 * GetFaceInfoFactory.java
 *
 * @author liangzz
 * 2014-12-26
 */
package com.canace.mybaby.handler.factory;

import com.canace.mybaby.handler.httphandler.GetFaceInfoHttpHandler;
import com.canace.mybaby.handler.httphandler.HttpHandler;
import com.canace.mybaby.handler.parser.FaceInfoJsonParser;
import com.canace.mybaby.handler.parser.JsonParser;

public class GetFaceInfoFactory extends AbstractFactory {

	@Override
	public HttpHandler createHttpHandler() {
		return new GetFaceInfoHttpHandler();
	}

	@Override
	public JsonParser createJsonParser() {
		return new FaceInfoJsonParser();
	}
}
