package io.mosip.registration.service.bio.impl;

import static io.mosip.registration.constants.LoggerConstants.LOG_REG_FACE_FACADE;
import static io.mosip.registration.constants.LoggerConstants.LOG_REG_FINGERPRINT_FACADE;
import static io.mosip.registration.constants.LoggerConstants.LOG_REG_IRIS_FACADE;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.machinezoo.sourceafis.FingerprintTemplate;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.LoggerConstants;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.device.fp.FingerprintProvider;
import io.mosip.registration.dto.AuthenticationValidatorDTO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.biometric.BiometricDTO;
import io.mosip.registration.dto.biometric.BiometricExceptionDTO;
import io.mosip.registration.dto.biometric.FaceDetailsDTO;
import io.mosip.registration.dto.biometric.FingerprintDetailsDTO;
import io.mosip.registration.dto.biometric.IrisDetailsDTO;
import io.mosip.registration.entity.UserBiometric;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.exception.RegistrationExceptionConstants;
import io.mosip.registration.mdm.dto.CaptureResponsBioDataDto;
import io.mosip.registration.mdm.dto.CaptureResponseBioDto;
import io.mosip.registration.mdm.dto.CaptureResponseDto;
import io.mosip.registration.mdm.service.impl.MosipBioDeviceManager;
import io.mosip.registration.service.BaseService;
import io.mosip.registration.service.bio.BioService;
import io.mosip.registration.service.security.AuthenticationService;

/**
 * This class {@code BioServiceImpl} handles all the biometric captures and
 * validations through MDM service
 * 
 * @author taleev.aalam
 *
 */
@Service
public class BioServiceImpl extends BaseService implements BioService {

	@Autowired
	MosipBioDeviceManager mosipBioDeviceManager;

	@Autowired
	private AuthenticationService authService;

	@Autowired
	private FingerprintProvider fingerprintProvider;

	/**
	 * Instance of {@link Logger}
	 */
	private static final Logger LOGGER = AppConfig.getLogger(BioServiceImpl.class);

	private byte[] isoImage;

	/**
	 * Returns Authentication validator Dto that will be passed 
	 * 
	 * <p>
	 * The method will return fingerPrint validator Dto that will be passed to 
	 * finger print authentication method for fingerPrint validator
	 * </p>.
	 *
	 * @param userId            - the user ID
	 * @return AuthenticationValidatorDTO - authenticationValidatorDto
	 * @throws RegBaseCheckedException - the exception that handles all checked exceptions
	 * @throws IOException             - Exception that may occur while reading the resource
	 */
	@Override
	public AuthenticationValidatorDTO getFingerPrintAuthenticationDto(String userId) throws RegBaseCheckedException, IOException {

		LOGGER.info(LoggerConstants.BIO_SERVICE, APPLICATION_NAME, APPLICATION_ID, "Invoking FingerPrint validator");

		AuthenticationValidatorDTO authenticationValidatorDTO=null;
		CaptureResponseDto captureResponseDto=null;
		CaptureResponsBioDataDto captureResponseData=null;
		String bioType= "Right little";

		if (isMdmEnabled()) {
			captureResponseDto= mosipBioDeviceManager.authScan(RegistrationConstants.FINGERPRINT_SLAB_LEFT);
			if(captureResponseDto==null)
				throw new RegBaseCheckedException("","Decice is not available");
			if(captureResponseDto.getError().getErrorCode().equals("202"))
				throw new RegBaseCheckedException(captureResponseDto.getError().getErrorCode(), captureResponseDto.getError().getErrorInfo());
			if(captureResponseDto.getError().getErrorCode().equals("403"))

			decode(captureResponseDto);
			captureResponseData =  captureResponseDto.getMosipBioDeviceDataResponses().get(0).getCaptureResponseData();
			bioType=captureResponseData.getBioType();
			isoImage = Base64.getDecoder().decode(captureResponseData.getBioExtract());
		} else {
			isoImage = IOUtils.toByteArray(
					this.getClass().getResourceAsStream("/UserOnboard/rightHand/rightLittle/ISOTemplate.iso"));
		}

		if (isoImage == null) {
			return null;
		} else {
			LOGGER.info(LoggerConstants.BIO_SERVICE, APPLICATION_NAME, APPLICATION_ID,
					"Calling for finger print validation through authService");

			authenticationValidatorDTO = new AuthenticationValidatorDTO();
			List<FingerprintDetailsDTO> fingerprintDetailsDTOs = new ArrayList<>();
			FingerprintDetailsDTO fingerprintDetailsDTO = new FingerprintDetailsDTO();
			fingerprintDetailsDTO.setFingerPrintISOImage(isoImage);
			fingerprintDetailsDTO.setFingerType(bioType);
			fingerprintDetailsDTOs.add(fingerprintDetailsDTO);
			authenticationValidatorDTO.setFingerPrintDetails(fingerprintDetailsDTOs);
			authenticationValidatorDTO.setUserId(userId);
			authenticationValidatorDTO.setAuthValidationType(RegistrationConstants.VALIDATION_TYPE_FP_SINGLE);
			validateFingerPrint(authenticationValidatorDTO);
		}
		LOGGER.info(LoggerConstants.BIO_SERVICE, APPLICATION_NAME, APPLICATION_ID, "End FingerPrint validator");

		return authenticationValidatorDTO;
	}

