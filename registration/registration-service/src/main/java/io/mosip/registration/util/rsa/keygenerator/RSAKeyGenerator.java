package io.mosip.registration.util.rsa.keygenerator;

public interface RSAKeyGenerator {
	/**
	 * Generate public key and private key for encryption and decryption
	 */
	void generateKey();
	
	/**
	 * Returns the encoded key byte array of either public key or private key If the
	 * input is true, returns the public key else returns the private key
	 * 
	 * @param isPublic
	 * @return
	 */
	byte[] getEncodedKey(boolean isPublic);
}
