package com.project.admin.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.project.admin.dao.AdminAccessDao;
import com.project.admin.model.AdminUsers;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private AdminAccessDao adminAccessDao;

	@Bean
    public PasswordEncoder getPasswordEncoder(){
        return new CustomPasswordEncoder();
    }
	
	@Override
	public UserDetails loadUserByUsername(String UserName) throws UsernameNotFoundException {
		AdminUsers adminuser=adminAccessDao.getUserCredential(UserName);
		GrantedAuthority authority = new SimpleGrantedAuthority(adminuser.getRole());
		return (UserDetails)new User(adminuser.getUser_name(), adminuser.getPassword(), Arrays.asList(authority));
	}
}
