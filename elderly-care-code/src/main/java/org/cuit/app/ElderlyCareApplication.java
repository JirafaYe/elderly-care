package org.cuit.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class ElderlyCareApplication {
    public static void main(String[] args) {
        SpringApplication.run(ElderlyCareApplication.class, args);
    }
}
