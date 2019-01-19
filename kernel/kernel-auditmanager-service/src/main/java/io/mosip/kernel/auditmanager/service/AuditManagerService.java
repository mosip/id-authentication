/**
 * 
 */
package io.mosip.kernel.auditmanager.service;

import io.mosip.kernel.auditmanager.dto.AuditRequestDto;
import io.mosip.kernel.auditmanager.dto.AuditResponseDto;
import io.mosip.kernel.auditmanager.entity.Audit;

/**
 * Interface for AuditManager Serivce having function to add new {@link Audit}
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
public interface AuditManagerService {

	/**
	 * Function to add new audit
	 * 
	 * @param auditRequestDto
	 *            The {@link AuditRequestDto} having required field to audit
	 * @return The {@link AuditResponseDto} having status of audit
	 */
	AuditResponseDto addAudit(AuditRequestDto auditRequestDto);

}