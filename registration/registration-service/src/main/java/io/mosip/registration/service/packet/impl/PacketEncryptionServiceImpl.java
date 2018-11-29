package io.mosip.registration.service.packet.impl;

import static io.mosip.registration.constants.LoggerConstants.LOG_PKT_ENCRYPTION;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.audit.AuditFactory;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.Components;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dao.AuditDAO;
import io.mosip.registration.dao.RegistrationDAO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.AESEncryptionService;
import io.mosip.registration.service.external.StorageService;
import io.mosip.registration.service.packet.PacketEncryptionService;

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
	private static final Logger LOGGER = AppConfig.getLogger(PacketEncryptionServiceImpl.class);

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
		LOGGER.debug(LOG_PKT_ENCRYPTION, APPLICATION_NAME,
				APPLICATION_ID, "Packet encryption had been started");
		try {
			// Encrypt the packet
			byte[] encryptedPacket = aesEncryptionService.encrypt(packetZipData);
			
			LOGGER.debug(LOG_PKT_ENCRYPTION, APPLICATION_NAME,
					APPLICATION_ID, "Packet encrypted successfully");

			// Generate Zip File Name with absolute path
			String filePath = storageService.storeToDisk(registrationDTO.getRegistrationId(), encryptedPacket,
					registrationDTO.getDemographicDTO().getApplicantDocumentDTO().getAcknowledgeReceipt());
			
			LOGGER.debug(LOG_PKT_ENCRYPTION, APPLICATION_NAME,
					APPLICATION_ID,
					"Encrypted Packet and Acknowledgement Receipt saved successfully");

			// Insert the Registration Details into DB
			registrationDAO.save(filePath, registrationDTO.getDemographicDTO().getDemoInUserLang().getFullName());
			
			LOGGER.debug(LOG_PKT_ENCRYPTION, APPLICATION_NAME,
					APPLICATION_ID, "Encrypted Packet persisted");
			
			// Update the sync'ed audits
			// TODO: Below lines of code had been commented intentionally. Will be updated.
			//List<String> auditUUIDs = new LinkedList<>();
			//registrationDTO.getAuditDTOs().parallelStream().map(AuditDTO::getUuid).forEach(auditUUIDs::add);
			//auditDAO.updateSyncAudits(auditUUIDs);
			
			LOGGER.debug(LOG_PKT_ENCRYPTION, APPLICATION_NAME,
					APPLICATION_ID, "Sync'ed audit logs updated");
			
			auditFactory.audit(AuditEvent.PACKET_ENCRYPTED, Components.PACKET_ENCRYPTOR,
					"Packet encrypted successfully", "registration reference id", "123456");
			
			LOGGER.debug(LOG_PKT_ENCRYPTION, APPLICATION_NAME,
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
