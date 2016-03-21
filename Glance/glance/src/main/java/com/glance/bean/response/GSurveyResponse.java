package com.glance.bean.response;

import java.util.ArrayList;

import com.glance.bean.model.GSurveyAnswer;
import com.glance.bean.model.GlanceBaseBean;
import com.google.gson.annotations.SerializedName;

public class GSurveyResponse extends GlanceBaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8661839388640241327L;

	@SerializedName("message")
	private String message;

	@SerializedName("status")
	private String status;
	
	@SerializedName("message")
	private String surveyId;
	
	private ArrayList<GSurveyAnswer> answers;

	public String getSurveyId() {
		return surveyId;
	}

	public ArrayList<GSurveyAnswer> getAnswers() {
		return answers;
	}

	public void setSurveyId(String surveyId) {
		this.surveyId = surveyId;
	}

	public void setAnswers(ArrayList<GSurveyAnswer> answers) {
		this.answers = answers;
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
