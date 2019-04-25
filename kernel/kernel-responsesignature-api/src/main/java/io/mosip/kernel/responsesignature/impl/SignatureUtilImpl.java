package io.mosip.kernel.responsesignature.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.signatureutil.spi.SignatureUtil;
import io.mosip.kernel.responsesignature.util.SigningUtil;


/**
 *  SignatureUtilImpl implements {@link SignatureUtil} .
 *  @author Srinivasan
 *  @since 1.0.0
 */
@Component
public class SignatureUtilImpl implements SignatureUtil {

	/** The signing util. */
	@Autowired
	SigningUtil signingUtil;

	/**
	 * Sign response.
	 *
	 * @param response the response
	 * @return the string
	 */
	@Override
	public String signResponse(String response) {
		return signingUtil.signResponseData(response);

	}

}
