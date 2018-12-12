package io.mosip.kernel.masterdata.test.integration;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.masterdata.dto.MachineDto;
import io.mosip.kernel.masterdata.dto.MachineTypeDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.entity.MachineType;
import io.mosip.kernel.masterdata.repository.MachineTypeRepository;
import io.mosip.kernel.masterdata.utils.MapperUtils;


@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class MachineTypeTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private MachineTypeRepository machineTypeRepository;
	
	private ObjectMapper mapper;
	private MachineType machineType;
	private MachineTypeDto machineTypeDto;
	private RequestDto<MachineTypeDto> requestDto;
	
	@Before
	public void machinetypeSetUp() {
		mapper = new ObjectMapper();

		
		machineType = new MachineType();
		machineType.setCode("1000");
		machineType.setLangCode("ENG");
		machineType.setName("HP");
		machineType.setIsActive(true);
		machineType.setDescription("HP Description" );


		
		machineTypeDto = new MachineTypeDto();
		MapperUtils.map(machineType, machineTypeDto);

		requestDto = new RequestDto<>();
		requestDto.setId("mosip.match.regcentr.machinetypecode");
		requestDto.setVer("1.0.0");
		requestDto.setRequest(machineTypeDto);

	}
	
	@Test
	public void createMachineTypeTest() throws Exception {

		String machineTypeJson = mapper.writeValueAsString(requestDto);

		when(machineTypeRepository.create(Mockito.any())).thenReturn(machineType);
		mockMvc.perform(post("/v1.0/machinetypes").contentType(MediaType.APPLICATION_JSON).content(machineTypeJson))
				.andExpect(status().isCreated());
	}
	
	@Test
	public void createMachineTypeExceptionTest() throws Exception {
		
		String machineTypeJson = mapper.writeValueAsString(requestDto);

		Mockito.when(machineTypeRepository.create(Mockito.any()))
				.thenThrow(new DataAccessLayerException("", "cannot insert", null));
		mockMvc.perform(
				MockMvcRequestBuilders.post("/v1.0/machinetypes").contentType(MediaType.APPLICATION_JSON).content(machineTypeJson))
				.andExpect(status().isInternalServerError());
	}

}
