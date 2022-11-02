package io.mosip.authentication.service.kyc.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.mosip.authentication.service.kyc.filter.KycAuthFilter;
import io.mosip.authentication.service.kyc.filter.KycAuthenticationFilter;

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
	public FilterRegistrationBean<KycAuthenticationFilter> getEkycFilter() {
		FilterRegistrationBean<KycAuthenticationFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new KycAuthenticationFilter());
		registrationBean.addUrlPatterns("/kyc/*");
		return registrationBean;
	}

	/**
	 * Gets the Kyc Auth filter.
	 *
	 * @return the Kyc Auth filter
	 */
	@Bean
	public FilterRegistrationBean<KycAuthFilter> getKycAuthFilter() {
		FilterRegistrationBean<KycAuthFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new KycAuthFilter());
		registrationBean.addUrlPatterns("/kyc-auth/*");
		return registrationBean;
	}

	/**
	 * Gets the Kyc Exchange filter.
	 *
	 * @return the Kyc Exchange filter
	 */
	/* @Bean
	public FilterRegistrationBean<KycAuthenticationFilter> getKycExchangeFilter() {
		FilterRegistrationBean<KycAuthenticationFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new KycAuthenticationFilter());
		registrationBean.addUrlPatterns("/kyc-exchange/*");
		return registrationBean;
	} */
}
