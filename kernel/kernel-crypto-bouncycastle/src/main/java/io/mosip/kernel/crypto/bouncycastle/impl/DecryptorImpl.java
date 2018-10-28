/*
 * 
 * 
 * 
 * 
 * 
 */
package io.mosip.kernel.crypto.bouncycastle.impl;

import io.mosip.kernel.core.crypto.exception.MosipNoSuchAlgorithmException;
import io.mosip.kernel.core.crypto.spi.Decryptor;
import io.mosip.kernel.crypto.bouncycastle.algorithm.MosipAES;
import io.mosip.kernel.crypto.bouncycastle.algorithm.MosipDES;
import io.mosip.kernel.crypto.bouncycastle.algorithm.MosipRSA;
import io.mosip.kernel.crypto.bouncycastle.algorithm.MosipTWOFISH;
import io.mosip.kernel.crypto.bouncycastle.constant.MosipSecurityExceptionCodeConstant;
import io.mosip.kernel.crypto.bouncycastle.constant.MosipSecurityMethod;
import io.mosip.kernel.crypto.bouncycastle.util.SecurityUtils;

/**
 * Factory class for Mosip Decryptor
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class DecryptorImpl implements Decryptor<byte[], byte[], byte[], MosipSecurityMethod> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.core.security.bouncycastle.decryption.Decryptor#
	 * asymmetricPrivateDecrypt(byte[], byte[],
	 * io.mosip.kernel.core.security.bouncycastle.constants.MosipSecurityMethod)
	 */
	@Override
	public byte[] asymmetricPrivateDecrypt(byte[] privateKey, byte[] data, MosipSecurityMethod mosipSecurityMethod) {
		SecurityUtils.checkMethod(mosipSecurityMethod);
		switch (mosipSecurityMethod) {

		case HYBRID_RSA_AES_WITH_PKCS1PADDING:
			return MosipRSA.hybridRsaAesWithPKCS1Padding(SecurityUtils.bytesToPrivateKey(privateKey), data, false);

		case HYBRID_RSA_AES_WITH_OAEP_WITH_MD5_AND_MGF1PADDING:
			return MosipRSA.hybridRsaAesWithOAEPWithMD5AndMGF1Padding(SecurityUtils.bytesToPrivateKey(privateKey), data,
					false);

		case HYBRID_RSA_AES_WITH_OAEP_WITH_SHA3512_AND_MGF1PADDING:
			return MosipRSA.hybridRsaAesWithOAEPWithSHA3512AndMGF1Padding(SecurityUtils.bytesToPrivateKey(privateKey),
					data, false);

		case RSA_WITH_PKCS1PADDING:
			return MosipRSA.rsaWithPKCS1Padding(SecurityUtils.bytesToPrivateKey(privateKey), data, false);

		case RSA_WITH_OAEP_WITH_MD5_AND_MGF1PADDING:
			return MosipRSA.rsaWithOAEPWithMD5AndMGF1Padding(SecurityUtils.bytesToPrivateKey(privateKey), data, false);

		case RSA_WITH_OAEP_WITH_SHA3512_AND_MGF1PADDING:
			return MosipRSA.rsaWithOAEPWithSHA3512AndMGF1Padding(SecurityUtils.bytesToPrivateKey(privateKey), data,
					false);

		default:
			throw new MosipNoSuchAlgorithmException(
					MosipSecurityExceptionCodeConstant.MOSIP_NO_SUCH_ALGORITHM_EXCEPTION.getErrorCode(),
					MosipSecurityExceptionCodeConstant.MOSIP_NO_SUCH_ALGORITHM_EXCEPTION.getErrorMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.core.security.bouncycastle.decryption.Decryptor#
	 * asymmetricPublicDecrypt(byte[], byte[],
	 * io.mosip.kernel.core.security.bouncycastle.constants.MosipSecurityMethod)
	 */
	@Override
	public byte[] asymmetricPublicDecrypt(byte[] publicKey, byte[] data, MosipSecurityMethod mosipSecurityMethod) {
		SecurityUtils.checkMethod(mosipSecurityMethod);
		switch (mosipSecurityMethod) {

		case HYBRID_RSA_AES_WITH_PKCS1PADDING:
			return MosipRSA.hybridRsaAesWithPKCS1Padding(SecurityUtils.bytesToPublicKey(publicKey), data, false);

		case HYBRID_RSA_AES_WITH_OAEP_WITH_MD5_AND_MGF1PADDING:
			return MosipRSA.hybridRsaAesWithOAEPWithMD5AndMGF1Padding(SecurityUtils.bytesToPublicKey(publicKey), data,
					false);

		case HYBRID_RSA_AES_WITH_OAEP_WITH_SHA3512_AND_MGF1PADDING:
			return MosipRSA.hybridRsaAesWithOAEPWithSHA3512AndMGF1Padding(SecurityUtils.bytesToPublicKey(publicKey),
					data, false);
		case RSA_WITH_PKCS1PADDING:
			return MosipRSA.rsaWithPKCS1Padding(SecurityUtils.bytesToPublicKey(publicKey), data, false);

		case RSA_WITH_OAEP_WITH_MD5_AND_MGF1PADDING:
			return MosipRSA.rsaWithOAEPWithMD5AndMGF1Padding(SecurityUtils.bytesToPublicKey(publicKey), data, false);

		case RSA_WITH_OAEP_WITH_SHA3512_AND_MGF1PADDING:
			return MosipRSA.rsaWithOAEPWithSHA3512AndMGF1Padding(SecurityUtils.bytesToPublicKey(publicKey), data,
					false);

		default:
			throw new MosipNoSuchAlgorithmException(
					MosipSecurityExceptionCodeConstant.MOSIP_NO_SUCH_ALGORITHM_EXCEPTION.getErrorCode(),
					MosipSecurityExceptionCodeConstant.MOSIP_NO_SUCH_ALGORITHM_EXCEPTION.getErrorMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.core.security.bouncycastle.decryption.Decryptor#
	 * symmetricDecrypt(byte[], byte[],
	 * io.mosip.kernel.core.security.bouncycastle.constants.MosipSecurityMethod)
	 */
	@Override
	public byte[] symmetricDecrypt(byte[] key, byte[] data, MosipSecurityMethod mosipSecurityMethod) {
		SecurityUtils.checkMethod(mosipSecurityMethod);
		switch (mosipSecurityMethod) {

		case AES_WITH_CBC_AND_PKCS7PADDING:
			return MosipAES.aesWithCBCandPKCS7Padding(key, data, false);

		case DES_WITH_CBC_AND_PKCS7PADDING:
			return MosipDES.desWithCBCandPKCS7Padding(key, data, false);

		case TWOFISH_WITH_CBC_AND_PKCS7PADDING:
			return MosipTWOFISH.twoFishWithCBCandPKCS7Padding(key, data, false);

		default:
			throw new MosipNoSuchAlgorithmException(
					MosipSecurityExceptionCodeConstant.MOSIP_NO_SUCH_ALGORITHM_EXCEPTION.getErrorCode(),
					MosipSecurityExceptionCodeConstant.MOSIP_NO_SUCH_ALGORITHM_EXCEPTION.getErrorMessage());
		}
	}

}
