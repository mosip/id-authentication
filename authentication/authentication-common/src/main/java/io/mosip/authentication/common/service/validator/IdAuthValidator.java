package io.mosip.authentication.common.service.validator;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.ID;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.IDV_ID;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.IDV_ID_TYPE;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.REQUEST;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.REQ_TIME;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.SESSION_ID;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.TRANSACTION_ID;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.util.IdValidationUtil;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.exception.ParseException;
import io.mosip.kernel.core.function.FunctionWithThrowable;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.StringUtils;

/**
 * The Class IdAuthValidator - abstract class containing common validations.
 *
 * @author Manoj SP
 */
@Component
public abstract class IdAuthValidator implements Validator {
	
	/** The Constant VALIDATE_REQUEST_TIMED_OUT. */
	private static final String VALIDATE_REQUEST_TIMED_OUT = "validateRequestTimedOut";

	/** The Constant MISSING_INPUT_PARAMETER. */
	protected static final String MISSING_INPUT_PARAMETER = "MISSING_INPUT_PARAMETER - ";

	/** The Constant VALIDATE. */
	protected static final String VALIDATE = "VALIDATE";

	/** The Constant A_Z0_9_10. */
	private static final Pattern A_Z0_9_10 = Pattern.compile("^[A-Za-z0-9]{10}");

	/** The mosip logger. */
	private static Logger mosipLogger = IdaLogger.getLogger(IdAuthValidator.class);

	/** The Constant CONSENT_OBTAINED. */
	private static final String CONSENT_OBTAINED = "consentObtained";

	@Autowired
	IdValidationUtil idValidator;
	

