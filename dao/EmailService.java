package com.project.admin.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.project.admin.config.TOTP;
//import com.btc.adapter.api.to.PaymentProcessingResponseTo;
import com.project.admin.model.ErrorResponse;

@Service
public class EmailService {

	@Autowired
	Environment env;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private DataSource dataSource;
	@Autowired
	private MailContentBuilderService mailContentBuilderService;
	@Autowired
	private MailClientService mailClientService;
	@Autowired
	RestTemplateBuilder restTemplateBuilder;

	Connection conn = null; // create connection instance to connect with database
	PreparedStatement ps = null; // create Prepared statement instance to run sql query

	private static final Logger log = LoggerFactory.getLogger(EmailService.class);

	private static final String GOOGLE_RECAPTCHA_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

	/*
	 * public void sendPendingBuyMail(List<PaymentProcessingResponseTo>
	 * listConfirm,String currency,Connection conn) { String
	 * currency_name="",currency_code=""; double igst=0,amount=0,txn_charge_mail=0;
	 * if(currency.equals("btc")) {
	 * currency_name=getValueFromSettings("crypto_name_1", conn);
	 * currency_code=getValueFromSettings("crypto_code_1", conn);
	 * txn_charge_mail=Double.parseDouble(getValueFromSettings("buy_txn_charges",
	 * currency, conn));
	 * 
	 * Iterator<PaymentProcessingResponseTo> itt = listConfirm.iterator();
	 * 
	 * while(itt.hasNext()){ PaymentProcessingResponseTo itr = itt.next();
	 * 
	 * //igst=Double.parseDouble(itr.getChargeCGST())+Double.parseDouble(itr.
	 * getChargeSGST());
	 * amount=Double.parseDouble(itr.getDebit_fiat_amount())-Double.parseDouble(itr.
	 * getTxnCharge()); System.out.println(" Amount :"+ amount);
	 * 
	 * //send mail
	 * sendMailBuy(itr.getCustomer_Id(),itr.getOrderID(),itr.getClosing_btc_bal(),
	 * itr.getClosing_fiat_bal(),Double.parseDouble(itr.getCredit_btc_amount()),
	 * Double.parseDouble(itr.getDebit_fiat_amount()),txn_charge_mail,Double.
	 * parseDouble(itr.getTxnCharge()),igst,amount,currency_name,currency_code,"",
	 * conn);
	 * 
	 * } } else if(currency.equals("hcx")) {
	 * currency_name=getValueFromSettings("crypto_name_5", conn);
	 * currency_code=getValueFromSettings("crypto_name_5", conn);
	 * txn_charge_mail=Double.parseDouble(getValueFromSettings("buy_txn_charges",
	 * currency, conn));
	 * 
	 * Iterator<PaymentProcessingResponseTo> itt = listConfirm.iterator();
	 * 
	 * while(itt.hasNext()){ PaymentProcessingResponseTo itr = itt.next();
	 * 
	 * //igst=Double.parseDouble(itr.getChargeCGST())+Double.parseDouble(itr.
	 * getChargeSGST());
	 * amount=Double.parseDouble(itr.getDebit_fiat_amount())-Double.parseDouble(itr.
	 * getMiningFees())-Double.parseDouble(itr.getTxnCharge());
	 * 
	 * System.out.println(" Amount:" + amount); //send mail
	 * //sendMailBuyHCX(itr.getCustomer_Id(),itr.getOrderID(),itr.
	 * getClosingHCXBalance(),itr.getClosing_fiat_bal(),Double.parseDouble(itr.
	 * getCreditHCXAmount()),Double.parseDouble(itr.getDebit_fiat_amount()),Double.
	 * parseDouble(itr.getMiningFees()),txn_charge_mail,Double.parseDouble(itr.
	 * getTxnCharge()),igst,amount,currency_name,currency_code,"",conn); } } else
	 * if(currency.equals("bch")) {
	 * currency_name=getValueFromSettings("crypto_name_3", conn);
	 * currency_code=getValueFromSettings("crypto_code_3", conn);
	 * txn_charge_mail=Double.parseDouble(getValueFromSettings("buy_txn_charges",
	 * currency, conn));
	 * 
	 * Iterator<PaymentProcessingResponseTo> itt = listConfirm.iterator();
	 * 
	 * while(itt.hasNext()){ PaymentProcessingResponseTo itr = itt.next();
	 * 
	 * // igst=Double.parseDouble(itr.getChargeCGST())+Double.parseDouble(itr.
	 * getChargeSGST());
	 * amount=Double.parseDouble(itr.getDebit_fiat_amount())-Double.parseDouble(itr.
	 * getTxnCharge());
	 * 
	 * //send mail
	 * sendMailBuy(itr.getCustomer_Id(),itr.getOrderID(),itr.getClosingBccBalance(),
	 * itr.getClosing_fiat_bal(),Double.parseDouble(itr.getCreditBccAmount()),Double
	 * .parseDouble(itr.getDebit_fiat_amount()),txn_charge_mail,Double.parseDouble(
	 * itr.getTxnCharge()),igst,amount,currency_name,currency_code,"",conn);
	 * 
	 * } } else if(currency.equals("eth")) {
	 * currency_name=getValueFromSettings("crypto_name_4", conn);
	 * currency_code=getValueFromSettings("crypto_code_4", conn);
	 * txn_charge_mail=Double.parseDouble(getValueFromSettings("buy_txn_charges",
	 * currency, conn));
	 * 
	 * Iterator<PaymentProcessingResponseTo> itt = listConfirm.iterator();
	 * 
	 * while(itt.hasNext()){ PaymentProcessingResponseTo itr = itt.next();
	 * 
	 * // igst=Double.parseDouble(itr.getChargeCGST())+Double.parseDouble(itr.
	 * getChargeSGST());
	 * amount=Double.parseDouble(itr.getDebit_fiat_amount())-Double.parseDouble(itr.
	 * getTxnCharge());
	 * 
	 * //send mail
	 * sendMailBuy(itr.getCustomer_Id(),itr.getOrderID(),itr.getClosingEthBalance(),
	 * itr.getClosing_fiat_bal(),Double.parseDouble(itr.getCreditEthAmount()),Double
	 * .parseDouble(itr.getDebit_fiat_amount()),txn_charge_mail,Double.parseDouble(
	 * itr.getTxnCharge()),igst,amount,currency_name,currency_code,"",conn);
	 * 
	 * } } else if(currency.equals("iec")) {
	 * currency_name=getValueFromSettings("crypto_name_6", conn);
	 * currency_code=getValueFromSettings("crypto_name_6", conn);
	 * 
	 * Iterator<PaymentProcessingResponseTo> itt = listConfirm.iterator();
	 * 
	 * while(itt.hasNext()){ PaymentProcessingResponseTo itr = itt.next();
	 * 
	 * igst=Double.parseDouble(itr.getChargeCGST())+Double.parseDouble(itr.
	 * getChargeSGST());
	 * amount=Double.parseDouble(itr.getDebit_fiat_amount())-Double.parseDouble(itr.
	 * getMiningFees())-Double.parseDouble(itr.getTxnCharge())-igst;
	 * 
	 * System.out.println(" Amount:" + amount); //send mail
	 * //sendMailBuyHCX(itr.getCustomer_Id(),itr.getOrderID(),itr.
	 * getClosingHCXBalance(),itr.getClosing_fiat_bal(),Double.parseDouble(itr.
	 * getCreditHCXAmount()),Double.parseDouble(itr.getDebit_fiat_amount()),Double.
	 * parseDouble(itr.getMiningFees()),txn_charge_mail,Double.parseDouble(itr.
	 * getTxnCharge()),igst,amount,currency_name,currency_code,"",conn); } } }
	 */

