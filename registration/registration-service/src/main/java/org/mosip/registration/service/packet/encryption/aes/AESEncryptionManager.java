/**
 * 
 */
package org.mosip.registration.service.packet.encryption.aes;

import java.security.Security;
import java.util.List;

import javax.crypto.SecretKey;

import org.mosip.kernel.core.security.constants.MosipSecurityMethod;
import org.mosip.kernel.core.security.encryption.MosipEncryptor;
import org.mosip.kernel.core.security.exception.MosipInvalidDataException;
import org.mosip.kernel.core.security.exception.MosipInvalidKeyException;
import org.mosip.registration.constants.RegProcessorExceptionCode;
import org.mosip.registration.exception.RegBaseCheckedException;
import org.mosip.registration.exception.RegBaseUncheckedException;
import org.mosip.registration.service.packet.encryption.rsa.RSAEncryptionManager;
import org.mosip.registration.util.keymanager.AESKeyManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * API class to encrypt the data using AES algorithm
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
@Component
public class AESEncryptionManager {

	/**
	 * Class to generate the seeds for AES Session Key
	 */
	@Autowired
	private AESSeedGenerator aesSeedGenerator;
	/**
	 * Class to generate the AES Session Key
	 */
	@Autowired
	private AESKeyManager aesKeyManager;
	/**
	 * Class to encrypt the date using AES Algorithm
	 */
	@Autowired
	private AESEncryption aesEncryption;
	/**
	 * Class to encrypt the AES Session Key using RSA Algorithm
	 */
	@Autowired
	private RSAEncryptionManager rsaEncryptionManager;

	/**
	 * The API method to encrypt the data using AES Algorithm
	 * @param dataToEncrypt
	 * @return encrypted data as byte array
	 * @throws RegBaseCheckedException 
	 */
	public byte[] encrypt(final byte[] dataToEncrypt) throws RegBaseCheckedException {
		try {
			// Enable AES 256 bit encryption
			Security.setProperty("crypto.policy", "unlimited");
			// Get the Seeds for AES Session Key
			// TODO: Add Log and Audit for AES Session Key - Seeds generation
			// TODO: Warp seeds, key and encrypt
			List<String> aesKeySeeds = aesSeedGenerator.generateAESKeySeeds();
	
			// Generate AES Session Key
			// TODO: Add Log and Audit for AES Session Key Generation
			final SecretKey sessionKey = aesKeyManager.generateSessionKey(aesKeySeeds);
	
			// Encrypt the Data using AES
			// TODO: Add Log and Audit for Encryption using AES Algorithm
			final byte[] encryptedData = MosipEncryptor.symmetricEncrypt(sessionKey.getEncoded(), dataToEncrypt, MosipSecurityMethod.AES_WITH_CBC_AND_PKCS7PADDING);
					//aesEncryption.encrypt(dataToEncrypt,sessionKey.getEncoded());
			
			final byte[] rsaEncryptedKey = rsaEncryptionManager.encrypt(sessionKey.getEncoded());
	
			// Combine the RSA Encrypted AES Session Key, AES Key Splitter and AES Encrypted Data
			// TODO: Add Log and Audit for concatenating the byte arrays of RSA Encrypted AES Session Key and AES encrypted data
			return aesEncryption.combineKeyEncryptedData(rsaEncryptedKey,
					encryptedData);
		} catch (RegBaseUncheckedException uncheckedException) {
			throw new RegBaseUncheckedException(RegProcessorExceptionCode.AES_ENCRYPTION_MANAGER, uncheckedException.getMessage());
		} catch (MosipInvalidDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RegBaseCheckedException("", "");
		} catch (MosipInvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RegBaseCheckedException("", "");
		}
	}

}
