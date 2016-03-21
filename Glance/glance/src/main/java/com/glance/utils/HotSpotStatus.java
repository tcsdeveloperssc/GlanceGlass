package com.glance.utils;

public class HotSpotStatus{
	private String subNodeKey;
	private String hotspotId;
	private int status;
	
	public final static int STATUS_RUNNING = 0;
	public final static int STATUS_PAUSED = -1;
	public final static int STATUS_COMPLETED = 1;
	public final static int STATUS_NOT_STARTED = 2;
	public final static int STATUS_ERROR = -2;
	public String getSubNodeKey() {
		return subNodeKey;
	}
	public void setSubNodeKey(String subNodeKey) {
		this.subNodeKey = subNodeKey;
	}
	public String getHotspotId() {
		return hotspotId;
	}
	public void setHotspotId(String hotspotId) {
		this.hotspotId = hotspotId;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
}