package io.mosip.registration.service;

import java.util.LinkedList;
import java.util.List;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.factory.MosipLogfactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.registration.audit.AuditFactory;
import io.mosip.registration.constants.AppModuleEnum;
import io.mosip.registration.constants.AuditEventEnum;
import io.mosip.registration.constants.RegProcessorExceptionCode;
import io.mosip.registration.dao.AuditDAO;
import io.mosip.registration.dao.RegistrationDAO;
import io.mosip.registration.dto.AuditDTO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.packet.encryption.aes.AESEncryptionService;
import io.mosip.registration.util.store.StorageService;

import static io.mosip.registration.constants.RegConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegConstants.APPLICATION_NAME;
import static io.mosip.registration.constants.LoggerConstants.LOG_PKT_ENCRYPTION;

@Service
public class PacketEncryptionService {

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
			List<String> auditUUIDs = new LinkedList<>();
			registrationDTO.getAuditDTOs().parallelStream().map(AuditDTO::getUuid).forEach(auditUUIDs::add);
			auditDAO.updateSyncAudits(auditUUIDs);
			logger.debug(LOG_PKT_ENCRYPTION, APPLICATION_NAME,
					APPLICATION_ID, "Sync'ed audit logs updated");
			auditFactory.audit(AuditEventEnum.PACKET_ENCRYPTED, AppModuleEnum.PACKET_ENCRYPTOR,
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
			throw new RegBaseUncheckedException(RegProcessorExceptionCode.PACKET_ENCRYPTION_MANAGER,
					runtimeException.toString());
		}
	}
}
