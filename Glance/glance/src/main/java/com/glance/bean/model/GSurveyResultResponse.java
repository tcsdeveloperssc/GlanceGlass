package com.glance.bean.model;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class GSurveyResultResponse extends GlanceBaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -581991470417247377L;

	@SerializedName("message")
	private String message;

	@SerializedName("status")
	private String status;
	
	@SerializedName("createdTime")
	private String createdTime;
	
	@SerializedName("expiryTime")
	private String expiryTime;
	
	@SerializedName("mode")
	private String mode;
	
	@SerializedName("name")
	private String name;
	
	@SerializedName("publishedTime")
	private String publishedTime;
	
	@SerializedName("qnsAndResponses")
	private ArrayList<SurveyResponses> qnsAndResponses;

	public String getMessage() {
		return message;
	}

	public String getStatus() {
		return status;
	}

	public String getCreatedTime() {
		return createdTime;
	}

	public String getExpiryTime() {
		return expiryTime;
	}

	public String getMode() {
		return mode;
	}

	public String getName() {
		return name;
	}

	public String getPublishedTime() {
		return publishedTime;
	}

	public ArrayList<SurveyResponses> getQnsAndResponses() {
		return qnsAndResponses;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setCreatedTime(String createdTime) {
		this.createdTime = createdTime;
	}

	public void setExpiryTime(String expiryTime) {
		this.expiryTime = expiryTime;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPublishedTime(String publishedTime) {
		this.publishedTime = publishedTime;
	}

	public void setQnsAndResponses(ArrayList<SurveyResponses> qnsAndResponses) {
		this.qnsAndResponses = qnsAndResponses;
	}
}