	/**
	 * Validates FingerPrint after getting the scanned data for the particular given
	 * User id
	 * 
	 * <p>
	 * The MDM service will be triggered to capture the user fingerprint which will
	 * be validated against the stored fingerprint from the DB
	 * </p>.
	 *
	 * @param authenticationValidatorDTO            - authenticationValidatorDto
	 * @return boolean the validation result. <code>true</code> if match is found,
	 *         else <code>false</code>
	 */
	public boolean validateFingerPrint(AuthenticationValidatorDTO authenticationValidatorDTO) {
		return authService.authValidator(RegistrationConstants.FINGERPRINT,
				authenticationValidatorDTO);
	}

	/**
	 * Returns Authentication validator Dto that will be passed 
	 * 
	 * <p>
	 * The method will return iris validator Dto that will be passed to 
	 * finger print authentication method for iris validator
	 * </p>.
	 *
	 * @param userId            - the user ID
	 * @return AuthenticationValidatorDTO - authenticationValidatorDto
	 * @throws RegBaseCheckedException - the exception that handles all checked exceptions
	 * @throws IOException             - Exception that may occur while reading the resource
	 */
	@Override
	public AuthenticationValidatorDTO getIrisAuthenticationDto(String userId) throws RegBaseCheckedException, IOException {

		LOGGER.info(LoggerConstants.BIO_SERVICE, APPLICATION_NAME, APPLICATION_ID, "Scanning Iris");

		AuthenticationValidatorDTO authenticationValidatorDTO = new AuthenticationValidatorDTO();
		List<IrisDetailsDTO> irisDetailsDTOs = new ArrayList<>();
		IrisDetailsDTO irisDetailsDTO = new IrisDetailsDTO();
		irisDetailsDTO.setIris(captureIris());
		irisDetailsDTOs.add(irisDetailsDTO);
		authenticationValidatorDTO.setUserId(userId);
		authenticationValidatorDTO.setIrisDetails(irisDetailsDTOs);

		LOGGER.info(LoggerConstants.BIO_SERVICE, APPLICATION_NAME, APPLICATION_ID, "Iris scan done");
		return authenticationValidatorDTO;
		
	}

	/**
	 * Validates Iris after getting the scanned data for the given user ID
	 * 
	 * *
	 * <p>
	 * The MDM service will be triggered to capture the user Iris data which will be
	 * validated against the stored iris from the DB through auth validator service
	 * </p>.
	 *
	 * @param authenticationValidatorDTO            - authenticationValidtorDto
	 * @return boolean the validation result. <code>true</code> if match is found,
	 *         else <code>false</code>
	 */
	public boolean validateIris(AuthenticationValidatorDTO authenticationValidatorDTO) {
		return authService.authValidator(RegistrationConstants.IRIS, authenticationValidatorDTO);
	}

