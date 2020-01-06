package io.mosip.preregistration.login.config;

import java.util.Objects;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.preregistration.core.common.dto.MainRequestDTO;
import io.mosip.preregistration.core.config.LoggerConfiguration;
import io.mosip.preregistration.core.errorcodes.ErrorCodes;
import io.mosip.preregistration.core.errorcodes.ErrorMessages;
import io.mosip.preregistration.core.util.BaseValidator;

@Component
public class LoginValidator extends BaseValidator implements Validator {

	/** The Constant REQUEST. */
	private static final String ID = "id";

	/**
	 * Logger configuration for BaseValidator
	 */
	private static Logger mosipLogger = LoggerConfiguration.logConfig(LoginValidator.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	@Override
	public boolean supports(Class<?> clazz) {
		return clazz.isAssignableFrom(MainRequestDTO.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.validation.Validator#validate(java.lang.Object,
	 * org.springframework.validation.Errors)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void validate(@NonNull Object target, Errors errors) {
		MainRequestDTO<Object> request = (MainRequestDTO<Object>) target;
		validateReqTime(request.getRequesttime(), errors);
		validateVersion(request.getVersion(), errors);
		validateRequest(request.getRequest(), errors);
	}

	public void validateId(String operation, String requestId, Errors errors) {
		if (Objects.nonNull(requestId)) {
			if (!errors.hasErrors() && !requestId.equals(id.get(operation))) {
				mosipLogger.error("", "", "validateId", "\n" + "Id is not correct");
				errors.rejectValue(ID, ErrorCodes.PRG_CORE_REQ_001.getCode(),
						String.format(ErrorMessages.INVALID_REQUEST_ID.getMessage(), ID));
			}
		} else {
			mosipLogger.error("", "", "validateId", "\n" + "Id is null");
			errors.rejectValue(ID, ErrorCodes.PRG_CORE_REQ_001.getCode(),
					String.format(ErrorMessages.INVALID_REQUEST_ID.getMessage(), ID));
		}
	}


}
