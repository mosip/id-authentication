package io.mosip.kernel.synchandler.controller.test;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import io.mosip.kernel.synchandler.dto.ApplicationDto;
import io.mosip.kernel.synchandler.dto.HolidayDto;
import io.mosip.kernel.synchandler.dto.MachineDto;
import io.mosip.kernel.synchandler.dto.MachineSpecificationDto;
import io.mosip.kernel.synchandler.dto.MachineTypeDto;
import io.mosip.kernel.synchandler.dto.response.MasterDataResponseDto;
import io.mosip.kernel.synchandler.service.MasterDataService;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class SyncHandlerControllerTest {
	private MasterDataResponseDto masterDataResponseDto;
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private MasterDataService masterDataService;

	@Before
	public void setup() {
		masterDataResponseDto = new MasterDataResponseDto();
		List<ApplicationDto> applications = new ArrayList<>();
		applications.add(new ApplicationDto("01", "REG FORM", "REG Form", "ENG", true));
		masterDataResponseDto.setApplications(applications);
		List<HolidayDto> holidays = new ArrayList<>();
		holidays.add(new HolidayDto("1", "2018-01-01", "01", "01", "2018", "NEW YEAR", "ENG", "LOC01", true));
		masterDataResponseDto.setHolidays(holidays);
		List<MachineDto> machines = new ArrayList<>();
		machines.add(new MachineDto("1001", "Laptop", "QWE23456", "1223:23:31:23", "172.12.128.1", "1", "ENG", true,
				LocalDateTime.parse("2018-01-01T01:01:01")));
		masterDataResponseDto.setMachineDetails(machines);
		List<MachineSpecificationDto> machineSpecifications = new ArrayList<>();
		machineSpecifications.add(new MachineSpecificationDto("1", "lenovo Thinkpad", "Lenovo", "T480", "1", "1.0.1",
				"Thinkpad", "ENG", true));
		masterDataResponseDto.setMachineSpecification(machineSpecifications);
		List<MachineTypeDto> machineTypes = new ArrayList<>();
		machineTypes.add(new MachineTypeDto("1", "ENG", "Laptop", "Laptop", true));
		masterDataResponseDto.setMachineType(machineTypes);
	}

	@Test
	public void syncMasterDataSuccess() throws Exception {
		when(masterDataService.syncData(Mockito.anyString(), Mockito.isNull())).thenReturn(masterDataResponseDto);
		mockMvc.perform(get("/syncmasterdata/{machineId}", "1001")).andExpect(status().isOk());
	}

	@Test
	public void syncMasterDataWithlastUpdatedTimestampSuccess() throws Exception {
		when(masterDataService.syncData(Mockito.anyString(), Mockito.any())).thenReturn(masterDataResponseDto);
		mockMvc.perform(get("/syncmasterdata/{machineId}?lastUpdated=2018-01-01T01:01:01", "1001"))
				.andExpect(status().isOk());
	}

	@Test
	public void syncMasterDataWithlastUpdatedTimestampfailure() throws Exception {
		mockMvc.perform(get("/syncmasterdata/{machineId}?lastUpdated=2018-01-016501:01:01", "1001"))
				.andExpect(status().isBadRequest());
	}
}
