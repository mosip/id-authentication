package org.mosip.registration.util.dataprovider;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.mosip.registration.constants.RegConstants;
import org.mosip.registration.constants.RegProcessorExceptionCode;
import org.mosip.registration.dto.AuditDTO;
import org.mosip.registration.dto.OSIDataDTO;
import org.mosip.registration.dto.RegistrationDTO;
import org.mosip.registration.dto.RegistrationMetaDataDTO;
import org.mosip.registration.dto.biometric.BiometricDTO;
import org.mosip.registration.dto.biometric.BiometricInfoDTO;
import org.mosip.registration.dto.biometric.ExceptionFingerprintDetailsDTO;
import org.mosip.registration.dto.biometric.ExceptionIrisDetailsDTO;
import org.mosip.registration.dto.biometric.FingerprintDetailsDTO;
import org.mosip.registration.dto.biometric.IrisDetailsDTO;
import org.mosip.registration.dto.demographic.AddressDTO;
import org.mosip.registration.dto.demographic.ApplicantDocumentDTO;
import org.mosip.registration.dto.demographic.DemographicDTO;
import org.mosip.registration.dto.demographic.DemographicInfoDTO;
import org.mosip.registration.dto.demographic.DocumentDetailsDTO;
import org.mosip.registration.exception.RegBaseCheckedException;
import org.mosip.registration.util.kernal.RIDGenerator;

public class DataProvider {

	public static final String PERMANANENT = "Permananent";
	public static final String THUMB_JPG = "/thumb.jpg";

	private DataProvider() {

	}

	public static byte[] getImageBytes(String filePath) throws RegBaseCheckedException {
		filePath = "/dataprovider".concat(filePath);

		try {
			return Files.readAllBytes(Paths.get(filePath));
		} catch (IOException ioException) {
			throw new RegBaseCheckedException(RegProcessorExceptionCode.SERVICE_DATA_PROVIDER_UTIL,
					"Unable to read the Image bytes", ioException);
		}
	}

	public static RegistrationDTO getPacketDTO() throws RegBaseCheckedException {
		RegistrationDTO registrationDTO = new RegistrationDTO();
		registrationDTO.setAuditDTOs(DataProvider.getAuditDTOs());
		registrationDTO.setOsiDataDTO(DataProvider.getOsiDataDTO());
		registrationDTO.setRegistrationMetaDataDTO(DataProvider.getPacketMetaDataDTO());
		registrationDTO.setPreRegistrationId("PEN1345T");
		registrationDTO.setRegistrationId(RIDGenerator.nextRID());

		registrationDTO.setDemographicDTO(DataProvider.getDemographicDTO());
		registrationDTO.setBiometricDTO(DataProvider.getBiometricDTO());
		return registrationDTO;

	}

	private static BiometricDTO getBiometricDTO() throws RegBaseCheckedException {
		BiometricDTO biometricDTO = new BiometricDTO();
		biometricDTO.setApplicantBiometricDTO(DataProvider.getApplicantBiometricDTO("applicant"));
		biometricDTO.setHofBiometricDTO(DataProvider.getHofBiometricDTO("hof"));
		biometricDTO.setIntroducerBiometricDTO(DataProvider.getIntroducerBiometricDTO("introducer"));
		biometricDTO.setSupervisorBiometricDTO(DataProvider.getSupervisorBiometricDTO("supervisor"));
		biometricDTO.setOperatorBiometricDTO(DataProvider.getOperatorBiometricDTO("operator"));
		return biometricDTO;
	}

	private static BiometricInfoDTO getHofBiometricDTO(String persontype) throws RegBaseCheckedException {
		return buildBioMerticDTO(persontype);
	}

	private static BiometricInfoDTO getIntroducerBiometricDTO(String persontype) throws RegBaseCheckedException {
		return buildBioMerticDTO(persontype);
	}

	private static BiometricInfoDTO getSupervisorBiometricDTO(String persontype) throws RegBaseCheckedException {
		return buildBioMerticDTO(persontype);
	}

	private static BiometricInfoDTO buildBioMerticDTO(String persontype) throws RegBaseCheckedException {
		BiometricInfoDTO biometricInfoDTO = new BiometricInfoDTO();
		biometricInfoDTO.setFingerprintDetailsDTO(DataProvider.getFingerprintDetailsDTO(persontype));
		biometricInfoDTO.setNumOfFingerPrintRetry(4);
		biometricInfoDTO.setExceptionFingerprintDetailsDTO(DataProvider.getExceptionFingerprintDetailsDTO(persontype));
		biometricInfoDTO.setIrisDetailsDTO(DataProvider.getIrisDetailsDTO(persontype));
		biometricInfoDTO.setNumOfIrisRetry(2);
		biometricInfoDTO.setExceptionIrisDetailsDTO(DataProvider.getExceptionIrisDetailsDTO(persontype));
		return biometricInfoDTO;
	}

