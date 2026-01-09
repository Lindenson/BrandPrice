package com.wolper.prices.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "spring.datasource")
public class PriceAppConfig {

    @NotBlank
    private String url;

    @NotBlank
    private String driverClassName;

    @NotBlank
    private String username;

    @NotBlank
    private String password;
}
