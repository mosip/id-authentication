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
import io.mosip.kernel.masterdata.entity.MachineType;
import io.mosip.kernel.masterdata.repository.MachineTypeRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class MachineTypeTest {
	
	
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	MachineTypeRepository machineTypeRepository;

	@Test
	public void createMachineTypeTest() throws Exception {

		MachineType machineType = new MachineType();
		machineType.setCode("1000");
		machineType.setLangCode("ENG");
		
		String machineTypeJson = "{ \"id\": \"string\", \"ver\": \"string\", \"timestamp\": \"2018-12-06T11:18:51.265Z\", \"request\": { \"code\": \"1000\", \"description\": \"Printer Description\", \"isActive\": true, \"langCode\": \"ENG\", \"name\": \"Printer\" } }";
		
		Mockito.when(machineTypeRepository.create(Mockito.any())).thenReturn(machineType);
		mockMvc.perform(MockMvcRequestBuilders.post("/v1.0/machinetypes")
				.contentType(MediaType.APPLICATION_JSON).content(machineTypeJson)).andExpect(status().isCreated());
	}

	@Test
	public void createMachineTypeExceptionTest() throws Exception {

		String machineTypeJson = "{ \"id\": \"string\", \"ver\": \"string\", \"timestamp\": \"2018-12-06T11:18:51.265Z\", \"request\": { \"code\": \"1000\", \"description\": \"Printer Description\", \"isActive\": true, \"langCode\": \"ENG\", \"name\": \"Printer\" } }";
				
		Mockito.when(machineTypeRepository.create(Mockito.any()))
				.thenThrow(new DataAccessLayerException("", "cannot insert", null));
		mockMvc.perform(MockMvcRequestBuilders.post("/v1.0/machinetypes")
				.contentType(MediaType.APPLICATION_JSON).content(machineTypeJson)).andExpect(status().isInternalServerError());
	}


}
