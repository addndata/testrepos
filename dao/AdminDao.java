package com.project.admin.dao;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.math.BigDecimal;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.sql.DataSource;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.dao.EmptyResultDataAccessException;
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
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.project.admin.model.AdminUsers;
import com.project.admin.model.AdminUsersResponse;
import com.project.admin.model.ApiKeyDetails;
import com.project.admin.model.AssetPairDetails;
import com.project.admin.model.AssetWiseOfferDetails;
import com.project.admin.model.BankDetails;
import com.project.admin.model.ContractDetails;
import com.project.admin.model.CurrencyMaster;
import com.project.admin.model.CurrencyRate;
import com.project.admin.model.DbProcessDetails;
import com.project.admin.model.ErrorResponse;
import com.project.admin.model.ExchangeHealthCheckResponse;
import com.project.admin.model.Franchise;
import com.project.admin.model.FranchiseBankDetails;
import com.project.admin.model.FranchiseRequestInput;
import com.project.admin.model.FutureMatchingEngineAsset;
import com.project.admin.model.FuturesContractTypeMaster;
import com.project.admin.model.FuturesTradingFees;
import com.project.admin.model.Maintenance;
import com.project.admin.model.MarginCallOrLiquidityValue;
import com.project.admin.model.MiningFees;
import com.project.admin.model.OfferDetails;
import com.project.admin.model.SystemResponse;
import com.project.admin.model.TierWiseTradingFees;
import com.project.admin.model.CurrencyWiseSetting;
import com.project.admin.model.TomcatDetails;
import com.project.admin.model.TradingFees;
import com.project.admin.model.Users;

@Repository
public class AdminDao {

	private static final Logger log = LoggerFactory.getLogger(AdminDao.class);

	@Autowired
	private DataSource dataSource;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	MailContentBuilderService mailContentBuilderService;
	@Autowired
	MailClientService mailClientService;
	@Autowired
	NotificationService notificationService;
	@Autowired
	Environment env;
	@Autowired
	private EmailService emailService;

	CallableStatement callableStatement = null; // create Callable Statement
												// instance to call stored
												// procedure

	ErrorResponse error = null;
	AdminUsersResponse adminUsersResponse = null;

