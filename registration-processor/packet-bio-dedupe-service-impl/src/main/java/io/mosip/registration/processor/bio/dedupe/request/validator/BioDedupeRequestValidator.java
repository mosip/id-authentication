package io.mosip.registration.processor.bio.dedupe.request.validator;

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
import io.mosip.registration.processor.core.bio.dedupe.dto.BioDedupeRequestDTO;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;

/**
 * The Class BioDedupeRequestValidator.
 * @author Rishabh Keshari
 */
@Component
public class BioDedupeRequestValidator implements Validator {

	/** The Constant VER. */
	private static final String VER = "version";

	/** The Constant verPattern. */
	private static final Pattern verPattern = Pattern.compile("^[0-9](\\.\\d{1,1})?$");

	/** The Constant DATETIME_TIMEZONE. */
	private static final String DATETIME_TIMEZONE = "mosip.kernel.idrepo.datetime.timezone";

	/** The Constant DATETIME_PATTERN. */
	private static final String DATETIME_PATTERN = "mosip.kernel.idrepo.datetime.pattern";

	/** The mosip logger. */
	Logger regProcLogger = RegProcessorLogger.getLogger(BioDedupeRequestValidator.class);

	/** The Constant ID_REPO_SERVICE. */
	private static final String BIO_DEDUPE_SERVICE = "BioDedupeService";

	/** The Constant TIMESTAMP. */
	private static final String TIMESTAMP = "timestamp";

	/** The Constant ID_FIELD. */
	private static final String ID_FIELD = "id";

	/** The env. */
	@Autowired
	private Environment env;

	/** The id. */
	private Map<String, String> id=new HashMap<>();

	

	/* (non-Javadoc)
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	@Override
	public boolean supports(Class<?> clazz) {
		
			id.put("sync", "mosip.packet.bio.dedupe");
			return clazz.isAssignableFrom(BioDedupeRequestDTO.class);
		
		
	}

	
	/* (non-Javadoc)
	 * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
	 */
	@Override
	public void validate(@NonNull Object target, Errors errors) {
			BioDedupeRequestDTO request = (BioDedupeRequestDTO) target;
		
		validateReqTime(request.getTimestamp(), errors);

		if (!errors.hasErrors()) {
			validateId(request.getId(), errors);
			validateVersion(request.getVersion(), errors);
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
			errors.rejectValue(ID_FIELD, PlatformErrorMessages.RPR_BDD_MISSING_INPUT_PARAMETER.getCode(),
					String.format(PlatformErrorMessages.RPR_BDD_MISSING_INPUT_PARAMETER.getMessage(), ID_FIELD));
		} else if (!this.id.containsValue(id)) {
			errors.rejectValue(ID_FIELD, PlatformErrorMessages.RPR_BDD_INVALID_INPUT_PARAMETER.getCode(),
					String.format(PlatformErrorMessages.RPR_BDD_INVALID_INPUT_PARAMETER.getMessage(), ID_FIELD));
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
			errors.rejectValue(VER, PlatformErrorMessages.RPR_BDD_MISSING_INPUT_PARAMETER.getCode(),
					String.format(PlatformErrorMessages.RPR_BDD_MISSING_INPUT_PARAMETER.getMessage(), VER));
		} else if ((!verPattern.matcher(ver).matches())) {
			errors.rejectValue(VER, PlatformErrorMessages.RPR_BDD_INVALID_INPUT_PARAMETER.getCode(),
					String.format(PlatformErrorMessages.RPR_BDD_INVALID_INPUT_PARAMETER.getMessage(), VER));
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
			errors.rejectValue(TIMESTAMP, PlatformErrorMessages.RPR_BDD_MISSING_INPUT_PARAMETER.getCode(),
					String.format(PlatformErrorMessages.RPR_BDD_MISSING_INPUT_PARAMETER.getMessage(), TIMESTAMP));
		} else {
			try {
				if (Objects.nonNull(env.getProperty(DATETIME_PATTERN))) {
					DateTimeFormatterFactory timestampFormat = new DateTimeFormatterFactory(
							env.getProperty(DATETIME_PATTERN));
					timestampFormat.setTimeZone(TimeZone.getTimeZone(env.getProperty(DATETIME_TIMEZONE)));
					if (!DateTime.parse(timestamp, timestampFormat.createDateTimeFormatter()).isBeforeNow()) {
						errors.rejectValue(TIMESTAMP, PlatformErrorMessages.RPR_BDD_INVALID_INPUT_PARAMETER.getCode(),
								String.format(PlatformErrorMessages.RPR_BDD_INVALID_INPUT_PARAMETER.getMessage(),
										TIMESTAMP));
					}

				}
			} catch (IllegalArgumentException e) {
				regProcLogger.error(BIO_DEDUPE_SERVICE, "BioDedupeRequestValidator", "validateReqTime",
						"\n" + ExceptionUtils.getStackTrace(e));
				errors.rejectValue(TIMESTAMP, PlatformErrorMessages.RPR_BDD_INVALID_INPUT_PARAMETER.getCode(),
						String.format(PlatformErrorMessages.RPR_BDD_INVALID_INPUT_PARAMETER.getMessage(), TIMESTAMP));
			}
		}
	}


}
