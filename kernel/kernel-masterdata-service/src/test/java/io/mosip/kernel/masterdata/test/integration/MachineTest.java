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
import io.mosip.kernel.masterdata.entity.Machine;
import io.mosip.kernel.masterdata.repository.MachineRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class MachineTest {
	
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	MachineRepository machineRepository;

	@Test
	public void createMachineTest() throws Exception {

		Machine machine = new Machine();
		machine.setId("1000");
		machine.setLangCode("ENG");
	
		String machineJson = "{ \"id\": \"string\", \"ver\": \"string\", \"timestamp\": \"\", \"request\": { \"id\": \"1000\", \"ipAddress\": \"127.0.0.1\", \"isActive\": true, \"langCode\": \"ENG\", \"macAddress\": \"127.0.0.2\", \"machineSpecId\": \"1010\", \"name\": \"Printer\", \"serialNum\": \"12345\", \"validityDateTime\": \"2018-12-06T10:57:09.103Z\" } }";
		Mockito.when(machineRepository.create(Mockito.any())).thenReturn(machine);
		mockMvc.perform(MockMvcRequestBuilders.post("/v1.0/machines")
				.contentType(MediaType.APPLICATION_JSON).content(machineJson)).andExpect(status().isCreated());
	}

	@Test
	public void createMachineExceptionTest() throws Exception {

		String machineJson = "{ \"id\": \"string\", \"ver\": \"string\", \"timestamp\": \"\", \"request\": { \"id\": \"1000\", \"ipAddress\": \"127.0.0.1\", \"isActive\": true, \"langCode\": \"ENG\", \"macAddress\": \"127.0.0.2\", \"machineSpecId\": \"1010\", \"name\": \"Printer\", \"serialNum\": \"12345\", \"validityDateTime\": \"2018-12-06T10:57:09.103Z\" } }";
		Mockito.when(machineRepository.create(Mockito.any()))
				.thenThrow(new DataAccessLayerException("", "cannot insert", null));
		mockMvc.perform(MockMvcRequestBuilders.post("/v1.0/machines")
				.contentType(MediaType.APPLICATION_JSON).content(machineJson)).andExpect(status().isInternalServerError());
	}

}
