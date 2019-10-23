package io.mosip.kernel.masterdata.test.integration;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
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
import io.mosip.kernel.masterdata.dto.DigitalIdDto;
import io.mosip.kernel.masterdata.dto.ValidateDeviceDto;
import io.mosip.kernel.masterdata.entity.DeviceProvider;
import io.mosip.kernel.masterdata.entity.MOSIPDeviceService;
import io.mosip.kernel.masterdata.entity.RegisteredDevice;
import io.mosip.kernel.masterdata.repository.DeviceProviderRepository;
import io.mosip.kernel.masterdata.repository.MOSIPDeviceServiceRepository;
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

	@Autowired
	private MockMvc mockBean;

	private MOSIPDeviceService deviceService;

	private DeviceProvider deviceProvider;

	private RegisteredDevice registeredDevice;

	private static final String DPM_url = "/deviceprovidermanagement/validate";

	private ValidateDeviceDto validateDeviceDto;

	@Autowired
	private ObjectMapper objectMapper;

	private RequestWrapper<ValidateDeviceDto> requestWrapper;

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
		registeredDevice.setProviderId("1111");
		registeredDevice.setProviderName("INTEL");
		registeredDevice.setMake("make-updated");
		registeredDevice.setModel("model-updated");
		registeredDevice.setSerialNumber("GV3434343M");

		validateDeviceDto = new ValidateDeviceDto();
		validateDeviceDto.setDeviceCode("10001");
		validateDeviceDto.setDeviceServiceVersion("0.1v");
		DigitalIdDto digitalIdDto = new DigitalIdDto();
		digitalIdDto.setDpId("1111");
		digitalIdDto.setDp("INTEL");
		digitalIdDto.setMake("make-updated");
		digitalIdDto.setModel("model-updated");
		digitalIdDto.setSerialNo("GV3434343M");
		validateDeviceDto.setDigitalId(digitalIdDto);

		requestWrapper = new RequestWrapper<>();
		requestWrapper.setId("1.0");
		requestWrapper.setRequesttime(LocalDateTime.now());
		requestWrapper.setMetadata("masterdata.location.create");

		when(deviceServiceRepository.findByDeviceProviderIdAndSwVersion(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(deviceService);
		when(deviceProviderRepository.findByIdAndIsActiveIsTrue(Mockito.anyString())).thenReturn(deviceProvider);
		when(deviceServiceRepository.findBySwVersionAndIsActiveIsTrue(Mockito.anyString()))
				.thenReturn(Arrays.asList(deviceService));
		when(regDeviceRepository.findByCodeAndIsActiveIsTrue(Mockito.anyString())).thenReturn(registeredDevice);
	}

	@WithUserDetails("zonal-admin")
	@Test
	public void validateDeviceProvider() throws Exception {
		requestWrapper.setRequest(validateDeviceDto);
		String req = objectMapper.writeValueAsString(requestWrapper);
		mockBean.perform(post(DPM_url).contentType(MediaType.APPLICATION_JSON).content(req)).andExpect(status().isOk());
	}

	@WithUserDetails("zonal-admin")
	@Test
	public void validateDeviceProviderWhenRegisteredDeviceNull() throws Exception {

		requestWrapper.setRequest(validateDeviceDto);
		String req = objectMapper.writeValueAsString(requestWrapper);
		when(regDeviceRepository.findByCodeAndIsActiveIsTrue(Mockito.anyString())).thenReturn(null);
		mockBean.perform(post(DPM_url).contentType(MediaType.APPLICATION_JSON).content(req)).andExpect(status().isOk());
	}

	@WithUserDetails("zonal-admin")
	@Test
	public void validateDeviceProviderWhenRegisteredDeviceDataBaseException() throws Exception {
		requestWrapper.setRequest(validateDeviceDto);
		String req = objectMapper.writeValueAsString(requestWrapper);
		when(regDeviceRepository.findByCodeAndIsActiveIsTrue(Mockito.anyString()))
				.thenThrow(DataRetrievalFailureException.class);
		mockBean.perform(post(DPM_url).contentType(MediaType.APPLICATION_JSON).content(req))
				.andExpect(status().isInternalServerError());
	}

	@WithUserDetails("zonal-admin")
	@Test
	public void validateDeviceProviderWhenUnRegistered() throws Exception {
		requestWrapper.setRequest(validateDeviceDto);
		String req = objectMapper.writeValueAsString(requestWrapper);
		registeredDevice.setStatusCode("UnRegistered");
		when(regDeviceRepository.findByCodeAndIsActiveIsTrue(Mockito.anyString())).thenReturn(registeredDevice);
		mockBean.perform(post(DPM_url).contentType(MediaType.APPLICATION_JSON).content(req)).andExpect(status().isOk());
	}

	@WithUserDetails("zonal-admin")
	@Test
	public void validateDeviceProviderWhenDeviceProviderNull() throws Exception {
		requestWrapper.setRequest(validateDeviceDto);
		String req = objectMapper.writeValueAsString(requestWrapper);
		when(deviceProviderRepository.findByIdAndIsActiveIsTrue(Mockito.anyString())).thenReturn(null);
		mockBean.perform(post(DPM_url).contentType(MediaType.APPLICATION_JSON).content(req)).andExpect(status().isOk());
	}

	@WithUserDetails("zonal-admin")
	@Test
	public void validateDeviceProviderWhenDeviceProviderDbException() throws Exception {
		requestWrapper.setRequest(validateDeviceDto);
		String req = objectMapper.writeValueAsString(requestWrapper);
		when(deviceProviderRepository.findByIdAndIsActiveIsTrue(Mockito.anyString()))
				.thenThrow(DataRetrievalFailureException.class);
		mockBean.perform(post(DPM_url).contentType(MediaType.APPLICATION_JSON).content(req))
				.andExpect(status().isInternalServerError());
	}

	@WithUserDetails("zonal-admin")
	@Test
	public void validateDeviceProviderWhenDeviceserviceNull() throws Exception {
		requestWrapper.setRequest(validateDeviceDto);
		String req = objectMapper.writeValueAsString(requestWrapper);
		when(deviceServiceRepository.findBySwVersionAndIsActiveIsTrue(Mockito.anyString()))
				.thenReturn(new ArrayList<MOSIPDeviceService>());
		mockBean.perform(post(DPM_url).contentType(MediaType.APPLICATION_JSON).content(req)).andExpect(status().isOk());
	}

	@WithUserDetails("zonal-admin")
	@Test
	public void validateDeviceProviderWhenDeviceServiceDbException() throws Exception {
		requestWrapper.setRequest(validateDeviceDto);
		String req = objectMapper.writeValueAsString(requestWrapper);
		when(deviceServiceRepository.findBySwVersionAndIsActiveIsTrue(Mockito.anyString()))
				.thenThrow(DataRetrievalFailureException.class);
		mockBean.perform(post(DPM_url).contentType(MediaType.APPLICATION_JSON).content(req))
				.andExpect(status().isInternalServerError());
	}

	@WithUserDetails("zonal-admin")
	@Test
	public void validateDeviceProviderWhenDeviceserviceVersionMismatch() throws Exception {
		deviceService.setSwVersion("0.3v");
		requestWrapper.setRequest(validateDeviceDto);
		String req = objectMapper.writeValueAsString(requestWrapper);
		when(deviceServiceRepository.findBySwVersionAndIsActiveIsTrue(Mockito.anyString()))
				.thenReturn(Arrays.asList(deviceService));
		mockBean.perform(post(DPM_url).contentType(MediaType.APPLICATION_JSON).content(req)).andExpect(status().isOk());
	}

	@WithUserDetails("zonal-admin")
	@Test
	public void validateDeviceProviderWhenMappingNull() throws Exception {
		requestWrapper.setRequest(validateDeviceDto);
		String req = objectMapper.writeValueAsString(requestWrapper);
		when(deviceServiceRepository.findByDeviceCode(Mockito.anyString())).thenReturn(null);
		mockBean.perform(post(DPM_url).contentType(MediaType.APPLICATION_JSON).content(req)).andExpect(status().isOk());
	}
	
	@WithUserDetails("zonal-admin")
	@Test
	public void validateDeviceProviderWhenMappingDatabaseException() throws Exception {
		requestWrapper.setRequest(validateDeviceDto);
		String req = objectMapper.writeValueAsString(requestWrapper);
		when(deviceServiceRepository.findByDeviceCode(Mockito.anyString())).thenThrow(DataRetrievalFailureException.class);
		mockBean.perform(post(DPM_url).contentType(MediaType.APPLICATION_JSON).content(req)).andExpect(status().isInternalServerError());
	}

	@WithUserDetails("zonal-admin")
	@Test
	public void validateDeviceProviderWhenMappingDBException() throws Exception {
		requestWrapper.setRequest(validateDeviceDto);
		String req = objectMapper.writeValueAsString(requestWrapper);
		when(deviceServiceRepository.findByDeviceProviderIdAndSwVersion(Mockito.anyString(), Mockito.anyString()))
				.thenThrow(DataRetrievalFailureException.class);
		mockBean.perform(post(DPM_url).contentType(MediaType.APPLICATION_JSON).content(req))
				.andExpect(status().isInternalServerError());
	}

	@WithUserDetails("zonal-admin")
	@Test
	public void validateDeviceProviderWhenDeviceCodeIsNull() throws Exception {
		requestWrapper.setRequest(validateDeviceDto);
		String req = objectMapper.writeValueAsString(requestWrapper);
		when(deviceServiceRepository.findByDeviceProviderIdAndSwVersion(Mockito.anyString(), Mockito.anyString()))
				.thenThrow(DataRetrievalFailureException.class);
		mockBean.perform(post(DPM_url).contentType(MediaType.APPLICATION_JSON).content(req))
				.andExpect(status().isInternalServerError());
	}

	@WithUserDetails("zonal-admin")
	@Test
	public void validateDeviceProviderWhenMappingDeviceServiceDetails() throws Exception {
		registeredDevice.setDeviceId("1001");
		registeredDevice.setStatusCode("Registered");
		registeredDevice.setProviderId("111");
		registeredDevice.setProviderName("INTE");
		registeredDevice.setMake("make-update");
		registeredDevice.setModel("model-update");
		registeredDevice.setSerialNumber("GV343434");
		requestWrapper.setRequest(validateDeviceDto);
		String req = objectMapper.writeValueAsString(requestWrapper);
		mockBean.perform(post(DPM_url).contentType(MediaType.APPLICATION_JSON).content(req)).andExpect(status().isOk());
	}

}
