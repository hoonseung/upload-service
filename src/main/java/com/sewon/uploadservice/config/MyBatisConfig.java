package com.sewon.uploadservice.config;

import javax.sql.DataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.LocalDateTimeTypeHandler;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

@Configuration
@MapperScan(basePackages = "com.sewon.uploadservice.repository.erp", sqlSessionFactoryRef = "mssqlSessionFactory")
@MapperScan(basePackages = "com.sewon.uploadservice.repository.mes", sqlSessionFactoryRef = "postgresqlSessionFactory0")
@MapperScan(basePackages = "com.sewon.uploadservice.repository.car", sqlSessionFactoryRef = "postgresqlSessionFactory1")
public class MyBatisConfig {

    @Bean
    public SqlSessionFactory mssqlSessionFactory(DataSource mssqlDataSource) throws Exception {
        SqlSessionFactoryBean sessionFactoryBean = getSessionFactoryBean(
            mssqlDataSource,
            "classpath:mapper/mssql/*.xml"
        );

        return sessionFactoryBean.getObject();
    }

    @Bean
    public SqlSessionFactory postgresqlSessionFactory0(@Qualifier("postgresqlDataSourceByMes") DataSource postgresqlDataSourceByMes)
        throws Exception {
        SqlSessionFactoryBean sessionFactoryBean = getSessionFactoryBean(
            postgresqlDataSourceByMes,
            "classpath:mapper/postgresql/*.xml"
        );
        return sessionFactoryBean.getObject();
    }

    @Bean
    public SqlSessionFactory postgresqlSessionFactory1(@Qualifier("postgresqlDataSource") DataSource postgresqlDataSource)
        throws Exception {
        SqlSessionFactoryBean sessionFactoryBean = getSessionFactoryBean(
            postgresqlDataSource,
            "classpath:mapper/postgresql/*.xml"
        );
        sessionFactoryBean.setTypeHandlers(new LocalDateTimeTypeHandler());
        return sessionFactoryBean.getObject();
    }

    private SqlSessionFactoryBean getSessionFactoryBean(DataSource dataSource, String path)
        throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(
            path
        ));
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setCacheEnabled(true);
        configuration.setLazyLoadingEnabled(true);
        factoryBean.setConfiguration(configuration);


        factoryBean.setTypeAliasesPackage("com.sewon.uploadservice.model");
        return factoryBean;
    }
}
