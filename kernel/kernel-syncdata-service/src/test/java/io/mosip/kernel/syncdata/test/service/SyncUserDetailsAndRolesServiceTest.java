package io.mosip.kernel.syncdata.test.service;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withUnauthorizedRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.auth.adapter.exception.AuthNException;
import io.mosip.kernel.auth.adapter.exception.AuthZException;
import io.mosip.kernel.syncdata.entity.RegistrationCenterUser;
import io.mosip.kernel.syncdata.entity.id.RegistrationCenterUserID;
import io.mosip.kernel.syncdata.exception.DataNotFoundException;
import io.mosip.kernel.syncdata.exception.ParseResponseException;
import io.mosip.kernel.syncdata.exception.SyncDataServiceException;
import io.mosip.kernel.syncdata.exception.SyncServiceException;
import io.mosip.kernel.syncdata.repository.MachineRepository;
import io.mosip.kernel.syncdata.repository.RegistrationCenterUserRepository;
import io.mosip.kernel.syncdata.service.SyncJobDefService;
import io.mosip.kernel.syncdata.service.SyncRolesService;
import io.mosip.kernel.syncdata.service.SyncUserDetailsService;

@SpringBootTest
@RunWith(SpringRunner.class)
public class SyncUserDetailsAndRolesServiceTest {
	@Autowired
	private SyncUserDetailsService syncUserDetailsService;

	@MockBean
	MachineRepository machineRespository;

	@Autowired
	private SyncRolesService syncRolesService;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	RestTemplate restTemplate;

	@MockBean
	RegistrationCenterUserRepository registrationCenterUserRepository;

	@MockBean
	private SyncJobDefService registrationCenterUserService;

	@Value("${mosip.kernel.syncdata.auth-manager-base-uri}")
	private String authUserDetailsBaseUri;

	@Value("${mosip.kernel.syncdata.auth-user-details:/userdetails}")
	private String authUserDetailsUri;

	@Value("${mosip.kernel.syncdata.auth-manager-base-uri}")
	private String authBaseUri;

	@Value("${mosip.kernel.syncdata.auth-manager-roles}")
	private String authAllRolesUri;

	private StringBuilder userDetailsUri;

	private StringBuilder builder;

	private List<RegistrationCenterUser> registrationCenterUsers = null;

	@Before
	public void setup() {

		registrationCenterUsers = new ArrayList<>();
		RegistrationCenterUserID regCenterId = new RegistrationCenterUserID();
		regCenterId.setRegCenterId("10001");
		regCenterId.setUserId("M10411022");
		RegistrationCenterUser registrationCenterUser = new RegistrationCenterUser();
		registrationCenterUser.setIsActive(true);
		registrationCenterUser.setRegistrationCenterUserID(regCenterId);
		registrationCenterUsers.add(registrationCenterUser);

		userDetailsUri = new StringBuilder();
		userDetailsUri.append(authUserDetailsBaseUri).append(authUserDetailsUri);
		builder = new StringBuilder();
		builder.append(authBaseUri).append(authAllRolesUri);

	}

	// ------------------------------------------UserDetails--------------------------//
	@Test
	public void getAllUserDetail() throws JsonParseException, JsonMappingException, IOException {
		String response = "{\"id\":\"SYNCDATA.REQUEST\",\"version\":\"v1.0\",\"responsetime\":\"2019-03-31T10:40:29.935Z\",\"metadata\":null,\"response\":{\"mosipUserDtoList\":[{\"userId\":\"110001\",\"mobile\":\"9663175928\",\"mail\":\"110001@mosip.io\",\"langCode\":null,\"userPassword\":\"e1NTSEE1MTJ9L25EVy9tajdSblBMZFREYjF0dXB6TzdCTmlWczhKVnY1TXJ1aXRSZlBrSCtNVmJDTXVIM2lyb2thcVhsdlR6WkNKYXAwSncrSXc5SFc3aWRYUnpnaHBTQktrNXRSVTA3\",\"name\":\"user\",\"role\":\"REGISTRATION_ADMIN,REGISTRATION_OFFICER\"}]},\"errors\":null}";
		String regId = "10044";

		when(registrationCenterUserRepository.findByRegistrationCenterUserByRegCenterId(regId))
				.thenReturn(registrationCenterUsers);

		MockRestServiceServer mockRestServiceServer = MockRestServiceServer.bindTo(restTemplate).build();
		mockRestServiceServer.expect(requestTo(userDetailsUri.toString() + "/registrationclient"))
				.andRespond(withSuccess().body(response).contentType(MediaType.APPLICATION_JSON));
		syncUserDetailsService.getAllUserDetail(regId);
	}

