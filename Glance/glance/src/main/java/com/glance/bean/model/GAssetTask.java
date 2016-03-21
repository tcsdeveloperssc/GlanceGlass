package com.glance.bean.model;

import com.google.gson.annotations.SerializedName;

public class GAssetTask extends GlanceBaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 266759548386876936L;
	@SerializedName("assignedTime")
	private String assignedTime;
	
	@SerializedName("priority")
	private String priority;
	
	@SerializedName("startTime")
	private String startTime;
	
	@SerializedName("storyId")
	private String storyId;
	
	@SerializedName("taskId")
	private String taskId;
	
	@SerializedName("latitude")
	private String latitude;
	@SerializedName("longitude")
	private String longitude;
	
	public String getStoryId() {
		return storyId;
	}

	public void setStoryId(String storyId) {
		this.storyId = storyId;
	}

	@SerializedName("status")
	private String status;
	
	@SerializedName("taskDescription")
	private String taskDescription;
	
	@SerializedName("taskName")
	private String taskName;
	
	@SerializedName("userName")
	private String userName;

	public String getAssignedTime() {
		return assignedTime;
	}

	public void setAssignedTime(String assignedTime) {
		this.assignedTime = assignedTime;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}
	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTaskDescription() {
		return taskDescription;
	}

	public void setTaskDescription(String taskDescription) {
		this.taskDescription = taskDescription;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

}