	public AdminUsersResponse getAllConfirmUsers(Users users) {
		Connection conn = null; // create connection instance to connect with
								// database
		PreparedStatement ps = null; // create Prepared statement instance to
										// run sql query
		adminUsersResponse = new AdminUsersResponse();
		error = new ErrorResponse();
		Users _user = null;
		List<Users> _usersList = new ArrayList<Users>();
		int starts_form = (users.getPage_no() - 1) * users.getNo_of_items_per_page();
		int end = starts_form + users.getNo_of_items_per_page();
		int total_count = 0;
		String sql = "", search_string = "";
		try {
			conn = dataSource.getConnection();

			if (users.getSearch_string() != null && users.getSearch_string() != "") {
				search_string = "( to_char(user_id) ='" + users.getSearch_string() + "' or first_name like '%"
						+ users.getSearch_string() + "%' " + " or phone like '%" + users.getSearch_string()
						+ "%' or email like '%" + users.getSearch_string() + "%' ) ";
			}

			if (search_string != "") {
				sql = "SELECT a.* FROM (SELECT b.*,rownum b_rownum FROM ( select * from  users where  " + search_string
						+ " AND USER_DOCS_STATUS NOT IN (0,1,99) order by created desc) b "
						+ "  WHERE rownum <= ?) a WHERE b_rownum >= ?";
			} else {
				sql = "SELECT a.* FROM (SELECT b.*,rownum b_rownum FROM ( select * from  users  where "
						+ "USER_DOCS_STATUS NOT IN (0,1,99) order by created desc ) b  WHERE rownum <= ?) a WHERE b_rownum >= ? ";
			}
			log.info("sql: {}", sql);
			ps = conn.prepareStatement(sql);
			ps.setInt(1, end);
			ps.setInt(2, starts_form);
			ResultSet resultSet = ps.executeQuery();
			while (resultSet.next()) {
				_user = new Users();
				_user.setUser_id(resultSet.getObject("user_id") != null ? resultSet.getInt("user_id") : 0);
				_user.setFirstName(resultSet.getObject("first_name") != null ? resultSet.getString("first_name") : "");
				_user.setState(resultSet.getObject("state") != null ? resultSet.getString("state") : "");
				_user.setCity(resultSet.getObject("city") != null ? resultSet.getString("city") : "");
				_user.setZip(resultSet.getObject("zip") != null ? resultSet.getString("zip") : "");
				_user.setEmail(resultSet.getObject("email") != null ? resultSet.getString("email") : "");
				_user.setCountry(resultSet.getObject("country") != null ? resultSet.getString("country") : "");
				_user.setPhone(resultSet.getObject("phone") != null ? resultSet.getString("phone") : "");
				_user.setCreated(resultSet.getObject("created") != null ? resultSet.getString("created") : "");
				_user.setUser_docs_status(
						resultSet.getObject("user_docs_status") != null ? resultSet.getInt("user_docs_status") : 0);
				_user.setBank_details_status(resultSet.getObject("bank_details_status") != null
						? resultSet.getInt("bank_details_status") + ""
						: "");
				_user.setBuy_limit(resultSet.getObject("buy_limit") != null ? resultSet.getInt("buy_limit") : 0);
				_user.setSell_limit(resultSet.getObject("sell_limit") != null ? resultSet.getInt("sell_limit") : 0);
				_user.setTierGroup(resultSet.getInt("tier_group"));
				_usersList.add(_user);

			}
			ps.close();

			if (search_string != "") {
				sql = " select count(*) total_count from users where " + search_string;
			} else {
				sql = " select count(*) total_count from users  where USER_DOCS_STATUS NOT IN (0,1,99)";
			}
			ps = conn.prepareStatement(sql);
			resultSet = ps.executeQuery();
			if (resultSet.next()) {
				total_count = resultSet.getInt("total_count");
			}
			ps.close();

			if (!_usersList.isEmpty()) {
				adminUsersResponse.setUsersListResult(_usersList);
				adminUsersResponse.setTotalcount(total_count);

				error.setError_data(0);
				error.setError_msg("");
				adminUsersResponse.setError(error);
			} else {
				error.setError_data(0);
				error.setError_msg("no data");
				adminUsersResponse.setError(error);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			error.setError_data(1);
			error.setError_msg(e.getMessage());
			adminUsersResponse.setError(error);

		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return adminUsersResponse;
	}

	public AdminUsersResponse getAllUnconfirmUsers(Users users) {
		Connection conn = null; // create connection instance to connect with
								// database
		PreparedStatement ps = null; // create Prepared statement instance to
										// run sql query
		adminUsersResponse = new AdminUsersResponse();
		error = new ErrorResponse();
		Users _user = null;
		List<Users> _usersList = new ArrayList<Users>();
		int starts_form = (users.getPage_no() - 1) * users.getNo_of_items_per_page();
		int end = starts_form + users.getNo_of_items_per_page();
		int total_count = 0;
		String sql = "", searchString = "", bankDocStatus = "";
		try {
			conn = dataSource.getConnection();

			if (users.getSearch_string() != null && users.getSearch_string() != "") {
				searchString = "( to_char(user_id) ='" + users.getSearch_string() + "' or first_name like '%"
						+ users.getSearch_string() + "%' " + " or phone like '%" + users.getSearch_string()
						+ "%' or email like '%" + users.getSearch_string() + "%' ) ";
			}

			if (StringUtils.hasText(users.getBank_details_status())) {
				if (users.getBank_details_status().equals("4")) {
					bankDocStatus = " AND BANK_DETAILS_STATUS is null ";
				} else {
					bankDocStatus = " AND BANK_DETAILS_STATUS=" + users.getBank_details_status();
				}
			}

			if (searchString != "") {
				sql = "SELECT a.* FROM (SELECT b.*,rownum b_rownum FROM ( select * from  users where  " + searchString
						+ " AND USER_DOCS_STATUS IN (0,1,99) order by created desc) b "
						+ "  WHERE rownum <= ?) a WHERE b_rownum >= ?";
			} else {
				sql = "SELECT a.* FROM (SELECT b.*,rownum b_rownum FROM ( select * from  users  where "
						+ "USER_DOCS_STATUS IN (0,1,99) " + bankDocStatus
						+ " order by created desc ) b  WHERE rownum <= ?) a WHERE b_rownum >= ? ";
			}

			ps = conn.prepareStatement(sql);
			ps.setInt(1, end);
			ps.setInt(2, starts_form);
			ResultSet resultSet = ps.executeQuery();
			while (resultSet.next()) {
				_user = new Users();
				_user.setUser_id(resultSet.getObject("user_id") != null ? resultSet.getInt("user_id") : 0);
				_user.setFirstName(resultSet.getObject("first_name") != null ? resultSet.getString("first_name") : "");
				_user.setState(resultSet.getObject("state") != null ? resultSet.getString("state") : "");
				_user.setCity(resultSet.getObject("city") != null ? resultSet.getString("city") : "");
				_user.setZip(resultSet.getObject("zip") != null ? resultSet.getString("zip") : "");
				_user.setEmail(resultSet.getObject("email") != null ? resultSet.getString("email") : "");
				_user.setCountry(resultSet.getObject("country") != null ? resultSet.getString("country") : "");
				_user.setPhone(resultSet.getObject("phone") != null ? resultSet.getString("phone") : "");
				_user.setCreated(resultSet.getObject("created") != null ? resultSet.getString("created") : "");
				_user.setUser_docs_status(
						resultSet.getObject("user_docs_status") != null ? resultSet.getInt("user_docs_status") : 0);
				_user.setBank_details_status(resultSet.getObject("bank_details_status") != null
						? resultSet.getInt("bank_details_status") + ""
						: "");
				_user.setBuy_limit(resultSet.getObject("buy_limit") != null ? resultSet.getInt("buy_limit") : 0);
				_user.setSell_limit(resultSet.getObject("sell_limit") != null ? resultSet.getInt("sell_limit") : 0);
				_user.setTierGroup(resultSet.getInt("tier_group"));
				_usersList.add(_user);

			}
			ps.close();

			sql = " select count(*) total_count from users  where USER_DOCS_STATUS IN (0,1,99) ";
			ps = conn.prepareStatement(sql);
			resultSet = ps.executeQuery();
			if (resultSet.next()) {
				total_count = resultSet.getInt("total_count");
			}
			ps.close();

			if (!_usersList.isEmpty()) {
				adminUsersResponse.setUsersListResult(_usersList);
				adminUsersResponse.setTotalcount(total_count);

				error.setError_data(0);
				error.setError_msg("");
				adminUsersResponse.setError(error);
			} else {
				error.setError_data(0);
				error.setError_msg("no data");
				adminUsersResponse.setError(error);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			error.setError_data(1);
			error.setError_msg(e.getMessage());
			adminUsersResponse.setError(error);

		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return adminUsersResponse;
	}

	public AdminUsersResponse addUserInTierGroup(Users users) {
		adminUsersResponse = new AdminUsersResponse();
		String query = "UPDATE USERS SET TIER_GROUP = ? WHERE USER_ID = ? ";
		try {
			if (users.getUser_id() > 0) {
				int i = jdbcTemplate.update(query, users.getTierGroup(), users.getUser_id());
				if (i > 0) {
					error = new ErrorResponse();
				} else {
					error = new ErrorResponse(1, "Updation failed.");
				}
			} else {
				error = new ErrorResponse(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
			error = new ErrorResponse(1, e.getMessage());
		}
		adminUsersResponse.setError(error);
		return adminUsersResponse;
	}

	public AdminUsersResponse totalKycApproveUsersByName(Users users) {
		adminUsersResponse = new AdminUsersResponse();
		error = new ErrorResponse();
		try {
			String sql = "SELECT count(*) FROM USERS WHERE USER_TAG=1 AND USER_DOCS_STATUS=1 AND UPPER(first_name) LIKE '%'||?||'%' ";
			int total = jdbcTemplate.queryForObject(sql, new Object[] { users.getFirstName().toUpperCase() },
					Integer.class);
			adminUsersResponse.setTotalcount(total);
			error.setError_data(0);
			error.setError_msg("");
		} catch (Exception e) {
			log.error(e.getMessage());
			error.setError_data(1);
			error.setError_msg(e.getMessage());
		}
		adminUsersResponse.setError(error);
		return adminUsersResponse;
	}

	public AdminUsersResponse getUsersDetails(Users users) {
		Connection conn = null; // create connection instance to connect with
								// database
		PreparedStatement ps = null; // create Prepared statement instance to
										// run sql query
		adminUsersResponse = new AdminUsersResponse();
		error = new ErrorResponse();
		Users _user = null;
		List<Users> _usersList = new ArrayList<Users>();
		try {
			conn = dataSource.getConnection();
			String sql = "";
			sql = " select user_id,uuid,first_name,middle_name,last_name,address,state,city,zip,email,country,phone,email,user_docs_status,bank_details_status,ssn,"
					+ " profile_pic,address_proof_doc,id_proof_front,id_proof_back,role,created,status,buy_limit,sell_limit,send_limit,receive_limit,"
					+ " fiat_deposit,futures_trade,blocked_by,blocked_on,unblocked_by,unblocked_on from users where user_id = ? "
					+ "  ";
			ps = conn.prepareStatement(sql);
			ps.setInt(1, users.getUser_id());
			ResultSet resultSet = ps.executeQuery();
			while (resultSet.next()) {
				_user = new Users();
				_user.setUser_id(resultSet.getObject("user_id") != null ? resultSet.getInt("user_id") : 0);
				_user.setUuid(resultSet.getObject("uuid") != null ? resultSet.getString("uuid") : "");
				_user.setFirstName(resultSet.getObject("first_name") != null ? resultSet.getString("first_name") : "");
				_user.setMiddleName(resultSet.getObject("middle_name") != null ? resultSet.getString("middle_name") : "");
				_user.setLastName(resultSet.getObject("last_name") != null ? resultSet.getString("last_name") : "");
				_user.setAddress(resultSet.getObject("address") != null ? resultSet.getString("address") : "");
				_user.setState(resultSet.getObject("state") != null ? resultSet.getString("state") : "");
				_user.setCity(resultSet.getObject("city") != null ? resultSet.getString("city") : "");
				_user.setZip(resultSet.getObject("zip") != null ? resultSet.getString("zip") : "");
				_user.setEmail(resultSet.getObject("email") != null ? resultSet.getString("email") : "");
				_user.setCountry(resultSet.getObject("country") != null ? resultSet.getString("country") : "");
				_user.setPhone(resultSet.getObject("phone") != null ? resultSet.getString("phone") : "");
				_user.setSsn(resultSet.getObject("ssn") != null ? resultSet.getString("ssn") : "");
				_user.setProfile_pic(
						resultSet.getObject("profile_pic") != null ? resultSet.getString("profile_pic") : "");
				_user.setAddress_proof_doc(
						resultSet.getObject("address_proof_doc") != null ? resultSet.getString("address_proof_doc")
								: "");
				_user.setIdProofFront(
						resultSet.getObject("id_proof_front") != null ? resultSet.getString("id_proof_front") : "");
				_user.setIdProofBack(
						resultSet.getObject("id_proof_back") != null ? resultSet.getString("id_proof_back") : "");
				_user.setRole(resultSet.getObject("role") != null ? resultSet.getString("role") : "");
				_user.setCreated(resultSet.getObject("created") != null ? resultSet.getString("created") : "");
				_user.setStatus(resultSet.getObject("status") != null ? resultSet.getInt("status") : 0);
				_user.setUser_docs_status(
						resultSet.getObject("user_docs_status") != null ? resultSet.getInt("user_docs_status") : 0);
				_user.setBank_details_status(resultSet.getObject("bank_details_status") != null
						? resultSet.getInt("bank_details_status") + ""
						: "");
				_user.setBuy_limit(resultSet.getObject("buy_limit") != null ? resultSet.getInt("buy_limit") : 0);
				_user.setSell_limit(resultSet.getObject("sell_limit") != null ? resultSet.getInt("sell_limit") : 0);
				_user.setSend_limit(resultSet.getObject("send_limit") != null ? resultSet.getInt("send_limit") : 0);
				_user.setReceive_limit(
						resultSet.getObject("receive_limit") != null ? resultSet.getInt("receive_limit") : 0);
				_user.setFiatDeposit(
						resultSet.getObject("fiat_deposit") != null ? resultSet.getInt("fiat_deposit") : 0);
				_user.setFuturesTrade(
						resultSet.getObject("futures_trade") != null ? resultSet.getInt("futures_trade") : 0);
				_user.setBlockedBy(resultSet.getObject("blocked_by") != null ? resultSet.getString("blocked_by") : "");
				_user.setBlockedOn(resultSet.getObject("blocked_on") != null ? resultSet.getString("blocked_on") : "");
				_user.setUnblockedBy(resultSet.getObject("unblocked_by") != null ? resultSet.getString("unblocked_by") : "");
				_user.setUnblockedOn(
						resultSet.getObject("unblocked_on") != null ? resultSet.getString("unblocked_on") : "");
				_usersList.add(_user);
			}
			ps.close();
			if (!_usersList.isEmpty()) {
				adminUsersResponse.setUsersListResult(_usersList);
				error.setError_data(0);
				error.setError_msg("");
				adminUsersResponse.setError(error);
			} else {
				error.setError_data(1);
				error.setError_msg("Invalid User ID");
				adminUsersResponse.setError(error);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			error.setError_data(1);
			error.setError_msg(e.getMessage());
			adminUsersResponse.setError(error);

		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return adminUsersResponse;
	}

	public AdminUsersResponse getUserBankDetails(Users user) {
		Connection conn = null; // create connection instance to connect with
								// database
		PreparedStatement ps = null; // create Prepared statement instance to
										// run sql query
		adminUsersResponse = new AdminUsersResponse();
		error = new ErrorResponse();
		BankDetails bankDetails = new BankDetails();
		try {
			conn = dataSource.getConnection();
			String sql = "select benificiary_name,bank_name,bank_address,account_no,account_type,institution_transit_no,swift_code,ifsc_code,verification_amount,bank_cheque_doc from bank_details where user_id = ? ";
			// String sql = "select
			// benificiary_name,bank_name,account_no,routing_no from
			// bank_details bd inner join users u "
			// + " on bd.user_id=u.user_id where u.email='"+user.getEmail()+"'";
			// execute insertDBUSER store procedure
			ps = conn.prepareStatement(sql);
			ps.setInt(1, user.getUser_id());
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				bankDetails.setBeneficiary_name(
						rs.getObject("benificiary_name") != null ? rs.getString("benificiary_name") : "");
				bankDetails.setBank_name(rs.getObject("bank_name") != null ? rs.getString("bank_name") : "");
				bankDetails.setBankAddress(rs.getObject("bank_address") != null ? rs.getString("bank_address") : "");
				bankDetails.setAccountType(rs.getObject("account_type") != null ? rs.getString("account_type") : "");
				bankDetails.setAccount_no(rs.getObject("account_no") != null ? rs.getString("account_no") : "");
				bankDetails.setInstitutionTransitNo(rs.getObject("institution_transit_no") != null ? rs.getString("institution_transit_no") : "");
				bankDetails.setSwiftCode(rs.getObject("swift_code") != null ? rs.getString("swift_code") : "");
				bankDetails.setIfscCode(rs.getObject("ifsc_code") != null ? rs.getString("ifsc_code") : "");
				bankDetails.setVerification_amount(
						rs.getObject("verification_amount") != null ? rs.getDouble("verification_amount") : 0);
				bankDetails
						.setBank_cheque(rs.getObject("bank_cheque_doc") != null ? rs.getString("bank_cheque_doc") : "");
				adminUsersResponse.setBankDetailsResult(bankDetails);
			}
			error.setError_data(0);
			error.setError_msg("");
			adminUsersResponse.setError(error);

			ps.close();

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			error.setError_data(1);
			error.setError_msg(e.getMessage());
			adminUsersResponse.setError(error);

		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return adminUsersResponse;
	}

	public AdminUsersResponse updateUserKycStatus(Users user) {
		Connection conn = null;
		PreparedStatement ps = null;
		adminUsersResponse = new AdminUsersResponse();
		error = new ErrorResponse();
		String title = "", message = "";

		try {
			conn = dataSource.getConnection();
			String sql = " select user_id from users where uuid = ? and user_docs_status=1 and status = 1";
			ps = conn.prepareStatement(sql);
			ps.setString(1, user.getUuid());
			ResultSet rSet = ps.executeQuery();
			if (rSet.next()) {
				updateKyc(user.getUser_docs_status(), user.getUuid());
				ps.close();
				// send notification to user
				sql = " select u.first_name,u.email,u.android_device_token,u.ios_device_token,uas.sound_alert from users u "
						+ " inner join user_app_settings uas on u.user_id=uas.user_id and u.user_id = "
						+ "(select user_id from users where uuid = ?) ";
				ps = conn.prepareStatement(sql);
				ps.setString(1, user.getUuid());
				ResultSet rs1 = ps.executeQuery();
				if (rs1.next()) {
					if (user.getUser_docs_status()== 3) {
						title = "Documents Approved";
						message = "Congratulations your KYC is approved. The next step would be to add your bank information "
								+ "(from the left side menu) so that we are able to process your deposits and withdrawals.";
					} else {
						title = "Documents Declined";
						message = "Your documents have been declined.Kindly check and resubmit.";
					}

					if (rs1.getString("android_device_token") != null) {
						notificationService.send_notification_android(message, title,
								rs1.getString("android_device_token"), rs1.getInt("sound_alert"),
								user.getUser_id() + "", "", 4, conn);
					}

					if (rs1.getString("ios_device_token") != null) {
						notificationService.send_notification_ios(message, title, rs1.getString("ios_device_token"),
								rs1.getInt("sound_alert"), user.getUser_id() + "", "", 4, conn);
					}

					// send mail to user
					String customerEmail = rs1.getString("email");
					HashMap<String, String> nameVal = new HashMap<>();
					nameVal.put("welcome_message", "Dear");
					nameVal.put("user_name", rs1.getString("first_name"));
					nameVal.put("message1", message);
					nameVal.put("message2", "");
					nameVal.put("message3", "");
					String mail_content = mailContentBuilderService.build("basic", nameVal);

					String subject = "Your Documents Verification Status.";
					mailClientService.mailthreding(env.getProperty("spring.mail.username"), customerEmail, subject,
							mail_content);
				}
				rs1.close();
				ps.close();
				rSet.close();
				error.setError_data(0);
				error.setError_msg("");
				adminUsersResponse.setError(error);

			} else {
				error.setError_data(1);
				error.setError_msg("User is blocked or Documents not submitted");
				adminUsersResponse.setError(error);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			error.setError_data(1);
			error.setError_msg(e.getMessage());
			adminUsersResponse.setError(error);

		} finally {
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return adminUsersResponse;
	}
	
	public AdminUsersResponse updateKycStatus(Users user) {
		adminUsersResponse = new AdminUsersResponse(); // and futures_trade = 1
		error = new ErrorResponse();
		try {
			updateKyc(user.getUser_docs_status(), user.getUuid());
			error.setError_data(0);
			error.setError_msg("Update successful.");
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			error.setError_data(1);
			error.setError_msg(e.getMessage());
		}
		adminUsersResponse.setError(error);
		return adminUsersResponse;
	}

	public String updateKyc(int kycLevel, String uuid) {
		Connection conn = null;
		PreparedStatement ps = null;
		int i = 0;
		String subject = "";
		String sql = "update users set user_docs_status = ? ";
		try {
			conn = dataSource.getConnection();
			if (kycLevel == 2 || kycLevel == 3 || kycLevel == 4 || kycLevel == 5) {
				sql = sql + ",send_limit = 1,buy_limit = 1 ";
				if (kycLevel == 2) {
					sql = sql + ",fiat_deposit = 0,futures_trade = 1 ";
					subject = "Crypto Approved | Spot & Futures";
				}
				if (kycLevel == 3) {
					sql = sql + ",fiat_deposit = 1,futures_trade = 1 ";
					subject = "Crypto & Fiat Approved | Spot & Futures";
				}
				if (kycLevel == 4) {
					sql = sql + ",fiat_deposit = 0,futures_trade = 0 ";
					subject = "Crypto Approved | Spot Only";
				}
				if (kycLevel == 5) {
					sql = sql + ",fiat_deposit = 1,futures_trade = 0 ";
					subject = "Crypto & Fiat Approved | Spot Only";
				}
			} else {
				sql = sql + ",send_limit = 0,buy_limit = 0,fiat_deposit = 0,futures_trade = 0";
			}
			sql = sql + " where uuid = ? and status = 1";
			log.info("Final KYC Update Query : " + sql);
			ps = conn.prepareStatement(sql);
			ps.setInt(1, kycLevel);
			ps.setString(2, uuid);
			i = ps.executeUpdate();
			if (i == 0) {
				subject = "KYC updation failed.";
			}
			ps.close();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		} finally {
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return subject;
	}

	public AdminUsersResponse approveUserBankDetails(Users user) {
		Connection conn = null; // create connection instance to connect with
								// database
		PreparedStatement ps = null; // create Prepared statement instance to
										// run sql query
		adminUsersResponse = new AdminUsersResponse();
		error = new ErrorResponse();
		String title = "", message = "";
		try {
			conn = dataSource.getConnection();
			String sql = " select user_id from users where user_id = ? and bank_details_status=0 and status = 1";
			ps = conn.prepareStatement(sql);
			ps.setInt(1, user.getUser_id());
			ResultSet rSet = ps.executeQuery();
			if (rSet.next()) {

				sql = "update users set bank_details_status= ? where user_id = ? ";
				// execute insertDBUSER store procedure
				ps = conn.prepareStatement(sql);
				ps.setString(1, user.getBank_details_status());
				ps.setInt(2, user.getUser_id());
				ps.executeUpdate();
				ps.close();
				// send notification to user
				sql = " select u.first_name,u.email,u.android_device_token,u.ios_device_token,uas.sound_alert from users u "
						+ " inner join user_app_settings uas on u.user_id=uas.user_id and u.user_id = ? ";
				ps = conn.prepareStatement(sql);
				ps.setInt(1, user.getUser_id());
				ResultSet rs1 = ps.executeQuery();
				if (rs1.next()) {
					if (user.getBank_details_status().equals("2")) {
						title = "Payment Method Approved";
						message = "Your payment method has been approved.";
					} else if (user.getBank_details_status().equals("3")) {
						title = "Payment Method Declined";
						message = "	Your payment method has been declined.Please resubmit the details after checking.";
					}
					if (rs1.getString("android_device_token") != null) {
						notificationService.send_notification_android(message, title,
								rs1.getString("android_device_token"), rs1.getInt("sound_alert"),
								user.getUser_id() + "", "", 4, conn);
					}
					if (rs1.getString("ios_device_token") != null) {
						notificationService.send_notification_ios(message, title, rs1.getString("ios_device_token"),
								rs1.getInt("sound_alert"), user.getUser_id() + "", "", 4, conn);
					}
					String customerEmail = rs1.getString("email");
					HashMap<String, String> nameVal = new HashMap<>();
					nameVal.put("welcome_message", "Dear");
					nameVal.put("user_name",
							rs1.getString("first_name") != null ? rs1.getString("first_name") : customerEmail);
					nameVal.put("message1", message);
					String mail_content = mailContentBuilderService.build("basic", nameVal);

					String subject = "Your Payment Method Verification Status ";
					mailClientService.mailthreding(env.getProperty("spring.mail.username"), customerEmail, subject,
							mail_content);
				}
				ps.close();
				error.setError_data(0);
				error.setError_msg("");
				adminUsersResponse.setError(error);
			} else {
				error.setError_data(1);
				error.setError_msg("User is blocked or Bank details not submitted.");
				adminUsersResponse.setError(error);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			error.setError_data(1);
			error.setError_msg(e.getMessage());
			adminUsersResponse.setError(error);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return adminUsersResponse;
	}

	public AdminUsersResponse updateUserAccountStatus(Users user) {
		adminUsersResponse = new AdminUsersResponse();
		error = new ErrorResponse();
		String sql = "";
		String message = "";
		try {
			if (user.getStatus() == 2) {
				sql = "update users set status = ?,blocked_by = ?,blocked_on = current_timestamp where user_id = ?";
				message = "Account is blocked successfully.";
			} else if (user.getStatus() == 1) {
				sql = "update users set status = ?,unblocked_by = ?,unblocked_on = current_timestamp where user_id = ?";
				message = "Account is unblocked successfully.";
			}
			int i = jdbcTemplate.update(sql, user.getStatus(), "Admin "+ user.getAdminUserId(),user.getUser_id());
			if (i != 0) {
				error.setError_data(0);
				error.setError_msg(message);
			} else {
				error.setError_data(1);
				error.setError_msg("Account updation failed, please try again.");
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			error.setError_data(1);
			error.setError_msg(e.getMessage());
		}
		adminUsersResponse.setError(error);
		return adminUsersResponse;
	}

	public AdminUsersResponse restrictBuy(Users user) {
		Connection conn = null; // create connection instance to connect with
								// database
		PreparedStatement ps = null; // create Prepared statement instance to
										// run sql query
		adminUsersResponse = new AdminUsersResponse();
		error = new ErrorResponse();
		// String title="",message="";

		try {
			conn = dataSource.getConnection();

			String sql = "update users set buy_limit= ? where user_id = ? ";
			// execute insertDBUSER store procedure
			ps = conn.prepareStatement(sql);
			ps.setInt(1, user.getBuy_limit());
			ps.setInt(2, user.getUser_id());
			ps.executeUpdate();
			ps.close();

			error.setError_data(0);
			error.setError_msg("");
			adminUsersResponse.setError(error);

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			error.setError_data(1);
			error.setError_msg(e.getMessage());
			adminUsersResponse.setError(error);

		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return adminUsersResponse;
	}

	public AdminUsersResponse restrictSell(Users user) {
		Connection conn = null; // create connection instance to connect with
								// database
		PreparedStatement ps = null; // create Prepared statement instance to
										// run sql query
		adminUsersResponse = new AdminUsersResponse();
		error = new ErrorResponse();
		// String title="",message="";

		try {
			conn = dataSource.getConnection();

			String sql = "update users set sell_limit= ? where user_id = ? ";
			// execute insertDBUSER store procedure
			ps = conn.prepareStatement(sql);
			ps.setInt(1, user.getSell_limit());
			ps.setInt(2, user.getUser_id());
			ps.executeUpdate();
			ps.close();

			error.setError_data(0);
			error.setError_msg("");
			adminUsersResponse.setError(error);

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			error.setError_data(1);
			error.setError_msg(e.getMessage());
			adminUsersResponse.setError(error);

		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return adminUsersResponse;
	}

	public AdminUsersResponse restrictSend(Users user) {
		Connection conn = null; // create connection instance to connect with
								// database
		PreparedStatement ps = null; // create Prepared statement instance to
										// run sql query
		adminUsersResponse = new AdminUsersResponse();
		error = new ErrorResponse();

		try {
			conn = dataSource.getConnection();

			String sql = "update users set send_limit= ? where user_id = ? ";
			ps = conn.prepareStatement(sql);
			ps.setInt(1, user.getSend_limit());
			ps.setInt(2, user.getUser_id());
			ps.executeUpdate();
			ps.close();

			error.setError_data(0);
			error.setError_msg("");
			adminUsersResponse.setError(error);

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			error.setError_data(1);
			error.setError_msg(e.getMessage());
			adminUsersResponse.setError(error);

		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return adminUsersResponse;
	}

	public AdminUsersResponse restrictReceive(Users user) {
		Connection conn = null; // create connection instance to connect with
								// database
		PreparedStatement ps = null; // create Prepared statement instance to
										// run sql query
		adminUsersResponse = new AdminUsersResponse();
		error = new ErrorResponse();
		// String title="",message="";

		try {
			conn = dataSource.getConnection();

			String sql = "update users set receive_limit= ? where user_id = ? ";
			// execute insertDBUSER store procedure
			ps = conn.prepareStatement(sql);
			ps.setInt(1, user.getReceive_limit());
			ps.setInt(2, user.getUser_id());
			ps.executeUpdate();
			ps.close();

			error.setError_data(0);
			error.setError_msg("");
			adminUsersResponse.setError(error);

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			error.setError_data(1);
			error.setError_msg(e.getMessage());
			adminUsersResponse.setError(error);

		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return adminUsersResponse;
	}

	public AdminUsersResponse SendMailToUser(Users user) {
		Connection conn = null; // create connection instance to connect with
								// database
		PreparedStatement ps = null; // create Prepared statement instance to
										// run sql query
		adminUsersResponse = new AdminUsersResponse();
		error = new ErrorResponse();
		try {
			conn = dataSource.getConnection();

			// send password to admin user
			String sql = " select first_name,email from users where user_id = ? ";
			ps = conn.prepareStatement(sql);
			ps.setInt(1, user.getUser_id());
			ResultSet rs1 = ps.executeQuery();
			if (rs1.next()) {
				String customer_email = rs1.getString("email");

				// send mail to user
				HashMap<String, String> nameVal = new HashMap<>();
//				nameVal.put("welcome_message", "Hello " + rs1.getString("first_name") + ",");
//				nameVal.put("user_name", "");
				nameVal.put("message1", user.getMessage());
//				nameVal.put("message2", "Thanks and regards, ");
//				nameVal.put("message3", "Paybito Team");
				String mail_content = mailContentBuilderService.build("basic", nameVal);

				String subject = "Mail From Admin";
				mailClientService.mailthreding(env.getProperty("spring.mail.username"), customer_email, subject,
						mail_content);

				error.setError_data(0);
				error.setError_msg("");

			} else {
				error.setError_data(0);
				error.setError_msg("Invalid user id.");
			}
			ps.close();

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

		adminUsersResponse.setError(error);
		return adminUsersResponse;
	}

	public AdminUsersResponse SendMailFromPaybitoSite(String user_email, String email_cc, String email_content,
			MultipartFile coin_logo, MultipartFile legal_docs, String recaptchaResponse) {
		adminUsersResponse = new AdminUsersResponse();
		error = new ErrorResponse();
		try {
			String response = emailService.recaptchaService(recaptchaResponse); // check
																				// captcha
																				// service
			if (response != null && response.equalsIgnoreCase("success")) {
				log.info("coin list mail ");
				String subject = "Mail Sent From " + env.getProperty("project.company.product")
						+ ".com for Coin Listing ";
				if (coin_logo != null && legal_docs != null) {
					log.info("logo and docs file upload ");
					mailClientService.mailthreding(env.getProperty("spring.mail.username"), user_email, subject,
							email_content, email_cc, coin_logo, legal_docs);
				} else if (coin_logo != null) {
					log.info("only logo  file upload ");
					mailClientService.mailthreding(env.getProperty("spring.mail.username"), user_email, subject,
							email_content, email_cc, coin_logo);
				} else if (legal_docs != null) {
					log.info(" only docs file upload ");
					mailClientService.mailthreding(env.getProperty("spring.mail.username"), user_email, subject,
							email_content, email_cc, legal_docs);
				} else {
					log.info(" without file upload ");
					mailClientService.mailthreding(env.getProperty("spring.mail.username"), user_email, subject,
							email_content, email_cc);
				}

				error.setError_data(0);
				error.setError_msg("");
			} else {
				error.setError_data(1);
				error.setError_msg("Captcha unverified.");
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			error.setError_data(1);
			error.setError_msg(e.getMessage());

		}

		adminUsersResponse.setError(error);
		return adminUsersResponse;
	}

	// for authentication..
	public AdminUsers getUserCredential(String UserName) {
		Connection conn = null; // create connection instance to connect with
								// database
		PreparedStatement ps = null; // create Prepared statement instance to
										// run sql query
		AdminUsers _adminuser = new AdminUsers();
		try {
			conn = dataSource.getConnection();

			// String encoded = getEncodedPassword("Pa87#bi$09to");

			String sql = "select email,password,role from  admin_users where email = ? ";
			// execute insertDBUSER store procedure
			ps = conn.prepareStatement(sql);
			ps.setString(1, UserName);
			ResultSet resultSet = ps.executeQuery();
			if (resultSet.next()) {
				_adminuser.setEmail(resultSet.getString("email"));
				_adminuser.setPassword(resultSet.getString("password"));
				_adminuser.setRole(resultSet.getString("role"));
			}

			ps.close();

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());

		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return _adminuser;
	}

	public String getEncodedPassword(String password) {
		String encoded_password = "";
		try {
			// encrypt password
			String text = password;
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
			encoded_password = Base64.getEncoder().encodeToString(hash);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return encoded_password;
	}

	protected String getSaltString() {
		String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ!$%+*1234567890abcdefghijklmnopqrstuvwxyz";
		StringBuilder salt = new StringBuilder();
		Random rnd = new Random();
		while (salt.length() < 12) { // length of the random string.
			int index = (int) (rnd.nextFloat() * SALTCHARS.length());
			salt.append(SALTCHARS.charAt(index));
		}
		String saltStr = salt.toString();
		return saltStr;
	}

	public AdminUsersResponse changeUserProfile(Users users) {
		Connection conn = null;
		PreparedStatement ps = null;
		adminUsersResponse = new AdminUsersResponse();
		error = new ErrorResponse();
		try {
			conn = dataSource.getConnection();
			String userCheck = "select uuid,first_name,email,phone,user_docs_status,bank_details_status from users where user_id = ? and status = 1";
			ps = conn.prepareStatement(userCheck);
			ps.setInt(1, users.getUser_id());
			ResultSet resultSet = ps.executeQuery();
			if (resultSet.next()) {
				String firstName = resultSet.getString("first_name");
				String email = resultSet.getString("email");
				String uuid = resultSet.getString("uuid");
				String requestBody = "{\"uuid\":\"" + uuid;
				String updateSql = "";
				String sql = "";
				if (users.getEmail() != null && !users.getEmail().isEmpty()) {
					sql = "select user_id from users where email = ?";
					updateSql = "update users set email = ?  where user_id = ?";
					ps.close();
					resultSet.close();
					ps = conn.prepareStatement(sql);
					ps.setString(1, users.getEmail());
					resultSet = ps.executeQuery();
					if (resultSet.next()) {
						error.setError_data(1);
						error.setError_msg("Email already exist.");
					} else {
						ps = conn.prepareStatement(updateSql);
						ps.setString(1, users.getEmail());
						ps.setInt(2, users.getUser_id());
						ps.executeUpdate();
						String message = "Your Email is updated successfully. New Email: " + users.getEmail();
						requestBody = requestBody + "\",\"email\":\"" + users.getEmail() + "\"}";
						sendProfileUpdationMail(firstName, email, message);
						error.setError_data(0);
						error.setError_msg("");
					}
				} else if (users.getPhone() != null && !users.getPhone().isEmpty()) {
					sql = "select user_id from users where phone = ?";
					updateSql = "update users set phone = ?  where user_id = ?";
					ps.close();
					resultSet.close();
					ps = conn.prepareStatement(sql);
					ps.setString(1, users.getPhone());
					resultSet = ps.executeQuery();
					if (resultSet.next()) {
						error.setError_data(1);
						error.setError_msg("Phone No. already exist.");
					} else {
						ps = conn.prepareStatement(updateSql);
						ps.setString(1, users.getPhone());
						ps.setInt(2, users.getUser_id());
						ps.executeUpdate();
						String message = "Your Phone No. is updated successfully. New Phone No: " + users.getPhone();
						requestBody = requestBody + "\",\"mobile\":\"" + users.getPhone() + "\"}";
						sendProfileUpdationMail(firstName, email, message);
						error.setError_data(0);
						error.setError_msg("");
					}
				} else if (users.getFirstName() != null && !users.getFirstName().isEmpty()) {
					if (resultSet.getInt("user_docs_status") != 0 || resultSet.getInt("user_docs_status") != 1
							|| resultSet.getInt("user_docs_status") != 99) {
						error.setError_data(1);
						error.setError_msg("Name change is restricted for confirmed user");
					} else {
						updateSql = "update users set first_name = ? where user_id = ?";
						ps.close();
						ps = conn.prepareStatement(updateSql);
						ps.setString(1, users.getFirstName());
						ps.setInt(2, users.getUser_id());
						ps.executeUpdate();
						String message = "Your Name is updated successfully.";
						requestBody = requestBody + "\",\"first_name\":\"" + users.getFirstName() + "\"}";
						sendProfileUpdationMail(firstName, email, message);
						error.setError_data(0);
						error.setError_msg("");
					}
				} else {
					error = error.GetErrorSet(1);
				}
			} else {
				error.setError_data(1);
				error.setError_msg("Invalid User or User is blocked.");
			}
			ps.close();
			resultSet.close();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			error.setError_data(1);
			error.setError_msg(e.getMessage());
		} finally {
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		adminUsersResponse.setError(error);
		return adminUsersResponse;
	}

	private void sendProfileUpdationMail(String name, String email, String message) {
		HashMap<String, String> nameVal = new HashMap<>();
		nameVal.put("welcome_message", "Dear");
		nameVal.put("user_name", StringUtils.hasText(name) ? name : email);
		nameVal.put("message1", message);
//		nameVal.put("message2", "");
//		nameVal.put("message3", "Thank you.");
		String mailContent = mailContentBuilderService.build("basic", nameVal);
		String subject = "User profile updation";
		mailClientService.mailthreding(env.getProperty("spring.mail.username"), email, subject, mailContent);
	}

	// ============================================= Health Check Up
	// ===================================================

	// system hard disk and memory usage
	@SuppressWarnings("restriction")
	public ExchangeHealthCheckResponse getMemoryAndSpaceDetails() {
		ExchangeHealthCheckResponse healthcheckResponse = null;
		SystemResponse systemResponse = new SystemResponse();
		try {
			File root = new File("/");
			System.out.println(String.format("Total space: %.2f GB", (double) root.getTotalSpace() / 1073741824));
			systemResponse.setTotalSpace(String.format("%.2f", (double) root.getTotalSpace() / 1073741824));
			System.out.println(String.format("Free space: %.2f GB", (double) root.getFreeSpace() / 1073741824));
			systemResponse.setFreeSpace(String.format("%.2f", (double) root.getFreeSpace() / 1073741824));
			System.out.println(String.format("Usable space: %.2f GB", (double) root.getUsableSpace() / 1073741824));
			systemResponse.setUsableSpace(String.format("%.2f", (double) root.getUsableSpace() / 1073741824));

			com.sun.management.OperatingSystemMXBean memoryMXBean = (com.sun.management.OperatingSystemMXBean) ManagementFactory
					.getOperatingSystemMXBean();
			System.out.println(String.format("Initial memory: %.2f GB",
					(double) memoryMXBean.getTotalPhysicalMemorySize() / 1073741824));
			systemResponse.setTotalPhysicalMemory(
					String.format("%.2f", (double) memoryMXBean.getTotalPhysicalMemorySize() / 1073741824));
			System.out.println(String.format("Used free memory: %.2f GB",
					(double) memoryMXBean.getFreePhysicalMemorySize() / 1073741824));
			systemResponse.setFreePhysicalMemory(
					String.format("%.2f", (double) memoryMXBean.getFreePhysicalMemorySize() / 1073741824));
			System.out.println(String.format("Committed memory: %.2f GB",
					(double) memoryMXBean.getCommittedVirtualMemorySize() / 1073741824));
			systemResponse.setCommittedVirtualMemory(
					String.format("%.2f", (double) memoryMXBean.getCommittedVirtualMemorySize() / 1073741824));

			healthcheckResponse = new ExchangeHealthCheckResponse(new ErrorResponse());
			healthcheckResponse.setSystemResponse(systemResponse);

		} catch (Exception ex) {
			ex.printStackTrace();
			healthcheckResponse = new ExchangeHealthCheckResponse(new ErrorResponse(11));
		}
		return healthcheckResponse;
	}

	// tomcat details
	public ExchangeHealthCheckResponse getTomcatDetails() {
		ErrorResponse errorResponse = new ErrorResponse();
		List<TomcatDetails> tomcatLists = new ArrayList<>();
		TomcatDetails tomcatDetails = null;
		RestTemplate restTemplate = new RestTemplate();
		String url = "";
		try {
			// service tomcat
			url = env.getProperty("project.tomcat.url") + ":8080";
			tomcatDetails = new TomcatDetails();
			tomcatDetails.setTomcateName("Service");
			tomcatDetails.setTomcatport("8080");
			tomcatDetails.setTomcatUrl(url);
			ResponseEntity<String> res = restTemplate.getForEntity(url, String.class);
			if (res.getStatusCode() == HttpStatus.OK) {
				tomcatDetails.setStatus(true);
				log.info("Service Tomcat running");
			} else {
				tomcatDetails.setStatus(false);
				log.info("Service Tomcat stop");
			}
			tomcatLists.add(tomcatDetails);
		} catch (Exception ex) {
			ex.printStackTrace();
			errorResponse = new ErrorResponse(1, "Service Tomcat Exception.");
		}
		try {
			// trade tomcat
			url = env.getProperty("project.tomcat.url") + ":13080";
			tomcatDetails = new TomcatDetails();
			tomcatDetails.setTomcateName("Trade Tomcat");
			tomcatDetails.setTomcatport("13080");
			tomcatDetails.setTomcatUrl(url);
			ResponseEntity<String> res = restTemplate.getForEntity(url, String.class);
			if (res.getStatusCode() == HttpStatus.OK) {
				tomcatDetails.setStatus(true);
				log.info("Trade Tomcat running");
			} else {
				tomcatDetails.setStatus(false);
				log.info("Trade Tomcat running");
			}
			tomcatLists.add(tomcatDetails);
		} catch (Exception ex) {
			ex.printStackTrace();
			errorResponse = new ErrorResponse(1, "Trade Tomcat Exception.");
		}
		try {
			// matching engine tomcat
			url = env.getProperty("project.matching.engine.tomcat.url") + ":8080";
			tomcatDetails = new TomcatDetails();
			tomcatDetails.setTomcateName("Matching Engine");
			tomcatDetails.setTomcatport("8080");
			tomcatDetails.setTomcatUrl(url);
			ResponseEntity<String> res = restTemplate.getForEntity(url, String.class);
			if (res.getStatusCode() == HttpStatus.OK) {
				tomcatDetails.setStatus(true);
				log.info("Matching Engine Tomcat running");
			} else {
				tomcatDetails.setStatus(false);
				log.info("Exchange Tomcat stop");
			}
			tomcatLists.add(tomcatDetails);
		} catch (Exception ex) {
			ex.printStackTrace();
			errorResponse = new ErrorResponse(1, "Matching Engine Tomcat Exception.");
		}

		try {
			// futures matching engine tomcat
			url = env.getProperty("project.futures.matching.engine.tomcat.url") + ":8080";
			tomcatDetails = new TomcatDetails();
			tomcatDetails.setTomcateName("Futures Matching Engine");
			tomcatDetails.setTomcatport("8080");
			tomcatDetails.setTomcatUrl(url);
			ResponseEntity<String> res = restTemplate.getForEntity(url, String.class);
			if (res.getStatusCode() == HttpStatus.OK) {
				tomcatDetails.setStatus(true);
				log.info("Futures Matching Engine Tomcat running");
			} else {
				tomcatDetails.setStatus(false);
				log.info("Exchange Tomcat stop");
			}
			tomcatLists.add(tomcatDetails);
		} catch (Exception ex) {
			ex.printStackTrace();
			errorResponse = new ErrorResponse(1, "Futures Matching Engine Tomcat Exception.");
		}
		try {
			// Stream tomcat
			url = env.getProperty("project.stream.tomcat.url") + ":8080";
			tomcatDetails = new TomcatDetails();
			tomcatDetails.setTomcateName("Stream Tomcat");
			tomcatDetails.setTomcatport("8080");
			tomcatDetails.setTomcatUrl(url);
			ResponseEntity<String> res = restTemplate.getForEntity(url, String.class);
			if (res.getStatusCode() == HttpStatus.OK) {
				tomcatDetails.setStatus(true);
				log.info("Stream Tomcat running");
			} else {
				tomcatDetails.setStatus(false);
				log.info("Stream Tomcat running");
			}
			tomcatLists.add(tomcatDetails);
		} catch (Exception ex) {
			ex.printStackTrace();
			errorResponse = new ErrorResponse(1, "Stream Tomcat Exception.");
		}
		try {
			// Bot serivce tomcat
			url = env.getProperty("project.bot.tomcat.url") + ":8080";
			tomcatDetails = new TomcatDetails();
			tomcatDetails.setTomcateName("Bot Tomcat");
			tomcatDetails.setTomcatport("8080");
			tomcatDetails.setTomcatUrl(url);
			ResponseEntity<String> res = restTemplate.getForEntity(url, String.class);
			if (res.getStatusCode() == HttpStatus.OK) {
				tomcatDetails.setStatus(true);
				log.info("Bot Tomcat running");
			} else {
				tomcatDetails.setStatus(false);
				log.info("Bot Tomcat running");
			}
			tomcatLists.add(tomcatDetails);
		} catch (Exception ex) {
			ex.printStackTrace();
			errorResponse = new ErrorResponse(1, "Bot Tomcat Exception.");
		}
		ExchangeHealthCheckResponse healthcheckResponse = new ExchangeHealthCheckResponse(errorResponse);
		healthcheckResponse.setTomcatListResponse(tomcatLists);
		return healthcheckResponse;
	}

	public ExchangeHealthCheckResponse getDbProcessDetails() {
		ExchangeHealthCheckResponse healthcheckResponse = null;
		Connection conn = null;
		List<DbProcessDetails> dbProcessList = new ArrayList<>();
		DbProcessDetails dbProcessDetails = null;
		try {
			conn = dataSource.getConnection();
			String userCheck = " select upper(resource_name) resource_name, current_utilization, max_utilization,LIMIT_VALUE "
					+ " from v$resource_limit where resource_name in ('processes','sessions')";
			PreparedStatement ps = conn.prepareStatement(userCheck);
			ResultSet resultSet = ps.executeQuery();
			while (resultSet.next()) {
				dbProcessDetails = new DbProcessDetails();
				dbProcessDetails.setResourceName(resultSet.getString("resource_name"));
				dbProcessDetails.setCurrentUtilization(resultSet.getString("current_utilization"));
				dbProcessDetails.setMaxUtilization(resultSet.getString("max_utilization"));
				dbProcessDetails.setLimitValue(resultSet.getString("LIMIT_VALUE"));
				dbProcessList.add(dbProcessDetails);
			}
			resultSet.close();
			ps.close();

			if (!dbProcessList.isEmpty()) {
				healthcheckResponse = new ExchangeHealthCheckResponse(new ErrorResponse());
				healthcheckResponse.setDbProcessReponse(dbProcessList);
			} else {
				healthcheckResponse = new ExchangeHealthCheckResponse(new ErrorResponse(0, "no data"));
			}

		} catch (Exception e) {
			e.printStackTrace();
			healthcheckResponse = new ExchangeHealthCheckResponse(new ErrorResponse(11));
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return healthcheckResponse;
	}

	@SuppressWarnings("unchecked")
	public ExchangeHealthCheckResponse getOfferDetails() {
		ExchangeHealthCheckResponse healthcheckResponse = null;
		List<OfferDetails> offersList = new ArrayList<>();
		List<AssetWiseOfferDetails> assetWiseoffersList = new ArrayList<>();
		List<AssetWiseOfferDetails> assetWiseofferDetailsList = new ArrayList<>();
		RestTemplate restTemplate = new RestTemplate();
		try {
			SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
					.withProcedureName("GET_DATEWISE_OFFER_ACTIVITY")
					.returningResultSet("ASSETWISE_ACTIVE_OFFER_DETAILS",
							BeanPropertyRowMapper.newInstance(AssetWiseOfferDetails.class));
			Map<String, Object> result = simpleJdbcCall.execute();
			offersList = (List<OfferDetails>) result.get("OFFER_ACTIVITY_DETAILS");
			assetWiseoffersList = (List<AssetWiseOfferDetails>) result.get("ASSETWISE_ACTIVE_OFFER_DETAILS");
			long totalOffers = ((BigDecimal) result.get("TOTAL_ACTIVE_OFFER")).longValue();
			for (AssetWiseOfferDetails assetWiseoffersDetails : assetWiseoffersList) {
				String[] arr = assetWiseoffersDetails.getAssetPair().split("-");
				String currency = arr[0];
				String baseCurrency = arr[1];
				String bestOfferUrl = env.getProperty("project.new.orderbook.api.url");
				bestOfferUrl = bestOfferUrl + "symbol=" + currency + baseCurrency + "&limit=1";
				String bestOffer = restTemplate.getForObject(bestOfferUrl, String.class);
				if (bestOffer != null) {
					JSONObject partsData = new JSONObject(bestOffer);
					if (!partsData.get("ask").equals(null) && partsData.getJSONArray("ask").length() > 0) {
						JSONArray jsonArray = partsData.getJSONArray("ask");
						JSONObject asksData = jsonArray.getJSONObject(0);
						String askPrice = asksData.getString("price");
						assetWiseoffersDetails.setBestSellOffer(askPrice);
					}
					if (!partsData.get("bid").equals(null) && partsData.getJSONArray("bid").length() > 0) {
						JSONArray jsonArray = partsData.getJSONArray("bid");
						JSONObject bidsData = jsonArray.getJSONObject(0);
						String bidPrice = bidsData.getString("price");
						assetWiseoffersDetails.setBestBuyOffer(bidPrice);
					}
					assetWiseofferDetailsList.add(assetWiseoffersDetails);
				}
			}
			if (!offersList.isEmpty()) {
				healthcheckResponse = new ExchangeHealthCheckResponse(new ErrorResponse());
				healthcheckResponse.setOfferListResponse(offersList);
				healthcheckResponse.setAssetWiseOffersList(assetWiseofferDetailsList);
				healthcheckResponse.setTotalOffers(totalOffers);
			} else {
				healthcheckResponse = new ExchangeHealthCheckResponse(new ErrorResponse(0, "no data"));
			}

		} catch (Exception e) {
			e.printStackTrace();
			healthcheckResponse = new ExchangeHealthCheckResponse(new ErrorResponse(11));
		}
		return healthcheckResponse;
	}

	@SuppressWarnings("unchecked")
	public ExchangeHealthCheckResponse getfuturesOfferDetails() {
		ExchangeHealthCheckResponse healthcheckResponse = null;
		List<OfferDetails> offersList = new ArrayList<>();
		List<AssetWiseOfferDetails> assetWiseoffersList = new ArrayList<>();
		List<AssetWiseOfferDetails> assetWiseofferDetailsList = new ArrayList<>();
		RestTemplate restTemplate = new RestTemplate();
		try {
			SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
					.withProcedureName("GET_FUTURES_OFFER_ACTIVITY")
					.returningResultSet("ASSETPAIR_ACTIVE_OFFER_DETAILS",
							BeanPropertyRowMapper.newInstance(AssetWiseOfferDetails.class));
			Map<String, Object> result = simpleJdbcCall.execute();
			offersList = (List<OfferDetails>) result.get("FUTURES_OFFER_ACTIVITY_DETAILS");
			assetWiseoffersList = (List<AssetWiseOfferDetails>) result.get("ASSETPAIR_ACTIVE_OFFER_DETAILS");
			long totalOffers = ((BigDecimal) result.get("TOTAL_ACTIVE_OFFER")).longValue();
			for (AssetWiseOfferDetails assetWiseoffersDetails : assetWiseoffersList) {
				String bestOfferUrl = env.getProperty("project.futures.orderbook.api.url");
				bestOfferUrl = bestOfferUrl + "symbol=" + assetWiseoffersDetails.getAssetPair() + "&limit=1";
				String bestOffer = restTemplate.getForObject(bestOfferUrl, String.class);
				if (bestOffer != null) {
					JSONObject partsData = new JSONObject(bestOffer);
					if (!partsData.get("ask").equals(null) && partsData.getJSONArray("ask").length() > 0) {
						JSONArray jsonArray = partsData.getJSONArray("ask");
						JSONObject asksData = jsonArray.getJSONObject(0);
						String askPrice = asksData.getString("price");
						assetWiseoffersDetails.setBestSellOffer(askPrice);
					}
					if (!partsData.get("bid").equals(null) && partsData.getJSONArray("bid").length() > 0) {
						JSONArray jsonArray = partsData.getJSONArray("bid");
						JSONObject bidsData = jsonArray.getJSONObject(0);
						String bidPrice = bidsData.getString("price");
						assetWiseoffersDetails.setBestBuyOffer(bidPrice);
					}
					assetWiseofferDetailsList.add(assetWiseoffersDetails);
				}
			}
			if (!offersList.isEmpty()) {
				healthcheckResponse = new ExchangeHealthCheckResponse(new ErrorResponse());
				healthcheckResponse.setOfferListResponse(offersList);
				healthcheckResponse.setAssetWiseOffersList(assetWiseofferDetailsList);
				healthcheckResponse.setTotalOffers(totalOffers);
			} else {
				healthcheckResponse = new ExchangeHealthCheckResponse(new ErrorResponse(0, "no data"));
			}

		} catch (Exception e) {
			e.printStackTrace();
			healthcheckResponse = new ExchangeHealthCheckResponse(new ErrorResponse(11));
		}
		return healthcheckResponse;
	}

	public ExchangeHealthCheckResponse getNetworkActiveOffers() {
		ExchangeHealthCheckResponse healthcheckResponse = null;
		String url = env.getProperty("project.paybito.new.networkoffers.api") + "offersCount";
		RestTemplate restTemplate = new RestTemplate();
		try {
			String totalOffers = restTemplate.getForObject(url, String.class);
			JSONObject jsonObject = new JSONObject(totalOffers);
			System.out.println("Total : " + jsonObject.getString("activeoffers"));
			healthcheckResponse = new ExchangeHealthCheckResponse(new ErrorResponse());
			healthcheckResponse.setHcNetActiveOffers(jsonObject.getString("activeoffers"));
		} catch (Exception e) {
			e.printStackTrace();
			healthcheckResponse = new ExchangeHealthCheckResponse(new ErrorResponse(11));
		}
		return healthcheckResponse;
	}

	public ExchangeHealthCheckResponse futuresNetworkActiveOffers() {
		ExchangeHealthCheckResponse healthcheckResponse = null;
		String url = env.getProperty("project.paybito.futures.networkoffers.api") + "offersCount";
		RestTemplate restTemplate = new RestTemplate();
		try {
			String totalOffers = restTemplate.getForObject(url, String.class);
			JSONObject jsonObject = new JSONObject(totalOffers);
			System.out.println("Total : " + jsonObject.getString("activeoffers"));
			healthcheckResponse = new ExchangeHealthCheckResponse(new ErrorResponse());
			healthcheckResponse.setHcNetActiveOffers(jsonObject.getString("activeoffers"));
		} catch (Exception e) {
			e.printStackTrace();
			healthcheckResponse = new ExchangeHealthCheckResponse(new ErrorResponse(11));
		}
		return healthcheckResponse;
	}

	public Map<String, Object> exchangeMaintenance(Maintenance maintenance) {
		Map<String, Object> response = new HashMap<>();
		error = new ErrorResponse();
		try {
			SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
					.withProcedureName("ADD_EXCHANGE_MAINTENANCE");
			MapSqlParameterSource input = new MapSqlParameterSource();
			input.addValue("P_STATUS", maintenance.getStatus());
			input.addValue("P_DESCRIPTION", maintenance.getDescription());
			Map<String, Object> result = simpleJdbcCall.execute(input);
			int returnId = ((BigDecimal) result.get("RETURN_ID")).intValue();
			String message = (String) result.get("MESSAGE");
			if (returnId == 1) {
				error.setError_data(0);
				error.setError_msg("");
			} else {
				error.setError_data(1);
				error.setError_msg(message);
			}

		} catch (Exception e) {
			e.printStackTrace();
			error.setError_data(1);
			error.setError_msg(e.getMessage());
		}
		response.put("error", error);
		return response;
	}

	public Map<String, Object> getBlockDetails() {
		Map<String, Object> response = new HashMap<>();
		error = new ErrorResponse();
		final String url = env.getProperty("project.paybito.admin.model.api") + "admin/getBlockDetails";
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders header = new HttpHeaders();
		header.setContentType(MediaType.APPLICATION_JSON);
		JSONObject json = new JSONObject();
		String query = "SELECT CURRENCY_ID,CURRENCY_CODE FROM CURRENCY_MASTER WHERE IS_ACTIVE=1 AND IS_NODE_BLOCK_STATUS=1 ";
		List<Map<String, Object>> latestblockList = new ArrayList<Map<String, Object>>();
		try {
			List<Map<String, Object>> currencyList = jdbcTemplate.queryForList(query);
			for (Map<String, Object> map : currencyList) {
				log.info("Block details for currency : " + String.valueOf(map.get("CURRENCY_CODE")));
				json.put("currencyid", String.valueOf(map.get("CURRENCY_ID")));
				HttpEntity<String> entity = new HttpEntity<>(json.toString(), header);
				String res = restTemplate.postForObject(url, entity, String.class);
				if (res != null) {
					JSONObject partsData = new JSONObject(res);
					map.put("latestBlock",
							partsData.get("latestblock").equals(null) ? "" : partsData.getString("latestblock"));
					latestblockList.add(map);
					error.setError_data(0);
					error.setError_msg("");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			error.setError_data(1);
			error.setError_msg(e.getMessage());
		}

		response.put("latestblockList", latestblockList);
		String status = checkPort();
		Map<String, String> networkStatus = new HashMap<String, String>();
		networkStatus.put("server", status);
		networkStatus.put("currency", "XRP");
		response.put("networkStatus", networkStatus);
		response.put("error", error);
		return response;
	}

	public String checkPort() {
		Socket s = null;
		try {
			s = new Socket(env.getProperty("project.xrp.instance.check"), 10094);
			return "Running";
		} catch (Exception e) {
			e.printStackTrace();
			return "Not running";
		} finally {
			if (s != null)
				try {
					s.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
	}

	public Map<String, Object> tierWiseTradingFees() {
		Map<String, Object> response = new HashMap<>();
		List<TierWiseTradingFees> tradingFeesList = new ArrayList<>();
		String query = "select * from tier_group_master";
		try {
			tradingFeesList = jdbcTemplate.query(query,
					new BeanPropertyRowMapper<TierWiseTradingFees>(TierWiseTradingFees.class));
			response.put("value", tradingFeesList);
			response.put("error", "");
		} catch (EmptyResultDataAccessException e) {
			log.error("empty resultset error: ", e);
			response.put("error", e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			response.put("error", e.getMessage());
		}
		return response;
	}

	public Map<String, Object> updateTierWiseTradingFees(TierWiseTradingFees tierWiseTradingFees) {
		Map<String, Object> response = new HashMap<>();
		int i = 0;
		try {
			if (StringUtils.hasText(tierWiseTradingFees.getAction())) {
				if (tierWiseTradingFees.getAction().equalsIgnoreCase("add")) {
					String query = "insert into tier_group_master (name,maker_fee,taker_fee) values (?,?,?)	";
					i = jdbcTemplate.update(query, tierWiseTradingFees.getName(), tierWiseTradingFees.getMakerFee(),
							tierWiseTradingFees.getTakerFee());
				} else if (tierWiseTradingFees.getAction().equalsIgnoreCase("update")) {
					String query = "update tier_group_master set name = ?,maker_fee = ?,taker_fee = ? where id = ? ";
					i = jdbcTemplate.update(query, tierWiseTradingFees.getName(), tierWiseTradingFees.getMakerFee(),
							tierWiseTradingFees.getTakerFee(), tierWiseTradingFees.getId());
				} else {
					String query = "delete from tier_group_master where id = ? ";
					i = jdbcTemplate.update(query, tierWiseTradingFees.getId());
				}
				if (i > 0) {
					error = new ErrorResponse();
				}
			} else {
				error = new ErrorResponse(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
			error = new ErrorResponse(1, e.getMessage());
		}
		response.put("error", error);
		return response;
	}

	public Map<String, Object> volumeWiseTradingFees() {
		Map<String, Object> response = new HashMap<>();
		List<TradingFees> tradingFeesList = new ArrayList<>();
		String query = "select * from volume_wise_taker_maker_fees";
		try {
			tradingFeesList = jdbcTemplate.query(query, new BeanPropertyRowMapper<TradingFees>(TradingFees.class));
			response.put("value", tradingFeesList);
			response.put("error", "");
		} catch (EmptyResultDataAccessException e) {
			log.error("empty resultset error: ", e);
			response.put("error", e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			response.put("error", e.getMessage());
		}
		return response;
	}

	public AdminUsersResponse updateVolumeWiseTradingFees(TradingFees tradingFees) {
		adminUsersResponse = new AdminUsersResponse();
		error = new ErrorResponse();
		try {
			SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("UPDATE_TRADING_FEES");
			MapSqlParameterSource input = new MapSqlParameterSource();
			input.addValue("P_ID", tradingFees.getId());
			input.addValue("P_VOLUME_FROM", tradingFees.getVolumeFrom());
			input.addValue("P_VOLUME_TO", tradingFees.getVolumeTo());
			input.addValue("P_TAKER_FEE", tradingFees.getTakerFee());
			input.addValue("P_MAKER_FEE", tradingFees.getMakerFee());
			Map<String, Object> result = simpleJdbcCall.execute(input);
			String message = (String) result.get("MESSAGE");
			int returnId = ((BigDecimal) result.get("RETURN_ID")).intValue();
			if (returnId == 1) {
				error.setError_data(0);
				error.setError_msg(message);
			} else {
				error.setError_data(1);
				error.setError_msg(message);
			}
		} catch (Exception e) {
			e.printStackTrace();
			error.setError_data(1);
			error.setError_msg(e.getMessage());
		}
		adminUsersResponse.setError(error);
		return adminUsersResponse;
	}

	public Map<String, Object> getAllCurrencyWiseSettingsData() {
		Map<String, Object> response = new HashMap<>();
		List<CurrencyWiseSetting> currencyWiseSettingList = new ArrayList<CurrencyWiseSetting>();
		String query = "SELECT a.CURRENCY_ID,GET_CURRENCY(a.CURRENCY_ID) CURRENCY,a.MIN_LIMIT,a.DAILY_SEND_LIMIT,a.MONTHLY_SEND_LIMIT,"
				+ "a.DAILY_BUY_LIMIT,a.DAILY_SELL_LIMIT,a.MAKER_CHARGE,a.DISCOUNT_MAKER_CHARGE,a.TAKER_CHARGE,a.DISCOUNT_TAKER_CHARGE,"
				+ "nvl(a.TXN_CHARGE,0) TXN_CHARGE,a.MIN_BALANCE FROM CURRENCY_TRANSACTION_SETTINGS  a,CURRENCY_MASTER b "
				+ "WHERE a.CURRENCY_ID=b.CURRENCY_ID and b.IS_ACTIVE = 1 ORDER BY CURRENCY";
		try {
			currencyWiseSettingList = jdbcTemplate.query(query,
					new BeanPropertyRowMapper<CurrencyWiseSetting>(CurrencyWiseSetting.class));
			response.put("value", currencyWiseSettingList);
			response.put("error", "");
		} catch (EmptyResultDataAccessException e) {
			log.error("empty resultset error: ", e);
			response.put("error", e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			response.put("error", e.getMessage());
		}
		return response;
	}

	public Map<String, Object> currencyWiseMiningFees() {
		Map<String, Object> response = new HashMap<>();
		List<MiningFees> miningFeesList = new ArrayList<>();
		String query = "SELECT CURRENCY_ID,CURRENCY,TOKEN_TYPE,FROMFEE,TOFEE,MIN_FEE,FEE_RATE,ACTUAL_FEES FROM CURRENCY_FEES";
		try {
			miningFeesList = jdbcTemplate.query(query, new BeanPropertyRowMapper<MiningFees>(MiningFees.class));
			response.put("value", miningFeesList);
			response.put("error", "");
		} catch (EmptyResultDataAccessException e) {
			log.error("empty resultset error: ", e);
			response.put("error", e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			response.put("error", e.getMessage());
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> settingsDataFiatConvertion() {
		Map<String, Object> response = new HashMap<>();
		error = new ErrorResponse();
		try {
			SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
					.withProcedureName("GET_ALL_SETTINGS_VALUE");
			Map<String, Object> result = simpleJdbcCall.execute();
			List<Object> returnResult = (List<Object>) result.get("P_SETTINGS_VALUE");
			response.put("response", returnResult);
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

	public AdminUsersResponse updateCurrencyWiseSettingsData(CurrencyWiseSetting currencyWiseSetting) {
		adminUsersResponse = new AdminUsersResponse();
		error = new ErrorResponse();
		String query = "UPDATE CURRENCY_TRANSACTION_SETTINGS SET MIN_LIMIT = ?,DAILY_SEND_LIMIT = ?,MONTHLY_SEND_LIMIT = ?,DAILY_BUY_LIMIT = ?,"
				+ "DAILY_SELL_LIMIT = ?,MAKER_CHARGE = ?,TAKER_CHARGE = ?,DISCOUNT_MAKER_CHARGE = ?,DISCOUNT_TAKER_CHARGE = ?,TXN_CHARGE = ?,MIN_BALANCE = ? "
				+ " WHERE CURRENCY_ID = ? ";
		int i = 0;
		try {
			i = jdbcTemplate.update(query, currencyWiseSetting.getMinLimit(), currencyWiseSetting.getDailySendLimit(),
					currencyWiseSetting.getMonthlySendLimit(), currencyWiseSetting.getDailyBuyLimit(),
					currencyWiseSetting.getDailySellLimit(), currencyWiseSetting.getMakerCharge(),
					currencyWiseSetting.getTakerCharge(), currencyWiseSetting.getDiscountMakerCharge(),
					currencyWiseSetting.getDiscountTakerCharge(), currencyWiseSetting.getTxnCharge(),
					currencyWiseSetting.getMinBalance(), currencyWiseSetting.getCurrencyId());
			if (i == 0) {
				error.setError_data(1);
				error.setError_msg("Updation failed");
			}
		} catch (Exception e) {
			e.printStackTrace();
			error.setError_data(1);
			error.setError_msg(e.getMessage());
		}
		adminUsersResponse.setError(error);
		return adminUsersResponse;
	}

	public AdminUsersResponse updateMiningFees(MiningFees miningFees) {
		adminUsersResponse = new AdminUsersResponse();
		error = new ErrorResponse();
		try {
			SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
					.withProcedureName("UPDATE_CURRENCY_FEES_SP");
			MapSqlParameterSource input = new MapSqlParameterSource();
			input.addValue("P_CURRENCY_ID", miningFees.getCurrencyId());
			input.addValue("P_FROMFEE", miningFees.getFromFee());
			input.addValue("P_TOFEE", miningFees.getToFee());
			input.addValue("P_MINFEE", miningFees.getMinFee());
			input.addValue("P_FEEERATE", miningFees.getFeeRate());
			input.addValue("P_TOKEN_TYPE", miningFees.getTokenType());
			Map<String, Object> result = simpleJdbcCall.execute(input);
			String message = (String) result.get("MESSAGE");
			int returnId = ((BigDecimal) result.get("RETURN_ID")).intValue();
			if (returnId == 1) {
				error.setError_data(0);
				error.setError_msg("");
			} else {
				error.setError_data(1);
				error.setError_msg(message);
			}
		} catch (Exception e) {
			e.printStackTrace();
			error.setError_data(1);
			error.setError_msg(e.getMessage());
		}
		adminUsersResponse.setError(error);
		return adminUsersResponse;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> getFiatCurrencyRate() {
		Map<String, Object> response = new HashMap<>();
		List<CurrencyRate> currencyRateList = new ArrayList<CurrencyRate>();
		error = new ErrorResponse();
		try {
			SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("GET_FIAT_CURRENCY_RATE")
					.returningResultSet("GET_CURRENCY_RATE", BeanPropertyRowMapper.newInstance(CurrencyRate.class));
			Map<String, Object> result = simpleJdbcCall.execute();
			currencyRateList = (List<CurrencyRate>) result.get("GET_CURRENCY_RATE");
			response.put("response", currencyRateList);
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

	public Map<String, Object> updateFiatCurrencyRate(CurrencyRate currencyRate) {
		Map<String, Object> response = new HashMap<>();
		error = new ErrorResponse();
		try {
			SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
					.withProcedureName("UPDATE_FIAT_CURRENCY_RATE");
			MapSqlParameterSource input = new MapSqlParameterSource();
			input.addValue("P_ASSET_PAIR", currencyRate.getAssetPair());
			input.addValue("P_INCR_PREC", currencyRate.getIncrBy());
			Map<String, Object> result = simpleJdbcCall.execute(input);
			String message = (String) result.get("MESSAGE");
			int returnId = ((BigDecimal) result.get("RETURN_ID")).intValue();
			if (returnId == 1) {
				error.setError_data(0);
				error.setError_msg(message);
			} else {
				error.setError_data(1);
				error.setError_msg(message);
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
	public Map<String, Object> getAllCurrencyByType() {
		Map<String, Object> response = new HashMap<>();
		error = new ErrorResponse();
		List<CurrencyMaster> currencyList = null;
		String sql = "select currency_id,currency_code from currency_master where is_Active = 1 and is_base_currency = 1 order by currency_code";
		try {
			currencyList = new ArrayList<CurrencyMaster>();
			currencyList = jdbcTemplate.query(sql, new BeanPropertyRowMapper(CurrencyMaster.class));
			response.put("baseCurrencyList", currencyList);

			currencyList = new ArrayList<CurrencyMaster>();
			sql = "select currency_id,currency_code from currency_master where is_active = 1 and currency_type = 2 order by currency_code";
			currencyList = jdbcTemplate.query(sql, new BeanPropertyRowMapper(CurrencyMaster.class));
			response.put("currencyList", currencyList);

			currencyList = new ArrayList<CurrencyMaster>();
			sql = "select currency_id,currency_code from currency_master where is_Active = 1 and currency_type = 1 order by currency_code";
			currencyList = jdbcTemplate.query(sql, new BeanPropertyRowMapper(CurrencyMaster.class));
			response.put("fiatList", currencyList);

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

	@SuppressWarnings("unchecked")
	public Map<String, Object> getAllCurrency() {
		Map<String, Object> response = new HashMap<>();
		error = new ErrorResponse();
		try {
			SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("GET_CURRENCY_MASTER")
					.returningResultSet("P_RESULTS", BeanPropertyRowMapper.newInstance(CurrencyMaster.class));
			Map<String, Object> result = simpleJdbcCall.execute();
			List<CurrencyMaster> currencyList = (List<CurrencyMaster>) result.get("P_RESULTS");
			response.put("currencyList", currencyList);
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

	public Map<String, Object> getWalletOrder() {
		Map<String, Object> response = new HashMap<>();
		String sql = "select max(wallet_order) from currency_master";
		try {
			BigDecimal walletOrder = DataAccessUtils
					.singleResult(jdbcTemplate.query(sql, new SingleColumnRowMapper<BigDecimal>()));
			if (walletOrder != null) {
				response.put("walletOrder", walletOrder.intValue() + 1);
				error = new ErrorResponse();
			}
		} catch (Exception e) {
			e.printStackTrace();
			error = new ErrorResponse(1, e.getMessage());
		}
		response.put("error", error);
		return response;
	}

	public Map<String, Object> addCurrency(CurrencyMaster currencyMaster) {
		return changesInCurrencyMaster(currencyMaster, "INSERT");
	}

	public Map<String, Object> updateCurrency(CurrencyMaster currencyMaster) {
		return changesInCurrencyMaster(currencyMaster, "UPDATE");
	}

	private Map<String, Object> changesInCurrencyMaster(CurrencyMaster currencyMaster, String action) {
		Map<String, Object> response = new HashMap<>();
		try {
			SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
					.withProcedureName("CURRENCY_MASTER_INSERT_SP");
			MapSqlParameterSource input = new MapSqlParameterSource();
			input.addValue("P_CURRENCY_ID", currencyMaster.getCurrencyId());
			input.addValue("P_CURRENCY_CODE", currencyMaster.getCurrencyCode());
			input.addValue("P_CURRENCY_NAME", currencyMaster.getCurrencyName());
			input.addValue("P_IS_ACTIVE", currencyMaster.getIsActive());
			input.addValue("P_IS_BASE_CURRENCY", currencyMaster.getIsBaseCurrency());
			input.addValue("P_C_PRECISION", currencyMaster.getcPrecision());
			input.addValue("P_IS_SEND_RECEIVED", currencyMaster.getIsSend());
			input.addValue("P_CURRENCY_TYPE", currencyMaster.getCurrencyType());
			input.addValue("P_WALLET_ORDER", currencyMaster.getWalletOrder());
			input.addValue("P_IS_NODE_BLOCK_STATUS", currencyMaster.getIsNodeBlockStatus());
			input.addValue("P_WEBSITE_LINK", currencyMaster.getWebsiteLink());
			input.addValue("P_LISTING_COIN_FLAG", currencyMaster.getListingCoinFlag());
			input.addValue("P_TXN_CHARGE", currencyMaster.getTxnCharge());
			input.addValue("P_AUTO_LIQUIDITY_STATUS", currencyMaster.getAutoLiquidityStatus());
			input.addValue("P_PASSWORD_REQUIRED", currencyMaster.getPasswordRequired());
			input.addValue("P_IS_FUND", currencyMaster.getIsFund());
			input.addValue("P_IS_ISO_MARGIN", currencyMaster.getIsIsoMargin());
			input.addValue("P_IS_CROSS_MARGIN", currencyMaster.getIsCrossMargin());
			input.addValue("P_INTEREST_RATE", currencyMaster.getInterestRate());
			input.addValue("P_LENDER_INTEREST", currencyMaster.getLenderInterest());
			input.addValue("P_EXCHANGE_INTEREST", currencyMaster.getExchangeInterest());
			input.addValue("P_ACTION", action);
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
	public Map<String, Object> getAssetPairDetails() {
		Map<String, Object> response = new HashMap<>();
		error = new ErrorResponse();
		try {
			SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("GET_ASSET_PAIR_DETAILS")
					.returningResultSet("P_RESULTS", BeanPropertyRowMapper.newInstance(AssetPairDetails.class));
			Map<String, Object> result = simpleJdbcCall.execute();
			List<AssetPairDetails> assetPairList = (List<AssetPairDetails>) result.get("P_RESULTS");
			response.put("assetPairList", assetPairList);
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

	public Map<String, Object> getAssetOrder(int baseCurrencyId) {
		Map<String, Object> response = new HashMap<>();
		String sql = "select max(asset_order) from currency_master_mapping where BASE_CURRENCY_ID = ?";
		try {
			BigDecimal assetOrder = DataAccessUtils.singleResult(
					jdbcTemplate.query(sql, new Object[] { baseCurrencyId }, new SingleColumnRowMapper<BigDecimal>()));
			if (assetOrder != null) {
				response.put("assetOrder", assetOrder.intValue());
				error = new ErrorResponse();
			} else {
				error = new ErrorResponse(1, "Invalid Base Currency.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			error = new ErrorResponse(1, e.getMessage());
		}
		response.put("error", error);
		return response;
	}

	public Map<String, Object> addAssetPair(AssetPairDetails assetPairDetails) {
		return changesInAssetPairDetails(assetPairDetails, "INSERT");
	}

	public Map<String, Object> updateAssetPair(AssetPairDetails assetPairDetails) {
		return changesInAssetPairDetails(assetPairDetails, "UPDATE");
	}

	private Map<String, Object> changesInAssetPairDetails(AssetPairDetails assetPairDetails, String action) {
		Map<String, Object> response = new HashMap<>();
		try {
			SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("ASSET_PAIR_INSERT_SP");
			MapSqlParameterSource input = new MapSqlParameterSource();
			input.addValue("P_ID", assetPairDetails.getId());
			input.addValue("P_BASE_CURRENCY_ID", assetPairDetails.getBaseCurrencyId());
			input.addValue("P_CURRENCY_ID", assetPairDetails.getCurrencyId());
			input.addValue("P_ASSET_CODE", assetPairDetails.getAssetCode());
			input.addValue("P_IS_ACTIVE", assetPairDetails.getIsActive());
			input.addValue("P_ASSET_ORDER", assetPairDetails.getAssetOrder());
			input.addValue("P_ASSET_PAIR_TYPE", assetPairDetails.getAssetPairType());
			input.addValue("P_ACTION", action);
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

	@SuppressWarnings("rawtypes")
	public Map<String, Object> getMachingEngineAssetPair() {
		Map<String, Object> response = new HashMap<>();
		error = new ErrorResponse();
		String url = env.getProperty("project.paybito.new.networkoffers.api") + "getAsset";
		System.out.println("url: "+ url);
		RestTemplate restTemplate = new RestTemplate();
		try {
			List assetPairList = restTemplate.getForObject(url, List.class);
			response.put("assetPairList", assetPairList);
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

	public Map<String, Object> addMachingEngineAssetPair(AssetPairDetails assetPairDetails) {
		Map<String, Object> response = new HashMap<>();
		error = new ErrorResponse();
		String url = env.getProperty("project.paybito.new.networkoffers.api") + "addAsset";
		RestTemplate restTemplate = new RestTemplate();
		try {
			JSONObject requestJson = new JSONObject();
			requestJson.put("symbol", assetPairDetails.getAssetCode());
			requestJson.put("assetcode", assetPairDetails.getAssetPair());
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> entity = new HttpEntity<String>(requestJson.toString(), headers);
			String restResponse = restTemplate.postForObject(url, entity, String.class);
			if (restResponse != null) {
				JSONObject responseJson = new JSONObject(restResponse);
				if (responseJson.getInt("statuscode") == 1) {
					error.setError_data(0);
					error.setError_msg(responseJson.getString("message"));
				} else {
					error.setError_data(1);
					error.setError_msg(responseJson.getString("message"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			error.setError_data(1);
			error.setError_msg(e.getMessage());
		}
		response.put("error", error);
		return response;
	}

	public Map<String, Object> getFuturesContractType() {
		Map<String, Object> response = new HashMap<>();
		String query = "select * from futures_contract_type_master where is_active = 1 ";
		try {
			List<FuturesContractTypeMaster> resultList = jdbcTemplate.query(query, new BeanPropertyRowMapper<FuturesContractTypeMaster>(FuturesContractTypeMaster.class));
			response.put("response", resultList);
			response.put("error", "");
		} catch (EmptyResultDataAccessException e) {
			log.error("empty resultset error: ", e);
			response.put("error", e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			response.put("error", e.getMessage());
		}
		return response;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> getFuturesAssetPairDetails() {
		Map<String, Object> response = new HashMap<>();
		error = new ErrorResponse();
		try {
			SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("FUTURES_ASSET_PAIR_DETAILS")
					.returningResultSet("P_RESULTS", BeanPropertyRowMapper.newInstance(AssetPairDetails.class));
			Map<String, Object> result = simpleJdbcCall.execute();
			List<AssetPairDetails> assetPairList = (List<AssetPairDetails>) result.get("P_RESULTS");
			response.put("assetPairList", assetPairList);
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

	public Map<String, Object> addFuturesAssetPair(List<AssetPairDetails> assetPairDetails) {
		System.out.println("addFuturesAssetPair dao called.");
		return changesInFuturesAssetPairDetails(assetPairDetails, "INSERT");
	}

	public Map<String, Object> updateFuturesAssetPair(List<AssetPairDetails> assetPairDetails) {
		return changesInFuturesAssetPairDetails(assetPairDetails, "UPDATE");
	}

	private Map<String, Object> changesInFuturesAssetPairDetails(List<AssetPairDetails> assetPairDetails, String action) {
		Map<String, Object> response = new HashMap<>();
		try {
			int lastInsertedId = 0;
			String message = "";
			SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("FUTURE_ASSETPAIR_INSERT_SP");
			for (AssetPairDetails assetPairDetails2 : assetPairDetails) {
				MapSqlParameterSource input = new MapSqlParameterSource();
				input.addValue("P_ID", assetPairDetails2.getId());
				input.addValue("P_BASE_CURRENCY_ID", assetPairDetails2.getBaseCurrencyId());
				input.addValue("P_BASECURRENCY", assetPairDetails2.getBaseCurrency());
				input.addValue("P_CURRENCY_ID", assetPairDetails2.getCurrencyId());
				input.addValue("P_CURRENCY", assetPairDetails2.getCurrency());
				input.addValue("P_ASSET_CODE", assetPairDetails2.getAssetCode());
				input.addValue("P_ASSET_PAIR_NAME", assetPairDetails2.getAssetPairName());
				input.addValue("P_ASSET_PAIR", assetPairDetails2.getAssetPair());
//				input.addValue("P_ASSET_PAIR_TYPE", assetPairDetails2.getAssetPairType());
				input.addValue("P_ASSET_ORDER", assetPairDetails2.getAssetOrder());
				input.addValue("P_CONTRACT_TYPE_ID", assetPairDetails2.getContractTypeId());
				input.addValue("P_QUARTER_VALUE", assetPairDetails2.getQuarterValue());
				input.addValue("P_AMOUNT_PRECISION", assetPairDetails2.getAmountPrecision());
				input.addValue("P_PRICE_PRECISION", assetPairDetails2.getPricePrecision());
				input.addValue("P_BUY_SELL_FLAG", assetPairDetails2.getBuySellFlag());
				input.addValue("P_IS_ACTIVE", assetPairDetails2.getIsActive());
				input.addValue("P_ACTION", action);
				System.out.println("addFuturesAssetPair proc input : " + input);
				Map<String, Object> result = simpleJdbcCall.execute(input);
				message = (String) result.get("MESSAGE");
				System.out.println("message :" + message);
				System.out.println("result.get(\"LAST_INSERT_ID\") :" + result.get("LAST_INSERT_ID"));
				lastInsertedId = ((BigDecimal) result.get("LAST_INSERT_ID")).intValue();
				
			}
			
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
	
	@SuppressWarnings("rawtypes")
	public Map<String, Object> getFuturesMachingEngineAssetPair() {
		Map<String, Object> response = new HashMap<>();
		error = new ErrorResponse();
		String url = env.getProperty("project.paybito.futures.networkoffers.api") + "getFutureAsset";
		RestTemplate restTemplate = new RestTemplate();
		try {
			List assetPairList = restTemplate.getForObject(url, List.class);
			response.put("assetPairList", assetPairList);
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

	public Map<String, Object> addFuturesMachingEngineAssetPair(List<AssetPairDetails> assetPairDetails) {
		Map<String, Object> response = new HashMap<>();
		error = new ErrorResponse();
		String url = env.getProperty("project.paybito.futures.networkoffers.api") + "addFutureAsset";
		RestTemplate restTemplate = new RestTemplate();
		System.out.println("assetPairDetails list : " + assetPairDetails);
//		Map<String, Object> request = new HashMap<>();
		List<FutureMatchingEngineAsset> requestList = new ArrayList<>();
		try {
			for (AssetPairDetails assetPairDetails2 : assetPairDetails) {
				FutureMatchingEngineAsset requestJson = new FutureMatchingEngineAsset();
				requestJson.setSymbol(Integer.parseInt(assetPairDetails2.getAssetCode()));
				requestJson.setPair_name(assetPairDetails2.getAssetPair());
				requestJson.setContract_type(assetPairDetails2.getContractType());
				requestJson.setExpiry_date(assetPairDetails2.getExpiryDate());
				requestList.add(requestJson);
			}
			String restResponse = restTemplate.postForObject(url, requestList, String.class);
			if (restResponse != null) {
				JSONObject responseJson = new JSONObject(restResponse);
				if (responseJson.getInt("statuscode") == 1) {
					error.setError_data(0);
					error.setError_msg(responseJson.getString("message"));
				} else {
					error.setError_data(1);
					error.setError_msg(responseJson.getString("message"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			error.setError_data(1);
			error.setError_msg(e.getMessage());
		}
		response.put("error", error);
		return response;
	}
	
	public Map<String, Object> marginCallOrAutoliquidity() {
		Map<String, Object> response = new HashMap<>();
		List<MarginCallOrLiquidityValue> resultList = new ArrayList<>();
		String query = "select * from margin_call_auto_liquid";
		try {
			resultList = jdbcTemplate.query(query,
					new BeanPropertyRowMapper<MarginCallOrLiquidityValue>(MarginCallOrLiquidityValue.class));
			;
			response.put("value", resultList);
			response.put("error", "");
		} catch (EmptyResultDataAccessException e) {
			log.error("empty resultset error: ", e);
			response.put("error", e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			response.put("error", e.getMessage());
		}
		return response;
	}

	public Map<String, Object> updateMarginCallOrAutoliquidity(MarginCallOrLiquidityValue marginCallOrLiquidityValue) {
		Map<String, Object> response = new HashMap<>();
		int i = 0;
		try {
			if (marginCallOrLiquidityValue.getId() > 0) {
				String query = "update margin_call_auto_liquid set margin_call = ?,autoliquidation = ? where id = ? ";
				i = jdbcTemplate.update(query, marginCallOrLiquidityValue.getMarginCall(),
						marginCallOrLiquidityValue.getAutoLiquidation(), marginCallOrLiquidityValue.getId());
				if (i > 0) {
					error = new ErrorResponse();
				}
			} else {
				error = new ErrorResponse(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
			error = new ErrorResponse(1, e.getMessage());
		}
		response.put("error", error);
		return response;
	}
	
	public Map<String, Object> futuresTradingFees() {
		Map<String, Object> response = new HashMap<>();
		List<FuturesTradingFees> resultList = new ArrayList<>();
		String query = "select i.currency_id,c.currency_code currency,i.interest_rate,i.txn_charge,c.lender_interest,c.exchange_interest "
				+ "from interest_master i, currency_master c where i.isactive = 1 and i.currency_id= c.currency_id";
		try {
			resultList = jdbcTemplate.query(query,new BeanPropertyRowMapper<FuturesTradingFees>(FuturesTradingFees.class));
			response.put("value", resultList);
			response.put("error", "");
		} catch (EmptyResultDataAccessException e) {
			log.error("empty resultset error: ", e);
			response.put("error", e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			response.put("error", e.getMessage());
		}
		return response;
	}

	public Map<String, Object> updateFuturesTradingFees(FuturesTradingFees futuresTradingFees) {
		Map<String, Object> response = new HashMap<>();
		SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("UPDATE_MARGIN_FEES");
		SqlParameterSource input = new MapSqlParameterSource().addValue("P_CURRENCY_ID", futuresTradingFees.getCurrencyId())
				.addValue("P_INTEREST_RATE", futuresTradingFees.getInterestRate())
				.addValue("P_TXN_CHARGE", futuresTradingFees.getTxnCharge())
				.addValue("P_LENDER_INTEREST_RATE", futuresTradingFees.getLenderInterest())
				.addValue("P_EXCHANGE_INTEREST_RATE", futuresTradingFees.getExchangeInterest());
		Map<String, Object> result = simpleJdbcCall.execute(input);
		int returnId = ((BigDecimal) result.get("RETURN_ID")).intValue();
		String message = (String) result.get("MESSAGE");
		if (returnId == 1) {
			response.put("error", 0);
			response.put("message", message);
		} else {
			response.put("error", 1);
			response.put("message", message);
		}
		return response;
	}

	public AdminUsersResponse franchiseRegistration(FranchiseRequestInput franchiseRequestInput) {
		adminUsersResponse = new AdminUsersResponse();
		error = new ErrorResponse();
		Franchise franchise = new Franchise();
		try {
			SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
					.withProcedureName("ADD_FRANCHISES_DETAILS_SP");
			MapSqlParameterSource input = new MapSqlParameterSource();
			input.addValue("P_COMPANY_NAME", franchiseRequestInput.getCompanyName());
			input.addValue("P_NICK_NAME", franchiseRequestInput.getCompanyNickName());
			input.addValue("P_EMAIL", franchiseRequestInput.getEmail());
			input.addValue("P_REGISTRATION_ID", franchiseRequestInput.getRegistrationId());
			input.addValue("P_ADDRESS_LINE_1", franchiseRequestInput.getAddressLine1());
			input.addValue("P_ADDRESS_LINE_2", franchiseRequestInput.getAddressLine2());
			input.addValue("P_CITY", franchiseRequestInput.getCity());
			input.addValue("P_COUNTRY", franchiseRequestInput.getCity());
			input.addValue("P_ZIP_CODE", franchiseRequestInput.getZipCode());
			input.addValue("P_PHONE", franchiseRequestInput.getPhone());
			input.addValue("P_WEBSITE", franchiseRequestInput.getWebsite());
			input.addValue("P_COMMISSION_PCT", franchiseRequestInput.getCommissionPct());
			input.addValue("P_HOLDER_NAME", franchiseRequestInput.getAccountHolderName());
			input.addValue("P_BANK_NAME", franchiseRequestInput.getBankName());
			input.addValue("P_ACCOUNT_NO", franchiseRequestInput.getAccountNo());
			input.addValue("P_IFSC_NO", franchiseRequestInput.getIfscNo());
			input.addValue("P_IBAN_NO", franchiseRequestInput.getIbanNo());
			input.addValue("P_ROUTING_NO", franchiseRequestInput.getRoutingNo());
			input.addValue("P_BRANCH_ADDRESS", franchiseRequestInput.getBranchAddress());
			input.addValue("P_SWIFT_CODE", franchiseRequestInput.getSwiftCode());

			Map<String, Object> result = simpleJdbcCall.execute(input);
			String franchiseCode = (String) result.get("GET_FRANCHISE_CODE");
			franchise.setFranchiseCode(franchiseCode);
			error.setError_data(0);
			error.setError_msg("");
			adminUsersResponse.setFranchiseResult(franchise);
		} catch (Exception e) {
			e.printStackTrace();
			error.setError_data(1);
			error.setError_msg(e.getMessage());
		}
		adminUsersResponse.setError(error);
		return adminUsersResponse;
	}

	public AdminUsersResponse getAllFranchise() {
		adminUsersResponse = new AdminUsersResponse();
		error = new ErrorResponse();
		List<Franchise> franchiseList = new ArrayList<Franchise>();
		String query = "select id,company_name,company_nick_name from franchises where is_active = 1";
		try {
			franchiseList = jdbcTemplate.query(query, new BeanPropertyRowMapper<Franchise>(Franchise.class));
			error.setError_data(0);
			error.setError_msg("");
			adminUsersResponse.setFranchiseList(franchiseList);
		} catch (Exception e) {
			log.error("error in getAllFranchise: ", e);
			error.setError_data(1);
			error.setError_msg(e.getMessage());
		}
		adminUsersResponse.setError(error);
		return adminUsersResponse;
	}

	public AdminUsersResponse franchiseById(FranchiseRequestInput franchiseRequestInput) {
		adminUsersResponse = new AdminUsersResponse();
		error = new ErrorResponse();
		Franchise franchise = new Franchise();
		FranchiseBankDetails franchiseBankDetails = new FranchiseBankDetails();
		String franchiseSql = "SELECT ID,FRANCHISE_CODE,COMPANY_NAME,COMPANY_NICK_NAME,EMAIL,REGISTRATION_ID,ADDRESS_LINE_1,ADDRESS_LINE_2,"
				+ "CITY,COUNTRY,ZIP_CODE,PHONE,WEBSITE,COMMISSION_PCT FROM FRANCHISES WHERE IS_ACTIVE=1 AND ID=?";
		String franchiseBankDetailsSql = "SELECT FBANK_ID,FRANCHISE_ID,ACCOUNT_HOLDER_NAME,BANK_NAME,ACCOUNT_NO,IFSC_NO,IBAN_NO,"
				+ "ROUTING_NO,BRANCH_ADDRESS,SWIFT_CODE FROM FRANCHISES_BANK_DETAILS WHERE IS_ACTIVE=1 AND FRANCHISE_ID=?";
		try {
			franchise = jdbcTemplate.queryForObject(franchiseSql, new Object[] { franchiseRequestInput.getId() },
					new BeanPropertyRowMapper<Franchise>(Franchise.class));
			franchiseBankDetails = jdbcTemplate.queryForObject(franchiseBankDetailsSql,
					new Object[] { franchiseRequestInput.getId() },
					new BeanPropertyRowMapper<FranchiseBankDetails>(FranchiseBankDetails.class));
			error.setError_data(0);
			error.setError_msg("");
			adminUsersResponse.setFranchiseResult(franchise);
			adminUsersResponse.setFranchiseBankDetails(franchiseBankDetails);
		} catch (EmptyResultDataAccessException e) {
			log.error("empty resultset error: ", e);
			error.setError_data(1);
			error.setError_msg("Invalid franchise id");
		} catch (Exception e) {
			e.printStackTrace();
			error.setError_data(1);
			error.setError_msg(e.getMessage());
		}
		adminUsersResponse.setError(error);
		return adminUsersResponse;
	}

	public AdminUsersResponse updateFranchise(FranchiseRequestInput franchiseRequestInput) {
		adminUsersResponse = new AdminUsersResponse();
		error = new ErrorResponse();
		try {
			SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
					.withProcedureName("UPDATE_FRANCHISES_DETAILS_SP");
			MapSqlParameterSource input = new MapSqlParameterSource();
			input.addValue("P_FRANCHISES_ID", franchiseRequestInput.getId());
			input.addValue("P_COMPANY_NAME", franchiseRequestInput.getCompanyName());
			input.addValue("P_NICK_NAME", franchiseRequestInput.getCompanyNickName());
			input.addValue("P_EMAIL", franchiseRequestInput.getEmail());
			input.addValue("P_ADDRESS_LINE_1", franchiseRequestInput.getAddressLine1());
			input.addValue("P_ADDRESS_LINE_2", franchiseRequestInput.getAddressLine2());
			input.addValue("P_CITY", franchiseRequestInput.getCity());
			input.addValue("P_COUNTRY", franchiseRequestInput.getCountry());
			input.addValue("P_ZIP_CODE", franchiseRequestInput.getZipCode());
			input.addValue("P_PHONE", franchiseRequestInput.getPhone());
			input.addValue("P_WEBSITE", franchiseRequestInput.getWebsite());
			input.addValue("P_COMMISSION_PCT", franchiseRequestInput.getCommissionPct());
			input.addValue("P_HOLDER_NAME", franchiseRequestInput.getAccountHolderName());
			input.addValue("P_BANK_NAME", franchiseRequestInput.getBankName());
			input.addValue("P_ACCOUNT_NO", franchiseRequestInput.getAccountNo());
			input.addValue("P_IFSC_NO", franchiseRequestInput.getIfscNo());
			input.addValue("P_IBAN_NO", franchiseRequestInput.getIbanNo());
			input.addValue("P_ROUTING_NO", franchiseRequestInput.getRoutingNo());
			input.addValue("P_BRANCH_ADDRESS", franchiseRequestInput.getBranchAddress());
			input.addValue("P_SWIFT_CODE", franchiseRequestInput.getSwiftCode());
			Map<String, Object> result = simpleJdbcCall.execute(input);
			int returnId = ((BigDecimal) result.get("return_id")).intValue();
			String message = (String) result.get("message");
			if (returnId == 1) {
				error.setError_data(0);
				error.setError_msg("");
			} else {
				error.setError_data(1);
				error.setError_msg(message);
			}
		} catch (Exception e) {
			adminUsersResponse = new AdminUsersResponse();
			error = new ErrorResponse();
		}
		adminUsersResponse.setError(error);
		return adminUsersResponse;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> earningByFranchiseUsers(FranchiseRequestInput franchiseRequestInput) {
		Map<String, Object> response = new HashMap<>();
		error = new ErrorResponse();
		try {
			SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
					.withProcedureName("GET_FRANCHISE_EARNING_USERWISE");
			MapSqlParameterSource input = new MapSqlParameterSource();
			input.addValue("P_FRANCHISE_ID", franchiseRequestInput.getId());
			Map<String, Object> result = simpleJdbcCall.execute(input);
			List<Map<String, Object>> returnResult = (List<Map<String, Object>>) result.get("EARNING_USERWISE_DETAILS");
			response.put("response", returnResult);
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

	@SuppressWarnings("unchecked")
	public Map<String, Object> totalEarningByDateRange(FranchiseRequestInput franchiseRequestInput) {
		Map<String, Object> response = new HashMap<>();
		error = new ErrorResponse();
		try {
			SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
					.withProcedureName("GET_FRANCHISE_EARNING_SUMMARY");
			MapSqlParameterSource input = new MapSqlParameterSource();
			input.addValue("P_FRANCHISE_ID", franchiseRequestInput.getId());
			input.addValue("P_START_DATE", franchiseRequestInput.getFromDate());
			input.addValue("P_END_DATE", franchiseRequestInput.getToDate());
			Map<String, Object> result = simpleJdbcCall.execute(input);
			List<Object> returnResult = (List<Object>) result.get("GET_EARNING_SUMMARY");
			response.put("response", returnResult.get(0));
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

	@SuppressWarnings("unchecked")
	public Map<String, Object> dayWiseEarning(FranchiseRequestInput franchiseRequestInput) {
		Map<String, Object> response = new HashMap<>();
		error = new ErrorResponse();
		try {
			SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
					.withProcedureName("GET_FRANCHISE_EARNING_DETAILS");
			MapSqlParameterSource input = new MapSqlParameterSource();
			input.addValue("P_FRANCHISE_ID", franchiseRequestInput.getId());
			input.addValue("P_START_DATE", franchiseRequestInput.getFromDate());
			input.addValue("P_END_DATE", franchiseRequestInput.getToDate());
			Map<String, Object> result = simpleJdbcCall.execute(input);
			List<Object> returnResult = (List<Object>) result.get("GET_EARNING_DETAILS");
			response.put("response", returnResult);
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

	@SuppressWarnings("unchecked")
	public Map<String, Object> allReferralUser() {
		Map<String, Object> response = new HashMap<>();
		error = new ErrorResponse();
		try {
			SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
					.withProcedureName("GET_ALL_REFERRED_USERS");
			Map<String, Object> result = simpleJdbcCall.execute();
			List<Map<String, Object>> returnResult = (List<Map<String, Object>>) result
					.get("GET_REFERRED_USERS_DETAILS");
			response.put("response", returnResult);
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

	@SuppressWarnings("unchecked")
	public Map<String, Object> allUsersApiKeyDetails(int pageNo, int noOfRows, String searchString) {
		Map<String, Object> response = new HashMap<>();
		List<ApiKeyDetails> apiKeyDetailsList = new ArrayList<ApiKeyDetails>();
		try {
			SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("GET_USERS_API_DETAILS")
					.returningResultSet("API_DETAILS", BeanPropertyRowMapper.newInstance(ApiKeyDetails.class));
			MapSqlParameterSource input = new MapSqlParameterSource().addValue("P_PAGE_NO", pageNo)
					.addValue("P_TOTAL_ROW_PER_PAGE", noOfRows).addValue("P_SEARCH_STRING", searchString);
			Map<String, Object> result = simpleJdbcCall.execute(input);
			apiKeyDetailsList = (List<ApiKeyDetails>) result.get("ALL_API_DETAILS");
			int totalRow = ((BigDecimal) result.get("TOTAL_ROW")).intValue();
			response.put("apiKeyDetailsList", apiKeyDetailsList);
			response.put("totalRow", totalRow);
			response.put("error", new ErrorResponse());
		} catch (Exception e) {
			log.error("Error in getUserWiseApiKeyDetails : ", e);
			response.put("error", new ErrorResponse(11));
		}
		return response;
	}

	public Map<String, Object> updateUserDetails(Users user) {
		Map<String, Object> response = new HashMap<>();
		String sql = "select email from users where uuid = ? ";
		String query = "UPDATE USERS SET FIRST_NAME = ?,MIDDLE_NAME = ?,LAST_NAME = ?,ADDRESS = ?,STATE = ?,CITY = ?,"
				+ "ZIP = ?,COUNTRY = ?,USER_DOCS_STATUS = ? WHERE UUID = ? ";
		int i = 0;
		try {
			System.out.println("User : " + user.getFirstName() + "  " + user.getMiddleName() + "  " + user.getLastName()
					+ "  " + user.getAddress() + "  " + user.getState() + "  " + user.getCity() + "  " + user.getZip()
					+ "  " + user.getCountry() + "  " + "  " + user.getUser_docs_status() + user.getUuid());
			if (StringUtils.hasText(user.getUuid()) && user.getUser_docs_status() > 0) {
				String email = DataAccessUtils.singleResult(
						jdbcTemplate.query(sql, new Object[] { user.getUuid() }, new SingleColumnRowMapper<String>()));
				if (StringUtils.hasText(email)) {
					i = jdbcTemplate.update(query, user.getFirstName(), user.getMiddleName(), user.getLastName(),
							user.getAddress(), user.getState(), user.getCity(), user.getZip(), user.getCountry(),
							user.getUser_docs_status(), user.getUuid());
					if (i != 0) {
						String subject = updateKyc(user.getUser_docs_status(), user.getUuid());
						if (!StringUtils.hasText(subject)) {
							if (user.getUser_docs_status() == 99) {
								subject = "Verification Unsuccessful";
							} else {
								subject = "Documents Successfully Submitted";
							}
						} 
						String templateName = "kyc_level" + user.getUser_docs_status();
						response.put("error", new ErrorResponse(0, "User details updated successfully."));
						kycMailSend(templateName, email, subject);
					} else {
						response.put("error", new ErrorResponse(1, "User details updation failed."));
					}
				} else {
					response.put("error", new ErrorResponse(1, "Invalid User."));
				}
			} else {
				response.put("error", new ErrorResponse(1));
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Error in updateUserDetails : ", e);
			response.put("error", new ErrorResponse(11));
		}
		return response;
	}

	public void kycMailSend(String templateName, String email, String subject) {
		String mailContent = mailContentBuilderService.build(templateName, new HashMap<>());
		mailClientService.mailthreding(env.getProperty("spring.mail.username"), email, subject, mailContent);
	}

	public Map<String, Object> futuresAllAssetPair() {
		Map<String, Object> response = new HashMap<>();
		String sql = "select BASE_CURRENCY_ID,ASSET_PAIR,ASSET_CODE from currency_master_mapping where asset_pair_type=2 and is_Active=1";
		try {
			List<ContractDetails> contractDetails = jdbcTemplate.query(sql,
					new BeanPropertyRowMapper<ContractDetails>(ContractDetails.class));
			response.put("contractDetails", contractDetails);
			response.put("error", 0);
			response.put("message", "Success.");
		} catch (Exception e) {
			e.printStackTrace();
			response.put("error", new ErrorResponse(11));
		}
		return response;
	}

}
