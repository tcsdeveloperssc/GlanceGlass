package com.glance.bean.model;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class GUserMail extends GlanceBaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 266759548386876936L;
	@SerializedName("senderId")
	private String senderId;

	@SerializedName("senderName")
	private String senderName;

	@SerializedName("text")
	private String text;

	@SerializedName("time")
	private String time;

	@SerializedName("alertId")
	private String alertId;

	@SerializedName("receivers")
	private ArrayList<Receivers> receivers;

	public class Receivers {
		@SerializedName("receiverId")
		private String receiverId;

		@SerializedName("receiverName")
		private String receiverName;

		public String getReceiverId() {
			return receiverId;
		}

		public void setReceiverId(String receiverId) {
			this.receiverId = receiverId;
		}

		public String getReceiverName() {
			return receiverName;
		}

		public void setReceiverName(String receiverName) {
			this.receiverName = receiverName;
		}
	}

	public String getSenderId() {
		return senderId;
	}

	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getAlertId() {
		return alertId;
	}

	public void setAlertId(String alertId) {
		this.alertId = alertId;
	}

	public ArrayList<Receivers> getReceivers() {
		return receivers;
	}

	public String getReceiverName() {
		if (receivers != null) {
			if (receivers.size() > 0) {
				return receivers.get(0).getReceiverName();
			}
		}
		return null;
	}

	public void setReceivers(ArrayList<Receivers> receivers) {
		this.receivers = receivers;
	}

}
