package io.mosip.kernel.synchandler.service.test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.synchandler.dto.ApplicationDto;
import io.mosip.kernel.synchandler.dto.HolidayDto;
import io.mosip.kernel.synchandler.dto.MachineDto;
import io.mosip.kernel.synchandler.dto.MachineSpecificationDto;
import io.mosip.kernel.synchandler.dto.MachineTypeDto;
import io.mosip.kernel.synchandler.dto.response.MasterDataResponseDto;
import io.mosip.kernel.synchandler.exception.MasterDataServiceException;
import io.mosip.kernel.synchandler.service.MasterDataService;
import io.mosip.kernel.synchandler.service.MasterDataServiceHelper;

@SpringBootTest
@RunWith(SpringRunner.class)
public class MasterDataServiceTest {
	@MockBean
	private MasterDataServiceHelper masterDataServiceHelper;

	@Autowired
	private MasterDataService masterDataService;
	private MasterDataResponseDto masterDataResponseDto;
	private List<ApplicationDto> applications;
	List<HolidayDto> holidays;
	List<MachineDto> machines;
	List<MachineSpecificationDto> machineSpecifications;
	List<MachineTypeDto> machineTypes;

	@Before
	public void setup() {
		masterDataResponseDto = new MasterDataResponseDto();
		applications = new ArrayList<>();
		applications.add(new ApplicationDto("01", "REG FORM", "REG Form", "ENG", true));
		masterDataResponseDto.setApplications(applications);
		holidays = new ArrayList<>();
		holidays.add(new HolidayDto("1", "2018-01-01", "01", "01", "2018", "NEW YEAR", "ENG", "LOC01", true));
		masterDataResponseDto.setHolidays(holidays);
		machines = new ArrayList<>();
		machines.add(new MachineDto("1001", "Laptop", "QWE23456", "1223:23:31:23", "172.12.128.1", "1", "ENG", true,
				LocalDateTime.parse("2018-01-01T01:01:01")));
		masterDataResponseDto.setMachineDetails(machines);
		machineSpecifications = new ArrayList<>();
		machineSpecifications.add(new MachineSpecificationDto("1", "lenovo Thinkpad", "Lenovo", "T480", "1", "1.0.1",
				"Thinkpad", "ENG", true));
		masterDataResponseDto.setMachineSpecification(machineSpecifications);
		machineTypes = new ArrayList<>();
		machineTypes.add(new MachineTypeDto("1", "ENG", "Laptop", "Laptop", true));
		masterDataResponseDto.setMachineType(machineTypes);
	}

	private void mockForSuccess() {
		when(masterDataServiceHelper.getApplications(Mockito.any())).thenReturn(applications);
		when(masterDataServiceHelper.getHolidays(Mockito.any(), Mockito.anyString())).thenReturn(holidays);
		when(masterDataServiceHelper.getMachines(Mockito.anyString(), Mockito.any())).thenReturn(machines);
		when(masterDataServiceHelper.getMachineSpecification(Mockito.anyString(), Mockito.any()))
				.thenReturn(machineSpecifications);
		when(masterDataServiceHelper.getMachineType(Mockito.anyString(), Mockito.any())).thenReturn(machineTypes);
	}

	@Test
	public void syncDataSuccess() {
		mockForSuccess();
		MasterDataResponseDto result = masterDataService.syncData("1001", null);
		assertEquals("1001", result.getMachineDetails().get(0).getId());

	}

	@Test(expected=MasterDataServiceException.class)
	public void syncDataFailure() {
		when(masterDataServiceHelper.getMachines(Mockito.anyString(), Mockito.any()))
				.thenThrow(MasterDataServiceException.class);
		masterDataService.syncData("1001", null);

	}
}
