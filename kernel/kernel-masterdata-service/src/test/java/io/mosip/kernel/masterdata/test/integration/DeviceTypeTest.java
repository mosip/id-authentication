package io.mosip.kernel.masterdata.test.integration;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.masterdata.entity.DeviceSpecification;
import io.mosip.kernel.masterdata.entity.DeviceType;
import io.mosip.kernel.masterdata.repository.DeviceTypeRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class DeviceTypeTest {
	
	
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	DeviceTypeRepository deviceTypeRepository;

	@Test
	public void createDeviceSpecificationTest() throws Exception {
		DeviceType deviceType = new DeviceType();
		deviceType.setCode("1000");
		deviceType.setLangCode("ENG");
		String deviceTypeJson = "{ \"id\": \"string\", \"ver\": \"string\", \"timestamp\": \"2018-12-06T06:58:37.498Z\", \"request\": { \"code\": \"6655\", \"description\": \"descr\", \"isActive\": true, \"langCode\": \"ENG\", \"name\": \"Printer\" } }";
		
		Mockito.when(deviceTypeRepository.create(Mockito.any())).thenReturn(deviceType);
		mockMvc.perform(MockMvcRequestBuilders.post("/v1.0/devicetypes")
				.contentType(MediaType.APPLICATION_JSON).content(deviceTypeJson)).andExpect(status().isCreated());
	}

	@Test
	public void createDeviceTypeExceptionTest() throws Exception {
		String deviceTypeJson = "{ \"id\": \"string\", \"ver\": \"string\", \"timestamp\": \"2018-12-06T06:58:37.498Z\", \"request\": { \"code\": \"6655\", \"description\": \"descr\", \"isActive\": true, \"langCode\": \"ENG\", \"name\": \"Printer\" } }";
		Mockito.when(deviceTypeRepository.create(Mockito.any()))
				.thenThrow(new DataAccessLayerException("", "cannot insert", null));
		mockMvc.perform(MockMvcRequestBuilders.post("/v1.0/devicetypes")
				.contentType(MediaType.APPLICATION_JSON).content(deviceTypeJson)).andExpect(status().isInternalServerError());
	}


}
