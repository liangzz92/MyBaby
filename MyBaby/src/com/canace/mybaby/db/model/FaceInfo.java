/**
 * FaceInfo.java
 *
 * @author liangzz
 * 2014-12-26
 */
package com.canace.mybaby.db.model;

import com.canace.mybaby.db.utils.Column;
import com.canace.mybaby.db.utils.Model;
import com.canace.mybaby.db.utils.Table;

@Table(name = "FaceInfo")
public class FaceInfo extends Model {
	@Column(name = "faceId", type = Column.DataType.INTEGER, pk = true)
	private Integer faceId;

	@Column(name = "fid", type = Column.DataType.TEXT)
	private String fid;

	@Column(name = "smileScore", type = Column.DataType.DOUBLE)
	private Double smileScore;

	@Column(name = "infos", type = Column.DataType.TEXT)
	private String infos;

	/**
	 * @return the infos
	 */
	public String getInfos() {
		return infos;
	}

	/**
	 * @param infos
	 *            the infos to set
	 */
	public void setInfos(String infos) {
		this.infos = infos;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.canace.mybaby.db.utils.Model#idColumnName()
	 */
	@Override
	public String idColumnName() {
		// TODO Auto-generated method stub
		return "faceId";
	}

	/**
	 * @return the faceId
	 */
	public Integer getFaceId() {
		return faceId;
	}

	/**
	 * @param faceId
	 *            the faceId to set
	 */
	public void setFaceid(Integer faceId) {
		this.faceId = faceId;
	}

	/**
	 * @return the fid
	 */
	public String getFid() {
		return fid;
	}

	/**
	 * @param fid
	 *            the fid to set
	 */
	public void setFid(String fid) {
		this.fid = fid;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.canace.mybaby.db.utils.Model#getId()
	 */
	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return null;
	}
}
