package io.mosip.registration.service;

import static io.mosip.registration.constants.RegConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegConstants.APPLICATION_NAME;
import static io.mosip.registration.util.reader.PropertyFileReader.getPropertyValue;

import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.logger.logback.appender.MosipRollingFileAppender;
import io.mosip.kernel.logger.logback.factory.MosipLogfactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.registration.audit.AuditFactory;
import io.mosip.registration.constants.AppModuleEnum;
import io.mosip.registration.constants.AuditEventEnum;
import io.mosip.registration.constants.RegProcessorExceptionCode;
import io.mosip.registration.dao.RegistrationDAO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.packet.encryption.aes.AESEncryptionManager;
import io.mosip.registration.util.store.StorageManager;

@Service
public class PacketEncryptionService {

	/**
	 * Class to encrypt the data using AES Algorithm
	 */
	@Autowired
	private AESEncryptionManager aesEncryptionManager;
	/**
	 * Class to insert the Registration Details into DB
	 */
	@Autowired
	private RegistrationDAO registrationDAO;
	/**
	 * Instance of StorageManager
	 */
	@Autowired
	private StorageManager storageManager;
	/**
	 * Object for Logger
	 */
	private static MosipLogger LOGGER;

	@Autowired
	private void initializeLogger(MosipRollingFileAppender mosipRollingFileAppender) {
		LOGGER = MosipLogfactory.getMosipDefaultRollingFileLogger(mosipRollingFileAppender, this.getClass());
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
		LOGGER.debug("REGISTRATION - PACKET_ENCRYPTION - ENCRYPTION", getPropertyValue(APPLICATION_NAME),
				getPropertyValue(APPLICATION_ID), "Packet encryption had been started");
		try {
			// Encrypt the packet
			byte[] encryptedPacket = aesEncryptionManager.encrypt(packetZipData);
			LOGGER.debug("REGISTRATION - PACKET_ENCRYPTION - ENCRYPTION", getPropertyValue(APPLICATION_NAME),
					getPropertyValue(APPLICATION_ID), "Packet encrypted successfully");

			// Generate Zip File Name with absolute path
			String filePath = storageManager.storeToDisk(registrationDTO.getRegistrationId(), encryptedPacket,
					registrationDTO.getDemographicDTO().getApplicantDocumentDTO().getAcknowledgeReceipt());
			LOGGER.debug("REGISTRATION - PACKET_ENCRYPTION - ENCRYPTION", getPropertyValue(APPLICATION_NAME),
					getPropertyValue(APPLICATION_ID),
					"Encrypted Packet and Acknowledgement Receipt saved successfully");

			// Insert the Registration Details into DB
			registrationDAO.save(filePath, registrationDTO.getDemographicDTO().getDemoInLocalLang().getFullName());
			LOGGER.debug("REGISTRATION - PACKET_ENCRYPTION - ENCRYPTION", getPropertyValue(APPLICATION_NAME),
					getPropertyValue(APPLICATION_ID), "Encrypted Packet persisted");

			LOGGER.debug("REGISTRATION - PACKET_ENCRYPTION - ENCRYPTION", getPropertyValue(APPLICATION_NAME),
					getPropertyValue(APPLICATION_ID), "Packet encryption had been ended");
			auditFactory.audit(AuditEventEnum.PACKET_ENCRYPTED, AppModuleEnum.PACKET_ENCRYPTOR,
					"Packet encrypted successfully", "registration reference id", "123456");
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
