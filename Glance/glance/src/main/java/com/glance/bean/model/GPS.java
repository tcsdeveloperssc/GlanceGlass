package com.glance.bean.model;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class GPS {
	
		
	@SerializedName("coordinates")
	private ArrayList<String> coordinates;

	public ArrayList<String> getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(ArrayList<String> coordinates) {
		this.coordinates = coordinates;
	}
	
	
	

}
