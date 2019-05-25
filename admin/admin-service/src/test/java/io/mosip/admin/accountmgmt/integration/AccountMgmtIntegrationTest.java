package io.mosip.admin.accountmgmt.integration;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
import io.mosip.admin.accountmgmt.dto.UserDetailDto;
import io.mosip.admin.accountmgmt.dto.UserNameDto;
import io.mosip.admin.accountmgmt.dto.ValidationResponseDto;
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

	@Value("${mosip.admin.accountmgmt.user-detail-url}")
	private String userDetailUrl;

	@Value("${mosip.admin.accountmgmt.validate-url}")
	private String validateUrl;

	@Value("${mosip.admin.app-id}")
	private String appId;

	@Autowired
	private ObjectMapper objectMapper;

	private StringBuilder uriBuilder;

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
		ResponseWrapper<UserDetailDto> responeWrapper = new ResponseWrapper<>();
		UserDetailDto userDetailDto = new UserDetailDto();
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
	public void testValidateUserName() throws Exception {
		ResponseWrapper<ValidationResponseDto> responeWrapper = new ResponseWrapper<>();
		ValidationResponseDto validateResponseDto = new ValidationResponseDto();
		validateResponseDto.setStatus("Valid");
		responeWrapper.setResponse(validateResponseDto);
		String response = objectMapper.writeValueAsString(responeWrapper);
		mockRestServiceServer.expect(requestTo((uriBuilder.append(validateUrl).append(appId + "/12345433")).toString()))
				.andRespond(withSuccess().body(response));

		mockMvc.perform(get("/accountmanagement/validate/12345433")).andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void testValidateUserNameException() throws Exception {

		mockRestServiceServer.expect(requestTo((uriBuilder.append(validateUrl).append(appId + "/12345433")).toString()))
				.andRespond(withServerError());

		mockMvc.perform(get("/accountmanagement/validate/12345433")).andExpect(status().isInternalServerError());
	}
	
	@Test
	@WithUserDetails("zonal-admin")
	public void testValidateUserNameServiceException() throws Exception {
        ResponseWrapper<ServiceError> responseWrapper= new ResponseWrapper<>();
        ServiceError ser= new ServiceError();
        ser.setErrorCode("KER-ATH-100");
        ser.setMessage("bad request");
        List<ServiceError> serList=new ArrayList<>();
        serList.add(ser);
        responseWrapper.setErrors(serList);
        String res=objectMapper.writeValueAsString(responseWrapper);
		mockRestServiceServer.expect(requestTo((uriBuilder.append(validateUrl).append(appId + "/12345433")).toString()))
				.andRespond(withBadRequest().body(res));

		mockMvc.perform(get("/accountmanagement/validate/12345433")).andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void testGetUserDetailBasedOnMobileException() throws Exception {

		mockRestServiceServer
				.expect(requestTo((uriBuilder.append(userDetailUrl).append(appId + "/12345433")).toString()))
				.andRespond(withServerError());

		mockMvc.perform(get("/accountmanagement/userdetail/12345433")).andExpect(status().isOk());
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
				.andRespond(withSuccess().body(res));

		mockMvc.perform(get("/accountmanagement/userdetail/12345433")).andExpect(status().isOk());
	}

}
