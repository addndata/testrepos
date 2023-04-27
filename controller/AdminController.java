package com.project.admin.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.project.admin.model.AdminUsersResponse;
import com.project.admin.model.AssetPairDetails;
import com.project.admin.model.CurrencyMaster;
import com.project.admin.model.CurrencyRate;
import com.project.admin.model.ExchangeHealthCheckResponse;
import com.project.admin.model.FranchiseRequestInput;
import com.project.admin.model.FuturesTradingFees;
import com.project.admin.model.Maintenance;
import com.project.admin.model.MarginCallOrLiquidityValue;
import com.project.admin.model.MiningFees;
import com.project.admin.model.TierWiseTradingFees;
import com.project.admin.model.TradingFees;
import com.project.admin.model.CurrencyWiseSetting;
import com.project.admin.model.Users;
import com.project.admin.service.AWSS3Service;
import com.project.admin.service.AdminService;
import com.project.admin.service.FileUploadService;

@RestController
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	AdminService adminService;
	@Autowired
	AWSS3Service aWSS3Service;
	@Autowired
	FileUploadService fileUploadService;

	@PostMapping(value = "/GetAllConfirmUsers")
	public AdminUsersResponse getAllConfirmUsers(@RequestBody Users users) {
		return adminService.getAllConfirmUsers(users);
	}

	@PostMapping(value = "/GetAllUnconfirmUsers")
	public AdminUsersResponse getAllUnconfirmUsers(@RequestBody Users users) {
		return adminService.getAllUnconfirmUsers(users);
	}

	@PostMapping(value = "/addUserInTierGroup")
	public AdminUsersResponse addUserInTierGroup(@RequestBody Users users) {
		return adminService.addUserInTierGroup(users);
	}
	
	@PostMapping(value = "/totalKycApproveUsersByName")
	public AdminUsersResponse totalKycApproveUsersByName(@RequestBody Users users) {
		return adminService.totalKycApproveUsersByName(users);
	}
	
	@PostMapping(value = "/GetUsersDetails")
	public AdminUsersResponse getUsersDetails(@RequestBody Users users) {
		return adminService.GetUsersDetails(users);
	}

	@PostMapping(value = "/GetUserBankDetails")
	public AdminUsersResponse getUserBankDetails(@RequestBody Users users) {
		return adminService.GetUserBankDetails(users);
	}

	@PostMapping(value = "/updateUserKycStatus")
	public AdminUsersResponse updateUserKycStatus(@RequestBody Users users) {
		return adminService.updateUserKycStatus(users);
	}
	
	@PostMapping(value = "/updateKycStatus")
	public AdminUsersResponse updateKycStatus(@RequestBody Users users) {
		return adminService.updateKycStatus(users);
	}

	@PostMapping(value = "/ApproveUserBankDetails")
	public AdminUsersResponse approveUserBankDetails(@RequestBody Users users) {
		return adminService.approveUserBankDetails(users);
	}

	@PostMapping(value = "/blockUserAccount")
	public AdminUsersResponse blockUserAccount(@RequestBody Users user) {
		return adminService.updateUserAccountStatus(user);
	}
	
	@PostMapping(value = "/unblockUserAccount")
	public AdminUsersResponse unblockUserAccount(@RequestBody Users user) {
		return adminService.updateUserAccountStatus(user);
	} 
	
	@PostMapping(value = "/RestrictBuy")
	public AdminUsersResponse restrictBuy(@RequestBody Users users) {
		return adminService.RestrictBuy(users);
	}

	@PostMapping(value = "/RestrictSell")
	public AdminUsersResponse restrictSell(@RequestBody Users users) {
		return adminService.RestrictSell(users);
	}

	@PostMapping(value = "/RestrictSend")
	public AdminUsersResponse restrictSend(@RequestBody Users users) {
		return adminService.RestrictSend(users);
	}

	@PostMapping(value = "/RestrictReceive")
	public AdminUsersResponse restrictReceive(@RequestBody Users users) {
		return adminService.RestrictReceive(users);
	}

	@RequestMapping(value = "/{uuid}/file/{file_name.+}", method = RequestMethod.GET)
	@ResponseBody
	public void getFile(@PathVariable("uuid") String uuid, @PathVariable("file_name.+") String fileName,
			HttpServletResponse response) throws IOException {
		File output_file = aWSS3Service.readFile(uuid, fileName);
		InputStreamResource iStreamResource = new InputStreamResource(new FileInputStream(output_file));
		org.apache.commons.io.IOUtils.copy(iStreamResource.getInputStream(), response.getOutputStream());
		response.setContentType(new MimetypesFileTypeMap().getContentType(output_file));
		response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
		response.setHeader("Content-Type", "image/jpeg");
		response.flushBuffer();

	}

	@RequestMapping(path = "/{uuid}/download/{file_name.+}", method = RequestMethod.GET)
	public ResponseEntity<?> downloadS3Image(@PathVariable("uuid") String uuid,
			@PathVariable("file_name.+") String fileName) throws IOException {
		return aWSS3Service.downloadFile(uuid,fileName);
	}

	@PostMapping(value = "/SendMailToUser")
	public AdminUsersResponse sendMailToUser(@RequestBody Users users) {
		return adminService.SendMailToUser(users);
	}

	@PostMapping(value = "/SendMailFromPaybitoSite")
	public AdminUsersResponse sendMailFromPaybitoSite(@RequestParam("user_email") String user_email,
			@RequestParam("email_cc") String email_cc, @RequestParam("email_content") String email_content,
			@RequestParam(value = "coin_logo", required = false) MultipartFile coin_logo,
			@RequestParam(value = "legal_docs", required = false) MultipartFile legal_docs,
			@RequestParam(name = "g_recaptcha_response") String recaptchaResponse) {
		return adminService.SendMailFromPaybitoSite(user_email, email_cc, email_content, coin_logo, legal_docs,
				recaptchaResponse);
	}

	/*
	 * @PostMapping(value = "/ChangeUserProfile") public AdminUsersResponse
	 * changeUserProfile(@RequestBody Users users) { return
	 * adminService.changeUserProfile(users); }
	 */

	// ============================================= Health Check Up  ===================================================

	@GetMapping(value = "/getMemoryAndSpaceDetails")
	public ExchangeHealthCheckResponse getMemoryAndSpaceDetails() {
		return adminService.getMemoryAndSpaceDetails();
	}
	
	@GetMapping(value = "/getTomcatDetails")
	public ExchangeHealthCheckResponse getTomcatDetails() {
		return adminService.getTomcatDetails();
	}
	
	@GetMapping(value = "/getDbProcessDetails")
	public ExchangeHealthCheckResponse getDbProcessDetails() {
		return adminService.getDbProcessDetails();
	}
	
	@GetMapping(value = "/getOfferDetails")
	public ExchangeHealthCheckResponse getOfferDetails() {
		return adminService.getOfferDetails();
	}
	
	@GetMapping(value = "/getfuturesOfferDetails")
	public ExchangeHealthCheckResponse getfuturesOfferDetails() {
		return adminService.getfuturesOfferDetails();
	}
	
	@GetMapping(value = "/getNetworkActiveOffers")
	public ExchangeHealthCheckResponse getNetworkActiveOffers() {
		return adminService.getNetworkActiveOffers();
	}
	
	@GetMapping(value = "/futuresNetworkActiveOffers")
	public ExchangeHealthCheckResponse futuresNetworkActiveOffers() {
		return adminService.futuresNetworkActiveOffers();
	}
	
	@PostMapping(value = "/exchangeMaintenance")
	public Map<String, Object> exchangeMaintenance(@RequestBody Maintenance maintenance) {
		return adminService.exchangeMaintenance(maintenance);
	}
	
	@PostMapping(value = "/getBlockDetails")
	public Map<String, Object> getBlockDetails() {
		return adminService.getBlockDetails();
	}
	
	// ============================================= Settings  ===================================================
	
	@GetMapping(value = "/tierWiseTradingFees")
	public Map<String, Object> tierWiseTradingFees() {
		return adminService.tierWiseTradingFees();
	}
	
	@PostMapping(value = "/updateTierWiseTradingFees")
	public Map<String, Object> updateTierWiseTradingFees(@RequestBody TierWiseTradingFees tierWiseTradingFees) {
		return adminService.updateTierWiseTradingFees(tierWiseTradingFees);
	}
	
	@GetMapping(value = "/volumeWiseTradingFees")
	public Map<String, Object> volumeWiseTradingFees() {
		return adminService.volumeWiseTradingFees();
	}

	@PostMapping(value = "/updateVolumeWiseTradingFees")
	public AdminUsersResponse updateVolumeWiseTradingFees(@RequestBody TradingFees tradingFees) {
		return adminService.updateVolumeWiseTradingFees(tradingFees);
	}
	
	@GetMapping(value = "/getAllCurrencyWiseSettingsData")
	public Map<String, Object> getAllCurrencyWiseSettingsData() {
		return adminService.getAllCurrencyWiseSettingsData();
	}
	
	@GetMapping(value = "/currencyWiseSettingsDetails")
	public Map<String, Object> currencyWiseSettingsDetails() {
		return adminService.getAllCurrencyWiseSettingsData();
	}
	
	@GetMapping(value = "/currencyWiseMiningFees")
	public Map<String, Object> currencyWiseMiningFees() {
		return adminService.currencyWiseMiningFees();
	}
	
	@GetMapping(value = "/miningFeesDetails")
	public Map<String, Object> miningFeesDetails() {
		return adminService.currencyWiseMiningFees();
	}
	
	@GetMapping(value = "/settingsDataFiatConvertion")
	public Map<String, Object> settingsDataFiatConvertion() {
		return adminService.settingsDataFiatConvertion();
	}
	
	@PostMapping(value = "/updateCurrencyWiseSettingsData")
	public AdminUsersResponse updateCurrencyWiseSettingsData(@RequestBody CurrencyWiseSetting currencyWiseSetting) {
		return adminService.updateCurrencyWiseSettingsData(currencyWiseSetting);
	}
	
	@PostMapping(value = "/updateMiningFees")
	public AdminUsersResponse updateMiningFees(@RequestBody MiningFees miningFees) {
		return adminService.updateMiningFees(miningFees);
	}
	
	@GetMapping(value = "/getFiatCurrencyRate")
	public Map<String, Object> getFiatCurrencyRate() {
		return adminService.getFiatCurrencyRate();
	}
	
	@PostMapping(value = "/updateFiatCurrencyRate")
	public Map<String, Object> updateFiatCurrencyRate(@RequestBody CurrencyRate currencyRate) {
		return adminService.updateFiatCurrencyRate(currencyRate);
	}
	
	@GetMapping(value = "/getAllCurrencyByType")
	public Map<String, Object> getAllCurrencyByType() {
		return adminService.getAllCurrencyByType();
	}
	
	@GetMapping(value = "/getAllCurrency")
	public Map<String, Object> getAllCurrency() {
		return adminService.getAllCurrency();
	}
	
	@GetMapping(value = "/getWalletOrder")
	public Map<String, Object> getWalletOrder() {
		return adminService.getWalletOrder();
	}
	
	@PostMapping(value = "/addCurrency")
	public Map<String, Object> addCurrency(@RequestBody CurrencyMaster currencyMaster) {
		return adminService.addCurrency(currencyMaster);
	}
	
	@PostMapping(value = "/updateCurrency")
	public Map<String, Object> updateCurrency(@RequestBody CurrencyMaster currencyMaster) {
		return adminService.updateCurrency(currencyMaster);
	}
	
	@GetMapping(value = "/getAssetPairDetails")
	public Map<String, Object> getAssetPairDetails() {
		return adminService.getAssetPairDetails();
	}
	
	@GetMapping(value = "/getAssetOrder")
	public Map<String, Object> getAssetOrder(@RequestParam(required = true) int baseCurrencyId) {
		return adminService.getAssetOrder(baseCurrencyId);
	}
	
	@PostMapping(value = "/addAssetPair")
	public Map<String, Object> addAssetPair(@RequestBody AssetPairDetails assetPairDetails) {
		return adminService.addAssetPair(assetPairDetails);
	}
	
	@PostMapping(value = "/updateAssetPair")
	public Map<String, Object> updateAssetPair(@RequestBody AssetPairDetails assetPairDetails) {
		return adminService.updateAssetPair(assetPairDetails);
	}
	
	@GetMapping(value = "/getMachingEngineAssetPair")
	public Map<String, Object> getAsset() {
		return adminService.getMachingEngineAssetPair();
	}
	
	@PostMapping(value = "/addMachingEngineAssetPair")
	public Map<String, Object> addMachingEngineAssetPair(@RequestBody AssetPairDetails assetPairDetails) {
		return adminService.addMachingEngineAssetPair(assetPairDetails);
	}
	
	/*Futures Asset Automation*/
	@GetMapping(value = "/getFuturesContractType")
	public Map<String, Object> getFuturesContractType() {
		return adminService.getFuturesContractType();
	}
	
	@GetMapping(value = "/getFuturesAssetPairDetails")
	public Map<String, Object> getFuturesAssetPairDetails() {
		return adminService.getFuturesAssetPairDetails();
	}
	
	@PostMapping(value = "/addFuturesAssetPair")
	public Map<String, Object> addFuturesAssetPair(@RequestBody List<AssetPairDetails> assetPairDetails) {
		System.out.println("addFuturesAssetPair controller called.");
		return adminService.addFuturesAssetPair(assetPairDetails);
	}
	
	@PostMapping(value = "/updateFuturesAssetPair")
	public Map<String, Object> updateFuturesAssetPair(@RequestBody List<AssetPairDetails> assetPairDetails) {
		return adminService.updateFuturesAssetPair(assetPairDetails);
	}
	
	@GetMapping(value = "/getFuturesMachingEngineAssetPair")
	public Map<String, Object> getFuturesMachingEngineAssetPair() {
		return adminService.getFuturesMachingEngineAssetPair();
	}
	
	@PostMapping(value = "/addFuturesMachingEngineAssetPair")
	public Map<String, Object> addFuturesMachingEngineAssetPair(@RequestBody List<AssetPairDetails> assetPairDetails) {
		return adminService.addFuturesMachingEngineAssetPair(assetPairDetails);
	}
	
	@GetMapping(value = "/marginCallOrAutoliquidity")
	public Map<String, Object> marginCallOrAutoliquidity() {
		return adminService.marginCallOrAutoliquidity();
	}
	
	@PostMapping(value = "/updateMarginCallOrAutoliquidity")
	public Map<String, Object> updateMarginCallOrAutoliquidity(@RequestBody MarginCallOrLiquidityValue marginCallOrLiquidityValue) {
		return adminService.updateMarginCallOrAutoliquidity(marginCallOrLiquidityValue);
	}
	
	@GetMapping(value = "/futuresTradingFees")
	public Map<String, Object> futuresTradingFees() {
		return adminService.futuresTradingFees();
	}
	
	@PostMapping(value = "/updateFuturesTradingFees")
	public Map<String, Object> updateFuturesTradingFees(@RequestBody FuturesTradingFees futuresTradingFees) {
		return adminService.updateFuturesTradingFees(futuresTradingFees);
	}
	
	//================================================  Franchise Promotion ================================================

	@PostMapping(value = "/franchiseRegistration")
	public AdminUsersResponse franchiseRegistration(@RequestBody FranchiseRequestInput franchiseRequestInput) {
		return adminService.franchiseRegistration(franchiseRequestInput);
	}
	
	@GetMapping(value = "/getAllFranchise")
	public AdminUsersResponse getAllFranchise() {
		return adminService.getAllFranchise();
	}
	
	@PostMapping(value = "/franchiseById")
	public AdminUsersResponse franchiseById(@RequestBody FranchiseRequestInput franchiseRequestInput) {
		return adminService.franchiseById(franchiseRequestInput);
	}
	
	@PostMapping(value = "/updateFranchise")
	public AdminUsersResponse updateFranchise(@RequestBody FranchiseRequestInput franchiseRequestInput) {
		return adminService.updateFranchise(franchiseRequestInput);
	}
	
	@PostMapping(value = "/earningByFranchiseUsers")
	public Map<String, Object> earningByFranchiseUsers(@RequestBody FranchiseRequestInput franchiseRequestInput) {
		return adminService.earningByFranchiseUsers(franchiseRequestInput);
	}
	
	@PostMapping(value = "/totalEarningByDateRange")
	public Map<String, Object> totalEarningByDateRange(@RequestBody FranchiseRequestInput franchiseRequestInput) {
		return adminService.totalEarningByDateRange(franchiseRequestInput);
	}
	
	@PostMapping(value = "/dayWiseEarning")
	public Map<String, Object> dayWiseEarning(@RequestBody FranchiseRequestInput franchiseRequestInput) {
		return adminService.dayWiseEarning(franchiseRequestInput);
	}
	//================================================  Show all refer user ================================================

	@GetMapping(value = "/allReferralUser")
	public Map<String, Object> allReferralUser() {
		return adminService.allReferralUser();
	}
	
	//================================================  Show all user api key details ================================================
	
	@GetMapping(value = "/allUsersApiKeyDetails")
	public Map<String, Object> allUsersApiKeyDetails(@RequestParam int pageNo,@RequestParam int noOfRows, 
			@RequestParam String searchString) {
		return adminService.allUsersApiKeyDetails(pageNo,noOfRows,searchString);
	}
	
	@PostMapping(value = "/updateUserDetails")
	public Map<String, Object> updateUserDetails(@RequestBody Users user) {
		return adminService.updateUserDetails(user);
	}
	
	/* Futures all asset pair details */
	@GetMapping(value = "/futuresAllAssetPair")
	public Map<String, Object> futuresAllAssetPair() {
		return adminService.futuresAllAssetPair();
	}
}
