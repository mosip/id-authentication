package io.mosip.authentication.service.impl.indauth.validator;

import java.text.ParseException;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.validation.Errors;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthTypeDTO;
import io.mosip.authentication.core.dto.indauth.BaseAuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.BioInfo;
import io.mosip.authentication.core.dto.indauth.BioType;
import io.mosip.authentication.core.dto.indauth.IdentityDTO;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.dto.indauth.LanguageType;
import io.mosip.authentication.core.dto.indauth.PinInfo;
import io.mosip.authentication.core.dto.indauth.PinType;
import io.mosip.authentication.core.dto.indauth.RequestDTO;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.indauth.match.AuthType;
import io.mosip.authentication.core.spi.indauth.match.IdMapping;
import io.mosip.authentication.core.spi.indauth.match.MatchType;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategyType;
import io.mosip.authentication.service.helper.IdInfoHelper;
import io.mosip.authentication.service.impl.indauth.service.demo.DOBMatchingStrategy;
import io.mosip.authentication.service.impl.indauth.service.demo.DOBType;
import io.mosip.authentication.service.impl.indauth.service.demo.DemoAuthType;
import io.mosip.authentication.service.impl.indauth.service.demo.DemoMatchType;
import io.mosip.authentication.service.impl.indauth.service.demo.GenderType;
import io.mosip.authentication.service.validator.IdAuthValidator;
import io.mosip.kernel.core.datavalidator.exception.InvalidPhoneNumberException;
import io.mosip.kernel.core.datavalidator.exception.InvalideEmailException;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.datavalidator.email.impl.EmailValidatorImpl;
import io.mosip.kernel.datavalidator.phone.impl.PhoneValidatorImpl;

/**
 * The Class BaseAuthRequestValidator.
 *
 * @author Manoj SP
 * @author Prem Kumar
 * @author Rakesh Roshan
 * 
 */
public class BaseAuthRequestValidator extends IdAuthValidator {

	/** The mosip logger. */
	private static Logger mosipLogger = IdaLogger.getLogger(BaseAuthRequestValidator.class);

	/** The Constant ID_AUTH_VALIDATOR. */
	private static final String ID_AUTH_VALIDATOR = "ID_AUTH_VALIDATOR";

	/** The Constant SESSION_ID. */
	private static final String SESSION_ID = "SESSION_ID";

	/** The Constant VALIDATE. */
	private static final String VALIDATE = "VALIDATE";

	/** The Constant ID_AUTH_VALIDATOR. */
	private static final String AUTH_REQUEST_VALIDATOR = "AUTH_REQUEST_VALIDATOR";

	/** The Constant PRIMARY_LANG_CODE. */
	private static final String PRIMARY_LANG_CODE = "mosip.primary.lang-code";

	/** The Constant SECONDARY_LANG_CODE. */
	private static final String SECONDARY_LANG_CODE = "mosip.secondary.lang-code";

	/** The Constant INVALID_INPUT_PARAMETER. */
	private static final String INVALID_INPUT_PARAMETER = "INVALID_INPUT_PARAMETER - ";

	/** The Constant VALIDATE_CHECK_OTP_AUTH. */
	private static final String VALIDATE_CHECK_OTP_AUTH = "validate -> checkOTPAuth";

	/** The Constant PIN_INFO. */
	private static final String PIN_INFO = "pinInfo";

	/** The Constant REQUEST. */
	private static final String REQUEST = "request";

	/** The Constant OTP_LENGTH. */
	private static final Integer OTP_LENGTH = 6;

	private static final String FINGER = "finger";

	private static final String IRIS = "iris";

	private static final String FULLADDRESS = "fullAddress";

	private static final String ADDRESS = "Address";

	private static final String PERSONALIDENTITY = "personalIdentity";

	private static final String FACE = "face";

	private static final String IDENTITY_INFO_DTO = "IdentityInfoDTO";

	/** email Validator */
	@Autowired
	EmailValidatorImpl emailValidatorImpl;

	/** phone Validator */
	@Autowired
	PhoneValidatorImpl phoneValidatorImpl;

	/** The env. */
	@Autowired
	protected Environment env;

