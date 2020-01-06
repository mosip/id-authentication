/*
 * 
 * 
 * 
 * 
 * 
 */
package io.mosip.kernel.core.security.encryption;

import io.mosip.kernel.core.security.algorithms.MosipAES;
import io.mosip.kernel.core.security.algorithms.MosipDES;
import io.mosip.kernel.core.security.algorithms.MosipRSA;
import io.mosip.kernel.core.security.algorithms.MosipTWOFISH;
import io.mosip.kernel.core.security.constants.MosipSecurityExceptionCodeConstants;
import io.mosip.kernel.core.security.constants.MosipSecurityMethod;
import io.mosip.kernel.core.security.exception.MosipInvalidDataException;
import io.mosip.kernel.core.security.exception.MosipInvalidKeyException;
import io.mosip.kernel.core.security.exception.MosipNoSuchAlgorithmException;
import io.mosip.kernel.core.security.util.SecurityUtil;

/**
 * Factory class for Mosip Encryptor
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class MosipEncryptor {

	/**
	 * Constructor for this class
	 */
	private MosipEncryptor() {

	}

	/**
	 * Asymmetric Encrypt with private key
	 * 
	 * @param privateKey          key for encryption
	 * @param data                data for encryption
	 * @param mosipSecurityMethod {@link MosipSecurityMethod} for processing
	 * @return Processed array
	 * @throws MosipInvalidDataException if data is not valid in length,corrupted
	 * @throws MosipInvalidKeyException  if key is not valid in length,corrupted and
	 *                                   wrong
	 */
	public static byte[] asymmetricPrivateEncrypt(byte[] privateKey, byte[] data,
			MosipSecurityMethod mosipSecurityMethod) throws MosipInvalidDataException, MosipInvalidKeyException {
		SecurityUtil.checkMethod(mosipSecurityMethod);
		switch (mosipSecurityMethod) {

		case HYBRID_RSA_AES_WITH_PKCS1PADDING:
			return MosipRSA.hybridRsaAesWithPKCS1Padding(SecurityUtil.bytesToPrivateKey(privateKey), data, true);

		case HYBRID_RSA_AES_WITH_OAEP_WITH_MD5_AND_MGF1PADDING:
			return MosipRSA.hybridRsaAesWithOAEPWithMD5AndMGF1Padding(SecurityUtil.bytesToPrivateKey(privateKey), data,
					true);

		case HYBRID_RSA_AES_WITH_OAEP_WITH_SHA3512_AND_MGF1PADDING:
			return MosipRSA.hybridRsaAesWithOAEPWithSHA3512AndMGF1Padding(SecurityUtil.bytesToPrivateKey(privateKey),
					data, true);

		case RSA_WITH_PKCS1PADDING:
			return MosipRSA.rsaWithPKCS1Padding(SecurityUtil.bytesToPrivateKey(privateKey), data, true);

		case RSA_WITH_OAEP_WITH_MD5_AND_MGF1PADDING:
			return MosipRSA.rsaWithOAEPWithMD5AndMGF1Padding(SecurityUtil.bytesToPrivateKey(privateKey), data, true);

		case RSA_WITH_OAEP_WITH_SHA3512_AND_MGF1PADDING:
			return MosipRSA.rsaWithOAEPWithSHA3512AndMGF1Padding(SecurityUtil.bytesToPrivateKey(privateKey), data,
					true);

		default:
			throw new MosipNoSuchAlgorithmException(
					MosipSecurityExceptionCodeConstants.MOSIP_NO_SUCH_ALGORITHM_EXCEPTION);
		}
	}

	/**
	 * Asymmetric Encrypt with public key
	 * 
	 * @param publicKey           key for encryption
	 * @param data                data for encryption
	 * @param mosipSecurityMethod {@link MosipSecurityMethod} for processing
	 * @return Processed array
	 * @throws MosipInvalidDataException if data is not valid in length,corrupted
	 * @throws MosipInvalidKeyException  if key is not valid in length,corrupted and
	 *                                   wrong
	 */
	public static byte[] asymmetricPublicEncrypt(byte[] publicKey, byte[] data, MosipSecurityMethod mosipSecurityMethod)
			throws MosipInvalidDataException, MosipInvalidKeyException {
		SecurityUtil.checkMethod(mosipSecurityMethod);
		switch (mosipSecurityMethod) {

		case HYBRID_RSA_AES_WITH_PKCS1PADDING:
			return MosipRSA.hybridRsaAesWithPKCS1Padding(SecurityUtil.bytesToPublicKey(publicKey), data, true);

		case HYBRID_RSA_AES_WITH_OAEP_WITH_MD5_AND_MGF1PADDING:
			return MosipRSA.hybridRsaAesWithOAEPWithMD5AndMGF1Padding(SecurityUtil.bytesToPublicKey(publicKey), data,
					true);

		case HYBRID_RSA_AES_WITH_OAEP_WITH_SHA3512_AND_MGF1PADDING:
			return MosipRSA.hybridRsaAesWithOAEPWithSHA3512AndMGF1Padding(SecurityUtil.bytesToPublicKey(publicKey),
					data, true);

		case RSA_WITH_PKCS1PADDING:
			return MosipRSA.rsaWithPKCS1Padding(SecurityUtil.bytesToPublicKey(publicKey), data, true);

		case RSA_WITH_OAEP_WITH_MD5_AND_MGF1PADDING:
			return MosipRSA.rsaWithOAEPWithMD5AndMGF1Padding(SecurityUtil.bytesToPublicKey(publicKey), data, true);

		case RSA_WITH_OAEP_WITH_SHA3512_AND_MGF1PADDING:
			return MosipRSA.rsaWithOAEPWithSHA3512AndMGF1Padding(SecurityUtil.bytesToPublicKey(publicKey), data, true);

		default:
			throw new MosipNoSuchAlgorithmException(
					MosipSecurityExceptionCodeConstants.MOSIP_NO_SUCH_ALGORITHM_EXCEPTION);
		}
	}

	/**
	 * Symmetric Encrypt with key
	 * 
	 * @param key                 key for encryption
	 * @param data                data for encryption
	 * @param mosipSecurityMethod {@link MosipSecurityMethod} for processing
	 * @return Processed array
	 * @throws MosipInvalidDataException if data is not valid in length,corrupted
	 * @throws MosipInvalidKeyException  if key is not valid in length,corrupted and
	 *                                   wrong
	 */
	public static byte[] symmetricEncrypt(byte[] key, byte[] data, MosipSecurityMethod mosipSecurityMethod)
			throws MosipInvalidDataException, MosipInvalidKeyException {
		SecurityUtil.checkMethod(mosipSecurityMethod);
		switch (mosipSecurityMethod) {

		case AES_WITH_CBC_AND_PKCS7PADDING:
			return MosipAES.aesWithCBCandPKCS7Padding(key, data, true);

		case DES_WITH_CBC_AND_PKCS7PADDING:
			return MosipDES.desWithCBCandPKCS7Padding(key, data, true);

		case TWOFISH_WITH_CBC_AND_PKCS7PADDING:
			return MosipTWOFISH.twoFishWithCBCandPKCS7Padding(key, data, true);

		default:
			throw new MosipNoSuchAlgorithmException(
					MosipSecurityExceptionCodeConstants.MOSIP_NO_SUCH_ALGORITHM_EXCEPTION);
		}
	}
}