	/**
	 * Validate id - check whether id is null or not.
	 *
	 * @param id     the id
	 * @param errors the errors
	 */
	public void validateId(String id, Errors errors) {
		// TODO check id based on the request and add validation for version.
		if (StringUtils.isEmpty(id)) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), VALIDATE,
					MISSING_INPUT_PARAMETER + " - id");
			errors.rejectValue(ID, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
					new Object[] { ID }, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
		}
	}

	/**
	 * Validate idv id.
	 *
	 * @param id the id
	 * @param idType the id type
	 * @param errors the errors
	 */
	public void validateIdvId(String id, String idType, Errors errors) {
		validateIdvId(id, idType, errors, REQUEST);
	}

	/**
	 * Validate individual's id - check whether id is null or not and if valid,
	 * validates idType and UIN/VID.
	 *
	 * @param id     the id
	 * @param idType the id type
	 * @param errors the errors
	 * @param idFieldName the id field name
	 */
	public void validateIdvId(String id, String idType, Errors errors, String idFieldName) {
		if (id == null || StringUtils.isEmpty(id.trim())) {
			mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), VALIDATE, MISSING_INPUT_PARAMETER + IDV_ID);
			errors.rejectValue(idFieldName, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
					new Object[] { IDV_ID }, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
		} else {
			validateIdtypeUinVid(id, idType, errors, idFieldName);
		}
	}

	/**
	 * Validate txn id - check whether it is of length 10 and alphanumeric.
	 *
	 * @param txnID  the txn ID
	 * @param errors the errors
	 * @param paramName the param name
	 */
	protected void validateTxnId(String txnID, Errors errors, String paramName) {
		if (StringUtils.isEmpty(txnID)) {
			mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), VALIDATE,
					MISSING_INPUT_PARAMETER + TRANSACTION_ID + paramName);
			errors.rejectValue(TRANSACTION_ID, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
					new Object[] { paramName },
					IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
		} else if (!A_Z0_9_10.matcher(txnID).matches()) {
			mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), VALIDATE,
					"INVALID_INPUT_PARAMETER - txnID - value -> " + txnID + paramName);
			errors.rejectValue(TRANSACTION_ID, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					new Object[] { paramName },
					IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
		}
	}
	
	/**
	 * Validate req time.
	 *
	 * @param reqTime the req time
	 * @param errors the errors
	 * @param paramName the param name
	 */
	protected void validateReqTime(String reqTime, Errors errors, String paramName) {
		validateReqTime(reqTime, errors, paramName, this::requestTimeParser);
	}
	
	/**
	 * Validate req time.
	 *
	 * @param reqTime the req time
	 * @param errors the errors
	 * @param paramName the param name
	 * @param dateTimeParser the date time parser
	 */
	protected void validateReqTime(String reqTime, Errors errors, String paramName, FunctionWithThrowable<Date, String, ParseException> dateTimeParser) {
		validateReqTime(reqTime, errors, paramName, REQ_TIME, dateTimeParser);
	}

	/**
	 * Validate req time.
	 *
	 * @param reqTime the req time
	 * @param errors  the errors
	 * @param paramName the param name
	 * @param fieldName the field name
	 * @param parser the parser
	 */
	private void validateReqTime(String reqTime, Errors errors, String paramName, String fieldName, FunctionWithThrowable<Date, String, ParseException> parser) {

		if (StringUtils.isEmpty(reqTime)) {
			mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), VALIDATE,
					MISSING_INPUT_PARAMETER + paramName);
			errors.rejectValue(fieldName, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
					new Object[] { paramName },
					IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
		} else {
			checkFutureReqTime(reqTime, errors, paramName, fieldName, parser);
		}
	}

	/**
	 * Check future req time.
	 *
	 * @param reqTime the req time
	 * @param errors  the errors
	 * @param paramName the param name
	 * @param fieldName the field name
	 * @param dateTimeParser the date time parser
	 */
	private void checkFutureReqTime(String reqTime, Errors errors, String paramName, String fieldName, FunctionWithThrowable<Date, String, ParseException> dateTimeParser) {
		Date reqDateAndTime = null;
		try {
			reqDateAndTime = dateTimeParser.apply(reqTime);
		} catch (ParseException e) {
			mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), VALIDATE,
					"ParseException : Invalid Date\n" + ExceptionUtils.getStackTrace(e));
			errors.rejectValue(fieldName, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					new Object[] { paramName },
					IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
		}

		Date plusAdjustmentTime = getCurrentTimePlusAdjutsmentTime();

		if (reqDateAndTime != null && DateUtils.after(reqDateAndTime, plusAdjustmentTime)) {
			mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), VALIDATE, "Invalid Date");
			Long reqDateMaxTimeLong = EnvUtil.getAuthRequestReceivedTimeAllowedInSeconds();
			String message;
			if (paramName == null) {
				message = IdAuthenticationErrorConstants.INVALID_TIMESTAMP.getErrorMessage();
			} else {
				message = String.format("%s. Attribute: %s", IdAuthenticationErrorConstants.INVALID_TIMESTAMP.getErrorMessage(), paramName);
			}
			errors.rejectValue(IdAuthCommonConstants.REQ_TIME,
					IdAuthenticationErrorConstants.INVALID_TIMESTAMP.getErrorCode(),
					new Object[] { reqDateMaxTimeLong },
					message);
		}
	}

	/**
	 * Gets the current time plus adjutsment time.
	 *
	 * @return the current time plus adjutsment time
	 */
	private Date getCurrentTimePlusAdjutsmentTime() {
		return getAdjustmentTime(LocalDateTime.now(), LocalDateTime::plusSeconds);
	}
	
	/**
	 * Gets the adjustment time.
	 *
	 * @param originalLdt the original ldt
	 * @param adjustmentFunc the adjustment func
	 * @return the adjustment time
	 */
	private Date getAdjustmentTime(LocalDateTime originalLdt, BiFunction<LocalDateTime, Long, LocalDateTime> adjustmentFunc) {
		long adjustmentSeconds = EnvUtil.getRequestTimeAdjustmentSeconds();
		LocalDateTime ldt = adjustmentFunc.apply(originalLdt, adjustmentSeconds);
		Date plusAdjustmentTime = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
		return plusAdjustmentTime;
	}

	/**
	 * Validate request timed out.
	 *
	 * @param reqTime the req time
	 * @param errors the errors
	 */
	protected void validateRequestTimedOut(String reqTime, Errors errors) {
		validateRequestTimedOut(reqTime, errors, this::requestTimeParser, null);
	}
	
	/**
	 * Validate request timed out.
	 *
	 * @param reqTime the req time
	 * @param errors  the errors
	 * @param dateTimeParser the date time parser
	 * @param paramName the param name
	 */
	protected void validateRequestTimedOut(String reqTime, Errors errors, FunctionWithThrowable<Date, String, ParseException> dateTimeParser, String paramName) {
		try {
			Instant reqTimeInstance = dateTimeParser.apply(reqTime).toInstant();
			Instant now = Instant.now();
			mosipLogger.debug(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
					VALIDATE_REQUEST_TIMED_OUT,
					"reqTimeInstance" + reqTimeInstance.toString() + " -- current time : " + now.toString());
			Long reqDateMaxTimeLong = EnvUtil.getAuthRequestReceivedTimeAllowedInSeconds();
			Long adjustmentSeconds = EnvUtil.getRequestTimeAdjustmentSeconds();
			Instant maxAllowedEarlyInstant = now.minus(reqDateMaxTimeLong + adjustmentSeconds, ChronoUnit.SECONDS);
			if (reqTimeInstance.isBefore(maxAllowedEarlyInstant)) {
				mosipLogger.debug(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
						VALIDATE_REQUEST_TIMED_OUT,
						"Time difference in sec : " + Duration.between(reqTimeInstance, now).toSeconds());
				mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
						VALIDATE_REQUEST_TIMED_OUT,
						"INVALID_AUTH_REQUEST_TIMESTAMP -- "
								+ String.format(IdAuthenticationErrorConstants.INVALID_TIMESTAMP.getErrorMessage(),
										Duration.between(reqTimeInstance, now).toSeconds() - reqDateMaxTimeLong));
				String message;
				if (paramName == null) {
					message = IdAuthenticationErrorConstants.INVALID_TIMESTAMP.getErrorMessage();
				} else {
					message = String.format("%s. Attribute: %s", IdAuthenticationErrorConstants.INVALID_TIMESTAMP.getErrorMessage(), paramName);
				}
				errors.rejectValue(IdAuthCommonConstants.REQ_TIME,
						IdAuthenticationErrorConstants.INVALID_TIMESTAMP.getErrorCode(),
						new Object[] { reqDateMaxTimeLong },
						message);
			}
		} catch (DateTimeParseException | ParseException e) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
					VALIDATE_REQUEST_TIMED_OUT,
					IdAuthCommonConstants.INVALID_INPUT_PARAMETER + IdAuthCommonConstants.REQ_TIME);
			errors.rejectValue(IdAuthCommonConstants.REQ_TIME,
					IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					new Object[] { IdAuthCommonConstants.REQ_TIME },
					IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
		}

	}

	/**
	 * Validate UIN, VID.
	 *
	 * @param id     the id
	 * @param idTypeOrAlias the id type
	 * @param errors the errors
	 * @param idFieldName the id field name
	 */
	private void validateIdtypeUinVid(String id, String idTypeOrAlias, Errors errors, String idFieldName) {
		Set<String> allowedIdTypeSet = getAllowedIdTypes();
		// Checks for null IdType
		if (StringUtils.isEmpty(idTypeOrAlias)) {
			mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), VALIDATE,
					MISSING_INPUT_PARAMETER + IDV_ID_TYPE);
			errors.rejectValue(idFieldName, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
					new Object[] { IDV_ID_TYPE },
					IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
		} // checks IdType is Allowed or Not
		else if (allowedIdTypeSet.contains(IdType.getIDTypeStrOrSameStr(idTypeOrAlias))) {
			Optional<IdType> idTypeOpt = IdType.getIDType(idTypeOrAlias);
			if(idTypeOpt.isPresent()) {
				IdType idType = idTypeOpt.get();
				//If UIN alias is configured only that is allowed
				if (idType.getAliasOrType().equals(idTypeOrAlias)) {
					if (idType == IdType.UIN) {
						try {
							idValidator.validateUIN(id);
						} catch (IdAuthenticationBusinessException e) {
							mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), VALIDATE,
									"InvalidIDException - " + e);
							errors.rejectValue(idFieldName, IdAuthenticationErrorConstants.INVALID_UIN.getErrorCode(),
									IdAuthenticationErrorConstants.INVALID_UIN.getErrorMessage());

						}
					} else if (idType == IdType.VID) {
						try {
							idValidator.validateVID(id);
						} catch (IdAuthenticationBusinessException e) {
							mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), VALIDATE,
									"InvalidIDException - " + e);
							errors.rejectValue(idFieldName, IdAuthenticationErrorConstants.INVALID_VID.getErrorCode(),
									IdAuthenticationErrorConstants.INVALID_VID.getErrorMessage());
						}
					}
				} else {
					mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), VALIDATE,
							"INCORRECT_IDTYPE - " + idTypeOrAlias);
					errors.rejectValue(IDV_ID_TYPE, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
							new Object[] { IDV_ID_TYPE },
							IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
				}
			} else {
				mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), VALIDATE,
						"INCORRECT_IDTYPE - " + idTypeOrAlias);
				errors.rejectValue(IDV_ID_TYPE, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
						new Object[] { IDV_ID_TYPE },
						IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
			}
		} else {
			// Checks idType is valid or invalid.If Valid and not configured
			// IDENTITYTYPE_NOT_ALLOWED error is thrown else INVALID_INPUT_PARAMETER will be
			// thrown.
			if (IdType.getIDType(idTypeOrAlias)
					.filter(idType -> idType.getAliasOrType().equals(idTypeOrAlias)).isPresent()) {
				mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), VALIDATE,
						"NOT ALLOWED IDENTITY TYPE - " + idTypeOrAlias);
				errors.rejectValue(IDV_ID_TYPE, IdAuthenticationErrorConstants.IDENTITYTYPE_NOT_ALLOWED.getErrorCode(),
						new Object[] { idTypeOrAlias },
						IdAuthenticationErrorConstants.IDENTITYTYPE_NOT_ALLOWED.getErrorMessage());
			} else {
				mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), VALIDATE,
						"INCORRECT_IDTYPE - " + idTypeOrAlias);
				errors.rejectValue(IDV_ID_TYPE, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
						new Object[] { IDV_ID_TYPE },
						IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
			}
		}
	}

	/**
	 * Gets the allowed id types.
	 *
	 * @return the allowed id types
	 */
	protected Set<String> getAllowedIdTypes() {
		return Stream.of(getAllowedIdTypesConfigVal().split(","))
				.map(String::trim)
				.filter(str -> !str.isEmpty())
				.collect(Collectors.toSet());
	}
	
	/**
	 * Gets the allowed auth types.
	 *
	 * @return the allowed auth types
	 */
	protected Set<String> getAllowedAuthTypes() {
		return getAllowedAuthTypes(getAllowedAuthTypeProperty());
	}
	
	/**
	 * Extract auth info.
	 *
	 * @param configKey the config key
	 * @return the sets the
	 */
	private Set<String> getAllowedAuthTypes(String configKey) {
		return Stream.of(configKey.split(","))
				.map(String::trim)
				.filter(str -> !str.isEmpty())
				.collect(Collectors.toSet());
	}

	/**
	 * Gets the allowed auth type property.
	 *
	 * @return the allowedAuthType
	 */
	protected String getAllowedAuthTypeProperty() {
		return EnvUtil.getAllowedAuthType();
	}

	/**
	 * Gets the allowed id types config key.
	 *
	 * @return the allowed id types config key
	 */
	protected String getAllowedIdTypesConfigVal() {
		return EnvUtil.getAllowedIdTypes();
	}

	/**
	 * Validates the ConsentRequest on request.
	 *
	 * @param consentValue the consent value
	 * @param errors         the errors
	 */
	protected void validateConsentReq(boolean consentValue, Errors errors) {
		if (!consentValue) {
			mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), VALIDATE,
					"consentObtained - " + consentValue);
			errors.rejectValue(CONSENT_OBTAINED, IdAuthenticationErrorConstants.CONSENT_NOT_AVAILABLE.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.CONSENT_NOT_AVAILABLE.getErrorMessage(),
							CONSENT_OBTAINED));
		}
	}

	/**
	 * Validate txn id.
	 *
	 * @param transactionID        the transaction ID
	 * @param requestTransactionID the request transaction ID
	 * @param errors               the errors
	 */
	protected void validateTxnId(String transactionID, String requestTransactionID, Errors errors) {
		if (!StringUtils.isEmpty(requestTransactionID) && !StringUtils.isEmpty(transactionID)
				&& !transactionID.equals(requestTransactionID)) {
			errors.rejectValue(TRANSACTION_ID, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					new Object[] { TRANSACTION_ID },
					IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
		}
	}

	/**
	 * Request time parser.
	 *
	 * @param reqTime the req time
	 * @return the date
	 * @throws ParseException the parse exception
	 */
	protected Date requestTimeParser(String reqTime) throws ParseException {
		return DateUtils.parseToDate(reqTime, EnvUtil.getDateTimePattern());
	}

}
