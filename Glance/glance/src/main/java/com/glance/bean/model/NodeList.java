package com.glance.bean.model;

import java.util.ArrayList;

public class NodeList{
	private ArrayList<MainNode> mainNodeList;
	private ArrayList<String> rootCause;
	
	
	public ArrayList<MainNode> getMainNodeList() {
		return mainNodeList;
	}
	public void setMainNodeList(ArrayList<MainNode> mainNodeList) {
		this.mainNodeList = mainNodeList;
	}
	public ArrayList<String> getRootCause() {
		return rootCause;
	}
	public void setRootCause(ArrayList<String> rootCause) {
		this.rootCause = rootCause;
	}
}