	private static BiometricInfoDTO getOperatorBiometricDTO(String persontype) throws RegBaseCheckedException {
		return buildBioMerticDTO(persontype);
	}

	private static BiometricInfoDTO getApplicantBiometricDTO(String personType) throws RegBaseCheckedException {
		return buildBioMerticDTO(personType);
	}

	private static List<FingerprintDetailsDTO> getFingerprintDetailsDTO(String personType)
			throws RegBaseCheckedException {
		List<FingerprintDetailsDTO> fingerList = new ArrayList<>();

		FingerprintDetailsDTO fingerprintDetailsDTO2 = new FingerprintDetailsDTO();

		fingerprintDetailsDTO2.setFingerPrint(DataProvider.getImageBytes(DataProvider.THUMB_JPG));
		fingerprintDetailsDTO2.setFingerType("leftIndexFinger");
		fingerprintDetailsDTO2.setForceCaptured(false);
		fingerprintDetailsDTO2.setQualityScore(70.42);

		FingerprintDetailsDTO fingerprintDetailsDTO3 = new FingerprintDetailsDTO();

		fingerprintDetailsDTO3.setFingerPrint(DataProvider.getImageBytes(DataProvider.THUMB_JPG));
		fingerprintDetailsDTO3.setFingerType("leftMiddleFinger");
		fingerprintDetailsDTO3.setForceCaptured(false);
		fingerprintDetailsDTO3.setQualityScore(70.57);

		FingerprintDetailsDTO fingerprintDetailsDTO4 = new FingerprintDetailsDTO();

		fingerprintDetailsDTO4.setFingerPrint(DataProvider.getImageBytes(DataProvider.THUMB_JPG));
		fingerprintDetailsDTO4.setFingerType("leftRingFinger");
		fingerprintDetailsDTO4.setForceCaptured(false);
		fingerprintDetailsDTO4.setQualityScore(30);

		FingerprintDetailsDTO fingerprintDetailsDTO5 = new FingerprintDetailsDTO();

		fingerprintDetailsDTO5.setFingerPrint(DataProvider.getImageBytes(DataProvider.THUMB_JPG));
		fingerprintDetailsDTO5.setFingerType("leftLittleFinger");
		fingerprintDetailsDTO5.setForceCaptured(false);
		fingerprintDetailsDTO5.setQualityScore(80);

		FingerprintDetailsDTO fingerprintDetailsDTO6 = new FingerprintDetailsDTO();

		fingerprintDetailsDTO6.setFingerPrint(DataProvider.getImageBytes(DataProvider.THUMB_JPG));
		fingerprintDetailsDTO6.setFingerType("rightIndexFinger");
		fingerprintDetailsDTO6.setForceCaptured(false);
		fingerprintDetailsDTO6.setQualityScore(80);

		FingerprintDetailsDTO fingerprintDetailsDTO7 = new FingerprintDetailsDTO();

		fingerprintDetailsDTO7.setFingerPrint(DataProvider.getImageBytes(DataProvider.THUMB_JPG));
		fingerprintDetailsDTO7.setFingerType("rightMiddleFinger");
		fingerprintDetailsDTO7.setForceCaptured(false);
		fingerprintDetailsDTO7.setQualityScore(65);

		FingerprintDetailsDTO fingerprintDetailsDTO8 = new FingerprintDetailsDTO();

		fingerprintDetailsDTO8.setFingerPrint(DataProvider.getImageBytes(DataProvider.THUMB_JPG));
		fingerprintDetailsDTO8.setFingerType("rightRingFinger");
		fingerprintDetailsDTO8.setForceCaptured(false);
		fingerprintDetailsDTO8.setQualityScore(80);

		FingerprintDetailsDTO fingerprintDetailsDTO9 = new FingerprintDetailsDTO();

		fingerprintDetailsDTO9.setFingerPrint(DataProvider.getImageBytes(DataProvider.THUMB_JPG));
		fingerprintDetailsDTO9.setFingerType("rightLittleFinger");
		fingerprintDetailsDTO9.setForceCaptured(false);
		fingerprintDetailsDTO9.setQualityScore(80);

		fingerList.add(fingerprintDetailsDTO2);
		fingerList.add(fingerprintDetailsDTO3);
		fingerList.add(fingerprintDetailsDTO4);
		fingerList.add(fingerprintDetailsDTO5);
		fingerList.add(fingerprintDetailsDTO6);
		fingerList.add(fingerprintDetailsDTO7);
		fingerList.add(fingerprintDetailsDTO8);
		fingerList.add(fingerprintDetailsDTO9);

		return fingerList;
	}

