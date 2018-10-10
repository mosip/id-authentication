package io.mosip.registration.test.util.rsa.impl;

import static io.mosip.registration.constants.RegConstants.RSA_ALG;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import io.mosip.registration.test.util.rsa.RSADecryption;
import org.springframework.stereotype.Component;

import io.mosip.registration.constants.RegConstants;
import io.mosip.registration.util.reader.PropertyFileReader;

@Component
public class RSADecryptionImpl implements RSADecryption {

//	PropertyFileReader propertyFileReader;
//	
//	public RSADecryptionImpl() {
//		this.propertyFileReader=propertyFileReader;
//	}

	/* (non-Javadoc)
	 * @see com.mindtree.mosip.manager.rsa.RSADecryption#decryptRsaEncryptedBytes(byte[], java.security.PrivateKey)
	 */
	public byte[] decryptRsaEncryptedBytes(final byte[] rsaEncryptedBytes,final PrivateKey privateKey) {
		 Cipher encryptCipher = null;
			try {
				//Loading Cipher security with specified algorithm
				encryptCipher = Cipher.getInstance(RegConstants.RSA_CIPHER_ALG);
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		        try {
		        	//initialising cipher security to decrypt mode
					encryptCipher.init(Cipher.DECRYPT_MODE, privateKey);
				} catch (InvalidKeyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

		        byte[] rsaDecryptedBytes=null;
		        try {
		        	//Actual decryption for rsa encrypted byte[]
		        	rsaDecryptedBytes= encryptCipher.doFinal(rsaEncryptedBytes);
				} catch (IllegalBlockSizeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (BadPaddingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return rsaDecryptedBytes;
	}

}
