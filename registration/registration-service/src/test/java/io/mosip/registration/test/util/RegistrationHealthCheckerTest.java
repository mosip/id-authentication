package io.mosip.registration.test.util;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;

import io.mosip.registration.util.healthcheck.RegistrationAppHealthCheckUtil;

public class RegistrationHealthCheckerTest {

	@Mock
	private RegistrationAppHealthCheckUtil registrationAppHealthCheckUtil;

	@Ignore
	@Test
	public void diskSpaceAvailableTest() {
		boolean actualStatus = RegistrationAppHealthCheckUtil.isDiskSpaceAvailable();
		assertTrue(actualStatus);
	}

	@Ignore
	@Test
	public void networkAvailableTest() throws IOException, URISyntaxException {
		boolean actualStatus = RegistrationAppHealthCheckUtil.isNetworkAvailable();
		assertTrue(!actualStatus);
	}
}
