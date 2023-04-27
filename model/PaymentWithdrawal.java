package com.project.admin.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentWithdrawal {

	 private String withdrawalId;
     private String customerId;
     private String creditAmount;
     private String description;
     private String currencyId;
     private String currency;
     private String status;
     private String action;
     private String transactionTimestamp;
     private String orderId;
     private String baseCurrencyId;

     private String chargeSgst;
     private String chargeCgst;

     private String withdrawalAmount;
     private String txnCharge;
     private String txnId;
     private String beneficiaryName;
     private String bankName;
     private String bankAddress;
     private String ifscCode;
     private String swiftCode;
     private String institutionTransitNo;
     private int openingBalance;
     private int refNo;
     private int poolId;
     private int isDeleted;
     private double insuredAmount;
     private String txnCount;
     
     private String customerName;
     private String accountNo;
     private String accountType;
     private int adminId;
     private int corporateRemitId;
     private int adminBankId;
     private String adminBankName;
     private String approvedDate;
     
	public String getWithdrawalId() {
		return withdrawalId;
	}
	public void setWithdrawalId(String withdrawalId) {
		this.withdrawalId = withdrawalId;
	}
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public String getCreditAmount() {
		return creditAmount;
	}
	public void setCreditAmount(String creditAmount) {
		this.creditAmount = creditAmount;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
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
	public String getBaseCurrencyId() {
		return baseCurrencyId;
	}
	public void setBaseCurrencyId(String baseCurrencyId) {
		this.baseCurrencyId = baseCurrencyId;
	}
	public String getChargeSgst() {
		return chargeSgst;
	}
	public void setChargeSgst(String chargeSgst) {
		this.chargeSgst = chargeSgst;
	}
	public String getChargeCgst() {
		return chargeCgst;
	}
	public void setChargeCgst(String chargeCgst) {
		this.chargeCgst = chargeCgst;
	}
	public String getWithdrawalAmount() {
		return withdrawalAmount;
	}
	public void setWithdrawalAmount(String withdrawalAmount) {
		this.withdrawalAmount = withdrawalAmount;
	}
	public String getTxnCharge() {
		return txnCharge;
	}
	public void setTxnCharge(String txnCharge) {
		this.txnCharge = txnCharge;
	}
	public String getTxnId() {
		return txnId;
	}
	public void setTxnId(String txnId) {
		this.txnId = txnId;
	}
	public String getBeneficiaryName() {
		return beneficiaryName;
	}
	public void setBeneficiaryName(String beneficiaryName) {
		this.beneficiaryName = beneficiaryName;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getBankAddress() {
		return bankAddress;
	}
	public void setBankAddress(String bankAddress) {
		this.bankAddress = bankAddress;
	}
	public String getIfscCode() {
		return ifscCode;
	}
	public void setIfscCode(String ifscCode) {
		this.ifscCode = ifscCode;
	}
	public String getSwiftCode() {
		return swiftCode;
	}
	public void setSwiftCode(String swiftCode) {
		this.swiftCode = swiftCode;
	}
	public String getInstitutionTransitNo() {
		return institutionTransitNo;
	}
	public void setInstitutionTransitNo(String institutionTransitNo) {
		this.institutionTransitNo = institutionTransitNo;
	}
	public int getOpeningBalance() {
		return openingBalance;
	}
	public void setOpeningBalance(int openingBalance) {
		this.openingBalance = openingBalance;
	}
	public int getRefNo() {
		return refNo;
	}
	public void setRefNo(int refNo) {
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
	public double getInsuredAmount() {
		return insuredAmount;
	}
	public void setInsuredAmount(double insuredAmount) {
		this.insuredAmount = insuredAmount;
	}
	public String getTxnCount() {
		return txnCount;
	}
	public void setTxnCount(String txnCount) {
		this.txnCount = txnCount;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getAccountNo() {
		return accountNo;
	}
	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}
	public String getAccountType() {
		return accountType;
	}
	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}
	public int getAdminId() {
		return adminId;
	}
	public void setAdminId(int adminId) {
		this.adminId = adminId;
	}
	public int getCorporateRemitId() {
		return corporateRemitId;
	}
	public void setCorporateRemitId(int corporateRemitId) {
		this.corporateRemitId = corporateRemitId;
	}
	public int getAdminBankId() {
		return adminBankId;
	}
	public void setAdminBankId(int adminBankId) {
		this.adminBankId = adminBankId;
	}
	public String getAdminBankName() {
		return adminBankName;
	}
	public void setAdminBankName(String adminBankName) {
		this.adminBankName = adminBankName;
	}
	public String getApprovedDate() {
		return approvedDate;
	}
	public void setApprovedDate(String approvedDate) {
		this.approvedDate = approvedDate;
	}
}
