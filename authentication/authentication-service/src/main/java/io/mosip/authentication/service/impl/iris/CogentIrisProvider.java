package io.mosip.authentication.service.impl.iris;

import org.springframework.stereotype.Component;

import io.mosip.authentication.core.spi.irisauth.provider.IrisProvider;



/**
 * The Class CogentIrisProvider.
 *
 * @author Arun Bose S
 */

@Component
public class CogentIrisProvider extends IrisProvider {

	
	/* (non-Javadoc)
	 * @see io.mosip.authentication.core.spi.bioauth.provider.MosipBiometricProvider#createMinutiae(byte[])
	 */
	public String createMinutiae(byte[] inputImage) {
		// TODO Auto-generated method stub
		return null;
	}

}
