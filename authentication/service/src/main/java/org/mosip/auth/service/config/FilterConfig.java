package org.mosip.auth.service.config;

import org.mosip.auth.service.filter.IDAuthFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * The configuration for adding filters.
 *
 * @author Loganathan Sekar
 */
//TODO
//@Configuration
public class FilterConfig {
	
	@Bean
	public FilterRegistrationBean<IDAuthFilter> getOtpAuthFilter() {
		FilterRegistrationBean<IDAuthFilter> registrationBean = new FilterRegistrationBean<>();

		registrationBean.setFilter(new IDAuthFilter());
		registrationBean.addUrlPatterns("/v0.1/id-usage-service/otp-auth/*"); //FIXME change this

		return registrationBean;
	}
	
}
