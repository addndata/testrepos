package com.project.admin.model;

public class NodeBalance {

	private String currencyId;
	private String currencyCode;
	private String currencyName;
	private String nodeBalance;
	private String nodeAddress;
	private String coldWalletBalance;
	private String liquidityBalance;
	private String dbBalance;
	private String sendPendingAmount;
	private String coinOwnerBalance;
	private String withdrawableAmount;
	private String balanceDifference;
	private String tradableAmount;
	private String tradableAmountInUsd;
	private String marketPriceInUsd;
	private int isSend;
	
	public String getCurrencyId() {
		return currencyId;
	}
	public void setCurrencyId(String currencyId) {
		this.currencyId = currencyId;
	}
	
	public String getCurrencyCode() {
		return currencyCode;
	}
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
	public String getCurrencyName() {
		return currencyName;
	}
	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}
	public String getNodeBalance() {
		return nodeBalance;
	}
	public void setNodeBalance(String nodeBalance) {
		this.nodeBalance = nodeBalance;
	}
	public String getNodeAddress() {
		return nodeAddress;
	}
	public void setNodeAddress(String nodeAddress) {
		this.nodeAddress = nodeAddress;
	}
	public String getColdWalletBalance() {
		return coldWalletBalance;
	}
	public void setColdWalletBalance(String coldWalletBalance) {
		this.coldWalletBalance = coldWalletBalance;
	}
	public String getLiquidityBalance() {
		return liquidityBalance;
	}
	public void setLiquidityBalance(String liquidityBalance) {
		this.liquidityBalance = liquidityBalance;
	}
	public String getDbBalance() {
		return dbBalance;
	}
	public void setDbBalance(String dbBalance) {
		this.dbBalance = dbBalance;
	}
	public String getSendPendingAmount() {
		return sendPendingAmount;
	}
	public void setSendPendingAmount(String sendPendingAmount) {
		this.sendPendingAmount = sendPendingAmount;
	}
	public String getCoinOwnerBalance() {
		return coinOwnerBalance;
	}
	public void setCoinOwnerBalance(String coinOwnerBalance) {
		this.coinOwnerBalance = coinOwnerBalance;
	}
	public String getWithdrawableAmount() {
		return withdrawableAmount;
	}
	public void setWithdrawableAmount(String withdrawableAmount) {
		this.withdrawableAmount = withdrawableAmount;
	}
	public String getBalanceDifference() {
		return balanceDifference;
	}
	public void setBalanceDifference(String balanceDifference) {
		this.balanceDifference = balanceDifference;
	}
	public String getTradableAmount() {
		return tradableAmount;
	}
	public void setTradableAmount(String tradableAmount) {
		this.tradableAmount = tradableAmount;
	}
	public String getTradableAmountInUsd() {
		return tradableAmountInUsd;
	}
	public void setTradableAmountInUsd(String tradableAmountInUsd) {
		this.tradableAmountInUsd = tradableAmountInUsd;
	}
	public String getMarketPriceInUsd() {
		return marketPriceInUsd;
	}
	public void setMarketPriceInUsd(String marketPriceInUsd) {
		this.marketPriceInUsd = marketPriceInUsd;
	}
	public int getIsSend() {
		return isSend;
	}
	public void setIsSend(int isSend) {
		this.isSend = isSend;
	}
}
