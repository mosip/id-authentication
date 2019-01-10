package io.mosip.kernel.otpmanager.util;

import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Component;

import io.mosip.kernel.otpmanager.util.PasscodeGenerator.Signer;

/**
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@Component
public class OtpProvider {

	/**
	 * @param key
	 * @param otpLength
	 * @param authenticationCode
	 * @return
	 */
	public String computeOtp(String key, int otpLength, String authenticationCode) {

		try {
			PasscodeGenerator pcg = new PasscodeGenerator(getSigning(key, authenticationCode), otpLength);

			return pcg.generateResponseCode(System.currentTimeMillis());
		} catch (GeneralSecurityException e) {

		}
		return null;

	}

	/**
	 * @param secret
	 * @param macAlgo
	 * @return
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
		} catch (NoSuchAlgorithmException error) {

		} catch (InvalidKeyException error) {

		}

		return null;
	}

}
