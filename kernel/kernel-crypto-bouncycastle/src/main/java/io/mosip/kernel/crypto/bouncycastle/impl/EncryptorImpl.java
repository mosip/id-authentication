package io.mosip.kernel.crypto.bouncycastle.impl;

import org.springframework.stereotype.Component;

import io.mosip.kernel.core.crypto.spi.Encryptor;
import io.mosip.kernel.core.exception.NoSuchAlgorithmException;
import io.mosip.kernel.crypto.bouncycastle.constant.SecurityExceptionCodeConstant;
import io.mosip.kernel.crypto.bouncycastle.constant.SecurityMethod;
import io.mosip.kernel.crypto.bouncycastle.processor.AESProcessor;
import io.mosip.kernel.crypto.bouncycastle.processor.DESProcessor;
import io.mosip.kernel.crypto.bouncycastle.processor.RSAProcessor;
import io.mosip.kernel.crypto.bouncycastle.processor.TWOFISHProcessor;
import io.mosip.kernel.crypto.bouncycastle.util.CryptoUtils;

/**
 * Factory class for  Encryptor
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
@Component
public class EncryptorImpl implements Encryptor<byte[], byte[], byte[], SecurityMethod> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.core.security.bouncycastle.encryption.Encryptor#
	 * asymmetricPrivateEncrypt(byte[], byte[],
	 * io.mosip.kernel.core.security.bouncycastle.constants.SecurityMethod)
	 */
	@Override
	public byte[] asymmetricPrivateEncrypt(byte[] privateKey, byte[] data, SecurityMethod mosipSecurityMethod) {
		CryptoUtils.checkMethod(mosipSecurityMethod);
		switch (mosipSecurityMethod) {

		case HYBRID_RSA_AES_WITH_PKCS1PADDING:
			return RSAProcessor.hybridRsaAesWithPKCS1Padding(CryptoUtils.bytesToPrivateKey(privateKey), data, true);

		case HYBRID_RSA_AES_WITH_OAEP_WITH_MD5_AND_MGF1PADDING:
			return RSAProcessor.hybridRsaAesWithOAEPWithMD5AndMGF1Padding(CryptoUtils.bytesToPrivateKey(privateKey), data,
					true);

		case HYBRID_RSA_AES_WITH_OAEP_WITH_SHA3512_AND_MGF1PADDING:
			return RSAProcessor.hybridRsaAesWithOAEPWithSHA3512AndMGF1Padding(CryptoUtils.bytesToPrivateKey(privateKey), data,
					true);

		case RSA_WITH_PKCS1PADDING:
			return RSAProcessor.rsaWithPKCS1Padding(CryptoUtils.bytesToPrivateKey(privateKey), data, true);

		case RSA_WITH_OAEP_WITH_MD5_AND_MGF1PADDING:
			return RSAProcessor.rsaWithOAEPWithMD5AndMGF1Padding(CryptoUtils.bytesToPrivateKey(privateKey), data, true);

		case RSA_WITH_OAEP_WITH_SHA3512_AND_MGF1PADDING:
			return RSAProcessor.rsaWithOAEPWithSHA3512AndMGF1Padding(CryptoUtils.bytesToPrivateKey(privateKey), data, true);

		default:
			throw new NoSuchAlgorithmException(
					SecurityExceptionCodeConstant.MOSIP_NO_SUCH_ALGORITHM_EXCEPTION.getErrorCode(),
					SecurityExceptionCodeConstant.MOSIP_NO_SUCH_ALGORITHM_EXCEPTION.getErrorMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.core.security.bouncycastle.encryption.Encryptor#
	 * asymmetricPublicEncrypt(byte[], byte[],
	 * io.mosip.kernel.core.security.bouncycastle.constants.SecurityMethod)
	 */
	@Override
	public byte[] asymmetricPublicEncrypt(byte[] publicKey, byte[] data, SecurityMethod mosipSecurityMethod) {
		CryptoUtils.checkMethod(mosipSecurityMethod);
		switch (mosipSecurityMethod) {

		case HYBRID_RSA_AES_WITH_PKCS1PADDING:
			return RSAProcessor.hybridRsaAesWithPKCS1Padding(CryptoUtils.bytesToPublicKey(publicKey), data, true);

		case HYBRID_RSA_AES_WITH_OAEP_WITH_MD5_AND_MGF1PADDING:
			return RSAProcessor.hybridRsaAesWithOAEPWithMD5AndMGF1Padding(CryptoUtils.bytesToPublicKey(publicKey), data, true);

		case HYBRID_RSA_AES_WITH_OAEP_WITH_SHA3512_AND_MGF1PADDING:
			return RSAProcessor.hybridRsaAesWithOAEPWithSHA3512AndMGF1Padding(CryptoUtils.bytesToPublicKey(publicKey), data,
					true);

		case RSA_WITH_PKCS1PADDING:
			return RSAProcessor.rsaWithPKCS1Padding(CryptoUtils.bytesToPublicKey(publicKey), data, true);

		case RSA_WITH_OAEP_WITH_MD5_AND_MGF1PADDING:
			return RSAProcessor.rsaWithOAEPWithMD5AndMGF1Padding(CryptoUtils.bytesToPublicKey(publicKey), data, true);

		case RSA_WITH_OAEP_WITH_SHA3512_AND_MGF1PADDING:
			return RSAProcessor.rsaWithOAEPWithSHA3512AndMGF1Padding(CryptoUtils.bytesToPublicKey(publicKey), data, true);

		default:
			throw new NoSuchAlgorithmException(
					SecurityExceptionCodeConstant.MOSIP_NO_SUCH_ALGORITHM_EXCEPTION.getErrorCode(),
					SecurityExceptionCodeConstant.MOSIP_NO_SUCH_ALGORITHM_EXCEPTION.getErrorMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.core.security.bouncycastle.encryption.Encryptor#
	 * symmetricEncrypt(byte[], byte[],
	 * io.mosip.kernel.core.security.bouncycastle.constants.SecurityMethod)
	 */
	@Override
	public byte[] symmetricEncrypt(byte[] key, byte[] data, SecurityMethod mosipSecurityMethod) {
		CryptoUtils.checkMethod(mosipSecurityMethod);
		switch (mosipSecurityMethod) {

		case AES_WITH_CBC_AND_PKCS7PADDING:
			return AESProcessor.aesWithCBCandPKCS7Padding(key, data, true);

		case DES_WITH_CBC_AND_PKCS7PADDING:
			return DESProcessor.desWithCBCandPKCS7Padding(key, data, true);

		case TWOFISH_WITH_CBC_AND_PKCS7PADDING:
			return TWOFISHProcessor.twoFishWithCBCandPKCS7Padding(key, data, true);

		default:
			throw new NoSuchAlgorithmException(
					SecurityExceptionCodeConstant.MOSIP_NO_SUCH_ALGORITHM_EXCEPTION.getErrorCode(),
					SecurityExceptionCodeConstant.MOSIP_NO_SUCH_ALGORITHM_EXCEPTION.getErrorMessage());
		}
	}
}
