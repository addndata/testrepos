package com.project.admin.model;

public class PaymentOrders {

	private int orderId;
	private int userId;
	private int adminUserId;
	private String orderNo;
	private int invoiceId;
	private double amount;
	private String referenceNo;
	private int paymentMethod;
	private String created;
	private String updated;
	private int status;
	private int pageNo;
	private String remarks;
	private int noOfItemsPerPage;
	private Users users;
	private String searchString ;
	private BankDetails bankDetails;
	private String paymentGateway;
	private AdminBankDetails adminBankDetails;
	private int currencyId;
	private String currency;
	private String description;
	private String action;
	private String transactionFee;
	private int securityCode;
	private int otp;
	
	public int getOrderId() {
		return orderId;
	}
	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public int getAdminUserId() {
		return adminUserId;
	}
	public void setAdminUserId(int adminUserId) {
		this.adminUserId = adminUserId;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public int getInvoiceId() {
		return invoiceId;
	}
	public void setInvoiceId(int invoiceId) {
		this.invoiceId = invoiceId;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public String getReferenceNo() {
		return referenceNo;
	}
	public void setReferenceNo(String referenceNo) {
		this.referenceNo = referenceNo;
	}
	public int getPaymentMethod() {
		return paymentMethod;
	}
	public void setPaymentMethod(int paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
	public String getCreated() {
		return created;
	}
	public void setCreated(String created) {
		this.created = created;
	}
	public String getUpdated() {
		return updated;
	}
	public void setUpdated(String updated) {
		this.updated = updated;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getPageNo() {
		return pageNo;
	}
	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public int getNoOfItemsPerPage() {
		return noOfItemsPerPage;
	}
	public void setNoOfItemsPerPage(int noOfItemsPerPage) {
		this.noOfItemsPerPage = noOfItemsPerPage;
	}
	public Users getUsers() {
		return users;
	}
	public void setUsers(Users users) {
		this.users = users;
	}
	public String getSearchString() {
		return searchString;
	}
	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}
	public BankDetails getBankDetails() {
		return bankDetails;
	}
	public void setBankDetails(BankDetails bankDetails) {
		this.bankDetails = bankDetails;
	}
	public String getPaymentGateway() {
		return paymentGateway;
	}
	public void setPaymentGateway(String paymentGateway) {
		this.paymentGateway = paymentGateway;
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
	public AdminBankDetails getAdminBankDetails() {
		return adminBankDetails;
	}
	public void setAdminBankDetails(AdminBankDetails adminBankDetails) {
		this.adminBankDetails = adminBankDetails;
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
	public String getTransactionFee() {
		return transactionFee;
	}
	public void setTransactionFee(String transactionFee) {
		this.transactionFee = transactionFee;
	}
	public int getSecurityCode() {
		return securityCode;
	}
	public void setSecurityCode(int securityCode) {
		this.securityCode = securityCode;
	}
	public int getOtp() {
		return otp;
	}
	public void setOtp(int otp) {
		this.otp = otp;
	}
	
	@Override
	public String toString() {
		return "PaymentOrders [orderId=" + orderId + ", userId=" + userId + ", adminUserId=" + adminUserId
				+ ", orderNo=" + orderNo + ", invoiceId=" + invoiceId + ", amount=" + amount + ", referenceNo="
				+ referenceNo + ", paymentMethod=" + paymentMethod + ", created=" + created + ", updated=" + updated
				+ ", status=" + status + ", pageNo=" + pageNo + ", remarks=" + remarks + ", noOfItemsPerPage="
				+ noOfItemsPerPage + ", users=" + users + ", searchString=" + searchString + ", bankDetails="
				+ bankDetails + ", paymentGateway=" + paymentGateway + ", adminBankDetails=" + adminBankDetails
				+ ", currencyId=" + currencyId + ", currency=" + currency + ", description=" + description + ", action="
				+ action + ", transactionFee=" + transactionFee + "]";
	}
	
	
}
