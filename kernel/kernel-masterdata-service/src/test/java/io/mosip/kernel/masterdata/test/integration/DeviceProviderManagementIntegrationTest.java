package io.mosip.kernel.masterdata.test.integration;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.masterdata.dto.DigitalIdDto;
import io.mosip.kernel.masterdata.dto.ValidateDeviceDto;
import io.mosip.kernel.masterdata.dto.ValidateDeviceHistoryDto;
import io.mosip.kernel.masterdata.entity.DeviceProvider;
import io.mosip.kernel.masterdata.entity.DeviceProviderHistory;
import io.mosip.kernel.masterdata.entity.MOSIPDeviceService;
import io.mosip.kernel.masterdata.entity.MOSIPDeviceServiceHistory;
import io.mosip.kernel.masterdata.entity.RegisteredDevice;
import io.mosip.kernel.masterdata.entity.RegisteredDeviceHistory;
import io.mosip.kernel.masterdata.repository.DeviceProviderHistoryRepository;
import io.mosip.kernel.masterdata.repository.DeviceProviderRepository;
import io.mosip.kernel.masterdata.repository.MOSIPDeviceServiceHistoryRepository;
import io.mosip.kernel.masterdata.repository.MOSIPDeviceServiceRepository;
import io.mosip.kernel.masterdata.repository.RegisteredDeviceHistoryRepository;
import io.mosip.kernel.masterdata.repository.RegisteredDeviceRepository;
import io.mosip.kernel.masterdata.test.TestBootApplication;

/**
 * 
 * @author Srinivasan
 * @since 1.0.0
 *
 */
@SpringBootTest(classes = TestBootApplication.class)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc(print = MockMvcPrint.LOG_DEBUG, printOnlyOnFailure = false)
public class DeviceProviderManagementIntegrationTest {

	@MockBean
	private MOSIPDeviceServiceRepository deviceServiceRepository;

	@MockBean
	private DeviceProviderRepository deviceProviderRepository;

	@MockBean
	private RegisteredDeviceRepository regDeviceRepository;

	@MockBean
	private DeviceProviderHistoryRepository deviceProviderHistoryRepository;

	@MockBean
	private RegisteredDeviceHistoryRepository registeredDeviceHistoryRepository;

	@MockBean
	private MOSIPDeviceServiceHistoryRepository deviceServiceHistoryRepository;

	@Autowired
	private MockMvc mockBean;

	private MOSIPDeviceService deviceService;

	private DeviceProvider deviceProvider;

	private RegisteredDevice registeredDevice;

	private RegisteredDeviceHistory registeredDeviceHistory;

	private DeviceProviderHistory deviceProviderHistory;

	private MOSIPDeviceServiceHistory deviceServiceHistory;

	private static final String DPM_URL = "/deviceprovidermanagement/validate";

	private static final String DPM_HISTORY_URL = "/deviceprovidermanagement/validate/history";

	private ValidateDeviceDto validateDeviceDto;

	private ValidateDeviceHistoryDto validateDeviceHistoryDto;

	@Autowired
	private ObjectMapper objectMapper;

	private RequestWrapper<ValidateDeviceDto> requestWrapper;

	private RequestWrapper<ValidateDeviceHistoryDto> requestWrapperHistory;

