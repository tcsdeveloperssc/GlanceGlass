package com.glance.bean.response;

import java.util.ArrayList;

import com.glance.bean.model.GAssetTask;
import com.glance.bean.model.GlanceBaseBean;
import com.google.gson.annotations.SerializedName;

public class GGetAssetTaskQueueResponse extends GlanceBaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 462211450157933009L;
	
	@SerializedName("message")
	private String message;
	
	@SerializedName("status")
	private String status;
	
	@SerializedName("userTasksList")
	private ArrayList<GAssetTask> userTasksList;
	
	
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


	public ArrayList<GAssetTask> getUserTasksList() {
		return userTasksList;
	}


	public void setUserTasksList(ArrayList<GAssetTask> userTasksList) {
		this.userTasksList = userTasksList;
	}
	
	public GAssetTask getTaskByStoryId(String storyId){
		
		GAssetTask newTask = null;
		for(GAssetTask task : userTasksList){
			
			if(task.getStoryId().equalsIgnoreCase(storyId)){
				newTask = task;
				break;
			}
		}
		
		return newTask;
	}

}
