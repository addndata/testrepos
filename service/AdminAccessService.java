package com.project.admin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.admin.dao.AdminAccessDao;
import com.project.admin.model.AdminResponse;
import com.project.admin.model.AdminUsers;
import com.project.admin.model.AdminUsersResponse;
import com.project.admin.model.ErrorResponse;
import com.project.admin.model.GroupMaster;
import com.project.admin.model.RequestJson;

@Service
public class AdminAccessService {

	@Autowired
	private AdminAccessDao adminAccessDao;
	
	public AdminResponse AddAdminGroup(GroupMaster groupMaster){
		AdminResponse adminResponse=new AdminResponse();
		ErrorResponse errorResponse=new ErrorResponse();
		if(groupMaster.getGroup_name()!=null && !groupMaster.getGroup_name().isEmpty() && 
				groupMaster.getGroup_desc()!=null && !groupMaster.getGroup_desc().isEmpty() && 
				groupMaster.getGroup_prefix()!=null && !groupMaster.getGroup_prefix().isEmpty() && 
				groupMaster.getCreated_by()!=0 ){
			adminResponse=adminAccessDao.addAdminGroup(groupMaster);
			
		}else{
			errorResponse=errorResponse.GetErrorSet(1);
			adminResponse.setError(errorResponse);
		}
		return adminResponse;
	}
	
	public AdminResponse AddAdminRole(RequestJson requestJson) {
		AdminResponse adminResponse = new AdminResponse();
		ErrorResponse errorResponse = new ErrorResponse();
		if (requestJson.getGroup_id() != 0 && requestJson.getRole_name() != null
				&& !requestJson.getRole_name().isEmpty() && requestJson.getRole_desc() != null
				&& !requestJson.getRole_desc().isEmpty() && requestJson.getRole_prefix() != null
				&& !requestJson.getRole_prefix().isEmpty() && requestJson.getCreated_by() != 0) {
			adminResponse = adminAccessDao.addAdminRole(requestJson);

		} else {
			errorResponse = errorResponse.GetErrorSet(1);
			adminResponse.setError(errorResponse);
		}
		return adminResponse;
	}
	
	public AdminResponse GetAllGroups(GroupMaster groupMaster){
		AdminResponse adminResponse=new AdminResponse();
		ErrorResponse errorResponse=new ErrorResponse();
		if(groupMaster.getCreated_by()!=0){
		   adminResponse=adminAccessDao.getAllGroups(groupMaster);
		} else {
		   errorResponse=errorResponse.GetErrorSet(1);
		   adminResponse.setError(errorResponse);
		}
		return adminResponse;
	}
	
	public AdminResponse GetAllRoles(RequestJson requestJson){
		AdminResponse adminResponse=new AdminResponse();
		ErrorResponse errorResponse=new ErrorResponse();
		if(requestJson.getCreated_by()!=0  ){
			adminResponse=adminAccessDao.getAllRoles(requestJson);
			
		}else{
			errorResponse=errorResponse.GetErrorSet(1);
			adminResponse.setError(errorResponse);
		}
		return adminResponse;
	}
	
	public AdminResponse GetAllRolesByGroup(RequestJson requestJson){
		AdminResponse adminResponse=new AdminResponse();
		ErrorResponse errorResponse=new ErrorResponse();
		if(requestJson.getCreated_by()!=0  && requestJson.getGroup_id()!=0  ){
			adminResponse=adminAccessDao.GetAllRolesByGroup(requestJson);
			
		}else{
			errorResponse=errorResponse.GetErrorSet(1);
			adminResponse.setError(errorResponse);
		}
		return adminResponse;
	}
	
