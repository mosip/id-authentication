package io.mosip.kernel.idrepo.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.idrepo.constant.AuditEvents;
import io.mosip.kernel.core.idrepo.constant.AuditModules;
import io.mosip.kernel.core.idrepo.constant.RestServicesConstants;
import io.mosip.kernel.core.idrepo.exception.IdRepoDataValidationException;
import io.mosip.kernel.idrepo.builder.AuditRequestBuilder;
import io.mosip.kernel.idrepo.builder.RestRequestBuilder;
import io.mosip.kernel.idrepo.dto.AuditRequestDto;
import io.mosip.kernel.idrepo.dto.AuditResponseDto;
import io.mosip.kernel.idrepo.dto.RestRequestDTO;

/**
 * The Class AuditHelper.
 *
 * @author Manoj SP
 */
@Component
public class AuditHelper {

	/** The rest helper. */
	@Autowired
	private RestHelper restHelper;

	/** The audit factory. */
	@Autowired
	private AuditRequestBuilder auditBuilder;

	/** The rest factory. */
	@Autowired
	private RestRequestBuilder restBuilder;

	/**
	 * Audit.
	 *
	 * @param module
	 *            the module
	 * @param event
	 *            the event
	 * @param id
	 *            the id
	 * @param desc
	 *            the desc
	 * @throws IdRepoDataValidationException
	 *             the ID data validation exception
	 */
	public void audit(AuditModules module, AuditEvents event, String id, String desc)
			throws IdRepoDataValidationException {
		RequestWrapper<AuditRequestDto> auditRequest = auditBuilder.buildRequest(module, event, id, desc);
		RestRequestDTO restRequest = restBuilder.buildRequest(RestServicesConstants.AUDIT_MANAGER_SERVICE, auditRequest,
				AuditResponseDto.class);
		restHelper.requestAsync(restRequest);
	}

}
