package io.mosip.registration.util.dataprovider;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import javax.imageio.ImageIO;

import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dto.AuditDTO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.RegistrationMetaDataDTO;
import io.mosip.registration.dto.biometric.BiometricDTO;
import io.mosip.registration.dto.biometric.BiometricInfoDTO;
import io.mosip.registration.dto.biometric.FingerprintDetailsDTO;
import io.mosip.registration.dto.demographic.ApplicantDocumentDTO;
import io.mosip.registration.dto.demographic.DemographicDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.util.kernal.RIDGenerator;

public class DataProvider {

	public static final String PERMANANENT = "Permananent";
	public static final String THUMB_JPG = "/thumb.jpg";
	private static final String APPLICANT ="applicant";

	private DataProvider() {

	}

	public static byte[] getImageBytes(String filePath) throws RegBaseCheckedException {
		filePath = "/dataprovider".concat(filePath);

		try {
			BufferedImage bufferedImage = ImageIO.read(DataProvider.class.getResourceAsStream(filePath));
			
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			ImageIO.write(bufferedImage, RegistrationConstants.IMAGE_FORMAT, byteArrayOutputStream);

			return byteArrayOutputStream.toByteArray();
		} catch (IOException ioException) {
			throw new RegBaseCheckedException(RegistrationConstants.SERVICE_DATA_PROVIDER_UTIL,
					"Unable to read the Image bytes", ioException);
		}
	}

	public static RegistrationDTO getPacketDTO(RegistrationDTO registrationDTO, String capturePhotoUsingDevice) throws RegBaseCheckedException {
		registrationDTO.setAuditDTOs(DataProvider.getAuditDTOs());
		registrationDTO.setRegistrationMetaDataDTO(DataProvider.getRegistrationMetaDataDTO());
		registrationDTO.setRegistrationId(RIDGenerator.nextRID());

		if(capturePhotoUsingDevice.equals("N")) {
			registrationDTO.setDemographicDTO(DataProvider.getDemographicDTO(registrationDTO.getDemographicDTO()));
		}
		registrationDTO.setBiometricDTO(DataProvider.getBiometricDTO(registrationDTO.getBiometricDTO()));
		return registrationDTO;

	}

	private static BiometricDTO getBiometricDTO(BiometricDTO biometricDTO) throws RegBaseCheckedException {
		biometricDTO.setIntroducerBiometricDTO(DataProvider.buildBioMerticDTO("introducer"));
		return biometricDTO;
	}

	private static BiometricInfoDTO buildBioMerticDTO(String persontype) throws RegBaseCheckedException {
		BiometricInfoDTO biometricInfoDTO = new BiometricInfoDTO();
		biometricInfoDTO.setFingerprintDetailsDTO(DataProvider.getFingerprintDetailsDTO(persontype));
		return biometricInfoDTO;
	}

	private static List<FingerprintDetailsDTO> getFingerprintDetailsDTO(String personType)
			throws RegBaseCheckedException {
		List<FingerprintDetailsDTO> fingerList = new ArrayList<>();

		if (personType.equals(DataProvider.APPLICANT)) {
			fingerList.add(DataProvider.buildFingerPrintDetailsDTO(DataProvider.THUMB_JPG, "BothThumbs.jpg", 85.0, false,
					"BothThumbs", 0));
			fingerList.add(DataProvider.buildFingerPrintDetailsDTO(DataProvider.THUMB_JPG, "LeftPalm.jpg", 80.0, false,
					"LeftPalm", 3));
			fingerList.add(DataProvider.buildFingerPrintDetailsDTO(DataProvider.THUMB_JPG, "RightPalm.jpg", 95.0, false,
					"RightPalm", 2));
		} else {
			fingerList.add(DataProvider.buildFingerPrintDetailsDTO(DataProvider.THUMB_JPG, personType+"LeftThumb.jpg", 0, false,
					"LeftThumb", 0));
		}

		return fingerList;
	}

