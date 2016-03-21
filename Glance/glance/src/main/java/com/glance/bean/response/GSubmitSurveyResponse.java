package com.glance.bean.response;

import com.glance.bean.model.GlanceBaseBean;
import com.google.gson.annotations.SerializedName;

public class GSubmitSurveyResponse extends GlanceBaseBean {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7962520657382000792L;

	@SerializedName("id")
	private String resultId;
	
	@SerializedName("message")
	private String message;
	
	@SerializedName("status")
	private String status;

	public String getMessage() {
		return message;
	}

	public String getStatus() {
		return status;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	

}
