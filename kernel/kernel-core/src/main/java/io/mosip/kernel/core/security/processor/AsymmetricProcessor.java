/*
 * 
 * 
 * 
 * 
 */
package io.mosip.kernel.core.security.processor;

import java.security.SecureRandom;
import java.security.Security;

import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import io.mosip.kernel.core.security.constants.MosipSecurityExceptionCodeConstants;
import io.mosip.kernel.core.security.constants.MosipSecurityMethod;
import io.mosip.kernel.core.security.decryption.MosipDecryptor;
import io.mosip.kernel.core.security.encryption.MosipEncryptor;
import io.mosip.kernel.core.security.exception.MosipInvalidDataException;
import io.mosip.kernel.core.security.exception.MosipInvalidKeyException;
import io.mosip.kernel.core.security.exception.MosipNullDataException;

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
	 * @param asymmetricBlockCipher initialized asymmetric block cipher
	 * @param key                   key for encryption/decryption
	 * @param data                  data for encryption/decryption
	 * @param mode                  if true process mode is Encrypt ,else process
	 *                              mode is Decrypt
	 * @return Processed array
	 * @throws MosipInvalidDataException if data is not valid(length or form)
	 * @throws MosipInvalidKeyException  if key is not valid (length or form)
	 */
	protected static byte[] processHybrid(AsymmetricBlockCipher asymmetricBlockCipher, AsymmetricKeyParameter key,
			byte[] data, boolean mode) throws MosipInvalidDataException, MosipInvalidKeyException {
		init(asymmetricBlockCipher, key, mode);
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
	 * Asymmetric Encryption/Decryption processor
	 * 
	 * @param asymmetricBlockCipher initialized asymmetric block cipher
	 * @param key                   key for encryption/decryption
	 * @param data                  data for encryption/decryption
	 * @param mode                  if true process mode is Encrypt ,else process
	 *                              mode is Decrypt
	 * @return Processed array
	 * @throws MosipInvalidDataException if data is not valid(length or form)
	 * @throws MosipInvalidKeyException  if key is not valid (length or form)
	 */
	protected static byte[] process(AsymmetricBlockCipher asymmetricBlockCipher, AsymmetricKeyParameter key,
			byte[] data, boolean mode) throws MosipInvalidDataException, MosipInvalidKeyException {
		init(asymmetricBlockCipher, key, mode);
		if (data == null) {
			throw new MosipNullDataException(MosipSecurityExceptionCodeConstants.MOSIP_NULL_DATA_EXCEPTION);
		}
		return processData(asymmetricBlockCipher, data, 0, data.length);
	}

	/**
	 * Initialization method for this processor
	 * 
	 * @param asymmetricBlockCipher initialized asymmetric block cipher
	 * @param key                   key for encryption/decryption
	 * @param mode                  if true process mode is Encrypt ,else process
	 *                              mode is Decrypt
	 */
	private static void init(AsymmetricBlockCipher asymmetricBlockCipher, AsymmetricKeyParameter key, boolean mode) {
		Security.addProvider(new BouncyCastleProvider());
		asymmetricBlockCipher.init(mode, key);
	}

	/**
	 * Encryption/Decryption processor for Asymmetric Cipher
	 * 
	 * @param asymmetricBlockCipher configured asymmetric block cipher
	 * @param data                  data for encryption/decryption
	 * @param start                 offset to start processing
	 * @param end                   limit of processing
	 * @return Processed Array
	 * @throws MosipInvalidDataException if data is not valid(length or form)
	 * @throws MosipInvalidKeyException  if key is not valid (length or form)
	 */
	private static byte[] processData(AsymmetricBlockCipher asymmetricBlockCipher, byte[] data, int start, int end)
			throws MosipInvalidDataException, MosipInvalidKeyException {
		try {
			return asymmetricBlockCipher.processBlock(data, start, end);
		} catch (InvalidCipherTextException e) {
			throw new MosipInvalidDataException(
					MosipSecurityExceptionCodeConstants.MOSIP_INVALID_ENCRYPTED_DATA_CORRUPT_EXCEPTION);
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new MosipInvalidDataException(MosipSecurityExceptionCodeConstants.MOSIP_INVALID_DATA_EXCEPTION);
		} catch (DataLengthException e) {
			throw new MosipInvalidDataException(MosipSecurityExceptionCodeConstants.MOSIP_INVALID_LENGTH_EXCEPTION);
		} catch (NullPointerException e) {
			throw new MosipNullDataException(MosipSecurityExceptionCodeConstants.MOSIP_NULL_DATA_EXCEPTION);
		} catch (IllegalArgumentException e) {
			throw new MosipInvalidDataException(
					MosipSecurityExceptionCodeConstants.MOSIP_INVALID_DATA_LENGTH_EXCEPTION);
		} catch (ArithmeticException e) {
			throw new MosipInvalidKeyException(MosipSecurityExceptionCodeConstants.MOSIP_INVALID_KEY_CORRUPT_EXCEPTION);
		}

	}

	/**
	 * Generate Internal Symmetric Key
	 * 
	 * @param length length of Symmetric key
	 * @return Generated symmetric key
	 */
	private static byte[] generateSymetricKey(int length) {
		SecureRandom random = new SecureRandom();
		byte[] keyBytes = new byte[length];
		random.nextBytes(keyBytes);
		SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
		return key.getEncoded();
	}

}
