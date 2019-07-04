/**
 * 
 */
package io.mosip.authentication.vid.service.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Configuration;

import io.mosip.authentication.common.service.filter.DefaultIDAFilter;

/**
 * @author Dinesh Karuppiah.T
 *
 */
@Configuration
public class VidFilterConfig {

	/**
	 * Gets the Vid filter.
	 *
	 * @return the Vid filter
	 */
	public FilterRegistrationBean<DefaultIDAFilter> getStaticPinStoreFilter() {
		FilterRegistrationBean<DefaultIDAFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new DefaultIDAFilter());
		registrationBean.addUrlPatterns("/*");
		return registrationBean;
	}

}