	@Test(expected = SyncDataServiceException.class)
	public void getAllUserDetailExcp() {

		String regId = "10044";

		when(registrationCenterUserRepository.findByRegistrationCenterUserByRegCenterId(regId))
				.thenReturn(registrationCenterUsers);

		MockRestServiceServer mockRestServiceServer = MockRestServiceServer.bindTo(restTemplate).build();
		mockRestServiceServer.expect(requestTo(userDetailsUri.toString() + "/registrationclient"))
				.andRespond(withServerError().contentType(MediaType.APPLICATION_JSON));
		syncUserDetailsService.getAllUserDetail(regId);
	}

	@Test(expected = SyncDataServiceException.class)
	public void getAllUserDetailNoDetail() {

		String regId = "10044";

		when(registrationCenterUserRepository.findByRegistrationCenterUserByRegCenterId(regId))
				.thenReturn(registrationCenterUsers);

		MockRestServiceServer mockRestServiceServer = MockRestServiceServer.bindTo(restTemplate).build();
		mockRestServiceServer.expect(requestTo(userDetailsUri.toString() + "/registrationclient"))
				.andRespond(withBadRequest());
		syncUserDetailsService.getAllUserDetail(regId);
	}

	@Test(expected = ParseResponseException.class)
	public void getAllUserDetailParseException() {
		String response = "{\"id\":\"SYNCDATA.REQUEST\",\"version\":\"v1.0\",\"responsetime\":\"2019-03-31T10:40:29.935Z\",\"metadata\":null,\"response\":{\"mosipUserDtoList\":[\"userId\":\"110001\",\"mobile\":\"9663175928\",\"mail\":\"110001@mosip.io\",\"langCode\":null,\"userPassword\":\"e1NTSEE1MTJ9L25EVy9tajdSblBMZFREYjF0dXB6TzdCTmlWczhKVnY1TXJ1aXRSZlBrSCtNVmJDTXVIM2lyb2thcVhsdlR6WkNKYXAwSncrSXc5SFc3aWRYUnpnaHBTQktrNXRSVTA3\",\"name\":\"user\",\"role\":\"REGISTRATION_ADMIN,REGISTRATION_OFFICER\"}]},\"errors\":null}";
		String regId = "10044";

		when(registrationCenterUserRepository.findByRegistrationCenterUserByRegCenterId(regId))
				.thenReturn(registrationCenterUsers);

		MockRestServiceServer mockRestServiceServer = MockRestServiceServer.bindTo(restTemplate).build();
		mockRestServiceServer.expect(requestTo(userDetailsUri.toString() + "/registrationclient"))
				.andRespond(withSuccess().body(response));
		syncUserDetailsService.getAllUserDetail(regId);
	}

	@Test(expected = SyncServiceException.class)
	public void getAllUserDetailServiceException() {
		String response = "{ \"id\": null, \"version\": null, \"responsetime\": \"2019-03-31T11:40:39.847Z\", \"metadata\": null, \"response\": null, \"errors\": [ { \"errorCode\": \"KER-SNC-303\", \"message\": \"Registration center user not found \" } ] }";
		String regId = "10044";

		when(registrationCenterUserRepository.findByRegistrationCenterUserByRegCenterId(regId))
				.thenReturn(registrationCenterUsers);

		MockRestServiceServer mockRestServiceServer = MockRestServiceServer.bindTo(restTemplate).build();
		mockRestServiceServer.expect(requestTo(userDetailsUri.toString() + "/registrationclient"))
				.andRespond(withSuccess().body(response));
		syncUserDetailsService.getAllUserDetail(regId);
	}

