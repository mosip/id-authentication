package io.mosip.authentication.service.kyc.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.mosip.authentication.service.kyc.filter.KycAuthFilter;

/**
 * The configuration for adding filters.
 *
 * @author Manoj SP
 */
@Configuration
public class KycFilterConfig {

	/**
	 * Gets the eKyc filter.
	 *
	 * @return the eKyc filter
	 */
	@Bean
	public FilterRegistrationBean<KycAuthFilter> getEkycFilter() {
		FilterRegistrationBean<KycAuthFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new KycAuthFilter());
		registrationBean.addUrlPatterns("/kyc/*");
		return registrationBean;
	}

}
