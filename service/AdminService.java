package com.project.admin.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import com.project.admin.dao.AdminDao;
import com.project.admin.model.AdminUsersResponse;
import com.project.admin.model.ApiKeyDetails;
import com.project.admin.model.AssetPairDetails;
import com.project.admin.model.CurrencyMaster;
import com.project.admin.model.CurrencyRate;
import com.project.admin.model.ErrorResponse;
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

@Service
public class AdminService {

	@Autowired
	AdminDao adminDao;

	ErrorResponse errorResponse = null;
	AdminUsersResponse adminUsersResponse = null;

	public AdminUsersResponse getAllConfirmUsers(Users users) {
		return adminDao.getAllConfirmUsers(users);
	}

	public AdminUsersResponse getAllUnconfirmUsers(Users users) {
		return adminDao.getAllUnconfirmUsers(users);
	}
	
	public AdminUsersResponse addUserInTierGroup(Users users) {
		return adminDao.addUserInTierGroup(users);
	}
	
	public AdminUsersResponse totalKycApproveUsersByName(Users users) {
		adminUsersResponse = new AdminUsersResponse();
		errorResponse = new ErrorResponse();

		if (users.getFirstName() != null && !users.getFirstName().isEmpty()) {
			adminUsersResponse = adminDao.totalKycApproveUsersByName(users);
		} else {
			errorResponse = errorResponse.GetErrorSet(1);
			adminUsersResponse.setError(errorResponse);
		}
		return adminUsersResponse;
	}
	
	public AdminUsersResponse GetUsersDetails(Users users) {
		adminUsersResponse = new AdminUsersResponse();
		errorResponse = new ErrorResponse();

		if (users.getUser_id() != 0) {
			adminUsersResponse = adminDao.getUsersDetails(users);
		} else {
			errorResponse = errorResponse.GetErrorSet(1);
			adminUsersResponse.setError(errorResponse);
		}
		return adminUsersResponse;
	}

	public AdminUsersResponse GetUserBankDetails(Users users) {

		adminUsersResponse = new AdminUsersResponse();
		errorResponse = new ErrorResponse();

		if (users.getUser_id() != 0) {
			adminUsersResponse = adminDao.getUserBankDetails(users);
		} else {
			errorResponse = errorResponse.GetErrorSet(1);
			adminUsersResponse.setError(errorResponse);
		}
		return adminUsersResponse;
	}

	public AdminUsersResponse updateUserKycStatus(Users users) {
		adminUsersResponse = new AdminUsersResponse();
		errorResponse = new ErrorResponse();
		if (StringUtils.hasText(users.getUuid()) && users.getUser_docs_status() != 0) {
			adminUsersResponse = adminDao.updateUserKycStatus(users);
		} else {
			errorResponse = errorResponse.GetErrorSet(1);
			adminUsersResponse.setError(errorResponse);
		}
		return adminUsersResponse;
	}
	
	public AdminUsersResponse updateKycStatus(Users users) {
		adminUsersResponse = new AdminUsersResponse();
		errorResponse = new ErrorResponse();
		if (StringUtils.hasText(users.getUuid())) {
			adminUsersResponse = adminDao.updateKycStatus(users);
		} else {
			errorResponse = errorResponse.GetErrorSet(1);
			adminUsersResponse.setError(errorResponse);
		}
		return adminUsersResponse;
	}

	public AdminUsersResponse approveUserBankDetails(Users users) {
		adminUsersResponse = new AdminUsersResponse();
		errorResponse = new ErrorResponse();
		if (users.getUser_id() != 0 && users.getBank_details_status() != null && users.getBank_details_status() != "") {
			adminUsersResponse = adminDao.approveUserBankDetails(users);
		} else {
			errorResponse = errorResponse.GetErrorSet(1);
			adminUsersResponse.setError(errorResponse);
		}
		return adminUsersResponse;
	}

	public AdminUsersResponse updateUserAccountStatus(Users user) {
		adminUsersResponse = new AdminUsersResponse();
		errorResponse = new ErrorResponse();
		if (user.getUser_id() != 0 && user.getStatus() != 0) {
			adminUsersResponse = adminDao.updateUserAccountStatus(user);
		} else {
			errorResponse = errorResponse.GetErrorSet(1);
			adminUsersResponse.setError(errorResponse);
		}
		return adminUsersResponse;
	}

	
	public AdminUsersResponse RestrictBuy(Users users) {

		adminUsersResponse = new AdminUsersResponse();
		errorResponse = new ErrorResponse();

		if (users.getUser_id() != 0) {
			adminUsersResponse = adminDao.restrictBuy(users);
		} else {
			errorResponse = errorResponse.GetErrorSet(1);
			adminUsersResponse.setError(errorResponse);
		}
		return adminUsersResponse;
	}

