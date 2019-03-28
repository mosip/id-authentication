package io.mosip.kernel.auth.login.service.config;

import javax.servlet.Filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Raj Jha
 * 
 * @since 1.0.0
 *
 */
@Configuration
public class CorsConfig {


	@Bean
	public FilterRegistrationBean<Filter> registerCORSFilterBean() {
		FilterRegistrationBean<Filter> corsBean = new FilterRegistrationBean<>();
		corsBean.setFilter(registerCORSFilter());
		corsBean.setOrder(0);
		return corsBean;
	}

	@Bean
	public Filter registerCORSFilter() {
		return new CorsFilter();
	}


}