	public AdminResponse SetRoleModuleRelation(RequestJson requestJson){
		AdminResponse adminResponse=new AdminResponse();
		ErrorResponse errorResponse=new ErrorResponse();
		if(requestJson.getRole_id()!=0 && requestJson.getModule_id()!=0 && requestJson.getCreated_by()!=0 ){
			adminResponse=adminAccessDao.setRoleModuleRelation(requestJson);
			
		}else{
			errorResponse=errorResponse.GetErrorSet(1);
			adminResponse.setError(errorResponse);
		}
		return adminResponse;
	}
	
	public AdminResponse UpdateRoleModuleRelation(RequestJson requestJson){
		AdminResponse adminResponse=new AdminResponse();
		ErrorResponse errorResponse=new ErrorResponse();
		if(requestJson.getRm_id()!=0 && requestJson.getCreated_by()!=0 ){
			adminResponse=adminAccessDao.updateRoleModuleRelation(requestJson);
			
		}else{
			errorResponse=errorResponse.GetErrorSet(1);
			adminResponse.setError(errorResponse);
		}
		return adminResponse;
	}
	
	public AdminResponse GetAllRoleModuleRelation(RequestJson requestJson){
		AdminResponse adminResponse=new AdminResponse();
		ErrorResponse errorResponse=new ErrorResponse();
		if(requestJson.getCreated_by()!=0 ){
			adminResponse=adminAccessDao.getAllRoleModuleRelation(requestJson);
			
		}else{
			errorResponse=errorResponse.GetErrorSet(1);
			adminResponse.setError(errorResponse);
		}
		return adminResponse;
	}
	
	public AdminResponse GetAllRoleModuleRelationByRole(RequestJson requestJson){
		AdminResponse adminResponse=new AdminResponse();
		ErrorResponse errorResponse=new ErrorResponse();
		if(requestJson.getRole_id()!=0 && requestJson.getCreated_by()!=0 ){
			adminResponse=adminAccessDao.getAllRoleModuleRelationByRole(requestJson);
			
		}else{
			errorResponse=errorResponse.GetErrorSet(1);
			adminResponse.setError(errorResponse);
		}
		return adminResponse;
	}
	
	public AdminResponse SetRoleMethodRelation(RequestJson requestJson){
		AdminResponse adminResponse=new AdminResponse();
		ErrorResponse errorResponse=new ErrorResponse();
		if(requestJson.getRole_id()!=0 && requestJson.getMethod_id()!=0 && requestJson.getCreated_by()!=0 ){
			adminResponse=adminAccessDao.setRoleMethodRelation(requestJson);
			
		}else{
			errorResponse=errorResponse.GetErrorSet(1);
			adminResponse.setError(errorResponse);
		}
		return adminResponse;
	}
	
	public AdminResponse UpdateRoleMethodRelation(RequestJson requestJson){
		AdminResponse adminResponse=new AdminResponse();
		ErrorResponse errorResponse=new ErrorResponse();
		if(requestJson.getRm_id()!=0 && requestJson.getCreated_by()!=0 ){
			adminResponse=adminAccessDao.updateRoleMethodRelation(requestJson);
			
		}else{
			errorResponse=errorResponse.GetErrorSet(1);
			adminResponse.setError(errorResponse);
		}
		return adminResponse;
	}
	
	public AdminResponse GetAllRoleMethodRelation(RequestJson requestJson){
		AdminResponse adminResponse=new AdminResponse();
		ErrorResponse errorResponse=new ErrorResponse();
		if(requestJson.getCreated_by()!=0 ){
			adminResponse=adminAccessDao.getAllRoleMethodRelation(requestJson);
			
		}else{
			errorResponse=errorResponse.GetErrorSet(1);
			adminResponse.setError(errorResponse);
		}
		return adminResponse;
	}
	
	public AdminResponse GetAllRoleMethodRelationByRole(RequestJson requestJson){
		AdminResponse adminResponse=new AdminResponse();
		ErrorResponse errorResponse=new ErrorResponse();
		if(requestJson.getRole_id()!=0 && requestJson.getCreated_by()!=0 ){
			adminResponse=adminAccessDao.getAllRoleMethodRelationByRole(requestJson);
			
		}else{
			errorResponse=errorResponse.GetErrorSet(1);
			adminResponse.setError(errorResponse);
		}
		return adminResponse;
	}
	
