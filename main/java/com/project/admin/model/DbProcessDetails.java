package com.project.admin.model;

public class DbProcessDetails {

	private String resourceName;
	private String currentUtilization;
	private String maxUtilization;
	private String limitValue;
	
	
	public String getResourceName() {
		return resourceName;
	}
	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}
	public String getCurrentUtilization() {
		return currentUtilization;
	}
	public void setCurrentUtilization(String currentUtilization) {
		this.currentUtilization = currentUtilization;
	}
	public String getMaxUtilization() {
		return maxUtilization;
	}
	public void setMaxUtilization(String maxUtilization) {
		this.maxUtilization = maxUtilization;
	}
	public String getLimitValue() {
		return limitValue;
	}
	public void setLimitValue(String limitValue) {
		this.limitValue = limitValue;
	}
	
	
}
