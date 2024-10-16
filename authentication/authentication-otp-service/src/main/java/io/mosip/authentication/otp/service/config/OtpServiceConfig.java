package io.mosip.authentication.otp.service.config;

import io.mosip.authentication.common.service.helper.IdentityAttributesForMatchTypeHelper;
import io.mosip.authentication.common.service.helper.MatchIdentityDataHelper;
import io.mosip.authentication.common.service.helper.MatchTypeHelper;
import io.mosip.authentication.common.service.helper.SeparatorHelper;
import io.mosip.authentication.common.service.integration.RequireOtpNotFrozenHelper;
import io.mosip.authentication.common.service.integration.ValidateOtpHelper;
import io.mosip.authentication.common.service.util.EntityInfoUtil;
import io.mosip.authentication.common.service.util.LanguageUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 @author Kamesh Shekhar Prasad
 */

@Configuration
public class OtpServiceConfig {

    @Bean
    public LanguageUtil languageUtil() {
        return new LanguageUtil();
    }

    @Bean
    public IdentityAttributesForMatchTypeHelper identityAttributesForMatchTypeHelper() {
        return new IdentityAttributesForMatchTypeHelper();
    }

    @Bean
    public ValidateOtpHelper validateOtpHelper() {
        return new ValidateOtpHelper();
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
    public MatchTypeHelper matchTypeHelper() {
        return new MatchTypeHelper();
    }

    @Bean
    public EntityInfoUtil entityInfoUtil() {
        return new EntityInfoUtil();
    }

    @Bean
    public MatchIdentityDataHelper matchIdentityDataHelper() {
        return new MatchIdentityDataHelper();
    }


}