	/**
	 * Gets the finger print image as DTO with MDM
	 *
	 * @param fpDetailsDTO
	 *            the fp details DTO
	 * @param fingerType
	 *            the finger type
	 * @throws RegBaseCheckedException
	 *             the reg base checked exception
	 * @throws IOException 
	 */
	public void getFingerPrintImageAsDTOWithMdm(FingerprintDetailsDTO fpDetailsDTO, String fingerType)
			throws RegBaseCheckedException, IOException {
		CaptureResponseDto captureResponseDto = mosipBioDeviceManager.scan(fingerType);
		if(captureResponseDto==null)
			throw new RegBaseCheckedException("","Decice is not available");
		if(captureResponseDto.getError().getErrorCode().equals("202"))
			throw new RegBaseCheckedException(captureResponseDto.getError().getErrorCode(), captureResponseDto.getError().getErrorInfo());
		if(captureResponseDto.getError().getErrorCode().equals("403"))
			throw new RegBaseCheckedException(captureResponseDto.getError().getErrorCode(), captureResponseDto.getError().getErrorInfo());
		fpDetailsDTO.setSegmentedFingerprints(new ArrayList<FingerprintDetailsDTO>());
		decode(captureResponseDto);
		List<CaptureResponseBioDto> mosipBioDeviceDataResponses = captureResponseDto.getMosipBioDeviceDataResponses();
		mosipBioDeviceDataResponses.forEach(captured->{
			FingerprintDetailsDTO fingerPrintDetail = new FingerprintDetailsDTO();
			CaptureResponsBioDataDto captureRespoonse = captured.getCaptureResponseData();
			fingerPrintDetail.setFingerPrintISOImage(Base64.getDecoder().decode(captureRespoonse.getBioExtract()));
			fingerPrintDetail.setFingerType(captureRespoonse.getBioSubType());
			fingerPrintDetail.setFingerPrint(captureRespoonse.getBioValue());
			fingerPrintDetail.setQualityScore(Integer.parseInt(captureRespoonse.getQualityScore()));
			fingerPrintDetail.setFingerprintImageName("FingerPrint "+captureRespoonse.getBioSubType());
			fpDetailsDTO.getSegmentedFingerprints().add(fingerPrintDetail);
		});
		double slapQuality = (fpDetailsDTO.getSegmentedFingerprints().stream().mapToDouble(finger->finger.getQualityScore()).sum())/fpDetailsDTO.getSegmentedFingerprints().size();
		fpDetailsDTO.setCaptured(true);
		fpDetailsDTO.setFingerType(fingerType);
		fpDetailsDTO.setQualityScore(slapQuality);
	}

	private void decode(CaptureResponseDto mosipBioCaptureResponseDto) throws IOException, JsonParseException, JsonMappingException {
		ObjectMapper mapper = new ObjectMapper();
        for (CaptureResponseBioDto captureResponseBioDto : mosipBioCaptureResponseDto.getMosipBioDeviceDataResponses()) {
         if (null != captureResponseBioDto) {
               String bioJson = new String(Base64.getDecoder().decode(captureResponseBioDto.getCaptureBioData()));
               if (null != bioJson) {
                      CaptureResponsBioDataDto captureResponsBioDataDto = mapper.readValue(bioJson.getBytes(),
                                   CaptureResponsBioDataDto.class);
                      captureResponseBioDto.setCaptureResponseData(captureResponsBioDataDto);
               }
         }
        }
	}


	/**
	 * Gets the finger print image as DTO without MDM.
	 *
	 * @param fpDetailsDTO
	 *            the fp details DTO
	 * @param fingerType
	 *            the finger type
	 * @throws RegBaseCheckedException
	 *             the reg base checked exception
	 */
	private void getFingerPrintImageAsDTONonMdm(FingerprintDetailsDTO fpDetailsDTO, String fingerType)
			throws RegBaseCheckedException {
		Map<String, Object> fingerMap = null;

		try {
			// TODO : Currently stubbing the data. once we have the device, we
			// can remove
			// this.

			if (fingerType.equals(RegistrationConstants.FINGERPRINT_SLAB_LEFT)) {
				fingerMap = getFingerPrintScannedImageWithStub(RegistrationConstants.LEFTHAND_SLAP_FINGERPRINT_PATH);
			} else if (fingerType.equals(RegistrationConstants.FINGERPRINT_SLAB_RIGHT)) {
				fingerMap = getFingerPrintScannedImageWithStub(RegistrationConstants.RIGHTHAND_SLAP_FINGERPRINT_PATH);
			} else if (fingerType.equals(RegistrationConstants.FINGERPRINT_SLAB_THUMBS)) {
				fingerMap = getFingerPrintScannedImageWithStub(RegistrationConstants.BOTH_THUMBS_FINGERPRINT_PATH);
			}

			if ((fingerMap != null)
					&& ((boolean) SessionContext.map().get(RegistrationConstants.ONBOARD_USER) || (fpDetailsDTO
							.getQualityScore() < (double) fingerMap.get(RegistrationConstants.IMAGE_SCORE_KEY)))) {
				fpDetailsDTO.setFingerPrint((byte[]) fingerMap.get(RegistrationConstants.IMAGE_BYTE_ARRAY_KEY));
				fpDetailsDTO.setFingerprintImageName(fingerType.concat(RegistrationConstants.DOT)
						.concat((String) fingerMap.get(RegistrationConstants.IMAGE_FORMAT_KEY)));
				fpDetailsDTO.setFingerType(fingerType);
				fpDetailsDTO.setForceCaptured(false);
				if (!(boolean) SessionContext.map().get(RegistrationConstants.ONBOARD_USER)) {
					fpDetailsDTO.setQualityScore((double) fingerMap.get(RegistrationConstants.IMAGE_SCORE_KEY));
				}
			}

		} finally {
			if (fingerMap != null && !fingerMap.isEmpty())
				fingerMap.clear();
		}
	}

