package io.mosip.authentication.service.impl.fingerauth.provider.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import MFS100.DeviceInfo;
import MFS100.MFS100;

@RunWith(MockitoJUnitRunner.class)
public class MantraFingerprintProviderTest {
	@Mock
	MFS100 fpDevice;
	
	@Mock
	DeviceInfo info;
	
	MantraFingerprintProvider fp = new MantraFingerprintProvider();

	@Test
	public void deviceInfoTest() {
		
	}

	@Test
	public void testCapture() {
	}
}
