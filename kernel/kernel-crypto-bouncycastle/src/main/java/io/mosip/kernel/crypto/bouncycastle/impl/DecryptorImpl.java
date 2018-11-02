package io.mosip.kernel.crypto.bouncycastle.impl;

import org.springframework.stereotype.Component;

import io.mosip.kernel.core.crypto.spi.Decryptor;
import io.mosip.kernel.core.exception.NoSuchAlgorithmException;
import io.mosip.kernel.crypto.bouncycastle.algorithm.AES;
import io.mosip.kernel.crypto.bouncycastle.algorithm.DES;
import io.mosip.kernel.crypto.bouncycastle.algorithm.RSA;
import io.mosip.kernel.crypto.bouncycastle.algorithm.TWOFISH;
import io.mosip.kernel.crypto.bouncycastle.constant.SecurityExceptionCodeConstant;
import io.mosip.kernel.crypto.bouncycastle.constant.SecurityMethod;
import io.mosip.kernel.crypto.bouncycastle.util.SecurityUtils;

/**
 * Factory class for Mosip Decryptor
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
@Component
public class DecryptorImpl implements Decryptor<byte[], byte[], byte[], SecurityMethod> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.core.security.bouncycastle.decryption.Decryptor#
	 * asymmetricPrivateDecrypt(byte[], byte[],
	 * io.mosip.kernel.core.security.bouncycastle.constants.SecurityMethod)
	 */
	@Override
	public byte[] asymmetricPrivateDecrypt(byte[] privateKey, byte[] data, SecurityMethod mosipSecurityMethod) {
		SecurityUtils.checkMethod(mosipSecurityMethod);
		switch (mosipSecurityMethod) {

		case HYBRID_RSA_AES_WITH_PKCS1PADDING:
			return RSA.hybridRsaAesWithPKCS1Padding(SecurityUtils.bytesToPrivateKey(privateKey), data, false);

		case HYBRID_RSA_AES_WITH_OAEP_WITH_MD5_AND_MGF1PADDING:
			return RSA.hybridRsaAesWithOAEPWithMD5AndMGF1Padding(SecurityUtils.bytesToPrivateKey(privateKey), data,
					false);

		case HYBRID_RSA_AES_WITH_OAEP_WITH_SHA3512_AND_MGF1PADDING:
			return RSA.hybridRsaAesWithOAEPWithSHA3512AndMGF1Padding(SecurityUtils.bytesToPrivateKey(privateKey), data,
					false);

		case RSA_WITH_PKCS1PADDING:
			return RSA.rsaWithPKCS1Padding(SecurityUtils.bytesToPrivateKey(privateKey), data, false);

		case RSA_WITH_OAEP_WITH_MD5_AND_MGF1PADDING:
			return RSA.rsaWithOAEPWithMD5AndMGF1Padding(SecurityUtils.bytesToPrivateKey(privateKey), data, false);

		case RSA_WITH_OAEP_WITH_SHA3512_AND_MGF1PADDING:
			return RSA.rsaWithOAEPWithSHA3512AndMGF1Padding(SecurityUtils.bytesToPrivateKey(privateKey), data, false);

		default:
			throw new NoSuchAlgorithmException(
					SecurityExceptionCodeConstant.MOSIP_NO_SUCH_ALGORITHM_EXCEPTION.getErrorCode(),
					SecurityExceptionCodeConstant.MOSIP_NO_SUCH_ALGORITHM_EXCEPTION.getErrorMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.core.security.bouncycastle.decryption.Decryptor#
	 * asymmetricPublicDecrypt(byte[], byte[],
	 * io.mosip.kernel.core.security.bouncycastle.constants.SecurityMethod)
	 */
	@Override
	public byte[] asymmetricPublicDecrypt(byte[] publicKey, byte[] data, SecurityMethod mosipSecurityMethod) {
		SecurityUtils.checkMethod(mosipSecurityMethod);
		switch (mosipSecurityMethod) {

		case HYBRID_RSA_AES_WITH_PKCS1PADDING:
			return RSA.hybridRsaAesWithPKCS1Padding(SecurityUtils.bytesToPublicKey(publicKey), data, false);

		case HYBRID_RSA_AES_WITH_OAEP_WITH_MD5_AND_MGF1PADDING:
			return RSA.hybridRsaAesWithOAEPWithMD5AndMGF1Padding(SecurityUtils.bytesToPublicKey(publicKey), data,
					false);

		case HYBRID_RSA_AES_WITH_OAEP_WITH_SHA3512_AND_MGF1PADDING:
			return RSA.hybridRsaAesWithOAEPWithSHA3512AndMGF1Padding(SecurityUtils.bytesToPublicKey(publicKey), data,
					false);
		case RSA_WITH_PKCS1PADDING:
			return RSA.rsaWithPKCS1Padding(SecurityUtils.bytesToPublicKey(publicKey), data, false);

		case RSA_WITH_OAEP_WITH_MD5_AND_MGF1PADDING:
			return RSA.rsaWithOAEPWithMD5AndMGF1Padding(SecurityUtils.bytesToPublicKey(publicKey), data, false);

		case RSA_WITH_OAEP_WITH_SHA3512_AND_MGF1PADDING:
			return RSA.rsaWithOAEPWithSHA3512AndMGF1Padding(SecurityUtils.bytesToPublicKey(publicKey), data, false);

		default:
			throw new NoSuchAlgorithmException(
					SecurityExceptionCodeConstant.MOSIP_NO_SUCH_ALGORITHM_EXCEPTION.getErrorCode(),
					SecurityExceptionCodeConstant.MOSIP_NO_SUCH_ALGORITHM_EXCEPTION.getErrorMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.core.security.bouncycastle.decryption.Decryptor#
	 * symmetricDecrypt(byte[], byte[],
	 * io.mosip.kernel.core.security.bouncycastle.constants.SecurityMethod)
	 */
	@Override
	public byte[] symmetricDecrypt(byte[] key, byte[] data, SecurityMethod mosipSecurityMethod) {
		SecurityUtils.checkMethod(mosipSecurityMethod);
		switch (mosipSecurityMethod) {

		case AES_WITH_CBC_AND_PKCS7PADDING:
			return AES.aesWithCBCandPKCS7Padding(key, data, false);

		case DES_WITH_CBC_AND_PKCS7PADDING:
			return DES.desWithCBCandPKCS7Padding(key, data, false);

		case TWOFISH_WITH_CBC_AND_PKCS7PADDING:
			return TWOFISH.twoFishWithCBCandPKCS7Padding(key, data, false);

		default:
			throw new NoSuchAlgorithmException(
					SecurityExceptionCodeConstant.MOSIP_NO_SUCH_ALGORITHM_EXCEPTION.getErrorCode(),
					SecurityExceptionCodeConstant.MOSIP_NO_SUCH_ALGORITHM_EXCEPTION.getErrorMessage());
		}
	}

}
