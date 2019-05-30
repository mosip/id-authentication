package io.mosip.admin.accountmgmt.integration;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.admin.TestBootApplication;
import io.mosip.admin.accountmgmt.dto.PasswordDto;
import io.mosip.admin.accountmgmt.dto.ResetPasswordDto;
import io.mosip.admin.accountmgmt.dto.StatusResponseDto;
import io.mosip.admin.accountmgmt.dto.UserDetailRestClientDto;
import io.mosip.admin.accountmgmt.dto.UserDetailsDto;
import io.mosip.admin.accountmgmt.dto.UserNameDto;
import io.mosip.admin.accountmgmt.entity.RegistrationCenterUser;
import io.mosip.admin.accountmgmt.entity.id.RegistrationCenterUserID;
import io.mosip.admin.accountmgmt.repository.RegistrationCenterUserRepository;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseWrapper;

@SpringBootTest(classes = TestBootApplication.class)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class AccountMgmtIntegrationTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	RestTemplate restTemplate;

	@Value("${mosip.admin.accountmgmt.auth-manager-base-uri}")
	private String authManagerBaseUrl;

	@Value("${mosip.admin.accountmgmt.user-name-url}")
	private String userNameUrl;

	@Value("${mosip.admin.accountmgmt.unblock-url}")
	private String unBlockUrl;

	@Value("${mosip.admin.accountmgmt.change-passoword-url}")
	private String changePassword;

	@Value("${mosip.admin.accountmgmt.reset-password-url}")
	private String resetPassword;

	@Value("${mosip.admin.accountmgmt.user-detail}")
	private String userDetailBasedOnUidUrl;

	@Value("${mosip.admin.accountmgmt.user-detail-url}")
	private String userDetailUrl;

	@Value("${mosip.admin.app-id}")
	private String appId;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private RegistrationCenterUserRepository registrationCenterUserRepository;

	private StringBuilder uriBuilder;

	private List<RegistrationCenterUser> registrationCenters;

	MockRestServiceServer mockRestServiceServer;

	ResponseWrapper<StatusResponseDto> resSuccess;

	@Before
	public void setUp() {
		mockRestServiceServer = MockRestServiceServer.bindTo(restTemplate).build();
		StatusResponseDto status = new StatusResponseDto();
		status.setMessage("Successfully unblocked");
		status.setStatus("Success");
		uriBuilder = new StringBuilder();
		uriBuilder.append(authManagerBaseUrl);
		resSuccess = new ResponseWrapper<>();
		resSuccess.setResponse(status);
		RegistrationCenterUser reg = new RegistrationCenterUser();
		RegistrationCenterUserID registrationCenterUserID = new RegistrationCenterUserID();
		registrationCenterUserID.setRegCenterId("10001");
		registrationCenterUserID.setUserId("110001");
		reg.setRegistrationCenterUserID(registrationCenterUserID);
		registrationCenters = new ArrayList<>();
		registrationCenters.add(reg);

	}

	@Test
	@WithUserDetails("zonal-admin")
	public void testUnblockUser() throws Exception {
		String response = objectMapper.writeValueAsString(resSuccess);
		mockRestServiceServer
				.expect(requestTo((uriBuilder.append(unBlockUrl).append(appId + "/").append("10001")).toString()))
				.andRespond(withSuccess().body(response));

		mockMvc.perform(get("/accountmanagement/unblockaccount/10001")).andExpect(status().isOk());

	}

	@Test
	@WithUserDetails("zonal-admin")
	public void testchangePassword() throws Exception {
		String response = objectMapper.writeValueAsString(resSuccess);
		RequestWrapper<PasswordDto> req = new RequestWrapper<>();
		req.setRequesttime(LocalDateTime.now(ZoneOffset.UTC));
		PasswordDto passwordDto = new PasswordDto();
		passwordDto.setHashAlgo("SSHA-256");
		passwordDto.setNewPassword("Abcde@123");
		passwordDto.setOldPassword("Abcde@123");
		passwordDto.setUserId("110030");
		req.setRequest(passwordDto);
		String request = objectMapper.writeValueAsString(req);
		mockRestServiceServer.expect(requestTo((uriBuilder.append(changePassword).append(appId + "/")).toString()))
				.andRespond(withSuccess().body(response));

		mockMvc.perform(
				post("/accountmanagement/changepassword").contentType(MediaType.APPLICATION_JSON).content(request))
				.andExpect(status().isOk());

	}

	@Test
	@WithUserDetails("zonal-admin")
	public void testResetPassword() throws Exception {
		String response = objectMapper.writeValueAsString(resSuccess);
		RequestWrapper<ResetPasswordDto> req = new RequestWrapper<>();
		req.setRequesttime(LocalDateTime.now(ZoneOffset.UTC));
		ResetPasswordDto passwordDto = new ResetPasswordDto();
		passwordDto.setHashAlgo("SSHA-256");
		passwordDto.setNewPassword("Abcde@123");
		passwordDto.setUserId("110030");
		req.setRequest(passwordDto);
		String request = objectMapper.writeValueAsString(req);
		mockRestServiceServer.expect(requestTo((uriBuilder.append(resetPassword).append(appId)).toString()))
				.andRespond(withSuccess().body(response));

		mockMvc.perform(
				post("/accountmanagement/resetpassword").contentType(MediaType.APPLICATION_JSON).content(request))
				.andExpect(status().isOk());

	}

	@Test
	@WithUserDetails("zonal-admin")
	public void testGetUserNameBasedOnMobile() throws Exception {
		ResponseWrapper<UserNameDto> responeWrapper = new ResponseWrapper<>();
		UserNameDto userNameDto = new UserNameDto();
		userNameDto.setUserName("110030");
		responeWrapper.setResponse(userNameDto);
		String response = objectMapper.writeValueAsString(responeWrapper);
		mockRestServiceServer.expect(requestTo((uriBuilder.append(userNameUrl).append(appId + "/12345433")).toString()))
				.andRespond(withSuccess().body(response));

		mockMvc.perform(get("/accountmanagement/username/12345433")).andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void testGetUserDetailBasedOnMobile() throws Exception {
		ResponseWrapper<UserDetailsDto> responeWrapper = new ResponseWrapper<>();
		UserDetailsDto userDetailDto = new UserDetailsDto();
		userDetailDto.setUserId("110030");
		userDetailDto.setMail("asdasda@aas.com");
		userDetailDto.setName("asas");
		responeWrapper.setResponse(userDetailDto);
		String response = objectMapper.writeValueAsString(responeWrapper);
		mockRestServiceServer
				.expect(requestTo((uriBuilder.append(userDetailUrl).append(appId + "/12345433")).toString()))
				.andRespond(withSuccess().body(response));

		mockMvc.perform(get("/accountmanagement/userdetail/12345433")).andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void testGetUserDetailBasedOnMobileException() throws Exception {

		mockRestServiceServer
				.expect(requestTo((uriBuilder.append(userDetailUrl).append(appId + "/12345433")).toString()))
				.andRespond(withServerError());

		mockMvc.perform(get("/accountmanagement/userdetail/12345433")).andExpect(status().isInternalServerError());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void testGetUserDetailBasedOnMobileClientException() throws Exception {
		ResponseWrapper<ServiceError> resError = new ResponseWrapper<>();
		ServiceError serError = new ServiceError();
		serError.setErrorCode("KER-AUTH-101");
		serError.setMessage("Auth manager exception");
		List<ServiceError> serErrorList = new ArrayList<>();
		serErrorList.add(serError);
		resError.setErrors(serErrorList);
		String res = objectMapper.writeValueAsString(resError);
		mockRestServiceServer
				.expect(requestTo((uriBuilder.append(userDetailUrl).append(appId + "/12345433")).toString()))
				.andRespond(withBadRequest().body(res));

		mockMvc.perform(get("/accountmanagement/userdetail/12345433")).andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void testGetUserDetails() throws Exception {
		when(registrationCenterUserRepository.findAllByRegistrationCenterId(Mockito.anyString()))
				.thenReturn(registrationCenters);
		ResponseWrapper<UserDetailRestClientDto> res = new ResponseWrapper<>();
		UserDetailsDto userDetailDto = new UserDetailsDto();
	    UserDetailRestClientDto userDetailRestClientDto= new UserDetailRestClientDto();
		userDetailDto.setUserId("110001");
		userDetailDto.setMail("asdasd@adsa.com");
		userDetailDto.setRole("ZONAL-ADMIN,REG-PROC");
		List<UserDetailsDto> userDetailsDtos= new ArrayList<>();
		userDetailsDtos.add(userDetailDto);
		userDetailRestClientDto.setUserDetails(userDetailsDtos);
		res.setResponse(userDetailRestClientDto);
		String response = objectMapper.writeValueAsString(res);
		mockRestServiceServer.expect(requestTo((uriBuilder.append(userDetailBasedOnUidUrl).append(appId)).toString()))
				.andRespond(withSuccess().body(response));

		mockMvc.perform(get("/accountmanagement/userdetails/10001")).andExpect(status().isOk());

	}

	@Test
	@WithUserDetails("zonal-admin")
	public void testGetUserDetailsServerError() throws Exception {
		when(registrationCenterUserRepository.findAllByRegistrationCenterId(Mockito.anyString()))
				.thenReturn(registrationCenters);
		mockRestServiceServer.expect(requestTo((uriBuilder.append(userDetailBasedOnUidUrl).append(appId)).toString()))
				.andRespond(withServerError());

		mockMvc.perform(get("/accountmanagement/userdetails/10001")).andExpect(status().isInternalServerError());

	}
	
	@Test
	@WithUserDetails("zonal-admin")
	public void testGetUserDetailsClientError() throws Exception {
		when(registrationCenterUserRepository.findAllByRegistrationCenterId(Mockito.anyString()))
				.thenReturn(registrationCenters);
		ResponseWrapper<ServiceError> res= new ResponseWrapper<>();
		ServiceError serError= new ServiceError();
		serError.setErrorCode("KER-ATH-119");
		serError.setMessage("Unable to find users for the userId");
		List<ServiceError> serviceErrors= new ArrayList<>();
		serviceErrors.add(serError);
		res.setErrors(serviceErrors);
		String response=objectMapper.writeValueAsString(res);
		mockRestServiceServer.expect(requestTo((uriBuilder.append(userDetailBasedOnUidUrl).append(appId)).toString()))
				.andRespond(withServerError().body(response));

		mockMvc.perform(get("/accountmanagement/userdetails/10001")).andExpect(status().isOk());

	}

}
