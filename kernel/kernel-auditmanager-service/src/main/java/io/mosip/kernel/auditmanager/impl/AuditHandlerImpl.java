package io.mosip.kernel.auditmanager.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.auditmanager.dto.AuditRequestDto;
import io.mosip.kernel.auditmanager.entity.Audit;
import io.mosip.kernel.auditmanager.repository.AuditRepository;
import io.mosip.kernel.auditmanager.util.AuditUtils;
import io.mosip.kernel.core.auditmanager.spi.AuditHandler;

/**
 * Implementation of {@link AuditHandler} with function to write
 * {@link AuditRequestDto}
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Service
public class AuditHandlerImpl implements AuditHandler<AuditRequestDto> {

	/**
	 * Field for {@link AuditRepository} having data access operations related to
	 * audit
	 */
	@Autowired
	private AuditRepository auditRepository;

	/**
	 * Field for {@link ModelMapper} for performing object mapping
	 */
	@Autowired
	private ModelMapper modelMapper;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.core.audit.handler.AuditHandler#writeAudit(io.mosip.kernel.
	 * core.audit.dto.AuditRequest)
	 */
	@Override
	public boolean addAudit(AuditRequestDto auditRequest) {
		
		AuditUtils.validateAuditRequest(auditRequest);

		Audit event = modelMapper.map(auditRequest, Audit.class);
		auditRepository.create(event);
		return true;
	}


}
