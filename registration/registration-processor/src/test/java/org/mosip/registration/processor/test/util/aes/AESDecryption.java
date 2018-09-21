package org.mosip.registration.processor.test.util.aes;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Interface for AES Decryption
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
public interface AESDecryption {

	/**
	 * Decrypts the AES encrypted data
	 * 
	 * @param encryptedData
	 *            the AES Encrypted Data to be decrypted
	 * @param sessionKey the AES Session Key to be used for decryption
	 * @return <b>byte[]</b> Decrypted Data
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws InvalidAlgorithmParameterException
	 */
	byte[] decrypt(byte[] encryptedData, byte[] sessionKey) throws InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException;
}
