package io.mosip.authentication.service.impl.otpgen.validator;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.indauth.IdType;
import io.mosip.authentication.core.dto.otpgen.OtpRequestDTO;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.idvalidator.exception.MosipInvalidIDException;
import io.mosip.kernel.idvalidator.uin.impl.UinValidatorImpl;
import io.mosip.kernel.idvalidator.vid.impl.VidValidatorImpl;

/**
 * {@code OTPRequestValidator} do constraint validate of {@link OtpRequestDTO}
 * and enum atribute "idType" validation.
 * 
 * @author Rakesh Roshan
 */
@Component
public class OTPRequestValidator implements Validator {

	/** The Constant A_Z0_9_10. */
	private static final String A_Z0_9_10 = "^[A-Z0-9]{10}";
	
	/** The Constant VALIDATE. */
	private static final String VALIDATE = "validate";
	
	/** The Constant AUTH_REQUEST_VALIDATOR. */
	private static final String AUTH_REQUEST_VALIDATOR = "OTPValidator";
	
	/** The Constant SESSION_ID. */
	private static final String SESSION_ID = "sessionId";

	/** The Constant verPattern. */
	private static final Pattern verPattern = Pattern.compile("^\\d+(\\.\\d{1,1})?$");
	
	/** The Constant muaCodePattern. */
	private static final Pattern muaCodePattern = Pattern.compile(A_Z0_9_10);
	
	/** The Constant txnIdPattern. */
	private static final Pattern txnIdPattern = Pattern.compile(A_Z0_9_10);
	
	/** The Constant msaLicenseKeyPattern. */
	private static final Pattern msaLicenseKeyPattern = Pattern.compile(A_Z0_9_10);

	/** The mosip logger. */
	private static MosipLogger mosipLogger = IdaLogger.getLogger(OTPRequestValidator.class);

	/** The env. */
	@Autowired
	private Environment env;

	/** The uin validator. */
	@Autowired
	private UinValidatorImpl uinValidator;

	/** The vid validator. */
	@Autowired
	private VidValidatorImpl vidValidator;

	/* (non-Javadoc)
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	@Override
	public boolean supports(Class<?> clazz) {
		return OtpRequestDTO.class.equals(clazz);
	}

	/* (non-Javadoc)
	 * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
	 */
	@Override
	public void validate(Object target, Errors errors) {

		OtpRequestDTO otpRequestDto = (OtpRequestDTO) target;

		validateId(errors, otpRequestDto.getId());

		validateIdType(errors, otpRequestDto);

		validateVer(errors, otpRequestDto.getVer());

		validateMuaCode(errors, otpRequestDto.getMuaCode());

		validateTxnId(errors, otpRequestDto.getTxnID());

		validateMsaLicenseKey(errors, otpRequestDto.getMsaLicenseKey());

	}

	/**
	 * Validate id - check whether id is null or not.
	 *
	 * @param errors the errors
	 * @param id the id
	 */
	private void validateId(Errors errors, String id) {
		if (Objects.isNull(id)) {
			mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE,
					"INVALID_INPUT_PARAMETER - idType - value -> " + id);
			errors.rejectValue("id", IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(), "id"));
		}
	}

	/**
	 * Validate id type - check whether id type is present in {@code IdType} enum.
	 *
	 * @param errors the errors
	 * @param otpRequestDto the otp request dto
	 */
	private void validateIdType(Errors errors, OtpRequestDTO otpRequestDto) {
		String idType = otpRequestDto.getIdType();

		if (!Objects.isNull(idType) && idType.equalsIgnoreCase(IdType.UIN.getType())) {
			try {
				uinValidator.validateId(otpRequestDto.getId());
			} catch (MosipInvalidIDException e) {
				mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE, "MosipInvalidIDException - " + e);
				errors.rejectValue("id", IdAuthenticationErrorConstants.INVALID_UIN.getErrorCode(),
						IdAuthenticationErrorConstants.INVALID_UIN.getErrorMessage());
			}
		} else if (!Objects.isNull(idType) && idType.equalsIgnoreCase(IdType.VID.getType())) {
			try {
				vidValidator.validateId(otpRequestDto.getId());
			} catch (MosipInvalidIDException e) {
				mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE, "MosipInvalidIDException - " + e);
				errors.rejectValue("id", IdAuthenticationErrorConstants.INVALID_VID.getErrorCode(),
						IdAuthenticationErrorConstants.INVALID_VID.getErrorMessage());
			}
		} else {
			mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE,
					"INVALID_INPUT_PARAMETER - idType - value -> " + idType);
			errors.rejectValue("idType", IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(), "idType"));
		}

		if (!isTimestampValid(otpRequestDto.getReqTime())) {
			errors.rejectValue("reqTime", IdAuthenticationErrorConstants.INVALID_OTP_REQUEST_TIMESTAMP.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.INVALID_OTP_REQUEST_TIMESTAMP.getErrorMessage(),
							env.getProperty("requestdate.received.in.max.time.mins")));
		}
	}

	/**
	 * Validate ver - check whether version is one digit with one fraction.
	 *
	 * @param errors the errors
	 * @param ver the ver
	 */
	private void validateVer(Errors errors, String ver) {
		if (!Objects.isNull(ver) && !verPattern.matcher(ver).matches()) {
			mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE,
					"INVALID_INPUT_PARAMETER - ver - value -> " + ver);
			errors.rejectValue("ver", IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(), "ver"));
		}
	}

	/**
	 * Validate mua code - check whether it is of length 10 and alphanumeric.
	 *
	 * @param errors the errors
	 * @param muaCode the mua code
	 */
	private void validateMuaCode(Errors errors, String muaCode) {
		if (!Objects.isNull(muaCode) && !muaCodePattern.matcher(muaCode).matches()) {
			mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE,
					"INVALID_INPUT_PARAMETER - muaCode - value -> " + muaCode);
			errors.rejectValue("muaCode", IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(), "muaCode"));
		}
	}

	/**
	 * Validate txn id - check whether it is of length 10 and alphanumeric.
	 *
	 * @param errors the errors
	 * @param txnID the txn ID
	 */
	private void validateTxnId(Errors errors, String txnID) {
		if (!Objects.isNull(txnID) && !txnIdPattern.matcher(txnID).matches()) {
			mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE,
					"INVALID_INPUT_PARAMETER - txnID - value -> " + txnID);
			errors.rejectValue("txnID", IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(), "txnID"));
		}
	}

	/**
	 * Validate msa license key - check whether it is of length 10 and alphanumeric.
	 *
	 * @param errors the errors
	 * @param msaLicenseKey the msa license key
	 */
	private void validateMsaLicenseKey(Errors errors, String msaLicenseKey) {
		if (!Objects.isNull(msaLicenseKey) && !msaLicenseKeyPattern.matcher(msaLicenseKey).matches()) {
			mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE,
					"INVALID_INPUT_PARAMETER - msaLicenseKey - value -> " + msaLicenseKey);
			errors.rejectValue("msaLicenseKey", IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(),
							"msaLicenseKey"));
		}
	}

	/**
	 * Checks if is timestamp valid.
	 *
	 * @param timestamp the timestamp
	 * @return true, if is timestamp valid
	 */
	private boolean isTimestampValid(Date timestamp) {

		Date reqTime = (Date) timestamp.clone();
		Instant reqTimeInstance = reqTime.toInstant();
		Instant now = Instant.now();

		return Duration.between(reqTimeInstance, now).toMinutes() < env
				.getProperty("requestdate.received.in.max.time.mins", Integer.class);

	}
}
