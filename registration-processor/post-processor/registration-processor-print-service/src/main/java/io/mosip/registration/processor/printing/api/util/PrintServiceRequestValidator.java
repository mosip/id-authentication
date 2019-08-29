package io.mosip.registration.processor.printing.api.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.format.datetime.joda.DateTimeFormatterFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.idvalidator.exception.InvalidIDException;
import io.mosip.kernel.core.idvalidator.spi.RidValidator;
import io.mosip.kernel.core.idvalidator.spi.UinValidator;
import io.mosip.kernel.core.idvalidator.spi.VidValidator;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.constant.CardType;
import io.mosip.registration.processor.core.constant.IdType;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.packet.storage.exception.IdRepoAppException;
import io.mosip.registration.processor.packet.storage.exception.VidCreationException;
import io.mosip.registration.processor.packet.storage.utils.Utilities;
import io.mosip.registration.processor.print.service.exception.RegPrintAppException;
import io.mosip.registration.processor.printing.api.dto.PrintRequest;
import io.mosip.registration.processor.printing.api.dto.RequestDTO;

/**
 * The Class PrintServiceRequestValidator.
 * 
 * @author M1048358 Alok
 */
@Component
public class PrintServiceRequestValidator implements Validator {

	/** The Constant VER. */
	private static final String VER = "version";

	/** The Constant verPattern. */
	private static final Pattern verPattern = Pattern.compile("^[0-9](\\.\\d{1,1})?$");

	/** The Constant DATETIME_TIMEZONE. */
	private static final String DATETIME_TIMEZONE = "mosip.registration.processor.timezone";

	/** The Constant DATETIME_PATTERN. */
	private static final String DATETIME_PATTERN = "mosip.registration.processor.datetime.pattern";

	/** The mosip logger. */
	Logger regProcLogger = RegProcessorLogger.getLogger(PrintServiceRequestValidator.class);

	/** The Constant PRINT_SERVICE. */
	private static final String PRINT_SERVICE = "PrintService";

	/** The Constant TIMESTAMP. */
	private static final String TIMESTAMP = "requesttime";

	/** The Constant ID_FIELD. */
	private static final String ID_FIELD = "id";

	/** The env. */
	@Autowired
	private Environment env;

	/** The id. */
	private Map<String, String> id = new HashMap<>();

	/** The grace period. */
	@Value("${mosip.registration.processor.grace.period}")
	private int gracePeriod;

	@Autowired
	private RidValidator<String> ridValidator;

	/** The uin validator impl. */
	@Autowired
	private UinValidator<String> uinValidatorImpl;

	/** The vid validator impl. */
	@Autowired
	private VidValidator<String> vidValidatorImpl;

