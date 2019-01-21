package io.mosip.authentication.service.impl.indauth.service.bio;

import org.junit.Test;
import org.mockito.InjectMocks;

import io.mosip.authentication.service.impl.iris.CogentIrisProvider;



/**
 *
 * The Class CogentIrisProviderTest.
 *  @author Arun Bose S
 */
public class CogentIrisProviderTest {
	
	/** The cogent iris provider. */
	private CogentIrisProvider cogentIrisProvider = new CogentIrisProvider(null);
	
	/**
	 * Cogent test.
	 */
	@Test
	public void cogentTest() {
		String image="cdA_sa#233";
		byte[] imageByte=image.getBytes();
		cogentIrisProvider.createMinutiae(imageByte);
		cogentIrisProvider.matchMinutiae(null,null);
		cogentIrisProvider.matchMultiMinutae(null, null);
		
	}
	
	

}
