package io.mosip.kernel.syncdata.test.service;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import io.mosip.kernel.syncdata.dto.ApplicationDto;
import io.mosip.kernel.syncdata.dto.HolidayDto;
import io.mosip.kernel.syncdata.dto.MachineDto;
import io.mosip.kernel.syncdata.dto.MachineSpecificationDto;
import io.mosip.kernel.syncdata.dto.MachineTypeDto;
import io.mosip.kernel.syncdata.dto.response.MasterDataResponseDto;
import io.mosip.kernel.syncdata.exception.SyncDataServiceException;
import io.mosip.kernel.syncdata.exception.SyncInvalidArgumentException;
import io.mosip.kernel.syncdata.repository.MachineRepository;
import io.mosip.kernel.syncdata.service.SyncJobDefService;
import io.mosip.kernel.syncdata.service.SyncConfigDetailsService;
import io.mosip.kernel.syncdata.service.SyncRolesService;
import io.mosip.kernel.syncdata.utils.SyncMasterDataServiceHelper;
import net.minidev.json.JSONObject;

@SpringBootTest
@RunWith(SpringRunner.class)
public class SyncDataServiceTest {

	@MockBean
	private SyncMasterDataServiceHelper masterDataServiceHelper;

	@Autowired
	RestTemplate restTemplate;

	@MockBean
	private SyncJobDefService registrationCenterUserService;

	@MockBean
	MachineRepository machineRespository;

	@Autowired
	private SyncRolesService syncRolesService;

	/**
	 * Environment instance
	 */
	@Autowired
	private Environment env;

	/**
	 * file name referred from the properties file
	 */
	@Value("${mosip.kernel.syncdata.registration-center-config-file}")
	private String regCenterfileName;

	/**
	 * file name referred from the properties file
	 */
	@Value("${mosip.kernel.syncdata.global-config-file}")
	private String globalConfigFileName;

	@Value("${mosip.kernel.syncdata.auth-manager-base-uri}")
	private String authUserDetailsBaseUri;

	@Value("${mosip.kernel.syncdata.auth-user-details:/userdetails}")
	private String authUserDetailsUri;

	@Value("${mosip.kernel.syncdata.auth-manager-base-uri}")
	private String authBaseUri;

	@Value("${mosip.kernel.syncdata.auth-manager-roles}")
	private String authAllRolesUri;

	@Value("${mosip.kernel.keymanager-service-publickey-url}")
	private String publicKeyUrl;

	private String configServerUri = null;
	private String configLabel = null;
	private String configProfile = null;
	private String configAppName = null;

	private StringBuilder uriBuilder;

	StringBuilder userDetailsUri;

	private StringBuilder builder;

	@Autowired
	private SyncConfigDetailsService syncConfigDetailsService;
	private MasterDataResponseDto masterDataResponseDto;
	private List<ApplicationDto> applications;
	List<HolidayDto> holidays;
	List<MachineDto> machines;
	List<MachineSpecificationDto> machineSpecifications;
	List<MachineTypeDto> machineTypes;
	Map<String, String> uriParams = null;

	JSONObject globalConfigMap = null;
	JSONObject regCentreConfigMap = null;

	@Before
	public void setup() {
		masterDataSyncSetup();
		configDetialsSyncSetup();
		userDetailsUri = new StringBuilder();
		userDetailsUri.append(authUserDetailsBaseUri).append(authUserDetailsUri);

	}

