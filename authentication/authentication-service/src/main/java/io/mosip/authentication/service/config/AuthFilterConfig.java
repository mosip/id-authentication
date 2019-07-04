package io.mosip.authentication.service.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.mosip.authentication.common.service.filter.IdAuthFilter;

/**
 * The configuration for adding filters.
 *
 * @author Manoj SP
 */

@Configuration
public class AuthFilterConfig {

	/**
	 * Gets the auth filter.
	 *
	 * @return the auth filter
	 */
	@Bean
	public FilterRegistrationBean<IdAuthFilter> getIdAuthFilter() {
		FilterRegistrationBean<IdAuthFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new IdAuthFilter());
		registrationBean.addUrlPatterns("/*");
		return registrationBean;
	}

}
