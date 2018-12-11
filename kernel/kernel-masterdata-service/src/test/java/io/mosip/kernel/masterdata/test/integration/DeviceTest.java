package io.mosip.kernel.masterdata.test.integration;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.masterdata.dto.DeviceDto;
import io.mosip.kernel.masterdata.dto.MachineDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.entity.Device;
import io.mosip.kernel.masterdata.repository.DeviceRepository;
import io.mosip.kernel.masterdata.utils.MapperUtils;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class DeviceTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	DeviceRepository deviceRepository;

	private List<Device> deviceList;
	private List<Object[]> objectList;
	private Device renDevice;
	private DeviceDto deviceDto;
	private RequestDto<DeviceDto> requestDto;

	private ObjectMapper mapper;
	private LocalDateTime specificDate;
	private String deviceJson;

	@Before
	public void deviceSetup() {
		// LocalDateTime specificDate = LocalDateTime.of(2018, Month.JANUARY, 1, 10, 10,
		// 30);
		mapper = new ObjectMapper();
		deviceJson = null;
		specificDate = LocalDateTime.now(ZoneId.of("UTC"));
		Timestamp validDateTime = Timestamp.valueOf(specificDate);
		deviceList = new ArrayList<>();
		renDevice = new Device();
		renDevice.setId("1000");
		renDevice.setName("Printer");
		renDevice.setLangCode("ENG");
		renDevice.setIsActive(true);
		renDevice.setMacAddress("127.0.0.0");
		renDevice.setIpAddress("127.0.0.10");
		renDevice.setSerialNum("234");
		renDevice.setDeviceSpecId("234");
		renDevice.setValidityDateTime(specificDate);
		deviceList.add(renDevice);

		objectList = new ArrayList<>();
		Object objects[] = { "1001", "Laptop", "129.0.0.0", "123", "129.0.0.0", "1212", "ENG", true, validDateTime,
				"LaptopCode" };
		objectList.add(objects);

		deviceDto = new DeviceDto();
		MapperUtils.map(renDevice, deviceDto);

		requestDto = new RequestDto<>();
		requestDto.setId("mosip.match.regcentr.deviceid");
		requestDto.setVer("1.0.0");
		requestDto.setRequest(deviceDto);

		mapper.registerModule(new JavaTimeModule());
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

	}

	@Test
	public void createDeviceTest() throws Exception {

		deviceJson = mapper.writeValueAsString(requestDto);

		when(deviceRepository.create(Mockito.any())).thenReturn(renDevice);
		mockMvc.perform(post("/v1.0/devices").contentType(MediaType.APPLICATION_JSON).content(deviceJson))
				.andExpect(status().isCreated());
	}

	@Test
	public void createDeviceExceptionTest() throws Exception {
		String deviceJson = "{ \"id\": \"string\", \"request\": { \"deviceSpecId\": \"234\", \"id\": \"1000\", \"ipAddress\": \"129.0.0.10\", \"isActive\": true, \"langCode\": \"ENG\", \"macAddress\": \"129.0.0.0\", \"name\": \"Printer\", \"serialNum\": \"234\", \"validityDateTime\": \"2018-12-07T11:37:36.862Z\" }, \"timestamp\": \"2018-12-07T11:37:36.862Z\", \"ver\": \"string\" }";

		Mockito.when(deviceRepository.create(Mockito.any()))
				.thenThrow(new DataAccessLayerException("", "cannot insert", null));
		mockMvc.perform(MockMvcRequestBuilders.post("/v1.0/devices").contentType(MediaType.APPLICATION_JSON)
				.content(deviceJson)).andExpect(status().isInternalServerError());
	}
}
