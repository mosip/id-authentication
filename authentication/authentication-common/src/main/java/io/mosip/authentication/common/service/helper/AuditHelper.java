package io.mosip.authentication.common.service.helper;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.exception.IdAuthExceptionHandler;
import io.mosip.authentication.common.service.factory.AuditRequestFactory;
import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.core.constant.AuditEvents;
import io.mosip.authentication.core.constant.AuditModules;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.dto.AuditRequestDto;
import io.mosip.authentication.core.dto.RestRequestDTO;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBaseException;
import io.mosip.authentication.core.indauth.dto.AuthError;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.kernel.core.http.RequestWrapper;

/**
 * The Class AuditHelper - build audit requests and send it to audit service.
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
	private AuditRequestFactory auditFactory;

	/** The rest factory. */
	@Autowired
	private RestRequestFactory restFactory;
	
	@Autowired
	private ObjectMapper mapper;

	/**
	 * Method to build audit requests and send it to audit service.
	 *
	 * @param module {@link AuditModules}
	 * @param event  {@link AuditEvents}
	 * @param id     UIN/VID
	 * @param idType {@link IdType}
	 * @param desc   the desc
	 * @throws IDDataValidationException the ID data validation exception
	 */
	public void audit(AuditModules module, AuditEvents event, String id, IdType idType, String desc)
			throws IDDataValidationException {
		RequestWrapper<AuditRequestDto> auditRequest = auditFactory.buildRequest(module, event, id, idType, desc);
		RestRequestDTO restRequest = restFactory.buildRequest(RestServicesConstants.AUDIT_MANAGER_SERVICE, auditRequest,
				Map.class);
		restHelper.requestAsync(restRequest);
	}
	
	/**
	 * Method to build audit error scenarios and send it to audit service.
	 *
	 * @param module {@link AuditModules}
	 * @param event  {@link AuditEvents}
	 * @param id     UIN/VID
	 * @param idType {@link IdType}
	 * @param desc   the desc
	 * @throws IDDataValidationException the ID data validation exception
	 */
	public void audit(AuditModules module, AuditEvents event, String id, IdType idType, IdAuthenticationBaseException e)
			throws IDDataValidationException {
		List<AuthError> errorList = IdAuthExceptionHandler.getAuthErrors(e);
		String error;
		try {
			error = mapper.writeValueAsString(errorList);
		} catch (JsonProcessingException e1) {
			//Probably will not occur
			error = "Error : " + e.getErrorCode() + " - " + e.getErrorText();
		}
		audit(module, event, id, idType, error);
	}

}