	public void masterDataSyncSetup() {
		masterDataResponseDto = new MasterDataResponseDto();
		applications = new ArrayList<>();
		applications.add(new ApplicationDto("01", "REG FORM", "REG Form"));
		masterDataResponseDto.setApplications(applications);
		holidays = new ArrayList<>();
		holidays.add(new HolidayDto("1", "2018-01-01", "01", "01", "2018", "NEW YEAR", "LOC01"));
		masterDataResponseDto.setHolidays(holidays);
		machines = new ArrayList<>();
		machines.add(new MachineDto("1001", "Laptop", "QWE23456", "1223:23:31:23", "172.12.128.1", "1",
				LocalDateTime.parse("2018-01-01T01:01:01"),null,null));
		masterDataResponseDto.setMachineDetails(machines);
		machineSpecifications = new ArrayList<>();
		machineSpecifications
				.add(new MachineSpecificationDto("1", "lenovo Thinkpad", "Lenovo", "T480", "1", "1.0.1", "Thinkpad"));
		masterDataResponseDto.setMachineSpecification(machineSpecifications);
		machineTypes = new ArrayList<>();
		machineTypes.add(new MachineTypeDto("1", "Laptop", "Laptop"));
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
		configServerUri = env.getProperty("spring.cloud.config.uri");
		configLabel = env.getProperty("spring.cloud.config.label");
		configProfile = env.getProperty("spring.profiles.active");
		configAppName = env.getProperty("spring.application.name");
		uriBuilder = new StringBuilder();
		uriBuilder.append("/" + configServerUri + "/").append(configAppName + "/").append(configProfile + "/")
				.append(configLabel + "/");

		builder = new StringBuilder();
		builder.append(authBaseUri).append(authAllRolesUri);

	}

	/*
	 * @Test(expected = SyncDataServiceException.class) public void
	 * syncDataFailure() throws InterruptedException, ExecutionException { Machine
	 * machine = new Machine(); machine.setId("10001"); machine.setLangCode("eng");
	 * List<Machine> machines = new ArrayList<>(); machines.add(machine);
	 * when(machineRespository.findByMachineIdAndIsActive(Mockito.anyString())).
	 * thenReturn(machines);
	 * when(masterDataServiceHelper.getMachines(Mockito.anyString(), Mockito.any(),
	 * Mockito.any())) .thenThrow(SyncDataServiceException.class);
	 * masterDataService.syncData("1001", null, null);
	 * 
	 * }
	 */

	// @Test
	public void globalConfigsyncSuccess() {

		MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();
		server.expect(requestTo(uriBuilder.append(globalConfigFileName).toString())).andRespond(withSuccess());
		syncConfigDetailsService.getGlobalConfigDetails();

	}

	// @Test
	public void registrationConfigsyncSuccess() {
		MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();
		server.expect(requestTo(uriBuilder.append(regCenterfileName).toString())).andRespond(withSuccess());
		syncConfigDetailsService.getRegistrationCenterConfigDetails("1");
		// Assert.assertEquals(120, jsonObject.get("fingerprintQualityThreshold"));
	}

	// @Test(expected = SyncDataServiceException.class)
	public void registrationConfigsyncFailure() {

		MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();
		server.expect(requestTo(uriBuilder.append(regCenterfileName).toString())).andRespond(withBadRequest());
		syncConfigDetailsService.getRegistrationCenterConfigDetails("1");
	}

	// @Test(expected = SyncDataServiceException.class)
	public void globalConfigsyncFailure() {

		MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();
		server.expect(requestTo(uriBuilder.append(globalConfigFileName).toString())).andRespond(withBadRequest());
		syncConfigDetailsService.getGlobalConfigDetails();
	}

	// @Test(expected = SyncDataServiceException.class)
	public void globalConfigsyncFileNameNullFailure() {

		MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();
		server.expect(requestTo(uriBuilder.append(globalConfigFileName).toString())).andRespond(withBadRequest());
		syncConfigDetailsService.getGlobalConfigDetails();
	}

	// @Test
	public void getConfigurationSuccess() {
		MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();
		server.expect(requestTo(uriBuilder.append(globalConfigFileName).toString())).andRespond(withSuccess());
		uriBuilder = new StringBuilder();
		uriBuilder.append(configServerUri + "/").append(configAppName + "/").append(configProfile + "/")
				.append(configLabel + "/");
		server.expect(requestTo(uriBuilder.append(regCenterfileName).toString())).andRespond(withSuccess());
		syncConfigDetailsService.getConfiguration("1");
	}