	private static FingerprintDetailsDTO buildFingerPrintDetailsDTO(String imageLoc, String fingerprintImageName,
			double qualityScore, boolean isForceCaptured, String fingerType, int numRetry)
			throws RegBaseCheckedException {
		FingerprintDetailsDTO fingerprintDetailsDTO = new FingerprintDetailsDTO();
		fingerprintDetailsDTO.setFingerPrint(DataProvider.getImageBytes(imageLoc));
		fingerprintDetailsDTO.setFingerprintImageName(fingerprintImageName);
		fingerprintDetailsDTO.setQualityScore(qualityScore);
		fingerprintDetailsDTO.setForceCaptured(isForceCaptured);
		fingerprintDetailsDTO.setFingerType(fingerType);
		fingerprintDetailsDTO.setNumRetry(numRetry);
		return fingerprintDetailsDTO;
	}

	private static DemographicDTO getDemographicDTO(DemographicDTO demographicDTO) throws RegBaseCheckedException {
		demographicDTO.setApplicantDocumentDTO(DataProvider.setApplicantDocumentDTO(demographicDTO.getApplicantDocumentDTO()));
		return demographicDTO;
	}

	private static ApplicantDocumentDTO setApplicantDocumentDTO(ApplicantDocumentDTO applicantDocumentDTO) throws RegBaseCheckedException {
		applicantDocumentDTO.setPhoto(DataProvider.getImageBytes("/applicantPhoto.jpg"));
		applicantDocumentDTO.setPhotographName("ApplicantPhoto.jpg");
		applicantDocumentDTO.setHasExceptionPhoto(false);
		applicantDocumentDTO.setQualityScore(89.0);
		applicantDocumentDTO.setNumRetry(1);
		return applicantDocumentDTO;
	}

	private static RegistrationMetaDataDTO getRegistrationMetaDataDTO() {

		RegistrationMetaDataDTO registrationMetaDataDTO = new RegistrationMetaDataDTO();
		registrationMetaDataDTO.setRegistrationCategory("New");
		registrationMetaDataDTO.setApplicationType("Child");
		registrationMetaDataDTO.setGeoLatitudeLoc(13.0049);
		registrationMetaDataDTO.setGeoLongitudeLoc(80.24492);
		return registrationMetaDataDTO;
	}

	private static List<AuditDTO> getAuditDTOs() {
		LinkedList<AuditDTO> auditDTOList = new LinkedList<>();

		addAuditDTOToList(auditDTOList, "Capture Demographic Data", "Data Capture", "Caputured demographic data");
		addAuditDTOToList(auditDTOList, "Capture Left Iris", "Iris Capture", "Caputured left iris");
		addAuditDTOToList(auditDTOList, "Capture Right Iris", "Iris Capture", "Caputured right iris");
		addAuditDTOToList(auditDTOList, "Capture Right Palm", "Palm Capture", "Caputured Right Palm");
		addAuditDTOToList(auditDTOList, "Capture Left Palm", "Palm Capture", "Caputured Left Palm");
		addAuditDTOToList(auditDTOList, "Capture Both Thumb", "Thumbs Capture", "Caputured Both Thumb");

		return auditDTOList;
	}

	private static void addAuditDTOToList(List<AuditDTO> auditDTOList, String eventName, String eventType,
			String description) {
		LocalDateTime dateTime = LocalDateTime.now();

		AuditDTO audit = new AuditDTO();

		audit.setUuid(String.valueOf(UUID.randomUUID().getMostSignificantBits()));
		audit.setCreatedAt(dateTime);
		audit.setEventId("1");
		audit.setEventName(eventName);
		audit.setEventType(eventType);
		audit.setActionTimeStamp(dateTime);
		audit.setHostName(RegistrationConstants.LOCALHOST);
		audit.setHostIp(RegistrationConstants.LOCALHOST);
		audit.setApplicationId("1");
		audit.setApplicationName("Registration-UI");
		audit.setSessionUserId("mosip");
		audit.setSessionUserName("mosip");
		audit.setId("1");
		audit.setIdType("application");
		audit.setCreatedBy("mosip");
		audit.setModuleId("1");
		audit.setModuleName("New Registration");
		audit.setDescription(description);
		auditDTOList.add(audit);
	}
}
