package io.mosip.registration.service.impl;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.crypto.spi.Encryptor;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dao.PolicySyncDAO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.RSAEncryptionService;

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
public class RSAEncryptionServiceImpl implements RSAEncryptionService {

	static final Logger LOGGER = AppConfig.getLogger(RSAEncryptionServiceImpl.class);
	@Autowired
	private PolicySyncDAO policySyncDAO;
	@Autowired
	private Encryptor<PrivateKey, PublicKey, SecretKey> encryptor;
	@Autowired
	private Environment environment;

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
			LOGGER.debug(LOG_PKT_RSA_ENCRYPTION, APPLICATION_NAME, APPLICATION_ID,
					"Packet RSA Encryption had been called");

			// encrypt AES Session Key using RSA public key
			PublicKey publicKey = KeyFactory
					.getInstance(environment.getProperty(RegistrationConstants.RSA))
					.generatePublic(new X509EncodedKeySpec(
							CryptoUtil.decodeBase64(new String(policySyncDAO.findByMaxExpireTime().getPublicKey()))));

			return encryptor.asymmetricPublicEncrypt(publicKey, sessionKey);
		} catch (InvalidKeySpecException | NoSuchAlgorithmException compileTimeException) {
			throw new RegBaseCheckedException(RegistrationConstants.RSA_ENCRYPTION_MANAGER,
					compileTimeException.toString(), compileTimeException);
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegistrationConstants.RSA_ENCRYPTION_MANAGER,
					runtimeException.toString(), runtimeException);
		}
	}

}
