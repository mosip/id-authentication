package io.mosip.authentication.common.service.helper;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.reactive.function.client.WebClient;

import io.mosip.idrepository.core.helper.RestHelper;

@Configuration
public class InternalRestHelperConfig {

	@Bean
	@Primary
	public RestHelper restHelper() {
		return new RestHelper();
	}

	@Bean("withSelfTokenWebclient")
	public RestHelper restHelperWithAuth(@Qualifier("selfTokenWebClient") WebClient webClient) {
		return new RestHelper(webClient);
	}
	
}
