package com.project.admin.model;

public class CurrencyRate {
	private String assetPair;
	private double marketPrice;
	private double incrBy;
	private double exchangePrice;

	public String getAssetPair() {
		return assetPair;
	}
	public void setAssetPair(String assetPair) {
		this.assetPair = assetPair;
	}
	public double getMarketPrice() {
		return marketPrice;
	}
	public void setMarketPrice(double marketPrice) {
		this.marketPrice = marketPrice;
	}
	public double getIncrBy() {
		return incrBy;
	}
	public void setIncrBy(double incrBy) {
		this.incrBy = incrBy;
	}
	public double getExchangePrice() {
		return exchangePrice;
	}
	public void setExchangePrice(double exchangePrice) {
		this.exchangePrice = exchangePrice;
	}
}