	// ------------------------------------------UserDetails--------------------------//
	/*
	 * // @Test public void getAllUserDetail() { String response =
	 * "{ \"mosipUserDtoList\": [ { \"userName\": \"individual\", \"mail\": \"individual@mosip.io\", \"mobile\": \"8976394859\", \"langCode\": null, \"userPassword\": \"e1NTSEE1MTJ9TkhVb1c2WHpkZVJCa0drbU9tTk9ZcElvdUlNRGl5ODlJK3RhNm04d0FlTWhMSEoyTG4wSVJkNEJ2dkNqVFg4bTBuV2ZySStneXBTVittbVJKWnAxTkFwT3BWY3MxTVU5\", \"name\": \"individual\", \"role\": \"REGISTRATION_ADMIN,INDIVIDUAL\"  } ] }"
	 * ; String regId = "10044"; RegistrationCenterUserResponseDto
	 * registrationCenterUserResponseDto = new RegistrationCenterUserResponseDto();
	 * List<RegistrationCenterUserDto> registrationCenterUserDtos = new
	 * ArrayList<>(); RegistrationCenterUserDto registrationCenterUserDto = new
	 * RegistrationCenterUserDto(); registrationCenterUserDto.setIsActive(true);
	 * registrationCenterUserDto.setRegCenterId(regId);
	 * registrationCenterUserDto.setUserId("M10411022");
	 * registrationCenterUserDtos.add(registrationCenterUserDto);
	 * registrationCenterUserResponseDto.setRegistrationCenterUsers(
	 * registrationCenterUserDtos);
	 * 
	 * when(registrationCenterUserService.getUsersBasedOnRegistrationCenterId(regId)
	 * ) .thenReturn(registrationCenterUserResponseDto);
	 * 
	 * MockRestServiceServer mockRestServiceServer =
	 * MockRestServiceServer.bindTo(restTemplate).build();
	 * mockRestServiceServer.expect(requestTo(userDetailsUri.toString() +
	 * "/registrationclient"))
	 * .andRespond(withSuccess().body(response).contentType(MediaType.
	 * APPLICATION_JSON)); syncUserDetailsService.getAllUserDetail(regId); }
	 */

	/*
	 * // @Test(expected = SyncDataServiceException.class) public void
	 * getAllUserDetailExcp() { String response =
	 * "{ \"userDetails\": [ { \"userName\": \"individual\", \"mail\": \"individual@mosip.io\", \"mobile\": \"8976394859\", \"langCode\": null, \"userPassword\": \"e1NTSEE1MTJ9TkhVb1c2WHpkZVJCa0drbU9tTk9ZcElvdUlNRGl5ODlJK3RhNm04d0FlTWhMSEoyTG4wSVJkNEJ2dkNqVFg4bTBuV2ZySStneXBTVittbVJKWnAxTkFwT3BWY3MxTVU5\", \"name\": \"individual\", \"roles\": [ \"REGISTRATION_ADMIN\", \"INDIVIDUAL\" ] } ] }"
	 * ; String regId = "10044"; RegistrationCenterUserResponseDto
	 * registrationCenterUserResponseDto = new RegistrationCenterUserResponseDto();
	 * List<RegistrationCenterUserDto> registrationCenterUserDtos = new
	 * ArrayList<>(); RegistrationCenterUserDto registrationCenterUserDto = new
	 * RegistrationCenterUserDto(); registrationCenterUserDto.setIsActive(true);
	 * registrationCenterUserDto.setRegCenterId(regId);
	 * registrationCenterUserDto.setUserId("M10411022");
	 * registrationCenterUserDtos.add(registrationCenterUserDto);
	 * registrationCenterUserResponseDto.setRegistrationCenterUsers(
	 * registrationCenterUserDtos);
	 * 
	 * when(registrationCenterUserService.getUsersBasedOnRegistrationCenterId(regId)
	 * ) .thenReturn(registrationCenterUserResponseDto);
	 * 
	 * MockRestServiceServer mockRestServiceServer =
	 * MockRestServiceServer.bindTo(restTemplate).build();
	 * mockRestServiceServer.expect(requestTo(userDetailsUri.toString() +
	 * "/registrationclient"))
	 * .andRespond(withServerError().body(response).contentType(MediaType.
	 * APPLICATION_JSON)); syncUserDetailsService.getAllUserDetail(regId); }
	 */
	/*
	 * // @Test public void getAllUserDetailNoDetail() { // String response =
	 * "{ \"userDetails\": [] }"; String regId = "10044";
	 * RegistrationCenterUserResponseDto registrationCenterUserResponseDto = new
	 * RegistrationCenterUserResponseDto(); List<RegistrationCenterUserDto>
	 * registrationCenterUserDtos = new ArrayList<>(); RegistrationCenterUserDto
	 * registrationCenterUserDto = new RegistrationCenterUserDto();
	 * registrationCenterUserDto.setIsActive(true);
	 * registrationCenterUserDto.setRegCenterId(regId);
	 * registrationCenterUserDto.setUserId("M10411022");
	 * registrationCenterUserDtos.add(registrationCenterUserDto);
	 * registrationCenterUserResponseDto.setRegistrationCenterUsers(
	 * registrationCenterUserDtos);
	 * 
	 * when(registrationCenterUserService.getUsersBasedOnRegistrationCenterId(regId)
	 * ) .thenReturn(registrationCenterUserResponseDto);
	 * 
	 * MockRestServiceServer mockRestServiceServer =
	 * MockRestServiceServer.bindTo(restTemplate).build();
	 * mockRestServiceServer.expect(requestTo(userDetailsUri.toString() +
	 * "/registrationclient")) .andRespond(withSuccess());
	 * assertNull(syncUserDetailsService.getAllUserDetail(regId)); }
	 */

