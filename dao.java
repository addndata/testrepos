import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PublishResult;
import com.paybito.admin.dao.AdminDao;
import com.paybito.admin.dao.EmailService;
import com.paybito.admin.dao.MailClientService;
import com.paybito.admin.dao.MailContentBuilderService;
import com.paybito.admin.dao.NotificationService;
import com.paybito.admin.dao.Utility;
import com.paybito.admin.model.AdminUsers;
import com.paybito.admin.model.AdminUsersResponse;
import com.paybito.admin.model.ApiKeyDetails;
import com.paybito.admin.model.AssetPairDetails;
import com.paybito.admin.model.AssetWiseOfferDetails;
import com.paybito.admin.model.BankDetails;
import com.paybito.admin.model.BrokerExchangeApp;
import com.paybito.admin.model.BrokerKycCharge;
import com.paybito.admin.model.ContractDetails;
import com.paybito.admin.model.CurrencyMaster;
import com.paybito.admin.model.CurrencyRate;
import com.paybito.admin.model.DbProcessDetails;
import com.paybito.admin.model.ErrorResponse;
import com.paybito.admin.model.ExchangeHealthCheckResponse;
import com.paybito.admin.model.Franchise;
import com.paybito.admin.model.FranchiseBankDetails;
import com.paybito.admin.model.FranchiseRequestInput;
import com.paybito.admin.model.FutureMatchingEngineAsset;
import com.paybito.admin.model.FuturesContractTypeMaster;
import com.paybito.admin.model.Maintenance;
import com.paybito.admin.model.MarginCallOrLiquidityValue;
import com.paybito.admin.model.MiningFees;
import com.paybito.admin.model.OfferDetails;
import com.paybito.admin.model.SystemResponse;
import com.paybito.admin.model.TierWiseSetting;
import com.paybito.admin.model.TierWiseTradingFees;
import com.paybito.admin.model.TomcatDetails;
import com.paybito.admin.model.TradingFees;
import com.paybito.admin.model.TradingVolumeWiseAction;
import com.paybito.admin.model.UserTransactions;
import com.paybito.admin.model.Users;
import com.sun.management.OperatingSystemMXBean;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.math.BigDecimal;
import java.net.Socket;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Repository
@EnableScheduling
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
  
  @Autowired
  Utility utility;
  
  static boolean flag = true;
  
  CallableStatement callableStatement = null;
  
  ErrorResponse error = null;
  
  AdminUsersResponse adminUsersResponse = null;
  
  public AdminUsersResponse getAllUsers(Users users) {
    Connection conn = null;
    PreparedStatement ps = null;
    this.adminUsersResponse = new AdminUsersResponse();
    this.error = new ErrorResponse();
    Users _user = null;
    List<Users> _usersList = new ArrayList<>();
    int starts_form = (users.getPage_no() - 1) * users.getNo_of_items_per_page();
    int end = starts_form + users.getNo_of_items_per_page();
    int total_count = 0;
    String sql = "", search_string = "";
    try {
      conn = this.dataSource.getConnection();
      if (users.getSearch_string() != null && users.getSearch_string() != "")
        search_string = "( to_char(user_id) ='" + users.getSearch_string() + "' or first_name like '%" + users.getSearch_string() + "%'  or phone like '%" + users.getSearch_string() + "%' or email like '%" + users.getSearch_string() + "%' or broker_id like '%" + users.getSearch_string() + "%' ) "; 
      String user_doc_status = "", bank_doc_status = "";
      if (users.getUser_docs_status() != null && users.getUser_docs_status() != "") {
        if (users.getUser_docs_status().equals("3")) {
          user_doc_status = " where USER_DOCS_STATUS is null ";
        } else {
          user_doc_status = " where USER_DOCS_STATUS=" + users.getUser_docs_status();
        } 
        if (users.getBank_details_status() != null && users.getBank_details_status() != "")
          if (users.getBank_details_status().equals("4")) {
            bank_doc_status = " and BANK_DETAILS_STATUS is null ";
          } else {
            bank_doc_status = " and BANK_DETAILS_STATUS=" + users.getBank_details_status();
          }  
      } else if (users.getUserTierDocsStatus() != null && users.getUserTierDocsStatus() != "") {
        if (users.getUserTierDocsStatus().equals("3")) {
          user_doc_status = " where user_tier_docs_status is null ";
        } else {
          user_doc_status = " where user_tier_docs_status = " + users.getUserTierDocsStatus();
        } 
        if (users.getBank_details_status() != null && users.getBank_details_status() != "")
          if (users.getBank_details_status().equals("4")) {
            bank_doc_status = " and BANK_DETAILS_STATUS is null ";
          } else {
            bank_doc_status = " and BANK_DETAILS_STATUS=" + users.getBank_details_status();
          }  
      } else if (users.getBank_details_status() != null && users.getBank_details_status() != "") {
        if (users.getBank_details_status().equals("4")) {
          bank_doc_status = " where BANK_DETAILS_STATUS is null ";
        } else {
          bank_doc_status = " where BANK_DETAILS_STATUS=" + users.getBank_details_status();
        } 
      } 
      if (search_string != "") {
        sql = "SELECT a.* FROM (SELECT b.*,rownum b_rownum FROM ( select * from  users where  " + search_string + " order by created desc) b   WHERE rownum <= ?) a WHERE b_rownum >= ?";
      } else {
        sql = "SELECT a.* FROM (SELECT b.*,rownum b_rownum FROM ( select * from  users  " + user_doc_status + bank_doc_status + " order by created desc ) b  WHERE rownum <= ?) a WHERE b_rownum >= ?";
      } 
      log.info("sql: {}", sql);
      ps = conn.prepareStatement(sql);
      ps.setInt(1, end);
      ps.setInt(2, starts_form);
      ResultSet resultSet = ps.executeQuery();
      while (resultSet.next()) {
        _user = new Users();
        _user.setUser_id((resultSet.getObject("user_id") != null) ? resultSet.getInt("user_id") : 0);
        _user.setFirstName((resultSet.getObject("first_name") != null) ? resultSet.getString("first_name") : "");
        _user.setMiddleName(
            (resultSet.getObject("middle_name") != null) ? resultSet.getString("middle_name") : "");
        _user.setLastName((resultSet.getObject("last_name") != null) ? resultSet.getString("last_name") : "");
        _user.setState((resultSet.getObject("state") != null) ? resultSet.getString("state") : "");
        _user.setCity((resultSet.getObject("city") != null) ? resultSet.getString("city") : "");
        _user.setZip((resultSet.getObject("zip") != null) ? resultSet.getString("zip") : "");
        _user.setEmail((resultSet.getObject("email") != null) ? resultSet.getString("email") : "");
        _user.setCountry((resultSet.getObject("country") != null) ? resultSet.getString("country") : "");
        _user.setPhone((resultSet.getObject("phone") != null) ? resultSet.getString("phone") : "");
        _user.setCreated((resultSet.getObject("created") != null) ? resultSet.getString("created") : "");
        _user.setUser_docs_status(
            (resultSet.getObject("user_docs_status") != null) ? (resultSet.getInt("user_docs_status") + "") : 
            "");
        _user.setBank_details_status((resultSet.getObject("bank_details_status") != null) ? (
            resultSet.getInt("bank_details_status") + "") : 
            "");
        _user.setBuy_limit((resultSet.getObject("buy_limit") != null) ? resultSet.getInt("buy_limit") : 0);
        _user.setSell_limit((resultSet.getObject("sell_limit") != null) ? resultSet.getInt("sell_limit") : 0);
        _user.setTierGroup(resultSet.getInt("tier_group"));
        _user.setbBook(resultSet.getInt("b_book"));
        _user.setbBookFutures(resultSet.getInt("b_book_futures"));
        _user.setBrokerId((resultSet.getObject("broker_id") != null) ? resultSet.getString("broker_id") : "");
        _usersList.add(_user);
      } 
      ps.close();
      if (search_string != "") {
        sql = " select count(*) total_count from users where " + search_string;
      } else {
        sql = " select count(*) total_count from users  " + user_doc_status + bank_doc_status;
      } 
      ps = conn.prepareStatement(sql);
      resultSet = ps.executeQuery();
      if (resultSet.next())
        total_count = resultSet.getInt("total_count"); 
      ps.close();
      if (!_usersList.isEmpty()) {
        this.adminUsersResponse.setUsersListResult(_usersList);
        this.adminUsersResponse.setTotalcount(total_count);
        this.error.setError_data(0);
        this.error.setError_msg("");
        this.adminUsersResponse.setError(this.error);
      } else {
        this.error.setError_data(0);
        this.error.setError_msg("no data");
        this.adminUsersResponse.setError(this.error);
      } 
    } catch (Exception e) {
      e.printStackTrace();
      log.error(e.getMessage());
      this.error.setError_data(1);
      this.error.setError_msg(e.getMessage());
      this.adminUsersResponse.setError(this.error);
    } finally {
      if (conn != null)
        try {
          conn.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }  
    } 
    return this.adminUsersResponse;
  }
  
  public AdminUsersResponse getAllUnconfirmUsers(Users users) {
    Connection conn = null;
    PreparedStatement ps = null;
    this.adminUsersResponse = new AdminUsersResponse();
    this.error = new ErrorResponse();
    Users _user = null;
    List<Users> _usersList = new ArrayList<>();
    int starts_form = (users.getPage_no() - 1) * users.getNo_of_items_per_page();
    int end = starts_form + users.getNo_of_items_per_page();
    int total_count = 0;
    String sql = "";
    try {
      conn = this.dataSource.getConnection();
      sql = "SELECT a.* FROM (SELECT b.*,rownum b_rownum FROM ( select * from  users  where ((USER_DOCS_STATUS is null) or (BANK_DETAILS_STATUS is null) or (USER_DOCS_STATUS=0) or  (BANK_DETAILS_STATUS=0) or (USER_DOCS_STATUS=2) or (BANK_DETAILS_STATUS=3))   order by created desc ) b  WHERE rownum <= ?) a WHERE b_rownum >= ?";
      ps = conn.prepareStatement(sql);
      ps.setInt(1, end);
      ps.setInt(2, starts_form);
      ResultSet resultSet = ps.executeQuery();
      while (resultSet.next()) {
        _user = new Users();
        _user.setUser_id((resultSet.getObject("user_id") != null) ? resultSet.getInt("user_id") : 0);
        _user.setFirstName((resultSet.getObject("first_name") != null) ? resultSet.getString("first_name") : "");
        _user.setMiddleName(
            (resultSet.getObject("middle_name") != null) ? resultSet.getString("middle_name") : "");
        _user.setLastName((resultSet.getObject("last_name") != null) ? resultSet.getString("last_name") : "");
        _user.setState((resultSet.getObject("state") != null) ? resultSet.getString("state") : "");
        _user.setCity((resultSet.getObject("city") != null) ? resultSet.getString("city") : "");
        _user.setZip((resultSet.getObject("zip") != null) ? resultSet.getString("zip") : "");
        _user.setEmail((resultSet.getObject("email") != null) ? resultSet.getString("email") : "");
        _user.setCountry((resultSet.getObject("country") != null) ? resultSet.getString("country") : "");
        _user.setPhone((resultSet.getObject("phone") != null) ? resultSet.getString("phone") : "");
        _user.setCreated((resultSet.getObject("created") != null) ? resultSet.getString("created") : "");
        _user.setUser_docs_status(
            (resultSet.getObject("user_docs_status") != null) ? (resultSet.getInt("user_docs_status") + "") : 
            "");
        _user.setBank_details_status((resultSet.getObject("bank_details_status") != null) ? (
            resultSet.getInt("bank_details_status") + "") : 
            "");
        _user.setBuy_limit((resultSet.getObject("buy_limit") != null) ? resultSet.getInt("buy_limit") : 0);
        _user.setSell_limit((resultSet.getObject("sell_limit") != null) ? resultSet.getInt("sell_limit") : 0);
        _user.setTierGroup(resultSet.getInt("tier_group"));
        _user.setbBook(resultSet.getInt("b_book"));
        _user.setbBookFutures(resultSet.getInt("b_book_futures"));
        _user.setBrokerId((resultSet.getObject("broker_id") != null) ? resultSet.getString("broker_id") : "");
        _usersList.add(_user);
      } 
      ps.close();
      sql = " select count(*) total_count from users  where ((USER_DOCS_STATUS is null) or (BANK_DETAILS_STATUS is null) or (USER_DOCS_STATUS=0) or  (BANK_DETAILS_STATUS=0) or (USER_DOCS_STATUS=2) or (BANK_DETAILS_STATUS=3)) ";
      ps = conn.prepareStatement(sql);
      resultSet = ps.executeQuery();
      if (resultSet.next())
        total_count = resultSet.getInt("total_count"); 
      ps.close();
      if (!_usersList.isEmpty()) {
        this.adminUsersResponse.setUsersListResult(_usersList);
        this.adminUsersResponse.setTotalcount(total_count);
        this.error.setError_data(0);
        this.error.setError_msg("");
        this.adminUsersResponse.setError(this.error);
      } else {
        this.error.setError_data(0);
        this.error.setError_msg("no data");
        this.adminUsersResponse.setError(this.error);
      } 
    } catch (Exception e) {
      e.printStackTrace();
      log.error(e.getMessage());
      this.error.setError_data(1);
      this.error.setError_msg(e.getMessage());
      this.adminUsersResponse.setError(this.error);
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /GetAllConfirmUsers\nClass: AdminDao\nMethod: getAllUnconfirmUsers" + 
            
            System.lineSeparator() + errors.toString());
      } 
    } finally {
      if (conn != null)
        try {
          conn.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }  
    } 
    return this.adminUsersResponse;
  }
  
  public AdminUsersResponse addUserInTierGroup(Users users) {
    this.adminUsersResponse = new AdminUsersResponse();
    String query = "UPDATE USERS SET TIER_GROUP = ? WHERE USER_ID = ? ";
    try {
      if (users.getUser_id() > 0) {
        int i = this.jdbcTemplate.update(query, new Object[] { Integer.valueOf(users.getTierGroup()), Integer.valueOf(users.getUser_id()) });
        if (i > 0) {
          this.error = new ErrorResponse();
        } else {
          this.error = new ErrorResponse(1, "Updation failed.");
        } 
      } else {
        this.error = new ErrorResponse(1);
      } 
    } catch (Exception e) {
      e.printStackTrace();
      this.error = new ErrorResponse(1, e.getMessage());
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /addUserInTierGroup\nClass: AdminDao\nMethod: addUserInTierGroup" + 
            System.lineSeparator() + errors.toString());
      } 
    } 
    this.adminUsersResponse.setError(this.error);
    return this.adminUsersResponse;
  }
  
  public AdminUsersResponse totalKycApproveUsersByName(Users users) {
    this.adminUsersResponse = new AdminUsersResponse();
    this.error = new ErrorResponse();
    try {
      String sql = "SELECT count(*) FROM USERS WHERE USER_TAG=1 AND USER_DOCS_STATUS=1 AND UPPER(first_name) LIKE '%'||?||'%' ";
      int total = ((Integer)this.jdbcTemplate.queryForObject(sql, new Object[] { users.getFirstName().toUpperCase() }, Integer.class)).intValue();
      this.adminUsersResponse.setTotalcount(total);
      this.error.setError_data(0);
      this.error.setError_msg("");
    } catch (Exception e) {
      log.error(e.getMessage());
      this.error.setError_data(1);
      this.error.setError_msg(e.getMessage());
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /totalKycApproveUsersByName\nClass: AdminDao\nMethod: totalKycApproveUsersByName" + 
            
            System.lineSeparator() + errors.toString());
      } 
    } 
    this.adminUsersResponse.setError(this.error);
    return this.adminUsersResponse;
  }
  
  public AdminUsersResponse getUsersDetails(Users users, HttpServletRequest request) {
    this.adminUsersResponse = new AdminUsersResponse();
    this.error = new ErrorResponse();
    Users user = null;
    List<Users> usersList = new ArrayList<>();
    try {
      Map<String, Object> result = this.utility.accessCheck(users.getAdminUserId(), 27, request.getRemoteAddr());
      int returnId = ((BigDecimal)result.get("R_RETURN_ID")).intValue();
      String message = (String)result.get("R_MESSAGE");
      if (returnId == 1) {
        String sql = " select user_id,uuid,first_name,middle_name,last_name,address,state,city,zip,email,country,phone,email,user_docs_status,bank_details_status,ssn, profile_pic,address_proof_doc,id_proof_front,id_proof_back,address_proof_doc_2,id_proof_doc,role,created,status,buy_limit,sell_limit, send_limit,receive_limit,user_type,user_tier_type,tin_no,bank_stmt,tax_stmt,user_tier_docs_status, blocked_by,blocked_on,unblocked_by,unblocked_on,dob from users where user_id = ?  ";
        Map<String, Object> verificationData = this.jdbcTemplate.queryForMap(sql, new Object[] { Integer.valueOf(users.getUser_id()) });
        user = new Users();
        user.setUser_id((verificationData.get("user_id") != null) ? 
            Integer.parseInt(verificationData.get("user_id").toString()) : 
            0);
        user.setUuid((verificationData.get("uuid") != null) ? verificationData.get("uuid").toString() : "");
        user.setFirstName(
            (verificationData.get("first_name") != null) ? (String)verificationData.get("first_name") : "");
        user.setMiddleName(
            (verificationData.get("middle_name") != null) ? (String)verificationData.get("middle_name") : 
            "");
        user.setLastName(
            (verificationData.get("last_name") != null) ? (String)verificationData.get("last_name") : "");
        user.setAddress(
            (verificationData.get("address") != null) ? (String)verificationData.get("address") : "");
        user.setState((verificationData.get("state") != null) ? (String)verificationData.get("state") : "");
        user.setCity((verificationData.get("city") != null) ? (String)verificationData.get("city") : "");
        user.setZip((verificationData.get("zip") != null) ? (String)verificationData.get("zip") : "");
        user.setEmail((verificationData.get("email") != null) ? (String)verificationData.get("email") : "");
        user.setCountry(
            (verificationData.get("country") != null) ? (String)verificationData.get("country") : "");
        user.setPhone((verificationData.get("phone") != null) ? (String)verificationData.get("phone") : "");
        user.setSsn((verificationData.get("ssn") != null) ? (String)verificationData.get("ssn") : "");
        user.setProfilePic(
            (verificationData.get("profile_pic") != null) ? (String)verificationData.get("profile_pic") : 
            "");
        user.setAddressProofDoc((verificationData.get("address_proof_doc") != null) ? 
            (String)verificationData.get("address_proof_doc") : 
            "");
        user.setIdProofFront(
            (verificationData.get("id_proof_front") != null) ? (String)verificationData.get("id_proof_front") : 
            "");
        user.setIdProofBack(
            (verificationData.get("id_proof_back") != null) ? (String)verificationData.get("id_proof_back") : 
            "");
        user.setAddress_proof_doc_2((verificationData.get("address_proof_doc_2") != null) ? 
            (String)verificationData.get("address_proof_doc_2") : 
            "");
        user.setId_proof_doc(
            (verificationData.get("id_proof_doc") != null) ? (String)verificationData.get("id_proof_doc") : 
            "");
        user.setRole((verificationData.get("role") != null) ? (String)verificationData.get("role") : "");
        user.setCreated(
            (verificationData.get("created") != null) ? verificationData.get("created").toString() : "");
        user.setStatus((verificationData.get("status") != null) ? (
            (BigDecimal)verificationData.get("status")).intValue() : 
            0);
        user.setUser_docs_status((verificationData.get("user_docs_status") != null) ? 
            String.valueOf(((BigDecimal)verificationData.get("user_docs_status")).intValue()) : 
            "");
        user.setBank_details_status((verificationData.get("bank_details_status") != null) ? 
            String.valueOf(((BigDecimal)verificationData.get("bank_details_status")).intValue()) : 
            "");
        user.setBuy_limit((verificationData.get("buy_limit") != null) ? (
            (BigDecimal)verificationData.get("buy_limit")).intValue() : 
            0);
        user.setSell_limit((verificationData.get("sell_limit") != null) ? (
            (BigDecimal)verificationData.get("sell_limit")).intValue() : 
            0);
        user.setSend_limit((verificationData.get("send_limit") != null) ? (
            (BigDecimal)verificationData.get("send_limit")).intValue() : 
            0);
        user.setReceive_limit((verificationData.get("receive_limit") != null) ? (
            (BigDecimal)verificationData.get("receive_limit")).intValue() : 
            0);
        user.setUserType((verificationData.get("user_type") != null) ? (
            (BigDecimal)verificationData.get("user_type")).intValue() : 
            0);
        user.setUserTierType((verificationData.get("user_tier_type") != null) ? (
            (BigDecimal)verificationData.get("user_tier_type")).intValue() : 
            0);
        user.setTin((verificationData.get("tin_no") != null) ? (String)verificationData.get("tin_no") : "");
        user.setBankStatement(
            (verificationData.get("bank_stmt") != null) ? (String)verificationData.get("bank_stmt") : "");
        user.setTaxStatement(
            (verificationData.get("tax_stmt") != null) ? (String)verificationData.get("tax_stmt") : "");
        user.setUserTierDocsStatus((verificationData.get("user_tier_docs_status") != null) ? 
            String.valueOf(((BigDecimal)verificationData.get("user_tier_docs_status")).intValue()) : 
            "");
        user.setBlockedBy(
            (verificationData.get("blocked_by") != null) ? (String)verificationData.get("blocked_by") : "");
        user.setBlockedOn(
            (verificationData.get("blocked_on") != null) ? verificationData.get("blocked_on").toString() : "");
        user.setUnblockedBy(
            (verificationData.get("unblocked_by") != null) ? (String)verificationData.get("unblocked_by") : 
            "");
        user.setUnblockedOn(
            (verificationData.get("unblocked_on") != null) ? verificationData.get("unblocked_on").toString() : 
            "");
        user.setDob((verificationData.get("dob") != null) ? (String)verificationData.get("dob") : "");
        usersList.add(user);
        this.adminUsersResponse.setUsersListResult(usersList);
        this.error.setError_data(0);
        this.error.setError_msg("");
        this.adminUsersResponse.setError(this.error);
      } else {
        this.error.setError_data(1);
        this.error.setError_msg(message);
        this.adminUsersResponse.setError(this.error);
      } 
    } catch (EmptyResultDataAccessException e) {
      this.error.setError_data(1);
      this.error.setError_msg("Invalid User.");
      this.adminUsersResponse.setError(this.error);
    } catch (Exception e) {
      e.printStackTrace();
      log.error(e.getMessage());
      this.error.setError_data(1);
      this.error.setError_msg(e.getMessage());
      this.adminUsersResponse.setError(this.error);
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /GetUsersDetails\nClass: AdminDao\nMethod: getUsersDetails" + 
            System.lineSeparator() + errors.toString());
      } 
    } 
    return this.adminUsersResponse;
  }
  
  public AdminUsersResponse getUserBankDetails(Users user, HttpServletRequest request) {
    this.adminUsersResponse = new AdminUsersResponse();
    this.error = new ErrorResponse();
    BankDetails bankDetails = new BankDetails();
    try {
      Map<String, Object> result = this.utility.accessCheck(user.getAdminUserId(), 27, request.getRemoteAddr());
      int returnId = ((BigDecimal)result.get("R_RETURN_ID")).intValue();
      String message = (String)result.get("R_MESSAGE");
      if (returnId == 1) {
        String sql = "select benificiary_name,bank_name,bank_address,account_no,account_type,routing_no,swift_code,ifsc_code,verification_amount,bank_cheque_doc from  bank_details where user_id = ? ";
        Map<String, Object> verificationData = this.jdbcTemplate.queryForMap(sql, new Object[] { Integer.valueOf(user.getUser_id()) });
        bankDetails.setBeneficiary_name((verificationData.get("benificiary_name") != null) ? 
            verificationData.get("benificiary_name").toString() : 
            "");
        bankDetails.setBank_name(
            (verificationData.get("bank_name") != null) ? verificationData.get("bank_name").toString() : "");
        bankDetails.setBankAddress(
            (verificationData.get("bank_address") != null) ? verificationData.get("bank_address").toString() : 
            "");
        bankDetails.setAccountType(
            (verificationData.get("account_type") != null) ? verificationData.get("account_type").toString() : 
            "");
        bankDetails.setAccount_no(
            (verificationData.get("account_no") != null) ? verificationData.get("account_no").toString() : 
            "");
        bankDetails.setRouting_no(
            (verificationData.get("routing_no") != null) ? verificationData.get("routing_no").toString() : 
            "");
        bankDetails.setSwiftCode(
            (verificationData.get("swift_code") != null) ? verificationData.get("swift_code").toString() : 
            "");
        bankDetails.setIfscCode(
            (verificationData.get("ifsc_code") != null) ? verificationData.get("ifsc_code").toString() : "");
        bankDetails.setVerification_amount((verificationData.get("verification_amount") != null) ? 
            Double.valueOf(verificationData.get("verification_amount").toString()).doubleValue() : 
            0.0D);
        bankDetails.setBank_cheque((verificationData.get("bank_cheque_doc") != null) ? 
            verificationData.get("bank_cheque_doc").toString() : 
            "");
        this.adminUsersResponse.setBankDetailsResult(bankDetails);
        this.error.setError_data(0);
        this.error.setError_msg("");
      } else {
        this.error.setError_data(1);
        this.error.setError_msg(message);
      } 
    } catch (EmptyResultDataAccessException e) {
      this.error.setError_data(1);
      this.error.setError_msg("Invalid User.");
      this.adminUsersResponse.setError(this.error);
    } catch (Exception e) {
      e.printStackTrace();
      log.error(e.getMessage());
      this.error.setError_data(1);
      this.error.setError_msg(e.getMessage());
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /GetUserBankDetails\nClass: AdminDao\nMethod: getUserBankDetails" + 
            System.lineSeparator() + errors.toString());
      } 
    } 
    this.adminUsersResponse.setError(this.error);
    return this.adminUsersResponse;
  }
  
  public AdminUsersResponse approveUsers(Users user, HttpServletRequest request) {
    this.adminUsersResponse = new AdminUsersResponse();
    String title = "", message = "";
    try {
      message = this.emailService.check2Fa(user.getAdminUserId(), user.getOtp());
      if (message.equalsIgnoreCase("Success")) {
        String sql = "select is_blocked from admin_user_login where user_id = ? ";
        BigDecimal status = (BigDecimal)DataAccessUtils.singleResult(this.jdbcTemplate.query(sql, new Object[] { Integer.valueOf(user.getAdminUserId()) }, (RowMapper)new SingleColumnRowMapper()));
        if (status != null) {
          if (status.intValue() == 1) {
            this.error = new ErrorResponse(1, "You are not authorized.");
          } else {
            Map<String, Object> result = this.utility.accessCheck(user.getAdminUserId(), 27, request
                .getRemoteAddr());
            int returnId = ((BigDecimal)result.get("R_RETURN_ID")).intValue();
            message = (String)result.get("R_MESSAGE");
            if (returnId == 1) {
              sql = " select user_id,user_tier_type from users where user_id = ? and status = 1 and user_docs_status=0 ";
              Map<String, Object> verificationData = this.jdbcTemplate.queryForMap(sql, new Object[] { Integer.valueOf(user.getUser_id()) });
              int action = 0;
              if (user.getUser_docs_status().equals("1")) {
                sql = "update users set user_docs_status= ?,user_tier_type = 2,tier_2_updated_date=current_timestamp where user_id = ? ";
                action = 1;
              } else {
                sql = "update users set user_docs_status= ?,tier_2_updated_date=current_timestamp where user_id = ? ";
                action = 2;
              } 
              int i = this.jdbcTemplate.update(sql, new Object[] { user.getUser_docs_status(), Integer.valueOf(user.getUser_id()) });
              if (i > 0) {
                sql = "INSERT INTO ADMIN_USER_ACTIVITY (ADMIN_USER_ID,USER_ID,TIER2_ACTIVITY,KYC_ACTIVITY_DATE) VALUES (?,?,?,CURRENT_TIMESTAMP) ";
                this.jdbcTemplate.update(sql, new Object[] { Integer.valueOf(user.getAdminUserId()), Integer.valueOf(user.getUser_id()), Integer.valueOf(action) });
              } 
              sql = " select u.first_name,u.email,u.android_device_token,u.ios_device_token,uas.sound_alert from users u  inner join user_app_settings uas on u.user_id=uas.user_id and u.user_id = ? ";
              verificationData = this.jdbcTemplate.queryForMap(sql, new Object[] { Integer.valueOf(user.getUser_id()) });
              if (!verificationData.isEmpty()) {
                if (user.getUser_docs_status().equals("1")) {
                  title = "Documents Approved";
                  message = "Your documents have been approved.";
                } else {
                  title = "Documents Declined";
                  message = "Your documents have been declined.Kindly check and resubmit.";
                } 
                if (verificationData.get("android_device_token") != null)
                  this.notificationService.send_notification_android(message, title, verificationData
                      .get("android_device_token").toString(), ((BigDecimal)verificationData
                      .get("sound_alert")).intValue(), user
                      .getUser_id() + "", "", 4); 
                if (verificationData.get("ios_device_token") != null)
                  this.notificationService.send_notification_ios(message, title, verificationData
                      .get("ios_device_token").toString(), ((BigDecimal)verificationData
                      .get("sound_alert")).intValue(), user
                      .getUser_id() + "", "", 4); 
                String customer_email = verificationData.get("email").toString();
                HashMap<String, String> nameVal = new HashMap<>();
                nameVal.put("welcome_message", "Dear");
                nameVal.put("user_name", verificationData.get("first_name").toString());
                nameVal.put("message1", message);
                nameVal.put("message2", "");
                nameVal.put("message3", "");
                String mail_content = this.mailContentBuilderService.build("basic", nameVal);
                String subject = "Your Documents Verification Status.";
                this.mailClientService.mailthreding(this.env.getProperty("spring.mail.username"), customer_email, subject, mail_content);
                this.error = new ErrorResponse(0, message);
              } 
            } else {
              this.error = new ErrorResponse(1, message);
            } 
          } 
        } else {
          this.error = new ErrorResponse(1, "Invalid Admin User.");
        } 
      } else {
        this.error = new ErrorResponse(1, message);
      } 
    } catch (EmptyResultDataAccessException e) {
      this.error = new ErrorResponse(1, "User is blocked or Documents not submitted");
    } catch (Exception e) {
      e.printStackTrace();
      log.error(e.getMessage());
      this.error = new ErrorResponse(1, e.getMessage());
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /ApproveUsers\nClass: AdminDao\nMethod: approveUsers" + 
            System.lineSeparator() + errors.toString());
      } 
    } 
    this.adminUsersResponse.setError(this.error);
    return this.adminUsersResponse;
  }
  
  public AdminUsersResponse approveUserBankDetails(Users user, HttpServletRequest request) {
    this.adminUsersResponse = new AdminUsersResponse();
    String title = "", message = "";
    try {
      message = this.emailService.check2Fa(user.getAdminUserId(), user.getOtp());
      if (message.equalsIgnoreCase("Success")) {
        String sql = "select is_blocked from admin_user_login where user_id = ?";
        BigDecimal status = (BigDecimal)DataAccessUtils.singleResult(this.jdbcTemplate.query(sql, new Object[] { Integer.valueOf(user.getAdminUserId()) }, (RowMapper)new SingleColumnRowMapper()));
        if (status != null) {
          if (status.intValue() == 1) {
            this.error = new ErrorResponse(1, "You are not authorized.");
          } else {
            Map<String, Object> result = this.utility.accessCheck(user.getAdminUserId(), 27, request
                .getRemoteAddr());
            int returnId = ((BigDecimal)result.get("R_RETURN_ID")).intValue();
            message = (String)result.get("R_MESSAGE");
            if (returnId == 1) {
              sql = " select user_id from users where user_id = ? and status = 1 and bank_details_status=0 ";
              BigDecimal userCheck = (BigDecimal)DataAccessUtils.singleResult(this.jdbcTemplate.query(sql, new Object[] { Integer.valueOf(user.getUser_id()) }, (RowMapper)new SingleColumnRowMapper()));
              if (userCheck != null) {
                sql = "update users set bank_details_status= ? where user_id = ? ";
                this.jdbcTemplate.update(sql, new Object[] { user.getBank_details_status(), Integer.valueOf(user.getUser_id()) });
                sql = " select u.first_name,u.email,u.android_device_token,u.ios_device_token,uas.sound_alert from users u  inner join user_app_settings uas on u.user_id=uas.user_id and u.user_id = ? ";
                Map<String, Object> verificationData = this.jdbcTemplate.queryForMap(sql, new Object[] { Integer.valueOf(user.getUser_id()) });
                if (!verificationData.isEmpty()) {
                  if (user.getBank_details_status().equals("2")) {
                    title = "Payment Method Approved";
                    message = "Your payment method has been approved.";
                  } else if (user.getBank_details_status().equals("3")) {
                    title = "Payment Method Declined";
                    message = "\tYour payment method has been declined.Please resubmit the details after checking.";
                  } 
                  if (verificationData.get("android_device_token") != null)
                    this.notificationService.send_notification_android(message, title, verificationData
                        .get("android_device_token").toString(), ((BigDecimal)verificationData
                        .get("sound_alert")).intValue(), user
                        .getUser_id() + "", "", 4); 
                  if (verificationData.get("ios_device_token") != null)
                    this.notificationService.send_notification_ios(message, title, verificationData
                        .get("ios_device_token").toString(), ((BigDecimal)verificationData
                        .get("sound_alert")).intValue(), user
                        .getUser_id() + "", "", 4); 
                  String customer_email = verificationData.get("email").toString();
                  HashMap<String, String> nameVal = new HashMap<>();
                  nameVal.put("welcome_message", "Dear");
                  nameVal.put("user_name", verificationData.get("first_name").toString());
                  nameVal.put("message1", message);
                  String mail_content = this.mailContentBuilderService.build("basic", nameVal);
                  String subject = "Your Payment Method Verification Status ";
                  this.mailClientService.mailthreding(this.env.getProperty("spring.mail.username"), customer_email, subject, mail_content);
                } 
                this.error = new ErrorResponse();
              } else {
                this.error = new ErrorResponse(1, "User is blocked or Bank details not submitted");
              } 
            } else {
              this.error = new ErrorResponse(1, message);
            } 
          } 
        } else {
          this.error = new ErrorResponse(1, "Invalid Admin User.");
        } 
      } else {
        this.error = new ErrorResponse(1, message);
      } 
    } catch (Exception e) {
      e.printStackTrace();
      log.error(e.getMessage());
      this.error = new ErrorResponse(1, e.getMessage());
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /ApproveUserBankDetails\nClass: AdminDao\nMethod: approveUserBankDetails" + 
            
            System.lineSeparator() + errors.toString());
      } 
    } 
    this.adminUsersResponse.setError(this.error);
    return this.adminUsersResponse;
  }
  
  public AdminUsersResponse updateUserAccountStatus(Users user, HttpServletRequest request) {
    this.adminUsersResponse = new AdminUsersResponse();
    this.error = new ErrorResponse();
    String sql = "";
    String message = "";
    try {
      message = this.emailService.check2Fa(user.getAdminUserId(), user.getOtp());
      if (message.equalsIgnoreCase("Success")) {
        Map<String, Object> result = this.utility.accessCheck(user.getAdminUserId(), 89, request.getRemoteAddr());
        int returnId = ((BigDecimal)result.get("R_RETURN_ID")).intValue();
        message = (String)result.get("R_MESSAGE");
        if (returnId == 1) {
          if (user.getStatus() == 2) {
            sql = "update users set status = ?,blocked_by = ?,blocked_on = current_timestamp where user_id = ?";
            message = "Account is blocked successfully.";
          } else if (user.getStatus() == 1) {
            sql = "update users set status = ?,unblocked_by = ?,unblocked_on = current_timestamp where user_id = ?";
            message = "Account is unblocked successfully.";
          } 
          int i = this.jdbcTemplate.update(sql, new Object[] { Integer.valueOf(user.getStatus()), "Admin " + user.getAdminUserId(), 
                Integer.valueOf(user.getUser_id()) });
          if (i == 1) {
            this.error.setError_data(0);
            this.error.setError_msg(message);
          } else {
            this.error.setError_data(1);
            this.error.setError_msg("Account updation failed, please try again.");
          } 
        } else {
          this.error = new ErrorResponse(1, message);
        } 
      } else {
        this.error = new ErrorResponse(1, message);
      } 
    } catch (Exception e) {
      log.error(e.getMessage());
      this.error.setError_data(1);
      this.error.setError_msg(e.getMessage());
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /blockUserAccount\nClass: AdminDao\nMethod: updateUserAccountStatus" + 
            
            System.lineSeparator() + errors.toString());
      } 
    } 
    this.adminUsersResponse.setError(this.error);
    return this.adminUsersResponse;
  }
  
  public AdminUsersResponse restrictBuy(Users user, HttpServletRequest request) {
    this.adminUsersResponse = new AdminUsersResponse();
    this.error = new ErrorResponse();
    try {
      Map<String, Object> result = this.utility.accessCheck(user.getAdminUserId(), 22, request.getRemoteAddr());
      int returnId = ((BigDecimal)result.get("R_RETURN_ID")).intValue();
      String message = (String)result.get("R_MESSAGE");
      if (returnId == 1) {
        String sql = "update users set buy_limit= ? where user_id = ? ";
        this.jdbcTemplate.update(sql, new Object[] { Integer.valueOf(user.getBuy_limit()), Integer.valueOf(user.getUser_id()) });
        this.error.setError_data(0);
        this.error.setError_msg("");
      } else {
        this.error.setError_data(1);
        this.error.setError_msg(message);
      } 
    } catch (Exception e) {
      e.printStackTrace();
      log.error(e.getMessage());
      this.error.setError_data(1);
      this.error.setError_msg(e.getMessage());
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /RestrictBuy\nClass: AdminDao\nMethod: restrictBuy" + 
            System.lineSeparator() + errors.toString());
      } 
    } 
    this.adminUsersResponse.setError(this.error);
    return this.adminUsersResponse;
  }
  
  public AdminUsersResponse restrictSell(Users user, HttpServletRequest request) {
    this.adminUsersResponse = new AdminUsersResponse();
    this.error = new ErrorResponse();
    try {
      Map<String, Object> result = this.utility.accessCheck(user.getAdminUserId(), 22, request.getRemoteAddr());
      int returnId = ((BigDecimal)result.get("R_RETURN_ID")).intValue();
      String message = (String)result.get("R_MESSAGE");
      if (returnId == 1) {
        String sql = "update users set sell_limit= ? where user_id = ? ";
        this.jdbcTemplate.update(sql, new Object[] { Integer.valueOf(user.getSell_limit()), Integer.valueOf(user.getUser_id()) });
        this.error.setError_data(0);
        this.error.setError_msg("");
      } else {
        this.error.setError_data(1);
        this.error.setError_msg(message);
      } 
    } catch (Exception e) {
      e.printStackTrace();
      log.error(e.getMessage());
      this.error.setError_data(1);
      this.error.setError_msg(e.getMessage());
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /RestrictSell\nClass: AdminDao\nMethod: restrictSell" + 
            System.lineSeparator() + errors.toString());
      } 
    } 
    this.adminUsersResponse.setError(this.error);
    return this.adminUsersResponse;
  }
  
  public AdminUsersResponse restrictSend(Users user, HttpServletRequest request) {
    this.adminUsersResponse = new AdminUsersResponse();
    this.error = new ErrorResponse();
    try {
      Map<String, Object> result = this.utility.accessCheck(user.getAdminUserId(), 22, request.getRemoteAddr());
      int returnId = ((BigDecimal)result.get("R_RETURN_ID")).intValue();
      String message = (String)result.get("R_MESSAGE");
      if (returnId == 1) {
        String sql = "update users set send_limit= ? where user_id = ? ";
        this.jdbcTemplate.update(sql, new Object[] { Integer.valueOf(user.getSend_limit()), Integer.valueOf(user.getUser_id()) });
        this.error.setError_data(0);
        this.error.setError_msg("");
      } else {
        this.error.setError_data(1);
        this.error.setError_msg(message);
      } 
    } catch (Exception e) {
      e.printStackTrace();
      log.error(e.getMessage());
      this.error.setError_data(1);
      this.error.setError_msg(e.getMessage());
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /RestrictSend\nClass: AdminDao\nMethod: restrictSend" + 
            System.lineSeparator() + errors.toString());
      } 
    } 
    this.adminUsersResponse.setError(this.error);
    return this.adminUsersResponse;
  }
  
  public AdminUsersResponse restrictReceive(Users user, HttpServletRequest request) {
    this.adminUsersResponse = new AdminUsersResponse();
    this.error = new ErrorResponse();
    try {
      Map<String, Object> result = this.utility.accessCheck(user.getAdminUserId(), 22, request.getRemoteAddr());
      int returnId = ((BigDecimal)result.get("R_RETURN_ID")).intValue();
      String message = (String)result.get("R_MESSAGE");
      if (returnId == 1) {
        String sql = "update users set receive_limit= ? where user_id = ? ";
        this.jdbcTemplate.update(sql, new Object[] { Integer.valueOf(user.getReceive_limit()), Integer.valueOf(user.getUser_id()) });
        this.error.setError_data(0);
        this.error.setError_msg("");
      } else {
        this.error.setError_data(1);
        this.error.setError_msg(message);
      } 
    } catch (Exception e) {
      e.printStackTrace();
      log.error(e.getMessage());
      this.error.setError_data(1);
      this.error.setError_msg(e.getMessage());
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /RestrictReceive\nClass: AdminDao\nMethod: restrictReceive" + 
            System.lineSeparator() + errors.toString());
      } 
    } 
    this.adminUsersResponse.setError(this.error);
    return this.adminUsersResponse;
  }
  
  public AdminUsersResponse SendMailToUser(Users user) {
    Connection conn = null;
    PreparedStatement ps = null;
    this.adminUsersResponse = new AdminUsersResponse();
    this.error = new ErrorResponse();
    try {
      conn = this.dataSource.getConnection();
      String sql = " select first_name,email from users where user_id = ? ";
      ps = conn.prepareStatement(sql);
      ps.setInt(1, user.getUser_id());
      ResultSet rs1 = ps.executeQuery();
      if (rs1.next()) {
        String customer_email = rs1.getString("email");
        HashMap<String, String> nameVal = new HashMap<>();
        nameVal.put("message1", user.getMessage());
        String mail_content = this.mailContentBuilderService.build("basic", nameVal);
        String subject = "Mail From Admin";
        this.mailClientService.mailthreding(this.env.getProperty("spring.mail.username"), customer_email, subject, mail_content);
        this.error.setError_data(0);
        this.error.setError_msg("");
      } else {
        this.error.setError_data(0);
        this.error.setError_msg("Invalid user id.");
      } 
      ps.close();
    } catch (Exception e) {
      e.printStackTrace();
      log.error(e.getMessage());
      this.error.setError_data(1);
      this.error.setError_msg(e.getMessage());
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /SendMailToUser\nClass: AdminDao\nMethod: SendMailToUser" + 
            System.lineSeparator() + errors.toString());
      } 
    } finally {
      if (conn != null)
        try {
          conn.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }  
    } 
    this.adminUsersResponse.setError(this.error);
    return this.adminUsersResponse;
  }
  
  public AdminUsersResponse SendMailFromPaybitoSite(String user_email, String email_cc, String email_content, MultipartFile coin_logo, MultipartFile legal_docs, String recaptchaResponse) {
    this.adminUsersResponse = new AdminUsersResponse();
    this.error = new ErrorResponse();
    try {
      String response = this.emailService.recaptchaService(recaptchaResponse);
      if (response != null && response.equalsIgnoreCase("success")) {
        log.info("coin list mail ");
        String subject = "Mail Sent From " + this.env.getProperty("project.company.product") + ".com for Coin Listing ";
        if (coin_logo != null && legal_docs != null) {
          log.info("logo and docs file upload ");
          this.mailClientService.mailthreding(this.env.getProperty("spring.mail.username"), user_email, subject, email_content, email_cc, coin_logo, legal_docs);
        } else if (coin_logo != null) {
          log.info("only logo  file upload ");
          this.mailClientService.mailthreding(this.env.getProperty("spring.mail.username"), user_email, subject, email_content, email_cc, coin_logo);
        } else if (legal_docs != null) {
          log.info(" only docs file upload ");
          this.mailClientService.mailthreding(this.env.getProperty("spring.mail.username"), user_email, subject, email_content, email_cc, legal_docs);
        } else {
          log.info(" without file upload ");
          this.mailClientService.mailthreding(this.env.getProperty("spring.mail.username"), user_email, subject, email_content, email_cc);
        } 
        this.error.setError_data(0);
        this.error.setError_msg("");
      } else {
        this.error.setError_data(1);
        this.error.setError_msg("Captcha unverified.");
      } 
    } catch (Exception e) {
      e.printStackTrace();
      log.error(e.getMessage());
      this.error.setError_data(1);
      this.error.setError_msg(e.getMessage());
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /SendMailFromPaybitoSite\nClass: AdminDao\nMethod: SendMailFromPaybitoSite" + 
            
            System.lineSeparator() + errors.toString());
      } 
    } 
    this.adminUsersResponse.setError(this.error);
    return this.adminUsersResponse;
  }
  
  public AdminUsers getUserCredential(String UserName) {
    Connection conn = null;
    PreparedStatement ps = null;
    AdminUsers _adminuser = new AdminUsers();
    try {
      conn = this.dataSource.getConnection();
      String sql = "select email,password,role from  admin_users where email = ? ";
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
      if (conn != null)
        try {
          conn.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }  
    } 
    return _adminuser;
  }
  
  protected String getSaltString() {
    String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ!$%+*1234567890abcdefghijklmnopqrstuvwxyz";
    StringBuilder salt = new StringBuilder();
    Random rnd = new Random();
    while (salt.length() < 12) {
      int index = (int)(rnd.nextFloat() * SALTCHARS.length());
      salt.append(SALTCHARS.charAt(index));
    } 
    String saltStr = salt.toString();
    return saltStr;
  }
  
  public AdminUsersResponse changeUserTier(Users users, HttpServletRequest request) {
    this.adminUsersResponse = new AdminUsersResponse();
    String title = "", message = "";
    try {
      message = this.emailService.check2Fa(users.getAdminUserId(), users.getOtp());
      if (message.equalsIgnoreCase("Success")) {
        String sql = "select is_blocked from admin_user_login where user_id = ? ";
        BigDecimal status = (BigDecimal)DataAccessUtils.singleResult(this.jdbcTemplate.query(sql, new Object[] { Integer.valueOf(users.getAdminUserId()) }, (RowMapper)new SingleColumnRowMapper()));
        if (status != null) {
          if (status.intValue() == 1) {
            this.error = new ErrorResponse(1, "You are not authorized.");
          } else {
            Map<String, Object> result = this.utility.accessCheck(users.getAdminUserId(), 27, request
                .getRemoteAddr());
            int returnId = ((BigDecimal)result.get("R_RETURN_ID")).intValue();
            message = (String)result.get("R_MESSAGE");
            if (returnId == 1) {
              sql = " select user_id from users where user_id = ? and status = 1 and user_docs_status=1 ";
              BigDecimal user = (BigDecimal)DataAccessUtils.singleResult(this.jdbcTemplate.query(sql, new Object[] { Integer.valueOf(users.getUser_id()) }, (RowMapper)new SingleColumnRowMapper()));
              if (user != null) {
                int action = 0;
                if (users.getUserTierDocsStatus().equals("1")) {
                  sql = "update users set user_tier_docs_status = ?,user_tier_type = 3,tier_3_updated_date=current_timestamp where user_id = ? ";
                  action = 1;
                } else {
                  sql = "update users set user_tier_docs_status = ?,tier_3_updated_date=current_timestamp where user_id = ? ";
                  action = 2;
                } 
                int i = this.jdbcTemplate.update(sql, new Object[] { users.getUserTierDocsStatus(), Integer.valueOf(users.getUser_id()) });
                if (i > 0) {
                  sql = "INSERT INTO ADMIN_USER_ACTIVITY (ADMIN_USER_ID,USER_ID,TIER3_ACTIVITY,KYC_ACTIVITY_DATE) VALUES (?,?,?,CURRENT_TIMESTAMP) ";
                  this.jdbcTemplate.update(sql, new Object[] { Integer.valueOf(users.getAdminUserId()), Integer.valueOf(users.getUser_id()), Integer.valueOf(action) });
                } 
                sql = " select u.first_name,u.email,u.android_device_token,u.ios_device_token,uas.sound_alert from users u  inner join user_app_settings uas on u.user_id=uas.user_id and u.user_id = ? ";
                Map<String, Object> verificationData = this.jdbcTemplate.queryForMap(sql, new Object[] { Integer.valueOf(users.getUser_id()) });
                if (!verificationData.isEmpty()) {
                  if (users.getUserTierDocsStatus().equals("1")) {
                    title = "Documents Approved";
                    message = "Your documents have been approved.";
                  } else {
                    title = "Documents Declined";
                    message = "Your documents have been declined.Kindly check and resubmit.";
                  } 
                  if (verificationData.get("android_device_token") != null)
                    this.notificationService.send_notification_android(message, title, verificationData
                        .get("android_device_token").toString(), ((BigDecimal)verificationData
                        .get("sound_alert")).intValue(), users
                        .getUser_id() + "", "", 4); 
                  if (verificationData.get("ios_device_token") != null)
                    this.notificationService.send_notification_ios(message, title, verificationData
                        .get("ios_device_token").toString(), ((BigDecimal)verificationData
                        .get("sound_alert")).intValue(), users
                        .getUser_id() + "", "", 4); 
                  String customer_email = verificationData.get("email").toString();
                  HashMap<String, String> nameVal = new HashMap<>();
                  nameVal.put("welcome_message", "Dear");
                  nameVal.put("user_name", verificationData.get("first_name").toString());
                  nameVal.put("message1", message);
                  String mail_content = this.mailContentBuilderService.build("basic", nameVal);
                  String subject = "Your Multi tier documents Verification Status.";
                  this.mailClientService.mailthreding(this.env.getProperty("spring.mail.username"), customer_email, subject, mail_content);
                } 
                this.error = new ErrorResponse();
              } else {
                this.error = new ErrorResponse(1, "Tier 2 documents are not approved.");
              } 
            } else {
              this.error = new ErrorResponse(1, message);
            } 
          } 
        } else {
          this.error = new ErrorResponse(1, "Invalid Admin User.");
        } 
      } else {
        this.error = new ErrorResponse(1, message);
      } 
    } catch (Exception e) {
      e.printStackTrace();
      log.error(e.getMessage());
      this.error = new ErrorResponse(1, e.getMessage());
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /ChangeUserTier\nClass: AdminDao\nMethod: changeUserTier" + 
            System.lineSeparator() + errors.toString());
      } 
    } 
    this.adminUsersResponse.setError(this.error);
    return this.adminUsersResponse;
  }
  
  public AdminUsersResponse changeUserProfile(Users users, HttpServletRequest request) {
    this.adminUsersResponse = new AdminUsersResponse();
    String message = "";
    try {
      message = this.emailService.check2Fa(users.getAdminUserId(), users.getOtp());
      if (message.equalsIgnoreCase("Success")) {
        String sql = "select is_blocked from admin_user_login where user_id = ? ";
        BigDecimal status = (BigDecimal)DataAccessUtils.singleResult(this.jdbcTemplate.query(sql, new Object[] { Integer.valueOf(users.getAdminUserId()) }, (RowMapper)new SingleColumnRowMapper()));
        if (status != null) {
          if (status.intValue() == 1) {
            this.error = new ErrorResponse(1, "You are not authorized.");
          } else {
            Map<String, Object> result = this.utility.accessCheck(users.getAdminUserId(), 27, request
                .getRemoteAddr());
            int returnId = ((BigDecimal)result.get("R_RETURN_ID")).intValue();
            message = (String)result.get("R_MESSAGE");
            if (returnId == 1) {
              String userCheck = "select first_name,email,phone,user_docs_status,bank_details_status from users where user_id = ? and status = 1";
              Map<String, Object> verificationData = this.jdbcTemplate.queryForMap(userCheck, new Object[] { Integer.valueOf(users.getUser_id()) });
              if (!verificationData.isEmpty()) {
                String firstName = verificationData.get("first_name").toString();
                String email = verificationData.get("email").toString();
                String updateSql = "";
                if (StringUtils.hasText(users.getEmail())) {
                  sql = "select user_id from users where email = ?";
                  updateSql = "update users set email = ?  where user_id = ?";
                  BigDecimal user = (BigDecimal)DataAccessUtils.singleResult(this.jdbcTemplate.query(sql, new Object[] { users.getEmail() }, (RowMapper)new SingleColumnRowMapper()));
                  if (user != null) {
                    this.error = new ErrorResponse(1, "Email already exist.");
                  } else {
                    this.jdbcTemplate.update(updateSql, new Object[] { users.getEmail(), Integer.valueOf(users.getUser_id()) });
                    message = "Your Email is updated successfully. New Email: " + users.getEmail();
                    sendProfileUpdationMail(firstName, email, message);
                    this.error = new ErrorResponse();
                  } 
                } else if (StringUtils.hasText(users.getPhone())) {
                  sql = "select user_id from users where phone = ?";
                  updateSql = "update users set phone = ?  where user_id = ?";
                  BigDecimal user = (BigDecimal)DataAccessUtils.singleResult(this.jdbcTemplate.query(sql, new Object[] { users.getPhone() }, (RowMapper)new SingleColumnRowMapper()));
                  if (user != null) {
                    this.error = new ErrorResponse(1, "Phone No. already exist.");
                  } else {
                    this.jdbcTemplate.update(updateSql, new Object[] { users.getPhone(), Integer.valueOf(users.getUser_id()) });
                    message = "Your Phone No. is updated successfully. New Phone No: " + users.getPhone();
                    sendProfileUpdationMail(firstName, email, message);
                    this.error = new ErrorResponse(0, message);
                  } 
                } else if (StringUtils.hasText(users.getFirstName())) {
                  if ((BigDecimal)verificationData.get("user_docs_status") != null && ((BigDecimal)verificationData
                    .get("user_docs_status")).intValue() == 1 && (BigDecimal)verificationData
                    .get("bank_details_status") != null && ((BigDecimal)verificationData
                    .get("bank_details_status"))
                    .intValue() == 2) {
                    this.error = new ErrorResponse(1, "Name change is restricted for confirmed user.");
                  } else {
                    updateSql = "update users set first_name = ? where user_id = ?";
                    this.jdbcTemplate.update(updateSql, new Object[] { users.getFirstName(), Integer.valueOf(users.getUser_id()) });
                    message = "Your Name is updated successfully.";
                    sendProfileUpdationMail(firstName, email, message);
                    this.error = new ErrorResponse(0, message);
                  } 
                } else if (StringUtils.hasText(users.getAddress()) && 
                  StringUtils.hasText(users.getCountry()) && 
                  StringUtils.hasText(users.getState()) && StringUtils.hasText(users.getCity()) && 
                  StringUtils.hasText(users.getZip())) {
                  if ((BigDecimal)verificationData.get("user_docs_status") != null && ((BigDecimal)verificationData
                    .get("user_docs_status")).intValue() == 1 && (BigDecimal)verificationData
                    .get("bank_details_status") != null && ((BigDecimal)verificationData
                    .get("bank_details_status"))
                    .intValue() == 2) {
                    this.error = new ErrorResponse(1, "Address change is restricted for confirmed user.");
                  } else {
                    updateSql = "update users set address = ?,country = ?,state = ?,city = ?,zip = ? where user_id = ?";
                    this.jdbcTemplate.update(updateSql, new Object[] { users.getAddress(), users.getCountry(), users
                          .getState(), users.getCity(), users.getZip(), Integer.valueOf(users.getUser_id()) });
                    message = "Address updated successfully.";
                    sendProfileUpdationMail(firstName, email, message);
                    this.error = new ErrorResponse(0, message);
                  } 
                } else {
                  this.error = new ErrorResponse(1);
                } 
              } else {
                this.error = new ErrorResponse(1, "Invalid User or User is blocked.");
              } 
            } else {
              this.error = new ErrorResponse(1, message);
            } 
          } 
        } else {
          this.error = new ErrorResponse(1, "Invalid Admin User.");
        } 
      } else {
        this.error = new ErrorResponse(1, message);
      } 
    } catch (Exception e) {
      e.printStackTrace();
      log.error(e.getMessage());
      this.error = new ErrorResponse(1, e.getMessage());
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /ChangeUserProfile\nClass: AdminDao\nMethod: changeUserProfile" + 
            System.lineSeparator() + errors.toString());
      } 
    } 
    this.adminUsersResponse.setError(this.error);
    return this.adminUsersResponse;
  }
  
  private void sendProfileUpdationMail(String name, String email, String message) {
    HashMap<String, String> nameVal = new HashMap<>();
    nameVal.put("welcome_message", "Dear");
    nameVal.put("user_name", name);
    nameVal.put("message1", message);
    String mailContent = this.mailContentBuilderService.build("basic", nameVal);
    String subject = "User profile updation";
    this.mailClientService.mailthreding(this.env.getProperty("spring.mail.username"), email, subject, mailContent);
  }
  
  public ExchangeHealthCheckResponse getMemoryAndSpaceDetails() {
    ExchangeHealthCheckResponse healthcheckResponse = null;
    SystemResponse systemResponse = new SystemResponse();
    try {
      File root = new File("/");
      System.out.println(String.format("Total space: %.2f GB", new Object[] { Double.valueOf(root.getTotalSpace() / 1.073741824E9D) }));
      systemResponse.setTotalSpace(String.format("%.2f", new Object[] { Double.valueOf(root.getTotalSpace() / 1.073741824E9D) }));
      System.out.println(String.format("Free space: %.2f GB", new Object[] { Double.valueOf(root.getFreeSpace() / 1.073741824E9D) }));
      systemResponse.setFreeSpace(String.format("%.2f", new Object[] { Double.valueOf(root.getFreeSpace() / 1.073741824E9D) }));
      System.out.println(String.format("Usable space: %.2f GB", new Object[] { Double.valueOf(root.getUsableSpace() / 1.073741824E9D) }));
      systemResponse.setUsableSpace(String.format("%.2f", new Object[] { Double.valueOf(root.getUsableSpace() / 1.073741824E9D) }));
      OperatingSystemMXBean memoryMXBean = (OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
      System.out.println(String.format("Initial memory: %.2f GB", new Object[] { Double.valueOf(memoryMXBean.getTotalPhysicalMemorySize() / 1.073741824E9D) }));
      systemResponse.setTotalPhysicalMemory(
          String.format("%.2f", new Object[] { Double.valueOf(memoryMXBean.getTotalPhysicalMemorySize() / 1.073741824E9D) }));
      System.out.println(String.format("Used free memory: %.2f GB", new Object[] { Double.valueOf(memoryMXBean.getFreePhysicalMemorySize() / 1.073741824E9D) }));
      systemResponse.setFreePhysicalMemory(
          String.format("%.2f", new Object[] { Double.valueOf(memoryMXBean.getFreePhysicalMemorySize() / 1.073741824E9D) }));
      System.out.println(String.format("Committed memory: %.2f GB", new Object[] { Double.valueOf(memoryMXBean.getCommittedVirtualMemorySize() / 1.073741824E9D) }));
      systemResponse.setCommittedVirtualMemory(
          String.format("%.2f", new Object[] { Double.valueOf(memoryMXBean.getCommittedVirtualMemorySize() / 1.073741824E9D) }));
      healthcheckResponse = new ExchangeHealthCheckResponse(new ErrorResponse());
      healthcheckResponse.setSystemResponse(systemResponse);
    } catch (Exception ex) {
      ex.printStackTrace();
      healthcheckResponse = new ExchangeHealthCheckResponse(new ErrorResponse(11));
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        ex.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /getMemoryAndSpaceDetails\nClass: AdminDao\nMethod: getMemoryAndSpaceDetails" + 
            
            System.lineSeparator() + errors.toString());
      } 
    } 
    return healthcheckResponse;
  }
  
  public ExchangeHealthCheckResponse getTomcatDetails() {
    ErrorResponse errorResponse = new ErrorResponse();
    List<TomcatDetails> tomcatLists = new ArrayList<>();
    TomcatDetails tomcatDetails = null;
    RestTemplate restTemplate = new RestTemplate();
    String url = "";
    try {
      url = this.env.getProperty("project.tomcat.url") + ":6388";
      tomcatDetails = new TomcatDetails();
      tomcatDetails.setTomcateName("Admin Service");
      tomcatDetails.setTomcatport("6388");
      tomcatDetails.setTomcatUrl(url);
      ResponseEntity<String> res = restTemplate.getForEntity(url, String.class, new Object[0]);
      if (res.getStatusCode() == HttpStatus.OK) {
        tomcatDetails.setStatus(true);
        log.info("Admin Service Tomcat running");
      } else {
        tomcatDetails.setStatus(false);
        log.info("Admin Service Tomcat stop");
      } 
      tomcatLists.add(tomcatDetails);
    } catch (Exception ex) {
      ex.printStackTrace();
      errorResponse = new ErrorResponse(1, "Admin Service Tomcat Exception.");
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        ex.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /getTomcatDetails\nClass: AdminDao\nMethod: getTomcatDetails" + 
            System.lineSeparator() + errors.toString());
      } 
    } 
    try {
      url = this.env.getProperty("project.service.tomcat.url") + ":7319";
      tomcatDetails = new TomcatDetails();
      tomcatDetails.setTomcateName("Web Service");
      tomcatDetails.setTomcatport("7319");
      tomcatDetails.setTomcatUrl(url);
      ResponseEntity<String> res = restTemplate.getForEntity(url, String.class, new Object[0]);
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
      errorResponse = new ErrorResponse(1, "Web Service Tomcat Exception.");
    } 
    try {
      url = this.env.getProperty("project.trade.model.tomcat.url") + ":8971";
      tomcatDetails = new TomcatDetails();
      tomcatDetails.setTomcateName("Transaction Service Tomcat");
      tomcatDetails.setTomcatport("8971");
      tomcatDetails.setTomcatUrl(url);
      ResponseEntity<String> res = restTemplate.getForEntity(url, String.class, new Object[0]);
      if (res.getStatusCode() == HttpStatus.OK) {
        tomcatDetails.setStatus(true);
        log.info("Transaction Service Tomcat running");
      } else {
        tomcatDetails.setStatus(false);
        log.info("Transaction Service Tomcat running");
      } 
      tomcatLists.add(tomcatDetails);
    } catch (Exception ex) {
      ex.printStackTrace();
      errorResponse = new ErrorResponse(1, "Transaction Service Tomcat Exception.");
    } 
    try {
      url = this.env.getProperty("project.trade.model.tomcat.url") + ":13080";
      tomcatDetails = new TomcatDetails();
      tomcatDetails.setTomcateName("Trade Model Tomcat");
      tomcatDetails.setTomcatport("13080");
      tomcatDetails.setTomcatUrl(url);
      ResponseEntity<String> res = restTemplate.getForEntity(url, String.class, new Object[0]);
      if (res.getStatusCode() == HttpStatus.OK) {
        tomcatDetails.setStatus(true);
        log.info("Trade Model Tomcat running");
      } else {
        tomcatDetails.setStatus(false);
        log.info("Trade Model Tomcat running");
      } 
      tomcatLists.add(tomcatDetails);
    } catch (Exception ex) {
      ex.printStackTrace();
      errorResponse = new ErrorResponse(1, "Trade Model Tomcat Exception.");
    } 
    try {
      url = this.env.getProperty("project.matching.engine.tomcat.url") + ":7844";
      tomcatDetails = new TomcatDetails();
      tomcatDetails.setTomcateName("Spot Matching Engine");
      tomcatDetails.setTomcatport("7844");
      tomcatDetails.setTomcatUrl(url);
      ResponseEntity<String> res = restTemplate.getForEntity(url, String.class, new Object[0]);
      if (res.getStatusCode() == HttpStatus.OK) {
        tomcatDetails.setStatus(true);
        log.info("Spot Matching Engine Tomcat running");
      } else {
        tomcatDetails.setStatus(false);
        log.info("Spot Exchange Tomcat stop");
      } 
      tomcatLists.add(tomcatDetails);
    } catch (Exception ex) {
      ex.printStackTrace();
      errorResponse = new ErrorResponse(1, "Spot Matching Engine Tomcat Exception.");
    } 
    try {
      url = this.env.getProperty("project.futures.matching.engine.tomcat.url") + ":5783";
      tomcatDetails = new TomcatDetails();
      tomcatDetails.setTomcateName("Futures Matching Engine");
      tomcatDetails.setTomcatport("5783");
      tomcatDetails.setTomcatUrl(url);
      ResponseEntity<String> res = restTemplate.getForEntity(url, String.class, new Object[0]);
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
      url = this.env.getProperty("project.options.matching.engine.tomcat.url") + ":6981";
      tomcatDetails = new TomcatDetails();
      tomcatDetails.setTomcateName("Options Matching Engine");
      tomcatDetails.setTomcatport("6981");
      tomcatDetails.setTomcatUrl(url);
      ResponseEntity<String> res = restTemplate.getForEntity(url, String.class, new Object[0]);
      if (res.getStatusCode() == HttpStatus.OK) {
        tomcatDetails.setStatus(true);
        log.info("Options Matching Engine Tomcat running");
      } else {
        tomcatDetails.setStatus(false);
        log.info("Options Matching Engine Tomcat stop");
      } 
      tomcatLists.add(tomcatDetails);
    } catch (Exception ex) {
      ex.printStackTrace();
      errorResponse = new ErrorResponse(1, "Options Matching Engine Tomcat Exception.");
    } 
    try {
      url = this.env.getProperty("project.bot.tomcat.url") + ":8974";
      tomcatDetails = new TomcatDetails();
      tomcatDetails.setTomcateName("Spot Bot Offer");
      tomcatDetails.setTomcatport("8974");
      tomcatDetails.setTomcatUrl(url);
      ResponseEntity<String> res = restTemplate.getForEntity(url, String.class, new Object[0]);
      if (res.getStatusCode() == HttpStatus.OK) {
        tomcatDetails.setStatus(true);
        log.info("Spot Bot Offer Tomcat Running");
      } else {
        tomcatDetails.setStatus(false);
        log.info("Spot Bot Offer Tomcat Stop");
      } 
      tomcatLists.add(tomcatDetails);
    } catch (Exception ex) {
      ex.printStackTrace();
      errorResponse = new ErrorResponse(1, "Spot Bot Offer Tomcat Exception.");
    } 
    try {
      url = this.env.getProperty("project.bot.tomcat.url") + ":9080";
      tomcatDetails = new TomcatDetails();
      tomcatDetails.setTomcateName("Spot Bot Trade");
      tomcatDetails.setTomcatport("9080");
      tomcatDetails.setTomcatUrl(url);
      ResponseEntity<String> res = restTemplate.getForEntity(url, String.class, new Object[0]);
      if (res.getStatusCode() == HttpStatus.OK) {
        tomcatDetails.setStatus(true);
        log.info("Spot Bot Trade Tomcat Running");
      } else {
        tomcatDetails.setStatus(false);
        log.info("Spot Bot Trade Tomcat Stop");
      } 
      tomcatLists.add(tomcatDetails);
    } catch (Exception ex) {
      ex.printStackTrace();
      errorResponse = new ErrorResponse(1, "Spot Bot Trade Tomcat Exception.");
    } 
    try {
      url = this.env.getProperty("project.futures.bot.tomcat.url") + ":8540";
      tomcatDetails = new TomcatDetails();
      tomcatDetails.setTomcateName("Futures Bot Offer");
      tomcatDetails.setTomcatport("8540");
      tomcatDetails.setTomcatUrl(url);
      ResponseEntity<String> res = restTemplate.getForEntity(url, String.class, new Object[0]);
      if (res.getStatusCode() == HttpStatus.OK) {
        tomcatDetails.setStatus(true);
        log.info("Futures Bot Offer Tomcat Running");
      } else {
        tomcatDetails.setStatus(false);
        log.info("Futures Bot Offer Tomcat Stop");
      } 
      tomcatLists.add(tomcatDetails);
    } catch (Exception ex) {
      ex.printStackTrace();
      errorResponse = new ErrorResponse(1, "Futures Bot Offer Tomcat Exception.");
    } 
    try {
      url = this.env.getProperty("project.futures.bot.tomcat.url") + ":9080";
      tomcatDetails = new TomcatDetails();
      tomcatDetails.setTomcateName("Futures Bot Offers Delete");
      tomcatDetails.setTomcatport("9080");
      tomcatDetails.setTomcatUrl(url);
      ResponseEntity<String> res = restTemplate.getForEntity(url, String.class, new Object[0]);
      if (res.getStatusCode() == HttpStatus.OK) {
        tomcatDetails.setStatus(true);
        log.info("Futures Bot Offers Delete Tomcat Running");
      } else {
        tomcatDetails.setStatus(false);
        log.info("Futures Bot Offers Delete Tomcat Stop");
      } 
      tomcatLists.add(tomcatDetails);
    } catch (Exception ex) {
      ex.printStackTrace();
      errorResponse = new ErrorResponse(1, "Futures Bot Offers Delete Tomcat Exception.");
    } 
    try {
      url = this.env.getProperty("project.options.bot.tomcat.url") + ":7650";
      tomcatDetails = new TomcatDetails();
      tomcatDetails.setTomcateName("Options Bot Offer");
      tomcatDetails.setTomcatport("7650");
      tomcatDetails.setTomcatUrl(url);
      ResponseEntity<String> res = restTemplate.getForEntity(url, String.class, new Object[0]);
      if (res.getStatusCode() == HttpStatus.OK) {
        tomcatDetails.setStatus(true);
        log.info("Options Bot Offer Tomcat Running");
      } else {
        tomcatDetails.setStatus(false);
        log.info("Options Bot Offer Tomcat Stop");
      } 
      tomcatLists.add(tomcatDetails);
    } catch (Exception ex) {
      ex.printStackTrace();
      errorResponse = new ErrorResponse(1, "Options Bot Offer Tomcat Exception.");
    } 
    try {
      url = this.env.getProperty("project.options.bot.tomcat.url") + ":9080";
      tomcatDetails = new TomcatDetails();
      tomcatDetails.setTomcateName("Options Bot Offers Delete");
      tomcatDetails.setTomcatport("9080");
      tomcatDetails.setTomcatUrl(url);
      ResponseEntity<String> res = restTemplate.getForEntity(url, String.class, new Object[0]);
      if (res.getStatusCode() == HttpStatus.OK) {
        tomcatDetails.setStatus(true);
        log.info("Options Bot Offers Delete Tomcat Running");
      } else {
        tomcatDetails.setStatus(false);
        log.info("Options Bot Offers Delete Tomcat Stop");
      } 
      tomcatLists.add(tomcatDetails);
    } catch (Exception ex) {
      ex.printStackTrace();
      errorResponse = new ErrorResponse(1, "Options Bot Offers Delete Tomcat Exception.");
    } 
    try {
      url = this.env.getProperty("project.options.bot.tomcat.url") + ":7080";
      tomcatDetails = new TomcatDetails();
      tomcatDetails.setTomcateName("Options Contract Exp.");
      tomcatDetails.setTomcatport("7080");
      tomcatDetails.setTomcatUrl(url);
      ResponseEntity<String> res = restTemplate.getForEntity(url, String.class, new Object[0]);
      if (res.getStatusCode() == HttpStatus.OK) {
        tomcatDetails.setStatus(true);
        log.info("Options Contract Exp. Tomcat Running");
      } else {
        tomcatDetails.setStatus(false);
        log.info("Options Contract Exp. Tomcat Stop");
      } 
      tomcatLists.add(tomcatDetails);
    } catch (Exception ex) {
      ex.printStackTrace();
      errorResponse = new ErrorResponse(1, "Options Contract Exp. Tomcat Exception.");
    } 
    try {
      url = this.env.getProperty("project.stream.tomcat.url") + ":8944";
      tomcatDetails = new TomcatDetails();
      tomcatDetails.setTomcateName("Spot Stream");
      tomcatDetails.setTomcatport("8944");
      tomcatDetails.setTomcatUrl(url);
      ResponseEntity<String> res = restTemplate.getForEntity(url, String.class, new Object[0]);
      if (res.getStatusCode() == HttpStatus.OK) {
        tomcatDetails.setStatus(true);
        log.info("Spot Stream Tomcat running");
      } else {
        tomcatDetails.setStatus(false);
        log.info("Spot Stream Tomcat stop");
      } 
      tomcatLists.add(tomcatDetails);
    } catch (Exception ex) {
      ex.printStackTrace();
      errorResponse = new ErrorResponse(1, "Spot Stream Tomcat Exception.");
    } 
    try {
      url = this.env.getProperty("project.futures.stream.tomcat.url") + ":6729";
      tomcatDetails = new TomcatDetails();
      tomcatDetails.setTomcateName("Futures Stream");
      tomcatDetails.setTomcatport("6729");
      tomcatDetails.setTomcatUrl(url);
      ResponseEntity<String> res = restTemplate.getForEntity(url, String.class, new Object[0]);
      if (res.getStatusCode() == HttpStatus.OK) {
        tomcatDetails.setStatus(true);
        log.info("Futures Stream Tomcat running");
      } else {
        tomcatDetails.setStatus(false);
        log.info("Futures Stream Tomcat stop");
      } 
      tomcatLists.add(tomcatDetails);
    } catch (Exception ex) {
      ex.printStackTrace();
      errorResponse = new ErrorResponse(1, "Futures Stream Tomcat Exception.");
    } 
    try {
      url = this.env.getProperty("project.options.stream.tomcat.url") + ":8965";
      tomcatDetails = new TomcatDetails();
      tomcatDetails.setTomcateName("Options Stream");
      tomcatDetails.setTomcatport("8965");
      tomcatDetails.setTomcatUrl(url);
      ResponseEntity<String> res = restTemplate.getForEntity(url, String.class, new Object[0]);
      if (res.getStatusCode() == HttpStatus.OK) {
        tomcatDetails.setStatus(true);
        log.info("Options Stream Tomcat running");
      } else {
        tomcatDetails.setStatus(false);
        log.info("Options Stream Tomcat stop");
      } 
      tomcatLists.add(tomcatDetails);
    } catch (Exception ex) {
      ex.printStackTrace();
      errorResponse = new ErrorResponse(1, "Options Stream Tomcat Exception.");
    } 
    try {
      url = this.env.getProperty("project.liquidity.tomcat.url") + ":8675";
      tomcatDetails = new TomcatDetails();
      tomcatDetails.setTomcateName("Liquidity");
      tomcatDetails.setTomcatport("8675");
      tomcatDetails.setTomcatUrl(url);
      ResponseEntity<String> res = restTemplate.getForEntity(url, String.class, new Object[0]);
      if (res.getStatusCode() == HttpStatus.OK) {
        tomcatDetails.setStatus(true);
        log.info("Liquidity Tomcat running");
      } else {
        tomcatDetails.setStatus(false);
        log.info("Liquidity Tomcat running");
      } 
      tomcatLists.add(tomcatDetails);
    } catch (Exception ex) {
      ex.printStackTrace();
      errorResponse = new ErrorResponse(1, "Liquidity Tomcat Exception.");
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
      conn = this.dataSource.getConnection();
      String userCheck = " select upper(resource_name) resource_name, current_utilization, max_utilization,LIMIT_VALUE  from v$resource_limit where resource_name in ('processes','sessions')";
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
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /getDbProcessDetails\nClass: AdminDao\nMethod: getDbProcessDetails" + 
            
            System.lineSeparator() + errors.toString());
      } 
    } finally {
      if (conn != null)
        try {
          conn.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }  
    } 
    return healthcheckResponse;
  }
  
  public ExchangeHealthCheckResponse getOfferDetails() {
    ExchangeHealthCheckResponse healthcheckResponse = null;
    List<OfferDetails> offersList = new ArrayList<>();
    try {
      SimpleJdbcCall simpleJdbcCall = (new SimpleJdbcCall(this.jdbcTemplate)).withProcedureName("GET_DATEWISE_OFFER_ACTIVITY").returningResultSet("OFFER_ACTIVITY_DETAILS", 
          (RowMapper)BeanPropertyRowMapper.newInstance(OfferDetails.class));
      Map<String, Object> result = simpleJdbcCall.execute(new Object[0]);
      offersList = (List<OfferDetails>)result.get("OFFER_ACTIVITY_DETAILS");
      long totalOffers = ((BigDecimal)result.get("TOTAL_ACTIVE_OFFER")).longValue();
      if (!offersList.isEmpty()) {
        healthcheckResponse = new ExchangeHealthCheckResponse(new ErrorResponse());
        healthcheckResponse.setOfferListResponse(offersList);
        healthcheckResponse.setTotalOffers(totalOffers);
      } else {
        healthcheckResponse = new ExchangeHealthCheckResponse(new ErrorResponse(0, "no data"));
      } 
    } catch (Exception e) {
      e.printStackTrace();
      healthcheckResponse = new ExchangeHealthCheckResponse(new ErrorResponse(11));
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /getOfferDetails\nClass: AdminDao\nMethod: getOfferDetails" + 
            System.lineSeparator() + errors.toString());
      } 
    } 
    return healthcheckResponse;
  }
  
  public ExchangeHealthCheckResponse getPairWiseOfferDetails() {
    ExchangeHealthCheckResponse healthcheckResponse = null;
    List<AssetWiseOfferDetails> assetWiseoffersList = new ArrayList<>();
    List<AssetWiseOfferDetails> assetWiseofferDetailsList = new ArrayList<>();
    RestTemplate restTemplate = new RestTemplate();
    try {
      SimpleJdbcCall simpleJdbcCall = (new SimpleJdbcCall(this.jdbcTemplate)).withProcedureName("GET_ASSETWISE_ACTIVE_OFFER").returningResultSet("ASSETWISE_ACTIVE_OFFER_DETAILS", 
          (RowMapper)BeanPropertyRowMapper.newInstance(AssetWiseOfferDetails.class));
      Map<String, Object> result = simpleJdbcCall.execute(new Object[0]);
      assetWiseoffersList = (List<AssetWiseOfferDetails>)result.get("ASSETWISE_ACTIVE_OFFER_DETAILS");
      for (AssetWiseOfferDetails assetWiseoffersDetails : assetWiseoffersList) {
        String[] arr = assetWiseoffersDetails.getAssetPair().split("-");
        String currency = arr[0];
        String baseCurrency = arr[1];
        String bestOfferUrl = this.env.getProperty("project.new.orderbook.api.url");
        bestOfferUrl = bestOfferUrl + "symbol=" + currency + baseCurrency + "&limit=1";
        String bestOffer = (String)restTemplate.getForObject(bestOfferUrl, String.class, new Object[0]);
        if (StringUtils.hasText(bestOffer) && !"Invalid input".equals(bestOffer)) {
          JSONObject partsData = new JSONObject(bestOffer);
          if (!partsData.get("ask").equals((Object)null) && partsData.getJSONArray("ask").length() > 0) {
            JSONArray jsonArray = partsData.getJSONArray("ask");
            JSONObject asksData = jsonArray.getJSONObject(0);
            String askPrice = asksData.getString("price");
            assetWiseoffersDetails.setBestSellOffer(askPrice);
          } 
          if (!partsData.get("bid").equals((Object)null) && partsData.getJSONArray("bid").length() > 0) {
            JSONArray jsonArray = partsData.getJSONArray("bid");
            JSONObject bidsData = jsonArray.getJSONObject(0);
            String bidPrice = bidsData.getString("price");
            assetWiseoffersDetails.setBestBuyOffer(bidPrice);
          } 
          assetWiseofferDetailsList.add(assetWiseoffersDetails);
        } 
      } 
      if (!assetWiseofferDetailsList.isEmpty()) {
        healthcheckResponse = new ExchangeHealthCheckResponse(new ErrorResponse());
        healthcheckResponse.setAssetWiseOffersList(assetWiseofferDetailsList);
      } else {
        healthcheckResponse = new ExchangeHealthCheckResponse(new ErrorResponse(0, "no data"));
      } 
    } catch (Exception e) {
      e.printStackTrace();
      healthcheckResponse = new ExchangeHealthCheckResponse(new ErrorResponse(11));
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /getPairWiseOfferDetails\nClass: AdminDao\nMethod: getPairWiseOfferDetails" + 
            
            System.lineSeparator() + errors.toString());
      } 
    } 
    return healthcheckResponse;
  }
  
  public ExchangeHealthCheckResponse getfuturesOfferDetails() {
    ExchangeHealthCheckResponse healthcheckResponse = null;
    List<OfferDetails> offersList = new ArrayList<>();
    try {
      SimpleJdbcCall simpleJdbcCall = (new SimpleJdbcCall(this.jdbcTemplate)).withProcedureName("GET_FUTURES_OFFER_ACTIVITY").returningResultSet("FUTURES_OFFER_ACTIVITY_DETAILS", 
          (RowMapper)BeanPropertyRowMapper.newInstance(OfferDetails.class));
      Map<String, Object> result = simpleJdbcCall.execute(new Object[0]);
      offersList = (List<OfferDetails>)result.get("FUTURES_OFFER_ACTIVITY_DETAILS");
      long totalOffers = ((BigDecimal)result.get("TOTAL_ACTIVE_OFFER")).longValue();
      if (!offersList.isEmpty()) {
        healthcheckResponse = new ExchangeHealthCheckResponse(new ErrorResponse());
        healthcheckResponse.setOfferListResponse(offersList);
        healthcheckResponse.setTotalOffers(totalOffers);
      } else {
        healthcheckResponse = new ExchangeHealthCheckResponse(new ErrorResponse(0, "no data"));
      } 
    } catch (Exception e) {
      e.printStackTrace();
      healthcheckResponse = new ExchangeHealthCheckResponse(new ErrorResponse(11));
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /getfuturesOfferDetails\nClass: AdminDao\nMethod: getfuturesOfferDetails" + 
            
            System.lineSeparator() + errors.toString());
      } 
    } 
    return healthcheckResponse;
  }
  
  public ExchangeHealthCheckResponse futuresContractOfferDetails() {
    ExchangeHealthCheckResponse healthcheckResponse = null;
    List<AssetWiseOfferDetails> assetWiseoffersList = new ArrayList<>();
    List<AssetWiseOfferDetails> assetWiseofferDetailsList = new ArrayList<>();
    RestTemplate restTemplate = new RestTemplate();
    try {
      SimpleJdbcCall simpleJdbcCall = (new SimpleJdbcCall(this.jdbcTemplate)).withProcedureName("FUTURES_ASSETWISE_ACTIVE_OFFER").returningResultSet("ASSETPAIR_ACTIVE_OFFER_DETAILS", 
          (RowMapper)BeanPropertyRowMapper.newInstance(AssetWiseOfferDetails.class));
      Map<String, Object> result = simpleJdbcCall.execute(new Object[0]);
      assetWiseoffersList = (List<AssetWiseOfferDetails>)result.get("ASSETPAIR_ACTIVE_OFFER_DETAILS");
      for (AssetWiseOfferDetails assetWiseoffersDetails : assetWiseoffersList) {
        String bestOfferUrl = this.env.getProperty("project.futures.orderbook.api.url");
        bestOfferUrl = bestOfferUrl + "symbol=" + assetWiseoffersDetails.getAssetPair() + "&limit=1";
        String bestOffer = (String)restTemplate.getForObject(bestOfferUrl, String.class, new Object[0]);
        if (bestOffer != null) {
          JSONObject partsData = new JSONObject(bestOffer);
          if (!partsData.get("ask").equals((Object)null) && partsData.getJSONArray("ask").length() > 0) {
            JSONArray jsonArray = partsData.getJSONArray("ask");
            JSONObject asksData = jsonArray.getJSONObject(0);
            String askPrice = asksData.getString("price");
            assetWiseoffersDetails.setBestSellOffer(askPrice);
          } 
          if (!partsData.get("bid").equals((Object)null) && partsData.getJSONArray("bid").length() > 0) {
            JSONArray jsonArray = partsData.getJSONArray("bid");
            JSONObject bidsData = jsonArray.getJSONObject(0);
            String bidPrice = bidsData.getString("price");
            assetWiseoffersDetails.setBestBuyOffer(bidPrice);
          } 
          assetWiseofferDetailsList.add(assetWiseoffersDetails);
        } 
      } 
      if (!assetWiseofferDetailsList.isEmpty()) {
        healthcheckResponse = new ExchangeHealthCheckResponse(new ErrorResponse());
        healthcheckResponse.setAssetWiseOffersList(assetWiseofferDetailsList);
      } else {
        healthcheckResponse = new ExchangeHealthCheckResponse(new ErrorResponse(0, "no data"));
      } 
    } catch (Exception e) {
      e.printStackTrace();
      healthcheckResponse = new ExchangeHealthCheckResponse(new ErrorResponse(11));
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /futuresContractOfferDetails\nClass: AdminDao\nMethod: futuresContractOfferDetails" + 
            
            System.lineSeparator() + errors.toString());
      } 
    } 
    return healthcheckResponse;
  }
  
  public ExchangeHealthCheckResponse getOptionsOfferDetails() {
    ExchangeHealthCheckResponse healthcheckResponse = null;
    List<OfferDetails> offersList = new ArrayList<>();
    try {
      SimpleJdbcCall simpleJdbcCall = (new SimpleJdbcCall(this.jdbcTemplate)).withProcedureName("GET_OPTIONS_OFFER_ACTIVITY").returningResultSet("OPTIONS_OFFER_ACTIVITY_DETAILS", 
          (RowMapper)BeanPropertyRowMapper.newInstance(OfferDetails.class));
      Map<String, Object> result = simpleJdbcCall.execute(new Object[0]);
      offersList = (List<OfferDetails>)result.get("OPTIONS_OFFER_ACTIVITY_DETAILS");
      long totalOffers = ((BigDecimal)result.get("TOTAL_ACTIVE_OFFER")).longValue();
      if (!offersList.isEmpty()) {
        healthcheckResponse = new ExchangeHealthCheckResponse(new ErrorResponse());
        healthcheckResponse.setOfferListResponse(offersList);
        healthcheckResponse.setTotalOffers(totalOffers);
      } else {
        healthcheckResponse = new ExchangeHealthCheckResponse(new ErrorResponse(0, "no data"));
      } 
    } catch (Exception e) {
      e.printStackTrace();
      healthcheckResponse = new ExchangeHealthCheckResponse(new ErrorResponse(11));
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /getOptionsOfferDetails\nClass: AdminDao\nMethod: getOptionsOfferDetails" + 
            
            System.lineSeparator() + errors.toString());
      } 
    } 
    return healthcheckResponse;
  }
  
  public ExchangeHealthCheckResponse optionsContractOfferDetails() {
    ExchangeHealthCheckResponse healthcheckResponse = null;
    List<AssetWiseOfferDetails> assetWiseoffersList = new ArrayList<>();
    List<AssetWiseOfferDetails> assetWiseofferDetailsList = new ArrayList<>();
    RestTemplate restTemplate = new RestTemplate();
    try {
      SimpleJdbcCall simpleJdbcCall = (new SimpleJdbcCall(this.jdbcTemplate)).withProcedureName("GET_OPS_ASSETPAIR_ACTIVE_OFFER").returningResultSet("ASSETPAIR_ACTIVE_OFFER_DETAILS", 
          (RowMapper)BeanPropertyRowMapper.newInstance(AssetWiseOfferDetails.class));
      Map<String, Object> result = simpleJdbcCall.execute(new Object[0]);
      assetWiseoffersList = (List<AssetWiseOfferDetails>)result.get("ASSETPAIR_ACTIVE_OFFER_DETAILS");
      for (AssetWiseOfferDetails assetWiseoffersDetails : assetWiseoffersList) {
        String bestOfferUrl = this.env.getProperty("project.options.orderbook.api.url");
        bestOfferUrl = bestOfferUrl + "symbol=" + assetWiseoffersDetails.getAssetPair() + "&limit=1";
        String bestOffer = (String)restTemplate.getForObject(bestOfferUrl, String.class, new Object[0]);
        if (bestOffer != null) {
          JSONObject partsData = new JSONObject(bestOffer);
          if (!partsData.get("ask").equals((Object)null) && partsData.getJSONArray("ask").length() > 0) {
            JSONArray jsonArray = partsData.getJSONArray("ask");
            JSONObject asksData = jsonArray.getJSONObject(0);
            String askPrice = asksData.getString("price");
            assetWiseoffersDetails.setBestSellOffer(askPrice);
          } 
          if (!partsData.get("bid").equals((Object)null) && partsData.getJSONArray("bid").length() > 0) {
            JSONArray jsonArray = partsData.getJSONArray("bid");
            JSONObject bidsData = jsonArray.getJSONObject(0);
            String bidPrice = bidsData.getString("price");
            assetWiseoffersDetails.setBestBuyOffer(bidPrice);
          } 
          assetWiseofferDetailsList.add(assetWiseoffersDetails);
        } 
      } 
      if (!assetWiseofferDetailsList.isEmpty()) {
        healthcheckResponse = new ExchangeHealthCheckResponse(new ErrorResponse());
        healthcheckResponse.setAssetWiseOffersList(assetWiseofferDetailsList);
      } else {
        healthcheckResponse = new ExchangeHealthCheckResponse(new ErrorResponse(0, "no data"));
      } 
    } catch (Exception e) {
      e.printStackTrace();
      healthcheckResponse = new ExchangeHealthCheckResponse(new ErrorResponse(11));
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /optionsContractOfferDetails\nClass: AdminDao\nMethod: optionsContractOfferDetails" + 
            
            System.lineSeparator() + errors.toString());
      } 
    } 
    return healthcheckResponse;
  }
  
  public ExchangeHealthCheckResponse getNetworkActiveOffers() {
    ExchangeHealthCheckResponse healthcheckResponse = null;
    String url = this.env.getProperty("project.paybito.new.networkoffers.api") + "offersCount";
    RestTemplate restTemplate = new RestTemplate();
    try {
      String totalOffers = (String)restTemplate.getForObject(url, String.class, new Object[0]);
      JSONObject jsonObject = new JSONObject(totalOffers);
      System.out.println("Total : " + jsonObject.getString("activeoffers"));
      healthcheckResponse = new ExchangeHealthCheckResponse(new ErrorResponse());
      healthcheckResponse.setHcNetActiveOffers(jsonObject.getString("activeoffers"));
    } catch (Exception e) {
      e.printStackTrace();
      healthcheckResponse = new ExchangeHealthCheckResponse(new ErrorResponse(11));
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /getNetworkActiveOffers\nClass: AdminDao\nMethod: getNetworkActiveOffers" + 
            
            System.lineSeparator() + errors.toString());
      } 
    } 
    return healthcheckResponse;
  }
  
  public ExchangeHealthCheckResponse futuresNetworkActiveOffers() {
    ExchangeHealthCheckResponse healthcheckResponse = null;
    String url = this.env.getProperty("project.paybito.futures.networkoffers.api") + "offersCount";
    RestTemplate restTemplate = new RestTemplate();
    try {
      String totalOffers = (String)restTemplate.getForObject(url, String.class, new Object[0]);
      JSONObject jsonObject = new JSONObject(totalOffers);
      System.out.println("Total : " + jsonObject.getString("activeoffers"));
      healthcheckResponse = new ExchangeHealthCheckResponse(new ErrorResponse());
      healthcheckResponse.setHcNetActiveOffers(jsonObject.getString("activeoffers"));
    } catch (Exception e) {
      e.printStackTrace();
      healthcheckResponse = new ExchangeHealthCheckResponse(new ErrorResponse(11));
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /futuresNetworkActiveOffers\nClass: AdminDao\nMethod: futuresNetworkActiveOffers" + 
            
            System.lineSeparator() + errors.toString());
      } 
    } 
    return healthcheckResponse;
  }
  
  public ExchangeHealthCheckResponse optionsNetworkActiveOffers() {
    ExchangeHealthCheckResponse healthcheckResponse = null;
    String url = this.env.getProperty("project.paybito.options.networkoffers.api") + "offersCount";
    RestTemplate restTemplate = new RestTemplate();
    try {
      String totalOffers = (String)restTemplate.getForObject(url, String.class, new Object[0]);
      JSONObject jsonObject = new JSONObject(totalOffers);
      System.out.println("Total : " + jsonObject.getString("activeoffers"));
      healthcheckResponse = new ExchangeHealthCheckResponse(new ErrorResponse());
      healthcheckResponse.setHcNetActiveOffers(jsonObject.getString("activeoffers"));
    } catch (Exception e) {
      e.printStackTrace();
      healthcheckResponse = new ExchangeHealthCheckResponse(new ErrorResponse(11));
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /optionsNetworkActiveOffers\nClass: AdminDao\nMethod: optionsNetworkActiveOffers" + 
            
            System.lineSeparator() + errors.toString());
      } 
    } 
    return healthcheckResponse;
  }
  
  public Map<String, Object> exchangeMaintenance(Maintenance maintenance) {
    Map<String, Object> response = new HashMap<>();
    this.error = new ErrorResponse();
    try {
      SimpleJdbcCall simpleJdbcCall = (new SimpleJdbcCall(this.jdbcTemplate)).withProcedureName("ADD_EXCHANGE_MAINTENANCE");
      MapSqlParameterSource input = new MapSqlParameterSource();
      input.addValue("P_STATUS", Integer.valueOf(maintenance.getStatus()));
      input.addValue("P_DESCRIPTION", maintenance.getDescription());
      Map<String, Object> result = simpleJdbcCall.execute((SqlParameterSource)input);
      int returnId = ((BigDecimal)result.get("RETURN_ID")).intValue();
      String message = (String)result.get("MESSAGE");
      if (returnId == 1) {
        this.error.setError_data(0);
        this.error.setError_msg("");
      } else {
        this.error.setError_data(1);
        this.error.setError_msg(message);
      } 
    } catch (Exception e) {
      e.printStackTrace();
      this.error.setError_data(1);
      this.error.setError_msg(e.getMessage());
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /exchangeMaintenance\nClass: AdminDao\nMethod: exchangeMaintenance" + 
            
            System.lineSeparator() + errors.toString());
      } 
    } 
    response.put("error", this.error);
    return response;
  }
  
  public Map<String, Object> getBlockDetails() {
    Map<String, Object> response = new HashMap<>();
    this.error = new ErrorResponse();
    String url = this.env.getProperty("project.paybito.admin.model.api") + "admin/getBlockDetails";
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders header = new HttpHeaders();
    header.setContentType(MediaType.APPLICATION_JSON);
    JSONObject json = new JSONObject();
    String query = "SELECT CURRENCY_ID,CURRENCY_CODE FROM CURRENCY_MASTER WHERE IS_ACTIVE=1 AND IS_NODE_BLOCK_STATUS=1 ";
    List<Map<String, Object>> latestblockList = new ArrayList<>();
    try {
      List<Map<String, Object>> currencyList = this.jdbcTemplate.queryForList(query);
      for (Map<String, Object> map : currencyList) {
        log.info("Block details for currency : " + String.valueOf(map.get("CURRENCY_CODE")));
        json.put("currencyid", String.valueOf(map.get("CURRENCY_ID")));
        HttpEntity<String> entity = new HttpEntity(json.toString(), (MultiValueMap)header);
        String res = (String)restTemplate.postForObject(url, entity, String.class, new Object[0]);
        if (res != null) {
          JSONObject partsData = new JSONObject(res);
          map.put("latestBlock", 
              partsData.get("latestblock").equals((Object)null) ? "" : partsData.getString("latestblock"));
          latestblockList.add(map);
          this.error.setError_data(0);
          this.error.setError_msg("");
        } 
      } 
    } catch (Exception e) {
      e.printStackTrace();
      this.error.setError_data(1);
      this.error.setError_msg(e.getMessage());
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /getBlockDetails\nClass: AdminDao\nMethod: getBlockDetails" + 
            System.lineSeparator() + errors.toString());
      } 
    } 
    response.put("latestblockList", latestblockList);
    response.put("error", this.error);
    return response;
  }
  
  public static String checkPort() {
    Socket s = null;
    try {
      s = new Socket("10.0.1.128", 10094);
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
  
  public Map<String, Object> getAllTierWiseSettingsData() {
    Map<String, Object> response = new HashMap<>();
    List<TierWiseSetting> tierWiseSettingList = new ArrayList<>();
    String query = "SELECT a.TIER_TYPE,a.CURRENCY_ID,GET_CURRENCY(a.CURRENCY_ID) CURRENCY,a.MIN_LIMIT,a.DAILY_SEND_LIMIT,a.MONTHLY_SEND_LIMIT,a.DAILY_BUY_LIMIT,a.DAILY_SELL_LIMIT,a.MAKER_CHARGE,a.DISCOUNT_MAKER_CHARGE,a.TAKER_CHARGE,a.DISCOUNT_TAKER_CHARGE,nvl(a.TXN_CHARGE,0) TXN_CHARGE,a.MIN_BALANCE FROM TIER_WISE_TRANSACTION_SETTINGS  a,CURRENCY_MASTER b WHERE a.CURRENCY_ID=b.CURRENCY_ID and b.IS_ACTIVE = 1 ORDER BY CURRENCY,TIER_TYPE";
    try {
      tierWiseSettingList = this.jdbcTemplate.query(query, (RowMapper)new BeanPropertyRowMapper(TierWiseSetting.class));
      response.put("value", tierWiseSettingList);
      response.put("error", "");
    } catch (EmptyResultDataAccessException e) {
      log.error("empty resultset error: ", (Throwable)e);
      response.put("error", e.getMessage());
    } catch (Exception e) {
      e.printStackTrace();
      log.error(e.getMessage());
      response.put("error", e.getMessage());
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /getAllTierWiseSettingsData\nClass: AdminDao\nMethod: getAllTierWiseSettingsData" + 
            
            System.lineSeparator() + errors.toString());
      } 
    } 
    return response;
  }
  
  public Map<String, Object> currencyWiseMiningFees() {
    Map<String, Object> response = new HashMap<>();
    List<MiningFees> miningFeesList = new ArrayList<>();
    String query = "SELECT CURRENCY_ID,CURRENCY,TOKEN_TYPE,FROMFEE,TOFEE,MIN_FEE,FEE_RATE,ACTUAL_FEES FROM CURRENCY_FEES";
    try {
      miningFeesList = this.jdbcTemplate.query(query, (RowMapper)new BeanPropertyRowMapper(MiningFees.class));
      response.put("value", miningFeesList);
      response.put("error", "");
    } catch (EmptyResultDataAccessException e) {
      log.error("empty resultset error: ", (Throwable)e);
      response.put("error", e.getMessage());
    } catch (Exception e) {
      e.printStackTrace();
      log.error(e.getMessage());
      response.put("error", e.getMessage());
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /currencyWiseMiningFees\nClass: AdminDao\nMethod: currencyWiseMiningFees" + 
            
            System.lineSeparator() + errors.toString());
      } 
    } 
    return response;
  }
  
  public Map<String, Object> settingsDataFiatConvertion() {
    Map<String, Object> response = new HashMap<>();
    this.error = new ErrorResponse();
    try {
      SimpleJdbcCall simpleJdbcCall = (new SimpleJdbcCall(this.jdbcTemplate)).withProcedureName("GET_ALL_SETTINGS_VALUE");
      Map<String, Object> result = simpleJdbcCall.execute(new Object[0]);
      List<Object> returnResult = (List<Object>)result.get("P_SETTINGS_VALUE");
      response.put("response", returnResult);
      this.error.setError_data(0);
      this.error.setError_msg("");
    } catch (Exception e) {
      e.printStackTrace();
      this.error.setError_data(1);
      this.error.setError_msg(e.getMessage());
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /settingsDataFiatConvertion\nClass: AdminDao\nMethod: settingsDataFiatConvertion" + 
            
            System.lineSeparator() + errors.toString());
      } 
    } 
    response.put("error", this.error);
    return response;
  }
  
  public AdminUsersResponse updateTierwiseSettingsData(TierWiseSetting tierWiseSetting, HttpServletRequest request) {
    this.adminUsersResponse = new AdminUsersResponse();
    this.error = new ErrorResponse();
    String query = "UPDATE TIER_WISE_TRANSACTION_SETTINGS SET MIN_LIMIT = ?,DAILY_SEND_LIMIT = ?,MONTHLY_SEND_LIMIT = ?,DAILY_BUY_LIMIT = ?,DAILY_SELL_LIMIT = ?,MAKER_CHARGE = ?,TAKER_CHARGE = ?,DISCOUNT_MAKER_CHARGE = ?,DISCOUNT_TAKER_CHARGE = ?,TXN_CHARGE = ?,MIN_BALANCE = ?  WHERE CURRENCY_ID = ? AND TIER_TYPE = ?";
    int i = 0;
    try {
      Map<String, Object> result = this.utility.accessCheck(tierWiseSetting.getAdminUserId(), 384, request
          .getRemoteAddr());
      int returnId = ((BigDecimal)result.get("R_RETURN_ID")).intValue();
      String message = (String)result.get("R_MESSAGE");
      if (returnId == 1) {
        i = this.jdbcTemplate.update(query, new Object[] { 
              Double.valueOf(tierWiseSetting.getMinLimit()), Double.valueOf(tierWiseSetting.getDailySendLimit()), 
              Double.valueOf(tierWiseSetting.getMonthlySendLimit()), Double.valueOf(tierWiseSetting.getDailyBuyLimit()), 
              Double.valueOf(tierWiseSetting.getDailySellLimit()), Double.valueOf(tierWiseSetting.getMakerCharge()), 
              Double.valueOf(tierWiseSetting.getTakerCharge()), Double.valueOf(tierWiseSetting.getDiscountMakerCharge()), 
              Double.valueOf(tierWiseSetting.getDiscountTakerCharge()), Double.valueOf(tierWiseSetting.getTxnCharge()), 
              Double.valueOf(tierWiseSetting.getMinBalance()), Integer.valueOf(tierWiseSetting.getCurrencyId()), 
              Integer.valueOf(tierWiseSetting.getTierType()) });
        if (i == 0) {
          this.error.setError_data(1);
          this.error.setError_msg("Updation failed");
        } 
      } else {
        this.error.setError_data(1);
        this.error.setError_msg(message);
      } 
    } catch (Exception e) {
      e.printStackTrace();
      this.error.setError_data(1);
      this.error.setError_msg(e.getMessage());
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /updateTierwiseSettingsData\nClass: AdminDao\nMethod: updateTierwiseSettingsData" + 
            
            System.lineSeparator() + errors.toString());
      } 
    } 
    this.adminUsersResponse.setError(this.error);
    return this.adminUsersResponse;
  }
  
  public AdminUsersResponse updateMiningFees(MiningFees miningFees, HttpServletRequest request) {
    this.adminUsersResponse = new AdminUsersResponse();
    this.error = new ErrorResponse();
    try {
      Map<String, Object> result = this.utility.accessCheck(miningFees.getAdminUserId(), 298, request.getRemoteAddr());
      int returnId = ((BigDecimal)result.get("R_RETURN_ID")).intValue();
      String message = (String)result.get("R_MESSAGE");
      if (returnId == 1) {
        SimpleJdbcCall simpleJdbcCall = (new SimpleJdbcCall(this.jdbcTemplate)).withProcedureName("UPDATE_CURRENCY_FEES_SP");
        MapSqlParameterSource input = new MapSqlParameterSource();
        input.addValue("P_CURRENCY_ID", Integer.valueOf(miningFees.getCurrencyId()));
        input.addValue("P_FROMFEE", Double.valueOf(miningFees.getFromFee()));
        input.addValue("P_TOFEE", Double.valueOf(miningFees.getToFee()));
        input.addValue("P_MINFEE", Double.valueOf(miningFees.getMinFee()));
        input.addValue("P_FEEERATE", Double.valueOf(miningFees.getFeeRate()));
        input.addValue("P_TOKEN_TYPE", miningFees.getTokenType());
        result = simpleJdbcCall.execute((SqlParameterSource)input);
        message = (String)result.get("MESSAGE");
        returnId = ((BigDecimal)result.get("RETURN_ID")).intValue();
        if (returnId == 1) {
          this.error.setError_data(0);
          this.error.setError_msg("");
        } else {
          this.error.setError_data(1);
          this.error.setError_msg(message);
        } 
      } else {
        this.error.setError_data(1);
        this.error.setError_msg(message);
      } 
    } catch (Exception e) {
      e.printStackTrace();
      this.error.setError_data(1);
      this.error.setError_msg(e.getMessage());
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /updateMiningFees\nClass: AdminDao\nMethod: updateMiningFees" + 
            System.lineSeparator() + errors.toString());
      } 
    } 
    this.adminUsersResponse.setError(this.error);
    return this.adminUsersResponse;
  }
  
  public Map<String, Object> getFiatCurrencyRate() {
    Map<String, Object> response = new HashMap<>();
    List<CurrencyRate> currencyRateList = new ArrayList<>();
    this.error = new ErrorResponse();
    try {
      SimpleJdbcCall simpleJdbcCall = (new SimpleJdbcCall(this.jdbcTemplate)).withProcedureName("GET_FIAT_CURRENCY_RATE").returningResultSet("GET_CURRENCY_RATE", (RowMapper)BeanPropertyRowMapper.newInstance(CurrencyRate.class));
      Map<String, Object> result = simpleJdbcCall.execute(new Object[0]);
      currencyRateList = (List<CurrencyRate>)result.get("GET_CURRENCY_RATE");
      response.put("response", currencyRateList);
      this.error.setError_data(0);
      this.error.setError_msg("");
    } catch (Exception e) {
      e.printStackTrace();
      this.error.setError_data(1);
      this.error.setError_msg(e.getMessage());
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /getFiatCurrencyRate\nClass: AdminDao\nMethod: getFiatCurrencyRate" + 
            
            System.lineSeparator() + errors.toString());
      } 
    } 
    response.put("error", this.error);
    return response;
  }
  
  public Map<String, Object> updateFiatCurrencyRate(CurrencyRate currencyRate, HttpServletRequest request) {
    Map<String, Object> response = new HashMap<>();
    try {
      if (currencyRate.getAdminUserId() > 0 && currencyRate.getIncrBy() > 0.0D && 
        StringUtils.hasText(currencyRate.getAssetPair())) {
        Map<String, Object> result = this.utility.accessCheck(currencyRate.getAdminUserId(), 385, request
            .getRemoteAddr());
        int returnId = ((BigDecimal)result.get("R_RETURN_ID")).intValue();
        String message = (String)result.get("R_MESSAGE");
        if (returnId == 1) {
          SimpleJdbcCall simpleJdbcCall = (new SimpleJdbcCall(this.jdbcTemplate)).withProcedureName("UPDATE_FIAT_CURRENCY_RATE");
          MapSqlParameterSource input = new MapSqlParameterSource();
          input = new MapSqlParameterSource();
          input.addValue("P_ASSET_PAIR", currencyRate.getAssetPair());
          input.addValue("P_INCR_PREC", Double.valueOf(currencyRate.getIncrBy()));
          result = simpleJdbcCall.execute((SqlParameterSource)input);
          message = (String)result.get("MESSAGE");
          returnId = ((BigDecimal)result.get("RETURN_ID")).intValue();
          if (returnId == 1) {
            this.error = new ErrorResponse();
          } else {
            this.error = new ErrorResponse(1, message);
          } 
        } else {
          this.error = new ErrorResponse(1, message);
        } 
      } else {
        this.error = new ErrorResponse(1);
      } 
    } catch (Exception e) {
      e.printStackTrace();
      this.error = new ErrorResponse(1, e.getMessage());
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /updateFiatCurrencyRate\nClass: AdminDao\nMethod: updateFiatCurrencyRate" + 
            
            System.lineSeparator() + errors.toString());
      } 
    } 
    response.put("error", this.error);
    return response;
  }
  
  public Map<String, Object> getAllCurrencyByType() {
    Map<String, Object> response = new HashMap<>();
    this.error = new ErrorResponse();
    List<CurrencyMaster> currencyList = null;
    String sql = "select currency_id,currency_code from currency_master where is_Active = 1 and is_base_currency = 1 order by currency_code";
    try {
      currencyList = new ArrayList<>();
      currencyList = this.jdbcTemplate.query(sql, (RowMapper)new BeanPropertyRowMapper(CurrencyMaster.class));
      response.put("baseCurrencyList", currencyList);
      currencyList = new ArrayList<>();
      sql = "select currency_id,currency_code from currency_master where is_active = 1 and currency_type = 2 order by currency_code";
      currencyList = this.jdbcTemplate.query(sql, (RowMapper)new BeanPropertyRowMapper(CurrencyMaster.class));
      response.put("currencyList", currencyList);
      currencyList = new ArrayList<>();
      sql = "select currency_id,currency_code from currency_master where is_Active = 1 and currency_type = 1 order by currency_code";
      currencyList = this.jdbcTemplate.query(sql, (RowMapper)new BeanPropertyRowMapper(CurrencyMaster.class));
      response.put("fiatList", currencyList);
      this.error.setError_data(0);
      this.error.setError_msg("");
    } catch (Exception e) {
      e.printStackTrace();
      this.error.setError_data(1);
      this.error.setError_msg(e.getMessage());
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /getAllCurrencyByType\nClass: AdminDao\nMethod: getAllCurrencyByType" + 
            
            System.lineSeparator() + errors.toString());
      } 
    } 
    response.put("error", this.error);
    return response;
  }
  
  public Map<String, Object> getAllCurrency() {
    Map<String, Object> response = new HashMap<>();
    this.error = new ErrorResponse();
    try {
      SimpleJdbcCall simpleJdbcCall = (new SimpleJdbcCall(this.jdbcTemplate)).withProcedureName("GET_CURRENCY_MASTER").returningResultSet("P_RESULTS", (RowMapper)BeanPropertyRowMapper.newInstance(CurrencyMaster.class));
      Map<String, Object> result = simpleJdbcCall.execute(new Object[0]);
      List<CurrencyMaster> currencyList = (List<CurrencyMaster>)result.get("P_RESULTS");
      response.put("currencyList", currencyList);
      this.error.setError_data(0);
      this.error.setError_msg("");
    } catch (Exception e) {
      e.printStackTrace();
      this.error.setError_data(1);
      this.error.setError_msg(e.getMessage());
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /getAllCurrency\nClass: AdminDao\nMethod: getAllCurrency" + 
            System.lineSeparator() + errors.toString());
      } 
    } 
    response.put("error", this.error);
    return response;
  }
  
  public Map<String, Object> getWalletOrder() {
    Map<String, Object> response = new HashMap<>();
    String sql = "select max(wallet_order) from currency_master";
    try {
      BigDecimal walletOrder = (BigDecimal)DataAccessUtils.singleResult(this.jdbcTemplate.query(sql, (RowMapper)new SingleColumnRowMapper()));
      if (walletOrder != null) {
        response.put("walletOrder", Integer.valueOf(walletOrder.intValue() + 1));
        this.error = new ErrorResponse();
      } 
    } catch (Exception e) {
      e.printStackTrace();
      this.error = new ErrorResponse(1, e.getMessage());
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /getWalletOrder\nClass: AdminDao\nMethod: getWalletOrder" + 
            System.lineSeparator() + errors.toString());
      } 
    } 
    response.put("error", this.error);
    return response;
  }
  
  public Map<String, Object> addCurrency(CurrencyMaster currencyMaster, HttpServletRequest request) {
    return changesInCurrencyMaster(currencyMaster, "INSERT", request);
  }
  
  public Map<String, Object> updateCurrency(CurrencyMaster currencyMaster, HttpServletRequest request) {
    return changesInCurrencyMaster(currencyMaster, "UPDATE", request);
  }
  
  private Map<String, Object> changesInCurrencyMaster(CurrencyMaster currencyMaster, String action, HttpServletRequest request) {
    Map<String, Object> response = new HashMap<>();
    try {
      if (currencyMaster.getAdminUserId() > 0) {
        Map<String, Object> result = this.utility.accessCheck(currencyMaster.getAdminUserId(), 386, request
            .getRemoteAddr());
        int returnId = ((BigDecimal)result.get("R_RETURN_ID")).intValue();
        String message = (String)result.get("R_MESSAGE");
        if (returnId == 1) {
          SimpleJdbcCall simpleJdbcCall = (new SimpleJdbcCall(this.jdbcTemplate)).withProcedureName("CURRENCY_MASTER_INSERT_SP");
          MapSqlParameterSource input = new MapSqlParameterSource();
          input.addValue("P_CURRENCY_ID", Integer.valueOf(currencyMaster.getCurrencyId()));
          input.addValue("P_CURRENCY_CODE", currencyMaster.getCurrencyCode());
          input.addValue("P_CURRENCY_NAME", currencyMaster.getCurrencyName());
          input.addValue("P_IS_ACTIVE", Integer.valueOf(currencyMaster.getIsActive()));
          input.addValue("P_IS_BASE_CURRENCY", Integer.valueOf(currencyMaster.getIsBaseCurrency()));
          input.addValue("P_C_PRECISION", Integer.valueOf(currencyMaster.getcPrecision()));
          input.addValue("P_IS_SEND_RECEIVED", Integer.valueOf(currencyMaster.getIsSend()));
          input.addValue("P_CURRENCY_TYPE", Integer.valueOf(currencyMaster.getCurrencyType()));
          input.addValue("P_WALLET_ORDER", Integer.valueOf(currencyMaster.getWalletOrder()));
          input.addValue("P_IS_NODE_BLOCK_STATUS", Integer.valueOf(currencyMaster.getIsNodeBlockStatus()));
          input.addValue("P_WEBSITE_LINK", currencyMaster.getWebsiteLink());
          input.addValue("P_LISTING_COIN_FLAG", Integer.valueOf(currencyMaster.getListingCoinFlag()));
          input.addValue("P_TXN_CHARGE", Float.valueOf(currencyMaster.getTxnCharge()));
          input.addValue("P_AUTO_LIQUIDITY_STATUS", Integer.valueOf(currencyMaster.getAutoLiquidityStatus()));
          input.addValue("P_PASSWORD_REQUIRED", Integer.valueOf(currencyMaster.getPasswordRequired()));
          input.addValue("P_IS_FUND", Integer.valueOf(currencyMaster.getIsFund()));
          input.addValue("P_IS_ISO_MARGIN", Integer.valueOf(currencyMaster.getIsIsoMargin()));
          input.addValue("P_IS_CROSS_MARGIN", Integer.valueOf(currencyMaster.getIsCrossMargin()));
          input.addValue("P_INTEREST_RATE", Float.valueOf(currencyMaster.getInterestRate()));
          input.addValue("P_LENDER_INTEREST", Float.valueOf(currencyMaster.getLenderInterest()));
          input.addValue("P_EXCHANGE_INTEREST", Float.valueOf(currencyMaster.getExchangeInterest()));
          input.addValue("P_IS_ERC20", Integer.valueOf(currencyMaster.getIsErc20()));
          input.addValue("P_ACTION", action);
          result = simpleJdbcCall.execute((SqlParameterSource)input);
          int lastInsertedId = ((BigDecimal)result.get("LAST_INSERT_ID")).intValue();
          message = (String)result.get("MESSAGE");
          if (lastInsertedId != 0) {
            response.put("lastInsertedId", Integer.valueOf(lastInsertedId));
            response.put("error", message);
          } else {
            response.put("error", message);
          } 
        } else {
          response.put("error", message);
        } 
      } else {
        response.put("error", "Mandatory fields missing.");
      } 
    } catch (Exception e) {
      e.printStackTrace();
      log.error(e.getMessage());
      response.put("error", e.getMessage());
    } 
    return response;
  }
  
  public Map<String, Object> getAssetPairDetails() {
    Map<String, Object> response = new HashMap<>();
    this.error = new ErrorResponse();
    try {
      SimpleJdbcCall simpleJdbcCall = (new SimpleJdbcCall(this.jdbcTemplate)).withProcedureName("GET_ASSET_PAIR_DETAILS").returningResultSet("P_RESULTS", (RowMapper)BeanPropertyRowMapper.newInstance(AssetPairDetails.class));
      Map<String, Object> result = simpleJdbcCall.execute(new Object[0]);
      List<AssetPairDetails> assetPairList = (List<AssetPairDetails>)result.get("P_RESULTS");
      response.put("assetPairList", assetPairList);
      this.error.setError_data(0);
      this.error.setError_msg("");
    } catch (Exception e) {
      e.printStackTrace();
      this.error.setError_data(1);
      this.error.setError_msg(e.getMessage());
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /getAssetPairDetails\nClass: AdminDao\nMethod: getAssetPairDetails" + 
            
            System.lineSeparator() + errors.toString());
      } 
    } 
    response.put("error", this.error);
    return response;
  }
  
  public Map<String, Object> getAssetOrder(int baseCurrencyId) {
    Map<String, Object> response = new HashMap<>();
    String sql = "select max(asset_order) from currency_master_mapping where BASE_CURRENCY_ID = ?";
    try {
      BigDecimal assetOrder = (BigDecimal)DataAccessUtils.singleResult(this.jdbcTemplate
          .query(sql, new Object[] { Integer.valueOf(baseCurrencyId) }, (RowMapper)new SingleColumnRowMapper()));
      if (assetOrder != null) {
        response.put("assetOrder", Integer.valueOf(assetOrder.intValue()));
        this.error = new ErrorResponse();
      } else {
        this.error = new ErrorResponse(1, "Invalid Base Currency.");
      } 
    } catch (Exception e) {
      e.printStackTrace();
      this.error = new ErrorResponse(1, e.getMessage());
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /getAssetOrder\nClass: AdminDao\nMethod: getAssetOrder" + 
            System.lineSeparator() + errors.toString());
      } 
    } 
    response.put("error", this.error);
    return response;
  }
  
  public Map<String, Object> addAssetPair(AssetPairDetails assetPairDetails, HttpServletRequest request) {
    return changesInAssetPairDetails(assetPairDetails, "INSERT", request);
  }
  
  public Map<String, Object> updateAssetPair(AssetPairDetails assetPairDetails, HttpServletRequest request) {
    return changesInAssetPairDetails(assetPairDetails, "UPDATE", request);
  }
  
  private Map<String, Object> changesInAssetPairDetails(AssetPairDetails assetPairDetails, String action, HttpServletRequest request) {
    Map<String, Object> response = new HashMap<>();
    try {
      if (assetPairDetails.getAdminUserId() > 0 && assetPairDetails.getId() > 0) {
        Map<String, Object> result = this.utility.accessCheck(assetPairDetails.getAdminUserId(), 400, request
            .getRemoteAddr());
        int returnId = ((BigDecimal)result.get("R_RETURN_ID")).intValue();
        String message = (String)result.get("R_MESSAGE");
        if (returnId == 1) {
          SimpleJdbcCall simpleJdbcCall = (new SimpleJdbcCall(this.jdbcTemplate)).withProcedureName("ASSET_PAIR_INSERT_SP");
          MapSqlParameterSource input = new MapSqlParameterSource();
          input.addValue("P_ID", Integer.valueOf(assetPairDetails.getId()));
          input.addValue("P_BASE_CURRENCY_ID", Integer.valueOf(assetPairDetails.getBaseCurrencyId()));
          input.addValue("P_CURRENCY_ID", Integer.valueOf(assetPairDetails.getCurrencyId()));
          input.addValue("P_ASSET_CODE", assetPairDetails.getAssetCode());
          input.addValue("P_IS_ACTIVE", Integer.valueOf(assetPairDetails.getIsActive()));
          input.addValue("P_ASSET_ORDER", Integer.valueOf(assetPairDetails.getAssetOrder()));
          input.addValue("P_ASSET_PAIR_TYPE", Integer.valueOf(assetPairDetails.getAssetPairType()));
          input.addValue("P_ACTION", action);
          result = simpleJdbcCall.execute((SqlParameterSource)input);
          int lastInsertedId = ((BigDecimal)result.get("LAST_INSERT_ID")).intValue();
          message = (String)result.get("MESSAGE");
          if (lastInsertedId != 0) {
            response.put("lastInsertedId", Integer.valueOf(lastInsertedId));
            response.put("error", message);
          } else {
            response.put("error", message);
          } 
        } else {
          response.put("error", message);
        } 
      } else {
        response.put("error", "Mandatory fields missing.");
      } 
    } catch (Exception e) {
      e.printStackTrace();
      log.error(e.getMessage());
      response.put("error", e.getMessage());
    } 
    return response;
  }
  
  public Map<String, Object> getMachingEngineAssetPair() {
    Map<String, Object> response = new HashMap<>();
    this.error = new ErrorResponse();
    String url = this.env.getProperty("project.paybito.new.networkoffers.api") + "getAsset";
    RestTemplate restTemplate = new RestTemplate();
    try {
      List assetPairList = (List)restTemplate.getForObject(url, List.class, new Object[0]);
      response.put("assetPairList", assetPairList);
      this.error.setError_data(0);
      this.error.setError_msg("");
    } catch (Exception e) {
      e.printStackTrace();
      this.error.setError_data(1);
      this.error.setError_msg(e.getMessage());
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /getMachingEngineAssetPair\nClass: AdminDao\nMethod: getMachingEngineAssetPair" + 
            
            System.lineSeparator() + errors.toString());
      } 
    } 
    response.put("error", this.error);
    return response;
  }
  
  public Map<String, Object> addMachingEngineAssetPair(AssetPairDetails assetPairDetails, HttpServletRequest request) {
    Map<String, Object> response = new HashMap<>();
    try {
      if (assetPairDetails.getAdminUserId() > 0 && StringUtils.hasText(assetPairDetails.getAssetCode()) && 
        StringUtils.hasText(assetPairDetails.getAssetPair())) {
        String url = this.env.getProperty("project.engine.add.assetpair.api") + "addAsset";
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> result = this.utility.accessCheck(assetPairDetails.getAdminUserId(), 301, request
            .getRemoteAddr());
        int returnId = ((BigDecimal)result.get("R_RETURN_ID")).intValue();
        String message = (String)result.get("R_MESSAGE");
        if (returnId == 1) {
          JSONObject requestJson = new JSONObject();
          requestJson.put("symbol", assetPairDetails.getAssetCode());
          requestJson.put("assetcode", assetPairDetails.getAssetPair());
          HttpHeaders headers = new HttpHeaders();
          headers.setContentType(MediaType.APPLICATION_JSON);
          HttpEntity<String> entity = new HttpEntity(requestJson.toString(), (MultiValueMap)headers);
          String restResponse = (String)restTemplate.postForObject(url, entity, String.class, new Object[0]);
          if (restResponse != null) {
            JSONObject responseJson = new JSONObject(restResponse);
            if (responseJson.getInt("statuscode") == 1) {
              this.error = new ErrorResponse(0, responseJson.getString("message"));
            } else {
              this.error = new ErrorResponse(1, responseJson.getString("message"));
            } 
          } 
        } else {
          this.error = new ErrorResponse(0, message);
        } 
      } else {
        this.error = new ErrorResponse(1);
      } 
    } catch (Exception e) {
      e.printStackTrace();
      this.error.setError_data(1);
      this.error.setError_msg(e.getMessage());
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /addMachingEngineAssetPair\nClass: AdminDao\nMethod: addMachingEngineAssetPair" + 
            
            System.lineSeparator() + errors.toString());
      } 
    } 
    response.put("error", this.error);
    return response;
  }
  
  public Map<String, Object> getFuturesContractType() {
    Map<String, Object> response = new HashMap<>();
    String query = "select * from futures_contract_type_master where is_active = 1 ";
    try {
      List<FuturesContractTypeMaster> resultList = this.jdbcTemplate.query(query, (RowMapper)new BeanPropertyRowMapper(FuturesContractTypeMaster.class));
      response.put("response", resultList);
      response.put("error", "");
    } catch (EmptyResultDataAccessException e) {
      log.error("empty resultset error: ", (Throwable)e);
      response.put("error", e.getMessage());
    } catch (Exception e) {
      e.printStackTrace();
      log.error(e.getMessage());
      response.put("error", e.getMessage());
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /getFuturesContractType\nClass: AdminDao\nMethod: getFuturesContractType" + 
            
            System.lineSeparator() + errors.toString());
      } 
    } 
    return response;
  }
  
  public Map<String, Object> getFuturesAssetPairDetails() {
    Map<String, Object> response = new HashMap<>();
    this.error = new ErrorResponse();
    try {
      SimpleJdbcCall simpleJdbcCall = (new SimpleJdbcCall(this.jdbcTemplate)).withProcedureName("FUTURES_ASSET_PAIR_DETAILS").returningResultSet("P_RESULTS", (RowMapper)BeanPropertyRowMapper.newInstance(AssetPairDetails.class));
      Map<String, Object> result = simpleJdbcCall.execute(new Object[0]);
      List<AssetPairDetails> assetPairList = (List<AssetPairDetails>)result.get("P_RESULTS");
      response.put("assetPairList", assetPairList);
      this.error.setError_data(0);
      this.error.setError_msg("");
    } catch (Exception e) {
      e.printStackTrace();
      this.error.setError_data(1);
      this.error.setError_msg(e.getMessage());
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /getFuturesAssetPairDetails\nClass: AdminDao\nMethod: getFuturesAssetPairDetails" + 
            
            System.lineSeparator() + errors.toString());
      } 
    } 
    response.put("error", this.error);
    return response;
  }
  
  public Map<String, Object> addFuturesAssetPair(int adminUser, List<AssetPairDetails> assetPairDetails, HttpServletRequest request) {
    System.out.println("addFuturesAssetPair dao called.");
    return changesInFuturesAssetPairDetails(adminUser, assetPairDetails, "INSERT", request);
  }
  
  public Map<String, Object> updateFuturesAssetPair(int adminUser, List<AssetPairDetails> assetPairDetails, HttpServletRequest request) {
    return changesInFuturesAssetPairDetails(adminUser, assetPairDetails, "UPDATE", request);
  }
  
  private Map<String, Object> changesInFuturesAssetPairDetails(int adminUser, List<AssetPairDetails> assetPairDetails, String action, HttpServletRequest request) {
    Map<String, Object> response = new HashMap<>();
    try {
      if (adminUser > 0) {
        int lastInsertedId = 0;
        String message = "";
        Map<String, Object> result = this.utility.accessCheck(adminUser, 308, request.getRemoteAddr());
        int returnId = ((BigDecimal)result.get("R_RETURN_ID")).intValue();
        message = (String)result.get("R_MESSAGE");
        if (returnId == 1) {
          SimpleJdbcCall simpleJdbcCall = (new SimpleJdbcCall(this.jdbcTemplate)).withProcedureName("FUTURE_ASSETPAIR_INSERT_SP");
          for (AssetPairDetails assetPairDetails2 : assetPairDetails) {
            MapSqlParameterSource input = new MapSqlParameterSource();
            input.addValue("P_ID", Integer.valueOf(assetPairDetails2.getId()));
            input.addValue("P_BASE_CURRENCY_ID", Integer.valueOf(assetPairDetails2.getBaseCurrencyId()));
            input.addValue("P_BASECURRENCY", assetPairDetails2.getBaseCurrency());
            input.addValue("P_CURRENCY_ID", Integer.valueOf(assetPairDetails2.getCurrencyId()));
            input.addValue("P_CURRENCY", assetPairDetails2.getCurrency());
            input.addValue("P_ASSET_CODE", assetPairDetails2.getAssetCode());
            input.addValue("P_ASSET_PAIR_NAME", assetPairDetails2.getAssetPairName());
            input.addValue("P_ASSET_PAIR", assetPairDetails2.getAssetPair());
            input.addValue("P_ASSET_ORDER", Integer.valueOf(assetPairDetails2.getAssetOrder()));
            input.addValue("P_CONTRACT_TYPE_ID", Integer.valueOf(assetPairDetails2.getContractTypeId()));
            input.addValue("P_QUARTER_VALUE", assetPairDetails2.getQuarterValue());
            input.addValue("P_AMOUNT_PRECISION", assetPairDetails2.getAmountPrecision());
            input.addValue("P_PRICE_PRECISION", assetPairDetails2.getPricePrecision());
            input.addValue("P_BUY_SELL_FLAG", Integer.valueOf(assetPairDetails2.getBuySellFlag()));
            input.addValue("P_IS_ACTIVE", Integer.valueOf(assetPairDetails2.getIsActive()));
            input.addValue("P_ACTION", action);
            System.out.println("addFuturesAssetPair proc input : " + input);
            result = simpleJdbcCall.execute((SqlParameterSource)input);
            message = (String)result.get("MESSAGE");
            System.out.println("message :" + message);
            System.out.println("result.get(\"LAST_INSERT_ID\") :" + result.get("LAST_INSERT_ID"));
            lastInsertedId = ((BigDecimal)result.get("LAST_INSERT_ID")).intValue();
          } 
          if (lastInsertedId != 0) {
            response.put("lastInsertedId", Integer.valueOf(lastInsertedId));
            response.put("error", message);
          } else {
            response.put("error", message);
          } 
        } else {
          response.put("error", message);
        } 
      } else {
        response.put("error", "Mandatory fields missing.");
      } 
    } catch (Exception e) {
      e.printStackTrace();
      log.error(e.getMessage());
      response.put("error", e.getMessage());
    } 
    return response;
  }
  
  public Map<String, Object> getFuturesMachingEngineAssetPair() {
    Map<String, Object> response = new HashMap<>();
    this.error = new ErrorResponse();
    String url = this.env.getProperty("project.paybito.futures.networkoffers.api") + "getFutureAsset";
    RestTemplate restTemplate = new RestTemplate();
    try {
      List assetPairList = (List)restTemplate.getForObject(url, List.class, new Object[0]);
      response.put("assetPairList", assetPairList);
      this.error.setError_data(0);
      this.error.setError_msg("");
    } catch (Exception e) {
      e.printStackTrace();
      this.error.setError_data(1);
      this.error.setError_msg(e.getMessage());
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /getFuturesMachingEngineAssetPair\nClass: AdminDao\nMethod: getFuturesMachingEngineAssetPair" + 
            
            System.lineSeparator() + errors.toString());
      } 
    } 
    response.put("error", this.error);
    return response;
  }
  
  public Map<String, Object> addFuturesMachingEngineAssetPair(int adminUser, List<AssetPairDetails> assetPairDetails, HttpServletRequest request) {
    Map<String, Object> response = new HashMap<>();
    this.error = new ErrorResponse();
    String url = this.env.getProperty("project.paybito.futures.networkoffers.api") + "addFutureAsset";
    RestTemplate restTemplate = new RestTemplate();
    List<FutureMatchingEngineAsset> requestList = new ArrayList<>();
    try {
      Map<String, Object> result = this.utility.accessCheck(adminUser, 307, request.getRemoteAddr());
      int returnId = ((BigDecimal)result.get("R_RETURN_ID")).intValue();
      String message = (String)result.get("R_MESSAGE");
      if (returnId == 1) {
        for (AssetPairDetails assetPairDetails2 : assetPairDetails) {
          FutureMatchingEngineAsset requestJson = new FutureMatchingEngineAsset();
          requestJson.setSymbol(Integer.parseInt(assetPairDetails2.getAssetCode()));
          requestJson.setPair_name(assetPairDetails2.getAssetPair());
          requestJson.setContract_type(assetPairDetails2.getContractType());
          requestJson.setExpiry_date(assetPairDetails2.getExpiryDate());
          requestList.add(requestJson);
        } 
        String restResponse = (String)restTemplate.postForObject(url, requestList, String.class, new Object[0]);
        if (restResponse != null) {
          JSONObject responseJson = new JSONObject(restResponse);
          if (responseJson.getInt("statuscode") == 1) {
            this.error.setError_data(0);
            this.error.setError_msg(responseJson.getString("message"));
          } else {
            this.error.setError_data(1);
            this.error.setError_msg(responseJson.getString("message"));
          } 
        } 
      } else {
        this.error.setError_data(1);
        this.error.setError_msg(message);
      } 
    } catch (Exception e) {
      e.printStackTrace();
      this.error.setError_data(1);
      this.error.setError_msg(e.getMessage());
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /addFuturesMachingEngineAssetPair\nClass: AdminDao\nMethod: addFuturesMachingEngineAssetPair" + 
            
            System.lineSeparator() + errors.toString());
      } 
    } 
    response.put("error", this.error);
    return response;
  }
  
  public Map<String, Object> getOptionsContractType() {
    Map<String, Object> response = new HashMap<>();
    String query = "select * from option_contract_type_master where is_active = 1 ";
    try {
      List<FuturesContractTypeMaster> resultList = this.jdbcTemplate.query(query, (RowMapper)new BeanPropertyRowMapper(FuturesContractTypeMaster.class));
      response.put("response", resultList);
      response.put("error", "");
    } catch (EmptyResultDataAccessException e) {
      log.error("empty resultset error: ", (Throwable)e);
      response.put("error", e.getMessage());
    } catch (Exception e) {
      e.printStackTrace();
      log.error(e.getMessage());
      response.put("error", e.getMessage());
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /getOptionsContractType\nClass: AdminDao\nMethod: getOptionsContractType" + 
            
            System.lineSeparator() + errors.toString());
      } 
    } 
    return response;
  }
  
  public Map<String, Object> getOptionsAssetPairDetails() {
    Map<String, Object> response = new HashMap<>();
    this.error = new ErrorResponse();
    try {
      SimpleJdbcCall simpleJdbcCall = (new SimpleJdbcCall(this.jdbcTemplate)).withProcedureName("OPTIONS_ASSET_PAIR_DETAILS").returningResultSet("P_RESULTS", (RowMapper)BeanPropertyRowMapper.newInstance(AssetPairDetails.class));
      Map<String, Object> result = simpleJdbcCall.execute(new Object[0]);
      List<AssetPairDetails> assetPairList = (List<AssetPairDetails>)result.get("P_RESULTS");
      response.put("assetPairList", assetPairList);
      this.error.setError_data(0);
      this.error.setError_msg("");
    } catch (Exception e) {
      e.printStackTrace();
      this.error.setError_data(1);
      this.error.setError_msg(e.getMessage());
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /getOptionsAssetPairDetails\nClass: AdminDao\nMethod: getOptionsAssetPairDetails" + 
            
            System.lineSeparator() + errors.toString());
      } 
    } 
    response.put("error", this.error);
    return response;
  }
  
  public Map<String, Object> addOptionsAssetPair(List<AssetPairDetails> assetPairDetails) {
    return changesInOptionsAssetPairDetails(assetPairDetails, "INSERT");
  }
  
  public Map<String, Object> updateOptionsAssetPair(List<AssetPairDetails> assetPairDetails) {
    return changesInOptionsAssetPairDetails(assetPairDetails, "UPDATE");
  }
  
  private Map<String, Object> changesInOptionsAssetPairDetails(List<AssetPairDetails> assetPairDetails, String action) {
    Map<String, Object> response = new HashMap<>();
    try {
      int lastInsertedId = 0;
      String message = "";
      SimpleJdbcCall simpleJdbcCall = (new SimpleJdbcCall(this.jdbcTemplate)).withProcedureName("OPTIONS_ASSETPAIR_INSERT_SP");
      for (AssetPairDetails assetPairDetails2 : assetPairDetails) {
        MapSqlParameterSource input = new MapSqlParameterSource();
        input.addValue("P_ID", Integer.valueOf(assetPairDetails2.getId()));
        input.addValue("P_BASE_CURRENCY_ID", Integer.valueOf(assetPairDetails2.getBaseCurrencyId()));
        input.addValue("P_BASECURRENCY", assetPairDetails2.getBaseCurrency());
        input.addValue("P_CURRENCY_ID", Integer.valueOf(assetPairDetails2.getCurrencyId()));
        input.addValue("P_CURRENCY", assetPairDetails2.getCurrency());
        input.addValue("P_ASSET_CODE", assetPairDetails2.getAssetCode());
        input.addValue("P_ASSET_PAIR_NAME", assetPairDetails2.getAssetPairName());
        input.addValue("P_ASSET_PAIR", assetPairDetails2.getAssetPair());
        input.addValue("P_ASSET_ORDER", Integer.valueOf(assetPairDetails2.getAssetOrder()));
        input.addValue("P_CONTRACT_TYPE_ID", Integer.valueOf(assetPairDetails2.getContractTypeId()));
        input.addValue("P_QUARTER_VALUE", assetPairDetails2.getQuarterValue());
        input.addValue("P_AMOUNT_PRECISION", assetPairDetails2.getAmountPrecision());
        input.addValue("P_PRICE_PRECISION", assetPairDetails2.getPricePrecision());
        input.addValue("P_BUY_SELL_FLAG", Integer.valueOf(assetPairDetails2.getBuySellFlag()));
        input.addValue("P_IS_ACTIVE", Integer.valueOf(assetPairDetails2.getIsActive()));
        input.addValue("P_ACTION", action);
        System.out.println("OPTIONS_ASSETPAIR_INSERT_SP proc input : " + input);
        Map<String, Object> result = simpleJdbcCall.execute((SqlParameterSource)input);
        message = (String)result.get("MESSAGE");
        System.out.println("message :" + message);
        System.out.println("result.get(\"LAST_INSERT_ID\") :" + result.get("LAST_INSERT_ID"));
        lastInsertedId = ((BigDecimal)result.get("LAST_INSERT_ID")).intValue();
      } 
      if (lastInsertedId != 0) {
        response.put("lastInsertedId", Integer.valueOf(lastInsertedId));
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
  
  public Map<String, Object> getOptionsMachingEngineAssetPair() {
    Map<String, Object> response = new HashMap<>();
    this.error = new ErrorResponse();
    String url = this.env.getProperty("project.paybito.options.networkoffers.api") + "getOptionsAsset";
    RestTemplate restTemplate = new RestTemplate();
    try {
      List assetPairList = (List)restTemplate.getForObject(url, List.class, new Object[0]);
      response.put("assetPairList", assetPairList);
      this.error.setError_data(0);
      this.error.setError_msg("");
    } catch (Exception e) {
      e.printStackTrace();
      this.error.setError_data(1);
      this.error.setError_msg(e.getMessage());
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /getOptionsMachingEngineAssetPair\nClass: AdminDao\nMethod: getOptionsMachingEngineAssetPair" + 
            
            System.lineSeparator() + errors.toString());
      } 
    } 
    response.put("error", this.error);
    return response;
  }
  
  public Map<String, Object> addOptionsMachingEngineAssetPair(List<AssetPairDetails> assetPairDetails) {
    Map<String, Object> response = new HashMap<>();
    this.error = new ErrorResponse();
    String url = this.env.getProperty("project.paybito.options.networkoffers.api") + "addOptionsAsset";
    RestTemplate restTemplate = new RestTemplate();
    System.out.println("assetPairDetails list : " + assetPairDetails);
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
      String restResponse = (String)restTemplate.postForObject(url, requestList, String.class, new Object[0]);
      if (restResponse != null) {
        JSONObject responseJson = new JSONObject(restResponse);
        if (responseJson.getInt("statuscode") == 1) {
          this.error.setError_data(0);
          this.error.setError_msg(responseJson.getString("message"));
        } else {
          this.error.setError_data(1);
          this.error.setError_msg(responseJson.getString("message"));
        } 
      } 
    } catch (Exception e) {
      e.printStackTrace();
      this.error.setError_data(1);
      this.error.setError_msg(e.getMessage());
    } 
    response.put("error", this.error);
    return response;
  }
  
  public Map<String, Object> tierWiseTradingFees() {
    Map<String, Object> response = new HashMap<>();
    List<TierWiseTradingFees> tradingFeesList = new ArrayList<>();
    String query = "select * from tier_group_master";
    try {
      tradingFeesList = this.jdbcTemplate.query(query, (RowMapper)new BeanPropertyRowMapper(TierWiseTradingFees.class));
      response.put("value", tradingFeesList);
      response.put("error", "");
    } catch (EmptyResultDataAccessException e) {
      log.error("empty resultset error: ", (Throwable)e);
      response.put("error", e.getMessage());
    } catch (Exception e) {
      e.printStackTrace();
      log.error(e.getMessage());
      response.put("error", e.getMessage());
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /tierWiseTradingFees\nClass: AdminDao\nMethod: tierWiseTradingFees" + 
            
            System.lineSeparator() + errors.toString());
      } 
    } 
    return response;
  }
  
  public Map<String, Object> updateTierWiseTradingFees(TierWiseTradingFees tierWiseTradingFees, HttpServletRequest request) {
    Map<String, Object> response = new HashMap<>();
    int i = 0;
    try {
      Map<String, Object> result = this.utility.accessCheck(tierWiseTradingFees.getAdminUserId(), 304, request
          .getRemoteAddr());
      int returnId = ((BigDecimal)result.get("R_RETURN_ID")).intValue();
      String message = (String)result.get("R_MESSAGE");
      if (returnId == 1) {
        if (StringUtils.hasText(tierWiseTradingFees.getAction())) {
          if (tierWiseTradingFees.getAction().equalsIgnoreCase("add")) {
            String query = "insert into tier_group_master (name,maker_fee,taker_fee) values (?,?,?)\t";
            i = this.jdbcTemplate.update(query, new Object[] { tierWiseTradingFees.getName(), Double.valueOf(tierWiseTradingFees.getMakerFee()), 
                  Double.valueOf(tierWiseTradingFees.getTakerFee()) });
          } else if (tierWiseTradingFees.getAction().equalsIgnoreCase("update")) {
            String query = "update tier_group_master set name = ?,maker_fee = ?,taker_fee = ? where id = ? ";
            i = this.jdbcTemplate.update(query, new Object[] { tierWiseTradingFees.getName(), Double.valueOf(tierWiseTradingFees.getMakerFee()), 
                  Double.valueOf(tierWiseTradingFees.getTakerFee()), Integer.valueOf(tierWiseTradingFees.getId()) });
          } else {
            String query = "delete from tier_group_master where id = ? ";
            i = this.jdbcTemplate.update(query, new Object[] { Integer.valueOf(tierWiseTradingFees.getId()) });
          } 
          if (i > 0)
            this.error = new ErrorResponse(); 
        } else {
          this.error = new ErrorResponse(1);
        } 
      } else {
        this.error = new ErrorResponse(1, message);
      } 
    } catch (Exception e) {
      e.printStackTrace();
      this.error = new ErrorResponse(1, e.getMessage());
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /updateTierWiseTradingFees\nClass: AdminDao\nMethod: updateTierWiseTradingFees" + 
            
            System.lineSeparator() + errors.toString());
      } 
    } 
    response.put("error", this.error);
    return response;
  }
  
  public Map<String, Object> volumeWiseTradingFees() {
    Map<String, Object> response = new HashMap<>();
    List<TradingFees> tradingFeesList = new ArrayList<>();
    String query = "select * from volume_wise_taker_maker_fees";
    try {
      tradingFeesList = this.jdbcTemplate.query(query, (RowMapper)new BeanPropertyRowMapper(TradingFees.class));
      response.put("value", tradingFeesList);
      response.put("error", "");
    } catch (EmptyResultDataAccessException e) {
      log.error("empty resultset error: ", (Throwable)e);
      response.put("error", e.getMessage());
    } catch (Exception e) {
      e.printStackTrace();
      log.error(e.getMessage());
      response.put("error", e.getMessage());
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /volumeWiseTradingFees\nClass: AdminDao\nMethod: volumeWiseTradingFees" + 
            
            System.lineSeparator() + errors.toString());
      } 
    } 
    return response;
  }
  
  public AdminUsersResponse updateVolumeWiseTradingFees(TradingFees tradingFees, HttpServletRequest request) {
    this.adminUsersResponse = new AdminUsersResponse();
    this.error = new ErrorResponse();
    try {
      Map<String, Object> result = this.utility.accessCheck(tradingFees.getAdminUserId(), 304, request
          .getRemoteAddr());
      int returnId = ((BigDecimal)result.get("R_RETURN_ID")).intValue();
      String message = (String)result.get("R_MESSAGE");
      if (returnId == 1) {
        SimpleJdbcCall simpleJdbcCall = (new SimpleJdbcCall(this.jdbcTemplate)).withProcedureName("UPDATE_SPOT_TRADING_FEES");
        MapSqlParameterSource input = new MapSqlParameterSource();
        input.addValue("P_ID", Integer.valueOf(tradingFees.getId()));
        input.addValue("P_VOLUME_FROM", Double.valueOf(tradingFees.getVolumeFrom()));
        input.addValue("P_VOLUME_TO", Double.valueOf(tradingFees.getVolumeTo()));
        input.addValue("P_TAKER_FEE", Double.valueOf(tradingFees.getTakerFee()));
        input.addValue("P_MAKER_FEE", Double.valueOf(tradingFees.getMakerFee()));
        input.addValue("P_DISCOUNT_TAKER_FEE", Double.valueOf(tradingFees.getDiscountTakerFee()));
        input.addValue("P_DISCOUNT_MAKER_FEE", Double.valueOf(tradingFees.getDiscountMakerFee()));
        result = simpleJdbcCall.execute((SqlParameterSource)input);
        message = (String)result.get("MESSAGE");
        returnId = ((BigDecimal)result.get("RETURN_ID")).intValue();
        if (returnId == 1) {
          this.error.setError_data(0);
          this.error.setError_msg(message);
        } else {
          this.error.setError_data(1);
          this.error.setError_msg(message);
        } 
      } else {
        this.error.setError_data(1);
        this.error.setError_msg(message);
      } 
    } catch (Exception e) {
      e.printStackTrace();
      this.error.setError_data(1);
      this.error.setError_msg(e.getMessage());
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /updateVolumeWiseTradingFees\nClass: AdminDao\nMethod: updateVolumeWiseTradingFees" + 
            
            System.lineSeparator() + errors.toString());
      } 
    } 
    this.adminUsersResponse.setError(this.error);
    return this.adminUsersResponse;
  }
  
  public Map<String, Object> volumeWiseFuturesTradingFees() {
    Map<String, Object> response = new HashMap<>();
    List<TradingFees> tradingFeesList = new ArrayList<>();
    String query = "select * from F_VOLUME_WISE_TAKER_MAKER_FEES";
    try {
      tradingFeesList = this.jdbcTemplate.query(query, (RowMapper)new BeanPropertyRowMapper(TradingFees.class));
      response.put("value", tradingFeesList);
      response.put("error", "");
    } catch (EmptyResultDataAccessException e) {
      log.error("empty resultset error: ", (Throwable)e);
      response.put("error", e.getMessage());
    } catch (Exception e) {
      e.printStackTrace();
      log.error(e.getMessage());
      response.put("error", e.getMessage());
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /volumeWiseFuturesTradingFees\nClass: AdminDao\nMethod: volumeWiseFuturesTradingFees" + 
            
            System.lineSeparator() + errors.toString());
      } 
    } 
    return response;
  }
  
  public AdminUsersResponse updateVolumeWiseFuturesTradingFees(TradingFees tradingFees, HttpServletRequest request) {
    this.adminUsersResponse = new AdminUsersResponse();
    this.error = new ErrorResponse();
    try {
      Map<String, Object> result = this.utility.accessCheck(tradingFees.getAdminUserId(), 304, request
          .getRemoteAddr());
      int returnId = ((BigDecimal)result.get("R_RETURN_ID")).intValue();
      String message = (String)result.get("R_MESSAGE");
      if (returnId == 1) {
        SimpleJdbcCall simpleJdbcCall = (new SimpleJdbcCall(this.jdbcTemplate)).withProcedureName("UPDATE_FUTURES_TRADING_FEES");
        MapSqlParameterSource input = new MapSqlParameterSource();
        input.addValue("P_ID", Integer.valueOf(tradingFees.getId()));
        input.addValue("P_VOLUME_FROM", Double.valueOf(tradingFees.getVolumeFrom()));
        input.addValue("P_VOLUME_TO", Double.valueOf(tradingFees.getVolumeTo()));
        input.addValue("P_TAKER_FEE", Double.valueOf(tradingFees.getTakerFee()));
        input.addValue("P_MAKER_FEE", Double.valueOf(tradingFees.getMakerFee()));
        result = simpleJdbcCall.execute((SqlParameterSource)input);
        message = (String)result.get("MESSAGE");
        returnId = ((BigDecimal)result.get("RETURN_ID")).intValue();
        if (returnId == 1) {
          this.error.setError_data(0);
          this.error.setError_msg(message);
        } else {
          this.error.setError_data(1);
          this.error.setError_msg(message);
        } 
      } else {
        this.error.setError_data(1);
        this.error.setError_msg(message);
      } 
    } catch (Exception e) {
      e.printStackTrace();
      this.error.setError_data(1);
      this.error.setError_msg(e.getMessage());
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /updateVolumeWiseFuturesTradingFees\nClass: AdminDao\nMethod: updateVolumeWiseFuturesTradingFees" + 
            
            System.lineSeparator() + errors.toString());
      } 
    } 
    this.adminUsersResponse.setError(this.error);
    return this.adminUsersResponse;
  }
  
  public Map<String, Object> marginCallOrAutoliquidity() {
    Map<String, Object> response = new HashMap<>();
    List<MarginCallOrLiquidityValue> resultList = new ArrayList<>();
    String query = "select * from margin_call_auto_liquid";
    try {
      resultList = this.jdbcTemplate.query(query, (RowMapper)new BeanPropertyRowMapper(MarginCallOrLiquidityValue.class));
      response.put("value", resultList);
      response.put("error", "");
    } catch (EmptyResultDataAccessException e) {
      log.error("empty resultset error: ", (Throwable)e);
      response.put("error", e.getMessage());
    } catch (Exception e) {
      e.printStackTrace();
      log.error(e.getMessage());
      response.put("error", e.getMessage());
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /marginCallOrAutoliquidity\nClass: AdminDao\nMethod: marginCallOrAutoliquidity" + 
            
            System.lineSeparator() + errors.toString());
      } 
    } 
    return response;
  }
  
  public Map<String, Object> updateMarginCallOrAutoliquidity(MarginCallOrLiquidityValue marginCallOrLiquidityValue, HttpServletRequest request) {
    Map<String, Object> response = new HashMap<>();
    int i = 0;
    try {
      Map<String, Object> result = this.utility.accessCheck(marginCallOrLiquidityValue.getAdminUserId(), 383, request
          .getRemoteAddr());
      int returnId = ((BigDecimal)result.get("R_RETURN_ID")).intValue();
      String message = (String)result.get("R_MESSAGE");
      if (returnId == 1) {
        if (marginCallOrLiquidityValue.getId() > 0) {
          String query = "update margin_call_auto_liquid set margin_call = ?,autoliquidation = ? where id = ? ";
          i = this.jdbcTemplate.update(query, new Object[] { marginCallOrLiquidityValue.getMarginCall(), marginCallOrLiquidityValue
                .getAutoLiquidation(), Integer.valueOf(marginCallOrLiquidityValue.getId()) });
          if (i > 0)
            this.error = new ErrorResponse(); 
        } else {
          this.error = new ErrorResponse(1);
        } 
      } else {
        this.error = new ErrorResponse(1, message);
      } 
    } catch (Exception e) {
      e.printStackTrace();
      this.error = new ErrorResponse(1, e.getMessage());
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /updateMarginCallOrAutoliquidity\nClass: AdminDao\nMethod: updateMarginCallOrAutoliquidity" + 
            
            System.lineSeparator() + errors.toString());
      } 
    } 
    response.put("error", this.error);
    return response;
  }
  
  public AdminUsersResponse franchiseRegistration(FranchiseRequestInput franchiseRequestInput) {
    this.adminUsersResponse = new AdminUsersResponse();
    this.error = new ErrorResponse();
    Franchise franchise = new Franchise();
    try {
      SimpleJdbcCall simpleJdbcCall = (new SimpleJdbcCall(this.jdbcTemplate)).withProcedureName("ADD_FRANCHISE_DETAILS_SP");
      MapSqlParameterSource input = new MapSqlParameterSource();
      input.addValue("P_COMPANY_NAME", franchiseRequestInput.getCompanyName());
      input.addValue("P_NICK_NAME", franchiseRequestInput.getCompanyNickName());
      input.addValue("P_EMAIL", franchiseRequestInput.getEmail());
      input.addValue("P_REGISTRATION_ID", franchiseRequestInput.getRegistrationId());
      input.addValue("P_ADDRESS_LINE_1", franchiseRequestInput.getAddressLine1());
      input.addValue("P_ADDRESS_LINE_2", franchiseRequestInput.getAddressLine2());
      input.addValue("P_CITY", franchiseRequestInput.getCity());
      input.addValue("P_COUNTRY", franchiseRequestInput.getCountry());
      input.addValue("P_ZIP_CODE", franchiseRequestInput.getZipCode());
      input.addValue("P_PHONE", franchiseRequestInput.getPhone());
      input.addValue("P_WEBSITE", franchiseRequestInput.getWebsite());
      input.addValue("P_AFFILIATE_COMMISSION", Double.valueOf(franchiseRequestInput.getAffiliateCommission()));
      input.addValue("P_SUB_AFFILIATE_COMMISSION", Double.valueOf(franchiseRequestInput.getSubAffiliateCommission()));
      input.addValue("P_HOLDER_NAME", franchiseRequestInput.getAccountHolderName());
      input.addValue("P_BANK_NAME", franchiseRequestInput.getBankName());
      input.addValue("P_ACCOUNT_NO", franchiseRequestInput.getAccountNo());
      input.addValue("P_IFSC_NO", franchiseRequestInput.getIfscNo());
      input.addValue("P_IBAN_NO", franchiseRequestInput.getIbanNo());
      input.addValue("P_ROUTING_NO", franchiseRequestInput.getRoutingNo());
      input.addValue("P_BRANCH_ADDRESS", franchiseRequestInput.getBranchAddress());
      input.addValue("P_SWIFT_CODE", franchiseRequestInput.getSwiftCode());
      input.addValue("P_AFFILIATE_BY", franchiseRequestInput.getAffiliateBy());
      input.addValue("P_BROKER_ID", null);
      Map<String, Object> result = simpleJdbcCall.execute((SqlParameterSource)input);
      String franchiseCode = (String)result.get("GET_FRANCHISE_CODE");
      franchise.setFranchiseCode(franchiseCode);
      this.error.setError_data(0);
      this.error.setError_msg("");
      this.adminUsersResponse.setFranchiseResult(franchise);
    } catch (Exception e) {
      e.printStackTrace();
      this.error.setError_data(1);
      this.error.setError_msg(e.getMessage());
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /franchiseRegistration\nClass: AdminDao\nMethod: franchiseRegistration" + 
            
            System.lineSeparator() + errors.toString());
      } 
    } 
    this.adminUsersResponse.setError(this.error);
    return this.adminUsersResponse;
  }
  
  public AdminUsersResponse getAllFranchise() {
    this.adminUsersResponse = new AdminUsersResponse();
    this.error = new ErrorResponse();
    List<Franchise> franchiseList = new ArrayList<>();
    String query = "select id,franchise_code,company_name,company_nick_name,is_affiliated from franchises where is_affiliated = 0 and is_active = 1 ";
    try {
      franchiseList = this.jdbcTemplate.query(query, (RowMapper)new BeanPropertyRowMapper(Franchise.class));
      this.error.setError_data(0);
      this.error.setError_msg("");
      this.adminUsersResponse.setFranchiseList(franchiseList);
    } catch (Exception e) {
      log.error("error in getAllFranchise: ", e);
      this.error.setError_data(1);
      this.error.setError_msg(e.getMessage());
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /getAllFranchise\nClass: AdminDao\nMethod: getAllFranchise" + 
            System.lineSeparator() + errors.toString());
      } 
    } 
    this.adminUsersResponse.setError(this.error);
    return this.adminUsersResponse;
  }
  
  public AdminUsersResponse franchiseById(FranchiseRequestInput franchiseRequestInput) {
    this.adminUsersResponse = new AdminUsersResponse();
    this.error = new ErrorResponse();
    Franchise franchise = new Franchise();
    FranchiseBankDetails franchiseBankDetails = new FranchiseBankDetails();
    String franchiseSql = "SELECT ID,BROKER_ID,FRANCHISE_CODE,COMPANY_NAME,COMPANY_NICK_NAME,EMAIL,REGISTRATION_ID,ADDRESS_LINE_1,ADDRESS_LINE_2,CITY,COUNTRY,ZIP_CODE,PHONE,WEBSITE,AFFILIATE_COMMISSION,SUB_AFFILIATE_COMMISSION,AFFILIATE_BY,IS_AFFILIATED,AFFILIATE_LINK FROM FRANCHISES WHERE IS_ACTIVE=1 AND ID=?";
    String franchiseBankDetailsSql = "SELECT FBANK_ID,FRANCHISE_ID,ACCOUNT_HOLDER_NAME,BANK_NAME,ACCOUNT_NO,IFSC_NO,IBAN_NO,ROUTING_NO,BRANCH_ADDRESS,SWIFT_CODE FROM FRANCHISES_BANK_DETAILS WHERE IS_ACTIVE=1 AND FRANCHISE_ID=?";
    try {
      franchise = (Franchise)this.jdbcTemplate.queryForObject(franchiseSql, new Object[] { Integer.valueOf(franchiseRequestInput.getId()) }, (RowMapper)new BeanPropertyRowMapper(Franchise.class));
      franchiseBankDetails = (FranchiseBankDetails)this.jdbcTemplate.queryForObject(franchiseBankDetailsSql, new Object[] { Integer.valueOf(franchiseRequestInput.getId()) }, (RowMapper)new BeanPropertyRowMapper(FranchiseBankDetails.class));
      this.error.setError_data(0);
      this.error.setError_msg("");
      this.adminUsersResponse.setFranchiseResult(franchise);
      this.adminUsersResponse.setFranchiseBankDetails(franchiseBankDetails);
    } catch (EmptyResultDataAccessException e) {
      log.error("empty resultset error: ", (Throwable)e);
      this.error.setError_data(1);
      this.error.setError_msg("Invalid franchise id");
    } catch (Exception e) {
      e.printStackTrace();
      this.error.setError_data(1);
      this.error.setError_msg(e.getMessage());
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /franchiseById\nClass: AdminDao\nMethod: franchiseById" + 
            System.lineSeparator() + errors.toString());
      } 
    } 
    this.adminUsersResponse.setError(this.error);
    return this.adminUsersResponse;
  }
  
  public AdminUsersResponse affiliateFranchisesById(int id) {
    this.adminUsersResponse = new AdminUsersResponse();
    this.error = new ErrorResponse();
    List<Franchise> franchiseList = new ArrayList<>();
    String query = "select id,franchise_code,company_name,company_nick_name,is_affiliated from franchises where is_active = 1 and affiliate_by = ? ";
    try {
      franchiseList = this.jdbcTemplate.query(query, new Object[] { Integer.valueOf(id) }, (RowMapper)new BeanPropertyRowMapper(Franchise.class));
      this.error.setError_data(0);
      this.error.setError_msg("");
      this.adminUsersResponse.setFranchiseList(franchiseList);
    } catch (Exception e) {
      log.error("error in affiliateFranchisesById: ", e);
      this.error.setError_data(1);
      this.error.setError_msg(e.getMessage());
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /affiliateFranchisesById\nClass: AdminDao\nMethod: affiliateFranchisesById" + 
            
            System.lineSeparator() + errors.toString());
      } 
    } 
    this.adminUsersResponse.setError(this.error);
    return this.adminUsersResponse;
  }
  
  public AdminUsersResponse updateFranchise(FranchiseRequestInput franchiseRequestInput) {
    this.adminUsersResponse = new AdminUsersResponse();
    this.error = new ErrorResponse();
    try {
      SimpleJdbcCall simpleJdbcCall = (new SimpleJdbcCall(this.jdbcTemplate)).withProcedureName("UPDATE_FRANCHISE_DETAILS_SP");
      MapSqlParameterSource input = new MapSqlParameterSource();
      input.addValue("P_FRANCHISES_ID", Integer.valueOf(franchiseRequestInput.getId()));
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
      input.addValue("P_AFFILIATE_COMMISSION", Double.valueOf(franchiseRequestInput.getAffiliateCommission()));
      input.addValue("P_SUB_AFFILIATE_COMMISSION", Double.valueOf(franchiseRequestInput.getSubAffiliateCommission()));
      input.addValue("P_HOLDER_NAME", franchiseRequestInput.getAccountHolderName());
      input.addValue("P_BANK_NAME", franchiseRequestInput.getBankName());
      input.addValue("P_ACCOUNT_NO", franchiseRequestInput.getAccountNo());
      input.addValue("P_IFSC_NO", franchiseRequestInput.getIfscNo());
      input.addValue("P_IBAN_NO", franchiseRequestInput.getIbanNo());
      input.addValue("P_ROUTING_NO", franchiseRequestInput.getRoutingNo());
      input.addValue("P_BRANCH_ADDRESS", franchiseRequestInput.getBranchAddress());
      input.addValue("P_SWIFT_CODE", franchiseRequestInput.getSwiftCode());
      Map<String, Object> result = simpleJdbcCall.execute((SqlParameterSource)input);
      int returnId = ((BigDecimal)result.get("RETURN_ID")).intValue();
      String message = (String)result.get("MESSAGE");
      if (returnId == 1) {
        this.error.setError_data(0);
        this.error.setError_msg("");
      } else {
        this.error.setError_data(1);
        this.error.setError_msg(message);
      } 
    } catch (Exception e) {
      this.adminUsersResponse = new AdminUsersResponse();
      this.error = new ErrorResponse();
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /updateFranchise\nClass: AdminDao\nMethod: updateFranchise" + 
            System.lineSeparator() + errors.toString());
      } 
    } 
    this.adminUsersResponse.setError(this.error);
    return this.adminUsersResponse;
  }
  
  public Map<String, Object> earningByFranchiseUsers(FranchiseRequestInput franchiseRequestInput) {
    Map<String, Object> response = new HashMap<>();
    this.error = new ErrorResponse();
    try {
      SimpleJdbcCall simpleJdbcCall = (new SimpleJdbcCall(this.jdbcTemplate)).withProcedureName("GET_FRANCHISE_EARNING_USERWISE");
      MapSqlParameterSource input = new MapSqlParameterSource();
      input.addValue("P_FRANCHISE_ID", Integer.valueOf(franchiseRequestInput.getId()));
      Map<String, Object> result = simpleJdbcCall.execute((SqlParameterSource)input);
      List<Map<String, Object>> returnResult = (List<Map<String, Object>>)result.get("EARNING_USERWISE_DETAILS");
      response.put("response", returnResult);
      this.error.setError_data(0);
      this.error.setError_msg("");
    } catch (Exception e) {
      e.printStackTrace();
      this.error.setError_data(1);
      this.error.setError_msg(e.getMessage());
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /earningByFranchiseUsers\nClass: AdminDao\nMethod: earningByFranchiseUsers" + 
            
            System.lineSeparator() + errors.toString());
      } 
    } 
    response.put("error", this.error);
    return response;
  }
  
  public Map<String, Object> totalEarningByDateRange(FranchiseRequestInput franchiseRequestInput) {
    Map<String, Object> response = new HashMap<>();
    this.error = new ErrorResponse();
    try {
      SimpleJdbcCall simpleJdbcCall = (new SimpleJdbcCall(this.jdbcTemplate)).withProcedureName("GET_FRANCHISE_EARNING_SUMMARY");
      MapSqlParameterSource input = new MapSqlParameterSource();
      input.addValue("P_FRANCHISE_ID", Integer.valueOf(franchiseRequestInput.getId()));
      input.addValue("P_START_DATE", franchiseRequestInput.getFromDate());
      input.addValue("P_END_DATE", franchiseRequestInput.getToDate());
      Map<String, Object> result = simpleJdbcCall.execute((SqlParameterSource)input);
      List<Object> returnResult = (List<Object>)result.get("GET_EARNING_SUMMARY");
      response.put("response", returnResult.get(0));
      this.error.setError_data(0);
      this.error.setError_msg("");
    } catch (Exception e) {
      e.printStackTrace();
      this.error.setError_data(1);
      this.error.setError_msg(e.getMessage());
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /totalEarningByDateRange\nClass: AdminDao\nMethod: totalEarningByDateRange" + 
            
            System.lineSeparator() + errors.toString());
      } 
    } 
    response.put("error", this.error);
    return response;
  }
  
  public Map<String, Object> dayWiseEarning(FranchiseRequestInput franchiseRequestInput) {
    Map<String, Object> response = new HashMap<>();
    this.error = new ErrorResponse();
    try {
      SimpleJdbcCall simpleJdbcCall = (new SimpleJdbcCall(this.jdbcTemplate)).withProcedureName("GET_FRANCHISE_EARNING_DETAILS");
      MapSqlParameterSource input = new MapSqlParameterSource();
      input.addValue("P_FRANCHISE_ID", Integer.valueOf(franchiseRequestInput.getId()));
      input.addValue("P_START_DATE", franchiseRequestInput.getFromDate());
      input.addValue("P_END_DATE", franchiseRequestInput.getToDate());
      Map<String, Object> result = simpleJdbcCall.execute((SqlParameterSource)input);
      List<Object> returnResult = (List<Object>)result.get("GET_EARNING_DETAILS");
      response.put("response", returnResult);
      this.error.setError_data(0);
      this.error.setError_msg("");
    } catch (Exception e) {
      e.printStackTrace();
      this.error.setError_data(1);
      this.error.setError_msg(e.getMessage());
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /dayWiseEarning\nClass: AdminDao\nMethod: dayWiseEarning" + 
            System.lineSeparator() + errors.toString());
      } 
    } 
    response.put("error", this.error);
    return response;
  }
  
  public Map<String, Object> allReferralUser() {
    Map<String, Object> response = new HashMap<>();
    this.error = new ErrorResponse();
    try {
      SimpleJdbcCall simpleJdbcCall = (new SimpleJdbcCall(this.jdbcTemplate)).withProcedureName("GET_ALL_REFERRED_USERS");
      Map<String, Object> result = simpleJdbcCall.execute(new Object[0]);
      List<Map<String, Object>> returnResult = (List<Map<String, Object>>)result.get("GET_REFERRED_USERS_DETAILS");
      response.put("response", returnResult);
      this.error.setError_data(0);
      this.error.setError_msg("");
    } catch (Exception e) {
      e.printStackTrace();
      this.error.setError_data(1);
      this.error.setError_msg(e.getMessage());
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /allReferralUser\nClass: AdminDao\nMethod: allReferralUser" + 
            System.lineSeparator() + errors.toString());
      } 
    } 
    response.put("error", this.error);
    return response;
  }
  
  public Map<String, Object> allUsersApiKeyDetails(int pageNo, int noOfRows, String searchString) {
    Map<String, Object> response = new HashMap<>();
    List<ApiKeyDetails> apiKeyDetailsList = new ArrayList<>();
    try {
      SimpleJdbcCall simpleJdbcCall = (new SimpleJdbcCall(this.jdbcTemplate)).withProcedureName("GET_USERS_API_DETAILS").returningResultSet("API_DETAILS", (RowMapper)BeanPropertyRowMapper.newInstance(ApiKeyDetails.class));
      MapSqlParameterSource input = (new MapSqlParameterSource()).addValue("P_PAGE_NO", Integer.valueOf(pageNo)).addValue("P_TOTAL_ROW_PER_PAGE", Integer.valueOf(noOfRows)).addValue("P_SEARCH_STRING", searchString);
      Map<String, Object> result = simpleJdbcCall.execute((SqlParameterSource)input);
      apiKeyDetailsList = (List<ApiKeyDetails>)result.get("ALL_API_DETAILS");
      int totalRow = ((BigDecimal)result.get("TOTAL_ROW")).intValue();
      response.put("apiKeyDetailsList", apiKeyDetailsList);
      response.put("totalRow", Integer.valueOf(totalRow));
      response.put("error", new ErrorResponse());
    } catch (Exception e) {
      log.error("Error in getUserWiseApiKeyDetails : ", e);
      response.put("error", new ErrorResponse(21));
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /allUsersApiKeyDetails\nClass: AdminDao\nMethod: allUsersApiKeyDetails" + 
            
            System.lineSeparator() + errors.toString());
      } 
    } 
    return response;
  }
  
  public Map<String, Object> futuresAllAssetPair() {
    Map<String, Object> response = new HashMap<>();
    String sql = "select BASE_CURRENCY_ID,ASSET_PAIR,ASSET_CODE from currency_master_mapping where asset_pair_type=2 and is_expire=0 and is_Active=1";
    try {
      List<ContractDetails> contractDetails = this.jdbcTemplate.query(sql, (RowMapper)new BeanPropertyRowMapper(ContractDetails.class));
      response.put("contractDetails", contractDetails);
      response.put("error", Integer.valueOf(0));
      response.put("message", "Success.");
    } catch (Exception e) {
      e.printStackTrace();
      response.put("error", new ErrorResponse(11));
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /futuresAllAssetPair\nClass: AdminDao\nMethod: futuresAllAssetPair" + 
            
            System.lineSeparator() + errors.toString());
      } 
    } 
    return response;
  }
  
  public Map<String, Object> changeBBookStatus(Users users, HttpServletRequest request) {
    Map<String, Object> response = new HashMap<>();
    int i = 0;
    try {
      Map<String, Object> result = this.utility.accessCheck(users.getAdminUserId(), 218, request.getRemoteAddr());
      int returnId = ((BigDecimal)result.get("R_RETURN_ID")).intValue();
      String message = (String)result.get("R_MESSAGE");
      if (returnId == 1) {
        if (users.getUser_id() > 0) {
          String query = "update users set b_book = ?,b_book_futures = ? where user_id = ? ";
          i = this.jdbcTemplate.update(query, new Object[] { Integer.valueOf(users.getbBook()), Integer.valueOf(users.getbBookFutures()), Integer.valueOf(users.getUser_id()) });
          if (i > 0) {
            this.error = new ErrorResponse();
          } else {
            this.error = new ErrorResponse(1, "updation failed.");
          } 
        } else {
          this.error = new ErrorResponse(1);
        } 
      } else {
        this.error = new ErrorResponse(1, message);
      } 
    } catch (Exception e) {
      e.printStackTrace();
      this.error = new ErrorResponse(1, e.getMessage());
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /changeBBookStatus\nClass: AdminDao\nMethod: changeBBookStatus" + 
            System.lineSeparator() + errors.toString());
      } 
    } 
    response.put("error", this.error);
    return response;
  }
  
  public Map<String, Object> userOfferDelete(UserTransactions userTransactions) {
    Map<String, Object> response = new HashMap<>();
    int status = 1;
    String message = "";
    String url = this.env.getProperty("project.spot.trade.api.url") + "ManageOffer";
    try {
      if (userTransactions.getCustomerId() > 0 && StringUtils.hasText(userTransactions.getOfferId()) && 
        StringUtils.hasText(userTransactions.getBaseCurrency()) && 
        StringUtils.hasText(userTransactions.getCurrency()) && 
        StringUtils.hasText(userTransactions.getOfferPrice()) && 
        StringUtils.hasText(userTransactions.getOfferQty()) && userTransactions.getTxnType() > 0) {
        String buyAsset, sellAsset;
        if (userTransactions.getTxnType() == 1) {
          buyAsset = userTransactions.getCurrency();
          sellAsset = userTransactions.getBaseCurrency();
        } else {
          buyAsset = userTransactions.getBaseCurrency();
          sellAsset = userTransactions.getCurrency();
        } 
        JSONObject requestJson = new JSONObject();
        requestJson.put("userId", userTransactions.getCustomerId());
        requestJson.put("offer_id", userTransactions.getOfferId());
        requestJson.put("selling_asset_code", sellAsset);
        requestJson.put("buying_asset_code", buyAsset);
        requestJson.put("price", userTransactions.getOfferPrice());
        requestJson.put("amount", userTransactions.getOfferQty());
        requestJson.put("txn_type", userTransactions.getTxnType());
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity(requestJson.toString(), (MultiValueMap)headers);
        String output = (String)restTemplate.postForObject(url, entity, String.class, new Object[0]);
        JSONObject jsonObj = new JSONObject(output);
        JSONObject errcode = new JSONObject(jsonObj.get("error").toString());
        status = errcode.getInt("error_data");
        message = errcode.getString("error_msg");
      } else {
        message = "Mandatory fields missing.";
      } 
    } catch (Exception e) {
      e.printStackTrace();
      message = e.getMessage();
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /userOfferDelete\nClass: AdminDao\nMethod: userOfferDelete" + 
            System.lineSeparator() + errors.toString());
      } 
    } 
    response.put("status", Integer.valueOf(status));
    response.put("message", message);
    return response;
  }
  
  public Map<String, Object> userFuturesOfferDelete(UserTransactions userTransactions) {
    Map<String, Object> response = new HashMap<>();
    int status = 1;
    String message = "";
    String url = this.env.getProperty("project.futures.trade.api.url") + "ManageOffer";
    try {
      if (userTransactions.getCustomerId() > 0 && StringUtils.hasText(userTransactions.getOfferId()) && 
        StringUtils.hasText(userTransactions.getBaseCurrency()) && 
        StringUtils.hasText(userTransactions.getCurrency()) && 
        StringUtils.hasText(userTransactions.getOfferPrice()) && 
        StringUtils.hasText(userTransactions.getOfferQty()) && userTransactions.getTxnType() > 0) {
        String buyAsset, sellAsset;
        if (userTransactions.getTxnType() == 1) {
          buyAsset = userTransactions.getCurrency();
          sellAsset = userTransactions.getBaseCurrency();
        } else {
          buyAsset = userTransactions.getBaseCurrency();
          sellAsset = userTransactions.getCurrency();
        } 
        JSONObject requestJson = new JSONObject();
        requestJson.put("userId", userTransactions.getCustomerId());
        requestJson.put("offer_id", userTransactions.getOfferId());
        requestJson.put("selling_asset_code", sellAsset);
        requestJson.put("buying_asset_code", buyAsset);
        requestJson.put("price", userTransactions.getOfferPrice());
        requestJson.put("amount", userTransactions.getOfferQty());
        requestJson.put("txn_type", userTransactions.getTxnType());
        requestJson.put("assetPair", userTransactions.getAssetPair());
        requestJson.put("leverage", userTransactions.getLeverage());
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity(requestJson.toString(), (MultiValueMap)headers);
        String output = (String)restTemplate.postForObject(url, entity, String.class, new Object[0]);
        JSONObject jsonObj = new JSONObject(output);
        JSONObject errcode = new JSONObject(jsonObj.get("error").toString());
        status = errcode.getInt("error_data");
        message = errcode.getString("error_msg");
      } else {
        message = "Mandatory fields missing.";
      } 
    } catch (Exception e) {
      e.printStackTrace();
      message = e.getMessage();
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /userFuturesOfferDelete\nClass: AdminDao\nMethod: userFuturesOfferDelete" + 
            
            System.lineSeparator() + errors.toString());
      } 
    } 
    response.put("status", Integer.valueOf(status));
    response.put("message", message);
    return response;
  }
  
  public Map<String, Object> tradingVolumeWiseCommission(String brokerId) {
    Map<String, Object> response = new HashMap<>();
    List<TradingVolumeWiseAction> tradingCommissionList = new ArrayList<>();
    String query = "select * from volume_wise_commission_fees where broker_id = ? order by id";
    try {
      if (StringUtils.hasText(brokerId)) {
        tradingCommissionList = this.jdbcTemplate.query(query, new Object[] { brokerId }, (RowMapper)new BeanPropertyRowMapper(TradingVolumeWiseAction.class));
        response.put("value", tradingCommissionList);
        this.error = new ErrorResponse();
      } else {
        this.error = new ErrorResponse(1);
      } 
    } catch (EmptyResultDataAccessException e) {
      log.error("empty resultset error: ", (Throwable)e);
      this.error = new ErrorResponse(11);
    } catch (Exception e) {
      e.printStackTrace();
      log.error(e.getMessage());
      this.error = new ErrorResponse(11);
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /tradingVolumeWiseCommission\nClass: AdminDao\nMethod: tradingVolumeWiseCommission" + 
            
            System.lineSeparator() + errors.toString());
      } 
    } 
    response.put("error", this.error);
    return response;
  }
  
  public AdminUsersResponse updateTradingVolumeWiseCommission(TradingVolumeWiseAction tradingVolumeWiseAction, HttpServletRequest request) {
    this.adminUsersResponse = new AdminUsersResponse();
    this.error = new ErrorResponse();
    try {
      Map<String, Object> result = this.utility.accessCheck(tradingVolumeWiseAction.getAdminUserId(), 304, request
          .getRemoteAddr());
      int returnId = ((BigDecimal)result.get("R_RETURN_ID")).intValue();
      String message = (String)result.get("R_MESSAGE");
      if (returnId == 1) {
        SimpleJdbcCall simpleJdbcCall = (new SimpleJdbcCall(this.jdbcTemplate)).withProcedureName("UPDATE_TRADING_COMMISSION");
        MapSqlParameterSource input = new MapSqlParameterSource();
        input.addValue("P_ID", Integer.valueOf(tradingVolumeWiseAction.getId()));
        input.addValue("P_VOLUME_FROM", Double.valueOf(tradingVolumeWiseAction.getVolumeFrom()));
        input.addValue("P_VOLUME_TO", Double.valueOf(tradingVolumeWiseAction.getVolumeTo()));
        input.addValue("P_COMMISSION_FEE", Double.valueOf(tradingVolumeWiseAction.getCommissionFee()));
        input.addValue("P_BROKER_ID", tradingVolumeWiseAction.getBrokerId());
        result = simpleJdbcCall.execute((SqlParameterSource)input);
        message = (String)result.get("MESSAGE");
        returnId = ((BigDecimal)result.get("RETURN_ID")).intValue();
        if (returnId == 1) {
          this.error.setError_data(0);
          this.error.setError_msg(message);
        } else {
          this.error.setError_data(1);
          this.error.setError_msg(message);
        } 
      } else {
        this.error.setError_data(1);
        this.error.setError_msg(message);
      } 
    } catch (Exception e) {
      e.printStackTrace();
      this.error.setError_data(1);
      this.error.setError_msg(e.getMessage());
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /updateTradingVolumeWiseCommission\nClass: AdminDao\nMethod: updateTradingVolumeWiseCommission" + 
            
            System.lineSeparator() + errors.toString());
      } 
    } 
    this.adminUsersResponse.setError(this.error);
    return this.adminUsersResponse;
  }
  
  public Map<String, Object> tradingVolumeWiseDeposit() {
    Map<String, Object> response = new HashMap<>();
    List<TradingVolumeWiseAction> tradingCommissionList = new ArrayList<>();
    String query = "select * from COLLATERAL_DEPOSIT_MASTER";
    try {
      tradingCommissionList = this.jdbcTemplate.query(query, (RowMapper)new BeanPropertyRowMapper(TradingVolumeWiseAction.class));
      response.put("value", tradingCommissionList);
      this.error = new ErrorResponse();
    } catch (EmptyResultDataAccessException e) {
      log.error("empty resultset error: ", (Throwable)e);
      this.error = new ErrorResponse(11);
    } catch (Exception e) {
      e.printStackTrace();
      log.error(e.getMessage());
      this.error = new ErrorResponse(11);
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /tradingVolumeWiseDeposit\nClass: AdminDao\nMethod: tradingVolumeWiseDeposit" + 
            
            System.lineSeparator() + errors.toString());
      } 
    } 
    response.put("error", this.error);
    return response;
  }
  
  public Map<String, Object> updateTradingVolumeWiseDeposit(TradingVolumeWiseAction tradingVolumeWiseAction, HttpServletRequest request) {
    Map<String, Object> response = new HashMap<>();
    int i = 0;
    try {
      if (tradingVolumeWiseAction.getId() > 0) {
        Map<String, Object> result = this.utility.accessCheck(tradingVolumeWiseAction.getAdminUserId(), 304, request
            .getRemoteAddr());
        int returnId = ((BigDecimal)result.get("R_RETURN_ID")).intValue();
        String message = (String)result.get("R_MESSAGE");
        if (returnId == 1) {
          String query = "UPDATE COLLATERAL_DEPOSIT_MASTER SET VOLUME_FROM = ?,VOLUME_TO = ?,DEPOSIT_REQUIREMENT = ? WHERE ID = ? ";
          i = this.jdbcTemplate.update(query, new Object[] { Double.valueOf(tradingVolumeWiseAction.getVolumeFrom()), 
                Double.valueOf(tradingVolumeWiseAction.getVolumeTo()), tradingVolumeWiseAction.getDepositRequirement(), 
                Integer.valueOf(tradingVolumeWiseAction.getId()) });
          if (i > 0) {
            this.error = new ErrorResponse();
          } else {
            this.error = new ErrorResponse(1, "updation failed.");
          } 
        } else {
          this.error = new ErrorResponse(1, message);
        } 
      } else {
        this.error = new ErrorResponse(1);
      } 
    } catch (Exception e) {
      e.printStackTrace();
      this.error = new ErrorResponse(1, e.getMessage());
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /updateTradingVolumeWiseDeposit\nClass: AdminDao\nMethod: updateTradingVolumeWiseDeposit" + 
            
            System.lineSeparator() + errors.toString());
      } 
    } 
    response.put("error", this.error);
    return response;
  }
  
  public Map<String, Object> getBrokersKycCharge() {
    Map<String, Object> response = new HashMap<>();
    int statusCode = 0;
    String message = "";
    try {
      SimpleJdbcCall simpleJdbcCall = (new SimpleJdbcCall(this.jdbcTemplate)).withProcedureName("BROKER_KYC_CHARGE_DETAILS").returningResultSet("P_RESULT", (RowMapper)BeanPropertyRowMapper.newInstance(BrokerKycCharge.class));
      MapSqlParameterSource input = (new MapSqlParameterSource()).addValue("P_BROKER_ID", null);
      Map<String, Object> officeInfo = simpleJdbcCall.execute((SqlParameterSource)input);
      response.put("value", officeInfo.get("P_RESULT"));
    } catch (Exception e) {
      e.printStackTrace();
      statusCode = 1;
      message = e.getMessage();
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /getBrokersKycCharge\nClass: AdminDao\nMethod: getBrokersKycCharge" + 
            
            System.lineSeparator() + errors.toString());
      } 
    } 
    response.put("code", Integer.valueOf(statusCode));
    response.put("message", message);
    return response;
  }
  
  public Map<String, Object> updateBrokerKycCharge(BrokerKycCharge brokerKycCharge, HttpServletRequest request) {
    Map<String, Object> response = new HashMap<>();
    int statusCode = 0;
    String message = "";
    String query = "update broker_kyc_charge set tier2_kyc_charge = ?,tier3_kyc_charge = ?,paid_amount = ? where id = ? ";
    try {
      if (brokerKycCharge.getId() > 0) {
        Map<String, Object> result = this.utility.accessCheck(brokerKycCharge.getAdminUserId(), 388, request
            .getRemoteAddr());
        int returnId = ((BigDecimal)result.get("R_RETURN_ID")).intValue();
        message = (String)result.get("R_MESSAGE");
        if (returnId == 1) {
          int i = this.jdbcTemplate.update(query, new Object[] { Double.valueOf(brokerKycCharge.getTier2KycCharge()), 
                Double.valueOf(brokerKycCharge.getTier3KycCharge()), Double.valueOf(brokerKycCharge.getPaidAmount()), 
                Integer.valueOf(brokerKycCharge.getId()) });
          if (i > 0) {
            message = "Successfully updated.";
          } else {
            statusCode = 1;
            message = "Updation faild.";
          } 
        } else {
          statusCode = 1;
        } 
      } else {
        statusCode = 1;
        message = "Mandatory fields missing.";
      } 
    } catch (Exception e) {
      e.printStackTrace();
      statusCode = 1;
      message = e.getMessage();
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /updateBrokerKycCharge\nClass: AdminDao\nMethod: updateBrokerKycCharge" + 
            
            System.lineSeparator() + errors.toString());
      } 
    } 
    response.put("code", Integer.valueOf(statusCode));
    response.put("message", message);
    return response;
  }
  
  public Map<String, Object> getBrokersExchangeApp() {
    Map<String, Object> response = new HashMap<>();
    int statusCode = 0;
    String message = "";
    List<BrokerExchangeApp> brokerExchangeApp = new ArrayList<>();
    String query = "select * from broker_exchange_app";
    try {
      brokerExchangeApp = this.jdbcTemplate.query(query, (RowMapper)new BeanPropertyRowMapper(BrokerExchangeApp.class));
      response.put("value", brokerExchangeApp);
    } catch (Exception e) {
      e.printStackTrace();
      statusCode = 1;
      message = e.getMessage();
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /getBrokersExchangeApp\nClass: AdminDao\nMethod: getBrokersExchangeApp" + 
            
            System.lineSeparator() + errors.toString());
      } 
    } 
    response.put("code", Integer.valueOf(statusCode));
    response.put("message", message);
    return response;
  }
  
  public Map<String, Object> updateBrokersExchangeApp(BrokerExchangeApp brokerExchangeApp, HttpServletRequest request) {
    Map<String, Object> response = new HashMap<>();
    int statusCode = 0;
    String message = "";
    String query = "update broker_exchange_app set link = ? where id = ? ";
    try {
      if (brokerExchangeApp.getId() > 0) {
        Map<String, Object> result = this.utility.accessCheck(brokerExchangeApp.getAdminUserId(), 389, request
            .getRemoteAddr());
        int returnId = ((BigDecimal)result.get("R_RETURN_ID")).intValue();
        message = (String)result.get("R_MESSAGE");
        if (returnId == 1) {
          int i = this.jdbcTemplate.update(query, new Object[] { brokerExchangeApp.getLink(), Integer.valueOf(brokerExchangeApp.getId()) });
          if (i > 0) {
            message = "Successfully updated.";
          } else {
            statusCode = 1;
            message = "Updation faild.";
          } 
        } else {
          statusCode = 1;
        } 
      } else {
        statusCode = 1;
        message = "Mandatory fields missing.";
      } 
    } catch (Exception e) {
      e.printStackTrace();
      statusCode = 1;
      message = e.getMessage();
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /updateBrokersExchangeApp\nClass: AdminDao\nMethod: updateBrokersExchangeApp" + 
            
            System.lineSeparator() + errors.toString());
      } 
    } 
    response.put("code", Integer.valueOf(statusCode));
    response.put("message", message);
    return response;
  }
  
  public Map<String, Object> updateAllBrokersExchangeApp(BrokerExchangeApp brokerExchangeApp, HttpServletRequest request) {
    Map<String, Object> response = new HashMap<>();
    int statusCode = 0;
    String message = "";
    String query = "update broker_exchange_app set link = ?, app_version = ?, version_updated_on = current_timestamp  ";
    try {
      if (StringUtils.hasText(brokerExchangeApp.getLink()) && 
        StringUtils.hasText(brokerExchangeApp.getAppVersion()) && brokerExchangeApp
        .getAdminUserId() > 0) {
        Map<String, Object> result = this.utility.accessCheck(brokerExchangeApp.getAdminUserId(), 389, request
            .getRemoteAddr());
        int returnId = ((BigDecimal)result.get("R_RETURN_ID")).intValue();
        message = (String)result.get("R_MESSAGE");
        if (returnId == 1) {
          int i = this.jdbcTemplate.update(query, new Object[] { brokerExchangeApp.getLink(), brokerExchangeApp.getAppVersion() });
          if (i > 0) {
            message = "Successfully updated.";
          } else {
            statusCode = 1;
            message = "Updation faild.";
          } 
        } else {
          statusCode = 1;
        } 
      } else {
        statusCode = 1;
        message = "Mandatory fields missing.";
      } 
    } catch (Exception e) {
      e.printStackTrace();
      statusCode = 1;
      message = e.getMessage();
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /updateAllBrokersExchangeApp\nClass: AdminDao\nMethod: updateAllBrokersExchangeApp" + 
            
            System.lineSeparator() + errors.toString());
      } 
    } 
    response.put("code", Integer.valueOf(statusCode));
    response.put("message", message);
    return response;
  }
  
  public Map<String, Object> currentAppVersionAndLink() {
    Map<String, Object> response = new HashMap<>();
    String query = "select * from broker_exchange_app where broker_id = 'PAYB18022021121103' ";
    try {
      BrokerExchangeApp brokerExchangeApp = (BrokerExchangeApp)this.jdbcTemplate.queryForObject(query, (RowMapper)new BeanPropertyRowMapper(BrokerExchangeApp.class));
      response.put("value", brokerExchangeApp);
      this.error = new ErrorResponse();
    } catch (Exception e) {
      e.printStackTrace();
      this.error = new ErrorResponse(11);
      if (flag) {
        flag = false;
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errors.toString();
        pubTopic("Project: AdminService\nApi: /currentAppVersionAndLink\nClass: AdminDao\nMethod: currentAppVersionAndLink" + 
            
            System.lineSeparator() + errors.toString());
      } 
    } 
    response.put("error", this.error);
    return response;
  }
  
  public void pubTopic(String message) {
    try {
      System.out.println("Enter in");
      String arn = this.env.getProperty("arn");
      AmazonSNS snsClient = (AmazonSNS)((AmazonSNSClientBuilder)AmazonSNSClient.builder().withRegion(this.env.getProperty("regionarn"))).build();
      PublishResult res = snsClient.publish(arn, message);
      System.out.println("end............" + res.getMessageId());
    } catch (Exception e) {
      System.err.println(e.getMessage());
      System.exit(1);
    } 
  }
  
  @Scheduled(cron = "0 0 0/2 * * ?")
  public void exceptionScheduler() {
    flag = true;
  }
}
