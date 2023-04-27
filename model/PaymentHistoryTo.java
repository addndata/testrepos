package com.project.admin.model;

public class PaymentHistoryTo {

	private String transactionId;
	private int customerId;
	private String toCustomer;
	private String orderID;
	private String debitFiatAmount;
	private String creditFiatAmount;
	private String debitBtcAmount;
	private String creditBtcAmount;
	private String fiatTransactionId;
	private String btcTransactionId;
	private String bccTransactionId;
	private String ethTransactionId;
	private String hcxTransactionId;
	private String closingBtcBalance;
	private String closingFiatBalance;
	private String transactionTimestamp;
	private String description;
	private String action;
	private String flag;
	private String fromDate;
	private String toDate;
	private String pageNo;
	private String txnCount;
	private String noTxn;

	private String txnCharge;
	private String chargeSGST;
	private String chargeCGST;
	private String mining_fees;
	private String currency;
	private String debitBccAmount;
	private String creditBccAmount;
	private String debitEthAmount;
	private String creditEthAmount;
	private String closingBccBalance;
	private String closingEthBalance;
	private String debitHcxAmount;
	private String creditHcxAmount;
	private String closingHcxBalance;

	private String fiatAmount;

	private String email;
	private String firstName;
	private String status;

	/** IEC Member **/
	private String iecTransactionId;
	private String debitIecAmount;
	private String creditIecAmount;
	private String closingIecBalance;
	/** LTC *///
	private String ltc_txnID;
	private String debitLtcAmount;
	private String creditLtcAmount;
	private String closingLtcBalance;

	public String getLtc_txnID() {
		return ltc_txnID;
	}

	public void setLtc_txnID(String ltc_txnID) {
		this.ltc_txnID = ltc_txnID;
	}

	public String getDebitLtcAmount() {
		return debitLtcAmount;
	}

	public void setDebitLtcAmount(String debitLtcAmount) {
		this.debitLtcAmount = debitLtcAmount;
	}

	public String getCreditLtcAmount() {
		return creditLtcAmount;
	}

	public void setCreditLtcAmount(String creditLtcAmount) {
		this.creditLtcAmount = creditLtcAmount;
	}

	public String getClosingLtcBalance() {
		return closingLtcBalance;
	}

	public void setClosingLtcBalance(String closingLtcBalance) {
		this.closingLtcBalance = closingLtcBalance;
	}

	/** DIAM Member **/
	private String diamTransactionId;
	private String debitDiamAmount;
	private String creditDiamAmount;
	private String closingDiamBalance;
	/** TRIGGERS Member **/
	private String triggersTransactionId;
	private String debittriggersAmount;
	private String credittriggersAmount;
	private String closingtriggersBalance;

	public String getTriggersTransactionId() {
		return triggersTransactionId;
	}

	public void setTriggersTransactionId(String triggersTransactionId) {
		this.triggersTransactionId = triggersTransactionId;
	}

	public String getDebittriggersAmount() {
		return debittriggersAmount;
	}

	public void setDebittriggersAmount(String debittriggersAmount) {
		this.debittriggersAmount = debittriggersAmount;
	}

	public String getCredittriggersAmount() {
		return credittriggersAmount;
	}

	public void setCredittriggersAmount(String credittriggersAmount) {
		this.credittriggersAmount = credittriggersAmount;
	}

	public String getClosingtriggersBalance() {
		return closingtriggersBalance;
	}

	public void setClosingtriggersBalance(String closingtriggersBalance) {
		this.closingtriggersBalance = closingtriggersBalance;
	}

	/** BSV Member **/
	private String bsvTransactionId;
	private String debitBsvAmount;
	private String creditBsvAmount;
	private String closingBsvBalance;

	/** Etc Member **/
	private String etcTransactionId;
	private String debitEtcAmount;
	private String creditEtcAmount;
	private String closingEtcBalance;
	/** Zec Member **/
	private String zecTransactionId;
	private String debitZecAmount;
	private String creditZecAmount;
	private String closingZecBalance;
	private String corporate_remit_id;

	public String getCorporate_remit_id() {
		return corporate_remit_id;
	}

