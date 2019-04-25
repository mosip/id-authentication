package io.mosip.kernel.core.signatureutil.spi;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * SignatureUtil interface.
 * 
 * @author Srinivasan
 * @since 1.0.0
 */
public interface SignatureUtil {

	/**
	 * Sign response.
	 *
	 * @param response the response
	 * @return the string
	 */
	public String signResponse(String response);

	boolean validateWithPublicKey(String responseSignature, String responseBody, String publicKey)
			throws InvalidKeySpecException, NoSuchAlgorithmException;

	boolean validateWithPublicKey(String responseSignature, String responseBody)
			throws InvalidKeySpecException, NoSuchAlgorithmException;
}
