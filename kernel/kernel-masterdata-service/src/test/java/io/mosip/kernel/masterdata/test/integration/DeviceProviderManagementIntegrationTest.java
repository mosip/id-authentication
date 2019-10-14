package io.mosip.kernel.masterdata.test.integration;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

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

		when(deviceServiceRepository.findByIdAndDeviceProviderId(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(deviceService);
		when(deviceProviderRepository.findByIdAndIsActiveIsTrue(Mockito.anyString())).thenReturn(deviceProvider);
		when(deviceServiceRepository.findByIdAndIsActiveIsTrue(Mockito.anyString())).thenReturn(deviceService);
		when(regDeviceRepository.findByCodeAndIsActiveIsTrue(Mockito.anyString())).thenReturn(registeredDevice);
	}

	@WithUserDetails("zonal-admin")
	@Test
	public void validateDeviceProvider() throws Exception {

		mockBean.perform(get("/deviceprovider/validate/10001/1111/10001/0.1v")).andExpect(status().isOk());
	}

	@WithUserDetails("zonal-admin")
	@Test
	public void validateDeviceProviderWhenRegisteredDeviceNull() throws Exception {
		when(regDeviceRepository.findByCodeAndIsActiveIsTrue(Mockito.anyString())).thenReturn(null);
		mockBean.perform(get("/deviceprovider/validate/10001/1111/10001/0.1v")).andExpect(status().isOk());
	}

	@WithUserDetails("zonal-admin")
	@Test
	public void validateDeviceProviderWhenRegisteredDeviceDataBaseException() throws Exception {
		when(regDeviceRepository.findByCodeAndIsActiveIsTrue(Mockito.anyString()))
				.thenThrow(DataRetrievalFailureException.class);
		mockBean.perform(get("/deviceprovider/validate/10001/1111/10001/0.1v"))
				.andExpect(status().isInternalServerError());
	}

	@WithUserDetails("zonal-admin")
	@Test
	public void validateDeviceProviderWhenUnRegistered() throws Exception {
		registeredDevice.setStatusCode("UnRegistered");
		when(regDeviceRepository.findByCodeAndIsActiveIsTrue(Mockito.anyString())).thenReturn(registeredDevice);
		mockBean.perform(get("/deviceprovider/validate/10001/1111/10001/0.1v")).andExpect(status().isOk());
	}

	@WithUserDetails("zonal-admin")
	@Test
	public void validateDeviceProviderWhenDeviceProviderNull() throws Exception {
		when(deviceProviderRepository.findByIdAndIsActiveIsTrue(Mockito.anyString())).thenReturn(null);
		mockBean.perform(get("/deviceprovider/validate/10001/1111/10001/0.1v")).andExpect(status().isOk());
	}

	@WithUserDetails("zonal-admin")
	@Test
	public void validateDeviceProviderWhenDeviceProviderDbException() throws Exception {
		when(deviceProviderRepository.findByIdAndIsActiveIsTrue(Mockito.anyString()))
				.thenThrow(DataRetrievalFailureException.class);
		mockBean.perform(get("/deviceprovider/validate/10001/1111/10001/0.1v"))
				.andExpect(status().isInternalServerError());
	}

	@WithUserDetails("zonal-admin")
	@Test
	public void validateDeviceProviderWhenDeviceserviceNull() throws Exception {
		when(deviceServiceRepository.findByIdAndIsActiveIsTrue(Mockito.anyString())).thenReturn(null);
		mockBean.perform(get("/deviceprovider/validate/10001/1111/10001/0.1v")).andExpect(status().isOk());
	}

	@WithUserDetails("zonal-admin")
	@Test
	public void validateDeviceProviderWhenDeviceServiceDbException() throws Exception {
		when(deviceServiceRepository.findByIdAndIsActiveIsTrue(Mockito.anyString()))
				.thenThrow(DataRetrievalFailureException.class);
		mockBean.perform(get("/deviceprovider/validate/10001/1111/10001/0.1v"))
				.andExpect(status().isInternalServerError());
	}

	@WithUserDetails("zonal-admin")
	@Test
	public void validateDeviceProviderWhenDeviceserviceVersionMismatch() throws Exception {
		deviceService.setSwVersion("0.3v");
		when(deviceServiceRepository.findByIdAndIsActiveIsTrue(Mockito.anyString())).thenReturn(deviceService);
		mockBean.perform(get("/deviceprovider/validate/10001/1111/10001/0.1v")).andExpect(status().isOk());
	}

	@WithUserDetails("zonal-admin")
	@Test
	public void validateDeviceProviderWhenMappingNull() throws Exception {

		when(deviceServiceRepository.findByIdAndDeviceProviderId(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(null);
		mockBean.perform(get("/deviceprovider/validate/10001/1111/10001/0.1v")).andExpect(status().isOk());
	}

	@WithUserDetails("zonal-admin")
	@Test
	public void validateDeviceProviderWhenMappingDBException() throws Exception {

		when(deviceServiceRepository.findByIdAndDeviceProviderId(Mockito.anyString(), Mockito.anyString()))
				.thenThrow(DataRetrievalFailureException.class);
		mockBean.perform(get("/deviceprovider/validate/10001/1111/10001/0.1v"))
				.andExpect(status().isInternalServerError());
	}

}
