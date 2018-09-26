package org.mosip.auth.service.helper;

import org.mosip.auth.core.constant.RestServicesConstants;
import org.mosip.auth.core.exception.IDDataValidationException;
import org.mosip.auth.core.util.dto.AuditRequestDto;
import org.mosip.auth.core.util.dto.AuditResponseDto;
import org.mosip.auth.core.util.dto.RestRequestDTO;
import org.mosip.auth.service.factory.AuditRequestFactory;
import org.mosip.auth.service.factory.RestRequestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The Class AuditHelper.
 *
 * @author Manoj SP
 */
@Component
public class AuditHelper {
	
	@Autowired
	private RestHelper restHelper;

	@Autowired
	private AuditRequestFactory auditFactory;

	@Autowired
	private RestRequestFactory restFactory;

	/**
	 * Audit.
	 *
	 * @param moduleId the module id
	 * @param description the description
	 * @throws IDDataValidationException the ID data validation exception
	 */
	public void audit(String moduleId, String description) throws IDDataValidationException {
		AuditRequestDto auditRequest = auditFactory.buildRequest(moduleId, description);
		RestRequestDTO restRequest = restFactory.buildRequest(RestServicesConstants.AUDIT_MANAGER_SERVICE, auditRequest,
				AuditResponseDto.class);
		restHelper.requestAsync(restRequest);
	}

}
