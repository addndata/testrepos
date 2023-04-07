package com.project.admin.model;

public class AssetWiseOfferDetails {
	private String currency;
	private String baseCurrency;
	private String assetPair;
	private long buyOffer; 
	private long sellOffer;
	private double marketBuyPrice;
	private double marketSellPrice;
	private String bestBuyOffer;
	private String bestSellOffer;
	private long totalDelete;
	private String lastDeleted;
	private String lastPriceUpdatedOn;
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
	public String getAssetPair() {
		return assetPair;
	}
	public void setAssetPair(String assetPair) {
		this.assetPair = assetPair;
	}
	public long getBuyOffer() {
		return buyOffer;
	}
	public void setBuyOffer(long buyOffer) {
		this.buyOffer = buyOffer;
	}
	public long getSellOffer() {
		return sellOffer;
	}
	public void setSellOffer(long sellOffer) {
		this.sellOffer = sellOffer;
	}
	public double getMarketBuyPrice() {
		return marketBuyPrice;
	}
	public void setMarketBuyPrice(double marketBuyPrice) {
		this.marketBuyPrice = marketBuyPrice;
	}
	public double getMarketSellPrice() {
		return marketSellPrice;
	}
	public void setMarketSellPrice(double marketSellPrice) {
		this.marketSellPrice = marketSellPrice;
	}
	public String getBestBuyOffer() {
		return bestBuyOffer;
	}
	public void setBestBuyOffer(String bestBuyOffer) {
		this.bestBuyOffer = bestBuyOffer;
	}
	public String getBestSellOffer() {
		return bestSellOffer;
	}
	public void setBestSellOffer(String bestSellOffer) {
		this.bestSellOffer = bestSellOffer;
	}
	public long getTotalDelete() {
		return totalDelete;
	}
	public void setTotalDelete(long totalDelete) {
		this.totalDelete = totalDelete;
	}
	public String getLastDeleted() {
		return lastDeleted;
	}
	public void setLastDeleted(String lastDeleted) {
		this.lastDeleted = lastDeleted;
	}
	public String getLastPriceUpdatedOn() {
		return lastPriceUpdatedOn;
	}
	public void setLastPriceUpdatedOn(String lastPriceUpdatedOn) {
		this.lastPriceUpdatedOn = lastPriceUpdatedOn;
	}
}
