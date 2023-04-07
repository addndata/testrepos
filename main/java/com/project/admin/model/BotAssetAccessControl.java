package com.project.admin.model;

public class BotAssetAccessControl {

	private int id;
	private String baseAsset;
	private String counterAsset;
	private String bidMin;
	private String bidMax;
	private String askMin;
	private String askMax;
	private String schedulerTime;
	private String assetStatus;
	private String botStatus;
	private String botIsActive;
	private String createdOn;
	private String updatedOn;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getBaseAsset() {
		return baseAsset;
	}
	public void setBaseAsset(String baseAsset) {
		this.baseAsset = baseAsset;
	}
	public String getCounterAsset() {
		return counterAsset;
	}
	public void setCounterAsset(String counterAsset) {
		this.counterAsset = counterAsset;
	}
	public String getBidMin() {
		return bidMin;
	}
	public void setBidMin(String bidMin) {
		this.bidMin = bidMin;
	}
	public String getBidMax() {
		return bidMax;
	}
	public void setBidMax(String bidMax) {
		this.bidMax = bidMax;
	}
	public String getAskMin() {
		return askMin;
	}
	public void setAskMin(String askMin) {
		this.askMin = askMin;
	}
	public String getAskMax() {
		return askMax;
	}
	public void setAskMax(String askMax) {
		this.askMax = askMax;
	}
	public String getSchedulerTime() {
		return schedulerTime;
	}
	public void setSchedulerTime(String schedulerTime) {
		this.schedulerTime = schedulerTime;
	}
	public String getAssetStatus() {
		return assetStatus;
	}
	public void setAssetStatus(String assetStatus) {
		this.assetStatus = assetStatus;
	}
	public String getBotStatus() {
		return botStatus;
	}
	public void setBotStatus(String botStatus) {
		this.botStatus = botStatus;
	}
	public String getBotIsActive() {
		return botIsActive;
	}
	public void setBotIsActive(String botIsActive) {
		this.botIsActive = botIsActive;
	}
	public String getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}
	public String getUpdatedOn() {
		return updatedOn;
	}
	public void setUpdatedOn(String updatedOn) {
		this.updatedOn = updatedOn;
	}
	
}
