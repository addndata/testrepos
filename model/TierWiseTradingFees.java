package com.project.admin.model;

public class TierWiseTradingFees {
	private int id;
	private String name;
	private double takerFee;
	private double makerFee;
	private String action;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getTakerFee() {
		return takerFee;
	}
	public void setTakerFee(double takerFee) {
		this.takerFee = takerFee;
	}
	public double getMakerFee() {
		return makerFee;
	}
	public void setMakerFee(double makerFee) {
		this.makerFee = makerFee;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
}
