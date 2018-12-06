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
import io.mosip.kernel.masterdata.repository.DeviceSpecificationRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class DeviceSpecificationTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private DeviceSpecificationRepository deviceSpecificationRepository;

	@Test
	public void createDeviceSpecificationTest() throws Exception {

		DeviceSpecification deviceSpecification = new DeviceSpecification();
		deviceSpecification.setId("1000");
		deviceSpecification.setLangCode("ENG");

		String json = "{ \"id\": \"string\", \"ver\": \"string\", \"timestamp\": \"2018-12-06T07:01:16.196Z\", \"request\": { \"brand\": \"HP\", \"description\": \"dercs\", \"deviceTypeCode\": \"6655\", \"id\": \"666555\", \"isActive\": true, \"langCode\": \"ENG\", \"minDriverversion\": \"min driver\", \"model\": \"Model\", \"name\": \"HP\" } }";

		Mockito.when(deviceSpecificationRepository.create(Mockito.any())).thenReturn(deviceSpecification);
		mockMvc.perform(MockMvcRequestBuilders.post("/v1.0/devicespecifications")
				.contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isCreated());
	}

	@Test
	public void createDeviceSpecificationExceptionTest() throws Exception {
		String json = "{ \"id\": \"string\", \"ver\": \"string\", \"timestamp\": \"2018-12-06T07:01:16.196Z\", \"request\": { \"brand\": \"HP\", \"description\": \"dercs\", \"deviceTypeCode\": \"6655\", \"id\": \"666555\", \"isActive\": true, \"langCode\": \"ENG\", \"minDriverversion\": \"min driver\", \"model\": \"Model\", \"name\": \"HP\" } }";

		Mockito.when(deviceSpecificationRepository.create(Mockito.any()))
				.thenThrow(new DataAccessLayerException("", "cannot insert", null));
		mockMvc.perform(MockMvcRequestBuilders.post("/v1.0/devicespecifications")
				.contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isInternalServerError());
	}

}
