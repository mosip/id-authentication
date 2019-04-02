package io.mosip.kernel.syncdata.utils;

import org.springframework.stereotype.Component;

import io.mosip.kernel.core.util.HMACUtils;


/**
 * HashUtil which wraps HMACUtil to hashes the response.
 * @author Srinivasan
 * @since 1.0.0
 */
@Component
public class HashUtil {

	/**
	 * Hash data.
	 *
	 * @param response the response
	 * @return digestasplainText {@link Stirng}
	 */
	public String hashData(String response) {

		byte[] responseByteArray = HMACUtils.generateHash(response.getBytes());
		System.out.println(HMACUtils.digestAsPlainText(responseByteArray));
		return HMACUtils.digestAsPlainText(responseByteArray);

	}

}
