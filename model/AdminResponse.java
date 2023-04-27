package com.project.admin.model;

import java.util.List;

import com.project.admin.model.GroupMaster.MethodMaster;
import com.project.admin.model.GroupMaster.ModuleMaster;
import com.project.admin.model.GroupMaster.RoleMaster;
import com.project.admin.model.GroupMaster.RoleMethodRelation;
import com.project.admin.model.GroupMaster.RoleModuleRelation;

public class AdminResponse {

	private ErrorResponse Error;
	private AdminUsers AdminUsersResult;
	private List<AdminUsers> AdminUsersList;
	private List<GroupMaster> groupMasterList;
	private List<RoleMaster> roleMasterList;
	private List<ModuleMaster> moduleMasterList;
	private List<MethodMaster> methodMasterList;
	private List<RoleModuleRelation> roleModuleRelationList;
	private List<RoleMethodRelation> roleMethodRelationList;
	
	public ErrorResponse getError() {
		return Error;
	}
	public void setError(ErrorResponse error) {
		Error = error;
	}
	public AdminUsers getAdminUsersResult() {
		return AdminUsersResult;
	}
	public void setAdminUsersResult(AdminUsers adminUsersResult) {
		AdminUsersResult = adminUsersResult;
	}
	public List<AdminUsers> getAdminUsersList() {
		return AdminUsersList;
	}
	public void setAdminUsersList(List<AdminUsers> adminUsersList) {
		AdminUsersList = adminUsersList;
	}
	public List<GroupMaster> getGroupMasterList() {
		return groupMasterList;
	}
	public void setGroupMasterList(List<GroupMaster> groupMasterList) {
		this.groupMasterList = groupMasterList;
	}
	public List<RoleMaster> getRoleMasterList() {
		return roleMasterList;
	}
	public void setRoleMasterList(List<RoleMaster> roleMasterList) {
		this.roleMasterList = roleMasterList;
	}
	public List<ModuleMaster> getModuleMasterList() {
		return moduleMasterList;
	}
	public void setModuleMasterList(List<ModuleMaster> moduleMasterList) {
		this.moduleMasterList = moduleMasterList;
	}
	public List<MethodMaster> getMethodMasterList() {
		return methodMasterList;
	}
	public void setMethodMasterList(List<MethodMaster> methodMasterList) {
		this.methodMasterList = methodMasterList;
	}
	public List<RoleModuleRelation> getRoleModuleRelationList() {
		return roleModuleRelationList;
	}
	public void setRoleModuleRelationList(List<RoleModuleRelation> roleModuleRelationList) {
		this.roleModuleRelationList = roleModuleRelationList;
	}
	public List<RoleMethodRelation> getRoleMethodRelationList() {
		return roleMethodRelationList;
	}
	public void setRoleMethodRelationList(List<RoleMethodRelation> roleMethodRelationList) {
		this.roleMethodRelationList = roleMethodRelationList;
	} 
	
	
}
