package com.project.admin.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.admin.dao.TransactionsDao;
import com.project.admin.model.Asset;
import com.project.admin.model.ColdWalletBalance;
import com.project.admin.model.ErrorResponse;
import com.project.admin.model.PaymentOrders;
import com.project.admin.model.PaymentWithdrawal;
import com.project.admin.model.Remittance;
import com.project.admin.model.ReportRequest;
import com.project.admin.model.TransactionResponse;
import com.project.admin.model.TransactionValidation;
import com.project.admin.model.UserTransactions;

@Service
public class TransactionsService {

	@Autowired
	TransactionsDao transactionsDao;

	ErrorResponse errorResponse = null;
	TransactionResponse transactionResponse = null;

	public TransactionResponse UpdateReferenceNo(PaymentOrders paymentOrders) {

		transactionResponse = new TransactionResponse();
		errorResponse = new ErrorResponse();

		if (paymentOrders.getOrderId() != 0 && paymentOrders.getUserId() != 0
				&& paymentOrders.getReferenceNo() != null && !paymentOrders.getReferenceNo().isEmpty()) {
			transactionResponse = transactionsDao.updateReferenceNo(paymentOrders);
		} else {
			errorResponse = errorResponse.GetErrorSet(1);
			transactionResponse.setError(errorResponse);
		}
		return transactionResponse;
	}

	public TransactionResponse GetPaymentOrderList(PaymentOrders paymentOrders) {

		transactionResponse = new TransactionResponse();
		errorResponse = new ErrorResponse();
		if (paymentOrders.getPaymentGateway() != null && paymentOrders.getSearchString() != null) {
			transactionResponse = transactionsDao.getPaymentOrderList(paymentOrders);
		} else {
			errorResponse = errorResponse.GetErrorSet(1);
			transactionResponse.setError(errorResponse);
		}

		return transactionResponse;
	}

	public TransactionResponse GetCustomerTransactionsList(UserTransactions userTransactions) {

		transactionResponse = new TransactionResponse();
		errorResponse = new ErrorResponse();

		if (userTransactions.getCustomerId() != 0) {
			transactionResponse = transactionsDao.getCustomerListTrans(userTransactions);
		} else {
			errorResponse = errorResponse.GetErrorSet(1);
			transactionResponse.setError(errorResponse);
		}
		return transactionResponse;
	}

	public TransactionResponse GetCustomerAllBuySell(UserTransactions userTransactions) {

		transactionResponse = new TransactionResponse();
		errorResponse = new ErrorResponse();

		transactionResponse = transactionsDao.getallBuySell(userTransactions);

		return transactionResponse;
	}

	public TransactionResponse getPaybitoBalance() {

		transactionResponse = new TransactionResponse();
		errorResponse = new ErrorResponse();

		transactionResponse = transactionsDao.getPaybitoBalance();

		return transactionResponse;
	}
	
	public TransactionResponse GetCustomerAllWithdrawls(UserTransactions userTransactions) {

		transactionResponse = new TransactionResponse();
		errorResponse = new ErrorResponse();

		transactionResponse = transactionsDao.getCustomerAllWithdrawls(userTransactions);

		return transactionResponse;
	}

	public TransactionResponse ApprovePendingWithdrawl(PaymentWithdrawal paymentWithdrawal) {

		transactionResponse = new TransactionResponse();
		errorResponse = new ErrorResponse();

		if (paymentWithdrawal.getCustomerId() != null && paymentWithdrawal.getCustomerId() != ""
				&& paymentWithdrawal.getWithdrawalId() != null && !paymentWithdrawal.getWithdrawalId().isEmpty()
				&& paymentWithdrawal.getCreditAmount() != null && !paymentWithdrawal.getCreditAmount().isEmpty()
				&& paymentWithdrawal.getDescription() != "" && paymentWithdrawal.getDescription() != null
				&& paymentWithdrawal.getAdminBankId()!= 0) {
			transactionResponse = transactionsDao.approvePendingWithdrawl(paymentWithdrawal);
		} else {
			errorResponse = errorResponse.GetErrorSet(1);
			transactionResponse.setError(errorResponse);
		}
		return transactionResponse;
	}

	public TransactionResponse GetAllSendReceive(UserTransactions userTransactions) {

		transactionResponse = new TransactionResponse();
		errorResponse = new ErrorResponse();
		transactionResponse = transactionsDao.getallSendReceive(userTransactions);
		return transactionResponse;
	}

	public TransactionResponse declinePendingWithdrawl(PaymentWithdrawal paymentWithdrawal) {
		transactionResponse = new TransactionResponse();
		errorResponse = new ErrorResponse();
		if (paymentWithdrawal.getCustomerId() != null && paymentWithdrawal.getCustomerId() != "" 
				&& Integer.valueOf(paymentWithdrawal.getWithdrawalId()) != 0) {
			transactionResponse = transactionsDao.declinePendingWithdrawl(paymentWithdrawal);
		} else {
			errorResponse = errorResponse.GetErrorSet(1);
			transactionResponse.setError(errorResponse);
		}
		return transactionResponse;
	}
	
