package io.mosip.kernel.ridgenerator.config;

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
	 * @return reqResFilter.
	 */
	@Bean
	public FilterRegistrationBean<Filter> registerReqResFilter() {
		FilterRegistrationBean<Filter> reqResFilter = new FilterRegistrationBean<>();
		reqResFilter.setFilter(getReqResFilter());
		reqResFilter.setOrder(1);
		return reqResFilter;
	}

	/**
	 * Bean for RequestResponseFilter.
	 * 
	 * @return reqResFilter object.
	 */
	@Bean
	public Filter getReqResFilter() {
		return new ReqResFilter();
	}
}