	// ------------------------------------------AllRolesSync--------------------------//

	// @Test
	public void getAllRoles() {

		MockRestServiceServer mockRestServer = MockRestServiceServer.bindTo(restTemplate).build();

		mockRestServer.expect(requestTo(builder.toString() + "/registrationclient")).andRespond(withSuccess());
		syncRolesService.getAllRoles();
	}

	// @Test(expected = SyncDataServiceException.class)
	public void getAllRolesException() {

		MockRestServiceServer mockRestServer = MockRestServiceServer.bindTo(restTemplate).build();
		mockRestServer.expect(requestTo(builder.toString() + "/registrationclient")).andRespond(withServerError());
		syncRolesService.getAllRoles();
	}

	// -----------------------------------------publicKey-----------------------//

	@Test
	public void getPublicKey() throws JsonParseException, JsonMappingException, IOException {

		uriParams = new HashMap<>();
		uriParams.put("applicationId", "REGISTRATION");

		// Query parameters
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(publicKeyUrl)
				// Add query parameter
				.queryParam("referenceId", "referenceId")
				.queryParam("timeStamp", "2019-09-09T09:00:00.000Z");
		MockRestServiceServer mockRestServer = MockRestServiceServer.bindTo(restTemplate).build();

		mockRestServer.expect(MockRestRequestMatchers.requestTo(builder.buildAndExpand(uriParams).toString()))
				.andRespond(withSuccess().body(
						"{ \"id\": null, \"version\": null, \"responsetime\": \"2019-04-24T09:07:42.017Z\", \"metadata\": null, \"response\": { \"lastSyncTime\": \"2019-04-24T09:07:41.771Z\", \"publicKey\": \"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtzi3nWiNMEcaBV2cWO5ZLTBZe1TEGnT95bTvrpEEr-kJLrn80dn9k156zjQpjSzNfEOFVwugTEhEWdxrdrjDUACpA0cF4tUdAM5XJBB0xmzNGS5s7lmcliAOjXbCGU2VJwOUnYV4DSCgrReMCCe6LD_aApwu45OAZ9_sWG6R-jlIUOHLTdDUs6O8zLk8zl7tOX6Rlp25Zk9CLQw1m9drHJqxCbr9Wc9PQKUHBPqhtvCe9ZZeySsZb83dXpKKAZlkjdbrB25i_4O0pbv9LHk0qQlk0twqaef6D5nCTqcB5KQ4QqVYLcrtAhdbMXaDvpSf9syRQ3P3fAeiGkvUIhUWPwIDAQAB\", \"issuedAt\": \"2019-04-23T06:17:46.753\", \"expiryAt\": \"2020-04-23T06:17:46.753\" }, \"errors\": null }"));

		syncConfigDetailsService.getPublicKey("REGISTRATION", "2019-09-09T09:00:00.000Z", "referenceId");
	}

