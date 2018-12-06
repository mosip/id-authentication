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
import io.mosip.kernel.masterdata.entity.MachineSpecification;
import io.mosip.kernel.masterdata.repository.MachineSpecificationRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class MachineSpecificationTest {
	
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	MachineSpecificationRepository machineSpecificationRepository;

	@Test
	public void createMachineTest() throws Exception {

		MachineSpecification machineSpeicification = new MachineSpecification();
		machineSpeicification.setId("1000");
		machineSpeicification.setLangCode("ENG");

		String machineSpecJson = "{ \"id\": \"string\", \"ver\": \"string\", \"timestamp\": \"2018-12-06T11:08:41.265Z\", \"request\": { \"brand\": \"intel\", \"description\": \"intel Description\", \"id\": \"1000\", \"isActive\": true, \"langCode\": \"ENG\", \"machineTypeCode\": \"1010\", \"minDriverversion\": \"10\", \"model\": \"2014\", \"name\": \"Laptop\" } }";
		
		Mockito.when(machineSpecificationRepository.create(Mockito.any())).thenReturn(machineSpeicification);
		mockMvc.perform(MockMvcRequestBuilders.post("/v1.0/machinespecifications")
				.contentType(MediaType.APPLICATION_JSON).content(machineSpecJson)).andExpect(status().isCreated());
	}

	@Test
	public void createMachineExceptionTest() throws Exception {

		String machineSpecJson = "{ \"id\": \"string\", \"ver\": \"string\", \"timestamp\": \"2018-12-06T11:08:41.265Z\", \"request\": { \"brand\": \"intel\", \"description\": \"intel Description\", \"id\": \"1000\", \"isActive\": true, \"langCode\": \"ENG\", \"machineTypeCode\": \"1010\", \"minDriverversion\": \"10\", \"model\": \"2014\", \"name\": \"Laptop\" } }";
		
		Mockito.when(machineSpecificationRepository.create(Mockito.any()))
				.thenThrow(new DataAccessLayerException("", "cannot insert", null));
		mockMvc.perform(MockMvcRequestBuilders.post("/v1.0/machinespecifications")
				.contentType(MediaType.APPLICATION_JSON).content(machineSpecJson)).andExpect(status().isInternalServerError());
	}

}
