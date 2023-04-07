package com.project.admin.service;

import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.project.admin.dao.BotDao;
import com.project.admin.model.BotResponse;
import com.project.admin.model.ErrorResponse;
import com.project.admin.model.RequestJson;
import com.project.admin.model.UserBalance;


@Service
public class BotService {

	@Autowired
	private BotDao botDao;
	
	@Autowired
	Environment env;
	
	public BotResponse updatePriceSprade(RequestJson requestJson) {
		if(requestJson.getSpradeValue()!=0 ){
			return botDao.updateSprade(requestJson);
		}else{
			return new BotResponse(new ErrorResponse(1));
		}
		
	}
	
	public BotResponse getPriceSprade() {
		return botDao.getSprade();
	}
	
	public BotResponse getBotAssetsList() {
		return botDao.getBotAssetList();
	}
	
	public BotResponse updateAmountMinMax(RequestJson requestJson) {
		if(requestJson.getBidMin()!=null && !requestJson.getBidMin().isEmpty() && requestJson.getBidMax()!=null && 
				!requestJson.getBidMax().isEmpty() && requestJson.getAskMin()!=null && !requestJson.getAskMin().isEmpty()
				&& requestJson.getAskMax()!=null && !requestJson.getAskMax().isEmpty() && 
				requestJson.getBaseAsset()!=null && !requestJson.getBaseAsset().isEmpty() && requestJson.getCounterAsset()!=null
				&& !requestJson.getCounterAsset().isEmpty()){
			if(Double.parseDouble(requestJson.getBidMin())!=0 && Double.parseDouble(requestJson.getBidMax())!=0 
					&& Double.parseDouble(requestJson.getAskMax())!=0 && Double.parseDouble(requestJson.getAskMin())!=0){
				return botDao.updateBotMinMax(requestJson);
			} else {
				return new BotResponse(new ErrorResponse(10));
			}			
		}else{
			return new BotResponse(new ErrorResponse(1));
		}
		
	}

	public BotResponse resume(RequestJson requestJson) {
		BotResponse restApiResponse = null;
		if (requestJson.getBotName()!=null && !requestJson.getBotName().isEmpty() && requestJson.getBaseAsset()!=null && 
				!requestJson.getBaseAsset().isEmpty() && requestJson.getCounterAsset()!=null && !requestJson.getCounterAsset().isEmpty()) {
			restApiResponse = new BotResponse();
			RestTemplate restTemp = new RestTemplate();
			HttpHeaders header = new HttpHeaders();
			header.setContentType(MediaType.APPLICATION_JSON);
			JSONObject json = new JSONObject();
			String url = env.getProperty("project.paybito.admin.botcontrolapi") + "resume";
			System.out.println("Resume Url : " + url);
			try {
				json.put("name", requestJson.getBotName());
				json.put("baseAssetCode", requestJson.getBaseAsset());
				json.put("counterAssetCode", requestJson.getCounterAsset());
				HttpEntity<String> entity = new HttpEntity<String>(json.toString(), header);
				restApiResponse = restTemp.postForObject(url, entity, BotResponse.class);
			} catch (Exception e) {
				e.printStackTrace();
				restApiResponse = new BotResponse(new ErrorResponse(1,e.getMessage()));
			}
		} else{
			restApiResponse = new BotResponse(new ErrorResponse(1));
			}
		return restApiResponse;
	}

	public BotResponse pause(RequestJson requestJson) {
		BotResponse restApiResponse = null;
		if (requestJson.getBotName()!=null && !requestJson.getBotName().isEmpty() && requestJson.getBaseAsset()!=null && 
				!requestJson.getBaseAsset().isEmpty() && requestJson.getCounterAsset()!=null && !requestJson.getCounterAsset().isEmpty()) {
			restApiResponse = new BotResponse();
			RestTemplate restTemp = new RestTemplate();
			HttpHeaders header = new HttpHeaders();
			header.setContentType(MediaType.APPLICATION_JSON);
			JSONObject json = new JSONObject();
			String url = env.getProperty("project.paybito.admin.botcontrolapi") + "pause";
			System.out.println("Pause Url : " + url);
			try {
				json.put("name", requestJson.getBotName());
				json.put("baseAssetCode", requestJson.getBaseAsset());
				json.put("counterAssetCode", requestJson.getCounterAsset());
				HttpEntity<String> entity = new HttpEntity<String>(json.toString(), header);
				restApiResponse = restTemp.postForObject(url, entity, BotResponse.class);
			} catch (Exception e) {
				e.printStackTrace();
				restApiResponse = new BotResponse(new ErrorResponse(1,e.getMessage()));
			}
		} else{
			restApiResponse = new BotResponse(new ErrorResponse(1));
			}
		return restApiResponse;
	}
	
	public Map getAllBotUsers() {
		return botDao.getAllBotUsers();
	}

	public Map getUserBalance(UserBalance userBalance) {
		return botDao.getUserBalance(userBalance);
	}

	public Map updateUserBalance(List<UserBalance> userBalance) {
		return botDao.updateUserBalance(userBalance);
	}
}
