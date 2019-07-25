package io.mosip.registration.test.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flickr4java.flickr.util.Base64;

import io.mosip.registration.audit.AuditManagerService;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.mdm.constants.MosipBioDeviceConstants;
import io.mosip.registration.mdm.dto.CaptureResponsBioDataDto;
import io.mosip.registration.mdm.dto.CaptureResponseBioDto;
import io.mosip.registration.mdm.dto.CaptureResponseDto;
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
	@Mock
	private ObjectMapper objectMapper;

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
		Mockito.when(serviceDelegateUtil.invokeRestService(Mockito.anyString(), Mockito.anyString(), Mockito.any(),
				Mockito.any())).thenReturn(new Object());
		mosipBioDeviceIntegratorImpl.getDeviceInfo("url", Object.class);

	}

	@Test
	public void capture() throws RegBaseCheckedException, JsonParseException, JsonMappingException, IOException {

		CaptureResponseDto captureResponseDto = new CaptureResponseDto();
		CaptureResponseBioDto captureResponseBioDto = new CaptureResponseBioDto();

		captureResponseBioDto.setHash("hash");
		captureResponseBioDto.setSessionKey("sessionKey");
		captureResponseBioDto.setSignature("signature");

		CaptureResponsBioDataDto captureResponsBioDataDto = new CaptureResponsBioDataDto();
		captureResponsBioDataDto.setBioExtract(
				"ExtractbioExtractbioExtractctbioExtractctbExtractioExtractbioExtractbioExtractctbioExtractctbioExtract"
						);
		captureResponsBioDataDto.setBioSubType("bioSubType");
		captureResponsBioDataDto.setBioType("bioType");
		captureResponsBioDataDto.setBioValue("bio".getBytes());
		captureResponsBioDataDto.setDeviceCode("deviceCode");
		captureResponsBioDataDto.setDeviceProviderID("deviceProviderID");
		captureResponsBioDataDto.setDeviceServiceID("deviceServiceID");
		captureResponsBioDataDto.setDeviceServiceVersion("deviceServiceVersion");
		captureResponsBioDataDto.setEnv("env");
		captureResponsBioDataDto.setMosipProcess("mosipProcess");
		captureResponsBioDataDto.setQualityScore("qualityScore");
		captureResponsBioDataDto.setRequestedScore("requestedScore");
		captureResponsBioDataDto.setTimestamp("timestamp");
		captureResponsBioDataDto.setRegistrationID("transactionID");

		ObjectMapper objectMapper = new ObjectMapper();

		byte[] byt = Base64.encode(objectMapper.writeValueAsString(captureResponsBioDataDto).getBytes());
		captureResponseBioDto.setCaptureBioData(new String(byt));
		captureResponseBioDto.setCaptureResponseData(captureResponsBioDataDto);

		captureResponseDto.setMosipBioDeviceDataResponses(Arrays.asList(captureResponseBioDto));
		String json = objectMapper.writeValueAsString(captureResponseDto);

		Map<String, Object> mosipBioCaptureResponseMap = new HashMap<>();
		Map<String, Object> mosipBioCaptureResponseMap1 = objectMapper.readValue(json,
				mosipBioCaptureResponseMap.getClass());

		Mockito.when(serviceDelegateUtil.invokeRestService(Mockito.anyString(), Mockito.anyString(), Mockito.any(),
				Mockito.any())).thenReturn(mosipBioCaptureResponseMap1);
		mosipBioDeviceIntegratorImpl.capture("url", new Object(), Object.class);

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

	@Test
	public void captureFailureTest()
			throws RegBaseCheckedException, JsonParseException, JsonMappingException, IOException {

		CaptureResponseDto captureResponseDto = new CaptureResponseDto();
		CaptureResponseBioDto captureResponseBioDto = new CaptureResponseBioDto();

		captureResponseBioDto.setHash("hash");
		captureResponseBioDto.setSessionKey("sessionKey");
		captureResponseBioDto.setSignature("signature");

		CaptureResponsBioDataDto captureResponsBioDataDto = new CaptureResponsBioDataDto();
		captureResponsBioDataDto.setBioExtract("Extract");
		captureResponsBioDataDto.setBioSubType("bioSubType");
		captureResponsBioDataDto.setBioType("bioType");
		captureResponsBioDataDto.setBioValue("bio".getBytes());
		captureResponsBioDataDto.setDeviceCode("deviceCode");
		captureResponsBioDataDto.setDeviceProviderID("deviceProviderID");
		captureResponsBioDataDto.setDeviceServiceID("deviceServiceID");
		captureResponsBioDataDto.setDeviceServiceVersion("deviceServiceVersion");
		captureResponsBioDataDto.setEnv("env");
		captureResponsBioDataDto.setMosipProcess("mosipProcess");
		captureResponsBioDataDto.setQualityScore("qualityScore");
		captureResponsBioDataDto.setRequestedScore("requestedScore");
		captureResponsBioDataDto.setTimestamp("timestamp");
		captureResponsBioDataDto.setRegistrationID("transactionID");

		ObjectMapper objectMapper = new ObjectMapper();

		byte[] byt = Base64.encode(objectMapper.writeValueAsString(captureResponsBioDataDto).getBytes());
		captureResponseBioDto.setCaptureBioData("data");
		captureResponseBioDto.setCaptureResponseData(captureResponsBioDataDto);

		captureResponseDto.setMosipBioDeviceDataResponses(Arrays.asList(captureResponseBioDto));
		String json = objectMapper.writeValueAsString(captureResponseDto);

		Map<String, Object> mosipBioCaptureResponseMap = new HashMap<>();
		Map<String, Object> mosipBioCaptureResponseMap1 = objectMapper.readValue(json,
				mosipBioCaptureResponseMap.getClass());

		Mockito.when(serviceDelegateUtil.invokeRestService(Mockito.anyString(), Mockito.anyString(), Mockito.any(),
				Mockito.any())).thenReturn(mosipBioCaptureResponseMap1);
		mosipBioDeviceIntegratorImpl.capture("url", new Object(), Object.class);

	}

}
