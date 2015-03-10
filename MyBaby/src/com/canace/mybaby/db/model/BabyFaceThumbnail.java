/**
 * BabyFaceThumbnail.java
 * 2015-2-6 下午9:56:42
 * 
 * @author 悦
 */
package com.canace.mybaby.db.model;

import java.io.Serializable;

import android.graphics.Bitmap;

import com.canace.mybaby.db.utils.Column;
import com.canace.mybaby.db.utils.Model;
import com.canace.mybaby.db.utils.Table;

@Table(name = "BabyFaceThumbnail")
public class BabyFaceThumbnail extends Model implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5414626636603288203L;

	@Column(name = "itemId", type = Column.DataType.INTEGER, pk = true)
	private Integer itemId;

	@Column(name = "imagePath", type = Column.DataType.TEXT)
	private String imagePath;

	@Column(name = "faceInfos", type = Column.DataType.TEXT)
	private String faceInfos;
	
	private Bitmap thumbnailBitmap;

	/**
	 * @return the thumbnailBitmap
	 */
	public Bitmap getThumbnailBitmap() {
		return thumbnailBitmap;
	}

	/**
	 * @param thumbnailBitmap the thumbnailBitmap to set
	 */
	public void setThumbnailBitmap(Bitmap thumbnailBitmap) {
		this.thumbnailBitmap = thumbnailBitmap;
	}

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

}
