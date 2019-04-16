package io.mosip.authentication.common.impl.indauth.service.bio;

import org.junit.Test;

import io.mosip.authentication.core.spi.provider.bio.MorphoFaceProvider;

/**
 * @author Arun Bose S The Class MorphoIrisProviderTest.
 */
public class MorphoFaceProviderTest {

	/** The morpho iris provider. */
	private MorphoFaceProvider morphoFaceProvider = new MorphoFaceProvider(null);

	/**
	 * Cogent test.
	 */
	@Test
	public void cogentTest() {
		String image = "cdA_sa#233";
		byte[] imageByte = image.getBytes();
		morphoFaceProvider.createMinutiae(imageByte);
		morphoFaceProvider.matchMinutiae(null, null);
		morphoFaceProvider.matchMultiMinutae(null, null);
		morphoFaceProvider.matchMultiImage(null, null);
	}

}
