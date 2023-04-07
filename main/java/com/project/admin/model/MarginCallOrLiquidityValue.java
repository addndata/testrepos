package com.project.admin.model;

public class MarginCallOrLiquidityValue {
	private int id;
	private String type;
	private String marginCall;
	private String autoLiquidation;
	private String createdOn;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getMarginCall() {
		return marginCall;
	}
	public void setMarginCall(String marginCall) {
		this.marginCall = marginCall;
	}
	public String getAutoLiquidation() {
		return autoLiquidation;
	}
	public void setAutoLiquidation(String autoLiquidation) {
		this.autoLiquidation = autoLiquidation;
	}
	public String getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}
	
}
