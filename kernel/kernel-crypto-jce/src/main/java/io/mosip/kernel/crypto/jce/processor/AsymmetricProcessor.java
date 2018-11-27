
package io.mosip.kernel.crypto.jce.processor;


import java.security.Key;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import io.mosip.kernel.core.crypto.exception.InvalidDataException;
import io.mosip.kernel.core.crypto.exception.InvalidKeyException;
import io.mosip.kernel.core.exception.NoSuchAlgorithmException;
import io.mosip.kernel.crypto.jce.constant.SecurityExceptionCodeConstant;
import io.mosip.kernel.crypto.jce.constant.SecurityMethod;
import io.mosip.kernel.crypto.jce.util.CryptoUtils;

/**
 * Asymmetric Encryption/Decryption processor
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class AsymmetricProcessor {

	/**
	 * Constructor for this class
	 */
	protected AsymmetricProcessor() {
	}

	/**
	 * Asymmetric Encryption/Decryption processor
	 * 
	 * @param method
	 *            security method to use
	 * @param key
	 *            key for encryption/decryption
	 * @param data
	 *            data for encryption/decryption
	 * @param mode
	 *            process mode for operation either Encrypt or Decrypt
	 * @return Processed array
	 */
	public static byte[] process(SecurityMethod method, Key key, byte[] data, int mode) {
		Cipher cipher = init(key, mode, method);
		CryptoUtils.verifyData(data);
		return processData(cipher, data, 0, data.length);
	}

	/**
	 * Initialization method for this processor
	 * 
	 * @param key
	 *            key for encryption/decryption
	 * @param mode
	 *            process mode for operation either Encrypt or Decrypt
	 * @param method
	 *            security method to use
	 */
	private static Cipher init(Key key, int mode, SecurityMethod method) {
		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance(method.getValue());
			cipher.init(mode, key);
		} catch (java.security.NoSuchAlgorithmException | NoSuchPaddingException e) {
			throw new NoSuchAlgorithmException(
					SecurityExceptionCodeConstant.MOSIP_NO_SUCH_ALGORITHM_EXCEPTION.getErrorCode(),
					SecurityExceptionCodeConstant.MOSIP_NO_SUCH_ALGORITHM_EXCEPTION.getErrorMessage());
		} catch (java.security.InvalidKeyException e) {
			throw new InvalidKeyException(
					SecurityExceptionCodeConstant.MOSIP_INVALID_KEY_EXCEPTION.getErrorCode(),
					SecurityExceptionCodeConstant.MOSIP_INVALID_KEY_EXCEPTION.getErrorMessage());
		}
		return cipher;

	}

	/**
	 * Encryption/Decryption processor for Asymmetric Cipher
	 * 
	 * @param cipher
	 *            configured asymmetric block cipher
	 * @param data
	 *            data for encryption/decryption
	 * @param start
	 *            offset to start processing
	 * @param end
	 *            limit of processing
	 * @return Processed Array
	 */
	private static byte[] processData(Cipher cipher, byte[] data, int start, int end) {
		try {
			return cipher.doFinal(data, start, end);

		} catch (BadPaddingException | IllegalStateException | IllegalBlockSizeException e) {
			throw new InvalidDataException(
					SecurityExceptionCodeConstant.MOSIP_INVALID_DATA_EXCEPTION.getErrorCode(),
					SecurityExceptionCodeConstant.MOSIP_INVALID_DATA_EXCEPTION.getErrorMessage());
		}

	}
}
