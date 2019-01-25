package io.mosip.kernel.auditmanager.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.auditmanager.dto.AuditResponseDto;
import io.mosip.kernel.auditmanager.entity.Audit;
import io.mosip.kernel.auditmanager.request.AuditRequestDto;
import io.mosip.kernel.auditmanager.service.AuditManagerService;
import io.mosip.kernel.auditmanager.util.AuditAsyncUtil;

/**
 * AuditManager service implementation with function to add new {@link Audit}
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Service
public class AuditManagerServiceImpl implements AuditManagerService {

	private static final Logger LOGGER = LoggerFactory.getLogger(AuditManagerServiceImpl.class);

	/**
	 * Field for audit handler
	 */
	@Autowired
	private AuditAsyncUtil auditUtil;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.core.audit.service.impl.AuditManagerService#addAudit(org.
	 * mosip.kernel.core.audit.dto.AuditRequestDto)
	 */
	@Override
	public AuditResponseDto addAudit(AuditRequestDto auditRequestDto) {
		LOGGER.info("{}- Request received to audit with {} {} {}", Thread.currentThread().getName(),
				auditRequestDto.getSessionUserId(), auditRequestDto.getIdType(), auditRequestDto.getId());
		AuditResponseDto auditResponseDto = new AuditResponseDto();
		auditUtil.addAudit(auditRequestDto);
		auditResponseDto.setStatus(true);
		LOGGER.info("{}- Audit Status sent for audit request with {} {} {}", Thread.currentThread().getName(),
				auditRequestDto.getSessionUserId(), auditRequestDto.getIdType(), auditRequestDto.getId());
		return auditResponseDto;
	}

}
