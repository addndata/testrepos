package com.project.admin.model;

public class AssetPairDetails {
	private int id;
	private String assetPair;
	private int assetPairType;
	private String assetCode;
	private int baseCurrencyId;
	private String baseCurrency;
	private int currencyId;
	private String currency;
	private int assetOrder;
	private int isActive;
	private String assetPairName;
	private int contractTypeId;
	private String contractType;
	private String quarterValue;
	private String amountPrecision;
	private String pricePrecision;
	private int buySellFlag;
	private String expiryDate;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getAssetPair() {
		return assetPair;
	}
	public void setAssetPair(String assetPair) {
		this.assetPair = assetPair;
	}
	public int getAssetPairType() {
		return assetPairType;
	}
	public void setAssetPairType(int assetPairType) {
		this.assetPairType = assetPairType;
	}
	public String getAssetCode() {
		return assetCode;
	}
	public void setAssetCode(String assetCode) {
		this.assetCode = assetCode;
	}
	public int getBaseCurrencyId() {
		return baseCurrencyId;
	}
	public void setBaseCurrencyId(int baseCurrencyId) {
		this.baseCurrencyId = baseCurrencyId;
	}
	public String getBaseCurrency() {
		return baseCurrency;
	}
	public void setBaseCurrency(String baseCurrency) {
		this.baseCurrency = baseCurrency;
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
	public int getAssetOrder() {
		return assetOrder;
	}
	public void setAssetOrder(int assetOrder) {
		this.assetOrder = assetOrder;
	}
	public int getIsActive() {
		return isActive;
	}
	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}
	public String getAssetPairName() {
		return assetPairName;
	}
	public void setAssetPairName(String assetPairName) {
		this.assetPairName = assetPairName;
	}
	public int getContractTypeId() {
		return contractTypeId;
	}
	public void setContractTypeId(int contractTypeId) {
		this.contractTypeId = contractTypeId;
	}
	public String getContractType() {
		return contractType;
	}
	public void setContractType(String contractType) {
		this.contractType = contractType;
	}
	public String getQuarterValue() {
		return quarterValue;
	}
	public void setQuarterValue(String quarterValue) {
		this.quarterValue = quarterValue;
	}
	public String getAmountPrecision() {
		return amountPrecision;
	}
	public void setAmountPrecision(String amountPrecision) {
		this.amountPrecision = amountPrecision;
	}
	public String getPricePrecision() {
		return pricePrecision;
	}
	public void setPricePrecision(String pricePrecision) {
		this.pricePrecision = pricePrecision;
	}
	public int getBuySellFlag() {
		return buySellFlag;
	}
	public void setBuySellFlag(int buySellFlag) {
		this.buySellFlag = buySellFlag;
	}
	public String getExpiryDate() {
		return expiryDate;
	}
	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
	}
	
	@Override
	public String toString() {
		return "AssetPairDetails [id=" + id + ", assetPair=" + assetPair + ", assetPairType=" + assetPairType
				+ ", assetCode=" + assetCode + ", baseCurrencyId=" + baseCurrencyId + ", baseCurrency=" + baseCurrency
				+ ", currencyId=" + currencyId + ", currency=" + currency + ", assetOrder=" + assetOrder + ", isActive="
				+ isActive + ", assetPairName=" + assetPairName + ", contractTypeId=" + contractTypeId
				+ ", contractType=" + contractType + ", quarterValue=" + quarterValue + ", amountPrecision="
				+ amountPrecision + ", pricePrecision=" + pricePrecision + ", buySellFlag=" + buySellFlag
				+ ", expiryDate=" + expiryDate + "]";
	}
}
