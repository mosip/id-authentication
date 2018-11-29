package io.mosip.authentication.service.impl.indauth.validator;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthTypeDTO;
import io.mosip.authentication.core.dto.indauth.BaseAuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.BioInfo;
import io.mosip.authentication.core.dto.indauth.BioType;
import io.mosip.authentication.core.dto.indauth.IdentityDTO;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.dto.indauth.RequestDTO;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.service.validator.IdAuthValidator;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * The Class BaseAuthRequestValidator.
 *
 * @author Manoj SP
 * @author Prem Kumar
 * 
 */
public class BaseAuthRequestValidator implements Validator {

	/** The mosip logger. */
	private static Logger mosipLogger = IdaLogger.getLogger(IdAuthValidator.class);

	/** The Constant ID_AUTH_VALIDATOR. */
	private static final String ID_AUTH_VALIDATOR = "ID_AUTH_VALIDATOR";

	/** The Constant SESSION_ID. */
	private static final String SESSION_ID = "SESSION_ID";

	/** The Constant MISSING_INPUT_PARAMETER. */
	private static final String MISSING_INPUT_PARAMETER = "MISSING_INPUT_PARAMETER - ";

	/** The Constant VALIDATE. */
	private static final String VALIDATE = "VALIDATE";

	/** The Constant ID. */
	private static final String ID = "id";

	/** The Constant VER. */
	private static final String VER = "ver";

	/** The Constant verPattern. */
	private static final Pattern verPattern = Pattern.compile("^\\d+(\\.\\d{1,1})?$");

