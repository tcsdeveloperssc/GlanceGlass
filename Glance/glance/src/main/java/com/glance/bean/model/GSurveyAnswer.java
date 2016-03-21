package com.glance.bean.model;

import java.util.ArrayList;

public class GSurveyAnswer extends GlanceBaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1254800352139373600L;
	
	private String questionNumber;
	private String optionType;
	private ArrayList<GSurveyOption> optionList;

	public String getQuestionNumber() {
		return questionNumber;
	}

	public String getOptionType() {
		return optionType;
	}

	public ArrayList<GSurveyOption> getOptionList() {
		return optionList;
	}

	public void setQuestionNumber(String questionNumber) {
		this.questionNumber = questionNumber;
	}

	public void setOptionType(String optionType) {
		this.optionType = optionType;
	}

	public void setOptionList(ArrayList<GSurveyOption> optionList) {
		this.optionList = optionList;
	}

}
