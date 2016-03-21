package com.glance.bean.model;


public class GAnnotatePoint extends GlanceBaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7370506040259053109L;
	private float x;
	private float y;

	public GAnnotatePoint(String data) {
		String points[] = data.split(",");
		float xVal = Float.valueOf(points[0]);
		float yVal = Float.valueOf(points[1]);
		setX(xVal);
		setY(yVal);
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public void setX(float x) {
		this.x = x;
	}

	public void setY(float y) {
		this.y = y;
	}
}
