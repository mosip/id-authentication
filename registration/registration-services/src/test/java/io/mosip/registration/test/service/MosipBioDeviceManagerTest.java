package io.mosip.registration.test.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.registration.audit.AuditManagerService;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.mdm.dto.BioDevice;
import io.mosip.registration.mdm.integrator.IMosipBioDeviceIntegrator;
import io.mosip.registration.mdm.service.impl.MosipBioDeviceManager;
import io.mosip.registration.util.healthcheck.RegistrationAppHealthCheckUtil;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ RegistrationAppHealthCheckUtil.class })
public class MosipBioDeviceManagerTest {
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@InjectMocks
	private MosipBioDeviceManager mosipBioDeviceManager;
	@Mock
	private IMosipBioDeviceIntegrator mosipBioDeviceIntegrator;
	@Mock
	private AuditManagerService auditManagerService;

	@Test
	public void init() throws RegBaseCheckedException {
		ReflectionTestUtils.setField(mosipBioDeviceManager, "host", "127.0.0.1");
		ReflectionTestUtils.setField(mosipBioDeviceManager, "hostProtocol", "http");
		ReflectionTestUtils.setField(mosipBioDeviceManager, "portFrom", 8080);
		ReflectionTestUtils.setField(mosipBioDeviceManager, "portTo", 8090);
		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		PowerMockito.when(RegistrationAppHealthCheckUtil.checkServiceAvailability("http://127.0.0.1:8080/deviceInfo"))
				.thenReturn(true);
		List<LinkedHashMap<String, Object>> deviceInfoResponseDtos = new ArrayList<>();

		LinkedHashMap<String, Object> map = new LinkedHashMap<>();
		map.put("type", "FINGERPRINT");
		map.put("subType", "SLAP");
		map.put("status", "RUNNING");
		map.put("deviceInfo", null);
		map.put("deviceInfoSignature", null);
		map.put("serviceVersion", null);
		map.put("callbackId", null);
		map.put("deviceSubId", null);

		deviceInfoResponseDtos.add(map);
		Mockito.when(mosipBioDeviceIntegrator.getDeviceInfo("http://127.0.0.1:8080/deviceInfo", Object[].class))
				.thenReturn(deviceInfoResponseDtos);
		mosipBioDeviceManager.init();

	}

	@Test
	public void single() throws RegBaseCheckedException {
		ReflectionTestUtils.setField(mosipBioDeviceManager, "host", "127.0.0.1");
		ReflectionTestUtils.setField(mosipBioDeviceManager, "hostProtocol", "http");
		ReflectionTestUtils.setField(mosipBioDeviceManager, "portFrom", 8080);
		ReflectionTestUtils.setField(mosipBioDeviceManager, "portTo", 8090);
		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		PowerMockito.when(RegistrationAppHealthCheckUtil.checkServiceAvailability("http://127.0.0.1:8080/deviceInfo"))
				.thenReturn(true);
		List<LinkedHashMap<String, Object>> deviceInfoResponseDtos = new ArrayList<>();

		LinkedHashMap<String, Object> map = new LinkedHashMap<>();
		map.put("type", "FINGERPRINT");
		map.put("subType", "SINGLE");
		map.put("status", "RUNNING");
		map.put("deviceInfo", null);
		map.put("deviceInfoSignature", null);
		map.put("serviceVersion", null);
		map.put("callbackId", null);
		map.put("deviceSubId", null);

		deviceInfoResponseDtos.add(map);
		Mockito.when(mosipBioDeviceIntegrator.getDeviceInfo("http://127.0.0.1:8080/deviceInfo", Object[].class))
				.thenReturn(deviceInfoResponseDtos);
		mosipBioDeviceManager.init();

	}

