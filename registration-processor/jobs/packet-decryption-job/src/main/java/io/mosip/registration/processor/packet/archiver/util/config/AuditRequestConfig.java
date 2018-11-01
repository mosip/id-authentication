package io.mosip.registration.processor.packet.archiver.util.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.mosip.kernel.auditmanager.builder.AuditRequestBuilder;

/**
 * The Class AuditRequestConfig.
 * 
 * @author M1039285
 */
@Configuration
public class AuditRequestConfig {

	/**
	 * Audit request builder bean.
	 *
	 * @return the audit request builder
	 */
	@Bean
	public AuditRequestBuilder auditRequestBuilderBean() {
		return new AuditRequestBuilder();
	}
}
