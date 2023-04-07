package com.project.admin.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.admin.model.Asset;
import com.project.admin.model.ColdWalletBalance;
import com.project.admin.model.CurrencyMaster;
import com.project.admin.model.PaymentOrders;
import com.project.admin.model.PaymentWithdrawal;
import com.project.admin.model.Remittance;
import com.project.admin.model.ReportRequest;
import com.project.admin.model.TransactionResponse;
import com.project.admin.model.TransactionValidation;
import com.project.admin.model.UserTransactions;
import com.project.admin.service.FileUploadService;
import com.project.admin.service.TransactionsService;

@RestController
@RequestMapping("/transactions")
public class TransactionsController {

	@Autowired
	TransactionsService transactionsService;

	@Autowired
	FileUploadService fileUploadService;

	@PostMapping(value = "/UpdateReferenceNo")
	public TransactionResponse updateReferenceNo(@RequestBody PaymentOrders paymentOrders) {
		return transactionsService.UpdateReferenceNo(paymentOrders);
	}

	@PostMapping(value = "/GetPaymentOrderList")
	public TransactionResponse getPaymentOrderList(@RequestBody PaymentOrders paymentOrders) {
		return transactionsService.GetPaymentOrderList(paymentOrders);
	}

	@PostMapping(value = "/GetCustomerTransactionsList")
	public TransactionResponse getCustomerTransactionsList(@RequestBody UserTransactions userTransactions) {
		return transactionsService.GetCustomerTransactionsList(userTransactions);
	}

	@PostMapping(value = "/GetCustomerAllBuySell")
	public TransactionResponse getCustomerAllBuySell(@RequestBody UserTransactions userTransactions) {
		return transactionsService.GetCustomerAllBuySell(userTransactions);
	}

	@GetMapping(value = "/GetPaybitoBalance")
	public TransactionResponse getPaybitoBalance() {
		return transactionsService.getPaybitoBalance();
	}
	
	@PostMapping(value = "/GetCustomerAllWithdrawls")
	public TransactionResponse getCustomerAllWithdrawls(@RequestBody UserTransactions userTransactions) {
		return transactionsService.GetCustomerAllWithdrawls(userTransactions);
	}

	@PostMapping(value = "/ApprovePendingWithdrawl")
	public TransactionResponse approvePendingWithdrawl(@RequestBody PaymentWithdrawal paymentWithdrawal) {
		return transactionsService.ApprovePendingWithdrawl(paymentWithdrawal);
	}

	@PostMapping(value = "/DeclinePendingWithdrawl")
	public TransactionResponse declinePendingWithdrawl(@RequestBody PaymentWithdrawal paymentWithdrawal) {
		return transactionsService.declinePendingWithdrawl(paymentWithdrawal);
	}

	@PostMapping(value = "/GetAllSendReceive")
	public TransactionResponse getAllSendReceive(@RequestBody UserTransactions userTransactions) {
		return transactionsService.GetAllSendReceive(userTransactions);
	}

	// bot control service
	@PostMapping(value = "/BuySellAsset")
	public TransactionResponse buySellAsset(@RequestBody Asset asset) {
		return transactionsService.BuySellAsset(asset);
	}

	// bot control service
	@PostMapping(value = "/BuySellAssetDetails")
	public TransactionResponse buySellAssetDetails(@RequestBody Asset asset) {
		return transactionsService.BuySellAssetDetails(asset);
	}

	@PostMapping(value = "/GetAllSendPending")
	public TransactionResponse getAllSendPending() {
		return transactionsService.getAllSendPending();
	}

	@PostMapping(value = "/ReviewSendRequest")
	public TransactionResponse reviewSendRequest(@RequestBody TransactionValidation transactionValidation) {
		return transactionsService.reviewSendRequest(transactionValidation);
	}
	
	@PostMapping("/getCryptoAddress")
	public Map getCryptoAddress(@RequestBody TransactionValidation requestInput) {
		return transactionsService.getCryptoAddress(requestInput);
	}

	@PostMapping(value = "/sendToOther")
	public TransactionResponse sendToOther(@RequestBody TransactionValidation transactionValidation) {
		return transactionsService.sendToOther(transactionValidation);
	}
	
	@PostMapping(value = "/ExchangeReport")
	public Map exchangeReport(@RequestBody ReportRequest reportRequest) {
		return transactionsService.exchangeReport(reportRequest);
	}
	
	/* For Futures Exchange Report drop down menu*/ 
	@GetMapping(value = "/getAllFuturesExchangeReport")
	public Map<String, Object> getAllFuturesExchangeReport() {
		return transactionsService.getAllFuturesExchangeReport();
	}

	/* For Futures Exchange Report*/
	@PostMapping(value = "/futuresExchangeReport")
	public Map<String, Object> futuresExchangeReport(@RequestBody ReportRequest reportRequest) {
		return transactionsService.futuresExchangeReport(reportRequest);
	}
	
	/* For Futures User Report drop down menu*/ 
	@GetMapping(value = "/getAllFuturesUserReport")
	public Map<String, Object> getAllFuturesUserReport() {
		return transactionsService.getAllFuturesUserReport();
	}
	
	@GetMapping(value = "/futuresBalanceComparison")
	public Map<String, Object> futuresBalanceComparison() {
		return transactionsService.futuresBalanceComparison();
	}
	
	@GetMapping(value = "/getAssetWiseBalanceComparison")
	public Map<String, Object> getAssetWiseBalanceComparison() {
		return transactionsService.getAssetWiseBalanceComparison();
	}
	
	@GetMapping(value = "/SendAssetBalanceComparison")
	public Map sendAssetBalanceComparison() {
		return transactionsService.sendAssetBalanceComparison();
	}

	@PostMapping(value = "/GetAdminBankDetails")
	public Map getAdminBankDetails() {
		return transactionsService.getAdminBankDetails();
	}

	@GetMapping(value = "/getColdWalletBalance")
	public Map<String, Object> getColdWalletBalance() {
		return transactionsService.getColdWalletBalance();
	}

	@PostMapping(value = "/updateColdWalletBalance")
	public Map<String, Object> updateColdWalletBalance(@RequestBody ColdWalletBalance coldWalletBalance) {
		return transactionsService.updateColdWalletBalance(coldWalletBalance);
	}

	@GetMapping(value = "/GetColdWalletAddress")
	public Map getColdWalletAddress() {
		return transactionsService.getColdWalletAddress();
	}
	
	@PostMapping(value = "/addRemittance")
	public Map<String, Object> addRemittance(@RequestBody Remittance remittance) {
		return transactionsService.addRemittance(remittance);
	}
	
	@GetMapping(value = "/showAllRemittance")
	public Map showAllRemittance() {
		return transactionsService.showAllRemittance();
	}

	// ================================================ Other company coin related  ================================================

	@PostMapping(value = "/coinListingByPaybito")
	public Map coinListingByPaybito(@RequestBody ReportRequest reportRequest) {
		return transactionsService.coinListingByPaybito(reportRequest);
	}

	@PostMapping(value = "/coinLiabilityReport")
	public Map coinLiabilityReport(@RequestBody ReportRequest reportRequest) {
		return transactionsService.coinLiabilityReport(reportRequest);
	}
	
}
