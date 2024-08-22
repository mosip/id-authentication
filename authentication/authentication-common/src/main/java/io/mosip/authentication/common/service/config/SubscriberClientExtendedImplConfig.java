package io.mosip.authentication.common.service.config;

import io.mosip.kernel.core.websub.spi.SubscriptionExtendedClient;
import io.mosip.kernel.websub.api.client.SubscriberClientImpl;
import io.mosip.kernel.websub.api.model.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SubscriberClientExtendedImplConfig {

    @Bean
//    @Qualifier("subscriptionExtendedClient")
    public SubscriptionExtendedClient<FailedContentResponse, FailedContentRequest> extendedClient() {
        return new SubscriberClientImpl();
    }
}
