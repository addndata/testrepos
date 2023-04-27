package com.project.admin.model;

import java.util.List;

public class BotResponse {

	private ErrorResponse errorResponse;
	private List<BotAssetAccessControl> botAssetList;
	private BotControl botControl;
	private int totalCount;

	public ErrorResponse getErrorResponse() {
		return errorResponse;
	}

	public void setErrorResponse(ErrorResponse errorResponse) {
		this.errorResponse = errorResponse;
	}

	public List<BotAssetAccessControl> getBotAssetList() {
		return botAssetList;
	}

	public void setBotAssetList(List<BotAssetAccessControl> botAssetList) {
		this.botAssetList = botAssetList;
	}

	public BotControl getBotControl() {
		return botControl;
	}

	public void setBotControl(BotControl botControl) {
		this.botControl = botControl;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public BotResponse() {

	}

	public BotResponse(ErrorResponse errorResponse) {
		this.errorResponse = errorResponse;
	}
}
