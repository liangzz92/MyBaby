/**
 * User.java
 * 数据库表:用户信息
 * @author liangzz
 * 2014-12-2
 */
package com.canace.mybaby.db.model;

import com.canace.mybaby.db.utils.Column;
import com.canace.mybaby.db.utils.Model;
import com.canace.mybaby.db.utils.Table;

@Table(name = "User")
public class User extends Model {

	@Column(name = "userId", type = Column.DataType.INTEGER, pk = true)
	private Integer userId;

	@Column(name = "userName", type = Column.DataType.TEXT)
	private String userName;

	@Column(name = "password", type = Column.DataType.TEXT)
	private String password;

	public User() {
		userId = -1;
	}

	public User(int id) {
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

	public void setPassword(String infoString) {
		password = infoString;
	}

	public void setUserName(String name) {
		userName = name;
	}

	public void setId(int id) {
		userId = id;
	}

	@Override
	public String idColumnName() {
		return columnName("userId");
	}

	@Override
	public Integer getId() {
		return userId;
	}

}