	@Test(expected = SyncDataServiceException.class)
	public void getPublicKeyServiceExceptionTest() {

		uriParams = new HashMap<>();
		uriParams.put("applicationId", "REGISTRATION");

		// Query parameters
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(publicKeyUrl)
				// Add query parameter
				.queryParam("referenceId", "referenceId")
				.queryParam("timeStamp", "2019-09-09T09:00:00.000Z");
		MockRestServiceServer mockRestServer = MockRestServiceServer.bindTo(restTemplate).build();

		mockRestServer.expect(MockRestRequestMatchers.requestTo(builder.buildAndExpand(uriParams).toString()))
				.andRespond(withServerError());

		syncConfigDetailsService.getPublicKey("REGISTRATION", "2019-09-09T09:00:00.000Z", "referenceId");
	}

	@Test(expected=SyncInvalidArgumentException.class)
	public void getPublicErrorList() throws IOException {
		uriParams = new HashMap<>();
		uriParams.put("applicationId", "REGISTRATION");

		// Query parameters
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(publicKeyUrl)
				// Add query parameter
				.queryParam("referenceId", "referenceId")
				.queryParam("timeStamp", "2019-09-09T09:00:00.000Z");
		MockRestServiceServer mockRestServer = MockRestServiceServer.bindTo(restTemplate).build();

		mockRestServer.expect(MockRestRequestMatchers.requestTo(builder.buildAndExpand(uriParams).toString()))
				.andRespond(withSuccess().body(
						"{ \"id\": null, \"version\": null, \"responsetime\": \"2019-04-24T10:24:23.760Z\", \"metadata\": null, \"response\": null, \"errors\": [ { \"errorCode\": \"KER-ATH-401\", \"message\": \"JWT expired at 2019-04-17T14:12:05+0000. Current time: 2019-04-24T10:24:23+0000\" } ] }"));

		syncConfigDetailsService.getPublicKey("REGISTRATION", "2019-09-09T09:00:00.000Z", "referenceId");
	}

	@Test(expected = SyncDataServiceException.class)
	public void getPublicServiceException() throws IOException {
		uriParams = new HashMap<>();
		uriParams.put("applicationId", "REGISTRATION");

		// Query parameters
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(publicKeyUrl)
				// Add query parameter
				.queryParam("referenceId", "referenceId")
				.queryParam("timeStamp", "2019-09-09T09:00:00.000Z");
		MockRestServiceServer mockRestServer = MockRestServiceServer.bindTo(restTemplate).build();

		mockRestServer.expect(MockRestRequestMatchers.requestTo(builder.buildAndExpand(uriParams).toString()))
				.andRespond(withSuccess().body(
						" \"id\": null, \"version\": null, \"responsetime\": \"2019-04-24T09:07:42.017Z\", \"metadata\": null, \"response\": { \"lastSyncTime\": \"2019-04-24T09:07:41.771Z\", \"publicKey\": \"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtzi3nWiNMEcaBV2cWO5ZLTBZe1TEGnT95bTvrpEEr-kJLrn80dn9k156zjQpjSzNfEOFVwugTEhEWdxrdrjDUACpA0cF4tUdAM5XJBB0xmzNGS5s7lmcliAOjXbCGU2VJwOUnYV4DSCgrReMCCe6LD_aApwu45OAZ9_sWG6R-jlIUOHLTdDUs6O8zLk8zl7tOX6Rlp25Zk9CLQw1m9drHJqxCbr9Wc9PQKUHBPqhtvCe9ZZeySsZb83dXpKKAZlkjdbrB25i_4O0pbv9LHk0qQlk0twqaef6D5nCTqcB5KQ4QqVYLcrtAhdbMXaDvpSf9syRQ3P3fAeiGkvUIhUWPwIDAQAB\", \"issuedAt\": \"2019-04-23T06:17:46.753\", \"expiryAt\": \"2020-04-23T06:17:46.753\" }, \"errors\": null }"));

		syncConfigDetailsService.getPublicKey("REGISTRATION", "2019-09-09T09:00:00.000Z", "referenceId");
	}

}
