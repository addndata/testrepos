package com.project.admin.model;

public class ReportRequest {
	private int reportNumber;
	private int customerId;
	private String fromDate;
	private String toDate;
	private String action;
	private int currencyId;
	private String currency;
	private String baseCurrency;
	private String exchange;
	private String assetPair;

	public int getReportNumber() {
		return reportNumber;
	}
	public void setReportNumber(int reportNumber) {
		this.reportNumber = reportNumber;
	}
	public int getCustomerId() {
		return customerId;
	}
	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}
	public String getFromDate() {
		return fromDate;
	}
	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}
	public String getToDate() {
		return toDate;
	}
	public void setToDate(String toDate) {
		this.toDate = toDate;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public int getCurrencyId() {
		return currencyId;
	}
	public void setCurrencyId(int currencyId) {
		this.currencyId = currencyId;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getBaseCurrency() {
		return baseCurrency;
	}
	public void setBaseCurrency(String baseCurrency) {
		this.baseCurrency = baseCurrency;
	}
	public String getExchange() {
		return exchange;
	}
	public void setExchange(String exchange) {
		this.exchange = exchange;
	}
	public String getAssetPair() {
		return assetPair;
	}
	public void setAssetPair(String assetPair) {
		this.assetPair = assetPair;
	}
}
