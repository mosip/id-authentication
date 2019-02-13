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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.crypto.spi.Encryptor;
import io.mosip.kernel.core.exception.NoSuchAlgorithmException;
import io.mosip.kernel.crypto.jce.constant.SecurityExceptionCodeConstant;
import io.mosip.kernel.crypto.jce.constant.SecurityMethod;
import io.mosip.kernel.crypto.jce.processor.AsymmetricProcessor;
import io.mosip.kernel.crypto.jce.processor.SymmetricProcessor;


/**
 * Factory class for Mosip Encryptor
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
@Component
public class EncryptorImpl implements Encryptor<PrivateKey, PublicKey, SecretKey> {

	@Value("${mosip.kernel.crypto.symmetric-algorithm-name}")
	private String symmetricAlgorithm;

	@Value("${mosip.kernel.crypto.asymmetric-algorithm-name}")
	private String asymmetricAlgorithm;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.core.spi.security.Encryptor#asymmetricPrivateEncrypt(java
	 * .lang.Object, byte[], java.lang.Object)
	 */
	@Override
	public byte[] asymmetricPrivateEncrypt(PrivateKey privateKey, byte[] data) {
		if (SecurityMethod.RSA_WITH_PKCS1PADDING.getValue().contains(asymmetricAlgorithm)) {
			return AsymmetricProcessor.process(SecurityMethod.RSA_WITH_PKCS1PADDING,privateKey, data, Cipher.ENCRYPT_MODE);
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
	public byte[] asymmetricPublicEncrypt(PublicKey publicKey, byte[] data) {
		if (SecurityMethod.RSA_WITH_PKCS1PADDING.getValue().contains(asymmetricAlgorithm)) {
			return AsymmetricProcessor.process(SecurityMethod.RSA_WITH_PKCS1PADDING,publicKey, data, Cipher.ENCRYPT_MODE);
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
	public byte[] symmetricEncrypt(SecretKey key, byte[] data) {
		if (SecurityMethod.AES_WITH_CBC_AND_PKCS5PADDING.getValue().contains(symmetricAlgorithm)) {
			return SymmetricProcessor.process(SecurityMethod.AES_WITH_CBC_AND_PKCS5PADDING,key, data, Cipher.ENCRYPT_MODE);
		} else {
			throw new NoSuchAlgorithmException(
					SecurityExceptionCodeConstant.MOSIP_NO_SUCH_ALGORITHM_EXCEPTION.getErrorCode(),
					SecurityExceptionCodeConstant.MOSIP_NO_SUCH_ALGORITHM_EXCEPTION.getErrorMessage());
		}
	}

}
