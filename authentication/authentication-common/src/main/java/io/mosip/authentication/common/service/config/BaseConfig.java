package io.mosip.authentication.common.service.config;

import io.mosip.authentication.common.service.helper.*;
import io.mosip.authentication.common.service.integration.RequireOtpNotFrozenHelper;
import io.mosip.authentication.common.service.integration.ValidateOtpHelper;
import io.mosip.authentication.common.service.util.EntityInfoUtil;
import io.mosip.authentication.common.service.util.LanguageUtil;
import io.mosip.kernel.core.websub.spi.SubscriptionClient;
import io.mosip.kernel.core.websub.spi.SubscriptionExtendedClient;
import io.mosip.kernel.websub.api.client.SubscriberClientImpl;
import io.mosip.kernel.websub.api.model.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
@author Kamesh Shekhar Prasad
 */

@Configuration
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
    public SubscriptionExtendedClient<FailedContentResponse, FailedContentRequest> extendedClient() {
        return new SubscriberClientImpl();
    }

    @Bean
    @Qualifier("subscriptionExtendedClient")
    public SubscriptionClient<SubscriptionChangeRequest, UnsubscriptionRequest, SubscriptionChangeResponse> subscriptionClient() {
        return new SubscriberClientImpl();
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
