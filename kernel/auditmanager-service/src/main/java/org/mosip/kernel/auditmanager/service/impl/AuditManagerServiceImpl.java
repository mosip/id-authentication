package org.mosip.kernel.auditmanager.service.impl;

import org.modelmapper.ModelMapper;
import org.mosip.kernel.auditmanager.dto.AuditRequestDto;
import org.mosip.kernel.auditmanager.dto.AuditResponseDto;
import org.mosip.kernel.auditmanager.model.Audit;
import org.mosip.kernel.auditmanager.request.AuditRequest;
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
	private AuditHandler<AuditRequest> handler;

	/**
	 * Field for {@link ModelMapper} for performing object mapping
	 */
	@Autowired
	private ModelMapper modelMapper;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mosip.kernel.core.audit.service.impl.AuditManagerService#addAudit(org.
	 * mosip.kernel.core.audit.dto.AuditRequestDto)
	 */
	@Override
	public AuditResponseDto addAudit(AuditRequestDto auditRequestDto) {
		AuditRequest auditRequest = modelMapper.map(auditRequestDto, AuditRequest.class);
		AuditResponseDto auditResponseDto = new AuditResponseDto();
		auditResponseDto.setStatus(handler.writeAudit(auditRequest));
		return auditResponseDto;
	}
}
