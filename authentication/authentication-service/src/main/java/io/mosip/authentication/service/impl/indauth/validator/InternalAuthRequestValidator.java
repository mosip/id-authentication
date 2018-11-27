package io.mosip.authentication.service.impl.indauth.validator;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthTypeDTO;
import io.mosip.authentication.core.dto.indauth.IdType;
import io.mosip.authentication.core.dto.indauth.IdentityDTO;
import io.mosip.authentication.core.dto.indauth.RequestDTO;
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
		if (authTypeDTO != null) {

			if (authRequestDTO.getAuthType().isFingerPrint() && !validateFinger(authRequestDTO)) {
				errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorCode(),
						String.format(IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorMessage(), REQUEST));
			}

			if (authRequestDTO.getAuthType().isIris() && !validateIris(authRequestDTO)) {
				errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorCode(),
						String.format(IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorMessage(), REQUEST));
			}

			if (authRequestDTO.getAuthType().isFace() && !validateFace(authRequestDTO)) {
				errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorCode(),
						String.format(IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorMessage(), REQUEST));
			}

		} else {
			errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorMessage(), REQUEST));
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

	public boolean validateFinger(AuthRequestDTO authRequestDTO) {

		return Optional.ofNullable(authRequestDTO.getRequest()).map(RequestDTO::getIdentity)
				.map(IdentityDTO::getLeftIndex).filter(list -> !list.isEmpty()).isPresent()
				|| Optional.ofNullable(authRequestDTO.getRequest()).map(RequestDTO::getIdentity)
						.map(IdentityDTO::getLeftLittle).filter(list -> !list.isEmpty()).isPresent()
				|| Optional.ofNullable(authRequestDTO.getRequest()).map(RequestDTO::getIdentity)
						.map(IdentityDTO::getLeftMiddle).filter(list -> !list.isEmpty()).isPresent()
				|| Optional.ofNullable(authRequestDTO.getRequest()).map(RequestDTO::getIdentity)
						.map(IdentityDTO::getLeftRing).filter(list -> !list.isEmpty()).isPresent()
				|| Optional.ofNullable(authRequestDTO.getRequest()).map(RequestDTO::getIdentity)
						.map(IdentityDTO::getLeftThumb).filter(list -> !list.isEmpty()).isPresent()
				|| Optional.ofNullable(authRequestDTO.getRequest()).map(RequestDTO::getIdentity)
						.map(IdentityDTO::getRightIndex).filter(list -> !list.isEmpty()).isPresent()
				|| Optional.ofNullable(authRequestDTO.getRequest()).map(RequestDTO::getIdentity)
						.map(IdentityDTO::getRightLittle).filter(list -> !list.isEmpty()).isPresent()
				|| Optional.ofNullable(authRequestDTO.getRequest()).map(RequestDTO::getIdentity)
						.map(IdentityDTO::getRightMiddle).filter(list -> !list.isEmpty()).isPresent()
				|| Optional.ofNullable(authRequestDTO.getRequest()).map(RequestDTO::getIdentity)
						.map(IdentityDTO::getRightRing).filter(list -> !list.isEmpty()).isPresent()
				|| Optional.ofNullable(authRequestDTO.getRequest()).map(RequestDTO::getIdentity)
						.map(IdentityDTO::getRightThumb).filter(list -> !list.isEmpty()).isPresent();

	}

	public boolean validateIris(AuthRequestDTO authRequestDTO) {
		return (authRequestDTO.getRequest() != null && authRequestDTO.getRequest().getIdentity() != null
				&& authRequestDTO.getRequest().getIdentity().getLeftEye() != null
				&& !authRequestDTO.getRequest().getIdentity().getLeftEye().isEmpty()
				|| authRequestDTO.getRequest() != null && authRequestDTO.getRequest().getIdentity() != null
						&& authRequestDTO.getRequest().getIdentity().getRightEye() != null
						&& !authRequestDTO.getRequest().getIdentity().getRightEye().isEmpty());
	}

	public boolean validateFace(AuthRequestDTO authRequestDTO) {
		return authRequestDTO.getRequest() != null && authRequestDTO.getRequest().getIdentity() != null
				&& authRequestDTO.getRequest().getIdentity().getFace() != null;
	}

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