	public String getValueFromSettings(String name, Connection conn) {
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

	public String getValueFromSettings(String name, String currency, Connection conn) {
		String sql = "", val = "";
		try {
			if (currency.equals("btc")) {
				sql = "select btc_value from settings where name=? ";
			} else if (currency.equals("bch")) {
				sql = "select bch_value from settings where name=? ";
			} else if (currency.equals("hcx")) {
				sql = "select hcx_value from settings where name=? ";
			} else if (currency.equals("iec")) {
				sql = "select iec_value from settings where name=? ";
			} else {
				sql = "select btc_value from settings where name=? ";
			}
			ps = conn.prepareStatement(sql);
			ps.setString(1, name);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				val = rs.getString(1);
			}
			rs.close();
			ps.close();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}

		return val;
	}

	public void sendMailBuy(String Customer_ID, String orderID, String closingCryptoBalance, String closingFiatBalance,
			double Credit_btc_amount, double Debit_fiat_amount, double txn_charge_mail, double txn_charge, double igst,
			double amount, String currency_name, String currency_code, String status, Connection conn) {
		try {

			if (conn.isClosed()) {
				conn = dataSource.getConnection();
			}

			String sql = " select u.first_name,u.ssn,u.email,u.phone,u.address,u.city,u.country,u.zip,u.email,u.android_device_token,u.ios_device_token,uas.sound_alert from users u "
					+ " inner join user_app_settings uas on u.user_id=uas.user_id and u.user_id = ? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, Customer_ID);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				LocalDate localDate = LocalDate.now();
				String current_date = DateTimeFormatter.ofPattern("dd/MM/yyy").format(localDate);
				System.out.println(current_date);

				DecimalFormat eightDecimal = new DecimalFormat("#0.00000000");

				// email send to user
				String customer_email = rs.getString("email");
				HashMap<String, String> nameVal = new HashMap<>();

				// heading text change on status pending or not
				String heading = currency_name + " Bought";
				/*
				 * if(status.equalsIgnoreCase("pending")) {
				 * heading=currency_name+" Bought Pending"; }
				 */

				// set name value pair for email template
				nameVal.put("heading", heading);
				nameVal.put("heading2", currency_name + " Bought");
				nameVal.put("closing_crypto_heading", currency_code);
				nameVal.put("closing_crypto_balance_heading", currency_name + " Wallet Balance");
				nameVal.put("user_name", rs.getString("first_name"));
				nameVal.put("phone_no", rs.getString("phone"));
				nameVal.put("email", rs.getString("email"));
				// nameVal.put("pan_no", rs.getString("ssn"));
				nameVal.put("user_address", rs.getString("address") + "," + rs.getString("city") + "-"
						+ rs.getString("zip") + "," + rs.getString("country"));
				nameVal.put("company_name", env.getProperty("admin.company.name"));
//				nameVal.put("company_address", env.getProperty("admin.company.address"));
				nameVal.put("company_email", env.getProperty("admin.company.email"));
				nameVal.put("buy_btc_cash_amount", eightDecimal.format(Credit_btc_amount));
				nameVal.put("order_no", orderID);
				nameVal.put("date", current_date);
				nameVal.put("closing_btc_cash_balance", closingCryptoBalance);
				nameVal.put("closing_fiat_balance", closingFiatBalance);
				nameVal.put("fiat_amount", String.format("%.2f", amount));
				nameVal.put("txn_charge", String.format("%.2f", txn_charge_mail));
				nameVal.put("txn_fee", String.format("%.2f", txn_charge));
				// nameVal.put("igst",String.format("%.2f",igst));
				nameVal.put("total_amount", String.format("%.2f", Debit_fiat_amount));
				// nameVal.put("company_pan_no", env.getProperty("admin.paybito_routing_no"));
				// nameVal.put("company_gst_no", env.getProperty("admin.paybito_gstno"));
				nameVal.put("bank_user_name", env.getProperty("admin.payito_bank_user_name"));
				nameVal.put("bank_name", env.getProperty("admin.paybito_bank_name"));
				nameVal.put("account_no", env.getProperty("admin.paybito_account_no"));
				nameVal.put("ifsc_code", env.getProperty("admin.paybito_routing_no"));

				String mail_content = mailContentBuilderService.build("buy_sell_bitcoin_cash", nameVal);
				String subject = "Transaction Mail From " + env.getProperty("project.company.product") + " ";
				mailClientService.mailthreding(env.getProperty("spring.mail.username"), customer_email, subject,
						mail_content, "");
			}
			rs.close();
			ps.close();
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
	}

