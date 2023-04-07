package com.project.admin.model;

public class BankDetails {
  
	//private data members
	 private int bank_details_id;
	 private int user_id;
	 private String beneficiary_name;
	 private String bank_name;
	 private String bankAddress;
     private String accountType;
	 private String account_no;
	 private String institutionTransitNo;
	 private String swiftCode;
	 private String ifscCode;
	 private double verification_amount;
	 private String bank_cheque;
	 
	 
	//public member functions 
	public int getBank_details_id() {
		return bank_details_id;
	}
	public void setBank_details_id(int bank_details_id) {
		this.bank_details_id = bank_details_id;
	}
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	public String getBeneficiary_name() {
		return beneficiary_name;
	}
	public void setBeneficiary_name(String beneficiary_name) {
		this.beneficiary_name = beneficiary_name;
	}
	public String getBank_name() {
		return bank_name;
	}
	public void setBank_name(String bank_name) {
		this.bank_name = bank_name;
	}
	public String getBankAddress() {
		return bankAddress;
	}
	public void setBankAddress(String bankAddress) {
		this.bankAddress = bankAddress;
	}
	public String getAccountType() {
		return accountType;
	}
	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}
	public String getAccount_no() {
		return account_no;
	}
	public void setAccount_no(String account_no) {
		this.account_no = account_no;
	}
	public String getInstitutionTransitNo() {
		return institutionTransitNo;
	}
	public void setInstitutionTransitNo(String institutionTransitNo) {
		this.institutionTransitNo = institutionTransitNo;
	}
	public String getSwiftCode() {
		return swiftCode;
	}
	public void setSwiftCode(String swiftCode) {
		this.swiftCode = swiftCode;
	}
	public String getIfscCode() {
		return ifscCode;
	}
	public void setIfscCode(String ifscCode) {
		this.ifscCode = ifscCode;
	}
	public double getVerification_amount() {
		return verification_amount;
	}
	public void setVerification_amount(double verification_amount) {
		this.verification_amount = verification_amount;
	}
	public String getBank_cheque() {
		return bank_cheque;
	}
	public void setBank_cheque(String bank_cheque) {
		this.bank_cheque = bank_cheque;
	}
	
	

}
