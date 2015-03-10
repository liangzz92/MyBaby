/**
 * Directory.java
 * 数据库表:检测路径信息
 * @author liangzz
 * 2014-12-2
 */
package com.canace.mybaby.db.model;

import com.canace.mybaby.db.utils.Column;
import com.canace.mybaby.db.utils.Model;
import com.canace.mybaby.db.utils.Table;

@Table(name = "Directory")
public class Directory extends Model {

	@Column(name = "directoryId", type = Column.DataType.INTEGER, pk = true)
	private Integer directoryId;

	@Column(name = "path", type = Column.DataType.TEXT)
	private String path;

	@Column(name = "isScannedOver", type = Column.DataType.BOOLEAN)
	private Integer isScannedOver;

	@Column(name = "hasImage", type = Column.DataType.BOOLEAN)
	private Integer hasImage;

	/**
	 * @return the hasImage
	 */
	public Integer getHasImage() {
		return hasImage;
	}

	/**
	 * @param hasImage
	 *            the hasImage to set
	 */
	public void setHasImage(Integer hasImage) {
		this.hasImage = hasImage;
	}

	/**
	 * @return the directoryId
	 */
	public Integer getDirectoryId() {
		return directoryId;
	}

	/**
	 * @param directoryId
	 *            the directoryId to set
	 */
	public void setDirectoryId(Integer directoryId) {
		this.directoryId = directoryId;
	}

	/**
	 * @return the isScannedOver
	 */
	public Integer getIsScannedOver() {
		return isScannedOver;
	}

	/**
	 * @param isScannedOver
	 *            the isScannedOver to set
	 */
	public void setIsScannedOver(Integer isScannedOver) {
		this.isScannedOver = isScannedOver;
	}

	public Directory() {
		directoryId = -1;
	}

	public Directory(int id) {
		this.directoryId = id;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getID() {
		return directoryId;
	}

	public void setId(int id) {
		directoryId = id;
	}

	@Override
	public String idColumnName() {
		return columnName("directoryId");
	}

	@Override
	public Integer getId() {
		return directoryId;
	}

}