	public void sendMailBuyHCX(String Customer_ID, String orderID, String closingCryptoBalance,
			String closingFiatBalance, double Credit_btc_amount, double Debit_fiat_amount, double mining_fee,
			double txn_charge_mail, double txn_charge, double igst, double amount, String currency_name,
			String currency_code, String status, Connection conn) {
		try {

			if (conn.isClosed()) {
				conn = dataSource.getConnection();
			}

			String sql = " select u.first_name,u.ssn,u.email,u.phone,u.address,u.city,u.country,u.zip,u.email,u.android_device_token,u.ios_device_token,uas.sound_alert from users u "
					+ " inner join user_app_settings uas on u.user_id=uas.user_id and u.user_id = ? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, Customer_ID);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				LocalDate localDate = LocalDate.now();
				String current_date = DateTimeFormatter.ofPattern("dd/MM/yyy").format(localDate);

				DecimalFormat sevenDecimal = new DecimalFormat("#0.0000000");

				// email send to user
				String customer_email = rs.getString("email");
				HashMap<String, String> nameVal = new HashMap<>();

				// heading text change on status pending or not
				String heading = currency_name + " Bought";
				/*
				 * if(status.equalsIgnoreCase("pending")) {
				 * heading=currency_name+" Bought Pending"; }
				 */

				// set name value pair for email template
				nameVal.put("heading", heading);
				nameVal.put("heading2", currency_name + " Bought");
				nameVal.put("closing_crypto_heading", currency_code);
				nameVal.put("closing_crypto_balance_heading", currency_name + " Wallet Balance");
				nameVal.put("user_name", rs.getString("first_name"));
				nameVal.put("phone_no", rs.getString("phone"));
				nameVal.put("email", rs.getString("email"));
				// nameVal.put("pan_no", rs.getString("ssn"));
				nameVal.put("user_address", rs.getString("address") + "," + rs.getString("city") + "-"
						+ rs.getString("zip") + "," + rs.getString("country"));
				nameVal.put("company_name", env.getProperty("admin.company.name"));
//				nameVal.put("company_address", env.getProperty("admin.company.address"));
				nameVal.put("company_email", env.getProperty("admin.company.email"));
				nameVal.put("buy_btc_cash_amount", sevenDecimal.format(Credit_btc_amount));
				nameVal.put("order_no", orderID);
				nameVal.put("date", current_date);
				nameVal.put("closing_btc_cash_balance", sevenDecimal.format(Double.parseDouble(closingCryptoBalance)));
				nameVal.put("closing_fiat_balance", closingFiatBalance);
				nameVal.put("fiat_amount", String.format("%.2f", amount));
				nameVal.put("mining_fee", String.format("%.4f", mining_fee));
				nameVal.put("txn_charge", String.format("%.2f", txn_charge_mail));
				nameVal.put("txn_fee", String.format("%.2f", txn_charge));
				// nameVal.put("igst",String.format("%.2f",igst));
				nameVal.put("total_amount", String.format("%.2f", Debit_fiat_amount));
				// nameVal.put("company_pan_no", env.getProperty("admin.paybito_routing_no"));
				// nameVal.put("company_gst_no", env.getProperty("admin.paybito_gstno"));
				nameVal.put("bank_user_name", env.getProperty("admin.payito_bank_user_name"));
				nameVal.put("bank_name", env.getProperty("admin.paybito_bank_name"));
				nameVal.put("account_no", env.getProperty("admin.paybito_account_no"));
				nameVal.put("ifsc_code", env.getProperty("admin.paybito_routing_no"));

				String mail_content = mailContentBuilderService.build("buy_sell_hcx", nameVal);
				String subject = "Transaction Mail From " + env.getProperty("project.company.product") + " ";
				mailClientService.mailthreding(env.getProperty("spring.mail.username"), customer_email, subject,
						mail_content, "");
			}
			rs.close();
			ps.close();
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
	}

