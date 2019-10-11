package io.mosip.preregistration.core.util;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.preregistration.core.code.RequestCodes;
import io.mosip.preregistration.core.config.LoggerConfiguration;
import io.mosip.preregistration.core.errorcodes.ErrorCodes;
import io.mosip.preregistration.core.errorcodes.ErrorMessages;
import io.mosip.preregistration.core.exception.InvalidRequestParameterException;

@Component
public abstract class BaseValidator {

	/** The Constant BASE_ID_REPO_VALIDATOR. */
	private static final String BASE_VALIDATOR = "BaseValidator";

	/** The Constant REQUEST. */
	private static final String REQUEST = "request";

	/**
	 * Logger configuration for BaseValidator
	 */
	private static Logger mosipLogger = LoggerConfiguration.logConfig(BaseValidator.class);

	/** The Constant TIMESTAMP. */
	private static final String REQUEST_TIME = "requesttime";

	/** The Constant VER. */
	private static final String VER = "version";

	/** The Constant ID. */
	protected static final String ID = "id";

	/** The Environment. */
	@Autowired
	protected Environment env;

	/** The id. */
	@Resource
	protected Map<String, String> id;

	/**
	 * Validate request time.
	 *
	 * @param reqTime
	 *            the timestamp
	 * @param errors
	 *            the errors
	 */
	protected void validateReqTime(Date reqTime, Errors errors) {
		if (Objects.isNull(reqTime)) {
			mosipLogger.error("", "", "validateReqTime", "requesttime is null");
			errors.rejectValue(REQUEST_TIME, ErrorCodes.PRG_CORE_REQ_003.toString(),
					String.format(ErrorMessages.INVALID_REQUEST_DATETIME.getMessage(), REQUEST_TIME));
		} else {
			LocalDate localDate = reqTime.toInstant().atZone(ZoneId.of("UTC")).toLocalDate();
			LocalDate serverDate = new Date().toInstant().atZone(ZoneId.of("UTC")).toLocalDate();
			if (localDate.isBefore(serverDate) || localDate.isAfter(serverDate)) {
				errors.rejectValue(REQUEST_TIME, ErrorCodes.PRG_CORE_REQ_013.getCode(), String
						.format(ErrorMessages.INVALID_REQUEST_DATETIME_NOT_CURRENT_DATE.getMessage(), REQUEST_TIME));
			}
		}
	}

	/**
	 * Validate version.
	 *
	 * @param ver
	 *            the ver
	 * @param errors
	 *            the errors
	 */
	protected void validateVersion(String ver, Errors errors) {
		if (Objects.isNull(ver)) {
			mosipLogger.error("", "", "validateVersion", "version is null");
			errors.rejectValue(VER, ErrorCodes.PRG_CORE_REQ_002.toString(),
					String.format(ErrorMessages.INVALID_REQUEST_VERSION.getMessage(), VER));
		} else if (!env.getProperty("version").equalsIgnoreCase(ver)) {
			mosipLogger.error("", "", "validateVersion", "version is not correct");
			errors.rejectValue(VER, ErrorCodes.PRG_CORE_REQ_002.toString(),
					String.format(ErrorMessages.INVALID_REQUEST_VERSION.getMessage(), VER));
		}
	}

	/**
	 * Validate request.
	 *
	 * @param request
	 *            the request
	 * @param errors
	 *            the errors
	 */
	protected void validateRequest(Object request, Errors errors) {
		if (Objects.isNull(request)) {
			mosipLogger.error("", "", "validateRequest", "\n" + "request is null");
			errors.rejectValue(REQUEST, ErrorCodes.PRG_CORE_REQ_004.getCode(),
					String.format(ErrorMessages.INVALID_REQUEST_BODY.getMessage(), REQUEST));
		}
	}

	
}
