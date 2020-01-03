
/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.batchjob.audit;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * This class is used for Swagger configuration, also to configure Host and
 * Port.
 * 
 * @author Kishan Rathore
 * @author Jagadishwari
 * @author Ravi C. Balaji
 * @since 1.0.0
 *
 */
@Configuration
@EnableSwagger2
@ConfigurationProperties("mosip.preregistration.batchjob")
public class BatchjobConfig {
	
	/** The id. */
	private Map<String, String> id;
	
	/**
	 * Sets the id.
	 *
	 * @param id the id
	 */
	public void setId(Map<String, String> id) {
		this.id = id;
	}
	

	/**
	 * Id.
	 *
	 * @return the map
	 */
	@Bean
	public Map<String, String> ic() {
		return Collections.unmodifiableMap(id);
	}

}
