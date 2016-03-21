package com.glance.bean.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;



public class TextSpots implements Parcelable{
	@SerializedName("x")
	private String x;
	@SerializedName("y")
	private String y;
	@SerializedName("key")
	private String key;
	@SerializedName("width")
	private String width;
	@SerializedName("height")
	private  String height;
	@SerializedName("content")
	private  String content;
	
	
	public String getX() {
		return x;
	}
	public String getY() {
		return y;
	}
	public String getWidth() {
		return width;
	}
	public String getHeight() {
		return height;
	}
	public String getContent() {
		return content;
	}
	public void setX(String x) {
		this.x = x;
	}
	public void setY(String y) {
		this.y = y;
	}
	public void setWidth(String width) {
		this.width = width;
	}
	public void setHeight(String height) {
		this.height = height;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public TextSpots()
	{
		
	}
	public void setKey(String key){
		this.key=key;
	}
	
    

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(key);
		dest.writeString(x);
		dest.writeString(y);
		dest.writeString(width);
		dest.writeString(height);
		dest.writeString(content);
		
	}
	
	public TextSpots(Parcel in)
	{
		
		
		
		this.key = in.readString();
		this.x = in.readString();
		this.y = in.readString();
		this.width = in.readString();
		this.height = in.readString();
		this.content = in.readString();
		
	   
	            // and all other elements
	    }
	public static final Parcelable.Creator<TextSpots> CREATOR = 
			new Parcelable.Creator<TextSpots>() { 
			public TextSpots createFromParcel(Parcel in) { 
				
			return new TextSpots(in);
			}

			@Override
			public TextSpots[] newArray(int size) {
			return new TextSpots[size];
			}
			};



	
}
