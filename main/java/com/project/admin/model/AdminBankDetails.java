package com.project.admin.model;

public class AdminBankDetails {
	private int bankId;
	private String bankName;
	private String beneficiaryName;
	private String accountNo;
	private String ibanNo;
	private String institutionTransitNo;
	private String swiftCode;
	private String address;
	public int getBankId() {
		return bankId;
	}
	public void setBankId(int bankId) {
		this.bankId = bankId;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getBeneficiaryName() {
		return beneficiaryName;
	}
	public void setBeneficiaryName(String beneficiaryName) {
		this.beneficiaryName = beneficiaryName;
	}
	public String getAccountNo() {
		return accountNo;
	}
	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}
	public String getIbanNo() {
		return ibanNo;
	}
	public void setIbanNo(String ibanNo) {
		this.ibanNo = ibanNo;
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
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
}
