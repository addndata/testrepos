package com.project.admin.model;

public class TomcatDetails {

	private String tomcateName;
	private String tomcatport;
	private String tomcatUrl;
	private boolean status;
	
	public String getTomcateName() {
		return tomcateName;
	}
	public void setTomcateName(String tomcateName) {
		this.tomcateName = tomcateName;
	}
	public String getTomcatport() {
		return tomcatport;
	}
	public void setTomcatport(String tomcatport) {
		this.tomcatport = tomcatport;
	}
	public String getTomcatUrl() {
		return tomcatUrl;
	}
	public void setTomcatUrl(String tomcatUrl) {
		this.tomcatUrl = tomcatUrl;
	}
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	
	
}
