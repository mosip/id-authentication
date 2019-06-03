package io.mosip.authentication.common.service.validator;

import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import io.mosip.authentication.common.service.helper.IdInfoHelper;
import io.mosip.authentication.common.service.impl.match.BioAuthType;
import io.mosip.authentication.common.service.impl.match.BioMatchType;
import io.mosip.authentication.common.service.impl.match.DOBType;
import io.mosip.authentication.common.service.impl.match.DemoAuthType;
import io.mosip.authentication.common.service.impl.match.DemoMatchType;
import io.mosip.authentication.common.service.impl.match.IdaIdMapping;
import io.mosip.authentication.common.service.impl.match.PinMatchType;
import io.mosip.authentication.common.service.integration.MasterDataManager;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.AuthTypeDTO;
import io.mosip.authentication.core.indauth.dto.BaseAuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.BioIdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.DataDTO;
import io.mosip.authentication.core.indauth.dto.IdentityDTO;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.KycAuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.RequestDTO;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.indauth.match.AuthType;
import io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher;
import io.mosip.authentication.core.spi.indauth.match.IdMapping;
import io.mosip.authentication.core.spi.indauth.match.MatchType;
import io.mosip.authentication.core.spi.indauth.match.MatchType.Category;
import io.mosip.kernel.core.exception.ParseException;
import io.mosip.kernel.core.idobjectvalidator.exception.IdObjectIOException;
import io.mosip.kernel.core.idobjectvalidator.exception.IdObjectValidationFailedException;
import io.mosip.kernel.core.idobjectvalidator.spi.IdObjectValidator;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.pinvalidator.exception.InvalidPinException;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.StringUtils;
import io.mosip.kernel.pinvalidator.impl.PinValidatorImpl;

/**
 * The Class BaseAuthRequestValidator.
 *
 * @author Manoj SP
 * @author Prem Kumar
 * @author RakeshRoshan
 * 
 */
@Component
public abstract class BaseAuthRequestValidator extends IdAuthValidator {

	private static final String IDENTITY = "identity";

	private static final String BIO_SUB_TYPE = "bioSubType";

	/** The Constant OTP2. */
	private static final String OTP2 = "OTP";

	/** The Constant PIN. */
	private static final String PIN = "PIN";

	/** The Constant BIO_TYPE. */
	private static final String BIO_TYPE = "bioType";

	/** The Final Constant For PIN_VALUE */
	private static final String PIN_VALUE = "pinValue";

	/** The mosip logger. */
	private static Logger mosipLogger = IdaLogger.getLogger(BaseAuthRequestValidator.class);

	/** The Constant iris. */
	private static final String IRIS = "iris";

	/** The Constant face. */
	private static final String FACE = "face";

	/** The id info helper. */
	@Autowired
	protected IdInfoHelper idInfoHelper;

	/** The id info helper. */
	@Autowired
	protected IdInfoFetcher idInfoFetcher;

	@Autowired
	private PinValidatorImpl pinValidator;

	/** The master Data Manager. */
	@Autowired
	private MasterDataManager masterDataManager;

	@Autowired
	@Qualifier("pattern")
	private IdObjectValidator idObjectValidator;

	/** The Constant REQUEST. */
	private static final String REQUEST = "request";

	/** The Constant SESSION_ID. */
	private static final String SESSION_ID = "SESSION_ID";

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	@Override
	public boolean supports(Class<?> clazz) {
		return BaseAuthRequestDTO.class.isAssignableFrom(clazz);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.validation.Validator#validate(java.lang.Object,
	 * org.springframework.validation.Errors)
	 */
	@Override
	public void validate(Object req, Errors errors) {
		BaseAuthRequestDTO baseAuthRequestDTO = (BaseAuthRequestDTO) req;

		if (baseAuthRequestDTO != null) {
			validateId(baseAuthRequestDTO.getId(), errors);
		}

	}

