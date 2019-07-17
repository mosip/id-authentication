package io.mosip.authentication.internal.service.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.mosip.authentication.common.service.filter.DefaultInternalFilter;
import io.mosip.authentication.common.service.filter.InternalOtpFilter;
import io.mosip.authentication.internal.service.filter.InternalAuthFilter;

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
		registrationBean.addUrlPatterns("/auth");
		return registrationBean;
	}

	@Bean
	public FilterRegistrationBean<InternalOtpFilter> getInternalOTPFilter() {
		FilterRegistrationBean<InternalOtpFilter> otpBean = new FilterRegistrationBean<>();
		otpBean.setFilter(new InternalOtpFilter());
		otpBean.addUrlPatterns("/otp");
		return otpBean;
	}

	@Bean
	public FilterRegistrationBean<DefaultInternalFilter> getDefaultInternalFilter() {
		FilterRegistrationBean<DefaultInternalFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new DefaultInternalFilter());
		registrationBean.addUrlPatterns("/auth-transactions/*");
		registrationBean.addInitParameter("IDType", "IDType");
		registrationBean.addInitParameter("ID", "ID");
		registrationBean.addInitParameter("pageStart", "pageStart");
		registrationBean.addInitParameter("pageFetch", "pageFetch");
		return registrationBean;
	}

}
