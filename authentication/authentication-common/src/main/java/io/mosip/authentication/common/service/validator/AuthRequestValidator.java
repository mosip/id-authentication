package io.mosip.authentication.common.service.validator;

import static io.mosip.authentication.core.constant.IdAuthCommonConstants.BIO_PATH;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.REQUEST;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.hotlist.dto.HotlistDTO;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.AuthTypeDTO;
import io.mosip.authentication.core.indauth.dto.BioIdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.DataDTO;
import io.mosip.authentication.core.indauth.dto.DigitalId;
import io.mosip.authentication.core.indauth.dto.RequestDTO;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.hotlist.service.HotlistService;
import io.mosip.kernel.core.exception.ParseException;
import io.mosip.kernel.core.function.FunctionWithThrowable;
import io.mosip.kernel.core.hotlist.constant.HotlistIdTypes;
import io.mosip.kernel.core.hotlist.constant.HotlistStatus;
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
public class AuthRequestValidator extends BaseAuthRequestValidator {

	private static final String DATA_TIMESTAMP = "data/timestamp";

	/** The Constant DIGITAL_ID. */
	private static final String DIGITAL_ID = "data/digitalId/";

	/** The Constant FINGERPRINT_COUNT. */
	private static final int FINGERPRINT_COUNT = 10;

	/** The Constant REQUEST_REQUEST_TIME. */
	private static final String REQUEST_REQUEST_TIME = "request/timestamp";

	/** The Constant AUTH_REQUEST. */
	private static final String AUTH_REQUEST = "authRequest";

	/** The mosip logger. */
	private static Logger mosipLogger = IdaLogger.getLogger(AuthRequestValidator.class);

