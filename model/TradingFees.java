package com.project.admin.model;

public class TradingFees {
	private int id;
	private double volumeFrom;
	private double volumeTo;
	private double takerFee;
	private double discountTakerFee;
	private double makerFee;
	private double discountMakerFee;
	private String updatedOn;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public double getVolumeFrom() {
		return volumeFrom;
	}
	public void setVolumeFrom(double volumeFrom) {
		this.volumeFrom = volumeFrom;
	}
	public double getVolumeTo() {
		return volumeTo;
	}
	public void setVolumeTo(double volumeTo) {
		this.volumeTo = volumeTo;
	}
	public double getTakerFee() {
		return takerFee;
	}
	public void setTakerFee(double takerFee) {
		this.takerFee = takerFee;
	}
	public double getDiscountTakerFee() {
		return discountTakerFee;
	}
	public void setDiscountTakerFee(double discountTakerFee) {
		this.discountTakerFee = discountTakerFee;
	}
	public double getMakerFee() {
		return makerFee;
	}
	public void setMakerFee(double makerFee) {
		this.makerFee = makerFee;
	}
	public double getDiscountMakerFee() {
		return discountMakerFee;
	}
	public void setDiscountMakerFee(double discountMakerFee) {
		this.discountMakerFee = discountMakerFee;
	}
	public String getUpdatedOn() {
		return updatedOn;
	}
	public void setUpdatedOn(String updatedOn) {
		this.updatedOn = updatedOn;
	}
}
