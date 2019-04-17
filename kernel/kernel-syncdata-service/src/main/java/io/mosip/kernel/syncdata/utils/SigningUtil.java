package io.mosip.kernel.syncdata.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.signatureutil.spi.SignatureUtil;

/**
 * 
 * 
 * @author Srinivasan
 * @since 1.0.0
 */
@Component
public class SigningUtil {

	@Autowired
	SignatureUtil signatureUtil;

	/**
	 * Sign response data.
	 *
	 * @param response
	 *            the response data
	 * @return Encrypted data {@link String}
	 */
	public String signResponseData(String response) {

		return signatureUtil.signResponse(response);
	}

}