	private static List<ExceptionFingerprintDetailsDTO> getExceptionFingerprintDetailsDTO(String personType) {
		List<ExceptionFingerprintDetailsDTO> fingerExcepList = new ArrayList<>();

		ExceptionFingerprintDetailsDTO exceptionFingerprint = new ExceptionFingerprintDetailsDTO();

		exceptionFingerprint.setExceptionDescription("Lost in accident");
		exceptionFingerprint.setExceptionType(DataProvider.PERMANANENT);
		exceptionFingerprint.setMissingFinger("rightThumb");

		fingerExcepList.add(exceptionFingerprint);
		exceptionFingerprint = new ExceptionFingerprintDetailsDTO();

		exceptionFingerprint.setExceptionDescription("Lost permanentlt");
		exceptionFingerprint.setExceptionType(DataProvider.PERMANANENT);
		exceptionFingerprint.setMissingFinger("leftThumb");

		fingerExcepList.add(exceptionFingerprint);
		return fingerExcepList;
	}

	private static List<IrisDetailsDTO> getIrisDetailsDTO(String personType) throws RegBaseCheckedException {
		List<IrisDetailsDTO> irisList = new ArrayList<>();
		IrisDetailsDTO irisDetailsDTO = new IrisDetailsDTO();

		irisDetailsDTO.setIris(DataProvider.getImageBytes("/eye.jpg"));
		irisDetailsDTO.setIrisName("leftEye");
		irisDetailsDTO.setIrisType("leftEye");
		irisDetailsDTO.setForceCaptured(false);
		irisDetailsDTO.setQualityScore(85);

		irisList.add(irisDetailsDTO);

		return irisList;
	}

	private static List<ExceptionIrisDetailsDTO> getExceptionIrisDetailsDTO(String personType) {
		LinkedList<ExceptionIrisDetailsDTO> irisExcepList = new LinkedList<>();
		ExceptionIrisDetailsDTO exceptionIrisDetailsDTO = new ExceptionIrisDetailsDTO();

		exceptionIrisDetailsDTO.setExceptionDescription("by birth");
		exceptionIrisDetailsDTO.setExceptionType(DataProvider.PERMANANENT);
		exceptionIrisDetailsDTO.setMissingIris("Lefteye");
		irisExcepList.add(exceptionIrisDetailsDTO);

		return irisExcepList;
	}

	private static DemographicDTO getDemographicDTO() throws RegBaseCheckedException {
		DemographicDTO demographicDTO = new DemographicDTO();
		demographicDTO.setApplicantDocumentDTO(DataProvider.setApplicantDocumentDTO());
		demographicDTO.setHOFRegistrationId("HOF00233ID");
		demographicDTO.setHOFUIN("HOF003");
		demographicDTO.setIntroducerUIN("INT001");
		demographicDTO.setDemoInLocalLang(DataProvider.getDemoInLocalLang());
		demographicDTO.setDemoInUserLang(DataProvider.getDemoInLocalLang());
		return demographicDTO;
	}

	private static DemographicInfoDTO getDemoInLocalLang() {
		DemographicInfoDTO demographicInfoDTO = new DemographicInfoDTO();
		demographicInfoDTO.setDateOfBirth(new Date());
		demographicInfoDTO.setEmailId("email");
		demographicInfoDTO.setFullName("Balaji S");
		demographicInfoDTO.setGender("Male");
		demographicInfoDTO.setLanguageCode("en");
		demographicInfoDTO.setChild(false);
		demographicInfoDTO.setMobile("9791941815");
		AddressDTO addressDTO = new AddressDTO();
		addressDTO.setLine1("1");
		addressDTO.setLine2("2");
		addressDTO.setCity("Chennai");
		addressDTO.setState("TN");
		addressDTO.setCountry("IN");
		demographicInfoDTO.setAddressDTO(addressDTO);

		return demographicInfoDTO;
	}

	private static ApplicantDocumentDTO setApplicantDocumentDTO() throws RegBaseCheckedException {
		ApplicantDocumentDTO applicantDocumentDTO = new ApplicantDocumentDTO();
		applicantDocumentDTO.setDocumentDetailsDTO(DataProvider.getDocumentDetailsDTO());
		applicantDocumentDTO.setPhoto(DataProvider.getImageBytes("/applicantPhoto.jpg"));
		applicantDocumentDTO.setPhotoName("applicantPhoto");
		applicantDocumentDTO.setHasExceptionPhoto(false);
		applicantDocumentDTO.setAcknowledgeReceipt(DataProvider.getImageBytes("/acknowledgementReceipt.jpg"));
		applicantDocumentDTO.setAcknowledgeReceiptName("acknowledgementReceipt");
		return applicantDocumentDTO;
	}

