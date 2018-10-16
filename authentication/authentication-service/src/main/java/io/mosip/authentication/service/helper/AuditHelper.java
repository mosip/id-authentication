package io.mosip.authentication.service.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.authentication.core.constant.AuditEvents;
import io.mosip.authentication.core.constant.AuditModules;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.dto.indauth.IdType;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.util.dto.AuditRequestDto;
import io.mosip.authentication.core.util.dto.AuditResponseDto;
import io.mosip.authentication.core.util.dto.RestRequestDTO;
import io.mosip.authentication.service.factory.AuditRequestFactory;
import io.mosip.authentication.service.factory.RestRequestFactory;

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
	 * @param module the module
	 * @param event the event
	 * @param id the id
	 * @param idType the id type
	 * @param desc the desc
	 * @throws IDDataValidationException the ID data validation exception
	 */
	public void audit(AuditModules module, AuditEvents event, String id, IdType idType, String desc) throws IDDataValidationException {
		AuditRequestDto auditRequest = auditFactory.buildRequest(module, event, id, idType, desc);
		RestRequestDTO restRequest = restFactory.buildRequest(RestServicesConstants.AUDIT_MANAGER_SERVICE, auditRequest,
				AuditResponseDto.class);
		restHelper.requestAsync(restRequest);
	}

}
