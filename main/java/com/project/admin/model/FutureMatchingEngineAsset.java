package com.project.admin.model;

public class FutureMatchingEngineAsset {
	private int symbol;
	private String pair_name;
	private String contract_type;
	private String expiry_date;
	public int getSymbol() {
		return symbol;
	}
	public void setSymbol(int symbol) {
		this.symbol = symbol;
	}
	public String getPair_name() {
		return pair_name;
	}
	public void setPair_name(String pair_name) {
		this.pair_name = pair_name;
	}
	public String getContract_type() {
		return contract_type;
	}
	public void setContract_type(String contract_type) {
		this.contract_type = contract_type;
	}
	public String getExpiry_date() {
		return expiry_date;
	}
	public void setExpiry_date(String expiry_date) {
		this.expiry_date = expiry_date;
	}
}
