/*
 * 
 * 
 * 
 * 
 * 
 */
package org.mosip.kernel.core.security.decryption;

import org.mosip.kernel.core.security.algorithms.MosipAES;
import org.mosip.kernel.core.security.algorithms.MosipDES;
import org.mosip.kernel.core.security.algorithms.MosipRSA;
import org.mosip.kernel.core.security.algorithms.MosipTWOFISH;
import org.mosip.kernel.core.security.constants.MosipSecurityExceptionCodeConstants;
import org.mosip.kernel.core.security.constants.MosipSecurityMethod;
import org.mosip.kernel.core.security.exception.MosipInvalidDataException;
import org.mosip.kernel.core.security.exception.MosipInvalidKeyException;
import org.mosip.kernel.core.security.exception.MosipNoSuchAlgorithmException;
import org.mosip.kernel.core.security.securityutil.SecurityUtil;

/**
 * Factory class for Mosip Decryptor
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class MosipDecryptor {

	/**
	 * Constructor for this class
	 */
	private MosipDecryptor() {

	}

	/**
	 * Asymmetric Decrypt with private key
	 * 
	 * @param privateKey
	 *            Key for decryption
	 * @param data
	 *            Data for decryption
	 * @param mosipSecurityMethod
	 *            {@link MosipSecurityMethod} for processing
	 * @return Processed array
	 * @throws MosipInvalidDataException
	 *             If data is not valid in length,corrupted
	 * @throws MosipInvalidKeyException
	 *             If key is not valid in length,corrupted and wrong
	 */
	public static byte[] asymmetricPrivateDecrypt(byte[] privateKey, byte[] data,
			MosipSecurityMethod mosipSecurityMethod) throws MosipInvalidDataException, MosipInvalidKeyException {
		switch (mosipSecurityMethod) {

		case HYBRID_RSA_AES_WITH_PKCS1PADDING:
			return MosipRSA.hybridRsaAesWithPKCS1Padding(SecurityUtil.bytesToPrivateKey(privateKey), data, false);

		case HYBRID_RSA_AES_WITH_OAEP_WITH_MD5_AND_MGF1PADDING:
			return MosipRSA.hybridRsaAesWithOAEPWithMD5AndMGF1Padding(SecurityUtil.bytesToPrivateKey(privateKey), data,
					false);

		case HYBRID_RSA_AES_WITH_OAEP_WITH_SHA3512_AND_MGF1PADDING:
			return MosipRSA.hybridRsaAesWithOAEPWithSHA3512AndMGF1Padding(SecurityUtil.bytesToPrivateKey(privateKey),
					data, false);

		case RSA_WITH_PKCS1PADDING:
			return MosipRSA.rsaWithPKCS1Padding(SecurityUtil.bytesToPrivateKey(privateKey), data, false);

		case RSA_WITH_OAEP_WITH_MD5_AND_MGF1PADDING:
			return MosipRSA.rsaWithOAEPWithMD5AndMGF1Padding(SecurityUtil.bytesToPrivateKey(privateKey), data, false);

		case RSA_WITH_OAEP_WITH_SHA3512_AND_MGF1PADDING:
			return MosipRSA.rsaWithOAEPWithSHA3512AndMGF1Padding(SecurityUtil.bytesToPrivateKey(privateKey), data,
					false);

		default:
			throw new MosipNoSuchAlgorithmException(
					MosipSecurityExceptionCodeConstants.MOSIP_NO_SUCH_ALGORITHM_EXCEPTION);
		}
	}

	/**
	 * Asymmetric Decrypt with public key
	 * 
	 * @param publicKey
	 *            Key for decryption
	 * @param data
	 *            Data for decryption
	 * @param mosipSecurityMethod
	 *            {@link MosipSecurityMethod} for processing
	 * @return Processed array
	 * @throws MosipInvalidDataException
	 *             If data is not valid in length,corrupted
	 * @throws MosipInvalidKeyException
	 *             If key is not valid in length,corrupted and wrong
	 */
	public static byte[] asymmetricPublicDecrypt(byte[] publicKey, byte[] data, MosipSecurityMethod mosipSecurityMethod)
			throws MosipInvalidDataException, MosipInvalidKeyException {
		switch (mosipSecurityMethod) {

		case HYBRID_RSA_AES_WITH_PKCS1PADDING:
			return MosipRSA.hybridRsaAesWithPKCS1Padding(SecurityUtil.bytesToPublicKey(publicKey), data, false);

		case HYBRID_RSA_AES_WITH_OAEP_WITH_MD5_AND_MGF1PADDING:
			return MosipRSA.hybridRsaAesWithOAEPWithMD5AndMGF1Padding(SecurityUtil.bytesToPublicKey(publicKey), data,
					false);

		case HYBRID_RSA_AES_WITH_OAEP_WITH_SHA3512_AND_MGF1PADDING:
			return MosipRSA.hybridRsaAesWithOAEPWithSHA3512AndMGF1Padding(SecurityUtil.bytesToPublicKey(publicKey),
					data, false);
		case RSA_WITH_PKCS1PADDING:
			return MosipRSA.rsaWithPKCS1Padding(SecurityUtil.bytesToPublicKey(publicKey), data, false);

		case RSA_WITH_OAEP_WITH_MD5_AND_MGF1PADDING:
			return MosipRSA.rsaWithOAEPWithMD5AndMGF1Padding(SecurityUtil.bytesToPublicKey(publicKey), data, false);

		case RSA_WITH_OAEP_WITH_SHA3512_AND_MGF1PADDING:
			return MosipRSA.rsaWithOAEPWithSHA3512AndMGF1Padding(SecurityUtil.bytesToPublicKey(publicKey), data, false);

		default:
			throw new MosipNoSuchAlgorithmException(
					MosipSecurityExceptionCodeConstants.MOSIP_NO_SUCH_ALGORITHM_EXCEPTION);
		}
	}

	/**
	 * Symmetric Decrypt with key
	 * 
	 * @param key
	 *            Key for decryption
	 * @param data
	 *            Data for decryption
	 * @param mosipSecurityMethod
	 *            {@link MosipSecurityMethod} for processing
	 * @return Processed array
	 * @throws MosipInvalidDataException
	 *             If data is not valid in length,corrupted
	 * @throws MosipInvalidKeyException
	 *             If key is not valid in length,corrupted and wrong
	 */
	public static byte[] symmetricDecrypt(byte[] key, byte[] data, MosipSecurityMethod mosipSecurityMethod)
			throws MosipInvalidDataException, MosipInvalidKeyException {
		switch (mosipSecurityMethod) {

		case AES_WITH_CBC_AND_PKCS7PADDING:
			return MosipAES.aesWithCBCandPKCS7Padding(key, data, false);

		case DES_WITH_CBC_AND_PKCS7PADDING:
			return MosipDES.desWithCBCandPKCS7Padding(key, data, false);

		case TWOFISH_WITH_CBC_AND_PKCS7PADDING:
			return MosipTWOFISH.twoFishWithCBCandPKCS7Padding(key, data, false);

		default:
			throw new MosipNoSuchAlgorithmException(
					MosipSecurityExceptionCodeConstants.MOSIP_NO_SUCH_ALGORITHM_EXCEPTION);
		}
	}

}
