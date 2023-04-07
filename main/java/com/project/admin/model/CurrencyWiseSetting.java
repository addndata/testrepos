package com.project.admin.model;

public class CurrencyWiseSetting {

	private int currencyId;
	private String currency;
	private double minLimit;
	private double dailySendLimit;
	private double monthlySendLimit;
	private double dailySellLimit;
	private double dailyBuyLimit;
	private double makerCharge;
	private double takerCharge;
	private double discountMakerCharge;
	private double discountTakerCharge;
	private double txnCharge;
	private double minBalance;
	
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
	public double getMinLimit() {
		return minLimit;
	}
	public void setMinLimit(double minLimit) {
		this.minLimit = minLimit;
	}
	public double getDailySendLimit() {
		return dailySendLimit;
	}
	public void setDailySendLimit(double dailySendLimit) {
		this.dailySendLimit = dailySendLimit;
	}
	public double getMonthlySendLimit() {
		return monthlySendLimit;
	}
	public void setMonthlySendLimit(double monthlySendLimit) {
		this.monthlySendLimit = monthlySendLimit;
	}
	public double getDailySellLimit() {
		return dailySellLimit;
	}
	public void setDailySellLimit(double dailySellLimit) {
		this.dailySellLimit = dailySellLimit;
	}
	public double getDailyBuyLimit() {
		return dailyBuyLimit;
	}
	public void setDailyBuyLimit(double dailyBuyLimit) {
		this.dailyBuyLimit = dailyBuyLimit;
	}
	public double getMakerCharge() {
		return makerCharge;
	}
	public void setMakerCharge(double makerCharge) {
		this.makerCharge = makerCharge;
	}
	public double getTakerCharge() {
		return takerCharge;
	}
	public void setTakerCharge(double takerCharge) {
		this.takerCharge = takerCharge;
	}
	public double getDiscountMakerCharge() {
		return discountMakerCharge;
	}
	public void setDiscountMakerCharge(double discountMakerCharge) {
		this.discountMakerCharge = discountMakerCharge;
	}
	public double getDiscountTakerCharge() {
		return discountTakerCharge;
	}
	public void setDiscountTakerCharge(double discountTakerCharge) {
		this.discountTakerCharge = discountTakerCharge;
	}
	public double getTxnCharge() {
		return txnCharge;
	}
	public void setTxnCharge(double txnCharge) {
		this.txnCharge = txnCharge;
	}
	public double getMinBalance() {
		return minBalance;
	}
	public void setMinBalance(double minBalance) {
		this.minBalance = minBalance;
	}
}