	public AdminResponse GetAllModules(RequestJson requestJson){
		AdminResponse adminResponse=new AdminResponse();
		ErrorResponse errorResponse=new ErrorResponse();
		if(requestJson.getCreated_by()!=0 ){
			adminResponse=adminAccessDao.getAllModules(requestJson);
			
		}else{
			errorResponse=errorResponse.GetErrorSet(1);
			adminResponse.setError(errorResponse);
		}
		return adminResponse;
	}
	
	public AdminResponse GetAllMethods(RequestJson requestJson){
		AdminResponse adminResponse=new AdminResponse();
		ErrorResponse errorResponse=new ErrorResponse();
		if(requestJson.getCreated_by()!=0 ){
			adminResponse=adminAccessDao.getAllMethods(requestJson);
			
		}else{
			errorResponse=errorResponse.GetErrorSet(1);
			adminResponse.setError(errorResponse);
		}
		return adminResponse;
	}
	
	public AdminResponse GetAllMethodsByModule(RequestJson requestJson){
		AdminResponse adminResponse=new AdminResponse();
		ErrorResponse errorResponse=new ErrorResponse();
		if(requestJson.getCreated_by()!=0 && requestJson.getModule_id()!=0){
			adminResponse=adminAccessDao.getAllMethodsByModule(requestJson);
			
		}else{
			errorResponse=errorResponse.GetErrorSet(1);
			adminResponse.setError(errorResponse);
		}
		return adminResponse;
	}
	
	public AdminUsersResponse RegisterAdminUsers(AdminUsers adminUsers){
		AdminUsersResponse adminResponse=new AdminUsersResponse();
		ErrorResponse errorResponse=new ErrorResponse();
		if(adminUsers.getName()!=null && !adminUsers.getName().isEmpty() && adminUsers.getEmail()!=null && 
			 !adminUsers.getEmail().isEmpty() && adminUsers.getPhone_no()!=null && !adminUsers.getPhone_no().isEmpty()
			 && adminUsers.getPassword()!=null && !adminUsers.getPassword().isEmpty() && adminUsers.getCreated_by()!=0 ){
			adminResponse=adminAccessDao.registerAdminUsers(adminUsers);
			
		}else{
			errorResponse=errorResponse.GetErrorSet(1);
			adminResponse.setError(errorResponse);
		}
		return adminResponse;
	}
	
	public AdminUsersResponse ConfirmAdminUser(AdminUsers adminUsers){
		AdminUsersResponse adminResponse=new AdminUsersResponse();
		ErrorResponse errorResponse=new ErrorResponse();
		if(adminUsers.getUser_id()!=0 && adminUsers.getCreated_by()!=0 ){
			adminResponse=adminAccessDao.confirmAdminUser(adminUsers);
			
		}else{
			errorResponse=errorResponse.GetErrorSet(1);
			adminResponse.setError(errorResponse);
		}
		return adminResponse;
	}
	
	public AdminUsersResponse SetAdminUserRole(RequestJson requestJson){
		AdminUsersResponse adminResponse=new AdminUsersResponse();
		ErrorResponse errorResponse=new ErrorResponse();
		if(requestJson.getUser_id()!=0 && requestJson.getCreated_by()!=0 && requestJson.getRole_id()!=0){
			adminResponse=adminAccessDao.setAdminUserRole(requestJson);
			
		}else{
			errorResponse=errorResponse.GetErrorSet(1);
			adminResponse.setError(errorResponse);
		}
		return adminResponse;
	}
	
