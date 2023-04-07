package com.project.admin.model;

public class SystemResponse {

	private String totalSpace;
	private String freeSpace;
	private String usableSpace;
	private String totalPhysicalMemory;
	private String freePhysicalMemory;
	private String committedVirtualMemory;
	
	
	public String getTotalSpace() {
		return totalSpace;
	}
	public void setTotalSpace(String totalSpace) {
		this.totalSpace = totalSpace;
	}
	public String getFreeSpace() {
		return freeSpace;
	}
	public void setFreeSpace(String freeSpace) {
		this.freeSpace = freeSpace;
	}
	public String getUsableSpace() {
		return usableSpace;
	}
	public void setUsableSpace(String usableSpace) {
		this.usableSpace = usableSpace;
	}
	public String getTotalPhysicalMemory() {
		return totalPhysicalMemory;
	}
	public void setTotalPhysicalMemory(String totalPhysicalMemory) {
		this.totalPhysicalMemory = totalPhysicalMemory;
	}
	public String getFreePhysicalMemory() {
		return freePhysicalMemory;
	}
	public void setFreePhysicalMemory(String freePhysicalMemory) {
		this.freePhysicalMemory = freePhysicalMemory;
	}
	public String getCommittedVirtualMemory() {
		return committedVirtualMemory;
	}
	public void setCommittedVirtualMemory(String committedVirtualMemory) {
		this.committedVirtualMemory = committedVirtualMemory;
	}
	
	
}
