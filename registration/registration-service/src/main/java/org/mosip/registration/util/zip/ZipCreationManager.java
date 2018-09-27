package org.mosip.registration.util.zip;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static java.io.File.separator;
import static org.mosip.registration.consts.RegConstants.IMAGE_TYPE;
import static org.mosip.registration.consts.RegConstants.JSON_FILE_EXTENSION;
import static org.mosip.registration.consts.RegProcessorExceptionEnum.REG_IO_EXCEPTION;

import org.mosip.kernel.core.spi.logging.MosipLogger;
import org.mosip.kernel.logger.appenders.MosipRollingFileAppender;
import org.mosip.kernel.logger.factory.MosipLogfactory;
import org.mosip.registration.consts.RegConstants;
import org.mosip.registration.consts.RegProcessorExceptionCode;
import org.mosip.registration.dto.EnrollmentDTO;
import org.mosip.registration.dto.PacketDTO;
import org.mosip.registration.dto.biometric.BiometricInfoDTO;
import org.mosip.registration.dto.biometric.FingerprintDetailsDTO;
import org.mosip.registration.dto.biometric.IrisDetailsDTO;
import org.mosip.registration.dto.demographic.ApplicantDocumentDTO;
import org.mosip.registration.dto.demographic.DocumentDetailsDTO;
import org.mosip.registration.exception.RegBaseCheckedException;
import org.mosip.registration.exception.RegBaseUncheckedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * API Class to generate the Enrollment Registration Structure for zip file
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
@Component
public class ZipCreationManager {

	private static MosipLogger LOGGER;
	@Autowired
	private void initializeLogger(MosipRollingFileAppender idaRollingFileAppender) {
		LOGGER = MosipLogfactory.getMosipDefaultRollingFileLogger(idaRollingFileAppender, this.getClass());
	}
	
