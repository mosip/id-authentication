package io.mosip.authentication.service.impl.indauth.validator;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthTypeDTO;
import io.mosip.authentication.core.dto.indauth.IdType;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.spi.id.service.IdAuthService;
import io.mosip.authentication.service.helper.DateHelper;

/**
 * Validator for internal authentication request
 * 
 * @author Prem Kumar
 *
 */
@Component
public class InternalAuthRequestValidator extends BaseAuthRequestValidator {

	private static final String IDV_ID = "idvId";

	private static final String REQ_TIME = "reqTime";

	private static final String REQUEST = "request";

	@Autowired
	private IdAuthService idAuthService;

	@Autowired
	private DateHelper datehelper;

	@Override
	public boolean supports(Class<?> clazz) {
		return clazz.equals(AuthRequestDTO.class);
	}

	@Override
	public void validate(Object authRequestDTO, Errors errors) {
		if (authRequestDTO instanceof AuthRequestDTO) {
			AuthRequestDTO requestDTO = (AuthRequestDTO) authRequestDTO;
			validateIdvId(requestDTO, errors);
			validateDate(requestDTO, errors);
			validateRequest(requestDTO, errors);
		}
	}

	/**
	 * Method to validate auth type
	 * 
	 * @param requestDTO
	 * @param errors
	 */
	private void validateRequest(AuthRequestDTO requestDTO, Errors errors) {
		AuthTypeDTO authTypeDTO = requestDTO.getAuthType();
		if (authTypeDTO != null) {			
			if(authTypeDTO.isOtp() || authTypeDTO.isPersonalIdentity() 
					|| authTypeDTO.isAddress() || authTypeDTO.isFullAddress()) { //config file
				errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorCode(),
						String.format(IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorMessage(), REQUEST));
			}
			validateBioDetails(requestDTO, errors);
		}else {
			errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorMessage(), REQUEST));
		}
		
		
	}

	/** validation for UIN and VIN */
	public void validateIdvId(AuthRequestDTO authRequestDTO, Errors errors) {
		String refId = authRequestDTO.getIdvIdType();

		if (refId != null) {
			validateUinVin(authRequestDTO, refId, errors);
		}

	}

	/** Validation for DateTime */
	public void validateDate(AuthRequestDTO authRequestDTO, Errors errors) {
		if (!authRequestDTO.getReqTime().isEmpty()) {
			try {
				Date reqDate = datehelper.convertStringToDate(authRequestDTO.getReqTime());
				if (reqDate.after(new Date())) {
					errors.rejectValue(REQ_TIME, IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorCode(),
							String.format(IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorMessage(),
									REQ_TIME));
				}

			} catch (IDDataValidationException e) {
				errors.rejectValue(REQ_TIME, IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorCode(),
						String.format(IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorMessage(), REQ_TIME));
			}

		}
	}

	/**
	 * Validate uin vin.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param refId the ref id
	 * @param errors the errors
	 */
	public void validateUinVin(AuthRequestDTO authRequestDTO, String refId, Errors errors) {
		String idvIdType = authRequestDTO.getIdvIdType();
		if (idvIdType.equals(IdType.UIN.getType())) {
			try {
				idAuthService.validateUIN(authRequestDTO.getIdvId());
			} catch (IdAuthenticationBusinessException e) {
				errors.rejectValue(IDV_ID, IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorCode(),
						String.format(IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorMessage(), IDV_ID));
			}
		} else if (idvIdType.equals(IdType.VID.getType())) {
			try {
				idAuthService.validateVID(authRequestDTO.getIdvId());
			} catch (IdAuthenticationBusinessException e) {
				errors.rejectValue(IDV_ID, IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorCode(),
						String.format(IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorMessage(), IDV_ID));
			}
		}
	}

}
