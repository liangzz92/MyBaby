/**
 * Table.java
 * 利用java反射机制，生成SQLite的表
 * @author liangzz
 * 2014-12-2
 */
package com.canace.mybaby.db.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value = RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Table {
	String name();
}