	/**
	 * Stub method to get the finger print scanned image from local hard disk. Once
	 * SDK and device avilable then we can remove it.
	 *
	 * @param path
	 *            the path
	 * @return the finger print scanned image
	 * @throws RegBaseCheckedException
	 *             the reg base checked exception
	 */
	private Map<String, Object> getFingerPrintScannedImageWithStub(String path) throws RegBaseCheckedException {
		try {
			LOGGER.info(LOG_REG_FINGERPRINT_FACADE, APPLICATION_NAME, APPLICATION_ID,
					"Scanning of fingerprints details for user registration");

			BufferedImage bufferedImage = ImageIO.read(this.getClass().getResourceAsStream(path));

			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			ImageIO.write(bufferedImage, "jpeg", byteArrayOutputStream);

			byte[] scannedFingerPrintBytes = byteArrayOutputStream.toByteArray();

			// Add image format, image and quality score in bytes array to map
			Map<String, Object> scannedFingerPrints = new WeakHashMap<>();
			scannedFingerPrints.put(RegistrationConstants.IMAGE_FORMAT_KEY, "jpg");
			scannedFingerPrints.put(RegistrationConstants.IMAGE_BYTE_ARRAY_KEY, scannedFingerPrintBytes);
			if (!(boolean) SessionContext.map().get(RegistrationConstants.ONBOARD_USER)) {
				if (path.contains(RegistrationConstants.THUMBS)) {
					scannedFingerPrints.put(RegistrationConstants.IMAGE_SCORE_KEY, 90.0);
				} else if (path.contains(RegistrationConstants.LEFTPALM)) {
					scannedFingerPrints.put(RegistrationConstants.IMAGE_SCORE_KEY, 85.0);
				} else if (path.contains(RegistrationConstants.RIGHTPALM)) {
					scannedFingerPrints.put(RegistrationConstants.IMAGE_SCORE_KEY, 90.0);
				}
			}

			LOGGER.info(LOG_REG_FINGERPRINT_FACADE, APPLICATION_NAME, APPLICATION_ID,
					"Scanning of fingerprints details for user registration completed");

			return scannedFingerPrints;
		} catch (IOException ioException) {
			throw new RegBaseCheckedException(
					RegistrationExceptionConstants.REG_FINGERPRINT_SCANNING_ERROR.getErrorCode(),
					RegistrationExceptionConstants.REG_FINGERPRINT_SCANNING_ERROR.getErrorMessage());
		} catch (RuntimeException runtimeException) {
			LOGGER.error(LOG_REG_FINGERPRINT_FACADE, APPLICATION_NAME, APPLICATION_ID,
					String.format(
							"Exception while scanning fingerprints details for user registration: %s caused by %s",
							runtimeException.getMessage(), runtimeException.getCause()));

			throw new RegBaseUncheckedException(RegistrationConstants.USER_REG_FINGERPRINT_SCAN_EXP,
					String.format(
							"Exception while scanning fingerprints details for user registration: %s caused by %s",
							runtimeException.getMessage(), runtimeException.getCause()));
		}
	}

	/**
	 * Gets the finger print image as DTO from the MDM service based on the
	 * fingerType
	 *
	 *
	 * @param fpDetailsDTO
	 *            the fp details DTO
	 * @param fingerType
	 *            the finger type
	 * @throws RegBaseCheckedException
	 *             the reg base checked exception
	 * @throws IOException 
	 */
	public void getFingerPrintImageAsDTO(FingerprintDetailsDTO fpDetailsDTO, String fingerType)
			throws RegBaseCheckedException, IOException {

		if (isMdmEnabled())
			getFingerPrintImageAsDTOWithMdm(fpDetailsDTO, fingerType);
		else
			getFingerPrintImageAsDTONonMdm(fpDetailsDTO, fingerType);
	}

	/**
	 * checks if the MDM service is enabled
	 * 
	 * @return boolean the validation result. <code>true</code> if match is found,
	 *         else <code>false</code>
	 */
	@Override
	public boolean isMdmEnabled() {
		return RegistrationConstants.ENABLE
				.equalsIgnoreCase(((String) ApplicationContext.map().get(RegistrationConstants.MDM_ENABLED)));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.service.bio.BioService#segmentFingerPrintImage(io.mosip
	 * .registration.dto.biometric.FingerprintDetailsDTO, java.lang.String[],
	 * java.lang.String)
	 */
	@Override
	public void segmentFingerPrintImage(FingerprintDetailsDTO fingerprintDetailsDTO, String[] filePath,
			String fingerType) throws RegBaseCheckedException {

		readSegmentedFingerPrintsSTUB(fingerprintDetailsDTO, filePath, fingerType);

	}

