package com.glance.bean.model;

import com.google.gson.annotations.SerializedName;

public class GAssignedSurvey extends GlanceBaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4598154149362713070L;

	@SerializedName("createdTime")
	private String createdTime;

	@SerializedName("expiryTime")
	private String expiryTime;

	@SerializedName("publishedSurveyId")
	private String publishedSurveyId;

	@SerializedName("surveyId")
	private String surveyId;

	@SerializedName("name")
	private String name;

	public String getCreatedTime() {
		return createdTime;
	}

	public String getExpiryTime() {
		return expiryTime;
	}

	public String getName() {
		return name;
	}

	public void setCreatedTime(String createdTime) {
		this.createdTime = createdTime;
	}

	public void setExpiryTime(String expiryTime) {
		this.expiryTime = expiryTime;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPublishedSurveyId() {
		return publishedSurveyId;
	}

	public String getSurveyId() {
		return surveyId;
	}

	public void setPublishedSurveyId(String publishedSurveyId) {
		this.publishedSurveyId = publishedSurveyId;
	}

	public void setSurveyId(String surveyId) {
		this.surveyId = surveyId;
	}

}
