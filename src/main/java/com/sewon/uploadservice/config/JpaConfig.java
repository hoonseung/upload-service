package com.sewon.uploadservice.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.PlatformTransactionManager;


/*
@EnableJpaAuditing
@EnableJpaRepositories(
    basePackages = "com.sewon.uploadservice.repository",
    entityManagerFactoryRef = "postgreSql2EntityManagerFactory",
    transactionManagerRef = "postgreSql2TransactionManager"
)*/

public class JpaConfig {


   /* @Bean
    @Primary
    public EntityManagerFactory postgreSql2EntityManagerFactory(@Qualifier("postgresqlDataSource") DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(dataSource);
        factory.setPackagesToScan("com.sewon.uploadservice.model");
            factory.setPersistenceUnitName("postgresql2");
        factory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        factory.afterPropertiesSet();
        return factory.getObject();
    }


    @Bean
    public PlatformTransactionManager postgreSql2TransactionManager(
        @Qualifier("postgreSql2EntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }*/


}
