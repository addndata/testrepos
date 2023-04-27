package com.project.admin.model;

public class FuturesBalanceComparison {
	private String assetPair;
	private String currency;
	private String dbBalance;
	private String isoBalance;
	private String crossBalance;
	private String liquidityBuyQty;
	private String liquiditySellQty;
	private String liquidityBalance;
	private String tradableAmount;
	
	public String getAssetPair() {
		return assetPair;
	}
	public void setAssetPair(String assetPair) {
		this.assetPair = assetPair;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getDbBalance() {
		return dbBalance;
	}
	public void setDbBalance(String dbBalance) {
		this.dbBalance = dbBalance;
	}
	public String getIsoBalance() {
		return isoBalance;
	}
	public void setIsoBalance(String isoBalance) {
		this.isoBalance = isoBalance;
	}
	public String getCrossBalance() {
		return crossBalance;
	}
	public void setCrossBalance(String crossBalance) {
		this.crossBalance = crossBalance;
	}
	public String getLiquidityBuyQty() {
		return liquidityBuyQty;
	}
	public void setLiquidityBuyQty(String liquidityBuyQty) {
		this.liquidityBuyQty = liquidityBuyQty;
	}
	public String getLiquiditySellQty() {
		return liquiditySellQty;
	}
	public void setLiquiditySellQty(String liquiditySellQty) {
		this.liquiditySellQty = liquiditySellQty;
	}
	public String getLiquidityBalance() {
		return liquidityBalance;
	}
	public void setLiquidityBalance(String liquidityBalance) {
		this.liquidityBalance = liquidityBalance;
	}
	public String getTradableAmount() {
		return tradableAmount;
	}
	public void setTradableAmount(String tradableAmount) {
		this.tradableAmount = tradableAmount;
	}
}
