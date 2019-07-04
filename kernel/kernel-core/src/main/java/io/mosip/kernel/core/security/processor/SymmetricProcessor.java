/*
 * 
 * 
 * 
 * 
 */
package io.mosip.kernel.core.security.processor;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;

import io.mosip.kernel.core.security.constants.MosipSecurityExceptionCodeConstants;
import io.mosip.kernel.core.security.exception.MosipInvalidDataException;
import io.mosip.kernel.core.security.exception.MosipInvalidKeyException;
import io.mosip.kernel.core.security.exception.MosipNullDataException;
import io.mosip.kernel.core.security.exception.MosipNullKeyException;

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
	 * @param blockCipher initialized Symmetric block cipher
	 * @param key         key for encryption/decryption
	 * @param data        data for encryption/decryption
	 * @param mode        if true process mode is Encrypt ,else process mode is
	 *                    Decrypt
	 * @return Processed array
	 * @throws MosipInvalidDataException if data is not valid(length or form)
	 * @throws MosipInvalidKeyException  if key is not valid (length or form)
	 */
	protected static byte[] process(BlockCipher blockCipher, byte[] key, byte[] data, boolean mode)
			throws MosipInvalidKeyException, MosipInvalidDataException {
		BufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(blockCipher));
		try {
			cipher.init(mode, new KeyParameter(key));
		} catch (NullPointerException e) {
			throw new MosipNullKeyException(MosipSecurityExceptionCodeConstants.MOSIP_NULL_KEY_EXCEPTION);
		} catch (IllegalArgumentException e) {
			throw new MosipInvalidKeyException(MosipSecurityExceptionCodeConstants.MOSIP_INVALID_KEY_SIZE_EXCEPTION);
		}
		byte[] output = null;
		try {
			output = new byte[cipher.getOutputSize(data.length)];
		} catch (NullPointerException e) {
			throw new MosipNullDataException(MosipSecurityExceptionCodeConstants.MOSIP_NULL_DATA_EXCEPTION);
		}
		int processedBytes = cipher.processBytes(data, 0, data.length, output, 0);
		try {
			cipher.doFinal(output, processedBytes);
		} catch (InvalidCipherTextException e) {
			throw new MosipInvalidDataException(
					MosipSecurityExceptionCodeConstants.MOSIP_INVALID_ENCRYPTED_DATA_CORRUPT_EXCEPTION);
		} catch (DataLengthException e) {
			throw new MosipInvalidDataException(MosipSecurityExceptionCodeConstants.MOSIP_INVALID_DATA_SIZE_EXCEPTION);
		}
		return output;
	}

}
