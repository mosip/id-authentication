package io.mosip.admin.packetstatusupdater.service;

import io.mosip.admin.packetstatusupdater.dto.AuditManagerRequestDto;

/**
 * The Interface AuditManagerProxyService.
 * 
 * @author Megha Tanga
 */
public interface AuditManagerProxyService {

	public void logAdminAudit(AuditManagerRequestDto auditManagerRequestDto);

}