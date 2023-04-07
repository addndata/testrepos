package com.project.admin.model;

public class Bid {

	private int bidId;
	private double bidprice;
	private double startQty;
	private double currentQty;
	private String bid_timestamp;
	private String deletionFlag;
	private String deletion_timestamp;
	private int page_no;
	private int no_of_items_per_page;
	private String crypto_currency;
	
	public int getBidId() {
		return bidId;
	}
	public void setBidId(int bidId) {
		this.bidId = bidId;
	}
	public double getBidprice() {
		return bidprice;
	}
	public void setBidprice(double bidprice) {
		this.bidprice = bidprice;
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
	public String getBid_timestamp() {
		return bid_timestamp;
	}
	public void setBid_timestamp(String bid_timestamp) {
		this.bid_timestamp = bid_timestamp;
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
