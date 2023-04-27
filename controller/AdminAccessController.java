package com.project.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.admin.model.AdminResponse;
import com.project.admin.model.AdminUsers;
import com.project.admin.model.AdminUsersResponse;
import com.project.admin.model.GroupMaster;
import com.project.admin.model.RequestJson;
import com.project.admin.service.AdminAccessService;

@RestController
@RequestMapping("/adminAccess")
public class AdminAccessController {

	@Autowired
	private AdminAccessService adminAccessService;
	
	@PostMapping(value="/AddAdminGroup")
	public AdminResponse addAdminGroup(@RequestBody GroupMaster groupMaster){
		return adminAccessService.AddAdminGroup(groupMaster);
	}
	
	@PostMapping(value="/GetAllGroups")
	public AdminResponse getAllGroups(@RequestBody GroupMaster groupMaster){
		return adminAccessService.GetAllGroups(groupMaster);
	}
	
	@PostMapping(value="/AddAdminRole")
	public AdminResponse addAdminRole(@RequestBody RequestJson requestJson){
		return adminAccessService.AddAdminRole(requestJson);
	}

	@PostMapping(value="/GetAllRoles")
	public AdminResponse getAllRoles(@RequestBody RequestJson requestJson){
		return adminAccessService.GetAllRoles(requestJson);
	}
	
	@PostMapping(value="/GetAllRolesByGroup")
	public AdminResponse getAllRolesByGroup(@RequestBody RequestJson requestJson){
		return adminAccessService.GetAllRolesByGroup(requestJson);
	}
	
	@PostMapping(value="/SetRoleModuleRelation")
	public AdminResponse setRoleModuleRelation(@RequestBody RequestJson requestJson){
		return adminAccessService.SetRoleModuleRelation(requestJson);
	}
	
	@PostMapping(value="/UpdateRoleModuleRelation")
	public AdminResponse updateRoleModuleRelation(@RequestBody RequestJson requestJson){
		return adminAccessService.UpdateRoleModuleRelation(requestJson);
	}
	
	@PostMapping(value="/GetAllRoleModuleRelation")
	public AdminResponse getAllRoleModuleRelation(@RequestBody RequestJson requestJson){
		return adminAccessService.GetAllRoleModuleRelation(requestJson);
	}
	
	@PostMapping(value="/GetAllRoleModuleRelationByRole")
	public AdminResponse getAllRoleModuleRelationByRole(@RequestBody RequestJson requestJson){
		return adminAccessService.GetAllRoleModuleRelationByRole(requestJson);
	}
	
	@PostMapping(value="/SetRoleMethodRelation")
	public AdminResponse setRoleMethodRelation(@RequestBody RequestJson requestJson){
		return adminAccessService.SetRoleMethodRelation(requestJson);
	}
	
	@PostMapping(value="/UpdateRoleMethodRelation")
	public AdminResponse updateRoleMethodRelation(@RequestBody RequestJson requestJson){
		return adminAccessService.UpdateRoleMethodRelation(requestJson);
	}
	
	@PostMapping(value="/GetAllRoleMethodRelation")
	public AdminResponse getAllRoleMethodRelation(@RequestBody RequestJson requestJson){
		return adminAccessService.GetAllRoleMethodRelation(requestJson);
	}
	
	@PostMapping(value="/GetAllRoleMethodRelationByRole")
	public AdminResponse getAllRoleMethodRelationByRole(@RequestBody RequestJson requestJson){
		return adminAccessService.GetAllRoleMethodRelationByRole(requestJson);
	}
	
	@PostMapping(value="/GetAllModules")
	public AdminResponse getAllModules(@RequestBody RequestJson requestJson){
		return adminAccessService.GetAllModules(requestJson);
	}
	
	@PostMapping(value="/GetAllMethods")
	public AdminResponse getAllMethods(@RequestBody RequestJson requestJson){
		return adminAccessService.GetAllMethods(requestJson);
	}
	
	@PostMapping(value="/GetAllMethodsByModule")
	public AdminResponse getAllMethodsByModule(@RequestBody RequestJson requestJson){
		return adminAccessService.GetAllMethodsByModule(requestJson);
	}

