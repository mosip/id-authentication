package io.mosip.authentication.service.impl.indauth.validator;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthTypeDTO;
import io.mosip.authentication.core.dto.indauth.InternalAuthType;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.service.helper.DateHelper;

/**
 * Validator for internal authentication request
 * 
 * @author Prem Kumar
 *
 */
@Component
public class InternalAuthRequestValidator extends BaseAuthRequestValidator {

	private static final String INTERNAL_ALLOWED_AUTH_TYPE = "internal.allowed.auth.type";

	/** The Constant REQ_TIME. */
	private static final String REQ_TIME = "reqTime";

	/** The Constant REQUEST. */
	private static final String REQUEST = "request";
	
	/** The Constant AUTH_TYPE. */
	private static final String AUTH_TYPE = "authType";

	/** The datehelper. */
	@Autowired
	private DateHelper datehelper;

	/* (non-Javadoc)
	 * @see io.mosip.authentication.service.impl.indauth.validator.BaseAuthRequestValidator#supports(java.lang.Class)
	 */
	@Override
	public boolean supports(Class<?> clazz) {
		return clazz.equals(AuthRequestDTO.class);
	}

	/* (non-Javadoc)
	 * @see io.mosip.authentication.service.impl.indauth.validator.BaseAuthRequestValidator#validate(java.lang.Object, org.springframework.validation.Errors)
	 */
	@Override
	public void validate(Object authRequestDTO, Errors errors) {
		if (authRequestDTO instanceof AuthRequestDTO) {
			AuthRequestDTO requestDTO = (AuthRequestDTO) authRequestDTO;
			validateId(requestDTO.getId(), errors);
			validateIdvId(requestDTO.getIdvId(), requestDTO.getIdvIdType(), errors);
			validateVer(requestDTO.getVer(), errors);
			validateTxnId(requestDTO.getTxnID(), errors);
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
			Set<String> allowedAuthType = extractAuthInfo();			
			validateAuthType(requestDTO, errors, authTypeDTO, allowedAuthType);
		}else {
			errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorMessage(), REQUEST));
		}
		
		
	}

	/**
	 * Validate auth type.
	 *
	 * @param requestDTO the request DTO
	 * @param errors the errors
	 * @param authTypeDTO the auth type DTO
	 * @param allowedAuthType the allowed auth type
	 */
	private void validateAuthType(AuthRequestDTO requestDTO, Errors errors, AuthTypeDTO authTypeDTO,
			Set<String> allowedAuthType) {
		if((authTypeDTO.isPersonalIdentity() || authTypeDTO.isFullAddress() || authTypeDTO.isAddress())) {
			if(allowedAuthType.contains(InternalAuthType.DEMO.getType())) {
				checkDemoAuth(requestDTO, errors);
			} else {
				errors.rejectValue(AUTH_TYPE, IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorCode(),
						new Object[]{AUTH_TYPE} , IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorMessage());
			}
		} 
		
		if(authTypeDTO.isOtp()) {
			if(allowedAuthType.contains(InternalAuthType.OTP.getType())) {
				checkOTPAuth(requestDTO, errors);
			} else {
				errors.rejectValue(AUTH_TYPE, IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorCode(),
						new Object[]{AUTH_TYPE} , IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorMessage());
			}
		}
		
		if(authTypeDTO.isBio()) {
			if(allowedAuthType.contains(InternalAuthType.BIO.getType())) {
				validateBioDetails(requestDTO, errors);
			} else {
				errors.rejectValue(AUTH_TYPE, IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorCode(),
						new Object[]{AUTH_TYPE} , IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorMessage());
			}
			
		}
	}

	/**
	 * Extract auth info.
	 *
	 * @return the sets the
	 */
	private Set<String> extractAuthInfo() {
		Set<String> allowedAuthType = new HashSet<>();
		String intAllowedAuthType = env.getProperty(INTERNAL_ALLOWED_AUTH_TYPE);
		if (intAllowedAuthType.contains(",")) {
			String value[] = intAllowedAuthType.split(",");
			for (int i = 0; i < value.length; i++) {
				allowedAuthType.add(value[i]);				
			}
		}else {
			allowedAuthType.add(intAllowedAuthType);
		}
		return allowedAuthType;
	}

	/** Validation for DateTime */
	public void validateDate(AuthRequestDTO authRequestDTO, Errors errors) {
		if (!authRequestDTO.getReqTime().isEmpty()) {
			try {
				Date reqDate = datehelper.convertStringToDate(authRequestDTO.getReqTime());
				if (reqDate.after(new Date())) {
					errors.rejectValue(REQ_TIME, IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorCode(),
							new Object[] {REQ_TIME},IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorMessage());
				}

			} catch (IDDataValidationException e) {
				errors.rejectValue(REQ_TIME, IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorCode(),
						new Object[] {REQ_TIME},IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorMessage());
			}

		}
	}

}
