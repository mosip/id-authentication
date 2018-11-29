package io.mosip.registration.service.external.impl;

import static io.mosip.registration.constants.LoggerConstants.LOG_ZIP_CREATION;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;
import static io.mosip.registration.constants.RegistrationConstants.JSON_FILE_EXTENSION;
import static io.mosip.registration.constants.RegistrationExceptions.REG_IO_EXCEPTION;
import static java.io.File.separator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.stereotype.Service;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.biometric.BiometricInfoDTO;
import io.mosip.registration.dto.biometric.FingerprintDetailsDTO;
import io.mosip.registration.dto.biometric.IrisDetailsDTO;
import io.mosip.registration.dto.demographic.ApplicantDocumentDTO;
import io.mosip.registration.dto.demographic.DocumentDetailsDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.external.ZipCreationService;

/**
 * API Class to generate the in-memory zip file for Registration Packet.
 *
 * @author Balaji Sridharan
 * @since 1.0.0
 */
@Service
public class ZipCreationServiceImpl implements ZipCreationService {

	/** The logger. */
	private static final Logger LOGGER = AppConfig.getLogger(ZipCreationServiceImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.ZipCreationService#createPacket(io.mosip.
	 * registration.dto.RegistrationDTO, java.util.Map)
	 */
	@Override
	public byte[] createPacket(final RegistrationDTO registrationDTO, final Map<String, byte[]> jsonMap)
			throws RegBaseCheckedException {
		LOGGER.debug(LOG_ZIP_CREATION, APPLICATION_NAME, APPLICATION_ID, "Packet Zip had been called");

		try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)) {
			// Create folder structure for Biometric
			if (checkNotNull(registrationDTO)) {
				registrationDTO.getBiometricDTO();
				if (checkNotNull(registrationDTO.getBiometricDTO())) {
					String folderName;
					// Biometric -> Applicant Folder
					if (checkNotNull(registrationDTO.getBiometricDTO().getApplicantBiometricDTO())) {
						folderName = "Biometric".concat(separator).concat("Applicant").concat(separator);
						addBiometricImages(registrationDTO.getBiometricDTO().getApplicantBiometricDTO(), folderName,
								zipOutputStream);

						LOGGER.debug(LOG_ZIP_CREATION, APPLICATION_NAME, APPLICATION_ID, "Applicant's biometric added");
					}

					// Add Introducer Biometrics to packet zip
					if (checkNotNull(registrationDTO.getBiometricDTO().getIntroducerBiometricDTO())) {
						folderName = "Biometric".concat(separator).concat("Introducer").concat(separator);
						addBiometricImages(registrationDTO.getBiometricDTO().getIntroducerBiometricDTO(), folderName,
								zipOutputStream);

						LOGGER.debug(LOG_ZIP_CREATION, APPLICATION_NAME, APPLICATION_ID,
								"Introcucer's biometric added");
					}
				}

				// Create folder structure for Demographic
				if (checkNotNull(registrationDTO.getDemographicDTO())) {
					if (checkNotNull(registrationDTO.getDemographicDTO().getApplicantDocumentDTO())) {
						String folderName = "Demographic".concat(separator).concat("Applicant").concat(separator);
						addDemogrpahicData(registrationDTO.getDemographicDTO().getApplicantDocumentDTO(), folderName,
								zipOutputStream);

						LOGGER.debug(LOG_ZIP_CREATION, APPLICATION_NAME, APPLICATION_ID,
								"Applicant's demographic added");
					}
					writeFileToZip(
							"Demographic".concat(separator).concat("DemographicInfo").concat(JSON_FILE_EXTENSION),
							jsonMap.get(RegistrationConstants.DEMOGRPAHIC_JSON_NAME), zipOutputStream);

					LOGGER.debug(LOG_ZIP_CREATION, APPLICATION_NAME, APPLICATION_ID, "Demographic JSON added");
				}

				// Add the HMAC Info
				writeFileToZip("HMACFile.txt", jsonMap.get(RegistrationConstants.HASHING_JSON_NAME), zipOutputStream);

				LOGGER.debug(LOG_ZIP_CREATION, APPLICATION_NAME, APPLICATION_ID, "HMAC added");

				if (checkNotNull(registrationDTO.getBiometricDTO())) {
					if (checkNotNull(registrationDTO.getBiometricDTO().getSupervisorBiometricDTO())) {
						addOfficerBiometric("EnrollmentSupervisorBioImage",
								registrationDTO.getBiometricDTO().getSupervisorBiometricDTO(), zipOutputStream);
					}

					if (checkNotNull(registrationDTO.getBiometricDTO().getOperatorBiometricDTO())) {
						addOfficerBiometric("EnrollmentOfficerBioImage",
								registrationDTO.getBiometricDTO().getOperatorBiometricDTO(), zipOutputStream);
					}

					LOGGER.debug(LOG_ZIP_CREATION, APPLICATION_NAME, APPLICATION_ID, "Supervisor's Biometric added");
				}

				// Add Registration Meta JSON
				writeFileToZip("PacketMetaInfo".concat(JSON_FILE_EXTENSION),
						jsonMap.get(RegistrationConstants.PACKET_META_JSON_NAME), zipOutputStream);

				LOGGER.debug(LOG_ZIP_CREATION, APPLICATION_NAME, APPLICATION_ID, "Registration Packet Meta added");

				// Add Audits
				writeFileToZip(RegistrationConstants.AUDIT_JSON_FILE.concat(JSON_FILE_EXTENSION),
						jsonMap.get(RegistrationConstants.AUDIT_JSON_FILE), zipOutputStream);

				LOGGER.debug(LOG_ZIP_CREATION, APPLICATION_NAME, APPLICATION_ID, "Registration Audit Logs Meta added");

				zipOutputStream.flush();
				byteArrayOutputStream.flush();
				zipOutputStream.close();
				byteArrayOutputStream.close();
			}

			LOGGER.debug(LOG_ZIP_CREATION, APPLICATION_NAME, APPLICATION_ID, "Packet zip had been ended");

			return byteArrayOutputStream.toByteArray();
		} catch (IOException exception) {
			throw new RegBaseCheckedException(REG_IO_EXCEPTION.getErrorCode(), exception.getCause().getMessage());
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegistrationConstants.PACKET_ZIP_CREATION, runtimeException.toString());
		}
	}

	/**
	 * Adds the officer biometric.
	 *
	 * @param fileName
	 *            the file name
	 * @param supervisorBio
	 *            the supervisor bio
	 * @param zipOutputStream
	 *            the zip output stream
	 * @throws RegBaseCheckedException
	 *             the reg base checked exception
	 */
	private static void addOfficerBiometric(final String fileName, final BiometricInfoDTO supervisorBio,
			final ZipOutputStream zipOutputStream) throws RegBaseCheckedException {
		List<FingerprintDetailsDTO> fingerprintDetailsDTOs = supervisorBio.getFingerprintDetailsDTO();
		List<IrisDetailsDTO> irisDetailsDTOs = supervisorBio.getIrisDetailsDTO();

		if (checkNotNull(fingerprintDetailsDTOs) && !fingerprintDetailsDTOs.isEmpty()) {
			writeFileToZip(fingerprintDetailsDTOs.get(0).getFingerprintImageName(), fingerprintDetailsDTOs.get(0).getFingerPrint(), zipOutputStream);
		} else if (checkNotNull(irisDetailsDTOs) && !irisDetailsDTOs.isEmpty()) {
			writeFileToZip(irisDetailsDTOs.get(0).getIrisImageName(), irisDetailsDTOs.get(0).getIris(), zipOutputStream);
		}
	}

	/**
	 * Adds the demogrpahic data.
	 *
	 * @param applicantDocumentDTO
	 *            the applicant document DTO
	 * @param folderName
	 *            the folder name
	 * @param zipOutputStream
	 *            the zip output stream
	 * @throws RegBaseCheckedException
	 *             the reg base checked exception
	 */
	private static void addDemogrpahicData(final ApplicantDocumentDTO applicantDocumentDTO, final String folderName,
			final ZipOutputStream zipOutputStream) throws RegBaseCheckedException {
		// Add Proofs
		if (checkNotNull(applicantDocumentDTO.getDocumentDetailsDTO())) {
			for (DocumentDetailsDTO documentDetailsDTO : applicantDocumentDTO.getDocumentDetailsDTO()) {
				writeFileToZip(folderName + documentDetailsDTO.getDocumentName(), documentDetailsDTO.getDocument(),
						zipOutputStream);
			}
		}

		addToZip(applicantDocumentDTO.getPhoto(), folderName + applicantDocumentDTO.getPhotographName(),
				zipOutputStream);
		addToZip(applicantDocumentDTO.getExceptionPhoto(), folderName + applicantDocumentDTO.getExceptionPhotoName(),
				zipOutputStream);
		addToZip(applicantDocumentDTO.getAcknowledgeReceipt(),
				folderName + applicantDocumentDTO.getAcknowledgeReceiptName(), zipOutputStream);
	}

	/**
	 * Adds the to zip.
	 *
	 * @param content
	 *            the content
	 * @param fileNameWithPath
	 *            the file name with path
	 * @param zipOutputStream
	 *            the zip output stream
	 * @throws RegBaseCheckedException
	 *             the reg base checked exception
	 */
	private static void addToZip(final byte[] content, final String fileNameWithPath,
			final ZipOutputStream zipOutputStream) throws RegBaseCheckedException {
		if (checkNotNull(content)) {
			writeFileToZip(fileNameWithPath, content, zipOutputStream);
		}
	}

	/**
	 * Adds the biometric images.
	 *
	 * @param biometricDTO
	 *            the biometric DTO
	 * @param folderName
	 *            the folder name
	 * @param zipOutputStream
	 *            the zip output stream
	 * @throws RegBaseCheckedException
	 *             the reg base checked exception
	 */
	private static void addBiometricImages(final BiometricInfoDTO biometricDTO, String folderName,
			ZipOutputStream zipOutputStream) throws RegBaseCheckedException {
		// Biometric -> Applicant - Files
		// Add the Fingerprint images to zip folder structure
		if (checkNotNull(biometricDTO.getFingerprintDetailsDTO())) {
			for (FingerprintDetailsDTO fingerprintDetailsDTO : biometricDTO.getFingerprintDetailsDTO()) {
				writeFileToZip(folderName + fingerprintDetailsDTO.getFingerprintImageName(),
						fingerprintDetailsDTO.getFingerPrint(), zipOutputStream);
			}
		}

		// Add Iris Images to zip folder structure
		if (checkNotNull(biometricDTO.getIrisDetailsDTO())) {
			for (IrisDetailsDTO irisDetailsDTO : biometricDTO.getIrisDetailsDTO()) {
				writeFileToZip(folderName + irisDetailsDTO.getIrisImageName(), irisDetailsDTO.getIris(),
						zipOutputStream);
			}
		}
	}

	/**
	 * Check not null.
	 *
	 * @param object
	 *            the object
	 * @return true, if successful
	 */
	private static boolean checkNotNull(Object object) {
		return object != null;
	}

	/**
	 * Write file to zip.
	 *
	 * @param fileName
	 *            the file name
	 * @param file
	 *            the file
	 * @param zipOutputStream
	 *            the zip output stream
	 * @throws RegBaseCheckedException
	 *             the reg base checked exception
	 */
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
