package com.glance.bean.response;

import java.util.ArrayList;

import com.glance.bean.model.GUserMail;
import com.glance.bean.model.GlanceBaseBean;
import com.google.gson.annotations.SerializedName;

public class GGetMailsResponse extends GlanceBaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 462211450157933009L;
	
	
	@SerializedName("status")
	private String status;
	
	@SerializedName("message")
	private String message;
	
	@SerializedName("sent")
	private ArrayList<GUserMail> sent;
	
	@SerializedName("received")
	private ArrayList<GUserMail> received;
	
	
	
	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public ArrayList<GUserMail> getSent() {
		return sent;
	}


	public void setSent(ArrayList<GUserMail> sent) {
		this.sent = sent;
	}


	public ArrayList<GUserMail> getReceived() {
		return received;
	}


	public void setReceived(ArrayList<GUserMail> received) {
		this.received = received;
	}


	public String getMessage() {
		return message;
	}


	public void setMessage(String message) {
		this.message = message;
		
	}



}