	private static List<DocumentDetailsDTO> getDocumentDetailsDTO() throws RegBaseCheckedException {

		List<DocumentDetailsDTO> docdetailsList = new ArrayList<>();

		DocumentDetailsDTO documentDetailsDTO = new DocumentDetailsDTO();
		documentDetailsDTO.setDocument(DataProvider.getImageBytes("/proofOfAddress.jpg"));
		documentDetailsDTO.setDocumentName("addressCopy");
		documentDetailsDTO.setDocumentCategory("poi");
		documentDetailsDTO.setDocumentOwner("self");
		documentDetailsDTO.setDocumentType("passport");

		DocumentDetailsDTO documentDetailsResidenceDTO = new DocumentDetailsDTO();
		documentDetailsDTO.setDocument(DataProvider.getImageBytes("/proofOfAddress.jpg"));
		documentDetailsDTO.setDocumentName("ResidenceCopy");
		documentDetailsDTO.setDocumentCategory("poA");
		documentDetailsDTO.setDocumentOwner("self");
		documentDetailsDTO.setDocumentType("passport");

		docdetailsList.add(documentDetailsDTO);
		docdetailsList.add(documentDetailsResidenceDTO);

		return docdetailsList;
	}

	private static RegistrationMetaDataDTO getPacketMetaDataDTO() {

		RegistrationMetaDataDTO registrationMetaDataDTO = new RegistrationMetaDataDTO();
		registrationMetaDataDTO.setApplicationCategory("Regular");
		registrationMetaDataDTO.setApplicationType("New Enrolment");
		registrationMetaDataDTO.setGeoLatitudeLoc(13.0049);
		registrationMetaDataDTO.setGeoLongitudeLoc(80.24492);
		return registrationMetaDataDTO;
	}

	private static OSIDataDTO getOsiDataDTO() {
		OSIDataDTO osiDataDTO = new OSIDataDTO();
		osiDataDTO.setIntroducerType("HoF");
		osiDataDTO.setOperatorName("test");
		osiDataDTO.setOperatorUIN("123245");
		osiDataDTO.setOperatorUserID("987654");
		osiDataDTO.setSupervisorName("supervisor");
		osiDataDTO.setSupervisorUIN("123456789");
		osiDataDTO.setSupervisorUserID("6787899");
		return osiDataDTO;
	}

	private static List<AuditDTO> getAuditDTOs() {
		LinkedList<AuditDTO> auditDTOList = new LinkedList<>();
		
		
		addAuditDTOToList(auditDTOList, "Capture Demographic Data", "Data Capture", "Caputured demographic data");
		addAuditDTOToList(auditDTOList, "Capture Left Iris", "Iris Capture", "Caputured left iris");
		addAuditDTOToList(auditDTOList, "Capture Right Iris", "Iris Capture", "Caputured right iris");
		addAuditDTOToList(auditDTOList, "Capture Right Palm", "Palm Capture", "Caputured Right Palm");
		addAuditDTOToList(auditDTOList, "Capture Left Palm", "Palm Capture", "Caputured Left Palm");
		addAuditDTOToList(auditDTOList, "Capture Right Thumb", "Thumb Capture", "Caputured Right Thumb");
		addAuditDTOToList(auditDTOList, "Capture Left Thumb", "Thumb Capture", "Caputured Left Thumb");

		return auditDTOList;
	}
	
	private static void addAuditDTOToList(List<AuditDTO> auditDTOList,String eventName, String eventType,String description) {
		OffsetDateTime dateTime = OffsetDateTime.now();
		
		AuditDTO audit = new AuditDTO();
		
		audit.setUuid(String.valueOf(UUID.randomUUID().getMostSignificantBits()));
		audit.setCreatedAt(dateTime);
		audit.setEventId("1");
		audit.setEventName(eventName);
		audit.setEventType(eventType);
		audit.setActionTimeStamp(dateTime);
		audit.setHostName(RegConstants.LOCALHOST);
		audit.setHostIp(RegConstants.LOCALHOST);
		audit.setApplicationId("1");
		audit.setApplicationName("Registration-UI");
		audit.setSessionUserId("12345");
		audit.setSessionUserName("Officer");
		audit.setId("1");
		audit.setIdType("registration");
		audit.setCreatedBy("Officer");
		audit.setModuleId("1");
		audit.setModuleName("New Registration");
		audit.setDescription(description);
		auditDTOList.add(audit);
	}
}
