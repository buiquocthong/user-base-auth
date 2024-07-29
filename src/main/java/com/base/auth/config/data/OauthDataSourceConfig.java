//package com.base.auth.config.data;
//
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.boot.jdbc.DataSourceBuilder;
//import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
//import org.springframework.orm.jpa.JpaTransactionManager;
//import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
//import org.springframework.transaction.PlatformTransactionManager;
//import org.springframework.transaction.annotation.EnableTransactionManagement;
//
//import javax.persistence.EntityManagerFactory;
//import javax.sql.DataSource;
//
//@Configuration
//@EnableTransactionManagement
//@EnableJpaRepositories(
//        basePackages = "com.example.oauth.repository", // Thay đổi đường dẫn này
//        entityManagerFactoryRef = "oauthEntityManagerFactory",
//        transactionManagerRef = "oauthTransactionManager"
//)
//public class OauthDataSourceConfig {
//
//    @Primary
//    @Bean(name = "oauthDataSource")
//    @ConfigurationProperties(prefix = "spring.datasource")
//    public DataSource oauthDataSource() {
//        return DataSourceBuilder.create().build();
//    }
//
//    @Primary
//    @Bean(name = "oauthEntityManagerFactory")
//    public LocalContainerEntityManagerFactoryBean oauthEntityManagerFactory(EntityManagerFactoryBuilder builder,
//                                                                            @Qualifier("oauthDataSource") DataSource dataSource) {
//        return builder
//                .dataSource(dataSource)
//                .packages("com.example.oauth.entity") // Thay đổi đường dẫn này
//                .persistenceUnit("oauth")
//                .build();
//    }
//
//    @Primary
//    @Bean(name = "oauthTransactionManager")
//    public PlatformTransactionManager oauthTransactionManager(@Qualifier("oauthEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
//        return new JpaTransactionManager(entityManagerFactory);
//    }
//}
//
