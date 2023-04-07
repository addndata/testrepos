package com.project.admin.model;

public class Ask {

	private int askId;
	private double askprice;
	private double startQty;
	private double currentQty;
	private String ask_timestamp;
	private String deletionFlag;
	private String deletion_timestamp;
	private int page_no;
	private int no_of_items_per_page;
	private String crypto_currency;
	
	public int getAskId() {
		return askId;
	}
	public void setAskId(int askId) {
		this.askId = askId;
	}
	public double getAskprice() {
		return askprice;
	}
	public void setAskprice(double askprice) {
		this.askprice = askprice;
	}
	public double getStartQty() {
		return startQty;
	}
	public void setStartQty(double startQty) {
		this.startQty = startQty;
	}
	public double getCurrentQty() {
		return currentQty;
	}
	public void setCurrentQty(double currentQty) {
		this.currentQty = currentQty;
	}
	public String getAsk_timestamp() {
		return ask_timestamp;
	}
	public void setAsk_timestamp(String ask_timestamp) {
		this.ask_timestamp = ask_timestamp;
	}
	public String getDeletionFlag() {
		return deletionFlag;
	}
	public void setDeletionFlag(String deletionFlag) {
		this.deletionFlag = deletionFlag;
	}
	public String getDeletion_timestamp() {
		return deletion_timestamp;
	}
	public void setDeletion_timestamp(String deletion_timestamp) {
		this.deletion_timestamp = deletion_timestamp;
	}
	public int getPage_no() {
		return page_no;
	}
	public void setPage_no(int page_no) {
		this.page_no = page_no;
	}
	public int getNo_of_items_per_page() {
		return no_of_items_per_page;
	}
	public void setNo_of_items_per_page(int no_of_items_per_page) {
		this.no_of_items_per_page = no_of_items_per_page;
	}
	public String getCrypto_currency() {
		return crypto_currency;
	}
	public void setCrypto_currency(String crypto_currency) {
		this.crypto_currency = crypto_currency;
	}
	
	
}
