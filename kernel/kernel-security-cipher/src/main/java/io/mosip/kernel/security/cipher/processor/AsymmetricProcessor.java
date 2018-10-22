
package io.mosip.kernel.security.cipher.processor;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import io.mosip.kernel.security.cipher.constant.MosipSecurityExceptionCodeConstants;
import io.mosip.kernel.security.cipher.constant.MosipSecurityMethod;
import io.mosip.kernel.security.cipher.exception.MosipInvalidDataException;
import io.mosip.kernel.security.cipher.exception.MosipInvalidKeyException;
import io.mosip.kernel.security.cipher.exception.MosipNoSuchAlgorithmException;
import io.mosip.kernel.security.cipher.util.SecurityUtil;

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
	protected static byte[] process(MosipSecurityMethod method, Key key,
			byte[] data, int mode) {
		Cipher cipher = init(key, mode, method);
		SecurityUtil.verifyData(data);
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
	private static Cipher init(Key key, int mode, MosipSecurityMethod method) {
		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance(method.getValue());
			cipher.init(mode, key);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			throw new MosipNoSuchAlgorithmException(
					MosipSecurityExceptionCodeConstants.MOSIP_NO_SUCH_ALGORITHM_EXCEPTION);
		} catch (InvalidKeyException e) {
			throw new MosipInvalidKeyException(
					MosipSecurityExceptionCodeConstants.MOSIP_INVALID_KEY_EXCEPTION);
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
	private static byte[] processData(Cipher cipher, byte[] data, int start,
			int end) {
		try {
			return cipher.doFinal(data, start, end);

		} catch (BadPaddingException | IllegalStateException
				| IllegalBlockSizeException e) {
			throw new MosipInvalidDataException(
					MosipSecurityExceptionCodeConstants.MOSIP_INVALID_DATA_EXCEPTION);
		}

	}
}