	/**
	 * {@code readFingerPrints} is to read the scanned fingerprints.
	 *
	 * @param fingerprintDetailsDTO
	 *            the fingerprint details DTO
	 * @param path
	 *            the path
	 * @throws RegBaseCheckedException
	 *             the reg base checked exception
	 */
	private void readSegmentedFingerPrintsSTUB(FingerprintDetailsDTO fingerprintDetailsDTO, String[] path,
			String fingerType) throws RegBaseCheckedException {
		LOGGER.info(LOG_REG_FINGERPRINT_FACADE, APPLICATION_NAME, APPLICATION_ID, "Reading scanned Finger has started");

		try {

			List<BiometricExceptionDTO> biometricExceptionDTOs;

			if ((boolean) SessionContext.map().get(RegistrationConstants.ONBOARD_USER)) {
				biometricExceptionDTOs = ((BiometricDTO) SessionContext.map()
						.get(RegistrationConstants.USER_ONBOARD_DATA)).getOperatorBiometricDTO()
								.getBiometricExceptionDTO();
			} else if (((RegistrationDTO) SessionContext.map().get(RegistrationConstants.REGISTRATION_DATA))
					.isUpdateUINNonBiometric() || (boolean) SessionContext.map().get(RegistrationConstants.IS_Child)) {
				biometricExceptionDTOs = ((RegistrationDTO) SessionContext.map()
						.get(RegistrationConstants.REGISTRATION_DATA)).getBiometricDTO().getIntroducerBiometricDTO()
								.getBiometricExceptionDTO();
			} else {
				biometricExceptionDTOs = ((RegistrationDTO) SessionContext.map()
						.get(RegistrationConstants.REGISTRATION_DATA)).getBiometricDTO().getApplicantBiometricDTO()
								.getBiometricExceptionDTO();
			}

			if (!isMdmEnabled()) {

				prepareSegmentedBiometrics(fingerprintDetailsDTO, path, biometricExceptionDTOs);
			}

		} catch (IOException ioException) {
			throw new RegBaseCheckedException(
					RegistrationExceptionConstants.REG_FINGERPRINT_SCANNING_ERROR.getErrorCode(),
					RegistrationExceptionConstants.REG_FINGERPRINT_SCANNING_ERROR.getErrorMessage());
		} catch (RuntimeException runtimeException) {
			LOGGER.error(LOG_REG_FINGERPRINT_FACADE, APPLICATION_NAME, APPLICATION_ID, String.format(
					"Exception while reading scanned fingerprints details for user registration: %s caused by %s",
					runtimeException.getMessage(), runtimeException.getCause()));

			throw new RegBaseUncheckedException(RegistrationConstants.USER_REG_FINGERPRINT_SCAN_EXP, String.format(
					"Exception while reading scanned fingerprints details for user registration: %s caused by %s",
					runtimeException.getMessage(), runtimeException.getCause()));
		}
		LOGGER.info(LOG_REG_FINGERPRINT_FACADE, APPLICATION_NAME, APPLICATION_ID, "Reading scanned Finger has ended");
	}

	/**
	 * Preparing segmentation detail of Biometric
	 * 
	 * @param fingerprintDetailsDTO
	 * @param path
	 * @param biometricExceptionDTOs
	 * @throws IOException
	 */
	private void prepareSegmentedBiometrics(FingerprintDetailsDTO fingerprintDetailsDTO, String[] path,
			List<BiometricExceptionDTO> biometricExceptionDTOs) throws IOException {
		List<String> filePaths = Arrays.asList(path);

		boolean isExceptionFinger = false;

		for (String folderPath : filePaths) {
			isExceptionFinger = false;
			String[] imageFileName = folderPath.split("/");

			for (BiometricExceptionDTO exceptionDTO : biometricExceptionDTOs) {

				if (imageFileName[3].equals(exceptionDTO.getMissingBiometric())) {
					isExceptionFinger = true;
					break;
				}
			}
			if (!isExceptionFinger) {
				FingerprintDetailsDTO segmentedDetailsDTO = new FingerprintDetailsDTO();

				byte[] isoTemplateBytes = IOUtils
						.resourceToByteArray(folderPath.concat(RegistrationConstants.ISO_FILE));
				segmentedDetailsDTO.setFingerPrint(isoTemplateBytes);

				byte[] isoImageBytes = IOUtils
						.resourceToByteArray(folderPath.concat(RegistrationConstants.ISO_IMAGE_FILE));
				segmentedDetailsDTO.setFingerPrintISOImage(isoImageBytes);

				segmentedDetailsDTO.setFingerType(imageFileName[3]);
				segmentedDetailsDTO.setFingerprintImageName(imageFileName[3]);
				segmentedDetailsDTO.setNumRetry(fingerprintDetailsDTO.getNumRetry());
				segmentedDetailsDTO.setForceCaptured(false);
				segmentedDetailsDTO.setQualityScore(90);

				if (fingerprintDetailsDTO.getSegmentedFingerprints() == null) {
					List<FingerprintDetailsDTO> segmentedFingerprints = new ArrayList<>(5);
					fingerprintDetailsDTO.setSegmentedFingerprints(segmentedFingerprints);
				}
				fingerprintDetailsDTO.getSegmentedFingerprints().add(segmentedDetailsDTO);
			}
		}
	}

