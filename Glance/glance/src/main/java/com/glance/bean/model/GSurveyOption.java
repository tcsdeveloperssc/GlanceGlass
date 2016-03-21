package com.glance.bean.model;

import com.google.gson.annotations.SerializedName;

public class GSurveyOption extends GlanceBaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4724525238557194764L;

	@SerializedName("optionNo")
	private String optionNo;
	
	@SerializedName("optionText")
	private String optionText;

	public String getOptionNo() {
		return optionNo;
	}

	public String getOptionText() {
		return optionText;
	}

	public void setOptionNo(String optionNo) {
		this.optionNo = optionNo;
	}

	public void setOptionText(String optionText) {
		this.optionText = optionText;
	}

}
