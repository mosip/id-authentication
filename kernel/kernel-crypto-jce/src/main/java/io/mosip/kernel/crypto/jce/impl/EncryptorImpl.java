/*
 * 
 * 
 * 
 * 
 * 
 */
package io.mosip.kernel.crypto.jce.impl;

import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

import io.mosip.kernel.core.crypto.spi.Encryptor;
import io.mosip.kernel.core.exception.MosipNoSuchAlgorithmException;
import io.mosip.kernel.crypto.jce.algorithm.MosipAES;
import io.mosip.kernel.crypto.jce.algorithm.MosipRSA;
import io.mosip.kernel.crypto.jce.constant.MosipSecurityExceptionCodeConstant;
import io.mosip.kernel.crypto.jce.constant.MosipSecurityMethod;
import io.mosip.kernel.crypto.jce.util.SecurityUtils;

/**
 * Factory class for Mosip Encryptor
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class EncryptorImpl implements Encryptor<PrivateKey, PublicKey, SecretKey, MosipSecurityMethod> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.core.spi.security.Encryptor#asymmetricPrivateEncrypt(java
	 * .lang.Object, byte[], java.lang.Object)
	 */
	@Override
	public byte[] asymmetricPrivateEncrypt(PrivateKey privateKey, byte[] data,
			MosipSecurityMethod mosipSecurityMethod) {
		SecurityUtils.checkMethod(mosipSecurityMethod);
		if (mosipSecurityMethod == MosipSecurityMethod.RSA_WITH_PKCS1PADDING) {
			return MosipRSA.rsaWithPKCS1Padding(privateKey, data, Cipher.ENCRYPT_MODE);
		} else {
			throw new MosipNoSuchAlgorithmException(
					MosipSecurityExceptionCodeConstant.MOSIP_NO_SUCH_ALGORITHM_EXCEPTION.getErrorCode(),
					MosipSecurityExceptionCodeConstant.MOSIP_NO_SUCH_ALGORITHM_EXCEPTION.getErrorMessage());
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.core.spi.security.Encryptor#asymmetricPublicEncrypt(java.
	 * lang.Object, byte[], java.lang.Object)
	 */
	@Override
	public byte[] asymmetricPublicEncrypt(PublicKey publicKey, byte[] data, MosipSecurityMethod mosipSecurityMethod) {
		SecurityUtils.checkMethod(mosipSecurityMethod);

		if (mosipSecurityMethod == MosipSecurityMethod.RSA_WITH_PKCS1PADDING) {
			return MosipRSA.rsaWithPKCS1Padding(publicKey, data, Cipher.ENCRYPT_MODE);
		} else {
			throw new MosipNoSuchAlgorithmException(
					MosipSecurityExceptionCodeConstant.MOSIP_NO_SUCH_ALGORITHM_EXCEPTION.getErrorCode(),
					MosipSecurityExceptionCodeConstant.MOSIP_NO_SUCH_ALGORITHM_EXCEPTION.getErrorMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.core.spi.security.Encryptor#symmetricEncrypt(java.lang.
	 * Object, byte[], java.lang.Object)
	 */
	@Override
	public byte[] symmetricEncrypt(SecretKey key, byte[] data, MosipSecurityMethod mosipSecurityMethod) {
		SecurityUtils.checkMethod(mosipSecurityMethod);
		if (mosipSecurityMethod == MosipSecurityMethod.AES_WITH_CBC_AND_PKCS5PADDING) {
			return MosipAES.aesWithCBCandPKCS5Padding(key, data, Cipher.ENCRYPT_MODE);
		} else {
			throw new MosipNoSuchAlgorithmException(
					MosipSecurityExceptionCodeConstant.MOSIP_NO_SUCH_ALGORITHM_EXCEPTION.getErrorCode(),
					MosipSecurityExceptionCodeConstant.MOSIP_NO_SUCH_ALGORITHM_EXCEPTION.getErrorMessage());
		}
	}

}
