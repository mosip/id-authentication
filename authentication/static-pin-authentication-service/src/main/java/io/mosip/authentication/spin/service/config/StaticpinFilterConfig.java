package io.mosip.authentication.spin.service.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.mosip.authentication.spin.service.filter.StaticPinFilter;

/**
 * The configuration for adding filters.
 *
 * @author Manoj SP
 */
@Configuration
public class StaticpinFilterConfig {

	/**
	 * Gets the otp filter.
	 *
	 * @return the otp filter
	 */
	@Bean
	public FilterRegistrationBean<StaticPinFilter> getStaticPinStoreFilter() {
		FilterRegistrationBean<StaticPinFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new StaticPinFilter());
		registrationBean.addUrlPatterns("/staticpin");
		return registrationBean;
	}

}
