package io.mosip.authentication.common.service.config;

import io.mosip.authentication.common.service.helper.*;
import io.mosip.authentication.common.service.integration.RequireOtpNotFrozenHelper;
import io.mosip.authentication.common.service.integration.ValidateOtpHelper;
import io.mosip.authentication.common.service.util.EntityInfoUtil;
import io.mosip.authentication.common.service.util.LanguageUtil;
import io.mosip.kernel.websub.api.config.WebSubClientConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
@author Kamesh Shekhar Prasad
 */

@Configuration
@Import(WebSubClientConfig.class)
public class BaseConfig {

    @Bean
    public LanguageUtil computeKeyHelper() {
        return new LanguageUtil();
    }

    @Bean
    public EntityInfoUtil entityInfoHelper() {
        return new EntityInfoUtil();
    }

    @Bean
    public IdentityAttributesForMatchTypeHelper identityAttributesForMatchTypeHelper() {
        return new IdentityAttributesForMatchTypeHelper();
    }

    @Bean
    public MatchIdentityDataHelper matchIdentityDataHelper() {
        return new MatchIdentityDataHelper();
    }

    @Bean
    public MatchTypeHelper matchTypeHelper() {
        return new MatchTypeHelper();
    }

    @Bean
    public RequireOtpNotFrozenHelper requireOtpNotFrozenHelper() {
        return new RequireOtpNotFrozenHelper();
    }

    @Bean
    public SeparatorHelper separatorHelper() {
        return new SeparatorHelper();
    }

    @Bean
    public TypeForIdNameHelper typeForIdNameHelper() {
        return new TypeForIdNameHelper();
    }

    @Bean
    public ValidateOtpHelper validateOtpHelper() {
        return new ValidateOtpHelper();
    }

}
