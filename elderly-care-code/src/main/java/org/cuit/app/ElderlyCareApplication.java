package org.cuit.app;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@MapperScan("org.cuit.app.mapper")
public class ElderlyCareApplication {
    public static void main(String[] args) {
        SpringApplication.run(ElderlyCareApplication.class, args);
    }
}
