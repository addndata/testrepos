package com.project.admin.model;

public class UserTransactions {

	// private data members
	private int transactionId;
	private int customerId;
	private String firstName;
	private String email;
	private String description;
	private String action;
	private int status;
	private String transactionTimestamp;
	private String orderId;
	private String tradeId;
	private String offerId;
	private String offerQty;
	private String offerPrice;
	private String requestAmount;
	private String requestPrice;
	private String currency;
	private String baseCurrency;
	private String currencyTxnid;
	private String debitAmount;
	private String creditAmount;
	private String miningfees;
	private String txncharge;
	private String networkfees;
	private String openingBalance;
	private String closingBalance;
	private String corporateRemitId;
	private String refNo;
	private int poolId;
	private int isDeleted;
	private String txnId;

	private int pageNo;
	private int noOfItemsPerPage;

	private String fromDate;
	private String toDate;
	private int userTag;
	private String searchString;
	public int getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(int transactionId) {
		this.transactionId = transactionId;
	}
	public int getCustomerId() {
		return customerId;
	}
	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getTransactionTimestamp() {
		return transactionTimestamp;
	}
	public void setTransactionTimestamp(String transactionTimestamp) {
		this.transactionTimestamp = transactionTimestamp;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getTradeId() {
		return tradeId;
	}
	public void setTradeId(String tradeId) {
		this.tradeId = tradeId;
	}
	public String getOfferId() {
		return offerId;
	}
	public void setOfferId(String offerId) {
		this.offerId = offerId;
	}
	public String getOfferQty() {
		return offerQty;
	}
	public void setOfferQty(String offerQty) {
		this.offerQty = offerQty;
	}
	public String getOfferPrice() {
		return offerPrice;
	}
	public void setOfferPrice(String offerPrice) {
		this.offerPrice = offerPrice;
	}
	public String getRequestAmount() {
		return requestAmount;
	}
	public void setRequestAmount(String requestAmount) {
		this.requestAmount = requestAmount;
	}
	public String getRequestPrice() {
		return requestPrice;
	}
	public void setRequestPrice(String requestPrice) {
		this.requestPrice = requestPrice;
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
	public String getCurrencyTxnid() {
		return currencyTxnid;
	}
	public void setCurrencyTxnid(String currencyTxnid) {
		this.currencyTxnid = currencyTxnid;
	}
	public String getDebitAmount() {
		return debitAmount;
	}
	public void setDebitAmount(String debitAmount) {
		this.debitAmount = debitAmount;
	}
	public String getCreditAmount() {
		return creditAmount;
	}
	public void setCreditAmount(String creditAmount) {
		this.creditAmount = creditAmount;
	}
	public String getMiningfees() {
		return miningfees;
	}
	public void setMiningfees(String miningfees) {
		this.miningfees = miningfees;
	}
	public String getTxncharge() {
		return txncharge;
	}
	public void setTxncharge(String txncharge) {
		this.txncharge = txncharge;
	}
	public String getNetworkfees() {
		return networkfees;
	}
	public void setNetworkfees(String networkfees) {
		this.networkfees = networkfees;
	}
	public String getOpeningBalance() {
		return openingBalance;
	}
	public void setOpeningBalance(String openingBalance) {
		this.openingBalance = openingBalance;
	}
	public String getClosingBalance() {
		return closingBalance;
	}
	public void setClosingBalance(String closingBalance) {
		this.closingBalance = closingBalance;
	}
	public String getCorporateRemitId() {
		return corporateRemitId;
	}
	public void setCorporateRemitId(String corporateRemitId) {
		this.corporateRemitId = corporateRemitId;
	}
	public String getRefNo() {
		return refNo;
	}
	public void setRefNo(String refNo) {
		this.refNo = refNo;
	}
	public int getPoolId() {
		return poolId;
	}
	public void setPoolId(int poolId) {
		this.poolId = poolId;
	}
	public int getIsDeleted() {
		return isDeleted;
	}
	public void setIsDeleted(int isDeleted) {
		this.isDeleted = isDeleted;
	}
	public String getTxnId() {
		return txnId;
	}
	public void setTxnId(String txnId) {
		this.txnId = txnId;
	}
	public int getPageNo() {
		return pageNo;
	}
	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}
	public int getNoOfItemsPerPage() {
		return noOfItemsPerPage;
	}
	public void setNoOfItemsPerPage(int noOfItemsPerPage) {
		this.noOfItemsPerPage = noOfItemsPerPage;
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
	public int getUserTag() {
		return userTag;
	}
	public void setUserTag(int userTag) {
		this.userTag = userTag;
	}
	public String getSearchString() {
		return searchString;
	}
	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}
	@Override
	public String toString() {
		return "UserTransactions [transactionId=" + transactionId + ", customerId=" + customerId + ", firstName="
				+ firstName + ", email=" + email + ", description=" + description + ", action=" + action + ", status="
				+ status + ", transactionTimestamp=" + transactionTimestamp + ", orderId=" + orderId + ", tradeId="
				+ tradeId + ", offerId=" + offerId + ", offerQty=" + offerQty + ", offerPrice=" + offerPrice
				+ ", requestAmount=" + requestAmount + ", requestPrice=" + requestPrice + ", currency=" + currency
				+ ", baseCurrency=" + baseCurrency + ", currencyTxnid=" + currencyTxnid + ", debitAmount=" + debitAmount
				+ ", creditAmount=" + creditAmount + ", miningfees=" + miningfees + ", txncharge=" + txncharge
				+ ", networkfees=" + networkfees + ", openingBalance=" + openingBalance + ", closingBalance="
				+ closingBalance + ", corporateRemitId=" + corporateRemitId + ", refNo=" + refNo + ", poolId=" + poolId
				+ ", isDeleted=" + isDeleted + ", txnId=" + txnId + ", pageNo=" + pageNo + ", noOfItemsPerPage="
				+ noOfItemsPerPage + ", fromDate=" + fromDate + ", toDate=" + toDate + ", userTag=" + userTag
				+ ", searchString =" + searchString + "]";
	}

}