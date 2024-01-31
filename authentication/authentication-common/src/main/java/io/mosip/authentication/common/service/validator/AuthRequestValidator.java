package io.mosip.authentication.common.service.validator;

import static io.mosip.authentication.core.constant.IdAuthCommonConstants.BIO_PATH;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.REQUEST;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.SESSION_ID;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import io.mosip.authentication.common.service.util.AuthTypeUtil;
import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.BioIdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.DataDTO;
import io.mosip.authentication.core.indauth.dto.DigitalId;
import io.mosip.authentication.core.indauth.dto.KycAuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.RequestDTO;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.kernel.core.exception.ParseException;
import io.mosip.kernel.core.function.FunctionWithThrowable;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.StringUtils;

/**
 * 
 * This class validates the parameters for Authorization Request. The class
 * {@code AuthRequestValidator} validates AuthRequestDTO
 * 
 * @author Manoj SP
 * @author Rakesh Roshan
 * 
 */
@Component
@Primary
public class AuthRequestValidator extends BaseAuthRequestValidator {

	private static final String DATE_TIME = "dateTime";

	private static final String DATA_TIMESTAMP = "data/timestamp";

	/** The Constant DIGITAL_ID. */
	private static final String DIGITAL_ID = "data/digitalId/";

	/** The Constant FINGERPRINT_COUNT. */
	private static final int FINGERPRINT_COUNT = 10;

	/** The Constant REQUEST_REQUEST_TIME. */
	private static final String REQUEST_REQUEST_TIME = "request/timestamp";

	/** The mosip logger. */
	private static Logger mosipLogger = IdaLogger.getLogger(AuthRequestValidator.class);

	/**
	 * Allowed environments
	 */
	private List<String> allowedEnvironments;
	
	/**
	 * Allowed domainUris
	 */
	private List<String> allowedDomainUris;
	
	@PostConstruct
	public void initialize() {
		allowedEnvironments = Arrays.stream(EnvUtil.getAllowedEnv().split((",")))
				.map(String::trim).collect(Collectors.toList());
		allowedDomainUris = Arrays.stream(EnvUtil.getAllowedDomainUri().split((",")))
				.map(String::trim).collect(Collectors.toList());
	}

	/**
	 * Supports.
	 *
	 * @param clazz the clazz
	 * @return true, if successful
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.service.impl.indauth.validator.
	 * BaseAuthRequestValidator#supports(java.lang.Class)
	 */
	@Override
	public boolean supports(Class<?> clazz) {
		return AuthRequestDTO.class.equals(clazz) || KycAuthRequestDTO.class.equals(clazz);
	}

