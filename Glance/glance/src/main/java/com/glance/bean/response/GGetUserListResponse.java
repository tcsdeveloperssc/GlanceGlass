package com.glance.bean.response;

import java.util.ArrayList;

import com.glance.bean.model.GUserBean;
import com.glance.bean.model.GlanceBaseBean;
import com.google.gson.annotations.SerializedName;

public class GGetUserListResponse extends GlanceBaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 462211450157933009L;
	
	@SerializedName("message")
	private String message;
	
	@SerializedName("status")
	private String status;
	
	@SerializedName("userList")
	private ArrayList<GUserBean> userList;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public ArrayList<GUserBean> getUserList() {
		return userList;
	}

	public void setUserList(ArrayList<GUserBean> userList) {
		this.userList = userList;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
	

}