	@Test(expected = AuthNException.class)
	public void getAllUserDetailServiceAuthNException() {
		String response = "{ \"id\": null, \"version\": null, \"responsetime\": \"2019-05-11T11:02:20.521Z\", \"metadata\": null, \"response\": null, \"errors\": [ { \"errorCode\": \"KER-ATH-402\", \"message\": \"Token expired\" } ] }";
		String regId = "10044";

		when(registrationCenterUserRepository.findByRegistrationCenterUserByRegCenterId(regId))
				.thenReturn(registrationCenterUsers);

		MockRestServiceServer mockRestServiceServer = MockRestServiceServer.bindTo(restTemplate).build();
		mockRestServiceServer.expect(requestTo(userDetailsUri.toString() + "/registrationclient"))
				.andRespond(withUnauthorizedRequest().body(response));
		syncUserDetailsService.getAllUserDetail(regId);
	}

	@Test(expected = BadCredentialsException.class)
	public void getAllUserDetailServiceBadCredentialsException() {

		String regId = "10044";

		when(registrationCenterUserRepository.findByRegistrationCenterUserByRegCenterId(regId))
				.thenReturn(registrationCenterUsers);

		MockRestServiceServer mockRestServiceServer = MockRestServiceServer.bindTo(restTemplate).build();
		mockRestServiceServer.expect(requestTo(userDetailsUri.toString() + "/registrationclient"))
				.andRespond(withUnauthorizedRequest());
		syncUserDetailsService.getAllUserDetail(regId);
	}

	@Test(expected = AuthZException.class)
	public void getAllUserDetailServiceAuthzException() {
		String response = "{ \"id\": null, \"version\": null, \"responsetime\": \"2019-05-11T11:02:20.521Z\", \"metadata\": null, \"response\": null, \"errors\": [ { \"errorCode\": \"KER-ATH-403\", \"message\": \"Forbidden\" } ] }";
		String regId = "10044";

		when(registrationCenterUserRepository.findByRegistrationCenterUserByRegCenterId(regId))
				.thenReturn(registrationCenterUsers);

		MockRestServiceServer mockRestServiceServer = MockRestServiceServer.bindTo(restTemplate).build();
		mockRestServiceServer.expect(requestTo(userDetailsUri.toString() + "/registrationclient"))
				.andRespond(MockRestResponseCreators.withStatus(HttpStatus.FORBIDDEN).body(response));
		syncUserDetailsService.getAllUserDetail(regId);
	}

	@Test(expected = AccessDeniedException.class)
	public void getAllUserDetailServicesAuthNException() {

		String regId = "10044";

		when(registrationCenterUserRepository.findByRegistrationCenterUserByRegCenterId(regId))
				.thenReturn(registrationCenterUsers);

		MockRestServiceServer mockRestServiceServer = MockRestServiceServer.bindTo(restTemplate).build();
		mockRestServiceServer.expect(requestTo(userDetailsUri.toString() + "/registrationclient"))
				.andRespond(MockRestResponseCreators.withStatus(HttpStatus.FORBIDDEN));
		syncUserDetailsService.getAllUserDetail(regId);
	}

	@Test(expected = SyncDataServiceException.class)
	public void getAllUserDetailServicesRegUserException() {

		String regId = "10044";

		when(registrationCenterUserRepository.findByRegistrationCenterUserByRegCenterId(regId))
				.thenThrow(DataRetrievalFailureException.class);

		syncUserDetailsService.getAllUserDetail(regId);
	}

	@Test(expected = DataNotFoundException.class)
	public void getAllUserDetailServicesDataNotFoundException() {

		String regId = "10044";

		when(registrationCenterUserRepository.findByRegistrationCenterUserByRegCenterId(regId))
				.thenReturn(new ArrayList<RegistrationCenterUser>());

		syncUserDetailsService.getAllUserDetail(regId);
	}
	// ------------------------------------------AllRolesSync--------------------------//

	@Test
	public void getAllRoles() {
		String response = "{ \"id\": null, \"version\": null, \"responsetime\": \"2019-03-31T11:51:42.113Z\", \"metadata\": null, \"response\": { \"lastSyncTime\": \"2019-03-31T11:51:35.458Z\", \"roles\": [ { \"roleId\": \"REGISTRATION_ADMIN\", \"roleName\": \"REGISTRATION_ADMIN\", \"roleDescription\": \"Registration administrator\" } ] }, \"errors\": null }";
		MockRestServiceServer mockRestServer = MockRestServiceServer.bindTo(restTemplate).build();

		mockRestServer.expect(requestTo(builder.toString() + "/registrationclient"))
				.andRespond(withSuccess().body(response));
		syncRolesService.getAllRoles();
	}

