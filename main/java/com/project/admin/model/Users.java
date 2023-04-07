package com.project.admin.model;

public class Users {

	//private data members
	private int user_id;
	private String uuid;
	private String firstName;
	private String middleName;
	private String lastName;
	private String address;
	private String state;
	private String city;
	private String zip;
	private String country;
	private String phone;
	private String password;
	private String new_password;
	private String mpin;
	private String new_mpin;
	private int otp;
	private String email;
	private String ssn;
	private String profile_pic;
	private String address_proof_doc;
	private String idProofFront;
	private String idProofBack;
	private String secure_token;
	private String otp_token;
	private String role;
	private String android_device_token;
	private String ios_device_token;
	private String created;
	private int status;
	private int user_docs_status;
	private String bank_details_status;
	private int page_no;
	private int no_of_items_per_page;
	private int submitted;
	private String search_string;
	private String message;
	private int buy_limit;
	private int sell_limit;
	private int send_limit;
	private int receive_limit;
	private int fiatDeposit;
	private int futuresTrade;
	private String blockedBy;
	private String blockedOn;
	private String unblockedBy;
	private String unblockedOn;
	private int tierGroup;
	private int adminUserId;
	//public member functions
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getMiddleName() {
		return middleName;
	}
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getNew_password() {
		return new_password;
	}
	public void setNew_password(String new_password) {
		this.new_password = new_password;
	}
	public String getNew_mpin() {
		return new_mpin;
	}
	public void setNew_mpin(String new_mpin) {
		this.new_mpin = new_mpin;
	}
	public String getMpin() {
		return mpin;
	}
	public void setMpin(String mpin) {
		this.mpin = mpin;
	}
	public int getOtp() {
		return otp;
	}
	public void setOtp(int otp) {
		this.otp = otp;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getSsn() {
		return ssn;
	}
	public void setSsn(String ssn) {
		this.ssn = ssn;
	}
	public String getProfile_pic() {
		return profile_pic;
	}
	public void setProfile_pic(String profile_pic) {
		this.profile_pic = profile_pic;
	}
	public String getAddress_proof_doc() {
		return address_proof_doc;
	}
	public void setAddress_proof_doc(String address_proof_doc) {
		this.address_proof_doc = address_proof_doc;
	}
	public String getIdProofFront() {
		return idProofFront;
	}
	public void setIdProofFront(String idProofFront) {
		this.idProofFront = idProofFront;
	}
	public String getIdProofBack() {
		return idProofBack;
	}
	public void setIdProofBack(String idProofBack) {
		this.idProofBack = idProofBack;
	}
	public String getSecure_token() {
		return secure_token;
	}
	public void setSecure_token(String secure_token) {
		this.secure_token = secure_token;
	}
	
	public String getOtp_token() {
		return otp_token;
	}
	public void setOtp_token(String otp_token) {
		this.otp_token = otp_token;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	
	public String getAndroid_device_token() {
		return android_device_token;
	}
	public void setAndroid_device_token(String android_device_token) {
		this.android_device_token = android_device_token;
	}
	public String getIos_device_token() {
		return ios_device_token;
	}
	public void setIos_device_token(String ios_device_token) {
		this.ios_device_token = ios_device_token;
	}
	public String getCreated() {
		return created;
	}
	public void setCreated(String created) {
		this.created = created;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
	public int getUser_docs_status() {
		return user_docs_status;
	}
	public void setUser_docs_status(int user_docs_status) {
		this.user_docs_status = user_docs_status;
	}
	public String getBank_details_status() {
		return bank_details_status;
	}
	public void setBank_details_status(String bank_details_status) {
		this.bank_details_status = bank_details_status;
	}
	public int getPage_no() {
		return page_no;
	}
	public void setPage_no(int page_no) {
		this.page_no = page_no;
	}
	public int getNo_of_items_per_page() {
		return no_of_items_per_page;
	}
	public void setNo_of_items_per_page(int no_of_items_per_page) {
		this.no_of_items_per_page = no_of_items_per_page;
	}
	public int getSubmitted() {
		return submitted;
	}
	public void setSubmitted(int submitted) {
		this.submitted = submitted;
	}
	public String getSearch_string() {
		return search_string;
	}
	public void setSearch_string(String search_string) {
		this.search_string = search_string;
	}
	public int getBuy_limit() {
		return buy_limit;
	}
	public void setBuy_limit(int buy_limit) {
		this.buy_limit = buy_limit;
	}
	public int getSell_limit() {
		return sell_limit;
	}
	public void setSell_limit(int sell_limit) {
		this.sell_limit = sell_limit;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public int getSend_limit() {
		return send_limit;
	}
	public void setSend_limit(int send_limit) {
		this.send_limit = send_limit;
	}
	public int getReceive_limit() {
		return receive_limit;
	}
	public void setReceive_limit(int receive_limit) {
		this.receive_limit = receive_limit;
	}
	public int getFiatDeposit() {
		return fiatDeposit;
	}
	public void setFiatDeposit(int fiatDeposit) {
		this.fiatDeposit = fiatDeposit;
	}
	public int getFuturesTrade() {
		return futuresTrade;
	}
	public void setFuturesTrade(int futuresTrade) {
		this.futuresTrade = futuresTrade;
	}
	public String getBlockedBy() {
		return blockedBy;
	}
	public void setBlockedBy(String blockedBy) {
		this.blockedBy = blockedBy;
	}
	public String getBlockedOn() {
		return blockedOn;
	}
	public void setBlockedOn(String blockedOn) {
		this.blockedOn = blockedOn;
	}
	public String getUnblockedBy() {
		return unblockedBy;
	}
	public void setUnblockedBy(String unblockedBy) {
		this.unblockedBy = unblockedBy;
	}
	public String getUnblockedOn() {
		return unblockedOn;
	}
	public void setUnblockedOn(String unblockedOn) {
		this.unblockedOn = unblockedOn;
	}
	public int getTierGroup() {
		return tierGroup;
	}
	public void setTierGroup(int tierGroup) {
		this.tierGroup = tierGroup;
	}
	public int getAdminUserId() {
		return adminUserId;
	}
	public void setAdminUserId(int adminUserId) {
		this.adminUserId = adminUserId;
	}
}
