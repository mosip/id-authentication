package io.mosip.authentication.common.service.config;

import io.mosip.authentication.common.service.helper.IdentityAttributesForMatchTypeHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IdentityAttributesForMatchTypeHelperConfig {

    @Bean
    public IdentityAttributesForMatchTypeHelper identityAttributesForMatchTypeHelper() {
        return new IdentityAttributesForMatchTypeHelper();
    }
}