	/**
	 * Capture Iris
	 * 
	 * @return byte[] of captured Iris
	 * @throws IOException
	 */
	private byte[] captureIris() throws RegBaseCheckedException, IOException {

		LOGGER.info(LOG_REG_IRIS_FACADE, APPLICATION_NAME, APPLICATION_ID, "Stub data for Iris");

		byte[] capturedByte = null;
		BufferedImage bufferedImage=null;

		if (isMdmEnabled()) {
			CaptureResponseDto captureResponseDto = mosipBioDeviceManager.scan(RegistrationConstants.IRIS_SINGLE);
			capturedByte = mosipBioDeviceManager.getSingleBioValue(captureResponseDto);
			return capturedByte;
		} else
			bufferedImage = ImageIO
					.read(this.getClass().getResourceAsStream(RegistrationConstants.IRIS_IMAGE_LOCAL));
		
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ImageIO.write(bufferedImage, RegistrationConstants.IMAGE_FORMAT_PNG, byteArrayOutputStream);

		capturedByte = byteArrayOutputStream.toByteArray();

		return capturedByte;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.service.bio.BioService#validateFace(java.lang.String)
	 */
	@Override
	public boolean validateFace(AuthenticationValidatorDTO authenticationValidatorDTO) {

		LOGGER.info(LoggerConstants.BIO_SERVICE, APPLICATION_NAME, APPLICATION_ID, "Authenticating face");

		return authService.authValidator(RegistrationConstants.FACE, authenticationValidatorDTO);
	}

	public AuthenticationValidatorDTO getFaceAuthenticationDto(String userId) {
		AuthenticationValidatorDTO authenticationValidatorDTO = new AuthenticationValidatorDTO();
		FaceDetailsDTO faceDetailsDTO = new FaceDetailsDTO();
		CaptureResponseDto captureResponseDto = captureFace();
		byte[] faceBytes = mosipBioDeviceManager.getSingleBioValue(captureResponseDto);
		if (null != faceBytes) {
			faceDetailsDTO.setFace(faceBytes);
		} else {
			faceDetailsDTO.setFace(RegistrationConstants.FACE.toLowerCase().getBytes());
		}
		authenticationValidatorDTO.setUserId(userId);
		authenticationValidatorDTO.setFaceDetail(faceDetailsDTO);
		return authenticationValidatorDTO;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.bio.BioService#getIrisImageAsDTO(io.mosip.
	 * registration.dto.biometric.IrisDetailsDTO, java.lang.String)
	 */
	@Override
	public void getIrisImageAsDTO(IrisDetailsDTO irisDetailsDTO, String irisType) throws RegBaseCheckedException, IOException {

		if (RegistrationConstants.ENABLE
				.equalsIgnoreCase(((String) ApplicationContext.map().get(RegistrationConstants.MDM_ENABLED))))
			getIrisImageAsDTOWithMdm(irisDetailsDTO, irisType);
		else
			getIrisImageAsDTONonMdm(irisDetailsDTO, irisType);
	}

	/**
	 * Get the Iris Image with MDM
	 * 
	 * @param detailsDTO
	 * @param eyeType
	 * @throws RegBaseCheckedException
	 * @throws IOException 
	 */
	private void getIrisImageAsDTOWithMdm(IrisDetailsDTO detailsDTO, String eyeType) throws RegBaseCheckedException, IOException {

		String type = eyeType;
		switch (eyeType) {
		case RegistrationConstants.LEFT + RegistrationConstants.EYE:
			eyeType = RegistrationConstants.IRIS_SINGLE;
			detailsDTO.setIrisImageName(RegistrationConstants.LEFT + RegistrationConstants.EYE);
			break;
		case RegistrationConstants.RIGHT + RegistrationConstants.EYE:
			eyeType = RegistrationConstants.IRIS_SINGLE;
			detailsDTO.setIrisImageName(RegistrationConstants.RIGHT + RegistrationConstants.EYE);
			break;
		case RegistrationConstants.IRIS_DOUBLE:
			eyeType = RegistrationConstants.IRIS_DOUBLE;
			detailsDTO.setIrisImageName(RegistrationConstants.IRIS_DOUBLE);
			break;

		default:
			break;

		}

		CaptureResponseDto captureResponseDto = mosipBioDeviceManager.scan(eyeType);
		byte[] irisByte = mosipBioDeviceManager.getSingleBioValue(captureResponseDto);
		detailsDTO.setIris(irisByte);
		detailsDTO.setIrisType(type);
		detailsDTO.setQualityScore(80);

	}

	/**
	 * Gets the iris stub image as DTO without MDM
	 *
	 * @param irisDetailsDTO
	 *            the iris details DTO
	 * @param irisType
	 *            the iris type
	 * @throws RegBaseCheckedException
	 *             the reg base checked exception
	 */
	private void getIrisImageAsDTONonMdm(IrisDetailsDTO irisDetailsDTO, String irisType)
			throws RegBaseCheckedException {
		try {
			LOGGER.info(LOG_REG_IRIS_FACADE, APPLICATION_NAME, APPLICATION_ID,
					"Stubbing iris details for user registration");

			Map<String, Object> scannedIrisMap = getIrisScannedImage(irisType);
			double qualityScore = 0;
			if (!(boolean) SessionContext.map().get(RegistrationConstants.ONBOARD_USER)) {
				qualityScore = (double) scannedIrisMap.get(RegistrationConstants.IMAGE_SCORE_KEY);
			}

			if ((boolean) SessionContext.map().get(RegistrationConstants.ONBOARD_USER)
					|| Double.compare(irisDetailsDTO.getQualityScore(), qualityScore) < 0) {
				// Set the values in IrisDetailsDTO object
				irisDetailsDTO.setIris((byte[]) scannedIrisMap.get(RegistrationConstants.IMAGE_BYTE_ARRAY_KEY));
				irisDetailsDTO.setForceCaptured(false);
				irisDetailsDTO.setIrisImageName(irisType.concat(RegistrationConstants.DOT)
						.concat((String) scannedIrisMap.get(RegistrationConstants.IMAGE_FORMAT_KEY)));
				irisDetailsDTO.setIrisType(irisType);
				if (!(boolean) SessionContext.map().get(RegistrationConstants.ONBOARD_USER)) {
					irisDetailsDTO.setQualityScore(qualityScore);
				}
				if (irisDetailsDTO.getNumOfIrisRetry() > 1) {
					irisDetailsDTO.setQualityScore(91.0);
				}
			}

			LOGGER.info(LOG_REG_IRIS_FACADE, APPLICATION_NAME, APPLICATION_ID,
					"Stubbing iris details for user registration completed");
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegistrationConstants.USER_REG_IRIS_SCAN_EXP,
					String.format("Exception while stubbing the iris details for user registration: %s caused by %s",
							runtimeException.getMessage(), runtimeException.getCause()));
		}
	}

