package org.mosip.kernel.auditmanager.service.impl;

import org.mosip.kernel.auditmanager.dto.AuditResponseDto;
import org.mosip.kernel.auditmanager.entity.Audit;
import org.mosip.kernel.auditmanager.request.AuditRequestDto;
import org.mosip.kernel.auditmanager.service.AuditManagerService;
import org.mosip.kernel.core.spi.auditmanager.AuditHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * AuditManager service implementation with function to add new {@link Audit}
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Service
@Transactional
public class AuditManagerServiceImpl implements AuditManagerService {

	/**
	 * Field for audit handler
	 */
	@Autowired
	private AuditHandler<AuditRequestDto> handler;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mosip.kernel.core.audit.service.impl.AuditManagerService#addAudit(org.
	 * mosip.kernel.core.audit.dto.AuditRequestDto)
	 */
	@Override
	public AuditResponseDto addAudit(AuditRequestDto auditRequestDto) {
		AuditResponseDto auditResponseDto = new AuditResponseDto();
		auditResponseDto.setStatus(handler.writeAudit(auditRequestDto));
		return auditResponseDto;
	}
}
