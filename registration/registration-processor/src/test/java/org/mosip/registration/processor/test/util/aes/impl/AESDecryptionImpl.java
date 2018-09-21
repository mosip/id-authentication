package org.mosip.registration.processor.test.util.aes.impl;

import static org.mosip.registration.processor.consts.RegConstants.AES_CIPHER_ALG;
import static org.mosip.registration.processor.consts.RegConstants.AES_KEY_MANAGER_ALG;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.mosip.registration.processor.test.util.aes.AESDecryption;
import org.mosip.registration.processor.util.reader.PropertyFileReader;
import org.springframework.stereotype.Component;

/**
 * Class to Decrypt the AES Encrypted Data
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
@Component
public class AESDecryptionImpl implements AESDecryption {
	/* (non-Javadoc)
	 * @see org.mosip.registration.processor.manager.aes.AESDecryption#decrypt(byte[], byte[])
	 */
	@Override	
	public byte[] decrypt(final byte[] encryptedData, byte[] sessionKey) throws InvalidKeyException, NoSuchAlgorithmException,
	NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
		final SecretKeySpec sskey = new SecretKeySpec(sessionKey, PropertyFileReader.getPropertyValue(AES_KEY_MANAGER_ALG));
		
		final Cipher cipher = Cipher.getInstance(PropertyFileReader.getPropertyValue(AES_CIPHER_ALG));
		
		cipher.init(Cipher.DECRYPT_MODE, sskey, new IvParameterSpec(new byte[16]));
		return cipher.doFinal(encryptedData);
	}
}
