package com.project.admin.model;

import java.util.List;

public class TrendResponse {

	private List<BtcTrendChart> chartData;
	private String low;
	private String high;
	
	public List<BtcTrendChart> getChartData() {
		return chartData;
	}
	public void setChartData(List<BtcTrendChart> chartData) {
		this.chartData = chartData;
	}
	public String getLow() {
		return low;
	}
	public void setLow(String low) {
		this.low = low;
	}
	public String getHigh() {
		return high;
	}
	public void setHigh(String high) {
		this.high = high;
	}
	
	
}
