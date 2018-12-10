package io.mosip.kernel.masterdata.test.integration;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import io.mosip.kernel.masterdata.entity.Device;
import io.mosip.kernel.masterdata.repository.DeviceRepository;


@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class DeviceTest {
	
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private DeviceRepository deviceRepository;
	
	List<Device> deviceList;
	Device device;
	
	public void deviceSetup() {
		LocalDateTime specificDate = LocalDateTime.of(2018, Month.JANUARY, 1, 10, 10, 30);
		Timestamp validDateTime = Timestamp.valueOf(specificDate);
		deviceList = new ArrayList<>();
		 device = new Device();
		device.setId("1000");
		device.setName("Printer");
		device.setLangCode("ENG");
		device.setIsActive(true);
		device.setMacAddress("127.0.0.0");
		device.setIpAddress("127.0.0.10");
		device.setSerialNum("234");
		device.setDeviceSpecId("234");
		device.setValidityDateTime(specificDate);
		deviceList.add(device);

		/*objectList = new ArrayList<>();
		Object objects[] = { "1001", "Laptop", "129.0.0.0", "123", "129.0.0.0", "1212", "ENG", true, validDateTime,
				"LaptopCode" };
		objectList.add(objects);*/
	}

	
	@Test
	public void createDeviceTest() throws Exception {
		String deviceJson = "{ \"id\": \"string\", \"request\": { \"deviceSpecId\": \"234\", \"id\": \"1000\", \"ipAddress\": \"129.0.0.10\", \"isActive\": true, \"langCode\": \"ENG\", \"macAddress\": \"129.0.0.0\", \"name\": \"Printer\", \"serialNum\": \"234\", \"validityDateTime\": \"2018-12-07T11:37:36.862Z\" }, \"timestamp\": \"2018-12-07T11:37:36.862Z\", \"ver\": \"string\" }";

		when(deviceRepository.create(Mockito.any())).thenReturn(device);
		mockMvc.perform(post("/v1.0/devices").contentType(MediaType.APPLICATION_JSON)
				.content(deviceJson)).andExpect(status().isCreated());
	}

}
