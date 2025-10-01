package com.sewon.uploadservice.config;

import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.flyway.FlywayDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSourceConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.mssql")
    public DataSource mssqlDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.postgresql1")
    public DataSource postgresqlDataSourceByMes() {
        return DataSourceBuilder.create().build();
    }

    @FlywayDataSource
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.postgresql2")
    public DataSource postgresqlDataSource() {
        return DataSourceBuilder.create().build();
    }
}
