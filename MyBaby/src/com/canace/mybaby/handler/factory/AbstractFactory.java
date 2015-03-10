/**
 * AbstractFactory.java
 * 创建网络请求handler和与其对应的jsonParser
 * @author liangzz
 * 2014-12-2
 */
package com.canace.mybaby.handler.factory;

import com.canace.mybaby.handler.httphandler.HttpHandler;
import com.canace.mybaby.handler.parser.JsonParser;

//抽象工厂类
public abstract class AbstractFactory {
	public abstract HttpHandler createHttpHandler();

	public abstract JsonParser createJsonParser();
}