package com.project.admin.model;

import java.util.List;

public class TransactionResponse {

	private ErrorResponse error;
	private List<PaymentOrders> paymentOrdersListResult;
	private List<UserTransactions> userTransactionsListResult;
	private UserTransactions userTransactionsResult;
	private TrendResponse trendResponse;
	private int totalcount;
	private List<PaymentWithdrawal> paymentWithdrawalListResult;
	private List<NodeBalance> nodeBalanceResult;
	private String botServiceResult;
	private List<TransactionValidation> transactionValidationList;
	
	public ErrorResponse getError() {
		return error;
	}
	public void setError(ErrorResponse error) {
		this.error = error;
	}
	public List<PaymentOrders> getPaymentOrdersListResult() {
		return paymentOrdersListResult;
	}
	public void setPaymentOrdersListResult(List<PaymentOrders> paymentOrdersListResult) {
		this.paymentOrdersListResult = paymentOrdersListResult;
	}	
	public List<UserTransactions> getUserTransactionsListResult() {
		return userTransactionsListResult;
	}
	public void setUserTransactionsListResult(List<UserTransactions> userTransactionsListResult) {
		this.userTransactionsListResult = userTransactionsListResult;
	}	
	public UserTransactions getUserTransactionsResult() {
		return userTransactionsResult;
	}
	public void setUserTransactionsResult(UserTransactions userTransactionsResult) {
		this.userTransactionsResult = userTransactionsResult;
	}
	public TrendResponse getTrendResponse() {
		return trendResponse;
	}
	public void setTrendResponse(TrendResponse trendResponse) {
		this.trendResponse = trendResponse;
	}
	public int getTotalcount() {
		return totalcount;
	}
	public void setTotalcount(int totalcount) {
		this.totalcount = totalcount;
	}
	public List<PaymentWithdrawal> getPaymentWithdrawalListResult() {
		return paymentWithdrawalListResult;
	}
	public void setPaymentWithdrawalListResult(List<PaymentWithdrawal> paymentWithdrawalListResult) {
		this.paymentWithdrawalListResult = paymentWithdrawalListResult;
	}
	public List<NodeBalance> getNodeBalanceResult() {
		return nodeBalanceResult;
	}
	public void setNodeBalanceResult(List<NodeBalance> nodeBalanceResult) {
		this.nodeBalanceResult = nodeBalanceResult;
	}
	public String getBotServiceResult() {
		return botServiceResult;
	}
	public void setBotServiceResult(String botServiceResult) {
		this.botServiceResult = botServiceResult;
	}
	public List<TransactionValidation> getTransactionValidationList() {
		return transactionValidationList;
	}
	public void setTransactionValidationList(List<TransactionValidation> transactionValidationList) {
		this.transactionValidationList = transactionValidationList;
	}
}
