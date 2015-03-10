/**
 * DBManager.java
 * 封装数据库插入、查询、更新、删除等操作，使用单例模式
 * @author liangzz
 * 2014-12-2
 */
package com.canace.mybaby.db;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import com.canace.mybaby.db.model.DetectImage;
import com.canace.mybaby.db.model.Directory;
import com.canace.mybaby.db.model.FaceInfo;
import com.canace.mybaby.db.model.ImageCache;
import com.canace.mybaby.db.model.ImageItem;
import com.canace.mybaby.db.model.Settings;
import com.canace.mybaby.db.model.User;
import com.canace.mybaby.db.utils.Model;
import com.canace.mybaby.db.utils.ModelUtils;
import com.canace.mybaby.utils.MyBabyConstants;

public class DBManager extends SQLiteOpenHelper {

	private static DBManager instance;

	private static Context context;

	private SQLiteDatabase db;

	private static Class<?>[] _models = { Directory.class, ImageItem.class,
			FaceInfo.class, ImageCache.class, Settings.class, User.class,
			DetectImage.class };

	private DBManager(Context context) {
		super(context, MyBabyConstants.DB_NAME, null,
				MyBabyConstants.DB_VERSION);
	}

	public static DBManager getInstance() {
		if (instance == null) {
			if (context == null)
				throw new NullPointerException(
						"context is null, please use DBManager.setContext() first.");
			instance = new DBManager(context);
		}
		return instance;
	}

	public static void setContext(Context c) {
		context = c;
	}

	public static Context getContext() {
		return context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		for (Class<?> m : _models) {
			db.execSQL(ModelUtils.getCreateTableSQL(m.asSubclass(Model.class)));
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i("DB", "db upgrade");
		if (oldVersion != newVersion) {
			for (Class<?> m : _models) {
				db.execSQL(ModelUtils.getDropTableSQL(m.asSubclass(Model.class)));
			}
		}
		onCreate(db);
	}

	public DBManager open() {
		if (db == null) {
			db = getWritableDatabase();
		}
		return this;
	}

	@Override
	public void close() {
		if (db != null) {
			db.close();
			db = null;
		}
	}

	public long save(Model entity) {
		ContentValues values = entity.toContentValues();
		return db.insert(entity.tableName(), null, values);
	}

	public void update(Model entity) {
		ContentValues values = entity.toContentValues();
		String where = entity.idColumnName() + " = ?";
		String[] whereArgs = new String[] { entity.getId() + "" };
		db.update(entity.tableName(), values, where, whereArgs);
	}

	public void delete(Model entity) {
		String where = entity.idColumnName() + " = ?";
		String[] whereArgs = new String[] { entity.getId() + "" };
		db.delete(entity.tableName(), where, whereArgs);
	}

	public Model findById(Class<? extends Model> cls, String fieldName,
			Integer id) {
		Model[] searchResult = findByFieldName(cls, fieldName, id);
		if (searchResult != null && searchResult.length > 0) {
			return searchResult[0];
		} else {
			return null;
		}
	}

	public Model[] findByFieldName(Class<? extends Model> cls,
			String fieldName, Object value) {
		// List<Model> result = new ArrayList<Model>();
		Model entity = null;
		try {
			entity = cls.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		String where = null;
		String[] whereArgs = null;
		if (fieldName != null) {
			where = entity.columnName(fieldName) + " = ?";
			whereArgs = new String[] { value.toString() };
		}

		Cursor c = db.query(entity.tableName(), null, where, whereArgs, null,
				null, null);

		Model[] results = ModelUtils.cursorToArray(c, cls);

		c.close();
		return results;
	}

	public Set<Model> findByFieldName(Object value, Class<? extends Model> cls,
			String fieldName) {
		Set<Model> resultSet = new HashSet<Model>();
		Model entity = null;
		try {
			entity = cls.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		String where = entity.columnName(fieldName) + " = ?";
		String[] whereArgs = new String[] { value.toString() };
		Cursor c = db.query(entity.tableName(), null, where, whereArgs, null,
				null, null);

		if (c.moveToFirst()) {
			while (!c.isAfterLast()) {

				try {
					entity = cls.newInstance();
				} catch (Exception e) {
					e.printStackTrace();
					entity = null;
				}

				if (entity != null) {
					entity.setFieldsByCursor(c);
					resultSet.add(entity);
				}
				c.moveToNext();
			}
		}
		c.close();
		return resultSet;
	}

	public Cursor query(SQLiteQueryBuilder sqb, String where, String[] whereArgs) {
		return query(sqb, null, where, whereArgs);
	}

	public Cursor query(SQLiteQueryBuilder sqb,
			Map<String, String> projectionMap, String where, String[] whereArgs) {
		String[] projectionIn = null;
		if (projectionMap != null && projectionMap.size() > 0) {
			Set<String> keys = projectionMap.keySet();
			projectionIn = keys.toArray(new String[keys.size()]);
			sqb.setProjectionMap(projectionMap);
		}
		return sqb.query(db, projectionIn, where, whereArgs, null, null, null);
	}

}
