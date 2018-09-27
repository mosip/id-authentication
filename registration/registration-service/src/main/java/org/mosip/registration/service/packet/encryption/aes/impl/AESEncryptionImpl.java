package org.mosip.registration.service.packet.encryption.aes.impl;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.mosip.registration.consts.RegProcessorExceptionCode;
import org.mosip.registration.consts.RegProcessorExceptionEnum;
import org.mosip.registration.exception.RegBaseCheckedException;
import org.mosip.registration.exception.RegBaseUncheckedException;
import org.mosip.registration.service.packet.encryption.aes.AESEncryption;
import org.springframework.stereotype.Component;

import static java.lang.System.arraycopy;
import static org.mosip.registration.consts.RegConstants.AES_CIPHER_ALG;
import static org.mosip.registration.consts.RegConstants.AES_KEY_CIPHER_SPLITTER;
import static org.mosip.registration.consts.RegConstants.AES_KEY_MANAGER_ALG;
import static org.mosip.registration.util.reader.PropertyFileReader.getPropertyValue;

/**
 * Class for Encrypting the data using AES Algorithm
 *
 * @author Balaji Sridharan
 * @since 1.0.0
 */
@Component
public class AESEncryptionImpl implements AESEncryption {
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mosip.registration.manager.packet.encryption.aes.AESEncryption#encrypt(byte[],
	 * byte[])
	 */
	public byte[] encrypt(final byte[] plainDataByteArray, final byte[] keyByteArray) throws RegBaseCheckedException {
		try {
			SecretKeySpec keySpec = new SecretKeySpec(keyByteArray,	getPropertyValue(AES_KEY_MANAGER_ALG));

			Cipher cipher = Cipher.getInstance(getPropertyValue(AES_CIPHER_ALG));

			cipher.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(new byte[16]));

			return cipher.doFinal(plainDataByteArray);
		} catch (NoSuchAlgorithmException noSuchAlgorithmException) {
			throw new RegBaseCheckedException(RegProcessorExceptionEnum.REG_NO_SUCH_ALGORITHM_ERROR_CODE.getErrorCode(),
					RegProcessorExceptionEnum.REG_NO_SUCH_ALGORITHM_ERROR_CODE.getErrorMessage());
		} catch (NoSuchPaddingException noSuchPaddingException) {
			throw new RegBaseCheckedException(RegProcessorExceptionEnum.REG_NO_SUCH_PADDING_ERROR_CODE.getErrorCode(),
					RegProcessorExceptionEnum.REG_NO_SUCH_PADDING_ERROR_CODE.getErrorMessage());
		} catch (InvalidKeyException invalidKeyException) {
			throw new RegBaseCheckedException(RegProcessorExceptionEnum.REG_INVALID_KEY_ERROR_CODE.getErrorCode(),
					RegProcessorExceptionEnum.REG_INVALID_KEY_ERROR_CODE.getErrorMessage());
		} catch (InvalidAlgorithmParameterException algorithmParameterException) {
			throw new RegBaseCheckedException(RegProcessorExceptionEnum.REG_INVALID_ALGORITHM_PARAMETER_ERROR_CODE.getErrorCode(),
					RegProcessorExceptionEnum.REG_INVALID_ALGORITHM_PARAMETER_ERROR_CODE.getErrorMessage());
		} catch (IllegalBlockSizeException blockSizeException) {
			throw new RegBaseCheckedException(RegProcessorExceptionEnum.REG_ILLEGAL_BLOCK_SIZE_ERROR_CODE.getErrorCode(),
					RegProcessorExceptionEnum.REG_ILLEGAL_BLOCK_SIZE_ERROR_CODE.getErrorMessage());
		} catch (BadPaddingException paddingException) {
			throw new RegBaseCheckedException(RegProcessorExceptionEnum.REG_BAD_PADDING_ERROR_CODE.getErrorCode(),
					RegProcessorExceptionEnum.REG_BAD_PADDING_ERROR_CODE.getErrorMessage());
		} catch (RegBaseUncheckedException uncheckedException) {
			throw new RegBaseUncheckedException(RegProcessorExceptionCode.AES_ENCRYPTION, uncheckedException.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mosip.registration.manager.packet.encryption.aes.AESEncryption#combineKeyEncryptedData(
	 * byte[], byte[])
	 */
	public byte[] combineKeyEncryptedData(final byte[] keyByteArray, final byte[] encryptedDataByteArray) {
		try {
			final String keySplitter = getPropertyValue(AES_KEY_CIPHER_SPLITTER);
			final int keyLength = keyByteArray.length;
			final int encryptedDataLength = encryptedDataByteArray.length;
			final int keySplitterLength = keySplitter.length();
	
			byte[] combinedData = new byte[keyLength + encryptedDataLength + keySplitterLength];
	
			arraycopy(keyByteArray, 0, combinedData, 0, keyLength);
			arraycopy(keySplitter.getBytes(), 0, combinedData, keyLength, keySplitterLength);
			arraycopy(encryptedDataByteArray, 0, combinedData, keyLength + keySplitterLength, encryptedDataLength);
	
			return combinedData;
		} catch (RegBaseUncheckedException uncheckedException) {
			throw new RegBaseUncheckedException(RegProcessorExceptionCode.CONCAT_ENCRYPTED_DATA, uncheckedException.getMessage());
		}
	}
}