	public AdminUsersResponse RestrictSell(Users users) {

		adminUsersResponse = new AdminUsersResponse();
		errorResponse = new ErrorResponse();

		if (users.getUser_id() != 0) {
			adminUsersResponse = adminDao.restrictSell(users);
		} else {
			errorResponse = errorResponse.GetErrorSet(1);
			adminUsersResponse.setError(errorResponse);
		}
		return adminUsersResponse;
	}

	public AdminUsersResponse RestrictSend(Users users) {

		adminUsersResponse = new AdminUsersResponse();
		errorResponse = new ErrorResponse();

		if (users.getUser_id() != 0) {
			adminUsersResponse = adminDao.restrictSend(users);
		} else {
			errorResponse = errorResponse.GetErrorSet(1);
			adminUsersResponse.setError(errorResponse);
		}
		return adminUsersResponse;
	}

	public AdminUsersResponse RestrictReceive(Users users) {

		adminUsersResponse = new AdminUsersResponse();
		errorResponse = new ErrorResponse();

		if (users.getUser_id() != 0) {
			adminUsersResponse = adminDao.restrictReceive(users);
		} else {
			errorResponse = errorResponse.GetErrorSet(1);
			adminUsersResponse.setError(errorResponse);
		}
		return adminUsersResponse;
	}

	public AdminUsersResponse SendMailToUser(Users user) {

		adminUsersResponse = new AdminUsersResponse();
		errorResponse = new ErrorResponse();
		if (user.getUser_id() != 0 && user.getMessage() != null && !user.getMessage().isEmpty()) {
			adminUsersResponse = adminDao.SendMailToUser(user);
		} else {
			errorResponse = errorResponse.GetErrorSet(1);
			adminUsersResponse.setError(errorResponse);
		}
		return adminUsersResponse;
	}

	public AdminUsersResponse SendMailFromPaybitoSite(String user_email, String email_cc, String email_content,
			MultipartFile coin_logo, MultipartFile legal_docs, String recaptchaResponse) {
		adminUsersResponse = new AdminUsersResponse();
		errorResponse = new ErrorResponse();
		if (user_email != null && !user_email.isEmpty() && email_cc != null && !email_cc.isEmpty()
				&& email_content != null && !email_content.isEmpty() && recaptchaResponse != null
				&& !recaptchaResponse.isEmpty()) {
			adminUsersResponse = adminDao.SendMailFromPaybitoSite(user_email, email_cc, email_content, coin_logo,
					legal_docs, recaptchaResponse);
		} else {
			errorResponse = errorResponse.GetErrorSet(1);
			adminUsersResponse.setError(errorResponse);
		}
		return adminUsersResponse;
	}

	public AdminUsersResponse changeUserProfile(Users users) {
		adminUsersResponse = new AdminUsersResponse();
		errorResponse = new ErrorResponse();
		if (users.getUser_id() != 0) {
			adminUsersResponse = adminDao.changeUserProfile(users);
		} else {
			errorResponse = errorResponse.GetErrorSet(1);
			adminUsersResponse.setError(errorResponse);
		}
		return adminUsersResponse;
	}
	
	//============================================= Health Check Up  =================================================== 
	
	public ExchangeHealthCheckResponse getMemoryAndSpaceDetails() {
		return adminDao.getMemoryAndSpaceDetails();
	}
	
	public ExchangeHealthCheckResponse getTomcatDetails() {
		return adminDao.getTomcatDetails();
	}
	
	public ExchangeHealthCheckResponse getDbProcessDetails() {
		return adminDao.getDbProcessDetails();
	}
	
	public ExchangeHealthCheckResponse getOfferDetails() {
		return adminDao.getOfferDetails();
	}
	
	public ExchangeHealthCheckResponse getfuturesOfferDetails() {
		return adminDao.getfuturesOfferDetails();
	}
	
	public ExchangeHealthCheckResponse getNetworkActiveOffers() {
		return adminDao.getNetworkActiveOffers();
	}
	
	public ExchangeHealthCheckResponse futuresNetworkActiveOffers() {
		return adminDao.futuresNetworkActiveOffers();
	}
	
	public Map<String, Object> exchangeMaintenance(Maintenance maintenance) {
		return adminDao.exchangeMaintenance(maintenance);
	}
	
	public Map<String, Object> getBlockDetails() {
		return adminDao.getBlockDetails();
	}
	
	public Map<String, Object> tierWiseTradingFees() {
		return adminDao.tierWiseTradingFees();
	}
	
