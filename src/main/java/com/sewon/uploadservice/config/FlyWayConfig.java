package com.sewon.uploadservice.config;

import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlyWayConfig {

    //  Flyway의 자동 설정을 비활성화하고 직접 마이그레이션 전략을 제어
    // Flyway는 기본적으로 하나의 DataSource 빈을 찾아 마이그레이션하는데 다중일 때는 여러개 지정해도 못 찾는 거 같다.
    @Bean
    public FlywayMigrationStrategy flywayMigrationStrategy(
        @Qualifier("postgresqlDataSource") DataSource postgresqlDataSource) {
        return flyway -> {
            Flyway firstFlyway = Flyway.configure()
                .dataSource(postgresqlDataSource)
                .locations("classpath:db/migration") // SQL 파일 경로 지정
                .baselineOnMigrate(true)
                .load();
            firstFlyway.migrate();
        };
    }

}
