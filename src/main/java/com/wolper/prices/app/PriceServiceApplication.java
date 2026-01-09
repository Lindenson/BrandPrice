package com.wolper.prices.app;

import com.wolper.prices.config.PriceAppConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(scanBasePackages = "com.wolper.prices")
@EnableConfigurationProperties(PriceAppConfig.class)
public class PriceServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PriceServiceApplication.class, args);
    }
}

