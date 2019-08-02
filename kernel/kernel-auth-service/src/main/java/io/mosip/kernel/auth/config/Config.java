package io.mosip.kernel.auth.config;

import java.util.Collections;

import javax.servlet.Filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import io.mosip.kernel.auth.dto.AccessTokenResponse;
import io.mosip.kernel.auth.util.MemoryCache;

/**
 * @author Raj Jha
 * 
 * @since 1.0.0
 *
 */
@Configuration
public class Config {

	@Autowired
	private RestInterceptor restInterceptor;

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

	@Bean(name="keycloakRestTemplate")
	public RestTemplate getRestTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setInterceptors(Collections.singletonList(restInterceptor));
		return restTemplate;
	}
	
	
	@Bean(name="authRestTemplate")
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
	
	@Bean
	public MemoryCache<String, AccessTokenResponse> memoryCache(){
		return new MemoryCache<>(1);
	}

}
