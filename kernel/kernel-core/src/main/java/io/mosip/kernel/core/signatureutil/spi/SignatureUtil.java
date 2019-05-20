package io.mosip.kernel.core.signatureutil.spi;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import io.mosip.kernel.core.signatureutil.model.SignatureResponse;

/**
 * SignatureUtil interface.
 * 
 * @author Srinivasan
 * @since 1.0.0
 */
public interface SignatureUtil {
	
	public SignatureResponse sign(String data, String timestamp);

	/**
	 * Validate with public key.
	 *
	 * @param responseSignature the response signature
	 * @param responseBody      the response body
	 * @param publicKey         the base64 encoded public key string
	 * @return true, if successful
	 * @throws InvalidKeySpecException  the invalid key spec exception
	 * @throws NoSuchAlgorithmException the no such algorithm exception
	 */
	public boolean validateWithPublicKey(String signature, String data, String publickey)
			throws InvalidKeySpecException, NoSuchAlgorithmException;

	/**
	 * Validate with public key.
	 *
	 * @param responseSignature the response signature
	 * @param responseBody      the response body
	 * @return true, if successful
	 * @throws InvalidKeySpecException  the invalid key spec exception
	 * @throws NoSuchAlgorithmException the no such algorithm exception
	 */
	public boolean validate(String signature, String data, String timestamp)
			throws InvalidKeySpecException, NoSuchAlgorithmException;
}
