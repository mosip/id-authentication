/*
 * 
 * 
 * 
 * 
 */
package io.mosip.kernel.crypto.bouncycastle.processor;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;

import io.mosip.kernel.core.crypto.exception.InvalidDataException;
import io.mosip.kernel.core.crypto.exception.InvalidKeyException;
import io.mosip.kernel.core.crypto.exception.NullKeyException;
import io.mosip.kernel.crypto.bouncycastle.constant.SecurityExceptionCodeConstant;
import io.mosip.kernel.crypto.bouncycastle.util.CryptoUtils;

/**
 * Symmetric Encryption/Decryption processor
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class SymmetricProcessor {
	/**
	 * Constructor for this class
	 */
	protected SymmetricProcessor() {
	}

	/**
	 * Symmetric Encryption/Decryption processor
	 * 
	 * @param blockCipher
	 *            initialized Symmetric block cipher
	 * @param key
	 *            key for encryption/decryption
	 * @param data
	 *            data for encryption/decryption
	 * @param mode
	 *            if true process mode is Encrypt ,else process mode is Decrypt
	 * @return Processed array
	 */
	protected static byte[] process(BlockCipher blockCipher, byte[] key, byte[] data, boolean mode) {
		BufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(blockCipher));
		try {
			cipher.init(mode, new KeyParameter(key));
		} catch (NullPointerException e) {
			throw new NullKeyException(SecurityExceptionCodeConstant.MOSIP_NULL_KEY_EXCEPTION.getErrorCode(),
					SecurityExceptionCodeConstant.MOSIP_NULL_KEY_EXCEPTION.getErrorMessage());
		} catch (IllegalArgumentException e) {
			throw new InvalidKeyException(
					SecurityExceptionCodeConstant.MOSIP_INVALID_KEY_SIZE_EXCEPTION.getErrorCode(),
					SecurityExceptionCodeConstant.MOSIP_INVALID_KEY_SIZE_EXCEPTION.getErrorMessage());
		}
		CryptoUtils.verifyData(data);
		byte[] output = new byte[cipher.getOutputSize(data.length)];
		int processedBytes = cipher.processBytes(data, 0, data.length, output, 0);
		try {
			cipher.doFinal(output, processedBytes);
		} catch (InvalidCipherTextException e) {
			throw new InvalidDataException(
					SecurityExceptionCodeConstant.MOSIP_INVALID_ENCRYPTED_DATA_CORRUPT_EXCEPTION.getErrorCode(),
					SecurityExceptionCodeConstant.MOSIP_INVALID_ENCRYPTED_DATA_CORRUPT_EXCEPTION
							.getErrorMessage());
		} catch (DataLengthException e) {
			throw new InvalidDataException(
					SecurityExceptionCodeConstant.MOSIP_INVALID_DATA_SIZE_EXCEPTION.getErrorCode(),
					SecurityExceptionCodeConstant.MOSIP_INVALID_DATA_SIZE_EXCEPTION.getErrorMessage());
		}
		return output;
	}

}
