package io.mosip.kernel.auth.config;

import javax.servlet.Filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

/**
 * @author Raj Jha
 * 
 * @since 1.0.0
 *
 */
@Configuration
public class Config {

	@Bean(name = "CorsFilter")
	public FilterRegistrationBean<Filter> registerCORSFilterBean() {
		FilterRegistrationBean<Filter> corsBean = new FilterRegistrationBean<>();
		corsBean.setFilter(registerCORSFilter());
		corsBean.setOrder(0);
		return corsBean;
	}

	@Bean(name = "ReqResponseFilter")
	public FilterRegistrationBean<Filter> registerReqResFilterBean() {
		FilterRegistrationBean<Filter> reqResFilter = new FilterRegistrationBean<>();
		reqResFilter.setFilter(getReqResFilter());
		reqResFilter.setOrder(1);
		return reqResFilter;
	}

	@Bean
	public Filter registerCORSFilter() {
		return new CorsFilter();
	}

	@Bean
	public Filter getReqResFilter() {
		return new ReqResFilter();
	}
	
	@Bean
	public CommonsRequestLoggingFilter logFilter() {
		CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
		filter.setIncludeQueryString(true);
		filter.setIncludePayload(true);
		filter.setMaxPayloadLength(100000);
		filter.setIncludeHeaders(true);
		filter.setAfterMessagePrefix("REQUEST DATA : ");
		return filter;
	}

}
