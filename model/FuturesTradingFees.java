package com.project.admin.model;

public class FuturesTradingFees {
	private String currencyId;
	private String currency;
	private String interestRate;
	private String txnCharge;
	private String lenderInterest;
	private String exchangeInterest;
	public String getCurrencyId() {
		return currencyId;
	}
	public void setCurrencyId(String currencyId) {
		this.currencyId = currencyId;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getInterestRate() {
		return interestRate;
	}
	public void setInterestRate(String interestRate) {
		this.interestRate = interestRate;
	}
	public String getTxnCharge() {
		return txnCharge;
	}
	public void setTxnCharge(String txnCharge) {
		this.txnCharge = txnCharge;
	}
	public String getLenderInterest() {
		return lenderInterest;
	}
	public void setLenderInterest(String lenderInterest) {
		this.lenderInterest = lenderInterest;
	}
	public String getExchangeInterest() {
		return exchangeInterest;
	}
	public void setExchangeInterest(String exchangeInterest) {
		this.exchangeInterest = exchangeInterest;
	}

}
