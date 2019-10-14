package io.mosip.kernel.masterdata.test.integration;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

		when(deviceServiceRepository.findByIdAndDeviceProviderId(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(deviceService);
		when(deviceServiceRepository.findByIdAndIsActiveIsTrue(Mockito.anyString())).thenReturn(deviceService);
		when(regDeviceRepository.findByCodeAndIsActiveIsTrue(Mockito.anyString())).thenReturn(registeredDevice);
	}

	@Ignore
	@WithUserDetails("zonal-admin")
	@Test
	public void validateDeviceProvider() throws Exception {

		mockBean.perform(get("/deviceprovider/validate/10001/1111/10001/0.1v")).andExpect(status().isOk());
	}
}
