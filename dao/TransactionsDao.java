package com.project.admin.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.project.admin.model.AdminBankDetails;
import com.project.admin.model.Asset;
import com.project.admin.model.BankDetails;
import com.project.admin.model.ColdWalletAddress;
import com.project.admin.model.ColdWalletBalance;
import com.project.admin.model.ErrorResponse;
import com.project.admin.model.FuturesBalanceComparison;
import com.project.admin.model.NodeBalance;
import com.project.admin.model.PaymentOrders;
import com.project.admin.model.PaymentWithdrawal;
import com.project.admin.model.Remittance;
import com.project.admin.model.ReportName;
import com.project.admin.model.ReportRequest;
import com.project.admin.model.TransactionResponse;
import com.project.admin.model.TransactionValidation;
import com.project.admin.model.UserTransactions;
import com.project.admin.model.Users;

@Component
public class TransactionsDao extends MyJdbcDaoSupport {
	private static final Logger log = LoggerFactory.getLogger(TransactionsDao.class);
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	Environment env;
	@Autowired
	MailContentBuilderService mailContentBuilderService;
	@Autowired
	MailClientService mailClientService;
	@Autowired
	NotificationService notificationService;
	@Autowired
	private EmailService emailService;

	ErrorResponse error = null;
	TransactionResponse transactionResponse = null;
	// constant
	private static final String STARTDATE = "2010-01-01";
	private static final String ADMIN_BANK_DETAILS = "select * from admin_bank_details";
	private static final String COLD_WALLET_ADDRESSES = "select * from cold_wallet_address";
	private static final String INSERT_ASSET_BALANCE = "INSERT INTO ASSET_BALANCE_DETAILS (TOTAL_TRADE_VALUE_USD,FEES,OTHERS_TRADE_SPEND_VALUE,TOTAL_ASSET_VALUE) VALUES (?,?,?,?)";