	/*
	 * public void send_notification_android(String message,String title,String
	 * devicetoken,int sound,String user_id,String content_id,int
	 * event_id,Connection conn) throws SQLException { String response=""; String
	 * deviceToken=devicetoken; try { String
	 * androidFcmKey=env.getProperty("project.android.ios.key"); String
	 * androidFcmUrl="https://fcm.googleapis.com/fcm/send";
	 * 
	 * RestTemplate restTemplate = new RestTemplate(); HttpHeaders httpHeaders = new
	 * HttpHeaders(); httpHeaders.set("Authorization", "key=" + androidFcmKey);
	 * httpHeaders.set("Content-Type", "application/json"); JSONObject json = new
	 * JSONObject(); JSONObject notification = new JSONObject(); JSONObject data =
	 * new JSONObject();
	 * 
	 * notification.put("title", title); notification.put("body", message);
	 * notification.put("icon", "notify_icon"); if(sound == 0){
	 * notification.put("sound", ""); } else { notification.put("sound", "default");
	 * } json.put("notification", notification);
	 * 
	 * data.put("title", title); data.put("body", message); json.put("data", data);
	 * //data.put("param1", "value1"); //data.put("param2", "value2");
	 * //json.put("data", data);
	 * 
	 * 
	 * json.put("to", deviceToken); json.put("priority", "high");
	 * 
	 * System.out.println("device_token:"+deviceToken+" ,message:"
	 * +message+" , title:"+title+", sound:"+sound);
	 * 
	 * HttpEntity<String> httpEntity = new
	 * HttpEntity<String>(json.toString(),httpHeaders); response =
	 * restTemplate.postForObject(androidFcmUrl,httpEntity,String.class);
	 * System.out.println(response); JSONObject partsData = new
	 * JSONObject(response); if(partsData.optInt("success")==1) { conn =
	 * dataSource.getConnection(); String sql =
	 * "insert into notifications(NOTIFICATION_ID,user_id,notification_message,content_id,event_id) values(NOTIFICATIONS_SEQ.NEXTVAL,?,?,?,?) "
	 * ; ps=conn.prepareStatement(sql); ps.setString(1,user_id); ps.setString(2,
	 * message); ps.setString(3, content_id); ps.setInt(4, event_id);
	 * ps.executeUpdate(); ps.close(); }
	 * 
	 * } catch (JSONException e) { e.printStackTrace(); log.error(e.getMessage()); }
	 * 
	 * }
	 * 
	 * public void send_notification_ios(String message,String title,String
	 * devicetoken,int sound,String user_id,String content_id,int
	 * event_id,Connection conn) throws SQLException { String response=""; String
	 * deviceToken=devicetoken; try { String
	 * androidFcmKey=env.getProperty("project.android.ios.key"); String
	 * androidFcmUrl="https://fcm.googleapis.com/fcm/send";
	 * 
	 * RestTemplate restTemplate = new RestTemplate(); HttpHeaders httpHeaders = new
	 * HttpHeaders(); httpHeaders.set("Authorization", "key=" + androidFcmKey);
	 * httpHeaders.set("Content-Type", "application/json"); // JSONObject data = new
	 * JSONObject(); JSONObject json = new JSONObject(); JSONObject notification =
	 * new JSONObject();
	 * 
	 * 
	 * notification.put("title", title); notification.put("body", message); if(sound
	 * == 0){ notification.put("sound", ""); } else { notification.put("sound",
	 * "default"); } notification.put("badge", "1"); json.put("notification",
	 * notification);
	 * 
	 * //data.put("param1", "value1"); //data.put("param2", "value2");
	 * //json.put("data", data);
	 * 
	 * 
	 * json.put("to", deviceToken); json.put("priority", "high");
	 * 
	 * System.out.println("device_token: "+deviceToken+" ,  message:"
	 * +message+" , title:"+title+" , sound:"+sound); HttpEntity<String> httpEntity
	 * = new HttpEntity<String>(json.toString(),httpHeaders); response =
	 * restTemplate.postForObject(androidFcmUrl,httpEntity,String.class);
	 * System.out.println(response); JSONObject partsData = new
	 * JSONObject(response); if(partsData.optInt("success")==1) { String sql =
	 * "insert into notifications(NOTIFICATION_ID,user_id,notification_message,content_id,event_id) values(NOTIFICATIONS_SEQ.NEXTVAL,?,?,?,?) "
	 * ; ps=conn.prepareStatement(sql); ps.setString(1,user_id); ps.setString(2,
	 * message); ps.setString(3, content_id); ps.setInt(4, event_id);
	 * ps.executeUpdate(); ps.close(); }
	 * 
	 * } catch (JSONException e) { e.printStackTrace(); log.error(e.getMessage()); }
	 * }
	 */

