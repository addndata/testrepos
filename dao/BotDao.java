package com.project.admin.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Component;

import com.project.admin.model.Asset;
import com.project.admin.model.BotAssetAccessControl;
import com.project.admin.model.BotControl;
import com.project.admin.model.BotResponse;
import com.project.admin.model.ErrorResponse;
import com.project.admin.model.RequestJson;
import com.project.admin.model.UserBalance;

@Component
public class BotDao {

	private static final Logger log = LoggerFactory.getLogger(BotDao.class);

	@Autowired
	private DataSource dataSource;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public final String GET_BOT_USERS = "select user_id from users where USER_TAG=2";
	
	/////// bot control queries
	public final String GET_BOT_TIME = "select scheduler_time from botcontrol";
	private final String UPDATE_SPRADE = "update botcontrol set ask_sprade = ?,bid_sprade = ? ";
	private final String GET_SPRADE =  "select ask_sprade,bid_sprade from botcontrol ";
	
	/////// bot asset buy, bot asset and bot asset access control queries 
	private final String GET_BOT_SELL_PRICE = "select base_asset,counter_asset,price_max,price_min from "
			+ " offer_asset where base_asset = ? and counter_asset = ?";
	
	private final String GET_BOT_BUY_PRICE = "select base_asset,counter_asset,price_max,price_min from "
			+ " offer_asset_buy where base_asset = ? and counter_asset = ?";

	private final String GET_BOT_SELL_MIN_MAX_AMOUNT = "select ask_max,ask_min from "
			+ " bot_asset_access_control where base_asset = ? and counter_asset = ? ";
	
	private final String GET_BOT_BUY_MIN_MAX_AMOUNT = "select bid_max,bid_min from "
			+ " bot_asset_access_control where base_asset = ? and counter_asset = ? ";
	
	private final String UPDATE_MIN_MAX = "update bot_asset_access_control set bid_min = ?,bid_max = ?,ask_min = ?,ask_max = ?,updated_on = current_timestamp "
			+ " where base_asset = ? and counter_asset = ? and asset_status = 1";
	private final String UPDATE_ASSET_STATUS = "update bot_asset_access_control set asset_status = ? "
			+ " where base_asset = ? and counter_asset = ? ";
	private final String CREATE_BOT_STATUS = "update bot_asset_access_control set bot_status = ?,bot_isactive = 1,created_on = current_timestamp  "
			+ " where base_asset = ? and counter_asset = ? and asset_status = 1";
	private final String DELETE_BOT_STATUS = "update bot_asset_access_control set bot_status = ?,bot_isactive = 0,updated_on = current_timestamp "
			+ " where base_asset = ? and counter_asset = ? and asset_status = 1";

	private final String UPDATE_BOT_ACTIVE_INACTIVE = "update bot_asset_access_control set bot_isactive = ?,updated_on = current_timestamp "
			+ " where base_asset = ? and counter_asset = ? and asset_status = 1 and bot_status = 1 ";
	private final String UPDATE_BOT_ASSET_SCHEDULER_TIME = "update bot_asset_access_control set scheduler_time = ?,updated_on = current_timestamp "
			+ " where base_asset = ? and counter_asset = ? and asset_status = 1 and bot_status = 1 ";
	private static final String GET_ALL_BOT_USERS = "select user_id,first_name,email from users where user_tag = 2";
	
