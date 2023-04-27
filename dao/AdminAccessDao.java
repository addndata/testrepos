package com.project.admin.dao;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.sql.DataSource;

import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.project.admin.config.TOTP;
import com.project.admin.model.AdminResponse;
import com.project.admin.model.AdminUsers;
import com.project.admin.model.AdminUsersResponse;
import com.project.admin.model.ErrorResponse;
import com.project.admin.model.GroupMaster;
import com.project.admin.model.GroupMaster.MethodMaster;
import com.project.admin.model.GroupMaster.ModuleMaster;
import com.project.admin.model.GroupMaster.RoleMaster;
import com.project.admin.model.GroupMaster.RoleMethodRelation;
import com.project.admin.model.GroupMaster.RoleModuleRelation;
import com.project.admin.model.RequestJson;

import oracle.jdbc.internal.OracleTypes;

@Component
public class AdminAccessDao {

	private static final Logger log = LoggerFactory.getLogger(AdminAccessDao.class);
	private static String adminUserPassword = "";
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private DataSource dataSource;
	@Autowired
	MailContentBuilderService mailContentBuilderService;
	@Autowired
	MailClientService mailClientService;
	@Autowired
	NotificationService notificationService;
	@Autowired
	Environment env;

	/**
	 * Using this Procedure an Admin User who has got permission to create Group,
	 * can add new group. There is no need to confirm or activate this newly create
	 * group.
	 */
	public AdminResponse addAdminGroup(GroupMaster groupMaster) {

		Connection conn = null; // create connection instance to connect with
								// database
		CallableStatement callableStatement = null;
		AdminResponse adminResponse = new AdminResponse();
		ErrorResponse _error = new ErrorResponse();
		try {
			conn = dataSource.getConnection();

			String sql = "{call GROUP_ADD_PROC(?,?,?,?,?,?)}";
			callableStatement = conn.prepareCall(sql);
			callableStatement.setString(1, groupMaster.getGroup_name());
			callableStatement.setString(2, groupMaster.getGroup_desc());
			callableStatement.setString(3, groupMaster.getGroup_prefix());
			callableStatement.setInt(4, groupMaster.getCreated_by());
			callableStatement.registerOutParameter(5, Types.INTEGER);
			callableStatement.registerOutParameter(6, Types.VARCHAR);
			callableStatement.executeUpdate();

			if (callableStatement.getInt(5) == 1) {
				_error.setError_data(0);
				_error.setError_msg(callableStatement.getString(6));
			} else {
				_error.setError_data(1);
				_error.setError_msg(callableStatement.getString(6));
			}

			callableStatement.close();

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			_error.setError_data(1);
			_error.setError_msg(e.getMessage());

		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		adminResponse.setError(_error);
		return adminResponse;
	}

	/**
	 * Using this Procedure an Admin User can view all Group.
	 * 
	 * @param groupMaster
	 * @return
	 */
	public AdminResponse getAllGroups(GroupMaster groupMaster) {
		Connection conn = null; // create connection instance to connect with
								// database
		CallableStatement callableStatement = null;
		AdminResponse adminResponse = new AdminResponse();
		ErrorResponse _error = new ErrorResponse();
		GroupMaster _groupMaster = null;
		List<GroupMaster> groupMasters = new ArrayList<>();
		try {
			conn = dataSource.getConnection();

			String sql = "{call GROUP_GET_ALL(?,?,?,?)}";
			callableStatement = conn.prepareCall(sql);
			callableStatement.setInt(1, groupMaster.getCreated_by());
			callableStatement.registerOutParameter(2, Types.INTEGER);
			callableStatement.registerOutParameter(3, Types.VARCHAR);
			callableStatement.registerOutParameter(4, OracleTypes.CURSOR);
			callableStatement.execute();
			if (callableStatement.getInt(2) == 1) {
				ResultSet rSet = (ResultSet) callableStatement.getObject(4);
				while (rSet.next()) {
					_groupMaster = new GroupMaster();
					_groupMaster.setGroup_id(rSet.getInt("group_id"));
					_groupMaster.setGroup_name(rSet.getString("group_name"));
					_groupMaster.setGroup_desc(rSet.getString("group_desc"));
					_groupMaster.setGroup_prefix(rSet.getString("group_prefix"));
					_groupMaster.setGroup_status(rSet.getString("group_status"));
					groupMasters.add(_groupMaster);
				}

				if (!groupMasters.isEmpty()) {
					adminResponse.setGroupMasterList(groupMasters);

					_error.setError_data(0);
					_error.setError_msg(callableStatement.getString(3));
				} else {
					_error.setError_data(0);
					_error.setError_msg("no data");
				}
			} else {
				_error.setError_data(1);
				_error.setError_msg(callableStatement.getString(3));
			}
			callableStatement.close();

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			_error.setError_data(1);
			_error.setError_msg(e.getMessage());

		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {

					e.printStackTrace();
				}
			}
		}
		adminResponse.setError(_error);
		return adminResponse;
	}