	public String recaptchaService(String recaptcharesponse) {
		String errorMessage = "";
		try {
			Map<String, String> body = new HashMap<>();
			body.put("secret", env.getProperty("project.recaptcha.secret.key"));
			body.put("response", recaptcharesponse);
			ResponseEntity<Map> recaptchaResponseEntity = restTemplateBuilder.build().postForEntity(
					GOOGLE_RECAPTCHA_VERIFY_URL + "?secret={secret}&response={response}", body, Map.class, body);
			Map<String, Object> responseBody = recaptchaResponseEntity.getBody();

			boolean recaptchaSucess = (Boolean) responseBody.get("success");
			if (!recaptchaSucess) {
				List<String> errorCodes = (List) responseBody.get("error-codes");

				errorMessage = errorCodes.stream().map(s -> ErrorResponse.RECAPTCHA_ERROR_CODE.get(s))
						.collect(Collectors.joining(", "));
			} else {
				errorMessage = "success";
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return errorMessage;
	}

	public void sendMailPaybitoOther(String customerId, String toAdd, String amount, String closingBalance,
			String currency, String type) {

		try {
			String sql = " select u.email,u.android_device_token,u.ios_device_token,uas.sound_alert from users u "
					+ " inner join user_app_settings uas on u.user_id=uas.user_id and u.user_id = ? ";
			Map<String, Object> resultMap = new JdbcTemplate(dataSource).queryForMap(sql, customerId);
			if (!resultMap.isEmpty()) {
				String title = currency + " sent successfully";
				String message = amount + " " + currency + " sent to address " + toAdd;

				if (resultMap.get("android_device_token") != null) {
					sendAndroidNotification(message, title, String.valueOf(resultMap.get("android_device_token")),
							Integer.parseInt((String) resultMap.get("sound_alert")), customerId, "", 6);
				}

				if (resultMap.get("ios_device_token") != null) {
					sendIosNotification(message, title, (String) resultMap.get("ios_device_token"),
							Integer.parseInt((String) resultMap.get("sound_alert")), customerId, "", 6);
				}
				// email send to user
				String email = (String) resultMap.get("email");
				HashMap<String, String> nameVal = new HashMap<>();

				// get today's date
				LocalDate localDate = LocalDate.now();
				String currentDate = DateTimeFormatter.ofPattern("dd/MM/yyy").format(localDate);

				DecimalFormat sevenDecimal = new DecimalFormat("#0.0000000");
				if (currency.equalsIgnoreCase("HCX") || currency.equalsIgnoreCase("IEC")) {
					amount = sevenDecimal.format(Double.parseDouble(amount));
				}
				if (type.equalsIgnoreCase("approve")) {
					nameVal.put("heading", currency + " Sent");
				} else {
					nameVal.put("heading", currency + " Send declined");
				}

				nameVal.put("heading1", currency + " Send To,");
				nameVal.put("send_address", toAdd);
				nameVal.put("closing_heading", currency);
				nameVal.put("send_amount", amount);
				nameVal.put("date", currentDate);
				nameVal.put("closing_heading_text", currency + " Wallet Balance");
				nameVal.put("closing_balance", closingBalance);
//				nameVal.put("closing_fiat_balance", closingFiatBalance);

				String mailContent = mailContentBuilderService.build("sent_email_template", nameVal);
				String subject = "Transaction Mail From " + env.getProperty("project.company.product") + " ";
//				String bcc ="treasury@mexdigital.com";
				mailClientService.mailthreding(env.getProperty("spring.mail.username"), email, subject, mailContent,"");
			}
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
	}

	public void sendAndroidNotification(String message, String title, String devicetoken, int sound, String userId,
			String contentId, int eventId) throws SQLException {
		String response = "";
		String deviceToken = devicetoken;
		try {
			conn = dataSource.getConnection();
			String sql = "", androidFcmKey = "";
			sql = "select country from users where user_id =? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, userId);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				if (rs.getString("country").equalsIgnoreCase("United States")
						|| rs.getString("country").equalsIgnoreCase("United States of America")
						|| rs.getString("country").equalsIgnoreCase("USA")) {
					androidFcmKey = env.getProperty("project.android.ios.key");
				} else {
					androidFcmKey = env.getProperty("project.android.ios.key.otherCountry");
				}

			}
			rs.close();
			ps.close();
			String androidFcmUrl = "https://fcm.googleapis.com/fcm/send";

			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.set("Authorization", "key=" + androidFcmKey);
			httpHeaders.set("Content-Type", "application/json");
			JSONObject json = new JSONObject();
			JSONObject notification = new JSONObject();
			JSONObject data = new JSONObject();

			notification.put("title", title);
			notification.put("body", message);
			notification.put("icon", "notify_icon");
			if (sound == 0) {
				notification.put("sound", "");
			} else {
				notification.put("sound", "default");
			}
			json.put("notification", notification);

			data.put("title", title);
			data.put("body", message);
			json.put("data", data);
			/*
			 * data.put("param1", "value1"); data.put("param2", "value2"); json.put("data",
			 * data);
			 */

			json.put("to", deviceToken);
			json.put("priority", "high");

			HttpEntity<String> httpEntity = new HttpEntity<String>(json.toString(), httpHeaders);
			response = restTemplate.postForObject(androidFcmUrl, httpEntity, String.class);
			JSONObject partsData = new JSONObject(response);
			if (partsData.optInt("success") == 1) {
//				   conn = dataSource.getConnection();
				sql = "insert into notifications(NOTIFICATION_ID,user_id,notification_message,content_id,event_id) values(NOTIFICATIONS_SEQ.NEXTVAL,?,?,?,?) ";
				ps = conn.prepareStatement(sql);
				ps.setString(1, userId);
				ps.setString(2, message);
				ps.setString(3, contentId);
				ps.setInt(4, eventId);
				ps.executeUpdate();
				ps.close();
			}

		} catch (JSONException e) {
			e.printStackTrace();
			log.error(e.getMessage());
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	public void sendIosNotification(String message, String title, String devicetoken, int sound, String userId,
			String contentId, int eventId) throws SQLException {
		String response = "";
		String deviceToken = devicetoken;
		try {
			conn = dataSource.getConnection();
			String sql = "", androidFcmKey = "";
			sql = "select country from users where user_id =? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, userId);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				if (rs.getString("country").equalsIgnoreCase("United States")
						|| rs.getString("country").equalsIgnoreCase("United States of America")
						|| rs.getString("country").equalsIgnoreCase("USA")) {
					androidFcmKey = env.getProperty("project.android.ios.key");
				} else {
					androidFcmKey = env.getProperty("project.android.ios.key.otherCountry");
				}

			}
			rs.close();
			ps.close();

			String androidFcmUrl = "https://fcm.googleapis.com/fcm/send";

			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.set("Authorization", "key=" + androidFcmKey);
			httpHeaders.set("Content-Type", "application/json");
			// JSONObject data = new JSONObject();
			JSONObject json = new JSONObject();
			JSONObject notification = new JSONObject();

			notification.put("title", title);
			notification.put("body", message);
			if (sound == 0) {
				notification.put("sound", "");
			} else {
				notification.put("sound", "default");
			}
			notification.put("badge", "1");
			json.put("notification", notification);

			/*
			 * data.put("param1", "value1"); data.put("param2", "value2"); json.put("data",
			 * data);
			 */

			json.put("to", deviceToken);
			json.put("priority", "high");

			HttpEntity<String> httpEntity = new HttpEntity<String>(json.toString(), httpHeaders);
			response = restTemplate.postForObject(androidFcmUrl, httpEntity, String.class);
			JSONObject partsData = new JSONObject(response);
			if (partsData.optInt("success") == 1) {
				sql = "insert into notifications(NOTIFICATION_ID,user_id,notification_message,content_id,event_id) values(NOTIFICATIONS_SEQ.NEXTVAL,?,?,?,?) ";
				ps = conn.prepareStatement(sql);
				ps.setString(1, userId);
				ps.setString(2, message);
				ps.setString(3, contentId);
				ps.setInt(4, eventId);
				ps.executeUpdate();
				ps.close();
			}

		} catch (JSONException e) {
			e.printStackTrace();
			log.error(e.getMessage());
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public String check2Fa(int adminUserId, int code) {
		String response = "";
		String sql = "select user_id,two_factor_auth_key from admin_user_login where user_id = ?";
		try {
				Map<String, Object> verificationData = jdbcTemplate.queryForMap(sql, adminUserId);
				if (!verificationData.isEmpty()) {
					String securityCode = getTOTPCode(verificationData.get("two_factor_auth_key").toString());
					if (Integer.parseInt(securityCode) == code) {
						response = "Success";
					} else {
						response = "Invalid Security Code";
					}
				} else {
					response = "Invalid Admin User.";
				}
		} catch (Exception e) {
			e.printStackTrace();
			response = e.getMessage();
		}
		return response;
	}
	
	public String getTOTPCode(String secretKey) {
		String normalizedBase32Key = secretKey.replace(" ", "").toUpperCase();
		Base32 base32 = new Base32();
		byte[] bytes = base32.decode(normalizedBase32Key);
		String hexKey = Hex.encodeHexString(bytes);
		long time = (System.currentTimeMillis() / 1000) / 30;
		String hexTime = Long.toHexString(time);
		return TOTP.generateTOTP(hexKey, hexTime, "6");
	}
}
