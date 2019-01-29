/**
 * 
 */
package io.mosip.kernel.auditmanager.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import io.mosip.kernel.auditmanager.request.AuditRequestDto;
import io.mosip.kernel.core.auditmanager.spi.AuditHandler;

/**
 * Utility to asynchronously add audit record
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Component
public class AuditAsyncUtil {

	/**
	 * Field for audit handler
	 */
	@Autowired
	private AuditHandler<AuditRequestDto> auditHandler;

	/**
	 * Function to add audit asynchronously
	 * 
	 * @param auditRequestDto
	 *            auditRequestDto
	 */
	@Async
	public void addAudit(AuditRequestDto auditRequestDto) {
		auditHandler.addAudit(auditRequestDto);
	}

}
