package org.mosip.kernel.auditmanager.handler;

import org.modelmapper.ModelMapper;
import org.mosip.kernel.auditmanager.model.Audit;
import org.mosip.kernel.auditmanager.repository.AuditRepository;
import org.mosip.kernel.auditmanager.request.AuditRequest;
import org.mosip.kernel.auditmanager.utils.AuditUtils;
import org.mosip.kernel.core.spi.auditmanager.AuditHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link AuditHandler} with function to write
 * {@link AuditRequest}
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Service
public class AuditRequestHandler implements AuditHandler<AuditRequest> {

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
	 * org.mosip.kernel.core.audit.handler.AuditHandler#writeAudit(org.mosip.kernel.
	 * core.audit.dto.AuditRequest)
	 */
	@Override
	public boolean writeAudit(AuditRequest auditRequest) {
		
		AuditUtils.validateAuditRequest(auditRequest);

		Audit event = modelMapper.map(auditRequest, Audit.class);
		auditRepository.create(event);
		return true;
	}


}
