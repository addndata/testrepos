package com.project.admin.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserBalance {
	private int customerId;
	private int currencyId;
	private String currencyCode;
	private double closingBalance;
	private int sendAccess;
	private int receiveAccess;
	private double totalBuy;
	private double totalSell;
	public int getCustomerId() {
		return customerId;
	}
	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}
	public int getCurrencyId() {
		return currencyId;
	}
	public void setCurrencyId(int currencyId) {
		this.currencyId = currencyId;
	}
	public String getCurrencyCode() {
		return currencyCode;
	}
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
	public double getClosingBalance() {
		return closingBalance;
	}
	public void setClosingBalance(double closingBalance) {
		this.closingBalance = closingBalance;
	}
	public int getSendAccess() {
		return sendAccess;
	}
	public void setSendAccess(int sendAccess) {
		this.sendAccess = sendAccess;
	}
	public int getReceiveAccess() {
		return receiveAccess;
	}
	public void setReceiveAccess(int receiveAccess) {
		this.receiveAccess = receiveAccess;
	}
	public double getTotalBuy() {
		return totalBuy;
	}
	public void setTotalBuy(double totalBuy) {
		this.totalBuy = totalBuy;
	}
	public double getTotalSell() {
		return totalSell;
	}
	public void setTotalSell(double totalSell) {
		this.totalSell = totalSell;
	}
	
	@Override
	public String toString() {
		return "UserBalance [customerId=" + customerId + ", currencyId=" + currencyId + ", currencyCode=" + currencyCode
				+ ", closingBalance=" + closingBalance + ", sendAccess=" + sendAccess + ", receiveAccess="
				+ receiveAccess + ", totalBuy=" + totalBuy + ", totalSell=" + totalSell + "]";
	}	
}
