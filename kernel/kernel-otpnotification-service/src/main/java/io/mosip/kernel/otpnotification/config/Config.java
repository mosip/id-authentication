package io.mosip.kernel.otpnotification.config;

import javax.servlet.Filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for RequestResponseFilter Bean.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
@Configuration
public class Config {
	/**
	 * Bean to register RequestResponse Filter.
	 * 
	 * @return the bean.
	 */
	@Bean
	public FilterRegistrationBean<Filter> registerReqResFilter() {
		FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
		filterRegistrationBean.setFilter(getReqResFilter());
		filterRegistrationBean.setOrder(1);
		return filterRegistrationBean;
	}

	/**
	 * Bean for RequestResponseFilter.
	 * 
	 * @return the bean.
	 */
	@Bean
	public Filter getReqResFilter() {
		return new ReqResFilter();
	}
}
