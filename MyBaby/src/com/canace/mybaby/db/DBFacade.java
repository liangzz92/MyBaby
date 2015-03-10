/**
 * DBFacade.java
 * 封装数据库插入、查询、更新、删除等操作，使用外观模式
 * @author liangzz
 * 2014-12-2
 */
package com.canace.mybaby.db;

import android.content.Context;

import com.canace.mybaby.db.utils.Model;

public class DBFacade {
	private static final String TAG = "DBFacade";

	/**
	 * initialize, should be invoked in the activity once
	 * 
	 * @param context
	 */
	public static void init(Context context) {
		DBManager.setContext(context);
	}

	/**
	 * Store the entity into the database
	 * 
	 * @param entity
	 * @return
	 */
	public static long save(Model entity) {
		return DBManager.getInstance().open().save(entity);
	}

	/**
	 * Update the entity that already stored in the database
	 * 
	 * @param entity
	 * @return
	 */
	public static void update(Model entity) {
		DBManager.getInstance().open().update(entity);
	}

	/**
	 * Delete the entity in the database
	 * 
	 * @param entity
	 * @return
	 */
	public static void delete(Model entity) {
		DBManager.getInstance().open().delete(entity);
	}

	/**
	 * Search the entity in the database by the major key
	 * 
	 * @return
	 */
	public static Model findById(Class<? extends Model> cls, String fieldName,
			Integer id) {
		return DBManager.getInstance().open().findById(cls, fieldName, id);
	}

	/**
	 * Search the entity in the database by a specific field value the result
	 * may more than one
	 * 
	 * @return
	 */
	public static Model[] findByFieldName(Class<? extends Model> cls,
			String fieldName, Object value) {
		return DBManager.getInstance().open()
				.findByFieldName(cls, fieldName, value);
	}

}
