package com.project.admin.model;

import java.util.List;

public class AskBidResponse {

	private ErrorResponse Error;
	private List<Ask> AskResult;
	private List<Bid> BidResult;
	private int totalcount;
	
	
	public ErrorResponse getError() {
		return Error;
	}
	public void setError(ErrorResponse error) {
		Error = error;
	}
	public List<Ask> getAskResult() {
		return AskResult;
	}
	public void setAskResult(List<Ask> askResult) {
		AskResult = askResult;
	}
	public List<Bid> getBidResult() {
		return BidResult;
	}
	public void setBidResult(List<Bid> bidResult) {
		BidResult = bidResult;
	}
	public int getTotalcount() {
		return totalcount;
	}
	public void setTotalcount(int totalcount) {
		this.totalcount = totalcount;
	}
	
	
	
}