	public void setCorporate_remit_id(String corporate_remit_id) {
		this.corporate_remit_id = corporate_remit_id;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public int getCustomerId() {
		return customerId;
	}

	public void setCustomerId(int i) {
		this.customerId = i;
	}

	public String getToCustomer() {
		return toCustomer;
	}

	public void setToCustomer(String toCustomer) {
		this.toCustomer = toCustomer;
	}

	public String getOrderID() {
		return orderID;
	}

	public void setOrderID(String orderID) {
		this.orderID = orderID;
	}

	public String getDebitFiatAmount() {
		return debitFiatAmount;
	}

	public void setDebitFiatAmount(String debitFiatAmount) {
		this.debitFiatAmount = debitFiatAmount;
	}

	public String getCreditFiatAmount() {
		return creditFiatAmount;
	}

	public void setCreditFiatAmount(String creditFiatAmount) {
		this.creditFiatAmount = creditFiatAmount;
	}

	public String getDebitBtcAmount() {
		return debitBtcAmount;
	}

	public void setDebitBtcAmount(String debitBtcAmount) {
		this.debitBtcAmount = debitBtcAmount;
	}

	public String getCreditBtcAmount() {
		return creditBtcAmount;
	}

	public void setCreditBtcAmount(String creditBtcAmount) {
		this.creditBtcAmount = creditBtcAmount;
	}

	public String getFiatTransactionId() {
		return fiatTransactionId;
	}

	public void setFiatTransactionId(String fiatTransactionId) {
		this.fiatTransactionId = fiatTransactionId;
	}

	public String getBtcTransactionId() {
		return btcTransactionId;
	}

	public void setBtcTransactionId(String btcTransactionId) {
		this.btcTransactionId = btcTransactionId;
	}

	public String getBccTransactionId() {
		return bccTransactionId;
	}

	public void setBccTransactionId(String bccTransactionId) {
		this.bccTransactionId = bccTransactionId;
	}

	public String getEthTransactionId() {
		return ethTransactionId;
	}

	public void setEthTransactionId(String ethTransactionId) {
		this.ethTransactionId = ethTransactionId;
	}

	public String getHcxTransactionId() {
		return hcxTransactionId;
	}

	public void setHcxTransactionId(String hcxTransactionId) {
		this.hcxTransactionId = hcxTransactionId;
	}

	public String getClosingBtcBalance() {
		return closingBtcBalance;
	}

	public void setClosingBtcBalance(String closingBtcBalance) {
		this.closingBtcBalance = closingBtcBalance;
	}

	public String getClosingFiatBalance() {
		return closingFiatBalance;
	}

	public void setClosingFiatBalance(String closingFiatBalance) {
		this.closingFiatBalance = closingFiatBalance;
	}

	public String getTransactionTimestamp() {
		return transactionTimestamp;
	}

	public void setTransactionTimestamp(String transactionTimestamp) {
		this.transactionTimestamp = transactionTimestamp;
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

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
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

	public String getPageNo() {
		return pageNo;
	}

	public void setPageNo(String pageNo) {
		this.pageNo = pageNo;
	}

	public String getTxnCount() {
		return txnCount;
	}

	public void setTxnCount(String txnCount) {
		this.txnCount = txnCount;
	}

	public String getNoTxn() {
		return noTxn;
	}

	public void setNoTxn(String noTxn) {
		this.noTxn = noTxn;
	}

	public String getTxnCharge() {
		return txnCharge;
	}

	public void setTxnCharge(String txnCharge) {
		this.txnCharge = txnCharge;
	}

	public String getChargeSGST() {
		return chargeSGST;
	}

	public void setChargeSGST(String chargeSGST) {
		this.chargeSGST = chargeSGST;
	}

	public String getChargeCGST() {
		return chargeCGST;
	}

	public void setChargeCGST(String chargeCGST) {
		this.chargeCGST = chargeCGST;
	}

	public String getMining_fees() {
		return mining_fees;
	}

	public void setMining_fees(String mining_fees) {
		this.mining_fees = mining_fees;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getDebitBccAmount() {
		return debitBccAmount;
	}

	public void setDebitBccAmount(String debitBccAmount) {
		this.debitBccAmount = debitBccAmount;
	}

	public String getCreditBccAmount() {
		return creditBccAmount;
	}

	public void setCreditBccAmount(String creditBccAmount) {
		this.creditBccAmount = creditBccAmount;
	}

	public String getDebitEthAmount() {
		return debitEthAmount;
	}

	public void setDebitEthAmount(String debitEthAmount) {
		this.debitEthAmount = debitEthAmount;
	}

	public String getCreditEthAmount() {
		return creditEthAmount;
	}

	public void setCreditEthAmount(String creditEthAmount) {
		this.creditEthAmount = creditEthAmount;
	}

	public String getClosingBccBalance() {
		return closingBccBalance;
	}

	public void setClosingBccBalance(String closingBccBalance) {
		this.closingBccBalance = closingBccBalance;
	}

	public String getClosingEthBalance() {
		return closingEthBalance;
	}

	public void setClosingEthBalance(String closingEthBalance) {
		this.closingEthBalance = closingEthBalance;
	}

	public String getDebitHcxAmount() {
		return debitHcxAmount;
	}

	public void setDebitHcxAmount(String debitHcxAmount) {
		this.debitHcxAmount = debitHcxAmount;
	}

	public String getCreditHcxAmount() {
		return creditHcxAmount;
	}

	public void setCreditHcxAmount(String creditHcxAmount) {
		this.creditHcxAmount = creditHcxAmount;
	}

	public String getClosingHcxBalance() {
		return closingHcxBalance;
	}

	public void setClosingHcxBalance(String closingHcxBalance) {
		this.closingHcxBalance = closingHcxBalance;
	}

	public String getFiatAmount() {
		return fiatAmount;
	}

	public void setFiatAmount(String fiatAmount) {
		this.fiatAmount = fiatAmount;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getIecTransactionId() {
		return iecTransactionId;
	}

	public void setIecTransactionId(String iecTransactionId) {
		this.iecTransactionId = iecTransactionId;
	}

	public String getDebitIecAmount() {
		return debitIecAmount;
	}

	public void setDebitIecAmount(String debitIecAmount) {
		this.debitIecAmount = debitIecAmount;
	}

	public String getCreditIecAmount() {
		return creditIecAmount;
	}

	public void setCreditIecAmount(String creditIecAmount) {
		this.creditIecAmount = creditIecAmount;
	}

	public String getClosingIecBalance() {
		return closingIecBalance;
	}

	public void setClosingIecBalance(String closingIecBalance) {
		this.closingIecBalance = closingIecBalance;
	}

	public String getDiamTransactionId() {
		return diamTransactionId;
	}

	public void setDiamTransactionId(String diamTransactionId) {
		this.diamTransactionId = diamTransactionId;
	}

	public String getDebitDiamAmount() {
		return debitDiamAmount;
	}

	public void setDebitDiamAmount(String debitDiamAmount) {
		this.debitDiamAmount = debitDiamAmount;
	}

	public String getCreditDiamAmount() {
		return creditDiamAmount;
	}

	public void setCreditDiamAmount(String creditDiamAmount) {
		this.creditDiamAmount = creditDiamAmount;
	}

	public String getClosingDiamBalance() {
		return closingDiamBalance;
	}

	public void setClosingDiamBalance(String closingDiamBalance) {
		this.closingDiamBalance = closingDiamBalance;
	}

	public String getBsvTransactionId() {
		return bsvTransactionId;
	}

	public void setBsvTransactionId(String bsvTransactionId) {
		this.bsvTransactionId = bsvTransactionId;
	}

	public String getDebitBsvAmount() {
		return debitBsvAmount;
	}

	public void setDebitBsvAmount(String debitBsvAmount) {
		this.debitBsvAmount = debitBsvAmount;
	}

	public String getCreditBsvAmount() {
		return creditBsvAmount;
	}

	public void setCreditBsvAmount(String creditBsvAmount) {
		this.creditBsvAmount = creditBsvAmount;
	}

	public String getClosingBsvBalance() {
		return closingBsvBalance;
	}

	public void setClosingBsvBalance(String closingBsvBalance) {
		this.closingBsvBalance = closingBsvBalance;
	}

	public String getEtcTransactionId() {
		return etcTransactionId;
	}

	public void setEtcTransactionId(String etcTransactionId) {
		this.etcTransactionId = etcTransactionId;
	}

	public String getDebitEtcAmount() {
		return debitEtcAmount;
	}

	public void setDebitEtcAmount(String debitEtcAmount) {
		this.debitEtcAmount = debitEtcAmount;
	}

	public String getCreditEtcAmount() {
		return creditEtcAmount;
	}

	public void setCreditEtcAmount(String creditEtcAmount) {
		this.creditEtcAmount = creditEtcAmount;
	}

	public String getClosingEtcBalance() {
		return closingEtcBalance;
	}

	public void setClosingEtcBalance(String closingEtcBalance) {
		this.closingEtcBalance = closingEtcBalance;
	}

	public String getZecTransactionId() {
		return zecTransactionId;
	}

	public void setZecTransactionId(String zecTransactionId) {
		this.zecTransactionId = zecTransactionId;
	}

	public String getDebitZecAmount() {
		return debitZecAmount;
	}

	public void setDebitZecAmount(String debitZecAmount) {
		this.debitZecAmount = debitZecAmount;
	}

	public String getCreditZecAmount() {
		return creditZecAmount;
	}

	public void setCreditZecAmount(String creditZecAmount) {
		this.creditZecAmount = creditZecAmount;
	}

	public String getClosingZecBalance() {
		return closingZecBalance;
	}

	public void setClosingZecBalance(String closingZecBalance) {
		this.closingZecBalance = closingZecBalance;
	}

}
