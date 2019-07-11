package io.mosip.authentication.otp.service.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.mosip.authentication.otp.service.filter.OTPFilter;

/**
 * The configuration for adding filters.
 *
 * @author Manoj SP
 */
@Configuration
public class OtpFilterConfig {

	/**
	 * Gets the otp filter.
	 *
	 * @return the otp filter
	 */
	@Bean
	public FilterRegistrationBean<OTPFilter> getOtpFilter() {
		FilterRegistrationBean<OTPFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new OTPFilter());
		registrationBean.addUrlPatterns("/*");
		return registrationBean;
	}

}
