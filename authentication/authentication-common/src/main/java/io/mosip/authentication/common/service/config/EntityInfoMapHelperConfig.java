package io.mosip.authentication.common.service.config;

import io.mosip.authentication.common.service.helper.EntityInfoMapHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EntityInfoMapHelperConfig {

    @Bean
    public EntityInfoMapHelper entityInfoMapHelper() {
        return new EntityInfoMapHelper();
    }
}
