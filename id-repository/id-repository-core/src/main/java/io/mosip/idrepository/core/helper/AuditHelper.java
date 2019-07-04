package io.mosip.idrepository.core.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.idrepository.core.builder.AuditRequestBuilder;
import io.mosip.idrepository.core.builder.RestRequestBuilder;
import io.mosip.idrepository.core.constant.AuditEvents;
import io.mosip.idrepository.core.constant.AuditModules;
import io.mosip.idrepository.core.constant.IdType;
import io.mosip.idrepository.core.constant.RestServicesConstants;
import io.mosip.idrepository.core.dto.AuditRequestDTO;
import io.mosip.idrepository.core.dto.AuditResponseDTO;
import io.mosip.idrepository.core.dto.RestRequestDTO;
import io.mosip.idrepository.core.exception.IdRepoDataValidationException;
import io.mosip.kernel.core.http.RequestWrapper;

/**
 * The Class AuditHelper - helper class that makes async rest call to audit
 * service with provided audit details .
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
	 * Audit - method to call audit service and store audit details.
	 *
	 * @param module the module
	 * @param event  the event
	 * @param id     the id
	 * @param idType the id type
	 * @param desc   the desc
	 * @throws IdRepoDataValidationException the ID data validation exception
	 */
	public void audit(AuditModules module, AuditEvents event, String id, IdType idType,String desc)
			throws IdRepoDataValidationException {
		RequestWrapper<AuditRequestDTO> auditRequest = auditBuilder.buildRequest(module, event, id,idType, desc);
		RestRequestDTO restRequest = restBuilder.buildRequest(RestServicesConstants.AUDIT_MANAGER_SERVICE, auditRequest,
				AuditResponseDTO.class);
		 restHelper.requestAsync(restRequest);
	}

}
