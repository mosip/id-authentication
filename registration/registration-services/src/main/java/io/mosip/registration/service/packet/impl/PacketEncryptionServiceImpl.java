package io.mosip.registration.service.packet.impl;

import static io.mosip.registration.constants.LoggerConstants.LOG_PKT_ENCRYPTION;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.audit.AuditFactory;
import io.mosip.registration.builder.Builder;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.Components;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dao.AuditLogControlDAO;
import io.mosip.registration.dao.RegistrationDAO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.entity.AuditLogControl;
import io.mosip.registration.entity.Registration;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.exception.RegistrationExceptionConstants;
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
	 * Instance of {@link AuditLogControlDAO}
	 */
	@Autowired
	private AuditLogControlDAO auditLogControlDAO;

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
		LOGGER.info(LOG_PKT_ENCRYPTION, APPLICATION_NAME,
				APPLICATION_ID, "Packet encryption had been started");
		try {
			// Encrypt the packet
			byte[] encryptedPacket = aesEncryptionService.encrypt(packetZipData);
			
			LOGGER.info(LOG_PKT_ENCRYPTION, APPLICATION_NAME,
					APPLICATION_ID, "Packet encrypted successfully");
			
			// Validate the size of the generated registration packet
			long maxPacketSizeInBytes = Long.parseLong((String) ApplicationContext.getInstance().getApplicationMap()
					.get(RegistrationConstants.MAX_REG_PACKET_SIZE_IN_MB)) * 1024 * 1024;
			if (encryptedPacket.length > maxPacketSizeInBytes) {
				throw new RegBaseCheckedException(
						RegistrationExceptionConstants.REG_PACKET_SIZE_EXCEEDED_ERROR_CODE.getErrorCode(),
						RegistrationExceptionConstants.REG_PACKET_SIZE_EXCEEDED_ERROR_CODE.getErrorMessage());
			}

			LOGGER.info(LOG_PKT_ENCRYPTION, APPLICATION_NAME,
					APPLICATION_ID, "Packet size validated successfully");

			// Generate Zip File Name with absolute path
			String filePath = storageService.storeToDisk(registrationDTO.getRegistrationId(), encryptedPacket);
			
			LOGGER.info(LOG_PKT_ENCRYPTION, APPLICATION_NAME, APPLICATION_ID, "Encrypted Packet saved successfully");

			// Insert the Registration Details into DB
			registrationDAO.save(filePath, registrationDTO);
			
			LOGGER.info(LOG_PKT_ENCRYPTION, APPLICATION_NAME,
					APPLICATION_ID, "Registration details persisted to database");

			Timestamp currentTimestamp = Timestamp.valueOf(LocalDateTime.now());
			
			auditLogControlDAO.save(Builder.build(AuditLogControl.class)
					.with(auditLogControl -> auditLogControl
							.setAuditLogFromDateTime(registrationDTO.getAuditLogStartTime()))
					.with(auditLogControl -> auditLogControl
							.setAuditLogToDateTime(registrationDTO.getAuditLogEndTime()))
					.with(auditLogControl -> auditLogControl.setRegistrationId(registrationDTO.getRegistrationId()))
					.with(auditLogControl -> auditLogControl.setAuditLogSyncDateTime(currentTimestamp))
					.with(auditLogControl -> auditLogControl.setCrDtime(currentTimestamp))
					.with(auditLogControl -> auditLogControl
							.setCrBy(SessionContext.getInstance().getUserContext().getUserId()))
					.get());
			
			auditFactory.audit(AuditEvent.PACKET_ENCRYPTED, Components.PACKET_ENCRYPTOR,
					"Packet encrypted successfully", registrationDTO.getRegistrationId(),
					RegistrationConstants.REGISTRATION_ID);
			
			LOGGER.info(LOG_PKT_ENCRYPTION, APPLICATION_NAME,
					APPLICATION_ID, "Packet encryption had been ended");
			
			// Return the Response Object
			ResponseDTO responseDTO = new ResponseDTO();
			SuccessResponseDTO successResponseDTO = new SuccessResponseDTO();
			successResponseDTO.setCode("0000");
			successResponseDTO.setMessage("Success");
			responseDTO.setSuccessResponseDTO(successResponseDTO);
			return responseDTO;
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegistrationConstants.PACKET_ENCRYPTION_MANAGER,
					runtimeException.toString());
		}
	}
}
