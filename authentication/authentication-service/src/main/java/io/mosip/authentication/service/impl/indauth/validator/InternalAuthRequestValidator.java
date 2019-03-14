package io.mosip.authentication.service.impl.indauth.validator;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.kernel.core.exception.ParseException;
import io.mosip.kernel.core.util.DateUtils;

/**
 * Validator for internal authentication request
 * 
 * @author Prem Kumar
 *
 */
@Component
public class InternalAuthRequestValidator extends BaseAuthRequestValidator {

	/** The Constant REQ_TIME. */
	private static final String REQ_TIME = "requestTime";

	/** The Constant REQUESTDATE_RECEIVED_IN_MAX_TIME_MINS. */
	private static final String REQUESTDATE_RECEIVED_IN_MAX_TIME_MINS = "authrequest.received-time-allowed.in-hours";

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.service.impl.indauth.validator.
	 * BaseAuthRequestValidator#supports(java.lang.Class)
	 */
	@Override
	public boolean supports(Class<?> clazz) {
		return clazz.equals(AuthRequestDTO.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.service.impl.indauth.validator.
	 * BaseAuthRequestValidator#validate(java.lang.Object,
	 * org.springframework.validation.Errors)
	 */
	@Override
	public void validate(Object authRequestDTO, Errors errors) {
		if (authRequestDTO instanceof AuthRequestDTO) {
			AuthRequestDTO requestDTO = (AuthRequestDTO) authRequestDTO;
			validateId(requestDTO.getId(), errors);
			String individualId = requestDTO.getIndividualId();
			String individualIdType = requestDTO.getIndividualIdType();

			if (!individualId.isEmpty()) {
				validateIdvId(individualId, individualIdType, errors);
			} else {
				// TODO Missing UIN/VID
			}
			// validateVer(requestDTO.getVer(), errors);
			validateTxnId(requestDTO.getTransactionID(), errors);
			validateDate(requestDTO, errors);
			validateAuthType(requestDTO.getRequestedAuth(), errors);
			validateRequest(requestDTO, errors);
		}
	}

	/**
	 * Validation for DateTime.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param errors         the errors
	 */
	public void validateDate(AuthRequestDTO authRequestDTO, Errors errors) {
		if (null != authRequestDTO.getRequestTime() && !authRequestDTO.getRequestTime().isEmpty()) {
			try {
				Date reqDate = DateUtils.parseToDate(authRequestDTO.getRequestTime(),
						env.getProperty("datetime.pattern"));
				Instant reqTimeInstance = reqDate.toInstant();
				Instant now = Instant.now();
				Integer reqDateMaxTimeInt = env.getProperty(REQUESTDATE_RECEIVED_IN_MAX_TIME_MINS, Integer.class);
				if (reqDate.after(new Date()) || Duration.between(reqTimeInstance, now).toHours() > reqDateMaxTimeInt) {
					errors.rejectValue(REQ_TIME,
							IdAuthenticationErrorConstants.INVALID_OTP_REQUEST_TIMESTAMP.getErrorCode(),
							new Object[] { env.getProperty(REQUESTDATE_RECEIVED_IN_MAX_TIME_MINS, Integer.class) },
							IdAuthenticationErrorConstants.INVALID_OTP_REQUEST_TIMESTAMP.getErrorMessage());
				}

			} catch (ParseException e) {
				errors.rejectValue(REQ_TIME, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
						new Object[] { REQ_TIME },
						IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
			}

		} else {
			errors.rejectValue(REQ_TIME, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
					new Object[] { REQ_TIME },
					IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());

		}
	}

}
