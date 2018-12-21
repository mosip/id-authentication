package io.mosip.kernel.masterdata.test.integration;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
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
import io.mosip.kernel.masterdata.dto.DeviceDto;
import io.mosip.kernel.masterdata.dto.MachineDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.entity.DocumentCategory;
import io.mosip.kernel.masterdata.entity.Machine;
import io.mosip.kernel.masterdata.entity.MachineHistory;
import io.mosip.kernel.masterdata.repository.MachineHistoryRepository;
import io.mosip.kernel.masterdata.repository.MachineRepository;
import io.mosip.kernel.masterdata.utils.MapperUtils;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class MachineTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	MachineRepository machineRepository;
	@MockBean
	MachineHistoryRepository machineHistoryRepository;

	private List<Machine> machineList;
	private Machine machine;
	private MachineHistory machineHistory;
	private MachineDto machineDto;

	LocalDateTime specificDate;
	String machineJson;
	ObjectMapper mapper;

	@Before
	public void machineSetUp() {
		mapper = new ObjectMapper();

		specificDate = LocalDateTime.now(ZoneId.of("UTC"));
		// LocalDateTime specificDate = LocalDateTime.ofInstant(instant, zone)
		machineList = new ArrayList<>();
		machine = new Machine();
		machine.setId("1000");
		machine.setLangCode("ENG");
		machine.setName("HP");
		machine.setIpAddress("129.0.0.0");
		machine.setMacAddress("178.0.0.0");
		machine.setMachineSpecId("1010");
		machine.setSerialNum("123");
		machine.setIsActive(true);
		machine.setCreatedBy("Admin");
		machine.setCreatedDateTime(specificDate);
		// machine.setValidityDateTime(specificDate);
		machineList.add(machine);

		machineHistory = new MachineHistory();

		MapperUtils.mapFieldValues(machine, machineHistory);
		machineDto = new MachineDto();
		MapperUtils.map(machine, machineDto);

	}

	@Test
	public void deleteMachineTest() throws Exception {
		when(machineRepository.findMachineByIdAndIsDeletedFalseorIsDeletedIsNull(Mockito.any())).thenReturn(machine);
		when(machineRepository.update(Mockito.any())).thenReturn(machine);
		when(machineHistoryRepository.create(Mockito.any())).thenReturn(machineHistory);
		mockMvc.perform(delete("/v1.0/machines/1000").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	public void deleteMachineNotFoundExceptionTest() throws Exception {
		when(machineRepository.findMachineByIdAndIsDeletedFalseorIsDeletedIsNull(Mockito.any())).thenReturn(null);

		mockMvc.perform(delete("/v1.0/machines/1000").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());

	}

	@Test
	public void deleteMachineDatabaseConnectionExceptionTest() throws Exception {
		when(machineRepository.findMachineByIdAndIsDeletedFalseorIsDeletedIsNull(Mockito.any())).thenReturn(machine);
		when(machineRepository.update(Mockito.any()))
				.thenThrow(new DataAccessLayerException("", "cannot execute statement", null));
		mockMvc.perform(delete("/v1.0/machines/1000").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError());

	}
	// ------------------------------------------------------------------------

	@Test
	public void updateMachineTest() throws Exception {

		RequestDto<MachineDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.machine.update");
		requestDto.setVer("1.0.0");
		requestDto.setRequest(machineDto);
		String content = mapper.writeValueAsString(requestDto);
		
		when(machineRepository.findMachineByIdAndIsDeletedFalseorIsDeletedIsNull(Mockito.any())).thenReturn(machine);
		Mockito.when(machineRepository.update(Mockito.any())).thenReturn(machine);
		when(machineHistoryRepository.create(Mockito.any())).thenReturn(machineHistory);
		mockMvc.perform(
				MockMvcRequestBuilders.put("/v1.0/machines").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().isOk());
		
		
	}

	@Test
	public void updateMachineNotFoundExceptionTest() throws Exception {

		RequestDto<MachineDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.machine.update");
		requestDto.setVer("1.0.0");
		requestDto.setRequest(machineDto);
		String content = mapper.writeValueAsString(requestDto);
		when(machineRepository.findMachineByIdAndIsDeletedFalseorIsDeletedIsNull(Mockito.any())).thenReturn(null);

		mockMvc.perform(
				MockMvcRequestBuilders.put("/v1.0/machines").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().isNotFound());

	}

	@Test
	public void updateMachineDatabaseConnectionExceptionTest() throws Exception {

		RequestDto<MachineDto> requestDto = new RequestDto<>();
		requestDto.setId("mosip.machine.update");
		requestDto.setVer("1.0.0");
		requestDto.setRequest(machineDto);
		String content = mapper.writeValueAsString(requestDto);
		when(machineRepository.findMachineByIdAndIsDeletedFalseorIsDeletedIsNull(Mockito.any())).thenThrow(DataAccessLayerException.class);

		mockMvc.perform(
				MockMvcRequestBuilders.put("/v1.0/machines").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().isInternalServerError());

	}
}