	/**
	 * Validate.
	 *
	 * @param target the target
	 * @param errors the errors
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.service.impl.indauth.validator.
	 * BaseAuthRequestValidator#validate(java.lang.Object,
	 * org.springframework.validation.Errors)
	 */
	@Override
	public void validate(Object target, Errors errors) {

		AuthRequestDTO authRequestDto = (AuthRequestDTO) target;

		if (authRequestDto != null) {
			if (!errors.hasErrors()) {
				validateConsentReq(authRequestDto.isConsentObtained(), errors);
			}
			
			if (!errors.hasErrors()) {
				validateReqTime(authRequestDto.getRequestTime(), errors, IdAuthCommonConstants.REQ_TIME);
				// Validation for Time Stamp in the RequestDTO.
				validateReqTime(authRequestDto.getRequest().getTimestamp(), errors, REQUEST_REQUEST_TIME);
			}

			if (!errors.hasErrors()) {
				validateDomainURI(authRequestDto, errors);
			}
			
			if (!errors.hasErrors()) {
				validateEnv(authRequestDto, errors);
			}
			
			if (!errors.hasErrors()) {
				validateTxnId(authRequestDto.getTransactionID(), errors, IdAuthCommonConstants.TRANSACTION_ID);
			}
			if (!errors.hasErrors()) {
				validateAllowedAuthTypes(authRequestDto, errors);
			}
			
			if (!errors.hasErrors()) {
				validateBiometrics(authRequestDto.getRequest().getBiometrics(), authRequestDto.getTransactionID(), errors);
			}
			
			if (!errors.hasErrors()) {
				super.validate(target, errors);

				if (!errors.hasErrors()) {
					checkAuthRequest(authRequestDto, errors);
				}
			}
			
			if (!errors.hasErrors()) {
				validateAuthType(authRequestDto, errors);
			}
			
		} else {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), IdAuthCommonConstants.VALIDATE,
					IdAuthCommonConstants.INVALID_INPUT_PARAMETER + REQUEST);
			errors.rejectValue(REQUEST ,IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode(),
					IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorMessage());
		}
	}

	/**
	 * Validate biometric timestamps.
	 *
	 * @param biometrics the biometrics
	 * @param authTxnId 
	 * @param errors     the errors
	 */
	protected void validateBiometrics(List<BioIdentityInfoDTO> biometrics, String authTxnId, Errors errors) {
		if (biometrics != null) {
			for (int i = 0; i < biometrics.size(); i++) {
				BioIdentityInfoDTO bioIdentityInfoDTO = biometrics.get(i);
				if (bioIdentityInfoDTO.getData() == null) {
					errors.rejectValue(IdAuthCommonConstants.REQUEST,
							IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
							new Object[] { String.format(BIO_PATH, i, IdAuthCommonConstants.DATA) },
							IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
				} else {
					validateBioTxnId(authTxnId, errors, i, bioIdentityInfoDTO.getData().getTransactionId());
					validateBiometricTimestampAndDigitalIdTimestamp(biometrics.size() - 1, errors, i,
							bioIdentityInfoDTO.getData());
					validateSuccessiveBioSegmentTimestamp(biometrics, errors, i, bioIdentityInfoDTO);
				}
			}
		}
	}

	private void validateSuccessiveBioSegmentTimestamp(List<BioIdentityInfoDTO> biometrics, Errors errors, int index,
			BioIdentityInfoDTO bioIdentityInfoDTO) {
		if (!errors.hasErrors() && index != 0) {
			LocalDateTime currentIndexDateTime = DateUtils.parseDateToLocalDateTime(
					this.biometricTimestampParser(bioIdentityInfoDTO.getData().getTimestamp()));
			LocalDateTime previousIndexDateTime = DateUtils.parseDateToLocalDateTime(
					this.biometricTimestampParser((biometrics.get(index - 1).getData().getTimestamp())));
			long bioTimestampDiffInSeconds = Duration.between(previousIndexDateTime, currentIndexDateTime).toSeconds();
			
			Long allowedTimeDiffInSeconds = EnvUtil.getBioSegmentTimeDiffAllowed();
			if (bioTimestampDiffInSeconds < 0 || bioTimestampDiffInSeconds > allowedTimeDiffInSeconds) {
				mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), VALIDATE,
						IdAuthenticationErrorConstants.INVALID_BIO_TIMESTAMP);
				errors.rejectValue(IdAuthCommonConstants.REQUEST,
						IdAuthenticationErrorConstants.INVALID_BIO_TIMESTAMP.getErrorCode(), new Object[] { allowedTimeDiffInSeconds },
						IdAuthenticationErrorConstants.INVALID_BIO_TIMESTAMP.getErrorMessage());
			}
			validateSuccessiveDigitalIdTimestamp(biometrics, errors, index, bioIdentityInfoDTO, allowedTimeDiffInSeconds);
		}
	}

	protected void validateSuccessiveDigitalIdTimestamp(List<BioIdentityInfoDTO> biometrics, Errors errors, int index,
			BioIdentityInfoDTO bioIdentityInfoDTO, Long allowedTimeDiffInSeconds) {
		LocalDateTime currentIndexDateTime = DateUtils.parseDateToLocalDateTime(
				this.biometricTimestampParser(bioIdentityInfoDTO.getData().getDigitalId().getDateTime()));
		LocalDateTime previousIndexDateTime = DateUtils.parseDateToLocalDateTime(
				this.biometricTimestampParser(biometrics.get(index - 1).getData().getDigitalId().getDateTime()));
		long digitalIdTimestampDiffInSeconds = Duration.between(previousIndexDateTime, currentIndexDateTime).toSeconds();
		if (digitalIdTimestampDiffInSeconds < 0 || digitalIdTimestampDiffInSeconds > allowedTimeDiffInSeconds) {
			mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), VALIDATE,
					IdAuthenticationErrorConstants.INVALID_BIO_DIGITALID_TIMESTAMP);
			errors.rejectValue(IdAuthCommonConstants.REQUEST,
					IdAuthenticationErrorConstants.INVALID_BIO_DIGITALID_TIMESTAMP.getErrorCode(), new Object[] { allowedTimeDiffInSeconds },
					IdAuthenticationErrorConstants.INVALID_BIO_DIGITALID_TIMESTAMP.getErrorMessage());
		}
	}

	private void validateBioTxnId(String authTxnId, Errors errors, int index, String bioTxnId) {
		// authTxnId validation is already done at this point
		if (Objects.isNull(bioTxnId)) {
			errors.rejectValue(IdAuthCommonConstants.REQUEST,
					IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
					new Object[] { String.format(BIO_PATH, index, IdAuthCommonConstants.BIO_TXN_ID_PATH) },
					IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
			
		} else
		if(!authTxnId.contentEquals(bioTxnId)) {
			errors.rejectValue(IdAuthCommonConstants.REQUEST,
					IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					new Object[] { String.format(BIO_PATH, index, IdAuthCommonConstants.BIO_TXN_ID_PATH) },
					IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
		}
	}

	private void validateBiometricTimestampAndDigitalIdTimestamp(int biometricSize, Errors errors, int index,
			DataDTO dataDTO) {
		
		String paramName = String.format(BIO_PATH, index, DATA_TIMESTAMP);
		if (index == biometricSize) {
			// validating future datetime check and other checks on last segment of bio and
			// digitalId
			validateReqTime(dataDTO.getTimestamp(), errors, paramName, this::biometricTimestampParser);

			if (!errors.hasErrors()) {
				validateDigitalIdTimestamp(dataDTO.getDigitalId(), errors, String.format(BIO_PATH, index, DIGITAL_ID));
			}
		} else {
			// validating null check on bio timestamps and digitialId timestamps except last
			// segment
			nullCheckOnBioTimestampAndDigitalIdTimestamp(errors, index, dataDTO, paramName);
		}
	}

	private void nullCheckOnBioTimestampAndDigitalIdTimestamp(Errors errors, int i, DataDTO dataDTO, String paramName) {
		if (StringUtils.isEmpty(dataDTO.getTimestamp())) {
			mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), VALIDATE,
					MISSING_INPUT_PARAMETER + paramName);
			errors.rejectValue(IdAuthCommonConstants.REQUEST,
					IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(), new Object[] { paramName },
					IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
		}
		// null check only on digitalId and digitalId timestamp
		nullCheckDigitalIdAndTimestamp(dataDTO.getDigitalId(), errors, String.format(BIO_PATH, i, DIGITAL_ID));
	}

	/**
	 * Validate digital id timestamp.
	 *
	 * @param digitalId the digital id
	 * @param errors    the errors
	 * @param field     the field
	 */
	protected void validateDigitalIdTimestamp(DigitalId digitalId, Errors errors, String field) {
		final String dateTimeField = field + DATE_TIME;
		if (nullCheckDigitalIdAndTimestamp(digitalId, errors, field)) {
			validateReqTime(digitalId.getDateTime(), errors, dateTimeField, this::biometricTimestampParser);
		}

	}

	protected boolean nullCheckDigitalIdAndTimestamp(DigitalId digitalId, Errors errors, String field) {
		if (digitalId != null) {
			if (digitalId.getDateTime() == null) {
				errors.rejectValue(IdAuthCommonConstants.REQUEST,
						IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(), new Object[] { field + DATE_TIME },
						IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
				return false;
			}
		} else {
			errors.rejectValue(IdAuthCommonConstants.REQUEST,
					IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(), new Object[] { field },
					IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
			return false;
		}
		return true;
	}

	/**
	 * Validate domain URI and env.
	 *
	 * @param authRequestDto the auth request dto
	 * @param errors         the errors
	 */
	private void validateDomainURI(AuthRequestDTO authRequestDto, Errors errors) {		

		// It is error if domain URI in request is not null but in biometrics it is null
		if(authRequestDto.getDomainUri() != null) {
			String nullBioDomainUris = "";
			if(authRequestDto.getRequest().getBiometrics() != null) {
				nullBioDomainUris = IntStream.range(0, authRequestDto.getRequest().getBiometrics().size())
						.filter(i -> Objects.nonNull(authRequestDto.getRequest().getBiometrics().get(i).getData())
								&& authRequestDto.getRequest().getBiometrics().get(i).getData().getDomainUri() == null)
						.mapToObj(String::valueOf).collect(Collectors.joining(","));
			}
		
			if (!nullBioDomainUris.isEmpty()) {
				mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
						IdAuthCommonConstants.VALIDATE, "bio domain uri is null");
				errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
						String.format(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(),
								"request/biometrics/" + nullBioDomainUris + "/data/domainUri"));
			}
		}

		// It is error if domain URI in biometrics is not null and null in the request
		if (authRequestDto.getDomainUri() == null && (authRequestDto.getRequest().getBiometrics() != null &&
				authRequestDto.getRequest().getBiometrics().stream().filter(bio -> Objects.nonNull(bio.getData()))
				.anyMatch(bio -> bio.getData().getDomainUri() != null))) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
					IdAuthCommonConstants.VALIDATE, "request domainUri is null");
			errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(),
							"request/domainUri"));

		}

		if(authRequestDto.getDomainUri() != null && authRequestDto.getRequest().getBiometrics() != null) {
			// Both are not null and they both are not equal			
			String requestAndBioDomainUrisNotSame = IntStream
					.range(0, authRequestDto.getRequest().getBiometrics().size())
					.filter(i -> Objects.nonNull(authRequestDto.getRequest().getBiometrics().get(i).getData())
							&& authRequestDto.getRequest().getBiometrics().get(i).getData().getDomainUri() != null
							&& !authRequestDto.getRequest().getBiometrics().get(i).getData().getDomainUri()
									.contentEquals(authRequestDto.getDomainUri()))
					.mapToObj(String::valueOf).collect(Collectors.joining(","));
			if(!requestAndBioDomainUrisNotSame.isEmpty()) {
				mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
						IdAuthCommonConstants.VALIDATE, "request domainUri is no matching against bio domainUri");
				errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INPUT_MISMATCH.getErrorCode(),
						String.format(IdAuthenticationErrorConstants.INPUT_MISMATCH.getErrorMessage(),
								"request/biometrics/" + requestAndBioDomainUrisNotSame + "/data/domainUri", "request/domainUri"));
			}
		}
		
		if(authRequestDto.getRequest().getBiometrics() != null) {
			// bio domain uri is not null and not matching with configurations
			String notMatchingBioDomainsUris = IntStream.range(0, authRequestDto.getRequest().getBiometrics().size())
					.filter(i -> Objects.nonNull(authRequestDto.getRequest().getBiometrics().get(i).getData())
							&& authRequestDto.getRequest().getBiometrics().get(i).getData().getDomainUri() != null
							&& !isValuesContainsIgnoreCase(allowedDomainUris,
									authRequestDto.getRequest().getBiometrics().get(i).getData().getDomainUri()))
					.mapToObj(String::valueOf).collect(Collectors.joining(","));
			if (!notMatchingBioDomainsUris.isEmpty()) {
				mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
						IdAuthCommonConstants.VALIDATE, "bio domain uri is not matching with configured domain uris");
				errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
						String.format(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(),
								"request/biometrics/" + notMatchingBioDomainsUris + "/data/domainUri"));
	
			}
		}

		// request domain uri is not null and not matching with configurations
		if (authRequestDto.getDomainUri() != null
				&& !isValuesContainsIgnoreCase(allowedDomainUris, authRequestDto.getDomainUri())) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
					IdAuthCommonConstants.VALIDATE,
					"request domain uri is not matching with configured domain uris");
			errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(),
							"request/domainUri"));
		}

	}
	
	/**
	 * Validate domain URI and env.
	 *
	 * @param authRequestDto the auth request dto
	 * @param errors         the errors
	 */
	private void validateEnv(AuthRequestDTO authRequestDto, Errors errors) {		

		if(authRequestDto.getEnv() != null) {
			String nullBioEnvUris = "";
			if(authRequestDto.getRequest().getBiometrics() != null) {
				nullBioEnvUris = IntStream.range(0, authRequestDto.getRequest().getBiometrics().size())
						.filter(i -> Objects.nonNull(authRequestDto.getRequest().getBiometrics().get(i).getData())
								&& authRequestDto.getRequest().getBiometrics().get(i).getData().getEnv() == null)
						.mapToObj(String::valueOf).collect(Collectors.joining(","));
			}
		
			if (!nullBioEnvUris.isEmpty()) {
				mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
						IdAuthCommonConstants.VALIDATE, "bio env is null");
				errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
						String.format(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(),
								"request/biometrics/" + nullBioEnvUris + "/data/env"));
			}
		}

		// It is error if env in biometrics is not null and null in the request
		if (authRequestDto.getEnv() == null && (authRequestDto.getRequest().getBiometrics() != null &&
				authRequestDto.getRequest().getBiometrics().stream().filter(bio -> Objects.nonNull(bio.getData()))
				.anyMatch(bio -> bio.getData().getEnv() != null))) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
					IdAuthCommonConstants.VALIDATE, "request env is null");
			errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(),
							"request/env"));

		}

		if(authRequestDto.getEnv() != null && authRequestDto.getRequest().getBiometrics() != null) {
			// Both are not null and they both are not equal			
			String requestAndBioEnvNotSame = IntStream
					.range(0, authRequestDto.getRequest().getBiometrics().size())
					.filter(i -> Objects.nonNull(authRequestDto.getRequest().getBiometrics().get(i).getData())
							&& authRequestDto.getRequest().getBiometrics().get(i).getData().getEnv() != null
							&& !authRequestDto.getRequest().getBiometrics().get(i).getData().getEnv()
									.contentEquals(authRequestDto.getEnv()))
					.mapToObj(String::valueOf).collect(Collectors.joining(","));
			if(!requestAndBioEnvNotSame.isEmpty()) {
				mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
						IdAuthCommonConstants.VALIDATE, "request env is no matching against bio env");
				errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INPUT_MISMATCH.getErrorCode(),
						String.format(IdAuthenticationErrorConstants.INPUT_MISMATCH.getErrorMessage(),
								"request/biometrics/" + requestAndBioEnvNotSame + "/data/env", "request/env"));
			}
		}
		
		if(authRequestDto.getRequest().getBiometrics() != null) {
			// bio env is not null and not matching with configurations
			String notMatchingBioEnvss = IntStream.range(0, authRequestDto.getRequest().getBiometrics().size())
					.filter(i -> Objects.nonNull(authRequestDto.getRequest().getBiometrics().get(i).getData())
							&& authRequestDto.getRequest().getBiometrics().get(i).getData().getEnv() != null
							&& !isValuesContainsIgnoreCase(allowedEnvironments,
									authRequestDto.getRequest().getBiometrics().get(i).getData().getEnv()))
					.mapToObj(String::valueOf).collect(Collectors.joining(","));
			if (!notMatchingBioEnvss.isEmpty()) {
				mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
						IdAuthCommonConstants.VALIDATE, "bio env is not matching with configured environments");
				errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
						String.format(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(),
								"request/biometrics/" + notMatchingBioEnvss + "/data/env"));
	
			}
		}

		// request env is not null and not matching with configurations
		if (authRequestDto.getEnv() != null
				&& !isValuesContainsIgnoreCase(allowedEnvironments, authRequestDto.getEnv())) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
					IdAuthCommonConstants.VALIDATE,
					"request env is not matching with configured environments");
			errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(),
							"request/env"));
		}

	}

	/**
	 * Validate req time.
	 *
	 * @param reqTime   the req time
	 * @param errors    the errors
	 * @param paramName the param name
	 */
	@Override
	protected void validateReqTime(String reqTime, Errors errors, String paramName) {
		super.validateReqTime(reqTime, errors, paramName);
		if (!errors.hasErrors()) {
			validateRequestTimedOut(reqTime, errors);
		}
	}

	/**
	 * Validate req time.
	 *
	 * @param reqTime        the req time
	 * @param errors         the errors
	 * @param paramName      the param name
	 * @param dateTimeParser the date time parser
	 */
	protected void validateReqTime(String reqTime, Errors errors, String paramName,
			FunctionWithThrowable<Date, String, ParseException> dateTimeParser) {
		super.validateReqTime(reqTime, errors, paramName, dateTimeParser);
		if (!errors.hasErrors()) {
			validateRequestTimedOut(reqTime, errors, dateTimeParser, paramName);
		}
	}

	/**
	 * Check auth request.
	 *
	 * @param authRequest the auth request
	 * @param errors      the errors
	 */
	private void checkAuthRequest(AuthRequestDTO authRequest, Errors errors) {
		if (AuthTypeUtil.isDemo(authRequest)) {
			checkDemoAuth(authRequest, errors);
		} 
	}

	/**
	 * Gets the max finger count.
	 *
	 * @return the max finger count
	 */
	@Override
	protected int getMaxFingerCount() {
		return FINGERPRINT_COUNT;
	}

	/**
	 * Validate device details.
	 *
	 * @param authRequest the auth request
	 * @param errors      the errors
	 */
	public void validateDeviceDetails(AuthRequestDTO authRequest, Errors errors) {
		List<DataDTO> bioData = Optional.ofNullable(authRequest.getRequest()).map(RequestDTO::getBiometrics)
				.map(List<BioIdentityInfoDTO>::stream).orElseGet(Stream::empty).map(BioIdentityInfoDTO::getData)
				.collect(Collectors.toList());

		IntStream.range(0, bioData.size()).forEach(index -> {
			if (StringUtils.isEmpty(bioData.get(index).getDeviceCode())) {
				errors.rejectValue(IdAuthCommonConstants.REQUEST,
						IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
						new Object[] { String.format(BIO_PATH, index, "deviceCode") },
						IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
			}
			if (StringUtils.isEmpty(bioData.get(index).getDeviceServiceVersion())) {
				errors.rejectValue(IdAuthCommonConstants.REQUEST,
						IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
						new Object[] { String.format(BIO_PATH, index, "deviceServiceVersion") },
						IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
			}
			if (Objects.isNull(bioData.get(index).getDigitalId())) {
				errors.rejectValue(IdAuthCommonConstants.REQUEST,
						IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
						new Object[] { String.format(BIO_PATH, index, "digitalId") },
						IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
			} else {
				if (StringUtils.isEmpty(bioData.get(index).getDigitalId().getSerialNo())) {
					errors.rejectValue(IdAuthCommonConstants.REQUEST,
							IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
							new Object[] { String.format(BIO_PATH, index, "digitalId/serialNo") },
							IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
				}
				if (StringUtils.isEmpty(bioData.get(index).getDigitalId().getMake())) {
					errors.rejectValue(IdAuthCommonConstants.REQUEST,
							IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
							new Object[] { String.format(BIO_PATH, index, DIGITAL_ID + "make") },
							IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
				}
				if (StringUtils.isEmpty(bioData.get(index).getDigitalId().getModel())) {
					errors.rejectValue(IdAuthCommonConstants.REQUEST,
							IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
							new Object[] { String.format(BIO_PATH, index, DIGITAL_ID + "model") },
							IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
				}
				if (StringUtils.isEmpty(bioData.get(index).getDigitalId().getType())) {
					errors.rejectValue(IdAuthCommonConstants.REQUEST,
							IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
							new Object[] { String.format(BIO_PATH, index, DIGITAL_ID + "type") },
							IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
				}
				if (StringUtils.isEmpty(bioData.get(index).getDigitalId().getDeviceSubType())) {
					errors.rejectValue(IdAuthCommonConstants.REQUEST,
							IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
							new Object[] { String.format(BIO_PATH, index, DIGITAL_ID + "deviceSubType") },
							IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
				}
				if (StringUtils.isEmpty(bioData.get(index).getDigitalId().getDp())) {
					errors.rejectValue(IdAuthCommonConstants.REQUEST,
							IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
							new Object[] { String.format(BIO_PATH, index, DIGITAL_ID + "deviceProvider") },
							IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
				}
				if (StringUtils.isEmpty(bioData.get(index).getDigitalId().getDpId())) {
					errors.rejectValue(IdAuthCommonConstants.REQUEST,
							IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
							new Object[] { String.format(BIO_PATH, index, DIGITAL_ID + "deviceProviderId") },
							IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
				}

			}
		});
	}

	/**
	 * Biometric timestamp parser.
	 *
	 * @param timestamp the timestamp
	 * @return the date
	 * @throws ParseException the parse exception
	 */
	private Date biometricTimestampParser(String timestamp) throws ParseException {
		try {
			// First try parsing with biometric timestamp format
			return DateUtils.parseToDate(timestamp, EnvUtil.getBiometricDateTimePattern());
		} catch (ParseException e) {
			mosipLogger.debug(
					"error parsing timestamp  with biomerics date time pattern: {}, so paring with request time pattern",
					e.getMessage());
			// Try parsing with request time stamp format
			return this.requestTimeParser(timestamp);
		}
	}
	
	/**
	 * Checks the list of Strings contains given string or not by ignoring the case
	 * 
	 * @param values
	 * @param value
	 * @return
	 */
	private boolean isValuesContainsIgnoreCase(List<String> values, String value) {
		if (value != null) {
			return values.stream().anyMatch(value::equalsIgnoreCase);
		}
		return false;
	}
}