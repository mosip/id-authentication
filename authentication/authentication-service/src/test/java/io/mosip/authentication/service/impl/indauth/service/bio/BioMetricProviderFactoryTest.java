package io.mosip.authentication.service.impl.indauth.service.bio;

import javax.annotation.security.RunAs;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.authentication.service.factory.BiometricProviderFactory;

/**
 * The Class BioMetricProviderFactoryTest.
 * @author Arun Bose S
 */
@RunWith(SpringRunner.class)
public class BioMetricProviderFactoryTest {
	
	/** The bio metric provider factory. */
	@InjectMocks
	private BiometricProviderFactory bioMetricProviderFactory;
	
	/**
	 * Bio factory test.
	 */
	@Test
	public void bioFactoryTest() {
		bioMetricProviderFactory.initProviders();
	}

}