	@Test
	public void touchLess() throws RegBaseCheckedException {
		ReflectionTestUtils.setField(mosipBioDeviceManager, "host", "127.0.0.1");
		ReflectionTestUtils.setField(mosipBioDeviceManager, "hostProtocol", "http");
		ReflectionTestUtils.setField(mosipBioDeviceManager, "portFrom", 8080);
		ReflectionTestUtils.setField(mosipBioDeviceManager, "portTo", 8090);
		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		PowerMockito.when(RegistrationAppHealthCheckUtil.checkServiceAvailability("http://127.0.0.1:8080/deviceInfo"))
				.thenReturn(true);
		List<LinkedHashMap<String, Object>> deviceInfoResponseDtos = new ArrayList<>();

		LinkedHashMap<String, Object> map = new LinkedHashMap<>();
		map.put("type", "FINGERPRINT");
		map.put("subType", "TOUCHLESS");
		map.put("status", "RUNNING");
		map.put("deviceInfo", null);
		map.put("deviceInfoSignature", null);
		map.put("serviceVersion", null);
		map.put("callbackId", null);
		map.put("deviceSubId", null);

		deviceInfoResponseDtos.add(map);
		Mockito.when(mosipBioDeviceIntegrator.getDeviceInfo("http://127.0.0.1:8080/deviceInfo", Object[].class))
				.thenReturn(deviceInfoResponseDtos);
		mosipBioDeviceManager.init();

	}

	@Test
	public void face() throws RegBaseCheckedException {
		ReflectionTestUtils.setField(mosipBioDeviceManager, "host", "127.0.0.1");
		ReflectionTestUtils.setField(mosipBioDeviceManager, "hostProtocol", "http");
		ReflectionTestUtils.setField(mosipBioDeviceManager, "portFrom", 8080);
		ReflectionTestUtils.setField(mosipBioDeviceManager, "portTo", 8090);
		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		PowerMockito.when(RegistrationAppHealthCheckUtil.checkServiceAvailability("http://127.0.0.1:8080/deviceInfo"))
				.thenReturn(true);
		List<LinkedHashMap<String, Object>> deviceInfoResponseDtos = new ArrayList<>();

		LinkedHashMap<String, Object> map = new LinkedHashMap<>();
		map.put("type", "FACE");
		map.put("subType", "TOUCHLESS");
		map.put("status", "RUNNING");
		map.put("deviceInfo", null);
		map.put("deviceInfoSignature", null);
		map.put("serviceVersion", null);
		map.put("callbackId", null);
		map.put("deviceSubId", null);

		deviceInfoResponseDtos.add(map);
		Mockito.when(mosipBioDeviceIntegrator.getDeviceInfo("http://127.0.0.1:8080/deviceInfo", Object[].class))
				.thenReturn(deviceInfoResponseDtos);
		mosipBioDeviceManager.init();

	}

	@Test
	public void irisDouble() throws RegBaseCheckedException {
		ReflectionTestUtils.setField(mosipBioDeviceManager, "host", "127.0.0.1");
		ReflectionTestUtils.setField(mosipBioDeviceManager, "hostProtocol", "http");
		ReflectionTestUtils.setField(mosipBioDeviceManager, "portFrom", 8080);
		ReflectionTestUtils.setField(mosipBioDeviceManager, "portTo", 8090);
		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		PowerMockito.when(RegistrationAppHealthCheckUtil.checkServiceAvailability("http://127.0.0.1:8080/deviceInfo"))
				.thenReturn(true);
		List<LinkedHashMap<String, Object>> deviceInfoResponseDtos = new ArrayList<>();

		LinkedHashMap<String, Object> map = new LinkedHashMap<>();
		map.put("type", "IRIS");
		map.put("subType", "DOUBLE");
		map.put("status", "RUNNING");
		map.put("deviceInfo", null);
		map.put("deviceInfoSignature", null);
		map.put("serviceVersion", null);
		map.put("callbackId", null);
		map.put("deviceSubId", null);

		deviceInfoResponseDtos.add(map);
		Mockito.when(mosipBioDeviceIntegrator.getDeviceInfo("http://127.0.0.1:8080/deviceInfo", Object[].class))
				.thenReturn(deviceInfoResponseDtos);
		mosipBioDeviceManager.init();

	}