	/**
	 * Using this Procedure an Admin User can add Role. Role must belongs to a
	 * Group. So when you add Role you must have to provide the Group under which
	 * the Role is created.
	 */
	public AdminResponse addAdminRole(RequestJson requestJson) {
		Connection conn = null; // create connection instance to connect with
								// database
		CallableStatement callableStatement = null;
		AdminResponse adminResponse = new AdminResponse();
		ErrorResponse _error = new ErrorResponse();
		try {
			conn = dataSource.getConnection();

			String sql = "{call ROLE_ADD_PROC(?,?,?,?,?,?,?)}";
			callableStatement = conn.prepareCall(sql);
			callableStatement.setInt(1, requestJson.getGroup_id());
			callableStatement.setString(2, requestJson.getRole_name());
			callableStatement.setString(3, requestJson.getRole_desc());
			callableStatement.setString(4, requestJson.getRole_prefix());
			callableStatement.setInt(5, requestJson.getCreated_by());
			callableStatement.registerOutParameter(6, Types.INTEGER);
			callableStatement.registerOutParameter(7, Types.VARCHAR);
			callableStatement.executeUpdate();

			if (callableStatement.getInt(6) == 1) {
				_error.setError_data(0);
				_error.setError_msg(callableStatement.getString(7));
			} else {
				_error.setError_data(1);
				_error.setError_msg(callableStatement.getString(7));
			}

			callableStatement.close();

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			_error.setError_data(1);
			_error.setError_msg(e.getMessage());

		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {

					e.printStackTrace();
				}
			}
		}
		adminResponse.setError(_error);
		return adminResponse;
	}

	/**
	 * Using this Procedure an Admin User can view all Role and the Group under
	 * which the Role belongs.
	 * 
	 * @param roleMaster
	 * @return
	 */
	public AdminResponse getAllRoles(RequestJson requestJson) {
		Connection conn = null; // create connection instance to connect with
								// database
		CallableStatement callableStatement = null;
		AdminResponse adminResponse = new AdminResponse();
		ErrorResponse _error = new ErrorResponse();
		RoleMaster _roleMaster = null;
		// GroupMaster groupMaster=null;
		List<RoleMaster> roleMasters = new ArrayList<>();
		try {
			conn = dataSource.getConnection();

			String sql = "{call ROLE_GET_ALL(?,?,?,?)}";
			callableStatement = conn.prepareCall(sql);
			callableStatement.setInt(1, requestJson.getCreated_by());
			callableStatement.registerOutParameter(2, Types.INTEGER);
			callableStatement.registerOutParameter(3, Types.VARCHAR);
			callableStatement.registerOutParameter(4, OracleTypes.CURSOR);
			callableStatement.execute();
			if (callableStatement.getInt(2) == 1) {
				ResultSet rSet = (ResultSet) callableStatement.getObject(4);
				while (rSet.next()) {
					_roleMaster = setRole(rSet);
					roleMasters.add(_roleMaster);
				}

				if (!roleMasters.isEmpty()) {
					adminResponse.setRoleMasterList(roleMasters);

					_error.setError_data(0);
					_error.setError_msg(callableStatement.getString(3));
				} else {
					_error.setError_data(0);
					_error.setError_msg("no data");
				}
			} else {
				_error.setError_data(1);
				_error.setError_msg(callableStatement.getString(3));
			}
			callableStatement.close();

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			_error.setError_data(1);
			_error.setError_msg(e.getMessage());

		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {

					e.printStackTrace();
				}
			}
		}
		adminResponse.setError(_error);
		return adminResponse;
	}

	/**
	 * Using this Procedure an Admin User can view all Role under a specific Group
	 * under which the Role belongs.
	 * 
	 * @param roleMaster
	 * @return
	 */
	public AdminResponse GetAllRolesByGroup(RequestJson requestJson) {
		Connection conn = null; // create connection instance to connect with
								// database
		CallableStatement callableStatement = null;
		AdminResponse adminResponse = new AdminResponse();
		ErrorResponse _error = new ErrorResponse();
		RoleMaster _roleMaster = null;
		// GroupMaster groupMaster=null;
		List<RoleMaster> roleMasters = new ArrayList<>();
		try {
			conn = dataSource.getConnection();

			String sql = "{call ROLE_GET_ALL_BY_GROUP(?,?,?,?,?)}";
			callableStatement = conn.prepareCall(sql);
			callableStatement.setInt(1, requestJson.getGroup_id());
			callableStatement.setInt(2, requestJson.getCreated_by());
			callableStatement.registerOutParameter(3, Types.INTEGER);
			callableStatement.registerOutParameter(4, Types.VARCHAR);
			callableStatement.registerOutParameter(5, OracleTypes.CURSOR);
			callableStatement.execute();
			if (callableStatement.getInt(3) == 1) {
				ResultSet rSet = (ResultSet) callableStatement.getObject(5);
				while (rSet.next()) {
					_roleMaster = setRole(rSet);
					roleMasters.add(_roleMaster);
				}

				if (!roleMasters.isEmpty()) {
					adminResponse.setRoleMasterList(roleMasters);

					_error.setError_data(0);
					_error.setError_msg(callableStatement.getString(4));
				} else {
					_error.setError_data(0);
					_error.setError_msg("no data");
				}
			} else {
				_error.setError_data(1);
				_error.setError_msg(callableStatement.getString(4));
			}
			callableStatement.close();

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			_error.setError_data(1);
			_error.setError_msg(e.getMessage());

		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {

					e.printStackTrace();
				}
			}
		}
		adminResponse.setError(_error);
		return adminResponse;
	}

	/**
	 * Using this Procedure an Admin User who has the permission can set Relation
	 * between a Role a various Module. Module means the Menu(URL). When an Admin
	 * User added he/she must be belongs to a Role. When an Admin User Login to the
	 * System then Which Menu will open for this User will be defined by the Role
	 * and Module Relation.
	 */
	public AdminResponse setRoleModuleRelation(RequestJson requestJson) {
		Connection conn = null; // create connection instance to connect with
		// database
		CallableStatement callableStatement = null;
		AdminResponse adminResponse = new AdminResponse();
		ErrorResponse _error = new ErrorResponse();
		try {
			conn = dataSource.getConnection();

			String sql = "{call ROLE_MODULE_RELATION_SET_PROC(?,?,?,?,?)}";
			callableStatement = conn.prepareCall(sql);
			callableStatement.setInt(1, requestJson.getRole_id());
			callableStatement.setInt(2, requestJson.getModule_id());
			callableStatement.setInt(3, requestJson.getCreated_by());
			callableStatement.registerOutParameter(4, Types.INTEGER);
			callableStatement.registerOutParameter(5, Types.VARCHAR);
			callableStatement.executeUpdate();

			if (callableStatement.getInt(4) == 1) {
				_error.setError_data(0);
				_error.setError_msg(callableStatement.getString(5));
			} else {
				_error.setError_data(1);
				_error.setError_msg(callableStatement.getString(5));
			}

			callableStatement.close();

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			_error.setError_data(1);
			_error.setError_msg(e.getMessage());

		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {

					e.printStackTrace();
				}
			}
		}
		adminResponse.setError(_error);
		return adminResponse;
	}

	/**
	 * When an Admin User set a Relation between Role and Module firstly this
	 * Relation will be Inactive. Using this Procedure this Role Module Relation can
	 * be Activate.
	 */
	public AdminResponse updateRoleModuleRelation(RequestJson requestJson) {
		Connection conn = null; // create connection instance to connect with
		// database
		CallableStatement callableStatement = null;
		AdminResponse adminResponse = new AdminResponse();
		ErrorResponse _error = new ErrorResponse();
		try {
			conn = dataSource.getConnection();

			String sql = "{call ROLE_MODULE_RELATION_ACTIVATE(?,?,?,?)}";
			callableStatement = conn.prepareCall(sql);
			callableStatement.setInt(1, requestJson.getRm_id());
			callableStatement.setInt(2, requestJson.getCreated_by());
			callableStatement.registerOutParameter(3, Types.INTEGER);
			callableStatement.registerOutParameter(4, Types.VARCHAR);
			callableStatement.executeUpdate();

			if (callableStatement.getInt(3) == 1) {
				_error.setError_data(0);
				_error.setError_msg(callableStatement.getString(4));
			} else {
				_error.setError_data(1);
				_error.setError_msg(callableStatement.getString(4));
			}

			callableStatement.close();

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			_error.setError_data(1);
			_error.setError_msg(e.getMessage());

		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {

					e.printStackTrace();
				}
			}
		}
		adminResponse.setError(_error);
		return adminResponse;
	}

	/**
	 * Using this Procedure All Role with Module will be shown.
	 * 
	 * @param roleMaster
	 * @return
	 */
	public AdminResponse getAllRoleModuleRelation(RequestJson requestJson) {
		Connection conn = null; // create connection instance to connect with
								// database
		CallableStatement callableStatement = null;
		AdminResponse adminResponse = new AdminResponse();
		ErrorResponse _error = new ErrorResponse();
		RoleModuleRelation roleModuleRelation = null;
		// GroupMaster groupMaster=null;
		List<RoleModuleRelation> roleModuleRelationList = new ArrayList<>();
		try {
			conn = dataSource.getConnection();

			String sql = "{call ROLE_MODULE_RELATION_GET_ALL(?,?,?,?)}";
			callableStatement = conn.prepareCall(sql);
			callableStatement.setInt(1, requestJson.getCreated_by());
			callableStatement.registerOutParameter(2, Types.INTEGER);
			callableStatement.registerOutParameter(3, Types.VARCHAR);
			callableStatement.registerOutParameter(4, OracleTypes.CURSOR);
			callableStatement.execute();
			if (callableStatement.getInt(2) == 1) {
				ResultSet rSet = (ResultSet) callableStatement.getObject(4);
				while (rSet.next()) {
					roleModuleRelation = setRoleModule(rSet);
					roleModuleRelationList.add(roleModuleRelation);
				}

				if (!roleModuleRelationList.isEmpty()) {
					adminResponse.setRoleModuleRelationList(roleModuleRelationList);

					_error.setError_data(0);
					_error.setError_msg(callableStatement.getString(3));
				} else {
					_error.setError_data(0);
					_error.setError_msg("no data");
				}
			} else {
				_error.setError_data(1);
				_error.setError_msg(callableStatement.getString(3));
			}
			callableStatement.close();

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			_error.setError_data(1);
			_error.setError_msg(e.getMessage());

		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {

					e.printStackTrace();
				}
			}
		}
		adminResponse.setError(_error);
		return adminResponse;
	}

	/**
	 * Using this Procedure All Role with Module will be shown.
	 * 
	 * @param roleMaster
	 * @return
	 */
	public AdminResponse getAllRoleModuleRelationByRole(RequestJson requestJson) {
		Connection conn = null; // create connection instance to connect with
								// database
		CallableStatement callableStatement = null;
		AdminResponse adminResponse = new AdminResponse();
		ErrorResponse _error = new ErrorResponse();
		RoleModuleRelation roleModuleRelation = null;
		// GroupMaster groupMaster=null;
		List<RoleModuleRelation> roleModuleRelationList = new ArrayList<>();
		try {
			conn = dataSource.getConnection();

			String sql = "{call ROLE_MODULE_RELATION_BY_ROLE(?,?,?,?,?)}";
			callableStatement = conn.prepareCall(sql);
			callableStatement.setInt(1, requestJson.getRole_id());
			callableStatement.setInt(2, requestJson.getCreated_by());
			callableStatement.registerOutParameter(3, Types.INTEGER);
			callableStatement.registerOutParameter(4, Types.VARCHAR);
			callableStatement.registerOutParameter(5, OracleTypes.CURSOR);
			callableStatement.execute();
			if (callableStatement.getInt(3) == 1) {
				ResultSet rSet = (ResultSet) callableStatement.getObject(5);
				while (rSet.next()) {
					roleModuleRelation = setRoleModule(rSet);
					roleModuleRelationList.add(roleModuleRelation);
				}

				if (!roleModuleRelationList.isEmpty()) {
					adminResponse.setRoleModuleRelationList(roleModuleRelationList);

					_error.setError_data(0);
					_error.setError_msg(callableStatement.getString(4));
				} else {
					_error.setError_data(0);
					_error.setError_msg("no data");
				}
			} else {
				_error.setError_data(1);
				_error.setError_msg(callableStatement.getString(4));
			}
			callableStatement.close();

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			_error.setError_data(1);
			_error.setError_msg(e.getMessage());

		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {

					e.printStackTrace();
				}
			}
		}
		adminResponse.setError(_error);
		return adminResponse;
	}

	/**
	 * Using this Procedure an Admin User who has the permission can set Relation
	 * between a Role a various Method. Methods means the Procedure. When an Admin
	 * User added he/she must be belongs to a Role. In this Procedure Role and
	 * Methods are set.
	 */
	public AdminResponse setRoleMethodRelation(RequestJson requestJson) {
		Connection conn = null; // create connection instance to connect with
		// database
		CallableStatement callableStatement = null;
		AdminResponse adminResponse = new AdminResponse();
		ErrorResponse _error = new ErrorResponse();
		try {
			conn = dataSource.getConnection();

			String sql = "{call ROLE_METHOD_RELATION_SET_PROC(?,?,?,?,?)}";
			callableStatement = conn.prepareCall(sql);
			callableStatement.setInt(1, requestJson.getRole_id());
			callableStatement.setInt(2, requestJson.getMethod_id());
			callableStatement.setInt(3, requestJson.getCreated_by());
			callableStatement.registerOutParameter(4, Types.INTEGER);
			callableStatement.registerOutParameter(5, Types.VARCHAR);
			callableStatement.executeUpdate();

			if (callableStatement.getInt(4) == 1) {
				_error.setError_data(0);
				_error.setError_msg(callableStatement.getString(5));
			} else {
				_error.setError_data(1);
				_error.setError_msg(callableStatement.getString(5));
			}

			callableStatement.close();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			_error.setError_data(1);
			_error.setError_msg(e.getMessage());

		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {

					e.printStackTrace();
				}
			}
		}
		adminResponse.setError(_error);
		return adminResponse;
	}

	/**
	 * When an Admin User set a Relation between Role and Module firstly this
	 * Relation will be Inactive. Using this Procedure this Role Method Relation can
	 * be Activate.
	 */
	public AdminResponse updateRoleMethodRelation(RequestJson requestJson) {
		Connection conn = null; // create connection instance to connect with
		// database
		CallableStatement callableStatement = null;
		AdminResponse adminResponse = new AdminResponse();
		ErrorResponse _error = new ErrorResponse();
		try {
			conn = dataSource.getConnection();

			String sql = "{call ROLE_METHOD_RELATION_ACTIVATE(?,?,?,?)}";
			callableStatement = conn.prepareCall(sql);
			callableStatement.setInt(1, requestJson.getRm_id());
			callableStatement.setInt(2, requestJson.getCreated_by());
			callableStatement.registerOutParameter(3, Types.INTEGER);
			callableStatement.registerOutParameter(4, Types.VARCHAR);
			callableStatement.executeUpdate();

			if (callableStatement.getInt(3) == 1) {
				_error.setError_data(0);
				_error.setError_msg(callableStatement.getString(4));
			} else {
				_error.setError_data(1);
				_error.setError_msg(callableStatement.getString(4));
			}

			callableStatement.close();

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			_error.setError_data(1);
			_error.setError_msg(e.getMessage());

		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {

					e.printStackTrace();
				}
			}
		}
		adminResponse.setError(_error);
		return adminResponse;
	}

	/**
	 * In this Procedure All Role and their related Methods are shown.
	 * 
	 * @param roleMaster
	 * @return
	 */
	public AdminResponse getAllRoleMethodRelation(RequestJson requestJson) {
		Connection conn = null; // create connection instance to connect with
								// database
		CallableStatement callableStatement = null;
		AdminResponse adminResponse = new AdminResponse();
		ErrorResponse _error = new ErrorResponse();
		RoleMethodRelation roleMethodRelation = null;
		// GroupMaster groupMaster=null;
		List<RoleMethodRelation> roleMethodRelationList = new ArrayList<>();
		try {
			conn = dataSource.getConnection();

			String sql = "{call ROLE_METHOD_RELATION_GET_ALL(?,?,?,?)}";
			callableStatement = conn.prepareCall(sql);
			callableStatement.setInt(1, requestJson.getCreated_by());
			callableStatement.registerOutParameter(2, Types.INTEGER);
			callableStatement.registerOutParameter(3, Types.VARCHAR);
			callableStatement.registerOutParameter(4, OracleTypes.CURSOR);
			callableStatement.execute();
			if (callableStatement.getInt(2) == 1) {
				ResultSet rSet = (ResultSet) callableStatement.getObject(4);
				while (rSet.next()) {
					roleMethodRelation = setRoleMethod(rSet);
					roleMethodRelationList.add(roleMethodRelation);
				}

				if (!roleMethodRelationList.isEmpty()) {
					adminResponse.setRoleMethodRelationList(roleMethodRelationList);

					_error.setError_data(0);
					_error.setError_msg(callableStatement.getString(3));
				} else {
					_error.setError_data(0);
					_error.setError_msg("no data");
				}
			} else {
				_error.setError_data(1);
				_error.setError_msg(callableStatement.getString(3));
			}
			callableStatement.close();

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			_error.setError_data(1);
			_error.setError_msg(e.getMessage());

		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {

					e.printStackTrace();
				}
			}
		}
		adminResponse.setError(_error);
		return adminResponse;
	}

	/**
	 * In this Procedure List of Methods will be shown for a Single Role.
	 * 
	 * @param roleMaster
	 * @return
	 */
	public AdminResponse getAllRoleMethodRelationByRole(RequestJson requestJson) {
		Connection conn = null; // create connection instance to connect with
								// database
		CallableStatement callableStatement = null;
		AdminResponse adminResponse = new AdminResponse();
		ErrorResponse _error = new ErrorResponse();
		RoleMethodRelation roleMethodRelation = null;
		// GroupMaster groupMaster=null;
		List<RoleMethodRelation> roleMethodRelationList = new ArrayList<>();
		try {
			conn = dataSource.getConnection();

			String sql = "{call ROLE_METHOD_RELATION_BY_ROLE(?,?,?,?,?)}";
			callableStatement = conn.prepareCall(sql);
			callableStatement.setInt(1, requestJson.getRole_id());
			callableStatement.setInt(2, requestJson.getCreated_by());
			callableStatement.registerOutParameter(3, Types.INTEGER);
			callableStatement.registerOutParameter(4, Types.VARCHAR);
			callableStatement.registerOutParameter(5, OracleTypes.CURSOR);
			callableStatement.execute();
			if (callableStatement.getInt(3) == 1) {
				ResultSet rSet = (ResultSet) callableStatement.getObject(5);
				while (rSet.next()) {
					roleMethodRelation = setRoleMethod(rSet);
					roleMethodRelationList.add(roleMethodRelation);
				}

				if (!roleMethodRelationList.isEmpty()) {
					adminResponse.setRoleMethodRelationList(roleMethodRelationList);

					_error.setError_data(0);
					_error.setError_msg(callableStatement.getString(4));
				} else {
					_error.setError_data(0);
					_error.setError_msg("no data");
				}
			} else {
				_error.setError_data(1);
				_error.setError_msg(callableStatement.getString(4));
			}
			callableStatement.close();

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			_error.setError_data(1);
			_error.setError_msg(e.getMessage());

		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {

					e.printStackTrace();
				}
			}
		}
		adminResponse.setError(_error);
		return adminResponse;
	}

	/**
	 * List of Active Module will be shown.
	 */
	public AdminResponse getAllModules(RequestJson requestJson) {
		Connection conn = null; // create connection instance to connect with
								// database
		CallableStatement callableStatement = null;
		AdminResponse adminResponse = new AdminResponse();
		ErrorResponse _error = new ErrorResponse();
		ModuleMaster _moduleMaster = null;
		List<ModuleMaster> moduleMasters = new ArrayList<>();
		try {
			conn = dataSource.getConnection();

			String sql = "{call MODULE_GET_ALL(?,?,?,?)}";
			callableStatement = conn.prepareCall(sql);
			callableStatement.setInt(1, requestJson.getCreated_by());
			callableStatement.registerOutParameter(2, Types.INTEGER);
			callableStatement.registerOutParameter(3, Types.VARCHAR);
			callableStatement.registerOutParameter(4, OracleTypes.CURSOR);
			callableStatement.execute();
			if (callableStatement.getInt(2) == 1) {
				ResultSet rSet = (ResultSet) callableStatement.getObject(4);
				while (rSet.next()) {
					_moduleMaster = setModule(rSet);
					moduleMasters.add(_moduleMaster);
				}

				if (!moduleMasters.isEmpty()) {
					adminResponse.setModuleMasterList(moduleMasters);

					_error.setError_data(0);
					_error.setError_msg(callableStatement.getString(3));
				} else {
					_error.setError_data(0);
					_error.setError_msg("no data");
				}
			} else {
				_error.setError_data(1);
				_error.setError_msg(callableStatement.getString(3));
			}
			callableStatement.close();

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			_error.setError_data(1);
			_error.setError_msg(e.getMessage());

		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {

					e.printStackTrace();
				}
			}
		}
		adminResponse.setError(_error);
		return adminResponse;
	}

	/**
	 * List of Active Methods will be shown.
	 */
	public AdminResponse getAllMethods(RequestJson requestJson) {
		Connection conn = null; // create connection instance to connect with
								// database
		CallableStatement callableStatement = null;
		AdminResponse adminResponse = new AdminResponse();
		ErrorResponse _error = new ErrorResponse();
		MethodMaster _methodMaster = null;
		List<MethodMaster> methodMasters = new ArrayList<>();
		try {
			conn = dataSource.getConnection();

			String sql = "{call METHOD_GET_ALL(?,?,?,?)}";
			callableStatement = conn.prepareCall(sql);
			callableStatement.setInt(1, requestJson.getCreated_by());
			callableStatement.registerOutParameter(2, Types.INTEGER);
			callableStatement.registerOutParameter(3, Types.VARCHAR);
			callableStatement.registerOutParameter(4, OracleTypes.CURSOR);
			callableStatement.execute();
			if (callableStatement.getInt(2) == 1) {
				ResultSet rSet = (ResultSet) callableStatement.getObject(4);
				while (rSet.next()) {
					_methodMaster = setMethod(rSet);
					methodMasters.add(_methodMaster);
				}

				if (!methodMasters.isEmpty()) {
					adminResponse.setMethodMasterList(methodMasters);

					_error.setError_data(0);
					_error.setError_msg(callableStatement.getString(3));
				} else {
					_error.setError_data(0);
					_error.setError_msg("no data");
				}
			} else {
				_error.setError_data(1);
				_error.setError_msg(callableStatement.getString(3));
			}
			callableStatement.close();

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			_error.setError_data(1);
			_error.setError_msg(e.getMessage());

		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {

					e.printStackTrace();
				}
			}
		}
		adminResponse.setError(_error);
		return adminResponse;
	}

	/**
	 * In this Procedure All the Module and their Related Methods are shown.
	 */
	public AdminResponse getAllMethodsByModule(RequestJson requestJson) {
		Connection conn = null; // create connection instance to connect with
								// database
		CallableStatement callableStatement = null;
		AdminResponse adminResponse = new AdminResponse();
		ErrorResponse _error = new ErrorResponse();
		MethodMaster _methodMaster = null;
		GroupMaster groupMaster = null;
		List<MethodMaster> methodMasters = new ArrayList<>();
		try {
			conn = dataSource.getConnection();

			String sql = "{call METHOD_GET_ALL_BY_MODULE(?,?,?,?,?)}";
			callableStatement = conn.prepareCall(sql);
			callableStatement.setInt(1, requestJson.getModule_id());
			callableStatement.setInt(2, requestJson.getCreated_by());
			callableStatement.registerOutParameter(3, Types.INTEGER);
			callableStatement.registerOutParameter(4, Types.VARCHAR);
			callableStatement.registerOutParameter(5, OracleTypes.CURSOR);
			callableStatement.execute();
			if (callableStatement.getInt(3) == 1) {
				ResultSet rSet = (ResultSet) callableStatement.getObject(5);
				while (rSet.next()) {
					groupMaster = new GroupMaster();
					_methodMaster = groupMaster.new MethodMaster();
					_methodMaster.setMethod_id(rSet.getInt("method_id"));
					_methodMaster.setMethod_title(rSet.getString("method_title"));
					_methodMaster.setMethod_name(rSet.getString("method_name"));
					_methodMaster.setMethod_status(rSet.getString("method_status"));
					methodMasters.add(_methodMaster);
				}

				if (!methodMasters.isEmpty()) {
					adminResponse.setMethodMasterList(methodMasters);

					_error.setError_data(0);
					_error.setError_msg(callableStatement.getString(4));
				} else {
					_error.setError_data(0);
					_error.setError_msg("no data");
				}
			} else {
				_error.setError_data(1);
				_error.setError_msg(callableStatement.getString(4));
			}
			callableStatement.close();

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			_error.setError_data(1);
			_error.setError_msg(e.getMessage());

		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {

					e.printStackTrace();
				}
			}
		}
		adminResponse.setError(_error);
		return adminResponse;
	}

	/**
	 * Using this procedure Admin User who has got permission to create Add User can
	 * add Admin User. In this procedure Admin User created and stored in Temp
	 * Table. This newly created Admin User can’t do anything thus need
	 * confirmation.
	 */
	public AdminUsersResponse registerAdminUsers(AdminUsers adminUser) {
		Connection conn = null; // create connection instance to connect with
								// database
		CallableStatement callableStatement = null;
		AdminUsersResponse adminUsersResponse = new AdminUsersResponse();
		ErrorResponse _error = new ErrorResponse();
		AdminUsers _adminuser = new AdminUsers();
		try {
			conn = dataSource.getConnection();
			String encodedPassword = getEncodedPassword(adminUser.getPassword());
			String sql = "{call ADMIN_USER_ADD_PROC(?,?,?,?,?,?,?,?)}";
			callableStatement = conn.prepareCall(sql);
			callableStatement.setString(1, adminUser.getName());
			callableStatement.setString(2, adminUser.getEmail());
			callableStatement.setString(3, adminUser.getPhone_no());
			callableStatement.setString(4, encodedPassword);
			callableStatement.setInt(5, adminUser.getCreated_by());
			callableStatement.registerOutParameter(6, Types.INTEGER);
			callableStatement.registerOutParameter(7, Types.INTEGER);
			callableStatement.registerOutParameter(8, Types.VARCHAR);
			callableStatement.executeUpdate();

			if (callableStatement.getInt(6) == 1) {
				adminUserPassword = adminUser.getPassword();
				_adminuser.setUser_id(callableStatement.getInt(7));
				adminUsersResponse.setAdminUsersResult(_adminuser);

				_error.setError_data(0);
				_error.setError_msg(callableStatement.getString(8));
			} else {
				_error.setError_data(1);
				_error.setError_msg(callableStatement.getString(8));
			}

			callableStatement.close();

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			_error.setError_data(1);
			_error.setError_msg(e.getMessage());

		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {

					e.printStackTrace();
				}
			}
		}
		adminUsersResponse.setError(_error);
		return adminUsersResponse;
	}

	/**
	 * Using this procedure Role can be set for a newly created temp user. If Role
	 * is not set for an Temp Admin User then this Temp User can’t be confirmed.
	 */
	public AdminUsersResponse setAdminUserRole(RequestJson requestJson) {
		Connection conn = null; // create connection instance to connect with
								// database
		CallableStatement callableStatement = null;
		AdminUsersResponse adminUsersResponse = new AdminUsersResponse();
		ErrorResponse _error = new ErrorResponse();
		try {
			conn = dataSource.getConnection();

			String sql = "{call ADMIN_USER_ROLE_SET_PROC(?,?,?,?,?)}";
			callableStatement = conn.prepareCall(sql);
			callableStatement.setInt(1, requestJson.getUser_id());
			callableStatement.setInt(2, requestJson.getRole_id());
			callableStatement.setInt(3, requestJson.getCreated_by());
			callableStatement.registerOutParameter(4, Types.INTEGER);
			callableStatement.registerOutParameter(5, Types.VARCHAR);
			callableStatement.executeUpdate();

			if (callableStatement.getInt(4) == 1) {
				_error.setError_data(0);
				_error.setError_msg(callableStatement.getString(5));
			} else {
				_error.setError_data(1);
				_error.setError_msg(callableStatement.getString(5));
			}

			callableStatement.close();

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			_error.setError_data(1);
			_error.setError_msg(e.getMessage());

		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		adminUsersResponse.setError(_error);
		return adminUsersResponse;
	}

	/**
	 * Using this procedure Admin User who has got permission to confirm an Admin
	 * User can only confirm a newly created Admin User.
	 */
	public AdminUsersResponse confirmAdminUser(AdminUsers adminuser) {
		Connection conn = null; // create connection instance to connect with
								// database
		CallableStatement callableStatement = null;
		AdminUsersResponse adminUsersResponse = new AdminUsersResponse();
		ErrorResponse _error = new ErrorResponse();
		// AdminUsers _adminuser=new AdminUsers();
		try {
			conn = dataSource.getConnection();

			String sql = "{call ADMIN_USER_CONFIRM_PROC(?,?,?,?,?,?,?,?)}";
			callableStatement = conn.prepareCall(sql);
			callableStatement.setInt(1, adminuser.getUser_id());
			callableStatement.setInt(2, adminuser.getCreated_by());
			callableStatement.registerOutParameter(3, Types.INTEGER);
			callableStatement.registerOutParameter(4, Types.VARCHAR);
			callableStatement.registerOutParameter(5, Types.VARCHAR);
			callableStatement.registerOutParameter(6, Types.VARCHAR);
			callableStatement.registerOutParameter(7, Types.VARCHAR);
			callableStatement.registerOutParameter(8, Types.VARCHAR);
			callableStatement.executeUpdate();

			if (callableStatement.getInt(3) == 1) {
				// _adminuser.setUser_name(callableStatement.getString(5));
				// adminUsersResponse.setAdminUsersResult(_adminuser);

				// send mail to user
				String customer_email = callableStatement.getString(8);
				String message = "Your admin credentials are USERNAME: " + callableStatement.getString(6)
						+ " and PASSWORD: " + adminUserPassword;

				HashMap<String, String> nameVal = new HashMap<>();
				nameVal.put("welcome_message", "Dear");
				nameVal.put("user_name", callableStatement.getString(5));
				nameVal.put("message1", message);
//				 nameVal.put("message2", "Use this below link to go admin login page.");
//				 nameVal.put("message3", "https://trade.paybito.com/admin-access/login.html");
				String mail_content = mailContentBuilderService.build("new_admin_user", nameVal);

				String subject = "Your " + env.getProperty("project.company.name") + " Admin Login Credential.";
				mailClientService.mailthreding(env.getProperty("spring.mail.username"), customer_email, subject,
						mail_content);

				_error.setError_data(0);
				_error.setError_msg(callableStatement.getString(4));
			} else {
				_error.setError_data(1);
				_error.setError_msg(callableStatement.getString(4));
			}

			callableStatement.close();

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			_error.setError_data(1);
			_error.setError_msg(e.getMessage());

		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {

					e.printStackTrace();
				}
			}
		}
		adminUsersResponse.setError(_error);
		return adminUsersResponse;
	}

	public AdminResponse sendOTP(AdminUsers adminUsers) {
		AdminResponse adminResponse = new AdminResponse();
		ErrorResponse errorResponse = new ErrorResponse();
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = "select a.user_id,a.email_id from admin_user a,admin_user_login b where a.user_id=b.user_id and b.user_name = ? ";
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, adminUsers.getUser_name());
			ResultSet rSet = ps.executeQuery();
			if (rSet.next()) {
				String email = rSet.getString("email_id");
				int start = 10000;
				int end = 99998;
				int randomNumber = randomNumberGenerate(start, end);
				ps.close();
				sql = "update admin_user_login set otp = ? where user_name = ? ";
				ps = conn.prepareStatement(sql);
				ps.setInt(1, randomNumber);
				ps.setString(2, adminUsers.getUser_name());
				ps.executeUpdate();
				ps.close();
				// send otp via mail
				HashMap<String, String> nameVal = new HashMap<>();
				nameVal.put("heading", "OTP");
				nameVal.put("otp", String.valueOf(randomNumber));
				nameVal.put("description", "Please do not share your OTP with anyone.");
				String mailContent = mailContentBuilderService.build("otp_generate", nameVal);
				String subject = env.getProperty("project.company.product") + " OTP ";
				mailClientService.mailthreding(env.getProperty("spring.mail.username"), email, subject, mailContent);
				errorResponse.setError_data(0);
				errorResponse.setError_msg("Otp sent to your mail " + email + ".");
			} else {
				errorResponse.setError_data(1);
				errorResponse.setError_msg("Invalid Username.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			errorResponse.setError_data(1);
			errorResponse.setError_msg(e.getMessage());
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		adminResponse.setError(errorResponse);
		return adminResponse;
	}

	public AdminResponse loginAdmin(RequestJson requestJson) {
		Connection conn = null; // create connection instance to connect with database
		CallableStatement callableStatement = null;
		AdminResponse adminUsersResponse = new AdminResponse();
		ErrorResponse _error = new ErrorResponse();
		AdminUsers _adminuser = new AdminUsers();
		List<ModuleMaster> moduleMasters = new ArrayList<>();
		List<MethodMaster> methodMasters = new ArrayList<>();
		try {
			conn = dataSource.getConnection();
			String encodedPassword = getEncodedPassword(requestJson.getPassword());
			String sql = "{call ADMIN_USER_LOGIN_PROC(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
			callableStatement = conn.prepareCall(sql);
			callableStatement.setString(1, requestJson.getUser_name());
			callableStatement.setString(2, encodedPassword);
			callableStatement.setString(3, requestJson.getIp_address());
			callableStatement.setInt(4, requestJson.getOtp());
			callableStatement.registerOutParameter(5, Types.INTEGER);
			callableStatement.registerOutParameter(6, Types.VARCHAR);
			callableStatement.registerOutParameter(7, Types.INTEGER);
			callableStatement.registerOutParameter(8, Types.VARCHAR);
			callableStatement.registerOutParameter(9, Types.VARCHAR);
			callableStatement.registerOutParameter(10, Types.VARCHAR);
			callableStatement.registerOutParameter(11, Types.VARCHAR);
			callableStatement.registerOutParameter(12, Types.VARCHAR);
			callableStatement.registerOutParameter(13, Types.VARCHAR);
			callableStatement.registerOutParameter(14, Types.VARCHAR);
			callableStatement.registerOutParameter(15, Types.INTEGER);
			callableStatement.registerOutParameter(16, Types.INTEGER);
			callableStatement.registerOutParameter(17, OracleTypes.CURSOR);
			callableStatement.registerOutParameter(18, OracleTypes.CURSOR);
			callableStatement.executeUpdate();

			if (callableStatement.getInt(5) == 1) {
				_adminuser.setUser_id(callableStatement.getInt(15) == 0 ? callableStatement.getInt(7) : 0);
//				_adminuser.setUser_id(callableStatement.getInt(7));
				_adminuser.setName(callableStatement.getString(8));
				_adminuser.setPhone_no(callableStatement.getString(9));
				_adminuser.setEmail(callableStatement.getString(10));
				_adminuser.setLast_login_time(callableStatement.getString(11));
				_adminuser.setFranchiseId(callableStatement.getInt(12));
				_adminuser.setIsFranchiseRole(callableStatement.getInt(13));
				_adminuser.setCoinOwner(callableStatement.getString(14));
				_adminuser.setEnabled2fa(callableStatement.getInt(15));
				_adminuser.setExist2FaKey(callableStatement.getInt(16));
				adminUsersResponse.setAdminUsersResult(_adminuser);

				// get user access module list
				ResultSet rSet = (ResultSet) callableStatement.getObject(17);
				while (rSet.next()) {
					GroupMaster groupMaster = new GroupMaster();
					ModuleMaster _moduleMaster = groupMaster.new ModuleMaster();
					_moduleMaster.setModule_id(rSet.getInt("module_id"));
					_moduleMaster.setModule_title(rSet.getString("module_title"));
					_moduleMaster.setModule_url(rSet.getString("module_url"));
					_moduleMaster.setParent_id(rSet.getInt("parent_id"));
					_moduleMaster.setModule_order(rSet.getInt("module_order"));
					moduleMasters.add(_moduleMaster);
				}
				rSet.close();
				adminUsersResponse.setModuleMasterList(moduleMasters);

				// get user access method list
				rSet = (ResultSet) callableStatement.getObject(18);
				while (rSet.next()) {
					GroupMaster groupMaster = new GroupMaster();
					MethodMaster _methodMaster = groupMaster.new MethodMaster();
					_methodMaster.setModule_id(rSet.getInt("module_id"));
					_methodMaster.setMethod_id(rSet.getInt("method_id"));
					_methodMaster.setMethod_title(rSet.getString("method_title"));
					_methodMaster.setMethod_name(rSet.getString("method_name"));
					methodMasters.add(_methodMaster);
				}
				rSet.close();
				adminUsersResponse.setMethodMasterList(methodMasters);

				_error.setError_data(0);
				_error.setError_msg(callableStatement.getNString(6));
			} else {
				_error.setError_data(1);
				_error.setError_msg(callableStatement.getNString(6));
			}
			callableStatement.close();

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			_error.setError_data(1);
			_error.setError_msg(e.getMessage());

		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {

					e.printStackTrace();
				}
			}
		}
		adminUsersResponse.setError(_error);
		return adminUsersResponse;
	}

	public AdminResponse logoutAdmin(RequestJson requestJson) {
		Connection conn = null; // create connection instance to connect with
								// database
		CallableStatement callableStatement = null;
		AdminResponse adminUsersResponse = new AdminResponse();
		ErrorResponse _error = new ErrorResponse();
		try {
			conn = dataSource.getConnection();

			String sql = "{call ADMIN_USER_LOGOUT(?)}";
			callableStatement = conn.prepareCall(sql);
			callableStatement.setString(1, requestJson.getUser_name());
			callableStatement.executeUpdate();
			callableStatement.close();

			_error.setError_data(0);
			_error.setError_msg("");
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			_error.setError_data(1);
			_error.setError_msg(e.getMessage());

		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		adminUsersResponse.setError(_error);
		return adminUsersResponse;
	}

	/**
	 * Using this Procedure an Admin User who has the permission can set Relation
	 * between a Role and various Modules and methods. Module means the Menu(URL)
	 * ,Methods means the access level. When an Admin User added he/she must be
	 * belongs to a Role. When an Admin User Login to the System then Which Menu
	 * will open for this User will be defined by the Role and Module Relation. When
	 * an Admin User Login to the System then Which Method will open for this User
	 * will be defined by the Role and Method Relation.
	 */
	public AdminResponse setRoleModuleMethodRelation(RequestJson requestJson) {
		Connection conn = null; // create connection instance to connect with
		// database
		CallableStatement callableStatement = null;
		AdminResponse adminResponse = new AdminResponse();
		ErrorResponse _error = new ErrorResponse();
		String sql = "";
		try {
			conn = dataSource.getConnection();
			// module method list string sample 7(moduleId)=1,2(methodID)-8=3,4,5-2=3
			log.info("module string : " + requestJson.getModule_list());
			/*
			 * String []module_list=requestJson.getModule_list().split("-"); for(int
			 * i=0;i<module_list.length;i++){ String []module=module_list[i].split("=");
			 * log.info("module id : "+module[0]);
			 * 
			 * sql = "{call ROLE_MODULE_RELATION_SET_PROC(?,?,?,?,?,?)}"; callableStatement
			 * = conn.prepareCall(sql); callableStatement.setInt(1,
			 * requestJson.getRole_id()); callableStatement.setInt(2,
			 * Integer.parseInt(module[0])); callableStatement.setInt(3, 1);
			 * callableStatement.setInt(4, requestJson.getCreated_by());
			 * callableStatement.registerOutParameter(5, Types.INTEGER);
			 * callableStatement.registerOutParameter(6, Types.VARCHAR);
			 * callableStatement.executeUpdate(); callableStatement.close();
			 * 
			 * //get method from comma separated method list if(module.length>1){ String
			 * []methods=module[1].split(","); for(int j=0;j<methods.length;j++){
			 * log.info("method id : "+methods[j]); sql =
			 * "{call ROLE_METHOD_RELATION_SET_PROC(?,?,?,?,?,?)}"; callableStatement =
			 * conn.prepareCall(sql); callableStatement.setInt(1, requestJson.getRole_id());
			 * callableStatement.setInt(2, Integer.parseInt(methods[j]));
			 * callableStatement.setInt(3, 1); callableStatement.setInt(4,
			 * requestJson.getCreated_by()); callableStatement.registerOutParameter(5,
			 * Types.INTEGER); callableStatement.registerOutParameter(6, Types.VARCHAR);
			 * callableStatement.executeUpdate(); callableStatement.close(); } } }
			 */

			String[] module_check = requestJson.getModule_list().split(",");
			for (int i = 0; i < module_check.length; i++) {

				String[] module = module_check[i].split("-");
				// log.info("module id : "+module[0]);
				// log.info("check/uncheck : "+module[1]);

				sql = "{call ROLE_MODULE_RELATION_SET_PROC(?,?,?,?,?,?)}";
				callableStatement = conn.prepareCall(sql);
				callableStatement.setInt(1, requestJson.getRole_id());
				callableStatement.setInt(2, Integer.parseInt(module[0]));
				callableStatement.setInt(3, Integer.parseInt(module[1]));
				callableStatement.setInt(4, requestJson.getCreated_by());
				callableStatement.registerOutParameter(5, Types.INTEGER);
				callableStatement.registerOutParameter(6, Types.VARCHAR);
				callableStatement.executeUpdate();
				callableStatement.close();
			}

			log.info("method string : " + requestJson.getMethod_list());
			String[] method_check = requestJson.getMethod_list().split(",");
			for (int i = 0; i < method_check.length; i++) {

				String[] method = method_check[i].split("-");
				// log.info("method id : "+method[0]);
				// log.info("check/uncheck : "+method[1]);

				sql = "{call ROLE_METHOD_RELATION_SET_PROC(?,?,?,?,?,?)}";
				callableStatement = conn.prepareCall(sql);
				callableStatement.setInt(1, requestJson.getRole_id());
				callableStatement.setInt(2, Integer.parseInt(method[0]));
				callableStatement.setInt(3, Integer.parseInt(method[1]));
				callableStatement.setInt(4, requestJson.getCreated_by());
				callableStatement.registerOutParameter(5, Types.INTEGER);
				callableStatement.registerOutParameter(6, Types.VARCHAR);
				callableStatement.executeUpdate();
				callableStatement.close();
			}

			// if (callableStatement.getInt(4) == 1) {
			_error.setError_data(0);
			_error.setError_msg("");
			/*
			 * } else { _error.setError_data(1);
			 * _error.setError_msg(callableStatement.getString(5)); }
			 */

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			_error.setError_data(1);
			_error.setError_msg(e.getMessage());

		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {

					e.printStackTrace();
				}
			}
		}
		adminResponse.setError(_error);
		return adminResponse;
	}

	// get all admin users
	public AdminResponse getAllAdminUsers(RequestJson requestJson) {
		Connection conn = null; // create connection instance to connect with
								// database
		CallableStatement callableStatement = null;
		AdminResponse adminUsersResponse = new AdminResponse();
		ErrorResponse _error = new ErrorResponse();
		AdminUsers _adminuser = null;
		List<AdminUsers> _adminuserList = new ArrayList<>();
		try {
			conn = dataSource.getConnection();

			String sql = "{call ADMIN_USER_GET_ALL(?,?,?,?)}";
			callableStatement = conn.prepareCall(sql);
			callableStatement.setInt(1, requestJson.getCreated_by());
			callableStatement.registerOutParameter(2, Types.INTEGER);
			callableStatement.registerOutParameter(3, Types.VARCHAR);
			callableStatement.registerOutParameter(4, OracleTypes.CURSOR);
			callableStatement.executeUpdate();

			if (callableStatement.getInt(2) == 1) {
				// get user access module list
				ResultSet rSet = (ResultSet) callableStatement.getObject(4);
				while (rSet.next()) {
					_adminuser = new AdminUsers();
					_adminuser.setUser_id(rSet.getInt("user_id"));
					_adminuser.setName(rSet.getString("name"));
					_adminuser.setEmail(rSet.getString("email_id"));
					_adminuser.setPhone_no(rSet.getString("mobile_no"));
					_adminuser.setUser_name(rSet.getString("user_name"));
					_adminuser.setPassword(rSet.getString("password"));
					_adminuser.setLast_login_time(rSet.getString("last_login_time"));
					_adminuser.setLogin_status(rSet.getString("login_status"));
					_adminuser.setUser_status(rSet.getString("user_status"));
					_adminuser.setCreated_on(rSet.getString("created_on"));
					_adminuser.setCreated_by(rSet.getInt("created_by"));
					_adminuserList.add(_adminuser);
				}
				rSet.close();
				if (!_adminuserList.isEmpty()) {
					adminUsersResponse.setAdminUsersList(_adminuserList);
				}

				_error.setError_data(0);
				_error.setError_msg(callableStatement.getNString(3));
			} else {
				_error.setError_data(1);
				_error.setError_msg(callableStatement.getNString(3));
			}
			callableStatement.close();

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			_error.setError_data(1);
			_error.setError_msg(e.getMessage());

		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {

					e.printStackTrace();
				}
			}
		}
		adminUsersResponse.setError(_error);
		return adminUsersResponse;
	}

	/**
	 * To Block Admin user. Once Block the this user can't login to the system
	 */
	public AdminResponse blockAdminUser(RequestJson requestJson) {
		Connection conn = null; // create connection instance to connect with
		// database
		CallableStatement callableStatement = null;
		AdminResponse adminResponse = new AdminResponse();
		ErrorResponse _error = new ErrorResponse();
		try {
			conn = dataSource.getConnection();

			String sql = "{call ADMIN_USER_BLOCK(?,?,?,?)}";
			callableStatement = conn.prepareCall(sql);
			callableStatement.setInt(1, requestJson.getUser_id());
			callableStatement.setInt(2, requestJson.getCreated_by());
			callableStatement.registerOutParameter(3, Types.INTEGER);
			callableStatement.registerOutParameter(4, Types.VARCHAR);
			callableStatement.executeUpdate();

			if (callableStatement.getInt(3) == 1) {
				_error.setError_data(0);
				_error.setError_msg(callableStatement.getString(4));
			} else {
				_error.setError_data(1);
				_error.setError_msg(callableStatement.getString(4));
			}

			callableStatement.close();

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			_error.setError_data(1);
			_error.setError_msg(e.getMessage());

		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {

					e.printStackTrace();
				}
			}
		}
		adminResponse.setError(_error);
		return adminResponse;
	}

	/**
	 * To UnBlock Admin user. unBlock the this user can login to the system
	 */
	public AdminResponse unblockAdminUser(RequestJson requestJson) {
		Connection conn = null; // create connection instance to connect with
		// database
		CallableStatement callableStatement = null;
		AdminResponse adminResponse = new AdminResponse();
		ErrorResponse _error = new ErrorResponse();
		try {
			conn = dataSource.getConnection();

			String sql = "{call ADMIN_USER_UNBLOCK(?,?,?,?)}";
			callableStatement = conn.prepareCall(sql);
			callableStatement.setInt(1, requestJson.getUser_id());
			callableStatement.setInt(2, requestJson.getCreated_by());
			callableStatement.registerOutParameter(3, Types.INTEGER);
			callableStatement.registerOutParameter(4, Types.VARCHAR);
			callableStatement.executeUpdate();

			if (callableStatement.getInt(3) == 1) {
				_error.setError_data(0);
				_error.setError_msg(callableStatement.getString(4));
			} else {
				_error.setError_data(1);
				_error.setError_msg(callableStatement.getString(4));
			}

			callableStatement.close();

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			_error.setError_data(1);
			_error.setError_msg(e.getMessage());

		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {

					e.printStackTrace();
				}
			}
		}
		adminResponse.setError(_error);
		return adminResponse;
	}

	/**
	 * Change Mobile for Admin User
	 */
	public AdminResponse changeAdminPhone(RequestJson requestJson) {
		Connection conn = null; // create connection instance to connect with
		// database
		CallableStatement callableStatement = null;
		AdminResponse adminResponse = new AdminResponse();
		ErrorResponse _error = new ErrorResponse();
		try {
			conn = dataSource.getConnection();

			String sql = "{call ADMIN_USER_MOBILE_CHANGE(?,?,?,?,?)}";
			callableStatement = conn.prepareCall(sql);
			callableStatement.setInt(1, requestJson.getUser_id());
			callableStatement.setString(2, requestJson.getPhone());
			callableStatement.setInt(3, requestJson.getCreated_by());
			callableStatement.registerOutParameter(4, Types.INTEGER);
			callableStatement.registerOutParameter(5, Types.VARCHAR);
			callableStatement.executeUpdate();

			if (callableStatement.getInt(4) == 1) {
				_error.setError_data(0);
				_error.setError_msg(callableStatement.getString(5));
			} else {
				_error.setError_data(1);
				_error.setError_msg(callableStatement.getString(5));
			}
			callableStatement.close();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			_error.setError_data(1);
			_error.setError_msg(e.getMessage());
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {

					e.printStackTrace();
				}
			}
		}
		adminResponse.setError(_error);
		return adminResponse;
	}

	/**
	 * Change Admin User Password
	 */
	public AdminResponse changeAdminPassword(RequestJson requestJson) {
		Connection conn = null; // create connection instance to connect with
		// database
		CallableStatement callableStatement = null;
		AdminResponse adminResponse = new AdminResponse();
		ErrorResponse _error = new ErrorResponse();
		try {
			conn = dataSource.getConnection();
//			String encodedPassword = getEncodedPassword(requestJson.getPrev_password());
			String newEncodedPassword = getEncodedPassword(requestJson.getPassword());
			String sql = "{call ADMIN_USER_PASS_CHANGE(?,?,?,?,?)}";
			callableStatement = conn.prepareCall(sql);
			callableStatement.setInt(1, requestJson.getUser_id());
			callableStatement.setString(2, newEncodedPassword);
//			callableStatement.setString(3, encodedPassword);
			callableStatement.setInt(3, requestJson.getCreated_by());
			callableStatement.registerOutParameter(4, Types.INTEGER);
			callableStatement.registerOutParameter(5, Types.VARCHAR);
			callableStatement.executeUpdate();

			if (callableStatement.getInt(4) == 1) {
				_error.setError_data(0);
				_error.setError_msg(callableStatement.getString(5));
			} else {
				_error.setError_data(1);
				_error.setError_msg(callableStatement.getString(5));
			}
			callableStatement.close();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			_error.setError_data(1);
			_error.setError_msg(e.getMessage());
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		adminResponse.setError(_error);
		return adminResponse;
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

	public int randomNumberGenerate(int start, int end) {
		Random random = new Random();
		// get the range, casting to long to avoid overflow problems
		long range = (long) end - (long) start + 1;
		// compute a fraction of the range, 0 <= frac < range
		long fraction = (long) (range * random.nextDouble());
		int randomNumber = (int) (fraction + start);
		return randomNumber;
	}

	/**
	 * Block a Group. Once block can't add any Role to this Block
	 */
	public AdminResponse blockGroup(RequestJson requestJson) {
		Connection conn = null; // create connection instance to connect with
		// database
		CallableStatement callableStatement = null;
		AdminResponse adminResponse = new AdminResponse();
		ErrorResponse _error = new ErrorResponse();
		try {
			conn = dataSource.getConnection();

			String sql = "{call GROUP_BLOCK(?,?,?,?)}";
			callableStatement = conn.prepareCall(sql);
			callableStatement.setInt(1, requestJson.getGroup_id());
			callableStatement.setInt(2, requestJson.getCreated_by());
			callableStatement.registerOutParameter(3, Types.INTEGER);
			callableStatement.registerOutParameter(4, Types.VARCHAR);
			callableStatement.executeUpdate();

			if (callableStatement.getInt(3) == 1) {
				_error.setError_data(0);
				_error.setError_msg(callableStatement.getString(4));
			} else {
				_error.setError_data(1);
				_error.setError_msg(callableStatement.getString(4));
			}

			callableStatement.close();

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			_error.setError_data(1);
			_error.setError_msg(e.getMessage());

		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {

					e.printStackTrace();
				}
			}
		}
		adminResponse.setError(_error);
		return adminResponse;
	}

	/**
	 * Unblock a Group.
	 */
	public AdminResponse unBlockGroup(RequestJson requestJson) {
		Connection conn = null; // create connection instance to connect with
		// database
		CallableStatement callableStatement = null;
		AdminResponse adminResponse = new AdminResponse();
		ErrorResponse _error = new ErrorResponse();
		try {
			conn = dataSource.getConnection();

			String sql = "{call GROUP_UNBLOCK(?,?,?,?)}";
			callableStatement = conn.prepareCall(sql);
			callableStatement.setInt(1, requestJson.getGroup_id());
			callableStatement.setInt(2, requestJson.getCreated_by());
			callableStatement.registerOutParameter(3, Types.INTEGER);
			callableStatement.registerOutParameter(4, Types.VARCHAR);
			callableStatement.executeUpdate();

			if (callableStatement.getInt(3) == 1) {
				_error.setError_data(0);
				_error.setError_msg(callableStatement.getString(4));
			} else {
				_error.setError_data(1);
				_error.setError_msg(callableStatement.getString(4));
			}

			callableStatement.close();

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			_error.setError_data(1);
			_error.setError_msg(e.getMessage());

		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {

					e.printStackTrace();
				}
			}
		}
		adminResponse.setError(_error);
		return adminResponse;
	}

	/**
	 * Block a Role. Once block can't add any Method or Module to this Role,
	 */
	public AdminResponse blockRole(RequestJson requestJson) {
		Connection conn = null; // create connection instance to connect with
		// database
		CallableStatement callableStatement = null;
		AdminResponse adminResponse = new AdminResponse();
		ErrorResponse _error = new ErrorResponse();
		try {
			conn = dataSource.getConnection();

			String sql = "{call ROLE_BLOCK(?,?,?,?)}";
			callableStatement = conn.prepareCall(sql);
			callableStatement.setInt(1, requestJson.getRole_id());
			callableStatement.setInt(2, requestJson.getCreated_by());
			callableStatement.registerOutParameter(3, Types.INTEGER);
			callableStatement.registerOutParameter(4, Types.VARCHAR);
			callableStatement.executeUpdate();

			if (callableStatement.getInt(3) == 1) {
				_error.setError_data(0);
				_error.setError_msg(callableStatement.getString(4));
			} else {
				_error.setError_data(1);
				_error.setError_msg(callableStatement.getString(4));
			}

			callableStatement.close();

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			_error.setError_data(1);
			_error.setError_msg(e.getMessage());

		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {

					e.printStackTrace();
				}
			}
		}
		adminResponse.setError(_error);
		return adminResponse;
	}

	/**
	 * Unblock a Role
	 */
	public AdminResponse unBlockRole(RequestJson requestJson) {
		Connection conn = null; // create connection instance to connect with
		// database
		CallableStatement callableStatement = null;
		AdminResponse adminResponse = new AdminResponse();
		ErrorResponse _error = new ErrorResponse();
		try {
			conn = dataSource.getConnection();

			String sql = "{call ROLE_UNBLOCK(?,?,?,?)}";
			callableStatement = conn.prepareCall(sql);
			callableStatement.setInt(1, requestJson.getRole_id());
			callableStatement.setInt(2, requestJson.getCreated_by());
			callableStatement.registerOutParameter(3, Types.INTEGER);
			callableStatement.registerOutParameter(4, Types.VARCHAR);
			callableStatement.executeUpdate();

			if (callableStatement.getInt(3) == 1) {
				_error.setError_data(0);
				_error.setError_msg(callableStatement.getString(4));
			} else {
				_error.setError_data(1);
				_error.setError_msg(callableStatement.getString(4));
			}

			callableStatement.close();

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			_error.setError_data(1);
			_error.setError_msg(e.getMessage());

		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {

					e.printStackTrace();
				}
			}
		}
		adminResponse.setError(_error);
		return adminResponse;
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
			String sql = "select user_name,password from  admin_user_login where user_name = ? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, UserName);
			ResultSet resultSet = ps.executeQuery();
			if (resultSet.next()) {
				_adminuser.setUser_name(resultSet.getString("user_name"));
				_adminuser.setPassword(resultSet.getString("password"));
				_adminuser.setRole("ROLE_ADMIN");
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

	public RoleMaster setRole(ResultSet rSet) throws SQLException {
		GroupMaster groupMaster = new GroupMaster();
		RoleMaster _roleMaster = groupMaster.new RoleMaster();
		_roleMaster.setRole_id(rSet.getInt("role_id"));
		_roleMaster.setRole_name(rSet.getString("role_name"));
		_roleMaster.setRole_desc(rSet.getString("role_desc"));
		_roleMaster.setRole_prefix(rSet.getString("role_prefix"));
		_roleMaster.setRole_status(rSet.getString("role_status"));

		return _roleMaster;
	}

	public ModuleMaster setModule(ResultSet rSet) throws SQLException {
		GroupMaster groupMaster = new GroupMaster();
		ModuleMaster _moduleMaster = groupMaster.new ModuleMaster();
		_moduleMaster.setModule_id(rSet.getInt("module_id"));
		_moduleMaster.setModule_title(rSet.getString("module_title"));
		_moduleMaster.setModule_url(rSet.getString("module_url"));
		_moduleMaster.setParent_id(rSet.getInt("parent_id"));
		_moduleMaster.setModule_order(rSet.getInt("module_order"));
		_moduleMaster.setModule_status(rSet.getString("module_status"));
		return _moduleMaster;
	}

	public MethodMaster setMethod(ResultSet rSet) throws SQLException {
		GroupMaster groupMaster = new GroupMaster();
		MethodMaster _methodMaster = groupMaster.new MethodMaster();
		_methodMaster.setModule_id(rSet.getInt("module_id"));
		_methodMaster.setModule_status(rSet.getString("module_status"));
		_methodMaster.setModule_title(rSet.getString("module_title"));
		_methodMaster.setMethod_id(rSet.getInt("method_id"));
		_methodMaster.setMethod_title(rSet.getString("method_title"));
		_methodMaster.setMethod_status(rSet.getString("method_status"));
		return _methodMaster;
	}

	public RoleModuleRelation setRoleModule(ResultSet rSet) throws SQLException {
		GroupMaster groupMaster = new GroupMaster();
		RoleModuleRelation roleModuleRelation = groupMaster.new RoleModuleRelation();
		roleModuleRelation.setRole_id(rSet.getInt("role_id"));
		roleModuleRelation.setRole_name(rSet.getString("role_name"));
		roleModuleRelation.setRole_status(rSet.getString("role_status"));
		roleModuleRelation.setRelation_status(rSet.getString("relation_status"));
		roleModuleRelation.setModule_id(rSet.getInt("module_id"));
		roleModuleRelation.setModule_title(rSet.getString("module_title"));
		roleModuleRelation.setModule_status(rSet.getString("module_status"));

		return roleModuleRelation;
	}

	public RoleMethodRelation setRoleMethod(ResultSet rSet) throws SQLException {
		GroupMaster groupMaster = new GroupMaster();
		RoleMethodRelation roleMethodRelation = groupMaster.new RoleMethodRelation();
		roleMethodRelation.setRole_id(rSet.getInt("role_id"));
		roleMethodRelation.setRole_name(rSet.getString("role_name"));
		roleMethodRelation.setRole_status(rSet.getString("role_status"));
		roleMethodRelation.setRelation_status(rSet.getString("relation_status"));
		roleMethodRelation.setMethod_id(rSet.getInt("method_id"));
		roleMethodRelation.setMethod_title(rSet.getString("method_title"));
		roleMethodRelation.setMethod_status(rSet.getString("method_status"));

		return roleMethodRelation;
	}

	public AdminResponse changePassword(AdminUsers adminUsers) {
		AdminResponse adminResponse = new AdminResponse();
		ErrorResponse error = new ErrorResponse();
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			if (adminUsers.getUser_id() > 0 && StringUtils.hasText(adminUsers.getPassword())
					&& StringUtils.hasText(adminUsers.getNewPassword())) {
				conn = dataSource.getConnection();
				String encodedPassword = getEncodedPassword(adminUsers.getPassword());
				String newEncodedPassword = getEncodedPassword(adminUsers.getNewPassword());
				String sql = "SELECT USER_ID FROM ADMIN_USER_LOGIN WHERE PASSWORD = ? and USER_ID = ? ";
				ps = conn.prepareStatement(sql);
				ps.setString(1, encodedPassword);
				ps.setInt(2, adminUsers.getUser_id());
				ResultSet rSet = ps.executeQuery();
				if (rSet.next()) {
					sql = "UPDATE ADMIN_USER_LOGIN SET PASSWORD = ? WHERE USER_ID = ? ";
					ps.close();
					ps = conn.prepareStatement(sql);
					ps.setString(1, newEncodedPassword);
					ps.setInt(2, adminUsers.getUser_id());
					ps.executeUpdate();
					ps.close();
					error.setError_data(0);
					error.setError_msg("Password Changed Successfully.");
				} else {
					error.setError_data(1);
					error.setError_msg("Old password does not match. Please provide the correct old password.");
				}
				rSet.close();
				ps.close();
			} else {
				error = error.GetErrorSet(1);
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
		adminResponse.setError(error);
		return adminResponse;
	}

	public AdminResponse adminUserAccountStatus(RequestJson requestJson) {
		AdminResponse adminResponse = new AdminResponse();
		ErrorResponse error = null;
		try {
			if (requestJson.getUser_id() > 0) {
				String sql = "select is_blocked from admin_user_login where user_id = ? ";
				BigDecimal status = DataAccessUtils.singleResult(jdbcTemplate.query(sql,
						new Object[] { requestJson.getUser_id() }, new SingleColumnRowMapper<BigDecimal>()));
				if (status != null) {
					AdminUsers adminUsers = new AdminUsers();
					adminUsers.setIs_blocked(status.intValue());
					adminResponse.setAdminUsersResult(adminUsers);
					error = new ErrorResponse();
				} else {
					error = new ErrorResponse(1, "Invalid Admin User.");
				}
			} else {
				error = new ErrorResponse(1);
			}

		} catch (Exception e) {
			e.printStackTrace();
			error = new ErrorResponse(1, e.getMessage());
		}
		adminResponse.setError(error);
		return adminResponse;
	}

	public AdminResponse checkTwoFactor(AdminUsers adminUsers) {
		AdminResponse adminResponse = new AdminResponse();
		ErrorResponse error = null;
		String sql = "select user_id,two_factor_auth_key from admin_user_login where user_name = ?";
		try {
			if (StringUtils.hasText(adminUsers.getUser_name()) && adminUsers.getSecurityCode() > 0) {
				Map<String, Object> verificationData = jdbcTemplate.queryForMap(sql, adminUsers.getUser_name());
				if (!verificationData.isEmpty()) {
					String securityCode = getTOTPCode(verificationData.get("two_factor_auth_key").toString());
					if (Integer.parseInt(securityCode) == adminUsers.getSecurityCode()) {
						adminUsers.setUser_id(verificationData.get("user_id") != null
								? ((BigDecimal) verificationData.get("user_id")).intValue()
								: 0);
						adminResponse.setAdminUsersResult(adminUsers);
						error = new ErrorResponse();
					} else {
						error = new ErrorResponse(5);
					}
				} else {
					error = new ErrorResponse(1, "Invalid Admin User.");
				}
			} else {
				error = new ErrorResponse(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
			error = new ErrorResponse(1, e.getMessage());
		}
		adminResponse.setError(error);
		return adminResponse;
	}

	public AdminResponse getTwoFactorKey(AdminUsers adminUsers) {
		AdminResponse adminResponse = new AdminResponse();
		ErrorResponse error = null;
		String keyCheck = "Select two_factor_auth_key,enabled_2fa from admin_user_login where user_id = ? and otp = ?";
		String sql = "update admin_user_login set two_factor_auth_key = ?, exist_2fa_key = 1 where user_id = ? and otp = ?  ";
		try {
			if (adminUsers.getUser_id() > 0 && adminUsers.getOtp() > 0) {
				Map<String, Object> verificationData = jdbcTemplate.queryForMap(keyCheck, adminUsers.getUser_id(),
						adminUsers.getOtp());
				if (verificationData.get("two_factor_auth_key") == null  || Integer.parseInt(verificationData.get("enabled_2fa").toString()) == 0) {
					String secretKey = getRandomSecretKey();
					int i = jdbcTemplate.update(sql, secretKey, adminUsers.getUser_id(), adminUsers.getOtp());
					if (i > 0) {
						sql = "update admin_user_login set otp = ? where user_id = ? ";
						jdbcTemplate.update(sql, 0, adminUsers.getUser_id());
						adminUsers.setTwoFactorAuthKey(secretKey);
						adminResponse.setAdminUsersResult(adminUsers);
						error = new ErrorResponse();
					}
				} else {
					String securityCode = getTOTPCode(verificationData.get("two_factor_auth_key").toString());
					if (Integer.parseInt(securityCode) == adminUsers.getSecurityCode()) {
						String secretKey = getRandomSecretKey();
						int i = jdbcTemplate.update(sql, secretKey, adminUsers.getUser_id(), adminUsers.getOtp());
						if (i > 0) {
							sql = "update admin_user_login set otp = ? where user_id = ? ";
							jdbcTemplate.update(sql, 0, adminUsers.getUser_id());
							adminUsers.setTwoFactorAuthKey(secretKey);
							adminResponse.setAdminUsersResult(adminUsers);
							error = new ErrorResponse();
						}
					} else {
						error = new ErrorResponse(1, "Authentication Failed.");
					}
				}
			} else {
				error = new ErrorResponse(1);
			}
		} catch (EmptyResultDataAccessException e) {
			e.printStackTrace();
			error = new ErrorResponse(1, "Invalid request.");
		} catch (Exception e) {
			e.printStackTrace();
			error = new ErrorResponse(1, e.getMessage());
		}
		adminResponse.setError(error);
		return adminResponse;
	}

	public AdminResponse change2FaStatus(AdminUsers adminUsers) {
		AdminResponse adminResponse = new AdminResponse();
		ErrorResponse error = null;
		String sql = "update admin_user_login set enabled_2fa = ? where user_id = ? and otp = ? ";
		try {
			if (adminUsers.getUser_id() > 0 && adminUsers.getOtp() > 0) {
				if (adminUsers.getEnabled2fa() == 1) {
					int i = jdbcTemplate.update(sql, adminUsers.getEnabled2fa(), adminUsers.getUser_id(),
							adminUsers.getOtp());
					if (i == 1) {
						sql = "update admin_user_login set otp = ? where user_id = ? ";
						jdbcTemplate.update(sql, 0, adminUsers.getUser_id());
						error = new ErrorResponse();
					} else {
						error = new ErrorResponse(1, "Wrong credential.");
					}
				} else {
					String keySql = "select two_factor_auth_key from admin_user_login where user_id = ? and otp = ?";
					String key = DataAccessUtils.singleResult(
							jdbcTemplate.query(keySql, new Object[] { adminUsers.getUser_id(), adminUsers.getOtp() },
									new SingleColumnRowMapper<String>()));
					if (key != null) {
						String securityCode = getTOTPCode(key);
						if (Integer.parseInt(securityCode) == adminUsers.getSecurityCode()) {
							int i = jdbcTemplate.update(sql, adminUsers.getEnabled2fa(), adminUsers.getUser_id(),
									adminUsers.getOtp());
							if (i == 1) {
								sql = "update admin_user_login set otp = ? where user_id = ? ";
								jdbcTemplate.update(sql, 0, adminUsers.getUser_id());
								error = new ErrorResponse();
							} else {
								error = new ErrorResponse(1, "Wrong credential.");
							}
						} else {
							error = new ErrorResponse(1, "Authentication Failed.");
						}
					} else {
						error = new ErrorResponse(1, "Security key does not exist.");
					}
				}
			} else {
				error = new ErrorResponse(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
			error = new ErrorResponse(1, e.getMessage());
		}
		adminResponse.setError(error);
		return adminResponse;
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

	public String getRandomSecretKey() {
		SecureRandom random = new SecureRandom();
		byte[] bytes = new byte[20];
		random.nextBytes(bytes);
		Base32 base32 = new Base32();
		String secretKey = base32.encodeToString(bytes);
		return secretKey.toUpperCase();
	}
}
