package com.project.admin.dao;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class MyJdbcDaoSupport extends JdbcDaoSupport {
    @Autowired private DataSource dataSource;
    @PostConstruct
    private void initialize() {
        setDataSource(dataSource);
    }
}