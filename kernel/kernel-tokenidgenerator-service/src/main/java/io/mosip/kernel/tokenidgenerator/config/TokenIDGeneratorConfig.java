package io.mosip.kernel.tokenidgenerator.config;

import javax.servlet.Filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
public class TokenIDGeneratorConfig {
	/**
	 * @return the registered filter to the context.
	 */
	@Bean
	public FilterRegistrationBean<Filter> registerReqResFilter() {
		FilterRegistrationBean<Filter> corsBean = new FilterRegistrationBean<>();
		corsBean.setFilter(getReqResFilter());
		corsBean.setOrder(1);
		return corsBean;
	}

	/**
	 * @return a new {@link ReqResFilter} object.
	 */
	@Bean
	public Filter getReqResFilter() {
		return new ReqResFilter();
	}
}
