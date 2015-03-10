/**
 * ImageInfo.java
 * 2014-12-5 下午9:56:42
 * 
 * @author 悦
 */
package com.canace.mybaby.db.model;

import com.canace.mybaby.db.utils.Column;
import com.canace.mybaby.db.utils.Model;
import com.canace.mybaby.db.utils.Table;

@Table(name = "ImageItem")
public class ImageItem extends Model {

	@Column(name = "itemId", type = Column.DataType.INTEGER, pk = true)
	private Integer itemId;

	@Column(name = "imagePath", type = Column.DataType.TEXT)
	private String imagePath;

	@Column(name = "faceInfos", type = Column.DataType.TEXT)
	private String faceInfos;

	@Column(name = "timeScore", type = Column.DataType.BIGINT)
	private Long timeScore;

	@Column(name = "qualityScore", type = Column.DataType.DOUBLE)
	private Double qualityScore;

	@Column(name = "smileScore", type = Column.DataType.DOUBLE)
	private Double smileScore;

	@Column(name = "idHashcode", type = Column.DataType.TEXT)
	private String idHashcode;

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

	/**
	 * @return the timeScore
	 */
	public Long getTimeScore() {
		return timeScore;
	}

	/**
	 * @param timeScore
	 *            the timeScore to set
	 */
	public void setTimeScore(Long timeScore) {
		this.timeScore = timeScore;
	}

	@Column(name = "personId", type = Column.DataType.INTEGER)
	private Integer personId;

	@Override
	public String idColumnName() {
		// TODO Auto-generated method stub
		return "itemId";
	}

	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return itemId;
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

	/**
	 * @return the faceInfos
	 */
	public String getFaceInfos() {
		return faceInfos;
	}

	/**
	 * @param faceInfos
	 *            the faceInfos to set
	 */
	public void setFaceInfos(String faceInfos) {
		this.faceInfos = faceInfos;
	}

	/**
	 * @return the qualityScore
	 */
	public Double getQualityScore() {
		return qualityScore;
	}

	/**
	 * @param qualityScore
	 *            the qualityScore to set
	 */
	public void setQualityScore(Double qualityScore) {
		this.qualityScore = qualityScore;
	}

	/**
	 * @return the smileScore
	 */
	public Double getSmileScore() {
		return smileScore;
	}

	/**
	 * @param smileScore
	 *            the smileScore to set
	 */
	public void setSmileScore(Double smileScore) {
		this.smileScore = smileScore;
	}

	/**
	 * @return the personId
	 */
	public Integer getPersonId() {
		return personId;
	}

	/**
	 * @param personId
	 *            the personId to set
	 */
	public void setPersonId(Integer personId) {
		this.personId = personId;
	}

}
