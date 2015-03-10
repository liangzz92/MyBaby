/**
 * ModelUtils.java
 * 数据库工具包
 * @author liangzz
 * 2014-12-2
 */
package com.canace.mybaby.db.utils;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.database.Cursor;

import com.canace.mybaby.db.utils.Column.DataType;

public class ModelUtils {

	/**
	 * 利用java反射机制生成创建表的SQL语句
	 * 
	 * @param class1
	 * @return
	 */
	public static String getCreateTableSQL(Class<? extends Model> class1) {
		Model m = null;
		try {
			m = class1.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		StringBuilder sb = new StringBuilder(100);
		sb.append("CREATE TABLE IF NOT EXISTS ");
		sb.append(m.tableName()).append("(").append(m.idColumnName())
				.append(" INTEGER PRIMARY KEY AUTOINCREMENT");
		for (Field f : m.getClass().getDeclaredFields()) {
			if (f.isAnnotationPresent(Column.class)) {
				Column c = f.getAnnotation(Column.class);
				if (!c.pk()) {
					sb.append(",").append(c.name()).append(" ")
							.append(c.type());
				}
			}
		}
		sb.append(");");
		return sb.toString();
	}

	/**
	 * 利用java反射机制生成删除表的SQL语句
	 * 
	 * @param modelClass
	 * @return
	 */
	public static String getDropTableSQL(Class<? extends Model> modelClass) {
		Model m = null;
		try {
			m = modelClass.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		StringBuilder sb = new StringBuilder(35);
		sb.append("DROP TABLE IF EXISTS ").append(m.tableName()).append(";");
		return sb.toString();
	}

	/**
	 * datetime转换为String
	 * 
	 * @param datetime
	 * @return
	 */
	public static String datetimeToString(Date datetime) {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
				.format(datetime);
	}

	/**
	 * datetimeStr转换为Date对象
	 * 
	 * @param datetimeStr
	 * @return
	 */
	public static Date stringToDatetime(String datetimeStr) {
		try {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
					.parse(datetimeStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * dateStr转换为Date对象
	 * 
	 * @param dateStr
	 * @return
	 */
	public static Date stringToDate(String dateStr) {
		try {
			return new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
					.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 利用java反射机制，将数据库返回数据还原为Model数组
	 * 
	 * @param c
	 * @param cls
	 * @return
	 */
	public static Model[] cursorToArray(Cursor c, Class<? extends Model> cls) {
		if (c == null) {
			return null;
		}
		Model[] results = (Model[]) Array.newInstance(cls, c.getCount());
		if (c.moveToFirst()) {
			do {
				int pos = c.getPosition();
				try {
					results[pos] = cls.newInstance();
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (results[pos] != null) {
					results[pos].setFieldsByCursor(c);
				}
			} while (c.moveToNext());
		}
		return results;
	}

	/**
	 * 
	 * @param entity
	 * @param cursor
	 */
	public static void fillEntityWithCursor(Model entity, Cursor cursor) {
		if (entity == null || cursor == null || cursor.isBeforeFirst()
				|| cursor.isAfterLast()) {
			return;
		}
		// entity.setId(cursor.getInt(cursor.getColumnIndexOrThrow(entity.idColumnName())));
		for (Field f : entity.getClass().getDeclaredFields()) {
			if (f.isAnnotationPresent(Column.class)) {
				Column c = f.getAnnotation(Column.class);
				int index = cursor.getColumnIndex(c.name());
				DataType type = c.type();
				f.setAccessible(true);
				try {
					switch (type) {
					case INTEGER:
					case BOOLEAN:
						f.set(entity, cursor.getInt(index));
						break;
					case BIGINT:
						f.set(entity, cursor.getLong(index));
						break;
					case REAL:
					case DOUBLE:
						f.set(entity, cursor.getDouble(index));
						break;
					case FLOAT:
						f.set(entity, cursor.getFloat(index));
						break;
					case TEXT:
						f.set(entity, cursor.getString(index));
						break;
					case DATE:
						f.set(entity, stringToDate(cursor.getString(index)));
						break;
					case DATETIME:
						f.set(entity, stringToDatetime(cursor.getString(index)));
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

}