	/** The id info helper. */
	@Autowired
	protected IdInfoHelper idInfoHelper;

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
	 * Validate Biometric details i.e validating fingers,iris,face and device
	 * information.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param errors         the errors
	 */
	protected void validateBioDetails(AuthRequestDTO authRequestDTO, Errors errors) {

		AuthTypeDTO authTypeDTO = authRequestDTO.getAuthType();

		if ((authTypeDTO != null && authTypeDTO.isBio())) {

			List<BioInfo> bioInfo = authRequestDTO.getBioInfo();

			if (bioInfo != null && !bioInfo.isEmpty() && isContainDeviceInfo(bioInfo)) {

				validateFinger(authRequestDTO, bioInfo, errors);

				validateIris(authRequestDTO, bioInfo, errors);

				validateFace(authRequestDTO, bioInfo, errors);

			} else if (bioInfo == null || bioInfo.isEmpty()) {
				mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE, "missing biometric request");
				errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.MISSING_BIOMETRICDATA.getErrorCode(),
						String.format(IdAuthenticationErrorConstants.MISSING_BIOMETRICDATA.getErrorMessage(), REQUEST));
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
	private void validateFinger(AuthRequestDTO authRequestDTO, List<BioInfo> bioInfo, Errors errors) {
		if ((isAvailableBioType(bioInfo, BioType.FGRMIN) && isDuplicateBioType(authRequestDTO, BioType.FGRMIN))
				|| (isAvailableBioType(bioInfo, BioType.FGRIMG)
						&& isDuplicateBioType(authRequestDTO, BioType.FGRIMG))) {
			checkAtleastOneFingerRequestAvailable(authRequestDTO, errors);
			if (!errors.hasErrors()) {
				validateFingerRequestCount(authRequestDTO, errors);
				validateMultiFingersValue(authRequestDTO, errors);
			}
		}
	}

	/**
	 * Validate Iris.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param bioInfo        the bio info
	 * @param errors         the errors
	 */
	private void validateIris(AuthRequestDTO authRequestDTO, List<BioInfo> bioInfo, Errors errors) {
		if (isAvailableBioType(bioInfo, BioType.IRISIMG) && isDuplicateBioType(authRequestDTO, BioType.IRISIMG)) {

			checkAtleastOneIrisRequestAvailable(authRequestDTO, errors);
			if (!errors.hasErrors()) {
				validateIrisRequestCount(authRequestDTO, errors);
				validateMultiIrisValue(authRequestDTO, errors);
			}

		}
	}

	/**
	 * Validation for MultiIris Values
	 * 
	 * @param authRequestDTO
	 * @param errors
	 */
	private void validateMultiIrisValue(AuthRequestDTO authRequestDTO, Errors errors) {
		IdentityDTO identity = authRequestDTO.getRequest().getIdentity();
		List<Supplier<List<IdentityInfoDTO>>> listOfIris = Stream
				.<Supplier<List<IdentityInfoDTO>>>of(identity::getLeftEye, identity::getRightEye)
				.collect(Collectors.toList());

		List<IdentityInfoDTO> idendityInfoList = listOfIris.stream().map(Supplier::get).filter(Objects::nonNull)
				.flatMap(list -> list.stream()).collect(Collectors.toList());

		boolean isDuplicateIrisValue = checkIsDuplicate(idendityInfoList);

		if (isDuplicateIrisValue) {
			mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE, "Duplicate IRIS in request");
			errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.DUPLICATE_IRIS.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.DUPLICATE_IRIS.getErrorMessage(), REQUEST));
		}

	}

	/**
	 * Validate Face.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param bioInfo        the bio info
	 * @param errors         the errors
	 */
	private void validateFace(AuthRequestDTO authRequestDTO, List<BioInfo> bioInfo, Errors errors) {

		if (isAvailableBioType(bioInfo, BioType.FACEIMG) && isDuplicateBioType(authRequestDTO, BioType.FACEIMG)) {

			checkAtleastOneFaceRequestAvailable(authRequestDTO, errors);
		}
	}

	/**
	 * validate atleast one finger request should be available for Bio.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param errors         the errors
	 */
	private void checkAtleastOneFingerRequestAvailable(AuthRequestDTO authRequestDTO, Errors errors) {

		@SuppressWarnings("unchecked")
		boolean isAtleastOneFingerRequestAvailable = checkAnyIdInfoAvailable(authRequestDTO, IdentityDTO::getLeftThumb,
				IdentityDTO::getLeftIndex, IdentityDTO::getLeftMiddle, IdentityDTO::getLeftRing,
				IdentityDTO::getLeftLittle, IdentityDTO::getRightThumb, IdentityDTO::getRightIndex,
				IdentityDTO::getRightMiddle, IdentityDTO::getRightRing, IdentityDTO::getRightLittle);
		if (!isAtleastOneFingerRequestAvailable) {
			mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE, "finger request is not available");
			errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
					new Object[] { FINGER }, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
		}

	}

	/**
	 * validate atleast one Iris request should be available for Bio.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param errors         the errors
	 */
	private void checkAtleastOneIrisRequestAvailable(AuthRequestDTO authRequestDTO, Errors errors) {
		@SuppressWarnings("unchecked")
		boolean isIrisRequestAvailable = checkAnyIdInfoAvailable(authRequestDTO, IdentityDTO::getLeftEye,
				IdentityDTO::getRightEye);
		if (!isIrisRequestAvailable) {
			mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE, "iris request is not available");
			errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
					new Object[] { IRIS }, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
		}
	}

	/**
	 * validate atleast one Face request should be available for Bio.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param errors         the errors
	 */
	private void checkAtleastOneFaceRequestAvailable(AuthRequestDTO authRequestDTO, Errors errors) {
		boolean isFaceRequestAvailable = authRequestDTO.getRequest() != null
				&& authRequestDTO.getRequest().getIdentity() != null
				&& authRequestDTO.getRequest().getIdentity().getFace() != null;
		if (!isFaceRequestAvailable) {
			mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE, "face request is not available");
			errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
					new Object[] { FACE }, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
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
				.ofNullable(authRequestDTO.getRequest()).map(RequestDTO::getIdentity).map(func)
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
	private boolean isAvailableBioType(List<BioInfo> bioInfoList, BioType bioType) {
		return bioInfoList.parallelStream().filter(bio -> bio.getBioType() != null && !bio.getBioType().isEmpty())
				.anyMatch(bio -> bio.getBioType().equals(bioType.getType()));
	}

	/**
	 * If DemoAuthType is Bio, then validate device information is available or not.
	 *
	 * @param deviceInfoList the device info list
	 * @return true, if is contain device info
	 */
	private boolean isContainDeviceInfo(List<BioInfo> deviceInfoList) {

		return deviceInfoList.parallelStream().allMatch(deviceInfo -> deviceInfo.getDeviceInfo() != null);
	}

	/**
	 * If DemoAuthType is Bio, then check same bio request type should not be
	 * requested again.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param bioType        the bio type
	 * @return true, if is duplicate bio type
	 */
	private boolean isDuplicateBioType(AuthRequestDTO authRequestDTO, BioType bioType) {
		List<BioInfo> bioInfo = authRequestDTO.getBioInfo();
		Long bioTypeCount = Optional.ofNullable(bioInfo).map(List::parallelStream)
				.map(stream -> stream
						.filter(bio -> bio.getBioType().isEmpty() && bio.getBioType().equals(bioType.getType()))
						.count())
				.orElse((long) 0);

		return bioTypeCount <= 1;

	}

	/**
	 * If DemoAuthType is Bio, Then check duplicate request of finger and number
	 * finger of request should not exceed to 10.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param errors         the errors
	 */
	private void validateFingerRequestCount(AuthRequestDTO authRequestDTO, Errors errors) {
		IdentityDTO identity = authRequestDTO.getRequest().getIdentity();

		List<Supplier<List<IdentityInfoDTO>>> listOfIndInfoSupplier = Stream.<Supplier<List<IdentityInfoDTO>>>of(
				identity::getLeftThumb, identity::getLeftIndex, identity::getLeftMiddle, identity::getLeftRing,
				identity::getLeftLittle, identity::getRightThumb, identity::getRightIndex, identity::getRightMiddle,
				identity::getRightRing, identity::getRightLittle).collect(Collectors.toList());

		boolean anyInfoIsMoreThanOne = listOfIndInfoSupplier.stream().anyMatch(s -> getIdInfoCount(s.get()) > 1);
		if (anyInfoIsMoreThanOne) {
			mosipLogger.error(SESSION_ID, ID_AUTH_VALIDATOR, VALIDATE, "Duplicate fingers ");
			errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.DUPLICATE_FINGER.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.DUPLICATE_FINGER.getErrorMessage(), REQUEST));
		}

		Long fingerCountExceeding = listOfIndInfoSupplier.stream().map(s -> getIdInfoCount(s.get())).mapToLong(l -> l)
				.sum();
		if (fingerCountExceeding > 2) {
			mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE, "finger count is exceeding to 2");
			errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.FINGER_EXCEEDING.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.FINGER_EXCEEDING.getErrorMessage(), REQUEST));
		}
	}

	/**
	 * Validate multi fingers value.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param errors         the errors
	 */
	private void validateMultiFingersValue(AuthRequestDTO authRequestDTO, Errors errors) {
		IdentityDTO identity = authRequestDTO.getRequest().getIdentity();
		List<Supplier<List<IdentityInfoDTO>>> listOfFingerprint = Stream.<Supplier<List<IdentityInfoDTO>>>of(
				identity::getLeftThumb, identity::getLeftIndex, identity::getLeftMiddle, identity::getLeftRing,
				identity::getLeftLittle, identity::getRightThumb, identity::getRightIndex, identity::getRightMiddle,
				identity::getRightRing, identity::getRightLittle).collect(Collectors.toList());

		List<IdentityInfoDTO> idendityInfoList = listOfFingerprint.stream().map(Supplier::get).filter(Objects::nonNull)
				.flatMap(list -> list.stream()).collect(Collectors.toList());

		boolean isDuplicateFingerValue = checkIsDuplicate(idendityInfoList);

		if (isDuplicateFingerValue) {
			mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE, "Duplicate fingers in request");
			errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.DUPLICATE_FINGER.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.DUPLICATE_FINGER.getErrorMessage(), REQUEST));
		}
	}

	/**
	 * Gets the id info count.
	 *
	 * @param list the list
	 * @return the id info count
	 */
	private Long getIdInfoCount(List<IdentityInfoDTO> list) {
		return Optional.ofNullable(list).map(List::parallelStream)
				.map(stream -> stream.filter(lt -> lt.getValue() != null && !lt.getValue().isEmpty()).count())
				.orElse((long) 0);
	}

	/**
	 * Check is duplicate.
	 *
	 * @param list the list
	 * @return true, if successful
	 */
	private boolean checkIsDuplicate(List<IdentityInfoDTO> list) {
		return Optional.ofNullable(list).map(List::parallelStream).map(stream -> stream.filter((IdentityInfoDTO lt) -> {
			return lt.getValue() != null && !lt.getValue().isEmpty();
		}).collect(Collectors.groupingBy(IdentityInfoDTO::getValue, Collectors.counting())))
				.map((Map<String, Long> valueCountMap) -> valueCountMap.values().stream().anyMatch(count -> count > 1))
				.orElse(false);
	}

	/**
	 * validate Iris request count. left and right eye should not exceed 1 and total
	 * iris should not exceed 2.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param errors         the errors
	 */
	private void validateIrisRequestCount(AuthRequestDTO authRequestDTO, Errors errors) {
		IdentityDTO identity = authRequestDTO.getRequest().getIdentity();

		List<IdentityInfoDTO> leftEye = identity.getLeftEye();
		Long leftEyeCount = Optional.ofNullable(leftEye).map(List::parallelStream)
				.map(stream -> stream.filter(lt -> !lt.getValue().isEmpty()).count()).orElse((long) 0);

		List<IdentityInfoDTO> rightEye = identity.getRightEye();
		Long rightEyeCount = Optional.ofNullable(rightEye).map(List::parallelStream)
				.map(stream -> stream.filter(lt -> !lt.getValue().isEmpty()).count()).orElse((long) 0);

		if (leftEyeCount > 1 || rightEyeCount > 1) {
			mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE,
					"Iris : either left eye or right eye count is more than 1.");
			errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.IRIS_EXCEEDING.getErrorCode(),
					new Object[] { IRIS }, IdAuthenticationErrorConstants.IRIS_EXCEEDING.getErrorMessage());
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
			if (authType.isAuthTypeEnabled(authRequest, idInfoHelper)) {
				Set<MatchType> associatedMatchTypes = authType.getAssociatedMatchTypes();
				for (MatchType matchType : associatedMatchTypes) {
					List<IdentityInfoDTO> identityInfos = matchType
							.getIdentityInfoList(authRequest.getRequest().getIdentity());
					if (identityInfos != null && !identityInfos.isEmpty()) {
						availableAuthTypeInfos.add(authType.getType());
						hasMatch = true;
						checkIdentityInfoValue(identityInfos, errors);
						checkLangaugeDetails(matchType, identityInfos, errors);
					}
				}
			}
		}

		checkAvaliableAuthInfo(authRequest, errors, authTypes, availableAuthTypeInfos);

		if (!hasMatch) {
			mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE, "Missing IdentityInfoDTO");
			errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					new Object[] { IDENTITY_INFO_DTO },
					IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
		} else {
			checkOtherValues(authRequest, errors, availableAuthTypeInfos);
		}
	}

	/**
	 * Check avaliable auth info.
	 *
	 * @param authRequest            the auth request
	 * @param errors                 the errors
	 * @param authTypes              the auth types
	 * @param availableAuthTypeInfos the available auth type infos
	 */
	private void checkAvaliableAuthInfo(AuthRequestDTO authRequest, Errors errors, AuthType[] authTypes,
			Set<String> availableAuthTypeInfos) {
		for (AuthType authType : authTypes) {
			if (authType.isAuthTypeEnabled(authRequest, idInfoHelper)) {
				checkAvailableAuthType(errors, availableAuthTypeInfos, authType);

				checkAvailableMatchingStrategy(authRequest, errors, authType);

				checkAvailableMatchingThreshold(authRequest, errors, authType);
			}
		}
	}

	/**
	 * Check available matching threshold.
	 *
	 * @param authRequest the auth request
	 * @param errors      the errors
	 * @param authType    the auth type
	 */
	private void checkAvailableMatchingThreshold(AuthRequestDTO authRequest, Errors errors, AuthType authType) {
		Optional<Integer> matchingThreshold = authType.getMatchingThreshold(authRequest, idInfoHelper::getLanguageCode,
				env);
		if (matchingThreshold.isPresent()) {
			Integer integer = matchingThreshold.get();
			if (integer <= 0 || integer >= 100) {
				if (authType.equals(DemoAuthType.FAD_PRI)) {
					mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, INVALID_INPUT_PARAMETER,
							"Full Address Matching Strategy is Missing");
					errors.rejectValue(REQUEST,
							IdAuthenticationErrorConstants.INVALID_MATCHINGTHRESHOLD_FAD_PRI.getErrorCode(),
							new Object[] { PERSONALIDENTITY },
							IdAuthenticationErrorConstants.INVALID_MATCHINGTHRESHOLD_FAD_PRI.getErrorMessage());
				} else if (authType.equals(DemoAuthType.FAD_SEC)) {
					mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, INVALID_INPUT_PARAMETER,
							"Full Address Matching Threshold is Invalid");
					errors.rejectValue(REQUEST,
							IdAuthenticationErrorConstants.INVALID_MATCHINGTHRESHOLD_FAD_SEC.getErrorCode(),
							new Object[] { PERSONALIDENTITY },
							IdAuthenticationErrorConstants.INVALID_MATCHINGTHRESHOLD_FAD_SEC.getErrorMessage());
				} else if (authType.equals(DemoAuthType.PI_PRI)) {
					mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, INVALID_INPUT_PARAMETER,
							"Personal Identity Matching Threshold is Invalid");
					errors.rejectValue(REQUEST,
							IdAuthenticationErrorConstants.INVALID_MATCHINGTHRESHOLD_PI_PRI.getErrorCode(),
							new Object[] { PERSONALIDENTITY },
							IdAuthenticationErrorConstants.INVALID_MATCHINGTHRESHOLD_PI_PRI.getErrorMessage());
				} else if (authType.equals(DemoAuthType.PI_SEC)) {
					mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, INVALID_INPUT_PARAMETER,
							"Personal Identity Matching Threshold is Invalid");
					errors.rejectValue(REQUEST,
							IdAuthenticationErrorConstants.INVALID_MATCHINGTHRESHOLD_PI_SEC.getErrorCode(),
							new Object[] { PERSONALIDENTITY },
							IdAuthenticationErrorConstants.INVALID_MATCHINGTHRESHOLD_PI_SEC.getErrorMessage());
				}
			}
		}
	}

	/**
	 * Check available matching strategy.
	 *
	 * @param authRequest the auth request
	 * @param errors      the errors
	 * @param authType    the auth type
	 */
	private void checkAvailableMatchingStrategy(AuthRequestDTO authRequest, Errors errors, AuthType authType) {
		Optional<String> matchingStrategy = authType.getMatchingStrategy(authRequest, idInfoHelper::getLanguageCode);
		if (matchingStrategy.isPresent()) {
			if (!MatchingStrategyType.getMatchStrategyType(matchingStrategy.get()).isPresent()) {
				if (authType.equals(DemoAuthType.FAD_PRI)) {
					mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, INVALID_INPUT_PARAMETER,
							"fullAddress Matching Strategy is Missing");
					errors.rejectValue(REQUEST,
							IdAuthenticationErrorConstants.INVALID_MATCHINGSTRATEGY_FAD_PRI.getErrorCode(),
							new Object[] { FULLADDRESS },
							IdAuthenticationErrorConstants.INVALID_MATCHINGSTRATEGY_FAD_PRI.getErrorMessage());
				} else if (authType.equals(DemoAuthType.FAD_SEC)) {
					mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, INVALID_INPUT_PARAMETER,
							"fullAddress Matching Strategy is Missing");
					errors.rejectValue(REQUEST,
							IdAuthenticationErrorConstants.INVALID_MATCHINGSTRATEGY_FAD_SEC.getErrorCode(),
							new Object[] { FULLADDRESS },
							IdAuthenticationErrorConstants.INVALID_MATCHINGSTRATEGY_FAD_SEC.getErrorMessage());
				} else if (authType.equals(DemoAuthType.PI_PRI)) {
					mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, INVALID_INPUT_PARAMETER,
							"personalIdentity Matching Strategy is Missing");
					errors.rejectValue(REQUEST,
							IdAuthenticationErrorConstants.INVALID_MATCHINGSTRATEGY_PI_PRI.getErrorCode(),
							new Object[] { PERSONALIDENTITY },
							IdAuthenticationErrorConstants.INVALID_MATCHINGSTRATEGY_PI_PRI.getErrorMessage());
				} else if (authType.equals(DemoAuthType.PI_SEC)) {
					mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, INVALID_INPUT_PARAMETER,
							"personalIdentity Matching Strategy is Missing");
					errors.rejectValue(REQUEST,
							IdAuthenticationErrorConstants.INVALID_MATCHINGSTRATEGY_PI_SEC.getErrorCode(),
							new Object[] { PERSONALIDENTITY },
							IdAuthenticationErrorConstants.INVALID_MATCHINGSTRATEGY_PI_SEC.getErrorMessage());
				}
			}
		}
	}

	/**
	 * Check available auth type.
	 *
	 * @param errors                 the errors
	 * @param availableAuthTypeInfos the available auth type infos
	 * @param authType               the auth type
	 */
	private void checkAvailableAuthType(Errors errors, Set<String> availableAuthTypeInfos, AuthType authType) {
		if (!availableAuthTypeInfos.contains(authType.getType())) {
			if ((authType.equals(DemoAuthType.FAD_PRI)) || (authType.equals(DemoAuthType.FAD_SEC))) {
				mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, INVALID_INPUT_PARAMETER,
						"Full Address is Missing");
				errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.MISSING_FAD.getErrorCode(),
						new Object[] { FULLADDRESS }, IdAuthenticationErrorConstants.MISSING_FAD.getErrorMessage());
			} else if ((authType.equals(DemoAuthType.AD_PRI)) || (authType.equals(DemoAuthType.AD_SEC))) {
				mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, INVALID_INPUT_PARAMETER, "Address is Missing");
				errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.MISSING_AD.getErrorCode(),
						new Object[] { ADDRESS }, IdAuthenticationErrorConstants.MISSING_AD.getErrorMessage());
			} else if ((authType.equals(DemoAuthType.PI_PRI)) || (authType.equals(DemoAuthType.PI_SEC))) {
				mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, INVALID_INPUT_PARAMETER,
						"personalIdentity is Missing");
				errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.MISSING_PI.getErrorCode(),
						new Object[] { PERSONALIDENTITY }, IdAuthenticationErrorConstants.MISSING_PI.getErrorMessage());
			}
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
				mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE, "IdentityInfoDTO is invalid");
				errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
						new Object[] { IDENTITY_INFO_DTO },
						IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
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
		checkDOB(authRequest, errors);
		checkDOBType(authRequest, errors);
		checkAge(authRequest, errors);
		checkGender(authRequest, errors);
		validateEmail(authRequest, errors);
		validatePhone(authRequest, errors);
		validateAdAndFullAd(availableAuthTypeInfos, errors);
	}

	/**
	 * Validate ad and full ad.
	 *
	 * @param availableAuthTypeInfos the available auth type infos
	 * @param errors                 the errors
	 */
	private void validateAdAndFullAd(Set<String> availableAuthTypeInfos, Errors errors) {
		if (availableAuthTypeInfos.contains(DemoAuthType.AD_PRI.getType())
				&& availableAuthTypeInfos.contains(DemoAuthType.FAD_PRI.getType())) {
			mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE, "Ad and FAD are enabled");
			errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					new Object[] { IDENTITY_INFO_DTO },
					IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
		}
	}

	/**
	 * Check gender.
	 *
	 * @param authRequest the auth request
	 * @param errors      the errors
	 */
	private void checkGender(AuthRequestDTO authRequest, Errors errors) {
		List<IdentityInfoDTO> genderList = DemoMatchType.GENDER
				.getIdentityInfoList(authRequest.getRequest().getIdentity());
		if (genderList != null) {
			for (IdentityInfoDTO identityInfoDTO : genderList) {
				if (!GenderType.isTypePresent(identityInfoDTO.getValue())) {
					mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE,
							"Demographic data – Gender(pi) did not match");
					errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
							new Object[] { "gender" },
							IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
				}

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
		List<IdentityInfoDTO> dobTypeList = DemoMatchType.DOBTYPE
				.getIdentityInfoList(authRequest.getRequest().getIdentity());
		if (dobTypeList != null) {
			for (IdentityInfoDTO identityInfoDTO : dobTypeList) {
				if (!DOBType.isTypePresent(identityInfoDTO.getValue())) {
					mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE,
							"Demographic data – DOBType(pi) did not match");
					errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
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
		List<IdentityInfoDTO> ageList = DemoMatchType.AGE.getIdentityInfoList(authRequest.getRequest().getIdentity());
		if (ageList != null) {
			for (IdentityInfoDTO identityInfoDTO : ageList) {
				try {
					Integer.parseInt(identityInfoDTO.getValue());
				} catch (NumberFormatException e) {
					mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE,
							"Demographic data – Age(pi) did not match");
					errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
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
		List<IdentityInfoDTO> dobList = DemoMatchType.DOB.getIdentityInfoList(authRequest.getRequest().getIdentity());
		if (dobList != null) {
			for (IdentityInfoDTO identityInfoDTO : dobList) {
				try {
					DOBMatchingStrategy.getDateFormat().parse(identityInfoDTO.getValue());
				} catch (ParseException e) {
					// FIXME change to DOB - Invalid -DOB - Please enter DOB in specified date
					// format or Age in the acceptable range

					mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE,
							"Demographic data – DOB(pi) did not match");
					errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
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
		String priLangCode = env.getProperty(PRIMARY_LANG_CODE);
		String secLangCode = env.getProperty(SECONDARY_LANG_CODE);

		Map<String, Long> langCount = identityInfos.stream().map((IdentityInfoDTO idInfo) -> {
			String language = idInfo.getLanguage();
			if (language == null) {
				language = priLangCode;
			}
			return new SimpleEntry<>(language, idInfo);
		}).collect(Collectors.groupingBy(Entry::getKey, Collectors.counting()));

		Long primaryLangCount = langCount.get(priLangCode);
		Long secondaryLangCount = langCount.get(secLangCode);

		if (secondaryLangCount != null) {
			checkSecondayLanguage(demoMatchType, secondaryLangCount, errors);
		}

		boolean anyOtherLang = langCount.keySet().stream()
				.anyMatch(lang -> lang != null && !lang.equals(priLangCode) && !lang.equals(secLangCode));

		if (primaryLangCount != null && primaryLangCount > 1 || anyOtherLang) {
			mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, INVALID_INPUT_PARAMETER,
					"Invalid or Multiple Primary language code");
			errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					new Object[] { "PrimaryLanguageCode" },
					IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
		}
	}

	/**
	 * Check seconday language.
	 *
	 * @param demoMatchType the demo match type
	 * @param secCount      the sec count
	 * @param errors        the errors
	 */
	private void checkSecondayLanguage(MatchType demoMatchType, long secCount, Errors errors) {
		IdMapping idMapping = demoMatchType.getIdMapping();
		boolean checkForSecondaryLanguage = Stream.of(DemoMatchType.values())
				.filter(matchType -> matchType.getIdMapping().equals(idMapping))
				.anyMatch(matchType -> matchType.getLanguageType().equals(LanguageType.SECONDARY_LANG));
		if (checkForSecondaryLanguage) {
			if (secCount > 1) {
				mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, INVALID_INPUT_PARAMETER,
						"Invalid or Multiple Seconday language code");
				errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
						new Object[] { "SecondayLanguageCode" },
						IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
			}
		} else {
			if (secCount > 0) {
				mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, INVALID_INPUT_PARAMETER, "Invalid language code");
				errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
						new Object[] { "SecondayLanguageCode" },
						IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
			}
		}

	}

	/**
	 * Check OTP auth.
	 *
	 * @param authRequest the auth request
	 * @param errors      the errors
	 */
	protected void checkOTPAuth(AuthRequestDTO authRequest, Errors errors) {
		Optional<String> otp = getOtpValue(authRequest);
		if (!otp.isPresent()) {
			mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE_CHECK_OTP_AUTH,
					"INVALID_OTP - pinType is not OTP");
			errors.rejectValue(PIN_INFO, IdAuthenticationErrorConstants.OTP_NOT_PRESENT.getErrorCode(),
					IdAuthenticationErrorConstants.OTP_NOT_PRESENT.getErrorMessage());
		} else if (OTP_LENGTH != otp.orElse("").length()) {
			mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE_CHECK_OTP_AUTH,
					"INVALID_OTP - pinType is not OTP");
			errors.rejectValue(PIN_INFO, IdAuthenticationErrorConstants.INVALID_OTP.getErrorCode(),
					IdAuthenticationErrorConstants.INVALID_OTP.getErrorMessage());
		}
	}

	/**
	 * Gets the otp value.
	 *
	 * @param authreqdto the authreqdto
	 * @return the otp value
	 */
	private Optional<String> getOtpValue(AuthRequestDTO authreqdto) {
		return Optional.ofNullable(authreqdto.getPinInfo())
				.flatMap(pinInfos -> pinInfos.stream()
						.filter(pinInfo -> pinInfo.getType() != null && pinInfo.getType().equals(PinType.OTP.getType()))
						.findAny())
				.map(PinInfo::getValue);
	}

	/**
	 * validate email id.
	 *
	 * @param authRequest authRequest
	 * @param errors      the errors
	 */
	private void validateEmail(AuthRequestDTO authRequest, Errors errors) {
		try {
			List<IdentityInfoDTO> emailId = DemoMatchType.EMAIL
					.getIdentityInfoList(authRequest.getRequest().getIdentity());
			if (emailId != null) {
				for (IdentityInfoDTO email : emailId) {
					emailValidatorImpl.validateEmail(email.getValue());
				}
			}
		} catch (InvalideEmailException e) {
			mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, INVALID_INPUT_PARAMETER,
					"Invalid email \n" + ExceptionUtils.getStackTrace(e));
			errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					new Object[] { "emailId" },
					IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
		}
	}

	/**
	 * validate phone number.
	 *
	 * @param authRequest authRequest
	 * @param errors      the errors
	 */
	private void validatePhone(AuthRequestDTO authRequest, Errors errors) {
		try {
			List<IdentityInfoDTO> phoneNumber = DemoMatchType.PHONE
					.getIdentityInfoList(authRequest.getRequest().getIdentity());
			if (phoneNumber != null) {
				for (IdentityInfoDTO phone : phoneNumber) {
					phoneValidatorImpl.validatePhone(phone.getValue());
				}
			}
		} catch (InvalidPhoneNumberException e) {
			mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, INVALID_INPUT_PARAMETER,
					"Invalid email \n" + ExceptionUtils.getStackTrace(e));
			errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					new Object[] { "phoneNumber" },
					IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
		}
	}
}
