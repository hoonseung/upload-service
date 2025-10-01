package com.sewon.uploadservice.config;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class TransactionManagerConfig {

    //@Bean
    public PlatformTransactionManager mssqlTransactionManager(@Qualifier("mssqlDataSource") DataSource mssqlDataSource) {
        return new DataSourceTransactionManager(mssqlDataSource);
    }

    //@Bean
    public PlatformTransactionManager postgresqlTransactionManagerByMes(@Qualifier("postgresqlDataSourceByMes") DataSource postgresqlDataSourceByMes) {
        return new DataSourceTransactionManager(postgresqlDataSourceByMes);
    }

    @Bean
    public PlatformTransactionManager postgresqlTransactionManager(@Qualifier("postgresqlDataSource") DataSource postgresqlDataSource) {
        return new DataSourceTransactionManager(postgresqlDataSource);
    }
}
