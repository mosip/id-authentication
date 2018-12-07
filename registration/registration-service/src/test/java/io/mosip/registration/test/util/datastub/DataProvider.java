package io.mosip.registration.test.util.datastub;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import io.mosip.registration.constants.IntroducerType;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dto.AuditDTO;
import io.mosip.registration.dto.OSIDataDTO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.RegistrationMetaDataDTO;
import io.mosip.registration.dto.biometric.BiometricDTO;
import io.mosip.registration.dto.biometric.BiometricExceptionDTO;
import io.mosip.registration.dto.biometric.BiometricInfoDTO;
import io.mosip.registration.dto.biometric.FingerprintDetailsDTO;
import io.mosip.registration.dto.biometric.IrisDetailsDTO;
import io.mosip.registration.dto.demographic.AddressDTO;
import io.mosip.registration.dto.demographic.ApplicantDocumentDTO;
import io.mosip.registration.dto.demographic.DemographicDTO;
import io.mosip.registration.dto.demographic.DemographicInfoDTO;
import io.mosip.registration.dto.demographic.DocumentDetailsDTO;
import io.mosip.registration.dto.demographic.LocationDTO;
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
			InputStream file = DataProvider.class.getResourceAsStream(filePath);
			byte[] bytesArray = new byte[(int) file.available()];
			file.read(bytesArray);
			file.close();

			return bytesArray;
		} catch (IOException ioException) {
			throw new RegBaseCheckedException(RegistrationConstants.SERVICE_DATA_PROVIDER_UTIL,
					"Unable to read the Image bytes", ioException);
		}
	}

	public static RegistrationDTO getPacketDTO() throws RegBaseCheckedException {
		RegistrationDTO registrationDTO = new RegistrationDTO();
		registrationDTO.setAuditDTOs(DataProvider.getAuditDTOs());
		registrationDTO.setOsiDataDTO(DataProvider.getOsiDataDTO());
		registrationDTO.setRegistrationMetaDataDTO(DataProvider.getRegistrationMetaDataDTO());
		registrationDTO.setPreRegistrationId("PEN1345T");
		registrationDTO.setRegistrationId(RIDGenerator.nextRID());

		registrationDTO.setDemographicDTO(DataProvider.getDemographicDTO());
		registrationDTO.setBiometricDTO(DataProvider.getBiometricDTO());
		return registrationDTO;

	}

	private static BiometricDTO getBiometricDTO() throws RegBaseCheckedException {
		BiometricDTO biometricDTO = new BiometricDTO();
		biometricDTO.setApplicantBiometricDTO(DataProvider.buildBioMerticDTO(DataProvider.APPLICANT));
		biometricDTO.setIntroducerBiometricDTO(DataProvider.buildBioMerticDTO("introducer"));
		biometricDTO.setSupervisorBiometricDTO(DataProvider.buildBioMerticDTO("supervisor"));
		biometricDTO.setOperatorBiometricDTO(DataProvider.buildBioMerticDTO("officer"));
		return biometricDTO;
	}

	private static BiometricInfoDTO buildBioMerticDTO(String persontype) throws RegBaseCheckedException {
		BiometricInfoDTO biometricInfoDTO = new BiometricInfoDTO();
		biometricInfoDTO.setFingerprintDetailsDTO(DataProvider.getFingerprintDetailsDTO(persontype));
		if (persontype.equalsIgnoreCase(DataProvider.APPLICANT)) {
			biometricInfoDTO.setFingerPrintBiometricExceptionDTO(DataProvider.getExceptionFingerprintDetailsDTO());
			biometricInfoDTO.setIrisDetailsDTO(DataProvider.getIrisDetailsDTO());
			biometricInfoDTO.setIrisBiometricExceptionDTO(DataProvider.getExceptionIrisDetailsDTO());
		}
		return biometricInfoDTO;
	}

	private static List<FingerprintDetailsDTO> getFingerprintDetailsDTO(String personType)
			throws RegBaseCheckedException {
		List<FingerprintDetailsDTO> fingerList = new ArrayList<>();

		if (personType.equals(DataProvider.APPLICANT)) {
			fingerList.add(DataProvider.buildFingerPrintDetailsDTO(DataProvider.THUMB_JPG, "BothThumbs.jpg", 85.0, false,
					"thumbs", 0));
			fingerList.add(DataProvider.buildFingerPrintDetailsDTO(DataProvider.THUMB_JPG, "LeftPalm.jpg", 80.0, false,
					"leftSlap", 3));
			fingerList.add(DataProvider.buildFingerPrintDetailsDTO(DataProvider.THUMB_JPG, "RightPalm.jpg", 95.0, false,
					"rightSlap", 2));
		} else {
			fingerList.add(DataProvider.buildFingerPrintDetailsDTO(DataProvider.THUMB_JPG, personType + "LeftThumb.jpg", 0, false,
					"leftThumb", 0));
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

	private static List<BiometricExceptionDTO> getExceptionFingerprintDetailsDTO() {
		List<BiometricExceptionDTO> fingerExcepList = new ArrayList<>();

		fingerExcepList.add(DataProvider.buildBiometricExceptionDTO("fingerprint", "LeftThumb", "Due to accident",
				DataProvider.PERMANANENT));
		fingerExcepList.add(DataProvider.buildBiometricExceptionDTO("fingerprint", "LeftForefinger", "Due to accident",
				DataProvider.PERMANANENT));
		return fingerExcepList;
	}

	private static BiometricExceptionDTO buildBiometricExceptionDTO(String biometricType, String missingBiometric,
			String exceptionDescription, String exceptionType) {
		BiometricExceptionDTO biometricExceptionDTO = new BiometricExceptionDTO();
		biometricExceptionDTO.setBiometricType(biometricType);
		biometricExceptionDTO.setMissingBiometric(missingBiometric);
		biometricExceptionDTO.setExceptionDescription(exceptionDescription);
		biometricExceptionDTO.setExceptionType(exceptionType);
		return biometricExceptionDTO;
	}

	private static List<IrisDetailsDTO> getIrisDetailsDTO() throws RegBaseCheckedException {
		List<IrisDetailsDTO> irisList = new ArrayList<>();
		irisList.add(DataProvider.buildIrisDetailsDTO("/eye.jpg", "LeftEye.jpg", "leftEye", false, 79.0));

		return irisList;
	}

	private static IrisDetailsDTO buildIrisDetailsDTO(String iris, String irisImageName, String irisType,
			boolean isForcedCaptured, double qualityScore) throws RegBaseCheckedException {
		IrisDetailsDTO irisDetailsDTO = new IrisDetailsDTO();
		irisDetailsDTO.setIris(DataProvider.getImageBytes(iris));
		irisDetailsDTO.setIrisImageName(irisImageName);
		irisDetailsDTO.setIrisType(irisType);
		irisDetailsDTO.setForceCaptured(isForcedCaptured);
		irisDetailsDTO.setQualityScore(qualityScore);
		irisDetailsDTO.setNumOfIrisRetry(2);
		return irisDetailsDTO;
	}

	private static List<BiometricExceptionDTO> getExceptionIrisDetailsDTO() {
		LinkedList<BiometricExceptionDTO> irisExcepList = new LinkedList<>();
		irisExcepList
				.add(DataProvider.buildBiometricExceptionDTO("iris", "RightEye", "By birth", DataProvider.PERMANANENT));

		return irisExcepList;
	}

	private static DemographicDTO getDemographicDTO() throws RegBaseCheckedException {
		DemographicDTO demographicDTO = new DemographicDTO();
		demographicDTO.setApplicantDocumentDTO(DataProvider.setApplicantDocumentDTO());
		demographicDTO.setIntroducerRID("2018234500321157812");
		demographicDTO.setDemoInLocalLang(DataProvider.getDemoInLocalLang());
		demographicDTO.setDemoInUserLang(DataProvider.getDemoInLocalLang());
		return demographicDTO;
	}

	private static DemographicInfoDTO getDemoInLocalLang() {
		DemographicInfoDTO demographicInfoDTO = new DemographicInfoDTO();
		demographicInfoDTO.setFullName("John Lawernce Jr");
		//demographicInfoDTO.setDateOfBirth(new Date());
		demographicInfoDTO.setEmailId("john.lawerence@gmail.com");
		demographicInfoDTO.setGender("Male");
		demographicInfoDTO.setLanguageCode("en");
		demographicInfoDTO.setChild(false);
		demographicInfoDTO.setMobile("8889992233");
		AddressDTO addressDTO = new AddressDTO();
		addressDTO.setAddressLine1("1");
		addressDTO.setAddressLine2("2");
		LocationDTO locationDTO = new LocationDTO();
		locationDTO.setRegion("Taramani");
		locationDTO.setCity("Chennai");
		locationDTO.setProvince("TN");
		addressDTO.setLocationDTO(locationDTO);
		demographicInfoDTO.setAddressDTO(addressDTO);

		return demographicInfoDTO;
	}

	private static ApplicantDocumentDTO setApplicantDocumentDTO() throws RegBaseCheckedException {
		ApplicantDocumentDTO applicantDocumentDTO = new ApplicantDocumentDTO();
		applicantDocumentDTO.setDocumentDetailsDTO(DataProvider.getDocumentDetailsDTO());
		applicantDocumentDTO.setPhoto(DataProvider.getImageBytes("/applicantPhoto.jpg"));
		applicantDocumentDTO.setPhotographName("ApplicantPhoto.jpg");
		applicantDocumentDTO.setHasExceptionPhoto(true);
		applicantDocumentDTO.setExceptionPhoto(DataProvider.getImageBytes("/applicantPhoto.jpg"));
		applicantDocumentDTO.setExceptionPhotoName("ExceptionPhoto.jpg");
		applicantDocumentDTO.setQualityScore(89.0);
		applicantDocumentDTO.setNumRetry(1);
		applicantDocumentDTO.setAcknowledgeReceipt(DataProvider.getImageBytes("/acknowledgementReceipt.jpg"));
		applicantDocumentDTO.setAcknowledgeReceiptName("RegistrationAcknowledgement.jpg");
		return applicantDocumentDTO;
	}

	private static List<DocumentDetailsDTO> getDocumentDetailsDTO() throws RegBaseCheckedException {

		List<DocumentDetailsDTO> docdetailsList = new ArrayList<>();

		DocumentDetailsDTO documentDetailsDTO = new DocumentDetailsDTO();
		documentDetailsDTO.setDocument(DataProvider.getImageBytes("/proofOfAddress.jpg"));
		documentDetailsDTO.setDocumentName("ProofOfIdentity.jpg");
		documentDetailsDTO.setDocumentCategory("PoI");
		documentDetailsDTO.setDocumentOwner("Self");
		documentDetailsDTO.setDocumentType("PAN");

		DocumentDetailsDTO documentDetailsResidenceDTO = new DocumentDetailsDTO();
		documentDetailsResidenceDTO.setDocument(DataProvider.getImageBytes("/proofOfAddress.jpg"));
		documentDetailsResidenceDTO.setDocumentName("ProofOfAddress.jpg");
		documentDetailsResidenceDTO.setDocumentCategory("PoA");
		documentDetailsResidenceDTO.setDocumentOwner("hof");
		documentDetailsResidenceDTO.setDocumentType("passport");

		docdetailsList.add(documentDetailsDTO);
		docdetailsList.add(documentDetailsResidenceDTO);

		return docdetailsList;
	}

	private static RegistrationMetaDataDTO getRegistrationMetaDataDTO() {

		RegistrationMetaDataDTO registrationMetaDataDTO = new RegistrationMetaDataDTO();
		registrationMetaDataDTO.setRegistrationCategory("Parent");
		registrationMetaDataDTO.setApplicationType("Child");
		registrationMetaDataDTO.setGeoLatitudeLoc(13.0049);
		registrationMetaDataDTO.setGeoLongitudeLoc(80.24492);
		registrationMetaDataDTO.setCenterId("12245");
		registrationMetaDataDTO.setMachineId("yyeqy26356");
		registrationMetaDataDTO.setRegistrationCategory("New");
		
		return registrationMetaDataDTO;
	}

	private static OSIDataDTO getOsiDataDTO() {
		OSIDataDTO osiDataDTO = new OSIDataDTO();
		osiDataDTO.setOperatorID("op0r0s12");
		osiDataDTO.setSupervisorID("s9ju2jhu");
		osiDataDTO.setIntroducerType(IntroducerType.PARENT.getCode());
		return osiDataDTO;
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