	@Test
	public void iris() throws RegBaseCheckedException {
		ReflectionTestUtils.setField(mosipBioDeviceManager, "host", "127.0.0.1");
		ReflectionTestUtils.setField(mosipBioDeviceManager, "hostProtocol", "http");
		ReflectionTestUtils.setField(mosipBioDeviceManager, "portFrom", 8080);
		ReflectionTestUtils.setField(mosipBioDeviceManager, "portTo", 8090);
		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		PowerMockito.when(RegistrationAppHealthCheckUtil.checkServiceAvailability("http://127.0.0.1:8080/deviceInfo"))
				.thenReturn(true);
		List<LinkedHashMap<String, Object>> deviceInfoResponseDtos = new ArrayList<>();

		LinkedHashMap<String, Object> map = new LinkedHashMap<>();
		map.put("type", "IRIS");
		map.put("subType", "SINGLE");
		map.put("status", "RUNNING");
		map.put("deviceInfo", null);
		map.put("deviceInfoSignature", null);
		map.put("serviceVersion", null);
		map.put("callbackId", null);
		map.put("deviceSubId", null);

		deviceInfoResponseDtos.add(map);
		Mockito.when(mosipBioDeviceIntegrator.getDeviceInfo("http://127.0.0.1:8080/deviceInfo", Object[].class))
				.thenReturn(deviceInfoResponseDtos);
		mosipBioDeviceManager.init();

	}

	@Test
	public void vein() throws RegBaseCheckedException {
		ReflectionTestUtils.setField(mosipBioDeviceManager, "host", "127.0.0.1");
		ReflectionTestUtils.setField(mosipBioDeviceManager, "hostProtocol", "http");
		ReflectionTestUtils.setField(mosipBioDeviceManager, "portFrom", 8080);
		ReflectionTestUtils.setField(mosipBioDeviceManager, "portTo", 8090);
		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		PowerMockito.when(RegistrationAppHealthCheckUtil.checkServiceAvailability("http://127.0.0.1:8080/deviceInfo"))
				.thenReturn(true);
		List<LinkedHashMap<String, Object>> deviceInfoResponseDtos = new ArrayList<>();

		LinkedHashMap<String, Object> map = new LinkedHashMap<>();
		map.put("type", "VEIN");
		map.put("subType", "SINGLE");
		map.put("status", "RUNNING");
		map.put("deviceInfo", null);
		map.put("deviceInfoSignature", null);
		map.put("serviceVersion", null);
		map.put("callbackId", null);
		map.put("deviceSubId", null);

		deviceInfoResponseDtos.add(map);
		Mockito.when(mosipBioDeviceIntegrator.getDeviceInfo("http://127.0.0.1:8080/deviceInfo", Object[].class))
				.thenReturn(deviceInfoResponseDtos);
		mosipBioDeviceManager.init();

	}

	@Test
	public void getDeviceDiscovery() throws RegBaseCheckedException {
		ReflectionTestUtils.setField(mosipBioDeviceManager, "host", "127.0.0.1");
		ReflectionTestUtils.setField(mosipBioDeviceManager, "hostProtocol", "http");
		ReflectionTestUtils.setField(mosipBioDeviceManager, "portFrom", 8080);
		ReflectionTestUtils.setField(mosipBioDeviceManager, "portTo", 8090);
		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		PowerMockito
				.when(RegistrationAppHealthCheckUtil.checkServiceAvailability("http://127.0.0.1:8082/deviceDiscovery"))
				.thenReturn(true);
		mosipBioDeviceManager.getDeviceDiscovery("deviceType");

	}

	@Test
	public void scan() throws RegBaseCheckedException {
		Map<String, BioDevice> deviceRegistry = new HashMap<>();
		deviceRegistry.put("deviceType", new BioDevice());
		mosipBioDeviceManager.scan("deviceType");
	}

}
