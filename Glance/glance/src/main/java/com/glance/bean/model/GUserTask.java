package com.glance.bean.model;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class GUserTask extends GlanceBaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 266759548386876936L;
	@SerializedName("assignedTime")
	private long assignedTime;
	
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
	
	

	@SerializedName("status")
	private String status;
	
	@SerializedName("taskStatus")
	private String taskStatus;
	
	
	@SerializedName("taskDescription")
	private String taskDescription;
	
	@SerializedName("taskName")
	private String taskName;
	
	@SerializedName("storyName")
	private String storyName;
	
	@SerializedName("description")
	private String description;
	
	@SerializedName("userName")
	private String userName;
	
	@SerializedName("selfHelp")
	private String taskType;
	
	@SerializedName("title")
	private String title;
	
	@SerializedName("checked")
	private  ArrayList<String> checkedSubNodeKeys;
	

	public long getAssignedTime() {
		return assignedTime;
	}

	public void setAssignedTime(long assignedTime) {
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

	public String getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(String taskStatus) {
		this.taskStatus = taskStatus;
	}

	public String getStoryName() {
		return storyName;
	}

	public void setStoryName(String storyName) {
		this.storyName = storyName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	public String getStoryId() {
		return storyId;
	}

	public void setStoryId(String storyId) {
		this.storyId = storyId;
	}

	public String getTaskType() {
		return taskType;
	}

	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}

	public ArrayList<String> getCheckedSubNodeKeys() {
		return checkedSubNodeKeys;
	}

	public void setCheckedSubNodeKeys(ArrayList<String> checkedSubNodeKeys) {
		this.checkedSubNodeKeys = checkedSubNodeKeys;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
