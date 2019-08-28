package io.mosip.registration.service.packet.impl;

import static io.mosip.registration.constants.LoggerConstants.LOG_PKT_ENCRYPTION;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.registration.audit.AuditManagerService;
import io.mosip.registration.builder.Builder;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.AuditReferenceIdTypes;
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
import io.mosip.registration.service.BaseService;
import io.mosip.registration.service.external.StorageService;
import io.mosip.registration.service.packet.PacketEncryptionService;
import io.mosip.registration.service.security.AESEncryptionService;

/**
 * This class encrypts the Registration packet using RSA and AES algorithms.
 * Then saves the encrypted packet and acknowledgement receipt in the specified location.
 * And adds an entry in the {@link Registration} table
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 */
@Service
public class PacketEncryptionServiceImpl extends BaseService implements PacketEncryptionService {

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
	private AuditManagerService auditFactory;

	/**
	 * Instance of {@link AuditLogControlDAO}
	 */
	@Autowired
	private AuditLogControlDAO auditLogControlDAO;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.service.packet.PacketEncryptionService#encrypt(io.mosip
	 * .registration.dto.RegistrationDTO, byte[])
	 */
	@Override
	public ResponseDTO encrypt(final RegistrationDTO registrationDTO, final byte[] packetZipData)
			throws RegBaseCheckedException {
		LOGGER.info(LOG_PKT_ENCRYPTION, APPLICATION_NAME, APPLICATION_ID, "Packet encryption had been started");

		String rid = registrationDTO == null ? "RID not available" : registrationDTO.getRegistrationId();

		try {
			// Validate the input parameters and required configuration parameters
			validateInputData(registrationDTO, packetZipData);

			// Encrypt the packet
			byte[] encryptedPacket = aesEncryptionService.encrypt(packetZipData);

			LOGGER.info(LOG_PKT_ENCRYPTION, APPLICATION_NAME, APPLICATION_ID, "Packet encrypted successfully");

			// Validate the size of the generated registration packet
			long maxPacketSizeInBytes = Long.valueOf(
					String.valueOf(ApplicationContext.map().get(RegistrationConstants.REG_PKT_SIZE))) * 1024 * 1024;
			if (encryptedPacket.length > maxPacketSizeInBytes) {
				LOGGER.error(LOG_PKT_ENCRYPTION, APPLICATION_NAME, APPLICATION_ID,
						String.format("%s --> %s",
								RegistrationExceptionConstants.REG_PACKET_SIZE_EXCEEDED_ERROR_CODE.getErrorCode(),
								RegistrationExceptionConstants.REG_PACKET_SIZE_EXCEEDED_ERROR_CODE.getErrorMessage()));
			}

			LOGGER.info(LOG_PKT_ENCRYPTION, APPLICATION_NAME, APPLICATION_ID, "Packet size validated successfully");

			// Generate Zip File Name with absolute path
			String filePath = storageService.storeToDisk(rid, encryptedPacket);

			LOGGER.info(LOG_PKT_ENCRYPTION, APPLICATION_NAME, APPLICATION_ID, "Encrypted Packet saved successfully");

			// Insert the Registration Details into DB
			registrationDAO.save(filePath, registrationDTO);
			
			LOGGER.info(LOG_PKT_ENCRYPTION, APPLICATION_NAME,
					APPLICATION_ID, "Registration details persisted to database");

			auditLogControlDAO.save(Builder.build(AuditLogControl.class)
					.with(auditLogControl -> auditLogControl
							.setAuditLogFromDateTime(registrationDTO.getAuditLogStartTime()))
					.with(auditLogControl -> auditLogControl
							.setAuditLogToDateTime(registrationDTO.getAuditLogEndTime()))
					.with(auditLogControl -> auditLogControl.setRegistrationId(registrationDTO.getRegistrationId()))
					.with(auditLogControl -> auditLogControl.setAuditLogSyncDateTime(Timestamp.valueOf(DateUtils.getUTCCurrentDateTime())))
					.with(auditLogControl -> auditLogControl.setCrDtime(Timestamp.valueOf(DateUtils.getUTCCurrentDateTime())))
					.with(auditLogControl -> auditLogControl
							.setCrBy(SessionContext.userContext().getUserId()))
					.get());
			
			LOGGER.info(LOG_PKT_ENCRYPTION, APPLICATION_NAME,
					APPLICATION_ID, "Sync audit logs updated");
			
			auditFactory.audit(AuditEvent.PACKET_ENCRYPTED, Components.PACKET_ENCRYPTOR,
					registrationDTO.getRegistrationId(), AuditReferenceIdTypes.REGISTRATION_ID.getReferenceTypeId());
			
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
			throw new RegBaseUncheckedException(
					RegistrationExceptionConstants.REG_PACKET_ENCRYPTION_EXCEPTION.getErrorCode(),
					RegistrationExceptionConstants.REG_PACKET_ENCRYPTION_EXCEPTION.getErrorMessage(), runtimeException);
		} finally {
			LOGGER.info(LOG_PKT_ENCRYPTION, APPLICATION_NAME, APPLICATION_ID,
					String.format("Registration Process end for RID  : [ %s ] ", rid));
		}
	}

	private void validateInputData(final RegistrationDTO registration, final byte[] dataToBeEncrypted)
			throws RegBaseCheckedException {
		if (ApplicationContext.map().get(RegistrationConstants.REG_PKT_SIZE) == null
				|| !String.valueOf(ApplicationContext.map().get(RegistrationConstants.REG_PKT_SIZE))
						.matches(RegistrationConstants.NUMBER_REGEX)) {
			throwRegBaseCheckedException(RegistrationExceptionConstants.REG_PACKET_SIZE_INVALID);
		}

		if (registration == null || isStringEmpty(registration.getRegistrationId())
				|| registration.getAuditLogStartTime() == null || registration.getAuditLogEndTime() == null) {
			throwRegBaseCheckedException(RegistrationExceptionConstants.REG_PACKET_AUDIT_DATES_MISSING);
		}

		if (isByteArrayEmpty(dataToBeEncrypted)) {
			throwRegBaseCheckedException(RegistrationExceptionConstants.REG_PACKET_TO_BE_ENCRYPTED_INVALID);
		}
	}

}
