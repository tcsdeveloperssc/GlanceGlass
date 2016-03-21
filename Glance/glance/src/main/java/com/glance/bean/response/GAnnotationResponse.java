package com.glance.bean.response;

import java.util.ArrayList;

import com.glance.bean.model.GAnnotatePoint;
import com.glance.bean.model.GlanceBaseBean;
import com.google.gson.annotations.SerializedName;

public class GAnnotationResponse extends GlanceBaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4705352991675493059L;
	
	@SerializedName("color")
	private String color;
	
	@SerializedName("tool")
	private String tool;
	
	@SerializedName("toolsize")
	private String toolsize;
	
	@SerializedName("points")
	private ArrayList<String> pointList;
	
	@SerializedName("userId")
	private String userId;
	
	private ArrayList<GAnnotatePoint> changedPointList;

	public String getColor() {
		return color;
	}

	public String getTool() {
		return tool;
	}

	public String getToolsize() {
		return toolsize;
	}

	public ArrayList<String> getPointList() {
		return pointList;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public void setTool(String tool) {
		this.tool = tool;
	}

	public void setToolsize(String toolsize) {
		this.toolsize = toolsize;
	}

	public void setPointList(ArrayList<String> pointList) {
		this.pointList = pointList;
	}

	public ArrayList<GAnnotatePoint> getChangedPointList() {
		return changedPointList;
	}

	public void setChangedPointList(ArrayList<GAnnotatePoint> changedPointList) {
		this.changedPointList = changedPointList;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

}
