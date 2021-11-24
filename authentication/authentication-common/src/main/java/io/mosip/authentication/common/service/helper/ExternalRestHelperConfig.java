package io.mosip.authentication.common.service.helper;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import io.mosip.idrepository.core.helper.RestHelper;

@Configuration
public class ExternalRestHelperConfig {

	@Bean("withSelfTokenWebclient")
	public RestHelper restHelperWithAuth(@Qualifier("selfTokenWebClient") WebClient webClient) {
		return new RestHelper(webClient);
	}
	
}
