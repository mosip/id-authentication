package io.mosip.authentication.service.impl.fingerauth.provider.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import MFS100.DeviceInfo;
import MFS100.MFS100;
import io.mosip.authentication.core.dto.fingerprintauth.FingerprintDeviceInfo;

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
