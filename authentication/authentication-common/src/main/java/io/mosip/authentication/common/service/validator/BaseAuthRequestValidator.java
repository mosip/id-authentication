package io.mosip.authentication.common.service.validator;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.Errors;

import io.mosip.authentication.common.service.helper.IdInfoHelper;
import io.mosip.authentication.common.service.impl.match.BioAuthType;
import io.mosip.authentication.common.service.impl.match.BioMatchType;
import io.mosip.authentication.common.service.impl.match.DOBType;
import io.mosip.authentication.common.service.impl.match.DemoAuthType;
import io.mosip.authentication.common.service.impl.match.DemoMatchType;
import io.mosip.authentication.common.service.impl.match.KeyBindedTokenAuthType;
import io.mosip.authentication.common.service.impl.match.KeyBindedTokenMatchType;
import io.mosip.authentication.common.service.impl.match.PasswordMatchType;
import io.mosip.authentication.common.service.impl.match.PinMatchType;
import io.mosip.authentication.common.service.util.AuthTypeUtil;
import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.BaseAuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.BioIdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.DataDTO;
import io.mosip.authentication.core.indauth.dto.IdentityDTO;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.KeyBindedTokenDTO;
import io.mosip.authentication.core.indauth.dto.KycAuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.KycRequestDTO;
import io.mosip.authentication.core.indauth.dto.RequestDTO;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.indauth.match.AuthType;
import io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher;
import io.mosip.authentication.core.spi.indauth.match.IdMapping;
import io.mosip.authentication.core.spi.indauth.match.MatchType;
import io.mosip.authentication.core.spi.indauth.match.MatchType.Category;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.pinvalidator.exception.InvalidPinException;
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

	/** The Constant OTP2. */
	private static final String OTP2 = "OTP";

	/** The Constant PIN. */
	private static final String PIN = "PIN";

	/** The Final Constant For PIN_VALUE. */
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

	/** The pin validator. */
	@Autowired
	private PinValidatorImpl pinValidator;

	/** The Constant REQUEST. */
	private static final String REQUEST = "request";

	/** The Constant SESSION_ID. */
	private static final String SESSION_ID = "SESSION_ID";

	/** The Constant IRIS_COUNT. */
	private static final int IRIS_COUNT = 2;

	/**
	 * Supports.
	 *
	 * @param clazz the clazz
	 * @return true, if successful
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	@Override
	public boolean supports(Class<?> clazz) {
		return BaseAuthRequestDTO.class.isAssignableFrom(clazz);
	}

	/**
	 * Validate.
	 *
	 * @param req the req
	 * @param errors the errors
	 */
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
	 * validates the Static Pin Details.
	 *
	 * @param authRequestDTO
	 *            the auth request DTO
	 * @param errors
	 *            the errors
	 */
	protected void validateAdditionalFactorsDetails(AuthRequestDTO authRequestDTO, Errors errors) {

		if ((AuthTypeUtil.isPin(authRequestDTO) && isMatchtypeEnabled(PinMatchType.SPIN))) {

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
		} else if ((AuthTypeUtil.isOtp(authRequestDTO) && isMatchtypeEnabled(PinMatchType.OTP))) {
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

	private void validatePasswordDetails(AuthRequestDTO authRequestDTO, Errors errors) {
		
		if (isMatchtypeEnabled(PasswordMatchType.PASSWORD)) {
			KycAuthRequestDTO kycAuthRequestDTO = (KycAuthRequestDTO) authRequestDTO;
			Optional<String> passwordOpt = Optional.ofNullable(kycAuthRequestDTO.getRequest()).map(KycRequestDTO::getPassword);
			if (!passwordOpt.isPresent()) {
				mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
						IdAuthCommonConstants.VALIDATE, "Missing Password value in the request");
				errors.rejectValue(IdAuthCommonConstants.REQUEST,
						IdAuthenticationErrorConstants.MISSING_AUTHTYPE.getErrorCode(),
						new Object[] { Category.PWD.getType() },
						IdAuthenticationErrorConstants.MISSING_AUTHTYPE.getErrorMessage());
			} else {
				if (passwordOpt.get().isBlank()) {
					mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), "validatePasswordDetails",
							"INVALID_INPUT_PARAMETER - Pwd value -> " + passwordOpt.get());
					errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
							new Object[] { "PWD" },
							IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
				}
			}
		}
	}

	private void validateKBTDetails(AuthRequestDTO authRequestDTO, Errors errors) {
		if(authRequestDTO instanceof KycAuthRequestDTO) {
            KycAuthRequestDTO kycAuthRequestDTO = (KycAuthRequestDTO)authRequestDTO;
			boolean isKbt = CollectionUtils.isEmpty(kycAuthRequestDTO.getRequest().getKeyBindedTokens());
			if (!isKbt) {
				KeyBindedTokenDTO kbtDto = kycAuthRequestDTO.getRequest().getKeyBindedTokens().get(0);
				if (Objects.isNull(kbtDto.getFormat()) || kbtDto.getFormat().isBlank()) {
					mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), "validateKBTDetails",
							"INVALID_INPUT_PARAMETER - KBT value -> " + kbtDto.getFormat());
					errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
							new Object[] { "KeyBindedTokens.Format" },
							IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
				}
				if (Objects.isNull(kbtDto.getToken()) || kbtDto.getToken().isBlank()) {
					mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), "validateKBTDetails",
							"INVALID_INPUT_PARAMETER - KBT value -> " + kbtDto.getToken());
					errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
							new Object[] { "KeyBindedTokens.Token" },
							IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
				}
				if (Objects.isNull(kbtDto.getType()) || kbtDto.getType().isBlank()) {
					mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), "validateKBTDetails",
							"INVALID_INPUT_PARAMETER - KBT value -> " + kbtDto.getType());
					errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
							new Object[] { "KeyBindedTokens.Type" },
							IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
				}
			}
		}
	}

	/**
	 * Validate Biometric details i.e validating fingers,iris,face and device
	 * information.
	 *
	 * @param authRequestDTO
	 *            the auth request DTO
	 * @param errors
	 *            the errors
	 * @param allowedAuthType
	 *            the allowed auth type
	 */
	protected void validateBioMetadataDetails(AuthRequestDTO authRequestDTO, Errors errors,
			Set<String> allowedAuthType) {
		if (authRequestDTO.getRequest() != null) {
			List<BioIdentityInfoDTO> bioInfo = authRequestDTO.getRequest().getBiometrics();

			if (bioInfo != null && !bioInfo.isEmpty()) {
				OptionalInt nullDataIndex = IntStream.range(0, bioInfo.size())
						.filter(index -> bioInfo.get(index).getData() == null).findAny();
				if (nullDataIndex.isPresent()) {
					mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
							IdAuthCommonConstants.VALIDATE, "missing biometric request");
					errors.rejectValue(IdAuthCommonConstants.REQUEST,
							IdAuthenticationErrorConstants.MISSING_AUTHTYPE.getErrorCode(),
							new Object[] { Category.BIO.getType() + "/*/data" },
							IdAuthenticationErrorConstants.MISSING_AUTHTYPE.getErrorMessage());
				} else {
					List<DataDTO> bioData = bioInfo.stream().map(BioIdentityInfoDTO::getData)
							.collect(Collectors.toList());
					validateBioType(bioData, errors, allowedAuthType);
					validateBioData(bioData, errors);
					validateCount(authRequestDTO, errors, bioData);
				}
			}
		}
	}

	/**
	 * Validate count.
	 *
	 * @param authRequestDTO
	 *            the auth request DTO
	 * @param errors
	 *            the errors
	 * @param bioData
	 *            the bio data
	 */
	private void validateCount(AuthRequestDTO authRequestDTO, Errors errors, List<DataDTO> bioData) {
		if (!errors.hasErrors()) {
			BioAuthType[] fingerTypes;
			if(EnvUtil.getIsFmrEnabled()) {
				fingerTypes = new BioAuthType[] {BioAuthType.FGR_IMG, BioAuthType.FGR_MIN, BioAuthType.FGR_IMG_COMPOSITE, BioAuthType.FGR_MIN_COMPOSITE};
			} else {
				fingerTypes = new BioAuthType[] {BioAuthType.FGR_IMG, BioAuthType.FGR_IMG_COMPOSITE};
			}
			if (isAuthtypeEnabled(fingerTypes)) {
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

	/**
	 * Validate bio data.
	 *
	 * @param bioData
	 *            the bio data
	 * @param errors
	 *            the errors
	 */
	private void validateBioData(List<DataDTO> bioData, Errors errors) {
		List<DataDTO> filterdBioData = bioData.stream()
				.filter(dataDto -> dataDto.getBioValue() == null || dataDto.getBioValue().isEmpty())
				.collect(Collectors.toList());
		filterdBioData.forEach(bioInfo -> {
			errors.rejectValue(IdAuthCommonConstants.REQUEST,
					IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
					new Object[] { "bioValue for bioType - " + bioInfo.getBioType() + " " + "& bioSubType - "
							+ bioInfo.getBioSubType() },
					IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
		});

	}

	/**
	 * Validates the BioType value.
	 *
	 * @param bioInfos
	 *            the bio infos
	 * @param errors
	 *            the errors
	 * @param allowedAuthTypesFromConfig
	 *            the allowed auth types from config
	 */
	private void validateBioType(List<DataDTO> bioInfos, Errors errors, Set<String> allowedAuthTypesFromConfig) {
		BioAuthType[] authTypes = BioAuthType.values();
		Set<String> availableAuthTypeInfos = new HashSet<>();
		for (BioAuthType authType : authTypes) {
			availableAuthTypeInfos.add(authType.getConfigNameValue().toLowerCase());
		}
		Set<String> allowedAvailableAuthTypes = allowedAuthTypesFromConfig.stream().filter(authTypeFromConfig -> {
			String authType = authTypeFromConfig.toLowerCase();
			boolean contains = (authType.equalsIgnoreCase(MatchType.Category.DEMO.getType())
					|| authType.equalsIgnoreCase(MatchType.Category.OTP.getType())) ? true
							: availableAuthTypeInfos.contains(authType);
			// TODO handle invalid bio authtype cases
			if (!contains) {
				mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
						IdAuthCommonConstants.VALIDATE, "Invalid bio type config: " + authTypeFromConfig);
			}
			return contains;
		}).map(BioAuthType::getTypeForConfigNameValue).filter(Optional::isPresent).map(Optional::get)
				.collect(Collectors.toSet());

		for (int i = 0; i < bioInfos.size(); i++) {
			DataDTO bioInfo = bioInfos.get(i);
			String bioType = bioInfo.getBioType();
			if (StringUtils.isEmpty(bioType)) {
				errors.rejectValue(IdAuthCommonConstants.REQUEST,
						IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
						new Object[] { String.format(IdAuthCommonConstants.BIO_TYPE_INPUT_PARAM, i) },
						IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
			} else if (allowedAvailableAuthTypes.stream().noneMatch(authType -> authType.equalsIgnoreCase(bioType))) {
				errors.rejectValue(IdAuthCommonConstants.REQUEST,
						IdAuthenticationErrorConstants.AUTH_TYPE_NOT_SUPPORTED.getErrorCode(),
						new Object[] { MatchType.Category.BIO.getType() + "-" + bioType },
						IdAuthenticationErrorConstants.AUTH_TYPE_NOT_SUPPORTED.getErrorMessage());
			} else {
				validateBioType(errors, allowedAvailableAuthTypes, bioInfo, i);
			}
		}

	}

	/**
	 * Validate bio type.
	 *
	 * @param errors            the errors
	 * @param availableAuthTypeInfos            the available auth type infos
	 * @param bioInfo            the bio info
	 * @param bioIndex the bio index
	 */
	private void validateBioType(Errors errors, Set<String> availableAuthTypeInfos, DataDTO bioInfo, int bioIndex) {
		String bioType = bioInfo.getBioType();
		if (availableAuthTypeInfos.stream().noneMatch(authType -> authType.equalsIgnoreCase(bioType))) {
			errors.rejectValue(IdAuthCommonConstants.REQUEST,
					IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(), 
					new Object[] { String.format(IdAuthCommonConstants.BIO_TYPE_INPUT_PARAM, bioIndex) + " - " + bioType },
					IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
		} else {
			String bioSubType = bioInfo.getBioSubType();
			if(!BioAuthType.FACE_IMG.getType().equalsIgnoreCase(bioType)) {
				if (bioSubType != null && !bioSubType.isEmpty()) {
					// Valid bio type
					Optional<BioAuthType> bioAuthTypeOpt = BioAuthType.getSingleBioAuthTypeForType(bioType);
					if (bioAuthTypeOpt.isPresent()) {
						BioAuthType bioAuthType = bioAuthTypeOpt.get();
						Set<MatchType> associatedMatchTypes = bioAuthType.getAssociatedMatchTypes();
						boolean invalidBioType = associatedMatchTypes.stream()
								.filter(matchType -> matchType instanceof BioMatchType)
								.map(matchType -> (BioMatchType) matchType).map(BioMatchType::getIdMapping)
								.map(IdMapping::getSubType).distinct()
								.noneMatch(idName -> idName.equalsIgnoreCase(bioSubType));
						if (invalidBioType) {
							errors.rejectValue(IdAuthCommonConstants.REQUEST,
									IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
									new Object[] { String.format(IdAuthCommonConstants.BIO_SUB_TYPE_INPUT_PARAM, bioIndex) + " - " + bioSubType },
									IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
						}
	
					}
				} else {
					errors.rejectValue(IdAuthCommonConstants.REQUEST,
							IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
							new Object[] {  String.format(IdAuthCommonConstants.BIO_SUB_TYPE_INPUT_PARAM, bioIndex) },
							IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
				}
			}
		}
	}

	/**
	 * Validate fingers.
	 *
	 * @param authRequestDTO
	 *            the auth request DTO
	 * @param bioInfo
	 *            the bio info
	 * @param errors
	 *            the errors
	 */
	private void validateFinger(AuthRequestDTO authRequestDTO, List<DataDTO> bioInfo, Errors errors) {
		if (EnvUtil.getIsFmrEnabled() && isAvailableBioType(bioInfo, BioAuthType.FGR_MIN)) {
			validateFingerRequestCount(authRequestDTO, errors, BioAuthType.FGR_MIN.getType());
		}
		if (isAvailableBioType(bioInfo, BioAuthType.FGR_IMG)) {
			validateFingerRequestCount(authRequestDTO, errors, BioAuthType.FGR_IMG.getType());
		}
	}

	/**
	 * Validates the Iris parameters present in thr request.
	 *
	 * @param authRequestDTO
	 *            the auth request DTO
	 * @param bioInfo
	 *            the bio info
	 * @param errors
	 *            the errors
	 */
	private void validateIris(AuthRequestDTO authRequestDTO, List<DataDTO> bioInfo, Errors errors) {
		if (isAvailableBioType(bioInfo, BioAuthType.IRIS_IMG)) {
			validateIrisRequestCount(authRequestDTO, errors);
			validateMultiIrisValue(authRequestDTO, errors);
		}
	}

	/**
	 * Validation for MultiIris Values present in the request.
	 *
	 * @param authRequestDTO
	 *            the auth request DTO
	 * @param errors
	 *            the errors
	 */
	private void validateMultiIrisValue(AuthRequestDTO authRequestDTO, Errors errors) {
		if (isDuplicateBioValue(authRequestDTO, BioAuthType.IRIS_IMG.getType(), getMaxIrisCount())) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
					IdAuthCommonConstants.VALIDATE, "Duplicate IRIS in request");
			errors.rejectValue(IdAuthCommonConstants.REQUEST,
					IdAuthenticationErrorConstants.DUPLICATE_IRIS.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.DUPLICATE_IRIS.getErrorMessage(),
							IdAuthCommonConstants.REQUEST));
		}
	}

	/**
	 * Checks if is duplicate bio value.
	 *
	 * @param authRequestDTO
	 *            the auth request DTO
	 * @param type
	 *            the type
	 * @param maxCount
	 *            the max count
	 * @return true, if is duplicate bio value
	 */
	private boolean isDuplicateBioValue(AuthRequestDTO authRequestDTO, String type, int maxCount) {
		Map<String, Long> countsMap = getBioValueCounts(authRequestDTO, type);
		return hasDuplicate(countsMap, maxCount);
	}

	/**
	 * Checks for duplicate.
	 *
	 * @param countsMap
	 *            the counts map
	 * @param maxCount
	 *            the max count
	 * @return true, if successful
	 */
	private boolean hasDuplicate(Map<String, Long> countsMap, int maxCount) {
		return countsMap.entrySet().stream()
				.anyMatch(entry -> (entry.getKey().equalsIgnoreCase(IdAuthCommonConstants.UNKNOWN_BIO)
						&& entry.getValue() > maxCount)
						|| (!entry.getKey().equalsIgnoreCase(IdAuthCommonConstants.UNKNOWN_BIO)
								&& entry.getValue() > 1));
	}

	/**
	 * Gets the bio subtype counts.
	 *
	 * @param authRequestDTO
	 *            the auth request DTO
	 * @param type
	 *            the type
	 * @return the bio subtype counts
	 */
	private Map<String, Long> getBioSubtypeCounts(AuthRequestDTO authRequestDTO, String type) {
		return getBioSubtypeCount(getBioIds(authRequestDTO, type));
	}

	/**
	 * Gets the bio value counts.
	 *
	 * @param authRequestDTO
	 *            the auth request DTO
	 * @param type
	 *            the type
	 * @return the bio value counts
	 */
	private Map<String, Long> getBioValueCounts(AuthRequestDTO authRequestDTO, String type) {
		return getBioValuesCount(getBioIds(authRequestDTO, type));
	}

	/**
	 * Gets the bio ids.
	 *
	 * @param authRequestDTO
	 *            the auth request DTO
	 * @param type
	 *            the type
	 * @return the bio ids
	 */
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
	 * @param authRequestDTO
	 *            the auth request DTO
	 * @param bioInfo
	 *            the bio info
	 * @param errors
	 *            the errors
	 */
	private void validateFace(AuthRequestDTO authRequestDTO, List<DataDTO> bioInfo, Errors errors) {

		if (isAvailableBioType(bioInfo, BioAuthType.FACE_IMG)) {
			validateFaceBioType(authRequestDTO, errors);
		}
	}

	/**
	 * Validate face bio type.
	 *
	 * @param authRequestDTO
	 *            the auth request DTO
	 * @param errors
	 *            the errors
	 */
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
	 * @param authRequestDTO
	 *            the auth request DTO
	 * @param functions
	 *            the functions
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
	 * @param bioInfoList
	 *            the bio info list
	 * @param bioType
	 *            the bio type
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
	 * @param authRequestDTO
	 *            the auth request DTO
	 * @param errors
	 *            the errors
	 * @param bioType
	 *            the bio type
	 */
	private void validateFingerRequestCount(AuthRequestDTO authRequestDTO, Errors errors, String bioType) {
		Map<String, Long> fingerSubtypesCountsMap = getBioSubtypeCounts(authRequestDTO, bioType);
		boolean anyInfoIsMoreThanOne = hasDuplicate(fingerSubtypesCountsMap, getMaxFingerCount());
		Map<String, Long> fingerValuesCountsMap = getBioValueCounts(authRequestDTO, bioType);
		boolean anyValueIsMoreThanOne = hasDuplicate(fingerValuesCountsMap, getMaxFingerCount());

		if (anyInfoIsMoreThanOne || anyValueIsMoreThanOne) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
					IdAuthCommonConstants.VALIDATE, "Duplicate fingers");
			errors.reject(IdAuthenticationErrorConstants.DUPLICATE_FINGER.getErrorCode(),
					IdAuthenticationErrorConstants.DUPLICATE_FINGER.getErrorMessage());
		}

		validateMaxFingerCount(errors, fingerSubtypesCountsMap);
	}

	/**
	 * Validate max finger count.
	 *
	 * @param errors
	 *            the errors
	 * @param fingerSubtypesCountsMap
	 *            the finger subtypes counts map
	 */
	private void validateMaxFingerCount(Errors errors, Map<String, Long> fingerSubtypesCountsMap) {
		long fingerCountExceeding = fingerSubtypesCountsMap.values().stream().mapToLong(l -> l).sum();
		int maxFingerCount = getMaxFingerCount();
		if (fingerCountExceeding > maxFingerCount) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
					IdAuthCommonConstants.VALIDATE, "finger count is exceeding to " + maxFingerCount);
			errors.reject(IdAuthenticationErrorConstants.FINGER_EXCEEDING.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.FINGER_EXCEEDING.getErrorMessage(), maxFingerCount));
		}
	}

	/**
	 * Gets the max finger count.
	 *
	 * @return the max finger count
	 */
	protected abstract int getMaxFingerCount();

	/**
	 * Gets the bio subtype count.
	 *
	 * @param idendityInfoList
	 *            the idendity info list
	 * @return the bio subtype count
	 */
	private Map<String, Long> getBioSubtypeCount(List<BioIdentityInfoDTO> idendityInfoList) {
		return idendityInfoList.stream().map(BioIdentityInfoDTO::getData)
				.collect(Collectors.groupingBy(DataDTO::getBioSubType, Collectors.counting()));

	}

	/**
	 * Gets the bio values count.
	 *
	 * @param idendityInfoList
	 *            the idendity info list
	 * @return the bio values count
	 */
	private Map<String, Long> getBioValuesCount(List<BioIdentityInfoDTO> idendityInfoList) {
		return idendityInfoList.stream().map(BioIdentityInfoDTO::getData)
				.collect(Collectors.groupingBy(DataDTO::getBioValue, Collectors.counting()));

	}

	/**
	 * validate Iris request count. left and right eye should not exceed 1 and total
	 * iris should not exceed 2.
	 *
	 * @param authRequestDTO
	 *            the auth request DTO
	 * @param errors
	 *            the errors
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
	 * @param authRequest
	 *            the auth request
	 * @param errors
	 *            the errors
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
						if(!matchType.equals(DemoMatchType.DYNAMIC)) {
							List<IdentityInfoDTO> identityInfos = matchType.getIdentityInfoList(authRequest.getRequest());
							hasMatch = checkIdentityInfoAndLanguageDetails(errors, availableAuthTypeInfos, hasMatch, authType, matchType,
									identityInfos);
						} else {
							Set<String> dynamicAttributeNames = new HashSet<>(idInfoFetcher.getMappingConfig().getDynamicAttributes().keySet());
							Optional.ofNullable(authRequest.getRequest())
									.map(RequestDTO::getDemographics)
									.map(IdentityDTO::getMetadata)
									.map(Map::keySet)
									.ifPresent(dynamicAttributeNames::addAll);
							for(String idName : dynamicAttributeNames) {
								Map<String, List<IdentityInfoDTO>> identityInfosMap = idInfoFetcher.getIdentityInfo(matchType, idName, authRequest.getRequest());
								for(List<IdentityInfoDTO> identityInfos : identityInfosMap.values()) {
									hasMatch = checkIdentityInfoAndLanguageDetails(errors, availableAuthTypeInfos, hasMatch, authType, matchType,
											identityInfos);
								}
							}
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
	 * Check identity info and language details.
	 *
	 * @param errors the errors
	 * @param availableAuthTypeInfos the available auth type infos
	 * @param hasMatch the has match
	 * @param authType the auth type
	 * @param matchType the match type
	 * @param identityInfos the identity infos
	 * @return true, if successful
	 */
	private boolean checkIdentityInfoAndLanguageDetails(Errors errors, Set<String> availableAuthTypeInfos, boolean hasMatch, AuthType authType,
			MatchType matchType, List<IdentityInfoDTO> identityInfos) {
		if (identityInfos != null && !identityInfos.isEmpty()) {
			availableAuthTypeInfos.add(authType.getType());
			hasMatch = true;
			checkIdentityInfoValue(identityInfos, errors);
			checkLangaugeDetails(matchType, identityInfos, errors);
		}
		return hasMatch;
	}

	/**
	 * Check identity info value.
	 *
	 * @param identityInfos
	 *            the identity infos
	 * @param errors
	 *            the errors
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
	 * @param authRequest
	 *            the auth request
	 * @param errors
	 *            the errors
	 * @param availableAuthTypeInfos
	 *            the available auth type infos
	 */
	private void checkOtherValues(AuthRequestDTO authRequest, Errors errors, Set<String> availableAuthTypeInfos) {

		if (isMatchtypeEnabled(DemoMatchType.DOBTYPE)) {
			checkDOBType(authRequest, errors);
		}

		if (isMatchtypeEnabled(DemoMatchType.AGE)) {
			checkAge(authRequest, errors);
		}

		if (isAuthtypeEnabled(DemoAuthType.ADDRESS, DemoAuthType.FULL_ADDRESS)) {
			validateAdAndFullAd(availableAuthTypeInfos, errors);
		}

	}

	/**
	 * Checks if is matchtype enabled.
	 *
	 * @param matchType
	 *            the match type
	 * @return true, if is matchtype enabled
	 */
	private boolean isMatchtypeEnabled(MatchType matchType) {
		return idInfoHelper.isMatchtypeEnabled(matchType);
	}

	/**
	 * Checks if is authtype enabled.
	 *
	 * @param authTypes
	 *            the auth types
	 * @return true, if is authtype enabled
	 */
	private boolean isAuthtypeEnabled(AuthType... authTypes) {
		return Stream.of(authTypes).anyMatch(
				authType -> authType.getAssociatedMatchTypes().stream().anyMatch(idInfoHelper::isMatchtypeEnabled));
	}

	/**
	 * Validate ad and full ad.
	 *
	 * @param availableAuthTypeInfos
	 *            the available auth type infos
	 * @param errors
	 *            the errors
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
	 * Check DOB type.
	 *
	 * @param authRequest
	 *            the auth request
	 * @param errors
	 *            the errors
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
	 * @param authRequest
	 *            the auth request
	 * @param errors
	 *            the errors
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
	 * Check langauge details.
	 *
	 * @param demoMatchType
	 *            the demo match type
	 * @param identityInfos
	 *            the identity infos
	 * @param errors
	 *            the errors
	 */
	private void checkLangaugeDetails(MatchType demoMatchType, List<IdentityInfoDTO> identityInfos, Errors errors) {
		//Dynamic attributes validations are skipping here
		//will be done in match input building stage(MatchInputBuilder)
		if (!demoMatchType.isDynamic() && demoMatchType.isMultiLanguage() && identityInfos.stream().anyMatch(
				identityInfo -> (identityInfo.getLanguage() == null || identityInfo.getLanguage().isEmpty()))) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(),
					IdAuthCommonConstants.MISSING_INPUT_PARAMETER, "LanguageCode cannot be null");
			errors.rejectValue(IdAuthCommonConstants.REQUEST,
					IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
					new Object[] { "LanguageCode" },
					IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());

		}

		if (!demoMatchType.isDynamic() && !errors.hasErrors() && demoMatchType.isMultiLanguage()) {
			Map<String, Long> langCount = identityInfos.stream()
					.collect(Collectors.groupingBy(IdentityInfoDTO::getLanguage, Collectors.counting()));
			
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
	}

	/**
	 * Validates the AuthType.
	 *
	 * @param authType
	 *            the auth type
	 * @param errors
	 *            the errors
	 */
	protected void validateAuthType(AuthRequestDTO authRequestDto, Errors errors) {
		if (!(AuthTypeUtil.isDemo(authRequestDto) 
				|| AuthTypeUtil.isBio(authRequestDto) 
				|| AuthTypeUtil.isOtp(authRequestDto) 
				|| AuthTypeUtil.isPin(authRequestDto)
				|| AuthTypeUtil.isPassword(authRequestDto)
				|| AuthTypeUtil.isKeyBindedToken(authRequestDto))) {
			errors.rejectValue(IdAuthCommonConstants.REQUEST,
					IdAuthenticationErrorConstants.NO_AUTHENTICATION_TYPE_SELECTED_IN_REQUEST.getErrorCode(),
					IdAuthenticationErrorConstants.NO_AUTHENTICATION_TYPE_SELECTED_IN_REQUEST.getErrorMessage());
		}
	}

	/**
	 * Method to validate auth type.
	 *
	 * @param requestDTO
	 *            the request DTO
	 * @param errors
	 *            the errors
	 */
	protected void validateAllowedAuthTypes(AuthRequestDTO requestDTO, Errors errors) {
		Set<String> allowedAuthType = getAllowedAuthTypes();
		validateAuthType(requestDTO, errors, allowedAuthType);
	}

	/**
	 * Validate auth type.
	 *
	 * @param requestDTO
	 *            the request DTO
	 * @param errors
	 *            the errors
	 * @param authTypeDTO
	 *            the auth type DTO
	 * @param allowedAuthType
	 *            the allowed auth type
	 */
	private void validateAuthType(AuthRequestDTO requestDTO, Errors errors,
			Set<String> allowedAuthType) {
		checkAllowedAuthType(requestDTO, errors, allowedAuthType);
		validateBioMetadataDetails(requestDTO, errors, allowedAuthType);
	}

	/**
	 * Check allowed auth type.
	 *
	 * @param requestDTO
	 *            the request DTO
	 * @param errors
	 *            the errors
	 * @param authTypeDTO
	 *            the auth type DTO
	 * @param allowedAuthType
	 *            the allowed auth type
	 */
	private void checkAllowedAuthType(AuthRequestDTO requestDTO, Errors errors,
			Set<String> allowedAuthType) {
		if (AuthTypeUtil.isDemo(requestDTO)) {
			if (allowedAuthType.contains(MatchType.Category.DEMO.getType())) {
				checkDemoAuth(requestDTO, errors);
			} else {
				errors.rejectValue(IdAuthCommonConstants.REQUEST,
						IdAuthenticationErrorConstants.AUTH_TYPE_NOT_SUPPORTED.getErrorCode(),
						new Object[] { MatchType.Category.DEMO.getType() },
						IdAuthenticationErrorConstants.AUTH_TYPE_NOT_SUPPORTED.getErrorMessage());
			}
		}

		boolean isOtp = AuthTypeUtil.isOtp(requestDTO);
		if (isOtp && !allowedAuthType.contains(MatchType.Category.OTP.getType())) {
			errors.rejectValue(IdAuthCommonConstants.REQUEST,
					IdAuthenticationErrorConstants.AUTH_TYPE_NOT_SUPPORTED.getErrorCode(),
					new Object[] { MatchType.Category.OTP.getType() },
					IdAuthenticationErrorConstants.AUTH_TYPE_NOT_SUPPORTED.getErrorMessage());
		}

		boolean isPin = AuthTypeUtil.isPin(requestDTO);
		if (isPin && !allowedAuthType.contains(MatchType.Category.SPIN.getType())) {
			errors.rejectValue(IdAuthCommonConstants.REQUEST,
					IdAuthenticationErrorConstants.AUTH_TYPE_NOT_SUPPORTED.getErrorCode(),
					new Object[] { MatchType.Category.SPIN.getType() },
					IdAuthenticationErrorConstants.AUTH_TYPE_NOT_SUPPORTED.getErrorMessage());
		}

		boolean isKeyBindedToken = AuthTypeUtil.isKeyBindedToken(requestDTO);
		if (isKeyBindedToken && !allowedAuthType.contains(MatchType.Category.KBT.getType())) {
			errors.rejectValue(IdAuthCommonConstants.REQUEST,
					IdAuthenticationErrorConstants.AUTH_TYPE_NOT_SUPPORTED.getErrorCode(),
					new Object[] { MatchType.Category.KBT.getType() },
					IdAuthenticationErrorConstants.AUTH_TYPE_NOT_SUPPORTED.getErrorMessage());
		}

		boolean isPassword = AuthTypeUtil.isPassword(requestDTO);
		if (isPassword && !allowedAuthType.contains(MatchType.Category.PWD.getType())) {
			errors.rejectValue(IdAuthCommonConstants.REQUEST,
					IdAuthenticationErrorConstants.AUTH_TYPE_NOT_SUPPORTED.getErrorCode(),
					new Object[] { MatchType.Category.PWD.getType() },
					IdAuthenticationErrorConstants.AUTH_TYPE_NOT_SUPPORTED.getErrorMessage());
		}

		if ((isOtp || isPin) && !errors.hasErrors()) {
			validateAdditionalFactorsDetails(requestDTO, errors);
		}
		if(isPassword && !errors.hasErrors()) {
			validatePasswordDetails(requestDTO, errors);
		}
		if (!errors.hasErrors()) {
			validateKBTDetails(requestDTO, errors);
		}
	}

	/**
	 * validates langauges
	 * request.
	 *
	 * @param langCode the lang code
	 * @param errors   the errors
	 * @param field    the field
	 */
	protected void validateLangCode(String langCode, Errors errors, String field) {
		if (Objects.nonNull(langCode)) {
			if (!idInfoFetcher.getSystemSupportedLanguageCodes().contains(langCode)) {
				mosipLogger.error(SESSION_ID, this.getClass().getSimpleName(), IdAuthCommonConstants.VALIDATE,
						IdAuthCommonConstants.INVALID_INPUT_PARAMETER + field + " : " + langCode);
				errors.rejectValue(field, IdAuthenticationErrorConstants.UNSUPPORTED_LANGUAGE.getErrorCode(),
						new Object[] { field.concat(" : " + langCode) },
						IdAuthenticationErrorConstants.UNSUPPORTED_LANGUAGE.getErrorMessage());
			}
		}
	}

	/**
	 * Gets the max iris count.
	 *
	 * @return the max iris count
	 */
	protected int getMaxIrisCount() {
		return IRIS_COUNT;
	}

}