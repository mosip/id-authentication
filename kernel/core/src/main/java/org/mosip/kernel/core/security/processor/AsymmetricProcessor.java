/*
 * 
 * 
 * 
 * 
 */
package org.mosip.kernel.core.security.processor;

import java.security.SecureRandom;
import java.security.Security;

import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.mosip.kernel.core.security.constants.MosipSecurityExceptionCodeConstants;
import org.mosip.kernel.core.security.constants.MosipSecurityMethod;
import org.mosip.kernel.core.security.decryption.MosipDecryptor;
import org.mosip.kernel.core.security.encryption.MosipEncryptor;
import org.mosip.kernel.core.security.exception.MosipInvalidDataException;
import org.mosip.kernel.core.security.exception.MosipInvalidKeyException;
import org.mosip.kernel.core.security.exception.MosipNullDataException;

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
	 * @param asymmetricBlockCipher
	 *            Initialized asymmetric block cipher
	 * @param key
	 *            Key for encryption/decryption
	 * @param data
	 *            Data for encryption/decryption
	 * @param mode
	 *            If true process mode is Encrypt ,else process mode is Decrypt
	 * @return Processed array
	 * @throws MosipInvalidDataException
	 *             If data is not valid(length or form)
	 * @throws MosipInvalidKeyException
	 *             If key is not valid (length or form)
	 */
	protected static byte[] process(AsymmetricBlockCipher asymmetricBlockCipher, AsymmetricKeyParameter key,
			byte[] data, boolean mode) throws MosipInvalidDataException, MosipInvalidKeyException {
		Security.addProvider(new BouncyCastleProvider());
		asymmetricBlockCipher.init(mode, key);
		int blockSize = asymmetricBlockCipher.getInputBlockSize();
		byte[] symmetricKey = null;
		byte[] output = null;
		if (mode) {
			if (blockSize >= 256)
				symmetricKey = generateSymetricKey(32);
			else if (blockSize >= 192)
				symmetricKey = generateSymetricKey(24);
			else
				symmetricKey = generateSymetricKey(16);

			byte[] encryptedSymmetricData = MosipEncryptor.symmetricEncrypt(symmetricKey, data,
					MosipSecurityMethod.AES_WITH_CBC_AND_PKCS7PADDING);

			byte[] encryptedSymmetricKey = processData(asymmetricBlockCipher, symmetricKey, 0, symmetricKey.length);

			output = new byte[asymmetricBlockCipher.getOutputBlockSize() + encryptedSymmetricData.length];
			System.arraycopy(encryptedSymmetricKey, 0, output, 0, encryptedSymmetricKey.length);
			System.arraycopy(encryptedSymmetricData, 0, output, encryptedSymmetricKey.length,
					encryptedSymmetricData.length);
		} else {
			symmetricKey = processData(asymmetricBlockCipher, data, 0, blockSize);
			byte[] encrptedData = new byte[data.length - blockSize];
			System.arraycopy(data, blockSize, encrptedData, 0, encrptedData.length);
			output = MosipDecryptor.symmetricDecrypt(symmetricKey, encrptedData,
					MosipSecurityMethod.AES_WITH_CBC_AND_PKCS7PADDING);
		}
		return output;
	}

	/**
	 * Encryption/Decryption processor for Asymmetric Cipher
	 * 
	 * @param asymmetricBlockCipher
	 *            Configured asymmetric block cipher
	 * @param data
	 *            Data for encryption/decryption
	 * @param start
	 *            Offset to start processing
	 * @param end
	 *            Limit of processing
	 * @return Processed Array
	 * @throws MosipInvalidDataException
	 *             If data is not valid(length or form)
	 * @throws MosipInvalidKeyException
	 */
	private static byte[] processData(AsymmetricBlockCipher asymmetricBlockCipher, byte[] data, int start, int end)
			throws MosipInvalidDataException, MosipInvalidKeyException {
		try {
			return asymmetricBlockCipher.processBlock(data, start, end);
		} catch (InvalidCipherTextException e) {
			throw new MosipInvalidDataException(MosipSecurityExceptionCodeConstants.MOSIP_INVALID_DATA_EXCEPTION,
					MosipSecurityExceptionCodeConstants.MOSIP_INVALID_ENCRYPTED_DATA_CORRUPT_EXCEPTION_MESSAGE);
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new MosipInvalidDataException(MosipSecurityExceptionCodeConstants.MOSIP_INVALID_DATA_EXCEPTION,
					MosipSecurityExceptionCodeConstants.MOSIP_INVALID_DATA_EXCEPTION_MESSAGE);
		} catch (DataLengthException e) {
			throw new MosipInvalidKeyException(MosipSecurityExceptionCodeConstants.MOSIP_INVALID_KEY_EXCEPTION,
					MosipSecurityExceptionCodeConstants.MOSIP_INVALID_KEY_EXCEPTION_LARGER_KEY_MESSAGE);
		} catch (NullPointerException e) {
			throw new MosipNullDataException(MosipSecurityExceptionCodeConstants.MOSIP_NULL_DATA_EXCEPTION,
					MosipSecurityExceptionCodeConstants.MOSIP_NULL_DATA_EXCEPTION_MESSAGE);
		}

	}

	/**
	 * Generate Internal Symmetric Key
	 * 
	 * @param length
	 *            Length of Symmetric key
	 * @return Generated Symmetric key
	 */
	private static byte[] generateSymetricKey(int length) {
		SecureRandom random = new SecureRandom();
		byte[] keyBytes = new byte[length];
		random.nextBytes(keyBytes);
		SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
		return key.getEncoded();
	}

}