	/** The hotlist service. */
	@Autowired
	private HotlistService hotlistService;	
	
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
		allowedEnvironments = Arrays.stream(env.getProperty(IdAuthConfigKeyConstants.ALLOWED_ENVIRONMENTS).split((",")))
				.map(String::trim).collect(Collectors.toList());
		allowedDomainUris = Arrays.stream(env.getProperty(IdAuthConfigKeyConstants.ALLOWED_DOMAIN_URIS).split((",")))
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
		return AuthRequestDTO.class.equals(clazz);
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
				validateTxnId(authRequestDto.getTransactionID(), errors, IdAuthCommonConstants.TRANSACTION_ID);
			}
			if (!errors.hasErrors()) {
				validateAllowedAuthTypes(authRequestDto, errors);
			}
			if (!errors.hasErrors()) {
				validateAuthType(authRequestDto.getRequestedAuth(), errors);
			}
			if (!errors.hasErrors()) {
				super.validate(target, errors);

				if (!errors.hasErrors()) {
					checkAuthRequest(authRequestDto, errors);
				}
			}
			if (!errors.hasErrors()) {
				validateDomainURIandEnv(authRequestDto, errors);
			}
			if (!errors.hasErrors()) {
				validateHotlistedIds(errors, authRequestDto);
			}
			if (!errors.hasErrors() && authRequestDto.getRequestedAuth().isBio()) {
				validateBiometricTimestamps(authRequestDto.getRequest().getBiometrics(), errors);
			}
		} else {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), IdAuthCommonConstants.VALIDATE,
					IdAuthCommonConstants.INVALID_INPUT_PARAMETER + AUTH_REQUEST);
			errors.rejectValue(AUTH_REQUEST, IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode(),
					IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorMessage());
		}
	}

	/**
	 * Validate biometric timestamps.
	 *
	 * @param biometrics the biometrics
	 * @param errors     the errors
	 */
	protected void validateBiometricTimestamps(List<BioIdentityInfoDTO> biometrics, Errors errors) {
		if (biometrics != null) {
			for (int i = 0; i < biometrics.size(); i++) {
				BioIdentityInfoDTO bioIdentityInfoDTO = biometrics.get(i);
				if (bioIdentityInfoDTO.getData() == null) {
					errors.rejectValue(IdAuthCommonConstants.REQUEST,
							IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
							new Object[] { String.format(BIO_PATH, i, IdAuthCommonConstants.DATA) },
							IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
				} else {
					validateReqTime(bioIdentityInfoDTO.getData().getTimestamp(), errors,
							String.format(BIO_PATH, i, DATA_TIMESTAMP), this::biometricTimestampParser);

					if (!errors.hasErrors()) {
						validateDigitalIdTimestamp(bioIdentityInfoDTO.getData().getDigitalId(), errors,
								String.format(BIO_PATH, i, DIGITAL_ID));
					}
				}
			}
		} else {
			errors.rejectValue(IdAuthCommonConstants.REQUEST,
					IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(), new Object[] { "request/biometrics" },
					IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
		}
	}

	/**
	 * Validate digital id timestamp.
	 *
	 * @param digitalId the digital id
	 * @param errors    the errors
	 * @param field     the field
	 */
	protected void validateDigitalIdTimestamp(DigitalId digitalId, Errors errors, String field) {
		if (digitalId != null) {
			final String dateTimeField = field + "dateTime";
			if (digitalId.getDateTime() == null) {
				errors.rejectValue(IdAuthCommonConstants.REQUEST,
						IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(), new Object[] { dateTimeField },
						IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
			} else {
				validateReqTime(digitalId.getDateTime(), errors, dateTimeField, this::biometricTimestampParser);
			}
		} else {
			errors.rejectValue(IdAuthCommonConstants.REQUEST,
					IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(), new Object[] { field },
					IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
		}

	}

	/**
	 * Validate hotlisted ids.
	 *
	 * @param errors         the errors
	 * @param authRequestDto the auth request dto
	 */
	protected void validateHotlistedIds(Errors errors, AuthRequestDTO authRequestDto) {
		isIndividualIdHotlisted(authRequestDto.getIndividualId(), authRequestDto.getIndividualIdType(), errors);

		isPartnerIdHotlisted(authRequestDto.getMetadata("partnerId"), errors);

		if (Objects.nonNull(authRequestDto.getRequestedAuth()) && authRequestDto.getRequestedAuth().isBio()) {
			isDevicesHotlisted(authRequestDto.getRequest().getBiometrics(), errors);
			isDeviceProviderHotlisted(authRequestDto.getRequest().getBiometrics(), errors);
		}
	}

	/**
	 * Validate domain URI and env.
	 *
	 * @param authRequestDto the auth request dto
	 * @param errors         the errors
	 */
	private void validateDomainURIandEnv(AuthRequestDTO authRequestDto, Errors errors) {		
		if (Objects.nonNull(authRequestDto.getRequest()) && Objects.nonNull(authRequestDto.getRequest().getBiometrics())
				&& authRequestDto.getRequest().getBiometrics().stream().filter(bio -> Objects.nonNull(bio.getData()))
						.anyMatch(bio -> {
							if (bio.getData().getDomainUri() == null) {
								// It is error if domain URI in request is not null but in biometrics it is null
								return (authRequestDto.getDomainUri() != null
										|| allowedDomainUris.contains(authRequestDto.getDomainUri()));
							} else {
								// It is error if domain URI in biometrics is not null and the same in request
								// is not null or they both are not equal
								return authRequestDto.getDomainUri() == null
										|| !allowedDomainUris.contains(bio.getData().getDomainUri())
										|| !bio.getData().getDomainUri().contentEquals(authRequestDto.getDomainUri());
							}
						})) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
					IdAuthCommonConstants.VALIDATE, "request domainUri is no matching against bio domainUri");
			errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INPUT_MISMATCH.getErrorCode(), String
					.format(IdAuthenticationErrorConstants.INPUT_MISMATCH.getErrorMessage(), "domainUri", "domainUri"));
		}
		if (Objects.nonNull(authRequestDto.getRequest()) && Objects.nonNull(authRequestDto.getRequest().getBiometrics())
				&& authRequestDto.getRequest().getBiometrics().stream().filter(bio -> Objects.nonNull(bio.getData()))
						.anyMatch(bio -> {
							if (bio.getData().getEnv() == null) {
								// It is error if env in request is not null but in biometrics it is null
								return ((authRequestDto.getEnv() != null)
										|| allowedEnvironments.contains(authRequestDto.getEnv()));
							} else {
								// It is error if env in biometrics is not null and the same in request
								// is not null or they both are not equal
								return authRequestDto.getEnv() == null
										|| !allowedEnvironments.contains(bio.getData().getEnv())
										|| !bio.getData().getEnv().contentEquals(authRequestDto.getEnv());
							}
						})) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
					IdAuthCommonConstants.VALIDATE, "request env is no matching against bio env");
			errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INPUT_MISMATCH.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.INPUT_MISMATCH.getErrorMessage(), "env", "env"));
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
		AuthTypeDTO authType = authRequest.getRequestedAuth();
		if (authType.isDemo()) {
			checkDemoAuth(authRequest, errors);
		} else if (authType.isBio()) {
			Set<String> allowedAuthType = getAllowedAuthTypes();
			validateBioMetadataDetails(authRequest, errors, allowedAuthType);
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
	 * Checks if is individual id hotlisted.
	 *
	 * @param individualId     the individual id
	 * @param individualIdType the individual id type
	 * @param errors           the errors
	 */
	private void isIndividualIdHotlisted(String individualId, String individualIdType, Errors errors) {
		if (Objects.nonNull(individualId) && Objects.nonNull(individualIdType)) {
			HotlistDTO hotlistStatus = hotlistService.getHotlistStatus(
					IdAuthSecurityManager.generateHashAndDigestAsPlainText(individualId.getBytes()), individualIdType);
			if ((Objects.isNull(hotlistStatus.getExpiryDTimes()) && hotlistStatus.getStatus().contentEquals(HotlistStatus.BLOCKED))
					|| (Objects.nonNull(hotlistStatus.getExpiryDTimes())
							&& hotlistStatus.getStatus().contentEquals(HotlistStatus.BLOCKED)
							&& hotlistStatus.getExpiryDTimes().isAfter(DateUtils.getUTCCurrentDateTime()))) {
				errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.IDVID_DEACTIVATED_BLOCKED.getErrorCode(), String
						.format(IdAuthenticationErrorConstants.IDVID_DEACTIVATED_BLOCKED.getErrorMessage(), individualIdType));
			}
		}
	}

	/**
	 * Checks if is devices hotlisted.
	 *
	 * @param biometrics the biometrics
	 * @param errors     the errors
	 */
	protected void isDevicesHotlisted(List<BioIdentityInfoDTO> biometrics, Errors errors) {
		if (Objects.nonNull(biometrics) && !biometrics.isEmpty()) {
			IntStream.range(0, biometrics.size()).filter(index -> {
				HotlistDTO hotlistStatus = hotlistService.getHotlistStatus(
						IdAuthSecurityManager.generateHashAndDigestAsPlainText(biometrics.get(index).getData().getDigitalId()
								.getSerialNo().concat(biometrics.get(index).getData().getDigitalId().getMake())
								.concat(biometrics.get(index).getData().getDigitalId().getModel()).getBytes()),
						HotlistIdTypes.DEVICE);
				return hotlistStatus.getStatus().contentEquals(HotlistStatus.BLOCKED)
						|| (Objects.nonNull(hotlistStatus.getExpiryDTimes())
								&& hotlistStatus.getExpiryDTimes().isAfter(DateUtils.getUTCCurrentDateTime()));
			}).forEach(
					index -> errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.IDVID_DEACTIVATED_BLOCKED.getErrorCode(),
							String.format(IdAuthenticationErrorConstants.IDVID_DEACTIVATED_BLOCKED.getErrorMessage(),
									String.format(BIO_PATH, index, HotlistIdTypes.DEVICE))));
		}
	}

	/**
	 * Checks if is device provider hotlisted.
	 *
	 * @param biometrics the biometrics
	 * @param errors     the errors
	 */
	protected void isDeviceProviderHotlisted(List<BioIdentityInfoDTO> biometrics, Errors errors) {
		if (Objects.nonNull(biometrics) && !biometrics.isEmpty()) {
			IntStream.range(0, biometrics.size()).filter(index -> {
				HotlistDTO hotlistStatus = hotlistService.getHotlistStatus(
						IdAuthSecurityManager.generateHashAndDigestAsPlainText(biometrics.get(0).getData().getDigitalId().getDp()
								.concat(biometrics.get(0).getData().getDigitalId().getDpId()).getBytes()),
						HotlistIdTypes.DEVICE_PROVIDER);
				return hotlistStatus.getStatus().contentEquals(HotlistStatus.BLOCKED)
						|| (Objects.nonNull(hotlistStatus.getExpiryDTimes())
								&& hotlistStatus.getExpiryDTimes().isAfter(DateUtils.getUTCCurrentDateTime()));
			}).forEach(
					index -> errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.IDVID_DEACTIVATED_BLOCKED.getErrorCode(),
							String.format(IdAuthenticationErrorConstants.IDVID_DEACTIVATED_BLOCKED.getErrorMessage(),
									String.format(BIO_PATH, index, HotlistIdTypes.DEVICE_PROVIDER))));
		}
	}

	/**
	 * Checks if is partner id hotlisted.
	 *
	 * @param metadata the metadata
	 * @param errors   the errors
	 */
	protected void isPartnerIdHotlisted(Optional<Object> metadata, Errors errors) {
		if (Objects.nonNull(metadata) && metadata.isPresent()) {
			metadata.filter(partnerId -> hotlistService.getHotlistStatus((String) partnerId, HotlistIdTypes.PARTNER_ID)
					.getStatus().contentEquals(HotlistStatus.BLOCKED))
					.ifPresent(partnerId -> errors.rejectValue(REQUEST,
							IdAuthenticationErrorConstants.IDVID_DEACTIVATED_BLOCKED.getErrorCode(),
							String.format(IdAuthenticationErrorConstants.IDVID_DEACTIVATED_BLOCKED.getErrorMessage(),
									HotlistIdTypes.PARTNER_ID)));
		}
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
			return DateUtils.parseToDate(timestamp, env.getProperty(IdAuthConfigKeyConstants.BIO_DATE_TIME_PATTERN));
		} catch (ParseException e) {
			mosipLogger.debug(
					"error parsing timestamp  with biomerics date time pattern: {}, so paring with request time pattern",
					e.getMessage());
			// Try parsing with request time stamp format
			return this.requestTimeParser(timestamp);
		}
	}
}