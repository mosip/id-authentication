package org.mosip.registration.util.dataprovider;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

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
import org.mosip.registration.util.kernal.RIDGenerator;

public class DataProvider {

	public static byte[] getImageBytes(String filePath) throws IOException, URISyntaxException {
		filePath = "/dataprovider".concat(filePath);
		InputStream file = DataProvider.class.getClass().getResourceAsStream(filePath);
		byte[] bytesArray = new byte[(int) file.available()];
		file.read(bytesArray);
		file.close();

		return bytesArray;
	}

	public static RegistrationDTO getPacketDTO() throws IOException, URISyntaxException {
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

	private static BiometricDTO getBiometricDTO() throws IOException, URISyntaxException {
		BiometricDTO biometricDTO = new BiometricDTO();
		biometricDTO.setApplicantBiometricDTO(DataProvider.getApplicantBiometricDTO("applicant"));
		biometricDTO.setHofBiometricDTO(DataProvider.getHofBiometricDTO("hof"));
		biometricDTO.setIntroducerBiometricDTO(DataProvider.getIntroducerBiometricDTO("introducer"));
		biometricDTO.setSupervisorBiometricDTO(DataProvider.getSupervisorBiometricDTO("supervisor"));
		biometricDTO.setOperatorBiometricDTO(DataProvider.getOperatorBiometricDTO("operator"));
		return biometricDTO;
	}

	private static BiometricInfoDTO getHofBiometricDTO(String persontype) throws IOException, URISyntaxException {
		BiometricInfoDTO biometricInfoDTO = new BiometricInfoDTO();
		biometricInfoDTO.setFingerprintDetailsDTO(DataProvider.getFingerprintDetailsDTO(persontype));
		biometricInfoDTO.setNumOfFingerPrintRetry(4);
		biometricInfoDTO.setExceptionFingerprintDetailsDTO(DataProvider.getExceptionFingerprintDetailsDTO(persontype));
		biometricInfoDTO.setIrisDetailsDTO(DataProvider.getIrisDetailsDTO(persontype));
		biometricInfoDTO.setNumOfIrisRetry(2);
		biometricInfoDTO.setExceptionIrisDetailsDTO(DataProvider.getExceptionIrisDetailsDTO(persontype));
		return biometricInfoDTO;
	}

	private static BiometricInfoDTO getIntroducerBiometricDTO(String persontype) throws IOException, URISyntaxException {
		BiometricInfoDTO biometricInfoDTO = new BiometricInfoDTO();
		biometricInfoDTO.setFingerprintDetailsDTO(DataProvider.getFingerprintDetailsDTO(persontype));
		biometricInfoDTO.setNumOfFingerPrintRetry(4);
		biometricInfoDTO.setExceptionFingerprintDetailsDTO(DataProvider.getExceptionFingerprintDetailsDTO(persontype));
		biometricInfoDTO.setIrisDetailsDTO(DataProvider.getIrisDetailsDTO(persontype));
		biometricInfoDTO.setNumOfIrisRetry(2);
		biometricInfoDTO.setExceptionIrisDetailsDTO(DataProvider.getExceptionIrisDetailsDTO(persontype));
		return biometricInfoDTO;
	}

	private static BiometricInfoDTO getSupervisorBiometricDTO(String persontype) throws IOException, URISyntaxException {
		BiometricInfoDTO biometricInfoDTO = new BiometricInfoDTO();
		biometricInfoDTO.setFingerprintDetailsDTO(DataProvider.getFingerprintDetailsDTO(persontype));
		biometricInfoDTO.setNumOfFingerPrintRetry(4);
		biometricInfoDTO.setExceptionFingerprintDetailsDTO(DataProvider.getExceptionFingerprintDetailsDTO(persontype));
		biometricInfoDTO.setIrisDetailsDTO(DataProvider.getIrisDetailsDTO(persontype));
		biometricInfoDTO.setNumOfIrisRetry(2);
		biometricInfoDTO.setExceptionIrisDetailsDTO(DataProvider.getExceptionIrisDetailsDTO(persontype));
		return biometricInfoDTO;
	}

	private static BiometricInfoDTO getOperatorBiometricDTO(String persontype) throws IOException, URISyntaxException {
		BiometricInfoDTO biometricInfoDTO = new BiometricInfoDTO();
		biometricInfoDTO.setFingerprintDetailsDTO(DataProvider.getFingerprintDetailsDTO(persontype));
		biometricInfoDTO.setNumOfFingerPrintRetry(4);
		biometricInfoDTO.setExceptionFingerprintDetailsDTO(DataProvider.getExceptionFingerprintDetailsDTO(persontype));
		biometricInfoDTO.setIrisDetailsDTO(DataProvider.getIrisDetailsDTO(persontype));
		biometricInfoDTO.setNumOfIrisRetry(2);
		biometricInfoDTO.setExceptionIrisDetailsDTO(DataProvider.getExceptionIrisDetailsDTO(persontype));
		return biometricInfoDTO;
	}

	private static BiometricInfoDTO getApplicantBiometricDTO(String personType) throws IOException, URISyntaxException {
		BiometricInfoDTO biometricInfoDTO = new BiometricInfoDTO();
		biometricInfoDTO.setFingerprintDetailsDTO(DataProvider.getFingerprintDetailsDTO(personType));
		biometricInfoDTO.setNumOfFingerPrintRetry(4);
		biometricInfoDTO.setExceptionFingerprintDetailsDTO(DataProvider.getExceptionFingerprintDetailsDTO(personType));
		biometricInfoDTO.setIrisDetailsDTO(DataProvider.getIrisDetailsDTO(personType));
		biometricInfoDTO.setNumOfIrisRetry(2);
		biometricInfoDTO.setExceptionIrisDetailsDTO(DataProvider.getExceptionIrisDetailsDTO(personType));
		return biometricInfoDTO;
	}

	private static List<FingerprintDetailsDTO> getFingerprintDetailsDTO(String personType) throws IOException, URISyntaxException {
		List<FingerprintDetailsDTO> fingerList = new ArrayList<>();
		
		/*FingerprintDetailsDTO fingerprintDetailsDTO1 = new FingerprintDetailsDTO();

		fingerprintDetailsDTO1.setFingerPrint(DataProvider.getImageBytes("/thumb.jpg"));
		fingerprintDetailsDTO1.setFingerType("leftThumb");
		fingerprintDetailsDTO1.setForceCaptured(false);
		fingerprintDetailsDTO1.setQualityScore(80);*/
		
		FingerprintDetailsDTO fingerprintDetailsDTO2 = new FingerprintDetailsDTO();

		fingerprintDetailsDTO2.setFingerPrint(DataProvider.getImageBytes("/thumb.jpg"));
		fingerprintDetailsDTO2.setFingerType("leftIndexFinger");
		fingerprintDetailsDTO2.setForceCaptured(false);
		fingerprintDetailsDTO2.setQualityScore(70.42);
		
		FingerprintDetailsDTO fingerprintDetailsDTO3 = new FingerprintDetailsDTO();

		fingerprintDetailsDTO3.setFingerPrint(DataProvider.getImageBytes("/thumb.jpg"));
		fingerprintDetailsDTO3.setFingerType("leftMiddleFinger");
		fingerprintDetailsDTO3.setForceCaptured(false);
		fingerprintDetailsDTO3.setQualityScore(70.57);
		
		FingerprintDetailsDTO fingerprintDetailsDTO4 = new FingerprintDetailsDTO();

		fingerprintDetailsDTO4.setFingerPrint(DataProvider.getImageBytes("/thumb.jpg"));
		fingerprintDetailsDTO4.setFingerType("leftRingFinger");
		fingerprintDetailsDTO4.setForceCaptured(false);
		fingerprintDetailsDTO4.setQualityScore(30);
		
		FingerprintDetailsDTO fingerprintDetailsDTO5 = new FingerprintDetailsDTO();

		fingerprintDetailsDTO5.setFingerPrint(DataProvider.getImageBytes("/thumb.jpg"));
		fingerprintDetailsDTO5.setFingerType("leftLittleFinger");
		fingerprintDetailsDTO5.setForceCaptured(false);
		fingerprintDetailsDTO5.setQualityScore(80);
		
		FingerprintDetailsDTO fingerprintDetailsDTO6 = new FingerprintDetailsDTO();

		fingerprintDetailsDTO6.setFingerPrint(DataProvider.getImageBytes("/thumb.jpg"));
		fingerprintDetailsDTO6.setFingerType("rightIndexFinger");
		fingerprintDetailsDTO6.setForceCaptured(false);
		fingerprintDetailsDTO6.setQualityScore(80);
		
		FingerprintDetailsDTO fingerprintDetailsDTO7 = new FingerprintDetailsDTO();

		fingerprintDetailsDTO7.setFingerPrint(DataProvider.getImageBytes("/thumb.jpg"));
		fingerprintDetailsDTO7.setFingerType("rightMiddleFinger");
		fingerprintDetailsDTO7.setForceCaptured(false);
		fingerprintDetailsDTO7.setQualityScore(65);
		
		FingerprintDetailsDTO fingerprintDetailsDTO8 = new FingerprintDetailsDTO();

		fingerprintDetailsDTO8.setFingerPrint(DataProvider.getImageBytes("/thumb.jpg"));
		fingerprintDetailsDTO8.setFingerType("rightRingFinger");
		fingerprintDetailsDTO8.setForceCaptured(false);
		fingerprintDetailsDTO8.setQualityScore(80);
		
		FingerprintDetailsDTO fingerprintDetailsDTO9 = new FingerprintDetailsDTO();

		fingerprintDetailsDTO9.setFingerPrint(DataProvider.getImageBytes("/thumb.jpg"));
		fingerprintDetailsDTO9.setFingerType("rightLittleFinger");
		fingerprintDetailsDTO9.setForceCaptured(false);
		fingerprintDetailsDTO9.setQualityScore(80);

		//fingerList.add(fingerprintDetailsDTO1);
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
		List<ExceptionFingerprintDetailsDTO> FingerExcepList = new ArrayList<>();

		
		ExceptionFingerprintDetailsDTO exceptionFingerprint = new ExceptionFingerprintDetailsDTO();

		exceptionFingerprint.setExceptionDescription("Lost in accident");
		exceptionFingerprint.setExceptionType("Permananent");
		exceptionFingerprint.setMissingFinger("rightThumb");

		FingerExcepList.add(exceptionFingerprint);
		exceptionFingerprint = new ExceptionFingerprintDetailsDTO();

		exceptionFingerprint.setExceptionDescription("Lost permanentlt");
		exceptionFingerprint.setExceptionType("Permananent");
		exceptionFingerprint.setMissingFinger("leftThumb");

		FingerExcepList.add(exceptionFingerprint);
		return FingerExcepList;
	}

	private static List<IrisDetailsDTO> getIrisDetailsDTO(String personType) throws IOException, URISyntaxException {
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
		exceptionIrisDetailsDTO.setExceptionType("Permananent");
		exceptionIrisDetailsDTO.setMissingIris("Lefteye");
		irisExcepList.add(exceptionIrisDetailsDTO);

		return irisExcepList;
	}

	private static DemographicDTO getDemographicDTO() throws IOException, URISyntaxException {
		DemographicDTO demographicDTO = new DemographicDTO();
		demographicDTO.setApplicantDocumentDTO(DataProvider.setApplicantDocumentDTO());
		demographicDTO.setHOFRegistrationId("HOF00233ID");
		demographicDTO.setHOFUIN("HOF003");
		demographicDTO.setIntroducerUIN("INT001");
		demographicDTO.setDemoInLocalLang(DataProvider.getDemoInLocalLang());
		demographicDTO.setDemoInUserLang(DataProvider.getDemoInUserLang());
		return demographicDTO;
	}

	private static DemographicInfoDTO getDemoInUserLang() {
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

	private static ApplicantDocumentDTO setApplicantDocumentDTO() throws IOException, URISyntaxException {
		ApplicantDocumentDTO applicantDocumentDTO = new ApplicantDocumentDTO();
		applicantDocumentDTO.setDocumentDetailsDTO(DataProvider.getDocumentDetailsDTO());
		applicantDocumentDTO.setPhoto(DataProvider.getImageBytes("/applicantPhoto.jpg"));
		applicantDocumentDTO.setPhotoName("applicantPhoto");
		//applicantDocumentDTO.setExceptionPhoto(DataProvider.getImageBytes("/applicantExceptionPhoto.jpg"));
		applicantDocumentDTO.setHasExceptionPhoto(false);
		// applicantDto.setExceptionPhotoName("applicantExceptionPhoto");
		applicantDocumentDTO.setAcknowledgeReceipt(DataProvider.getImageBytes("/acknowledgementReceipt.jpg"));
		applicantDocumentDTO.setAcknowledgeReceiptName("acknowledgementReceipt");
		return applicantDocumentDTO;
	}

	private static List<DocumentDetailsDTO> getDocumentDetailsDTO() throws IOException, URISyntaxException {
		List<DocumentDetailsDTO> docdetailsList = new ArrayList<>();
		DocumentDetailsDTO documentDetailsDTO = new DocumentDetailsDTO();
		documentDetailsDTO = new DocumentDetailsDTO();
		documentDetailsDTO.setDocument(DataProvider.getImageBytes("/proofOfAddress.jpg"));
		documentDetailsDTO.setDocumentName("addressCopy");
		documentDetailsDTO.setDocumentCategory("poi");
		documentDetailsDTO.setDocumentOwner("self");
		documentDetailsDTO.setDocumentType("passport");

		documentDetailsDTO = new DocumentDetailsDTO();
		documentDetailsDTO = new DocumentDetailsDTO();
		documentDetailsDTO.setDocument(DataProvider.getImageBytes("/proofOfAddress.jpg"));
		documentDetailsDTO.setDocumentName("ResidenceCopy");
		documentDetailsDTO.setDocumentCategory("poA");
		documentDetailsDTO.setDocumentOwner("self");
		documentDetailsDTO.setDocumentType("passport");
		docdetailsList.add(documentDetailsDTO);
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
		LinkedList<AuditDTO> auditDTOList = new LinkedList<AuditDTO>();
		OffsetDateTime dateTime = OffsetDateTime.now();
		AuditDTO audit = new AuditDTO();
		audit.setUuid(String.valueOf(UUID.randomUUID().getMostSignificantBits()));
		audit.setCreatedAt(dateTime);
		audit.setEventId("1");
		audit.setEventName("Capture Demographic Data");
		audit.setEventType("Data Capture");
		audit.setActionTimeStamp(dateTime);
		audit.setHostName("localHost");
		audit.setHostIp("localhost");
		audit.setApplicationId("1");
		audit.setApplicationName("Registration-UI");
		audit.setSessionUserId("12345");
		audit.setSessionUserName("Officer");
		audit.setId("1");
		audit.setIdType("registration");
		audit.setCreatedBy("Officer");
		audit.setModuleId("1");
		audit.setModuleName("New Registration");
		audit.setDescription("Caputured demographic data");
		auditDTOList.add(audit);
		
		audit = new AuditDTO();
		audit.setUuid(String.valueOf(UUID.randomUUID().getMostSignificantBits()));
		audit.setCreatedAt(dateTime);
		audit.setEventId("1");
		audit.setEventName("Capture Left Iris");
		audit.setEventType("Iris Capture");
		audit.setActionTimeStamp(dateTime);
		audit.setHostName("localHost");
		audit.setHostIp("localhost");
		audit.setApplicationId("1");
		audit.setApplicationName("Registration-UI");
		audit.setSessionUserId("12345");
		audit.setSessionUserName("Officer");
		audit.setId("1");
		audit.setIdType("registration");
		audit.setCreatedBy("Officer");
		audit.setModuleId("1");
		audit.setModuleName("New Registration");
		audit.setDescription("Caputured left iris");
		auditDTOList.add(audit);
		
		audit = new AuditDTO();
		audit.setUuid(String.valueOf(UUID.randomUUID().getMostSignificantBits()));
		audit.setCreatedAt(dateTime);
		audit.setEventId("1");
		audit.setEventName("Capture Right Iris");
		audit.setEventType("Iris Capture");
		audit.setActionTimeStamp(dateTime);
		audit.setHostName("localHost");
		audit.setHostIp("localhost");
		audit.setApplicationId("1");
		audit.setApplicationName("Registration-UI");
		audit.setSessionUserId("12345");
		audit.setSessionUserName("Officer");
		audit.setId("1");
		audit.setIdType("registration");
		audit.setCreatedBy("Officer");
		audit.setModuleId("1");
		audit.setModuleName("New Registration");
		audit.setDescription("Caputured right iris");
		auditDTOList.add(audit);
		
		audit = new AuditDTO();
		audit.setUuid(String.valueOf(UUID.randomUUID().getMostSignificantBits()));
		audit.setCreatedAt(dateTime);
		audit.setEventId("1");
		audit.setEventName("Capture Right Palm");
		audit.setEventType("Palm Capture");
		audit.setActionTimeStamp(dateTime);
		audit.setHostName("localHost");
		audit.setHostIp("localhost");
		audit.setApplicationId("1");
		audit.setApplicationName("Registration-UI");
		audit.setSessionUserId("12345");
		audit.setSessionUserName("Officer");
		audit.setId("1");
		audit.setIdType("registration");
		audit.setCreatedBy("Officer");
		audit.setModuleId("1");
		audit.setModuleName("New Registration");
		audit.setDescription("Caputured Right Palm");
		auditDTOList.add(audit);
		
		audit = new AuditDTO();
		audit.setUuid(String.valueOf(UUID.randomUUID().getMostSignificantBits()));
		audit.setCreatedAt(dateTime);
		audit.setEventId("1");
		audit.setEventName("Capture Left Palm");
		audit.setEventType("Palm Capture");
		audit.setActionTimeStamp(dateTime);
		audit.setHostName("localHost");
		audit.setHostIp("localhost");
		audit.setApplicationId("1");
		audit.setApplicationName("Registration-UI");
		audit.setSessionUserId("12345");
		audit.setSessionUserName("Officer");
		audit.setId("1");
		audit.setIdType("registration");
		audit.setCreatedBy("Officer");
		audit.setModuleId("1");
		audit.setModuleName("New Registration");
		audit.setDescription("Caputured Left Palm");
		auditDTOList.add(audit);
		
		audit = new AuditDTO();
		audit.setUuid(String.valueOf(UUID.randomUUID().getMostSignificantBits()));
		audit.setCreatedAt(dateTime);
		audit.setEventId("1");
		audit.setEventName("Capture Right Thumb");
		audit.setEventType("Thumb Capture");
		audit.setActionTimeStamp(dateTime);
		audit.setHostName("localHost");
		audit.setHostIp("localhost");
		audit.setApplicationId("1");
		audit.setApplicationName("Registration-UI");
		audit.setSessionUserId("12345");
		audit.setSessionUserName("Officer");
		audit.setId("1");
		audit.setIdType("registration");
		audit.setCreatedBy("Officer");
		audit.setModuleId("1");
		audit.setModuleName("New Registration");
		audit.setDescription("Caputured Right Thumb");
		auditDTOList.add(audit);
		
		audit = new AuditDTO();
		audit.setUuid(String.valueOf(UUID.randomUUID().getMostSignificantBits()));
		audit.setCreatedAt(dateTime);
		audit.setEventId("1");
		audit.setEventName("Capture Left Thumb");
		audit.setEventType("Thumb Capture");
		audit.setActionTimeStamp(dateTime);
		audit.setHostName("localHost");
		audit.setHostIp("localhost");
		audit.setApplicationId("1");
		audit.setApplicationName("Registration-UI");
		audit.setSessionUserId("12345");
		audit.setSessionUserName("Officer");
		audit.setId("1");
		audit.setIdType("registration");
		audit.setCreatedBy("Officer");
		audit.setModuleId("1");
		audit.setModuleName("New Registration");
		audit.setDescription("Caputured Left Thumb");
		auditDTOList.add(audit);
		
		return auditDTOList;
	}
}