	/** The utilities. */
	@Autowired
	private Utilities utilities;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	@Override
	public boolean supports(Class<?> clazz) {
		id.put("status", "mosip.registration.print");
		return clazz.isAssignableFrom(PrintRequest.class);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.validation.Validator#validate(java.lang.Object,
	 * org.springframework.validation.Errors)
	 */
	@Override
	public void validate(@NonNull Object target, Errors errors) {
		PrintRequest request = (PrintRequest) target;

		validateReqTime(request.getRequesttime(), errors);

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
			errors.rejectValue(ID_FIELD, PlatformErrorMessages.RPR_PRT_MISSING_INPUT_PARAMETER.getCode(),
					String.format(PlatformErrorMessages.RPR_PRT_MISSING_INPUT_PARAMETER.getMessage(), ID_FIELD));
		} else if (!this.id.containsValue(id)) {
			errors.rejectValue(ID_FIELD, PlatformErrorMessages.RPR_PRT_INVALID_INPUT_PARAMETER.getCode(),
					String.format(PlatformErrorMessages.RPR_PRT_INVALID_INPUT_PARAMETER.getMessage(), ID_FIELD));
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
			errors.rejectValue(VER, PlatformErrorMessages.RPR_PRT_MISSING_INPUT_PARAMETER.getCode(),
					String.format(PlatformErrorMessages.RPR_PRT_MISSING_INPUT_PARAMETER.getMessage(), VER));
		} else if ((!verPattern.matcher(ver).matches())) {
			errors.rejectValue(VER, PlatformErrorMessages.RPR_PRT_INVALID_INPUT_PARAMETER.getCode(),
					String.format(PlatformErrorMessages.RPR_PRT_INVALID_INPUT_PARAMETER.getMessage(), VER));
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
			errors.rejectValue(TIMESTAMP, PlatformErrorMessages.RPR_PRT_MISSING_INPUT_PARAMETER.getCode(),
					String.format(PlatformErrorMessages.RPR_PRT_MISSING_INPUT_PARAMETER.getMessage(), TIMESTAMP));
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
						regProcLogger.error(PRINT_SERVICE, "PrintServiceRequestValidator", "validateReqTime",
								"\n" + String.format(PlatformErrorMessages.RPR_PRT_INVALID_INPUT_PARAMETER.getMessage(),
										TIMESTAMP));

						errors.rejectValue(TIMESTAMP, PlatformErrorMessages.RPR_PRT_INVALID_INPUT_PARAMETER.getCode(),
								String.format(PlatformErrorMessages.RPR_PRT_INVALID_INPUT_PARAMETER.getMessage(),
										TIMESTAMP));
					}

				}
			} catch (IllegalArgumentException e) {
				regProcLogger.error(PRINT_SERVICE, "PrintServiceRequestValidator", "validateReqTime",
						"\n" + ExceptionUtils.getStackTrace(e));
				errors.rejectValue(TIMESTAMP, PlatformErrorMessages.RPR_PRT_INVALID_INPUT_PARAMETER.getCode(),
						String.format(PlatformErrorMessages.RPR_PRT_INVALID_INPUT_PARAMETER.getMessage(), TIMESTAMP));
			}
		}
	}

	public boolean validateRequest(RequestDTO dto, Errors errors) throws RegPrintAppException {
		if (!errors.hasErrors()) {
			if (Objects.isNull(dto)) {
				throw new RegPrintAppException(PlatformErrorMessages.RPR_PRT_MISSING_INPUT_PARAMETER.getCode(),
						String.format(PlatformErrorMessages.RPR_PRT_MISSING_INPUT_PARAMETER.getMessage(), "request"));
			} else if (validateIdType(dto.getIdtype()) && validateIdValue(dto) && validateCardType(dto.getCardType())) {
				return true;
			}
		}
		return false;

	}

	private boolean validateCardType(String cardType) throws RegPrintAppException {
		if (cardType != null && !cardType.isEmpty() && (cardType.equalsIgnoreCase(CardType.UIN.toString())
				|| cardType.equalsIgnoreCase(CardType.MASKED_UIN.toString()))) {
			return true;
		} else {
			throw new RegPrintAppException(PlatformErrorMessages.RPR_PRT_CARDTYPE_VALIDATION_FAILED.getCode(),
					PlatformErrorMessages.RPR_PRT_CARDTYPE_VALIDATION_FAILED.getMessage());
		}

	}

	private boolean validateIdValue(RequestDTO dto) throws RegPrintAppException {
		boolean isValid = false;
		if (dto.getIdtype().equals(IdType.RID)) {
			isValid = validateRID(dto.getIdValue());
		} else if (dto.getIdtype().equals(IdType.UIN)) {
			isValid = validateUIN(dto.getIdValue());
		} else if (dto.getIdtype().equals(IdType.VID)) {
			isValid = validateVID(dto.getIdValue());
		}
		return isValid;
	}

	private boolean validateVID(String idValue) throws RegPrintAppException {
		boolean isValidVID = false;
		try {
			isValidVID = vidValidatorImpl.validateId(idValue);
			String result = utilities.getUinByVid(idValue);
			if (isValidVID && result != null) {
				isValidVID = true;
			} else {
				throw new RegPrintAppException(PlatformErrorMessages.RPR_PRT_VID_VALIDATION_FAILED,
						PlatformErrorMessages.RPR_PRT_VID_VALIDATION_FAILED.getMessage());
			}
		} catch (InvalidIDException ex) {
			throw new RegPrintAppException(PlatformErrorMessages.RPR_PRT_DATA_VALIDATION_FAILED.getCode(),
					ex.getErrorText(), ex.getCause());

		} catch (IdRepoAppException e) {
			throw new RegPrintAppException(PlatformErrorMessages.RPR_PRT_DATA_VALIDATION_FAILED.getCode(),
					e.getErrorText(), e.getCause());
		} catch (NumberFormatException e) {
			throw new RegPrintAppException(PlatformErrorMessages.RPR_PRT_DATA_VALIDATION_FAILED.getCode(),
					e.getMessage(), e.getCause());
		} catch (ApisResourceAccessException e) {
			throw new RegPrintAppException(PlatformErrorMessages.RPR_PRT_DATA_VALIDATION_FAILED.getCode(),
					e.getErrorText(), e.getCause());
		} catch (VidCreationException e) {
			throw new RegPrintAppException(PlatformErrorMessages.RPR_PRT_DATA_VALIDATION_FAILED.getCode(),
					e.getErrorText(), e.getCause());
		}
		return isValidVID;
	}

	private boolean validateUIN(String idValue) throws RegPrintAppException {
		boolean isValidUIN = false;
		try {
			isValidUIN = uinValidatorImpl.validateId(idValue);
			JSONObject jsonObject = utilities.retrieveIdrepoJson(Long.parseLong(idValue));
			if (isValidUIN && jsonObject != null) {
				isValidUIN = true;
			} else {
				throw new RegPrintAppException(PlatformErrorMessages.RPR_PRT_UIN_VALIDATION_FAILED.getCode(),
						PlatformErrorMessages.RPR_PRT_UIN_VALIDATION_FAILED.getMessage());

			}
		} catch (InvalidIDException ex) {
			throw new RegPrintAppException(PlatformErrorMessages.RPR_PRT_DATA_VALIDATION_FAILED.getCode(),
					ex.getErrorText(), ex.getCause());

		} catch (IdRepoAppException e) {
			throw new RegPrintAppException(PlatformErrorMessages.RPR_PRT_DATA_VALIDATION_FAILED.getCode(),
					e.getErrorText(), e.getCause());
		} catch (NumberFormatException e) {
			throw new RegPrintAppException(PlatformErrorMessages.RPR_PRT_DATA_VALIDATION_FAILED.getCode(),
					e.getMessage(), e.getCause());
		} catch (ApisResourceAccessException e) {
			throw new RegPrintAppException(PlatformErrorMessages.RPR_PRT_DATA_VALIDATION_FAILED.getCode(),
					e.getErrorText(), e.getCause());
		} catch (IOException e) {
			throw new RegPrintAppException(PlatformErrorMessages.RPR_PRT_DATA_VALIDATION_FAILED.getCode(),
					e.getMessage(), e.getCause());
		}
		return isValidUIN;
	}

	private boolean validateRID(String idValue) throws RegPrintAppException {
		boolean isValid = false;
		try {
			isValid = ridValidator.validateId(idValue);
			if (!isValid) {
				throw new RegPrintAppException(PlatformErrorMessages.RPR_PRT_RID_VALIDATION_FAILED.getCode(),
						PlatformErrorMessages.RPR_PRT_RID_VALIDATION_FAILED.getMessage());
			}
		} catch (InvalidIDException ex) {
			throw new RegPrintAppException(PlatformErrorMessages.RPR_PRT_DATA_VALIDATION_FAILED.getCode(),
					ex.getErrorText());
		}
		return isValid;
	}

	private boolean validateIdType(IdType idtype) throws RegPrintAppException {
		if (idtype != null && !idtype.toString().isEmpty()
				&& (idtype.equals(IdType.UIN) || (idtype.equals(IdType.VID) || (idtype.equals(IdType.RID))))) {
			return true;
		} else {

			throw new RegPrintAppException(PlatformErrorMessages.RPR_PRT_CARDTYPE_VALIDATION_FAILED.getCode(),
					PlatformErrorMessages.RPR_PRT_IDTYPE_VALIDATION_FAILED.getMessage());
		}

	}
}
