package io.mosip.internal.authentication.service.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.mosip.internal.authentication.service.filter.InternalAuthFilter;

/**
 * The configuration for adding filters.
 *
 * @author Manoj SP
 */

@Configuration
public class InternalAuthFilterConfig {

	/**
	 * Gets the internal auth filter.
	 *
	 * @return the internal auth filter
	 */
	@Bean
	public FilterRegistrationBean<InternalAuthFilter> getInternalAuthFilter() {
		FilterRegistrationBean<InternalAuthFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new InternalAuthFilter());
		registrationBean.addUrlPatterns("/auth/internal/*");

		return registrationBean;
	}

}
