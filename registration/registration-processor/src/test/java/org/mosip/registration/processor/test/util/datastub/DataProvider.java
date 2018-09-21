package org.mosip.registration.processor.test.util.datastub;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.mosip.registration.processor.dto.AuditDTO;
import org.mosip.registration.processor.dto.EnrollmentDTO;
import org.mosip.registration.processor.dto.EnrollmentMetaDataDTO;
import org.mosip.registration.processor.dto.OSIDataDTO;
import org.mosip.registration.processor.dto.PacketDTO;
import org.mosip.registration.processor.dto.PacketMetaDataDTO;
import org.mosip.registration.processor.dto.biometric.BiometricDTO;
import org.mosip.registration.processor.dto.biometric.BiometricInfoDTO;
import org.mosip.registration.processor.dto.biometric.ExceptionFingerprintDetailsDTO;
import org.mosip.registration.processor.dto.biometric.ExceptionIrisDetailsDTO;
import org.mosip.registration.processor.dto.biometric.FingerprintDetailsDTO;
import org.mosip.registration.processor.dto.biometric.IrisDetailsDTO;
import org.mosip.registration.processor.dto.demographic.AddressDTO;
import org.mosip.registration.processor.dto.demographic.ApplicantDocumentDTO;
import org.mosip.registration.processor.dto.demographic.DemographicDTO;
import org.mosip.registration.processor.dto.demographic.DemographicInfoDTO;
import org.mosip.registration.processor.dto.demographic.DocumentDetailsDTO;

import com.fasterxml.jackson.databind.ObjectMapper;

public class DataProvider {
	public static ObjectMapper mapper = new ObjectMapper();

	public static byte[] getImageBytes(String filePath) throws IOException, URISyntaxException {
		DataProvider dataProvider=new DataProvider();
		File file = new File(dataProvider.getClass().getResource(filePath).toURI());
		byte[] bytesArray = new byte[(int) file.length()];
		FileInputStream fileInputStream = new FileInputStream(file);
		fileInputStream.read(bytesArray);
		fileInputStream.close();

		return bytesArray;
	}

	public static EnrollmentDTO getEnrollmentDTO() throws IOException, URISyntaxException {
		EnrollmentDTO enrollmentDTO = new EnrollmentDTO();
		enrollmentDTO.setPacketDTO(DataProvider.getPacketDTO());
		enrollmentDTO.setEnrollmentMetaDataDTO(DataProvider.getEnrollmentMetaDataDTO());
		return enrollmentDTO;

	}

	private static EnrollmentMetaDataDTO getEnrollmentMetaDataDTO() {
		EnrollmentMetaDataDTO enrollmentMetaDataDTO = new EnrollmentMetaDataDTO();
		enrollmentMetaDataDTO.setMachineId("D4-6D-6D-30-21-86");
		/*enrollmentMetaDataDTO.setPacketStatus("Approved");
		enrollmentMetaDataDTO.setApproverName("Balaji");
		enrollmentMetaDataDTO.setApproverId("5678910ABC");
		Date date=new Date();
		enrollmentMetaDataDTO.setApprovedDate(date.getDate());
		enrollmentMetaDataDTO.setCheckSum(CheckSumUtil.checkSumMap);
		enrollmentMetaDataDTO.setComments("Registration approved");*/
		return enrollmentMetaDataDTO;
	}

	private static PacketDTO getPacketDTO() throws IOException, URISyntaxException {
		PacketDTO packetDTO = new PacketDTO();
		packetDTO.setAuditDTOs(DataProvider.getAuditDTOs());
		packetDTO.setOsiDataDTO(DataProvider.getOsiDataDTO());
		packetDTO.setPacketMetaDataDTO(DataProvider.getPacketMetaDataDTO());
		packetDTO.setPreEnrollmentId("PEN1345T");
		packetDTO.setEnrollmentID("ED786878");
		packetDTO.setDemographicDTO(DataProvider.getDemographicDTO());
		packetDTO.setBiometricDTO(DataProvider.getBiometricDTO());
		return packetDTO;
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
		
		FingerprintDetailsDTO fingerprintDetailsDTO1 = new FingerprintDetailsDTO();

		fingerprintDetailsDTO1.setFingerPrint(DataProvider.getImageBytes("/thumb.jpg"));
		fingerprintDetailsDTO1.setFingerPrintName("leftThumb");
		fingerprintDetailsDTO1.setFingerType("leftThumb");
		fingerprintDetailsDTO1.setForceCaptured(false);
		fingerprintDetailsDTO1.setQualityScore(80);

		fingerList.add(fingerprintDetailsDTO1);
		fingerprintDetailsDTO1 = new FingerprintDetailsDTO();

		fingerprintDetailsDTO1.setFingerPrint(DataProvider.getImageBytes("/thumb.jpg"));
		fingerprintDetailsDTO1.setFingerPrintName("rightThumb");
		fingerprintDetailsDTO1.setFingerType("rightThumb");
		fingerprintDetailsDTO1.setForceCaptured(false);
		fingerprintDetailsDTO1.setQualityScore(80);

		fingerList.add(fingerprintDetailsDTO1);

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
		exceptionFingerprint.setMissingFinger("LeftThumb");

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
		irisDetailsDTO = new IrisDetailsDTO();

		irisDetailsDTO.setIris(DataProvider.getImageBytes("/eye.jpg"));
		irisDetailsDTO.setIrisName("rightEye");
		irisDetailsDTO.setIrisType("rightEye");
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
		exceptionIrisDetailsDTO.setMissingIris("righteye");
		irisExcepList.add(exceptionIrisDetailsDTO);
		exceptionIrisDetailsDTO = new ExceptionIrisDetailsDTO();
		exceptionIrisDetailsDTO.setExceptionDescription("by birth");
		exceptionIrisDetailsDTO.setExceptionType("Permananent");
		exceptionIrisDetailsDTO.setMissingIris("Lefteye");
		irisExcepList.add(exceptionIrisDetailsDTO);

		return irisExcepList;
	}

	private static DemographicDTO getDemographicDTO() throws IOException, URISyntaxException {
		DemographicDTO demographicDTO = new DemographicDTO();
		demographicDTO.setApplicantDocumentDTO(DataProvider.setApplicantDocumentDTO());
		demographicDTO.setHofEnrollmentID("HOF00233ID");
		demographicDTO.setHofUIN("HOF003");
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

	private static PacketMetaDataDTO getPacketMetaDataDTO() {
		PacketMetaDataDTO packetMetaDataDTO = new PacketMetaDataDTO();
		packetMetaDataDTO.setApplicationCategory("Regular");
		packetMetaDataDTO.setApplicationType("New Enrolment");
		packetMetaDataDTO.setGeoLatitudeLoc(13.0049);
		packetMetaDataDTO.setGeoLongitudeLoc(80.24492);
		return packetMetaDataDTO;
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
		AuditDTO audit = new AuditDTO();
		audit.setApplicationID("2343243");
		audit.setEndTimestamp("Thu Jan 23 00:10:00 IST 1992");
		audit.setStartTimestamp("Thu Jan 23 00:10:00 IST 1992");
		audit.setEventId("e101");
		auditDTOList.add(audit);
		audit.setApplicationID("3343543");
		audit.setEndTimestamp("Fri Jan 27 00:10:00 IST 1992");
		audit.setStartTimestamp("Mon Jan 23 00:10:00 IST 1996");
		audit.setEventId("e105");
		return auditDTOList;
	}

	
}
