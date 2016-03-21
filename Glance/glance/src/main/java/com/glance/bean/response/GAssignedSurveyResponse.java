package com.glance.bean.response;

import java.util.ArrayList;

import com.glance.bean.model.GAssignedSurvey;
import com.glance.bean.model.GlanceBaseBean;
import com.google.gson.annotations.SerializedName;

public class GAssignedSurveyResponse extends GlanceBaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5132204951952573442L;

	@SerializedName("message")
	private String message;

	@SerializedName("status")
	private String status;
	
	@SerializedName("surveys")
	private ArrayList<GAssignedSurvey> assignedSurveys;

	public ArrayList<GAssignedSurvey> getAssignedSurveys() {
		return assignedSurveys;
	}

	public void setAssignedSurveys(ArrayList<GAssignedSurvey> assignedSurveys) {
		this.assignedSurveys = assignedSurveys;
	}

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
