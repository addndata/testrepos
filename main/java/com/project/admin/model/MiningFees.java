package com.project.admin.model;

public class MiningFees {
	private int currencyId;
	private String currency;
	private String tokenType;
	private double fromFee;
	private double toFee;
	private double minFee;
	private double feeRate;
	private String actualFees;
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
	public String getTokenType() {
		return tokenType;
	}
	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}
	public double getFromFee() {
		return fromFee;
	}
	public void setFromFee(double fromFee) {
		this.fromFee = fromFee;
	}
	public double getToFee() {
		return toFee;
	}
	public void setToFee(double toFee) {
		this.toFee = toFee;
	}
	public double getMinFee() {
		return minFee;
	}
	public void setMinFee(double minFee) {
		this.minFee = minFee;
	}
	public double getFeeRate() {
		return feeRate;
	}
	public void setFeeRate(double feeRate) {
		this.feeRate = feeRate;
	}
	public String getActualFees() {
		return actualFees;
	}
	public void setActualFees(String actualFees) {
		this.actualFees = actualFees;
	}
	
}
