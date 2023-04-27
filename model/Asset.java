package com.project.admin.model;

public class Asset {

	private String base_asset;
	private String counter_asset;
	private String max_amount;
	private String min_amount;
	private String max_price;
	private String min_price;
	private int txn_type;
	
	public String getBase_asset() {
		return base_asset;
	}
	public void setBase_asset(String base_asset) {
		this.base_asset = base_asset;
	}
	public String getCounter_asset() {
		return counter_asset;
	}
	public void setCounter_asset(String counter_asset) {
		this.counter_asset = counter_asset;
	}
	public String getMax_amount() {
		return max_amount;
	}
	public void setMax_amount(String max_amount) {
		this.max_amount = max_amount;
	}
	public String getMin_amount() {
		return min_amount;
	}
	public void setMin_amount(String min_amount) {
		this.min_amount = min_amount;
	}
	public String getMax_price() {
		return max_price;
	}
	public void setMax_price(String max_price) {
		this.max_price = max_price;
	}
	public String getMin_price() {
		return min_price;
	}
	public void setMin_price(String min_price) {
		this.min_price = min_price;
	}
	public int getTxn_type() {
		return txn_type;
	}
	public void setTxn_type(int txn_type) {
		this.txn_type = txn_type;
	}
	
	
	@Override
	public String toString() {
		return "Asset [base_asset=" + base_asset + ", counter_asset=" + counter_asset + ", max_amount=" + max_amount
				+ ", min_amount=" + min_amount + ", max_price=" + max_price + ", min_price=" + min_price +", txn_type="+ txn_type + "]";
	}
	
	
}