	@Test(expected = SyncDataServiceException.class)
	public void getAllRolesException() {

		MockRestServiceServer mockRestServer = MockRestServiceServer.bindTo(restTemplate).build();
		mockRestServer.expect(requestTo(builder.toString() + "/registrationclient")).andRespond(withServerError());
		syncRolesService.getAllRoles();
	}

	@Test(expected = SyncServiceException.class)
	public void getAllRolesValidationError() {
		String response = "{ \"id\": null, \"version\": null, \"responsetime\": \"2019-03-31T11:40:39.847Z\", \"metadata\": null, \"response\": null, \"errors\": [ { \"errorCode\": \"KER-SNC-303\", \"message\": \"Registration center user not found \" } ] }";
		MockRestServiceServer mockRestServer = MockRestServiceServer.bindTo(restTemplate).build();

		mockRestServer.expect(requestTo(builder.toString() + "/registrationclient"))
				.andRespond(withSuccess().body(response));
		syncRolesService.getAllRoles();
	}

	@Test(expected = ParseResponseException.class)
	public void getAllRolesParseError() {
		String response = "{ \"id\": null, \"version\": null, \"responsetime\": \"2019-03-31T11:51:42.113Z\", \"metadata\": null, \"response\":  \"lastSyncTime\": \"2019-03-31T11:51:35.458Z\", \"roles\": [ { \"roleId\": \"REGISTRATION_ADMIN\", \"roleName\": \"REGISTRATION_ADMIN\", \"roleDescription\": \"Registration administrator\" } ] }, \"errors\": null }";
		MockRestServiceServer mockRestServer = MockRestServiceServer.bindTo(restTemplate).build();

		mockRestServer.expect(requestTo(builder.toString() + "/registrationclient"))
				.andRespond(withSuccess().body(response));
		syncRolesService.getAllRoles();
	}

	@Test(expected = AuthNException.class)
	public void getRolesServiceException() {
		String response = "{ \"id\": null, \"version\": null, \"responsetime\": \"2019-05-11T11:02:20.521Z\", \"metadata\": null, \"response\": null, \"errors\": [ { \"errorCode\": \"KER-ATH-402\", \"message\": \"Token expired\" } ] }";

		MockRestServiceServer mockRestServer = MockRestServiceServer.bindTo(restTemplate).build();
		mockRestServer.expect(requestTo(builder.toString() + "/registrationclient"))
				.andRespond(withUnauthorizedRequest().body(response));
		syncRolesService.getAllRoles();
	}

	@Test(expected = BadCredentialsException.class)
	public void getAllRolesBadCredentialsException() {

		MockRestServiceServer mockRestServiceServer = MockRestServiceServer.bindTo(restTemplate).build();
		mockRestServiceServer.expect(requestTo(builder.toString() + "/registrationclient"))
				.andRespond(withUnauthorizedRequest());
		syncRolesService.getAllRoles();
	}

	@Test(expected = AuthZException.class)
	public void getRolesServiceAuthzException() {
		String response = "{ \"id\": null, \"version\": null, \"responsetime\": \"2019-05-11T11:02:20.521Z\", \"metadata\": null, \"response\": null, \"errors\": [ { \"errorCode\": \"KER-ATH-403\", \"message\": \"Forbidden\" } ] }";
		String regId = "10044";

		when(registrationCenterUserRepository.findByRegistrationCenterUserByRegCenterId(regId))
				.thenReturn(registrationCenterUsers);

		MockRestServiceServer mockRestServiceServer = MockRestServiceServer.bindTo(restTemplate).build();
		mockRestServiceServer.expect(requestTo(builder.toString() + "/registrationclient"))
				.andRespond(MockRestResponseCreators.withStatus(HttpStatus.FORBIDDEN).body(response));
		syncRolesService.getAllRoles();
	}

	@Test(expected = AccessDeniedException.class)
	public void getRolesAuthNException() {

		String regId = "10044";

		when(registrationCenterUserRepository.findByRegistrationCenterUserByRegCenterId(regId))
				.thenReturn(registrationCenterUsers);

		MockRestServiceServer mockRestServiceServer = MockRestServiceServer.bindTo(restTemplate).build();
		mockRestServiceServer.expect(requestTo(builder.toString() + "/registrationclient"))
				.andRespond(MockRestResponseCreators.withStatus(HttpStatus.FORBIDDEN));
		syncRolesService.getAllRoles();
	}
}
