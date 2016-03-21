package com.glance.bean.response;

import com.glance.bean.model.GlanceBaseBean;
import com.google.gson.annotations.SerializedName;

public class GGetAssetDetailResponse extends GlanceBaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3142798475896409909L;

	@SerializedName("id")
	private String id;

	@SerializedName("message")
	private String message;

	@SerializedName("status")
	private String status;

	@SerializedName("code")
	private String code;

	@SerializedName("description")
	private String description;

	@SerializedName("location")
	private String location;

	@SerializedName("name")
	private String name;

	/*@SerializedName("QRCode")
	private String QRCode;*/

	@SerializedName("tenantId")
	private String tenantId;

	@SerializedName("type")
	private String type;

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getId() {
		return id;
	}

	public String getMessage() {
		return message;
	}

	public String getStatus() {
		return status;
	}

	public String getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}

	public String getLocation() {
		return location;
	}

	public String getName() {
		return name;
	}

	public String getTenantId() {
		return tenantId;
	}

	public String getType() {
		return type;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public void setType(String type) {
		this.type = type;
	}
}
