package io.mosip.registration.util.dataprovider;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import javax.imageio.ImageIO;

import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dto.AuditDTO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.RegistrationMetaDataDTO;
import io.mosip.registration.dto.demographic.ApplicantDocumentDTO;
import io.mosip.registration.exception.RegBaseCheckedException;

public class DataProvider {

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
		DataProvider.getRegistrationMetaDataDTO(registrationDTO.getRegistrationMetaDataDTO());
		return registrationDTO;
	}

	public static void setApplicantDocumentDTO(ApplicantDocumentDTO applicantDocumentDTO, boolean isExceptionPhoto) throws RegBaseCheckedException {
		applicantDocumentDTO.setPhoto(DataProvider.getImageBytes("/applicantPhoto.jpg"));
		applicantDocumentDTO.setPhotographName("ApplicantPhoto.jpg");
		applicantDocumentDTO.setQualityScore(89.0);
		applicantDocumentDTO.setNumRetry(1);
		applicantDocumentDTO.setHasExceptionPhoto(isExceptionPhoto);
		if (isExceptionPhoto) {
			applicantDocumentDTO.setExceptionPhoto(DataProvider.getImageBytes("/applicantPhoto.jpg"));
			applicantDocumentDTO.setExceptionPhotoName("ExceptionPhoto.jpg");
		}
	}

	private static void getRegistrationMetaDataDTO(RegistrationMetaDataDTO registrationMetaDataDTO) {
		registrationMetaDataDTO.setGeoLatitudeLoc(13.0049);
		registrationMetaDataDTO.setGeoLongitudeLoc(80.24492);
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
