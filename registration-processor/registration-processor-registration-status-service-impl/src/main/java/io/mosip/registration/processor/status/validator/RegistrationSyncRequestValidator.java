package io.mosip.registration.processor.status.validator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.format.datetime.joda.DateTimeFormatterFactory;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.constant.ResponseStatusCode;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.status.dto.RegistrationSyncRequestDTO;
import io.mosip.registration.processor.status.dto.SyncResponseDto;
import io.mosip.registration.processor.status.dto.SyncResponseFailDto;
import io.mosip.registration.processor.status.exception.RegStatusValidationException;

/**
 * The Class RegistrationStatusRequestValidator.
 * 
 * @author Rishabh Keshari
 */
@Component
public class RegistrationSyncRequestValidator {

	/** The Constant VER. */
	private static final String VER = "version";

	/** The Constant DATETIME_TIMEZONE. */
	private static final String DATETIME_TIMEZONE = "mosip.registration.processor.timezone";

	/** The Constant DATETIME_PATTERN. */
	private static final String DATETIME_PATTERN = "mosip.registration.processor.datetime.pattern";

	/** The mosip logger. */
	Logger regProcLogger = RegProcessorLogger.getLogger(RegistrationSyncRequestValidator.class);

	/** The Constant ID_REPO_SERVICE. */
	private static final String REGISTRATION_SERVICE = "RegistrationService";

	/** The Constant TIMESTAMP. */
	private static final String TIMESTAMP = "requesttime";

	/** The Constant ID_FIELD. */
	private static final String ID_FIELD = "id";

	/** The Constant REG_SYNC_APPLICATION_VERSION. */
	private static final String REG_SYNC_APPLICATION_VERSION = "mosip.registration.processor.sync.version";

	/** The env. */
	@Autowired
	private Environment env;

	/** The grace period. */
	@Value("${mosip.registration.processor.grace.period}")
	private int gracePeriod;

	/** The id. */
	// @Resource
	private Map<String, String> id = new HashMap<>();

