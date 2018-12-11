package io.mosip.kernel.syncdata.controller.test;

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

import io.mosip.kernel.syncdata.dto.ApplicationDto;
import io.mosip.kernel.syncdata.dto.HolidayDto;
import io.mosip.kernel.syncdata.dto.MachineDto;
import io.mosip.kernel.syncdata.dto.MachineSpecificationDto;
import io.mosip.kernel.syncdata.dto.MachineTypeDto;
import io.mosip.kernel.syncdata.dto.response.MasterDataResponseDto;
import io.mosip.kernel.syncdata.exception.MasterDataServiceException;
import io.mosip.kernel.syncdata.service.MasterDataService;
import io.mosip.kernel.syncdata.service.SyncConfigDetailsService;
import net.minidev.json.JSONObject;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class SyncHandlerControllerTest {
	private MasterDataResponseDto masterDataResponseDto;
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private MasterDataService masterDataService;

	@MockBean
	private SyncConfigDetailsService syncConfigDetailsService;

	JSONObject globalConfigMap = null;
	JSONObject regCentreConfigMap=null;

	@Before
	public void setup() {
	
	     configDetialsSyncSetup();
	     syncMasterDataSetup();
		
	}

	public void configDetialsSyncSetup() {
		globalConfigMap = new JSONObject();
		globalConfigMap.put("archivalPolicy", "arc_policy_2");
		globalConfigMap.put("otpTimeOutInMinutes", 2);
		globalConfigMap.put("numberOfWrongAttemptsForOtp", 5);
		globalConfigMap.put("uinLength", 24);

		regCentreConfigMap = new JSONObject();

		regCentreConfigMap.put("fingerprintQualityThreshold", 120);
		regCentreConfigMap.put("irisQualityThreshold", 25);
		regCentreConfigMap.put("irisRetryAttempts", 10);
		regCentreConfigMap.put("faceQualityThreshold", 25);
		regCentreConfigMap.put("faceRetry", 12);
		regCentreConfigMap.put("supervisorVerificationRequiredForExceptions", true);
		regCentreConfigMap.put("operatorRegSubmissionMode", "fingerprint");

	}
	
	public void syncMasterDataSetup() {
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

	@Test
	public void syncGlobalConfigDetailsSuccess() throws Exception {
		when(syncConfigDetailsService.getGlobalConfigDetails()).thenReturn(globalConfigMap);
		mockMvc.perform(get("/globalconfigs")).andExpect(status().isOk());
	}
	
	@Test
	public void syncRegistrationConfigDetailsSuccess() throws Exception {
		when(syncConfigDetailsService.getRegistrationCenterConfigDetails(Mockito.anyString())).thenReturn(globalConfigMap);
		mockMvc.perform(get("/registrationcenterconfig/1")).andExpect(status().isOk());
	}
	
	@Test
	public void syncGlobalConfigDetailsFailure() throws Exception  {
		when(syncConfigDetailsService.getGlobalConfigDetails()).thenThrow(new MasterDataServiceException("KER-SYNC-127","Error occured in service"));
		mockMvc.perform(get("/globalconfigs")).andExpect(status().isInternalServerError());
	}
}
