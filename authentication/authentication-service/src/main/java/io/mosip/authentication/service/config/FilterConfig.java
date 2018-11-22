package io.mosip.authentication.service.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.mosip.authentication.service.filter.IdAuthFilter;
import io.mosip.authentication.service.filter.KycAuthFilter;
import io.mosip.authentication.service.filter.OTPFilter;

/**
 * The configuration for adding filters.
 *
 * @author Manoj SP
 */
@Configuration
public class FilterConfig {

	/**
	 * Gets the otp filter.
	 *
	 * @return the otp filter
	 */
	@Bean
	public FilterRegistrationBean<OTPFilter> getOtpFilter() {
		FilterRegistrationBean<OTPFilter> registrationBean =
				new FilterRegistrationBean<>();
		registrationBean.setFilter(new OTPFilter());
		registrationBean.addUrlPatterns("/otp");

		return registrationBean;
	}

	/**
	 * Gets the auth filter.
	 *
	 * @return the auth filter
	 */
	@Bean
	public FilterRegistrationBean<IdAuthFilter> getIdAuthFilter() {
		FilterRegistrationBean<IdAuthFilter> registrationBean =
				new FilterRegistrationBean<>();
		registrationBean.setFilter(new IdAuthFilter());
		registrationBean.addUrlPatterns("/auth");

		return registrationBean;
	}

	/**
	 * Gets the eKyc filter.
	 *
	 * @return the eKyc filter
	 */
	@Bean
	public FilterRegistrationBean<KycAuthFilter> getEkycFilter() {
		FilterRegistrationBean<KycAuthFilter> registrationBean =
				new FilterRegistrationBean<>();
		registrationBean.setFilter(new KycAuthFilter());
		registrationBean.addUrlPatterns("/ekyc");

		return registrationBean;
	}

}
