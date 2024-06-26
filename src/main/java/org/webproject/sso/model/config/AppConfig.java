package org.webproject.sso.model.config;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app")
@Data
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AppConfig {
    String smtpEmail;
    String loginTemplateSubject;
    String otpTemplateSubject;
    long userDbOptimizationTime;
}