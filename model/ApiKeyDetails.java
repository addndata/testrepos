package com.project.admin.model;

public class ApiKeyDetails {
	private int id;
	private int userId;
	private String firstName;
	private String email;
	private String keyName;
	private String apiKey;
	private String secretKey;
	private String ipAddress;
	private String action;
	private int isRead;
	private int isDeposit;
	private int isWithdraw;
	private int isTrade;
	private String createdOn;
	private String searchString;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getKeyName() {
		return keyName;
	}
	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}
	public String getApiKey() {
		return apiKey;
	}
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}
	public String getSecretKey() {
		return secretKey;
	}
	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public int getIsRead() {
		return isRead;
	}
	public void setIsRead(int isRead) {
		this.isRead = isRead;
	}
	public int getIsDeposit() {
		return isDeposit;
	}
	public void setIsDeposit(int isDeposit) {
		this.isDeposit = isDeposit;
	}
	public int getIsWithdraw() {
		return isWithdraw;
	}
	public void setIsWithdraw(int isWithdraw) {
		this.isWithdraw = isWithdraw;
	}
	public int getIsTrade() {
		return isTrade;
	}
	public void setIsTrade(int isTrade) {
		this.isTrade = isTrade;
	}
	public String getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}
	public String getSearchString() {
		return searchString;
	}
	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}
}
