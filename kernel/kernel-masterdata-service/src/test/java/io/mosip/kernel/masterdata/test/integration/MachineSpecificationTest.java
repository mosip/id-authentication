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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;


import org.springframework.http.MediaType;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.masterdata.dto.MachineSpecificationDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.entity.MachineSpecification;
import io.mosip.kernel.masterdata.repository.MachineSpecificationRepository;
import io.mosip.kernel.masterdata.utils.MapperUtils;



@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class MachineSpecificationTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private MachineSpecificationRepository machineSpecificationRepository;
	
	private ObjectMapper mapper;
	private MachineSpecification machineSpecification;
	private MachineSpecificationDto machineSpecificationDto;
	private RequestDto<MachineSpecificationDto> requestDto;
	
	@Before
	public void machineSpecificationRepositorySetUp() {
		mapper = new ObjectMapper();

		
		machineSpecification = new MachineSpecification();
		machineSpecification.setId("1000");
		machineSpecification.setLangCode("ENG");
		machineSpecification.setName("laptop");
		machineSpecification.setIsActive(true);
		machineSpecification.setDescription("HP Description" );
		machineSpecification.setBrand("HP");
		machineSpecification.setMachineTypeCode("1231");
		machineSpecification.setLangCode("ENG");
		machineSpecification.setMinDriverversion("version 0.1");
		machineSpecification.setModel("3168ngw");
	
		
		machineSpecificationDto = new MachineSpecificationDto();
		MapperUtils.map(machineSpecification, machineSpecificationDto);

		requestDto = new RequestDto<>();
		requestDto.setId("mosip.match.regcentr.machineSpecificationcode");
		requestDto.setVer("1.0.0");
		requestDto.setRequest(machineSpecificationDto);

	}
	
	@Test
	public void createMachineSpecificationTest() throws Exception {

		String machineSpecificationJson = mapper.writeValueAsString(requestDto);

		when(machineSpecificationRepository.create(Mockito.any())).thenReturn(machineSpecification);
		mockMvc.perform(post("/v1.0/machinespecifications").contentType(MediaType.APPLICATION_JSON).content(machineSpecificationJson))
		.andExpect(status().isCreated());
		
	}
	
	@Test
	public void createMachineSpecificationExceptionTest() throws Exception {
		
		String machineSpecificationJson = mapper.writeValueAsString(requestDto);

		Mockito.when(machineSpecificationRepository.create(Mockito.any()))
				.thenThrow(new DataAccessLayerException("", "cannot insert", null));
		mockMvc.perform(
				MockMvcRequestBuilders.post("/v1.0/machinespecifications").contentType(MediaType.APPLICATION_JSON).content(machineSpecificationJson))
				.andExpect(status().isInternalServerError());
		
	}

}