	public TransactionResponse BuySellAsset(Asset asset) {

		transactionResponse = new TransactionResponse();
		errorResponse = new ErrorResponse();
		if (asset.getBase_asset() != null && !asset.getBase_asset().isEmpty() && asset.getCounter_asset() != null
				&& !asset.getCounter_asset().isEmpty() && asset.getMax_amount() != null
				&& !asset.getMax_amount().isEmpty() && asset.getMin_amount() != null && !asset.getMin_amount().isEmpty()
				&& asset.getMax_price() != null && !asset.getMax_price().isEmpty() && asset.getMin_price() != null
				&& !asset.getMin_price().isEmpty() && asset.getTxn_type() != 0) {
			transactionResponse = transactionsDao.buySellAsset(asset);
		} else {
			errorResponse = errorResponse.GetErrorSet(1);
			transactionResponse.setError(errorResponse);
		}
		return transactionResponse;
	}

	public TransactionResponse BuySellAssetDetails(Asset asset) {

		transactionResponse = new TransactionResponse();
		errorResponse = new ErrorResponse();
		if (asset.getTxn_type() != 0) {
			transactionResponse = transactionsDao.buySellAssetDetails(asset);
		} else {
			errorResponse = errorResponse.GetErrorSet(1);
			transactionResponse.setError(errorResponse);
		}
		return transactionResponse;
	}

	public TransactionResponse getAllSendPending() {
		return transactionsDao.getAllSendPending();
	}

	public TransactionResponse reviewSendRequest(TransactionValidation transactionValidation) {
		transactionResponse=new TransactionResponse();
 		errorResponse =new ErrorResponse();
 		if(transactionValidation.getId()!=0 && transactionValidation.getCustomerId() != 0 
 				&& transactionValidation.getAmount()!=0 && !transactionValidation.getAction().isEmpty()
 				&& !transactionValidation.getCurrency().isEmpty()) {
 		
 		    transactionResponse=transactionsDao.reviewSendRequest(transactionValidation);
 		} else	{
 			errorResponse=errorResponse.GetErrorSet(1);
 			transactionResponse.setError(errorResponse);
 		}
 		return transactionResponse;
	}
	
	public Map<String, Object> getCryptoAddress(TransactionValidation requestInput) {
		Map<String, Object> response = new HashMap<>();
		if (requestInput.getCustomerId() != 0 &&  requestInput.getCurrencyId() != 0) {
			response = transactionsDao.getCryptoAddress(requestInput);
		} else {
			errorResponse =new ErrorResponse();
			errorResponse=errorResponse.GetErrorSet(1);
			response.put("error", errorResponse);
		}
		return response;
	}
	
	public TransactionResponse sendToOther(TransactionValidation transactionValidation) {
		transactionResponse=new TransactionResponse();
 		errorResponse =new ErrorResponse();
 		if(transactionValidation.getAmount()!=0 && transactionValidation.getCurrencyId()!= 0) {
 		    transactionResponse=transactionsDao.sendToOther(transactionValidation);
 		} else	{
 			errorResponse=errorResponse.GetErrorSet(1);
 			transactionResponse.setError(errorResponse);
 		}
 		return transactionResponse;
	}
	
	public Map<String, Object> exchangeReport(ReportRequest reportRequest) {
		Map<String, Object> response = new HashMap<>();
		if (reportRequest.getReportNumber()!= 0) {
			response = transactionsDao.exchangeReport(reportRequest);
		} else {
			errorResponse =new ErrorResponse(1);
			response.put("error", errorResponse);
		}
		return response;
	}
	
	public Map<String, Object> getAllFuturesExchangeReport() {
		return transactionsDao.getAllFuturesExchangeReport();
	}

	public Map<String, Object> futuresExchangeReport(ReportRequest reportRequest) {
		Map<String, Object> response = new HashMap<>();
		if (reportRequest.getReportNumber()!= 0) {
			response = transactionsDao.futuresExchangeReport(reportRequest);
		} else {
			errorResponse =new ErrorResponse(1);
			response.put("error", errorResponse);
		}
		return response;
	}
	
	public Map<String, Object> getAllFuturesUserReport() {
		return transactionsDao.getAllFuturesUserReport();
	}

	public Map<String, Object> futuresBalanceComparison() {
		return transactionsDao.futuresBalanceComparison();
	}
	
	public Map<String, Object> getAssetWiseBalanceComparison() {
		return transactionsDao.getAssetWiseBalanceComparison();
	}
	
	public Map<String, Object> sendAssetBalanceComparison() {
		return transactionsDao.sendAssetBalanceComparison();
	}
	
	public Map<String, Object> getAdminBankDetails() {
		return transactionsDao.getAdminBankDetails();
	}

	public Map<String, Object> getColdWalletBalance() {
		return transactionsDao.getColdWalletBalance();
	}

	public Map<String, Object> updateColdWalletBalance(ColdWalletBalance coldWalletBalance) {
		Map<String, Object> response = new HashMap<>();
		if (coldWalletBalance.getCurrencyId()!= 0) {
			response = transactionsDao.updateColdWalletBalance(coldWalletBalance);
		} else {
			errorResponse =new ErrorResponse();
			errorResponse=errorResponse.GetErrorSet(1);
			response.put("error", errorResponse);
		}
		return response;
	}
	
	public Map getColdWalletAddress() {
		return transactionsDao.getColdWalletAddress();
	}
	
	public Map<String, Object> addRemittance(Remittance remittance) {
		return transactionsDao.addRemittance(remittance);
	}
	
	public Map showAllRemittance() {
		return transactionsDao.showAllRemittance();
	}
	
	public Map coinListingByPaybito(ReportRequest reportRequest) {
		return transactionsDao.coinListingByPaybito(reportRequest);
	}

	public Map coinLiabilityReport(ReportRequest reportRequest) {
		return transactionsDao.coinLiabilityReport(reportRequest);
	}

}
