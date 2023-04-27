package com.project.admin.model;

import java.util.HashMap;
import java.util.Map;

public class ErrorResponse {
 
	//private data member 
	private int error_data;
	private String error_msg;
	
	
	//public member functions
	public int getError_data() {
		return error_data;
	}
	public void setError_data(int error_data) {
		this.error_data = error_data;
	}
	public String getError_msg() {
		return error_msg;
	}
	public void setError_msg(String error_msg) {
		this.error_msg = error_msg;
	}
	
	public ErrorResponse() {
		this.setError_data(0);
		this.setError_msg(""); 
	}
	
	public ErrorResponse(int errorData,String errorMsg) {
		this.setError_data(errorData);
		this.setError_msg(errorMsg); 
	}
	
	public ErrorResponse(int errorCode) {
		this.setError_data(1); 
		this.setError_msg(getErrorMessage(errorCode)); 
	}
	
	public String getErrorMessage(int errorCode) {
		String errorMessage = "";
		switch (errorCode) {
		case 1:
			errorMessage = "Mandatory fields missing.";break;
		case 2:
			errorMessage = "UserId missing.";break;			
		case 3:
			errorMessage = "EmailId missing.";break;		
		case 4:
			errorMessage = "Password missing.";break;	
		case 5:
			errorMessage = "Invalid OTP";break;	
		case 6:
			errorMessage = "Incorrect Email or Password";break;
		case 7:
			errorMessage = "Incorrect Email";break;
		case 8:
			errorMessage = "MPin missing.";break;
		case 9:
			errorMessage = "Internal Server Error.";break;	
		case 10:
			errorMessage = "Amount field can not be zero!!";break;	
		case 11:
			errorMessage = "Internal Server Error.";break;
		default:
			errorMessage = "General Error";break;	
		}
		return errorMessage;
	}
	
	public ErrorResponse GetErrorSet(int errorcode)
	{
		ErrorResponse errorResponse=new ErrorResponse();
		errorResponse.setError_data(1);
		switch (errorcode) {
		case 1:
			errorResponse.setError_msg("Mandatory fields missing");break;		
		case 2:
			errorResponse.setError_msg("UserId missing.");break;			
		case 3:
			errorResponse.setError_msg("EmailId missing.");break;		
		case 4:
			errorResponse.setError_msg("Password missing.");break;	
		case 5:
			errorResponse.setError_msg("Invalid OTP");break;	
		case 6:
			errorResponse.setError_msg("Incorrect Email or Password");break;
		case 7:
			errorResponse.setError_msg("Incorrect Email");break;
		case 8:
			errorResponse.setError_msg("MPin missing.");break;
		default:
			errorResponse.setError_msg("General Error");break;	
		}
		
		return errorResponse;
		
	}
	
	public static final Map<String, String> 
    RECAPTCHA_ERROR_CODE = new HashMap<>();
 
	  static {
	    RECAPTCHA_ERROR_CODE.put("missing-input-secret", 
	        "The secret parameter is missing");
	    RECAPTCHA_ERROR_CODE.put("invalid-input-secret", 
	        "The secret parameter is invalid or malformed");
	    RECAPTCHA_ERROR_CODE.put("missing-input-response", 
	        "The response parameter is missing");
	    RECAPTCHA_ERROR_CODE.put("invalid-input-response", 
	        "The response parameter is invalid or malformed");
	    RECAPTCHA_ERROR_CODE.put("bad-request", 
	        "The request is invalid or malformed");
	  }
}