	public String getSchedulerTime() {
		String time = "0";
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			pstmt = con.prepareStatement(GET_BOT_TIME);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				// time = rs.getString("scheduler_time");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (con != null) {
				try {
					pstmt.close();
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return time;
	}

	public Asset getAccountsForOfferSell(String baseAsset, String counterAsset) {
		Asset asset = new Asset();
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			pstmt = con.prepareStatement(GET_BOT_SELL_PRICE);
			pstmt.setString(1, baseAsset);
			pstmt.setString(2, counterAsset);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				asset.setBase_asset(rs.getString("base_asset"));
				asset.setCounter_asset(rs.getString("counter_asset"));
				asset.setMax_price(rs.getString("price_max")!=null?rs.getString("price_max"):"0");
				asset.setMin_price(rs.getString("price_min")!=null?rs.getString("price_min"):"0");
			}
			rs.close();
			pstmt.close();
			
			pstmt = con.prepareStatement(GET_BOT_SELL_MIN_MAX_AMOUNT);
			pstmt.setString(1, baseAsset);
			pstmt.setString(2, counterAsset);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				asset.setMax_amount(rs.getString("ask_max")!=null?rs.getString("ask_max"):"0");
				asset.setMin_amount(rs.getString("ask_min")!=null?rs.getString("ask_min"):"0");
			}
			rs.close();
			
		} catch (Exception e) {
			log.error("Error in get accoumts for sell: ", e);
		} finally {
			if (con != null) {
				try {
					pstmt.close();
					con.close();

				} catch (SQLException e) {
					log.error("Error in conn: ", e);
				}
			}
		}
		return asset;

	}

	public Asset getAccountsForOfferBuy(String baseAsset, String counterAsset) {
		Asset asset = new Asset();
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			pstmt = con.prepareStatement(GET_BOT_BUY_PRICE);
			pstmt.setString(1, baseAsset);
			pstmt.setString(2, counterAsset);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				asset.setBase_asset(rs.getString("base_asset"));
				asset.setCounter_asset(rs.getString("counter_asset"));
				asset.setMax_price(rs.getString("price_max")!=null?rs.getString("price_max"):"0");
				asset.setMin_price(rs.getString("price_min")!=null?rs.getString("price_min"):"0");
			}
			rs.close();
			pstmt.close();
			
			pstmt = con.prepareStatement(GET_BOT_BUY_MIN_MAX_AMOUNT);
			pstmt.setString(1, baseAsset);
			pstmt.setString(2, counterAsset);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				asset.setMax_amount(rs.getString("bid_max")!=null?rs.getString("bid_max"):"0");
				asset.setMin_amount(rs.getString("bid_min")!=null?rs.getString("bid_min"):"0");
			}
			rs.close();
		} catch (Exception e) {
			log.error("Error in get accoumts for buy: ", e);
		} finally {
			if (con != null) {
				try {
					pstmt.close();
					con.close();

				} catch (SQLException e) {
					log.error("Error in conn: ", e);
				}
			}
		}
		return asset;

	}

	public List<Integer> getBotUsers() {
		List<Integer> users = new ArrayList<>();
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			pstmt = con.prepareStatement(GET_BOT_USERS);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				users.add(rs.getInt("user_id"));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (con != null) {
				try {
					pstmt.close();
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return users;
	}

	public BotResponse updateSprade(RequestJson requestJson) {
		BotResponse botResponse = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			double askSprade = 1 + requestJson.getSpradeValue() / 100;
			double bidSprade = 0 + requestJson.getSpradeValue() / 100;

			con = dataSource.getConnection();
			pstmt = con.prepareStatement(UPDATE_SPRADE);
			pstmt.setDouble(1, askSprade);
			pstmt.setDouble(2, bidSprade);
			int status = pstmt.executeUpdate();

			if (status != 0) {
				botResponse = new BotResponse(new ErrorResponse());
			} else {
				botResponse = new BotResponse(new ErrorResponse(1, "Update failed."));
			}

		} catch (SQLException e) {
			e.printStackTrace();
			botResponse = new BotResponse(new ErrorResponse(9));
		} finally {
			if (con != null) {
				try {
					pstmt.close();
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return botResponse;
	}

	public BotResponse getSprade() {
		BotResponse botResponse = null;
		BotControl botControl = new BotControl();
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			pstmt = con.prepareStatement(GET_SPRADE);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				double spreadValue = Double.parseDouble(rs.getString("bid_sprade")) * 100;
				botControl.setSpradeValue(spreadValue);			
				botResponse = new BotResponse(new ErrorResponse());
				botResponse.setBotControl(botControl);
			} else {
				botResponse = new BotResponse(new ErrorResponse(1, "no data"));
			}

		} catch (SQLException e) {
			e.printStackTrace();
			botResponse = new BotResponse(new ErrorResponse(9));
		} finally {
			if (con != null) {
				try {
					pstmt.close();
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return botResponse;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public BotResponse getBotAssetList() {
		BotResponse botResponse = new BotResponse();
		List<BotAssetAccessControl> botAssetAccessControls = new ArrayList<>();
		try {
			// int startsFrom = (requestInput.getPageNo()-1)
			SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
					.withProcedureName("GET_BOTCONTROL_ASSET_DETAILS").returningResultSet("GET_BOT_DETAILS",
							BeanPropertyRowMapper.newInstance(BotAssetAccessControl.class));
			Map<String, Object> result = simpleJdbcCall.execute();
			botAssetAccessControls = (List) result.get("P_BOT_DETAILS");
			botResponse.setTotalCount(((BigDecimal) result.get("P_TOTAL_ROW")).intValue());
			
			if(botResponse.getTotalCount()!=0){
				botResponse.setBotAssetList(botAssetAccessControls);
				botResponse.setErrorResponse(new ErrorResponse());
			} else {
				botResponse.setErrorResponse(new ErrorResponse(0,"no data"));
			}	
			
		} catch (Exception e) {
			log.error("error in get user transaction db call: ", e);
			botResponse.setErrorResponse(new ErrorResponse(9));
		}

		return botResponse;
	}

	public BotResponse updateBotMinMax(RequestJson requestJson) {
		BotResponse botResponse = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			con = dataSource.getConnection();
			pstmt = con.prepareStatement(UPDATE_MIN_MAX);
			pstmt.setString(1, requestJson.getBidMin());
			pstmt.setString(2, requestJson.getBidMax());
			pstmt.setString(3, requestJson.getAskMin());
			pstmt.setString(4, requestJson.getAskMax());
			pstmt.setString(5, requestJson.getBaseAsset());
			pstmt.setString(6, requestJson.getCounterAsset());
			int status = pstmt.executeUpdate();

			if (status != 0) {
				botResponse = new BotResponse(new ErrorResponse());
			} else {
				botResponse = new BotResponse(new ErrorResponse(1, "Update failed."));
			}

		} catch (SQLException e) {
			e.printStackTrace();
			botResponse = new BotResponse(new ErrorResponse(9));
		} finally {
			if (con != null) {
				try {
					pstmt.close();
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return botResponse;
	}

	public BotResponse updateAssetStatus(int assetStatus, String base, String counter) {
		BotResponse botResponse = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			log.info("asset status: {}, base: {}, counter: {}", assetStatus, base, counter);
			con = dataSource.getConnection();
			pstmt = con.prepareStatement(UPDATE_ASSET_STATUS);
			pstmt.setInt(1, assetStatus);
			pstmt.setString(2, base);
			pstmt.setString(3, counter);
			int status = pstmt.executeUpdate();

			log.info("update status: {}", status);
			if (status != 0) {
				botResponse = new BotResponse(new ErrorResponse());
			} else {
				botResponse = new BotResponse(new ErrorResponse(1, "Update failed."));
			}

		} catch (SQLException e) {
			e.printStackTrace();
			botResponse = new BotResponse(new ErrorResponse(9));
		} finally {
			if (con != null) {
				try {
					pstmt.close();
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return botResponse;
	}

	public BotResponse updateBotStatus(int botStatus, String base, String counter) {
		BotResponse botResponse = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			log.info("bot status: {}, base: {}, counter: {}", botStatus, base, counter);
			con = dataSource.getConnection();
			if (botStatus == 1) {
				pstmt = con.prepareStatement(CREATE_BOT_STATUS);
			} else {
				pstmt = con.prepareStatement(DELETE_BOT_STATUS);
			}
			pstmt.setInt(1, botStatus);
			pstmt.setString(2, base);
			pstmt.setString(3, counter);
			int status = pstmt.executeUpdate();

			log.info("update status: {}", status);
			if (status != 0) {
				botResponse = new BotResponse(new ErrorResponse());
			} else {
				botResponse = new BotResponse(new ErrorResponse(1, "Update failed."));
			}

		} catch (SQLException e) {
			e.printStackTrace();
			botResponse = new BotResponse(new ErrorResponse(9));
		} finally {
			if (con != null) {
				try {
					pstmt.close();
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return botResponse;
	}

	public BotResponse updateBotActiveInactive(int botStatus, String base, String counter) {
		BotResponse botResponse = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			log.info("bot active inactive : {}, base: {}, counter: {}", botStatus, base, counter);
			con = dataSource.getConnection();
			pstmt = con.prepareStatement(UPDATE_BOT_ACTIVE_INACTIVE);
			pstmt.setInt(1, botStatus);
			pstmt.setString(2, base);
			pstmt.setString(3, counter);
			int status = pstmt.executeUpdate();

			log.info("update status: {}", status);
			if (status != 0) {
				botResponse = new BotResponse(new ErrorResponse());
			} else {
				botResponse = new BotResponse(new ErrorResponse(1, "Update failed."));
			}

		} catch (SQLException e) {
			e.printStackTrace();
			botResponse = new BotResponse(new ErrorResponse(9));
		} finally {
			if (con != null) {
				try {
					pstmt.close();
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return botResponse;
	}

	public BotResponse updateBotAssetSchedulerTime(String schedulerTime, String base, String counter) {
		BotResponse botResponse = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			log.info("schedulerTime: {}, base: {}, counter: {}", schedulerTime, base, counter);
			con = dataSource.getConnection();
			pstmt = con.prepareStatement(UPDATE_BOT_ASSET_SCHEDULER_TIME);
			pstmt.setString(1, schedulerTime);
			pstmt.setString(2, base);
			pstmt.setString(3, counter);
			int status = pstmt.executeUpdate();

			log.info("update status: {}", status);
			if (status != 0) {
				botResponse = new BotResponse(new ErrorResponse());
			} else {
				botResponse = new BotResponse(new ErrorResponse(1, "Update failed."));
			}

		} catch (SQLException e) {
			e.printStackTrace();
			botResponse = new BotResponse(new ErrorResponse(9));
		} finally {
			if (con != null) {
				try {
					pstmt.close();
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return botResponse;
	}
	
	public Map<String, Object> getAllBotUsers() {
		Map<String, Object> response = new HashMap<>();
		List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
		try {
			result = jdbcTemplate.queryForList(GET_ALL_BOT_USERS);
			if (!result.isEmpty()) {
				response.put("userResult", result);
				response.put("error", "");
			}
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map getUserBalance(UserBalance userBalance) {
		Map<String, Object> response = new HashMap<>();
		List<UserBalance> userBalanceList = new ArrayList<>();
		try {
			SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
					.withProcedureName("GET_CUSTOMER_WALLET_DETAILS")
					.returningResultSet("GET_WALLET_DETAILS", BeanPropertyRowMapper.newInstance(UserBalance.class));
			SqlParameterSource input = new MapSqlParameterSource().addValue("P_CUSTOMER_ID",
					userBalance.getCustomerId());
			Map<String, Object> result = simpleJdbcCall.execute(input);
			userBalanceList = (List) result.get("GET_WALLET_DETAILS");
			response.put("userBalance", userBalanceList);
			response.put("error", "");
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			response.put("error", e.getMessage());
		}
		return response;
	}

	public Map<String, Object> updateUserBalance(List<UserBalance> usersBalance) {
		Map<String, Object> response = new HashMap<>();
		List<Object[]> parameters = new ArrayList<Object[]>();
		try {
			String updateSql = "UPDATE CUSTOMER_LEDGER SET CURRENT_BALANCE=NVL(CURRENT_BALANCE,0)+ NVL(?,0) " 
					+" WHERE CUSTOMER_ID=? AND CURRENCY_ID=?";
			String insertSql = "insert into customer_ledger (customer_id,currency_id) values(?,?)";
			String query = "select customer_id from customer_ledger where customer_id = ? and currency_id = ? ";
			if (usersBalance.size() > 0) {
				for (UserBalance userBalance : usersBalance) {
					Object customerId = DataAccessUtils.singleResult(jdbcTemplate.query(query,
							new Object[] {userBalance.getCustomerId(),userBalance.getCurrencyId()}, new SingleColumnRowMapper<Integer>()));
					if (customerId == null) {
						jdbcTemplate.update(insertSql, userBalance.getCustomerId(),userBalance.getCurrencyId());
					}
					parameters.add(new Object[] {userBalance.getClosingBalance(),userBalance.getCustomerId(),
							userBalance.getCurrencyId()});
				}
				jdbcTemplate.batchUpdate(updateSql, parameters);
				response.put("error", "");
			} else {
				response.put("error", "List is empty.");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			response.put("error", e.getMessage());
		}
		return response;
	}
}