	public TransactionResponse updateReferenceNo(PaymentOrders paymentOrders) {
		Connection conn = null;
		transactionResponse = new TransactionResponse();
		error = new ErrorResponse();
		int i = 0;
		int userId = paymentOrders.getUserId();
		int adminUserId = paymentOrders.getAdminUserId();
		int orderId = paymentOrders.getOrderId();
		int code = paymentOrders.getSecurityCode();
		String referenceNo = paymentOrders.getReferenceNo();
		String remarks = paymentOrders.getRemarks() != null ? paymentOrders.getRemarks() : "";
		String sqlUpdate = "";
		int lastInsertId = 0;
		String message = "";
		String url = env.getProperty("project.paybito.admin.model.api") + "admin/fiatFundLoad";
		try {
			conn = jdbcTemplate.getDataSource().getConnection();
			if (userId > 0) {
				log.info(" update reference no");
				String sql = "select is_blocked from admin_user_login where user_id = ? and otp = ? ";
				BigDecimal status = DataAccessUtils.singleResult(jdbcTemplate.query(sql,
						new Object[] { adminUserId, paymentOrders.getOtp() }, new SingleColumnRowMapper<BigDecimal>()));
				if (status != null) {
					sql = "update admin_user_login set otp = ? where user_id = ? ";
					jdbcTemplate.update(sql, 0,adminUserId);
					if (status.intValue() == 1) {
						error.setError_data(1);
						error.setError_msg("You are not authorized.");
					} else {
						SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
								.withProcedureName("PRIVILEGE_CHECK");
						MapSqlParameterSource input = new MapSqlParameterSource();
						input.addValue("P_USER_ID", adminUserId);
						input.addValue("P_METHOD_ID", 40);
						Map<String, Object> result = simpleJdbcCall.execute(input);
						int returnId = ((BigDecimal) result.get("R_RETURN_ID")).intValue();
						message = (String) result.get("R_MESSAGE");
						if (returnId == 1) {
							sql = "select amount,payment_method,order_no,currency_id,currency,transaction_fee from payment_orders where user_id = ? and order_id = ?  ";
							paymentOrders = jdbcTemplate.queryForObject(sql, new Object[] { userId, orderId },
									new BeanPropertyRowMapper<PaymentOrders>(PaymentOrders.class));
							if (!remarks.isEmpty()) {
								sqlUpdate = " update payment_orders set reference_no = ?, admin_user_id = ?, status = 18 where order_id =? and user_id = ? ";
								i = jdbcTemplate.update(sqlUpdate, referenceNo, adminUserId, orderId, userId);
								if (i != 0) {
									paymentOrderMailSend(paymentOrders, conn, userId, "Declined");
									error.setError_data(0);
									error.setError_msg("");
								} else {
									error.setError_data(1);
									error.setError_msg("Reference no updation failed");
								}
							} else {
								String response = emailService.check2Fa(adminUserId, code);
								if (response.equalsIgnoreCase("Success")) {
									sqlUpdate = " update payment_orders set reference_no = ?, status = 16 where order_id =? and user_id = ? ";
									i = jdbcTemplate.update(sqlUpdate, referenceNo, orderId, userId);
									if (i > 0) {
										SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate);
										jdbcCall.withProcedureName("addInvoice_sp");
										MapSqlParameterSource in = new MapSqlParameterSource();
										in.addValue("p_user_id", userId);
										in.addValue("p_amount", paymentOrders.getAmount());
										in.addValue("p_order_id", orderId);
										BigDecimal id = jdbcCall.executeObject(BigDecimal.class, in);
										lastInsertId = id.intValue();
										RestTemplate restTemp = new RestTemplate();
										HttpHeaders header = new HttpHeaders();
										header.setContentType(MediaType.APPLICATION_JSON);
										JSONObject json = new JSONObject();
										if (lastInsertId != 0) {
											json.put("customerid", userId);
											json.put("amount", paymentOrders.getAmount());
											json.put("orderid", String.valueOf(orderId));
											json.put("invoiceno", lastInsertId);
											json.put("orderno", paymentOrders.getOrderNo());
											json.put("referenceno", referenceNo);
											json.put("description", "Load");
											json.put("status", "confirm");
											json.put("action", "Load");
											json.put("currencyid", String.valueOf(paymentOrders.getCurrencyId()));
											json.put("txnCharge", paymentOrders.getTransactionFee());
											HttpEntity<String> entity = new HttpEntity<String>(json.toString(), header);
											ResponseEntity<String> res = restTemp.postForEntity(url, entity,
													String.class);
											if (res != null) {
												sqlUpdate = "update payment_orders set invoice_id =?,admin_user_id = ?, status=17  where order_id = ? and user_id = ? ";
												i = jdbcTemplate.update(sqlUpdate, lastInsertId, adminUserId, orderId,
														userId);
												paymentOrderMailSend(paymentOrders, conn, userId, "Accepted");
												error.setError_data(0);
												error.setError_msg("");
											} else {
												// delete invoice row if load api failed .
												sql = "delete  from invoices where  invoice_id = ? and order_id = ?";
												PreparedStatement ps = conn.prepareStatement(sql);
												ps.setInt(1, lastInsertId);
												ps.setInt(2, paymentOrders.getOrderId());
												ps.executeUpdate();
												ps.close();
												error.setError_data(1);
												error.setError_msg("Invoice not generated.");
											}
										} else {
											error.setError_data(1);
											error.setError_msg("Invoice creation process failed, Please try again.");
										}
									} else {
										error.setError_data(1);
										error.setError_msg("Reference no updation failed");
									}
								} else {
									error.setError_data(1);
									error.setError_msg(response);
								}
							}
						} else {
							error.setError_data(1);
							error.setError_msg(message);
						}
					}
				} else {
					error.setError_data(1);
					error.setError_msg("Invalid Admin Or OTP.");
				}
			} else {
				error.setError_data(1);
				error.setError_msg("Invalid User.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			error.setError_data(1);
			error.setError_msg(e.getMessage());
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		transactionResponse.setError(error);
		return transactionResponse;
	}

	private void paymentOrderMailSend(PaymentOrders paymentOrders, Connection conn, int userId, String remarks) {
		PreparedStatement ps;
		String sql;
		try {
			// send notification to issuer
			sql = " select u.first_name,u.phone,u.ssn,u.address,u.city,u.country,u.zip,u.email,u.android_device_token,u.ios_device_token,uas.sound_alert from users u "
					+ " inner join user_app_settings uas on u.user_id=uas.user_id and u.user_id = ? ";
			ps = conn.prepareStatement(sql);
			ps.setInt(1, userId);
			ResultSet rs1 = ps.executeQuery();
			if (rs1.next()) {
				String title = "Payment Order " + remarks;// +getValueFromSettings("fiat_symbol_1",
															// conn)+" "
				String issuer_message = "Order no #" + paymentOrders.getOrderNo() + " for amount "
						+ env.getProperty("project.api.fiat_symbol1") + " " + String.valueOf(paymentOrders.getAmount())
						+ " has been " + remarks;
				if (rs1.getString("android_device_token") != null) {
					notificationService.send_notification_android(issuer_message, title,
							rs1.getString("android_device_token"), rs1.getInt("sound_alert"), userId + "", "", 10,
							conn);
				}
				if (rs1.getString("ios_device_token") != null) {
					notificationService.send_notification_ios(issuer_message, title, rs1.getString("ios_device_token"),
							rs1.getInt("sound_alert"), userId + "", "", 10, conn);
				}
				LocalDate localDate = LocalDate.now();
				String current_date = DateTimeFormatter.ofPattern("dd/MM/yyy").format(localDate);
				// send mail to user
				String customer_email = rs1.getString("email");
				HashMap<String, String> nameVal = new HashMap<>();

				nameVal.put("user_name", rs1.getString("first_name"));
				nameVal.put("phone_no", rs1.getString("phone"));
				nameVal.put("email", customer_email);
				System.out.println("customer_email" + customer_email);
				nameVal.put("user_address", rs1.getString("address") + "," + rs1.getString("city") + "-"
						+ rs1.getString("zip") + "," + rs1.getString("country"));
				nameVal.put("company_name", env.getProperty("admin.company.name"));
//				nameVal.put("company_address", env.getProperty("admin.company.address"));
				nameVal.put("company_email", env.getProperty("admin.company.email"));
				nameVal.put("order_no", paymentOrders.getOrderNo());
				nameVal.put("order_date", current_date);
				nameVal.put("order_total", String.valueOf(paymentOrders.getAmount()));
				nameVal.put("currency", paymentOrders.getCurrency().toUpperCase());
				String mail_content = mailContentBuilderService.build("payment_order_status", nameVal);
				String subject = "Payment Order " + remarks;
//				String bcc ="treasury@mexdigital.com";
				mailClientService.mailthreding(env.getProperty("spring.mail.username"), customer_email, subject,
						mail_content, "");
			}
			rs1.close();
			ps.close();
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	public TransactionResponse getPaymentOrderList(PaymentOrders paymentOrders) {
		Connection conn = null; // create connection instance to connect with
								// database
		PreparedStatement ps = null; // create Prepared statement instance to
										// run sql query
		transactionResponse = new TransactionResponse();
		error = new ErrorResponse();
		List<PaymentOrders> paymentOrderList = new ArrayList<>();
		int totalCount = 0;
		int startsForm = (paymentOrders.getPageNo() - 1) * paymentOrders.getNoOfItemsPerPage();
		int end = startsForm + paymentOrders.getNoOfItemsPerPage();
//		String condition = "";
		try {

			SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
					.withProcedureName("ADMIN_ALL_PAYMENT_ORDER");
			/*
			 * .withProcedureName("ADMIN_ALL_PAYMENT_ORDER").returningResultSet(
			 * "GET_PAYMENT_ORDER_DETAILS", new PaymentOrderListMapper())
			 */;
			MapSqlParameterSource input = new MapSqlParameterSource();
			input.addValue("P_TOTAL_ROW", end);
			input.addValue("P_FROM_ROW", startsForm);
			input.addValue("P_SEARCH_STRING", paymentOrders.getSearchString());
			Map<String, Object> result = simpleJdbcCall.execute(input);
			totalCount = ((BigDecimal) result.get("TOTAL_ROW")).intValue();
			List<Map<String, Object>> resultList = (List<Map<String, Object>>) result.get("GET_PAYMENT_ORDER_DETAILS");
			for (Map<String, Object> object : resultList) {
				PaymentOrders _paymentOrders = new PaymentOrders();
				_paymentOrders.setOrderId(
						object.get("ORDER_ID") != null ? Integer.parseInt(object.get("ORDER_ID").toString()) : 0);
				_paymentOrders.setUserId(
						object.get("USER_ID") != null ? Integer.parseInt(object.get("USER_ID").toString()) : 0);
				_paymentOrders.setInvoiceId(
						object.get("INVOICE_ID") != null ? Integer.parseInt(object.get("INVOICE_ID").toString()) : 0);
				_paymentOrders.setAmount(
						object.get("AMOUNT") != null ? Double.parseDouble(object.get("AMOUNT").toString()) : 0);
				_paymentOrders.setReferenceNo(
						object.get("REFERENCE_NO") != null ? object.get("REFERENCE_NO").toString() : "");
				_paymentOrders.setCreated(object.get("CREATED") != null ? object.get("CREATED").toString() : "");
				_paymentOrders.setStatus(
						object.get("STATUS") != null ? Integer.parseInt(object.get("STATUS").toString()) : 0);
				_paymentOrders.setOrderNo(object.get("ORDER_NO") != null ? object.get("ORDER_NO").toString() : "");
				_paymentOrders.setCurrencyId(
						object.get("CURRENCY_ID") != null ? Integer.parseInt(object.get("CURRENCY_ID").toString()) : 0);
				_paymentOrders.setCurrency(object.get("CURRENCY") != null ? object.get("CURRENCY").toString() : "");
				_paymentOrders
						.setUpdated(object.get("APPROVED_DATE") != null ? object.get("APPROVED_DATE").toString() : "");

				Users users = new Users();
				users.setFirstName(object.get("FIRST_NAME") != null ? object.get("FIRST_NAME").toString() : "");
				users.setPhone(object.get("PHONE") != null ? object.get("PHONE").toString() : "");
				users.setEmail(object.get("EMAIL") != null ? object.get("EMAIL").toString() : "");

				BankDetails bankDetails = new BankDetails();
				bankDetails.setBeneficiary_name(
						object.get("BENIFICIARY_NAME") != null ? object.get("BENIFICIARY_NAME").toString() : "");
				bankDetails.setBank_name(object.get("BANK_NAME") != null ? object.get("BANK_NAME").toString() : "");
				bankDetails.setAccount_no(object.get("ACCOUNT_NO") != null ? object.get("ACCOUNT_NO").toString() : "");
				bankDetails.setInstitutionTransitNo(
						object.get("institution_transit_no") != null ? object.get("institution_transit_no").toString()
								: "");
				bankDetails.setSwiftCode(object.get("SWIFT_CODE") != null ? object.get("SWIFT_CODE").toString() : "");
				bankDetails.setIfscCode(object.get("IFSC_CODE") != null ? object.get("IFSC_CODE").toString() : "");

				AdminBankDetails adminBankDetails = new AdminBankDetails();
				adminBankDetails.setBankName(object.get("ADMINBANKNAME").toString());
				adminBankDetails.setAccountNo(object.get("ADMINBANKACCOUNTNO").toString());

				_paymentOrders.setBankDetails(bankDetails);
				_paymentOrders.setUsers(users);
				_paymentOrders.setAdminBankDetails(adminBankDetails);
				paymentOrderList.add(_paymentOrders);
			}
			if (!paymentOrderList.isEmpty()) {
				transactionResponse.setTotalcount(totalCount);
				transactionResponse.setPaymentOrdersListResult(paymentOrderList);
				error.setError_data(0);
				error.setError_msg("");
			} else {
				error.setError_data(0);
				error.setError_msg("no data");
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			error.setError_data(1);
			error.setError_msg(e.getMessage());
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		transactionResponse.setError(error);
		return transactionResponse;
	}

	public TransactionResponse getCustomerListTrans(UserTransactions userTransactions) {
		transactionResponse = new TransactionResponse();
		error = new ErrorResponse();
		List<UserTransactions> userTransactionList = new ArrayList<>();
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate localDate = LocalDate.now();
		userTransactions.setFromDate(STARTDATE);
		userTransactions.setToDate(dtf.format(localDate));
		int page = (userTransactions.getPageNo()) - 1;
		int start = page * ((userTransactions.getNoOfItemsPerPage()));
		int end = ((userTransactions.getPageNo())) * ((userTransactions.getNoOfItemsPerPage()));
		try {
			SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate);
			MapSqlParameterSource input = new MapSqlParameterSource();
			if (userTransactions.getCurrency() == null) {
				simpleJdbcCall.withProcedureName("admin_all_transaction").returningResultSet("get_all_transaction",
						BeanPropertyRowMapper.newInstance(UserTransactions.class));
				input.addValue("P_CUSTOMER_ID", userTransactions.getCustomerId());
				input.addValue("P_START_DATE", userTransactions.getFromDate());
				input.addValue("P_END_DATE", userTransactions.getToDate());
				input.addValue("P_TOTAL_ROW", end);
				input.addValue("P_FROM_ROW", start);
			} else {
				simpleJdbcCall.withProcedureName("ADMIN_CURRENCYWISE_TRANSACTION").returningResultSet(
						"get_all_transaction", BeanPropertyRowMapper.newInstance(UserTransactions.class));
				input.addValue("P_CUSTOMER_ID", userTransactions.getCustomerId());
				input.addValue("P_CURRENCY_ID", userTransactions.getCurrency());
				input.addValue("P_START_DATE", userTransactions.getFromDate());
				input.addValue("P_END_DATE", userTransactions.getToDate());
				input.addValue("P_TOTAL_ROW", end);
				input.addValue("P_FROM_ROW", start);
			}
			Map<String, Object> result = simpleJdbcCall.execute(input);
			userTransactionList = (List<UserTransactions>) result.get("get_all_transaction");
			int totalCount = ((BigDecimal) result.get("TOTAL_ROW")).intValue();
			if (userTransactionList != null && !userTransactionList.isEmpty()) {
				transactionResponse.setTotalcount(totalCount);
				transactionResponse.setUserTransactionsListResult(userTransactionList);
				error.setError_data(0);
				error.setError_msg("");
				transactionResponse.setError(error);
			} else {
				error.setError_data(0);
				error.setError_msg("no data");
				transactionResponse.setError(error);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			error.setError_data(1);
			error.setError_msg(e.getMessage());
			transactionResponse.setError(error);
		}
		return transactionResponse;
	}

	public TransactionResponse getallBuySell(UserTransactions userTransactions) {
		transactionResponse = new TransactionResponse();
		error = new ErrorResponse();
		List<UserTransactions> userTransactionList = new ArrayList<>();
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate localDate = LocalDate.now();
		userTransactions.setFromDate(STARTDATE);
		userTransactions.setToDate(dtf.format(localDate));
		int page = userTransactions.getPageNo() - 1;
		int start = page * userTransactions.getNoOfItemsPerPage();
		int end = userTransactions.getPageNo() * userTransactions.getNoOfItemsPerPage();
		try {
			SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("ADMIN_ALL_BUY_SELL")
					.returningResultSet("GET_BUY_SELL_DETAILS",
							BeanPropertyRowMapper.newInstance(UserTransactions.class));
			MapSqlParameterSource input = new MapSqlParameterSource();
			input.addValue("START_DATE", userTransactions.getFromDate());
			input.addValue("END_DATE", userTransactions.getToDate());
			input.addValue("P_TOTAL_ROW", end);
			input.addValue("P_FROM_ROW", start);
			input.addValue("P_USER_TAG", userTransactions.getUserTag());
			input.addValue("P_SEARCH_STRING", userTransactions.getSearchString());
			Map<String, Object> result = simpleJdbcCall.execute(input);
			int totalCount = ((BigDecimal) result.get("TOTAL_ROW")).intValue();
			if (result.get("GET_BUY_SELL_DETAILS") != null) {
				userTransactionList = (List) result.get("GET_BUY_SELL_DETAILS");
			}
			if (userTransactionList != null && !userTransactionList.isEmpty()) {
				transactionResponse.setTotalcount(totalCount);
				transactionResponse.setUserTransactionsListResult(userTransactionList);
				error.setError_data(0);
				error.setError_msg("");
				transactionResponse.setError(error);
			} else {
				error.setError_data(0);
				error.setError_msg("no data");
				transactionResponse.setError(error);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			error.setError_data(1);
			error.setError_msg(e.getMessage());
			transactionResponse.setError(error);
		}
		return transactionResponse;
	}

	public TransactionResponse getPaybitoBalance() {
		transactionResponse = new TransactionResponse();
		error = new ErrorResponse();
		final String url = env.getProperty("project.paybito.admin.model.api") + "admin/walletBalance";
		List<NodeBalance> nodeList = new ArrayList<>();
		RestTemplate restTemp = new RestTemplate();
		HttpHeaders header = new HttpHeaders();
		header.setContentType(MediaType.APPLICATION_JSON);
		try {
			JSONObject json = new JSONObject();
			String query = "SELECT CURRENCY_ID,CURRENCY CURRENCYCODE FROM MONITOR_WALLET_ADDRESS WHERE IS_ACTIVE = 1 ORDER BY CURRENCY ";
			List<NodeBalance> currencyList = jdbcTemplate.query(query,
					new BeanPropertyRowMapper<NodeBalance>(NodeBalance.class));
			try {
				for (NodeBalance nodeBalance : currencyList) {
					json.put("currencyid", nodeBalance.getCurrencyId());
					json.put("currency", nodeBalance.getCurrencyCode());
					HttpEntity<String> entity = new HttpEntity<String>(json.toString(), header);
					ResponseEntity<String> res = restTemp.postForEntity(url, entity, String.class);
					if (res != null && res.getStatusCode() == HttpStatus.OK) {
						if (res.getBody() != null) {
							JSONObject obj = new JSONObject(res.getBody());
							if (!obj.get("nodeBalance").equals(null)) {
								nodeBalance.setNodeBalance(obj.getString("nodeBalance"));
							}
							nodeBalance.setNodeAddress(obj.get("nodeAddress").toString());
						} else {
							nodeBalance.setNodeBalance("Only available in live version.");
						}
					} else {
						nodeBalance.setNodeBalance("Only available in live version.");
					}
					nodeList.add(nodeBalance);
				}
			} catch (JSONException ex) {
				ex.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			error.setError_data(1);
			error.setError_msg(e.getMessage());
			transactionResponse.setError(error);
		}
		transactionResponse.setNodeBalanceResult(nodeList);
		error.setError_data(0);
		error.setError_msg("");
		transactionResponse.setError(error);
		return transactionResponse;
	}

	@SuppressWarnings("unchecked")
	public TransactionResponse getCustomerAllWithdrawls(UserTransactions userTransactions) {
		transactionResponse = new TransactionResponse();
		error = new ErrorResponse();
		List<PaymentWithdrawal> paymentWithdrawalList = new ArrayList<>();
		int totalCount = 0;
		try {
			SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
					.withProcedureName("GET_PAYMENT_WITHDRAWAL_DTLS").returningResultSet("PAYMENT_WITDRAWAL_DTLS",
							BeanPropertyRowMapper.newInstance(PaymentWithdrawal.class));
			MapSqlParameterSource input = new MapSqlParameterSource();
			input.addValue("P_TOTAL_ROW_PER_PAGE", userTransactions.getNoOfItemsPerPage());
			input.addValue("P_PAGE_NO", userTransactions.getPageNo());
			input.addValue("P_SEARCH_STRING", userTransactions.getSearchString());
			Map<String, Object> result = simpleJdbcCall.execute(input);
			paymentWithdrawalList = (List<PaymentWithdrawal>) result.get("PAYMENT_WITDRAWAL_DTLS");
			totalCount = ((BigDecimal) result.get("TOTAL_ROW")).intValue();

			if (!paymentWithdrawalList.isEmpty()) {
				transactionResponse.setTotalcount(totalCount);
				transactionResponse.setPaymentWithdrawalListResult(paymentWithdrawalList);
				error.setError_data(0);
				error.setError_msg("");
				transactionResponse.setError(error);
			} else {
				error.setError_data(0);
				error.setError_msg("no data");
				transactionResponse.setError(error);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			error.setError_data(1);
			error.setError_msg(e.getMessage());
			transactionResponse.setError(error);
		}
		return transactionResponse;
	}

	public TransactionResponse approvePendingWithdrawl(PaymentWithdrawal paymentWithdrawal) {
		Connection conn = null; // create connection instance to connect with
								// database
		PreparedStatement ps = null; // create Prepared statement instance to
										// run sql query
		transactionResponse = new TransactionResponse();
		error = new ErrorResponse();
		String orderid = "";
		try {
			log.info(" Approve pending withdrawal ");
			SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
					.withProcedureName("PAYMENT_WITHDRWAL_APPROVED_SP");
			MapSqlParameterSource in = new MapSqlParameterSource()
					.addValue("P_WITHDRWAL_ID", Integer.valueOf(paymentWithdrawal.getWithdrawalId()))
					.addValue("P_CUSTOMER_ID", paymentWithdrawal.getCustomerId())
					.addValue("P_ADMIN_ID", paymentWithdrawal.getAdminId())
					.addValue("P_ADMIN_BANK_ID", paymentWithdrawal.getAdminBankId())
					.addValue("P_CORPORATE_REMIT_ID", paymentWithdrawal.getCorporateRemitId())
					.addValue("P_REF_DESC", paymentWithdrawal.getDescription());
			Map<String, Object> result = simpleJdbcCall.execute(in);
			int returnId = ((BigDecimal) result.get("R_RETURN_ID")).intValue();
			String message = (String) result.get("R_MESSAGE");
			orderid = (String) result.get("R_ORDERID");
			if (returnId == 1) {
				try {
					conn = jdbcTemplate.getDataSource().getConnection();
					// send notification to issuer
					String sql = " select first_name,ssn,email,phone,address,city,country,zip,u.android_device_token,u.ios_device_token,uas.sound_alert  "
							+ " from users u inner join user_app_settings uas on u.user_id=uas.user_id and u.user_id = ? ";
					ps = conn.prepareStatement(sql);
					ps.setString(1, paymentWithdrawal.getCustomerId());
					ResultSet rs1 = ps.executeQuery();
					if (rs1.next()) {
						String title = "Fund Withdrawal Approved";
						String issuer_message = "Transaction no #" + orderid + " for amount "
								+ env.getProperty("project.api.fiat_symbol1") + " "
								+ paymentWithdrawal.getCreditAmount() + " has been approved.";
						if (rs1.getString("android_device_token") != null) {
							notificationService.send_notification_android(issuer_message, title,
									rs1.getString("android_device_token"), rs1.getInt("sound_alert"),
									paymentWithdrawal.getCustomerId(), "null", 11, conn);
						}
						if (rs1.getString("ios_device_token") != null) {
							notificationService.send_notification_ios(issuer_message, title,
									rs1.getString("ios_device_token"), rs1.getInt("sound_alert"),
									paymentWithdrawal.getCustomerId(), "null", 11, conn);
						}

						String adminBankQuery = "select * from admin_bank_details where bank_id = ?";
						ps = conn.prepareStatement(adminBankQuery);
						ps.setInt(1, paymentWithdrawal.getAdminBankId());
						ResultSet resultSet = ps.executeQuery();
						if (resultSet.next()) {
							// get txn charges
//							txnChargeEmail = Double.parseDouble(getValueFromSettings("withdrawal_txn_charges", conn));
							// send mail to user
							String customerEmail = rs1.getString("email");
							String userName = rs1.getString("first_name") != null ? rs1.getString("first_name")
									: customerEmail;
							HashMap<String, String> nameVal = new HashMap<>();
							LocalDate localDate = LocalDate.now();
							String current_date = DateTimeFormatter.ofPattern("dd/MM/yyy").format(localDate);
							log.info("Amount :" + paymentWithdrawal.getCreditAmount() + " cgst:"
									+ paymentWithdrawal.getChargeCgst() + " sgst:" + paymentWithdrawal.getChargeSgst()
									+ "txn charge:" + paymentWithdrawal.getTxnCharge());
							double igst = Double.parseDouble(paymentWithdrawal.getChargeCgst())
									+ Double.parseDouble(paymentWithdrawal.getChargeSgst());
							double total_amount = Double.parseDouble(paymentWithdrawal.getCreditAmount())
									- Double.parseDouble(paymentWithdrawal.getTxnCharge()) - igst;
							log.info("approve withdrawal charges:  amount" + paymentWithdrawal.getCreditAmount()
									+ " txn charges: " + paymentWithdrawal.getTxnCharge() + " igst:" + igst);
							nameVal.put("heading", "Withdrawal Order Approved");
							nameVal.put("user_name", userName);
							nameVal.put("phone_no", rs1.getString("phone"));
							nameVal.put("email", rs1.getString("email"));
							nameVal.put("user_address", rs1.getString("address") + "," + rs1.getString("city") + "-"
									+ rs1.getString("zip") + "," + rs1.getString("country"));
							nameVal.put("company_name", env.getProperty("admin.company.name"));
//							nameVal.put("company_address", env.getProperty("admin.company.address"));
							nameVal.put("company_email", env.getProperty("admin.company.email"));
							nameVal.put("order_no", orderid);
							nameVal.put("order_date", current_date);
							nameVal.put("order_total", String.valueOf(paymentWithdrawal.getCreditAmount()));
//							nameVal.put("txn_charge", String.format("%.2f", txnChargeEmail));
							nameVal.put("txn_fee", "-" + String.valueOf(paymentWithdrawal.getTxnCharge()));
							nameVal.put("total_amount", String.valueOf(total_amount));
							nameVal.put("bank_user_name", "HubKoin");
							nameVal.put("bank_name", resultSet.getString("bank_name"));
							nameVal.put("account_no", resultSet.getString("account_no"));
//							nameVal.put("ifsc_code", resultSet.getString("iban_no"));
							String mail_content = mailContentBuilderService.build("withdrawal_order", nameVal);
							String subject = "Fund Withdrawal Confirmation ";
							mailClientService.mailthreding(env.getProperty("spring.mail.username"), customerEmail,
									subject, mail_content, "");
						}
						resultSet.close();
					}
					rs1.close();
					ps.close();
				} catch (Exception ex) {
					ex.printStackTrace();
					log.error(ex.getMessage());
				}
				error.setError_data(0);
				error.setError_msg("");
				transactionResponse.setError(error);
			} else {
				error.setError_data(1);
				error.setError_msg(message);
				transactionResponse.setError(error);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			error.setError_data(1);
			error.setError_msg(e.getMessage());
			transactionResponse.setError(error);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return transactionResponse;
	}

	public TransactionResponse declinePendingWithdrawl(PaymentWithdrawal paymentWithdrawal) {
		transactionResponse = new TransactionResponse();
		error = new ErrorResponse();
		try {
			SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
					.withProcedureName("PAYMENT_WITHDRWAL_DECLINE_SP");
			MapSqlParameterSource in = new MapSqlParameterSource()
					.addValue("P_WITHDRWAL_ID", Integer.valueOf(paymentWithdrawal.getWithdrawalId()))
					.addValue("P_CUSTOMER_ID", paymentWithdrawal.getCustomerId());
			Map<String, Object> result = simpleJdbcCall.execute(in);
			int returnId = ((BigDecimal) result.get("R_RETURN_ID")).intValue();
			String procReturnMessage = (String) result.get("R_MESSAGE");
			if (returnId == 1) {
				String sql = " select first_name,email from users where user_id = ? ";
				Map<String, Object> getSenderData = jdbcTemplate.queryForMap(sql, paymentWithdrawal.getCustomerId());
				if (!getSenderData.isEmpty()) {
					String customerEmail = (String) getSenderData.get("email");
					String userName = getSenderData.get("first_name") != null ? (String) getSenderData.get("first_name")
							: customerEmail;
					String message = "Your withdrawal request has been declined. Your wallet balance is updated to reflect this change.";
					HashMap<String, String> nameVal = new HashMap<>();
					nameVal.put("heading", "Withdrawal Order Declined");
					nameVal.put("user_name", userName);
					nameVal.put("message1", message);
					String mail_content = mailContentBuilderService.build("update_request", nameVal);
					String subject = "Fund Withdrawal Declined";
//					String bcc ="treasury@mexdigital.com";
					mailClientService.mailthreding(env.getProperty("spring.mail.username"), customerEmail, subject,
							mail_content, "");
				}
				error.setError_data(0);
				error.setError_msg("");
			} else {
				error.setError_data(1);
				error.setError_msg(procReturnMessage);
			}
		} catch (Exception e) {
			log.error("Error in declinePendingWithdrawl: ", e);
			error.setError_data(1);
			error.setError_msg(e.getMessage());
		}
		transactionResponse.setError(error);
		return transactionResponse;
	}

	@SuppressWarnings("unchecked")
	public TransactionResponse getallSendReceive(UserTransactions userTransactions) {
		transactionResponse = new TransactionResponse();
		error = new ErrorResponse();
		List<UserTransactions> userTransactionList = new ArrayList<UserTransactions>();
		int totalCount = 0;
		int startsFrom = (userTransactions.getPageNo() - 1) * userTransactions.getNoOfItemsPerPage();
		int end = startsFrom + userTransactions.getNoOfItemsPerPage();
		try {
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate localDate = LocalDate.now();
			SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("ADMIN_ALL_SEND_RECEIVE")
					.returningResultSet("GET_SEND_RECEIVED_DETAILS",
							BeanPropertyRowMapper.newInstance(UserTransactions.class));
			MapSqlParameterSource input = new MapSqlParameterSource();
			input.addValue("START_DATE", STARTDATE);
			input.addValue("END_DATE", dtf.format(localDate));
			input.addValue("P_TOTAL_ROW", end);
			input.addValue("P_FROM_ROW", startsFrom);
			input.addValue("P_SEARCH_STRING", userTransactions.getSearchString());
			Map<String, Object> result = simpleJdbcCall.execute(input);
			userTransactionList = (List<UserTransactions>) result.get("GET_SEND_RECEIVED_DETAILS");
			totalCount = ((BigDecimal) result.get("TOTAL_ROW")).intValue();
			if (!userTransactionList.isEmpty()) {
				transactionResponse.setTotalcount(totalCount);
				transactionResponse.setUserTransactionsListResult(userTransactionList);
				error.setError_data(0);
				error.setError_msg("");
				transactionResponse.setError(error);
			} else {
				error.setError_data(0);
				error.setError_msg("no data");
				transactionResponse.setError(error);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			error.setError_data(1);
			error.setError_msg(e.getMessage());
			transactionResponse.setError(error);
		}
		return transactionResponse;
	}

	// calling bot services
	public TransactionResponse buySellAsset(Asset asset) {
		transactionResponse = new TransactionResponse();
		error = new ErrorResponse();
		String url = "";
		try {
			if (asset.getTxn_type() == 1) {
				url = env.getProperty("project.paybito.bot.api") + "buyasset";
			} else {
				url = env.getProperty("project.paybito.bot.api") + "sellasset";
			}
			JSONObject Requestjson = new JSONObject();
			Requestjson.put("base_asset", asset.getBase_asset());
			Requestjson.put("counter_asset", asset.getCounter_asset());
			Requestjson.put("max_amount", asset.getMax_amount());
			Requestjson.put("min_amount", asset.getMin_amount());
			Requestjson.put("max_price", asset.getMax_price());
			Requestjson.put("min_price", asset.getMin_price());
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			RestTemplate restTemplate = new RestTemplate();
			HttpEntity<String> entity = new HttpEntity<String>(Requestjson.toString(), headers);
			ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
			JSONObject resObject = new JSONObject(response.getBody());
			if (resObject.getString("status_code").equals("1")) {
				error.setError_data(0);
				error.setError_msg(resObject.getString("message"));
			} else {
				error.setError_data(1);
				error.setError_msg(resObject.getString("message"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			error.setError_data(1);
			error.setError_msg(e.getMessage());
		}
		transactionResponse.setError(error);
		return transactionResponse;
	}

	// calling bot buy sell asset details
	public TransactionResponse buySellAssetDetails(Asset asset) {
		transactionResponse = new TransactionResponse();
		error = new ErrorResponse();
		String url = "";
		try {
			if (asset.getTxn_type() == 1) {
				url = env.getProperty("project.paybito.bot.api") + "buyassetdetails";
			} else {
				url = env.getProperty("project.paybito.bot.api") + "sellassetdetails";
			}
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			RestTemplate restTemplate = new RestTemplate();
			HttpEntity<String> entity = new HttpEntity<String>(headers);
			ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
			transactionResponse.setBotServiceResult(response.getBody());
			error.setError_data(0);
			error.setError_msg("");
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			error.setError_data(1);
			error.setError_msg(e.getMessage());
		}
		transactionResponse.setError(error);
		return transactionResponse;
	}

	public String getValueFromSettings(String name, Connection conn) {
		PreparedStatement ps = null; // create Prepared statement instance to
										// run sql query
		String val = "";
		try {
			ps = conn.prepareStatement("select value from settings where name=? ");
			ps.setString(1, name);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				val = rs.getString("value");
			}
			rs.close();
			ps.close();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
		return val;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public TransactionResponse getAllSendPending() {
		transactionResponse = new TransactionResponse();
		error = new ErrorResponse();
		List<TransactionValidation> transactionValidationList = new ArrayList<>();
		try {
			SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
					.withProcedureName("GET_PENDING_SEND_TRANSACTION")
					.returningResultSet("P_RESULTS", BeanPropertyRowMapper.newInstance(TransactionValidation.class));
			Map<String, Object> result = simpleJdbcCall.execute();
			transactionValidationList = (List) result.get("P_RESULTS");
			if (!transactionValidationList.isEmpty()) {
				transactionResponse.setTransactionValidationList(transactionValidationList);
				error.setError_data(0);
				error.setError_msg("");
			} else {
				error.setError_data(0);
				error.setError_msg("no data found");
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			error.setError_data(1);
			error.setError_msg(e.getMessage());
		}
		transactionResponse.setError(error);
		return transactionResponse;
	}

	public TransactionResponse reviewSendRequest(TransactionValidation transactionValidation) {
		transactionResponse = new TransactionResponse();
		error = new ErrorResponse();
		String sql = "select user_id,is_blocked,two_factor_auth_key from admin_user_login where user_id = ? and otp = ? ";
		int adminUserId = 0, isBlocked = 0;
		String response = "";
		try {
			Map<String, Object> output = jdbcTemplate.queryForMap(sql, transactionValidation.getAdminUserId(),
					transactionValidation.getOtp());
			if (!output.isEmpty()) {
				adminUserId = ((BigDecimal) output.get("user_id")).intValue();
				isBlocked = ((BigDecimal) output.get("is_blocked")).intValue();
				response = isBlocked == 1 ? "You are not authorized." : "";
				sql = "update admin_user_login set otp = ? where user_id = ? ";
				jdbcTemplate.update(sql, 0,transactionValidation.getAdminUserId());
				if ((adminUserId == transactionValidation.getAdminUserId() && isBlocked != 1)) {
					response = emailService.check2Fa(adminUserId, transactionValidation.getSecurityCode());
					if (response.equalsIgnoreCase("Success")) {
						SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
								.withProcedureName("PRIVILEGE_CHECK");
						MapSqlParameterSource input = new MapSqlParameterSource();
						input.addValue("P_USER_ID", adminUserId);
						input.addValue("P_METHOD_ID", 81);
						Map<String, Object> result = simpleJdbcCall.execute(input);
						int returnId = ((BigDecimal) result.get("R_RETURN_ID")).intValue();
						response = (String) result.get("R_MESSAGE");
						if (returnId == 1) {
							response = sendToOtherApiCall(transactionValidation, "admin/sendToOtherWalletAddress");
							if (response.equalsIgnoreCase("Success")) {
								sql = "select current_balance from customer_ledger where CUSTOMER_ID = ? and "
										+ "CURRENCY_ID =" + transactionValidation.getCurrencyId();
								String cryptoBalance = jdbcTemplate.queryForObject(sql,
										new Object[] { transactionValidation.getCustomerId() }, String.class);
								if (!cryptoBalance.isEmpty()) {
									emailService.sendMailPaybitoOther(
											String.valueOf(transactionValidation.getCustomerId()),
											transactionValidation.getToAdd(),
											String.valueOf(transactionValidation.getAmount()), cryptoBalance,
											transactionValidation.getCurrency(), transactionValidation.getAction());
								}
								error.setError_data(0);
								error.setError_msg("");
							} else {
								error.setError_data(1);
								error.setError_msg(response);
							}
						} else {
							error.setError_data(1);
							error.setError_msg(response);
						}
					} else {
						error.setError_data(1);
						error.setError_msg(response);
					}
				} else {
					error.setError_data(1);
					error.setError_msg(response);
				}
			} else {
				response = "Invalid Admin Or OTP.";
			}
		} catch (Exception e) {
			log.error("Error in :", e);
			error.setError_data(1);
			error.setError_msg("Internal Api Error.");
		}
		transactionResponse.setError(error);
		return transactionResponse;
	}

	public String sendToOtherApiCall(TransactionValidation transactionValidation, String subUrl) {
		String result = "";
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("customerid", transactionValidation.getCustomerId());
			jsonObject.put("password", transactionValidation.getPassword());
			jsonObject.put("toadd", transactionValidation.getToAdd());
			jsonObject.put("amount", String.valueOf(transactionValidation.getAmount()));
			jsonObject.put("currencyid", transactionValidation.getCurrencyId());
			jsonObject.put("currency", transactionValidation.getCurrency());
			jsonObject.put("type", transactionValidation.getAction());
			jsonObject.put("description", transactionValidation.getDescription());
			jsonObject.put("id", transactionValidation.getId());
			jsonObject.put("transactionId", transactionValidation.getTransactionId());
			jsonObject.put("adminUserId", transactionValidation.getAdminUserId());
			if (transactionValidation.getCurrencyId() == 5 || transactionValidation.getCurrencyId() == 8
					|| transactionValidation.getCurrencyId() == 14 || transactionValidation.getCurrencyId() == 85) {
				jsonObject.put("memo", transactionValidation.getMemo());
			}
			if (transactionValidation.getCurrencyId() == 8 || transactionValidation.getCurrencyId() == 85) {
				jsonObject.put("tokenType", transactionValidation.getTokenType());
			}
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			String url = env.getProperty("project.paybito.admin.model.api") + subUrl;
			RestTemplate restTemplate = new RestTemplate();
			HttpEntity<String> entity = new HttpEntity<>(jsonObject.toString(), headers);
			String response = restTemplate.postForObject(url, entity, String.class);
			if (response != null) {
				JSONObject partsData = new JSONObject(response);
				if (partsData.getString("error").equalsIgnoreCase("Success")) {
					result = partsData.getString("error");
				} else {
					log.info("Error in call send to other api: {}", partsData.getString("error"));
					result = partsData.getString("error");
				}
			}
		} catch (Exception e) {
			log.error("error in send api calling: ", e);
		}
		return result;
	}

	public Map<String, Object> getCryptoAddress(TransactionValidation requestInput) {
		Map<String, Object> response = new HashMap<>();
		error = new ErrorResponse();
		int receiveLimit = 1;
		List<String> restApiResponse = new ArrayList<String>();
		String sql = " select u.user_id,u.send_limit,u.receive_limit,uas.lock_outgoing_transactions "
				+ " from users u inner join user_app_settings uas on u.user_id=uas.user_id and  u.user_id = ? ";
		try {
			if (StringUtils.hasText(requestInput.getUserType())
					&& requestInput.getUserType().equalsIgnoreCase("admin")) {
				restApiResponse = receiveCryptoAddressApiCall(requestInput, "receiveAddress");
			} else {
				Map<String, Object> verificationData = jdbcTemplate.queryForMap(sql, requestInput.getCustomerId());
				if (verificationData != null && !verificationData.isEmpty()) {
					// check send restriction or not
					receiveLimit = ((BigDecimal) verificationData.get("receive_limit")).intValue();
					if (receiveLimit == 1) {
						// call get receive api for get crypto address
						restApiResponse = receiveCryptoAddressApiCall(requestInput, "receiveAddress");
					} else {
						error.setError_data(1);
						error.setError_msg("Currently receive operation is not available.");
					}
				} else {
					error.setError_data(1);
					error.setError_msg("Your documents are being verified.");
				}
			}
			if (restApiResponse != null) {
				if (restApiResponse.get(0).equalsIgnoreCase("success")) {
					response.put("publicKey", restApiResponse.get(1));
					response.put("currencyId", requestInput.getCurrencyId());
					if (requestInput.getCurrencyId() == 5 || requestInput.getCurrencyId() == 8
							|| requestInput.getCurrencyId() == 14) {
						response.put("memo", restApiResponse.get(2));
					}
					error.setError_data(0);
					error.setError_msg("");
				} else {
					error.setError_data(1);
					error.setError_msg(restApiResponse.get(0));
				}
			}

		} catch (Exception e) {
			log.error("Error in get crypto address: ", e);
			error.setError_data(1);
			error.setError_msg(e.getMessage());
		}
		response.put("error", error);
		return response;
	}

	public List<String> setReceiveCryptoAddress(TransactionValidation requestInput) {

		List<String> res = null;
		try {
			if (requestInput.getCurrencyId() == 2) {
				res = receiveCryptoAddressApiCall(requestInput, "receiveBTC");
			} else if (requestInput.getCurrencyId() == 3) {
				res = receiveCryptoAddressApiCall(requestInput, "receiveETH");
			} else if (requestInput.getCurrencyId() == 4) {
				res = receiveCryptoAddressApiCall(requestInput, "receiveBCH");
			} else if (requestInput.getCurrencyId() == 5) {
				res = receiveCryptoAddressApiCall(requestInput, "receiveDIAM");
			} else if (requestInput.getCurrencyId() == 7) {
				res = receiveCryptoAddressApiCall(requestInput, "receiveLTC");
			} else if (requestInput.getCurrencyId() == 8) {
				res = receiveCryptoAddressApiCall(requestInput, "receiveHCX");
			} else if (requestInput.getCurrencyId() == 14) {
				res = receiveCryptoAddressApiCall(requestInput, "receiveXRP");
			} else if (requestInput.getCurrencyId() == 16) {
				res = receiveCryptoAddressApiCall(requestInput, "receiveUSDT");
			} else if (requestInput.getCurrencyId() == 25) {
				res = receiveCryptoAddressApiCall(requestInput, "receiveBAT");
			} else if (requestInput.getCurrencyId() == 26) {
				res = receiveCryptoAddressApiCall(requestInput, "receiveHBAR");
			} else if (requestInput.getCurrencyId() == 27) {
				res = receiveCryptoAddressApiCall(requestInput, "receiveLINK");
			} else if (requestInput.getCurrencyId() == 37) {
				res = receiveCryptoAddressApiCall(requestInput, "receiveKICKS");
			} else if (requestInput.getCurrencyId() == 38) {
				res = receiveCryptoAddressApiCall(requestInput, "receiveMRC");
			}
		} catch (Exception e) {
			log.error("error in send to other: ", e);
		}
		return res;
	}

	public List<String> receiveCryptoAddressApiCall(TransactionValidation requestInput, String subUrl) {
		List<String> res = new ArrayList<>();
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("customerid", String.valueOf(requestInput.getCustomerId()));
			jsonObject.put("currencyid", String.valueOf(requestInput.getCurrencyId()));
			jsonObject.put("tokenType", requestInput.getTokenType());
			jsonObject.put("password", requestInput.getPassword());

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			String url = env.getProperty("project.send.other.url") + subUrl;

			RestTemplate restTemplate = new RestTemplate();
			HttpEntity<String> entity = new HttpEntity<>(jsonObject.toString(), headers);
			String response = restTemplate.postForObject(url, entity, String.class);
			if (response != null) {
				JSONObject partsData = new JSONObject(response);
				if (partsData.getString("error").equalsIgnoreCase("success")) {
					if (partsData.getString("publicKey") != null && !partsData.getString("publicKey").isEmpty()) {
						res.add(partsData.getString("error"));
						res.add(partsData.getString("publicKey"));
						if (partsData.getInt("currencyid") == 5 || partsData.getInt("currencyid") == 8
								|| partsData.getInt("currencyid") == 14 || partsData.getInt("currencyid") == 85) {
							res.add(partsData.getString("memo"));
						}
					}
				} else {
					log.info("Error in call send to other api: {}", partsData.getString("error"));
					res.add(partsData.getString("error"));
				}
			}
		} catch (Exception e) {
			log.error("error in send api calling: ", e);
			res.add("Internal Api error");
		}
		return res;
	}

	public TransactionResponse sendToOther(TransactionValidation transactionValidation) {
		String response = "";
		transactionResponse = new TransactionResponse();
		error = new ErrorResponse();
		String sql = "select user_id from admin_user_login where user_id = ? and otp = ? ";
		try {
			BigDecimal adminUserId = DataAccessUtils.singleResult(jdbcTemplate.query(sql,
					new Object[] { transactionValidation.getAdminUserId(), transactionValidation.getOtp() },
					new SingleColumnRowMapper<BigDecimal>()));
			if (adminUserId != null) {
				response = sendToOtherCall(transactionValidation, "sendCryptoToOther");
				if (response != null && !response.isEmpty()) {
					JSONObject partsData = new JSONObject(response);
					response = partsData.getString("error");
					if (partsData.getString("error").equalsIgnoreCase("success")) {
						error.setError_data(0);
						error.setError_msg("");
					} else {
						log.info("Error in call Admin send to other api: {}", partsData.getString("error"));
						error.setError_data(1);
						error.setError_msg(response);
					}
				} else {
					error.setError_data(1);
					error.setError_msg("Rest api Response blank.");
				}
			} else {
				error.setError_data(1);
				error.setError_msg("Invalid Admin User Id Or OTP.");
			}
		} catch (Exception e) {
			log.error("Error in :", e);
			error.setError_data(1);
			error.setError_msg("Internal Api Error.");
		}
		transactionResponse.setError(error);
		return transactionResponse;
	}

	public String sendToOtherCall(TransactionValidation transactionValidation, String subUrl) {
		String response = "";
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("amount", transactionValidation.getAmount());
			jsonObject.put("currencyid", String.valueOf(transactionValidation.getCurrencyId()));
			jsonObject.put("currency", transactionValidation.getCurrency());
			jsonObject.put("tokenType", transactionValidation.getTokenType());
			jsonObject.put("password", transactionValidation.getPassword());
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			String url = env.getProperty("project.paybito.admin.model.api") + "admin/" + subUrl;
			RestTemplate restTemplate = new RestTemplate();
			HttpEntity<String> entity = new HttpEntity<>(jsonObject.toString(), headers);
			response = restTemplate.postForObject(url, entity, String.class);
		} catch (Exception e) {
			log.error("Error in Admin Model send api calling: ", e);
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> exchangeReport(ReportRequest reportRequest) {
		Map<String, Object> response = new HashMap<>();
		error = new ErrorResponse();
		ReportRequest requestInput = new ReportRequest();
		try {
			requestInput.setReportNumber(reportRequest.getReportNumber());
			requestInput.setFromDate(reportRequest.getFromDate() == null || !reportRequest.getFromDate().isEmpty()
					? reportRequest.getFromDate()
					: null);
			requestInput.setToDate(reportRequest.getToDate() == null || !reportRequest.getToDate().isEmpty()
					? reportRequest.getToDate()
					: null);
			requestInput.setAction(reportRequest.getAction() == null || !reportRequest.getAction().isEmpty()
					? reportRequest.getAction()
					: null);
			requestInput.setCurrencyId(reportRequest.getCurrencyId() != 0 ? reportRequest.getCurrencyId() : 0);
			requestInput.setExchange(reportRequest.getExchange() == null || !reportRequest.getExchange().isEmpty()
					? reportRequest.getExchange()
					: null);
			requestInput.setAssetPair(reportRequest.getAssetPair() == null || !reportRequest.getAssetPair().isEmpty()
					? reportRequest.getAssetPair()
					: null);
			requestInput.setCurrency(reportRequest.getCurrency() == null || !reportRequest.getCurrency().isEmpty()
					? reportRequest.getCurrency()
					: null);
			requestInput.setBaseCurrency(
					reportRequest.getBaseCurrency() == null || !reportRequest.getBaseCurrency().isEmpty()
							? reportRequest.getBaseCurrency()
							: null);
			requestInput.setCustomerId(reportRequest.getCustomerId() != 0 ? reportRequest.getCustomerId() : 0);
			SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
					.withProcedureName("get_exchage_all_reports");
			MapSqlParameterSource input = new MapSqlParameterSource();
			input.addValue("p_report_type_id", requestInput.getReportNumber());
			input.addValue("p_start_date", requestInput.getFromDate());
			input.addValue("p_end_date", requestInput.getToDate());
			input.addValue("p_action", requestInput.getAction());
			input.addValue("p_currency_id", requestInput.getCurrencyId());
			input.addValue("p_exchange", requestInput.getExchange());
			input.addValue("p_asset_pair", requestInput.getAssetPair());
			input.addValue("p_currency", requestInput.getCurrency());
			input.addValue("p_basecurrency", requestInput.getBaseCurrency());
			input.addValue("p_customer_id", requestInput.getCustomerId());
			Map<String, Object> result = simpleJdbcCall.execute(input);
			List<Object> reportDetailsList = (List<Object>) result.get("GET_REPORT_DETAILS");
			List<Object> profitDetailsList = (List<Object>) result.get("GET_PROFIT_DETAILS");
			response.put("Output", reportDetailsList);
			response.put("profitDetails", profitDetailsList);
			error.setError_data(0);
			error.setError_msg("");
		} catch (Exception e) {
			e.printStackTrace();
			error.setError_data(1);
			error.setError_msg(e.getMessage());
		}
		response.put("error", error);
		return response;
	}

	public Map<String, Object> getAllFuturesExchangeReport() {
		Map<String, Object> response = new HashMap<>();
		List<ReportName> reportList = new ArrayList<>();
		String sql = "select * from future_exchange_report_master";
		try {
			reportList = jdbcTemplate.query(sql, new BeanPropertyRowMapper<ReportName>(ReportName.class));
			;
			response.put("value", reportList);
			response.put("error", "");
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			response.put("error", e.getMessage());
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> futuresExchangeReport(ReportRequest reportRequest) {
		Map<String, Object> response = new HashMap<>();
		error = new ErrorResponse();
		ReportRequest requestInput = new ReportRequest();
		try {
			requestInput.setReportNumber(reportRequest.getReportNumber());
			requestInput.setFromDate(reportRequest.getFromDate() == null || !reportRequest.getFromDate().isEmpty()
					? reportRequest.getFromDate()
					: null);
			requestInput.setToDate(reportRequest.getToDate() == null || !reportRequest.getToDate().isEmpty()
					? reportRequest.getToDate()
					: null);
			requestInput.setAction(reportRequest.getAction() == null || !reportRequest.getAction().isEmpty()
					? reportRequest.getAction()
					: null);
			requestInput.setCurrencyId(reportRequest.getCurrencyId() != 0 ? reportRequest.getCurrencyId() : 0);
			requestInput.setExchange(reportRequest.getExchange() == null || !reportRequest.getExchange().isEmpty()
					? reportRequest.getExchange()
					: null);
			requestInput.setAssetPair(reportRequest.getAssetPair() == null || !reportRequest.getAssetPair().isEmpty()
					? reportRequest.getAssetPair()
					: null);
			requestInput.setCurrency(reportRequest.getCurrency() == null || !reportRequest.getCurrency().isEmpty()
					? reportRequest.getCurrency()
					: null);
			requestInput.setBaseCurrency(
					reportRequest.getBaseCurrency() == null || !reportRequest.getBaseCurrency().isEmpty()
							? reportRequest.getBaseCurrency()
							: null);
			requestInput.setCustomerId(reportRequest.getCustomerId() != 0 ? reportRequest.getCustomerId() : 0);
			SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
					.withProcedureName("GET_FUTURE_EXCHAGE_ALL_REPORTS");
			MapSqlParameterSource input = new MapSqlParameterSource();
			input.addValue("p_report_type_id", requestInput.getReportNumber());
			input.addValue("p_start_date", requestInput.getFromDate());
			input.addValue("p_end_date", requestInput.getToDate());
			input.addValue("p_action", requestInput.getAction());
			input.addValue("p_currency_id", requestInput.getCurrencyId());
			input.addValue("p_exchange", requestInput.getExchange());
			input.addValue("p_asset_pair", requestInput.getAssetPair());
			input.addValue("p_currency", requestInput.getCurrency());
			input.addValue("p_basecurrency", requestInput.getBaseCurrency());
			input.addValue("p_customer_id", requestInput.getCustomerId());
			Map<String, Object> result = simpleJdbcCall.execute(input);
			List<Object> reportDetailsList = (List<Object>) result.get("GET_REPORT_DETAILS");
			List<Object> profitDetailsList = (List<Object>) result.get("GET_PROFIT_DETAILS");
			response.put("Output", reportDetailsList);
			response.put("profitDetails", profitDetailsList);
			error.setError_data(0);
			error.setError_msg("");
		} catch (Exception e) {
			e.printStackTrace();
			error.setError_data(1);
			error.setError_msg(e.getMessage());
		}
		response.put("error", error);
		return response;
	}

	public Map<String, Object> getAllFuturesUserReport() {
		Map<String, Object> response = new HashMap<>();
		List<ReportName> reportList = new ArrayList<>();
		String sql = "select * from future_user_report_master";
		try {
			reportList = jdbcTemplate.query(sql, new BeanPropertyRowMapper<ReportName>(ReportName.class));
			;
			response.put("value", reportList);
			response.put("error", "");
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			response.put("error", e.getMessage());
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> futuresBalanceComparison() {
		Map<String, Object> response = new HashMap<>();
		List<FuturesBalanceComparison> resultList = new ArrayList<>();
		try {
			SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
					.withProcedureName("F_GETEXCHANGE_TOTAL_ASSET").returningResultSet("P_ASSET_DETAILS",
							BeanPropertyRowMapper.newInstance(FuturesBalanceComparison.class));
			Map<String, Object> result = simpleJdbcCall.execute();
			resultList = (List<FuturesBalanceComparison>) result.get("P_ASSET_DETAILS");
			response.put("result", resultList);
			error = new ErrorResponse();
		} catch (Exception e) {
			e.printStackTrace();
			error = new ErrorResponse(1, e.getMessage());
		}
		response.put("error", error);
		return response;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> getAssetWiseBalanceComparison() {
		Map<String, Object> response = new HashMap<>();
		error = new ErrorResponse();
		final String url = env.getProperty("project.paybito.admin.model.api") + "admin/walletBalance";
		List<NodeBalance> nodeList = new ArrayList<>();
		RestTemplate restTemp = new RestTemplate();
		HttpHeaders header = new HttpHeaders();
		header.setContentType(MediaType.APPLICATION_JSON);
		JSONObject json = new JSONObject();
		double tradableAmount = 0;
		double tradableAmountInUsd = 0;
		double totalTradableAmountInUsd = 0;
		double transactionCharge = 0;
		try {
			SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
					.withProcedureName("getexchange_total_asset");
			Map<String, Object> result = simpleJdbcCall.execute();
			double tradeFees = Double.parseDouble(result.get("P_TRADE_FEES_PREC").toString());
			double otherTradeValueInUsd = Double.parseDouble(result.get("P_OTH_TRADE_VALUE_USD").toString());
			List<Map<String, Object>> returnResult = (List<Map<String, Object>>) result.get("P_ASSET_DETAILS");
			for (Map<String, Object> map : returnResult) {
				NodeBalance nodeBalance = new NodeBalance();
				String currencyId = (map.get("CURRENCY_ID").toString());
				double dbBal = Double.parseDouble(map.get("ASSET_TOTAL").toString());
				double sendPendingBalance = Double.parseDouble(map.get("BATCH_PENDING_AMOUNT").toString());
				double coinOwnerBalance = Double.parseDouble(map.get("COIN_OWNER_BALANCE").toString());
				double withdrawableAmount = Double.parseDouble(map.get("WITHDRAWABLE_AMOUNT").toString());
				double coldWalletBalance = Double.parseDouble(map.get("COLD_WALLET_BALANCE").toString());
				double liquidityBalance = Double.parseDouble(map.get("LIQUIDITY_BALANCE").toString());
				if (Integer.parseInt(map.get("IS_SEND").toString()) == 1) {
					json.put("currencyid", currencyId);
					json.put("currency", map.get("CURRENCY_CODE").toString());
					HttpEntity<String> entity = new HttpEntity<>(json.toString(), header);
					ResponseEntity<String> res = restTemp.postForEntity(url, entity, String.class);
					if (res != null && res.getStatusCode() == HttpStatus.OK) {
						if (res.getBody() != null) {
							JSONObject jsonObject = new JSONObject(res.getBody());
							if (!jsonObject.get("nodeBalance").equals(null)) {
								nodeBalance.setCurrencyId(currencyId);
								nodeBalance.setCurrencyCode(map.get("CURRENCY_CODE").toString());
								double nodeBal = 0;
								nodeBal = Double.parseDouble(jsonObject.getString("nodeBalance"));
								nodeBalance.setNodeBalance(jsonObject.getString("nodeBalance"));
//								}
								double difference = (nodeBal + coldWalletBalance + liquidityBalance) - dbBal;
								tradableAmount = (nodeBal + coldWalletBalance + liquidityBalance)
										- (withdrawableAmount + sendPendingBalance + coinOwnerBalance);
								nodeBalance.setColdWalletBalance(map.get("COLD_WALLET_BALANCE").toString());
								nodeBalance.setLiquidityBalance(map.get("LIQUIDITY_BALANCE").toString());
								nodeBalance.setDbBalance(map.get("ASSET_TOTAL").toString());
								nodeBalance.setSendPendingAmount(map.get("BATCH_PENDING_AMOUNT").toString());
								nodeBalance.setCoinOwnerBalance(map.get("COIN_OWNER_BALANCE").toString());
								nodeBalance.setWithdrawableAmount(map.get("WITHDRAWABLE_AMOUNT").toString());
								nodeBalance.setBalanceDifference(String.valueOf(difference));
								nodeBalance.setTradableAmount(String.valueOf(tradableAmount));
								if (tradableAmount > 0) {
									tradableAmountInUsd = tradableAmount
											* Double.parseDouble(map.get("SELL_PRICE").toString());
									transactionCharge = transactionCharge + (tradableAmountInUsd * tradeFees);
									nodeBalance.setMarketPriceInUsd(map.get("SELL_PRICE").toString());
								} else {
									tradableAmountInUsd = tradableAmount
											* Double.parseDouble(map.get("BUY_PRICE").toString());
									transactionCharge = transactionCharge - (tradableAmountInUsd * tradeFees);
									nodeBalance.setMarketPriceInUsd(map.get("BUY_PRICE").toString());
								}
								String tradeAmountInUsd = String.format("%.2f", tradableAmountInUsd);
								nodeBalance.setTradableAmountInUsd(tradeAmountInUsd);
								totalTradableAmountInUsd = totalTradableAmountInUsd + tradableAmountInUsd;
							} else {
								nodeBalance.setCurrencyId(currencyId);
								nodeBalance.setCurrencyCode(map.get("CURRENCY_CODE").toString());
								nodeBalance.setDbBalance(map.get("ASSET_TOTAL").toString());
								nodeBalance.setSendPendingAmount(map.get("BATCH_PENDING_AMOUNT").toString());
								nodeBalance.setCoinOwnerBalance(map.get("COIN_OWNER_BALANCE").toString());
								nodeBalance.setWithdrawableAmount(map.get("WITHDRAWABLE_AMOUNT").toString());
								nodeBalance.setColdWalletBalance(map.get("COLD_WALLET_BALANCE").toString());
								nodeBalance.setLiquidityBalance(map.get("LIQUIDITY_BALANCE").toString());
								double difference = (0 + coldWalletBalance + liquidityBalance) - dbBal;
								nodeBalance.setBalanceDifference(String.valueOf(difference));
								tradableAmount = (0 + coldWalletBalance + liquidityBalance)
										- (withdrawableAmount + sendPendingBalance + coinOwnerBalance);
								nodeBalance.setTradableAmount(String.valueOf(tradableAmount));
								if (tradableAmount > 0) {
									tradableAmountInUsd = tradableAmount
											* Double.parseDouble(map.get("SELL_PRICE").toString());
									transactionCharge = transactionCharge + (tradableAmountInUsd * tradeFees);
									nodeBalance.setMarketPriceInUsd(map.get("SELL_PRICE").toString());
								} else {
									tradableAmountInUsd = tradableAmount
											* Double.parseDouble(map.get("BUY_PRICE").toString());
									transactionCharge = transactionCharge - (tradableAmountInUsd * tradeFees);
									nodeBalance.setMarketPriceInUsd(map.get("BUY_PRICE").toString());
								}
								String tradeAmountInUsd = String.format("%.2f", tradableAmountInUsd);
								nodeBalance.setTradableAmountInUsd(tradeAmountInUsd);
								totalTradableAmountInUsd = totalTradableAmountInUsd + tradableAmountInUsd;
							}
						} else {
							nodeBalance.setNodeBalance("Only available in live version.");
						}
					} else {
						nodeBalance.setNodeBalance("Only available in live version.");
					}
				} else {
					nodeBalance.setCurrencyId(currencyId);
					nodeBalance.setCurrencyCode(map.get("CURRENCY_CODE").toString());
					nodeBalance.setDbBalance(map.get("ASSET_TOTAL").toString());
					nodeBalance.setSendPendingAmount(map.get("BATCH_PENDING_AMOUNT").toString());
					nodeBalance.setCoinOwnerBalance(map.get("COIN_OWNER_BALANCE").toString());
					nodeBalance.setWithdrawableAmount(map.get("WITHDRAWABLE_AMOUNT").toString());
					nodeBalance.setColdWalletBalance(map.get("COLD_WALLET_BALANCE").toString());
					nodeBalance.setLiquidityBalance(map.get("LIQUIDITY_BALANCE").toString());
					double difference = (coldWalletBalance + liquidityBalance) - dbBal;
					nodeBalance.setBalanceDifference(String.valueOf(difference));
					tradableAmount = (coldWalletBalance + liquidityBalance)
							- (withdrawableAmount + sendPendingBalance + coinOwnerBalance);
					nodeBalance.setTradableAmount(String.valueOf(tradableAmount));
					if (tradableAmount > 0) {
						tradableAmountInUsd = tradableAmount * Double.parseDouble(map.get("SELL_PRICE").toString());
						transactionCharge = transactionCharge + (tradableAmountInUsd * tradeFees);
						nodeBalance.setMarketPriceInUsd(map.get("SELL_PRICE").toString());
					} else {
						tradableAmountInUsd = tradableAmount * Double.parseDouble(map.get("BUY_PRICE").toString());
						transactionCharge = transactionCharge - (tradableAmountInUsd * tradeFees);
						nodeBalance.setMarketPriceInUsd(map.get("BUY_PRICE").toString());
					}
					String tradeAmountInUsd = String.format("%.2f", tradableAmountInUsd);
					nodeBalance.setTradableAmountInUsd(tradeAmountInUsd);
					totalTradableAmountInUsd = totalTradableAmountInUsd + tradableAmountInUsd;
				}
				nodeList.add(nodeBalance);
			}
			String totalAmountInUsd = String.format("%.2f", totalTradableAmountInUsd);
			String totalFeesInUsd = String.format("%.2f", transactionCharge);
			String totalAssetInUsd = String.format("%.2f",
					totalTradableAmountInUsd - (transactionCharge + otherTradeValueInUsd));
			jdbcTemplate.update(INSERT_ASSET_BALANCE, totalAmountInUsd, totalFeesInUsd, otherTradeValueInUsd,
					totalAssetInUsd);
			response.put("Result", nodeList);
			response.put("totalTradableAmountInUsd", totalAmountInUsd);
			response.put("transactionCharge", totalFeesInUsd);
			response.put("otherTradeValueInUsd", otherTradeValueInUsd);
			response.put("totalAssetInUsd", totalAssetInUsd);
			error.setError_data(0);
			error.setError_msg("");
		} catch (Exception e) {
			e.printStackTrace();
			error.setError_data(1);
			error.setError_msg(e.getMessage());
		}
		response.put("error", error);
		return response;
	}

	public Map<String, Object> sendAssetBalanceComparison() {
		Map<String, Object> response = new HashMap<>();
		error = new ErrorResponse();
		final String url = env.getProperty("project.paybito.admin.model.api") + "admin/walletBalance";
		List<NodeBalance> nodeList = new ArrayList<>();
		RestTemplate restTemp = new RestTemplate();
		HttpHeaders header = new HttpHeaders();
		header.setContentType(MediaType.APPLICATION_JSON);
		JSONObject json = new JSONObject();
		String sql = "SELECT A.CURRENCY_ID,B.CURRENCY_CODE,SUM(A.AMOUNT) SEND_ASSET_TOTAL FROM TRANSACTION_VALIDATION A,CURRENCY_MASTER B "
				+ "WHERE A.CURRENCY_ID=B.CURRENCY_ID  AND B.IS_ACTIVE=1 AND A.CONFIRM=0 GROUP BY A.CURRENCY_ID,B.CURRENCY_CODE ORDER BY A.CURRENCY_ID";
		try {
			List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sql);
			if (resultList.size() > 0) {
				for (Map<String, Object> map : resultList) {
					NodeBalance nodeBalance = new NodeBalance();
					String currencyId = (map.get("CURRENCY_ID").toString());
					json.put("currencyid", currencyId);
					HttpEntity<String> entity = new HttpEntity<>(json.toString(), header);
					ResponseEntity<String> res = restTemp.postForEntity(url, entity, String.class);
					if (res != null && res.getStatusCode() == HttpStatus.OK) {
						if (res.getBody() != null) {
							JSONObject jsonObject = new JSONObject(res.getBody());
							if (!jsonObject.get("nodeBalance").equals(null)) {
								nodeBalance.setCurrencyId(currencyId);
								nodeBalance.setCurrencyCode(map.get("CURRENCY_CODE").toString());
								double assetNodeBal = Double.parseDouble(jsonObject.getString("nodeBalance"));
								double assetDbBal = Double.parseDouble(map.get("SEND_ASSET_TOTAL").toString());
								double difference = assetNodeBal - assetDbBal;
								nodeBalance.setNodeBalance(jsonObject.getString("nodeBalance"));
								nodeBalance.setDbBalance(map.get("SEND_ASSET_TOTAL").toString());
								nodeBalance.setBalanceDifference(String.valueOf(difference));
							} else {
								nodeBalance.setCurrencyId(currencyId);
								nodeBalance.setCurrencyCode(map.get("CURRENCY_CODE").toString());
								nodeBalance.setDbBalance(map.get("SEND_ASSET_TOTAL").toString());
								double assetDbBal = Double.parseDouble(map.get("SEND_ASSET_TOTAL").toString());
								double difference = 0 - assetDbBal;
								nodeBalance.setBalanceDifference(String.valueOf(difference));
							}
						} else {
							nodeBalance.setNodeBalance("Only available in live version.");
						}
					} else {
						nodeBalance.setNodeBalance("Only available in live version.");
					}
					nodeList.add(nodeBalance);
				}
				response.put("result", nodeList);
				error.setError_data(0);
				error.setError_msg("");
			}
		} catch (Exception e) {
			e.printStackTrace();
			error.setError_data(1);
			error.setError_msg(e.getMessage());
		}
		response.put("error", error);
		return response;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, Object> getAdminBankDetails() {
		Map<String, Object> result = new HashMap<>();
		List<AdminBankDetails> adminBankDetailsList = new ArrayList<>();
		try {
			adminBankDetailsList = jdbcTemplate.query(ADMIN_BANK_DETAILS,
					new BeanPropertyRowMapper(AdminBankDetails.class));
			if (!adminBankDetailsList.isEmpty()) {
				result.put("adminBankDetails", adminBankDetailsList);
				result.put("error", "");
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			result.put("error", e.getMessage());
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> getColdWalletBalance() {
		Map<String, Object> response = new HashMap<>();
		List<ColdWalletBalance> coldWalletBalanceList = new ArrayList<>();
		try {
			SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
					.withProcedureName("GET_COLD_WALLET_BALANCE").returningResultSet("P_COLD_WALLET_BALANCE",
							BeanPropertyRowMapper.newInstance(ColdWalletBalance.class));
			Map<String, Object> result = simpleJdbcCall.execute();
			coldWalletBalanceList = (List<ColdWalletBalance>) result.get("P_COLD_WALLET_BALANCE");
			if (!coldWalletBalanceList.isEmpty()) {
				response.put("coldWalletBalance", coldWalletBalanceList);
				response.put("error", "");
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			response.put("error", e.getMessage());
		}
		return response;
	}

	public Map<String, Object> updateColdWalletBalance(ColdWalletBalance coldWalletBalance) {
		Map<String, Object> response = new HashMap<>();
		try {
			SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
					.withProcedureName("UPDATE_COLD_WALLET_BALANCE");
			MapSqlParameterSource input = new MapSqlParameterSource();
			input.addValue("P_CURRENCY_ID", coldWalletBalance.getCurrencyId());
			input.addValue("P_BALANCE", coldWalletBalance.getBalance());
			Map<String, Object> result = simpleJdbcCall.execute(input);
			String message = (String) result.get("MESSAGE");
			int returnId = ((BigDecimal) result.get("RETURN_ID")).intValue();
			if (returnId == 1) {
				response.put("error", "");
			} else {
				response.put("error", message);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			response.put("error", e.getMessage());
		}
		return response;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, Object> getColdWalletAddress() {
		Map<String, Object> result = new HashMap<>();
		List<ColdWalletAddress> coldWalletAddressList = new ArrayList<>();
		try {
			coldWalletAddressList = jdbcTemplate.query(COLD_WALLET_ADDRESSES,
					new BeanPropertyRowMapper(ColdWalletAddress.class));
			if (!coldWalletAddressList.isEmpty()) {
				result.put("coldWalletAddress", coldWalletAddressList);
				result.put("error", "");
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			result.put("error", e.getMessage());
		}
		return result;
	}

	public Map<String, Object> addRemittance(Remittance remittance) {
		Map<String, Object> response = new HashMap<>();
		try {
			SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
					.withProcedureName("BANK_REMITTANCE_INSERT_SP");
			MapSqlParameterSource input = new MapSqlParameterSource();
			input.addValue("P_ID", remittance.getId());
			input.addValue("P_REMITTANCE_DATE", remittance.getRemittanceDate());
			input.addValue("P_REMITTANCE_OUT_INR", remittance.getRemittanceOutInr());
			input.addValue("P_INR_FEE", remittance.getInrFee());
			input.addValue("P_REMITTANCE_IN_USD", remittance.getRemittanceInUsd());
			input.addValue("P_USD_FEE", remittance.getUsdFee());
			input.addValue("P_ACTION", remittance.getAction());
			Map<String, Object> result = simpleJdbcCall.execute(input);
			int lastInsertedId = ((BigDecimal) result.get("LAST_INSERT_ID")).intValue();
			String message = (String) result.get("MESSAGE");
			if (lastInsertedId != 0) {
				response.put("lastInsertedId", lastInsertedId);
				response.put("error", message);
			} else {
				response.put("error", message);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			response.put("error", e.getMessage());
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> showAllRemittance() {
		Map<String, Object> response = new HashMap<>();
		List<Remittance> remittanceList = new ArrayList<>();
		try {
			SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
					.withProcedureName("GET_BANK_REMITTANCE_DETAILS").returningResultSet("P_BANK_REMITTANCE_DETAILS",
							BeanPropertyRowMapper.newInstance(Remittance.class));
			Map<String, Object> result = simpleJdbcCall.execute();
			remittanceList = (List<Remittance>) result.get("P_BANK_REMITTANCE_DETAILS");
			response.put("allRemittance", remittanceList);
			response.put("error", "");
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			response.put("error", e.getMessage());
		}
		return response;
	}

	public Map<String, Object> coinListingByPaybito(ReportRequest reportRequest) {
		Map<String, Object> response = new HashMap<>();
		error = new ErrorResponse();
		String sql = "";
		try {
			if (reportRequest.getCurrency() != null && !reportRequest.getCurrency().equalsIgnoreCase("null")) {
				sql = "select currency_code from currency_master where is_active = 1 and listing_coin_flag = 1 "
						+ " and currency_code= '" + reportRequest.getCurrency().toUpperCase() + "'";
			} else {
				sql = "select currency_code from currency_master where is_active = 1 and listing_coin_flag = 1";
			}
			List<Map<String, Object>> coins = jdbcTemplate.queryForList(sql);
			response.put("coins", coins);
			error.setError_data(0);
			error.setError_msg("");
		} catch (Exception e) {
			e.printStackTrace();
			error.setError_data(1);
			error.setError_msg(e.getMessage());
		}
		response.put("error", error);
		return response;
	}

	public Map<String, Object> coinLiabilityReport(ReportRequest reportRequest) {
		Map<String, Object> response = new HashMap<>();
		error = new ErrorResponse();
		final String url = env.getProperty("project.paybito.admin.model.api") + "admin/walletBalance";
		String query = "select currency_id from currency_master where currency_code = ? ";
		try {
			BigDecimal currencyId = DataAccessUtils
					.singleResult(jdbcTemplate.query(query, new Object[] { reportRequest.getCurrency().toUpperCase() },
							new SingleColumnRowMapper<BigDecimal>()));
			if (currencyId != null) {
				double nodeBalance = 0;
				RestTemplate restTemp = new RestTemplate();
				HttpHeaders header = new HttpHeaders();
				header.setContentType(MediaType.APPLICATION_JSON);
				JSONObject json = new JSONObject();
				json.put("currencyid", currencyId.toString());
				HttpEntity<String> entity = new HttpEntity<String>(json.toString(), header);
				String res = restTemp.postForObject(url, entity, String.class);
				if (res != null) {
					JSONObject obj = new JSONObject(res);
					nodeBalance = !obj.get("nodeBalance").equals(null)
							? Double.parseDouble(obj.get("nodeBalance").toString())
							: 0;
				}
				SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
						.withProcedureName("COIN_ASSET_LIABILITY_REPORT");
				MapSqlParameterSource input = new MapSqlParameterSource();
				input.addValue("P_CURRENCY", reportRequest.getCurrency().toUpperCase());
				input.addValue("P_CURRENCY_NODE_BALANCE", nodeBalance);
				Map<String, Object> result = simpleJdbcCall.execute(input);
				response.put("response", result);
				error.setError_data(0);
				error.setError_msg("");
			} else {
				error.setError_data(1);
				error.setError_msg("Invalid currency");
			}
		} catch (Exception e) {
			e.printStackTrace();
			error.setError_data(1);
			error.setError_msg(e.getMessage());
		}
		response.put("error", error);
		return response;
	}
}
