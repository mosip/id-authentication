package io.mosip.authentication.staticpin.service.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Configuration;

import io.mosip.authentication.common.service.filter.DefaultIDAFilter;

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
	public FilterRegistrationBean<DefaultIDAFilter> getStaticPinStoreFilter() {
		FilterRegistrationBean<DefaultIDAFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new DefaultIDAFilter());
		registrationBean.addUrlPatterns("/");
		return registrationBean;
	}

}
