package io.mosip.authentication.common.service.config;

import io.mosip.authentication.common.service.integration.RequireOtpNotFrozenHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RequireOtpNotFrozenHelperConfig {

    @Bean
    public RequireOtpNotFrozenHelper requireOtpNotFrozenHelper() {
        return new RequireOtpNotFrozenHelper();
    }
}
