package io.mosip.kernel.masterdata.test.integration;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
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
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.masterdata.dto.MachineDto;
import io.mosip.kernel.masterdata.dto.RequestDto;
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
	private RequestDto<MachineDto> requestDto;

	ObjectMapper mapper;
	LocalDateTime specificDate;
	String machineJson;

	@Before
	public void machineSetUp() {
		mapper = new ObjectMapper();

		specificDate = LocalDateTime.now(ZoneId.of("UTC"));
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
		machine.setValidityDateTime(specificDate);
		machineList.add(machine);
		
		
		machineHistory = new MachineHistory();

		MapperUtils.mapFieldValues(machine, machineHistory);
		machineDto = new MachineDto();
		MapperUtils.map(machine, machineDto);
		

		requestDto = new RequestDto<>();
		requestDto.setId("mosip.match.regcentr.machineid");
		requestDto.setVer("1.0.0");
		requestDto.setRequest(machineDto);
		
		mapper.registerModule(new JavaTimeModule());
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		
	}

	@Test
	public void getMachineAllSuccessTest() throws Exception {
		when(machineRepository.findAllByIsDeletedFalseOrIsDeletedIsNull()).thenReturn(machineList);
		mockMvc.perform(get("/v1.0/machines")).andExpect(status().isOk());
	}

	@Test
	public void getMachineAllNullResponseTest() throws Exception {
		when(machineRepository.findAllByIsDeletedFalseOrIsDeletedIsNull()).thenReturn(null);
		mockMvc.perform(get("/v1.0/machines")).andExpect(status().isNotFound());
	}

	@Test
	public void getMachineAllFetchExceptionTest() throws Exception {
		when(machineRepository.findAllByIsDeletedFalseOrIsDeletedIsNull())
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/v1.0/machines")).andExpect(status().isInternalServerError());
	}

	// --------------------------------------------
	@Test
	public void getMachineIdLangcodeSuccessTest() throws Exception {
		List<Machine> machines = new ArrayList<Machine>();
		machines.add(machine);
		when(machineRepository.findAllByIdAndLangCodeAndIsDeletedFalseorIsDeletedIsNull(Mockito.anyString(),
				Mockito.anyString())).thenReturn(machines);
		mockMvc.perform(get("/v1.0/machines/{id}/{langcode}", "1000", "ENG")).andExpect(status().isOk());
	}

	@Test
	public void getMachineIdLangcodeNullResponseTest() throws Exception {
		when(machineRepository.findAllByIdAndLangCodeAndIsDeletedFalseorIsDeletedIsNull(Mockito.anyString(),
				Mockito.anyString())).thenReturn(null);
		mockMvc.perform(get("/v1.0/machines/{id}/{langcode}", "1000", "ENG")).andExpect(status().isNotFound());
	}

	@Test
	public void getMachineIdLangcodeFetchExceptionTest() throws Exception {
		when(machineRepository.findAllByIdAndLangCodeAndIsDeletedFalseorIsDeletedIsNull(Mockito.anyString(),
				Mockito.anyString())).thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/v1.0/machines/{id}/{langcode}", "1000", "ENG"))
				.andExpect(status().isInternalServerError());
	}

	// -----------------------------------
	@Test
	public void getMachineLangcodeSuccessTest() throws Exception {
		when(machineRepository.findAllByLangCodeAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString()))
				.thenReturn(machineList);
		mockMvc.perform(get("/v1.0/machines/{langcode}", "ENG")).andExpect(status().isOk());
	}

	@Test
	public void getMachineLangcodeNullResponseTest() throws Exception {
		when(machineRepository.findAllByLangCodeAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString()))
				.thenReturn(null);
		mockMvc.perform(get("/v1.0/machines/{langcode}", "ENG")).andExpect(status().isNotFound());
	}

	@Test
	public void getMachineLangcodeFetchExceptionTest() throws Exception {
		when(machineRepository.findAllByLangCodeAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/v1.0/machines/{langcode}", "ENG")).andExpect(status().isInternalServerError());
	}

	@Test
	public void createMachineTest() throws Exception {

		machineJson = mapper.writeValueAsString(requestDto);

		when(machineRepository.create(Mockito.any())).thenReturn(machine);
		when(machineHistoryRepository.create(Mockito.any())).thenReturn(machineHistory);
		mockMvc.perform(post("/v1.0/machines").contentType(MediaType.APPLICATION_JSON).content(machineJson))
				.andExpect(status().isCreated());
	}


	/*@Test 
	public void createMachineExceptionTest() throws Exception { 
		
		machineJson = mapper.writeValueAsString(requestDto);

		when(machineRepository.create(Mockito.any())).thenThrow(DataAccessLayerException.class);
		//when(machineHistoryRepository.create(Mockito.any())).thenThrow(DataAccessLayerException.class);
		mockMvc.perform(post("/v1.0/machines").contentType(MediaType.APPLICATION_JSON).content(machineJson))
				.andExpect(status().isCreated());
	}*/

}
