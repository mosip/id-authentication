package io.mosip.authentication.service.impl.indauth.service.bio;

import org.junit.Test;

import io.mosip.authentication.service.impl.iris.MorphoIrisProvider;


/**
 * @author Arun Bose S
 * The Class MorphoIrisProviderTest.
 */
public class MorphoIrisProviderTest {
	
/** The morpho iris provider. */
private MorphoIrisProvider morphoIrisProvider = new MorphoIrisProvider(null);
	
	/**
	 * Cogent test.
	 */
	@Test
	public void cogentTest() {
		String image="cdA_sa#233";
		byte[] imageByte=image.getBytes();
		morphoIrisProvider.createMinutiae(imageByte);
		morphoIrisProvider.matchMinutiae(null,null);
		morphoIrisProvider.matchMultiMinutae(null, null);
		
	}

}
