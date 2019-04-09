package io.mosip.authentication.service.impl.indauth.service.bio;

import org.junit.Test;

import io.mosip.authentication.service.impl.face.CogentFaceProvider;

/**
 *
 * The Class CogentIrisProviderTest.
 * 
 * @author Arun Bose S
 */
public class CogentFaceProviderTest {

	/** The cogent iris provider. */
	private CogentFaceProvider cogentFaceProvider = new CogentFaceProvider(null);

	/**
	 * Cogent test.
	 */
	@Test
	public void cogentTest() {
		String image = "cdA_sa#233";
		byte[] imageByte = image.getBytes();
		cogentFaceProvider.createMinutiae(imageByte);
		cogentFaceProvider.matchMinutiae(null, null);
		cogentFaceProvider.matchMultiMinutae(null, null);
		cogentFaceProvider.matchMultiImage(null, null);

	}

}
