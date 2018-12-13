package io.mosip.authentication.service.impl.indauth.validator;

import java.text.ParseException;
import java.util.AbstractMap.SimpleEntry;
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
import io.mosip.authentication.service.impl.indauth.service.demo.DOBMatchingStrategy;
import io.mosip.authentication.service.impl.indauth.service.demo.DOBType;
import io.mosip.authentication.service.impl.indauth.service.demo.DemoAuthType;
import io.mosip.authentication.service.impl.indauth.service.demo.DemoMatchType;
import io.mosip.authentication.service.impl.indauth.service.demo.GenderType;
import io.mosip.authentication.service.validator.IdAuthValidator;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.datavalidator.email.impl.EmailValidatorImpl;
import io.mosip.kernel.datavalidator.phone.impl.PhoneValidatorImpl;

/**
 * The Class BaseAuthRequestValidator.
 *
 * @author Manoj SP
 * @author Prem Kumar
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

	/** email Validator */
	@Autowired
	EmailValidatorImpl emailValidatorImpl;

	/** phone Validator */
	@Autowired
	PhoneValidatorImpl phoneValidatorImpl;

	/** The env. */
	@Autowired
	protected Environment env;

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
			validateVer(baseAuthRequestDTO.getVer(), errors);
		}
	}

	/**
	 * Validate Biometric details i.e validating fingers,iris,face and device
	 * information.
	 * 
	 * @param authRequestDTO
	 * @param errors
	 */
	protected void validateBioDetails(AuthRequestDTO authRequestDTO, Errors errors) {

		AuthTypeDTO authTypeDTO = authRequestDTO.getAuthType();

		if ((authTypeDTO != null && authTypeDTO.isBio())) {

			List<BioInfo> bioInfo = authRequestDTO.getBioInfo();

			if (bioInfo != null && !bioInfo.isEmpty() && isContainDeviceInfo(bioInfo)) {

				validateFinger(authRequestDTO, bioInfo, errors);

				validateIris(authRequestDTO, bioInfo, errors);

				validateFace(authRequestDTO, bioInfo, errors);

			} else {
				errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorCode(),
						String.format(IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorMessage(), REQUEST));
			}
		}

	}

	/**
	 * Validate fingers.
	 * 
	 * @param authRequestDTO
	 * @param bioInfo
	 * @param errors
	 */
	private void validateFinger(AuthRequestDTO authRequestDTO, List<BioInfo> bioInfo, Errors errors) {
		if ((isAvailableBioType(bioInfo, BioType.FGRMIN) && isDuplicateBioType(authRequestDTO, BioType.FGRMIN))
				|| (isAvailableBioType(bioInfo, BioType.FGRIMG)
						&& isDuplicateBioType(authRequestDTO, BioType.FGRIMG))) {

			checkAtleastOneFingerRequestAvailable(authRequestDTO, errors);

			validateFingerRequestCount(authRequestDTO, errors);
		}
	}

	/**
	 * Validate Iris.
	 * 
	 * @param authRequestDTO
	 * @param bioInfo
	 * @param errors
	 */
	private void validateIris(AuthRequestDTO authRequestDTO, List<BioInfo> bioInfo, Errors errors) {
		if (isAvailableBioType(bioInfo, BioType.IRISIMG) && isDuplicateBioType(authRequestDTO, BioType.IRISIMG)) {

			checkAtleastOneIrisRequestAvailable(authRequestDTO, errors);

			validateIrisRequestCount(authRequestDTO);
		}
	}

	/**
	 * Validate Face.
	 * 
	 * @param authRequestDTO
	 * @param bioInfo
	 * @param errors
	 */
	private void validateFace(AuthRequestDTO authRequestDTO, List<BioInfo> bioInfo, Errors errors) {

		if (isAvailableBioType(bioInfo, BioType.FACEIMG) && isDuplicateBioType(authRequestDTO, BioType.FACEIMG)) {

			checkAtleastOneFaceRequestAvailable(authRequestDTO, errors);
		}
	}

	/**
	 * validate atleast one finger request should be available for Bio.
	 * 
	 * @param authRequestDTO
	 * @param errors
	 */
	private void checkAtleastOneFingerRequestAvailable(AuthRequestDTO authRequestDTO, Errors errors) {

		@SuppressWarnings("unchecked")
		boolean isAtleastOneFingerRequestAvailable = checkAnyIdInfoAvailable(authRequestDTO, IdentityDTO::getLeftThumb,
				IdentityDTO::getLeftIndex, IdentityDTO::getLeftMiddle, IdentityDTO::getLeftRing,
				IdentityDTO::getLeftLittle, IdentityDTO::getRightThumb, IdentityDTO::getRightIndex,
				IdentityDTO::getRightMiddle, IdentityDTO::getRightRing, IdentityDTO::getRightLittle);
		if (!isAtleastOneFingerRequestAvailable) {
			errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorMessage(), REQUEST));
		}

	}

	/**
	 * validate atleast one Iris request should be available for Bio.
	 * 
	 * @param authRequestDTO
	 * @param errors
	 */
	private void checkAtleastOneIrisRequestAvailable(AuthRequestDTO authRequestDTO, Errors errors) {
		@SuppressWarnings("unchecked")
		boolean isIrisRequestAvailable = checkAnyIdInfoAvailable(authRequestDTO, IdentityDTO::getLeftEye,
				IdentityDTO::getRightEye);
		if (!isIrisRequestAvailable) {
			errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorMessage(), REQUEST));
		}
	}

	/**
	 * validate atleast one Face request should be available for Bio.
	 * 
	 * @param authRequestDTO
	 * @param errors
	 */
	private void checkAtleastOneFaceRequestAvailable(AuthRequestDTO authRequestDTO, Errors errors) {
		boolean isFaceRequestAvailable = authRequestDTO.getRequest() != null
				&& authRequestDTO.getRequest().getIdentity() != null
				&& authRequestDTO.getRequest().getIdentity().getFace() != null;
		if (!isFaceRequestAvailable) {
			errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST.getErrorMessage(), REQUEST));
		}
	}

	/**
	 * check any IdentityInfoDto data available or not.
	 * 
	 * @param authRequestDTO
	 * @param functions
	 * @return
	 */
	@SuppressWarnings("unchecked")
	boolean checkAnyIdInfoAvailable(AuthRequestDTO authRequestDTO,
			Function<IdentityDTO, List<IdentityInfoDTO>>... functions) {
		return Stream.<Function<IdentityDTO, List<IdentityInfoDTO>>>of(functions)
				.anyMatch(func -> Optional.ofNullable(authRequestDTO.getRequest()).map(RequestDTO::getIdentity)
						.map(func).filter(list -> !list.isEmpty()).isPresent());
	}

	/**
	 * If DemoAuthType is Bio, then validate bioinfo is available or not.
	 * 
	 * @param bioInfoList
	 * @param bioType
	 * @return
	 */
	private boolean isAvailableBioType(List<BioInfo> bioInfoList, BioType bioType) {
		return bioInfoList.parallelStream().filter(bio -> bio.getBioType() != null && !bio.getBioType().isEmpty())
				.anyMatch(bio -> bio.getBioType().equals(bioType.getType()));
	}

	/**
	 * If DemoAuthType is Bio, then validate device information is available or not.
	 * 
	 * @param deviceInfoList
	 * @return
	 */
	private boolean isContainDeviceInfo(List<BioInfo> deviceInfoList) {

		return deviceInfoList.parallelStream().allMatch(deviceInfo -> deviceInfo.getDeviceInfo() != null);
	}

	/**
	 * If DemoAuthType is Bio, then check same bio request type should not be
	 * requested again.
	 * 
	 * @param authRequestDTO
	 * @param bioType
	 * @return
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
	 * @param authRequestDTO
	 * @param errors
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
		if (fingerCountExceeding > 10) {
			errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.FINGER_EXCEEDING.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.FINGER_EXCEEDING.getErrorMessage(), REQUEST));
		}
	}

	private Long getIdInfoCount(List<IdentityInfoDTO> list) {
		return Optional.ofNullable(list).map(List::parallelStream)
				.map(stream -> stream.filter(lt -> lt.getValue() != null && !lt.getValue().isEmpty()).count())
				.orElse((long) 0);
	}

	/**
	 * validate Iris request count. left and right eye should not exceed 1 and total
	 * iris should not exceed 2.
	 * 
	 * @param authRequestDTO
	 */
	private void validateIrisRequestCount(AuthRequestDTO authRequestDTO) {
		IdentityDTO identity = authRequestDTO.getRequest().getIdentity();

		List<IdentityInfoDTO> leftEye = identity.getLeftEye();
		Long leftEyeCount = Optional.ofNullable(leftEye).map(List::parallelStream)
				.map(stream -> stream.filter(lt -> !lt.getValue().isEmpty()).count()).orElse((long) 0);

		List<IdentityInfoDTO> rightEye = identity.getRightEye();
		Long rightEyeCount = Optional.ofNullable(rightEye).map(List::parallelStream)
				.map(stream -> stream.filter(lt -> !lt.getValue().isEmpty()).count()).orElse((long) 0);

		if (leftEyeCount > 1 || rightEyeCount > 1) {
			// add errors
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
		boolean hasMatch = false;
		for (AuthType authType : authTypes) {
			if (authType.isAuthTypeEnabled(authRequest)) {
				Set<MatchType> associatedMatchTypes = authType.getAssociatedMatchTypes();
				for (MatchType matchType : associatedMatchTypes) {
					List<IdentityInfoDTO> identityInfos = matchType.getIdentityInfoFunction()
							.apply(authRequest.getRequest().getIdentity());
					if (identityInfos != null) {
						hasMatch = true;
						checkIdentityInfoValue(identityInfos, errors);
						checkLangaugeDetails(matchType, identityInfos, errors);
					}
				}
			}
		}
		checkOtherValues(authRequest, errors, hasMatch);
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
						new Object[] { "IdentityInfoDTO" },
						IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
			}

		}

	}

	/**
	 * Check other values.
	 *
	 * @param authRequest the auth request
	 * @param errors      the errors
	 * @param hasMatch    the has match
	 */
	private void checkOtherValues(AuthRequestDTO authRequest, Errors errors, boolean hasMatch) {
		if (!hasMatch) {
			mosipLogger.error(SESSION_ID, AUTH_REQUEST_VALIDATOR, VALIDATE, "Missing IdentityInfoDTO");
			errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					new Object[] { "IdentityInfoDTO" },
					IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
		} else {
			checkDOB(authRequest, errors);
			checkDOBType(authRequest, errors);
			checkAge(authRequest, errors);
			checkGender(authRequest, errors);
			validateEmail(authRequest, errors);
			validatePhone(authRequest, errors);
		}
	}

	/**
	 * Check gender.
	 *
	 * @param authRequest the auth request
	 * @param errors      the errors
	 */
	private void checkGender(AuthRequestDTO authRequest, Errors errors) {
		List<IdentityInfoDTO> genderList = DemoMatchType.GENDER.getIdentityInfoFunction()
				.apply(authRequest.getRequest().getIdentity());
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
		List<IdentityInfoDTO> dobTypeList = DemoMatchType.DOBTYPE.getIdentityInfoFunction()
				.apply(authRequest.getRequest().getIdentity());
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
		List<IdentityInfoDTO> ageList = DemoMatchType.AGE.getIdentityInfoFunction()
				.apply(authRequest.getRequest().getIdentity());
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
		List<IdentityInfoDTO> dobList = DemoMatchType.DOB.getIdentityInfoFunction()
				.apply(authRequest.getRequest().getIdentity());
		if (dobList != null) {
			for (IdentityInfoDTO identityInfoDTO : dobList) {
				try {
					DOBMatchingStrategy.getDateFormat().parse(identityInfoDTO.getValue());
				} catch (ParseException e) {
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
	 */
	private void validateEmail(AuthRequestDTO authRequest, Errors errors) {

		List<IdentityInfoDTO> emailId = DemoMatchType.EMAIL.getIdentityInfoFunction()
				.apply(authRequest.getRequest().getIdentity());
		if (emailId != null) {
			for (IdentityInfoDTO email : emailId) {
				boolean isValidEmail = emailValidatorImpl.validateEmail(email.getValue());

				if (!isValidEmail) {
					errors.rejectValue("emailId", IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
							new Object[] { "emailId" },
							IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
				}
			}
		}

	}

	/**
	 * validate phone number.
	 * 
	 * @param authRequest authRequest
	 */
	private void validatePhone(AuthRequestDTO authRequest, Errors errors) {

		List<IdentityInfoDTO> phoneNumber = DemoMatchType.PHONE.getIdentityInfoFunction()
				.apply(authRequest.getRequest().getIdentity());
		if (phoneNumber != null) {
			for (IdentityInfoDTO phone : phoneNumber) {
				boolean isValidPhone = phoneValidatorImpl.validatePhone(phone.getValue());
				if (!isValidPhone) {
					errors.rejectValue("phoneNumber",
							IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
							new Object[] { "phoneNumber" },
							IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
				}
			}
		}

	}

}
