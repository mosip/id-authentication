package io.mosip.registration.service.impl;

import java.util.LinkedList;
import java.util.List;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.logback.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.logback.factory.MosipLogfactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.registration.audit.AuditFactory;
import io.mosip.registration.constants.AppModule;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dao.AuditDAO;
import io.mosip.registration.dao.RegistrationDAO;
import io.mosip.registration.dto.AuditDTO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.AESEncryptionService;
import io.mosip.registration.service.PacketEncryptionService;
import io.mosip.registration.service.StorageService;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;
import static io.mosip.registration.constants.LoggerConstants.LOG_PKT_ENCRYPTION;

/**
 * This class encrypts the Registration packet using RSA and AES algorithms.
 * Then saves the encrypted packet and acknowledgement receipt in the specified location.
 * And adds an entry in the {@link Registration} table
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 */
@Service
public class PacketEncryptionServiceImpl implements PacketEncryptionService {

	/**
	 * Class to encrypt the data using AES Algorithm
	 */
	@Autowired
	private AESEncryptionService aesEncryptionService;
	/**
	 * Class to insert the Registration Details into DB
	 */
	@Autowired
	private RegistrationDAO registrationDAO;
	/**
	 * Instance of StorageManager
	 */
	@Autowired
	private StorageService storageService;
	@Autowired
	private AuditDAO auditDAO;
	/**
	 * Object for Logger
	 */
	private static MosipLogger logger;

	@Autowired
	private void initializeLogger(MosipRollingFileAppender mosipRollingFileAppender) {
		logger = MosipLogfactory.getMosipDefaultRollingFileLogger(mosipRollingFileAppender, this.getClass());
	}

	/**
	 * Instance of {@code AuditFactory}
	 */
	@Autowired
	private AuditFactory auditFactory;

	/**
	 * Encrypts the input data using AES algorithm followed by RSA
	 * 
	 * @param packetZipData
	 *            the data to be encrypted
	 * @return encrypted data as byte array
	 * @throws RegBaseCheckedException
	 */
	@Override
	public ResponseDTO encrypt(final RegistrationDTO registrationDTO, final byte[] packetZipData)
			throws RegBaseCheckedException {
		logger.debug(LOG_PKT_ENCRYPTION, APPLICATION_NAME,
				APPLICATION_ID, "Packet encryption had been started");
		try {
			// Encrypt the packet
			byte[] encryptedPacket = aesEncryptionService.encrypt(packetZipData);
			
			logger.debug(LOG_PKT_ENCRYPTION, APPLICATION_NAME,
					APPLICATION_ID, "Packet encrypted successfully");

			// Generate Zip File Name with absolute path
			String filePath = storageService.storeToDisk(registrationDTO.getRegistrationId(), encryptedPacket,
					registrationDTO.getDemographicDTO().getApplicantDocumentDTO().getAcknowledgeReceipt());
			
			logger.debug(LOG_PKT_ENCRYPTION, APPLICATION_NAME,
					APPLICATION_ID,
					"Encrypted Packet and Acknowledgement Receipt saved successfully");

			// Insert the Registration Details into DB
			registrationDAO.save(filePath, registrationDTO.getDemographicDTO().getDemoInUserLang().getFullName());
			
			logger.debug(LOG_PKT_ENCRYPTION, APPLICATION_NAME,
					APPLICATION_ID, "Encrypted Packet persisted");
			
			// Update the sync'ed audits
			// TODO: Below lines of code had been commented intentionally. Will be updated.
			//List<String> auditUUIDs = new LinkedList<>();
			//registrationDTO.getAuditDTOs().parallelStream().map(AuditDTO::getUuid).forEach(auditUUIDs::add);
			//auditDAO.updateSyncAudits(auditUUIDs);
			
			logger.debug(LOG_PKT_ENCRYPTION, APPLICATION_NAME,
					APPLICATION_ID, "Sync'ed audit logs updated");
			
			auditFactory.audit(AuditEvent.PACKET_ENCRYPTED, AppModule.PACKET_ENCRYPTOR,
					"Packet encrypted successfully", "registration reference id", "123456");
			
			logger.debug(LOG_PKT_ENCRYPTION, APPLICATION_NAME,
					APPLICATION_ID, "Packet encryption had been ended");
			
			// Return the Response Object
			ResponseDTO responseDTO = new ResponseDTO();
			SuccessResponseDTO successResponseDTO = new SuccessResponseDTO();
			successResponseDTO.setCode("0000");
			successResponseDTO.setMessage("Success");
			responseDTO.setSuccessResponseDTO(successResponseDTO);
			return responseDTO;
		} catch (RegBaseCheckedException checkedException) {
			throw checkedException;
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegistrationConstants.PACKET_ENCRYPTION_MANAGER,
					runtimeException.toString());
		}
	}
}
