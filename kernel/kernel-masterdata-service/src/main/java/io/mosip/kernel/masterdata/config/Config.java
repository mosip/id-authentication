package io.mosip.kernel.masterdata.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

/**
 * Config class with beans for modelmapper and request logging
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Configuration
public class Config {

	/**
	 * Produce modelmapper bean
	 * 
	 * @return Modelmapper instance
	 */
	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

	/**
	 * Produce Request Logging bean
	 * 
	 * @return Request logging bean
	 */
	@Bean
	public CommonsRequestLoggingFilter logFilter() {
		CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
		filter.setIncludeQueryString(true);
		filter.setIncludePayload(true);
		filter.setMaxPayloadLength(10000);
		filter.setIncludeHeaders(false);
		filter.setAfterMessagePrefix("REQUEST DATA : ");
		return filter;
	}


}
