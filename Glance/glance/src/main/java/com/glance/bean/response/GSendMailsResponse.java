package com.glance.bean.response;

import com.glance.bean.model.GlanceBaseBean;
import com.google.gson.annotations.SerializedName;

public class GSendMailsResponse extends GlanceBaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 462211450157933009L;
	
	
	@SerializedName("status")
	private String status;
	
	@SerializedName("message")
	private String message;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	



}
