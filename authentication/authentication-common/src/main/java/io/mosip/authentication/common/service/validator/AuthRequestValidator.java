package io.mosip.authentication.common.service.validator;

import static io.mosip.authentication.core.constant.IdAuthCommonConstants.BIO_PATH;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.REQUEST;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.AuthTypeDTO;
import io.mosip.authentication.core.indauth.dto.BioIdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.DataDTO;
import io.mosip.authentication.core.indauth.dto.RequestDTO;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.hotlist.service.HotlistService;
import io.mosip.kernel.core.hotlist.constant.HotlistIdTypes;
import io.mosip.kernel.core.hotlist.constant.HotlistStatus;
import io.mosip.kernel.core.logger.spi.Logger;
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
				isIndividualIdHotlisted(authRequestDto.getIndividualId(), authRequestDto.getIndividualIdType(), errors);

				isPartnerIdHotlisted(authRequestDto.getMetadata("partnerId"), errors);

				if (Objects.nonNull(authRequestDto.getRequestedAuth()) && authRequestDto.getRequestedAuth().isBio()) {
					isDevicesHotlisted(authRequestDto.getRequest().getBiometrics(), errors);
					isDeviceProviderHotlisted(authRequestDto.getRequest().getBiometrics(), errors);
				}
			}
		} else {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
					IdAuthCommonConstants.VALIDATE, IdAuthCommonConstants.INVALID_INPUT_PARAMETER + AUTH_REQUEST);
			errors.rejectValue(AUTH_REQUEST, IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode(),
					IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorMessage());
		}
	}

	/**
	 * Validate domain UR iand env.
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
								return authRequestDto.getDomainUri() != null;
							} else {
								// It is error if domain URI in biometrics is not null and the same in request
								// is not null or they both are not equal
								return authRequestDto.getDomainUri() == null
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
								return authRequestDto.getEnv() != null;
							} else {
								// It is error if env in biometrics is not null and the same in request
								// is not null or they both are not equal
								return authRequestDto.getEnv() == null
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
				super.validateReqTime(bioData.get(index).getDigitalId().getDateTime(), errors,
						String.format(BIO_PATH, index, DIGITAL_ID + "dateTime"));
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
		if (Objects.nonNull(individualId) && Objects.nonNull(individualIdType)
				&& hotlistService.getHotlistStatus(
						IdAuthSecurityManager.generateHashAndDigestAsPlainText(individualId.getBytes()),
						individualIdType).getStatus().contentEquals(HotlistStatus.BLOCKED)) {
			errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.IDVID_DEACTIVATED_BLOCKED.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.IDVID_DEACTIVATED_BLOCKED.getErrorMessage(),
							individualIdType));
		}
	}

	/**
	 * Checks if is devices hotlisted.
	 *
	 * @param biometrics the biometrics
	 * @param errors     the errors
	 */
	private void isDevicesHotlisted(List<BioIdentityInfoDTO> biometrics, Errors errors) {
		if (Objects.nonNull(biometrics) && !biometrics.isEmpty()) {
			IntStream
					.range(0,
							biometrics.size())
					.filter(index -> hotlistService.getHotlistStatus(
							IdAuthSecurityManager.generateHashAndDigestAsPlainText(biometrics.get(index).getData()
									.getDigitalId().getSerialNo()
									.concat(biometrics.get(index).getData().getDigitalId().getMake())
									.concat(biometrics.get(index).getData().getDigitalId().getModel()).getBytes()),
							HotlistIdTypes.DEVICE).getStatus().contentEquals(HotlistStatus.BLOCKED))
					.forEach(index -> errors.rejectValue(REQUEST,
							IdAuthenticationErrorConstants.IDVID_DEACTIVATED_BLOCKED.getErrorCode(),
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
	private void isDeviceProviderHotlisted(List<BioIdentityInfoDTO> biometrics, Errors errors) {
		if (Objects.nonNull(biometrics) && !biometrics.isEmpty()) {
			IntStream
					.range(0, biometrics.size()).filter(
							index -> hotlistService
									.getHotlistStatus(
											IdAuthSecurityManager.generateHashAndDigestAsPlainText(
													biometrics.get(0).getData().getDigitalId().getDp()
															.concat(biometrics.get(0).getData().getDigitalId()
																	.getDpId())
															.getBytes()),
											HotlistIdTypes.DEVICE_PROVIDER)
									.getStatus().contentEquals(HotlistStatus.BLOCKED))
					.forEach(index -> errors.rejectValue(REQUEST,
							IdAuthenticationErrorConstants.IDVID_DEACTIVATED_BLOCKED.getErrorCode(),
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
	private void isPartnerIdHotlisted(Optional<Object> metadata, Errors errors) {
		if (Objects.nonNull(metadata) && metadata.isPresent()) {
			metadata.filter(partnerId -> hotlistService.getHotlistStatus((String) partnerId, HotlistIdTypes.PARTNER_ID)
					.getStatus().contentEquals(HotlistStatus.BLOCKED))
					.ifPresent(partnerId -> errors.rejectValue(REQUEST,
							IdAuthenticationErrorConstants.IDVID_DEACTIVATED_BLOCKED.getErrorCode(),
							String.format(IdAuthenticationErrorConstants.IDVID_DEACTIVATED_BLOCKED.getErrorMessage(),
									HotlistIdTypes.PARTNER_ID)));
		}
	}

}
