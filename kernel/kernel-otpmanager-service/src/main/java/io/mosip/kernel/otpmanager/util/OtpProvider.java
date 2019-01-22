package io.mosip.kernel.otpmanager.util;

import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Component;

import io.mosip.kernel.otpmanager.constant.OtpErrorConstants;
import io.mosip.kernel.otpmanager.exception.CryptoFailureException;
import io.mosip.kernel.otpmanager.exception.OtpServiceException;
import io.mosip.kernel.otpmanager.util.PasscodeGenerator.Signer;

/**
 * This class contains methods to generate OTP.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@Component
public class OtpProvider {

	/**
	 * This method compute OTP against provided key and macAlgo.
	 * 
	 * @param key
	 *            the key against which OTP generates.
	 * @param otpLength
	 *            the length of OTP.
	 * @param macAlgorithm
	 *            the crypto algorithm.
	 * @return the string OTP.
	 */
	public String computeOtp(String key, int otpLength, String macAlgorithm) {

		try {
			PasscodeGenerator pcg = new PasscodeGenerator(getSigning(key, macAlgorithm), otpLength);

			return pcg.generateResponseCode(System.currentTimeMillis());
		} catch (GeneralSecurityException e) {
			throw new CryptoFailureException(OtpErrorConstants.OTP_GEN_CRYPTO_FAILURE.getErrorCode(),
					OtpErrorConstants.OTP_GEN_CRYPTO_FAILURE.getErrorMessage(), e);
		}

	}

	/**
	 * Method to generate Signer for provided key.
	 * 
	 * @param secret
	 *            the key for which signer generates.
	 * @param macAlgo
	 *            the crypto algorithm.
	 * @return the signer.
	 */
	static Signer getSigning(String secret, String macAlgo) {
		try {
			final Mac mac = Mac.getInstance(macAlgo);
			mac.init(new SecretKeySpec(secret.getBytes(), ""));

			return new Signer() {
				@Override
				public byte[] sign(byte[] data) {
					return mac.doFinal(data);
				}
			};
		} catch (NoSuchAlgorithmException | InvalidKeyException error) {
			throw new OtpServiceException(OtpErrorConstants.OTP_GEN_ALGO_FAILURE.getErrorCode(),
					OtpErrorConstants.OTP_GEN_ALGO_FAILURE.getErrorMessage(), error);
		}

	}

}
