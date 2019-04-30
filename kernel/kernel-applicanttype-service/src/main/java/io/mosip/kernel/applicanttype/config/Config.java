package io.mosip.kernel.applicanttype.config;

import javax.servlet.Filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Config class with beans for keymanager service and request logging
 * 
 * @author Bal Vikash Sharma
 * @since 1.0.0
 *
 */
@Configuration
public class Config {

	@Bean
	public FilterRegistrationBean<Filter> registerReqResFilter() {
		FilterRegistrationBean<Filter> corsBean = new FilterRegistrationBean<>();
		corsBean.setFilter(getReqResFilter());
		corsBean.setOrder(1);
		return corsBean;
	}

	@Bean
	public Filter getReqResFilter() {
		return new ReqResFilter();
	}
}
