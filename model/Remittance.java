package com.project.admin.model;

public class Remittance {
	private int id;
	private String remittanceDate;
	private double remittanceOutInr;
	private double inrFee;
	private double remittanceInUsd;
	private double usdFee;
	private String action;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getRemittanceDate() {
		return remittanceDate;
	}
	public void setRemittanceDate(String remittanceDate) {
		this.remittanceDate = remittanceDate;
	}
	public double getRemittanceOutInr() {
		return remittanceOutInr;
	}
	public void setRemittanceOutInr(double remittanceOutInr) {
		this.remittanceOutInr = remittanceOutInr;
	}
	public double getInrFee() {
		return inrFee;
	}
	public void setInrFee(double inrFee) {
		this.inrFee = inrFee;
	}
	public double getRemittanceInUsd() {
		return remittanceInUsd;
	}
	public void setRemittanceInUsd(double remittanceInUsd) {
		this.remittanceInUsd = remittanceInUsd;
	}
	public double getUsdFee() {
		return usdFee;
	}
	public void setUsdFee(double usdFee) {
		this.usdFee = usdFee;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
}
