package io.mosip.authentication.service.impl.fingerauth.provider.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Map;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import MFS100.DeviceInfo;
import MFS100.FingerData;
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
		Mockito.when(info.Make()).thenReturn("Mantra");
		Mockito.when(info.Model()).thenReturn("MFS100");
		Mockito.when(info.SerialNo()).thenReturn("123456");
		Mockito.when(fpDevice.GetDeviceInfo()).thenReturn(info);
		Mockito.when(fpDevice.IsConnected()).thenReturn(true);
		Mockito.when(fpDevice.Init()).thenReturn(0);
		ReflectionTestUtils.setField(fpDevice, "deviceInfo", info);
		ReflectionTestUtils.setField(fp, "fpDevice", fpDevice);

		FingerprintDeviceInfo dInfo = new FingerprintDeviceInfo();
		dInfo.setDeviceId("123456");
		dInfo.setFingerType("Single");
		dInfo.setMake("Mantra");
		dInfo.setModel("MFS100");
		assertEquals(dInfo, fp.deviceInfo());
	}

	@Test
	public void testCapture() {
		Mockito.when(fpDevice.IsConnected()).thenReturn(true);
		Mockito.when(fpDevice.Init()).thenReturn(0);
		Mockito.when(fpDevice.AutoCapture(Mockito.any(FingerData.class), Mockito.anyInt(), Mockito.anyBoolean(),
				Mockito.anyBoolean())).thenReturn(0);
		Mockito.when(fpDevice.GetLastError()).thenReturn("");
		ReflectionTestUtils.setField(fpDevice, "deviceInfo", info);
		ReflectionTestUtils.setField(fp, "fpDevice", fpDevice);
		Optional<byte[]> captureFingerprint = fp.captureFingerprint(0, 0);
		assertFalse(captureFingerprint.isPresent());
	}

	@Test
	public void testCaptureFail() {
		Mockito.when(fpDevice.IsConnected()).thenReturn(false);
		ReflectionTestUtils.setField(fpDevice, "deviceInfo", info);
		ReflectionTestUtils.setField(fp, "fpDevice", fpDevice);
		Optional<byte[]> captureFingerprint = fp.captureFingerprint(0, 0);
		assertFalse(captureFingerprint.isPresent());
	}

	@Test
	public void testCaptureFailWithError() {
		Mockito.when(fpDevice.IsConnected()).thenReturn(true);
		Mockito.when(fpDevice.Init()).thenReturn(0);
		Mockito.when(fpDevice.AutoCapture(Mockito.any(FingerData.class), Mockito.anyInt(), Mockito.anyBoolean(),
				Mockito.anyBoolean())).thenReturn(1);
		ReflectionTestUtils.setField(fpDevice, "deviceInfo", info);
		ReflectionTestUtils.setField(fp, "fpDevice", fpDevice);
		Optional<byte[]> captureFingerprint = fp.captureFingerprint(0, 0);
		assertFalse(captureFingerprint.isPresent());
	}

	@Test
	public void testSegmentFingerprint() {
		Optional<Map> segmentFingerprint = fp.segmentFingerprint(new byte[10]);
		assertFalse(segmentFingerprint.isPresent());
	}

	@Test
	public void testOnPreview() {
		fp.OnPreview(null);
	}

	@Test
	public void testOnCaptureCompleted() {
		fp.OnCaptureCompleted(false, 0, null, null);
	}
	
	@Test
	public void testCreateMinutiae() {
		fp.createMinutiae(null);
	}
}