	/*@PostMapping(value="/RegisterAdminUsers")
	public AdminUsersResponse registerAdminUser(@RequestBody AdminUsers adminUsers){
		return adminAccessService.RegisterAdminUsers(adminUsers);
	}
	
	@PostMapping(value="/ConfirmAdminUser")
	public AdminUsersResponse confirmAdminUser(@RequestBody AdminUsers adminUsers){
		return adminAccessService.ConfirmAdminUser(adminUsers);
	}
	
	@PostMapping(value="/SetAdminUserRole")
	public AdminUsersResponse setAdminUserRole(@RequestBody RequestJson requestJson){
		return adminAccessService.SetAdminUserRole(requestJson);
	}*/
	
	@PostMapping(value = "/SendOTP")
	public AdminResponse sendOTP(@RequestBody AdminUsers adminUsers) {
		return adminAccessService.sendOTP(adminUsers);
	}
	
	@PostMapping(value="/LoginAdmin")
	public AdminResponse loginAdmin(@RequestBody RequestJson requestJson){
		return adminAccessService.LoginAdmin(requestJson);
	}
	
	@PostMapping(value="/LogoutAdmin")
	public AdminResponse logoutAdmin(@RequestBody RequestJson requestJson){
		return adminAccessService.LogoutAdmin(requestJson);
	}
	
	@PostMapping(value="/SetRoleModuleMethodRelation")
	public AdminResponse setRoleModuleMethodRelation(@RequestBody RequestJson requestJson){
		return adminAccessService.SetRoleModuleMethodRelation(requestJson);
	}
	
	@PostMapping(value="/GetAllAdminUsers")
	public AdminResponse getAllAdminUsers(@RequestBody RequestJson requestJson){
		return adminAccessService.GetAllAdminUsers(requestJson);
	}
	
	@PostMapping(value="/BlockAdminUser")
	public AdminResponse blockAdminUser(@RequestBody RequestJson requestJson){
		return adminAccessService.BlockAdminUser(requestJson);
	}
	
	@PostMapping(value="/UnblockAdminUser")
	public AdminResponse unblockAdminUser(@RequestBody RequestJson requestJson){
		return adminAccessService.UnblockAdminUser(requestJson);
	}
	
	@PostMapping(value="/ChangeAdminPhone")
	public AdminResponse changeAdminPhone(@RequestBody RequestJson requestJson){
		return adminAccessService.ChangeAdminPhone(requestJson);
	}
	
	/*
	 * @PostMapping(value="/ChangeAdminPassword") public AdminResponse
	 * changeAdminPassword(@RequestBody RequestJson requestJson){ return
	 * adminAccessService.ChangeAdminPassword(requestJson); }
	 */
	
	@PostMapping(value="/BlockGroup")
	public AdminResponse blockGroup(@RequestBody RequestJson requestJson){
		return adminAccessService.BlockGroup(requestJson);
	}
	
	@PostMapping(value="/UnBlockGroup")
	public AdminResponse unBlockGroup(@RequestBody RequestJson requestJson){
		return adminAccessService.UnBlockGroup(requestJson);
	}
	
	@PostMapping(value="/BlockRole")
	public AdminResponse blockRole(@RequestBody RequestJson requestJson){
		return adminAccessService.BlockRole(requestJson);
	}
	
	@PostMapping(value="/UnBlockRole")
	public AdminResponse unBlockRole(@RequestBody RequestJson requestJson){
		return adminAccessService.UnBlockRole(requestJson);
	}
	
	@PostMapping(value = "/changePassword")
	public AdminResponse changePassword(@RequestBody AdminUsers adminUsers) {
		return adminAccessService.changePassword(adminUsers);
	}
	
	@PostMapping(value="/adminUserAccountStatus")
	public AdminResponse adminUserAccountStatus(@RequestBody RequestJson requestJson){
		return adminAccessService.adminUserAccountStatus(requestJson);
	}
	
	@PostMapping(value = "/CheckTwoFactor")
	public AdminResponse checkTwoFactor(@RequestBody AdminUsers adminUsers) {
		return adminAccessService.checkTwoFactor(adminUsers);
	}
	
	@PostMapping(value = "/GetTwoFactorykey")
	public AdminResponse getTwoFactorKey(@RequestBody AdminUsers adminUsers) {
		return adminAccessService.getTwoFactorKey(adminUsers);
	}
	
	@PostMapping(value = "/change2FaStatus")
	public AdminResponse change2FaStatus(@RequestBody AdminUsers adminUsers) {
		return adminAccessService.change2FaStatus(adminUsers);
	}
}
