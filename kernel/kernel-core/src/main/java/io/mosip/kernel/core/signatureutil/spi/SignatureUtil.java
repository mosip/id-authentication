package io.mosip.kernel.core.signatureutil.spi;


/**
 * SignatureUtil interface.
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
}
