package com.glance.bean.model;

import android.os.Parcel;
import android.os.Parcelable;



public class HotSpots implements Parcelable{
	private String key;
	private String x;
	private String y;
	private String width;
	private String height;
	private String radius;
	private String links;
	
	
	
	public HotSpots()
	{
		
	}
	public void setKey(String key){
		this.key=key;
	}
	
    public String getKey()
    {
    	return key;
    }
    
    public void setx(String x){
		this.x=x;
	}
    
    public String getx()
    {
    	return x;
    }
    
    public void sety(String y){
		this.y=y;
	}
    
    public String gety()
    {
    	return y;
    }
    
    public void setwidth(String width){
		this.width=width;
	}
    
    public String getwidth()
    {
    	return width;
    }
    
    public void setheight(String height){
		this.height=height;
	}
    
    public String getheight()
    {
    	return height;
    }
    
    public void setlinks(String links){
		this.links=links;
	}
    
    public String getlinks()
    {
    	return links;
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
		dest.writeString(links);
		dest.writeString(radius);
	}
	
	public HotSpots(Parcel in)
	{
		
		
		
		this.key = in.readString();
		this.x = in.readString();
		this.y = in.readString();
		this.width = in.readString();
		this.height = in.readString();
		this.links = in.readString();
		this.radius = in.readString();
	   
	            // and all other elements
	    }
	public static final Parcelable.Creator<HotSpots> CREATOR = 
			new Parcelable.Creator<HotSpots>() { 
			public HotSpots createFromParcel(Parcel in) { 
				
			return new HotSpots(in);
			}

			@Override
			public HotSpots[] newArray(int size) {
			return new HotSpots[size];
			}
			};



	public String getRadius() {
		return radius;
	}
	public void setRadius(String radius) {
		this.radius = radius;
	}
}
