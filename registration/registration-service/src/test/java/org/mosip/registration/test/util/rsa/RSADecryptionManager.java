package org.mosip.registration.test.util.rsa;

import static org.mosip.registration.constants.RegConstants.RSA_PRIVATE_KEY_FILE;

import java.security.PrivateKey;

import org.mosip.registration.constants.RegConstants;
import org.mosip.registration.test.util.rsa.impl.RSADecryptionImpl;
import org.mosip.registration.util.reader.PropertyFileReader;
import org.mosip.registration.util.rsa.keygenerator.RSAKeyGenerator;
import org.mosip.registration.util.rsa.keygenerator.impl.RSAKeyGeneratorImpl;

public class RSADecryptionManager {
	
	static RSAKeyGenerator rsaKeyGenerator = new RSAKeyGeneratorImpl();
	static RSADecryption rsaDecryption = new RSADecryptionImpl();

	public byte[] decryptDemo() {
		// Read private key
		final PrivateKey privateKey = rsaKeyGenerator.readPrivatekey(RegConstants.RSA_PRIVATE_KEY_FILE);
		// Decrypt rsa encrypted bytes by using rsa private key
		byte[] rsaEncryptedBytes = "yash".getBytes();
		return rsaDecryption.decryptRsaEncryptedBytes(rsaEncryptedBytes, privateKey);

	}

}
