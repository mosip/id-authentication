package io.mosip.kernel.masterdata.test.integration;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.masterdata.dto.MachineDto;
import io.mosip.kernel.masterdata.dto.MachineSpecificationDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.entity.Machine;
import io.mosip.kernel.masterdata.entity.MachineSpecification;
import io.mosip.kernel.masterdata.repository.MachineRepository;
import io.mosip.kernel.masterdata.repository.MachineSpecificationRepository;
import io.mosip.kernel.masterdata.utils.MapperUtils;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class MachineSpecificationTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	MachineSpecificationRepository machineSpecificationRepository;

	@MockBean
	MachineRepository machineRepository;

	private MachineSpecification machineSpecification;
	private MachineSpecificationDto machineSpecificationDto;
	List<Machine> machineList;
	ObjectMapper mapper;

	@Before
	public void machineSpecificationSetUp() {

		mapper = new ObjectMapper();
		machineSpecification = new MachineSpecification();
		machineSpecification.setId("1000");
		machineSpecification.setLangCode("ENG");
		machineSpecification.setName("laptop");
		machineSpecification.setIsActive(true);
		machineSpecification.setDescription("HP Description");
		machineSpecification.setBrand("HP");
		machineSpecification.setMachineTypeCode("1231");
		machineSpecification.setLangCode("ENG");
		machineSpecification.setMinDriverversion("version 0.1");
		machineSpecification.setModel("3168ngw");

		machineList = new ArrayList<>();

		machineSpecificationDto = new MachineSpecificationDto();
		MapperUtils.map(machineSpecification, machineSpecificationDto);

	}

	@Test
	public void deleteMachineSpecificationTest() throws Exception {
		when(machineSpecificationRepository.findByIdAndIsDeletedFalseorIsDeletedIsNull(Mockito.any()))
				.thenReturn(machineSpecification);
		when(machineRepository.findMachineBymachineSpecIdAndIsDeletedFalseorIsDeletedIsNull(Mockito.any()))
				.thenReturn(machineList);
		when(machineSpecificationRepository.update(Mockito.any())).thenReturn(machineSpecification);
		mockMvc.perform(delete("/v1.0/machinespecifications/1000").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	public void deleteMachineSpecificationDataNotFoundExceptionTest() throws Exception {
		when(machineSpecificationRepository.findByIdAndIsDeletedFalseorIsDeletedIsNull(Mockito.any())).thenReturn(null);

		mockMvc.perform(delete("/v1.0/machinespecifications/1000").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());

	}

	@Test
	public void deleteMachineSpecificationDatabaseConnectionExceptionTest() throws Exception {
		when(machineSpecificationRepository.findByIdAndIsDeletedFalseorIsDeletedIsNull(Mockito.any()))
				.thenReturn(machineSpecification);
		when(machineRepository.findMachineBymachineSpecIdAndIsDeletedFalseorIsDeletedIsNull(Mockito.any()))
				.thenReturn(machineList);
		when(machineSpecificationRepository.update(Mockito.any()))
				.thenThrow(new DataAccessLayerException("", "cannot execute statement", null));
		mockMvc.perform(delete("/v1.0/machinespecifications/1000").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError());

	}

	// ----------------------------------------------------------------
	@Test
	public void updateMachineSpecificationTest() throws Exception {

		RequestDto<MachineSpecificationDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.machineSpecification.update");
		requestDto.setVer("1.0.0");
		requestDto.setRequest(machineSpecificationDto);
		String content = mapper.writeValueAsString(requestDto);
		when(machineSpecificationRepository.findByIdAndIsDeletedFalseorIsDeletedIsNull(Mockito.any()))
				.thenReturn(machineSpecification);
		Mockito.when(machineSpecificationRepository.update(Mockito.any())).thenReturn(machineSpecification);

		mockMvc.perform(MockMvcRequestBuilders.put("/v1.0/machinespecifications")
				.contentType(MediaType.APPLICATION_JSON).content(content)).andExpect(status().isOk());

	}

	@Test
	public void updateMachineSpecificationNotFoundExceptionTest() throws Exception {

		RequestDto<MachineSpecificationDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.machineSpecification.update");
		requestDto.setVer("1.0.0");
		requestDto.setRequest(machineSpecificationDto);
		String content = mapper.writeValueAsString(requestDto);
		when(machineSpecificationRepository.findByIdAndIsDeletedFalseorIsDeletedIsNull(Mockito.any())).thenReturn(null);

		mockMvc.perform(MockMvcRequestBuilders.put("/v1.0/machinespecification").contentType(MediaType.APPLICATION_JSON)
				.content(content)).andExpect(status().isNotFound());

	}

	@Test
	public void updateMachineSpecificationDatabaseConnectionExceptionTest() throws Exception {

		RequestDto<MachineSpecificationDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.machineSpecification.update");
		requestDto.setVer("1.0.0");
		requestDto.setRequest(machineSpecificationDto);
		String content = mapper.writeValueAsString(requestDto);
		when(machineSpecificationRepository.findByIdAndIsDeletedFalseorIsDeletedIsNull(Mockito.any()))
				.thenThrow(DataAccessLayerException.class);

		mockMvc.perform(MockMvcRequestBuilders.put("/v1.0/machinespecifications").contentType(MediaType.APPLICATION_JSON)
				.content(content)).andExpect(status().isInternalServerError());

	}

}
