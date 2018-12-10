package io.mosip.kernel.masterdata.test.integration;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeParseException;
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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import io.mosip.kernel.masterdata.entity.MachineHistory;
import io.mosip.kernel.masterdata.repository.MachineHistoryRepository;
import io.mosip.kernel.masterdata.service.MachineHistoryService;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class MachineHisStoryTest {
	
	@MockBean
	private MachineHistoryRepository machineHistoryRepository;
	
	/*@MockBean
	private MachineHistoryService machineHistoryService;*/
	
	@Autowired
	private MockMvc mockMvc;
	
	List<MachineHistory> machineHistoryList;
	
	@Before
	public void setUp(){
		LocalDateTime eDate = LocalDateTime.of(2018, Month.JANUARY, 1, 10, 10, 30);
		LocalDateTime vDate = LocalDateTime.of(2022, Month.JANUARY, 1, 10, 10, 30);
		machineHistoryList = new ArrayList<>();
		MachineHistory machineHistory = new MachineHistory();
		machineHistory.setId("1000");
		machineHistory.setName("Laptop");
		machineHistory.setIpAddress("129.0.0.0");
		machineHistory.setMacAddress("129.0.0.0");
		machineHistory.setEffectDateTime(eDate);
		machineHistory.setValidityDateTime(vDate);
		machineHistory.setIsActive(true);
		machineHistory.setLangCode("ENG");
		machineHistoryList.add(machineHistory);
	
		
	}

	
	@Test
	public void getMachineHistroyIdLangEffDTimeSuccessTest() throws Exception {
		when(machineHistoryRepository.findByFirstByIdAndLangCodeAndEffectDtimesLessThanEqualAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString(), Mockito.anyString(), Mockito.any())).thenReturn(machineHistoryList);
		mockMvc.perform(get("/v1.0/machineshistories/{id}/{langcode}/{effdatetimes}", "1000", "ENG","2018-01-01T10:10:30.956")).andExpect(status().isOk());
	}

	@Test
	public void getMachineHistroyIdLangEffDTimeNullResponseTest() throws Exception {
		when(machineHistoryRepository.findByFirstByIdAndLangCodeAndEffectDtimesLessThanEqualAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString(), Mockito.anyString(), Mockito.any())).thenReturn(null);
		mockMvc.perform(get("/v1.0/machineshistories/{id}/{langcode}/{effdatetimes}", "1000", "ENG","2018-01-01T10:10:30.956")).andExpect(status().isNotFound());
	}

	@Test
	public void getMachineHistroyIdLangEffDTimeFetchExceptionTest() throws Exception {
		when(machineHistoryRepository.findByFirstByIdAndLangCodeAndEffectDtimesLessThanEqualAndIsDeletedFalseOrIsDeletedIsNull(Mockito.anyString(), Mockito.anyString(), Mockito.any()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/v1.0/machineshistories/{id}/{langcode}/{effdatetimes}", "1000", "ENG","2018-01-01T10:10:30.956")).andExpect(status().isInternalServerError());
	}
	
	/*@Test
	public void getMachineHistroyIdLangEffDTimeDateParseExceptionTest() throws Exception {
		when(machineHistoryService.getMachineHistroyIdLangEffDTime(Mockito.anyString(), Mockito.anyString(), Mockito.any())).thenThrow(DateTimeParseException.class);
		mockMvc.perform(get("/v1.0/machineshistories/{id}/{langcode}/{effdatetimes}", "1000", "ENG","2018-01-01T10:10:30.956")).andExpect(status().isInternalServerError());
	}*/
}
