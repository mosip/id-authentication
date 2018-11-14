/*
 * 
 * 
 * 
 * 
 * 
 */
package io.mosip.kernel.crypto.bouncycastle.util;

import java.io.IOException;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.crypto.util.PublicKeyFactory;

import io.mosip.kernel.core.crypto.exception.InvalidDataException;
import io.mosip.kernel.core.crypto.exception.InvalidKeyException;
import io.mosip.kernel.core.crypto.exception.NullDataException;
import io.mosip.kernel.core.crypto.exception.NullKeyException;
import io.mosip.kernel.core.crypto.exception.NullMethodException;
import io.mosip.kernel.crypto.bouncycastle.constant.SecurityExceptionCodeConstant;
import io.mosip.kernel.crypto.bouncycastle.constant.SecurityMethod;

/**
 * Utility class for security
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class CryptoUtils {

	/**
	 * Constructor for this class
	 */
	private CryptoUtils() {

	}

	/**
	 * {@link AsymmetricKeyParameter} from encoded private key
	 * 
	 * @param privateKey
	 *            private Key for processing
	 * @return {@link AsymmetricKeyParameter} from encoded private key '
	 */
	public static AsymmetricKeyParameter bytesToPrivateKey(byte[] privateKey) {
		AsymmetricKeyParameter keyParameter = null;
		try {
			keyParameter = PrivateKeyFactory.createKey(privateKey);
		} catch (NullPointerException e) {
			throw new NullKeyException(SecurityExceptionCodeConstant.MOSIP_NULL_KEY_EXCEPTION.getErrorCode(),
					SecurityExceptionCodeConstant.MOSIP_NULL_KEY_EXCEPTION.getErrorMessage());
		} catch (ClassCastException e) {
			throw new InvalidKeyException(
					SecurityExceptionCodeConstant.MOSIP_INVALID_ASYMMETRIC_PRIVATE_KEY_EXCEPTION.getErrorCode(),
					SecurityExceptionCodeConstant.MOSIP_INVALID_ASYMMETRIC_PRIVATE_KEY_EXCEPTION
							.getErrorMessage());
		} catch (IOException e) {
			throw new InvalidKeyException(
					SecurityExceptionCodeConstant.MOSIP_INVALID_KEY_CORRUPT_EXCEPTION.getErrorCode(),
					SecurityExceptionCodeConstant.MOSIP_INVALID_KEY_CORRUPT_EXCEPTION.getErrorMessage());
		}
		return keyParameter;
	}

	/**
	 * {@link AsymmetricKeyParameter} from encoded public key
	 * 
	 * @param publicKey
	 *            private Key for processing
	 * @return {@link AsymmetricKeyParameter} from encoded public key
	 */
	public static AsymmetricKeyParameter bytesToPublicKey(byte[] publicKey) {
		AsymmetricKeyParameter keyParameter = null;
		try {
			keyParameter = PublicKeyFactory.createKey(publicKey);
		} catch (NullPointerException e) {
			throw new NullKeyException(SecurityExceptionCodeConstant.MOSIP_NULL_KEY_EXCEPTION.getErrorCode(),
					SecurityExceptionCodeConstant.MOSIP_NULL_KEY_EXCEPTION.getErrorMessage());
		} catch (IllegalArgumentException e) {
			throw new InvalidKeyException(
					SecurityExceptionCodeConstant.MOSIP_INVALID_ASYMMETRIC_PUBLIC_KEY_EXCEPTION.getErrorCode(),
					SecurityExceptionCodeConstant.MOSIP_INVALID_ASYMMETRIC_PUBLIC_KEY_EXCEPTION.getErrorMessage());
		} catch (IOException e) {
			throw new InvalidKeyException(
					SecurityExceptionCodeConstant.MOSIP_INVALID_KEY_CORRUPT_EXCEPTION.getErrorCode(),
					SecurityExceptionCodeConstant.MOSIP_INVALID_KEY_CORRUPT_EXCEPTION.getErrorMessage());
		}
		return keyParameter;
	}

	/**
	 * This method verifies mosip security method
	 * 
	 * @param mosipSecurityMethod
	 *            mosipSecurityMethod given by user
	 */
	public static void checkMethod(SecurityMethod mosipSecurityMethod) {
		if (mosipSecurityMethod == null) {
			throw new NullMethodException(
					SecurityExceptionCodeConstant.MOSIP_NULL_METHOD_EXCEPTION.getErrorCode(),
					SecurityExceptionCodeConstant.MOSIP_NULL_METHOD_EXCEPTION.getErrorMessage());
		}
	}

	/**
	 * Verify if data is null or empty
	 * 
	 * @param data
	 *            data provided by user
	 */
	public static void verifyData(byte[] data) {
		if (data == null) {
			throw new NullDataException(
					SecurityExceptionCodeConstant.MOSIP_NULL_DATA_EXCEPTION.getErrorCode(),
					SecurityExceptionCodeConstant.MOSIP_NULL_DATA_EXCEPTION.getErrorMessage());
		} else if (data.length == 0) {
			throw new InvalidDataException(
					SecurityExceptionCodeConstant.MOSIP_NULL_DATA_EXCEPTION.getErrorCode(),
					SecurityExceptionCodeConstant.MOSIP_NULL_DATA_EXCEPTION.getErrorMessage());
		}
	}
}
