package com.project.admin.config;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import org.springframework.security.crypto.password.PasswordEncoder;

public class CustomPasswordEncoder implements PasswordEncoder{

	 @Override
	 public String encode(CharSequence rawPassword) {
		 String encoded_password="";
	    return encoded_password;
	 }
	 
	 @Override
	 public boolean matches(CharSequence rawPassword, String encodedPassword) {
		 
		 String encoded_password="";
		 String text=rawPassword.toString();
		 MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));			
			encoded_password = Base64.getEncoder().encodeToString(hash);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
			return encoded_password.equals(encodedPassword);
	 }
	
}