	@Before
	public void setUp() {
		deviceService = new MOSIPDeviceService();
		deviceService.setId("1111");
		deviceService.setDeviceProviderId("10001");
		deviceService.setSwVersion("0.1v");

		deviceProvider = new DeviceProvider();
		deviceProvider.setId("1111");

		registeredDevice = new RegisteredDevice();
		registeredDevice.setDeviceId("10001");
		registeredDevice.setStatusCode("Registered");
		registeredDevice.setDpId("1111");
		registeredDevice.setDp("INTEL");
		registeredDevice.setMake("make-updated");
		registeredDevice.setModel("model-updated");
		registeredDevice.setSerialNo("GV3434343M");

		registeredDevice.setDeviceTypeCode("Face");
		registeredDevice.setDeviceSTypeCode("Slab");
		registeredDevice.setStatusCode("registered");
		registeredDevice.setDeviceSubId("1234");
		registeredDevice.setPurpose("Auth");
		registeredDevice.setFirmware("firmware");
		registeredDevice.setCertificationLevel("L0");
		registeredDevice.setFoundationalTPId("foundationalTPId");
		registeredDevice.setFoundationalTrustSignature("foundationalTrustSignature");
		registeredDevice.setDeviceProviderSignature("sign");

		validateDeviceDto = new ValidateDeviceDto();
		validateDeviceDto.setDeviceCode("10001");
		validateDeviceDto.setDeviceServiceVersion("0.1v");
		DigitalIdDto digitalIdDto = new DigitalIdDto();
		digitalIdDto.setDpId("1111");
		digitalIdDto.setDp("INTEL");
		digitalIdDto.setMake("make-updated");
		digitalIdDto.setModel("model-updated");
		digitalIdDto.setSerialNo("BS563Q2230890");
		//digitalIdDto.setType("face");
		validateDeviceDto.setDigitalId(digitalIdDto);

		validateDeviceHistoryDto = new ValidateDeviceHistoryDto();
		validateDeviceHistoryDto.setDeviceCode("10001");
		validateDeviceHistoryDto.setDeviceServiceVersion("0.1v");
		validateDeviceHistoryDto.setDigitalId(digitalIdDto);
		validateDeviceHistoryDto.setTimeStamp(DateUtils.getUTCCurrentDateTimeString());

		requestWrapper = new RequestWrapper<>();
		requestWrapper.setId("1.0");
		requestWrapper.setRequesttime(LocalDateTime.now());
		requestWrapper.setMetadata("masterdata.dpm.validate");

		requestWrapperHistory = new RequestWrapper<>();
		requestWrapper.setId("1.0");
		requestWrapper.setRequesttime(LocalDateTime.now());
		requestWrapper.setMetadata("masterdata.dpm.validate");

		registeredDeviceHistory = new RegisteredDeviceHistory();
		registeredDeviceHistory.setDeviceId("10001");
		registeredDeviceHistory.setStatusCode("Registered");
		registeredDeviceHistory.setDpId("1111");
		registeredDeviceHistory.setDp("INTEL");
		registeredDeviceHistory.setMake("make-updated");
		registeredDeviceHistory.setModel("model-updated");
		registeredDeviceHistory.setSerialNo("GV3434343M");
		registeredDeviceHistory.setEffectivetimes(LocalDateTime.now(ZoneOffset.UTC));

		deviceProviderHistory = new DeviceProviderHistory();
		deviceProviderHistory.setId("1111");
		deviceProviderHistory.setEffectivetimes(LocalDateTime.now(ZoneOffset.UTC));

		deviceServiceHistory = new MOSIPDeviceServiceHistory();
		deviceServiceHistory.setId("1111");
		deviceServiceHistory.setDeviceProviderId("10001");
		deviceServiceHistory.setSwVersion("0.1v");
		deviceServiceHistory.setEffectDateTime(LocalDateTime.now(ZoneOffset.UTC));

	}