	public Map<String, Object> updateTierWiseTradingFees(TierWiseTradingFees tierWiseTradingFees) {
		return adminDao.updateTierWiseTradingFees(tierWiseTradingFees);
	}
	
	public Map<String, Object> volumeWiseTradingFees() {
		return adminDao.volumeWiseTradingFees();
	}
	
	public AdminUsersResponse updateVolumeWiseTradingFees(TradingFees tradingFees) {
		adminUsersResponse = new AdminUsersResponse();
		errorResponse = new ErrorResponse();
		if (tradingFees.getId() != 0) {
			adminUsersResponse = adminDao.updateVolumeWiseTradingFees(tradingFees);
		}
		else {
			errorResponse = errorResponse.GetErrorSet(1);
			adminUsersResponse.setError(errorResponse);
		}
		return adminUsersResponse;
	}
	
	public Map<String, Object> getAllCurrencyWiseSettingsData() {
		return adminDao.getAllCurrencyWiseSettingsData();
	}

	public Map<String, Object> currencyWiseMiningFees() {
		return adminDao.currencyWiseMiningFees();
	}

	public Map<String, Object> settingsDataFiatConvertion() {
		return adminDao.settingsDataFiatConvertion();
	}
	
	public AdminUsersResponse updateCurrencyWiseSettingsData(CurrencyWiseSetting currencyWiseSetting) {
		adminUsersResponse = new AdminUsersResponse();
		errorResponse = new ErrorResponse();
		if (currencyWiseSetting.getCurrencyId() != 0) {
			adminUsersResponse = adminDao.updateCurrencyWiseSettingsData(currencyWiseSetting);
		}
		else {
			errorResponse = errorResponse.GetErrorSet(1);
			adminUsersResponse.setError(errorResponse);
		}
		return adminUsersResponse;
	}
	
	public AdminUsersResponse updateMiningFees(MiningFees miningFees) {
		adminUsersResponse = new AdminUsersResponse();
		errorResponse = new ErrorResponse();
		if (miningFees.getCurrencyId() != 0) {
			adminUsersResponse = adminDao.updateMiningFees(miningFees);
		}
		else {
			errorResponse = errorResponse.GetErrorSet(1);
			adminUsersResponse.setError(errorResponse);
		}
		return adminUsersResponse;
	}
	
	public Map<String, Object> getFiatCurrencyRate() {
		return adminDao.getFiatCurrencyRate();
	}
	
	public Map<String, Object> updateFiatCurrencyRate(CurrencyRate currencyRate) {
		return adminDao.updateFiatCurrencyRate(currencyRate);
	}
	
	public Map<String, Object> getAllCurrencyByType() {
		return adminDao.getAllCurrencyByType();
	}

	public Map<String, Object> getAllCurrency() {
		return adminDao.getAllCurrency();
	}

	public Map<String, Object> getWalletOrder() {
		return adminDao.getWalletOrder();
	}
	
	public Map<String, Object> addCurrency(CurrencyMaster currencyMaster) {
		return adminDao.addCurrency(currencyMaster);
	}

	public Map<String, Object> updateCurrency(CurrencyMaster currencyMaster) {
		return adminDao.updateCurrency(currencyMaster);
	}
	
	public Map<String, Object> getAssetPairDetails() {
		return adminDao.getAssetPairDetails();
	}

	public Map<String, Object> getAssetOrder(int baseCurrencyId) {
		return adminDao.getAssetOrder(baseCurrencyId);
	}
	
	public Map<String, Object> addAssetPair(AssetPairDetails assetPairDetails) {
		return adminDao.addAssetPair(assetPairDetails);
	}

	public Map<String, Object> updateAssetPair(AssetPairDetails assetPairDetails) {
		return adminDao.updateAssetPair(assetPairDetails);
	}
	
	public Map<String, Object> getMachingEngineAssetPair() {
		return adminDao.getMachingEngineAssetPair();
	}
	
	public Map<String, Object> addMachingEngineAssetPair(AssetPairDetails assetPairDetails) {
		return adminDao.addMachingEngineAssetPair(assetPairDetails);
	}
	
	public Map<String, Object> getFuturesContractType() {
		return adminDao.getFuturesContractType();
	}
	
	public Map<String, Object> getFuturesAssetPairDetails() {
		return adminDao.getFuturesAssetPairDetails();
	}
	
	public Map<String, Object> addFuturesAssetPair(List<AssetPairDetails> assetPairDetails) {
		System.out.println("addFuturesAssetPair service called.");
		return adminDao.addFuturesAssetPair(assetPairDetails);
	}

	public Map<String, Object> updateFuturesAssetPair(List<AssetPairDetails> assetPairDetails) {
		return adminDao.updateFuturesAssetPair(assetPairDetails);
	}
	
