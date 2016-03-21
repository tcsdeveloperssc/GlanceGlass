package com.glance.bean.response;

import java.util.ArrayList;

import com.glance.bean.model.GUserTask;
import com.glance.bean.model.GlanceBaseBean;
import com.google.gson.annotations.SerializedName;

public class GGetUserTaskQueueResponse extends GlanceBaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 462211450157933009L;

	@SerializedName("message")
	private String message;

	@SerializedName("status")
	private String status;

	@SerializedName("userTasksList")
	private ArrayList<GUserTask> userTasksList;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public ArrayList<GUserTask> getUserTasksList() {
		return userTasksList;
	}

	public void setUserTasksList(ArrayList<GUserTask> userTasksList) {
		this.userTasksList = userTasksList;
	}

	public GUserTask getTaskByTaskId(String taskId) {

		GUserTask newTask = null;
		for (GUserTask task : userTasksList) {
			if (task != null && task.getTaskId() != null){
			if (task.getTaskId().equalsIgnoreCase(taskId)) {
				newTask = task;
				break;
			}
			}
		}

		return newTask;
	}

	public void setTaskByTaskId(String taskId, GUserTask changedTask) {

		
		for (GUserTask task : userTasksList) {
			if (task != null && task.getTaskId() != null){
			if (task.getTaskId().equalsIgnoreCase(taskId)) {
				task = changedTask;
				break;
			}
		}
		}

		
	}

}
