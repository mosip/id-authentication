package io.mosip.authentication.common.service.config;

import io.mosip.authentication.common.service.helper.SeparatorHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SeperatorHelperConfig {

    @Bean
    public SeparatorHelper separatorHelper() {
        return new SeparatorHelper();
    }
}
