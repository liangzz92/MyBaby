/**
 * Settings.java
 * 数据库表:用户设置信息
 * @author liangzz
 * 2014-12-2
 */
package com.canace.mybaby.db.model;

import com.canace.mybaby.db.utils.Column;
import com.canace.mybaby.db.utils.Model;
import com.canace.mybaby.db.utils.Table;

@Table(name = "Settings")
public class Settings extends Model {
	@Column(name = "userId", type = Column.DataType.INTEGER, pk = true)
	private Integer userId;

	@Column(name = "userName", type = Column.DataType.TEXT)
	private String userName;

	@Column(name = "password", type = Column.DataType.TEXT)
	private String password;

	@Column(name = "avatarPath", type = Column.DataType.TEXT)
	private String avatarPath;

	@Column(name = "isImageShowed", type = Column.DataType.BOOLEAN)
	private boolean isImageShowed;

	@Column(name = "isPushed", type = Column.DataType.BOOLEAN)
	private boolean isPushed;

	@Column(name = "isQQZoneAuth", type = Column.DataType.BOOLEAN)
	private boolean isQQZoneAuth;

	@Column(name = "isWeiboAuth", type = Column.DataType.BOOLEAN)
	private boolean isWeiboAuth;

	@Column(name = "isWeixinAuth", type = Column.DataType.BOOLEAN)
	private boolean isWeixinAuth;

	@Column(name = "region", type = Column.DataType.TEXT)
	private String region;

	public Settings() {
		userId = -1;
	}

	public Settings(int id) {
		this.userId = id;
	}

	public String getUserName() {
		return userName;
	}

	public int getID() {
		return userId;
	}

	public String getPassword() {
		return password;
	}

	public String getAvatarPath() {
		return avatarPath;
	}

	public boolean getisImageShowed() {
		return isImageShowed;
	}

	public boolean getisPushed() {
		return isPushed;
	}

	public boolean getisQQZoneAuth() {
		return isQQZoneAuth;
	}

	public boolean getisWeiboAuth() {
		return isWeiboAuth;
	}

	public boolean getisWeixinAuth() {
		return isWeixinAuth;
	}

	public String getRegion() {
		return region;
	}

	public void setUserName(String infoString) {
		userName = infoString;
	}

	public void setID(int infoString) {
		userId = infoString;
	}

	public void setPassword(String infoString) {
		password = infoString;
	}

	public void setAvatarPath(String infoString) {
		avatarPath = infoString;
	}

	public void setisImageShowed(boolean is) {
		isImageShowed = is;
	}

	public void setisPushed(boolean is) {
		isPushed = is;
	}

	public void getisQQZoneAuth(boolean is) {
		isQQZoneAuth = is;
	}

	public void getisWeiboAuth(boolean is) {
		isWeiboAuth = is;
	}

	public void setisWeixinAuth(boolean is) {
		isWeixinAuth = is;
	}

	public void setRegion(String infoString) {
		region = infoString;
	}

	@Override
	public String idColumnName() {
		return "userId";
	}

	@Override
	public Integer getId() {
		return userId;
	}
}