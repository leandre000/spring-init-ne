package com.utility.billing.config;

import com.utility.billing.audit.AuditAwareImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditAwareImpl")
public class JpaConfig {

    @Bean
    public AuditAwareImpl auditAwareImpl() {
        return new AuditAwareImpl();
    }
}