	/**
	 * Returns the byte array of the packet zip file containing the Registration Details
	 * 
	 * @param enrollmentDTO
	 *            the Registration to be stored in zip file
	 * @return the byte array of packet zip file
	 * @throws RegBaseCheckedException 
	 */
	public static byte[] createPacket(final EnrollmentDTO enrollmentDTO, final Map<String, byte[]> jsonMap)
			throws RegBaseCheckedException {
		LOGGER.debug("REGISTRATION - PACKET_CREATION - ZIP_PACKET", "EnrollmentId", enrollmentDTO.getPacketDTO().getEnrollmentID(), "Packet Zip had been called");
		
		try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)) {
			// Create folder structure for Biometric
			PacketDTO packetDTO = enrollmentDTO.getPacketDTO();
			if (checkNotNull(packetDTO)) {
				packetDTO.getBiometricDTO();
				if (checkNotNull(packetDTO.getBiometricDTO())) {
					String folderName;
					// Biometric -> Applicant Folder
					if (checkNotNull(packetDTO.getBiometricDTO().getApplicantBiometricDTO())) {
						folderName = "Biometric".concat(separator).concat("Applicant").concat(separator);
						addBiometricImages(packetDTO.getBiometricDTO().getApplicantBiometricDTO(), folderName,
								zipOutputStream);
						LOGGER.debug("REGISTRATION - PACKET_CREATION - ZIP_PACKET", "EnrollmentId", enrollmentDTO.getPacketDTO().getEnrollmentID(), "Applicant's biometric added");
					}

					// Add HOF Biometrics to packet zip
					if (checkNotNull(packetDTO.getBiometricDTO().getHofBiometricDTO())) {
						folderName = "Biometric".concat(separator).concat("HOF").concat(separator);
						addBiometricImages(packetDTO.getBiometricDTO().getHofBiometricDTO(), folderName,
								zipOutputStream);
						LOGGER.debug("REGISTRATION - PACKET_CREATION - ZIP_PACKET", "EnrollmentId", enrollmentDTO.getPacketDTO().getEnrollmentID(), "HOF's biometric added");
					}

					// Add Introducer Biometrics to packet zip
					if (checkNotNull(packetDTO.getBiometricDTO().getIntroducerBiometricDTO())) {
						folderName = "Biometric".concat(separator).concat("Introducer").concat(separator);
						addBiometricImages(packetDTO.getBiometricDTO().getIntroducerBiometricDTO(), folderName,
								zipOutputStream);
						LOGGER.debug("REGISTRATION - PACKET_CREATION - ZIP_PACKET", "EnrollmentId", enrollmentDTO.getPacketDTO().getEnrollmentID(), "Introcucer's biometric added");
					}
				}

				// Create folder structure for Demographic
				if (checkNotNull(packetDTO.getDemographicDTO())) {
					if (checkNotNull(packetDTO.getDemographicDTO().getApplicantDocumentDTO())) {
						String folderName = "Demographic".concat(separator).concat("Applicant").concat(separator);
						addDemogrpahicData(packetDTO.getDemographicDTO().getApplicantDocumentDTO(), folderName,
								zipOutputStream);
						LOGGER.debug("REGISTRATION - PACKET_CREATION - ZIP_PACKET", "EnrollmentId", enrollmentDTO.getPacketDTO().getEnrollmentID(), "Applicant's demographic added");
					}
					writeFileToZip(
							"Demographic".concat(separator).concat("DemographicInfo").concat(JSON_FILE_EXTENSION),
							jsonMap.get(RegConstants.DEMOGRPAHIC_JSON_NAME), zipOutputStream);
					LOGGER.debug("REGISTRATION - PACKET_CREATION - ZIP_PACKET", "EnrollmentId", enrollmentDTO.getPacketDTO().getEnrollmentID(), "Demographic JSON added");
				}

				// Add the Enrollment ID
				writeFileToZip("EnrollmentID.txt", packetDTO.getEnrollmentID().getBytes(), zipOutputStream);
				LOGGER.debug("REGISTRATION - PACKET_CREATION - ZIP_PACKET", "EnrollmentId", enrollmentDTO.getPacketDTO().getEnrollmentID(), "Registration Id added");

				// Add the HMAC Info
				writeFileToZip("HMACFile.txt", jsonMap.get(RegConstants.HASHING_JSON_NAME), zipOutputStream);
				LOGGER.debug("REGISTRATION - PACKET_CREATION - ZIP_PACKET", "EnrollmentId", enrollmentDTO.getPacketDTO().getEnrollmentID(), "HMAC added");

				if (checkNotNull(packetDTO.getBiometricDTO())) {
					if (checkNotNull(packetDTO.getBiometricDTO().getSupervisorBiometricDTO())) {
						addOfficerBiometric("EnrollmentSupervisorBioImage",
								packetDTO.getBiometricDTO().getSupervisorBiometricDTO(), zipOutputStream);
					}

					if (checkNotNull(packetDTO.getBiometricDTO().getOperatorBiometricDTO())) {
						addOfficerBiometric("EnrollmentOfficerBioImage",
								packetDTO.getBiometricDTO().getOperatorBiometricDTO(), zipOutputStream);
					}
					LOGGER.debug("REGISTRATION - PACKET_CREATION - ZIP_PACKET", "EnrollmentId", enrollmentDTO.getPacketDTO().getEnrollmentID(), "Supervisor's Biometric added");
				}

				// Add Registration Meta JSON
				writeFileToZip("PacketMetaInfo".concat(JSON_FILE_EXTENSION),
						jsonMap.get(RegConstants.PACKET_META_JSON_NAME), zipOutputStream);
				LOGGER.debug("REGISTRATION - PACKET_CREATION - ZIP_PACKET", "EnrollmentId", enrollmentDTO.getPacketDTO().getEnrollmentID(), "Registration Packet Meta added");

				zipOutputStream.flush();
				byteArrayOutputStream.flush();
				zipOutputStream.close();
				byteArrayOutputStream.close();
			}

			LOGGER.debug("REGISTRATION - PACKET_CREATION - ZIP_PACKET", "EnrollmentId", enrollmentDTO.getPacketDTO().getEnrollmentID(), "Packet zip had been ended");
			return byteArrayOutputStream.toByteArray();
		} catch (IOException exception) {
			throw new RegBaseCheckedException(REG_IO_EXCEPTION.getErrorCode(), exception.getMessage());
		} catch (RegBaseUncheckedException uncheckedException) {
			throw new RegBaseUncheckedException(RegProcessorExceptionCode.PACKET_ZIP_CREATION,
					uncheckedException.getMessage());
		}
	}

	private static void addOfficerBiometric(final String fileName, final BiometricInfoDTO supervisorBio,
			final ZipOutputStream zipOutputStream) throws RegBaseCheckedException {
		List<FingerprintDetailsDTO> fingerprintDetailsDTOs = supervisorBio.getFingerprintDetailsDTO();

		if (checkNotNull(fingerprintDetailsDTOs)) {
			for (FingerprintDetailsDTO fingerPrint: fingerprintDetailsDTOs) {
				writeFileToZip(fileName + IMAGE_TYPE, fingerPrint.getFingerPrint(),
						zipOutputStream); break; 
			}
			
			/*fingerprintDetailsDTOs.forEach(fingerPrint -> {
				try {
					writeFileToZip(fileName + IMAGE_TYPE, fingerPrint.getFingerPrint(), zipOutputStream);
				} catch (RegBaseCheckedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});*/
		}
	}

	private static void addDemogrpahicData(final ApplicantDocumentDTO applicantDocumentDTO, final String folderName,
			final ZipOutputStream zipOutputStream) throws RegBaseCheckedException {
		// Add Proofs
		if (checkNotNull(applicantDocumentDTO.getDocumentDetailsDTO())) {
			for (DocumentDetailsDTO documentDetailsDTO : applicantDocumentDTO.getDocumentDetailsDTO()) {
				writeFileToZip(folderName
						+ RegConstants.DOCUMENT_TYPES_MAP.get(documentDetailsDTO.getDocumentCategory().toLowerCase())
						+ RegConstants.DOC_TYPE, documentDetailsDTO.getDocument(), zipOutputStream);
			}
			/*
			 * applicantDocumentDTO.getDocumentDetailsDTO().forEach(documentDetailsDTO ->
			 * writeFileToZip(folderName +
			 * RegConstants.DOCUMENT_TYPES_MAP.get(documentDetailsDTO.getDocumentCategory()
			 * .toLowerCase()) + RegConstants.DOC_TYPE, documentDetailsDTO.getDocument(),
			 * zipOutputStream));
			 */
		}

		addToZip(applicantDocumentDTO.getPhoto(), folderName + "ApplicantPhoto" + IMAGE_TYPE, zipOutputStream);
		addToZip(applicantDocumentDTO.getExceptionPhoto(), folderName + "ExceptionPhoto" + IMAGE_TYPE, zipOutputStream);
		addToZip(applicantDocumentDTO.getAcknowledgeReceipt(), folderName + "EnrollmentAcknowledgement" + IMAGE_TYPE,
				zipOutputStream);
	}

	private static void addToZip(final byte[] content, final String fileNameWithPath, final ZipOutputStream zipOutputStream)
			throws RegBaseCheckedException {
		if (checkNotNull(content)) {
			writeFileToZip(fileNameWithPath, content, zipOutputStream);
		}
	}

	private static void addBiometricImages(final BiometricInfoDTO biometricDTO, String folderName,
			ZipOutputStream zipOutputStream) throws RegBaseCheckedException {
		// Biometric -> Applicant - Files
		// Add the Fingerprint images to zip folder structure
		if (checkNotNull(biometricDTO.getFingerprintDetailsDTO())) {
			for (FingerprintDetailsDTO fingerprintDetailsDTO : biometricDTO.getFingerprintDetailsDTO()) {
				writeFileToZip(
						folderName + RegConstants.FINGERPRINT_IMAGE_NAMES_MAP
								.get(fingerprintDetailsDTO.getFingerType().toLowerCase()) + IMAGE_TYPE,
						fingerprintDetailsDTO.getFingerPrint(), zipOutputStream);
			}
			/*
			 * biometricDTO.getFingerprintDetailsDTO().forEach(fingerprintDetailsDTO ->
			 * writeFileToZip(folderName +
			 * RegConstants.FINGERPRINT_IMAGE_NAMES_MAP.get(fingerprintDetailsDTO.
			 * getFingerType().toLowerCase()) + IMAGE_TYPE,
			 * fingerprintDetailsDTO.getFingerPrint(), zipOutputStream));
			 */
		}

		// Add Iris Images to zip folder structure
		if (checkNotNull(biometricDTO.getIrisDetailsDTO())) {
			for (IrisDetailsDTO irisDetailsDTO : biometricDTO.getIrisDetailsDTO()) {
				writeFileToZip(
						folderName + RegConstants.IRIS_IMAGE_NAMES_MAP.get(irisDetailsDTO.getIrisType().toLowerCase())
								+ IMAGE_TYPE,
						irisDetailsDTO.getIris(), zipOutputStream);
			}
			/*
			 * biometricDTO.getIrisDetailsDTO().forEach(irisDetailsDTO ->
			 * writeFileToZip(folderName +
			 * RegConstants.IRIS_IMAGE_NAMES_MAP.get(irisDetailsDTO.getIrisType().
			 * toLowerCase()) + IMAGE_TYPE, irisDetailsDTO.getIris(), zipOutputStream));
			 */
		}
	}

	private static boolean checkNotNull(Object object) {
		return object != null;
	}

	private static void writeFileToZip(String fileName, byte[] file, ZipOutputStream zipOutputStream)
			throws RegBaseCheckedException {
		try {
			// TODO : To be replaced with core kernal util class.
			final ZipEntry zipEntry = new ZipEntry(fileName);
			zipOutputStream.putNextEntry(zipEntry);
			zipOutputStream.write(file);
			zipOutputStream.flush();
		} catch (IOException ioException) {
			throw new RegBaseCheckedException(REG_IO_EXCEPTION.getErrorCode(), REG_IO_EXCEPTION.getErrorMessage());
		}
	}
	
}
