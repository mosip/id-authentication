package io.mosip.registration.test.util.datastub;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import io.mosip.registration.builder.Builder;
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
import io.mosip.registration.dto.demographic.ApplicantDocumentDTO;
import io.mosip.registration.dto.demographic.ArrayPropertiesDTO;
import io.mosip.registration.dto.demographic.DemographicDTO;
import io.mosip.registration.dto.demographic.DemographicInfoDTO;
import io.mosip.registration.dto.demographic.DocumentDetailsDTO;
import io.mosip.registration.dto.demographic.Identity;
import io.mosip.registration.dto.demographic.SimplePropertiesDTO;
import io.mosip.registration.dto.demographic.ValuesDTO;
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
			biometricInfoDTO.setBiometricExceptionDTO(DataProvider.getExceptionFingerprintDetailsDTO());
			biometricInfoDTO.setIrisDetailsDTO(DataProvider.getIrisDetailsDTO());
			biometricInfoDTO.setBiometricExceptionDTO(DataProvider.getExceptionIrisDetailsDTO());
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
		demographicDTO.setDemographicInfoDTO(DataProvider.getDemoInLocalLang());
		getDocumentDetailsDTO(demographicDTO.getDemographicInfoDTO().getIdentity());
		return demographicDTO;
	}

	@SuppressWarnings("unchecked")
	private static DemographicInfoDTO getDemoInLocalLang() {
		String platformLanguageCode = "en";
		String localLanguageCode = "ar";
		
		DemographicInfoDTO demographicInfoDTO = Builder.build(DemographicInfoDTO.class)
				.with(demographicDTO -> demographicDTO.setIdentity(Builder.build(Identity.class)
						.with(identity -> identity.setFullName((ArrayPropertiesDTO)Builder.build(ArrayPropertiesDTO.class)
								.with(name -> name.setLabel("First Name"))
								.with(name -> name.setValues(Builder.build(LinkedList.class)
										.with(values -> values.add(Builder.build(ValuesDTO.class)
												.with(value -> value.setLanguage(platformLanguageCode))
												.with(value -> value.setValue("John Lawernce Jr")).get()))
										.with(values -> values.add(Builder.build(ValuesDTO.class)
												.with(value -> value.setLanguage(localLanguageCode))
												.with(value -> value.setValue("John Lawernce Jr"))
												.get()))
										.get()))
								.get()))
						.with(identity -> identity
								.setDateOfBirth(
										Builder.build(SimplePropertiesDTO.class)
												.with(value -> value.setLabel("Date Of Birth")).with(
														value -> value
																.setValue("2018/01/01"))
												.get()))
						.with(identity -> identity.setAge("1"))
						.with(identity -> identity.setGender((ArrayPropertiesDTO)Builder.build(ArrayPropertiesDTO.class)
								.with(genderValue -> genderValue.setLabel("Gender"))
								.with(genderValue -> genderValue.setValues(Builder.build(LinkedList.class)
										.with(values -> values.add(Builder.build(ValuesDTO.class)
												.with(value -> value.setLanguage(platformLanguageCode))
												.with(value -> value.setValue("Male")).get()))
										.with(values -> values.add(Builder.build(ValuesDTO.class)
												.with(value -> value.setLanguage(localLanguageCode))
												.with(value -> value.setValue("Male")).get()))
										.get()))
								.get()))
						.with(identity -> identity.setAddressLine1((ArrayPropertiesDTO)Builder.build(ArrayPropertiesDTO.class)
								.with(addressValue -> addressValue.setLabel("Address Line 1"))
								.with(addressValue -> addressValue.setValues(Builder.build(LinkedList.class)
										.with(values -> values.add(Builder.build(ValuesDTO.class)
												.with(value -> value.setLanguage(platformLanguageCode))
												.with(value -> value.setValue("Address Line 1")).get()))
										.with(values -> values.add(Builder.build(ValuesDTO.class)
												.with(value -> value.setLanguage(localLanguageCode))
												.with(value -> value.setValue("Address Line 1"))
												.get()))
										.get()))
								.get()))
						.with(identity -> identity.setAddressLine2((ArrayPropertiesDTO)Builder.build(ArrayPropertiesDTO.class)
								.with(addressValue -> addressValue.setLabel("Address Line 2"))
								.with(addressValue -> addressValue.setValues(Builder.build(LinkedList.class)
										.with(values -> values.add(Builder.build(ValuesDTO.class)
												.with(value -> value.setLanguage(platformLanguageCode))
												.with(value -> value.setValue("Address Line 2")).get()))
										.with(values -> values.add(Builder.build(ValuesDTO.class)
												.with(value -> value.setLanguage(localLanguageCode))
												.with(value -> value.setValue("Address Line 2"))
												.get()))
										.get()))
								.get()))
						.with(identity -> identity.setAddressLine3((ArrayPropertiesDTO)Builder.build(ArrayPropertiesDTO.class)
								.with(addressValue -> addressValue.setLabel("Address Line 3"))
								.with(addressValue -> addressValue.setValues(Builder.build(LinkedList.class)
										.with(values -> values.add(Builder.build(ValuesDTO.class)
												.with(value -> value.setLanguage(platformLanguageCode))
												.with(value -> value.setValue("Address Line 3")).get()))
										.with(values -> values.add(Builder.build(ValuesDTO.class)
												.with(value -> value.setLanguage(localLanguageCode))
												.with(value -> value.setValue("Address Line 3"))
												.get()))
										.get()))
								.get()))
						.with(identity -> identity.setRegion((ArrayPropertiesDTO)Builder.build(ArrayPropertiesDTO.class)
								.with(regionValue -> regionValue.setLabel("Region"))
								.with(regionValue -> regionValue.setValues(Builder.build(LinkedList.class)
										.with(values -> values.add(Builder.build(ValuesDTO.class)
												.with(value -> value.setLanguage(platformLanguageCode))
												.with(value -> value.setValue("Region")).get()))
										.with(values -> values.add(Builder.build(ValuesDTO.class)
												.with(value -> value.setLanguage(localLanguageCode))
												.with(value -> value.setValue("Region")).get()))
										.get()))
								.get()))
						.with(identity -> identity.setProvince((ArrayPropertiesDTO)Builder.build(ArrayPropertiesDTO.class)
								.with(provinceValue -> provinceValue.setLabel("Province"))
								.with(provinceValue -> provinceValue.setValues(Builder.build(LinkedList.class)
										.with(values -> values.add(Builder.build(ValuesDTO.class)
												.with(value -> value.setLanguage(platformLanguageCode))
												.with(value -> value.setValue("Province")).get()))
										.with(values -> values.add(Builder.build(ValuesDTO.class)
												.with(value -> value.setLanguage(localLanguageCode))
												.with(value -> value.setValue("Province")).get()))
										.get()))
								.get()))
						.with(identity -> identity.setCity((ArrayPropertiesDTO)Builder.build(ArrayPropertiesDTO.class)
								.with(cityValue -> cityValue.setLabel("City"))
								.with(cityValue -> cityValue.setValues(Builder.build(LinkedList.class)
										.with(values -> values.add(Builder.build(ValuesDTO.class)
												.with(value -> value.setLanguage(platformLanguageCode))
												.with(value -> value.setValue("City")).get()))
										.with(values -> values.add(Builder.build(ValuesDTO.class)
												.with(value -> value.setLanguage(localLanguageCode))
												.with(value -> value.setValue("City")).get()))
										.get()))
								.get()))
						.with(identity -> identity.setPostalCode("600113"))
						.with(identity -> identity.setPhone(
								Builder.build(SimplePropertiesDTO.class).with(value -> value.setLabel("Land Line"))
										.with(value -> value.setValue("8889992233")).get()))
						.with(identity -> identity.setEmail(Builder.build(SimplePropertiesDTO.class)
								.with(value -> value.setLabel("Business Email"))
								.with(value -> value.setValue("john.lawerence@gmail.com")).get()))
						.with(identity -> identity.setCnieNumber(""))
						.with(identity -> identity.setLocalAdministrativeAuthority((ArrayPropertiesDTO)Builder.build(ArrayPropertiesDTO.class)
								.with(localAdminAuthValue -> localAdminAuthValue.setLabel("Local Administrative Authority"))
								.with(localAdminAuthValue -> localAdminAuthValue.setValues(Builder.build(LinkedList.class)
										.with(values -> values.add(Builder.build(ValuesDTO.class)
												.with(value -> value.setLanguage(platformLanguageCode))
												.with(value -> value.setValue("Local Administrative Authority")).get()))
										.with(values -> values.add(Builder.build(ValuesDTO.class)
												.with(value -> value.setLanguage(localLanguageCode))
												.with(value -> value.setValue("Local Administrative Authority")).get()))
										.get()))
								.get()))
						.with(identity -> identity.setParentOrGuardianName((ArrayPropertiesDTO)Builder.build(ArrayPropertiesDTO.class)
								.with(parentValue -> parentValue.setLabel("Parent/Guardian"))
								.with(parentValue -> parentValue.setValues(Builder.build(LinkedList.class)
										.with(values -> values.add(Builder.build(ValuesDTO.class)
												.with(value -> value.setLanguage(platformLanguageCode))
												.with(value -> value.setValue("Parent/Guardian")).get()))
										.with(values -> values.add(Builder.build(ValuesDTO.class)
												.with(value -> value.setLanguage(localLanguageCode))
												.with(value -> value.setValue("Parent/Guardian")).get()))
										.get()))
								.get()))
						.with(identity -> identity.setParentOrGuardianRIDOrUIN("2018234500321157812")).get()))
				.get();

		return demographicInfoDTO;
	}

	private static ApplicantDocumentDTO setApplicantDocumentDTO() throws RegBaseCheckedException {
		ApplicantDocumentDTO applicantDocumentDTO = new ApplicantDocumentDTO();
		//applicantDocumentDTO.setDocumentDetailsDTO(DataProvider.getDocumentDetailsDTO());
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

	private static void getDocumentDetailsDTO(Identity identity) throws RegBaseCheckedException {

		DocumentDetailsDTO documentDetailsDTO = new DocumentDetailsDTO();
		documentDetailsDTO.setDocument(DataProvider.getImageBytes("/proofOfAddress.jpg"));
		documentDetailsDTO.setCategory("Passport");
		documentDetailsDTO.setFormat("jpg");
		documentDetailsDTO.setValue("ProofOfIdentity.jpg");
		documentDetailsDTO.setOwner("Self");
		
		identity.setProofOfIdentity(documentDetailsDTO);

		DocumentDetailsDTO documentDetailsResidenceDTO = new DocumentDetailsDTO();
		documentDetailsResidenceDTO.setDocument(DataProvider.getImageBytes("/proofOfAddress.jpg"));
		documentDetailsResidenceDTO.setCategory("Passport");
		documentDetailsResidenceDTO.setFormat("jpg");
		documentDetailsResidenceDTO.setValue("ProofOfAddress.jpg");
		documentDetailsResidenceDTO.setOwner("hof");
		
		identity.setProofOfAddress(documentDetailsResidenceDTO);

		documentDetailsDTO = new DocumentDetailsDTO();
		documentDetailsDTO.setDocument(DataProvider.getImageBytes("/proofOfAddress.jpg"));
		documentDetailsDTO.setCategory("Passport");
		documentDetailsDTO.setFormat("jpg");
		documentDetailsDTO.setValue("ProofOfRelationship.jpg");
		documentDetailsDTO.setOwner("Self");
		
		identity.setProofOfRelationship(documentDetailsDTO);

		documentDetailsResidenceDTO = new DocumentDetailsDTO();
		documentDetailsResidenceDTO.setDocument(DataProvider.getImageBytes("/proofOfAddress.jpg"));
		documentDetailsResidenceDTO.setCategory("Passport");
		documentDetailsResidenceDTO.setFormat("jpg");
		documentDetailsResidenceDTO.setValue("DateOfBirthProof.jpg");
		documentDetailsResidenceDTO.setOwner("hof");
		
		identity.setDateOfBirthProof(documentDetailsResidenceDTO);
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
