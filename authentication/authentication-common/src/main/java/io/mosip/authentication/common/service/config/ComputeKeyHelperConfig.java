package io.mosip.authentication.common.service.config;

import io.mosip.authentication.common.service.helper.ComputeKeyHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ComputeKeyHelperConfig {

    @Bean
    public ComputeKeyHelper computeKeyHelper() {
        return new ComputeKeyHelper();
    }
}
