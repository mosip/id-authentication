package io.mosip.authentication.common.service.helper;

import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.FMR_ENABLED_TEST;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.exception.IdAuthExceptionHandler;
import io.mosip.authentication.common.service.factory.AuditRequestFactory;
import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.common.service.impl.match.BioAuthType;
import io.mosip.authentication.core.constant.AuditEvents;
import io.mosip.authentication.core.constant.AuditModules;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.dto.AuditRequestDto;
import io.mosip.authentication.core.dto.RestRequestDTO;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBaseException;
import io.mosip.authentication.core.indauth.dto.AuthError;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.AuthTypeDTO;
import io.mosip.authentication.core.indauth.dto.BioIdentityInfoDTO;
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
	@Qualifier("external")
	private RestHelper restHelper;

	/** The audit factory. */
	@Autowired
	private AuditRequestFactory auditFactory;

	/** The rest factory. */
	@Autowired
	private RestRequestFactory restFactory;
	
	@Autowired
	private ObjectMapper mapper;
	
	/** The Environment */
	@Autowired
	private Environment env;

	
	/**
	 * Method to build audit requests and send it to audit service.
	 *
	 * @param module {@link AuditModules}
	 * @param event  {@link AuditEvents}
	 * @param id     UIN/VID
	 * @param idType {@link IdType} enum
	 * @param desc   the desc
	 * @throws IDDataValidationException the ID data validation exception
	 */
	public void audit(AuditModules module, AuditEvents event, String id, IdType idType, String desc)
			throws IDDataValidationException {
		audit(module, event, id, idType.name(), desc);
	}
	
	/**
	 * Method to build audit requests and send it to audit service.
	 *
	 * @param module {@link AuditModules}
	 * @param event  {@link AuditEvents}
	 * @param id     UIN/VID
	 * @param idType {@link IdType} name
	 * @param desc   the desc
	 * @throws IDDataValidationException the ID data validation exception
	 */
	public void audit(AuditModules module, AuditEvents event, String id, String idType, String desc)
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
	 * @param idType {@link IdType} enum
	 * @param desc   the desc
	 * @throws IDDataValidationException the ID data validation exception
	 */
	public void audit(AuditModules module, AuditEvents event, String id, IdType idType, IdAuthenticationBaseException e)
			throws IDDataValidationException {
		audit(module, event, id, idType.name(), e);
	}
	
	/**
	 * Method to build audit error scenarios and send it to audit service.
	 *
	 * @param module {@link AuditModules}
	 * @param event  {@link AuditEvents}
	 * @param id     UIN/VID
	 * @param idType {@link IdType} name
	 * @param desc   the desc
	 * @throws IDDataValidationException the ID data validation exception
	 */
	public void audit(AuditModules module, AuditEvents event, String id, String idType, IdAuthenticationBaseException e)
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
	
	public void auditExceptionForAuthRequestedModules(AuditEvents authAuditEvent, AuthRequestDTO authRequestDTO,
			IdAuthenticationBaseException e) throws IDDataValidationException {
		List<AuditModules> auditModules = getAuditModules(authRequestDTO);
		for (AuditModules auditModule : auditModules) {
			audit(auditModule, authAuditEvent, authRequestDTO.getIndividualId(), authRequestDTO.getIndividualIdType(),
					e);
		}
	}
	
	public void auditStatusForAuthRequestedModules(AuditEvents authAuditEvent, AuthRequestDTO authRequestDTO,
			String status) throws IDDataValidationException {
		List<AuditModules> auditModules = getAuditModules(authRequestDTO);
		for (AuditModules auditModule : auditModules) {
			audit(auditModule, authAuditEvent, authRequestDTO.getIndividualId(), authRequestDTO.getIndividualIdType(),
					status);
		}
	}

	private List<AuditModules> getAuditModules(AuthRequestDTO authRequestDTO) {
		List<AuditModules> auditModules = new ArrayList<>(5);
		if (Optional.ofNullable(authRequestDTO.getRequestedAuth()).filter(AuthTypeDTO::isOtp).isPresent()) {
			auditModules.add(AuditModules.OTP_AUTH);
		}

		if (Optional.ofNullable(authRequestDTO.getRequestedAuth()).filter(AuthTypeDTO::isDemo).isPresent()) {
			auditModules.add(AuditModules.DEMO_AUTH);
		}

		if (Optional.ofNullable(authRequestDTO.getRequestedAuth()).filter(AuthTypeDTO::isPin).isPresent()) {
			auditModules.add(AuditModules.PIN_AUTH);
		}

		if (Optional.ofNullable(authRequestDTO.getRequestedAuth()).filter(AuthTypeDTO::isBio).isPresent()) {
			if (authRequestDTO.getRequest() != null && authRequestDTO.getRequest().getBiometrics() != null) {
				if ((authRequestDTO.getRequest().getBiometrics().stream().map(BioIdentityInfoDTO::getData)
						.anyMatch(bioInfo -> BioAuthType.FGR_IMG.getType().equals(bioInfo.getBioType())
								|| (FMR_ENABLED_TEST.test(env)
										&& BioAuthType.FGR_MIN.getType().equals(bioInfo.getBioType()))))) {
					auditModules.add(AuditModules.FINGERPRINT_AUTH);
				}

				if (authRequestDTO.getRequest().getBiometrics().stream().map(BioIdentityInfoDTO::getData)
						.anyMatch(bioInfo -> BioAuthType.IRIS_IMG.getType().equals(bioInfo.getBioType()))) {
					auditModules.add(AuditModules.IRIS_AUTH);
				}

				if (authRequestDTO.getRequest().getBiometrics().stream().map(BioIdentityInfoDTO::getData)
						.anyMatch(bioInfo -> BioAuthType.FACE_IMG.getType().equals(bioInfo.getBioType()))) {
					auditModules.add(AuditModules.FACE_AUTH);
				}
			}
		}
		return auditModules;
	}

}