	/** The request. */
	RegistrationSyncRequestDTO request;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.validation.Validator#validate(java.lang.Object,
	 * org.springframework.validation.Errors)
	 */

	/**
	 * Validate.
	 *
	 * @param target the target
	 * @param serviceId the service id
	 * @param synchResponseList the synch response list
	 * @return true, if successful
	 */
	public boolean validate(Object target, String serviceId, List<SyncResponseDto> synchResponseList) {
		boolean isValid = false;
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(), "",
				"RegistrationSyncRequestValidator::validate()::entry");

		id.put("sync", serviceId);
		request = (RegistrationSyncRequestDTO) target;
		if (validateReqTime(request.getRequesttime(), synchResponseList)
				&& validateId(request.getId(), synchResponseList)
				&& validateVersion(request.getVersion(), synchResponseList)) {
			isValid = true;
		}
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(), "",
				"RegistrationSyncRequestValidator::validate()::exit");

		return isValid;
	}

	/**
	 * Validate id.
	 *
	 * @param id            the id
	 * @param syncResponseList the sync response list
	 * @return true, if successful
	 */
	private boolean validateId(String id, List<SyncResponseDto> syncResponseList) {
		if (Objects.isNull(id)) {

			SyncResponseFailDto syncResponseFailureDto = new SyncResponseFailDto();

			syncResponseFailureDto.setStatus(ResponseStatusCode.FAILURE.toString());
			syncResponseFailureDto.setMessage(
					String.format(PlatformErrorMessages.RPR_RGS_MISSING_INPUT_PARAMETER.getMessage(), ID_FIELD));
			syncResponseFailureDto.setErrorCode(PlatformErrorMessages.RPR_RGS_MISSING_INPUT_PARAMETER.getCode());
			syncResponseList.add(syncResponseFailureDto);
			return false;

		} else if (!this.id.containsValue(id)) {
			SyncResponseFailDto syncResponseFailureDto = new SyncResponseFailDto();

			syncResponseFailureDto.setStatus(ResponseStatusCode.FAILURE.toString());
			syncResponseFailureDto.setMessage(
					String.format(PlatformErrorMessages.RPR_RGS_INVALID_INPUT_PARAMETER.getMessage(), ID_FIELD));
			syncResponseFailureDto.setErrorCode(PlatformErrorMessages.RPR_RGS_INVALID_INPUT_PARAMETER.getCode());
			syncResponseList.add(syncResponseFailureDto);
			return false;

		} else {
			return true;
		}
	}

	/**
	 * Validate ver.
	 *
	 * @param ver            the ver
	 * @param syncResponseList the sync response list
	 * @return true, if successful
	 */
	private boolean validateVersion(String ver, List<SyncResponseDto> syncResponseList) {
		String version = env.getProperty(REG_SYNC_APPLICATION_VERSION);
		if (Objects.isNull(ver)) {

			SyncResponseFailDto syncResponseFailureDto = new SyncResponseFailDto();

			syncResponseFailureDto.setStatus(ResponseStatusCode.FAILURE.toString());
			syncResponseFailureDto
					.setMessage(String.format(PlatformErrorMessages.RPR_RGS_MISSING_INPUT_PARAMETER.getMessage(), VER));
			syncResponseFailureDto.setErrorCode(PlatformErrorMessages.RPR_RGS_MISSING_INPUT_PARAMETER.getCode());
			syncResponseList.add(syncResponseFailureDto);
			return false;

		} else if (!version.equals(ver)) {

			SyncResponseFailDto syncResponseFailureDto = new SyncResponseFailDto();

			syncResponseFailureDto.setStatus(ResponseStatusCode.FAILURE.toString());
			syncResponseFailureDto
					.setMessage(String.format(PlatformErrorMessages.RPR_RGS_INVALID_INPUT_PARAMETER.getMessage(), VER));
			syncResponseFailureDto.setErrorCode(PlatformErrorMessages.RPR_RGS_INVALID_INPUT_PARAMETER.getCode());
			syncResponseList.add(syncResponseFailureDto);
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Validate req time.
	 *
	 * @param timestamp            the timestamp
	 * @param syncResponseList the sync response list
	 * @return true, if successful
	 */
	private boolean validateReqTime(String timestamp, List<SyncResponseDto> syncResponseList) {
		if (Objects.isNull(timestamp)) {
			SyncResponseFailDto syncResponseFailureDto = new SyncResponseFailDto();

			syncResponseFailureDto.setStatus(ResponseStatusCode.FAILURE.toString());
			syncResponseFailureDto.setMessage(
					String.format(PlatformErrorMessages.RPR_RGS_MISSING_INPUT_PARAMETER.getMessage(), TIMESTAMP));
			syncResponseFailureDto.setErrorCode(PlatformErrorMessages.RPR_RGS_MISSING_INPUT_PARAMETER.getCode());
			syncResponseList.add(syncResponseFailureDto);
			return false;
		} else {
			try {
				if (Objects.nonNull(env.getProperty(DATETIME_PATTERN))) {
					DateTimeFormatterFactory timestampFormat = new DateTimeFormatterFactory(
							env.getProperty(DATETIME_PATTERN));
					timestampFormat.setTimeZone(TimeZone.getTimeZone(env.getProperty(DATETIME_TIMEZONE)));
					if (!(DateTime.parse(timestamp, timestampFormat.createDateTimeFormatter())
							.isAfter(new DateTime().minusSeconds(gracePeriod))
							&& DateTime.parse(timestamp, timestampFormat.createDateTimeFormatter())
									.isBefore(new DateTime().plusSeconds(gracePeriod)))) {

						SyncResponseFailDto syncResponseFailureDto = new SyncResponseFailDto();

						syncResponseFailureDto.setStatus(ResponseStatusCode.FAILURE.toString());

						syncResponseFailureDto.setMessage(String
								.format(PlatformErrorMessages.RPR_RGS_INVALID_INPUT_PARAMETER.getMessage(), TIMESTAMP));
						syncResponseFailureDto
								.setErrorCode(PlatformErrorMessages.RPR_RGS_INVALID_INPUT_PARAMETER.getCode());
						syncResponseList.add(syncResponseFailureDto);
						regProcLogger.error(REGISTRATION_SERVICE, "RegistrationSyncRequestValidator", "validateReqTime",
								"\n" + String.format(PlatformErrorMessages.RPR_RGS_INVALID_INPUT_PARAMETER.getMessage(),
										TIMESTAMP));

						return false;
					} else {
						return true;
					}

				} else {
					return false;
				}
			} catch (IllegalArgumentException e) {
				regProcLogger.error(REGISTRATION_SERVICE, "RegistrationSyncRequestValidator", "validateReqTime",
						"\n" + ExceptionUtils.getStackTrace(e));

				SyncResponseFailDto syncResponseFailureDto = new SyncResponseFailDto();

				syncResponseFailureDto.setStatus(ResponseStatusCode.FAILURE.toString());
				syncResponseFailureDto.setMessage(
						String.format(PlatformErrorMessages.RPR_RGS_INVALID_INPUT_PARAMETER.getMessage(), TIMESTAMP));
				syncResponseFailureDto.setErrorCode(PlatformErrorMessages.RPR_RGS_INVALID_INPUT_PARAMETER.getCode());
				syncResponseList.add(syncResponseFailureDto);
				return false;

			}
		}
	}

}