	/**
	 * validates the Static Pin Details
	 * 
	 * @param authRequestDTO
	 * @param errors
	 */
	protected void validateAdditionalFactorsDetails(AuthRequestDTO authRequestDTO, Errors errors) {
		AuthTypeDTO authTypeDTO = authRequestDTO.getRequestedAuth();

		if ((authTypeDTO != null && authTypeDTO.isPin() && isMatchtypeEnabled(PinMatchType.SPIN))) {

			Optional<String> pinOpt = Optional.ofNullable(authRequestDTO.getRequest()).map(RequestDTO::getStaticPin);

			if (!pinOpt.isPresent()) {
				mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
						IdAuthCommonConstants.VALIDATE, "Missing pinval in the request");
				errors.rejectValue(IdAuthCommonConstants.REQUEST,
						IdAuthenticationErrorConstants.MISSING_AUTHTYPE.getErrorCode(), new Object[] { PIN },
						IdAuthenticationErrorConstants.MISSING_AUTHTYPE.getErrorMessage());
			} else {
				try {
					pinValidator.validatePin(pinOpt.get());
				} catch (InvalidPinException e) {
					mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), "validateStaticPin",
							"INVALID_INPUT_PARAMETER - pinValue - value -> " + pinOpt.get());
					errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
							new Object[] { PIN_VALUE },
							IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
				}

			}
		} else if ((authTypeDTO != null && authTypeDTO.isOtp() && isMatchtypeEnabled(PinMatchType.OTP))) {
			Optional<String> otp = Optional.ofNullable(authRequestDTO.getRequest()).map(RequestDTO::getOtp);

			if (!otp.isPresent()) {
				mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
						IdAuthCommonConstants.VALIDATE, "Missing OTP value in the request");
				errors.rejectValue(IdAuthCommonConstants.REQUEST,
						IdAuthenticationErrorConstants.MISSING_AUTHTYPE.getErrorCode(),
						new Object[] { Category.OTP.getType() },
						IdAuthenticationErrorConstants.MISSING_AUTHTYPE.getErrorMessage());
			} else {
				try {
					pinValidator.validatePin(otp.get());
				} catch (InvalidPinException e) {
					mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), "validateOtpValue",
							"INVALID_INPUT_PARAMETER - OtppinValue - value -> " + otp.get());
					errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
							new Object[] { OTP2 },
							IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
				}
			}
		}
	}

	/**
	 * Validate Biometric details i.e validating fingers,iris,face and device
	 * information.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param errors         the errors
	 */
	protected void validateBioMetadataDetails(AuthRequestDTO authRequestDTO, Errors errors,
			Set<String> allowedAuthType) {

		AuthTypeDTO authTypeDTO = authRequestDTO.getRequestedAuth();

		if ((authTypeDTO != null && authTypeDTO.isBio())) {

			List<BioIdentityInfoDTO> bioInfo = authRequestDTO.getRequest().getBiometrics();

			if (bioInfo == null || bioInfo.isEmpty() || bioInfo.stream().anyMatch(bioDto -> bioDto.getData() == null)) {
				mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
						IdAuthCommonConstants.VALIDATE, "missing biometric request");
				errors.rejectValue(IdAuthCommonConstants.REQUEST,
						IdAuthenticationErrorConstants.MISSING_AUTHTYPE.getErrorCode(),
						new Object[] {Category.BIO.getType()},
						IdAuthenticationErrorConstants.MISSING_AUTHTYPE.getErrorMessage());
			} else {

				List<DataDTO> bioData = bioInfo.stream().map(BioIdentityInfoDTO::getData).collect(Collectors.toList());

				validateBioType(bioData, errors, allowedAuthType);

				validateBioData(bioData, errors);

				if (isAuthtypeEnabled(BioAuthType.FGR_MIN, BioAuthType.FGR_IMG, BioAuthType.FGR_MIN_MULTI)) {
					validateFinger(authRequestDTO, bioData, errors);
				}
				if (isAuthtypeEnabled(BioAuthType.IRIS_IMG, BioAuthType.IRIS_COMP_IMG)) {
					validateIris(authRequestDTO, bioData, errors);
				}
				if (isMatchtypeEnabled(BioMatchType.FACE)) {
					validateFace(authRequestDTO, bioData, errors);
				}

			}
		}

	}

	private void validateBioData(List<DataDTO> bioData, Errors errors) {
		List<DataDTO> filterdBioData = bioData.stream()
				.filter(dataDto -> dataDto.getBioValue() == null || dataDto.getBioValue().trim().isEmpty())
				.collect(Collectors.toList());
		filterdBioData.forEach(bioInfo -> {
			errors.rejectValue(IdAuthCommonConstants.REQUEST,
					IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
					new Object[] { "for bioType - " + bioInfo.getBioType() + " " + "& bioSubType - "
							+ bioInfo.getBioSubType() },
					IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
		});

	}

	/**
	 * Validates the BioType value
	 * 
	 * @param bioInfo
	 * @param errors
	 */
	private void validateBioType(List<DataDTO> bioInfos, Errors errors, Set<String> allowedAuthTypesFromConfig) {
		AuthType[] authTypes = BioAuthType.values();
		Set<String> availableAuthTypeInfos = new HashSet<>();
		for (AuthType authType : authTypes) {
			availableAuthTypeInfos.add(authType.getType());
		}
		Set<String> allowedAvailableAuthTypes = allowedAuthTypesFromConfig.stream().filter(authTypeFromConfig -> {
			boolean contains = availableAuthTypeInfos.contains(authTypeFromConfig);
			if (!contains) {
				mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
						IdAuthCommonConstants.VALIDATE, "Invalid bio type config: " + authTypeFromConfig);
			}
			return contains;
		}).collect(Collectors.toSet());
		for (DataDTO bioInfo : bioInfos) {
			String bioType = bioInfo.getBioType();
			if (StringUtils.isEmpty(bioType)) {
				errors.rejectValue(IdAuthCommonConstants.REQUEST,
						IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
						new Object[] { BIO_TYPE },
						IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
			} else if (!allowedAvailableAuthTypes.contains(bioType)) {
				errors.rejectValue(IdAuthCommonConstants.REQUEST,
						IdAuthenticationErrorConstants.AUTH_TYPE_NOT_SUPPORTED.getErrorCode(), new Object[] {MatchType.Category.BIO.getType() +"-"+bioType },
						IdAuthenticationErrorConstants.AUTH_TYPE_NOT_SUPPORTED.getErrorMessage());
			} else {
				validateBioType(errors, allowedAuthTypesFromConfig, bioInfo);
			}
		}

	}

	/**
	 * Validate bio type.
	 *
	 * @param errors                 the errors
	 * @param availableAuthTypeInfos the available auth type infos
	 * @param bioInfo                the bio info
	 */
	private void validateBioType(Errors errors, Set<String> availableAuthTypeInfos, DataDTO bioInfo) {
		String bioType = bioInfo.getBioType();
		if (!availableAuthTypeInfos.contains(bioType)) {
			errors.rejectValue(IdAuthCommonConstants.REQUEST,
					IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(), new Object[] { BIO_TYPE },
					IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
		} else {
			String bioSubType = bioInfo.getBioSubType();
			if (bioSubType != null && !bioSubType.isEmpty()) {
				// Valid bio type
				Optional<BioAuthType> bioAuthTypeOpt = BioAuthType.getSingleBioAuthTypeForType(bioType);
				if (bioAuthTypeOpt.isPresent()) {
					BioAuthType bioAuthType = bioAuthTypeOpt.get();
					Set<MatchType> associatedMatchTypes = bioAuthType.getAssociatedMatchTypes();
					boolean invalidBioType = associatedMatchTypes.stream()
							.filter(matchType -> matchType instanceof BioMatchType)
							.map(matchType -> (BioMatchType) matchType).map(BioMatchType::getIdMapping)
							.map(IdMapping::getIdname).distinct()
							.noneMatch(idName -> idName.equalsIgnoreCase(bioSubType));
					if (invalidBioType) {
						errors.rejectValue(IdAuthCommonConstants.REQUEST,
								IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
								new Object[] { BIO_SUB_TYPE + " - " + bioSubType + " for bioType " + bioType },
								IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
					}

				}

			} else {
				errors.rejectValue(IdAuthCommonConstants.REQUEST,
						IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
						new Object[] { BIO_SUB_TYPE },
						IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
			}
		}
	}

	/**
	 * Validate fingers.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param bioInfo        the bio info
	 * @param errors         the errors
	 */
	private void validateFinger(AuthRequestDTO authRequestDTO, List<DataDTO> bioInfo, Errors errors) {
		if (isAvailableBioType(bioInfo, BioAuthType.FGR_MIN)) {
			validateFingerRequestCount(authRequestDTO, errors, BioAuthType.FGR_MIN.getType());
		}
		if (isAvailableBioType(bioInfo, BioAuthType.FGR_IMG)) {
			validateFingerRequestCount(authRequestDTO, errors, BioAuthType.FGR_IMG.getType());
		}
	}

	/**
	 * Validates the Iris parameters present in thr request.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param bioInfo        the bio info
	 * @param errors         the errors
	 */
	private void validateIris(AuthRequestDTO authRequestDTO, List<DataDTO> bioInfo, Errors errors) {
		if (isAvailableBioType(bioInfo, BioAuthType.IRIS_IMG)) {
			validateIrisRequestCount(authRequestDTO, errors);
			validateMultiIrisValue(authRequestDTO, errors);
		}
	}

	/**
	 * Validation for MultiIris Values present in the request
	 * 
	 * @param authRequestDTO
	 * @param errors
	 */
	private void validateMultiIrisValue(AuthRequestDTO authRequestDTO, Errors errors) {
		if (isDuplicateBioValue(authRequestDTO, BioAuthType.IRIS_IMG.getType())) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
					IdAuthCommonConstants.VALIDATE, "Duplicate IRIS in request");
			errors.rejectValue(IdAuthCommonConstants.REQUEST,
					IdAuthenticationErrorConstants.DUPLICATE_IRIS.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.DUPLICATE_IRIS.getErrorMessage(),
							IdAuthCommonConstants.REQUEST));
		}
	}

	private boolean isDuplicateBioValue(AuthRequestDTO authRequestDTO, String type) {
		Map<String, Long> countsMap = getBioValueCounts(authRequestDTO, type);
		return hasDuplicate(countsMap);
	}

	private boolean hasDuplicate(Map<String, Long> countsMap) {
		return countsMap.entrySet().stream().anyMatch(
				entry -> (entry.getKey().equalsIgnoreCase(IdAuthCommonConstants.UNKNOWN_BIO) && entry.getValue() > 2)
						|| (!entry.getKey().equalsIgnoreCase(IdAuthCommonConstants.UNKNOWN_BIO)
								&& entry.getValue() > 1));
	}

	private Map<String, Long> getBioSubtypeCounts(AuthRequestDTO authRequestDTO, String type) {
		return getBioSubtypeCount(getBioIds(authRequestDTO, type));
	}

	private Map<String, Long> getBioValueCounts(AuthRequestDTO authRequestDTO, String type) {
		return getBioValuesCount(getBioIds(authRequestDTO, type));
	}

	private List<BioIdentityInfoDTO> getBioIds(AuthRequestDTO authRequestDTO, String type) {
		List<BioIdentityInfoDTO> identity = Optional.ofNullable(authRequestDTO.getRequest())
				.map(RequestDTO::getBiometrics).orElseGet(Collections::emptyList);
		if (!identity.isEmpty()) {
			return identity.stream().filter(Objects::nonNull)
					.filter(bioId -> bioId.getData().getBioType().equalsIgnoreCase(type)).collect(Collectors.toList());
		}
		return Collections.emptyList();
	}

	/**
	 * Validate Face.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param bioInfo        the bio info
	 * @param errors         the errors
	 */
	private void validateFace(AuthRequestDTO authRequestDTO, List<DataDTO> bioInfo, Errors errors) {

		if (isAvailableBioType(bioInfo, BioAuthType.FACE_IMG)) {
			validateFaceBioType(authRequestDTO, errors);
		}
	}

	private void validateFaceBioType(AuthRequestDTO authRequestDTO, Errors errors) {
		List<BioIdentityInfoDTO> listBioIdentity = getBioIds(authRequestDTO, BioAuthType.FACE_IMG.getType());
		if (listBioIdentity.size() > 1) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
					IdAuthCommonConstants.VALIDATE, "Face : face count is more than 1.");
			errors.rejectValue(IdAuthCommonConstants.REQUEST,
					IdAuthenticationErrorConstants.FACE_EXCEEDING.getErrorCode(), new Object[] { FACE },
					IdAuthenticationErrorConstants.FACE_EXCEEDING.getErrorMessage());
		}
	}

	/**
	 * check any IdentityInfoDto data available or not.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param functions      the functions
	 * @return true, if successful
	 */
	@SuppressWarnings("unchecked")
	boolean checkAnyIdInfoAvailable(AuthRequestDTO authRequestDTO,
			Function<IdentityDTO, List<IdentityInfoDTO>>... functions) {
		return Stream.<Function<IdentityDTO, List<IdentityInfoDTO>>>of(functions).anyMatch(func -> Optional
				.ofNullable(authRequestDTO.getRequest()).map(RequestDTO::getDemographics).map(func)
				.filter(list -> list != null && !list.isEmpty()
						&& list.stream().allMatch(idDto -> idDto.getValue() != null && !idDto.getValue().isEmpty()))
				.isPresent());
	}

	/**
	 * If DemoAuthType is Bio, then validate bioinfo is available or not.
	 *
	 * @param bioInfoList the bio info list
	 * @param bioType     the bio type
	 * @return true, if is available bio type
	 */
	private boolean isAvailableBioType(List<DataDTO> bioInfoList, BioAuthType bioType) {
		return bioInfoList.parallelStream().anyMatch(bio -> bio.getBioType() != null && !bio.getBioType().isEmpty()
				&& bio.getBioType().equals(bioType.getType()));
	}

	/**
	 * If DemoAuthType is Bio, Then check duplicate request of finger and number
	 * finger of request should not exceed to 10.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param errors         the errors
	 */
	private void validateFingerRequestCount(AuthRequestDTO authRequestDTO, Errors errors, String bioType) {
		Map<String, Long> fingerSubtypesCountsMap = getBioSubtypeCounts(authRequestDTO, bioType);
		boolean anyInfoIsMoreThanOne = hasDuplicate(fingerSubtypesCountsMap);
		Map<String, Long> fingerValuesCountsMap = getBioValueCounts(authRequestDTO, bioType);
		boolean anyValueIsMoreThanOne = hasDuplicate(fingerValuesCountsMap);

		if (anyInfoIsMoreThanOne || anyValueIsMoreThanOne) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
					IdAuthCommonConstants.VALIDATE, "Duplicate fingers");
			errors.reject(IdAuthenticationErrorConstants.DUPLICATE_FINGER.getErrorCode(),
					IdAuthenticationErrorConstants.DUPLICATE_FINGER.getErrorMessage());
		}

		validateMaxFingerCount(errors, fingerSubtypesCountsMap);
	}

	private void validateMaxFingerCount(Errors errors, Map<String, Long> fingerSubtypesCountsMap) {
		Long fingerCountExceeding = fingerSubtypesCountsMap.values().stream().mapToLong(l -> l).sum();
		int maxFingerCount = getMaxFingerCount();
		if (fingerCountExceeding > maxFingerCount) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
					IdAuthCommonConstants.VALIDATE, "finger count is exceeding to " + maxFingerCount);
			errors.reject(IdAuthenticationErrorConstants.FINGER_EXCEEDING.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.FINGER_EXCEEDING.getErrorMessage()));
		}
	}

	protected abstract int getMaxFingerCount();

	private Map<String, Long> getBioSubtypeCount(List<BioIdentityInfoDTO> idendityInfoList) {
		return idendityInfoList.stream().map(BioIdentityInfoDTO::getData)
				.collect(Collectors.groupingBy(DataDTO::getBioSubType, Collectors.counting()));

	}

	private Map<String, Long> getBioValuesCount(List<BioIdentityInfoDTO> idendityInfoList) {
		return idendityInfoList.stream().map(BioIdentityInfoDTO::getData)
				.collect(Collectors.groupingBy(DataDTO::getBioValue, Collectors.counting()));

	}

	/**
	 * validate Iris request count. left and right eye should not exceed 1 and total
	 * iris should not exceed 2.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param errors         the errors
	 */
	private void validateIrisRequestCount(AuthRequestDTO authRequestDTO, Errors errors) {
		Map<String, Long> irisSubtypeCounts = getBioSubtypeCounts(authRequestDTO, BioAuthType.IRIS_IMG.getType());
		if (irisSubtypeCounts.entrySet().stream().anyMatch(
				map -> (map.getKey().equalsIgnoreCase(IdAuthCommonConstants.UNKNOWN_BIO) && map.getValue() > 2)
						|| (!map.getKey().equalsIgnoreCase(IdAuthCommonConstants.UNKNOWN_BIO) && map.getValue() > 1))) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
					IdAuthCommonConstants.VALIDATE, "Iris : either left eye or right eye count is more than 1.");
			errors.rejectValue(IdAuthCommonConstants.REQUEST,
					IdAuthenticationErrorConstants.IRIS_EXCEEDING.getErrorCode(), new Object[] { IRIS },
					IdAuthenticationErrorConstants.IRIS_EXCEEDING.getErrorMessage());
		}

	}

	/**
	 * Check demo auth.
	 *
	 * @param authRequest the auth request
	 * @param errors      the errors
	 */
	protected void checkDemoAuth(AuthRequestDTO authRequest, Errors errors) {
		AuthType[] authTypes = DemoAuthType.values();
		Set<String> availableAuthTypeInfos = new HashSet<>();
		boolean hasMatch = false;
		for (AuthType authType : authTypes) {
			if (authType.isAuthTypeEnabled(authRequest, idInfoFetcher)) {
				Set<MatchType> associatedMatchTypes = authType.getAssociatedMatchTypes();
				for (MatchType matchType : associatedMatchTypes) {
					if (isMatchtypeEnabled(matchType)) {
						List<IdentityInfoDTO> identityInfos = matchType.getIdentityInfoList(authRequest.getRequest());
						if (identityInfos != null && !identityInfos.isEmpty()) {
							availableAuthTypeInfos.add(authType.getType());
							hasMatch = true;
							checkIdentityInfoValue(identityInfos, errors);
							checkLangaugeDetails(matchType, identityInfos, errors);
						}
					}

				}
			}
		}

		if (!hasMatch) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
					IdAuthCommonConstants.VALIDATE, "Missing IdentityInfoDTO");
			errors.rejectValue(IdAuthCommonConstants.REQUEST,
					IdAuthenticationErrorConstants.MISSING_AUTHTYPE.getErrorCode(),
					new Object[] { Category.DEMO.getType() },
					IdAuthenticationErrorConstants.MISSING_AUTHTYPE.getErrorMessage());
		} else {
			checkOtherValues(authRequest, errors, availableAuthTypeInfos);
		}
	}

	/**
	 * Check identity info value.
	 *
	 * @param identityInfos the identity infos
	 * @param errors        the errors
	 */
	private void checkIdentityInfoValue(List<IdentityInfoDTO> identityInfos, Errors errors) {
		for (IdentityInfoDTO identityInfoDTO : identityInfos) {
			if (Objects.isNull(identityInfoDTO.getValue())) {
				mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
						IdAuthCommonConstants.VALIDATE, "IdentityInfoDTO is invalid");
				errors.rejectValue(IdAuthCommonConstants.REQUEST,
						IdAuthenticationErrorConstants.MISSING_AUTHTYPE.getErrorCode(),
						new Object[] { Category.DEMO.getType() },
						IdAuthenticationErrorConstants.MISSING_AUTHTYPE.getErrorMessage());
			}

		}

	}

	/**
	 * Check other values.
	 *
	 * @param authRequest            the auth request
	 * @param errors                 the errors
	 * @param availableAuthTypeInfos
	 * @param hasMatch               the has match
	 */
	private void checkOtherValues(AuthRequestDTO authRequest, Errors errors, Set<String> availableAuthTypeInfos) {

		if (isMatchtypeEnabled(DemoMatchType.DOB)) {
			checkDOB(authRequest, errors);
		}

		if (isMatchtypeEnabled(DemoMatchType.DOBTYPE)) {
			checkDOBType(authRequest, errors);
		}

		if (isMatchtypeEnabled(DemoMatchType.AGE)) {
			checkAge(authRequest, errors);
		}

		if (isMatchtypeEnabled(DemoMatchType.GENDER)) {
			checkGender(authRequest, errors);
		}

		if (isAuthtypeEnabled(DemoAuthType.ADDRESS, DemoAuthType.FULL_ADDRESS)) {
			validateAdAndFullAd(availableAuthTypeInfos, errors);
		}

		validatePattern(authRequest, errors);

	}

	private void validatePattern(AuthRequestDTO authRequest, Errors errors) {

		try {
			Map<String, Map<String, String>> identityMap = new HashMap<>();
			Map<String, String> valueMap = new HashMap<>();
			IdentityDTO demographics = authRequest.getRequest().getDemographics();
			String phoneNumber = demographics.getPhoneNumber();
			String emailId = demographics.getEmailId();
			String pinCode = demographics.getPostalCode();
			if (phoneNumber != null && !phoneNumber.isEmpty()) {
				List<String> phonekey = getIdMappingValue(IdaIdMapping.PHONE, DemoMatchType.PHONE);
				valueMap.put(phonekey.get(0), phoneNumber);
			}

			if (emailId != null && !emailId.isEmpty()) {
				List<String> emailkey = getIdMappingValue(IdaIdMapping.EMAIL, DemoMatchType.EMAIL);
				valueMap.put(emailkey.get(0), emailId);
			}

			if (pinCode != null && !pinCode.isEmpty()) {
				List<String> pincodekey = getIdMappingValue(IdaIdMapping.PINCODE, DemoMatchType.PINCODE);
				valueMap.put(pincodekey.get(0), pinCode);
			}
			identityMap.put(IDENTITY, valueMap);
			idObjectValidator.validateIdObject(identityMap, null);
		} catch (IdObjectValidationFailedException | IdObjectIOException | IdAuthenticationBusinessException e) {
			if (e instanceof IdObjectValidationFailedException) {
				for (String error : e.getErrorTexts()) {
					String[] split = error.split("identity/");
					errors.rejectValue(IdAuthCommonConstants.REQUEST,
							IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
							new Object[] { split[1] },
							IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
				}
			}
		}

	}

	private List<String> getIdMappingValue(IdMapping idMapping, MatchType matchType)
			throws IdAuthenticationBusinessException {
		return idInfoHelper.getIdMappingValue(idMapping, matchType);
	}

	private boolean isMatchtypeEnabled(MatchType matchType) {
		return idInfoHelper.isMatchtypeEnabled(matchType);
	}

	private boolean isAuthtypeEnabled(AuthType... authTypes) {
		return Stream.of(authTypes).anyMatch(
				authType -> authType.getAssociatedMatchTypes().stream().anyMatch(idInfoHelper::isMatchtypeEnabled));
	}

	/**
	 * Validate ad and full ad.
	 *
	 * @param availableAuthTypeInfos the available auth type infos
	 * @param errors                 the errors
	 */
	private void validateAdAndFullAd(Set<String> availableAuthTypeInfos, Errors errors) {
		if (availableAuthTypeInfos.contains(DemoAuthType.ADDRESS.getType())
				&& availableAuthTypeInfos.contains(DemoAuthType.FULL_ADDRESS.getType())) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
					IdAuthCommonConstants.VALIDATE, "Ad and FAD are enabled");
			errors.rejectValue(IdAuthCommonConstants.REQUEST,
					IdAuthenticationErrorConstants.MISSING_AUTHTYPE.getErrorCode(),
					new Object[] { Category.DEMO.getType() },
					IdAuthenticationErrorConstants.MISSING_AUTHTYPE.getErrorMessage());
		}
	}

	/**
	 * Check gender.
	 *
	 * @param authRequest the auth request
	 * @param errors      the errors
	 */
	private void checkGender(AuthRequestDTO authRequest, Errors errors) {
		List<IdentityInfoDTO> genderList = DemoMatchType.GENDER.getIdentityInfoList(authRequest.getRequest());
		if (genderList != null && !genderList.isEmpty()) {
			Map<String, List<String>> fetchGenderType = null;
			try {
				fetchGenderType = masterDataManager.fetchGenderType();
			} catch (IdAuthenticationBusinessException e) {
				mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
						IdAuthCommonConstants.VALIDATE, "Master Data util failed to load - Gender Type");
				errors.rejectValue(IdAuthCommonConstants.REQUEST,
						IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
						new Object[] { "gender" },
						IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
			}
			if (null != fetchGenderType) {
				checkGender(errors, genderList, fetchGenderType);
			}
		}
	}

	private void checkGender(Errors errors, List<IdentityInfoDTO> genderList,
			Map<String, List<String>> fetchGenderType) {
		for (IdentityInfoDTO identityInfoDTO : genderList) {
			String language = identityInfoDTO.getLanguage() != null ? identityInfoDTO.getLanguage()
					: env.getProperty(IdAuthConfigKeyConstants.MOSIP_PRIMARY_LANGUAGE);
			List<String> genderTypeList = fetchGenderType.get(language);
			if (null != genderTypeList && !genderTypeList.contains(identityInfoDTO.getValue())) {
				mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
						IdAuthCommonConstants.VALIDATE, "Demographic data – Gender(pi) did not match");
				errors.rejectValue(IdAuthCommonConstants.REQUEST,
						IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
						new Object[] { "gender" },
						IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
			}

		}
	}

	/**
	 * Check DOB type.
	 *
	 * @param authRequest the auth request
	 * @param errors      the errors
	 */
	private void checkDOBType(AuthRequestDTO authRequest, Errors errors) {
		List<IdentityInfoDTO> dobTypeList = DemoMatchType.DOBTYPE.getIdentityInfoList(authRequest.getRequest());
		if (dobTypeList != null) {
			for (IdentityInfoDTO identityInfoDTO : dobTypeList) {
				if (!DOBType.isTypePresent(identityInfoDTO.getValue())) {
					mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
							IdAuthCommonConstants.VALIDATE, "Demographic data – DOBType(pi) did not match");
					errors.rejectValue(IdAuthCommonConstants.REQUEST,
							IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
							new Object[] { "DOBType" },
							IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
				}
			}
		}

	}

	/**
	 * Check age.
	 *
	 * @param authRequest the auth request
	 * @param errors      the errors
	 */
	private void checkAge(AuthRequestDTO authRequest, Errors errors) {
		List<IdentityInfoDTO> ageList = DemoMatchType.AGE.getIdentityInfoList(authRequest.getRequest());
		if (ageList != null) {
			for (IdentityInfoDTO identityInfoDTO : ageList) {
				try {
					Integer.parseInt(identityInfoDTO.getValue());
				} catch (NumberFormatException e) {
					mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
							IdAuthCommonConstants.VALIDATE, "Demographic data – Age(pi) did not match");
					errors.rejectValue(IdAuthCommonConstants.REQUEST,
							IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
							new Object[] { "age" },
							IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
				}
			}
		}
	}

	/**
	 * Check DOB.
	 *
	 * @param authRequest the auth request
	 * @param errors      the errors
	 */
	private void checkDOB(AuthRequestDTO authRequest, Errors errors) {
		List<IdentityInfoDTO> dobList = DemoMatchType.DOB.getIdentityInfoList(authRequest.getRequest());
		if (dobList != null) {
			for (IdentityInfoDTO identityInfoDTO : dobList) {
				try {
					DateUtils.parseToDate(identityInfoDTO.getValue(),
							env.getProperty(IdAuthConfigKeyConstants.DOB_REQ_DATE_PATTERN));
				} catch (ParseException e) {
					// FIXME change to DOB - Invalid -DOB - Please enter DOB in specified date
					// format or Age in the acceptable range

					mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
							IdAuthCommonConstants.VALIDATE, "Demographic data – DOB(pi) did not match");
					errors.rejectValue(IdAuthCommonConstants.REQUEST,
							IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
							new Object[] { "dob" },
							IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
				}
			}
		}
	}

	/**
	 * Check langauge details.
	 *
	 * @param demoMatchType the demo match type
	 * @param identityInfos the identity infos
	 * @param errors        the errors
	 */
	private void checkLangaugeDetails(MatchType demoMatchType, List<IdentityInfoDTO> identityInfos, Errors errors) {
		String priLangCode = env.getProperty(IdAuthConfigKeyConstants.MOSIP_PRIMARY_LANGUAGE);

		Map<String, Long> langCount = identityInfos.stream().map((IdentityInfoDTO idInfo) -> {
			String language = idInfo.getLanguage();
			if (language == null) {
				language = priLangCode;
			}
			return new SimpleEntry<>(language, idInfo);
		}).collect(Collectors.groupingBy(Entry::getKey, Collectors.counting()));

		langCount.keySet().forEach(langCode -> validateLangCode(langCode, errors, REQUEST));

		for (long value : langCount.values()) {
			if (value > 1) {
				mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
						IdAuthCommonConstants.INVALID_INPUT_PARAMETER, "Invalid or Multiple language code");
				errors.rejectValue(IdAuthCommonConstants.REQUEST,
						IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
						new Object[] { "LanguageCode" },
						IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
			}
		}

		if (langCount.keySet().size() > 1 && !demoMatchType.isMultiLanguage()) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
					IdAuthCommonConstants.INVALID_INPUT_PARAMETER, "Invalid or Multiple language code");
			errors.rejectValue(IdAuthCommonConstants.REQUEST,
					IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					new Object[] { "LanguageCode" },
					IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
		}
	}

	/**
	 * Validates the AuthType
	 * 
	 * @param authType
	 * @param errors
	 */
	protected void validateAuthType(AuthTypeDTO authType, Errors errors) {
		if (!(authType.isDemo() || authType.isBio() || authType.isOtp() || authType.isPin())) {
			errors.rejectValue(IdAuthCommonConstants.REQUESTEDAUTH,
					IdAuthenticationErrorConstants.NO_AUTHENTICATION_TYPE_SELECTED_IN_REQUEST.getErrorCode(),
					IdAuthenticationErrorConstants.NO_AUTHENTICATION_TYPE_SELECTED_IN_REQUEST.getErrorMessage());
		}
	}

	/**
	 * Method to validate auth type
	 * 
	 * @param requestDTO
	 * @param errors
	 */
	protected void validateAllowedAuthTypes(AuthRequestDTO requestDTO, Errors errors, String configKey) {
		AuthTypeDTO authTypeDTO = requestDTO.getRequestedAuth();
		if (authTypeDTO != null) {
			Set<String> allowedAuthType = getAllowedAuthTypes(configKey);
			validateAuthType(requestDTO, errors, authTypeDTO, allowedAuthType);
		} else {
			errors.rejectValue(IdAuthCommonConstants.REQUESTEDAUTH,
					IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage(),
							IdAuthCommonConstants.REQUESTEDAUTH));
		}

	}

	/**
	 * Validate auth type.
	 *
	 * @param requestDTO      the request DTO
	 * @param errors          the errors
	 * @param authTypeDTO     the auth type DTO
	 * @param allowedAuthType the allowed auth type
	 */
	private void validateAuthType(AuthRequestDTO requestDTO, Errors errors, AuthTypeDTO authTypeDTO,
			Set<String> allowedAuthType) {
		checkAllowedAuthType(requestDTO, errors, authTypeDTO, allowedAuthType);

		if (authTypeDTO.isBio()) {
			if (allowedAuthType.contains(MatchType.Category.BIO.getType())) {
				  validateBioMetadataDetails(requestDTO, errors, allowedAuthType);
			} else {
				errors.rejectValue(IdAuthCommonConstants.REQUESTEDAUTH,
						IdAuthenticationErrorConstants.AUTH_TYPE_NOT_SUPPORTED.getErrorCode(),
						new Object[] { MatchType.Category.BIO.getType() },
						IdAuthenticationErrorConstants.AUTH_TYPE_NOT_SUPPORTED.getErrorMessage());
			}

		}
	}

	/**
	 * Check allowed auth type.
	 *
	 * @param requestDTO      the request DTO
	 * @param errors          the errors
	 * @param authTypeDTO     the auth type DTO
	 * @param allowedAuthType the allowed auth type
	 */
	private void checkAllowedAuthType(AuthRequestDTO requestDTO, Errors errors, AuthTypeDTO authTypeDTO,
			Set<String> allowedAuthType) {
		if (authTypeDTO.isDemo()) {
			if (allowedAuthType.contains(MatchType.Category.DEMO.getType())) {
				checkDemoAuth(requestDTO, errors);
			} else {
				errors.rejectValue(IdAuthCommonConstants.REQUESTEDAUTH,
						IdAuthenticationErrorConstants.AUTH_TYPE_NOT_SUPPORTED.getErrorCode(),
						new Object[] { MatchType.Category.DEMO.getType() },
						IdAuthenticationErrorConstants.AUTH_TYPE_NOT_SUPPORTED.getErrorMessage());
			}
		}

		if (authTypeDTO.isOtp() && !allowedAuthType.contains(MatchType.Category.OTP.getType())) {
			errors.rejectValue(IdAuthCommonConstants.REQUEST,
					IdAuthenticationErrorConstants.AUTH_TYPE_NOT_SUPPORTED.getErrorCode(),
					new Object[] { MatchType.Category.OTP.getType() },
					IdAuthenticationErrorConstants.AUTH_TYPE_NOT_SUPPORTED.getErrorMessage());
		}

		if (authTypeDTO.isPin() && !allowedAuthType.contains(MatchType.Category.SPIN.getType())) {
			errors.rejectValue(IdAuthCommonConstants.REQUEST,
					IdAuthenticationErrorConstants.AUTH_TYPE_NOT_SUPPORTED.getErrorCode(),
					new Object[] { MatchType.Category.SPIN.getType() },
					IdAuthenticationErrorConstants.AUTH_TYPE_NOT_SUPPORTED.getErrorMessage());
		}

		if ((authTypeDTO.isPin() || authTypeDTO.isOtp()) && !errors.hasErrors()) {
			validateAdditionalFactorsDetails(requestDTO, errors);
		}
	}

	/**
	 * Extract auth info.
	 *
	 * @return the sets the
	 */
	protected Set<String> getAllowedAuthTypes(String configKey) {
		String intAllowedAuthType = env.getProperty(configKey);
		return Stream.of(intAllowedAuthType.split(",")).filter(str -> !str.isEmpty()).collect(Collectors.toSet());
	}

	/**
	 * validateSecondayLangCode method used to validate secondaryLangCode for KYC
	 * request
	 *
	 * @param string the {@link KycAuthRequestDTO}
	 * @param errors the errors
	 * @param field  the field
	 */
	protected void validateLangCode(String langCode, Errors errors, String field) {
		if (Objects.nonNull(langCode)) {
			Set<String> allowedLang;
			String languages = env.getProperty(IdAuthConfigKeyConstants.MOSIP_SUPPORTED_LANGUAGES);
			if (null != languages && languages.contains(",")) {
				allowedLang = Arrays.stream(languages.split(",")).collect(Collectors.toSet());
			} else {
				allowedLang = new HashSet<>();
				allowedLang.add(languages);
			}

			if (!allowedLang.contains(langCode)) {
				mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), IdAuthCommonConstants.VALIDATE,
						IdAuthCommonConstants.INVALID_INPUT_PARAMETER + field + " : " + langCode);
				errors.rejectValue(field, IdAuthenticationErrorConstants.UNSUPPORTED_LANGUAGE.getErrorCode(),
						new Object[] { field.concat(" : " + langCode) },
						IdAuthenticationErrorConstants.UNSUPPORTED_LANGUAGE.getErrorMessage());
			}
		}

	}

}