	public void mockSuccessTestCases() {
		when(deviceServiceRepository.findByDeviceProviderIdAndSwVersion(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(deviceService);
		when(deviceProviderRepository.findByIdAndIsActiveIsTrue(Mockito.anyString())).thenReturn(deviceProvider);
		when(deviceServiceRepository.findBySwVersionAndIsActiveIsTrue(Mockito.anyString()))
				.thenReturn(Arrays.asList(deviceService));
		when(regDeviceRepository.findByCodeAndIsActiveIsTrue(Mockito.anyString())).thenReturn(registeredDevice);
		when(deviceServiceRepository.findByDeviceProviderIdAndSwVersion(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(deviceService);
		when(deviceProviderHistoryRepository.findDeviceProviderHisByIdAndEffTimes(Mockito.anyString(), Mockito.any()))
				.thenReturn(deviceProviderHistory);
		when(deviceServiceHistoryRepository.findByIdAndIsActiveIsTrueAndByEffectiveTimes(Mockito.anyString(),
				Mockito.any())).thenReturn(Arrays.asList(deviceServiceHistory));
		when(registeredDeviceHistoryRepository.findRegisteredDeviceHistoryByIdAndEffTimes(Mockito.anyString(),
				Mockito.any())).thenReturn(registeredDeviceHistory);
		when(regDeviceRepository.findByCodeAndIsActiveIsTrue(Mockito.anyString())).thenReturn(registeredDevice);
		when(deviceServiceRepository.findByDeviceDetail(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(deviceService);
		when(deviceServiceRepository.findByDeviceProviderIdAndSwVersionAndMakeAndModel(Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(deviceService);

	}
	
	@WithUserDetails("zonal-admin")
	@Test
	public void mockSuccess() throws Exception {
		mockSuccessTestCases();
		requestWrapper.setRequest(validateDeviceDto);
		String req = objectMapper.writeValueAsString(requestWrapper);
		mockBean.perform(post(DPM_URL).contentType(MediaType.APPLICATION_JSON).content(req)).andExpect(status().isOk());
	}

	@WithUserDetails("zonal-admin")
	@Test
	public void validateDeviceProvider() throws Exception {
		mockSuccessTestCases();
		requestWrapper.setRequest(validateDeviceDto);
		String req = objectMapper.writeValueAsString(requestWrapper);
		mockBean.perform(post(DPM_URL).contentType(MediaType.APPLICATION_JSON).content(req)).andExpect(status().isOk());
	}

	@WithUserDetails("zonal-admin")
	@Test
	public void validateDeviceProviderWhenRegisteredDeviceNull() throws Exception {
		mockSuccessTestCases();
		requestWrapper.setRequest(validateDeviceDto);
		String req = objectMapper.writeValueAsString(requestWrapper);
		when(regDeviceRepository.findByCodeAndIsActiveIsTrue(Mockito.anyString())).thenReturn(null);
		mockBean.perform(post(DPM_URL).contentType(MediaType.APPLICATION_JSON).content(req)).andExpect(status().isOk());
	}

	@WithUserDetails("zonal-admin")
	@Test
	public void validateDeviceProviderWhenRegisteredDeviceDataBaseException() throws Exception {
		mockSuccessTestCases();
		requestWrapper.setRequest(validateDeviceDto);
		String req = objectMapper.writeValueAsString(requestWrapper);
		when(regDeviceRepository.findByCodeAndIsActiveIsTrue(Mockito.anyString()))
				.thenThrow(DataRetrievalFailureException.class);
		mockBean.perform(post(DPM_URL).contentType(MediaType.APPLICATION_JSON).content(req))
				.andExpect(status().isInternalServerError());
	}

	@WithUserDetails("zonal-admin")
	@Test
	public void validateDeviceProviderWhenUnRegistered() throws Exception {
		mockSuccessTestCases();
		requestWrapper.setRequest(validateDeviceDto);
		String req = objectMapper.writeValueAsString(requestWrapper);
		registeredDevice.setStatusCode("UnRegistered");
		when(regDeviceRepository.findByCodeAndIsActiveIsTrue(Mockito.anyString())).thenReturn(registeredDevice);
		mockBean.perform(post(DPM_URL).contentType(MediaType.APPLICATION_JSON).content(req)).andExpect(status().isOk());
	}

	@WithUserDetails("zonal-admin")
	@Test
	public void validateDeviceProviderWhenDeviceProviderNull() throws Exception {
		mockSuccessTestCases();
		requestWrapper.setRequest(validateDeviceDto);
		String req = objectMapper.writeValueAsString(requestWrapper);
		when(deviceProviderRepository.findByIdAndIsActiveIsTrue(Mockito.anyString())).thenReturn(null);
		mockBean.perform(post(DPM_URL).contentType(MediaType.APPLICATION_JSON).content(req)).andExpect(status().isOk());
	}

	@WithUserDetails("zonal-admin")
	@Test
	public void validateDeviceProviderWhenDeviceProviderDbException() throws Exception {
		mockSuccessTestCases();
		requestWrapper.setRequest(validateDeviceDto);
		String req = objectMapper.writeValueAsString(requestWrapper);
		when(deviceProviderRepository.findByIdAndIsActiveIsTrue(Mockito.anyString()))
				.thenThrow(DataRetrievalFailureException.class);
		mockBean.perform(post(DPM_URL).contentType(MediaType.APPLICATION_JSON).content(req))
				.andExpect(status().isInternalServerError());
	}

	@WithUserDetails("zonal-admin")
	@Test
	public void validateDeviceProviderWhenDeviceserviceNull() throws Exception {
		mockSuccessTestCases();
		requestWrapper.setRequest(validateDeviceDto);
		String req = objectMapper.writeValueAsString(requestWrapper);
		when(deviceServiceRepository.findBySwVersionAndIsActiveIsTrue(Mockito.anyString()))
				.thenReturn(new ArrayList<MOSIPDeviceService>());
		mockBean.perform(post(DPM_URL).contentType(MediaType.APPLICATION_JSON).content(req)).andExpect(status().isOk());
	}

	@WithUserDetails("zonal-admin")
	@Test
	public void validateDeviceProviderWhenDeviceServiceDbException() throws Exception {
		mockSuccessTestCases();
		requestWrapper.setRequest(validateDeviceDto);
		String req = objectMapper.writeValueAsString(requestWrapper);
		when(deviceServiceRepository.findBySwVersionAndIsActiveIsTrue(Mockito.anyString()))
				.thenThrow(DataRetrievalFailureException.class);
		mockBean.perform(post(DPM_URL).contentType(MediaType.APPLICATION_JSON).content(req))
				.andExpect(status().isInternalServerError());
	}

	@WithUserDetails("zonal-admin")
	@Test
	public void validateDeviceProviderWhenDeviceserviceVersionMismatch() throws Exception {
		mockSuccessTestCases();
		deviceService.setSwVersion("0.3v");
		requestWrapper.setRequest(validateDeviceDto);
		String req = objectMapper.writeValueAsString(requestWrapper);
		when(deviceServiceRepository.findBySwVersionAndIsActiveIsTrue(Mockito.anyString()))
				.thenReturn(Arrays.asList(deviceService));
		mockBean.perform(post(DPM_URL).contentType(MediaType.APPLICATION_JSON).content(req)).andExpect(status().isOk());
	}

	@WithUserDetails("zonal-admin")
	@Test
	public void validateDeviceProviderWhenMappingNull() throws Exception {
		mockSuccessTestCases();
		requestWrapper.setRequest(validateDeviceDto);
		String req = objectMapper.writeValueAsString(requestWrapper);
		when(deviceServiceRepository.findByDeviceDetail(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(null);
		mockBean.perform(post(DPM_URL).contentType(MediaType.APPLICATION_JSON).content(req)).andExpect(status().isOk());
	}

	@WithUserDetails("zonal-admin")
	@Test
	public void validateDeviceProviderWhenMappingDatabaseException() throws Exception {
		mockSuccessTestCases();
		requestWrapper.setRequest(validateDeviceDto);
		String req = objectMapper.writeValueAsString(requestWrapper);
		when(regDeviceRepository.findByCodeAndIsActiveIsTrue(Mockito.anyString()))
				.thenThrow(DataRetrievalFailureException.class);
		mockBean.perform(post(DPM_URL).contentType(MediaType.APPLICATION_JSON).content(req))
				.andExpect(status().isInternalServerError());
	}

	@WithUserDetails("zonal-admin")
	@Test
	public void validateDeviceProviderWhenMappingDBException() throws Exception {
		mockSuccessTestCases();
		requestWrapper.setRequest(validateDeviceDto);
		String req = objectMapper.writeValueAsString(requestWrapper);
		when(deviceServiceRepository.findByDeviceProviderIdAndSwVersionAndMakeAndModel(Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
						.thenThrow(DataRetrievalFailureException.class);
		mockBean.perform(post(DPM_URL).contentType(MediaType.APPLICATION_JSON).content(req))
				.andExpect(status().isInternalServerError());
	}

	@WithUserDetails("zonal-admin")
	@Test
	public void validateDeviceProviderWhenDeviceCodeIsNull() throws Exception {
		mockSuccessTestCases();
		requestWrapper.setRequest(validateDeviceDto);
		String req = objectMapper.writeValueAsString(requestWrapper);
		when(deviceServiceRepository.findByDeviceProviderIdAndSwVersionAndMakeAndModel(Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
						.thenThrow(DataRetrievalFailureException.class);
		mockBean.perform(post(DPM_URL).contentType(MediaType.APPLICATION_JSON).content(req))
				.andExpect(status().isInternalServerError());
	}

	@WithUserDetails("zonal-admin")
	@Test
	public void validateDeviceProviderWhenMappingDeviceServiceDetails() throws Exception {
		mockSuccessTestCases();
		registeredDevice.setDeviceId("1001");
		registeredDevice.setStatusCode("Registered");
		registeredDevice.setDpId("111");
		registeredDevice.setDp("INTE");
		registeredDevice.setMake("make-update");
		registeredDevice.setModel("model-update");
		registeredDevice.setSerialNo("GV343434");
		requestWrapper.setRequest(validateDeviceDto);
		String req = objectMapper.writeValueAsString(requestWrapper);
		mockBean.perform(post(DPM_URL).contentType(MediaType.APPLICATION_JSON).content(req)).andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void validateHistory() throws Exception {
		mockSuccessTestCases();
		requestWrapperHistory.setRequest(validateDeviceHistoryDto);
		String req = objectMapper.writeValueAsString(requestWrapperHistory);
		mockBean.perform(post(DPM_HISTORY_URL).contentType(MediaType.APPLICATION_JSON).content(req))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void validateHistoryWithDeviceServiceHistoryNull() throws Exception {
		mockSuccessTestCases();
		when(deviceServiceHistoryRepository.findByIdAndIsActiveIsTrueAndByEffectiveTimes(Mockito.anyString(),
				Mockito.any())).thenReturn(new ArrayList<>());
		requestWrapperHistory.setRequest(validateDeviceHistoryDto);
		String req = objectMapper.writeValueAsString(requestWrapperHistory);
		mockBean.perform(post(DPM_HISTORY_URL).contentType(MediaType.APPLICATION_JSON).content(req))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void validateHistoryWithDeviceServiceHistoryDBException() throws Exception {
		mockSuccessTestCases();
		when(deviceServiceHistoryRepository.findByIdAndIsActiveIsTrueAndByEffectiveTimes(Mockito.anyString(),
				Mockito.any())).thenThrow(DataRetrievalFailureException.class);
		requestWrapperHistory.setRequest(validateDeviceHistoryDto);
		String req = objectMapper.writeValueAsString(requestWrapperHistory);
		mockBean.perform(post(DPM_HISTORY_URL).contentType(MediaType.APPLICATION_JSON).content(req))
				.andExpect(status().isInternalServerError());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void validateHistoryWithDeviceProviderHistoryNull() throws Exception {
		mockSuccessTestCases();
		when(deviceProviderHistoryRepository.findDeviceProviderHisByIdAndEffTimes(Mockito.anyString(), Mockito.any()))
				.thenReturn(null);
		requestWrapperHistory.setRequest(validateDeviceHistoryDto);
		String req = objectMapper.writeValueAsString(requestWrapperHistory);
		mockBean.perform(post(DPM_HISTORY_URL).contentType(MediaType.APPLICATION_JSON).content(req))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void validateHistoryWithDeviceProviderHistoryDBException() throws Exception {
		mockSuccessTestCases();
		when(deviceProviderHistoryRepository.findDeviceProviderHisByIdAndEffTimes(Mockito.anyString(), Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		requestWrapperHistory.setRequest(validateDeviceHistoryDto);
		String req = objectMapper.writeValueAsString(requestWrapperHistory);
		mockBean.perform(post(DPM_HISTORY_URL).contentType(MediaType.APPLICATION_JSON).content(req))
				.andExpect(status().isInternalServerError());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void validateHistoryWithRegisteredDevicesHistoryNull() throws Exception {
		mockSuccessTestCases();
		when(registeredDeviceHistoryRepository.findRegisteredDeviceHistoryByIdAndEffTimes(Mockito.anyString(),
				Mockito.any())).thenReturn(null);
		requestWrapperHistory.setRequest(validateDeviceHistoryDto);
		String req = objectMapper.writeValueAsString(requestWrapperHistory);
		mockBean.perform(post(DPM_HISTORY_URL).contentType(MediaType.APPLICATION_JSON).content(req))
				.andExpect(status().isOk());
	}

	@Ignore
	@Test
	@WithUserDetails("zonal-admin")
	public void validateHistoryWithRegisteredDevicesHistoryDBException() throws Exception {
		mockSuccessTestCases();
		when(registeredDeviceHistoryRepository.findRegisteredDeviceHistoryByIdAndEffTimes(Mockito.anyString(),
				Mockito.any())).thenThrow(DataRetrievalFailureException.class);
		requestWrapperHistory.setRequest(validateDeviceHistoryDto);
		String req = objectMapper.writeValueAsString(requestWrapperHistory);
		mockBean.perform(post(DPM_HISTORY_URL).contentType(MediaType.APPLICATION_JSON).content(req))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void validateHistoryWithRegisteredDevicesHistoryRevokedStatus() throws Exception {
		mockSuccessTestCases();
		registeredDeviceHistory.setStatusCode("Revoked");
		requestWrapperHistory.setRequest(validateDeviceHistoryDto);
		String req = objectMapper.writeValueAsString(requestWrapperHistory);
		mockBean.perform(post(DPM_HISTORY_URL).contentType(MediaType.APPLICATION_JSON).content(req))
				.andExpect(status().isOk());
	}

	@WithUserDetails("zonal-admin")
	@Test
	public void validateDeviceProviderWhenMappingDeviceServiceHistoryDetails() throws Exception {
		mockSuccessTestCases();
		registeredDeviceHistory.setDeviceId("1001");
		registeredDeviceHistory.setStatusCode("Registered");
		registeredDeviceHistory.setDpId("111");
		registeredDeviceHistory.setDp("INTE");
		registeredDeviceHistory.setMake("make-update");
		registeredDeviceHistory.setModel("model-update");
		registeredDeviceHistory.setSerialNo("GV343434");
		requestWrapperHistory.setRequest(validateDeviceHistoryDto);
		String req = objectMapper.writeValueAsString(requestWrapperHistory);
		mockBean.perform(post(DPM_HISTORY_URL).contentType(MediaType.APPLICATION_JSON).content(req))
				.andExpect(status().isOk());
	}
}
