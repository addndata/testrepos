package com.project.admin;

import java.util.TimeZone;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.project.admin.config.CustomUserDetailsService;
import com.project.admin.controller.SimpleCORSFilter;

@SpringBootApplication
@ComponentScan({"com.project.admin.controller","com.project.admin.service","com.project.admin.model","com.project.admin.dao","com.project.admin.config"})
public class AdminServiceApplication extends SpringBootServletInitializer{
	@Autowired
	CustomUserDetailsService customUserDetailsService;
	@Autowired
    private PasswordEncoder passwordEncoder;
	
	@PostConstruct
	void started() {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}
	@Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
	
	@Bean
	@Order(Ordered.HIGHEST_PRECEDENCE)
	public SimpleCORSFilter simpleCORSFilter(){
		SimpleCORSFilter sc = new SimpleCORSFilter();
		return sc;
	}
	
	@Autowired
	public void authenticationManager(AuthenticationManagerBuilder builder) throws Exception
	{
		builder.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder);
	}
	
	public static void main(String[] args) {
		SpringApplication.run(AdminServiceApplication.class, args);
	}
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(AdminServiceApplication.class);
	}

}
