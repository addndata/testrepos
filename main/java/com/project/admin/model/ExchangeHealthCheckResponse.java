package com.project.admin.model;

import java.util.List;

public class ExchangeHealthCheckResponse {

	private ErrorResponse Error;
	private SystemResponse systemResponse;
	private List<TomcatDetails> tomcatListResponse;
	private List<DbProcessDetails> dbProcessReponse;
	private List<OfferDetails>  offerListResponse;
	private List<AssetWiseOfferDetails>  assetWiseOffersList;
	private String hcNetActiveOffers;
	private long totalOffers;
	

	
	public ErrorResponse getError() {
		return Error;
	}

	public void setError(ErrorResponse error) {
		Error = error;
	}

	public SystemResponse getSystemResponse() {
		return systemResponse;
	}

	public void setSystemResponse(SystemResponse systemResponse) {
		this.systemResponse = systemResponse;
	}
	
	public List<TomcatDetails> getTomcatListResponse() {
		return tomcatListResponse;
	}

	public void setTomcatListResponse(List<TomcatDetails> tomcatListResponse) {
		this.tomcatListResponse = tomcatListResponse;
	}
	public List<DbProcessDetails> getDbProcessReponse() {
		return dbProcessReponse;
	}

	public void setDbProcessReponse(List<DbProcessDetails> dbProcessReponse) {
		this.dbProcessReponse = dbProcessReponse;
	}

	public List<OfferDetails> getOfferListResponse() {
		return offerListResponse;
	}

	public void setOfferListResponse(List<OfferDetails> offerListResponse) {
		this.offerListResponse = offerListResponse;
	}

	public ExchangeHealthCheckResponse(){
		
	}
	
	public ExchangeHealthCheckResponse(ErrorResponse error){
		this.setError(error);
	}

	public String getHcNetActiveOffers() {
		return hcNetActiveOffers;
	}

	public void setHcNetActiveOffers(String hcNetActiveOffers) {
		this.hcNetActiveOffers = hcNetActiveOffers;
	}

	public List<AssetWiseOfferDetails> getAssetWiseOffersList() {
		return assetWiseOffersList;
	}

	public void setAssetWiseOffersList(List<AssetWiseOfferDetails> assetWiseOffersList) {
		this.assetWiseOffersList = assetWiseOffersList;
	}

	public long getTotalOffers() {
		return totalOffers;
	}

	public void setTotalOffers(long totalOffers) {
		this.totalOffers = totalOffers;
	}
	
}