	private Map<String, Object> getIrisScannedImage(String irisType) throws RegBaseCheckedException {
		try {
			LOGGER.info(LOG_REG_IRIS_FACADE, APPLICATION_NAME, APPLICATION_ID,
					"Scanning of iris details for user registration");

			double qualityScore;
			BufferedImage bufferedImage;
			if (irisType.equalsIgnoreCase(RegistrationConstants.TEMPLATE_LEFT_EYE)) {
				bufferedImage = ImageIO
						.read(this.getClass().getResourceAsStream(RegistrationConstants.IRIS_IMAGE_LOCAL));
				qualityScore = 90.5;
			} else {
				bufferedImage = ImageIO
						.read(this.getClass().getResourceAsStream(RegistrationConstants.IRIS_IMAGE_LOCAL_RIGHT));
				qualityScore = 50.0;
			}

			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			ImageIO.write(bufferedImage, RegistrationConstants.IMAGE_FORMAT_PNG, byteArrayOutputStream);

			byte[] scannedIrisBytes = byteArrayOutputStream.toByteArray();

			// Add image format, image and quality score in bytes array to map
			Map<String, Object> scannedIris = new WeakHashMap<>();
			scannedIris.put(RegistrationConstants.IMAGE_FORMAT_KEY, RegistrationConstants.IMAGE_FORMAT_PNG);
			scannedIris.put(RegistrationConstants.IMAGE_BYTE_ARRAY_KEY, scannedIrisBytes);
			if (!(boolean) SessionContext.map().get(RegistrationConstants.ONBOARD_USER)) {
				scannedIris.put(RegistrationConstants.IMAGE_SCORE_KEY, qualityScore);
			}

			LOGGER.info(LOG_REG_IRIS_FACADE, APPLICATION_NAME, APPLICATION_ID,
					"Scanning of iris details for user registration completed");

			return scannedIris;
		} catch (IOException ioException) {
			throw new RegBaseCheckedException(RegistrationExceptionConstants.REG_IRIS_SCANNING_ERROR.getErrorCode(),
					RegistrationExceptionConstants.REG_IRIS_SCANNING_ERROR.getErrorMessage());
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegistrationConstants.USER_REG_IRIS_STUB_IMAGE_EXP,
					String.format("Exception while scanning iris details for user registration: %s caused by %s",
							runtimeException.getMessage(), runtimeException.getCause()));
		}
	}

	/**
	 * Capture Face
	 * 
	 * @return byte[] of captured Face
	 */
	@Override
	public CaptureResponseDto captureFace() {

		LOGGER.info(LOG_REG_IRIS_FACADE, APPLICATION_NAME, APPLICATION_ID, "Stub data for Face");
		CaptureResponseDto captureResponseDto = null;
		try {
			if (isMdmEnabled()) {

				captureResponseDto = mosipBioDeviceManager.scan(RegistrationConstants.FACE);
			}
		} catch (RegBaseCheckedException | RuntimeException | IOException exception) {
			exception.printStackTrace();

		}
		return captureResponseDto;
	}

	@Override
	public byte[] getSingleBioValue(CaptureResponseDto captureResponseDto) {

		return mosipBioDeviceManager.getSingleBioValue(captureResponseDto);
	}

	@Override
	public byte[] getSingleBiometricIsoTemplate(CaptureResponseDto captureResponseDto) {

		return mosipBioDeviceManager.getSingleBiometricIsoTemplate(captureResponseDto);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.service.bio.BioService#validateFP(io.mosip.registration
	 * .dto.biometric.FingerprintDetailsDTO, java.util.List)
	 */
	@Override
	public boolean validateFP(FingerprintDetailsDTO fingerprintDetailsDTO, List<UserBiometric> userFingerprintDetails) {
		FingerprintTemplate fingerprintTemplate = new FingerprintTemplate()
				.convert(fingerprintDetailsDTO.getFingerPrint());
		String minutiae = fingerprintTemplate.serialize();
		int fingerPrintScore = Integer
				.parseInt(String.valueOf(ApplicationContext.map().get(RegistrationConstants.FINGER_PRINT_SCORE)));
		userFingerprintDetails.forEach(fingerPrintTemplateEach -> {
			if (fingerprintProvider.scoreCalculator(minutiae,
					fingerPrintTemplateEach.getBioMinutia()) > fingerPrintScore) {
				fingerprintDetailsDTO.setFingerType(fingerPrintTemplateEach.getUserBiometricId().getBioAttributeCode());
			}
		});
		return userFingerprintDetails.stream()
				.anyMatch(bio -> fingerprintProvider.scoreCalculator(minutiae, bio.getBioMinutia()) > fingerPrintScore);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.service.bio.BioService#validateIrisAgainstDb(io.mosip.
	 * registration.dto.biometric.IrisDetailsDTO, java.util.List)
	 */
	@Override
	public boolean validateIrisAgainstDb(IrisDetailsDTO irisDetailsDTO, List<UserBiometric> userIrisDetails) {

		LOGGER.info(LOG_REG_IRIS_FACADE, APPLICATION_NAME, APPLICATION_ID,
				"Validating iris details for user registration");

		userIrisDetails.forEach(
				irisEach -> irisDetailsDTO.setIrisType(irisEach.getUserBiometricId().getBioAttributeCode() + ".jpg"));
		
		return userIrisDetails.stream()
				.anyMatch(iris -> Arrays.equals(irisDetailsDTO.getIris(), iris.getBioIsoImage()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.service.bio.BioService#validateFaceAgainstDb(io.mosip.
	 * registration.dto.biometric.FaceDetailsDTO, java.util.List)
	 */
	@Override
	public boolean validateFaceAgainstDb(FaceDetailsDTO faceDetail, List<UserBiometric> userFaceDetails) {

		LOGGER.info(LOG_REG_FACE_FACADE, APPLICATION_NAME, APPLICATION_ID,
				"Stubbing face details for user registration");

		return userFaceDetails.stream().anyMatch(face -> Arrays.equals(faceDetail.getFace(), face.getBioIsoImage()));
	}

}
