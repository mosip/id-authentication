package io.mosip.registration.service.packet.encryption.rsa;

import java.security.PublicKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.registration.constants.RegConstants;
import io.mosip.registration.service.packet.encryption.rsa.impl.RSAEncryptionImpl;
import io.mosip.registration.util.rsa.keygenerator.RSAKeyGenerator;
import io.mosip.registration.util.rsa.keygenerator.impl.RSAKeyGeneratorImpl;

/**
 * Accepts aes encrypted bytes and encrypt it by using rsa algorithm
 * 
 * @author YASWANTH S
 * @since 1.0.0
 *
 */
@Component
public class RSAEncryptionManager {

	@Autowired
	public RSAKeyGenerator rsaKeyGenerator;
	@Autowired
	public RSAEncryption rsaEncryption;

	
	/**
	 * Encryption for aes encrypted bytes by using rsa encryption algorithm
	 * 
	 * @param sessionKey has to be encrypted
	 * @return rsaEncryptedBytes has encrypted by rsa
	 */
	public byte[] encrypt(final byte[] sessionKey) {
		// Generate key pair public and private key
		rsaKeyGenerator.generateKey();

		// Read public key from file
		final PublicKey publicKey = rsaKeyGenerator.readPublickey(RegConstants.RSA_PUBLIC_KEY_FILE);

		// encrypt aes encrypted bytes by using rsa public key
		return rsaEncryption.encrypt(sessionKey, publicKey);
	}

	

}
