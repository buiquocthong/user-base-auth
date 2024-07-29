package com.base.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Value("${spring.datasource.secondary.url}")
    private String primaryDbUrl;

    @Value("${spring.datasource.secondary.username}")
    private String primaryDbUsername;

    @Value("${spring.datasource.secondary.password}")
    private String primaryDbPassword;

    @Value("${spring.datasource.secondary.driver-class-name}")
    private String primaryDbDriverClassName;

    @Value("${oauth.datasource.url}")
    private String oauthDbUrl;

    @Value("${oauth.datasource.username}")
    private String oauthDbUsername;

    @Value("${oauth.datasource.password}")
    private String oauthDbPassword;

    @Value("${oauth.datasource.driver-class-name}")
    private String oauthDbDriverClassName;

    @Bean(name = "primaryDataSource")
    @Primary
    public DataSource primaryDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(primaryDbDriverClassName);
        dataSource.setUrl(primaryDbUrl);
        dataSource.setUsername(primaryDbUsername);
        dataSource.setPassword(primaryDbPassword);
        return dataSource;
    }

    @Bean(name = "oauthDataSource")
    public DataSource oauthDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(oauthDbDriverClassName);
        dataSource.setUrl(oauthDbUrl);
        dataSource.setUsername(oauthDbUsername);
        dataSource.setPassword(oauthDbPassword);
        return dataSource;
    }

    @Bean(name = "primaryJdbcTemplate")
    public JdbcTemplate primaryJdbcTemplate() {
        return new JdbcTemplate(primaryDataSource());
    }

    @Bean(name = "oauthJdbcTemplate")
    public JdbcTemplate oauthJdbcTemplate() {
        return new JdbcTemplate(oauthDataSource());
    }

}
