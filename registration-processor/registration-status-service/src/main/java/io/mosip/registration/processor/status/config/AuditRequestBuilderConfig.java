package io.mosip.registration.processor.status.config;

import io.mosip.kernel.auditmanager.builder.AuditRequestBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The Class AuditRequestBuilderConfig.
 */
@Configuration
public class AuditRequestBuilderConfig {

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
