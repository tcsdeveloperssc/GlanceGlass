package com.glance.bean.model;

import com.google.gson.annotations.SerializedName;

public class GUserBean extends GlanceBaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 266759548386876936L;

	@SerializedName("firstName")
	private String first_name;
	
	@SerializedName("lastName")
	private String last_name;
	
	@SerializedName("registrationId")
	private String regId;
	
	

	public String getFirst_name() {
		return first_name;
	}
	
	@SerializedName("distance")
	private String distance;

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}

	public String getLast_name() {
		return last_name;
	}

	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}

	public String getRegId() {
		return regId;
	}

	public void setRegId(String regId) {
		this.regId = regId;
	}
	
	
}
