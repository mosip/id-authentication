package io.mosip.registration.service.security.impl;

import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.crypto.spi.Encryptor;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.dao.PolicySyncDAO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.exception.RegistrationExceptionConstants;
import io.mosip.registration.service.BaseService;
import io.mosip.registration.service.security.RSAEncryptionService;
import io.mosip.registration.util.publickey.PublicKeyGenerationUtil;

import static io.mosip.registration.constants.LoggerConstants.LOG_PKT_RSA_ENCRYPTION;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

/**
 * Accepts AES session key as bytes and encrypt it by using RSA algorithm
 * 
 * @author YASWANTH S
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
@Service
public class RSAEncryptionServiceImpl extends BaseService implements RSAEncryptionService {

	static final Logger LOGGER = AppConfig.getLogger(RSAEncryptionServiceImpl.class);
	@Autowired
	private PolicySyncDAO policySyncDAO;
	@Autowired
	private Encryptor<PrivateKey, PublicKey, SecretKey> encryptor;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.service.packet.encryption.rsa.RSAEncryptionService#
	 * encrypt(byte[])
	 */
	@Override
	public byte[] encrypt(final byte[] sessionKey) throws RegBaseCheckedException {
		try {
			LOGGER.info(LOG_PKT_RSA_ENCRYPTION, APPLICATION_NAME, APPLICATION_ID,
					"Packet RSA Encryption had been called");

			String centerMachineId = getCenterId(getStationId(getMacAddress())) + "_" + getStationId(getMacAddress());

			// encrypt AES Session Key using RSA public key
			PublicKey publicKey = PublicKeyGenerationUtil
					.generatePublicKey(policySyncDAO.getPublicKey(centerMachineId).getPublicKey());

			return encryptor.asymmetricPublicEncrypt(publicKey, sessionKey);
		} catch (InvalidKeySpecException | NoSuchAlgorithmException compileTimeException) {
			throw new RegBaseCheckedException(
					RegistrationExceptionConstants.REG_INVALID_DATA_RSA_ENCRYPTION.getErrorCode(),
					RegistrationExceptionConstants.REG_INVALID_DATA_RSA_ENCRYPTION.getErrorMessage(),
					compileTimeException);
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(
					RegistrationExceptionConstants.REG_RUNTIME_RSA_ENCRYPTION.getErrorCode(),
					RegistrationExceptionConstants.REG_RUNTIME_RSA_ENCRYPTION.getErrorMessage(), runtimeException);
		}
	}

}