	private static final String REQUEST = "request";

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
	 * Validate id.
	 *
	 * @param id     the id
	 * @param errors the errors
	 */
	protected void validateId(String id, Errors errors) {
		if (Objects.isNull(id)) {
			mosipLogger.error(SESSION_ID, ID_AUTH_VALIDATOR, VALIDATE, MISSING_INPUT_PARAMETER + " - id");
			errors.rejectValue(ID, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
					new Object[] { ID }, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
		}
	}

	/**
	 * Validate ver.
	 *
	 * @param ver    the ver
	 * @param errors the errors
	 */
	protected void validateVer(String ver, Errors errors) {
		if (Objects.isNull(ver)) {
			mosipLogger.error(SESSION_ID, ID_AUTH_VALIDATOR, VALIDATE, MISSING_INPUT_PARAMETER + VER);
			errors.rejectValue(VER, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
					new Object[] { VER }, IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
		} else if (!verPattern.matcher(ver).matches()) {
			mosipLogger.error(SESSION_ID, ID_AUTH_VALIDATOR, VALIDATE,
					"INVALID_INPUT_PARAMETER - ver - value -> " + ver);
			errors.rejectValue(VER, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					new Object[] { VER }, IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
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
		if (isContainBioInfo(bioInfo, BioType.FGRMIN) && isDuplicateBioRequest(authRequestDTO, BioType.FGRMIN)) {

			checkAtleastOneFingerRequestAvailable(authRequestDTO, errors);

			validateFingerRequestCount(authRequestDTO, errors);
		}
		if (isContainBioInfo(bioInfo, BioType.FGRIMG) && isDuplicateBioRequest(authRequestDTO, BioType.FGRIMG)) {

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
		if (isContainBioInfo(bioInfo, BioType.IRISIMG) && isDuplicateBioRequest(authRequestDTO, BioType.IRISIMG)) {

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

		if (isContainBioInfo(bioInfo, BioType.FACEIMG) && isDuplicateBioRequest(authRequestDTO, BioType.FACEIMG)) {

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

		boolean isAtleastOneFingerRequestAvailable = Optional.ofNullable(authRequestDTO.getRequest())
				.map(RequestDTO::getIdentity).map(IdentityDTO::getLeftIndex).filter(list -> !list.isEmpty()).isPresent()
				|| Optional.ofNullable(authRequestDTO.getRequest()).map(RequestDTO::getIdentity)
						.map(IdentityDTO::getLeftLittle).filter(list -> !list.isEmpty()).isPresent()
				|| Optional.ofNullable(authRequestDTO.getRequest()).map(RequestDTO::getIdentity)
						.map(IdentityDTO::getLeftMiddle).filter(list -> !list.isEmpty()).isPresent()
				|| Optional.ofNullable(authRequestDTO.getRequest()).map(RequestDTO::getIdentity)
						.map(IdentityDTO::getLeftRing).filter(list -> !list.isEmpty()).isPresent()
				|| Optional.ofNullable(authRequestDTO.getRequest()).map(RequestDTO::getIdentity)
						.map(IdentityDTO::getLeftThumb).filter(list -> !list.isEmpty()).isPresent()
				|| Optional.ofNullable(authRequestDTO.getRequest()).map(RequestDTO::getIdentity)
						.map(IdentityDTO::getRightIndex).filter(list -> !list.isEmpty()).isPresent()
				|| Optional.ofNullable(authRequestDTO.getRequest()).map(RequestDTO::getIdentity)
						.map(IdentityDTO::getRightLittle).filter(list -> !list.isEmpty()).isPresent()
				|| Optional.ofNullable(authRequestDTO.getRequest()).map(RequestDTO::getIdentity)
						.map(IdentityDTO::getRightMiddle).filter(list -> !list.isEmpty()).isPresent()
				|| Optional.ofNullable(authRequestDTO.getRequest()).map(RequestDTO::getIdentity)
						.map(IdentityDTO::getRightRing).filter(list -> !list.isEmpty()).isPresent()
				|| Optional.ofNullable(authRequestDTO.getRequest()).map(RequestDTO::getIdentity)
						.map(IdentityDTO::getRightThumb).filter(list -> !list.isEmpty()).isPresent();
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
		boolean isIrisRequestAvailable = authRequestDTO.getRequest() != null
				&& authRequestDTO.getRequest().getIdentity() != null
				&& authRequestDTO.getRequest().getIdentity().getLeftEye() != null
				&& !authRequestDTO.getRequest().getIdentity().getLeftEye().isEmpty()
				|| authRequestDTO.getRequest() != null && authRequestDTO.getRequest().getIdentity() != null
						&& authRequestDTO.getRequest().getIdentity().getRightEye() != null
						&& !authRequestDTO.getRequest().getIdentity().getRightEye().isEmpty();
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
	 * If AuthType is Bio, then validate bioinfo is available or not.
	 * 
	 * @param bioInfoList
	 * @param bioType
	 * @return
	 */
	private boolean isContainBioInfo(List<BioInfo> bioInfoList, BioType bioType) {
		return bioInfoList.parallelStream().filter(bio -> bio.getBioType() != null && !bio.getBioType().isEmpty())
				.anyMatch(bio -> bio.getBioType().equals(bioType.getType()));
	}

	/**
	 * If AuthType is Bio, then validate device information is available or not.
	 * 
	 * @param deviceInfoList
	 * @return
	 */
	private boolean isContainDeviceInfo(List<BioInfo> deviceInfoList) {

		return deviceInfoList.parallelStream().allMatch(deviceInfo -> deviceInfo.getDeviceInfo() != null);
	}

	/**
	 * If AuthType is Bio, then check same bio request type should not be requested
	 * again.
	 * 
	 * @param authRequestDTO
	 * @param bioType
	 * @return
	 */
	private boolean isDuplicateBioRequest(AuthRequestDTO authRequestDTO, BioType bioType) {
		List<BioInfo> bioInfo = authRequestDTO.getBioInfo();
		Long bioTypeCount = Optional.ofNullable(bioInfo).map(List::parallelStream)
				.map(stream -> stream
						.filter(bio -> bio.getBioType().isEmpty() && bio.getBioType().equals(bioType.getType()))
						.count())
				.orElse((long) 0);

		return bioTypeCount <= 1;

	}

	/**
	 * If AuthType is Bio, Then check duplicate request of finger and number finger
	 * of request should not exceed to 10.
	 * 
	 * @param authRequestDTO
	 * @param errors
	 */
	private void validateFingerRequestCount(AuthRequestDTO authRequestDTO, Errors errors) {
		IdentityDTO identity = authRequestDTO.getRequest().getIdentity();

		// --- Left Finger ---
		List<IdentityInfoDTO> leftThumb = identity.getLeftThumb();
		Long leftThumbCount = Optional.ofNullable(leftThumb).map(List::parallelStream)
				.map(stream -> stream.filter(lt -> !lt.getValue().isEmpty()).count()).orElse((long) 0);

		List<IdentityInfoDTO> leftIndex = identity.getLeftIndex();
		Long leftIndexCount = Optional.ofNullable(leftIndex).map(List::parallelStream)
				.map(stream -> stream.filter(lt -> !lt.getValue().isEmpty()).count()).orElse((long) 0);

		List<IdentityInfoDTO> leftMiddle = identity.getLeftMiddle();
		Long leftMiddleCount = Optional.ofNullable(leftMiddle).map(List::parallelStream)
				.map(stream -> stream.filter(lt -> !lt.getValue().isEmpty()).count()).orElse((long) 0);

		List<IdentityInfoDTO> leftRing = identity.getLeftRing();
		Long leftRingCount = Optional.ofNullable(leftRing).map(List::parallelStream)
				.map(stream -> stream.filter(lt -> !lt.getValue().isEmpty()).count()).orElse((long) 0);

		List<IdentityInfoDTO> leftLittle = identity.getLeftLittle();
		Long leftLittleCount = Optional.ofNullable(leftLittle).map(List::parallelStream)
				.map(stream -> stream.filter(lt -> !lt.getValue().isEmpty()).count()).orElse((long) 0);

		// - Right Finger ----
		List<IdentityInfoDTO> rightThumb = identity.getRightThumb();
		Long rightThumbCount = Optional.ofNullable(rightThumb).map(List::parallelStream)
				.map(stream -> stream.filter(lt -> !lt.getValue().isEmpty()).count()).orElse((long) 0);

		List<IdentityInfoDTO> rightIndex = identity.getRightIndex();
		Long rightIndexCount = Optional.ofNullable(rightIndex).map(List::parallelStream)
				.map(stream -> stream.filter(lt -> !lt.getValue().isEmpty()).count()).orElse((long) 0);

		List<IdentityInfoDTO> rightMiddle = identity.getRightMiddle();
		Long rightMiddleCount = Optional.ofNullable(rightMiddle).map(List::parallelStream)
				.map(stream -> stream.filter(lt -> !lt.getValue().isEmpty()).count()).orElse((long) 0);

		List<IdentityInfoDTO> rightRing = identity.getRightRing();
		Long rightRingCount = Optional.ofNullable(rightRing).map(List::parallelStream)
				.map(stream -> stream.filter(lt -> !lt.getValue().isEmpty()).count()).orElse((long) 0);

		List<IdentityInfoDTO> rightLittle = identity.getRightLittle();
		Long rightLittleCount = Optional.ofNullable(rightLittle).map(List::parallelStream)
				.map(stream -> stream.filter(lt -> !lt.getValue().isEmpty()).count()).orElse((long) 0);

		if (leftThumbCount > 1 || leftIndexCount > 1 || leftMiddleCount > 1 || leftRingCount > 1 || leftLittleCount > 1
				|| rightThumbCount > 1 || rightIndexCount > 1 || rightMiddleCount > 1 || rightRingCount > 1
				|| rightLittleCount > 1) {

			mosipLogger.error(SESSION_ID, ID_AUTH_VALIDATOR, VALIDATE, "Duplicate fingers ");
			errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.DUPLICATE_FINGER.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.DUPLICATE_FINGER.getErrorMessage(), REQUEST));
		}

		Long fingerCountExceeding = leftThumbCount + leftIndexCount + leftMiddleCount + leftRingCount + leftLittleCount
				+ rightThumbCount + rightIndexCount + rightMiddleCount + rightRingCount + rightLittleCount;
		if (fingerCountExceeding > 10) {
			errors.rejectValue(REQUEST, IdAuthenticationErrorConstants.FINGER_EXCEEDING.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.FINGER_EXCEEDING.getErrorMessage(), REQUEST));
		}
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

}
