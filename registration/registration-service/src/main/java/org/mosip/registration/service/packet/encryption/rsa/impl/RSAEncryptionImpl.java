package org.mosip.registration.service.packet.encryption.rsa.impl;

import static org.mosip.registration.constants.RegProcessorExceptionEnum.REG_BAD_PADDING_ERROR_CODE;
import static org.mosip.registration.constants.RegProcessorExceptionEnum.REG_ILLEGAL_BLOCK_SIZE_ERROR_CODE;
import static org.mosip.registration.constants.RegProcessorExceptionEnum.REG_INVALID_KEY_ERROR_CODE;
import static org.mosip.registration.constants.RegProcessorExceptionEnum.REG_NO_SUCH_ALGORITHM_ERROR_CODE;
import static org.mosip.registration.constants.RegProcessorExceptionEnum.REG_NO_SUCH_PADDING_ERROR_CODE;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.mosip.registration.constants.RegConstants;
import org.mosip.registration.exception.RegBaseUncheckedException;
import org.mosip.registration.service.packet.encryption.rsa.RSAEncryption;
import org.springframework.stereotype.Component;

@Component
public class RSAEncryptionImpl implements RSAEncryption {
//	private PropertyFileReader propertyFileReader;


	/* (non-Javadoc)
	 * @see org.mosip.registration.manager.packet.encryption.rsa.RSAEncryption#encrypt(byte[], java.security.PublicKey)
	 */
	public byte[] encrypt(final byte[] sessionKey, final PublicKey publicKey) {
		
		Cipher encryptCipher = null;
		try {
			// Loading Cipher security with specified algorithm
			encryptCipher = Cipher.getInstance(RegConstants.RSA_ALG);
		} catch (NoSuchAlgorithmException noSuchAlgorithmException) {
			throw new RegBaseUncheckedException(REG_NO_SUCH_ALGORITHM_ERROR_CODE.getErrorCode(),
					REG_NO_SUCH_ALGORITHM_ERROR_CODE.getErrorMessage(), noSuchAlgorithmException);
		} catch (NoSuchPaddingException noSuchPaddingException) {
			throw new RegBaseUncheckedException(REG_NO_SUCH_PADDING_ERROR_CODE.getErrorCode(),
					REG_NO_SUCH_PADDING_ERROR_CODE.getErrorMessage(), noSuchPaddingException);
		}
		try {
			// initialising cipher security to encrypt mode
			encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);
		} catch (InvalidKeyException invalidKeyException) {
			throw new RegBaseUncheckedException(REG_INVALID_KEY_ERROR_CODE.getErrorCode(),
					REG_INVALID_KEY_ERROR_CODE.getErrorMessage(), invalidKeyException);

		}

		byte[] rsaEncryptedBytes = null;
		try {
			rsaEncryptedBytes = encryptCipher.doFinal(sessionKey);
		} catch (IllegalBlockSizeException illegalBlockSizeException) {
			throw new RegBaseUncheckedException(REG_ILLEGAL_BLOCK_SIZE_ERROR_CODE.getErrorCode(),
					REG_ILLEGAL_BLOCK_SIZE_ERROR_CODE.getErrorMessage(), illegalBlockSizeException);
		} catch (BadPaddingException paddingException) {
			throw new RegBaseUncheckedException(REG_BAD_PADDING_ERROR_CODE.getErrorCode(),
					REG_BAD_PADDING_ERROR_CODE.getErrorMessage(), paddingException);
		}

		return rsaEncryptedBytes;
	}

}
