/**
 * Model.java
 * 数据库元模型:行
 * @author liangzz
 * 2014-12-2
 */
package com.canace.mybaby.db.utils;

import java.lang.reflect.Field;

import android.content.ContentValues;
import android.database.Cursor;

import com.canace.mybaby.db.utils.Column.DataType;

abstract public class Model {

	public Model() {
	}

	/**
	 * 返回当前持久化对象所对应的数据库表名
	 * 
	 * @return
	 */
	public String tableName() {
		return getClass().getAnnotation(Table.class).name();
	}

	/**
	 * 返回当前持久化对象指定属性所对应数据库表的列名
	 * 
	 * @param fieldName
	 *            属性名
	 * @return
	 */
	public String columnName(String fieldName) {
		Class<?> cls = getClass();
		Field f = null;
		while (f == null && cls != null) {
			try {
				f = cls.getDeclaredField(fieldName);
				if (f.isAnnotationPresent(Column.class)) {
					return f.getAnnotation(Column.class).name();
				}
			} catch (NoSuchFieldException e) {
			}
			if (cls.getSimpleName().equals("Model")) {
				break;
			}
			cls = cls.getSuperclass();
		}
		return null;
	}

	/**
	 * 返回当前持久化对象id变量对应数据库表的列名
	 * 
	 * @return
	 */
	public abstract String idColumnName();

	/**
	 * 返回当前表的Id
	 * 
	 * @return id
	 */
	public abstract Integer getId();

	/**
	 * 返回ContentValues，它包含当前持久化对象中持久化的属性列名-列值对（不包含主键列）
	 * 
	 * @return
	 */
	public ContentValues toContentValues() {
		ContentValues values = new ContentValues();
		for (Field f : getClass().getDeclaredFields()) {
			Column c = f.getAnnotation(Column.class);
			if (c != null && !c.pk()) {
				Object value = null;
				try {
					f.setAccessible(true);
					value = f.get(this);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (value != null) {
					values.put(c.name(), value.toString());
				}
			}
		}
		return values;
	}

	/**
	 * 根据游标指示的当前行数据来设置当前对象各个属性的值
	 * 
	 * @param cursor
	 */
	public void setFieldsByCursor(Cursor cursor) {
		for (Field f : getClass().getDeclaredFields()) {
			if (f.isAnnotationPresent(Column.class)) {
				Column c = f.getAnnotation(Column.class);
				int index = cursor.getColumnIndex(c.name());
				DataType type = c.type();
				f.setAccessible(true); // 临时转为public
				// 根据成员变量的类型，将从表中读取的数据转为public
				try {
					switch (type) {
					case INTEGER:
					case BOOLEAN:
						f.set(this, cursor.getInt(index));
						break;
					case BIGINT:
						f.set(this, cursor.getLong(index));
						break;
					case REAL:
					case DOUBLE:
						f.set(this, cursor.getDouble(index));
						break;
					case FLOAT:
						f.set(this, cursor.getFloat(index));
						break;
					case TEXT:
						f.set(this, cursor.getString(index));
						break;
					case DATE:
						f.set(this, ModelUtils.stringToDate(cursor
								.getString(index)));
						break;
					case DATETIME:
						f.set(this, ModelUtils.stringToDatetime(cursor
								.getString(index)));
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

}
