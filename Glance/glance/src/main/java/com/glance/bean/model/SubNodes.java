package com.glance.bean.model;

import java.util.ArrayList;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;


public class SubNodes implements Parcelable{

	 String subNodeKey;
	 String subNodetitle;
	 String artifactId;
	 ArrayList<HotSpots> hotspots;
	 private  ArrayList<TextSpots> textSpots;
	 private  String checkList;
	 private  String selfHelp;
	 private  String color_code;
	 private boolean isCheckListDone;
	 
	 
			
	  
	
	public SubNodes()
	{
		
	}
	
	public void setsubNodeKey(String subNodeKey){
		this.subNodeKey=subNodeKey;
	}
	
    public String getsubNodeKey()
    {
    	return subNodeKey;
    }
    
    public void setsubNodetitle(String subNodetitle){
		this.subNodetitle=subNodetitle;
	}
    
    public String getsubNodetitle()
    {
    	return subNodetitle;
    }
    
    public void setartifactId(String artifactId){
		this.artifactId=artifactId;
	}
    
    public String getartifactId()
    {
    	return artifactId;
    }
    
    public void sethotspots(ArrayList<HotSpots> hotspots){
		this.hotspots=hotspots;
	}
    
    public ArrayList<HotSpots> gethotspots()
    {
    	return hotspots;
    }

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(subNodeKey);
		dest.writeString(subNodetitle);
		dest.writeString(artifactId);
		dest.writeString(checkList);
		dest.writeString(selfHelp);
		dest.writeString(color_code);
		
		Bundle b = new Bundle();
		b.putParcelableArrayList("hotspots", hotspots);
		b.putParcelableArrayList("textspots", textSpots);
		dest.writeBundle(b);
	}
	public SubNodes(Parcel in)
	{
		
		
		this.subNodeKey = in.readString();
		this.subNodetitle = in.readString();
		this.artifactId = in.readString();
		Bundle b = in.readBundle(HotSpots.class.getClassLoader());        
		this.hotspots = b.getParcelableArrayList("hotspots");
		this.textSpots = b.getParcelableArrayList("textspots");
	            
	    }
	public String getSubNodeKey() {
		return subNodeKey;
	}

	public String getSubNodetitle() {
		return subNodetitle;
	}

	public ArrayList<HotSpots> getHotspots() {
		return hotspots;
	}

	public ArrayList<TextSpots> getTextSpots() {
		return textSpots;
	}

	public String getCheckList() {
		return checkList;
	}

	public String getSelfHelp() {
		return selfHelp;
	}

	public String getColor_code() {
		return color_code;
	}

	public void setSubNodeKey(String subNodeKey) {
		this.subNodeKey = subNodeKey;
	}

	public void setSubNodetitle(String subNodetitle) {
		this.subNodetitle = subNodetitle;
	}

	public void setHotspots(ArrayList<HotSpots> hotspots) {
		this.hotspots = hotspots;
	}

	public void setTextSpots(ArrayList<TextSpots> textSpots) {
		this.textSpots = textSpots;
	}

	public void setCheckList(String checkList) {
		this.checkList = checkList;
	}

	public void setSelfhelp(String selfHelp) {
		this.selfHelp = selfHelp;
	}

	public void setColor_code(String color_code) {
		this.color_code = color_code;
	}
	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator<SubNodes> CREATOR = 
			new Parcelable.Creator<SubNodes>() { 
			public SubNodes createFromParcel(Parcel in) { 
				
			return new SubNodes(in);
			}

			@Override
			public SubNodes[] newArray(int size) {
			return new SubNodes[size];
			}
			};





	public boolean isCheckListDone() {
		return isCheckListDone;
	}

	public void setCheckListDone(boolean isCheckListDone) {
		this.isCheckListDone = isCheckListDone;
	}
	
}
