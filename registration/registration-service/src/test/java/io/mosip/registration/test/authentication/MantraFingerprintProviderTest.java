package io.mosip.registration.test.authentication;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import MFS100.DeviceInfo;
import MFS100.FingerData;
import MFS100.MFS100;
import io.mosip.registration.device.fp.impl.MantraFingerprintProvider;
import io.mosip.registration.service.BaseService;

public class MantraFingerprintProviderTest {

	@InjectMocks
	private MantraFingerprintProvider fingerprintProvider;

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@Mock
	private BaseService baseService;
	
	@Mock
	DeviceInfo deviceInfo;
	
	@Mock
	MFS100 fpDevice;
	
	@Mock
	FingerData fingerData;

	@Test
	public void captureFingerprintTest() {
		when(fpDevice.Init()).thenReturn(0);		
		when(fpDevice.GetDeviceInfo()).thenReturn(deviceInfo);
		when(deviceInfo.SerialNo()).thenReturn("1");
		when(baseService.isValidDevice("Fingerprint", "Mantra", "1")).thenReturn(true);
		when(fpDevice.StartCapture(90, 5, false)).thenReturn(1);
		assertThat(fingerprintProvider.captureFingerprint(90, 5, "minutia"), is(0));
	}
	
	@Test
	public void captureFingerprintNegativeTest() {
		when(fpDevice.Init()).thenReturn(1);		
		assertThat(fingerprintProvider.captureFingerprint(90, 5, "minutia"), is(-1));
	}
	
	@Test
	public void captureFingerprintNegTest() {
		when(fpDevice.Init()).thenReturn(0);		
		when(fpDevice.GetDeviceInfo()).thenReturn(deviceInfo);
		when(deviceInfo.SerialNo()).thenReturn("1");
		when(baseService.isValidDevice("Fingerprint", "Mantra", "1")).thenReturn(false);
		assertThat(fingerprintProvider.captureFingerprint(90, 5, "minutia"), is(-2));
	}
	
	@Test
	public void uninitFpTest() {		
		fingerprintProvider.OnPreview(fingerData);
		fingerprintProvider.OnCaptureCompleted(true, 1, "errorMsg", fingerData);
		fingerprintProvider.uninitFingerPrintDevice();
	}
	
	@Test
	public void getSerialNumberTest() {
		assertThat(fingerprintProvider.getSerialNumber(), is("SF0001"));
	}
}
