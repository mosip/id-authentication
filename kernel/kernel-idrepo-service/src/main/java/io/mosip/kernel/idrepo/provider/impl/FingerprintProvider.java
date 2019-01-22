package io.mosip.kernel.idrepo.provider.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import io.mosip.kernel.core.idrepo.spi.MosipFingerprintProvider;

/**
 * The Class FingerprintProvider.
 *
 * @author Manoj SP
 */
@Component
public class FingerprintProvider implements MosipFingerprintProvider<String> {

	/* (non-Javadoc)
	 * @see io.mosip.kernel.core.idrepo.spi.MosipFingerprintProvider#convertFIRtoFMR(java.util.List)
	 */
	@Override
	public List<String> convertFIRtoFMR(List<String> listOfBIR) {
		return listOfBIR;
	}

}
