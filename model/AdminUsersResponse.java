package com.project.admin.model;

import java.util.List;

public class AdminUsersResponse {

	private ErrorResponse Error;
	private AdminUsers AdminUsersResult;
	private List<Users> UsersListResult;
	private BankDetails BankDetailsResult;
	private CustomerLedger CustomerLedgeResult;
	private List<DesignationMaster> DesignationMastersList;
	private List<MenuMaster> MenuMastersList;
	private int totalcount;
	private Franchise franchiseResult;
	private List<Franchise> franchiseList;
	FranchiseBankDetails franchiseBankDetails;
	
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
	public List<Users> getUsersListResult() {
		return UsersListResult;
	}
	public void setUsersListResult(List<Users> usersListResult) {
		UsersListResult = usersListResult;
	}	
	public BankDetails getBankDetailsResult() {
		return BankDetailsResult;
	}
	public void setBankDetailsResult(BankDetails bankDetailsResult) {
		BankDetailsResult = bankDetailsResult;
	}	
	public CustomerLedger getCustomerLedgeResult() {
		return CustomerLedgeResult;
	}
	public void setCustomerLedgeResult(CustomerLedger customerLedgeResult) {
		CustomerLedgeResult = customerLedgeResult;
	}
	public List<DesignationMaster> getDesignationMastersList() {
		return DesignationMastersList;
	}
	public void setDesignationMastersList(List<DesignationMaster> designationMastersList) {
		DesignationMastersList = designationMastersList;
	}
	public List<MenuMaster> getMenuMastersList() {
		return MenuMastersList;
	}
	public void setMenuMastersList(List<MenuMaster> menuMastersList) {
		MenuMastersList = menuMastersList;
	}
	public int getTotalcount() {
		return totalcount;
	}
	public void setTotalcount(int totalcount) {
		this.totalcount = totalcount;
	}
	public Franchise getFranchiseResult() {
		return franchiseResult;
	}
	public void setFranchiseResult(Franchise franchiseResult) {
		this.franchiseResult = franchiseResult;
	}
	public List<Franchise> getFranchiseList() {
		return franchiseList;
	}
	public void setFranchiseList(List<Franchise> franchiseList) {
		this.franchiseList = franchiseList;
	}
	public FranchiseBankDetails getFranchiseBankDetails() {
		return franchiseBankDetails;
	}
	public void setFranchiseBankDetails(FranchiseBankDetails franchiseBankDetails) {
		this.franchiseBankDetails = franchiseBankDetails;
	}
}
