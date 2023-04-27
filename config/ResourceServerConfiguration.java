package com.project.admin.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

@Configuration
@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

	@Override
	public void configure(HttpSecurity http) throws Exception {
		/*
		 * http. anonymous().disable()
		 * .requestMatchers().antMatchers("/merchant/**","/admin/**",
		 * "/MerchantDashboard/**","/payment/**") .and().authorizeRequests()
		 * .antMatchers("/merchant/**","/admin/**","/MerchantDashboard/**","/payment/**"
		 * ).access("hasRole('ADMIN')")
		 * .and().exceptionHandling().accessDeniedHandler(new
		 * OAuth2AccessDeniedHandler());
		 */

		http.headers().frameOptions().disable().and().authorizeRequests()
				.antMatchers("/adminAccess/SendOTP","/adminAccess/LoginAdmin","/adminAccess/CheckTwoFactor",
						"/admin/SendMailFromPaybitoSite","/admin/getAllCurrencyByType","/admin/getAllCurrency",
						"/admin/getAssetPairDetails","/admin/getMachingEngineAssetPair","/admin/tierWiseSettingsDetails",
						"/admin/miningFeesDetails","/admin/getFiatCurrencyRate","/admin/coinListingByPaybito",
						"/transactions/getCryptoAddress", "/transactions/getAssetWiseBalanceComparison",
						"/transactions/SendAssetBalanceComparison","/transactions/GetAllBotUsers")
				.permitAll().antMatchers("/admin/**", "/askbid/**", "/transactions/**", "/adminAccess/**")
				.hasRole("ADMIN");
		/*
		 * .antMatchers("/admin/GetAdminDetails","/transactions/GetExchangeGraphData",
		 * "/admin/GetUsersDetails","/admin/GetUserBankDetails","/admin/ApproveUsers",
		 * "/admin/ApproveUserBankDetails","/admin/RestrictBuy","/admin/RestrictSell",
		 * "/admin//{user_id}/file/{file_name.+}",
		 * "/admin/{currency_name}/download/{file_name.+}",
		 * "/transactions/GetCustomerTransactionsList","/admin/SendMailToUser").
		 * access("hasRole('SUPERADMIN') or hasRole('ADMIN')")
		 * .antMatchers("/admin/GetAllConfirmUsers").hasRole("VIEWCFRMUSER")
		 * .antMatchers("/admin/GetAllUnconfirmUsers").hasRole("VIEWUNCFRMUSER")
		 * .antMatchers("/transactions/GetCustomerAllBuySell").hasRole("BUYSELL")
		 * .antMatchers("/transactions/GetAllSendReceive").hasRole("SENDRECEIVE")
		 * .antMatchers("/transactions/GetPaymentOrderList").hasRole("PORDER")
		 * .antMatchers("/transactions/UpdateReferenceNo","/transactions/AddInvoice").
		 * hasRole("PCFRMORDER")
		 * .antMatchers("/transactions/GetPaybitoBalance").hasRole("MBALANCE")
		 * .antMatchers("/transactions/GetCustomerAllWithdrawls",
		 * "/transactions/ApprovePendingWithdrawl").hasRole("WREQUEST")
		 * .antMatchers("/admin/GetPaybitoBalance","/admin/AddPaybitoBalance",
		 * "/admin/UpdatePaybitoBalance","/transactions.GetCutOffAmount").hasRole(
		 * "PUSER") .antMatchers("/transactions/GetPendingBuyTransaction",
		 * "/transactions/GetAllPendingBuyTransaction(file)",
		 * "/transactions/ClearPendingTransactions").hasRole("PTRANSACTIONS")
		 * .antMatchers("/askbid/AddAsk","/askbid/EditAsk","/askbid/DeleteAsk",
		 * "/askbid/GetAllAsks",
		 * "/askbid/AddBid","/askbid/EditBid","/askbid/DeleteBid","/askbid/GetAllBids").
		 * hasRole("OBOOK")
		 */

		/*
		 * .antMatchers("/admin/GetAdminDetails","/transactions/GetExchangeGraphData").
		 * access("hasRole('SUPERADMIN') or hasRole('ADMIN')")
		 * .antMatchers("/admin/GetUsersDetails","/admin/GetUserBankDetails",
		 * "/admin/ApproveUsers",
		 * "/admin/ApproveUserBankDetails","/admin/RestrictBuy","/admin/RestrictSell",
		 * "/admin//{user_id}/file/{file_name.+}",
		 * "/admin/{currency_name}/download/{file_name.+}",
		 * "/transactions/GetCustomerTransactionsList","/admin/SendMailToUser").
		 * access("hasRole('VIEWCFRMUSER') or hasRole('VIEWUNCFRMUSER') or hasRole('SUPERADMIN')"
		 * ) .antMatchers("/admin/GetAllConfirmUsers").
		 * access("hasRole('VIEWCFRMUSER') or hasRole('SUPERADMIN')")
		 * .antMatchers("/admin/GetAllUnconfirmUsers").
		 * access("hasRole('VIEWUNCFRMUSER') or hasRole('SUPERADMIN')")
		 * .antMatchers("/transactions/GetCustomerAllBuySell").
		 * access("hasRole('BUYSELL') or hasRole('SUPERADMIN')")
		 * .antMatchers("/transactions/GetAllSendReceive").
		 * access("hasRole('SENDRECEIVE') or hasRole('SUPERADMIN')")
		 * .antMatchers("/transactions/GetPaymentOrderList").
		 * access("hasRole('PORDER') or hasRole('SUPERADMIN')")
		 * .antMatchers("/transactions/UpdateReferenceNo","/transactions/AddInvoice").
		 * access("hasRole('PCFRMORDER') or hasRole('SUPERADMIN')")
		 * .antMatchers("/transactions/GetPaybitoBalance").
		 * access("hasRole('MBALANCE') or hasRole('SUPERADMIN')")
		 * .antMatchers("/transactions/GetCustomerAllWithdrawls",
		 * "/transactions/ApprovePendingWithdrawl").
		 * access("hasRole('WREQUEST') or hasRole('SUPERADMIN')")
		 * .antMatchers("/admin/GetPaybitoBalance","/admin/AddPaybitoBalance",
		 * "/admin/UpdatePaybitoBalance","/transactions.GetCutOffAmount").
		 * access("hasRole('PUSER') or hasRole('SUPERADMIN')")
		 * .antMatchers("/transactions/GetPendingBuyTransaction",
		 * "/transactions/GetAllPendingBuyTransaction(file)",
		 * "/transactions/ClearPendingTransactions").
		 * access("hasRole('PTRANSACTIONS') or hasRole('SUPERADMIN')")
		 * .antMatchers("/askbid/AddAsk","/askbid/EditAsk","/askbid/DeleteAsk",
		 * "/askbid/GetAllAsks",
		 * "/askbid/AddBid","/askbid/EditBid","/askbid/DeleteBid","/askbid/GetAllBids").
		 * access("hasRole('OBOOK') or hasRole('SUPERADMIN')")
		 * .antMatchers("/transactions/BuySellAsset","/transactions/BuySellAssetDetails"
		 * ).access("hasRole('BOTCONTROL') or hasRole('SUPERADMIN')")
		 */;

	}
}