	public Map<String, Object> getFuturesMachingEngineAssetPair() {
		return adminDao.getFuturesMachingEngineAssetPair();
	}
	
	public Map<String, Object> addFuturesMachingEngineAssetPair(List<AssetPairDetails> assetPairDetails) {
		return adminDao.addFuturesMachingEngineAssetPair(assetPairDetails);
	}
	
	public Map<String, Object> marginCallOrAutoliquidity() {
		return adminDao.marginCallOrAutoliquidity();
	}
	
	public Map<String, Object> updateMarginCallOrAutoliquidity(MarginCallOrLiquidityValue marginCallOrLiquidityValue) {
		return adminDao.updateMarginCallOrAutoliquidity(marginCallOrLiquidityValue);
	}
	
	public Map<String, Object> futuresTradingFees() {
		return adminDao.futuresTradingFees();
	}
	
	public Map<String, Object> updateFuturesTradingFees(FuturesTradingFees futuresTradingFees) {
		return adminDao.updateFuturesTradingFees(futuresTradingFees);
	}
	
	public AdminUsersResponse franchiseRegistration(FranchiseRequestInput franchiseRequestInput) {
		adminUsersResponse = new AdminUsersResponse();
		errorResponse = new ErrorResponse();
		if (franchiseRequestInput.getCompanyName() != null && !franchiseRequestInput.getCompanyName().isEmpty()
				&& franchiseRequestInput.getCompanyNickName() != null && !franchiseRequestInput.getCompanyNickName().isEmpty()
				&& franchiseRequestInput.getRegistrationId() != null && !franchiseRequestInput.getRegistrationId().isEmpty()
				&& franchiseRequestInput.getCity() != null && !franchiseRequestInput.getCity().isEmpty()
				&& franchiseRequestInput.getCountry() != null && !franchiseRequestInput.getCountry().isEmpty()
				&& franchiseRequestInput.getZipCode() != null && !franchiseRequestInput.getZipCode().isEmpty()
				&& franchiseRequestInput.getPhone() != null && !franchiseRequestInput.getPhone().isEmpty()
				&& franchiseRequestInput.getCommissionPct() != 0) {
			adminUsersResponse = adminDao.franchiseRegistration(franchiseRequestInput);
			
		} else {
			errorResponse = errorResponse.GetErrorSet(1);
			adminUsersResponse.setError(errorResponse);
		}
		return adminUsersResponse;
	}

	public AdminUsersResponse getAllFranchise() {
		return adminDao.getAllFranchise();
	}

	public AdminUsersResponse franchiseById(FranchiseRequestInput franchiseRequestInput) {
		adminUsersResponse = new AdminUsersResponse();
		errorResponse = new ErrorResponse();
		if (franchiseRequestInput.getId() != 0) {
			adminUsersResponse = adminDao.franchiseById(franchiseRequestInput);
		}
		else {
			errorResponse = errorResponse.GetErrorSet(1);
			adminUsersResponse.setError(errorResponse);
		}
		return adminUsersResponse;
	}

	public AdminUsersResponse updateFranchise(FranchiseRequestInput franchiseRequestInput) {
		adminUsersResponse = new AdminUsersResponse();
		errorResponse = new ErrorResponse();
		if (franchiseRequestInput.getId() != 0) {
			adminUsersResponse = adminDao.updateFranchise(franchiseRequestInput);
		}
		else {
			errorResponse = errorResponse.GetErrorSet(1);
			adminUsersResponse.setError(errorResponse);
		}
		return adminUsersResponse;
	}
	
	public Map<String, Object> earningByFranchiseUsers(FranchiseRequestInput franchiseRequestInput) {
		return adminDao.earningByFranchiseUsers(franchiseRequestInput);
	}
	
	public Map<String, Object> totalEarningByDateRange(FranchiseRequestInput franchiseRequestInput) {
		return adminDao.totalEarningByDateRange(franchiseRequestInput);
	}
	
	public Map<String, Object> dayWiseEarning(FranchiseRequestInput franchiseRequestInput) {
		return adminDao.dayWiseEarning(franchiseRequestInput);
	}

	public Map<String, Object> allReferralUser() {
		return adminDao.allReferralUser();
	}

	public Map<String, Object> allUsersApiKeyDetails(int pageNo,int noOfRows,String searchString) {
		return adminDao.allUsersApiKeyDetails(pageNo,noOfRows,searchString);
	}

	public Map<String, Object> updateUserDetails(Users user) {
		return adminDao.updateUserDetails(user);
	}

	public Map<String, Object> futuresAllAssetPair() {
		return adminDao.futuresAllAssetPair();
	}

}
