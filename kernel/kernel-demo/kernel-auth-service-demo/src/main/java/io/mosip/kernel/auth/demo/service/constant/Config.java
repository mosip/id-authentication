/*package io.mosip.kernel.auth.demo.service.constant;


import javax.servlet.Filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.mosip.kernel.auth.demo.service.filter.AuthSessionFilter;


@Configuration
public class Config {


	@Bean
	public FilterRegistrationBean<Filter> registerCORSFilterBean() {
		FilterRegistrationBean<Filter> corsBean = new FilterRegistrationBean<>();
		corsBean.setFilter(registerCORSFilter());
		corsBean.setOrder(1);
		return corsBean;
	}

	@Bean
	public Filter registerCORSFilter() {
		return new AuthSessionFilter();
	}


}
*/