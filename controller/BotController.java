package com.project.admin.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.admin.model.BotResponse;
import com.project.admin.model.RequestJson;
import com.project.admin.model.UserBalance;
import com.project.admin.service.BotService;



@RestController
@RequestMapping("/rest/bot")
public class BotController {

	@Autowired
	private BotService botService;
	
	@PostMapping(path = "/updatePriceSprade")
	public BotResponse updatePriceSprade(@RequestBody RequestJson requestJson) {
		return botService.updatePriceSprade(requestJson);
	}
	
	@GetMapping(path = "/getPriceSprade")
	public BotResponse getPriceSprade() {
		return botService.getPriceSprade();
	}
	
	@GetMapping(path = "/getBotAssetsList")
	public BotResponse getBotAssetsList() {
		return botService.getBotAssetsList();
	}
	
	@PostMapping(path = "/updateAmountMinMax")
	public BotResponse updateAmountMinMax(@RequestBody RequestJson requestJson) {
		return botService.updateAmountMinMax(requestJson);
	}
	
	@PostMapping(path = "/resume")
	public BotResponse resume(@RequestBody RequestJson requestJson) {
		return botService.resume(requestJson);
	}
	
	@PostMapping(path = "/pause")
	public BotResponse pause(@RequestBody RequestJson requestJson) {
		return botService.pause(requestJson);
	}
	
	@GetMapping(value = "/GetAllBotUsers")
	public Map getAllBotUsers() {
		return botService.getAllBotUsers();
	}
	
	@PostMapping("/GetUserBalance")
//	@PreAuthorize("#userBalance.customerId.toString() == authentication.name") 
	public Map getUserBalance(@RequestBody UserBalance userBalance) {
		return botService.getUserBalance(userBalance);
	}
	
	@PostMapping(value = "/UpdateUserBalance")
	public Map updateUserBalance(@RequestBody List<UserBalance> userBalance) {
		return botService.updateUserBalance(userBalance);
	}
}
