/**
 * ImageCache.java
 * 数据库表:缓存图片信息
 * @author liangzz
 * 2014-12-2
 */
package com.canace.mybaby.db.model;

import com.canace.mybaby.db.utils.Column;
import com.canace.mybaby.db.utils.Model;
import com.canace.mybaby.db.utils.Table;

@Table(name = "ImageCache")
public class ImageCache extends Model {

	@Column(name = "cacheId", type = Column.DataType.INTEGER, pk = true)
	private Integer cacheId;

	@Column(name = "imagePath", type = Column.DataType.TEXT)
	private String imagePath;

	public ImageCache() {
		cacheId = -1;
	}

	public ImageCache(int id) {
		this.cacheId = id;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public int getID() {
		return cacheId;
	}

	public void setId(int id) {
		cacheId = id;
	}

	@Override
	public String idColumnName() {
		return columnName("cacheId");
	}

	@Override
	public Integer getId() {
		return cacheId;
	}

}