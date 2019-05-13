package io.mosip.kernel.auditmanager.config;

import org.modelmapper.ModelMapper;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * The configuration class for Audit having package location to scan
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Configuration
@EntityScan("io.mosip.kernel.auditmanager.entity")
@ComponentScan("io.mosip.kernel.auditmanager")
public class AuditConfig {

	/**
	 * Creates a new Modelmapper bean
	 * 
	 * @return The {@link ModelMapper}
	 */
	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

}
