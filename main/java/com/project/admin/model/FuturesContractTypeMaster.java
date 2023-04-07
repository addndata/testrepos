package com.project.admin.model;

public class FuturesContractTypeMaster {
	private int contractTypeId;
	private int contractOrder;
	private String quarter;
	private String expiryDate;
	
	public int getContractTypeId() {
		return contractTypeId;
	}
	public void setContractTypeId(int contractTypeId) {
		this.contractTypeId = contractTypeId;
	}
	public int getContractOrder() {
		return contractOrder;
	}
	public void setContractOrder(int contractOrder) {
		this.contractOrder = contractOrder;
	}
	public String getQuarter() {
		return quarter;
	}
	public void setQuarter(String quarter) {
		this.quarter = quarter;
	}
	public String getExpiryDate() {
		return expiryDate;
	}
	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
	}
	
	
}
