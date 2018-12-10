package io.mosip.kernel.cryptomanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration class for Cryptomanager service
 * 
 * @author Urvil Joshi
 *
 * @since 1.0.0
 */
@Configuration
public class CryptomanagerConfig {

	/**
	 * {@link RestTemplate} Bean
	 * 
	 * @return {@link RestTemplate} instance
	 */
	@Bean
	public RestTemplate restTemplateConfig() {
		return new RestTemplate();
	}
}
