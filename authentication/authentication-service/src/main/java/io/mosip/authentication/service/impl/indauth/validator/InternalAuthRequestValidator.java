package io.mosip.authentication.service.impl.indauth.validator;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

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
public class InternalAuthRequestValidator implements Validator {

	private static final String REQUEST = "request";
	
	private static final String AUTH_REQUEST = "authRequest";

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
			validateRequest(requestDTO, errors);
			validateDate(requestDTO, errors);
		}
	}

	/** validation for UIN and VIN */
	public void validateIdvId(AuthRequestDTO authRequestDTO, Errors errors) {
		String refId = authRequestDTO.getIdvIdType();

		if (refId != null) {
			validateUinVin(authRequestDTO, refId, errors);
		}

	}

	/** Validation for Request AuthType */
	public void validateRequest(AuthRequestDTO authRequestDTO, Errors errors) {
		AuthTypeDTO authTypeDTO = authRequestDTO.getAuthType();
		if (authTypeDTO != null && !errors.hasErrors()) {

			if (authRequestDTO.getAuthType().isFingerPrint()) {

				boolean finger = validateFinger(authRequestDTO);
				if (!finger&&!errors.hasErrors()) {
					errors.rejectValue(AUTH_REQUEST, IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorCode(),
							String.format(IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorMessage(),
									AUTH_REQUEST));					}
			}
			if (authRequestDTO.getAuthType().isIris()) {
				boolean iris = validateIris(authRequestDTO);
				if (!iris &&!errors.hasErrors()) {
					errors.rejectValue(AUTH_REQUEST, IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorCode(),
							String.format(IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorMessage(),
									AUTH_REQUEST));					}
			}
			if (authRequestDTO.getAuthType().isFace()) {
				boolean face = validateFace(authRequestDTO);
				if (!face &&!errors.hasErrors()) {
					errors.rejectValue(AUTH_REQUEST, IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorCode(),
							String.format(IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorMessage(),
									AUTH_REQUEST));					}
			}
		} else {
			errors.rejectValue(AUTH_REQUEST, IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorMessage(),
							AUTH_REQUEST));			}

	}

	/** Validation for DateTime */
	public void validateDate(AuthRequestDTO authRequestDTO, Errors errors) {
		if (!authRequestDTO.getReqTime().isEmpty()) {
			try {
				Date reqDate = datehelper.convertStringToDate(authRequestDTO.getReqTime());
				if (reqDate.after(new Date())) {
					errors.rejectValue(AUTH_REQUEST, IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorCode(),
							String.format(IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorMessage(),
									AUTH_REQUEST));					}

			} catch (IDDataValidationException e) {
				errors.rejectValue(AUTH_REQUEST, IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorCode(),
						String.format(IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorMessage(),
								AUTH_REQUEST));				}

		}
	}

	public boolean validateFinger(AuthRequestDTO authRequestDTO) {

		return 
				 (authRequestDTO.getRequest().getIdentity().getLeftIndex() != null && !authRequestDTO.getRequest().getIdentity().getLeftIndex().isEmpty())
				|| (authRequestDTO.getRequest().getIdentity().getLeftLittle() != null && !authRequestDTO.getRequest().getIdentity().getLeftLittle().isEmpty())
				|| (authRequestDTO.getRequest().getIdentity().getLeftMiddle() != null && !authRequestDTO.getRequest().getIdentity().getLeftMiddle().isEmpty())
				|| (authRequestDTO.getRequest().getIdentity().getLeftRing() != null && !authRequestDTO.getRequest().getIdentity().getLeftRing().isEmpty())
				|| (authRequestDTO.getRequest().getIdentity().getLeftThumb() != null && !authRequestDTO.getRequest().getIdentity().getLeftThumb().isEmpty())
				|| (authRequestDTO.getRequest().getIdentity().getRightIndex() != null && !authRequestDTO.getRequest().getIdentity().getRightIndex().isEmpty())
				|| (authRequestDTO.getRequest().getIdentity().getRightLittle() != null && !authRequestDTO.getRequest().getIdentity().getRightLittle().isEmpty())
				|| (authRequestDTO.getRequest().getIdentity().getRightMiddle() != null && !authRequestDTO.getRequest().getIdentity().getRightMiddle().isEmpty())
				|| (authRequestDTO.getRequest().getIdentity().getRightRing() != null && !authRequestDTO.getRequest().getIdentity().getRightRing().isEmpty())
				|| (authRequestDTO.getRequest().getIdentity().getRightThumb() != null && !authRequestDTO.getRequest().getIdentity().getRightThumb().isEmpty());
				
	}

	public boolean validateIris(AuthRequestDTO authRequestDTO) {
		return (!authRequestDTO.getRequest().getIdentity().getLeftEye().isEmpty()
				|| !authRequestDTO.getRequest().getIdentity().getRightEye().isEmpty());
	}

	public boolean validateFace(AuthRequestDTO authRequestDTO) {
		return (!authRequestDTO.getRequest().getIdentity().getFace().isEmpty());
	}

	public void validateUinVin(AuthRequestDTO authRequestDTO, String refId, Errors errors)  {
			if (refId.equals(IdType.UIN.getType())) {
				try {
					idAuthService.validateUIN(authRequestDTO.getIdvId());
				} catch (IdAuthenticationBusinessException e) {
					errors.rejectValue(AUTH_REQUEST, IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorCode(),
							String.format(IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorMessage(),
									AUTH_REQUEST));					}
			} else if (refId.equals(IdType.VID.getType())) {
				try {
					idAuthService.validateVID(authRequestDTO.getIdvId());
				} catch (IdAuthenticationBusinessException e) {
					errors.rejectValue(AUTH_REQUEST, IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorCode(),
							String.format(IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorMessage(),
									AUTH_REQUEST));					}
			}
	}

}
