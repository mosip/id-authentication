package io.mosip.registration.service.external.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static java.io.File.separator;

import org.springframework.stereotype.Service;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.demographic.DemographicDTO;
import io.mosip.registration.dto.demographic.DocumentDetailsDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.external.ZipCreationService;

import static io.mosip.registration.constants.LoggerConstants.LOG_ZIP_CREATION;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;
import static io.mosip.registration.constants.RegistrationConstants.JSON_FILE_EXTENSION;
import static io.mosip.registration.exception.RegistrationExceptionConstants.REG_IO_EXCEPTION;

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
	public byte[] createPacket(final RegistrationDTO registrationDTO, final Map<String, byte[]> filesGeneratedForPacket)
			throws RegBaseCheckedException {
		LOGGER.debug(LOG_ZIP_CREATION, APPLICATION_NAME, APPLICATION_ID, "Packet Zip had been called");

		try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)) {
			// Create folder structure for Biometric

			registrationDTO.getBiometricDTO();
			if (checkNotNull(registrationDTO.getBiometricDTO())) {
				// Biometric -> Applicant Folder
				if (filesGeneratedForPacket.containsKey(RegistrationConstants.APPLICANT_BIO_CBEFF_FILE_NAME)) {
					writeFileToZip(
							"Biometric".concat(separator).concat(RegistrationConstants.APPLICANT_BIO_CBEFF_FILE_NAME),
							filesGeneratedForPacket.get(RegistrationConstants.APPLICANT_BIO_CBEFF_FILE_NAME),
							zipOutputStream);

					LOGGER.debug(LOG_ZIP_CREATION, APPLICATION_NAME, APPLICATION_ID, "Applicant's biometric added");
				}

				// Add Introducer Biometrics to packet zip
				if (filesGeneratedForPacket.containsKey(RegistrationConstants.INTRODUCER_BIO_CBEFF_FILE_NAME)) {
					writeFileToZip(
							"Biometric".concat(separator).concat(RegistrationConstants.INTRODUCER_BIO_CBEFF_FILE_NAME),
							filesGeneratedForPacket.get(RegistrationConstants.INTRODUCER_BIO_CBEFF_FILE_NAME),
							zipOutputStream);

					LOGGER.debug(LOG_ZIP_CREATION, APPLICATION_NAME, APPLICATION_ID, "Introcucer's biometric added");
				}
			}

			// Create folder structure for Demographic
			if (checkNotNull(registrationDTO.getDemographicDTO())) {
				if (checkNotNull(registrationDTO.getDemographicDTO().getApplicantDocumentDTO())) {
					addDemogrpahicData(registrationDTO.getDemographicDTO(), "Demographic".concat(separator),
							zipOutputStream);

					LOGGER.debug(LOG_ZIP_CREATION, APPLICATION_NAME, APPLICATION_ID, "Applicant's demographic added");
				}
				writeFileToZip("Demographic".concat(separator).concat(RegistrationConstants.DEMOGRPAHIC_JSON_NAME),
						filesGeneratedForPacket.get(RegistrationConstants.DEMOGRPAHIC_JSON_NAME), zipOutputStream);

				LOGGER.debug(LOG_ZIP_CREATION, APPLICATION_NAME, APPLICATION_ID, "Demographic JSON added");
			}

			// Add the HMAC Info
			writeFileToZip(RegistrationConstants.PACKET_DATA_HASH_FILE_NAME,
					filesGeneratedForPacket.get(RegistrationConstants.PACKET_DATA_HASH_FILE_NAME), zipOutputStream);

			LOGGER.debug(LOG_ZIP_CREATION, APPLICATION_NAME, APPLICATION_ID, "HMAC added");

			if (checkNotNull(registrationDTO.getBiometricDTO())) {
				if (filesGeneratedForPacket.containsKey(RegistrationConstants.SUPERVISOR_BIO_CBEFF_FILE_NAME)) {
					writeFileToZip(RegistrationConstants.SUPERVISOR_BIO_CBEFF_FILE_NAME,
							filesGeneratedForPacket.get(RegistrationConstants.SUPERVISOR_BIO_CBEFF_FILE_NAME),
							zipOutputStream);
				}

				if (filesGeneratedForPacket.containsKey(RegistrationConstants.OFFICER_BIO_CBEFF_FILE_NAME)) {
					writeFileToZip(RegistrationConstants.OFFICER_BIO_CBEFF_FILE_NAME,
							filesGeneratedForPacket.get(RegistrationConstants.OFFICER_BIO_CBEFF_FILE_NAME),
							zipOutputStream);
				}

				LOGGER.debug(LOG_ZIP_CREATION, APPLICATION_NAME, APPLICATION_ID, "Supervisor's Biometric added");
			}

			// Add Registration Meta JSON
			writeFileToZip(RegistrationConstants.PACKET_META_JSON_NAME,
					filesGeneratedForPacket.get(RegistrationConstants.PACKET_META_JSON_NAME), zipOutputStream);

			LOGGER.debug(LOG_ZIP_CREATION, APPLICATION_NAME, APPLICATION_ID, "Registration Packet Meta added");

			// Add Audits
			writeFileToZip(RegistrationConstants.AUDIT_JSON_FILE.concat(JSON_FILE_EXTENSION),
					filesGeneratedForPacket.get(RegistrationConstants.AUDIT_JSON_FILE), zipOutputStream);

			LOGGER.debug(LOG_ZIP_CREATION, APPLICATION_NAME, APPLICATION_ID, "Registration Audit Logs Meta added");

			// Add Packet_OSI_HASH
			writeFileToZip(RegistrationConstants.PACKET_OSI_HASH_FILE_NAME,
					filesGeneratedForPacket.get(RegistrationConstants.PACKET_OSI_HASH_FILE_NAME), zipOutputStream);

			LOGGER.debug(LOG_ZIP_CREATION, APPLICATION_NAME, APPLICATION_ID, "Registration packet_osi_hash added");

			zipOutputStream.flush();
			byteArrayOutputStream.flush();
			zipOutputStream.close();
			byteArrayOutputStream.close();

			LOGGER.debug(LOG_ZIP_CREATION, APPLICATION_NAME, APPLICATION_ID, "Packet zip had been ended");

			return byteArrayOutputStream.toByteArray();
		} catch (IOException exception) {
			throw new RegBaseCheckedException(REG_IO_EXCEPTION.getErrorCode(), exception.getCause().getMessage());
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegistrationConstants.PACKET_ZIP_CREATION, runtimeException.toString());
		}
	}

	/**
	 * Adds the demogrpahic data.
	 *
	 * @param demographicDTO
	 *            the applicant document DTO
	 * @param folderName
	 *            the folder name
	 * @param zipOutputStream
	 *            the zip output stream
	 * @throws RegBaseCheckedException
	 *             the reg base checked exception
	 */
	private static void addDemogrpahicData(final DemographicDTO demographicDTO, final String folderName,
			final ZipOutputStream zipOutputStream) throws RegBaseCheckedException {
		// Add Proofs
		DocumentDetailsDTO documentDetailsDTO = demographicDTO.getDemographicInfoDTO().getIdentity()
				.getProofOfIdentity();

		if (documentDetailsDTO != null) {
			writeFileToZip(folderName + getFileNameWithExt(documentDetailsDTO), documentDetailsDTO.getDocument(),
					zipOutputStream);
		}

		documentDetailsDTO = demographicDTO.getDemographicInfoDTO().getIdentity().getProofOfAddress();

		if (documentDetailsDTO != null) {
			writeFileToZip(folderName + getFileNameWithExt(documentDetailsDTO), documentDetailsDTO.getDocument(),
					zipOutputStream);
		}

		documentDetailsDTO = demographicDTO.getDemographicInfoDTO().getIdentity().getProofOfRelationship();

		if (documentDetailsDTO != null) {
			writeFileToZip(folderName + getFileNameWithExt(documentDetailsDTO), documentDetailsDTO.getDocument(),
					zipOutputStream);
		}

		documentDetailsDTO = demographicDTO.getDemographicInfoDTO().getIdentity().getProofOfDateOfBirth();

		if (documentDetailsDTO != null) {
			writeFileToZip(folderName + getFileNameWithExt(documentDetailsDTO), documentDetailsDTO.getDocument(),
					zipOutputStream);
		}

		addToZip(demographicDTO.getApplicantDocumentDTO().getPhoto(),
				folderName + demographicDTO.getApplicantDocumentDTO().getPhotographName(), zipOutputStream);
		addToZip(demographicDTO.getApplicantDocumentDTO().getExceptionPhoto(),
				folderName + demographicDTO.getApplicantDocumentDTO().getExceptionPhotoName(), zipOutputStream);
		if (demographicDTO.getApplicantDocumentDTO().getAcknowledgeReceipt() != null) {
			addToZip(demographicDTO.getApplicantDocumentDTO().getAcknowledgeReceipt(),
					folderName + demographicDTO.getApplicantDocumentDTO().getAcknowledgeReceiptName(), zipOutputStream);
		}
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
			// TODO : To be replaced with core kernel util class.
			final ZipEntry zipEntry = new ZipEntry(fileName);
			zipOutputStream.putNextEntry(zipEntry);
			zipOutputStream.write(file);
			zipOutputStream.flush();
		} catch (IOException ioException) {
			throw new RegBaseCheckedException(REG_IO_EXCEPTION.getErrorCode(), REG_IO_EXCEPTION.getErrorMessage());
		}
	}

	private static String getFileNameWithExt(DocumentDetailsDTO documentDetailsDTO) {
		return documentDetailsDTO.getValue().concat(".").concat(documentDetailsDTO.getFormat());
	}

}
