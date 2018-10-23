package io.mosip.authentication.service.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.mosip.authentication.service.filter.IDAuthFilter;


/**
 * The configuration for adding filters.
 *
 * @author Loganathan Sekar
 */
@Configuration
public class FilterConfig {
	
	/**
	 * Gets the otp auth filter.
	 *
	 * @return the otp auth filter
	 */
	@Bean
	public FilterRegistrationBean<IDAuthFilter> getOtpAuthFilter() {
		FilterRegistrationBean<IDAuthFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new IDAuthFilter());
		registrationBean.addUrlPatterns("/auth");

		return registrationBean;
	}
	
}
