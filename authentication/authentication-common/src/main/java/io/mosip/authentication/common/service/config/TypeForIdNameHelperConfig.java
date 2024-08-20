package io.mosip.authentication.common.service.config;

import io.mosip.authentication.common.service.helper.TypeForIdNameHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TypeForIdNameHelperConfig {

    @Bean
    public TypeForIdNameHelper typeForIdNameHelper() {
        return new TypeForIdNameHelper();
    }
}