	public AdminResponse sendOTP(AdminUsers adminUsers) {
		AdminResponse adminResponse = new AdminResponse();
		ErrorResponse errorResponse=new ErrorResponse();
		if (adminUsers.getUser_name()!=null && !adminUsers.getUser_name().isEmpty()) {
			adminResponse=adminAccessDao.sendOTP(adminUsers);
		} else{
			errorResponse=errorResponse.GetErrorSet(1);
			adminResponse.setError(errorResponse);
		}
		return adminResponse;
	}
	
	public AdminResponse LoginAdmin(RequestJson requestJson){
		AdminResponse adminResponse=new AdminResponse();
		ErrorResponse errorResponse=new ErrorResponse();
		if(requestJson.getUser_name()!=null && !requestJson.getUser_name().isEmpty() && 
				requestJson.getPassword()!=null && !requestJson.getPassword().isEmpty()){
			adminResponse=adminAccessDao.loginAdmin(requestJson);
			
		}else{
			errorResponse=errorResponse.GetErrorSet(1);
			adminResponse.setError(errorResponse);
		}
		return adminResponse;
	}
	
	public AdminResponse LogoutAdmin(RequestJson requestJson){
		AdminResponse adminResponse=new AdminResponse();
		ErrorResponse errorResponse=new ErrorResponse();
		if(requestJson.getUser_name()!=null && !requestJson.getUser_name().isEmpty() ){
			adminResponse=adminAccessDao.logoutAdmin(requestJson);
			
		}else{
			errorResponse=errorResponse.GetErrorSet(1);
			adminResponse.setError(errorResponse);
		}
		return adminResponse;
	}
	
	public AdminResponse SetRoleModuleMethodRelation(RequestJson requestJson){
		AdminResponse adminResponse=new AdminResponse();
		ErrorResponse errorResponse=new ErrorResponse();
		if(requestJson.getRole_id()!=0 && requestJson.getCreated_by()!=0 && requestJson.getModule_list()!=null && 
				!requestJson.getModule_list().isEmpty() && requestJson.getMethod_list()!=null && 
				!requestJson.getMethod_list().isEmpty()){
			adminResponse=adminAccessDao.setRoleModuleMethodRelation(requestJson);
			
		}else{
			errorResponse=errorResponse.GetErrorSet(1);
			adminResponse.setError(errorResponse);
		}
		return adminResponse;
	}
	
	public AdminResponse GetAllAdminUsers(RequestJson requestJson){
		AdminResponse adminResponse=new AdminResponse();
		ErrorResponse errorResponse=new ErrorResponse();
		if(requestJson.getCreated_by()!=0 ){
			adminResponse=adminAccessDao.getAllAdminUsers(requestJson);
			
		}else{
			errorResponse=errorResponse.GetErrorSet(1);
			adminResponse.setError(errorResponse);
		}
		return adminResponse;
	}
	
	public AdminResponse BlockAdminUser(RequestJson requestJson){
		AdminResponse adminResponse=new AdminResponse();
		ErrorResponse errorResponse=new ErrorResponse();
		if(requestJson.getCreated_by()!=0 && requestJson.getUser_id()!=0){
			adminResponse=adminAccessDao.blockAdminUser(requestJson);
			
		}else{
			errorResponse=errorResponse.GetErrorSet(1);
			adminResponse.setError(errorResponse);
		}
		return adminResponse;
	}
	
	public AdminResponse UnblockAdminUser(RequestJson requestJson){
		AdminResponse adminResponse=new AdminResponse();
		ErrorResponse errorResponse=new ErrorResponse();
		if(requestJson.getCreated_by()!=0 && requestJson.getUser_id()!=0){
			adminResponse=adminAccessDao.unblockAdminUser(requestJson);
			
		}else{
			errorResponse=errorResponse.GetErrorSet(1);
			adminResponse.setError(errorResponse);
		}
		return adminResponse;
	}
	
