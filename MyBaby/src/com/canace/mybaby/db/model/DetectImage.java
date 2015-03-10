/**
 * DetectImage.java
 *
 * @author liangzz
 * 2014-12-15
 */
package com.canace.mybaby.db.model;

import com.canace.mybaby.db.utils.Column;
import com.canace.mybaby.db.utils.Model;
import com.canace.mybaby.db.utils.Table;

@Table(name = "DetectImage")
public class DetectImage extends Model {

	@Column(name = "itemId", type = Column.DataType.INTEGER, pk = true)
	private Integer itemId;

	@Column(name = "imagePath", type = Column.DataType.TEXT)
	private String imagePath;

	@Column(name = "idHashcode", type = Column.DataType.TEXT)
	private String idHashcode;

	@Column(name = "hasLocalDetect", type = Column.DataType.BOOLEAN)
	private Integer hasLocalDetect = 0;

	@Column(name = "needOnlineDetect", type = Column.DataType.BOOLEAN)
	private Integer needOnlineDetect = 0;

	@Column(name = "itemTimeScore", type = Column.DataType.BIGINT)
	private Long itemTimeScore;

	/**
	 * @return the itemTimeScore
	 */
	public Long getItemTimeScore() {
		return itemTimeScore;
	}

	/**
	 * @param itemTimeScore
	 *            the itemTimeScore to set
	 */
	public void setItemTimeScore(Long itemTimeScore) {
		this.itemTimeScore = itemTimeScore;
	}

	/**
	 * @return the hasLocalDetect
	 */
	public Integer getHasLocalDetect() {
		return hasLocalDetect;
	}

	/**
	 * @param hasLocalDetect
	 *            the hasLocalDetect to set
	 */
	public void setHasLocalDetect(Integer hasLocalDetect) {
		this.hasLocalDetect = hasLocalDetect;
	}

	/**
	 * @return the hasOnlineDetect
	 */
	public Integer getNeedOnlineDetect() {
		return needOnlineDetect;
	}

	/**
	 * @param hasOnlineDetect
	 *            the hasOnlineDetect to set
	 */
	public void setNeedOnlineDetect(Integer needOnlineDetect) {
		this.needOnlineDetect = needOnlineDetect;
	}

	/**
	 * @return the idHashcode
	 */
	public String getIdHashcode() {
		return idHashcode;
	}

	/**
	 * @param idHashcode
	 *            the idHashcode to set
	 */
	public void setIdHashcode(String idHashcode) {
		this.idHashcode = idHashcode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.canace.mybaby.db.utils.Model#idColumnName()
	 */
	@Override
	public String idColumnName() {
		// TODO Auto-generated method stub
		return "itemId";
	}

	/**
	 * @return the itemId
	 */
	public Integer getItemId() {
		return itemId;
	}

	/**
	 * @param itemId
	 *            the itemId to set
	 */
	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}

	/**
	 * @return the imagePath
	 */
	public String getImagePath() {
		return imagePath;
	}

	/**
	 * @param imagePath
	 *            the imagePath to set
	 */
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.canace.mybaby.db.utils.Model#getId()
	 */
	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return itemId;
	}

}
