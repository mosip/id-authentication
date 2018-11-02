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

import org.springframework.stereotype.Component;

import io.mosip.kernel.core.crypto.spi.Encryptor;
import io.mosip.kernel.core.exception.NoSuchAlgorithmException;
import io.mosip.kernel.crypto.jce.algorithm.AES;
import io.mosip.kernel.crypto.jce.algorithm.RSA;
import io.mosip.kernel.crypto.jce.constant.SecurityExceptionCodeConstant;
import io.mosip.kernel.crypto.jce.constant.SecurityMethod;
import io.mosip.kernel.crypto.jce.util.SecurityUtils;

/**
 * Factory class for Mosip Encryptor
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
@Component
public class EncryptorImpl implements Encryptor<PrivateKey, PublicKey, SecretKey, SecurityMethod> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.core.spi.security.Encryptor#asymmetricPrivateEncrypt(java
	 * .lang.Object, byte[], java.lang.Object)
	 */
	@Override
	public byte[] asymmetricPrivateEncrypt(PrivateKey privateKey, byte[] data, SecurityMethod mosipSecurityMethod) {
		SecurityUtils.checkMethod(mosipSecurityMethod);
		if (mosipSecurityMethod == SecurityMethod.RSA_WITH_PKCS1PADDING) {
			return RSA.rsaWithPKCS1Padding(privateKey, data, Cipher.ENCRYPT_MODE);
		} else {
			throw new NoSuchAlgorithmException(
					SecurityExceptionCodeConstant.MOSIP_NO_SUCH_ALGORITHM_EXCEPTION.getErrorCode(),
					SecurityExceptionCodeConstant.MOSIP_NO_SUCH_ALGORITHM_EXCEPTION.getErrorMessage());
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
	public byte[] asymmetricPublicEncrypt(PublicKey publicKey, byte[] data, SecurityMethod mosipSecurityMethod) {
		SecurityUtils.checkMethod(mosipSecurityMethod);

		if (mosipSecurityMethod == SecurityMethod.RSA_WITH_PKCS1PADDING) {
			return RSA.rsaWithPKCS1Padding(publicKey, data, Cipher.ENCRYPT_MODE);
		} else {
			throw new NoSuchAlgorithmException(
					SecurityExceptionCodeConstant.MOSIP_NO_SUCH_ALGORITHM_EXCEPTION.getErrorCode(),
					SecurityExceptionCodeConstant.MOSIP_NO_SUCH_ALGORITHM_EXCEPTION.getErrorMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.core.spi.security.Encryptor#symmetricEncrypt(java.lang.
	 * Object, byte[], java.lang.Object)
	 */
	@Override
	public byte[] symmetricEncrypt(SecretKey key, byte[] data, SecurityMethod mosipSecurityMethod) {
		SecurityUtils.checkMethod(mosipSecurityMethod);
		if (mosipSecurityMethod == SecurityMethod.AES_WITH_CBC_AND_PKCS5PADDING) {
			return AES.aesWithCBCandPKCS5Padding(key, data, Cipher.ENCRYPT_MODE);
		} else {
			throw new NoSuchAlgorithmException(
					SecurityExceptionCodeConstant.MOSIP_NO_SUCH_ALGORITHM_EXCEPTION.getErrorCode(),
					SecurityExceptionCodeConstant.MOSIP_NO_SUCH_ALGORITHM_EXCEPTION.getErrorMessage());
		}
	}

}
