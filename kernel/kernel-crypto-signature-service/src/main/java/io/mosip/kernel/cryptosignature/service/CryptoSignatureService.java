package io.mosip.kernel.cryptosignature.service;

import io.mosip.kernel.core.signatureutil.model.SignatureResponse;
import io.mosip.kernel.cryptosignature.dto.SignResponseRequestDto;

public interface CryptoSignatureService {

	/**
	 * Sign response.
	 *
	 * @param signResponseRequestDto
	 *            the signResponseRequestDto
	 * @return the SignatureResponse
	 */
	public SignatureResponse signResponse(SignResponseRequestDto signResponseRequestDto);

}
