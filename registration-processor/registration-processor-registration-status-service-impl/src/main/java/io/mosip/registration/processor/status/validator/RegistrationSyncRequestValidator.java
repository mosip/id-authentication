package io.mosip.registration.processor.status.validator;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.regex.Pattern;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.format.datetime.joda.DateTimeFormatterFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.status.dto.RegistrationStatusRequestDTO;
import io.mosip.registration.processor.status.dto.RegistrationSyncRequestDTO;

/**
 * The Class RegistrationStatusRequestValidator.
 * @author Rishabh Keshari
 */
@Component
public class RegistrationSyncRequestValidator implements Validator {

	/** The Constant VER. */
	private static final String VER = "version";

	/** The Constant verPattern. */
	private static final Pattern verPattern = Pattern.compile("^[0-9](\\.\\d{1,1})?$");

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

	/** The env. */
	@Autowired
	private Environment env;

	/** The id. */
//	@Resource
	private Map<String, String> id=new HashMap<>();

	/** The clazz type. */
	private boolean clazzType=false;
	

	/* (non-Javadoc)
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	@Override
	public boolean supports(Class<?> clazz) {
		
		
		if(clazz.isAssignableFrom(RegistrationStatusRequestDTO.class)) {
			id.put("status", "mosip.registration.status");
			clazzType=true;
			return clazz.isAssignableFrom(RegistrationStatusRequestDTO.class);
		}else {
			id.put("sync", "mosip.registration.sync");
			return clazz.isAssignableFrom(RegistrationSyncRequestDTO.class);
		}
		
	}

	
	/* (non-Javadoc)
	 * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
	 */
	@Override
	public void validate(@NonNull Object target, Errors errors) {
		if(clazzType) {
		RegistrationStatusRequestDTO request = (RegistrationStatusRequestDTO) target;
		
		validateReqTime(request.getRequesttime(), errors);

		if (!errors.hasErrors()) {
			validateId(request.getId(), errors);
			validateVersion(request.getVersion(), errors);
		}
		}else {
			RegistrationSyncRequestDTO request = (RegistrationSyncRequestDTO) target;
			validateReqTime(request.getRequesttime(), errors);

			if (!errors.hasErrors()) {
				validateId(request.getId(), errors);
				validateVersion(request.getVersion(), errors);
			}
			
		}
	}

	/**
	 * Validate id.
	 *
	 * @param id
	 *            the id
	 * @param errors
	 *            the errors
	 */
	private void validateId(String id, Errors errors) {
		if (Objects.isNull(id)) {
			errors.rejectValue(ID_FIELD, PlatformErrorMessages.RPR_RGS_MISSING_INPUT_PARAMETER.getCode(),
					String.format(PlatformErrorMessages.RPR_RGS_MISSING_INPUT_PARAMETER.getMessage(), ID_FIELD));
		} else if (!this.id.containsValue(id)) {
			errors.rejectValue(ID_FIELD, PlatformErrorMessages.RPR_RGS_INVALID_INPUT_PARAMETER.getCode(),
					String.format(PlatformErrorMessages.RPR_RGS_INVALID_INPUT_PARAMETER.getMessage(), ID_FIELD));
		}
	}

	/**
	 * Validate ver.
	 *
	 * @param ver
	 *            the ver
	 * @param errors
	 *            the errors
	 */
	private void validateVersion(String ver, Errors errors) {
		if (Objects.isNull(ver)) {
			errors.rejectValue(VER, PlatformErrorMessages.RPR_RGS_MISSING_INPUT_PARAMETER.getCode(),
					String.format(PlatformErrorMessages.RPR_RGS_MISSING_INPUT_PARAMETER.getMessage(), VER));
		} else if ((!verPattern.matcher(ver).matches())) {
			errors.rejectValue(VER, PlatformErrorMessages.RPR_RGS_INVALID_INPUT_PARAMETER.getCode(),
					String.format(PlatformErrorMessages.RPR_RGS_INVALID_INPUT_PARAMETER.getMessage(), VER));
		}
	}

	
	/**
	 * Validate req time.
	 *
	 * @param timestamp
	 *            the timestamp
	 * @param errors
	 *            the errors
	 */
	private void validateReqTime(String timestamp, Errors errors) {
		if (Objects.isNull(timestamp)) {
			errors.rejectValue(TIMESTAMP, PlatformErrorMessages.RPR_RGS_MISSING_INPUT_PARAMETER.getCode(),
					String.format(PlatformErrorMessages.RPR_RGS_MISSING_INPUT_PARAMETER.getMessage(), TIMESTAMP));
		} else {
			try {
				if (Objects.nonNull(env.getProperty(DATETIME_PATTERN))) {
					DateTimeFormatterFactory timestampFormat = new DateTimeFormatterFactory(
							env.getProperty(DATETIME_PATTERN));
					timestampFormat.setTimeZone(TimeZone.getTimeZone(env.getProperty(DATETIME_TIMEZONE)));
					if (!DateTime.parse(timestamp, timestampFormat.createDateTimeFormatter()).isBeforeNow()) {
						errors.rejectValue(TIMESTAMP, PlatformErrorMessages.RPR_RGS_INVALID_INPUT_PARAMETER.getCode(),
								String.format(PlatformErrorMessages.RPR_RGS_INVALID_INPUT_PARAMETER.getMessage(),
										TIMESTAMP));
					}

				}
			} catch (IllegalArgumentException e) {
				regProcLogger.error(REGISTRATION_SERVICE, "RegistrationStatusRequestValidator", "validateReqTime",
						"\n" + ExceptionUtils.getStackTrace(e));
				errors.rejectValue(TIMESTAMP, PlatformErrorMessages.RPR_RGS_INVALID_INPUT_PARAMETER.getCode(),
						String.format(PlatformErrorMessages.RPR_RGS_INVALID_INPUT_PARAMETER.getMessage(), TIMESTAMP));
			}
		}
	}


}
