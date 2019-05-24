package io.mosip.registration.test.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.mosip.registration.audit.AuditManagerService;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.mdm.constants.MosipBioDeviceConstants;
import io.mosip.registration.mdm.dto.DeviceDiscoveryResponsetDto;
import io.mosip.registration.mdm.integrator.MosipBioDeviceIntegratorImpl;
import io.mosip.registration.util.restclient.ServiceDelegateUtil;

public class MosipBioDeviceIntegratorTest {
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@InjectMocks
	private MosipBioDeviceIntegratorImpl mosipBioDeviceIntegratorImpl;
	@Mock
	private AuditManagerService auditFactory;
	@Mock
	protected ServiceDelegateUtil serviceDelegateUtil;

	@Test
	public void getDeviceDiscoveryTest() throws RegBaseCheckedException {
		DeviceDiscoveryResponsetDto deviceDiscoveryResponsetDto = new DeviceDiscoveryResponsetDto();
		List<DeviceDiscoveryResponsetDto> list = new ArrayList<>();
		list.add(deviceDiscoveryResponsetDto);
		Mockito.when(serviceDelegateUtil.invokeRestService("url", MosipBioDeviceConstants.DEVICE_INFO_SERVICENAME, null,
				Object.class)).thenReturn(list);
		mosipBioDeviceIntegratorImpl.getDeviceDiscovery("url", "deviceType", Object.class);
	}

	@Test
	public void deviceInfo() throws RegBaseCheckedException {
		Mockito.when(serviceDelegateUtil.invokeRestService("url", MosipBioDeviceConstants.DEVICE_INFO_SERVICENAME, null,
				Object.class)).thenReturn(new Object());
		mosipBioDeviceIntegratorImpl.getDeviceInfo("url", Object.class);

	}

	@Test
	public void capture() throws RegBaseCheckedException {
		Map<String, Object> mosipBioCaptureResponseMap = new HashMap<>();
		Mockito.when(serviceDelegateUtil.invokeRestService("url", MosipBioDeviceConstants.DEVICE_INFO_SERVICENAME, null,
				Object.class)).thenReturn(mosipBioCaptureResponseMap);
		mosipBioDeviceIntegratorImpl.capture("url", Object.class, Object.class);

	}

	@Test
	public void captureFailure() throws RegBaseCheckedException {
		LinkedHashMap<String, Object> mosipBioCaptureResponseMap = new LinkedHashMap<>();
		Mockito.when(serviceDelegateUtil.invokeRestService("url", MosipBioDeviceConstants.DEVICE_INFO_SERVICENAME, null,
				Object.class)).thenReturn(null);
		mosipBioDeviceIntegratorImpl.capture("url", Object.class, Object.class);
	}

	@Test
	public void getFrameTest() {
		mosipBioDeviceIntegratorImpl.getFrame();
	}

	@Test
	public void forceCaptureTest() {
		mosipBioDeviceIntegratorImpl.forceCapture();

	}

	@Test
	public void responseParsingTest() {
		mosipBioDeviceIntegratorImpl.responseParsing();
	}

}
