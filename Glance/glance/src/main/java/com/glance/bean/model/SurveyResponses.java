package com.glance.bean.model;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class SurveyResponses extends GlanceBaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1251252606673660705L;
	
	@SerializedName("option")
	private ArrayList<GSurveyOption> options;
	
	@SerializedName("optionType")
	private String optionType;

	@SerializedName("optionsSelected")
	private ArrayList<GSurveyOption> optionsSelected;
	
	@SerializedName("qnNumber")
	private String qnNumber;
	
	@SerializedName("qnText")
	private String qnText;

	public ArrayList<GSurveyOption> getOptions() {
		return options;
	}

	public ArrayList<GSurveyOption> getOptionsSelected() {
		return optionsSelected;
	}

	public String getOptionType() {
		return optionType;
	}

	public void setOptionType(String optionType) {
		this.optionType = optionType;
	}
	public String getQnNumber() {
		return qnNumber;
	}

	public String getQnText() {
		return qnText;
	}

	public void setOptions(ArrayList<GSurveyOption> options) {
		this.options = options;
	}

	public void setOptionsSelected(ArrayList<GSurveyOption> optionsSelected) {
		this.optionsSelected = optionsSelected;
	}

	public void setQnNumber(String qnNumber) {
		this.qnNumber = qnNumber;
	}

	public void setQnText(String qnText) {
		this.qnText = qnText;
	}
	

}
