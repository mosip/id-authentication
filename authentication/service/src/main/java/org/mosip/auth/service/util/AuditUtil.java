package org.mosip.auth.service.util;

import org.mosip.auth.core.constant.RestServicesConstants;
import org.mosip.auth.core.exception.IDDataValidationException;
import org.mosip.auth.core.util.dto.AuditRequestDto;
import org.mosip.auth.core.util.dto.AuditResponseDto;
import org.mosip.auth.core.util.dto.RestRequestDTO;
import org.mosip.auth.service.factory.AuditRequestFactory;
import org.mosip.auth.service.factory.RestRequestFactory;

/**
 * The Class AuditUtil.
 *
 * @author Manoj SP
 */
public class AuditUtil {

	private static AuditRequestFactory auditFactory;

	private static RestRequestFactory restFactory;

	/**
	 * Instantiates a new audit util.
	 *
	 * @param auditFactory the audit factory
	 * @param restFactory the rest factory
	 */
	public AuditUtil(AuditRequestFactory auditFactory, RestRequestFactory restFactory) {
		AuditUtil.auditFactory = auditFactory;
		AuditUtil.restFactory = restFactory;
	}

	/**
	 * Audit.
	 *
	 * @param moduleId the module id
	 * @param description the description
	 * @throws IDDataValidationException the ID data validation exception
	 */
	public static void audit(String moduleId, String description) throws IDDataValidationException {
		AuditRequestDto auditRequest = auditFactory.buildRequest(moduleId, description);
		RestRequestDTO restRequest = restFactory.buildRequest(RestServicesConstants.AUDIT_MANAGER_SERVICE, auditRequest,
				AuditResponseDto.class);
		RestUtil.requestAsync(restRequest);
	}

}
