package io.mosip.kernel.synchandler.service.test;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import io.mosip.kernel.synchandler.dto.ApplicationDto;
import io.mosip.kernel.synchandler.dto.HolidayDto;
import io.mosip.kernel.synchandler.dto.MachineDto;
import io.mosip.kernel.synchandler.dto.MachineSpecificationDto;
import io.mosip.kernel.synchandler.dto.MachineTypeDto;
import io.mosip.kernel.synchandler.dto.response.MasterDataResponseDto;
import io.mosip.kernel.synchandler.exception.MasterDataServiceException;
import io.mosip.kernel.synchandler.service.MasterDataService;
import io.mosip.kernel.synchandler.service.MasterDataServiceHelper;
import io.mosip.kernel.synchandler.service.SyncConfigDetailsService;
import net.minidev.json.JSONObject;

@SpringBootTest
@RunWith(SpringRunner.class)
public class SyncDataServiceTest {
	@MockBean
	private MasterDataServiceHelper masterDataServiceHelper;

	@Autowired
	private MasterDataService masterDataService;

	@Autowired
	RestTemplate restemplate;
	
	@Autowired
	private SyncConfigDetailsService syncConfigDetailsService;
	private MasterDataResponseDto masterDataResponseDto;
	private List<ApplicationDto> applications;
	List<HolidayDto> holidays;
	List<MachineDto> machines;
	List<MachineSpecificationDto> machineSpecifications;
	List<MachineTypeDto> machineTypes;

	JSONObject globalConfigMap = null;
	JSONObject regCentreConfigMap = null;

	@Before
	public void setup() {
		masterDataSyncSetup();
		configDetialsSyncSetup();
	}
	
	public void masterDataSyncSetup() {
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

	@Test(expected = MasterDataServiceException.class)
	public void syncDataFailure() throws InterruptedException, ExecutionException {
		when(masterDataServiceHelper.getMachines(Mockito.anyString(), Mockito.any()))
				.thenThrow(MasterDataServiceException.class);
		masterDataService.syncData("1001", null);

	}

	@Test
	public void globalConfigsyncSuccess() {
        
		MockRestServiceServer server= MockRestServiceServer.bindTo(restemplate).build();
		server.expect(requestTo("http://104.211.212.28:51000/kernel-synchandler-service/test/DEV_SPRINT6_SYNC_HANDLER/global-config.json")).andRespond(withSuccess());
		syncConfigDetailsService.getGlobalConfigDetails();
		
	}

	@Test
	public void registrationConfigsyncSuccess() {
		JSONObject jsonObject = syncConfigDetailsService.getRegistrationCenterConfigDetails("1");
		Assert.assertEquals(120, jsonObject.get("fingerprintQualityThreshold"));
	}
	
	
	@Test(expected=MasterDataServiceException.class)
	public void registrationConfigsyncFailure() {
		
		MockRestServiceServer server= MockRestServiceServer.bindTo(restemplate).build();
		server.expect(requestTo("http://104.211.212.28:51000/kernel-synchandler-service/test/DEV_SPRINT6_SYNC_HANDLER/registration-center-config.json")).andRespond(withBadRequest());
		syncConfigDetailsService.getRegistrationCenterConfigDetails("1");
		}
	
	@Test(expected=MasterDataServiceException.class)
	public void globalConfigsyncFailure() {
		
		MockRestServiceServer server= MockRestServiceServer.bindTo(restemplate).build();
		server.expect(requestTo("http://104.211.212.28:51000/kernel-synchandler-service/test/DEV_SPRINT6_SYNC_HANDLER/global-config.json")).andRespond(withBadRequest());
		syncConfigDetailsService.getGlobalConfigDetails();
		}
}
