package com.utility.billing;

import com.utility.billing.security.JwtProperties;
import com.utility.billing.security.SecurityProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({JwtProperties.class, SecurityProperties.class})
public class UtilityBillingApplication {

    public static void main(String[] args) {
        SpringApplication.run(UtilityBillingApplication.class, args);
    }
}