	public AdminResponse ChangeAdminPhone(RequestJson requestJson){
		AdminResponse adminResponse=new AdminResponse();
		ErrorResponse errorResponse=new ErrorResponse();
		if(requestJson.getCreated_by()!=0 && requestJson.getUser_id()!=0 && requestJson.getPhone()!=null && 
				!requestJson.getPhone().isEmpty()){
			adminResponse=adminAccessDao.changeAdminPhone(requestJson);
			
		}else{
			errorResponse=errorResponse.GetErrorSet(1);
			adminResponse.setError(errorResponse);
		}
		return adminResponse;
	}
	
	public AdminResponse ChangeAdminPassword(RequestJson requestJson){
		AdminResponse adminResponse=new AdminResponse();
		ErrorResponse errorResponse=new ErrorResponse();
		if(requestJson.getCreated_by()!=0 && requestJson.getUser_id()!=0 && requestJson.getPassword()!=null && 
			!requestJson.getPassword().isEmpty()){
			adminResponse=adminAccessDao.changeAdminPassword(requestJson);
			
		}else{
			errorResponse=errorResponse.GetErrorSet(1);
			adminResponse.setError(errorResponse);
		}
		return adminResponse;
	}
	
	public AdminResponse BlockGroup(RequestJson requestJson){
		AdminResponse adminResponse=new AdminResponse();
		ErrorResponse errorResponse=new ErrorResponse();
		if(requestJson.getCreated_by()!=0 && requestJson.getGroup_id()!=0){
			adminResponse=adminAccessDao.blockGroup(requestJson);
			
		}else{
			errorResponse=errorResponse.GetErrorSet(1);
			adminResponse.setError(errorResponse);
		}
		return adminResponse;
	}
	
	public AdminResponse UnBlockGroup(RequestJson requestJson){
		AdminResponse adminResponse=new AdminResponse();
		ErrorResponse errorResponse=new ErrorResponse();
		if(requestJson.getCreated_by()!=0 && requestJson.getGroup_id()!=0){
			adminResponse=adminAccessDao.unBlockGroup(requestJson);
			
		}else{
			errorResponse=errorResponse.GetErrorSet(1);
			adminResponse.setError(errorResponse);
		}
		return adminResponse;
	}
	
	public AdminResponse BlockRole(RequestJson requestJson){
		AdminResponse adminResponse=new AdminResponse();
		ErrorResponse errorResponse=new ErrorResponse();
		if(requestJson.getCreated_by()!=0 && requestJson.getRole_id()!=0){
			adminResponse=adminAccessDao.blockRole(requestJson);
			
		}else{
			errorResponse=errorResponse.GetErrorSet(1);
			adminResponse.setError(errorResponse);
		}
		return adminResponse;
	}
	
	public AdminResponse UnBlockRole(RequestJson requestJson){
		AdminResponse adminResponse=new AdminResponse();
		ErrorResponse errorResponse=new ErrorResponse();
		if(requestJson.getCreated_by()!=0 && requestJson.getRole_id()!=0){
			adminResponse=adminAccessDao.unBlockRole(requestJson);
			
		}else{
			errorResponse=errorResponse.GetErrorSet(1);
			adminResponse.setError(errorResponse);
		}
		return adminResponse;
	}

	public AdminResponse changePassword(AdminUsers adminUsers) {
		return adminAccessDao.changePassword(adminUsers);
	}
	
	public AdminResponse adminUserAccountStatus(RequestJson requestJson) {
		return adminAccessDao.adminUserAccountStatus(requestJson);
	}
	
	public AdminResponse checkTwoFactor(AdminUsers adminUsers) {
		return adminAccessDao.checkTwoFactor(adminUsers);
	}

	public AdminResponse getTwoFactorKey(AdminUsers adminUsers) {
		return adminAccessDao.getTwoFactorKey(adminUsers);
	}

	public AdminResponse change2FaStatus(AdminUsers adminUsers) {
		return adminAccessDao.change2FaStatus(adminUsers);
	}
}
