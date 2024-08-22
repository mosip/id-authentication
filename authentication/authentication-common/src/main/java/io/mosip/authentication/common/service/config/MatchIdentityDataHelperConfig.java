package io.mosip.authentication.common.service.config;

import io.mosip.authentication.common.service.helper.MatchIdentityDataHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MatchIdentityDataHelperConfig {

    @Bean
    public MatchIdentityDataHelper matchIdentityDataHelper() {
        return new MatchIdentityDataHelper();
    }
}
